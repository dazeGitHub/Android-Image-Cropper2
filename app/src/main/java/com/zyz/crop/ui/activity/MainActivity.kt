package com.zyz.crop.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zyz.crop.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity2
import com.theartofdev.edmodo.cropper.CropImageView
import com.zyz.crop.common.utils.GachaCheckUtils
import com.zyz.crop.common.utils.ImageUtil

class MainActivity : AppCompatActivity() {

    private var mCurTakePicPath: String? = null    //当前路径，拍照回调后需要使用
    private val TAKE_PICTURE_REQUEST_CODE = 10086

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * 拍照/选择图片
     */
    fun onSelectImageClick(view: View?) {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this)
    }

    /**
     * 拍照
     */
    fun goTakePicture(view: View?) {
//        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
        GachaCheckUtils.checkCameraStoragePermission(this, success = {
            ImageUtil.openCamera(this, TAKE_PICTURE_REQUEST_CODE) {
                mCurTakePicPath = it
            }
        })
    }

    /**
     * 选择图片
     */
    fun selectPhoto(view: View?) {

    }

    /**
     * 拍照成功, 去裁剪
     */
    private fun onTakePictureSuccess() {
        mCurTakePicPath?.let { takePicPath ->
            CropImageActivity2.startActivity(this, takePicPath, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE_REQUEST_CODE){
                onTakePictureSuccess()
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                (findViewById<View>(R.id.quick_start_cropped_image) as ImageView).setImageURI(result.uri)
                Toast.makeText(this, "裁剪图片成功 Sample: " + result.sampleSize + " result.uri = " + result.uri, Toast.LENGTH_LONG).show()
            }
        }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            val result = CropImage.getActivityResult(data)
            Toast.makeText(this, "裁剪图片识别 errMsg: " + result.error, Toast.LENGTH_LONG).show()
        }
    }
}