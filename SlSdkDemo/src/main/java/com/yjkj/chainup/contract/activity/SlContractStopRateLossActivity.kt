package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.*
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.SlPositionStopRateLossItem
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.SoftKeyboardUtil
import kotlinx.android.synthetic.main.sl_activity_stop_rate_loss.*

/**
 * 止盈止损
 */
class SlContractStopRateLossActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_stop_rate_loss
    }

    private var contract: Contract? = null
    private var contractTicker: ContractTicker? = null
    private var contractAccount: ContractAccount? = null

    private var mPosition = ContractPosition()

    private var dfValue = NumberUtil.getDecimal(-1)
    /**
     * 1市场价 2合理价 4指数价
     */
    private var triggerType = 1
    /**
     * 止盈订单
     */
    private var originRateOrder: ContractOrder? = null
    private var originLossOrder: ContractOrder? = null

    private var requestCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
        initListener()
        loadDataFromNet()
    }


    override fun loadData() {
        super.loadData()
        mPosition = intent.getParcelableExtra("position")

        contract = ContractPublicDataAgent.getContract(mPosition.instrument_id)
        contractTicker = ContractPublicDataAgent.getContractTicker(mPosition.instrument_id)
        if (contract == null || contractTicker == null) {
            finish()
            return
        }
        contractAccount = ContractUserDataAgent.getContractAccount(contract!!.margin_coin
                ?: "")
        dfValue = NumberUtil.getDecimal(contract!!.value_index)
    }


    override fun initView() {
        super.initView()
        initAutoTextView()
        tab_trigger_latest.setCenterSize(14f)
        tab_trigger_fair.setCenterSize(14f)
        tab_trigger_index.setCenterSize(14f)
        tab_trigger_latest.forbidTouchDeal = true
        tab_trigger_fair.forbidTouchDeal = true
        tab_trigger_index.forbidTouchDeal = true

        initRateLossUi()

    }

    private fun initAutoTextView() {
        title_layout.title = getLineText("sl_str_stop_profit_loss")
        tv_trigger_type.onLineText("sl_str_trigger_type")
        tab_trigger_latest.setMiddle(getLineText("sl_str_latest_price_simple"))
        tab_trigger_fair.setMiddle(getLineText("sl_str_fair_price_simple"))
        tab_trigger_index.setMiddle(getLineText("sl_str_index_price_simple"))
        tv_confirm_btn.textContent = getLineText("common_text_btnConfirm")
    }

    /**
     * 初始化盈亏UI
     */
    private fun initRateLossUi() {
        contract?.let {
            item_stop_rate.apply {
                isRateItem = true
                itemTriggerType = triggerType
                contractPosition = mPosition
                title = getLineText("sl_str_stop_rate")
                originContract = it
                originContractTicker = contractTicker
                priceUnit = it.quote_coin
                pxUnit = it.px_unit
            }
            item_stop_loss.apply {
                isRateItem = false
                itemTriggerType = triggerType
                contractPosition = mPosition
                title = getLineText("sl_str_stop_loss")
                originContract = it
                originContractTicker = contractTicker
                priceUnit = it.quote_coin
                pxUnit = it.px_unit
            }
        }
    }

    /**
     * 更新盈亏UI
     */
    private fun updateRateLossUI() {
        originRateOrder?.let {
            item_stop_rate.checkTab = true
            //是否是市价
            item_stop_rate.isMarketPrice = it.category and 127 == 2
            //触发价格
            item_stop_rate.triggerPrice = it.px
            //执行价格
            item_stop_rate.executionPrice = it.exec_px
            triggerType = it.trigger_type
        }
        originLossOrder?.let {
            item_stop_loss.checkTab = true
            //是否是市价
            item_stop_loss.isMarketPrice = it.category and 127 == 2
            //触发价格
            item_stop_loss.triggerPrice = it.px
            //执行价格
            item_stop_loss.executionPrice = it.exec_px
            triggerType = it.trigger_type
        }
    }

    private fun initListener() {
        tv_confirm_btn.isEnable(true)
        tv_confirm_btn.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                val stopRateSelect = item_stop_rate.checkTab
                val stopLossSelect = item_stop_loss.checkTab

                var rateOrder:ContractOrder? = null
                if(stopRateSelect){
                    rateOrder = doSubmitPreVerify(true, item_stop_rate)
                }
                var lossOrder:ContractOrder? = null
                if(stopLossSelect){
                     lossOrder = doSubmitPreVerify(false, item_stop_loss)
                }
                if(stopRateSelect && rateOrder != null){
                    //如果选中止盈，并且校验参数合法,则提交止盈订单
                    requestCount++
                    LogUtil.d("DEBUG","提交止盈订单")
                    submitOrder(rateOrder)
                }else if(!stopRateSelect && originRateOrder!=null){
                    //如果没选中止盈，并且有原始订单，则取消订单
                    requestCount++
                    LogUtil.d("DEBUG","取消止盈订单")
                    doCancelOrder(originRateOrder!!)
                }


                if(stopLossSelect && lossOrder != null){
                    //如果选中止损，并且校验参数合法,则提交止损订单
                    requestCount++
                    LogUtil.d("DEBUG","提交止损订单")
                    doSubmit(lossOrder)
                }else if(!stopLossSelect && originLossOrder!=null){
                    //如果没选中止损，并且有原始订单，则取消订单
                    requestCount++
                    LogUtil.d("DEBUG","取消止损订单")
                    doCancelOrder(originLossOrder!!)
                }
            }
        }

    }

    /**
     * 提交预校验
     */
    private fun doSubmitPreVerify(isStopRateOrder: Boolean, itemLayout: SlPositionStopRateLossItem): ContractOrder? {
        val triggerTypePrice = getTriggerPrice()
        val triggerPrice = itemLayout.triggerPrice
        val executionPrice = itemLayout.executionPrice
        LogUtil.d("DEBUG", "doSubmitPreVerify isStopRateOrder:$isStopRateOrder  side:${mPosition.side} isStopRateOrder:$isStopRateOrder triggerPrice:$triggerPrice triggerTypePrice:$triggerTypePrice")
        val preTipsString = if(isStopRateOrder) getLineText("sl_str_stop_rate") else getLineText("sl_str_stop_loss")
        if (MathHelper.round(triggerPrice) <= 0) {
            NToastUtil.showTopToast(false,preTipsString+getLineText("sl_str_price_too_low"))
            return null
        }
        //非市价才校验执行价格
        if(!itemLayout.isMarketPrice &&  MathHelper.round(executionPrice) <= 0){
            NToastUtil.showTopToast(false,preTipsString+getLineText("sl_str_price_too_low") )
            return null
        }
        if(!itemLayout.isPriceSure){
            itemLayout.warnShake()
            return null
        }
        val order = ContractOrder()
        //止盈
        if (isStopRateOrder) {
            when (mPosition.side) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG  -> {
                    order.trend = 1
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    order.trend = 2
                }
            }
        } else {
            when (mPosition.side) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG  -> {
                    order.trend = 2
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    order.trend = 1
                }
            }
        }
        order.instrument_id = mPosition.instrument_id
        order.qty = mPosition.cur_qty.toString()
        order.side = if (mPosition.side == 1){
            ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        } else{
            ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }
        order.position_type = mPosition.position_type
        order.nonce = System.currentTimeMillis() / 1000
        order.px = triggerPrice
        if(!itemLayout.isMarketPrice){
            order.exec_px = executionPrice
        }
        order.type = if (isStopRateOrder) 1 else 2
        order.pid = mPosition.pid
        order.category = if (itemLayout.isMarketPrice) ContractOrder.ORDER_CATEGORY_MARKET else ContractOrder.ORDER_CATEGORY_NORMAL
        order.trigger_type = triggerType
        order.life_cycle = if (LogicContractSetting.getStrategyEffectTime(mActivity) == 0) 24 else 168
        return order
    }

    /**
     * 得到触发类型的价格
     * 0市场价 1合理价 2指数价
     */
    private fun getTriggerPrice(): Double {
        when (triggerType) {
            1 -> {
                return contractTicker!!.last_px.toDouble()
            }
            2 -> {
                return contractTicker!!.fair_px.toDouble()
            }
            4 -> {
                return contractTicker!!.index_px.toDouble()
            }
        }
        return contractTicker!!.last_px.toDouble()
    }

    /**
     * 得到触发类型文本
     */
    private fun getTriggerTypeText(): String {
        when (triggerType) {
            1 -> {
                return getLineText("sl_str_latest_price_simple")
            }
            2 -> {
                return getLineText("sl_str_fair_price_simple")
            }
            4 -> {
                return getLineText("sl_str_index_price_simple")
            }
        }
        return getLineText("sl_str_latest_price_simple")
    }

    /**
     * 提交订单
     */
    private fun doSubmit(info: ContractOrder) {
        val contractAccount = if (info.position_type == 2) {
            ContractUserDataAgent.getContractAccount(contract?.margin_coin!!)
        } else {
            null
        }
        val calculateOrderLiquidatePrice = ContractCalculate.CalculatePositionLiquidatePrice(mPosition, contractAccount, contract!!)
        val warnPx = ContractCalculate.calculateStopLostWarnPx(contract!!, info.px!!.toDouble(), calculateOrderLiquidatePrice)
        when (mPosition.side) {
            //多仓限价止损时限制若触发价格或者执行价格低于仓位预警价格提示: 止损价格触发价格或执行价格低于预警价格 6400.8，可能会导致止损失败，是否继续提交？
            ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                val execuPrice = if (item_stop_loss.isMarketPrice) {
                    item_stop_loss.triggerPrice
                }else{
                    item_stop_loss.executionPrice
                }
                if (item_stop_loss.triggerPrice.toDouble() < warnPx || execuPrice.toDouble() < warnPx) {
                    val decimal = NumberUtil.getDecimal(2).format(warnPx)
                    val warnStr = String.format(getLineText("contract_sting_moreProfitWaring"), decimal)
                    NewDialogUtils.showDialog(this, warnStr, false, object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            submitOrder(info)
                        }
                    }, cancelTitle = getLineText("contract_action_continueSubmit"),confrimTitle = getLineText("cancel"))
                } else {
                    submitOrder(info)
                }
            }
            //空仓限价止损时限制若触发价格或者执行价格高于于仓位预警价格提示：止损价格触发价格或执行价格高于预警价格 6400.8，可能会导致止损失败，是否继续提交？
            ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                val execuPrice = if (item_stop_loss.isMarketPrice) {
                    item_stop_loss.triggerPrice
                }else{
                    item_stop_loss.executionPrice
                }
                if (item_stop_loss.triggerPrice.toDouble() > warnPx || execuPrice.toDouble() > warnPx) {
                    val decimal = NumberUtil.getDecimal(2).format(warnPx)
                    val warnStr = String.format(getLineText("contract_sting_lessLossWaring"), decimal)
                    NewDialogUtils.showDialog(this, warnStr, false, object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            submitOrder(info)
                        }
                    }, cancelTitle = getLineText("contract_action_continueSubmit"),confrimTitle = getLineText("cancel"))
                } else {
                    submitOrder(info)
                }
            }
        }
    }

    /**
     * 直接提交订单
     */
    private fun submitOrder(info: ContractOrder){
        ContractUserDataAgent.doSubmitPlanOrder(info,object:IResponse<String>(){
            override fun onSuccess(data: String) {
                requestCount--
                if (requestCount == 0) {
                    tv_confirm_btn.hideLoading()
                }
                if (requestCount == 0) {
                    NToastUtil.showToast(getLineText("contract_tip_submitSuccess"), false)
                    SoftKeyboardUtil.hideSoftKeyboard(mActivity.currentFocus)
                    finish()
                }
            }

            override fun onFail(code: String, msg: String) {
                requestCount--
                if (requestCount == 0) {
                    tv_confirm_btn.hideLoading()
                }
                NToastUtil.showToast(msg, false)
            }
        })
    }

    /**
     * 取消计划订单
     */
    private fun doCancelOrder(info: ContractOrder){
        val orders = ContractOrders()
        orders.contract_id = info.instrument_id
        orders.orders?.add(info)
        ContractUserDataAgent.doCancelPlanOrders(orders,object :IResponse<MutableList<Long>>(){
            override fun onSuccess(data: MutableList<Long>) {
                requestCount--
                if (requestCount == 0) {
                    tv_confirm_btn.hideLoading()
                }
                if (requestCount == 0) {
                    NToastUtil.showToast(getLineText("sl_str_cancel"), false)
                    SoftKeyboardUtil.hideSoftKeyboard(mActivity.currentFocus)
                    finish()
                }
            }

            override fun onFail(code: String, msg: String) {
                requestCount--
                if (requestCount == 0) {
                    tv_confirm_btn.hideLoading()
                }
                NToastUtil.showToast(msg, false)
            }
        })
    }


    private fun loadDataFromNet() {
        contract?.let {
            showLoadingDialog()
            ContractUserDataAgent.loadContractPlanOrder(it.instrument_id, ContractOrder.ORDER_STATE_APPROVAL or ContractOrder.ORDER_STATE_ENTRUST, 0, 0,
            object :IResponse<MutableList<ContractOrder>>(){
                override fun onSuccess(data: MutableList<ContractOrder>) {
                    closeLoadingDialog()
                    if (data.isNotEmpty()) {
                        doFindStopRateLostOrder(data)
                    }
                }

                override fun onFail(code: String, msg: String) {
                    closeLoadingDialog()
                }

            })
        }

    }

    /**
     * 从计划委托中，找止损止盈订单
     * type:1 止盈计划委托
     * type:2 止损计划委托
     */
    private fun doFindStopRateLostOrder(data: List<ContractOrder>) {
        val triggerTypePrice = getTriggerPrice()
        for (i in data.indices) {
            val order = data[i]
            val type = order.type
            if (type == 1) {//止盈
                when(mPosition.side){
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                        if (order.px.toDouble() >= triggerTypePrice) {
                            originRateOrder = order
                        }
                    }
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                        if (order.px.toDouble() <= triggerTypePrice) {
                            originRateOrder = order
                        }
                    }
                }
            } else if (type == 2) {
                when(mPosition.side){
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                        if (order.px.toDouble() <= triggerTypePrice) {
                            originLossOrder = order
                        }
                    }
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                        if (order.px.toDouble() >= triggerTypePrice) {
                            originLossOrder = order
                        }
                    }
                }
            }
        }
        updateRateLossUI()
    }


    companion object {
        fun show(activity: Activity, position: ContractPosition) {
            val intent = Intent(activity, SlContractStopRateLossActivity::class.java)
            intent.putExtra("position", position)
            activity.startActivity(intent)
        }
    }
}