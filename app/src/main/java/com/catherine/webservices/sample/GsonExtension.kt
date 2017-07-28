package com.catherine.webservices.sample

import com.google.gson.Gson

/**
 * Created by Catherine on 2017/7/28.
 */
/**
 * 传入一个泛型参数T，返回T类型
 * 之所以要把T指定为Any（也就是Java里面的Objcet是因为class.java类有限制必须是Any类型）
 * inline
 */
inline fun <reified T : Any> Gson.fromJson(json: String): T {
    //取得T的class传入GSON自带的fromJson方法里
    return fromJson<T>(json, T::class.java)
}