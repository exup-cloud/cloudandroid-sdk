package com.yjkj.chainup.bean.fund

import com.google.gson.annotations.SerializedName

data class CashFlowBean(
        @SerializedName("financeList") val financeList: List<Finance> = listOf(),
        @SerializedName("count") val count: Int = 0, //1
        @SerializedName("pageSize") val pageSize: Int = 0 //10
) {
    data class Finance(
            @SerializedName("symbol") val symbol: String = "", //BTC
            @SerializedName("amount") val amount: String = "", //10000.00000000
            @SerializedName("fee") val fee: Double = 0.0, //0.0010000000000000
            @SerializedName("updateAt") val updateAt: String = "", //2018-04-16 21:35:01
            @SerializedName("txid") val txid: String = "", //2345fdgdhdhdoijoewrn34cvdfg
            @SerializedName("label") val label: String = "", //BTC
            @SerializedName("addressTo") val addressTo: String = "", //1LJkiVMaXBW6F55g8MysUhdGgsrbTe92Zz
            @SerializedName("updateAtTime") val updateAtTime: String = "0",
            @SerializedName("walletTime") val walletTime: String = "0",
            @SerializedName("createdAtTime") val createdAtTime: Long = 0L, //1529653663000
            @SerializedName("id") val id: Int = 0, //2643
            @SerializedName("createdAt") val createdAt: String = "", //2018-04-16 20:35:01
            @SerializedName("status_text") val statusText: String = "", //已完成
            @SerializedName("confirmDesc") val confirmDesc: String = "", //1/6
            @SerializedName("coinSymbol") val coinSymbol: String = "", //ADA
            @SerializedName("createTime") val createTime: String = "", //2018-06-21 20:53:40
            @SerializedName("transactionScene") val transactionScene: String = "", //present_coin
            @SerializedName("createTimeTime") val createTimeTime: Long = 0L, //1529585620000
            @SerializedName("status") val status: Int = 0, //1
            @SerializedName("transactionType") val transactionType: Int = 0, //1.转入场外，2. 转出场外
            @SerializedName("transactionType_text") val transactionType_text: String = "" //1.转入场外，2. 转出场外


    )
}