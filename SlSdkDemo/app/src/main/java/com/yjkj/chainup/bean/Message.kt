package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

data class Message(val type_list: MutableList<Type> = mutableListOf(), val message: MessageBean) {

    data class Type(val title: String = "", val tid: Int)

    data class MessageBean(val min_id: Int, @SerializedName("list") val messages: MutableList<Detail> = mutableListOf()) {
        data class Detail(val id: Int, val type_id: Int, val date: String = "",
                          val content: String = "")
    }
}