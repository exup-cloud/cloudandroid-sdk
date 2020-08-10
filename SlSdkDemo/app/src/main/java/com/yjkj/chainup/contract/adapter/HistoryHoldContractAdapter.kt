package com.yjkj.chainup.contract.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.ContractUpDownItemLayout
import java.text.DecimalFormat

/**
 * 合约历史持仓
 */
class HistoryHoldContractAdapter(data: ArrayList<ContractPosition>) : BaseQuickAdapter<ContractPosition, BaseViewHolder>(R.layout.sl_item_history_hold_contract,data){

    override fun convert(helper: BaseViewHolder?, item: ContractPosition) {
        helper?.run {
            val contract: Contract = ContractPublicDataAgent.getContract(item.instrument_id) ?: return
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)
            //方向
            val way: Int = item.side
            val tvType = getView<TextView>(R.id.tv_type)
            when (way) {
               1 -> {
                    tvType.onLineText("sl_str_hold_buy_open0")
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_green))
                }
                2 -> {
                    tvType.onLineText("sl_str_hold_sell_open0")
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_red))
                }
                else -> {
                }
            }
            //合约名称
            setText(R.id.tv_contract_name, contract.symbol)
            setText(R.id.tv_contract_name,  contract.symbol)
            //时间
            setText(R.id.tv_time, TimeFormatUtils.timeStampToDate(TimeFormatUtils.getUtcTimeToMillis(item.updated_at), "yyyy-MM-dd  HH:mm:ss"))
            //开仓均价
            var itemOpenPrice = getView<ContractUpDownItemLayout>(R.id.item_open_price)
            itemOpenPrice.content = dfDefault.format(MathHelper.round(item.avg_open_px, contract.price_index))
            itemOpenPrice.title = mContext.getLineText("sl_str_open_price")+" (${contract.quote_coin})"

            //平仓均价
            var itemCostPrice =  getView<ContractUpDownItemLayout>(R.id.item_cost_price)
            if(item.isForceClosePosition){
                //强平仓位不显示具体金额
                itemCostPrice.content = "--"
            }else{
                itemCostPrice.content = dfDefault.format(MathHelper.round(item.avg_close_px, contract.price_index))
            }
            itemCostPrice.title = mContext.getLineText("sl_str_avg_close_px")+" (${contract.quote_coin})"

            val profit = MathHelper.round(item.earnings)
            //已实现盈亏
            var itemGainsBalance = getView<ContractUpDownItemLayout>(R.id.item_gains_balance)
            itemGainsBalance.content = dfDefault.format(MathHelper.round(profit, contract.value_index))
            itemGainsBalance.title = mContext.getLineText("sl_str_gains_balance")+" (${contract.margin_coin})"
            itemGainsBalance.contentTextColor =  if (profit >= 0){mContext.resources.getColor(R.color.main_green)}else{mContext.resources.getColor(R.color.main_red)}
        }
    }


}