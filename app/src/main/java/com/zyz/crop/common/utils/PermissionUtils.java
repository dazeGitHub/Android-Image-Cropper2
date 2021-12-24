package com.zyz.crop.common.utils;

import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;


/**
 * Created by Ivan on 2018/7/13.
 * Email:greatDaze@163.com
 */
public class PermissionUtils {
    private static String TAG = PermissionUtils.class.getName();

    /**
     * @param successAction
     * @param permissions   Permission.Group.STORAGE, Permission.Group.CAMERA
     */
    public static void requestPermission(Context  context, final Action<List<String>> successAction, final Action<List<String>> denyAction, String[]... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (successAction != null) {
                            successAction.onAction(permissions);
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (denyAction != null) {
                            denyAction.onAction(permissions);
                        }
                    }
                })
                .start();
    }
}
