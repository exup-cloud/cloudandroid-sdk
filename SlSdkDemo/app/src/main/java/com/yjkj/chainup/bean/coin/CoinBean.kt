package com.yjkj.chainup.bean.coin

import com.google.gson.annotations.SerializedName
import com.yjkj.chainup.bean.BaseStickyBean

data class CoinBean(
        /**
         * @Id 必须是var ,其实必须是long
         */
       // @Id var cId: Long = 0,
        @SerializedName("otcOpen") val otcOpen: Int = 0,
        @SerializedName("depositOpen") val depositOpen: String = "",
        @SerializedName("name") val name: String = "",
        @SerializedName("icon") val icon: String = "",
        @SerializedName("showPrecision") val showPrecision: Int = 0,
        @SerializedName("isSelected") var isSelected: Boolean = false,
        @SerializedName("sort") val sort: Int = 0,
        @SerializedName("tagType") val tagType: Int = 0,
        @SerializedName("tokenBase") val tokenBase: String = "",
        @SerializedName("showName") val anotherName: String = ""


) : BaseStickyBean, Comparator<CoinBean> {
    override fun getStickItem(): String {
        return name.substring(0, 1).toUpperCase()
    }

    override fun compare(o1: CoinBean?, o2: CoinBean?): Int =
            o1!!.getStickItem().compareTo(o2!!.getStickItem())

}

