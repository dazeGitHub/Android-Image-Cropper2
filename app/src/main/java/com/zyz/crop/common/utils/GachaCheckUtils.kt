package com.zyz.crop.common.utils

import android.content.Context
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.Permission


/**
 * created by wxiangle by 2020-01-08
 * email : wang_x_le@163.com
 */

object GachaCheckUtils {

    val TAG = "GachaCheckUtils"

    public fun checkStoragePermission(context: Context, success: (() -> Unit)?, failed: (() -> Unit)? = null) {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            PermissionUtils.requestPermission(context, Action<List<String>> {
                success?.invoke()
            }, Action<List<String>> {
                ToastUtils.showShortToast(context, "请到\"设置\"中开启本应用的存储权限")
                failed?.invoke()
            }, Permission.Group.STORAGE)
        } else {
            success?.invoke()
        }
    }

    public fun checkCameraPermission(context: Context, success: (() -> Unit)?, failed: (() -> Unit)? = null) {
        PermissionUtils.requestPermission(context, Action<List<String>> {
            success?.invoke()
        }, Action<List<String>> {
            ToastUtils.showShortToast(context, "请到\"设置\"中开启本应用的图片和相册权限")
            failed?.invoke()
        }, Permission.Group.CAMERA)
    }

    fun checkCameraStoragePermission(context: Context, success: (() -> Unit)?, failed: (() -> Unit)? = null){
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            PermissionUtils.requestPermission(context, Action<List<String>> {
                success?.invoke()
            }, Action<List<String>> {
                ToastUtils.showShortToast(context, "请到\"设置\"中开启本应用的存储和相册权限")
                failed?.invoke()
            }, Permission.Group.STORAGE, Permission.Group.CAMERA)
        }else {
            success?.invoke()
        }
    }
}