package com.catherine.webservices.kotlin_sample

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Catherine on 2017/7/31.
 */
class SubAbsEntrance : JavaAbsEntrance() {
    //看到!代表可以为null，亦可以不是null，也就是要不要加?自己决定
    override fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.CHINA).format(date)
    }
}