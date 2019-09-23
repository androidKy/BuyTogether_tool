package com.buy.together.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.buy.together.R
import com.rmondjone.locktableview.DisplayUtil
import com.rmondjone.locktableview.LockTableView

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
        mRootView = inflater.inflate(getLayoutId(), container, false)
        initView()
        return mRootView
    }

    fun initTableView(container: FrameLayout?,tableDatas: ArrayList<ArrayList<String>>) {
        context?.run {
            container?.apply {
                removeAllViews()
                LockTableView(this@run, this@apply, tableDatas).let {
                    it.setLockFristColumn(true) //是否锁定第一列
                        .setLockFristRow(true) //是否锁定第一行
                        .setMaxColumnWidth(300) //列最大宽度
                        .setMinColumnWidth(60) //列最小宽度
                        //.setColumnWidth(1, 30) //设置指定列文本宽度
                        //.setColumnWidth(2, 0)
                        .setMinRowHeight(20)//行最小高度
                        .setMaxRowHeight(50)//行最大高度
                        .setTextViewSize(16) //单元格字体大小
                        .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                        .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                        .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                        .setCellPadding(5)//设置单元格内边距(dp)
                        .show()
                }
            }
        }
    }

     fun initHeader(tableDatas: ArrayList<ArrayList<String>>) {
        val headerList = ArrayList<String>()
        headerList.add("KEY")
        headerList.add("VALUE")

        tableDatas.add(headerList)
    }

     fun initDisplayOpinion() {

        val dm = resources.displayMetrics
        DisplayUtil.density = dm.density
        DisplayUtil.densityDPI = dm.densityDpi
        DisplayUtil.screenWidthPx = dm.widthPixels
        DisplayUtil.screenhightPx = dm.heightPixels
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(context, dm.widthPixels.toFloat()).toFloat()
        DisplayUtil.screenHightDip =
            DisplayUtil.px2dip(context, dm.heightPixels.toFloat()).toFloat()
    }


    /**
     * 显示进度
     */
    fun showLoadingDialog(msg: String) {

    }
}