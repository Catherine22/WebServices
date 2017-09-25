package com.catherine.webservices.sample.singleton

/**
 * Created by Catherine on 2017/7/28.
 * Java做法详见https://github.com/Catherine22/DesignPattern/blob/master/src/com/catherine/singleton/SafeLazyInitializingSingleton.java
 */
class SafeLazyInitializingSingleton private constructor() {
    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SafeLazyInitializingSingleton()
        }
    }
}