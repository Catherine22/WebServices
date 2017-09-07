package catherine.messagecenter

import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import android.content.Intent
import android.os.Bundle


/**
 * Created by Catherine on 2017/9/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class Server {
    private var ctx: Context? = null
    private var ar: AsyncResponse? = null

    /**
     * Before you use pushTypes(), you should initialize this constructor
     *
     * @param ctx Your Context
     */
    fun Server(ctx: Context, ar: AsyncResponse) {
        this.ctx = ctx
        this.ar = ar
    }

    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send bundle with broadcast
     */
    fun pushBundle(action: String?, messages: Bundle?) {
        if (action == null || messages == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_BUNDLE") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_BUNDLE", messages)
        Config.messagesList.put(action, "MESSAGES_BUNDLE")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }


    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send boolean with broadcast
     */
    fun pushBoolean(action: String?, messages: Boolean) {
        if (action == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_BOOLEAN") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_BOOLEAN", messages)
        Config.messagesList.put(action, "MESSAGES_BOOLEAN")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }


    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send String with broadcast
     */
    fun pushString(action: String?, messages: String?) {
        if (action == null || messages == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_STRING") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_STRING", messages)
        Config.messagesList.put(action, "MESSAGES_STRING")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }


    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send int with broadcast
     */
    fun pushInt(action: String?, messages: Int) {
        if (action == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_INT") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_INT", messages)
        Config.messagesList.put(action, "MESSAGES_INT")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }


    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send double with broadcast
     */
    fun pushDouble(action: String?, messages: Double) {
        if (action == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_DOUBLE") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_DOUBLE", messages)
        Config.messagesList.put(action, "MESSAGES_DOUBLE")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }

    /**
     * Send a message with broadcast
     *
     * @param action   ID
     * @param messages Send byte with broadcast
     */
    fun pushByte(action: String?, messages: Byte) {
        if (action == null) {
            ar?.onFailure(ErrorMessages.NULL_POINTER)
            return
        }
        if (Config.messagesList.containsKey(action)) {
            val key = Config.messagesList.get(action)
            if (key != "MESSAGES_BYTE") {
                ar?.onFailure(ErrorMessages.MULTIPLE_VALUE)
                CLog.e(Config.TAG, "MULTIPLE_VALUE:" + action)
                return
            }
        }
        val broadcast = Intent(action)
        broadcast.putExtra("MESSAGES_BYTE", messages)
        Config.messagesList.put(action, "MESSAGES_BYTE")
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(broadcast)
    }
}