package com.yjkj.chainup.bean.dev

import com.google.gson.annotations.SerializedName

/**
 * @author Bertking
 * @date 2018-6-20
 * @description 站内信的Bean
 */
data class MessageBean(
        @SerializedName("messageType") val messageType: Int = 0, //0
        @SerializedName("typeList") val typeList: List<Type> = listOf(),
        @SerializedName("count") val count: Int = 0, //2
        @SerializedName("pageSize") val pageSize: Int = 0, //5
        @SerializedName("userMessageList") val userMessageList: List<UserMessage> = listOf()
) {
    data class Type(
            @SerializedName("title") val title: String = "", //场外消息
            @SerializedName("tid") val tid: Int //7
    )

    data class UserMessage(
            @SerializedName("id") val id: Int, //14811
            @SerializedName("receiveUid") val receiveUid: Int, //10609
            @SerializedName("messageType") val messageType: Int, //1
            @SerializedName("messageContent") val messageContent: String = "", //test
            @SerializedName("status") val status: Int, //1
            @SerializedName("ctime") val ctime: Long = 0L //1527908620000
    )
}