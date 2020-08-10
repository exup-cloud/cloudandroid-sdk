package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class ReceiveRedPackageInfoBean(
        @SerializedName("allAmount")
        val allAmount: Double? = 0.0, // 0.010
        @SerializedName("count")
        val count: Int? = 0, // 1
        @SerializedName("nickName")
        val nickName: String? = "", // 13211111112
        @SerializedName("rateSymbol")
        val rateSymbol: String? = "" // BTC
) {
        override fun toString(): String {
                return "ReceiveRedPackageInfoBean(allAmount=$allAmount, count=$count, nickName=$nickName, rateSymbol=$rateSymbol)"
        }
}