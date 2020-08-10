package com.yjkj.chainup.contract.adapter

import android.app.Activity
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.activity.SlHoldShareActivity
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.GlobalLeverageUtils
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.NToastUtil
import java.text.DecimalFormat

class HoldContractAdapter(data: ArrayList<ContractPosition>) : BaseQuickAdapter<ContractPosition, BaseViewHolder>(R.layout.sl_item_hold_contract, data) {
    private var listener: OnItemClickedListener? = null
    fun bindListener(listener: OnItemClickedListener) {
        this.listener = listener
    }


    override fun convert(helper: BaseViewHolder?, item: ContractPosition) {
        helper?.run {
            val contract: Contract = ContractPublicDataAgent.getContract(item.instrument_id)
                    ?: return

            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)
            val dfPrice: DecimalFormat = NumberUtil.getDecimal(contract.price_index)
            val dfValue: DecimalFormat = NumberUtil.getDecimal(contract.value_index)
            val dfVol: DecimalFormat = NumberUtil.getDecimal(contract.vol_index)


            var profitRate = 0.0 //未实现盈亏
            var profitAmount = 0.0 //未实现盈亏额
            val contractTicker: ContractTicker? = ContractPublicDataAgent.getContractTicker(item.instrument_id)
                    ?: return
            val pnl_calculate: Int = LogicContractSetting.getPnlCalculate(mContext)
            val side: Int = item.side

            when (side) {
                1 -> {
                    //多仓
                    setText(R.id.tv_type, mContext.getLineText("sl_str_hold_buy_open0"))
                    setTextColor(R.id.tv_type, mContext.resources.getColor(R.color.main_green))

                    profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                            item.cur_qty,
                            item.avg_cost_px,
                            if (pnl_calculate == 0) contractTicker?.fair_px else contractTicker?.last_px,
                            contract.face_value,
                            contract.isReserve)
                    val p: Double = MathHelper.add(item.cur_qty, item.close_qty)
                    val plus: Double = MathHelper.mul(
                            MathHelper.round(item.tax),
                            MathHelper.div(MathHelper.round(item.cur_qty), p))
                    profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(item.im), plus)) * 100
                }
                2 -> {
                    //空仓
                    setText(R.id.tv_type, mContext.getLineText("sl_str_hold_sell_open0"))
                    setTextColor(R.id.tv_type, mContext.resources.getColor(R.color.main_red))
                    profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                            item.cur_qty,
                            item.avg_cost_px,
                            if (pnl_calculate == 0) contractTicker?.fair_px else contractTicker?.last_px,
                            contract.face_value,
                            contract.isReserve)

                    val p: Double = MathHelper.add(item.cur_qty, item.close_qty)
                    val plus = MathHelper.mul(
                            MathHelper.round(item.tax),
                            MathHelper.div(MathHelper.round(item.cur_qty), p))
                    profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(item.im), plus)) * 100
                }
                else -> {
                }
            }
            var liqPrice = 0.0 //强平价
            val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(contract.margin_coin)
            val open_type: Int = item.position_type
            if (open_type == 1) {
                setText(R.id.tv_open_type, mContext.getLineText("sl_str_gradually_position"))

                liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                        item, null, contract)
                if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                    setText(R.id.tv_leverage_value, item.avg_fixed_leverage)
                } else {
                    //实际杠杆
                    setText(R.id.tv_leverage_value, ContractCalculate.CalculatePositionLeverage(item, if (pnl_calculate == 0) contractTicker!!.fair_px else contractTicker!!.last_px, contract).toString())
                }
            } else if (open_type == 2) {//全仓
                setText(R.id.tv_open_type, mContext.getLineText("sl_str_full_position"))
                if (contractAccount != null) {
                    liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                            item, contractAccount, contract)
                }
                if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                    setText(R.id.tv_leverage_value, item.avg_fixed_leverage)
                } else {
                    //实际杠杆
                    setText(R.id.tv_leverage_value, ContractCalculate.calculateRealPositionLeverage(item, contractAccount, if (pnl_calculate == 0) contractTicker!!.fair_px else contractTicker!!.last_px, contract).toString())
                }
            }
            setText(R.id.tv_leverage, mContext.getLineText("sl_str_actual_lever") + " (${mContext.getLineText("sl_str_bei")})")

            setText(R.id.tv_contract_name, contract.symbol)
            //持仓均价
            setText(R.id.tv_open_price_value, dfPrice.format(MathHelper.round(item.avg_cost_px, contract.price_index)))
            setText(R.id.tv_open_price, mContext.getLineText("sl_str_cost_price") + " (${contract.quote_coin})")
            //强平价格
            setText(R.id.tv_forced_close_price_value, dfPrice.format(MathHelper.round(liqPrice, contract.price_index)))
            setText(R.id.tv_forced_close_price, mContext.getLineText("sl_str_forced_close_price") + " (${contract.quote_coin})")
            //保证金
            setText(R.id.tv_margins_value, dfDefault.format(MathHelper.round(item.im, contract.value_index)))
            setText(R.id.tv_margins, mContext.getLineText("sl_str_margins") + " (${contract.margin_coin})")

            val profit = MathHelper.round(item.earnings)
            //已实现盈亏
            setText(R.id.tv_gains_balance_value, dfDefault.format(MathHelper.round(profit, contract.value_index)))
            setText(R.id.tv_gains_balance, mContext.getLineText("sl_str_gains_balance") + " (${contract.margin_coin})")
            //未实现盈亏
            setText(R.id.tv_floating_gains_balance_value, if (profitAmount >= 0) {
                "+"
            } else {
                ""
            } + dfDefault.format(MathHelper.round(profitAmount, contract.value_index)))
            setTextColor(R.id.tv_floating_gains_balance_value, if (profitAmount >= 0) {
                mContext.resources.getColor(R.color.main_green)
            } else {
                mContext.resources.getColor(R.color.main_red)
            })
            //回报率 保持小数点后2位
            setText(R.id.tv_floating_gains, mContext.getLineText("sl_str_floating_gains"))
            setText(R.id.tv_floating_gains_value, if (profitRate >= 0) {
                "+"
            } else {
                ""
            } +
                    dfDefault.format(MathHelper.round(profitRate, 2)).toString() + "%"
            )
            setTextColor(R.id.tv_floating_gains_value, if (profitAmount >= 0) {
                mContext.resources.getColor(R.color.main_green)
            } else {
                mContext.resources.getColor(R.color.main_red)
            })
            val amountCanbeLiquidated: Double = MathHelper.sub(item.cur_qty, item.freeze_qty)
            //可平仓量
            setText(R.id.tv_amount_can_be_liquidated_value, dfVol.format(amountCanbeLiquidated))
            setText(R.id.tv_amount_can_be_liquidated, mContext.getLineText("sl_str_amount_can_be_liquidated") + " (${mContext.getLineText("sl_str_contracts_unit")})")
            //持仓量
            setText(R.id.tv_holdings_value, dfVol.format(MathHelper.round(item.cur_qty)))
            setText(R.id.tv_holdings, mContext.getLineText("sl_str_holdings") + " (${mContext.getLineText("sl_str_contracts_unit")})")
            //价值
            val value = ContractCalculate.CalculateContractValue(
                    item.cur_qty,
                    item.avg_cost_px,
                    contract)
            setText(R.id.tv_tag_price_value, dfValue.format(MathHelper.round(value, contract.value_index)))
            setText(R.id.tv_tag_price, String.format(mContext.getLineText("sl_str_tag_price"), contract.margin_coin))
            //强平价格弹窗
            getView<TextView>(R.id.tv_forced_close_price).setOnClickListener {
                NewDialogUtils.showDialog(mContext!!, mContext.getLineText("sl_str_forced_close_price_tips", true), true, null, mContext.getLineText("common_text_tip"), mContext.getLineText("sl_str_isee"))
            }
            //未实现盈亏/回报率弹窗
            setText(R.id.tv_floating_gains_balance, mContext.getLineText("sl_str_floating_gains_balance"))
            getView<TextView>(R.id.tv_floating_gains_balance).setOnClickListener {
                NewDialogUtils.showDialog(mContext!!, mContext.getLineText("sl_str_floating_gains_balance_tips"), true, null, mContext.getLineText("common_text_tip"), mContext.getLineText("sl_str_isee"))
            }
            //以实现盈亏弹窗
            getView<TextView>(R.id.tv_gains_balance).setOnClickListener {
                NewDialogUtils.showDialog(mContext!!, mContext.getLineText("sl_str_gains_balance_tips"), true, null, mContext.getLineText("common_text_tip"), mContext.getLineText("sl_str_isee"))
            }
            //仓位分享
            setText(R.id.iv_share, mContext.getLineText("contract_share_label"))
            getView<TextView>(R.id.iv_share).setOnClickListener {
                SlHoldShareActivity.show((mContext as Activity?)!!, contract.margin_coin, contract.instrument_id, item.pid)
            }
            //划转保证金
            setText(R.id.tv_adjust_margins, mContext.getLineText("sl_str_adjust_margins"))
            getView<TextView>(R.id.tv_adjust_margins).setOnClickListener {
                listener?.doTransferMargin(item)
            }
            //平仓
            setText(R.id.tv_close_position, mContext.getLineText("sl_str_close"))
            getView<TextView>(R.id.tv_close_position).setOnClickListener {
                listener?.doClosePosition(item)
            }
            //止盈止损
            setText(R.id.tv_stop_profit_loss, mContext.getLineText("sl_str_stop_profit_loss"))
            getView<TextView>(R.id.tv_stop_profit_loss).setOnClickListener {
                listener?.doStopRateLoss(item)
            }
        }
    }


    interface OnItemClickedListener {
        /**
         * 资金划转
         */
        fun doTransferMargin(info: ContractPosition)

        /**
         * 平仓
         */
        fun doClosePosition(info: ContractPosition)

        /**
         *  止盈止损
         */
        fun doStopRateLoss(info: ContractPosition)
    }


}