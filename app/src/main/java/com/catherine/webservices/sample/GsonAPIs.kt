package com.catherine.webservices.sample

import com.catherine.webservices.sample.data.BasicResult
import com.catherine.webservices.sample.data.ItemsList
import com.catherine.webservices.sample.data.Mission
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Proxy

/**
 * Created by Catherine on 2017/7/28.
 */
interface MissionApi {
    fun getMissionFromJson(json: String): BasicResult<Mission>
}

interface SimpleMissionApi {
    fun getSimpleMissionFromJson(json: String): BasicResult<ItemsList>
}

object ApiFactory {
    val mission: MissionApi by lazy {
        Proxy.newProxyInstance(ApiFactory.javaClass.classLoader, arrayOf(MissionApi::class.java)) {
            proxy, method, args ->
            val responseType = method.genericReturnType
            val adapter = Gson().getAdapter(TypeToken.get(responseType))
            adapter.fromJson(args[0].toString())
        } as MissionApi
    }

    val simpleMission: SimpleMissionApi by lazy {
        Proxy.newProxyInstance(ApiFactory.javaClass.classLoader, arrayOf(SimpleMissionApi::class.java)) {
            proxy, method, args ->
            val responseType = method.genericReturnType
            val adapter = Gson().getAdapter(TypeToken.get(responseType))
            adapter.fromJson(args[0].toString())
        } as SimpleMissionApi
    }
}