package com.yjkj.chainup.contract.widget

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper.Companion.CONDITIONCOMMISSIONORDER_TYPE_ALL
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper.Companion.CONDITIONCOMMISSIONORDER_TYPE_LOSS
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper.Companion.CONDITIONCOMMISSIONORDER_TYPE_PROFIT
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper.Companion.CONTRACT_ORDER_MARKET
import com.yjkj.chainup.contract.utils.PreferenceManager
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.NToastUtil
import kotlin.math.max

class ContractTradeConfirmWindow(private val context: Context, private val buyOrSellHelper: SLContractBuyOrSellHelper) : PopupWindow() {

    private lateinit var btnOk: TextView
    private lateinit var btnOkTwo: CommonlyUsedButton
    private lateinit var btnCancel: TextView
    private lateinit var btnCancelTwo: TextView
    private lateinit var title: TextView
    private lateinit var tvContract: TextView
    private lateinit var tvContractTwo: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvPriceValue: TextView
    private lateinit var tvAmountValue: TextView
    private lateinit var tvLeverageValue: TextView
    private lateinit var rlLeverage: RelativeLayout
    private lateinit var rlEntrustValue: RelativeLayout
    private lateinit var rlCost: RelativeLayout
    private lateinit var rlAval: RelativeLayout
    private lateinit var rlPosition: RelativeLayout
    private lateinit var tvEntrustValue: TextView
    private lateinit var tvCost: TextView
    private lateinit var tvAvailableBalance: TextView
    private lateinit var tvPosition: TextView
    private lateinit var tvTagPrice: TextView
    private lateinit var tvForceClosePrice: TextView
    private lateinit var tvTagRatioForce: TextView
    private lateinit var tvWarning: TextView
    private lateinit var cbNoRemind: CheckBox
    private lateinit var item_stop_rate: SlOrderStopRateLossItem
    private lateinit var item_stop_loss: SlOrderStopRateLossItem
    private lateinit var rootLayout: RelativeLayout
    private lateinit var cardView: CardView
    private lateinit var btnLayout: LinearLayout

    private var contractOrder: ContractOrder? = null
    private var contractTicker: ContractTicker? = null
    private var buildStopRateLoss = false

    init {
        this.contentView = View.inflate(context, R.layout.sl_item_tread_confirm_dialog, null)
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.update()
        setBackgroundDrawable(BitmapDrawable())
        initAutoTextView()
        initView()
        initData()
    }

    /**
     * 初始化文案
     */
    private fun initAutoTextView() {
        contentView.findViewById<TextView>(R.id.tv_leverage).onLineText("contract_text_lever")
        contentView.findViewById<TextView>(R.id.tv_entrust_value_label).onLineText("contract_text_entrustValue")
        contentView.findViewById<TextView>(R.id.tv_cost_label).onLineText("contract_text_cost")
        contentView.findViewById<TextView>(R.id.tv_available_balance_label).onLineText("sl_str_available_balance")
        contentView.findViewById<TextView>(R.id.tv_positions_label).onLineText("sl_str_positions_after_trade")
        contentView.findViewById<TextView>(R.id.tv_not_remind).onLineText("sl_str_no_longer_remind")
        contentView.findViewById<TextView>(R.id.btn_cancel).onLineText("common_text_btnCancel")
        contentView.findViewById<TextView>(R.id.btn_ok).onLineText("common_text_btnConfirm")
        contentView.findViewById<TextView>(R.id.btn_cancel_two).onLineText("common_text_btnCancel")
        contentView.findViewById<CommonlyUsedButton>(R.id.btn_ok_two).setContent(context.getLineText("common_text_btnConfirm"))
    }

