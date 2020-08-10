package com.yjkj.chainup.new_version.bean


import com.google.gson.annotations.SerializedName

data class KYCBean(
        @SerializedName("openSingPass")
        val openSingPass: String? = "", // 0
        @SerializedName("verfyTemplet")
        val verfyTemplet: String? = "", // 1
        @SerializedName("h5_templet2_url")
        val h5_templet2_url: String? = "",// minim veniam Ut irure
        @SerializedName("h5_singpass_url")
        val h5_singpass_url: String? = "" // minim veniam Ut irure
)