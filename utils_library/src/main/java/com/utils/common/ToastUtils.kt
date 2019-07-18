package com.utils.common

import android.content.Context
import android.widget.Toast

/**
 * Description:
 * Created by Quinin on 2019-07-18.
 **/
class ToastUtils {

    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}