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
import com.contract.sdk.data.ContractLiqRecord
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractOrders
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.activity.SlContractEntrustDetailActivity
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.ContractUpDownItemLayout
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.contract.widget.pswkeyboard.widget.PopEnterPassword
import com.yjkj.chainup.net_new.NLoadingDialog
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.dialog.NewDialogUtils.DialogBottomListener
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.ToastUtils
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 合约限价委托
 */
class ContractPriceEntrustAdapter(context:Context,data: ArrayList<ContractOrder>) : BaseQuickAdapter<ContractOrder, BaseViewHolder>(R.layout.sl_item_contract_price_entrust, data) {
    //是否是当前委托
    private var isCurrentEntrust = true
    private var loadDialog: NLoadingDialog? = null

    var sl_str_buy_open0 = ""
    var sl_str_sell_open0 = ""
    var contract_flat_short = ""
    var contract_flat_long = ""
    var sl_str_entrust_volume = ""
    var sl_str_make_volume_unit = ""
    var sl_str_market_price_simple = ""
    var sl_str_force_close_details = ""
    var sl_str_bankruptcy_details = ""
    var sl_str_reduce_position_details = ""
    var sl_str_entrust_price_unit = ""
    var sl_str_entrust_value_unit = ""
    var sl_str_cancel_order = ""
    var sl_str_order_complete = ""
    var sl_str_order_part_filled = ""
    var sl_str_user_canceled = ""
    var sl_str_system_canceled = ""

    init {
        sl_str_buy_open0 = context.getLineText("sl_str_buy_open0")
        sl_str_sell_open0 = context.getLineText("sl_str_sell_open0")
        contract_flat_short = context.getLineText("contract_flat_short")
        contract_flat_long = context.getLineText("contract_flat_long")
        sl_str_entrust_volume = context.getLineText("sl_str_entrust_volume")
        sl_str_make_volume_unit = context.getLineText("sl_str_make_volume_unit")
        sl_str_market_price_simple = context.getLineText("sl_str_market_price_simple")
        sl_str_force_close_details = context.getLineText("sl_str_force_close_details")
        sl_str_bankruptcy_details = context.getLineText("sl_str_bankruptcy_details")
        sl_str_reduce_position_details = context.getLineText("sl_str_reduce_position_details")
        sl_str_entrust_price_unit =  context.getLineText("sl_str_entrust_price_unit")
        sl_str_entrust_value_unit =  context.getLineText("sl_str_entrust_value_unit")
        sl_str_cancel_order = context.getLineText("sl_str_cancel_order")
        sl_str_order_complete = context.getLineText("sl_str_order_complete")
        sl_str_order_part_filled = context.getLineText("sl_str_order_part_filled")
        sl_str_user_canceled = context.getLineText("sl_str_user_canceled")
        sl_str_system_canceled = context.getLineText("sl_str_system_canceled")
    }

    fun setIsCurrentEntrust(isCurrentEntrust: Boolean = true) {
        this.isCurrentEntrust = isCurrentEntrust
    }

