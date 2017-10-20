package com.catherine.webservices

import android.Manifest
import android.annotation.TargetApi
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import catherine.messagecenter.AsyncResponse
import catherine.messagecenter.Server
import com.catherine.webservices.adapters.MainViewPagerAdapter
import com.catherine.webservices.fragments.P05_Gallery
import com.catherine.webservices.fragments.P13_Nested_WebView
import com.catherine.webservices.fragments.P14_Full_WebView
import com.catherine.webservices.fragments.P15_WebView_Settings
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
class MainActivity : FragmentActivity(), MainInterface {

    companion object {
        private val TAG = "MainActivity"
    }

    private var listener: OnRequestPermissionsListener? = null
    private var sv: Server? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //StrictMode
//        if (BuildConfig.SHOW_LOG) {
//            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectCustomSlowCalls()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .penaltyDialog()
//                    .build())
//            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .detectActivityLeaks()
//                    .detectLeakedSqlLiteObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build())
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        getPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE), object : OnRequestPermissionsListener {
            override fun onGranted() {
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
                    CLog.d(TAG, "isNetworkHealth:${networkHelper.isNetworkHealth()}")
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


//        testKotlin()
//        testXML()
            }

            override fun onDenied(deniedPermissions: List<String>?) {
                val context = StringBuilder()
                deniedPermissions?.map {
                    if (Manifest.permission.WRITE_EXTERNAL_STORAGE == it) {
                        context.append("存储、")
                    }
                }

                context.deleteCharAt(context.length - 1)

                val myAlertDialog = AlertDialog.Builder(this@MainActivity)
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭") { _, _ -> this@MainActivity.finish() }
                        .setPositiveButton("确定开启") { _, _ ->
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", this@MainActivity.packageName, null))
                            startActivityForResult(intent, Constants.OPEN_SETTINGS)
                        }
                myAlertDialog.show()
            }

            override fun onRetry() {
                init()
            }
        })
    }

    //constants
    private val OPEN_SETTINGS = 1
    private val ACCESS_PERMISSION = 2
    private val PERMISSION_OVERLAY = 3
    private val PERMISSION_WRITE_SETTINGS = 4

    private val GRANTED_SAW = 0x0001     //同意特殊权限(SYSTEM_ALERT_WINDOW)
    private val GRANTED_WS = 0x0010      //同意特殊权限(WRITE_SETTINGS)
    private var requestSpec = 0x0000           //需要的特殊权限
    private var grantedSpec = 0x0000           //已取得的特殊权限
    private var confirmedSpec = 0x0000         //已询问的特殊权限
    private var deniedPermissionsList: MutableList<String> = LinkedList<String>() //被拒绝的权限

    /**
     * 要求用户打开权限,仅限android 6.0 以上
     *
     *
     * SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS, 这两个权限比较特殊，
     * 不能通过代码申请方式获取，必须得用户打开软件设置页手动打开，才能授权。
     *
     * @param permissions 手机权限 e.g. Manifest.permission.ACCESS_FINE_LOCATION
     * @param listener    此变量implements事件的接口,负责传递信息
     */
    @TargetApi(Build.VERSION_CODES.M)
    override fun getPermissions(permissions: Array<String>, listener: OnRequestPermissionsListener) {
        if (permissions.isEmpty() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onGranted()
            return
        }
        this.listener = listener
        for (p in permissions) {
            if (p == android.Manifest.permission.SYSTEM_ALERT_WINDOW) {
                requestSpec = requestSpec or GRANTED_SAW
                if (android.provider.Settings.canDrawOverlays(this@MainActivity))
                    grantedSpec = grantedSpec or GRANTED_SAW
            } else if (p == android.Manifest.permission.WRITE_SETTINGS) {
                requestSpec = requestSpec or GRANTED_WS
                if (android.provider.Settings.System.canWrite(this@MainActivity))
                    grantedSpec = grantedSpec or GRANTED_WS
            } else if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionsList.add(p)
            }

        }

        if (requestSpec != grantedSpec) {
            getASpecPermission(requestSpec)
        } else {// Granted all of the special permissions
            if (deniedPermissionsList.size != 0) {
                //Ask for the permissions
                val deniedPermissions = arrayOfNulls<String>(deniedPermissionsList.size)
                for (i in 0 until deniedPermissionsList.size) {
                    deniedPermissions[i] = deniedPermissionsList[i]
                }
                ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION)
            } else {
                listener.onGranted()

                requestSpec = 0x0000
                grantedSpec = 0x0000
                confirmedSpec = 0x0000
                deniedPermissionsList.clear()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getASpecPermission(permissions: Int) {
        if (permissions and GRANTED_SAW == GRANTED_SAW && permissions and grantedSpec != GRANTED_SAW) {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this@MainActivity.packageName))
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY)
        }

        if (permissions and GRANTED_WS == GRANTED_WS && permissions and grantedSpec != GRANTED_WS) {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + this@MainActivity.packageName))
            startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Press home key then click icon to launch while checking permission
        if (permissions.isEmpty()) {
            requestSpec = 0x0000
            grantedSpec = 0x0000
            confirmedSpec = 0x0000
            deniedPermissionsList.clear()
            listener?.onRetry()
            return
        }

        val deniedResults = grantResults.indices
                .filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
                .mapTo(ArrayList()) { permissions[it] }

        if (requestSpec and GRANTED_WS == GRANTED_WS && grantedSpec and GRANTED_WS != GRANTED_WS)
            deniedResults.add(Manifest.permission.WRITE_SETTINGS)

        if (requestSpec and GRANTED_SAW == GRANTED_SAW && grantedSpec and GRANTED_SAW != GRANTED_SAW)
            deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW)


        if (deniedResults.size != 0)
            listener?.onDenied(deniedResults)
        else {
            MyApplication.INSTANCE.init()
            listener?.onGranted()
        }

        requestSpec = 0x0000
        grantedSpec = 0x0000
        confirmedSpec = 0x0000
        deniedPermissionsList.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        CLog.d(TAG, "request:$requestCode/resultCode$resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PERMISSION_OVERLAY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                confirmedSpec = confirmedSpec or GRANTED_SAW
                confirmedSpec = confirmedSpec or grantedSpec
                if (android.provider.Settings.canDrawOverlays(this))
                    grantedSpec = grantedSpec or GRANTED_SAW
                if (confirmedSpec == requestSpec) {
                    if (deniedPermissionsList.size != 0) {
                        //Ask for the permissions
                        val deniedPermissions = arrayOfNulls<String>(deniedPermissionsList.size)
                        for (i in 0 until deniedPermissionsList.size) {
                            deniedPermissions[i] = deniedPermissionsList[i]
                        }
                        ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION)
                    } else {
                        val deniedResults = ArrayList<String>()
                        if (requestSpec and GRANTED_WS == GRANTED_WS && grantedSpec and GRANTED_WS != GRANTED_WS)
                            deniedResults.add(Manifest.permission.WRITE_SETTINGS)

                        if (requestSpec and GRANTED_SAW == GRANTED_SAW && grantedSpec and GRANTED_SAW != GRANTED_SAW)
                            deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW)

                        if (deniedResults.size > 0)
                            listener?.onDenied(deniedResults)
                        else {
                            listener?.onGranted()
                        }

                        requestSpec = 0x0000
                        grantedSpec = 0x0000
                        confirmedSpec = 0x0000
                        deniedPermissionsList.clear()
                    }
                }
            }
            PERMISSION_WRITE_SETTINGS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                confirmedSpec = confirmedSpec or GRANTED_WS
                confirmedSpec = confirmedSpec or grantedSpec
                if (android.provider.Settings.System.canWrite(this))
                    grantedSpec = grantedSpec or GRANTED_WS
                if (confirmedSpec == requestSpec) {
                    if (!deniedPermissionsList.isEmpty()) {
                        //Ask for the permissions
                        val deniedPermissions = arrayOfNulls<String>(deniedPermissionsList.size)
                        for (i in 0 until deniedPermissionsList.size) {
                            deniedPermissions[i] = deniedPermissionsList.get(i)
                        }
                        ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION)
                    } else {
                        val deniedResults = ArrayList<String>()
                        if (requestSpec and GRANTED_WS == GRANTED_WS && grantedSpec and GRANTED_WS != GRANTED_WS)
                            deniedResults.add(Manifest.permission.WRITE_SETTINGS)

                        if (requestSpec and GRANTED_SAW == GRANTED_SAW && grantedSpec and GRANTED_SAW != GRANTED_SAW)
                            deniedResults.add(Manifest.permission.SYSTEM_ALERT_WINDOW)

                        if (deniedResults.size > 0)
                            listener?.onDenied(deniedResults)
                        else {
                            MyApplication.INSTANCE.init()
                            listener?.onGranted()
                        }
                        requestSpec = 0x0000
                        grantedSpec = 0x0000
                        confirmedSpec = 0x0000
                        deniedPermissionsList.clear()
                    }
                }
            }
            OPEN_SETTINGS -> {
                requestSpec = 0x0000
                grantedSpec = 0x0000
                confirmedSpec = 0x0000
                deniedPermissionsList.clear()
                listener?.onRetry()
            }
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

        //has bundle
            Constants.P14_FULL_WEBVIEW -> {
                title = "P14_Full_WebView"
                fragment = P14_Full_WebView.newInstance(true)
                tag = "P14"
            }
        }

        //Avoid to launch duplicated fragments
        if (fm.backStackEntryCount > 0 && fm.fragments[fm.fragments.size].tag == tag) {
            return
        }

        if (bundle != null)
            fragment?.arguments = bundle

        CLog.d(TAG, "call $id ,has bundle? ${bundle == null}")
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
        onBackPressed()
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
        left_drawer.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, pos, p3 ->
            when (pos) {
                0 -> {
                    callFragment(Constants.P14_FULL_WEBVIEW)
                }
                1 -> {
                    callFragment(Constants.P15_WEBVIEW_SETTINGS)
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

