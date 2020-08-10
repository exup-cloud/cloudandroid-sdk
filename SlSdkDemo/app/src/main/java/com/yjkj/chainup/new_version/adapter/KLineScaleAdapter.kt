package com.yjkj.chainup.new_version.adapter

import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.KLineUtil
import com.yjkj.chainup.new_version.view.CustomCheckBoxView

/**
 * @Author: Bertking
 * @Date：2019/3/20-7:25 PM
 * @Description: 竖版的KLine刻度
 */
class KLineScaleAdapter(scales: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_kline_scale, scales) {
    val TAG = KLineScaleAdapter::class.java.simpleName
    override fun convert(helper: BaseViewHolder?, item: String?) {
        Log.d(TAG, "======&&&&==+item:$item")

        val boxView = helper?.getView<CustomCheckBoxView>(R.id.cbtn_view)
        if(helper?.adapterPosition == 0){
            boxView?.setMiddle("line")
        }else{
            boxView?.setMiddle(item!!)
        }
        boxView?.setCenterColor(ColorUtil.getColor(R.color.normal_text_color))
        boxView?.setCenterSize(12f)
        boxView?.setIsNeedDraw(false)
        boxView?.isChecked = false
        if (KLineUtil.getCurTime4Index() == helper?.adapterPosition) {
            boxView?.isChecked = true
            boxView?.setIsNeedDraw(true)
            boxView?.setCenterColor(ColorUtil.getColor(R.color.text_color))
        } else {
            boxView?.isChecked = false
            boxView?.setCenterColor(ColorUtil.getColor(R.color.normal_text_color))
            boxView?.setIsNeedDraw(false)
        }
    }
}