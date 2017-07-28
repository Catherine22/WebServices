package com.catherine.webservices.sample.data

/**
 * Created by Catherine on 2017/7/28.
 */

data class Person(var id: Int, var name: String, var age: Int)

//Mission.json
data class ReturnMsg(var en_US: String, var id_ID: String, var zh_TW: String)

data class Detail(var TaskStatus: String, var TaskSn: String, var Task_Desc: String,
                  var Active_Start_Date: String, var Active_End_Date: String,
                  var Receive_Start_Date: String, var Receive_End_Date: String,
                  var Gift_Name: String, var Gift_Icon: String, var Rule_Desc: String,
                  var Task_Memo: String, var Rule_DescAlign: String, var Task_MemoAlign: String
                  , var BgColor: String, var TitleColor: String, var FontColor: String
                  , var IsLoginFB: String, var Reward: String)

data class ItemsList(var ItemID: String, var ItemColumn: String, var ItemValue: String)
/**
 * 如果给BasicResult指定泛型，如果呼叫时直接代入比如Mission，会得到null，因为其实并没有做泛型，在编译时拿到的仍是BasicResult类型，所以要用Proxy
 */
data class BasicResult<Data>(var ReturnMsg: ReturnMsg, var ReturnNo: Int, var data: Data)

data class Mission(var Detail: Detail, var ItemsList: List<ItemsList>)

