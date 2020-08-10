package com.yjkj.chainup.treaty.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
/**
 * 用户持仓信息
 */
data class UserPositionBean(
        @SerializedName("orderCount")
        val orderCount: Int? = 0, // 22
        @SerializedName("positionCount")
        val positionCount: Int? = 0, // 2
        @SerializedName("positions")
        val positions: ArrayList<Position?>? = arrayListOf()
) {
    data class Position(
            @SerializedName("accountType")
            val accountType: Int? = 0, // 2161001
            @SerializedName("assignedMargin")
            val assignedMargin: String? = "", // 0.64000000
            @SerializedName("avgPrice")
            val avgPrice: Double? = 0.0, // 0.032
            @SerializedName("baseSymbol")
            val baseSymbol: String? = "", // ETH
            @SerializedName("bond")
            val bond: String? = "", // BTC
            @SerializedName("canUseMargin")
            val canUseMargin: Double? = 0.0, // 4025897.39302531
            @SerializedName("closeFeeAmount")
            val closeFeeAmount: Double? = 0.0, // 0
            @SerializedName("contractId")
            val contractId: Int? = 0, // 3
            @SerializedName("contractName")
            val contractName: String? = "", // Ethereum
            @SerializedName("copType")
            val copType: Int? = 0, // 2
            @SerializedName("ctime")
            val ctime: String? = "", // 2018-10-23 21:49:02
            @SerializedName("holdAmount")
            val holdAmount: Double? = 0.0, // 0.64
            @SerializedName("holdRate")
            val holdRate: Double? = 0.0, // 0.01
            @SerializedName("id")
            val id: Int? = 0, // 13
            @SerializedName("indexPrice")
            val indexPrice: Double? = 0.0, // 0.032
            @SerializedName("leverageLevel")
            val leverageLevel: Int? = 0, // 50
            @SerializedName("liquidationPrice")
            val liquidationPrice: Double? = 0.0, // 0.00032
            @SerializedName("minMargin")
            val minMargin: Double? = 0.0, // 0.00001
            @SerializedName("multiplier")
            val multiplier: Int? = 0, // 1
            @SerializedName("pricePrecision")
            val pricePrecision: Int? = 0, // 5
            @SerializedName("priceValue")
            val priceValue: Double? = 0.0, // 0.64
            @SerializedName("quoteSymbol")
            val quoteSymbol: String? = "", // BTC
            @SerializedName("realisedAmountCurr")
            val realisedAmountCurr: String? = "", // 0.00000000
            @SerializedName("realisedAmountHistory")
            val realisedAmountHistory: Double? = 0.0, // -0.00166242
            @SerializedName("side")
            val side: String? = "", // BUY
            @SerializedName("symbol")
            val symbol: String? = "", // E_ETHBTC
            @SerializedName("unrealisedAmountIndex")
            val unrealisedAmountIndex: String? = "", // 0.00000000
            @SerializedName("unrealisedAmountMarket")
            val unrealisedAmountMarket: Double? = 0.0, // 0
            @SerializedName("unrealisedRateIndex")
            val unrealisedRateIndex: String? = "", // 0
            @SerializedName("unrealisedRateMarket")
            val unrealisedRateMarket: String? = "", // 0
            @SerializedName("usedMargin")
            val usedMargin: String? = "", // 0.64000000
            @SerializedName("valuePrecision")
            val valuePrecision: Int? = 0, // 2
            @SerializedName("volume")
            val volume: String? = "0" // 20
    ) : Parcelable {
            constructor(parcel: Parcel) : this(
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readString(),
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readString(),
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readString(),
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readValue(Double::class.java.classLoader) as? Double,
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readValue(Int::class.java.classLoader) as? String)

        override fun toString(): String {
                    return "Position(accountType=$accountType, assignedMargin=$assignedMargin, avgPrice=$avgPrice, baseSymbol=$baseSymbol, bond=$bond, canUseMargin=$canUseMargin, closeFeeAmount=$closeFeeAmount, contractId=$contractId, contractName=$contractName, copType=$copType, ctime=$ctime, holdAmount=$holdAmount, holdRate=$holdRate, id=$id, indexPrice=$indexPrice, leverageLevel=$leverageLevel, liquidationPrice=$liquidationPrice, minMargin=$minMargin, multiplier=$multiplier, pricePrecision=$pricePrecision, priceValue=$priceValue, quoteSymbol=$quoteSymbol, realisedAmountCurr=$realisedAmountCurr, realisedAmountHistory=$realisedAmountHistory, side=$side, symbol=$symbol, unrealisedAmountIndex=$unrealisedAmountIndex, unrealisedAmountMarket=$unrealisedAmountMarket, unrealisedRateIndex=$unrealisedRateIndex, unrealisedRateMarket=$unrealisedRateMarket, usedMargin=$usedMargin, valuePrecision=$valuePrecision, volume=$volume)"
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                    parcel.writeValue(accountType)
                    parcel.writeString(assignedMargin)
                    parcel.writeValue(avgPrice)
                    parcel.writeString(baseSymbol)
                    parcel.writeString(bond)
                    parcel.writeValue(canUseMargin)
                    parcel.writeValue(closeFeeAmount)
                    parcel.writeValue(contractId)
                    parcel.writeString(contractName)
                    parcel.writeValue(copType)
                    parcel.writeString(ctime)
                    parcel.writeValue(holdAmount)
                    parcel.writeValue(holdRate)
                    parcel.writeValue(id)
                    parcel.writeValue(indexPrice)
                    parcel.writeValue(leverageLevel)
                    parcel.writeValue(liquidationPrice)
                    parcel.writeValue(minMargin)
                    parcel.writeValue(multiplier)
                    parcel.writeValue(pricePrecision)
                    parcel.writeValue(priceValue)
                    parcel.writeString(quoteSymbol)
                    parcel.writeString(realisedAmountCurr)
                    parcel.writeValue(realisedAmountHistory)
                    parcel.writeString(side)
                    parcel.writeString(symbol)
                    parcel.writeString(unrealisedAmountIndex)
                    parcel.writeValue(unrealisedAmountMarket)
                    parcel.writeString(unrealisedRateIndex)
                    parcel.writeString(unrealisedRateMarket)
                    parcel.writeString(usedMargin)
                    parcel.writeValue(valuePrecision)
                    parcel.writeValue(volume)
            }

            override fun describeContents(): Int {
                    return 0
            }

            companion object CREATOR : Parcelable.Creator<Position> {
                    override fun createFromParcel(parcel: Parcel): Position {
                            return Position(parcel)
                    }

                    override fun newArray(size: Int): Array<Position?> {
                            return arrayOfNulls(size)
                    }
            }
    }
}