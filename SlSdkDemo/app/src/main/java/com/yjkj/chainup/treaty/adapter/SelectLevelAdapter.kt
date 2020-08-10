package com.yjkj.chainup.treaty.adapter

import android.support.v4.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.coorchice.library.SuperTextView
import com.yjkj.chainup.R
import org.jetbrains.anko.textColor

/**
 * @Author: Bertking
 * @Date：2019/1/17-5:25 PM
 * @Description:
 */
open class SelectLevelAdapter(data: ArrayList<String>, curLever: String) :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_select_level, data) {
    var curLever = curLever
    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.setText(R.id.tv_index_price, item+"x")

        if (curLever == item) {
            helper?.getView<SuperTextView>(R.id.tv_index_price)?.solid = ContextCompat.getColor(mContext, R.color.main_color)
            helper?.getView<SuperTextView>(R.id.tv_index_price)?.textColor = ContextCompat.getColor(mContext, R.color.white)
        }
    }

}