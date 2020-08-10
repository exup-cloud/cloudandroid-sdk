package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

data class HelpCenterBean(
        @SerializedName("fileName") val fileName: String = "", ///34324324.html
        @SerializedName("id") val id: Int, //214
        @SerializedName("title") val title: String = ""//如何开启微信授权
)