    /**
     * 初始化View
     */
    private fun initView() {
        rootLayout = contentView.findViewById(R.id.root_layout)
        cardView = contentView.findViewById(R.id.card_view)
        btnOk = contentView.findViewById(R.id.btn_ok)
        btnOkTwo = contentView.findViewById(R.id.btn_ok_two)
        btnCancel = contentView.findViewById(R.id.btn_cancel)
        btnCancelTwo = contentView.findViewById(R.id.btn_cancel_two)
        title = contentView.findViewById(R.id.tv_title)
        tvContract = contentView.findViewById(R.id.tv_contract)
        tvContractTwo = contentView.findViewById(R.id.tv_contract_two)
        btnLayout = contentView.findViewById(R.id.btn_layout)
        tvPrice = contentView.findViewById(R.id.tv_price)
        tvAmount = contentView.findViewById(R.id.tv_amount)
        tvPriceValue = contentView.findViewById(R.id.tv_price_value)
        tvAmountValue = contentView.findViewById(R.id.tv_amount_value)
        tvLeverageValue = contentView.findViewById(R.id.tv_leverage_value)
        rlLeverage = contentView.findViewById(R.id.rl_leverage)
        rlEntrustValue = contentView.findViewById(R.id.rl_entrust_value)
        rlCost = contentView.findViewById(R.id.rl_cost)
        rlAval = contentView.findViewById(R.id.rl_available_balance)
        rlPosition = contentView.findViewById(R.id.rl_position)
        tvEntrustValue = contentView.findViewById(R.id.tv_entrust_value)
        tvCost = contentView.findViewById(R.id.tv_cost)
        tvAvailableBalance = contentView.findViewById(R.id.tv_available_balance)
        tvPosition = contentView.findViewById(R.id.tv_positions)
        tvTagPrice = contentView.findViewById(R.id.tv_tag_price)
        tvForceClosePrice = contentView.findViewById(R.id.tv_forced_close_price)
        tvTagRatioForce = contentView.findViewById(R.id.tv_tag_ratio_force_close)
        tvWarning = contentView.findViewById(R.id.tv_warning)
        cbNoRemind = contentView.findViewById(R.id.cb_not_remind)
        cbNoRemind.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, _ -> })
        contentView.findViewById<View>(R.id.tv_not_remind).setOnClickListener {
            cbNoRemind.isChecked = !cbNoRemind.isChecked
        }
        title.visibility = View.GONE
        //开仓部分逻辑 止盈止损
        item_stop_rate = contentView.findViewById(R.id.item_stop_rate)
        item_stop_loss = contentView.findViewById(R.id.item_stop_loss)
        btnCancel.setOnClickListener { dismiss() }
        btnCancelTwo.setOnClickListener { dismiss() }
        setOnDismissListener {
            PreferenceManager.getInstance(ContractSDKAgent.context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !cbNoRemind.isChecked)
        }
    }

    /**
     * 设置确定和取消事件
     */
    fun setOkListener(ok: () -> Unit) {
        btnOk.setOnClickListener { doSubmit(ok) }
        btnOkTwo.clicked = true
        btnOkTwo.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                doSubmit(ok)
            }
        }
    }

    /**
     * 提交
     */
    private fun doSubmit(ok: () -> Unit) {
        if (!doSubmitPreVerify()) {
            return
        }
        val contractAccount = if (buyOrSellHelper.contractOrder?.position_type == 2) {
            ContractUserDataAgent.getContractAccount(buyOrSellHelper.contract?.margin_coin!!)
        } else {
            null
        }
        val calculateOrderLiquidatePrice = ContractCalculate.CalculateOrderLiquidatePrice(buyOrSellHelper.contractOrder!!, contractAccount, buyOrSellHelper.contract!!)
        val warnPx = ContractCalculate.calculateStopLostWarnPx(buyOrSellHelper.contract!!, buyOrSellHelper.contractOrder?.px!!.toDouble(), calculateOrderLiquidatePrice)
        when (contractOrder?.side) {
            //多仓限价止损时限制若触发价格或者执行价格低于仓位预警价格提示: 止损价格触发价格或执行价格低于预警价格 6400.8，可能会导致止损失败，是否继续提交？
            ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG , ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                val execuPrice = if (item_stop_loss.isMarketPrice) {
                    item_stop_loss.triggerPrice
                }else{
                    item_stop_loss.executionPrice
                }
                if (isLossOpen && (item_stop_loss.triggerPrice.toDouble() < warnPx || execuPrice.toDouble() < warnPx)) {
                    val decimal = NumberUtil.getDecimal(2).format(warnPx)
                    val warnStr = String.format(context.getLineText("contract_sting_moreProfitWaring"), decimal)
                    NewDialogUtils.showDialog(context, warnStr, false, object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            ok.invoke()
                        }
                    }, cancelTitle = context.getLineText("submit"), confrimTitle = context.getLineText("cancel"))
                } else {
                    ok.invoke()
                }
            }
            //空仓限价止损时限制若触发价格或者执行价格高于于仓位预警价格提示：止损价格触发价格或执行价格高于预警价格 6400.8，可能会导致止损失败，是否继续提交？
            ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT , ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                val execuPrice = if (item_stop_loss.isMarketPrice) {
                    item_stop_loss.triggerPrice
                }else{
                    item_stop_loss.executionPrice
                }
                if (isLossOpen && (item_stop_loss.triggerPrice.toDouble() > warnPx || execuPrice.toDouble() > warnPx)) {
                    val decimal = NumberUtil.getDecimal(2).format(warnPx)
                    val warnStr = String.format(context.getLineText("contract_sting_lessLossWaring"), decimal)
                    NewDialogUtils.showDialog(context, warnStr, false, object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            ok.invoke()
                        }
                    }, cancelTitle = context.getLineText("submit"), confrimTitle = context.getLineText("cancel"))
                } else {
                    ok.invoke()
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        buyOrSellHelper.contract?.let { contract ->
            contractOrder = buyOrSellHelper.contractOrder
            contractTicker = ContractPublicDataAgent.getContractTicker(contract.instrument_id)
            title.apply {
                visibility = View.VISIBLE
                if (buyOrSellHelper.isBuy) {
                    onLineText("sl_str_buy_open")
                    setTextColor(ContextCompat.getColor(context, R.color.main_green))
                } else {
                    onLineText("sl_str_sell_open")
                    setTextColor(ContextCompat.getColor(context, R.color.main_red))
                }
            }
            tvContract.text = contract.symbol
            tvContractTwo.text = contract.symbol
            val priceStr = "${context.getLineText("contract_text_price")}(${contract.quote_coin})"
            tvPrice.text = priceStr
            tvPriceValue.text = buyOrSellHelper.priceDisplay
            val amountStr = "${context.getLineText("sl_str_amount")}(${context.getLineText("sl_str_contracts_unit")})"
            tvAmount.text = amountStr
            tvAmountValue.text = contractOrder?.qty
            val mode = if (contractOrder?.position_type == 1) {
                context.getLineText("sl_str_gradually_position")
            } else {
                context.getLineText("sl_str_full_position")
            }
            val leverage = mode + (contractOrder?.leverage) + "X"
            tvLeverageValue.text = leverage
            val code = contract.margin_coin
            val dfDefault = NumberUtil.getDecimal(-1)
            val contractValue = ContractCalculate.CalculateContractValue(contractOrder?.qty!!, buyOrSellHelper.etPrice!!, contract)
            val entrust = dfDefault.format(MathHelper.round(contractValue, contract.value_index)) + " " + code
            tvEntrustValue.text = entrust
            val longOpenCost = if (buyOrSellHelper.isBuy) {
                ContractCalculate.CalculateAdvanceOpenCost(
                        contractOrder,
                        ContractUserDataAgent.getContractPosition(contract.instrument_id, ContractPosition.POSITION_TYPE_LONG),
                        ContractUserDataAgent.getContractOrderSize(contract.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                        contract)
            } else {
                ContractCalculate.CalculateAdvanceOpenCost(
                        contractOrder,
                        ContractUserDataAgent.getContractPosition(contract.instrument_id, ContractPosition.POSITION_TYPE_SHORT),
                        ContractUserDataAgent.getContractOrderSize(contract.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                        contract)
            }
            val cost = (if (longOpenCost == null) "0 $code" else dfDefault.format(MathHelper.round(longOpenCost.freezAssets, contract.value_index))) + " " + code
            tvCost.text = cost
            val contractAccount = ContractUserDataAgent.getContractAccount(contract.margin_coin!!)
            val available = dfDefault.format(MathHelper.round(contractAccount?.available_vol_real!!, contract.value_index)) + " " + code
            tvAvailableBalance.text = available
            val contractPosition = if (buyOrSellHelper.isBuy) {
                ContractUserDataAgent.getContractPosition(contract.instrument_id, ContractPosition.POSITION_TYPE_LONG )
            } else {
                ContractUserDataAgent.getContractPosition(contract.instrument_id, ContractPosition.POSITION_TYPE_SHORT)
            }
            val holdVol = if (contractPosition == null) "0" else contractPosition.cur_qty
            val positions = dfDefault.format(max(0.0, MathHelper.round(MathHelper.add(contractOrder?.qty, holdVol), contract.vol_index)))
            tvPosition.text = positions
            val tag = dfDefault.format(MathHelper.round(buyOrSellHelper.tagPrice, contract.price_index))
            tvTagPrice.text = tag
            val liquidatePrice = ContractCalculate.CalculateOrderLiquidatePrice(
                    contractOrder!!,
                    if(contractOrder?.position_type==2) ContractUserDataAgent.getContractAccount(contract.margin_coin) else null,
                    contract)
            val force = dfDefault.format(MathHelper.round(liquidatePrice, contract.price_index))
            tvForceClosePrice.text = force
            val ratio = MathHelper.div(MathHelper.round(buyOrSellHelper.tagPrice), liquidatePrice) * 100
            val ratioStr = dfDefault.format(MathHelper.round(ratio, contract.value_index)).toString() + "%"
            tvTagRatioForce.text = ratioStr
            if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                tvEntrustValue.setText(R.string.contract_action_marketPrice)
                tvCost.setText(R.string.contract_action_marketPrice)
                rlEntrustValue.visibility = View.GONE
                rlCost.visibility = View.GONE
                rlAval.visibility = View.GONE
                rlPosition.visibility = View.GONE
            }
            if (buyOrSellHelper.tradeType == 0) {
                buildStopRateLoss()
            }
        }
    }

    /**
     * 构建止盈止损
     */
    private fun buildStopRateLoss() {
        buildStopRateLoss = true
        buyOrSellHelper.showRateAndLoss = true
        item_stop_rate.visibility = View.VISIBLE
        item_stop_loss.visibility = View.VISIBLE
        item_stop_rate.apply {
            isRateItem = true
            title = context.getLineText("sl_str_stop_rate")
            buySellHelper = buyOrSellHelper
            originContract = buyOrSellHelper.contract
            contractOrder = buyOrSellHelper.contractOrder
            originContractTicker = contractTicker
            priceUnit = buyOrSellHelper.contract!!.margin_coin
            pxPrecision = buyOrSellHelper.contract!!.px_unit
        }
        item_stop_loss.apply {
            isRateItem = false
            title = context.getLineText("sl_str_stop_loss")
            buySellHelper = buyOrSellHelper
            originContract = buyOrSellHelper.contract
            contractOrder = buyOrSellHelper.contractOrder
            originContractTicker = contractTicker
            priceUnit = buyOrSellHelper.contract!!.margin_coin
            pxPrecision = buyOrSellHelper.contract!!.px_unit
        }
        // 修改弹窗样式
        rootLayout.gravity = Gravity.BOTTOM
        btnLayout.visibility = View.GONE
        cardView.radius = 0f
        cardView.setBackgroundResource(R.drawable.sl_item_tread_confirm_dialog_bg)
        tvContract.visibility = View.GONE
        tvContractTwo.visibility = View.VISIBLE
        btnOk.visibility = View.GONE
        btnOkTwo.visibility = View.VISIBLE
        btnCancel.visibility = View.GONE
        btnCancelTwo.visibility = View.VISIBLE
    }

    /**
     * 提交预校验
     */
    private fun doSubmitPreVerify(): Boolean {
        if (isRateOpen) {
            if (MathHelper.round(item_stop_rate.triggerPrice) <= 0) {
                NToastUtil.showTopToast(false, context.getLineText("sl_str_stop_rate") + context.getLineText("sl_str_price_too_low"))
                return false
            }
            if (!item_stop_rate.isMarketPrice && MathHelper.round(item_stop_rate.executionPrice) <= 0) {
                NToastUtil.showTopToast(false, context.getLineText("sl_str_stop_rate") + context.getLineText("sl_str_price_too_low"))
                return false
            }
            if (!item_stop_rate.isPriceSure) {
                item_stop_rate.warnShake()
                return false
            }
            contractOrder?.with_mission = CONDITIONCOMMISSIONORDER_TYPE_PROFIT
            contractOrder?.profit_price_type = 1 //默认都是最新价
            contractOrder?.profit_price = item_stop_rate.triggerPrice
            if (item_stop_rate.isMarketPrice) {
                contractOrder?.profit_category = 2 //市价
            } else {
                contractOrder?.profit_category = 1 //现价
                contractOrder?.profit_ex_price = item_stop_rate.executionPrice
            }
        } else {
            contractOrder?.profit_price_type = 0
            contractOrder?.profit_category = 0
            contractOrder?.profit_price = null
            contractOrder?.profit_ex_price = null
        }
        if (isLossOpen) {
            if (MathHelper.round(item_stop_loss.triggerPrice) <= 0) {
                NToastUtil.showTopToast(false, context.getLineText("sl_str_stop_loss") + context.getLineText("sl_str_price_too_low"))
                return false
            }
            if (!item_stop_loss.isMarketPrice && MathHelper.round(item_stop_loss.executionPrice) <= 0) {
                NToastUtil.showTopToast(false, context.getLineText("sl_str_stop_loss") + context.getLineText("sl_str_price_too_low"))
                return false
            }
            if (!item_stop_loss.isPriceSure) {
                item_stop_loss.warnShake()
                return false
            }
            contractOrder?.with_mission = CONDITIONCOMMISSIONORDER_TYPE_LOSS
            contractOrder?.loss_price_type = 1 //默认都是最新价
            contractOrder?.loss_price = item_stop_loss.triggerPrice
            if (item_stop_loss.isMarketPrice) {
                contractOrder?.loss_category = 2 //市价
            } else {
                contractOrder?.loss_category = 1 //现价
                contractOrder?.loss_ex_price = item_stop_loss.executionPrice
            }
        } else {
            contractOrder?.loss_price_type = 0
            contractOrder?.loss_category = 0
            contractOrder?.loss_price = null
            contractOrder?.loss_ex_price = null
        }
        if (isRateOpen && isLossOpen) {
            contractOrder?.with_mission = CONDITIONCOMMISSIONORDER_TYPE_ALL
        }
        return true
    }

    /**
     * 显示提示信息
     */
    fun showWarning(str: String?) {
        tvWarning.visibility = View.VISIBLE
        tvWarning.text = str
    }

    /**
     * 是否开启止盈
     */
    private val isRateOpen: Boolean
        get() = item_stop_rate.checkTab

    /**
     * 是否开启止损
     */
    private val isLossOpen: Boolean
        get() = item_stop_loss.checkTab

}