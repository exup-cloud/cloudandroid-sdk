package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class PayBean(
        @SerializedName("appKey")
        val appKey: String? = "", // f9f05e311538a9b625f129b68cbaf82e
        @SerializedName("assetType")
        val assetType: String? = "", // 201
        @SerializedName("orderNum")
        val orderNum: String? = "", // c21bc5158de9418fbd3edd4ef1197b92
        @SerializedName("userId")
        val userId: String? = "",// 10004
        @SerializedName("googleCode")
        val googleCode: String? = "", // 555
        @SerializedName("smsAuthCode")
        val smsAuthCode: String? = "" // 555
)