package com.yjkj.chainup.new_version.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2018/11/8-10:20 AM
 * @Email buptjinlong@163.com
 * @description  新首页涨跌幅
 *
 * data: ArrayList<JSONObject>
 */
open class NewhomepageTradeListAdapter : BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_new_home_page_trade) {

    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
    }

}