package com.catherine.webservices.interfaces

/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface OnRequestPermissionsListener {
    /**
     * 用户开启权限
     */
    fun onGranted()

    /**
     * 用户拒绝打开权限
     */
    fun onDenied(deniedPermissions: List<String>?)

    /**
     * 获取权限过程被中断，此处只要重新执行获取权限
     */
    fun onRetry()
}