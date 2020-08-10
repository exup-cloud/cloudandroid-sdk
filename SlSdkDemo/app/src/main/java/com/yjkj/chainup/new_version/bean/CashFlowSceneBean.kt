package com.yjkj.chainup.new_version.bean


import com.google.gson.annotations.SerializedName

data class CashFlowSceneBean(
        @SerializedName("sceneList")
        val sceneList: ArrayList<Scene> = arrayListOf()
) {
    data class Scene(
            @SerializedName("key")
            val key: String? = "", // unlock_position_v2
            @SerializedName("key_text")
            val keyText: String? = "" // 代币锁仓
    )
}