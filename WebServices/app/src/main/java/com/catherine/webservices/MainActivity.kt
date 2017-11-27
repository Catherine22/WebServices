package com.catherine.webservices

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import catherine.messagecenter.AsyncResponse
import catherine.messagecenter.Server
import com.catherine.webservices.adapters.MainViewPagerAdapter
import com.catherine.webservices.components.DialogManager
import com.catherine.webservices.fragments.*
import com.catherine.webservices.interfaces.BackKeyListener
import com.catherine.webservices.interfaces.MainInterface
import com.catherine.webservices.interfaces.OnItemClickListener
import com.catherine.webservices.interfaces.OnRequestPermissionsListener
import com.catherine.webservices.kotlin_sample.KotlinTemplate
import com.catherine.webservices.kotlin_sample.player.Player
import com.catherine.webservices.network.NetworkHealthListener
import com.catherine.webservices.network.NetworkHelper
import com.catherine.webservices.toolkits.CLog
import com.catherine.webservices.xml.DOMParser
import com.catherine.webservices.xml.SAXParser
import com.catherine.webservices.xml.XMLDelegate
import com.catherine.webservices.xml.XMLParserListener
import kotlinx.android.synthetic.main.activity_main.*
import org.dom4j.Document
import java.io.IOException
import java.util.*


