package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

data class CreatePackageBean(
        @SerializedName("appKey")
        val appKey: String? = "", // f9f05e311538a9b625f129b68cbaf82e
        @SerializedName("assetType")
        val assetType: Int? = 0, // 201
        @SerializedName("background")
        val background: String? = "", // http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20190629120150117.jpg
        @SerializedName("coinSymbol")
        val coinSymbol: String? = "", // btc
        @SerializedName("nickName")
        val nickName: String? = "", // 132****1112
        @SerializedName("orderNum")
        val orderNum: String? = "", // c21bc5158de9418fbd3edd4ef1197b92
        @SerializedName("shareUrl")
        val shareUrl: String? = "", // https://www.biki.com//600f1e768a37f95e0b4f295d28f1473e
        @SerializedName("toPayUri")
        val toPayUri: String? = "", // https://www.opbiki.com/to_pay
        @SerializedName("userId")
        val userId: Int? = 0, // 10004
        @SerializedName("isVersion2")
        val isVersion2: Int? = 0 // 1 // 是否新版本，存在该值的情况，支付接口请求本地
) {
    override fun toString(): String {
        return "CreatePackageBean(appKey=$appKey, assetType=$assetType, background=$background, coinSymbol=$coinSymbol, nickName=$nickName, orderNum=$orderNum, shareUrl=$shareUrl, toPayUri=$toPayUri, userId=$userId, isVersion2=$isVersion2)"
    }
}