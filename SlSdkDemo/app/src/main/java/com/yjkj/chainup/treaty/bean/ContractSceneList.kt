package com.yjkj.chainup.treaty.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019/6/25-10:42 AM
 * @Email buptjinlong@163.com
 * @description
 */
data class ContractSceneList(
        @SerializedName("childItem")
        val childItem: List<ChildItem?>? = arrayListOf(),
        @SerializedName("item")
        val item: String? = "", // CAPITAL_FEE
        @SerializedName("langTxt")
        val langTxt: String = "" // null
) {
    data class ChildItem(
            @SerializedName("item")
            val item: String? = "", // capital_cost_out
            @SerializedName("langTxt")
            val langTxt: String? = "" // 转入资金费用
    )
}