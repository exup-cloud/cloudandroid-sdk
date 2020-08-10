package com.yjkj.chainup.bean.coin

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

data class RateBean(
        @SerializedName("rate") val rate: TreeMap<String, TreeMap<String, String>>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readSerializable() as TreeMap<String, TreeMap<String, String>>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(rate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RateBean> = object : Parcelable.Creator<RateBean> {
            override fun createFromParcel(source: Parcel): RateBean = RateBean(source)
            override fun newArray(size: Int): Array<RateBean?> = arrayOfNulls(size)
        }
    }
}
