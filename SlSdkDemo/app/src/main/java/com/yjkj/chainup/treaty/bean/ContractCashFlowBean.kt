package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName

/**
 * 合约资金流水
 */
data class ContractCashFlowBean(
        @SerializedName("count")
        val count: Int? = 0, // 11894
        @SerializedName("transactionsList")
        val transactionsList: List<Transactions?>? = listOf()
) {
    data class Transactions(
            @SerializedName("accountBalance")
            val accountBalance: Double? = 0.0, // 9033826.56821114
            @SerializedName("address")
            val address: String? = "", // CUSD
            @SerializedName("amountStr")
            val amountStr: Double? = 0.0, // 0.00000167
            @SerializedName("ctimeL")
            val ctimeL: Long? = 0, // 1547203306000
            @SerializedName("sceneStr")
            val sceneStr: String? = "" // 主动单手续费
    )
}