/**
 * Created by Catherine on 2017/8/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class MainActivity : BaseFragmentActivity(), MainInterface {

    companion object {
        private val TAG = "MainActivity"
    }

    private var sv: Server? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_main, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH))
    }

    override fun onPermissionGranted() {
        setView()
        MyApplication.INSTANCE.init()
        sv = Server(this@MainActivity, object : AsyncResponse {
            override fun onFailure(errorCode: Int) {
                CLog.e(TAG, "errorCode:$errorCode")
            }
        })

        val checkStateWork = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
        checkStateWork.post {
            val networkHelper = NetworkHelper(this@MainActivity)
            CLog.d(TAG, "isNetworkHealthy:${networkHelper.isNetworkHealthy()}")
            CLog.d(TAG, "isWifi:${networkHelper.isWifi()}")
            networkHelper.listenToNetworkState(object : NetworkHealthListener {
                override fun networkConnected(type: String) {
                    CLog.i(TAG, "network connected, type:$type")
                }

                override fun networkDisable() {
                    CLog.e(TAG, "network disable")
                }
            })
        }
    }


    private val fm = supportFragmentManager

    /**
     * 跳页至某Fragment
     *
     * @param position position of tabLayout
     */
    override fun switchTab(position: Int) {
        if (position < Constants.MAIN_TABS.size) {
            vp_content.currentItem = position
        }
    }

    /**
     * 跳页至某Fragment
     *
     * @param id Tag of the Fragment
     */
    override fun callFragment(id: Int) {
        callFragment(id, null)
    }

    /**
     * 跳页至某Fragment
     *
     * @param id Tag of the Fragment
     * @param bundle argument of the Fragment
     */
    override fun callFragment(id: Int, bundle: Bundle?) {
        fl_main_container.visibility = View.VISIBLE
        var fragment: Fragment? = null
        var tag: String? = null
        var title = ""
        when (id) {

        //null bundle
            Constants.P05_Gallery -> {
                title = "P05_Gallery"
                fragment = P05_Gallery.newInstance(true)
                tag = "P05"
            }


            Constants.P15_WEBVIEW_SETTINGS -> {
                title = "P15_WebView_Settings"
                fragment = P15_WebView_Settings.newInstance(true)
                tag = "P15"
            }

            Constants.P16_WEBVIEW_HISTORY -> {
                title = "P16_WebView_History"
                fragment = P16_WebView_History.newInstance(true)
                tag = "P16"
            }

            Constants.P17_WEBVIEW_TEST_LIST -> {
                title = "P17_WebView_Test_List"
                fragment = P17_WebView_Test_List.newInstance(true)
                tag = "P17"
            }


        //has bundle
            Constants.P14_FULL_WEBVIEW -> {
                title = "P14_Full_WebView"
                fragment = P14_Full_WebView.newInstance(true)
                tag = "P14"
            }
        }

        //Avoid to launch duplicated fragments
        if (fm.backStackEntryCount > 0 && fm.fragments[fm.fragments.size - 1].tag == tag) {
            return
        }

        if (bundle != null)
            fragment?.arguments = bundle

        CLog.d(TAG, "call $id ,has bundle? ${bundle != null}")
        val transaction = fm.beginTransaction()
        transaction.add(R.id.fl_main_container, fragment, tag)
        transaction.addToBackStack(title)
        transaction.commitAllowingStateLoss()
    }


    /**
     * Clear all fragments in stack
     */
    override fun clearAllFragments() {
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
            fl_main_container.visibility = View.GONE
        }
    }

    /**
     * Simulate BackKey event
     */
    override fun backToPreviousPage() {
        if (fm.backStackEntryCount > 0) {
            if (fm.backStackEntryCount == 1)
                fl_main_container.visibility = View.GONE
            fm.popBackStack()
        } else
            onBackPressed()
    }

    override fun addBottomLayout(id: Int) {
        val view = layoutInflater.inflate(id, null)
        fl_bottom.addView(view)
    }

    override fun restoreBottomLayout() {
        fl_bottom.removeAllViews()
        addBottomLayout(R.layout.bottom_main)
    }

    override fun getBottomLayout(): View {
        return fl_bottom
    }

    private var backKeyEventListener: MutableList<BackKeyListener?>? = null
    override fun setBackKeyListener(listener: BackKeyListener) {
        backKeyEventListener?.set(vp_content.currentItem, listener)
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(left_drawer)) {
                drawer_layout.closeDrawer(left_drawer)
                return true
            } else {
                if (backKeyEventListener?.get(vp_content.currentItem) != null) {
                    backKeyEventListener?.get(vp_content.currentItem)?.OnKeyDown()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun openSlideMenu() {
        drawer_layout.openDrawer(left_drawer)
    }

    private fun setView() {
        val menu = resources.getStringArray(R.array.drawer_array)
        left_drawer.adapter = ArrayAdapter<String>(this, R.layout.drawer_list_item, menu)
        // Sets the drawer shadow
        drawer_layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        left_drawer.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            when (pos) {
                0 -> {
                    callFragment(Constants.P14_FULL_WEBVIEW)
                }
                2 -> {
                    callFragment(Constants.P15_WEBVIEW_SETTINGS)
                }
                3 -> {
                    callFragment(Constants.P16_WEBVIEW_HISTORY)
                }
            }
            //            left_drawer.setItemChecked(pos, true)
            drawer_layout.closeDrawer(left_drawer)
        }

        vp_content.adapter = MainViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(vp_content)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                restoreBottomLayout()
                when (tab) {
                    tabLayout.getTabAt(Constants.P01_APACHE) -> vp_content.currentItem = Constants.P01_APACHE
                    tabLayout.getTabAt(Constants.P02_HTTP_URL_CONNECTION) -> vp_content.currentItem = Constants.P02_HTTP_URL_CONNECTION
                    tabLayout.getTabAt(Constants.P03_DOWNLOADER) -> vp_content.currentItem = Constants.P03_DOWNLOADER
                    tabLayout.getTabAt(Constants.P04_CACHE) -> {
                        //Push broadcast before initialize so the broadcast won't be captured at first time.
                        //So I update view twice - first one would be done while initializing, another would be done after catch broadcast.
                        sv?.pushBoolean(Commands.UPDATE_P04, true)
                        vp_content.currentItem = Constants.P04_CACHE
                    }
                    tabLayout.getTabAt(Constants.P06_UPLOAD) -> vp_content.currentItem = Constants.P06_UPLOAD
                    tabLayout.getTabAt(Constants.P07_SOCKET) -> vp_content.currentItem = Constants.P07_SOCKET
                    tabLayout.getTabAt(Constants.P12_WEBVIEW) -> vp_content.currentItem = Constants.P12_WEBVIEW
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        //set current tab
//        tabLayout.setScrollPosition(vp_content.adapter.count, 0f, true)
//        vp_content.currentItem = vp_content.adapter.count


        backKeyEventListener = ArrayList()
        for (i in 0 until tabLayout.tabCount) {
            backKeyEventListener?.add(null)
        }

        addBottomLayout(R.layout.bottom_main)
        fl_bottom.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, DeviceInfoActivity::class.java)
            startActivity(intent)
        }

        iv_github.setOnClickListener {
            callFragment(Constants.P14_FULL_WEBVIEW)
        }
        tv_github.setOnClickListener {
            callFragment(Constants.P14_FULL_WEBVIEW)
        }
        iv_menu.setOnClickListener {
            openSlideMenu()
        }
    }

    fun testKotlin() {
        val tmp = KotlinTemplate()
        tmp.basicSyntax()
        tmp.printSth()
        tmp.printSth("20f8-ads3bqwe-9d8vasd", "3f-s1v0m3")

        tmp.doRecursive()

        val player = Player()
        player.play("http://ws.stream.qqmusic.qq.com/C2000012Ppbd3hjGOK.m4a")
        player.pause()
        player.resume()
        player.seekTo(30000)
        player.stop()

        tmp.callJava()
        tmp.runOnNewThread()
    }

    fun testXML() {
        try {
            val xmlDelegate = XMLDelegate()
            xmlDelegate.read("name", assets.open("sample.xml"), SAXParser(), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:" + doc.asXML())
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:" + message)
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:" + message)
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })

            xmlDelegate.read("time", assets.open("sample.xml"), DOMParser(), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
            xmlDelegate.modify(assets.open("sample.xml"), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
            xmlDelegate.romove(assets.open("sample.xml"), object : XMLParserListener {
                override fun onSuccess(doc: Document) {
                    CLog.d(TAG, "onSuccess:${doc.asXML()}")
                }

                override fun onSuccess(message: String) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onSuccess(message: List<String>) {
                    CLog.d(TAG, "onSuccess:$message")
                }

                override fun onFail() {
                    CLog.d(TAG, "onFail")
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

