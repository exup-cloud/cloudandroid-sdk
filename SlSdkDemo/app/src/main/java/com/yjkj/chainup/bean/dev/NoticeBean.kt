package com.yjkj.chainup.bean.dev

import com.google.gson.annotations.SerializedName

data class NoticeBean(
        @SerializedName("noticeInfoList") val noticeInfoList: List<NoticeInfo>,
        @SerializedName("count") val count: Int = 0, //8
        @SerializedName("pageSize") val pageSize: Int = 0 //10
) {
    data class NoticeInfo(
            @SerializedName("id") val id: Int = 0, //84
            @SerializedName("title") val title: String? = "", //Chain on the exchange platform will be October 10, 2017 officially launched~
            @SerializedName("lang") val lang: String? = "", //zh,en
            @SerializedName("content") val content: String? = "", //ssssssssss
            @SerializedName("timeLong") val timeLong: Long = 0L //1500213245646
    )

}