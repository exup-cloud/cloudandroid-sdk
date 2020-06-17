package com.bmtc.sdk.contract.adapter

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bmtc.sdk.contract.PNLShareActivity
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.dialog.AddMarginWindow
import com.bmtc.sdk.contract.dialog.CloseAllbyMarketPriceWindow
import com.bmtc.sdk.contract.dialog.ClosePositionWindow
import com.bmtc.sdk.contract.dialog.PromptWindow
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting
import com.bmtc.sdk.contract.utils.ToastUtil
import com.contract.sdk.ContractPublicDataAgent.getContract
import com.contract.sdk.ContractPublicDataAgent.getContractTicker
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateCloseLongProfitAmount
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateCloseShortProfitAmount
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateContractValue
import com.contract.sdk.extra.Contract.ContractCalculate.CalculatePositionLeverage
import com.contract.sdk.extra.Contract.ContractCalculate.CalculatePositionLiquidatePrice
import com.contract.sdk.extra.Contract.ContractCalculate.calculateProfitByAmount
import com.contract.sdk.extra.Contract.ContractCalculate.calculateRealPositionLeverage
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import java.util.*

/**
 * Created by zj on 2018/3/8.
 */
class HoldContractAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNews: List<ContractPosition>? = ArrayList()

    class HoldContractHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
        var tvType: TextView
        var tvContractName: TextView
        var tvOpenType: TextView
        var tvOpenPrice: TextView
        var tvFloatingGains: TextView
        var tvFloatingGainsBalance: TextView
        var tvHoldings: TextView
        var tvAmountCanbeLiquidated: TextView
        var tvTagPrice: TextView
        var tvForceClosePrice: TextView
        var tvMargins: TextView
        var tvLeverage: TextView
        var tvGainsBalance: TextView
        var tvAdjustMargins: TextView
        var tvClosePositions: TextView
        var tvShare: ImageView

        init {
            tvType = itemView.findViewById(R.id.tv_type)
            tvContractName = itemView.findViewById(R.id.tv_contract_name)
            tvOpenType = itemView.findViewById(R.id.tv_open_type)
            tvOpenPrice = itemView.findViewById(R.id.tv_open_price_value)
            tvFloatingGains = itemView.findViewById(R.id.tv_floating_gains_value)
            tvFloatingGainsBalance = itemView.findViewById(R.id.tv_floating_gains_balance_value)
            tvHoldings = itemView.findViewById(R.id.tv_holdings_value)
            tvAmountCanbeLiquidated = itemView.findViewById(R.id.tv_amount_can_be_liquidated_value)
            tvTagPrice = itemView.findViewById(R.id.tv_tag_price_value)
            tvForceClosePrice = itemView.findViewById(R.id.tv_forced_close_price_value)
            tvMargins = itemView.findViewById(R.id.tv_margins_value)
            tvLeverage = itemView.findViewById(R.id.tv_leverage_value)
            tvGainsBalance = itemView.findViewById(R.id.tv_gains_balance_value)
            tvAdjustMargins = itemView.findViewById(R.id.tv_adjust_margins)
            tvClosePositions = itemView.findViewById(R.id.tv_close_position)
            tvShare = itemView.findViewById(R.id.iv_share)
        }
    }

    fun setData(news: List<ContractPosition>?) {
        if (news == null) {
            return
        }
        mNews = news
    }

    fun getItem(position: Int): ContractPosition {
        return mNews!![position]
    }

    override fun getItemCount(): Int {
        return mNews!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as HoldContractHolder
        val contract = getContract(mNews!![position].instrument_id) ?: return
        val dfDefault = NumberUtil.getDecimal(-1)
        val dfPrice = NumberUtil.getDecimal(contract.price_index)
        val dfValue = NumberUtil.getDecimal(contract.value_index)
        val dfVol = NumberUtil.getDecimal(contract.vol_index)
        var profitRate = 0.0 //未实现盈亏
        var profitAmount = 0.0 //未实现盈亏额
        val contractTicker = getContractTicker(mNews!![position].instrument_id) ?: return
        val pnl_calculate = LogicContractSetting.getPnlCalculate(mContext)
        val side = mNews!![position].side
        if (side == 1) { //多仓
            itemViewHolder.tvType.setText(R.string.sl_str_buy_open)
            itemViewHolder.tvType.setTextColor(mContext.resources.getColor(R.color.sl_colorGreen))
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green)
            profitAmount += CalculateCloseLongProfitAmount(
                    mNews!![position].cur_qty,
                    mNews!![position].avg_cost_px,
                    if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px,
                    contract.face_value,
                    contract.isReserve)
            val p = MathHelper.add(mNews!![position].cur_qty, mNews!![position].close_qty)
            //            double plus = MathHelper.mul(
//                    MathHelper.round(mNews.get(position).getTax()),
//                    MathHelper.div(MathHelper.round(mNews.get(position).getCur_qty()), p));
            profitRate = calculateProfitByAmount(profitAmount.toString(), mNews!![position].oim)!! * 100
        } else if (side == 2) { //空仓
            itemViewHolder.tvType.setText(R.string.sl_str_sell_open)
            itemViewHolder.tvType.setTextColor(mContext.resources.getColor(R.color.sl_colorRed))
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red)
            profitAmount += CalculateCloseShortProfitAmount(
                    mNews!![position].cur_qty,
                    mNews!![position].avg_cost_px,
                    if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px,
                    contract.face_value,
                    contract.isReserve)
            val p = MathHelper.add(mNews!![position].cur_qty, mNews!![position].close_qty)
            //            double plus = MathHelper.mul(
