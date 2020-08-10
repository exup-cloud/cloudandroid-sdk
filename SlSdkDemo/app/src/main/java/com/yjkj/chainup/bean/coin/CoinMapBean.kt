package com.yjkj.chainup.bean.coin

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoinMapBean(
       // @Id var id: Long = 0,
       // @SerializedName("showMarket")
        //@Index var showMarket: String? = "", // USDT

        //@SerializedName("market")
       // @Index var market: String? = "", // USDT
       // @SerializedName("coinName")
        //var coinName: String? = "", // BIKI
       // @SerializedName("showCoinName")
        //var showCoinName: String? = "", // BIKI
        @SerializedName("volume") val volume: Int = 0,
        @SerializedName("limitVolumeMin") val limitVolumeMin: String = "0.0",
        @SerializedName("symbol") val symbol: String = "",
        @SerializedName("depth") val depth: String = "",
        @SerializedName("marketBuyMin") val marketBuyMin: String = "0.0",
        @SerializedName("price") val price: Int = 0,
        @SerializedName("isShow") val isShow: Int = 1,
        @SerializedName("name") val name: String = "",
        @SerializedName("marketSellMin") val marketSellMin: String = "0.0",
        @SerializedName("limitPriceMin") val limitPriceMin: String = "0.0",
        @SerializedName("isSelected") var isSelected: Boolean = false,
        @SerializedName("isAdd") var isAdd: Boolean = false,
        @SerializedName("sort") var sort: Int = 1,
        @SerializedName("newcoinFlag") val newcoinFlag: Int = 1, // 0主区 1创新区 2观察区
        @SerializedName("depth_level") val depth_level: Int = 1, // 自定义保存币对的深度类型
        @SerializedName("showName") val showName: String = ""
) : Serializable {

    companion object {
        private const val serialVersionUID: Long = -7414058080551024328L

    }

    override fun equals(other: Any?): Boolean {

        return when (other) {
            !is CoinMapBean -> false
            else -> this === other || symbol == other.symbol
        }

    }

    override fun toString(): String {
        return "CoinMapBean(volume=$volume, limitVolumeMin=$limitVolumeMin, showSymbol='$symbol', depth='$depth', marketBuyMin=$marketBuyMin, price=$price, name='$name', marketSellMin=$marketSellMin, limitPriceMin=$limitPriceMin, isSelected=$isSelected, isAdd=$isAdd, sort=$sort, newcoinFlag=$newcoinFlag, " +
                "depth_level=$depth_level,showName=$showName)"
    }


}

