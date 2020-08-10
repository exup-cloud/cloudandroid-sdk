package com.yjkj.chainup.new_version.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2018/10/17-下午5:13
 * @Email buptjinlong@163.com
 * @description 选择 语言
 */
open class OTCChangeLanguageAdapter(data: ArrayList<JSONObject>) : BaseQuickAdapter<JSONObject
        , BaseViewHolder>(R.layout.item_language_adapter, data) {
    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
        helper?.setText(R.id.item_change_payment_name, item?.optString("name"))
        helper?.setGone(R.id.item_change_payment_status, item?.optBoolean("open") ?: false)
    }

}