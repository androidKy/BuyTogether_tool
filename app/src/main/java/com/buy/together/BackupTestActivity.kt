package com.buy.together

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.accessibility.service.util.Constant
import com.buy.together.backup.BackupConstant.TAI_BACKUP
import com.buy.together.backup.BackupManage
import com.buy.together.backup.IResponListener
import com.buy.together.backup.utils.ThreadUtil
import com.safframework.log.L
import com.utils.common.CMDUtil
import com.utils.common.ToastUtils

class BackupTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_test)

        ThreadUtil.async(Runnable {
            // 关闭selinux，还原的文件 selinux 权限成了 syetem_data_file，应用读取出错
                CMDUtil().execCmd("setenforce 0")
        })
    }

    fun backup(view: View) {
       BackupManage()
           .setContext(this)
           .setParams("ip","2019","1011","pddData")
           .setBackupWay(TAI_BACKUP)
           .backup(Constant.BUY_TOGETHER_PKG,object:IResponListener{
               override fun onResponSuccess(zipFileName: String?) {
                   ToastUtils.showToast(this@BackupTestActivity,"backup finished")
                   L.i("backup zipFileName: $zipFileName")
               }

               override fun onResponFailed(msg: String?) {
                   ToastUtils.showToast(this@BackupTestActivity,"backup failed")
                   L.i("backup failed: $msg")
               }
           })
    }

    fun deleteData(view: View) {
        ThreadUtil.async(Runnable {
            CMDUtil().execCmd("pm clear ${Constant.BUY_TOGETHER_PKG}")
        })
    }

    fun restore(view: View) {
        BackupManage()
            .setContext(this)
            .setParams("ip","2019","1011","pddData")
            .restore(Constant.BUY_TOGETHER_PKG,object:IResponListener{
                override fun onResponSuccess(zipFileName: String?) {
                    ToastUtils.showToast(this@BackupTestActivity,"restore finished")
                    L.i("restore zipFileName: $zipFileName")
                }

                override fun onResponFailed(msg: String?) {
                    ToastUtils.showToast(this@BackupTestActivity,"restore failed:$msg")
                    L.i("restore failed: $msg")
                }
            })
    }
}
