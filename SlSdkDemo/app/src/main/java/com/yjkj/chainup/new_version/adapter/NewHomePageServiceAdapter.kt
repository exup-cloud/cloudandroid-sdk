package com.yjkj.chainup.new_version.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2018/11/16-4:03 PM
 * @Email buptjinlong@163.com
 * @description 首页 功能服务
 */
open class NewHomePageServiceAdapter(data: ArrayList<JSONObject>) : BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_new_homepage_service_dapter, data) {


    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {

    }

}