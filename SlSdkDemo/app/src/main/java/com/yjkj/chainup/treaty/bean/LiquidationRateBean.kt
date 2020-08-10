package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName

/**
 * 风险系数
 */
data class LiquidationRateBean(
        @SerializedName("contractId")
        val contractId: String? = "", // 1
        @SerializedName("liquidationRate")
        val liquidationRate: Double? = 0.0 // 33.00
) {
        override fun toString(): String {
                return "LiquidationRateBean(contractId=$contractId, liquidationRate=$liquidationRate)"
        }
}