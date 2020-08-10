package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/21-上午10:36
 * @Email buptjinlong@163.com
 * @description
 */
data class OTCGetCoinBean(
        @SerializedName("coinSymbol") val coinSymbol: String = "",//币种
        @SerializedName("exNormal") val exNormal: String = "",//交易所正常余额
        @SerializedName("otcLock") val otcLock: String = "",//场外冻结余额
        @SerializedName("exLock") val exLock: String = "",//交易所冻结余额
        @SerializedName("otcNormal") val otcNormal: String = ""//场外正常余额
)