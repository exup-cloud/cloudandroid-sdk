package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019/4/28-2:43 PM
 * @Email buptjinlong@163.com
 * @description 用于传值
 */
data class TradingSuccessBean(
        @SerializedName("coinName") val coinName: String? = "" // 购买币种

)