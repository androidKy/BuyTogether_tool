package com.utils.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.safframework.log.L
import java.io.*

/**
 * Description:
 * Created by Quinin on 2019-07-26.
 **/
class FileUtils {
    companion object {
        /**
         * 把文件转换成字符串格式
         */
        fun readFile2String(file: File): String? {
            if (!file.exists()) {
                return null
            }
            var bufferedInputStream: BufferedInputStream? = null
            try {
                bufferedInputStream = BufferedInputStream(file.inputStream())
                bufferedInputStream.readBytes().run {
                    return String(this)
                }

            } catch (e: Exception) {
                L.e(e.message, e)
            } finally {
                closeStream(bufferedInputStream)
            }
            return null
        }

        private fun closeStream(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (e: Exception) {
                L.e(e.message, e)
            }
        }

        // 通过文件得到图片的Bitmap数据
        fun getBitmapByFile(file: File): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val fileInputStream = FileInputStream(file)
                bitmap = BitmapFactory.decodeStream(fileInputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                L.e(e.message,e)
            }

            return bitmap
        }

        // Bitmap数据转成byte数据为借口提交数据使用
        fun getBytesByBitmap(bitmap: Bitmap): ByteArray {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            return baos.toByteArray()
        }
    }

}