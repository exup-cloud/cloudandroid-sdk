package com.yjkj.chainup.contract.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.listener.SLDoListener
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.*
import kotlinx.android.synthetic.main.sl_view_stop_rate_loss_layout.view.*

/**
 * 仓位止盈止损item
 */
class SlPositionStopRateLossItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle), SLDoListener {

    /**
     * 是否是止盈item
     * true 止盈
     * false 止损
     */
    var isRateItem = true
    var itemTriggerType = 1
    var contractPosition: ContractPosition? = null
    var originContract: Contract? = null
    var originContractTicker: ContractTicker? = null

    /**
     * 标题
     */
    var title = ""
        set(value) {
            field = value
            tv_tab?.text = title
        }

    /**
     * 是否开启
     */
    var checkTab = false
        set(value) {
            field = value
            et_execution_price.edit(value)
            et_trigger_price.edit(value)
            cb_tab.isChecked = value
            if (!value) {
                isMarketPrice = true
                tv_price_remind.visibility = View.GONE
                tv_price_warn.visibility = View.GONE
            }
        }
        get() {
            return cb_tab.isChecked
        }

    /**
     * 价格精度
     */
    var pxUnit = "0.0001"

    /**
     * 价格单位
     */
    var priceUnit = "USDT"
        set(value) {
            field = value
            tv_trigger_symbol.text = value
            tv_execution_symbol.text = value
        }

    /**
     * 触发价格
     */
    var triggerPrice = ""
        set(value) {
            field = value
            value.let {
                et_trigger_price.setText(value)
                et_trigger_price.setSelection(value.length)
            }
        }
        get() {
            return et_trigger_price.text.toString()
        }

    /**
     * 执行价格
     */
    var executionPrice = ""
        set(value) {
            field = value
            value.let {
                et_execution_price.setText(value)
                et_execution_price.setSelection(value.length)
            }
        }
        get() {
            return et_execution_price.text.toString()
        }

