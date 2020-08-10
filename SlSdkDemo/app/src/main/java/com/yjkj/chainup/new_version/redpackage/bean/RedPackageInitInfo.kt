package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class RedPackageInitInfo(
        @SerializedName("defaultTip")
        val defaultTip: String? = "",
        @SerializedName("symbolList")
        val symbolList: List<Symbol?>? = listOf()
) {
    data class Symbol(
            @SerializedName("amount")
            var amount: String? = "0.0", // 0
            @SerializedName("coinSymbol")
            val coinSymbol: String? = "", // LTC
            @SerializedName("expiredHour")
            val expiredHour: Int? = 0, // 82
            @SerializedName("generalStatus")
            val generalStatus: Int? = 0, // 1
            @SerializedName("randomStatus")
            val randomStatus: Int? = 0, // 1
            @SerializedName("singleAmountMax")
            val singleAmountMax: Double? = 0.0, // 80
            @SerializedName("singleAmountMin")
            val singleAmountMin: Double? = 0.0, // 8
            @SerializedName("singleCountMax")
            val singleCountMax: Int? = 0 // 888
    ) {
            override fun toString(): String {
                    return "Symbol(amount=$amount, coinSymbol=$coinSymbol, expiredHour=$expiredHour, generalStatus=$generalStatus, randomStatus=$randomStatus, singleAmountMax=$singleAmountMax, singleAmountMin=$singleAmountMin, singleCountMax=$singleCountMax)"
            }
    }

        override fun toString(): String {
                return "RedPackageInitInfo(defaultTip=$defaultTip, symbolList=$symbolList)"
        }
}