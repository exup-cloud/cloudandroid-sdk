package com.yjkj.chainup.contract.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractOrders
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.coorchice.library.SuperTextView
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.pswkeyboard.widget.PopEnterPassword
import com.yjkj.chainup.net_new.NLoadingDialog
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.dialog.NewDialogUtils.DialogBottomListener
import com.yjkj.chainup.util.ToastUtils
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 合约计划委托
 */
class ContractPlanEntrustAdapter(context: Context, data: ArrayList<ContractOrder>) : BaseQuickAdapter<ContractOrder, BaseViewHolder>(R.layout.sl_item_contract_plan_entrust, data) {
    private var loadDialog : NLoadingDialog?=null
    private var isCurrentEntrust = true

    var sl_str_buy_open0 = ""
    var sl_str_sell_open0 = ""
    var contract_flat_short = ""
    var contract_flat_long = ""
    var sl_str_latest_price_simple = ""
    var sl_str_fair_price_simple = ""
    var sl_str_index_price_simple = ""
    var sl_str_trigger_price = ""
    var sl_str_execution_price = ""
    var sl_str_execution_volume = ""
    var sl_str_contracts_unit = ""
    var sl_str_market_price_simple = ""
    var sl_str_deadline = ""
    var sl_str_trigger_time = ""
    var sl_str_cancel_order = ""
    var sl_str_order_complete = ""
    var sl_str_user_canceled = ""
    var sl_str_order_timeout = ""
    var sl_str_trigger_failed = ""

    init {
        sl_str_buy_open0 = context.getLineText("sl_str_buy_open0")
        sl_str_sell_open0 = context.getLineText("sl_str_sell_open0")
        contract_flat_short = context.getLineText("contract_flat_short")
        contract_flat_long = context.getLineText("contract_flat_long")
        sl_str_latest_price_simple = context.getLineText("sl_str_latest_price_simple")
        sl_str_fair_price_simple = context.getLineText("sl_str_fair_price_simple")
        sl_str_index_price_simple = context.getLineText("sl_str_index_price_simple")
        sl_str_trigger_price = context.getLineText("sl_str_trigger_price")
        sl_str_execution_price = context.getLineText("sl_str_execution_price")
        sl_str_execution_volume = context.getLineText("sl_str_execution_volume")
        sl_str_contracts_unit = context.getLineText("sl_str_contracts_unit")
        sl_str_market_price_simple = context.getLineText("sl_str_market_price_simple")
        sl_str_deadline =  context.getLineText("sl_str_deadline")
        sl_str_trigger_time = context.getLineText("sl_str_trigger_time")
        sl_str_cancel_order = context.getLineText("sl_str_cancel_order")
        sl_str_order_complete = context.getLineText("sl_str_order_complete")
        sl_str_user_canceled = context.getLineText("sl_str_user_canceled")
        sl_str_order_timeout = context.getLineText("sl_str_order_timeout")
        sl_str_trigger_failed = context.getLineText("sl_str_order_timeout")
    }


    fun setIsCurrentEntrust(isCurrentEntrust: Boolean = true) {
        this.isCurrentEntrust = isCurrentEntrust
    }


