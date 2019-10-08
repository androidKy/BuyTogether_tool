package com.accessibility.service.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.accessibility.service.listener.TaskListener
import com.safframework.log.L
import com.utils.common.ThreadUtils
import com.utils.common.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Description:保存图片到本地图库
 * Created by Quinin on 2019-09-21.
 **/
class PictureUtils private constructor() {

    private var mUrls: ArrayList<String> = ArrayList()
    private var mDownloadFinished: Int = 0
    private var mTaskListener:TaskListener?=null

    companion object {
        val instance: PictureUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PictureUtils()
        }

        const val MSG_DOWNLOAD_PROGRESS: Int = 1001
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper()) {
        if (it.what == MSG_DOWNLOAD_PROGRESS) {
            mDownloadFinished++
            L.i("Url size=${mUrls.size} mDownloadFinished=${mDownloadFinished}")
            if (mUrls.size == mDownloadFinished) {
                mTaskListener?.onTaskFinished()
            }
        }

        true
    }

    fun setUrls(urls: List<String>): PictureUtils {
        mUrls.clear()
        mUrls.addAll(urls)
        return this
    }

    fun setTaskListener(taskListener: TaskListener):PictureUtils
    {
        mTaskListener = taskListener
        return this
    }

    fun savePictures() {
        mDownloadFinished = 0
        downloadPictures()
    }


    private fun downloadPictures() {
        for (i in 0 until mUrls.size) {
            downloadPictureAndSave2Local(mUrls[i])
        }
    }

    /**
     * 下载图片并保存到本地
     */
    private fun downloadPictureAndSave2Local(url: String) {
        ThreadUtils.executeByCached(object : ThreadUtils.Task<Boolean>() {
            override fun doInBackground(): Boolean {
                var result: Boolean = false
                val bitmap: Bitmap? = downloadPicture(url)
                if (bitmap != null) {
                    val fileDir = File(Environment.getExternalStorageDirectory(), "pdd_picture")
                    L.i("fileDir: $fileDir")
                    if (!fileDir.exists()) {
                        fileDir.mkdir()
                    } /*else {
                        deleteFiles(fileDir)
                    }*/

                    val fileName = "${System.currentTimeMillis()}.jpg"
                    val file = File(fileDir, fileName)

                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.flush()
                        fos.close()
                    } catch (e: Exception) {
                        L.e(e.message, e)
                    } finally {
                        fos?.run {
                            close()
                        }
                    }
                    // 其次把文件插入到系统图库
                    try {
                        MediaStore.Images.Media.insertImage(
                            Utils.getApp().contentResolver,
                            file.absolutePath, fileName, null
                        )
                    } catch (e: Exception) {
                        L.e(e.message, e)
                    }
                    // 最后通知图库更新
                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    val uri = Uri.fromFile(file)
                    intent.data = uri
                    Utils.getApp().sendBroadcast(intent)
                    result = true
                }

                return result
            }

            override fun onSuccess(result: Boolean?) {
                result?.apply {
                    if (this) {
                        L.i("下载图片成功")
                        mHandler.sendEmptyMessage(MSG_DOWNLOAD_PROGRESS)
                    } else {
                        L.i("下载图片失败")
                        mTaskListener?.onTaskFailed("下载图片失败")
                    }
                }
            }

            override fun onCancel() {

            }

            override fun onFail(t: Throwable?) {

            }
        })
    }


    /**
     * 下载图片，返回bitmap
     */
    private fun downloadPicture(url: String): Bitmap? {
        L.i("url:$url")
        var myFileUrl: URL? = null
        var bitmap: Bitmap? = null
        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            myFileUrl = URL(url)
            conn = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            inputStream = conn.inputStream;
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (e: Exception) {
            L.e(e.message, e)
        } finally {
            inputStream?.apply {
                close()
            }
        }
        return bitmap
    }


    /**
     * 删除之前的文件，重新下载
     */
   private fun deleteFiles(file: File) {
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            if (listFiles != null && listFiles.isNotEmpty()) {
                for (i in 0 until listFiles.size) {
                    if (!listFiles[i].isDirectory) {
                        listFiles[i].delete()
                    }
                }
            }
        }
    }
}