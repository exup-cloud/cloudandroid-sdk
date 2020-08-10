package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class ReceiveRedPackageBean(
        @SerializedName("amount")
        val amount: Double? = 0.0, // 0.0107831478297496
        @SerializedName("coinSymbol")
        val coinSymbol: String? = "", // btc
        @SerializedName("ctime")
        val ctime: Long? = 0, // 1561552000000
        @SerializedName("isNew")
        val isNew: Int? = 0, // 0
        @SerializedName("nickName")
        val nickName: String? = "", // 哈哈哈
        @SerializedName("isLucky")
        val isLucky: Int? = 0,//  是否手气最佳 0.否 1.是
        @SerializedName("packetSn")
        val packetSn: String? = ""// 红包编号
) {
    override fun toString(): String {
        return "ReceiveRedPackageBean(amount=$amount, coinSymbol=$coinSymbol, ctime=$ctime, isNew=$isNew, nickName=$nickName, isLucky=$isLucky, packetSn=$packetSn)"
    }

}