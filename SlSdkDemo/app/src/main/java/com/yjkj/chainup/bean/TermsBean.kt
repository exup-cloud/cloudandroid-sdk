package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

data class TermsBean(
        @SerializedName("url") val url: String = "",
        @SerializedName("titleKey") val titleKey: String = ""


) {
    override fun toString(): String {
        return "TermsBean(url='$url', titleKey='$titleKey')"
    }
}