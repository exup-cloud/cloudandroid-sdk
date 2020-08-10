package com.yjkj.chainup.new_version.adapter

import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.treaty.bean.ContractSceneList
import java.util.*

/**
 * @Author lianshangljl
 * @Date 2019/6/25-9:49 PM
 * @Email buptjinlong@163.com
 * @description
 */
class ContractBillAdapter(data: ArrayList<ContractSceneList.ChildItem?>, var position: Int = 0) :
        BaseQuickAdapter<ContractSceneList.ChildItem, BaseViewHolder>(R.layout.item_new_screening_label, data) {


    fun setSelectPosition(index: Int) {
        position = index
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder?, item: ContractSceneList.ChildItem?) {
        if (position == helper?.adapterPosition) {
            helper.getView<RelativeLayout>(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style)
            helper.setGone(R.id.cut_view, true)
        } else {
            helper?.getView<RelativeLayout>(R.id.ll_layout)?.setBackgroundResource(R.drawable.bg_new_unselect_style)
            helper?.setGone(R.id.cut_view, false)
        }

        helper?.setText(R.id.tv_parent_content, item?.langTxt)
    }

}
