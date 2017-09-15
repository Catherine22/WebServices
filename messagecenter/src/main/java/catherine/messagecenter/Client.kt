package catherine.messagecenter

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager


/**
 * Created by Catherine on 2017/9/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class Client(ctx: Context, cr: CustomReceiver) {
    private var ctx: Context? = null
    private var result: Result? = null
    private var cr: CustomReceiver? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var broadcastManager: LocalBroadcastManager? = null


    /**
     * Before you use gotMessages(), you should initialize this constructor
     *
     */
    init {
        this.ctx = ctx
        this.cr = cr
        this.result = Result()
    }

    /**
     * Register to receive messages.
     *
     * @param action ID
     */
    fun gotMessages(action: String) {
        broadcastManager = LocalBroadcastManager.getInstance(ctx)
        val intentFilter = IntentFilter()
        intentFilter.addAction(action)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Config.messagesList[action] != null) {
                    CLog.d(Config.TAG, "($action)You got a message")
                    when {
                        Config.messagesList[action] == "MESSAGES_BUNDLE" -> {
                            result?.mBundle = intent.getBundleExtra("MESSAGES_BUNDLE")
                            cr?.onBroadcastReceive(result!!)
                        }
                        Config.messagesList[action] == "MESSAGES_BOOLEAN" -> {
                            result?.mBoolean = intent.getBooleanExtra("MESSAGES_BOOLEAN", false)
                            cr?.onBroadcastReceive(result!!)
                        }
                        Config.messagesList[action] == "MESSAGES_STRING" -> {
                            result?.mString = intent.getStringExtra("MESSAGES_STRING")
                            cr?.onBroadcastReceive(result!!)
                        }
                        Config.messagesList[action] == "MESSAGES_INT" -> {
                            result?.mInt = intent.getIntExtra("MESSAGES_INT", -1)
                            cr?.onBroadcastReceive(result!!)
                        }
                        Config.messagesList[action] == "MESSAGES_DOUBLE" -> {
                            result?.mDouble = intent.getDoubleExtra("MESSAGES_DOUBLE", 0.0)
                            cr?.onBroadcastReceive(result!!)
                        }
                    }
                }
            }
        }
        broadcastManager!!.registerReceiver(broadcastReceiver, intentFilter)
    }


    fun unRegister(actionName: String) {
        Config.messagesList.remove(actionName)
    }

    /**
     * You should unregister receiver when destroy app
     */
    fun release() {
        if (broadcastReceiver != null)
            broadcastManager!!.unregisterReceiver(broadcastReceiver)
    }
}