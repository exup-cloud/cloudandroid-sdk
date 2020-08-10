package com.yjkj.chainup.new_version.redpackage.bean


import com.google.gson.annotations.SerializedName

/**
 * 接收红包
 */
data class ReceiveRedPackageListBean(
        @SerializedName("mapList")
        val mapList: ArrayList<ReceiveRedPackageBean?>? = arrayListOf()
) {
        override fun toString(): String {
                return "ReceiveRedPackageListBean(mapList=$mapList)"
        }
}