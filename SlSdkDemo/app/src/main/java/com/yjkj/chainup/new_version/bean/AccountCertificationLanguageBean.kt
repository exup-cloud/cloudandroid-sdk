package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019-08-06-20:56
 * @Email buptjinlong@163.com
 * @description
 */
class AccountCertificationLanguageBean(
        @SerializedName("language")
        val language: String = "" // --开启自动审核   0未开启 1开启
)