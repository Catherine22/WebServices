package com.catherine.webservices.interfaces;

import java.util.List;

/**
 * Created by Catherine on 2017/11/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface OnRequestPermissionsListener {
    /**
     * 用户开启权限
     */
    void onGranted();

    /**
     * 用户拒绝打开权限
     */
    void onDenied(List<String> deniedPermissions);

    /**
     * 获取权限过程被中断，此处只要重新执行获取权限
     */
    void onRetry();
}
