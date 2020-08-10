package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019/4/22-9:32 PM
 * @Email buptjinlong@163.com
 * @description
 */
data class PersonAdsBean(
        @SerializedName("count") var count: String = "",//总数
        @SerializedName("adList") var adList: ArrayList<AdList> = arrayListOf() //最大交易量
) {
    data class Payments(
            @SerializedName("key") var key: String = "",//
            @SerializedName("title") var title: String = "",//
            @SerializedName("icon") var icon: String = "",//
            @SerializedName("used") var used: String = ""//
    )

    data class AdList(
            @SerializedName("payCoin") var payCoin: String = "",//支付币种
            @SerializedName("volume") var volume: String = "",//总量
            @SerializedName("side") var side: String = "",//买卖方向
            @SerializedName("createTime") var createTime: String = "",//创建时间
            @SerializedName("price") var price: String = "",//单价
            @SerializedName("sell") var sell: String = "",//已成交量
            @SerializedName("minTrade") var minTrade: String = "",//最小交易量
            @SerializedName("maxTrade") var maxTrade: String = "",//最大交易量
            @SerializedName("advertId") var advertId: String = "",//广告ID
            @SerializedName("payments") var payments: ArrayList<Payments> = arrayListOf(),//最大交易量
            @SerializedName("status") var status: String = "",//广告状态 1发布中  2交易中 3过期 4关闭
            @SerializedName("coin") var coin: String = "",//币种
            @SerializedName("isHaveOrder") var isHaveOrder: String = ""//是否有未完成订单1有0无

    )

}