package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019-08-06-16:16
 * @Email buptjinlong@163.com
 * @description
 */
class AccountCertificationBean(
        @SerializedName("openAuto")
        val openAuto: String = "", // --开启自动审核   0未开启 1开启
        @SerializedName("language")
        val language: String = "",// 未开启是能够获取文案，开启后字段不返回
        @SerializedName("limitFlag")
        val limitFlag: String = "",// --当日平台、个人是否超出使用次数，0未超出，1超出
        @SerializedName("limitMsg")
        val limitMsg: String = "",//
        @SerializedName("toKenUrl")
        val toKenUrl: String = "",// --唤醒第三方认证流程，以上不通过，字段不返回；
        @SerializedName("toResultUrl")
        val toResultUrl: String = ""//--以上不通过，字段不返回；
)