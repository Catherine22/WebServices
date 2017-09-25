package com.catherine.webservices.sample.singleton

/**
 * Created by Catherine on 2017/7/28.
 * Java做法详见https://github.com/Catherine22/DesignPattern/blob/master/src/com/catherine/singleton/LazyInitializingSingleton.java
 */
class LazyInitializingSingleton {
    companion object {
        //线程不安全
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.NONE) {
            LazyInitializingSingleton()
        }
    }
}