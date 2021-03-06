package com.zyz.crop.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_main.*
import me.iwf.photopicker.PhotoPicker

class MainActivity : AppCompatActivity() {

    private var mCurTakePicPath: String? = null    //当前路径，拍照回调后需要使用
    private val REQUEST_CODE_TAKE_PICTURE = 10086

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_take_picture.setOnClickListener{
            goTakePictureClick()
        }
        btn_select_album_photo.setOnClickListener{
            selectPhotoClick()
        }
        btn_select_album_photo_crop.setOnClickListener{
            onSelectCropImageClick()
        }
        btn_select_photo_crop_path.setOnClickListener{
            onSelectCropImagePathClick()
        }
    }

    /**
     * 拍照
     */
    fun goTakePictureClick() {
//        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
        GachaCheckUtils.checkCameraStoragePermission(this, success = {
            ImageUtil.openCamera(this, REQUEST_CODE_TAKE_PICTURE) {
                mCurTakePicPath = it
            }
        })
    }

    /**
     * 选择图片
     * demo 中鸿蒙手机选择的图片加载不了, 但是项目可以？
     */
    fun selectPhotoClick() {
        GachaCheckUtils.checkCameraStoragePermission(this, success = {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
//                    .setCustomeView(CustomView())
                    .start(this, PhotoPicker.REQUEST_CODE)
        })
    }

    /**
     * 拍照/选择图片使用 Crop
     */
    fun onSelectCropImageClick() {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this)
    }

    /**
     * 直接传路径选择图片再裁剪
     */
    fun onSelectCropImagePathClick(){
        val photoPath = "/storage/emulated/0/Pictures/1591843653688.jpg"
        mCurTakePicPath = photoPath
        Log.e("TAG", "选择图片成功, 开始裁剪 photoPath = $photoPath")
        CropImageActivity2.startActivity(this, photoPath, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_TAKE_PICTURE -> {                  //拍照成功
                    mCurTakePicPath?.let { takePicPath ->
                        CropImageActivity2.startActivity(this, takePicPath, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                    }
                }
                PhotoPicker.REQUEST_CODE -> {                  //使用 PhotoPicker 选择图片成功
                    data?.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).let{photos ->
                        photos?.firstOrNull()?.let{photoPath ->
                            mCurTakePicPath = photoPath
                            Log.e("TAG", "选择图片成功, 开始裁剪 photoPath = $photoPath")
                            CropImageActivity2.startActivity(this, photoPath, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                        }
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> { //裁剪成功
                    val result = CropImage.getActivityResult(data)
                    (findViewById<View>(R.id.quick_start_cropped_image) as ImageView).setImageURI(result.uri)
                    Toast.makeText(this, "裁剪图片成功 Sample: " + result.sampleSize + " result.uri = " + result.uri, Toast.LENGTH_LONG).show()
                }
            }
        }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            val result = CropImage.getActivityResult(data)
            Toast.makeText(this, "裁剪图片识别 errMsg: " + result.error, Toast.LENGTH_LONG).show()
        }
    }
}