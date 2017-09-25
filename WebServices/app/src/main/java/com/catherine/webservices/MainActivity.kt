package com.catherine.webservices

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import com.catherine.webservices.adapters.MainViewPagerAdapter
import com.catherine.webservices.fragments.P05_Gallery
import com.catherine.webservices.interfaces.BackKeyListener
import com.catherine.webservices.interfaces.MainInterface
import com.catherine.webservices.interfaces.OnRequestPermissionsListener
import com.catherine.webservices.network.NetworkHealthListener
import com.catherine.webservices.network.NetworkHelper
import com.catherine.webservices.kotlin_sample.KotlinTemplate
import com.catherine.webservices.kotlin_sample.player.Player
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()

        val checkStateWork = Handler(MyApplication.INSTANCE.calHandlerThread.looper)
        checkStateWork.post {
            val networkHelper = NetworkHelper(this)
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
                MyApplication.INSTANCE.init()
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
     * @param id Tag of the Fragment
     */
    override fun callFragment(id: Int) {
        CLog.d(TAG, "call " + id)
        var fragment: Fragment? = null
        var tag: String? = null
        var title = ""
        when (id) {
            Constants.P05_Gallery -> {
                title = "P05_Gallery"
                fragment = P05_Gallery.newInstance(true)
                tag = "P05"
            }
        }
        val transaction = fm.beginTransaction()
        transaction.add(R.id.fl_container, fragment, tag)
        transaction.addToBackStack(title)
        transaction.commitAllowingStateLoss()
    }


    /**
     * Clear all fragments in stack
     */
    override fun clearAllFragments() {
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    /**
     * Simulate BackKey event
     */
    override fun backToPreviousPage() {
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()

        } else
            onBackPressed()
    }

    private var backKeyEventListener: MutableList<BackKeyListener?>? = null
    override fun setBackKeyListener(listener: BackKeyListener) {
        backKeyEventListener?.set(vp_content.currentItem, listener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backKeyEventListener?.get(vp_content.currentItem) != null) {
                backKeyEventListener?.get(vp_content.currentItem)?.OnKeyDown()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setView() {
        vp_content.adapter = MainViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(vp_content)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab) {
                    tabLayout.getTabAt(0) -> vp_content.currentItem = 0
                    tabLayout.getTabAt(1) -> vp_content.currentItem = 1
                    tabLayout.getTabAt(2) -> vp_content.currentItem = 2
                    tabLayout.getTabAt(3) -> vp_content.currentItem = 3
                    tabLayout.getTabAt(4) -> vp_content.currentItem = 4
                    tabLayout.getTabAt(5) -> vp_content.currentItem = 5
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        //set current tab
        tabLayout.setScrollPosition(vp_content.adapter.count,0f,true)
        vp_content.currentItem = vp_content.adapter.count


        backKeyEventListener = ArrayList<BackKeyListener?>()
        for (i in 0 until tabLayout.tabCount) {
            backKeyEventListener?.add(null)
        }

        tv_info.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, P00_DeviceInfo::class.java)
            startActivity(intent)
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
