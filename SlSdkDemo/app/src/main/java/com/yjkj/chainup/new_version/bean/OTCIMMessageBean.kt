package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

data class OTCIMMessageBean(
        @SerializedName("id") var id: Int,
        @SerializedName("chatId") var chatId: Int,
        @SerializedName("orderId") var orderId: Long = 0L,
        @SerializedName("fromId") var fromId: String = "",
        @SerializedName("fromName") var fromName: String = "",
        @SerializedName("toId") var toId: String = "",
        @SerializedName("toName") var toName: String = "",
        @SerializedName("content") var content: String = "",
        @SerializedName("status") var status: Int,
        @SerializedName("ctime") var ctime: String = ""
)