package com.theartofdev.edmodo.cropper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImageView.*
import kotlinx.android.synthetic.main.activity_crop_image2.*
import kotlinx.android.synthetic.main.layout_crop_title.*
import java.io.File
import java.io.IOException

/**
 * 外部拍照 或 选择图片完了把图片路径的 uri 传过来, 直接启动该 Activity
 */
open class CropImageActivity2 : AppCompatActivity(), OnSetImageUriCompleteListener, OnCropImageCompleteListener {

    private var mImgPathStr: String? = null             //外部拍照 或 选择图片完了后得到的图片路径的 uri
    private var mOptions: CropImageOptions? = null
    private var mCropImageView: CropImageView? = null

    /**
     * Get Android uri to save the cropped image into.<br></br>
     * Use the given in options or create a temp file.
     */
    private val outputUri: Uri?
        get() {
            var outputUri = mOptions!!.outputUri
            if (outputUri == null || outputUri == Uri.EMPTY) {
                outputUri = try {
//                    val ext = if (mOptions!!.outputCompressFormat == Bitmap.CompressFormat.JPEG) ".jpg" else if (mOptions!!.outputCompressFormat == Bitmap.CompressFormat.PNG) ".png" else ".webp"
//                    Uri.fromFile(File.createTempFile("cropped", ext, cacheDir))
                    Uri.fromFile(File(mImgPathStr))
                } catch (e: IOException) {
                    throw RuntimeException("Failed to create temp file for output image", e)
                }
            }
            return outputUri
        }

    @SuppressLint("NewApi")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image2)
        initBundle()
        initClickListener()
        con_flip.visibility = View.GONE
        mCropImageView = cropImageView.apply {
            this.setImageUriAsync(Uri.parse("file://$mImgPathStr"))
        }
    }

    override fun onStart() {
        super.onStart()
        mCropImageView?.setOnSetImageUriCompleteListener(this)
        mCropImageView?.setOnCropImageCompleteListener(this)
    }

    override fun onStop() {
        super.onStop()
        mCropImageView?.setOnSetImageUriCompleteListener(null)
        mCropImageView?.setOnCropImageCompleteListener(null)
    }

    private fun initBundle() {
        intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE).let { bundle ->
            mImgPathStr = bundle?.getString(CropImage.CROP_IMAGE_EXTRA_SOURCE)
            mOptions = bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)
        }
    }

    private fun initClickListener() {
        iv_back.setOnClickListener {
            setResultCancel()
        }
        iv_rotate.setOnClickListener {
            rotateImage(mOptions!!.rotationDegrees)
        }
        iv_flip.setOnClickListener {
            if (con_flip.visibility == View.VISIBLE) {
                con_flip.visibility = View.GONE
            } else {
                con_flip.visibility = View.VISIBLE
            }
        }
        tv_hor_flip.setOnClickListener {
            mCropImageView?.flipImageHorizontally()
            con_flip.visibility = View.GONE
        }
        tv_ver_flip.setOnClickListener {
            mCropImageView?.flipImageVertically()
            con_flip.visibility = View.GONE
        }
        tv_cutting.setOnClickListener {
            cropImage()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error == null) {
            if (mOptions!!.initialCropWindowRectangle != null) {
                mCropImageView?.cropRect = mOptions!!.initialCropWindowRectangle
            }
            if (mOptions!!.initialRotation > -1) {
                mCropImageView?.rotatedDegrees = mOptions!!.initialRotation
            }
        } else {
            setResult(null, error, 1)
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropResult) {
        setResult(result.uri, result.error, result.sampleSize)
    }

    // region: Private methods
    /** Execute crop image and save the result tou output uri.  */
    private fun cropImage() {
        if (mOptions!!.noOutputImage) {
            setResult(null, null, 1)
        } else {
            val outputUri = outputUri
            mCropImageView?.saveCroppedImageAsync(
                    outputUri,
                    mOptions!!.outputCompressFormat,
                    mOptions!!.outputCompressQuality,
                    mOptions!!.outputRequestWidth,
                    mOptions!!.outputRequestHeight,
                    mOptions!!.outputRequestSizeOptions)
        }
    }

    /** Rotate the image in the crop image view.  */
    private fun rotateImage(degrees: Int) {
        mCropImageView?.rotateImage(degrees)
    }

    /** Result with cropped image data or error if failed.  */
    private fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val resultCode = if (error == null) RESULT_OK else CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
        setResult(resultCode, getResultIntent(uri, error, sampleSize))
        finish()
    }

    /** Cancel of cropping activity.  */
    private fun setResultCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    /** Get intent instance to be used for the result of this activity.  */
    private fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {
        val result = CropImage.ActivityResult(
                mCropImageView?.imageUri,
                uri,
                error,
                mCropImageView?.cropPoints,
                mCropImageView?.cropRect,
                mCropImageView?.rotatedDegrees ?: 0,
                mCropImageView?.wholeImageRect,
                sampleSize)
        val intent = Intent()
        intent.putExtras(getIntent())
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)
        return intent
    }

    //resultCode : RESULT_OK、RESULT_CANCELED、CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE
    companion object {
        fun startActivity(context: Activity,
                          existImgFilePath: String,
                          requestCode: Int,
                          cropImageOptions: CropImageOptions? = null) {
            cropImageOptions?.validate()
            context.startActivityForResult(Intent(context, CropImageActivity2::class.java).apply {
                this.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, Bundle().apply {
                    this.putString(CropImage.CROP_IMAGE_EXTRA_SOURCE, existImgFilePath)
                    this.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS,
                            cropImageOptions ?: CropImageOptions().apply {
                                this.guidelines = CropImageView.Guidelines.ON
                            })
                })
            }, requestCode)
        }
    }
}