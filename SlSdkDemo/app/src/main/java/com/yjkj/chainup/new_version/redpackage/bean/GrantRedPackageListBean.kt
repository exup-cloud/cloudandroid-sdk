package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

/**
 * 收到红包
 */
data class GrantRedPackageListBean(
        @SerializedName("count")
        val count: Int? = 0, // 1
        @SerializedName("pageNum")
        val pageNum: Int? = 0, // 1
        @SerializedName("pageSize")
        val pageSize: Int? = 0, // 10
        @SerializedName("redPacketList")
        val redPacketList: List<RedPacket?>? = listOf()
) {
    data class RedPacket(
            @SerializedName("amount")
            val amount: Double? = 0.0, // 0.5
            @SerializedName("coinSymbol")
            val coinSymbol: String? = "", // btc
            @SerializedName("redPacketAllCount")
            val redPacketAllCount: Int? = 0, // 50
            @SerializedName("redPacketGetCount")
            val redPacketGetCount: Int? = 0, // 0
            @SerializedName("packetSn")
            val redPacketSn: String? = "", // 1
            @SerializedName("status")
            val status: Int? = 0, // 1
            @SerializedName("stime")
            val stime: Long? = 0, // 1561553916000
            @SerializedName("type")
            val type: Int? = 0 // 1
    ) {
            override fun toString(): String {
                    return "RedPacket(amount=$amount, coinSymbol=$coinSymbol, redPacketAllCount=$redPacketAllCount, redPacketGetCount=$redPacketGetCount, redPacketSn=$redPacketSn, status=$status, stime=$stime, type=$type)"
            }
    }

        override fun toString(): String {
                return "GrantRedPackageListBean(count=$count, pageNum=$pageNum, pageSize=$pageSize, redPacketList=$redPacketList)"
        }
}