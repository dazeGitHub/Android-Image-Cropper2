package com.zyz.crop.common.utils

import android.content.Context
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.Permission
import java.lang.reflect.Method


/**
 * created by wxiangle by 2020-01-08
 * email : wang_x_le@163.com
 */

object GachaCheckUtils {

    val TAG = "GachaCheckUtils"
    private const val HARMONY_OS = "harmony"

    public fun checkStoragePermission(context: Context, success: (() -> Unit)?, failed: (() -> Unit)? = null) {
        PermissionUtils.requestPermission(context, Action<List<String>> {
            success?.invoke()
        }, Action<List<String>> {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                if(isHarmonyOS()){
                    success?.invoke()
                }else{
                    failed?.invoke()
                    ToastUtils.showShortToast(context, "请到\"设置\"中开启本应用的存储权限")
                }
            } else {
                success?.invoke()
            }
        }, Permission.Group.STORAGE)
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
        checkCameraPermission(context, {
            checkStoragePermission(context, success, failed)
        }, failed)
    }

    /**
     * check the system is harmony os
     * @return true if it is harmony os
     */
    fun isHarmonyOS(): Boolean {
        try {
            val clz = Class.forName("com.huawei.system.BuildEx")
            val method: Method = clz.getMethod("getOsBrand")
            return HARMONY_OS == method.invoke(clz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}