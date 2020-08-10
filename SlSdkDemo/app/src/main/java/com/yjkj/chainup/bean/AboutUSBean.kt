package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @author Bertking
 * @date 2018-9-14
 * @description 关于我们的Bean
 * @link http://wiki.365os.com/pages/viewpage.action?pageId=5152292
 */
data class AboutUSBean(
        @SerializedName("title") val title: String = "", //版本号
        @SerializedName("content") val content: String = "" //V1.0
)

data class PushItem(

        @SerializedName("status") val status: String = "0",
        @SerializedName("list") val list: ArrayList<PushBean>? = null

)

data class PushBean(
        @SerializedName("title") val title: String = "", //版本号
        @SerializedName("type") val type: String = "", //V1.0
        @SerializedName("value") var value: Boolean = true //V1.0
) {
    fun isSystemView():Boolean {
        return type == "all"
    }
}