package com.proxy.service

/**
 * Description:
 * Created by Quinin on 2019-08-16.
 **/
class ProxyConstant {
    companion object{
        //sp name
        const val SP_IP_PORTS:String = "sp_ip_ports"
        const val SP_CITY_LIST = "sp_city_data"
        //data key
        const val KEY_CITY_DATA = "key_cities"              //城市列表
        const val KEY_CITY_GET_DATE = "key_city_get_date"   //获取城市列表的时间
        const val KEY_IP_DATA = "key_ip_data" //IP数据
        const val KEY_CUR_PORT = "key_cur_port" //当前正在使用的端口
    }
}