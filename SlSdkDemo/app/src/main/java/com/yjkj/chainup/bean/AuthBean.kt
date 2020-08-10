package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2020-03-19-21:54
 * @Email buptjinlong@163.com
 * @description
 */
data class AuthBean(@SerializedName("applyTimeTime") val applyTimeTime: Long = 0, ///34324324.html
               @SerializedName("amount") val amount: String? = "", //214
               @SerializedName("coinSymbol") val coinSymbol: String? = "",//如何开启微信授权
               @SerializedName("address") val address: String? = "",//如何开启微信授权
               @SerializedName("isOpenCompanyCheck") val isOpenCompanyCheck: Boolean? = false,//如何开启微信授权
               @SerializedName("isOpenUserCheck") val isOpenUserCheck: Boolean? = false,//如何开启微信授权
               @SerializedName("label") val label: String? = "",//如何开启微信授权
               @SerializedName("withdrawId") val withdrawId: String? = "",//提币订单Id
               @SerializedName("applyTime") val applyTime: String? = ""//如何开启微信授权
)

