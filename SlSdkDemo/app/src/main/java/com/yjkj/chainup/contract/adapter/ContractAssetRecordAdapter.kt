package com.yjkj.chainup.contract.adapter

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.ContractCashBook
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractOrders
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.pswkeyboard.widget.PopEnterPassword
import com.yjkj.chainup.util.ToastUtils
import java.text.DecimalFormat

/**
 * 合约资产记录
 */
class ContractAssetRecordAdapter(ctx:Context, data: ArrayList<ContractCashBook>) : BaseQuickAdapter<ContractCashBook, BaseViewHolder>(R.layout.sl_item_asset_record, data) {

    var journalAccount_text_amount = ""
    var sl_str_fee = ""
    var sl_str_balance = ""
    var sl_str_buy_open = ""
    var sl_str_buy_close = ""
    var sl_str_sell_close = ""
    var sl_str_sell_open = ""
    var sl_str_transfer_bb2contract = ""
    var sl_str_transfer_contract2bb = ""
    var sl_str_transferim_position2contract = ""
    var sl_str_transferim_contract2position = ""
    var contract_fee_share = ""
    var sl_str_air_drop = ""
    var sl_str_contract_bonus_Issue = ""
    var sl_str_contract_bonus_recycle = ""

    init {
        journalAccount_text_amount = ctx.getLineText("journalAccount_text_amount")
        sl_str_fee = ctx.getLineText("sl_str_fee")
        sl_str_balance = ctx.getLineText("sl_str_balance")
        sl_str_buy_open = ctx.getLineText("sl_str_buy_open")
        sl_str_buy_close = ctx.getLineText("sl_str_buy_close")
        sl_str_sell_close = ctx.getLineText("sl_str_sell_close")
        sl_str_sell_open = ctx.getLineText("sl_str_sell_open")
        sl_str_transfer_bb2contract = ctx.getLineText("sl_str_transfer_bb2contract")
        sl_str_transfer_contract2bb = ctx.getLineText("sl_str_transfer_contract2bb")
        sl_str_transferim_position2contract = ctx.getLineText("sl_str_transferim_position2contract")
        sl_str_transferim_contract2position = ctx.getLineText("sl_str_transferim_contract2position")
        contract_fee_share = ctx.getLineText("contract_fee_share")
        sl_str_air_drop = ctx.getLineText("sl_str_air_drop")
        sl_str_contract_bonus_Issue = ctx.getLineText("contract_bonus_Issue")
        sl_str_contract_bonus_recycle = ctx.getLineText("contract_bonus_recycle")

    }

    override fun convert(helper: BaseViewHolder?, item: ContractCashBook) {
        helper?.run {
            val contract  = ContractPublicDataAgent.getContract(item.instrument_id)
            val volIndex:Int = contract?.value_index?:6
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)

            //金额
            setText(R.id.tv_amount_value,dfDefault.format(MathHelper.round(item.deal_count,volIndex)))
            getView<TextView>(R.id.tv_amount_label).text = journalAccount_text_amount
            //手续费
            setText(R.id.tv_fee_value,dfDefault.format(MathHelper.round(item.fee,volIndex)))
            getView<TextView>(R.id.tv_fee_label).text = sl_str_fee
            //余额
            setText(R.id.tv_balance_value,dfDefault.format(MathHelper.round(item.last_assets,volIndex)))
            getView<TextView>(R.id.tv_balance_label).text = sl_str_balance
            //时间
            setText(R.id.tv_time_value, TimeFormatUtils.convertZTime(item.created_at,TimeFormatUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MIN_SECOND))
            //类型
            val action: Int = item.action
            val tvTypeValue = getView<TextView>(R.id.tv_type_value)
            var typeColor = R.color.main_green
            var typeText = "--"
            when (action) {
                1 ->{
                    typeColor = R.color.main_green
                    typeText = sl_str_buy_open
                }
                2 -> {
                    typeColor = R.color.main_green
                    typeText = sl_str_buy_close
                }
                3 -> {
                    typeColor = R.color.main_red
                    typeText = sl_str_sell_close
                }
                4 -> {
                    typeColor = R.color.main_red
                    typeText = sl_str_sell_open
                }
                5, 7 -> {
                    typeColor = R.color.main_green
                    typeText = sl_str_transfer_bb2contract
                }
                6, 8 ->{
                    typeColor = R.color.main_red
                    typeText = sl_str_transfer_contract2bb
                }
                9 -> {
                    typeColor = R.color.main_red
                    typeText = sl_str_transferim_position2contract
                }
                10 -> {
                    typeColor = R.color.main_green
                    typeText = sl_str_transferim_contract2position
                }
                11 -> {
                    typeColor = R.color.main_red
                    typeText = sl_str_transferim_contract2position
                }
                12 -> {
                    //赠金发放 contract_bonus_Issue
                    typeText = sl_str_contract_bonus_Issue
                    typeColor = R.color.main_green
                }
                13 -> {
                    //赠金回收 contract_bonus_recycle
                    typeText = sl_str_contract_bonus_recycle
                    typeColor = R.color.main_red
                }
                20 -> {
                    typeColor = R.color.main_green
                    typeText = contract_fee_share
                }
                21 -> {
                    typeColor = R.color.main_green
                    typeText = sl_str_air_drop
                }
            }
            tvTypeValue.run {
                text = typeText
                setTextColor(mContext.resources.getColor(typeColor))
            }
        }
    }


}