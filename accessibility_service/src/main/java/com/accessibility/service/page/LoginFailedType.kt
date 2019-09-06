package com.accessibility.service.page

/**
 *  登录失败的种类：
 *    0：掉线
 *    1：账号被封。
 */
class LoginFailedType {

    companion object{
        const val  DROP_LINE:Int = 0
        const val  UNVAILD:Int = 1
    }
}