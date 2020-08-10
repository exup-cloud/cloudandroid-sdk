package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/22-下午2:24
 * @Email buptjinlong@163.com
 * @description
 */
data class OTCChatBean(
        @SerializedName("message") val message: Message, //0
        @SerializedName("type") val type: String = "", //类型
        @SerializedName("chatId") val chatId: String = "" //当前聊天记录id
) {
    data class Message(
            @SerializedName("from") val from: String = "", //0
            @SerializedName("to") val to: String = "", //0
            @SerializedName("content") val content: String = "", //内容
            @SerializedName("time") val time: String = "", //时间戳
            @SerializedName("orderId") val orderId: String = "" //id
    )
}