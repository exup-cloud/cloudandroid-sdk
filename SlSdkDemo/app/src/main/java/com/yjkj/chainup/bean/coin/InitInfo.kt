package com.yjkj.chainup.bean.coin

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.yjkj.chainup.bean.AppLanguageBean
import java.util.*
import kotlin.collections.ArrayList

/**
 * 	"klineScale": ["1min", "5min", "15min", "30min", "60min", "4h", "1day", "1week", "1month"],
 */
data class InitInfo(
        @SerializedName("coinList") val coinList: TreeMap<String, CoinBean>,
        @SerializedName("market") val market: TreeMap<String, TreeMap<String, CoinMapBean>>,

        @SerializedName("app_logo_list") val appLogo: LogoBean ?=null ,
        @SerializedName("lan") var lan: AppLanguageBean ?=null,
        @SerializedName("marketSort") val marketSort: ArrayList<String> ?=ArrayList(),
        @SerializedName("otcOpen") var otcOpen: String ?= "",
        @SerializedName("depositOpen") var depositOpen: String ?= "",
        @SerializedName("verificationType") var verificationType: Int,
        @SerializedName("otcUrl") var otcUrl: String ?= "",
        @SerializedName("klineScale") var klineScale: ArrayList<String> ?=ArrayList(),
        @SerializedName("bank_name_equal_auth") var bank_name_equal_auth: String ?= "0"

) : Parcelable {
    constructor(source: Parcel) : this(
            source.readSerializable() as TreeMap<String, CoinBean>,
            source.readSerializable() as TreeMap<String, TreeMap<String, CoinMapBean>>,

            source.readParcelable<LogoBean>(LogoBean::class.java.classLoader),
            source.readParcelable<AppLanguageBean>(AppLanguageBean::class.java.classLoader),
            source.createStringArrayList(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.createStringArrayList(),
            source.readString()
    )


    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(coinList)
        writeSerializable(market)
        writeParcelable(appLogo, 0)
        writeStringList(marketSort)
        dest.writeString(otcOpen)
        dest.writeString(depositOpen)
        dest.writeInt(verificationType)
        dest.writeString(otcUrl)
        writeStringList(klineScale)
        dest.writeString(bank_name_equal_auth)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<InitInfo> = object : Parcelable.Creator<InitInfo> {
            override fun createFromParcel(source: Parcel): InitInfo = InitInfo(source)
            override fun newArray(size: Int): Array<InitInfo?> = arrayOfNulls(size)
        }
    }

}