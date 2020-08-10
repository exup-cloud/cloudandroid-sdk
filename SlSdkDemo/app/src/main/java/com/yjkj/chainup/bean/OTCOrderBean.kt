package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/21-下午3:47
 * @Email buptjinlong@163.com
 * @description
 */
data class OTCOrderBean(
        @SerializedName("count") val count: Int, //9
        @SerializedName("orderList") val orderList: ArrayList<Order> = arrayListOf()
) {
    data class Order(
            @SerializedName("side") val side: String = "", //SELL
            @SerializedName("totalPrice") val totalPrice: String = "", //68.00CNY
            @SerializedName("otcNickName") val otcNickName: String = "",
            @SerializedName("isOnline") val isOnline: Int, //0
            @SerializedName("type") val type: String = "", //卖
            @SerializedName("buyerId") val buyerId: Int, //106462
            @SerializedName("adsId") val adsId: Int, //2385
            @SerializedName("volume") val volume: String = "", //10.00000000
            @SerializedName("sequence") val sequence: String = "", //2018101911915
            @SerializedName("coinSymbol") val coinSymbol: String = "", //USDT
            @SerializedName("paySymbol") val paySymbol: String = "", //USDT
            @SerializedName("sellerId") val sellerId: Int, //10609
            @SerializedName("createTime") val createTime: Long = 0L, //1539916374000
            @SerializedName("payTime") val payTime: Long = 0L, //1539916374000
            @SerializedName("price") val price: String = "", //6.80CNY
            @SerializedName("imageUrl") val imageUrl: String = "", //http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/common/image/ljefus1537614850690.jpg
            @SerializedName("status_text") val statusText: String = "", //已取消
            @SerializedName("id") val id: Int, //11915
            @SerializedName("status") val status: Int //4
    )
}