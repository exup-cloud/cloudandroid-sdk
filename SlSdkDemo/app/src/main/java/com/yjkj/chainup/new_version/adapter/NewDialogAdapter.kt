package com.yjkj.chainup.new_version.adapter

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R

/**
 * @Author lianshangljl
 * @Date 2019/3/8-5:37 PM
 * @Email buptjinlong@163.com
 * @description
 */
open class NewDialogAdapter(data: ArrayList<String>, var position: Int) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_string_dialog_adapter, data) {

    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.setText(R.id.tv_content, item)
        if (position == helper?.position) {
            helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.main_blue))
        } else {
            helper?.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.text_color))
        }
    }

}