    override fun convert(helper: BaseViewHolder?, item: ContractOrder?) {
        if (item == null) {
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
            //合约名称
            var tvContractName = getView<TextView>(R.id.tv_contract_name)
            tvContractName.text = contract.symbol
            //委托数量
            val itemEntrustVolume = getView<ContractUpDownItemLayout>(R.id.item_entrust_volume)
            itemEntrustVolume.title = sl_str_entrust_volume
            itemEntrustVolume.content = dfVol.format(MathHelper.round(item.qty))
            //成交量
            var itemVolumeValue = getView<ContractUpDownItemLayout>(R.id.item_volume_value)
            itemVolumeValue.content = dfVol.format(MathHelper.round(item.cum_qty))
            itemVolumeValue.title = sl_str_make_volume_unit
            val category: Int = item.category
            //是否是强平订单
            val isForceCloseOrder = item.isForceCloseOrder
            //委托价格
            var itemEntrustPrice = getView<ContractUpDownItemLayout>(R.id.item_entrust_price)
            itemEntrustPrice.content = item.px
            if (category and 127 == 2) {
                itemEntrustPrice.content = sl_str_market_price_simple
            }
            val tvDetail = getView<TextView>(R.id.tv_detail)
            //是否显示委托价格
            tvDetail.visibility = View.GONE
            if (category and 128 > 0) { //第7位为1表示:强平委托单
                tvDetail.visibility = View.VISIBLE
                tvDetail.text = sl_str_force_close_details
            } else if (category and 256 > 0) { //第8位为1表示:爆仓委托单
                tvDetail.visibility = View.VISIBLE
                tvDetail.text = sl_str_bankruptcy_details
            } else if (category == 513) {
                tvDetail.visibility = View.VISIBLE
                if (MathHelper.round(item.take_fee) > 0) {
                    tvDetail.text = sl_str_force_close_details
                } else {
                    tvDetail.text = sl_str_reduce_position_details
                }
            }
            //委托价格
            if (!isCurrentEntrust && isForceCloseOrder) {
                itemEntrustPrice.content = "--"
            }
            tvDetail.setOnClickListener {
                queryDetail(item)
            }
            itemEntrustPrice.title = sl_str_entrust_price_unit + " (" + contract.quote_coin + ")"
//            //成交均价
//            setText(R.id.tv_deal_price_value, dfDefault.format(MathHelper.round(item.avg_px, contract.price_index)))
//            setText(R.id.tv_deal_price, mContext.getLineText(R.string.contract_text_dealAverage) +" (" + contract.quote_coin+")")
            //委托价值
            val itemEntrustValue = getView<ContractUpDownItemLayout>(R.id.item_entrust_value)
            val value: Double = ContractCalculate.CalculateContractValue(
                    item.qty,
                    item.px,
                    contract)
            itemEntrustValue.title =  String.format(sl_str_entrust_value_unit, contract.margin_coin)
            if (!isCurrentEntrust && isForceCloseOrder) {
                itemEntrustValue.content = "--"
            } else {
                itemEntrustValue.content =  dfDefault.format(MathHelper.round(value, contract.value_index))
            }
            //时间
            setText(R.id.tv_time, TimeFormatUtils.timeStampToDate(TimeFormatUtils.getUtcTimeToMillis(item.created_at), "yyyy-MM-dd  HH:mm:ss"))
            //撤单
            if (isCurrentEntrust) {
                doDealSystemCanceled(0,tvContractName,false)
                tvContractName.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
                val tvCancel =  getView<TextView>(R.id.tv_cancel)
                tvCancel.text = sl_str_cancel_order
                tvCancel.visibility = View.VISIBLE
                setVisible(R.id.tv_order_type, false)
                tvCancel.setOnClickListener { v ->
                    NewDialogUtils.showDialog(mContext!!, mContext.getLineText("sl_str_cancel_order_tips"), false, object : DialogBottomListener {
                        override fun sendConfirm() {
                            doCancel(v, item, "")
                        }
                    }, mContext.getLineText("common_text_tip"), mContext.getLineText("common_text_btnConfirm"), mContext.getLineText("common_text_btnCancel"))
                }
            } else {
                setVisible(R.id.tv_cancel, false)
                setVisible(R.id.tv_order_type, true)
                //订单状态
                val errno: Int = item.errno
                val doneVol: Double = MathHelper.round(item.cum_qty, 8)
                var orderStatue = if (errno == ContractOrder.ORDER_ERRNO_NOERR) {
                    doDealSystemCanceled(0,tvContractName,false)
                    tvContractName.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
                    sl_str_order_complete
                } else if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
                    doDealSystemCanceled(0,tvContractName,false)
                    if (doneVol > 0) {
                        sl_str_order_part_filled
                    } else {
                        sl_str_user_canceled
                    }
                } else {
                    doDealSystemCanceled(errno,tvContractName,true)
                    sl_str_system_canceled
                }
                setText(R.id.tv_order_type, orderStatue)
                getView<TextView>(R.id.tv_order_type).setOnClickListener { v ->
                    SlContractEntrustDetailActivity.show(mContext as Activity, item)
                }
            }


        }
    }

    /**
     * 处理系统取消原因展示
     */
    private fun doDealSystemCanceled(type:Int,textView: TextView,isShow:Boolean){
        val cancelString = getSystemCanceledFormat(type)
        if(isShow && !TextUtils.isEmpty(cancelString)){
            textView.setCompoundDrawablesWithIntrinsicBounds(null,null,mContext.resources.getDrawable(R.drawable.sl_contract_prompt),null)
            textView.setOnClickListener {
                NewDialogUtils.showSingleDialog(mContext, cancelString, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {

                    }

                }, mContext.getLineText("common_text_tip"), mContext.getLineText("alert_common_iknow"))
            }
        }else{
            textView.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
            textView.setOnClickListener(null)
        }
    }

    private fun getSystemCanceledFormat(type:Int) : String{
        return when(type){
            2 -> mContext.getLineText("sl_str_entrust_failed_reason2")
            3 -> mContext.getLineText("sl_str_entrust_failed_reason3")
            4 -> mContext.getLineText("sl_str_entrust_failed_reason4")
            5 -> mContext.getLineText("sl_str_entrust_failed_reason5")
            6 -> mContext.getLineText("sl_str_entrust_failed_reason6")
            7 -> mContext.getLineText("sl_str_entrust_failed_reason7")
            8 -> mContext.getLineText("sl_str_entrust_failed_reason8")
            9 -> mContext.getLineText("sl_str_entrust_failed_reason9")
            10 -> mContext.getLineText("sl_str_entrust_failed_reason10")
            11 -> mContext.getLineText("sl_str_entrust_failed_reason11")
            12 -> mContext.getLineText("sl_str_entrust_failed_reason12")
            13 -> mContext.getLineText("sl_str_entrust_failed_reason13")
            14 -> mContext.getLineText("sl_str_entrust_failed_reason14")
            15 -> mContext.getLineText("sl_str_entrust_failed_reason15")
            16 -> mContext.getLineText("sl_str_entrust_failed_reason16")
            17 -> mContext.getLineText("sl_str_entrust_failed_reason17")
            18 -> mContext.getLineText("sl_str_entrust_failed_reason18")
            else -> ""
        }
    }

    private fun queryDetail(order: ContractOrder) {
        ContractUserDataAgent.loadLiqRecord(order.oid,order.instrument_id,object: IResponse<List<ContractLiqRecord>>(){
            override fun onSuccess(data: List<ContractLiqRecord>) {
                if (data != null && data.isNotEmpty()) {
                    var liqRecord: ContractLiqRecord? = data[0]
                    for (info in data) {
                        if (info.oid === order.oid) {
                            liqRecord = info
                            break
                        }
                    }

                    var createdAt = ""
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    sdf.timeZone = TimeZone.getTimeZone("GMT")
                    try {
                        var createAt = liqRecord!!.created_at
                        createAt = createAt.substring(0, createAt.lastIndexOf(".")) + "Z"
                        val date = sdf.parse(createAt)
                        val gmtFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        createdAt = gmtFormat.format(date)
                    } catch (ignored: ParseException) {
                    }

                    val contract = ContractPublicDataAgent.getContract(liqRecord!!.instrument_id) ?: return

                    val contractName = contract.symbol
                    val dfDefault = NumberUtil.getDecimal(-1)
                    val dfPrice = NumberUtil.getDecimal(contract.price_index)

                    var positionName = ""
                    var priceChange = ""
                    if (order.side === ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
                        priceChange = mContext.getLineText("sl_str_rose")
                        positionName = contractName + "-" + mContext.getLineText("sl_str_buy_close")
                    } else if (order.side === ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
                        priceChange = mContext.getLineText("sl_str_fall")
                        positionName = contractName + "-" + mContext.getLineText("sl_str_sell_close")
                    }

                    val type = liqRecord.type
                    val category: Int = order.category
                    if (type == 1) {    //部分强平
                        //部分强平
                        val intro1 = String.format(mContext.getLineText("sl_str_wiped_out_tips0"),
                                createdAt,
                                contractName,
                                priceChange + dfDefault.format(MathHelper.round(liqRecord.trigger_px, contract.price_index)) + contract.quote_coin,
                                positionName, MathHelper.round(MathHelper.mul(liqRecord.mmr, "100"), 2).toString() + "%")
                        val intro2 = java.lang.String.format(mContext.getLineText("sl_str_wiped_out_tips2"),
                                liqRecord.order_px + contract.quote_coin)
                        SlDialogHelper.showWipedOutIntroduceDialog(mContext, mContext.getLineText("sl_str_force_close_details"), intro1, intro2, mContext.getLineText("sl_str_isee"), object : DialogBottomListener {
                            override fun sendConfirm() {
                                //  UtilSystem.openHtmlActivity(BTConstants.BTURL_CONTRACT_FORCE_CLOSE, mContext?.getLineText(R.string.sl_str_force_close_details))
                            }
                        })
                    } else if (type == 2 || type == 3) { //爆仓
                        //爆仓
                        val intro1 = String.format(mContext.getLineText("sl_str_wiped_out_tips1"),
                                createdAt,
                                contractName,
                                priceChange + dfDefault.format(MathHelper.round(liqRecord.trigger_px, contract.price_index)) + contract.quote_coin,
                                positionName, MathHelper.round(MathHelper.mul(liqRecord.mmr, "100"), 2).toString() + "%")
                        val intro2 = java.lang.String.format(mContext.getLineText("sl_str_wiped_out_tips2"),
                                liqRecord.order_px + contract.quote_coin)
                        SlDialogHelper.showWipedOutIntroduceDialog(mContext, mContext.getLineText("sl_str_blowing_up_datail"), intro1, intro2, mContext.getLineText("sl_str_isee"), object : DialogBottomListener {
                            override fun sendConfirm() {
                                //  UtilSystem.openHtmlActivity(BTConstants.BTURL_CONTRACT_FORCE_CLOSE, mContext?.getLineText(R.string.sl_str_force_close_mechanism))
                            }
                        })
                    } else if (type == 4) {//减仓明细
                        if (category == 513) {
                            if (MathHelper.round(order.take_fee) > 0) {
                                val intro1 = String.format(mContext.getLineText("sl_str_wiped_out_tips0"),
                                        createdAt,
                                        contractName,
                                        priceChange + dfDefault.format(MathHelper.round(liqRecord.trigger_px, contract.price_index)) + contract.quote_coin,
                                        positionName, MathHelper.round(MathHelper.mul(liqRecord.mmr, "100"), 2).toString() + "%")
                                val intro2 = java.lang.String.format(mContext.getLineText("sl_str_wiped_out_tips2"),
                                        liqRecord.order_px + contract.quote_coin)
                                SlDialogHelper.showWipedOutIntroduceDialog(mContext, mContext.getLineText("sl_str_force_close_details"), intro1, intro2, mContext.getLineText("sl_str_isee"), object : DialogBottomListener {
                                    override fun sendConfirm() {
                                        //  UtilSystem.openHtmlActivity(BTConstants.BTURL_CONTRACT_FORCE_CLOSE, mContext?.getLineText(R.string.sl_str_force_close_details))
                                    }
                                })
                            } else {
                                val intro1 = java.lang.String.format(mContext.getLineText("sl_str_reduce_position_tips"),
                                        createdAt,
                                        dfDefault.format(MathHelper.round(liqRecord.trigger_px, contract.price_index)) + contract.quote_coin,
                                        dfDefault.format(MathHelper.round(liqRecord.order_px, contract.price_index)) + contract.quote_coin)

                                SlDialogHelper.showWipedOutIntroduceDialog(mContext, mContext.getLineText("sl_str_reduce_position_details"), intro1, "", mContext.getLineText("sl_str_isee"), object : DialogBottomListener {
                                    override fun sendConfirm() {
                                        //  UtilSystem.openHtmlActivity(BTConstants.BTURL_CONTRACT_AUTO_REDUCE, mContext?.getLineText(R.string.sl_str_automatically_reduce))
                                    }
                                })
                            }
                        } else {
                            val intro1 = java.lang.String.format(mContext.getLineText("sl_str_reduce_position_tips"),
                                    createdAt,
                                    dfDefault.format(MathHelper.round(liqRecord.trigger_px, contract.price_index)) + contract.quote_coin,
                                    dfDefault.format(MathHelper.round(liqRecord.order_px, contract.price_index)) + contract.quote_coin)

                            SlDialogHelper.showWipedOutIntroduceDialog(mContext, mContext.getLineText("sl_str_reduce_position_details"), intro1, "", mContext.getLineText("sl_str_isee"), object : DialogBottomListener {
                                override fun sendConfirm() {
                                    //  UtilSystem.openHtmlActivity(BTConstants.BTURL_CONTRACT_AUTO_REDUCE, mContext?.getLineText(R.string.sl_str_automatically_reduce))
                                }
                            })
                        }
                    }

                }
            }

            override fun onFail(code: String, msg: String) {
                NToastUtil.showToast(msg, false)
            }

        })

    }

    private fun doCancel(view: View, order: ContractOrder?, pwd: String) {
        if (order == null) {
            return
        }
        if (loadDialog == null) {
            loadDialog = NLoadingDialog(mContext as Activity?)
        }
        loadDialog?.showLoadingDialog()
        val orders = ContractOrders()
        orders.contract_id = order.instrument_id
        orders.orders.add(order)
        val response: IResponse<MutableList<Long>> = object : IResponse<MutableList<Long>>() {

            override fun onSuccess(data: MutableList<Long>) {
                loadDialog?.closeLoadingDialog()
                if (data != null && data.isNotEmpty()) {
                    ToastUtils.showToast(mContext, mContext.getLineText("sl_str_some_orders_cancel_failed"))
                    return
                }
                ToastUtils.showToast(mContext, mContext.getLineText("sl_str_cancel"))
            }

            override fun onFail(code: String, msg: String) {
                loadDialog?.closeLoadingDialog()
                ToastUtils.showToast(mContext, msg)
            }

        }
        ContractUserDataAgent.doCancelOrders(orders, response)
    }


}