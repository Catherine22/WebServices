package com.catherine.webservices.interfaces

import android.os.Bundle
import android.view.View


/**
 * Created by Catherine on 2017/8/28.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
interface MainInterface {
    fun getPermissions(permissions: Array<String>, listener: OnRequestPermissionsListener)
    fun switchTab(position: Int)
    fun callFragment(id: Int)
    fun callFragment(id: Int, bundle: Bundle?)
    fun hideKeyboard()
    fun openSlideMenu()
    fun backToPreviousPage()
    fun clearAllFragments()
    fun setBackKeyListener(listener: BackKeyListener)
    fun removeBackKeyListener()
    fun addBottomLayout(id: Int)
    fun getBottomLayout(): View
    fun restoreBottomLayout()
}