package com.catherine.webservices

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import catherine.messagecenter.AsyncResponse
import catherine.messagecenter.Server
import com.catherine.webservices.adapters.MainViewPagerAdapter
import com.catherine.webservices.components.DialogManager
import com.catherine.webservices.components.MyDialogFragment
import com.catherine.webservices.fragments.OkHttp3Fragment
import com.catherine.webservices.fragments.cache.GalleryFragment
import com.catherine.webservices.fragments.cellular_wifi.*
import com.catherine.webservices.fragments.webview.FullWebViewFragment
import com.catherine.webservices.fragments.webview.WebViewSettingsFragment
import com.catherine.webservices.fragments.webview.WebViewHistoryFragment
import com.catherine.webservices.fragments.webview.WebViewTestListFragment
import com.catherine.webservices.interfaces.*
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
    private var networkHealthListeners: MutableList<NetworkHealthListener?>? = null

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
            val networkHelper = NetworkHelper()
            CLog.d(TAG, "isNetworkHealthy:${NetworkHelper.isNetworkHealthy()}")
            CLog.d(TAG, "isWifi:${networkHelper.isWifi}")
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
        if (position < resources.getStringArray(R.array.tab_array).size) {
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
        val tag = Constants.Fragments.TAG(id)
        val title = Constants.Fragments.TITLE(id)
        when (id) {

        //null bundle
            Constants.Fragments.F_Gallery -> {
                fragment = GalleryFragment.newInstance(true)
            }


            Constants.Fragments.F_WEBVIEW_SETTINGS -> {
                fragment = WebViewSettingsFragment.newInstance(true)
            }

            Constants.Fragments.F_WEBVIEW_HISTORY -> {
                fragment = WebViewHistoryFragment.newInstance(true)
            }

            Constants.Fragments.F_WEBVIEW_TEST_LIST -> {
                fragment = WebViewTestListFragment.newInstance(true)
            }

            Constants.Fragments.F_NETWORK_ANALYTICS -> {
                fragment = NetworkAnalyticsFragment.newInstance(true)
            }

            Constants.Fragments.F_OKHTTP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    fragment = OkHttp3Fragment.newInstance(true)
                } else {
                    DialogManager.showAlertDialog(MainActivity@ this, "Can not run okHttp3 due to low API level.\n The min API level must be 19 (KITKAT).") { _, _ -> }
                }
            }


        //has bundle
            Constants.Fragments.F_FULL_WEBVIEW -> {
                fragment = FullWebViewFragment.newInstance(true)
            }

            Constants.Fragments.F_NETWORK_INFO_ANALYTICS -> {
                fragment = NetworkInfoAnalyticsFragment.newInstance(true)
            }

            Constants.Fragments.F_WIFI_CONFIGURATION_ANALYTICS -> {
                fragment = WifiConfigurationAnalyticsFragment.newInstance(true)
            }
            Constants.Fragments.F_WIFI_INFO -> {
                fragment = WifiInfoFragment()
            }
            Constants.Fragments.F_SCAN_RESULT_INFO -> {
                fragment = ScanResultInfoFragment()
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
     * 开启fragment dialog
     *
     * @param id Tag of the Fragment Dialog
     */
    override fun callFragmentDialog(id: Int) {
        callFragmentDialog(id, null)
    }

    /**
     * 开启fragment dialog
     *
     * @param id Tag of the Fragment Dialog
     * @param bundle argument of the Fragment Dialog
     */
    override fun callFragmentDialog(id: Int, bundle: Bundle?) {
        CLog.d(TAG, "call " + id + " with bundle? " + (bundle != null))
        var fragment: MyDialogFragment? = null
        val tag = Constants.Fragments.TAG(id)
        when (id) {
            Constants.Fragments.F_D_SCAN_RESULT -> {
                fragment = ScanResultDialog()
            }
            Constants.Fragments.F_D_WIFI_CONFIGURATIONS -> {
                fragment = WifiConfigurationsDialog()
            }
        }

        if (bundle != null)
            fragment?.arguments = bundle

        if (fragment != null) {
            val trans = supportFragmentManager.beginTransaction()
            trans.addToBackStack(null)
            fragment.show(trans, tag)
        }
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

    override fun removeBackKeyListener() {
        backKeyEventListener?.set(vp_content.currentItem, null)
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
                    callFragment(Constants.Fragments.F_FULL_WEBVIEW)
                }
                1 -> {
                    callFragment(Constants.Fragments.F_NETWORK_ANALYTICS)
                }
                2 -> {
                    callFragment(Constants.Fragments.F_WEBVIEW_SETTINGS)
                }
                3 -> {
                    callFragment(Constants.Fragments.F_WEBVIEW_HISTORY)
                }
            }
            //            left_drawer.setItemChecked(pos, true)
            drawer_layout.closeDrawer(left_drawer)
        }

        vp_content.adapter = MainViewPagerAdapter(MainActivity@ this, supportFragmentManager)
        tabLayout.setupWithViewPager(vp_content)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                restoreBottomLayout()
                when (tab) {
                    tabLayout.getTabAt(Constants.Fragments.F_APACHE) -> vp_content.currentItem = Constants.Fragments.F_APACHE
                    tabLayout.getTabAt(Constants.Fragments.F_HTTP_URL_CONNECTION) -> vp_content.currentItem = Constants.Fragments.F_HTTP_URL_CONNECTION
                    tabLayout.getTabAt(Constants.Fragments.F_OKHTTP) -> vp_content.currentItem = Constants.Fragments.F_OKHTTP
                    tabLayout.getTabAt(Constants.Fragments.F_DOWNLOADER) -> vp_content.currentItem = Constants.Fragments.F_DOWNLOADER
                    tabLayout.getTabAt(Constants.Fragments.F_CACHE) -> {
                        //Push broadcast before initialize so the broadcast won't be captured at first time.
                        //So I update view twice - first one would be done while initializing, another would be done after catch broadcast.
                        sv?.pushBoolean(Commands.UPDATE_P04, true)
                        vp_content.currentItem = Constants.Fragments.F_CACHE
                    }
                    tabLayout.getTabAt(Constants.Fragments.F_UPLOAD) -> vp_content.currentItem = Constants.Fragments.F_UPLOAD
                    tabLayout.getTabAt(Constants.Fragments.F_SOCKET) -> vp_content.currentItem = Constants.Fragments.F_SOCKET
                    tabLayout.getTabAt(Constants.Fragments.F_WEBVIEW) -> vp_content.currentItem = Constants.Fragments.F_WEBVIEW
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

        networkHealthListeners = ArrayList()
        backKeyEventListener = ArrayList()
        for (i in 0 until vp_content.adapter.count) {
            backKeyEventListener?.add(null)
        }

        addBottomLayout(R.layout.bottom_main)
        fl_bottom.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, DeviceInfoActivity::class.java)
            startActivity(intent)
        }

        iv_github.setOnClickListener {
            callFragment(Constants.Fragments.F_FULL_WEBVIEW)
        }
        tv_github.setOnClickListener {
            callFragment(Constants.Fragments.F_FULL_WEBVIEW)
        }
        iv_menu.setOnClickListener {
            openSlideMenu()
        }
    }

    override fun listenToNetworkState(listener: NetworkHealthListener) {
        networkHealthListeners?.add(listener)
    }


    override fun stopListeningToNetworkState(listener: NetworkHealthListener) {
        networkHealthListeners?.remove(listener)
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

