package com.catherine.webservices.sample

import com.catherine.webservices.toolkits.CLog
import java.util.*

/**
 * Created by Catherine on 2017/7/31.
 */
object KotlinEntrance {
    val TAG = "KotlinEntrance"
    fun printHello() {
        CLog.v(TAG, "你好啊！")
    }

    fun printGenerics() {
        var composite = ArrayList<Any>()
        composite.add("我是String")
        composite.add(false)
        composite.add(12)
        composite.map(::println)
    }
}

class KotlinDynamicEntrance {

    fun printParameters(a: Int, b: String) {
        CLog.v(KotlinEntrance.TAG, "a:$a, b:$b")
    }

    @JvmOverloads
    fun printOptionalParameters(a: Int = 0, b: String = "default", c: Int = 0, d: String = "default") {
        CLog.v(KotlinEntrance.TAG, "a:$a, b:$b, c:$c, d:$d")

    }
}