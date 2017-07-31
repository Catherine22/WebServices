package com.catherine.webservices.sample

import com.catherine.webservices.toolkits.CLog

/**
 * Created by Catherine on 2017/7/31.
 */
/**
 * 其实就是生成一个FunctionSetKt的类和一个printFunInPackage的静态方法
 */
fun printFunInPackage() {
    CLog.v(KotlinEntrance.TAG, "我是包下的方法")
}

/**
 * 扩展方法，给String类添加一个自定义的isEmpty方法
 */
fun String.isEmpty(): Boolean {
    return this == ""
}