package com.buy.together.fragment

import android.widget.FrameLayout
import com.buy.together.R
import com.buy.together.base.BaseFragment
import com.buy.together.fragment.view.MainView
import com.buy.together.fragment.viewmodel.MainViewModel
import com.rmondjone.locktableview.LockTableView
import com.rmondjone.locktableview.DisplayUtil
import com.safframework.log.L


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainFragment : BaseFragment(), MainView {

    private var mTableDatas = ArrayList<ArrayList<String>>()
    private var mContainer: FrameLayout? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        L.init(MainFragment::class.java.simpleName)
        mContainer = mRootView?.findViewById<FrameLayout>(R.id.mainFragment_container)

        initDisplayOpinion()

        initHeader()

        val mainViewModel = MainViewModel(context!!, this)
        mainViewModel.getTask()

        initTableView(mTableDatas)
    }

    private fun initHeader() {
        val headerList = ArrayList<String>()

        headerList.add("KEY")
        headerList.add("VALUE")

        mTableDatas.add(headerList)
    }

    private fun initDisplayOpinion() {

        val dm = resources.displayMetrics
        DisplayUtil.density = dm.density
        DisplayUtil.densityDPI = dm.densityDpi
        DisplayUtil.screenWidthPx = dm.widthPixels
        DisplayUtil.screenhightPx = dm.heightPixels
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(context, dm.widthPixels.toFloat()).toFloat()
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(context, dm.heightPixels.toFloat()).toFloat()
    }

    private fun initTableView(tableDatas: ArrayList<ArrayList<String>>) {

        val lockTableView = LockTableView(context, mContainer, tableDatas)

        lockTableView.setLockFristColumn(false) //是否锁定第一列
            .setLockFristRow(true) //是否锁定第一行
            .setMaxColumnWidth(100) //列最大宽度
            .setMinColumnWidth(60) //列最小宽度
            .setColumnWidth(1, 30) //设置指定列文本宽度
            .setColumnWidth(2, 20)
            .setMinRowHeight(20)//行最小高度
            .setMaxRowHeight(60)//行最大高度
            .setTextViewSize(16) //单元格字体大小
            .setFristRowBackGroudColor(R.color.table_head)//表头背景色
            .setTableHeadTextColor(R.color.beijin)//表头字体颜色
            .setTableContentTextColor(R.color.border_color)//单元格字体颜色
            .setCellPadding(15)//设置单元格内边距(dp)
            .setNullableString("N/A") //空值替换值
    }

    override fun onResponTask(taskData: ArrayList<String>) {

    }

    override fun onFailed(msg: String?) {
        L.i(msg)
    }

}