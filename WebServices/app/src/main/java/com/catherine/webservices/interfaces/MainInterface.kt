package com.catherine.webservices.interfaces

import android.view.View


/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface MainInterface {
    fun getPermissions(permissions: Array<String>, listener: OnRequestPermissionsListener)
    fun callFragment(id: Int)
    fun backToPreviousPage()
    fun clearAllFragments()
    fun setBackKeyListener(listener: BackKeyListener)
    fun addBottomLayout(id: Int)
    fun getBottomLayout(): View
    fun restoreBottomLayout()
}