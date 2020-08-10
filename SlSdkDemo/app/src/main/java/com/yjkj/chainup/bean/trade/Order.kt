package com.yjkj.chainup.bean.trade

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
        @SerializedName("avg_price")
        val avgPrice: String? = "", // 0.00000000
        @SerializedName("baseCoin")
        val baseCoin: String? = "", // BTC
        @SerializedName("countCoin")
        val countCoin: String? = "", // USDT
        @SerializedName("created_at")
        val createdAt: String? = "", // 2019-06-28 10:41:22
        @SerializedName("deal_volume")
        val dealVolume: String? = "", // 0.00000000
        @SerializedName("id")
        val id: Int? = 0, // 14431095
        @SerializedName("price")
        val price: String? = "", // 11219.56180000
        @SerializedName("remain_volume")
        val remainVolume: String? = "", // 0.10000000
        @SerializedName("side")
        val side: String? = "", // BUY
        @SerializedName("source")
        val source: String? = "", // APP
        @SerializedName("status")
        val status: Int? = 0, // 1
        @SerializedName("status_text")
        val statusText: String? = "", // 未成交
        @SerializedName("time_long")
        val timeLong: Long = 0, // 1561689682089
        @SerializedName("total_price")
        val totalPrice: String? = "", // 1121.95618000
        @SerializedName("type")
        val type: Int? = 0, // 1
        @SerializedName("volume")
        val volume: String? = "" // 0.10000000
) : Serializable