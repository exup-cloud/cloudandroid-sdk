package com.yjkj.chainup.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/22-下午4:05
 * @Email buptjinlong@163.com
 * @description
 */
data class LanguageListBean(
        @SerializedName("name") val name: String ?= "",//语言
        @SerializedName("open") var open: Boolean,//是否当前语言
        @SerializedName("id") val id: String ?= ""//对应的key
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (open) 1 else 0)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LanguageListBean> {
        override fun createFromParcel(parcel: Parcel): LanguageListBean {
            return LanguageListBean(parcel)
        }

        override fun newArray(size: Int): Array<LanguageListBean?> {
            return arrayOfNulls(size)
        }
    }

}