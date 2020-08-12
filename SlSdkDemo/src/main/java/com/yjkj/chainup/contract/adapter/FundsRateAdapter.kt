package com.yjkj.chainup.contract.adapter

import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.data.ContractFundingRate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.utils.*
import java.util.*

/**
 * 资金费率
 */
class FundsRateAdapter(ctx:Context,data: ArrayList<ContractFundingRate>) : BaseQuickAdapter<ContractFundingRate, BaseViewHolder>(R.layout.sl_item_funding_rate, data) {
    //交割周期 单位秒
    var settlementInterval:Int? = 0

    var sl_str_every_8hours = ""
    init {
        sl_str_every_8hours = ctx.getLineText("sl_str_every_8hours")
    }

    override fun convert(helper: BaseViewHolder?, item: ContractFundingRate) {
        helper?.run {

            getView<TextView>(R.id.tv_interval).text = sl_str_every_8hours
            //资金费率
            val rate = MathHelper.mul(item.rate, "100")
            getView<TextView>(R.id.tv_funding_rate).text = NumberUtil.getDecimal(4).format(rate).toString() + "%"
            //时间
            getView<TextView>(R.id.tv_time).text = TimeFormatUtils.timeStampToDate((item.timestamp+settlementInterval!!)*1000,"yyyy-MM-dd  HH:mm:ss")
        }
    }

}