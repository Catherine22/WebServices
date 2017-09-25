package com.catherine.webservices.kotlin_sample.singleton

/**
 * Created by Catherine on 2017/7/28.
 * 加载BillPughSingleton时，并没有加载SingletonHolder，这就是为啥能延迟加载。
 * 为什么是线程安全是因为虚拟机有对类加载做处理，确保类加载一定是线程安全。
 * Java做法详见https://github.com/Catherine22/DesignPattern/blob/master/src/com/catherine/singleton/BillPughSingleton.java
 */
class BillPughSingleton private constructor() {

    private object SingletonHolder {
        val INSTANCE = BillPughSingleton

    }

    companion object {
        fun getINSTANCE() {
            SingletonHolder.INSTANCE
        }
    }
}