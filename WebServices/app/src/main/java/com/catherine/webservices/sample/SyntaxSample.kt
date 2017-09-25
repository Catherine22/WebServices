package com.catherine.webservices.sample

import com.catherine.webservices.sample.data.Person
import com.catherine.webservices.sample.data.Person2
import com.catherine.webservices.sample.data.Person3
import com.catherine.webservices.toolkits.CLog
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by Catherine on 2017/7/27.
 */
class KotlinTemplate {
    companion object {
        val TAG = "KotlinTemplate"
    }

    fun basicSyntax() {
        //用arrayOf、arrayListOf、setOf等创建集合，利用map迭代
        val colors = arrayOf("Red", "Blue", "Orange")
        colors.map {
            //it就是迭代
            CLog.v(TAG, it)
        }

        //因为不可指定null，编译失败
        //val s1: String = null

        //编译可以过，实际运行s2时还是会报错，空指针
        val s2: String? = null

        //因为有可能是null，编译失败
        //CLog.i(TAG, s2)

        //当s2不是null时才调用，若为空值抛出异常
        //CLog.i(TAG, s2!!)

        val caroline = Person2(1, null, 12)
        //当caroline.name为null时，返回"Caroline"
        CLog.v(TAG, caroline.name ?: "Caroline")
    }

    /**
     *无返回值可直接省略": Unit"，但其实返回的是Unit类型
     */
    fun printSth(): Unit {
        //在enum里定义方法并呼叫
        val lang = Language.parse("English")
        lang.sayHello()

        val kris = Person(0, "Kris", 10)
        CLog.v(TAG, kris.toString())

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
            CLog.v(TAG, "$it, length=${it.length}") //it代表元素本身
        }
    }

    fun doRecursive() {
        val rec = TailrecSample()
        CLog.v(TAG, "5! = ${rec.factorial1(5)}")

        val result = TailrecResult()
        rec.factorial2(10000, result)
        CLog.v(TAG, "10000! = ${result.value}")
    }

    fun callJava() {
        val entrance = JavaEntrance()
        entrance.printDiana()
        entrance.printLavender()

        //Java调用Kotlin
        entrance.callKotlin()

        val alma = Person3()
        alma.age = 12
        alma.id = 3

        alma.name = "Alma"
        CLog.v(TAG, alma.toString())

        //继承Java父类
        val abs = SubAbsEntrance()
        val today = Date()
        CLog.v(TAG, abs.formatDate(today))
        CLog.v(TAG, abs.formatTime(today))

        //Java、Kotlin泛型
        entrance.printGenerics()
    }

    fun runOnNewThread() {
        CLog.v(TAG, "current thread:${Thread.currentThread().name}")

        val work = Executors.newCachedThreadPool()
        work.execute {
            CLog.v(TAG, "executing work, current thread:${Thread.currentThread().name}")
        }

        val runnable = {
            CLog.v(TAG, "executing runnable")
        }

        //坑！！！传入Java时，每次添加或移除的都是同一个对象，可是打印后发现实际上都是不同对象，因为传到Java那边会做一个转换
        val entrance = JavaEntrance()
        entrance.addRunnable(runnable)
        entrance.addRunnable(runnable)
        entrance.addRunnable(runnable)
        entrance.addRunnable(runnable)
        entrance.addRunnable(runnable)
        entrance.removeRunnable(runnable)
        entrance.removeRunnable(runnable)
        entrance.removeRunnable(runnable)
        entrance.removeRunnable(runnable)
        entrance.removeRunnable(runnable)
    }
}

//加上var代表参数
enum class Language(var hello: String, var hey: String) {
    ENGLISH("Hello!", "Hey!"),
    SIMPLIFIED_CHINESE("你好啊!", "嘿！");

    fun sayHello() {
        CLog.v(KotlinTemplate.TAG, hello)
    }

    fun sayHey() {
        CLog.v(KotlinTemplate.TAG, hey)
    }

    //构造方法
    init {
        CLog.d(KotlinTemplate.TAG, "呼叫enum构造方法")
    }

    companion object {
        fun parse(name: String): Language {
            return Language.valueOf(name.toUpperCase())
        }
    }
}

/**
 * 扩展方法，假设enum不能更改，只需在方法名前面加上类名。
 */
fun Language.sayBye() {
    val bye = when (this) {
        Language.ENGLISH -> "See you!"
        Language.SIMPLIFIED_CHINESE -> "再见!"
    }
    CLog.v(KotlinTemplate.TAG, bye)
}
