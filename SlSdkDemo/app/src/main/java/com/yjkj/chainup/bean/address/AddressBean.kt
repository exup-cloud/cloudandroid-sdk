package com.yjkj.chainup.bean.address

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * 提现地址列表
 */
data class AddressBean(
        @SerializedName("addressList") val addressList: List<Address> = listOf(),
        @SerializedName("cryptoCoinList") val cryptoCoinList: List<CryptoCoin> = listOf()
) {
    data class CryptoCoin(
            @SerializedName("coinSymbol") val coinSymbol: String = "", //ETH
            @SerializedName("coinShow") val coinShow: String = "" //ETH
    )

    data class Address(
            @SerializedName("id") val id: Int = 0, //522
            @SerializedName("uid") val uid: Int = 0, //10609
            @SerializedName("symbol") val symbol: String ?= "", //BTC
            @SerializedName("address") val address: String ?= "", //1LJkiVMaXBW6F55g8MysUhdGgsrbTe92Zz
            @SerializedName("label") val label: String ?= "", //BTC
            @SerializedName("status") val status: Int = 0, //1
            @SerializedName("trustType") val trustType: Int = 0, //1
            @SerializedName("ctime") val ctime: Long = 0L //1529653630000
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readLong())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeInt(uid)
            parcel.writeString(symbol)
            parcel.writeString(address)
            parcel.writeString(label)
            parcel.writeInt(status)
            parcel.writeInt(trustType)
            parcel.writeLong(ctime)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Address> {
            override fun createFromParcel(parcel: Parcel): Address {
                return Address(parcel)
            }

            override fun newArray(size: Int): Array<Address?> {
                return arrayOfNulls(size)
            }
        }
    }
}