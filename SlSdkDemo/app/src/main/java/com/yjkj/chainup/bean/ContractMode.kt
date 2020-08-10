package com.yjkj.chainup.bean


import com.google.gson.annotations.SerializedName

/**
 * 仓位模式，1-分仓。0净持仓
 */
data class ContractMode(
        @SerializedName("is_more_position")
        val isMorePosition: String? = "0" // 1
)