package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019/1/27-3:57 PM
 * @Email buptjinlong@163.com
 * @description
 */
data class ImageTokenBean(
        @SerializedName("AccessKeyId") var AccessKeyId: String = "",
        @SerializedName("AccessKeySecret") var AccessKeySecret: String = "",
        @SerializedName("Expiration") var Expiration: String = "",
        @SerializedName("SecurityToken") var SecurityToken: String = "",
        @SerializedName("catalog") var catalog: String = "",
        @SerializedName("ossUrl") var ossUrl: String = "",
        @SerializedName("bucketName") var bucketName: String = ""
)