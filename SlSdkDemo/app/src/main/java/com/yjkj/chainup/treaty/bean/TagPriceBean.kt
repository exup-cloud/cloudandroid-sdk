package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName

/**
 * 标记价格
 */
data class TagPriceBean(
        @SerializedName("indexPrice")
        val indexPrice: Double? = 0.0, // 3990.93
        @SerializedName("tagPrice")
        val tagPrice: Double? = 0.0 // 3999
)