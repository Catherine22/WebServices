package com.catherine.webservices.toolkits

import android.util.Log

/**
 * Created by Catherine on 2017/7/27.
 */
class KotlinTemplate {
    companion object {
        var TAG = "KotlinTemplate"
    }

    /**
     *无返回值可直接省略": Unit"，但其实返回的是Unit类型
     */
    fun printSth(): Unit {
        //在enum里定义方法并呼叫
        var lang = Language.parse("English")
        lang.sayHello()

        var kris = Person(0, "Kris", 10)
        Log.v(TAG, kris.toString())

        var caroline = Person(1, "Caroline", 12)
//        var kids = Array<Person>(10, 0 -> kris)

        //扩展enum，加入新方法
        lang.sayBye()
    }

    /**
     * 带入多参数，等同于Array<String>
     */
    fun printSth(vararg args: String) {
        args.flatMap {
            //flatMap 把全部的参数都给加到一个集合里
            it.split("-")
        }.map {
            //map 差不多就是个for循环
            Log.v(TAG, "$it, length=${it.length}") //it代表元素本身
        }
    }

}

data class Person(var id: Int, var name: String, var age: Int)


//加上var代表参数
enum class Language(var hello: String, var hey: String) {
    ENGLISH("Hello!", "Hey!"),
    SIMPLIFIED_CHINESE("你好啊!", "嘿！");

    fun sayHello() {
        Log.v(KotlinTemplate.TAG, hello)
    }

    fun sayHey() {
        Log.v(KotlinTemplate.TAG, hey)
    }

    //构造方法
    init {
        Log.d(KotlinTemplate.TAG, "呼叫enum构造方法")
    }

    companion object {
        fun parse(name: String): Language {
            return Language.valueOf(name.toUpperCase())
        }
    }
}

/**
 * 假设enum不能更改
 */
fun Language.sayBye() {
    var bye = when (this) {
        Language.ENGLISH -> "See you!"
        Language.SIMPLIFIED_CHINESE -> "再见!"
    }
    Log.v(KotlinTemplate.TAG, bye)
}