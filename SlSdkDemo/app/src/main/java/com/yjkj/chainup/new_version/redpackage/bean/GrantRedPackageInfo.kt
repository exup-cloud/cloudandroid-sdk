package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class GrantRedPackageInfo(
        @SerializedName("allAmount")
        val allAmount: Double? = 0.0, // 0.4999999999999999999902
        @SerializedName("allCount")
        val allCount: Int? = 0, // 50
        @SerializedName("getCount")
        val getCount: Int? = 0, // 0
        @SerializedName("newCount")
        val newCount: Int? = 0, // 0
        @SerializedName("nickName")
        val nickName: String? = "", // 13211111112
        @SerializedName("rateSymbol")
        val rateSymbol: String? = "" // BTC
)