    override fun convert(helper: BaseViewHolder?, item: ContractOrder?) {
        if(item == null){
            return
        }
        helper?.run {
            val contract: Contract = ContractPublicDataAgent.getContract(item.instrument_id)
                    ?: return


            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)
            val dfVol: DecimalFormat = NumberUtil.getDecimal(contract.vol_index)
            //方向
            val way: Int = item.side
            val tvType = getView<TextView>(R.id.tv_type)
            when (way) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG -> {
                    tvType.text = sl_str_buy_open0
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_green))
                }
                ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    tvType.text = sl_str_sell_open0
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_red))
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT -> {
                    tvType.text = contract_flat_short
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_green))
                }
                ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                    tvType.text = contract_flat_long
                    tvType.setTextColor(mContext.resources.getColor(R.color.main_red))
                }
                else -> {
                }
            }
            //委托价格类型
            var price_type = ""
            var trigger_type = item.trigger_type
            when {
                trigger_type === 1 -> {
                    price_type = sl_str_latest_price_simple
                }
                trigger_type === 2 -> {
                    price_type = sl_str_fair_price_simple
                }
                trigger_type === 4 -> {
                    price_type = sl_str_index_price_simple
                }
            }

            val createdTime = TimeFormatUtils.getUtcTimeToMillis(item.created_at)


            //合约名称
            setText(R.id.tv_contract_name, contract.symbol)
            //触发价格
            setText(R.id.tv_trigger_price,sl_str_trigger_price +" (" +contract.quote_coin +")")
            setText(R.id.tv_trigger_price_value,price_type + " " + dfDefault.format(MathHelper.round(item.px, contract.price_index)))

            val category: Int = item.category
            var executionPriceTitle = sl_str_execution_price +" (" +contract.quote_coin +")"
            var executionVolumeTitle = sl_str_execution_volume +" ("+sl_str_contracts_unit+")"
            var expireTime =  if(item.life_cycle == 24){
                TimeFormatUtils.timeStampToDate(createdTime+1000*60*60*24,"yyyy-MM-dd  HH:mm:ss")
            }else{
                TimeFormatUtils.timeStampToDate(createdTime+1000*60*60*24*7,"yyyy-MM-dd  HH:mm:ss")
            }
            //TODO  产品设计，当前委托和历史委托 布局不一样，故此处针对单独重新进行布局调整
            if(isCurrentEntrust){
                //执行价格
                setVisible(R.id.ll_hold_layout2,true)
                setText(R.id.tv_hold_2,executionPriceTitle)
                if (category and 127 == 2){
                    setText(R.id.tv_hold_value_2, sl_str_market_price_simple)
                }else{
                    setText(R.id.tv_hold_value_2,item.exec_px)
                }
                //执行数量
                setText(R.id.tv_hold_3, executionVolumeTitle)
                if(item.type == 1 || item.type == 2){
                    setText(R.id.tv_hold_value_3,"100%")
                }else{
                    setText(R.id.tv_hold_value_3,dfVol.format(MathHelper.round(item.qty)))
                }
                //到期时间  原时间加上周期时间
                setText(R.id.tv_hold_value_4, expireTime)
                setText(R.id.tv_hold_4,sl_str_deadline)
                //隐藏触发时间
                setGone(R.id.rl_deal_price,false)
            }else{
                setVisible(R.id.ll_hold_layout2,false)
                //执行价格
                setText(R.id.tv_hold_3, executionPriceTitle)
                if (category and 127 == 2){
                    setText(R.id.tv_hold_value_3, sl_str_market_price_simple)
                }else{
                    setText(R.id.tv_hold_value_3,item.exec_px)
                }
                //执行数量
                setText(R.id.tv_hold_4, executionVolumeTitle)
                if(item.type == 1 || item.type == 2){
                    setText(R.id.tv_hold_value_4,"100%")
                }else{
                    setText(R.id.tv_hold_value_4,dfVol.format(MathHelper.round(item.qty)))
                }
                //到期时间
                setText(R.id.tv_hold_value_5, expireTime)
                setText(R.id.tv_hold_5, sl_str_deadline)
                //触发时间
                setGone(R.id.rl_deal_price,true)
                getView<TextView>(R.id.tv_hold_6).text = sl_str_trigger_time
                setText(R.id.tv_hold_value_6, TimeFormatUtils.timeStampToDate(TimeFormatUtils.getUtcTimeToMillis(item.finished_at),"yyyy-MM-dd  HH:mm:ss"))
            }

            setText(R.id.tv_time, TimeFormatUtils.timeStampToDate(TimeFormatUtils.getUtcTimeToMillis(item.created_at),"yyyy-MM-dd  HH:mm:ss"))
            //撤单
            if(isCurrentEntrust){
                getView<SuperTextView>(R.id.tv_cancel).text = sl_str_cancel_order
                setGone(R.id.tv_cancel,true)
                setGone(R.id.tv_status,false)
                getView<SuperTextView>(R.id.tv_cancel).setOnClickListener { v ->
                    NewDialogUtils.showDialog(mContext!!, mContext.getLineText("sl_str_cancel_order_tips"), false, object : DialogBottomListener {
                        override fun sendConfirm() {
                            doCancel(v, item, "")
                        }
                    },  mContext.getLineText("common_text_tip"), mContext.getLineText("common_text_btnConfirm"),mContext.getLineText("common_text_btnCancel"))
                }
            }else{
                val tvStatus = getView<TextView>(R.id.tv_status)
                tvStatus.visibility = View.VISIBLE
                setGone(R.id.tv_cancel,false)
                val errno: Int = item.errno
                if (errno == ContractOrder.ORDER_ERRNO_NOERR) {
                    tvStatus.text = sl_str_order_complete
                }else if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
                    tvStatus.text = sl_str_user_canceled
                }else if (errno == ContractOrder.ORDER_ERRNO_TIMEOUT) {
                    tvStatus.text = sl_str_order_timeout
                }else{
                    tvStatus.text = sl_str_trigger_failed
                }

                if (errno >= ContractOrder.ORDER_ERRNO_ASSETS) {
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.sl_contract_prompt,0)
                    tvStatus.setOnClickListener {
                        queryDetail(item)
                    }
                }else{
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    tvStatus.setOnClickListener(null)
                }
            }

        }
    }

    private fun queryDetail(order: ContractOrder) {
        val contract: Contract = ContractPublicDataAgent.getContract(order.instrument_id) ?: return

        val dfDefault = NumberUtil.getDecimal(-1)
        var time = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            var create_at = if (order.finished_at == null) order.created_at else order.finished_at
            create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z"
            val date = sdf.parse(create_at)
            val gmtFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            time = gmtFormat.format(date)
        } catch (ignored: ParseException) {
        }

        var price_type = ""
        when {
            order.trigger_type === 1 -> {
                price_type = mContext.getLineText("sl_str_latest_price_simple")
            }
            order.trigger_type === 2 -> {
                price_type = mContext.getLineText("sl_str_fair_price_simple")
            }
            order.trigger_type === 4 -> {
                price_type = mContext.getLineText("sl_str_index_price_simple")
            }
        }
        var reason: String
        var content: String
        when (order.errno) {
            3 -> {
                reason = mContext.getLineText("sl_str_insufficient")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            4 -> {
                reason = mContext.getLineText("sl_str_trigger_failed_reason4")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            6 -> {
                reason = mContext.getLineText("sl_str_trigger_failed_reason6")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            7 -> {
                reason = mContext.getLineText("sl_str_trigger_failed_reason7")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            8 -> {
                reason = mContext.getLineText("sl_str_trigger_failed_reason8")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            9 -> {
                reason = mContext.getLineText("sl_str_trigger_failed_reason9")
                content = java.lang.String.format(mContext.getLineText("sl_str_trigger_failed_info"),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.px, contract.price_index)) + contract.quote_coin,
                        reason)
            }
            5 -> content = mContext.getLineText("sl_str_trigger_failed_reason5")
            10 -> content = mContext.getLineText("sl_str_trigger_failed_reason10")
            11 -> content = mContext.getLineText("sl_str_trigger_failed_reason11")
            12 -> content = mContext.getLineText("sl_str_trigger_failed_reason12")
            13 -> content = mContext.getLineText("sl_str_trigger_failed_reason13")
            else -> content = mContext.getLineText("sl_str_trigger_failed_reason13")
        }

        NewDialogUtils.showDialog(mContext,content,true,null,mContext.getLineText("sl_str_trigger_failed"))
    }

    private fun doCancel(view: View, order: ContractOrder?, pwd: String) {
        if (order == null) {
            return
        }
        val orders = ContractOrders()
        orders.contract_id = order.instrument_id
        orders.orders?.add(order)

        if(loadDialog == null){
            loadDialog = NLoadingDialog(mContext as Activity?)
        }
        loadDialog?.showLoadingDialog()

        val response: IResponse<MutableList<Long>> = object : IResponse<MutableList<Long>>() {

            override fun onSuccess(data: MutableList<Long>) {
                loadDialog?.closeLoadingDialog()
                if (data != null && data.isNotEmpty()) {
                    ToastUtils.showToast(mContext, mContext.getLineText("sl_str_some_orders_cancel_failed"))
                }
                ToastUtils.showToast(mContext,mContext.getLineText("sl_str_cancel"))
            }

            override fun onFail(code: String, msg: String) {
                loadDialog?.closeLoadingDialog()
                ToastUtils.showToast(mContext, msg)
            }

        }
        ContractUserDataAgent.doCancelPlanOrders(orders, response)
    }

}