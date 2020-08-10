package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/17-下午4:18
 * @Email buptjinlong@163.com
 * @description
 */
data class OTCInitInfoBean(
        @SerializedName("payments") val payments: ArrayList<PaymentBean> = arrayListOf(),//支付方式
        @SerializedName("paycoins") val paycoins: ArrayList<Paycoins> = arrayListOf(),//支付类型
        @SerializedName("feeOtcList") val feeOtcList: ArrayList<FeeOtcList> = arrayListOf(),
        @SerializedName("countryNumberInfo") val countryNumberInfo: ArrayList<CountryNumberInfo> = arrayListOf(),
        @SerializedName("defaultCoin") val defaultCoin: String = "",
        @SerializedName("defaultSeach") val defaultSeach: String = "",
        @SerializedName("otcDefaultPaycoin") val otcDefaultPaycoin: String = "",
        @SerializedName("otcChatWS") val otcChatWS: String = "",
        @SerializedName("rateUrl") val rateUrl: String = ""

) {

    data class PaymentBean(
            @SerializedName("key") val key: String = "",
            @SerializedName("title") var title: String = "",
            @SerializedName("icon") var icon: String = "",
            @SerializedName("open") var open: Boolean,
            @SerializedName("hide") var hide: Boolean,
            @SerializedName("used") var used: Boolean
    )

    data class Paycoins(
            @SerializedName("key") val key: String = "",
            @SerializedName("title") val title: String = "",
            @SerializedName("icon") val icon: String = "",
            @SerializedName("open") var open: Boolean,
            @SerializedName("hide") var hide: Boolean,
            @SerializedName("used") var used: Boolean
    )

    data class FeeOtcList(
            @SerializedName("symbol") val symbol: String = "",
            @SerializedName("rate") val rate: Double
    )

    data class CountryNumberInfo(
            @SerializedName("key") val key: String = "",
            @SerializedName("title") val title: String = "",
            @SerializedName("icon") val icon: String = "",
            @SerializedName("open") var open: Boolean,
            @SerializedName("hide") var hide: Boolean,
            @SerializedName("numberCode") val numberCode: String = "",
            @SerializedName("used") var used: Boolean

    )


}