package com.yjkj.chainup.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RegStep2Bean(
        @SerializedName("invitationCode_required")
        val invitationCodeRequired: Int, // 0
        @SerializedName("titleKey")
        val titleKey: String?="", // termsService
        @SerializedName("url")
        val url: String ?=""// https://m.chaindown.com/noticeDetail?id=terms&type=cms&isapp=1
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString(),
                parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(invitationCodeRequired)
                parcel.writeString(titleKey)
                parcel.writeString(url)
        }

        override fun describeContents(): Int {
                return 0
        }

        override fun toString(): String {
                return "RegStep2Bean(invitationCodeRequired=$invitationCodeRequired, titleKey='$titleKey', url='$url')"
        }

        companion object CREATOR : Parcelable.Creator<RegStep2Bean> {
                override fun createFromParcel(parcel: Parcel): RegStep2Bean {
                        return RegStep2Bean(parcel)
                }

                override fun newArray(size: Int): Array<RegStep2Bean?> {
                        return arrayOfNulls(size)
                }
        }
}