package com.zyz.crop.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by Ivan on 2018/6/6.
 * Email:greatDaze@163.com
 */
object ImageUtil {

    private val TAG = "ImageUtil"

    /**
     * 打开相机
     */
    fun openCamera(context: Activity?, requestCode: Int, absPathListener: (photoAbsPath: String?) -> Unit) {
        context?.let{
            val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                val photoFile: File? = getSavedFilePath(context)
                absPathListener.invoke(photoFile?.absolutePath)
                if (photoFile != null) {
                    val photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    } else {
                        getDestinationUri(context)
                    }
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    context.startActivityForResult(takePictureIntent, requestCode)
                }
            }
        }
    }

    /**
     * 获取 Uri
     * @return
     */
    private fun getDestinationUri(context: Context): Uri? {
        return Uri.fromFile(File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                String.format("gacha_%s.jpg", System.currentTimeMillis())
        ))
    }

    /**
     * 获取保存好的照片路径
     * @return
     */
    private fun getSavedFilePath(context: Context): File? {
        val newFolderFile: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName: String = System.currentTimeMillis().toString() + ".jpg"
        var tempFile: File? = null
        try {
            tempFile = File(newFolderFile.toString() + "/" + fileName)
            tempFile.createNewFile()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return tempFile
    }

    /**
     * @param bmp
     * 将缩略图缩小为 32kb 以内, 注意：是 JPEG 格式(此格式 为WEP 分享到iOS 无法查看，为PNG 无法分享 so 只能是JPEG格式的图片)
     * @param needRecycle
     * @return
     */
    @JvmStatic
    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        val output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output)
        val arrayLength = output.toByteArray().size

        val maxSize = 32 * 1024
        if (arrayLength > maxSize) {
            val quality = maxSize * 100 / arrayLength//32kb


            if (quality < 100) {
                output.reset()// 重置baos即清空baos
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, output)// 这里压缩options%，把压缩后的数据存放到baos中
            }
            Log.d("quality", "quality = $quality,arrayLength = ${arrayLength / 1024} ")
        }
        if (needRecycle) {
            bmp.recycle()
        }
        val result = output.toByteArray()
        Log.d("quality", "result size = ${result.size / 1024}")
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    private fun isFileExists(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        val f = File(path!!)
        return f.exists()
    }

    fun getFileBitmapSize(path: String): IntArray {
        val opts = BitmapFactory.Options()
        // 设置为 true 只获取图片大小
        opts.inJustDecodeBounds = true
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888
        // 返回为空
        BitmapFactory.decodeFile(path, opts)
        val width = opts.outWidth
        val height = opts.outHeight
        return intArrayOf(width, height)
    }
}



