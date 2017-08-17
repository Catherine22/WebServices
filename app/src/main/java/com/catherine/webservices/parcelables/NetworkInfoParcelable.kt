package com.catherine.webservices.parcelables

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Catherine on 2017/8/17.
 */
data class NetworkInfoParcelable(
        var mNetworkType: Int = 0,
        var mSubtype: Int = 0,
        var mTypeName: String? = null,
        var mSubtypeName: String? = null,
        var mState: Int = 0,
        var mDetailedState: Int = 0,
        var mReason: String? = null,
        var mExtraInfo: String? = null,
        var mIsState: BooleanArray = kotlin.BooleanArray(5)) : Parcelable {



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        parcel.writeInt(mNetworkType)
        parcel.writeInt(mSubtype)
        parcel.writeString(mTypeName)
        parcel.writeString(mSubtypeName)
        parcel.writeInt(mState)
        parcel.writeInt(mDetailedState)
        parcel.writeString(mReason)
        parcel.writeString(mExtraInfo)
        parcel.writeBooleanArray(mIsState)
    }

    override fun describeContents(): Int {
        booleanArrayOf()
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NetworkInfoParcelable> {
        override fun createFromParcel(parcel: Parcel): NetworkInfoParcelable {
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            val p: NetworkInfoParcelable = NetworkInfoParcelable()
            p.mNetworkType = parcel.readInt()
            p.mSubtype = parcel.readInt()
            p.mTypeName = parcel.readString()
            p.mSubtypeName = parcel.readString()
            p.mState = parcel.readInt()
            p.mDetailedState = parcel.readInt()
            p.mReason = parcel.readString()
            p.mExtraInfo = parcel.readString()
            parcel.readBooleanArray(p.mIsState)
            return p
        }

        override fun newArray(size: Int): Array<NetworkInfoParcelable?> {
            return arrayOfNulls(size)
        }
    }

}