package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class RedPackageDetailBean(
        @SerializedName("amount")
        val amount: Double? = 0.0, // 0.5
        @SerializedName("myAmount")
        val myAmount: Double? = 0.0, // 0.5
        @SerializedName("getAmount")
        val getAmount: Double? = 0.0, // 0.5
        @SerializedName("count")
        val count: Int? = 0, // 50
        @SerializedName("getCount")
        val getCount: Int? = 0, // 50
        @SerializedName("mapList")
        val mapList: ArrayList<ReceiveRedPackageBean?>? = arrayListOf(),
        @SerializedName("nickName")
        val nickName: String? = "", // 13211111112
        @SerializedName("QRCode")
        val qRCode: String? = "", // data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAIAAAAiOjnJAAADhElEQVR4
        @SerializedName("status")
        val status: Int? = 0, // 1
        @SerializedName("tip")
        val tip: String? = "", // ??????????????
        @SerializedName("coinSymbol")
        val coinSymbol: String? = "", // btc
        @SerializedName("url")
        val url: String? = "",// https://www.biki.com//ef0a4f3eb72940a5b21ff7a3b18a30a4
        @SerializedName("background")
        val background: String? = "" // http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20190629120150117.jpg
) {
    override fun toString(): String {
        return "RedPackageDetailBean(amount=$amount, myAmount=$myAmount, count=$count, mapList=$mapList, nickName=$nickName, qRCode=$qRCode, status=$status, tip=$tip, coinSymbol=$coinSymbol, url=$url)"
    }
}