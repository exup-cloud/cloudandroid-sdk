package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class TempBean(
        @SerializedName("coin")
        val coin: String? = "", // USDT
        @SerializedName("money")
        val money: String? = "" // https
)