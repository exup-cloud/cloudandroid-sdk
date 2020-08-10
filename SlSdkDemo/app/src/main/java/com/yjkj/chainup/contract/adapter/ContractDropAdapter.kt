package com.yjkj.chainup.contract.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.util.ColorUtil
import org.jetbrains.anko.textColor
import java.util.*

/**
 * Created by zj on 2018/3/7.
 */
class ContractDropAdapter(data: ArrayList<ContractTicker>) : BaseQuickAdapter<ContractTicker, BaseViewHolder>(R.layout.sl_item_contract_drop, data) {

    class SpotViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
        var rlContent: RelativeLayout
        var tvContractName: TextView
        var tvContractChg: TextView

        init {
            rlContent = itemView.findViewById(R.id.rl_content)
            tvContractName = itemView.findViewById(R.id.tv_contract_name)
            tvContractChg = itemView.findViewById(R.id.tv_contract_chg)
        }
    }
    override fun convert(helper: BaseViewHolder?, ticker: ContractTicker?) {
        ticker?.let {
           val contract = ContractPublicDataAgent.getContract(it.instrument_id) ?: return
           val dfVol = NumberUtil.getDecimal(contract.vol_index)
           val dfPrice = NumberUtil.getDecimal(contract.price_index)
           val dfRate = NumberUtil.getDecimal(2)
            //合约名
            val name = contract.getDisplayName(mContext)
            helper?.setText(R.id.tv_contract_name,name)
            val chg = MathHelper.round(it.change_rate.toDouble() * 100, 2)
            //比例
            val tvContractChg = helper?.getView<TextView>(R.id.tv_contract_chg)
            tvContractChg?.run {
                text =  if (chg >= 0) "+" + dfRate.format(chg) + "%" else dfRate.format(chg) + "%"
                textColor =  ColorUtil.getMainColorType(chg >= 0)
            }
            helper?.getView<RelativeLayout>(R.id.rl_content)?.setOnClickListener{
                var msgEvent = MessageEvent(MessageEvent.sl_contract_left_coin_type)
                msgEvent.msg_content = ticker
                EventBusUtil.post(msgEvent)
            }
       }
    }




}