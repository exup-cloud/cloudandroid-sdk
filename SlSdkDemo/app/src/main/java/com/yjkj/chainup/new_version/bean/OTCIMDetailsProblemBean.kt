package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/23-上午11:06
 * @Email buptjinlong@163.com
 * @description
 */
data class OTCIMDetailsProblemBean(
        @SerializedName("rqInfo") val rqInfo: RqInfo, //问题详情数据
        @SerializedName("rqReplyList") val rqReplyList: ArrayList<RqReplyList> = arrayListOf() //历史消息->追加问题列表
) {
    data class RqInfo(
            @SerializedName("id") val id: Int, //问题id
            @SerializedName("rqDescribe") val rqDescribe: String = "", //问题描述
            @SerializedName("rqTypeName") val rqTypeName: String = "", //问题类型
            @SerializedName("rqStatusName") val rqStatusName: String = "", //问题状态
            @SerializedName("ctime") val ctime: Long = 0L, //提交时间
            @SerializedName("imageDataStr") val imageDataStr: String = "" //附件信息
    )

    data class RqReplyList(
            @SerializedName("id") val id: Int, //问题id
            @SerializedName("rqId") val rqId: Int, //被追问的问题id
            @SerializedName("replayContent") val replayContent: String = "", //追问内容
            @SerializedName("contentType") val contentType: String = "", //1-文字内容 2-图片url （新增）
            @SerializedName("userType") val userType: String = "", //用户类型：1-后台用户 2-前端用户
            @SerializedName("ctime") val ctime: Long = 0L //提交时间
    )
}