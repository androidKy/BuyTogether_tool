package com.buy.together.base

import android.content.Context

open class BaseViewModel<c : Context,bv : BaseView> {
    /**
     * 释放Observable的资源
     */
    open fun clearSubscribes() {

    }
}