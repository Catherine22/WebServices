package com.catherine.webservices.kotlin_sample.player

/**
 * Created by Catherine on 2017/7/28.
 * 扩展版本的enum，enum只能有一个实例（static），如果有多个实例，就用class，只有一个就用object
 * sealed的class构造方法是私有的，只能从内部继承
 */
sealed class PlayerCmd {
    class Play(var url: String, var position: Long = 0) : PlayerCmd()
    class Seek(var position: Long) : PlayerCmd()
    object Pause : PlayerCmd()
    object Resume : PlayerCmd()
    object Stop : PlayerCmd()
}