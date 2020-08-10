package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName

data class InitTakeOrderBean(
        @SerializedName("buyOrderCost")
        val buyOrderCost: String? = "", // 0.0000283496959618
        @SerializedName("canUseBalance")
        val canUseBalance: Double? = 0.0, // 999.9999917751819911
        @SerializedName("contractConfig")
        val contractConfig: ContractConfig? = ContractConfig(),
        @SerializedName("contractId")
        val contractId: Int? = 0, // 19
        @SerializedName("level")
        val level: Int? = 0, // 10
        @SerializedName("liquidationBuyPrice")
        val liquidationBuyPrice: String? = "", // 3891.4438500007323278
        @SerializedName("liquidationSellPrice")
        val liquidationSellPrice: String? = "", // 3216.2161500000166185
        @SerializedName("orderPriceValue")
        val orderPriceValue: String? = "", // 0.0002813865604151
        @SerializedName("personFee")
        val personFee: PersonFee? = PersonFee(),
        @SerializedName("price")
        val price: String? = "", // 3553.83
        @SerializedName("sellOrderCost")
        val sellOrderCost: String? = "", // 0.000000211039920311325004393150849587960926623253499201382510364055633544921875
        @SerializedName("volume")
        val volume: String? = "0"// 1
) {
    data class ContractConfig(
            @SerializedName("baseSymbol")
            val baseSymbol: String? = "", // BTC
            @SerializedName("beginTime")
            val beginTime: String? = "", // 2019-01-01 00:00:00
            @SerializedName("bond")
            val bond: String? = "", // BTC
            @SerializedName("buyLimitRate")
            val buyLimitRate: Int? = 0, // 10000
            @SerializedName("contractName")
            val contractName: String? = "", // BitCoin
            @SerializedName("contractType")
            val contractType: Int? = 0, // 0
            @SerializedName("ctime")
            val ctime: String? = "", // 2019-01-18 14:55:13
            @SerializedName("feeRateMaker")
            val feeRateMaker: Double? = 0.0, // -2.5E-4
            @SerializedName("feeRateTaker")
            val feeRateTaker: Double? = 0.0, // 7.5E-4
            @SerializedName("holdRate")
            val holdRate: Double? = 0.0, // 0.005
            @SerializedName("id")
            val id: Int? = 0, // 19
            @SerializedName("leverTypes")
            val leverTypes: String? = "", // 1,2,3,5,10,25,50,100
            @SerializedName("maxLeverageLevel")
            val maxLeverageLevel: Int? = 0, // 100
            @SerializedName("maxOrderVolume")
            val maxOrderVolume: Int? = 0, // 10000000
            @SerializedName("minMargin")
            val minMargin: Double? = 0.0, // 0.0100000000000000
            @SerializedName("minOrderVolume")
            val minOrderVolume: Int? = 0, // 1
            @SerializedName("mtime")
            val mtime: String? = "", // 2019-01-22 08:00:00
            @SerializedName("multiplier")
            val multiplier: Int? = 0, // 1
            @SerializedName("pricePrecision")
            val pricePrecision: Int? = 0, // 2
            @SerializedName("quoteSymbol")
            val quoteSymbol: String? = "", // USD
            @SerializedName("sellLimitRate")
            val sellLimitRate: Double? = 0.0, // 0.01
            @SerializedName("settleFeeRate")
            val settleFeeRate: Double? = 0.0, // 7.5E-4
            @SerializedName("settleTime")
            val settleTime: String? = "", // 2099-12-31 00:00:00
            @SerializedName("settlementPrice")
            val settlementPrice: Double? = 0.0, // 0E-16
            @SerializedName("signType")
            val signType: Int? = 0, // 1
            @SerializedName("sort")
            val sort: Int? = 0, // 1
            @SerializedName("status")
            val status: Int? = 0, // 1
            @SerializedName("symbol")
            val symbol: String? = "" // E_BTCUSD
    )

    data class PersonFee(
            @SerializedName("createTime")
            val createTime: Any? = Any(), // null
            @SerializedName("id")
            val id: Int? = 0, // 0
            @SerializedName("personDepositFee")
            val personDepositFee: Int? = 0, // 0
            @SerializedName("personMakerFee")
            val personMakerFee: Double? = 0.0, // -0.00025000000000000000520417042793042128323577344417572021484375
            @SerializedName("personSettlementFee")
            val personSettlementFee: Double? = 0.0, // 0.00075000000000000001561251128379126384970732033252716064453125
            @SerializedName("personTakerFee")
            val personTakerFee: Double? = 0.0, // 0.00075000000000000001561251128379126384970732033252716064453125
            @SerializedName("personWithdrawFee")
            val personWithdrawFee: Int? = 0, // 0
            @SerializedName("symbol")
            val symbol: String? = "",
            @SerializedName("uid")
            val uid: Int? = 0, // 0
            @SerializedName("updateTime")
            val updateTime: Any? = Any() // null
    )
}