package com.buy.together.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Description:
 * Created by Quinin on 2019-06-26.
 **/
abstract class BaseFragment : Fragment() {

    abstract fun getLayoutId(): Int
    abstract fun initView()

    public var mRootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getLayoutId(),container,false)
        initView()
        return mRootView
    }
}