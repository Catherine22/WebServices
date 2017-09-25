package catherine.messagecenter

import android.util.Log

/**
 * Created by Catherine on 2017/9/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
internal class CLog {
    companion object {
        private val DEBUG = Config.SHOW_LOG
        fun getTag(): String {
            var tag = ""
            val ste = Thread.currentThread().stackTrace
            ste.indices
                    .filter { ste[it].methodName == "getTag" }
                    .forEach { tag = "(${ste[it + 1].fileName}:${ste[it + 1].lineNumber})" }
            return tag
        }

        fun v(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.v(tag, message)
        }

        fun d(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.d(tag, message)
        }

        fun i(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.i(tag, message)
        }

        fun w(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.w(tag, message)
        }

        fun e(tag: String = "Default TAG", message: String = "Default message") {
            if (DEBUG)
                Log.e(tag, message)
        }
    }
}