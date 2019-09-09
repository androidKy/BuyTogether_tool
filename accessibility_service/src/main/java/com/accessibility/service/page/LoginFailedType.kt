package com.accessibility.service.page

/**
 *  登录失败的种类：
 *    0：掉线
 *    1：账号被封。
 *    2:进入 "添加账号"界面。（也是账号被封的一种）
 */
class LoginFailedType {

    companion object{
        const val  DROP_LINE:Int = 0
        const val  UNVAILD:Int = 1
        const val  ADD_ACCOUNT:Int = 2
    }
}