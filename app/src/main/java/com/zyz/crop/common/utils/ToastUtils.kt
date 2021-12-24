package com.zyz.crop.common.utils

import android.content.Context
import android.widget.Toast

/**
 * <pre>
 *     author : ZYZ
 *     e-mail : zyz163mail@163.com
 *     time   : 2021/12/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object ToastUtils {
    fun showShortToast(context: Context, text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}