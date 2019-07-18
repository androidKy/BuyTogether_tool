package com.buy.together.fragment

import android.widget.FrameLayout
import com.buy.together.R
import com.buy.together.base.BaseFragment
import com.buy.together.bean.TaskBean
import com.buy.together.fragment.view.MainView
import com.buy.together.fragment.viewmodel.MainViewModel
import com.rmondjone.locktableview.DisplayUtil
import com.rmondjone.locktableview.LockTableView
import com.safframework.log.L
import com.utils.common.ToastUtils


/**
 * Description:
 * Created by Quinin on 2019-06-27.
 **/
class MainFragment : BaseFragment(), MainView {

    private var mTableDatas = ArrayList<ArrayList<String>>()
    private var mTaskBean: TaskBean? = null
    private var mContainer: FrameLayout? = null
    private var mViewModel: MainViewModel? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        L.init(MainFragment::class.java.simpleName)
        mContainer = mRootView?.findViewById<FrameLayout>(R.id.mainFragment_container)

        initDisplayOpinion()

        initHeader()

        mViewModel = MainViewModel(context!!, this)
        mViewModel?.getTask()

        //initTableView(mTableDatas)
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

        lockTableView.setLockFristColumn(true) //是否锁定第一列
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
        //.setNullableString("N/A") //空值替换值

        lockTableView.show()
    }

    override fun onResponTask(taskBean: TaskBean) {
        if (taskBean.code == 200) {
            mTaskBean = taskBean
            mViewModel?.parseTask(taskBean)
        } else {
            L.i("获取数据失败：${taskBean.msg}")
        }
    }

    override fun onParseDatas(taskData: ArrayList<ArrayList<String>>) {
        mTableDatas.addAll(taskData)

        initTableView(mTableDatas)

        mViewModel?.clearData()

       /* val launchIntentForPackage = context?.packageManager?.getLaunchIntentForPackage(Constant.BUY_TOGETHER_PKG)
        if (launchIntentForPackage != null) {

        } else {
            ToastUtils.showToast(context!!, "未安装拼多多")
        }*/
    }

    override fun onFailed(msg: String?) {
        L.i("获取任务失败：$msg")
    }


    override fun onClearDataResult(result: String) {
        if (result == "Success") {
            mViewModel?.getPorts(mTaskBean!!)
        } else {
            ToastUtils.showToast(context!!, "清理数据失败")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.clearSubscribes()
    }


}