    /**
     * 执行价格是否是 市价
     */
    var isMarketPrice = true
        set(value) {
            field = value
            tv_execution_symbol.visibility = if (value) View.GONE else View.VISIBLE
            view_execution_line.visibility = if (value) View.GONE else View.VISIBLE
            et_execution_price.visibility = if (value) View.GONE else View.VISIBLE
            tv_market_price_hint.visibility = if (!value) View.GONE else View.VISIBLE
            showRemind()
            showWarn(value)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.sl_view_stop_rate_loss_layout, this)
        checkTab = false
        isMarketPrice = true
        initAutoTextView()
        initListener()
    }

    private fun initAutoTextView() {
        tv_trigger_price_label.onLineText("sl_str_trigger_price")
        et_trigger_price.hint = context.getLineText("sl_str_trigger_price")
        tv_execution_price_label.onLineText("sl_str_execution_price")
        et_execution_price.hint = context.getLineText("sl_str_execution_price")
        tv_market_price.onLineText("contract_action_marketPrice")
        tv_market_price_hint.onLineText("sl_str_market_price")
    }

    private fun initListener() {
        et_trigger_price.afterTextChanged(this)
        et_execution_price.afterTextChanged(this)
        //选中tab
        ll_tab_layout.setOnClickListener {
            checkTab = !cb_tab.isChecked
        }
        cb_tab.setOnClickListener {
            checkTab = !cb_tab.isChecked
        }
        //市价
        tv_market_price.setOnClickListener {
            if (checkTab) {
                isMarketPrice = !isMarketPrice
            }
        }
    }

    /**
     * 监听价格变化
     */
    override fun doThing(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj == et_trigger_price) {
            updateTriggerPrice(et_trigger_price)
            showRemind()
        }
        if (obj == et_execution_price) {
            updateTriggerPrice(et_execution_price)
        }
        showWarn(isMarketPrice)
        return false
    }

    /**
     * 处理价格格式
     */
    private fun updateTriggerPrice(inputView: EditText) {
        var price: String = inputView.text.toString()
        price = price.replace(",", ".")
        if (pxUnit.contains(".")) {
            var priceIndex = pxUnit.length - pxUnit.indexOf(".") - 1
            if (priceIndex == 1) {
                priceIndex = 0
            }
            if (price.contains(".")) {
                val index = price.indexOf(".")
                if (index + priceIndex < price.length) {
                    price = price.substring(0, index + priceIndex)
                    inputView.setText(price)
                    inputView.setSelection(price.length)
                }
            }
        } else {
            if (price.contains(".")) {
                price = price.replace(".", "")
                inputView.setText(price)
                inputView.setSelection(price.length)
            }
        }
    }

    /**
     * 显示提示 输入触发价格后则在输入框显示提示：最新价达到xxx时以市场价格进行委托
     */
    private fun showRemind() {
        if (TextUtils.isEmpty(et_trigger_price.text.toString())) {
            tv_price_remind.visibility = View.GONE
        } else {
            tv_price_remind.text = String.format(context.getLineText("contract_sting_newPriceMessage"), et_trigger_price.text.toString())
            tv_price_remind.visibility = View.VISIBLE
            tv_price_warn.visibility = View.GONE
        }
    }

    /**
     * a\止损触发价格和执行价格：需大于强平价格，小于最新价格
     * b\止盈价触发价格和执行价格：需大于最新价格
     * c\止损触发价格和执行价格：需要小于强平价格大于最新价格
     * d\止盈触发价格和执行价格：需小于最新价格
     */
    private fun showWarn(isMarker: Boolean = isMarketPrice) {
        if (originContractTicker == null) {
            return
        }
        val tPrice: Double = if (TextUtils.isEmpty(triggerPrice)) {
            0.0
        } else {
            triggerPrice.toDouble()
        }
        val ePrice: Double = when {
            isMarker -> {
                tPrice
            }
            TextUtils.isEmpty(executionPrice) -> {
                0.0
            }
            else -> {
                executionPrice.toDouble()
            }
        }
        val originContractOrder = buildOrder()
        //强平价格
        val contractAccount = if (originContractOrder.position_type == 2) {
            ContractUserDataAgent.getContractAccount(originContract?.margin_coin!!)
        } else {
            null
        }
        val calculateOrderLiquidatePrice = ContractCalculate.CalculatePositionLiquidatePrice(contractPosition!!, contractAccount, originContract!!)
        //最新价格
        val lastPx = originContractTicker!!.last_px.toDouble()
        if (isRateItem) {
            when (contractPosition?.side) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                    //多仓止盈价触发价格和执行价格-需大于最新价格，若不符合则提示“止盈价触发价格和执行价格：需大于最新价格”；
                    if (tPrice < lastPx || ePrice < lastPx) {
                        tv_price_warn.apply {
                            onLineText("contract_sting_moreProfitMessage")
                            visibility = View.VISIBLE
                        }
                        tv_price_remind.visibility = View.GONE
                    } else {
                        tv_price_warn.visibility = View.GONE
                    }
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    //空仓止盈触发价格和执行价格-需小于最新价格，若不符合则提示“止盈触发价格和执行价格：需小于最新价格”；
                    if (tPrice > lastPx || ePrice > lastPx) {
                        tv_price_warn.apply {
                            onLineText("contract_sting_lessProfitMessage")
                            visibility = View.VISIBLE
                        }
                        tv_price_remind.visibility = View.GONE
                    } else {
                        tv_price_warn.visibility = View.GONE
                    }
                }
            }
        } else {
            when (contractPosition?.side) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                    //多仓止损触发价格和执行价格-需大于强平价格，小于最新价格，若不符合则提示“止损触发价格和执行价格：需大于强平价格，小于最新价格”；
                    if (tPrice > lastPx || tPrice < calculateOrderLiquidatePrice ||
                            ePrice > lastPx || ePrice < calculateOrderLiquidatePrice) {
                        tv_price_warn.apply {
                            onLineText("contract_sting_moreLossMessage")
                            visibility = View.VISIBLE
                        }
                        tv_price_remind.visibility = View.GONE
                    } else {
                        tv_price_warn.visibility = View.GONE
                    }
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    // 空仓止损触发价格和执行价格-需要小于强平价格大于最新价格，若不符合则提示“止损触发价格和执行价格：需要小于强平价格大于最新价格”；
                    if (tPrice < lastPx || tPrice > calculateOrderLiquidatePrice ||
                            ePrice < lastPx || ePrice > calculateOrderLiquidatePrice) {
                        tv_price_warn.apply {
                            onLineText("contract_sting_lessLossMessage")
                            visibility = View.VISIBLE
                        }
                        tv_price_remind.visibility = View.GONE
                    } else {
                        tv_price_warn.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * 构建Order
     */
    private fun buildOrder(): ContractOrder {
        val order = ContractOrder()
        order.instrument_id = contractPosition?.instrument_id!!
        order.side = if (contractPosition?.side == 1) {
            ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        } else {
            ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }
        order.nonce = System.currentTimeMillis() / 1000
        order.px = triggerPrice
        order.qty = contractPosition?.cur_qty.toString()
        order.position_type = contractPosition?.position_type!!
        if (!isMarketPrice) {
            order.exec_px = executionPrice
        }
        order.type = if (isRateItem) 1 else 2
        order.pid = contractPosition?.pid!!
        order.category = if (isMarketPrice) ContractOrder.ORDER_CATEGORY_MARKET else ContractOrder.ORDER_CATEGORY_NORMAL
        order.trigger_type = itemTriggerType
        order.life_cycle = if (LogicContractSetting.getStrategyEffectTime(context) == 0) 24 else 168
        return order
    }

    /**
     * 输入的价格没问题
     */
    val isPriceSure: Boolean
        get() = (tv_price_warn.visibility == View.GONE && checkTab)


    /**
     * 警告提示抖动
     */
    fun warnShake() {
        tv_price_warn.startResAnimation(R.anim.shake)
    }

}