//                    MathHelper.round(mNews.get(position).getTax()),
//                    MathHelper.div(MathHelper.round(mNews.get(position).getCur_qty()), p));
//            //profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mNews.get(position).getIm()), plus)) * 100;
            profitRate = calculateProfitByAmount(profitAmount.toString(), mNews!![position].oim)!! * 100
        }
        var liqPrice = 0.0 //强平价
        val open_type = mNews!![position].position_type
        if (open_type == 1) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_gradually_position)
            liqPrice = CalculatePositionLiquidatePrice(
                    mNews!![position], null, contract)
            //计算杠杆
            itemViewHolder.tvLeverage.text = CalculatePositionLeverage(mNews!![position],  if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px,contract).toString() + mContext.getString(R.string.sl_str_bei)
        } else if (open_type == 2) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_full_position)
            val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(contract.margin_coin)
            if (contractAccount != null) {
                liqPrice = CalculatePositionLiquidatePrice(
                        mNews!![position], contractAccount, contract)
            }
            //计算杠杆
            itemViewHolder.tvLeverage.text = calculateRealPositionLeverage(mNews!![position], contractAccount,if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px, contract).toString() + mContext.getString(R.string.sl_str_bei)
        }
        itemViewHolder.tvContractName.text = contract.symbol
        itemViewHolder.tvOpenPrice.text = dfDefault.format(MathHelper.round(mNews!![position].avg_open_px, contract.price_index)) + contract.quote_coin
        itemViewHolder.tvFloatingGains.text = dfDefault.format(MathHelper.round(profitRate, contract.value_index)) + "%"
        itemViewHolder.tvFloatingGains.setTextColor(if (profitRate >= 0.0) mContext.resources.getColor(R.color.sl_colorGreen) else mContext.resources.getColor(R.color.sl_colorRed))
        itemViewHolder.tvFloatingGainsBalance.text = dfDefault.format(MathHelper.round(profitAmount, contract.value_index)) + contract.margin_coin
        itemViewHolder.tvHoldings.text = dfVol.format(MathHelper.round(mNews!![position].cur_qty)) + mContext.getString(R.string.sl_str_contracts_unit)
        val amountCanbeLiquidated = MathHelper.sub(mNews!![position].cur_qty, mNews!![position].freeze_qty)
        itemViewHolder.tvAmountCanbeLiquidated.text = dfVol.format(amountCanbeLiquidated) + mContext.getString(R.string.sl_str_contracts_unit)
        val value = CalculateContractValue(
                mNews!![position].cur_qty,
                mNews!![position].avg_open_px,
                contract)
        itemViewHolder.tvTagPrice.text = dfDefault.format(MathHelper.round(value, contract.value_index)) + contract.margin_coin
        itemViewHolder.tvMargins.text = dfDefault.format(MathHelper.round(mNews!![position].im, contract.value_index)) + contract.margin_coin
        val profit = MathHelper.round(mNews!![position].earnings)
        itemViewHolder.tvGainsBalance.text = dfDefault.format(MathHelper.round(profit, contract.value_index)) + contract.margin_coin
        itemViewHolder.tvForceClosePrice.text = dfDefault.format(MathHelper.round(liqPrice, contract.price_index)) + contract.quote_coin
        itemViewHolder.tvAdjustMargins.setOnClickListener { view ->
            //                if (open_type == 1) {
            if (mNews != null && mNews!!.size > position) {
                val window = AddMarginWindow(mContext)
                window.setContractPosition(mNews!![position])
                window.showAtLocation(view, Gravity.CENTER, 0, 0)
            }

//                } else if (open_type == 2) {
//                    ToastUtil.shortToast(LogicGlobal.sContext, mContext.getString(R.string.sl_str_full_position_no_adjust_margin));
//                }
        }
        itemViewHolder.tvClosePositions.setOnClickListener { view ->
            val window = ClosePositionWindow(mContext)
            window.setContractPosition(mNews!![position])
            window.showAtLocation(view, Gravity.CENTER, 0, 0)
            window.btnCancel.setOnClickListener {
                window.dismiss()
                if (mNews != null && mNews!!.size > position) {
                    val type = if (mNews!![position].side == 1) mContext.getString(R.string.sl_str_buy_open) else mContext.getString(R.string.sl_str_sell_open)
                    val content = String.format(mContext.getString(R.string.sl_str_close_all_by_market_price_tips), contract.symbol + type)
                    val promptWindow = PromptWindow(mContext)
                    promptWindow.showTitle(mContext.getString(R.string.sl_str_close_all_by_market_price))
                    promptWindow.showTvContent(content)
                    promptWindow.showBtnOk(mContext.getString(R.string.sl_str_confirm))
                    promptWindow.showBtnCancel(mContext.getString(R.string.sl_str_cancel))
                    promptWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
                    promptWindow.btnOk.setOnClickListener {
                        val orderList = getEntrustOrders(mNews!![position])
                        if (orderList != null && orderList.size > 0) {
                            cancelOpenOrders(view, mNews!![position], orderList)
                        } else {
                            closePositionByMarketPrice(view, mNews!![position], "")
                        }
                        promptWindow.dismiss()
                    }
                    promptWindow.btnCancel.setOnClickListener { promptWindow.dismiss() }
                }
            }
        }
        itemViewHolder.tvShare.setOnClickListener {
            val intent = Intent(mContext, PNLShareActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("coin_code", contract.margin_coin)
            intent.putExtra("position_id", mNews!![position].pid)
            mContext.startActivity(intent)
        }
    }

    private fun getEntrustOrders(position: ContractPosition?): List<ContractOrder>? {
        if (position == null) {
            return null
        }
        val orderList: List<ContractOrder> = ContractUserDataAgent.getContractOrder(position.instrument_id)
        if (orderList == null || orderList.isEmpty()) {
            return null
        }
        val position_type = position.side
        val entrustList: MutableList<ContractOrder> = ArrayList()
        for (i in orderList.indices) {
            val order = orderList[i] ?: continue
            if (position_type == ContractPosition.POSITION_TYPE_LONG) {
                if (order.side == ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
                    entrustList.add(order)
                }
            } else if (position_type == ContractPosition.POSITION_TYPE_SHORT) {
                if (order.side == ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
                    entrustList.add(order)
                }
            }
        }
        return entrustList
    }

    private fun closePositionByMarketPrice(view: View?, position: ContractPosition?, pwd: String) {
        if (position == null || view == null) {
            return
        }
        val contract: Contract = getContract(position.instrument_id) ?: return
        val dfVol = NumberUtil.getDecimal(contract.vol_index)
        val vol = MathHelper.sub(position.cur_qty, position.freeze_qty)
        val order = ContractOrder()
        order.instrument_id = position.instrument_id
        order.nonce = System.currentTimeMillis()
        order.qty = dfVol.format(vol)
        if (position.side == 1) {
            order.pid = position.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        } else {
            order.pid = position.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }
        order.category = ContractOrder.ORDER_CATEGORY_MARKET
        view.isEnabled = false

        ContractUserDataAgent.doSubmitOrder(order,object:IResponse<String>(){
            override fun onSuccess(data: String) {
                ToastUtil.shortToast(mContext,mContext.getString(R.string.sl_str_order_submit_success))
            }

            override fun onFail(code: String, msg: String) {
                ToastUtil.shortToast(mContext, msg)
            }

        })
    }

    private fun cancelOpenOrders(view: View, position: ContractPosition, orderList: List<ContractOrder>) {
        val window = CloseAllbyMarketPriceWindow(mContext)
        window.setOrderList(position, orderList)
        window.showAtLocation(view, Gravity.CENTER, 0, 0)
        window.btnCloseAll.setOnClickListener {
            val newPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(position.instrument_id, position.side)
            if (newPosition != null) {
                closePositionByMarketPrice(view, newPosition, "")
            }
            window.dismiss()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sl_item_hold_contract, parent, false)
        return HoldContractHolder(v, viewType)
    }

}