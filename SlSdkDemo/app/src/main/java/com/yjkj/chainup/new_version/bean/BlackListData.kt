package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

data class BlackListData(
        @SerializedName("relationshipList") val relationshipList: ArrayList<Relationship> = ArrayList(),
        @SerializedName("count") val count: Int //2
) {
    data class Relationship(
            @SerializedName("userId") val userId: String = "", //10002
            @SerializedName("otcNickName") val otcNickName: String = "", //185****7133
            @SerializedName("creditGrade") val creditGrade: Double = 0.0, //0.9478
            @SerializedName("image") val imageUrl: String = "", //url
            @SerializedName("completeOrders") val completeOrders: Int //43
    )
}