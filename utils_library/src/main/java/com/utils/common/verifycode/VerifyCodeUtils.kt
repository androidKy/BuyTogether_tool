package com.utils.common.verifycode

import android.text.TextUtils
import com.safframework.log.L
import com.utils.common.FileUtils
import com.utils.common.ThreadUtils
import org.json.JSONObject
import java.io.File

/**
 * Description:斐斐打码工具 http://www.fateadm.com
 * Created by Quinin on 2019-07-26.
 **/
class VerifyCodeUtils {

    companion object {
        private const val URL_OCR_PIC = "http://pred.fateadm.com/api/capreg"
        private const val PD_ID = "115113"
        private const val PD_KEY = "gRYiULcIzy6sMEhr77SMxdWeF5VllKL8"
        private const val APP_ID = "315113"
        private const val APP_KEY = "Db4vTYPuH1oJBRZKpsO3tDsrlP4sksc/"
        private const val PREDICT_TYPE = "20400" //4位英文字母
        /**
         * 识别图片验证码
         */
        fun doOcr(file: File, resultListener: ResultListener) {
            if (!file.exists()) {
                L.i("识别图片不存在，请重新选择")
                resultListener.onResult("")
                return
            }
            val bitmap = FileUtils.getBitmapByFile(file)
            if (bitmap == null) {
                L.i("验证码的截图bitmap为空")
                resultListener.onResult("")
                return
            }
            val img_data = FileUtils.getBytesByBitmap(bitmap)

            ThreadUtils.executeByCached(object : ThreadUtils.Task<Util.HttpResp>() {
                override fun doInBackground(): Util.HttpResp {
                    return FateadmAPI().run {
                        init(APP_ID, APP_KEY, PD_ID, PD_KEY)
                        doOcr(PREDICT_TYPE, img_data)
                    }
                }

                override fun onSuccess(result: Util.HttpResp?) {
                    result?.apply {
                        if (!TextUtils.isEmpty(rsp_data)) {
                            try {
                                val jsonObject = JSONObject(rsp_data)

                                resultListener.onResult(jsonObject.getString("result"))
                            } catch (e: Exception) {
                                L.e(e.message, e)
                                resultListener.onResult("")
                            }
                        } else resultListener.onResult("")
                    }
                }

                override fun onCancel() {
                    L.i("执行验证码验证的线程被取消")
                    resultListener.onResult("")
                }

                override fun onFail(t: Throwable?) {
                    t?.run {
                        L.e(message, this)
                        resultListener.onResult("")
                    }
                }

            })
        }

    }

    interface ResultListener {
        fun onResult(result: String)
    }
}