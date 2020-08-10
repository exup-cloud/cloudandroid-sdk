package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName
import com.yjkj.chainup.bean.ContractMode
import com.yjkj.chainup.bean.coin.CoinBean

/**
 * 合约的公共信息
 */
data class ContractPublicInfoBean(

        @SerializedName("market")
        val market: LinkedHashMap<String, ArrayList<ContractBean>>,
        @SerializedName("marketSymbol")
        val marketSymbol: String = "",
        @SerializedName("sceneList")
        val sceneList: ArrayList<ContractSceneList> = arrayListOf(),
        @SerializedName("coinList")
        val coinList: HashMap<String, CoinBean>,
        @SerializedName("switch") var switch: ContractMode = ContractMode()
)


class ContractBean(
        @SerializedName("baseSymbol")
        val baseSymbol: String? = "", // ETH
        @SerializedName("beginTime")
        val beginTime: String? = "", // 2019-01-16 21:20:00
        @SerializedName("bond")
        val bond: String? = "",
        @SerializedName("buyLimitRate")
        val buyLimitRate: Int? = 0, // 1000000
        @SerializedName("contractName")
        val contractName: String? = "", // W-Ethereum
        @SerializedName("contractType")
        val contractType: Int? = 0, // 1
        @SerializedName("ctime")
        val ctime: String? = "", // 2019-01-16 21:21:00
        @SerializedName("feeRateMaker")
        val feeRateMaker: Double? = 0.0, // -0.0005
        @SerializedName("feeRateTaker")
        val feeRateTaker: Double? = 0.0, // 0.0025
        @SerializedName("holdRate")
        val holdRate: Double? = 0.0, // 0.005
        @SerializedName("id")
        val id: Int? = 0, // 18
        @SerializedName("leverTypes")
        val leverTypes: String? = "", // 1,2,3,5,10,25,35,50
        @SerializedName("maxLeverageLevel")
        val maxLeverageLevel: Int? = 0, // 50
        @SerializedName("maxOrderVolume")
        val maxOrderVolume: Int? = 0, // 10000000
        @SerializedName("minMargin")
        val minMargin: Double? = 0.0, // 0.00001
        @SerializedName("minOrderVolume")
        val minOrderVolume: Int? = 0, // 1
        @SerializedName("mtime")
        val mtime: String? = "", // 2019-01-16 21:21:00
        @SerializedName("multiplier")
        val multiplier: Int? = 0, // 1
        @SerializedName("pricePrecision")
        val pricePrecision: Int? = 0, // 5
        @SerializedName("quoteSymbol")
        val quoteSymbol: String? = "", // BTC
        @SerializedName("sellLimitRate")
        val sellLimitRate: Double? = 0.0, // 0.01
        @SerializedName("settleFeeRate")
        val settleFeeRate: Double? = 0.0, // 0.0025
        @SerializedName("settleTime")
        val settleTime: String? = "", // 2019-01-23 21:20:00
        @SerializedName("settlementPrice")
        val settlementPrice: Double? = 0.0, // 0.21
        @SerializedName("signType")
        val signType: Int? = 0, // 1
        @SerializedName("status")
        val status: Int? = 0, // 1
        @SerializedName("symbol")
        var symbol: String? = "", // W_ETHBTC
        @SerializedName("last_symbol")
        var lastSymbol: String? = "",// 该字段属于本地字段，用于取消上一次的ws订阅所使用
        @SerializedName("closePrice")
        var closePrice: String? = "",// 该字段属于本地字段，用于选择合约的最新成交价
        @SerializedName("rose")
        var rose: String? = "0.0"// 该字段属于本地字段，用于选择合约的涨跌幅

) {
    override fun toString(): String {
        return "ContractBean(baseSymbol=$baseSymbol, beginTime=$beginTime, bond=$bond, buyLimitRate=$buyLimitRate, contractName=$contractName, contractType=$contractType, ctime=$ctime, feeRateMaker=$feeRateMaker, feeRateTaker=$feeRateTaker, holdRate=$holdRate, id=$id, leverTypes=$leverTypes, maxLeverageLevel=$maxLeverageLevel, maxOrderVolume=$maxOrderVolume, minMargin=$minMargin, minOrderVolume=$minOrderVolume, mtime=$mtime, multiplier=$multiplier, pricePrecision=$pricePrecision, quoteSymbol=$quoteSymbol, sellLimitRate=$sellLimitRate, settleFeeRate=$settleFeeRate, settleTime=$settleTime, settlementPrice=$settlementPrice, signType=$signType, status=$status, symbol=$symbol, lastSymbol=$lastSymbol)"
    }
}