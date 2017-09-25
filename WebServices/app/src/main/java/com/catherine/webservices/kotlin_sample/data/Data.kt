package com.catherine.webservices.kotlin_sample.data

/**
 * Created by Catherine on 2017/7/28.
 */

data class Person(var id: Int, var name: String, var age: Int)

/**
 * var name: String?后面的问号代表可以允许null并且不报错
 *注解JvmField后，该成员不能定义getter和setter，同时不会是private，呼叫时直接person.age
 */
data class Person2(var id: Int, var name: String?, @JvmField var age: Int)
