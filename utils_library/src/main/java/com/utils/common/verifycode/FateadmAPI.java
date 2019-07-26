package com.utils.common.verifycode;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by John_Doe on 2018/8/7.
 */

public class FateadmAPI {
    protected String app_id;
    protected String app_key;
    protected String pd_id;
    protected String pd_key;
    protected String pred_url;

    public void init(String app_id, String app_key, String pd_id, String pd_key){
        this.app_id     = app_id;
        this.app_key    = app_key;
        this.pd_id     = pd_id;
        this.pd_key    = pd_key;
        this.pred_url   = "http://pred.fateadm.com";
    }

    /**
     * 查询余额
     * 参数：无
     * 返回值：
     *      resp.ret_code：正常返回0
     *      resp.err_msg：异常时返回异常详情
     *      resp.cust_val：余额
     */
    public Util.HttpResp QueryBalc() throws Exception {
        long cur_tm     = new Date().getTime()/1000;
        String stm      = String.valueOf(cur_tm);
        String sign     = Util.CalcSign( pd_id, pd_key, stm);
        String url      = this.pred_url + "/api/custval";
        String params   = "user_id="+this.pd_id + "&timestamp=" + stm + "&sign=" + sign;
        Log.d("Debug","url: "+url+"param: "+params);
        String pres     = Util.HttpPost(url, params);
        Util.HttpResp resp = Util.ParseHttpResp( pres);
        return resp;
    }

    /**
     * 识别验证码
     * 参数： pred_type：识别类型  file_data：图片文件数据
     * 返回值：
     *      resp.ret_code：正常返回0
     *      resp.err_msg：异常时返回异常详情
     *      resp.req_Id：唯一订单号
     *      resp.pred_resl：识别的结果
     */
    public Util.HttpResp doOcr(String pred_type, byte[] file_data) throws Exception {
        long cur_tm     = new Date().getTime()/1000;
        String stm      = String.valueOf(cur_tm);
        String sign     = Util.CalcSign( pd_id, pd_key, stm);
        String img_data = Util.CalcBase64(file_data);
        img_data        = URLEncoder.encode( img_data, "utf-8");
        String url      = pred_url + "/api/capreg";
        String params   = "user_id=" + pd_id + "&timestamp="+stm + "&sign=" + sign +"&predict_type=" + pred_type;
        if(!app_id.isEmpty()){
            String asign = Util.CalcSign( app_id, app_key, stm);
            params      = params + "&appid=" + app_id + "&asign=" + asign;
        }
        params          += "&img_data=" + img_data;
        String pres     = Util.HttpPost(url, params);
        Util.HttpResp resp  = Util.ParseHttpResp(pres);
        return resp;
    }

    public Util.HttpResp PredictMForm(String pred_type, byte[] img_data) throws Exception {
        long cur_tm     = new Date().getTime()/1000;
        String stm      = String.valueOf(cur_tm);
        String sign     = Util.CalcSign(pd_id,pd_key,stm);
        String boundary = "--" + Util.CalcMd5(stm);
        String boundarybytes_string = "--" + boundary + "\r\n";
        URL url = new URL(pred_url + "/api/capreg");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(30000);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(true);
        con.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        OutputStream out    = con.getOutputStream();
        String item_string  = boundarybytes_string + "Content-Disposition: form-data;name=\"";
        String param_string = item_string + "user_id\"\r\n\r\n" + pd_id + "\r\n"
                + item_string + "timestamp\"\r\n\r\n" + stm + "\r\n"
                + item_string + "sign\"\r\n\r\n" + sign + "\r\n"
                + item_string + "predict_type\"\r\n\r\n" + pred_type + "\r\n"
                + item_string + "up_type\"\r\n\r\nmt\r\n";
        if(!app_id.isEmpty()){
            String asign  = Util.CalcSign(app_id,app_key,stm);
            param_string  += item_string + "appid\"\r\n\r\n" + app_id + "\r\n"
                    + item_string + "asign\"\r\n\r\n" + asign + "\r\n";
        }
        String file_strig = item_string + "img_data\";filename=\"image.jpg\"\r\nContent-Type: image/jpg\r\n\r\n";
        String end_string = "\r\n--" + boundary + "--\r\n";
        out.write(param_string.getBytes("UTF-8"));
        out.write(file_strig.getBytes("UTF-8"));
        out.write(img_data);
        out.write(end_string.getBytes("UTF-8"));
        out.flush();
        out.close();
        StringBuffer buffer = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String temp;
        while((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append("\n");
        }
        String pres = buffer.toString().trim();
        Util.HttpResp resp = Util.ParseHttpResp(pres);
        return resp;
    }


    /**
     * 识别失败，进行退款请求
     * 参数： req_id：需要退款的订单号
     * 返回值：
     *      resp.ret_code：正常返回0
     *      resp.err_msg：异常时返回异常详情
     *
     * 注意：
     *      Predict识别接口，仅在RetCode == 0时才会进行扣款，才需要进行退款请求，否则无需进行退款操作
     * 注意2：
     *      退款仅在正常识别出结果后，无法通过网站验证的情况，请勿非法或者滥用，否则可能进行封号处理
     */
    public Util.HttpResp Justice(String req_id) throws Exception {
        long cur_tm     = new Date().getTime()/1000;
        String stm      = String.valueOf(cur_tm);
        String sign     = Util.CalcSign( pd_id, pd_key, stm);
        String url      = pred_url + "/api/capjust";
        String params   = "user_id=" + pd_id + "&timestamp="+stm + "&sign=" + sign + "&request_id=" + req_id;
        String pres     = Util.HttpPost(url, params);
        Util.HttpResp resp  = Util.ParseHttpResp( pres);
        return resp;
    }
}
