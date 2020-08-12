package com.yjkj.chainup.contract.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.*
import com.contract.sdk.extra.Contract.AdvanceOpenCost
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.extra.dispense.DataDepthHelper
import com.contract.sdk.impl.ContractDepthListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.listener.OnBindViewListener
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlContractDetailActivity
import com.yjkj.chainup.contract.activity.SlContractEntrustActivity
import com.yjkj.chainup.contract.activity.SlSelectLeverageActivity
import com.yjkj.chainup.contract.adapter.BuySellContractAdapter
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.helper.SLContractBuyOrSellHelper
import com.yjkj.chainup.contract.listener.SLDoListener
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.ContractTradeConfirmWindow
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.fragment_sl_contract_trade.*
import kotlinx.android.synthetic.main.layout_contract_trade.tv_coin_name
import kotlinx.android.synthetic.main.layout_contract_trade.tv_lever
import kotlinx.android.synthetic.main.layout_contract_trade.tv_order_type
import kotlinx.android.synthetic.main.sl_include_contract_trade_left_layout.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor

/**
 * 合约交易
 */
class SlContractTradeFragment : NBaseFragment(), BuySellContractAdapter.OnBuySellContractClickedListener, LogicContractSetting.IContractSettingListener, SLDoListener {

    /**
     * 限价模式下，判断是否是高级委托
     */
    private var isAdvancedLimit = false
    var orderTypeList = ArrayList<TabInfo>()
    var orderTypeDialog: TDialog? = null

    //订单成交方式 1:普通,2:FOK,3:IOC
    var currAdvancedLimit = 1
    var advancedLimitTypeList = ArrayList<TabInfo>()

    var mContract: Contract? = null

    //盘面
    var diskTypeList = ArrayList<TabInfo>()
    var currDiskType: TabInfo? = null
    var sidkDialog: TDialog? = null

    //资金费率
    var feeDialog: TDialog? = null
    var llFeeWarpLayout: LinearLayout? = null

    var inflater: LayoutInflater? = null

    // 0 限价委托 1 计划委托
    private var tabEntrustIndex = 0
    private var priceEntrustFragment = SlContractPriceEntrustFragment.newInstance()
    private var planEntrustFragment = SlContractPlanEntrustFragment.newInstance()
    private var currentFragment = Fragment()

    //合约深度
    private var lastPrice = "0.00"
    private var tagPrice = "0.00"
    private var indexPrice = "0.00"

    /**
     * 买卖数据辅助类
     */
    private val buyOrSellHelper = SLContractBuyOrSellHelper()


    override fun setContentView(): Int {
        return R.layout.fragment_sl_contract_trade
    }

    override fun initView() {
        initAutoStringView()
        LogicContractSetting.getInstance().registListener(this)
        inflater = LayoutInflater.from(context)
        doSwitchTab(0)
        updateLeverUI()
        updateBtnUI()
        changeDiskTypeUi(isInit = true)
        initListener()
        showFragment()
        updateOrderType(false)
    }

    /**
     * 文本动态初始化
     */
    private fun initAutoStringView() {
        tv_order_type.onLineText("sl_str_limit_entrust")
        tv_lever_title.onLineText("contract_action_lever")
        tab_latest_price.onLineText("sl_str_latest_price_simple")
        tab_fair_price.onLineText("sl_str_fair_price_simple")
        tab_index_price.onLineText("sl_str_index_price_simple")
        et_position.hint = getLineText("sl_str_amount")
        tv_balance_title.onLineText("sl_str_avbl")
        tab_market_price.onLineText("sl_str_market_order")
        tab_buy1.onLineText("sl_str_buy1_price")
        tab_sell1.onLineText("sl_str_sell1_price")
        tv_price_hint.onLineText("contract_action_marketPrice")
        tv_price_title.onLineText("contract_text_price")
        tv_index_price_title.onLineText("contract_text_indexPrice")
        tv_fair_price.onLineText("sl_str_fair_price")
        iv_funds_rate.onLineText("sl_str_funds_rate")
        rb_limit_entrust.text = getLineText("sl_str_limit_entrust")
        rb_plan_entrust.text = getLineText("sl_str_plan_entrust")
        ll_all_entrust_order.onLineText("common_action_sendall")
        et_price.hint = getLineText("contract_text_price")
        tv_long_title2.onLineText("sl_str_position")
        tv_short_title2.onLineText("sl_str_position")
        tv_market_price_hint.onLineText("sl_str_market_price")
        tv_sell_cost_label.onLineText("contract_text_mybeCost")
        tv_buy_cost_label.onLineText("contract_text_mybeCost")
        tv_amount.text = getLineText("sl_str_amount") + "(" + getLineText("contract_text_volumeUnit") + ")"
    }


    override fun loadData() {
        super.loadData()
        //订单类型
        orderTypeList.add(TabInfo(getLineText("sl_str_limit_entrust"), CONTRACT_ORDER_LIMIT))
        orderTypeList.add(TabInfo(getLineText("sl_str_plan_entrust"), CONTRACT_ORDER_PLAN))
        orderTypeList.add(TabInfo(getLineText("sl_str_limit_advanced_entrust"), CONTRACT_ORDER_ADVANCED_LIMIT))

        //高级委托设置类型
        advancedLimitTypeList.add(TabInfo(getLineText("sl_str_item_post_only"), 1))
        advancedLimitTypeList.add(TabInfo(getLineText("sl_str_item_fok"), 2))
        advancedLimitTypeList.add(TabInfo(getLineText("sl_str_item_ioc"), 3))

        ///杠杆
        initLeverageData()

        //盘面
        diskTypeList.add(TabInfo(getLineText("sl_str_default"), AppConstant.DEFAULT_TAPE))
        diskTypeList.add(TabInfo(getLineText("sl_str_ask"), AppConstant.SELL_TAPE))
        diskTypeList.add(TabInfo(getLineText("sl_str_bid"), AppConstant.BUY_TAPE))
        currDiskType = diskTypeList[0]

        //注册深度监听
        ContractPublicDataAgent.registerDepthWsListener(this, getDepthSubCount(), deepListener)
    }

    private fun getDepthSubCount(): Int {
        currDiskType?.let {
            return if (it.index == AppConstant.DEFAULT_TAPE) {
                5
            } else {
                10
            }
        }
        return 5
    }

    private val deepListener = object : ContractDepthListener() {

        override fun onWsContractDepth(contractId: Int, buyList: java.util.ArrayList<DepthData>, sellList: java.util.ArrayList<DepthData>) {
            if (contractId == mContract?.instrument_id) {
                if (isHidden || !isVisible || !isResumed) {
                    return
                }
                if (buyList.isNotEmpty()) {
                    updateDepth(true, buyList)
                }
                if (sellList.isNotEmpty()) {
                    updateDepth(false, sellList)
                }
            }
        }

    }

    /**
     * 绑定合约
     */
    fun bindContract(contract: Contract, resetData: Boolean = false) {
        mContract = contract
        buyOrSellHelper.contract = mContract
        LogUtil.d("DEBUG", "------------bindContract-----${contract}--${mContract!!.symbol}-----")
        //杠杆
        initLeverageData()
        updateLeverUI()
        setVolUnit()
        ll_sell_Layout.bindContract(mContract, this)
        ll_buy_layout.bindContract(mContract, this)

        //合约深度
        if (resetData) {
            ll_buy_layout?.initData(null)
            ll_sell_Layout?.initData(null)
        }
        //限价委托
        priceEntrustFragment.bindContract(mContract!!)
        //计划委托
        planEntrustFragment.bindContract(mContract!!)
        //加载仓位
        updateAvailableVol()
        updateUserAssetUI()
    }

    private fun initLeverageData() {
        mContract?.let {
            GlobalLeverageUtils.bindContract(it)
            GlobalLeverageUtils.updateLeverageListener = this
        }
    }

    private fun initListener() {
        //切换限价个计划列表tab
        rg_tab_layout.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_limit_entrust -> {
                    tabEntrustIndex = 0
                    showFragment()
                }
                R.id.rb_plan_entrust -> {
                    tabEntrustIndex = 1
                    showFragment()
                }
            }

        }
        //跳转全部委托
        ll_all_entrust_order.setOnClickListener {
            if (LoginManager.checkLogin(activity, true) && mContract != null) {
                SlContractEntrustActivity.show(mActivity!!, mContract?.instrument_id!!, tabEntrustIndex)
            }
        }
        //合理价格弹窗
        tv_fair_price.setOnClickListener {
            NewDialogUtils.showDialog(context!!, getLineText("sl_str_fair_price_intro", true), true, null, getLineText("sl_str_fair_price"), getLineText("sl_str_isee"))
        }
        //资金费率
        iv_funds_rate.setOnClickListener {
            showFundsRateDialog()
        }
        ///限价、计划,高级委托切换
        tv_order_type.setOnClickListener {
            showOrderTypeDialog()
        }
        //高级委托设置切换
        tv_order_advanced.setOnClickListener {
            showOrderAdvancedTypeDialog()
        }
        ///切换杠杆
        ll_lever.setOnClickListener {
            if (buyOrSellHelper.tradeType == 0) {
                mContract?.let {
                    if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                        if (LoginManager.checkLogin(activity, true)) {
                            if (ContractUserDataAgent.hasPositionOrOrder(it.instrument_id)) {
                                ToastUtils.showToast("contract_leavgerLimit".localized())
                            } else {
                                SlSelectLeverageActivity.show(mActivity!!, it.instrument_id, GlobalLeverageUtils.currLeverage, getRealPrice(), GlobalLeverageUtils.currentPositionType)
                            }
                        }
                    } else {
                        SlSelectLeverageActivity.show(mActivity!!, it.instrument_id, GlobalLeverageUtils.currLeverage, getRealPrice(), GlobalLeverageUtils.currentPositionType)
                    }
                }
            }
        }
        ///改变盘面
        ib_disk_layout.setOnClickListener {
            showSelectDiskDialog()
        }
        //市场价
        tab_market_price.setOnClickListener {
            buyOrSellHelper.priceType = if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                CONTRACT_ORDER_LIMIT
            } else {
                CONTRACT_ORDER_MARKET
            }
            updateOrderType(true)
        }
        //买一价
        tab_buy1.setOnClickListener {
            buyOrSellHelper.priceType = if (buyOrSellHelper.priceType == CONTRACT_ORDER_BID_PRICE) {
                CONTRACT_ORDER_LIMIT
            } else {
                CONTRACT_ORDER_BID_PRICE
            }
            updateOrderType(true)
        }
        //卖一价
        tab_sell1.setOnClickListener {
            buyOrSellHelper.priceType = if (buyOrSellHelper.priceType == CONTRACT_ORDER_ASK_PRICE) {
                CONTRACT_ORDER_LIMIT
            } else {
                CONTRACT_ORDER_ASK_PRICE
            }
            updateOrderType(true)
        }
        //最新价
        tab_latest_price.setOnClickListener {
            LogicContractSetting.setTriggerPriceType(mActivity, 1)
            onContractSettingChange()
        }
        //合理价
        tab_fair_price.setOnClickListener {
            LogicContractSetting.setTriggerPriceType(mActivity, 2)
            onContractSettingChange()
        }
        //指数价
        tab_index_price.setOnClickListener {
            LogicContractSetting.setTriggerPriceType(mActivity, 4)
            onContractSettingChange()
        }
        //计划-市价
        tv_price_hint.setOnClickListener {
            if (LogicContractSetting.getExecution(ContractSDKAgent.context) == 1) {
                LogicContractSetting.setExecution(ContractSDKAgent.context, 0)
            } else {
                LogicContractSetting.setExecution(ContractSDKAgent.context, 1)
            }
            buyOrSellHelper.priceType = CONTRACT_ORDER_PLAN
            updateOrderType(true)
        }
        //计划委托规则弹窗
        iv_plan_rule.setOnClickListener {
            NewDialogUtils.showDialog(context!!, getLineText("sl_str_plan_entrust_intro", true), true, null, getLineText("sl_str_plan_entrust"), getLineText("sl_str_isee"))
        }
        //买入
        stv_buy.isEnable(true)
        stv_buy.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                if (LoginManager.checkLogin(activity, true)) {
                    buyOrSellHelper.isBuy = true
                    doBuyOrSell()
                }
            }
        }
        //卖出
        stv_sell.isEnable(true)
        stv_sell.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                if (LoginManager.checkLogin(activity, true)) {
                    buyOrSellHelper.isBuy = false
                    doBuyOrSell()
                }
            }

        }
        et_price.addTextChangedListener(mTextWatcher)
        et_position.addTextChangedListener(mTextWatcher)
        et_trigger_price.addTextChangedListener(mTextWatcher)
        //焦点变化改变输入框背景
        et_price?.setOnFocusChangeListener { _, hasFocus ->
            ll_price?.setBackgroundResource(if (hasFocus) R.drawable.bg_trade_et_focused else R.drawable.bg_trade_et_unfocused)
        }
        et_position?.setOnFocusChangeListener { _, hasFocus ->
            ll_position?.setBackgroundResource(if (hasFocus) R.drawable.bg_trade_et_focused else R.drawable.bg_trade_et_unfocused)
        }
        et_trigger_price?.setOnFocusChangeListener { _, hasFocus ->
            ll_trigger_price?.setBackgroundResource(if (hasFocus) R.drawable.bg_trade_et_focused else R.drawable.bg_trade_et_unfocused)
        }
    }

    /**
     * 更新杠杆UI
     */
    private fun updateLeverUI() {
        tv_lever?.text = if (GlobalLeverageUtils.currentPositionType == 1) {
            getLineText("sl_str_gradually_position") + GlobalLeverageUtils.currLeverage + "X"
        } else {
            getLineText("sl_str_full_position") + GlobalLeverageUtils.currLeverage + "X"
        }
    }

    /**
     * 更新计划委托触发价
     */
    private fun updateTriggerPriceUi(triggerPriceIndex: Int) {
        tab_latest_price.isChecked = triggerPriceIndex == 1
        tab_fair_price.isChecked = triggerPriceIndex == 2
        tab_index_price.isChecked = triggerPriceIndex == 4
        val triggerTypeText = when (triggerPriceIndex) {
            1 -> {
                getLineText("sl_str_latest_price_simple")
            }
            2 -> {
                getLineText("sl_str_fair_price_simple")
            }
            4 -> {
                getLineText("sl_str_index_price_simple")
            }
            else -> getLineText("sl_str_latest_price_simple")
        }
        et_trigger_price.hint = getLineText("sl_str_trigger_price") + "(" + triggerTypeText + ")"
    }

    /**
     * 1、普通委托，委托价格为市价时，计算最大可开和成本时使用最新价格；
     * 2、计划委托，执行价格为限价，计算预估成本和最大可开张数时使用设置的执行价格；
     * 3、计划委托，执行价格为市价，计算预估成本和最大可开张数时使用设置的触发价格。
     */
    private fun getRealPrice(): String {
        var price = "0.00"
        if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {//普通市价->最新价
            price = lastPrice
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_BID_PRICE) {//普通买一价
            val list = ll_buy_layout?.getList()
            price = if (list != null && list.size > 0) {
                list[0].price
            } else {
                lastPrice
            }
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_ASK_PRICE) {//普通卖一价
            val list = ll_sell_Layout?.getList()
            price = if (list != null && list.size > 0) {
                list[0].price
            } else {
                lastPrice
            }
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_LIMIT) {//普通限价-输入框
            price = et_price.text.toString().trim()
            if (TextUtils.isEmpty(price)) {
                price = tagPrice//输入框为空取合理价
            }
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {//计划委托
            price = if (LogicContractSetting.getExecution(mActivity!!) == 0) {//限价->执行价格
                price = et_price.text.toString().trim()
                if (TextUtils.isEmpty(price)) tagPrice else price
            } else {//市价->触发价格
                price = et_trigger_price.text.toString().trim()
                if (TextUtils.isEmpty(price)) tagPrice else price
            }
        }
        return price
    }

    /**
     * 展示资金费率提示框
     */
    private fun showFundsRateDialog() {
        if (mContract == null) {
            return
        }
        if (Utils.isFastClick())
            return
        feeDialog = SlDialogHelper.showFundsRateDialog(context!!, OnBindViewListener { viewHolder ->
            viewHolder?.let {
                it.getView<TextView>(R.id.tv_title).onLineText("sl_str_funds_rate")
                it.getView<TextView>(R.id.tv_content).onLineText("sl_str_funds_rate_intro")
                it.getView<TextView>(R.id.tv_more).onLineText("sl_str_learn_more")
                it.getView<TextView>(R.id.tv_confirm_btn).onLineText("sl_str_isee")

                llFeeWarpLayout = it.getView(R.id.ll_fee_warp_layout)
                it.setOnClickListener(R.id.tv_more) {
                    SlContractDetailActivity.show(mActivity!!, mContract?.instrument_id!!, 2)
                    feeDialog?.dismiss()
                }
            }
        })
        ContractPublicDataAgent.loadFundingRate(mContract!!.instrument_id, object : IResponse<MutableList<ContractFundingRate>>() {
            override fun onSuccess(data: MutableList<ContractFundingRate>) {
                if (data != null && data.isNotEmpty()) {
                    for (i in 0 until Math.min(data.size, 4)) {
                        val item = data[i]
                        val itemView = inflater?.inflate(R.layout.sl_item_funding_rate_dlg, llFeeWarpLayout, false)
                        llFeeWarpLayout?.addView(itemView)
                        val tvTimeValue = itemView?.findViewById<TextView>(R.id.tv_time_value)
                        val tvFundingRateValue = itemView?.findViewById<TextView>(R.id.tv_funding_rate_value)
                        val rate: Double = MathHelper.mul(item.rate, "100")
                        tvFundingRateValue?.text = NumberUtil.getDecimal(4).format(rate).toString() + "%"

                        tvTimeValue?.text = TimeFormatUtils.timeStampToDate((item.timestamp + mContract!!.settlement_interval) * 1000, "yyyy-MM-dd  HH:mm:ss")
                    }
                }
            }

            override fun onFail(code: String, msg: String) {
                ToastUtils.showToast(ContractSDKAgent.context, msg)
            }
        })
    }

    /**
     * 展示 默认/卖盘/买盘
     */
    private fun showSelectDiskDialog() {
        sidkDialog = NewDialogUtils.showNewBottomListDialog(context!!, diskTypeList, currDiskType!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                if (currDiskType != diskTypeList[index]) {
                    currDiskType = diskTypeList[index]
                    changeDiskTypeUi()
                }
                sidkDialog?.dismiss()
                sidkDialog = null
            }
        })
    }

    /**
     * 高级限价展示 master/fok/ioc
     */
    private fun showOrderAdvancedTypeDialog() {
        orderTypeDialog = NewDialogUtils.showNewBottomListDialog(context!!, advancedLimitTypeList, currAdvancedLimit, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(item: Int) {
                currAdvancedLimit = advancedLimitTypeList[item].index
                when (currAdvancedLimit) {
                    1 -> {
                        tv_order_advanced?.text = getLineText("sl_str_tab_post_only")
                    }
                    2 -> {
                        tv_order_advanced?.text = getLineText("sl_str_tab_fok")
                    }
                    3 -> {
                        tv_order_advanced?.text = getLineText("sl_str_tab_ioc")
                    }
                }
                orderTypeDialog?.dismiss()
            }
        })
    }

    private fun showOrderTypeDialog() {
        orderTypeDialog = NewDialogUtils.showNewBottomListDialog(context!!, orderTypeList, if (isAdvancedLimit) CONTRACT_ORDER_ADVANCED_LIMIT else buyOrSellHelper.priceType, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(item: Int) {
                tv_order_type?.text = orderTypeList[item].name
                orderTypeDialog?.dismiss()
                if (orderTypeList[item].index == CONTRACT_ORDER_ADVANCED_LIMIT) {//如果选择高级委托,priceType 按照普通限价类型走逻辑
                    isAdvancedLimit = true
                    buyOrSellHelper.priceType = CONTRACT_ORDER_LIMIT
                    //高级委托，市价单置灰
                    tab_market_price.isEnabled = false
                } else {
                    tab_market_price.isEnabled = true
                    isAdvancedLimit = false
                    buyOrSellHelper.priceType = orderTypeList[item].index
                }
                updateOrderType(true)
            }
        })
    }

    /**
     * 更新盘面UI
     */
    fun changeDiskTypeUi(isInit: Boolean = false) {
        currDiskType?.index?.let {
            ColorUtil.setTapeIcon(ib_disk_layout, it)
            //切换了盘面，需处理数据
            if (!isInit) {
                deepListener.count = getDepthSubCount()
                ll_sell_Layout.updateDeepType(it)
                ll_buy_layout.updateDeepType(it)
            }
        }
    }

    /**
     * 更新订单类型
     */
    fun updateOrderType(updatePrice: Boolean) {
        iv_plan_rule.visibility = View.GONE
        tv_coin_name.visibility = View.VISIBLE
        tv_price_hint.visibility = View.GONE
        ll_order_advanced_setting.visibility = View.INVISIBLE
        tv_market_price_hint.visibility = View.GONE
        rg_trigger_type.visibility = View.GONE
        et_price.visibility = View.VISIBLE
        //限价
        if (buyOrSellHelper.priceType == CONTRACT_ORDER_LIMIT) {
            ll_price.visibility = View.VISIBLE
            ll_trigger_price.visibility = View.GONE
            tv_price_value.visibility = View.VISIBLE
            tv_order_tips_layout.visibility = View.GONE
            rg_order_type.visibility = View.VISIBLE
            rg_order_type.clearCheck()
            updateAdvancedLimitUiStatus()
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {//市价
            ll_price.visibility = View.GONE
            ll_trigger_price.visibility = View.GONE
            tv_price_value.visibility = View.GONE
            tv_order_tips_layout.visibility = View.VISIBLE
            tv_order_tips_layout.text = getLineText("sl_str_market_price")
            updateAdvancedLimitUiStatus()
        } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {//计划
            tv_price_hint.visibility = View.VISIBLE
            iv_plan_rule.visibility = View.VISIBLE
            ll_price.visibility = View.VISIBLE
            ll_trigger_price.visibility = View.VISIBLE
            tv_price_value.visibility = View.VISIBLE
            tv_order_tips_layout.visibility = View.GONE
            rg_order_type.visibility = View.GONE
            rg_trigger_type.visibility = View.VISIBLE
            updateTriggerPriceUi(LogicContractSetting.getTriggerPriceType(ContractSDKAgent.context))
            val execution = LogicContractSetting.getExecution(ContractSDKAgent.context)
            if (execution == 0) {
                tv_price_hint.setTextColor(resources.getColor(R.color.normal_text_color))
                tv_coin_name.visibility = View.GONE
                tv_market_price_hint.visibility = View.GONE
                et_price.visibility = View.VISIBLE
            } else if (execution == 1) {
                tv_price_hint.setTextColor(resources.getColor(R.color.main_blue))
                tv_market_price_hint.visibility = View.VISIBLE
                et_price.visibility = View.GONE
                tv_coin_name.visibility = View.GONE
            }
        } else {
            ll_price.visibility = View.GONE
            ll_trigger_price.visibility = View.GONE
            tv_price_value.visibility = View.GONE
            tv_order_tips_layout.visibility = View.VISIBLE
            if (buyOrSellHelper.priceType == CONTRACT_ORDER_BID_PRICE) {
                tv_order_tips_layout.text = getLineText("sl_str_buy1_price")
            } else {
                tv_order_tips_layout.text = getLineText("sl_str_sell1_price")
            }
            updateAdvancedLimitUiStatus()
        }
        updateAvailableVol()
    }

    private fun updateAdvancedLimitUiStatus() {
        if (isAdvancedLimit) {
            ll_order_advanced_setting.visibility = View.VISIBLE
        } else {
            ll_order_advanced_setting.visibility = View.INVISIBLE
        }
    }

    /**
     * 切换开仓和平仓
     */
    fun doSwitchTab(index: Int = 0) {
        if (buyOrSellHelper.tradeType != index) {
            buyOrSellHelper.tradeType = index
            updateBtnUI()
            //重置杠杆颜色
            resetLeverTextColor()
            //可开可平数量
            updateAvailableVolUI()
            updateUserAssetUI()
        }
    }

    /**
     * /重置可开可平UI
     */
    private fun updateAvailableVolUI() {
        if (buyOrSellHelper.tradeType == 1) {
            tv_long_title.onLineText("sl_str_sell_empty")
            tv_short_title.onLineText("sl_str_sell_max")
            //平仓需展示 持仓
            tv_short_title2.visibility = View.VISIBLE
            tv_short_value2.visibility = View.VISIBLE
            tv_long_title2.visibility = View.VISIBLE
            tv_long_value2.visibility = View.VISIBLE
            ll_buy_cost.visibility = View.GONE
            ll_sell_cost.visibility = View.GONE
        } else {
            tv_long_title.onLineText("sl_str_buy_open_up_to")
            tv_short_title.onLineText("sl_str_sell_open_up_to")
            tv_short_title2.visibility = View.GONE
            tv_short_value2.visibility = View.GONE
            tv_long_title2.visibility = View.GONE
            tv_long_value2.visibility = View.GONE
            ll_buy_cost.visibility = View.VISIBLE
            ll_sell_cost.visibility = View.VISIBLE
        }
        tv_long_value.text = "--"
        tv_short_value.text = "--"
        tv_long_value2.text = "--"
        tv_short_value2.text = "--"
        tv_buy_cost.text = " --"
        tv_sell_cost.text = " --"
        updateAvailableVol()
    }

    /**
     * 更新可开可平数量
     */
    fun updateAvailableVol() {
        if (et_price == null) {
            return
        }
        mContract?.let {
            var price = getRealPrice()
            var vol = et_position.text.toString()
            if (TextUtils.isEmpty(price) || price == ".") {
                price = "0.00"
            }
            if (TextUtils.isEmpty(vol)) {
                vol = "0"
            }

            //判断正向合约类型，计价货币不为USDT的则为混合合约类型。由于合约面值的问题，需要隐藏合约下单区的btc换算价格
            val unit = LogicContractSetting.getContractUint(ContractSDKAgent.context)
            if (!it.isReserve && unit == 0 && "USDT" != it.quote_coin && "BTC" == it.base_coin) {
                tv_volume_value.visibility = View.GONE
            } else {
                val value: String = ContractUtils.CalculateContractBasicValue(
                        ContractCalculate.trans2ContractVol(it, vol, price, LogicContractSetting.getContractUint(ContractSDKAgent.context)),
                        price,
                        it)
                tv_volume_value.visibility = View.VISIBLE
                tv_volume_value.text = "≈ $value"
            }

            if (buyOrSellHelper.tradeType == 1) {//平仓
                val longPosition = ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_LONG)
                if (longPosition != null) {
                    val avail = MathHelper.sub(longPosition.cur_qty, longPosition.freeze_qty)
                    tv_short_value2.text = ContractUtils.getVolUnit(context, it, longPosition.cur_qty, price)
                    tv_short_value.text = ContractUtils.getVolUnit(context, it, avail, MathHelper.round(price))
                    if (UserDataService.getInstance().isLogined) {
                        stv_sell.isEnable(avail > 0)
                    } else {
                        stv_sell.isEnable(true)
                    }
                } else {
                    tv_short_value.text = "--"
                    tv_short_value2.text = "--"
                    stv_sell.isEnable(false)
                }

                val shortPosition = ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_SHORT)
                if (shortPosition != null) {
                    val avail = MathHelper.sub(shortPosition.cur_qty, shortPosition.freeze_qty)
                    tv_long_value.text = ContractUtils.getVolUnit(context, it, avail, MathHelper.round(price))
                    tv_long_value2.text = ContractUtils.getVolUnit(context, it, shortPosition.cur_qty, price)
                    stv_buy.isEnable(avail > 0)
                } else {
                    tv_long_value.text = "--"
                    tv_long_value2.text = "--"
                    stv_buy.isEnable(false)
                }

                if (!UserDataService.getInstance().isLogined) {
                    stv_sell.isEnable(true)
                    stv_buy.isEnable(true)
                }
            } else {//开仓
                stv_sell.isEnable(true)
                stv_buy.isEnable(true)
                val contractAccount = ContractUserDataAgent.getContractAccount(it.margin_coin)
                if (contractAccount != null) {
                    val dfVol = NumberUtil.getDecimal(it.value_index)
                    val longVolume = ContractCalculate.CalculateVolume(
                            dfVol.format(contractAccount.available_vol_real),
                            GlobalLeverageUtils.currLeverage,
                            ContractUserDataAgent.getContractOrderSize(it.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                            ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_LONG),
                            price,
                            ContractPosition.POSITION_TYPE_LONG,
                            it)

                    val shortVolume = ContractCalculate.CalculateVolume(
                            dfVol.format(contractAccount.available_vol_real),
                            GlobalLeverageUtils.currLeverage,
                            ContractUserDataAgent.getContractOrderSize(it.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                            ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_SHORT),
                            price,
                            ContractPosition.POSITION_TYPE_SHORT,
                            it)
                    tv_long_value.text = ContractUtils.getVolUnit(context, it, longVolume, MathHelper.round(price))
                    tv_short_value.text = ContractUtils.getVolUnit(context, it, shortVolume, MathHelper.round(price))
                } else {
                    tv_long_value.text = "--"
                    tv_short_value.text = "--"
                }
                //计算预估成本
                val buyCost = calculateAdvanceOpenCost(vol, true)
                val sellCost = calculateAdvanceOpenCost(vol, false)
                val dfDefault = NumberUtil.getDecimal(-1)
                if (buyCost == null) {
                    tv_buy_cost.text = " --"
                } else {
                    tv_buy_cost.text = " " + dfDefault.format(MathHelper.round(buyCost.freezAssets, it.value_index)) + " " + it.margin_coin
                }

                if (sellCost == null) {
                    tv_sell_cost.text = " --"
                } else {
                    tv_sell_cost.text = dfDefault.format(MathHelper.round(sellCost.freezAssets, it.value_index)) + " " + it.margin_coin
                }

            }
        }
    }

    /**
     * 计算预估成本
     */
    fun calculateAdvanceOpenCost(vol: String, isBuy: Boolean): AdvanceOpenCost? {
        mContract?.let {
            val contractOrder = ContractOrder()
            contractOrder.instrument_id = it.instrument_id
            contractOrder.leverage = GlobalLeverageUtils.currLeverage
            contractOrder.qty = vol
            contractOrder.position_type = GlobalLeverageUtils.currentPositionType
            if (isBuy) {
                contractOrder.side = if (buyOrSellHelper.tradeType == 0) {
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG //买多
                } else {
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT // 买空
                }
            } else {
                contractOrder.side = if (buyOrSellHelper.tradeType == 0) {
                    ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT // 卖空
                } else {
                    ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG //卖多
                }
            }
            contractOrder.px = getRealPrice()
            if (buyOrSellHelper.priceType == CONTRACT_ORDER_LIMIT) {//限价
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {//市价
                contractOrder.category = ContractOrder.ORDER_CATEGORY_MARKET
            } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_BID_PRICE) {
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_ASK_PRICE) {
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }

            return if (isBuy) {
                ContractCalculate.CalculateAdvanceOpenCost(
                        contractOrder,
                        ContractUserDataAgent.getContractPosition(it.instrument_id, if (buyOrSellHelper.tradeType == 0) ContractPosition.POSITION_TYPE_LONG else ContractPosition.POSITION_TYPE_SHORT),
                        ContractUserDataAgent.getContractOrderSize(it.instrument_id, if (buyOrSellHelper.tradeType == 0) ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG else ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT),
                        it)
            } else {
                ContractCalculate.CalculateAdvanceOpenCost(
                        contractOrder,
                        ContractUserDataAgent.getContractPosition(it.instrument_id, if (buyOrSellHelper.tradeType == 0) ContractPosition.POSITION_TYPE_SHORT else ContractPosition.POSITION_TYPE_LONG),
                        ContractUserDataAgent.getContractOrderSize(it.instrument_id, if (buyOrSellHelper.tradeType == 0) ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT else ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG),
                        it)
            }
        }
        return null
    }

    /**
     * 更新合约Ticker
     */
    fun updateContractTicker(ticker: ContractTicker, updatePrice: Boolean = true) {
        if (isHidden) {
            return
        }
        mContract?.let {
            if (it.instrument_id != ticker.instrument_id) {
                return
            }
            val dfPrice = NumberUtil.getDecimal(it.price_index)
            val dfprice1 = NumberUtil.getDecimal(it.price_index - 1)
            var lastPx = dfPrice.format(MathHelper.round(ticker.last_px))
            if (!TextUtils.equals(lastPx, lastPrice)) {
                lastPrice = lastPx
                //如果是市价，需重新计算可开数量
                if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                    updateAvailableVol()
                }
            }
            val fairPx = dfPrice.format(MathHelper.round(ticker.fair_px))
            if (!TextUtils.equals(tagPrice, fairPx)) {
                tagPrice = fairPx
                buyOrSellHelper.tagPrice = tagPrice
                //合理价格改变 重新计算可用
                updateUserAssetUI()
            }

            indexPrice = dfPrice.format(MathHelper.round(ticker.index_px))
            val current: Double = MathHelper.round(ticker.last_px, it.price_index)

            //指数价格
            tv_index_price.text = indexPrice
            //合理价格
            tv_fair_price_value.text = tagPrice
            //资金费率
            val rate = MathHelper.mul(ticker.funding_rate, "100")
            iv_funds_rate_value.text = NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)).toString() + "%"
            if (updatePrice) {
                et_price.setText(dfprice1.format(current))
            }
            //展示币种类型
            val unit = LogicContractSetting.getContractUint(ContractSDKAgent.context)
            tv_amount.text = getLineText("sl_str_amount") + "(" + (if (unit == 0) getLineText("sl_str_contracts_unit") else it.base_coin) + ")"
        }
    }

    /**
     * 更新资产
     */
    fun updateUserAssetUI() {
        if (mContract == null || tv_aavl_value == null) {
            return
        }
        val marginCoin = mContract!!.margin_coin
        val contractAccount = ContractUserDataAgent.getContractAccount(marginCoin)
        if (contractAccount == null) {
            tv_aavl_value.text = "0 $marginCoin"
            return
        }
        val dfValue = NumberUtil.getDecimal(mContract!!.value_index)
        val available = contractAccount.available_vol_real
        tv_aavl_value.text = dfValue.format(available) + " " + marginCoin
    }

    /**
     * 平仓 杠杆颜色置灰
     */
    private fun resetLeverTextColor() {
        if (buyOrSellHelper.tradeType == 1) {//平仓
            val color = resources.getColor(R.color.normal_text_color)
            tv_lever_title.textColor = color
            tv_lever.textColor = color
        } else {
            val color = resources.getColor(R.color.text_color)
            tv_lever_title.textColor = color
            tv_lever.textColor = color
        }
    }

    /**
     * 更新买入卖出按钮UI
     */
    fun updateBtnUI() {
        if (UserDataService.getInstance().isLogined) {
            if (buyOrSellHelper.tradeType == 0) {
                stv_sell.textContent = "<font> ${getLineText("contract_sell_openLess2")} <small>${getLineText("contract_sell_openLess_tip")}</small> </font>"
                stv_buy.textContent = "<font> ${getLineText("contract_buy_openMore2")} <small>${getLineText("contract_buy_openMore_tip")}</small> </font>"
            } else {
                stv_sell.textContent = "<font> ${getLineText("contract_sell_closeMore2")} <small>${getLineText("contract_sell_closeMore_tip")}</small> </font>"
                stv_buy.textContent = "<font> ${getLineText("contract_buy_closeLess2")} <small>${getLineText("contract_buy_closeLess_tip")}</small> </font>"
            }
        } else {
            val textLogin = getLineText("sl_str_login_register")
            stv_sell.textContent = textLogin
            stv_buy.textContent = textLogin

            tv_long_value.text = "--"
            tv_short_value.text = "--"
            tv_long_value2.text = "--"
            tv_short_value2.text = "--"
            tv_aavl_value.text = "--"
            //退出登录时。如果当前在仓位页面，则调整出去
            if (tabEntrustIndex == 1) {
                tabEntrustIndex = 0
                showFragment()
            }
        }
    }

    companion object {
        /**
         * 限价
         */
        const val CONTRACT_ORDER_LIMIT = 0

        /**
         * 市价
         */
        const val CONTRACT_ORDER_MARKET = 1

        /**
         * 计划
         */
        const val CONTRACT_ORDER_PLAN = 2

        /**
         * 买一价
         */
        const val CONTRACT_ORDER_BID_PRICE = 3

        /**
         * 卖一价
         */
        const val CONTRACT_ORDER_ASK_PRICE = 4

        /**
         * 限价(高级委托)
         */
        const val CONTRACT_ORDER_ADVANCED_LIMIT = 5
    }

    override fun onBuySellContractVolClick(depthData: DepthData?, showVol: String?, flag: Int) {
        if (buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {
            return
        }
        mContract?.let {
            if (depthData != null) {
                val dfVol = NumberUtil.getDecimal(it.vol_index)
                et_position.setText(dfVol.format(depthData.vol.toString()))
            }

        }
    }

    override fun onBuySellContractClick(depthData: DepthData?, showVol: String?, flag: Int) {
        if (buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {
            mContract?.let {
                if (depthData != null && depthData.price > "0") {
                    val dfPrice = NumberUtil.getDecimal(it.price_index - 1)
                    val price = MathHelper.round(depthData.price)
                    et_trigger_price.setText(dfPrice.format(price))
                }
            }
        } else {
            mContract?.let {
                if (depthData != null && depthData.price > "0") {
                    val dfPrice = NumberUtil.getDecimal(it.price_index - 1)
                    val price = MathHelper.round(depthData.price)
                    et_price.setText(dfPrice.format(price))
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        when (messageEvent.msg_type) {
            MessageEvent.sl_contract_select_leverage_event -> {
                if (messageEvent.msg_content != null && messageEvent.msg_content is HashMap<*, *>) {
                    val map = messageEvent.msg_content as HashMap<String, Int>
                    GlobalLeverageUtils.currLeverage = map["leverage"]!!
                    GlobalLeverageUtils.currentPositionType = map["leverageType"]!!
                    mContract?.let {
                        if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                            GlobalLeverageUtils.uploadLeverage(it.instrument_id, GlobalLeverageUtils.currLeverage, GlobalLeverageUtils.currentPositionType)
                        } else {
                            GlobalLeverageUtils.saveLeverage()
                        }
                    }
                    updateLeverUI()
                    updateAvailableVol()
                }
            }
        }
    }

    private fun showFragment() {
        val transaction = childFragmentManager.beginTransaction()
        currentFragment = if (tabEntrustIndex == 0) {
            if (!priceEntrustFragment.isAdded) {
                transaction.hide(currentFragment).add(R.id.item_fragment_container, priceEntrustFragment, tabEntrustIndex.toString())
            } else {
                transaction.hide(currentFragment).show(priceEntrustFragment)
            }
            priceEntrustFragment
        } else {
            if (!planEntrustFragment.isAdded) {
                transaction.hide(currentFragment).add(R.id.item_fragment_container, planEntrustFragment, tabEntrustIndex.toString())
            } else {
                transaction.hide(currentFragment).show(planEntrustFragment)
            }
            planEntrustFragment
        }
        transaction.commitNow()
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (editable === et_trigger_price.editableText) {
                updateTriggerPrice()
            }
            if (editable === et_price.editableText) {
                updatePrice()
            } else if (editable === et_position.editableText) {
                updateVol()
            }
        }
    }

    private fun updateTriggerPrice() {
        mContract?.let {
            var price: String = et_trigger_price.text.toString()
            price = price.replace(",", ".")

            val priceUnit: String = it.px_unit
            if (priceUnit.contains(".")) {
                var price_index = priceUnit.length - priceUnit.indexOf(".") - 1
                if (price_index == 1) {
                    price_index = 0
                }
                if (price.contains(".")) {
                    val index = price.indexOf(".")
                    if (index + price_index < price.length) {
                        price = price.substring(0, index + price_index)
                        et_trigger_price.setText(price)
                        et_trigger_price.setSelection(price.length)
                    }
                }
            } else {
                if (price.contains(".")) {
                    price = price.replace(".", "")
                    et_trigger_price.setText(price)
                    et_trigger_price.setSelection(price.length)
                }
            }

            updateAvailableVol()

        }
    }


    /**
     * 更新价格
     */
    private fun updatePrice() {
        var price: String = et_price.text.toString()
        if (TextUtils.isEmpty(price)) {
            //et_price.setText("0")
            mContract?.let {
                tv_price_value.text = RateManager.getCNYByCoinName(it.quote_coin, "0", precision = 2)
            }
        }
        mContract?.let { contract ->
            val priceUnit: String? = contract.px_unit
            priceUnit?.let {
                if (it.contains(".")) {
                    var priceIndex = priceUnit.length - priceUnit.indexOf(".") - 1
                    if (priceIndex == 1) {
                        priceIndex = 0
                    }
                    if (price.contains(".")) {
                        val index = price.indexOf(".")
                        if (index + priceIndex < price.length) {
                            price = price.substring(0, index + priceIndex)
                            et_price.setText(price)
                            et_price.setSelection(price.length)
                        }
                    }
                } else {
                    if (price.contains(".")) {
                        price = price.replace(".", "")
                        et_price.setText(price)
                        et_price.setSelection(price.length)
                    }
                }
            }

            if (price == "." || TextUtils.isEmpty(price)) {
                price = "0"
            }
            tv_price_value.text = RateManager.getCNYByCoinName(contract.quote_coin, price, precision = 2)

            updateAvailableVol()
        }
    }

    private lateinit var vol: String

    private fun doBuyOrSell() {
        mContract?.let { contract ->
            val etPrice = getRealPrice()
            val etPosition = et_position.textNull2Zero()
            buyOrSellHelper.etPosition = etPosition
            buyOrSellHelper.etPrice = etPrice
            val triggerPrice = et_trigger_price.textNull2Zero()
            if (MathHelper.round(etPrice) <= 0 && buyOrSellHelper.priceType == CONTRACT_ORDER_LIMIT) {
                NToastUtil.showToast(getLineText("sl_str_price_too_low"), false)
                return
            }
            if (MathHelper.round(triggerPrice) <= 0 && buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {//计划委托
                NToastUtil.showToast(getLineText("sl_str_price_too_low"), false)
                return
            }
            vol = ContractCalculate.trans2ContractVol(contract, etPosition, etPrice, LogicContractSetting.getContractUint(ContractSDKAgent.context))
            if (MathHelper.round(vol) <= 0.0) {
                NToastUtil.showToast(getLineText("sl_str_volume_too_low"), false)
                return
            }
            //开仓 校验下最小最大订单量
            if (buyOrSellHelper.tradeType == 0) {
                if (vol.toDouble() < contract.min_qty.toDouble()) {
                    NToastUtil.showToast(getLineText("sl_str_volume_too_low"), false)
                    return
                }
                if (vol.toDouble() > contract.max_qty.toDouble()) {
                    NToastUtil.showToast(String.format(getLineText("sl_str_book_order_tips"), contract.max_qty), false)
                    return
                }
            }
            val marginCoin = contract.margin_coin
            val contractAccount = ContractUserDataAgent.getContractAccount(marginCoin)
            if (contractAccount == null) {
                val contractFragment = (parentFragment as SlContractFragment)
                contractFragment.hasShowCreateContractDialog = false
                contractFragment.createContractAccount()
                return
            }
            //开仓并且资金不足，跳转到划转页面
            if (buyOrSellHelper.tradeType == 0 && contractAccount.available_vol_real == 0.0) {
                NewDialogUtils.showDialog(mActivity!!, getLineText("sl_str_contract_no_asset_tips"), false, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        ArouterUtil.navigation(RoutePath.NewVersionTransferActivity, Bundle().apply {
                            putString(ParamConstant.TRANSFERSTATUS, ParamConstant.TRANSFER_CONTRACT)
                            putString(ParamConstant.TRANSFERSYMBOL, "USDT")
                        })
                    }

                }, getLineText("sl_str_Insufficient_funds"), getLineText("sl_str_Immediately_transfer"), "")
                return
            }
            LogUtil.d("DEBUG", "priceType:${buyOrSellHelper.priceType}")
            val cAccount = ContractUserDataAgent.getContractAccount(contract.margin_coin)
            if (cAccount == null) {
                val tips = java.lang.String.format(getLineText("sl_str_no_contract_account"), contract.margin_coin)
                NToastUtil.showToast(tips, false)
                return
            }
            val dfDefault = NumberUtil.getDecimal(-1)
            val accountMode = GlobalLeverageUtils.currentPositionType
            val mode = if (accountMode == 1) getLineText("sl_str_gradually_position") else getLineText("sl_str_full_position")
            val leverage = mode + GlobalLeverageUtils.currLeverage + "X"
            //下单二次确认弹窗开关
            val tradeConfirm = PreferenceManager.getInstance(ContractSDKAgent.context).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true)
            if (buyOrSellHelper.priceType != CONTRACT_ORDER_PLAN) {
                if (!tradeConfirm) {
                    innerBuyOrSell()
                    return
                }
                var warning = ""
                var priceDisplay = ""
                val contractOrder = ContractOrder()
                contractOrder.instrument_id = contract.instrument_id
                contractOrder.leverage = GlobalLeverageUtils.currLeverage
                contractOrder.qty = vol
                contractOrder.position_type = accountMode
                if (buyOrSellHelper.isBuy) {
                    contractOrder.side = if (buyOrSellHelper.tradeType == 0) {
                        ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
                    } else {
                        ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
                    }
                } else {
                    contractOrder.side = if (buyOrSellHelper.tradeType == 0) {
                        ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT
                    } else {
                        ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
                    }
                }
                contractOrder.px = getRealPrice()
                if (buyOrSellHelper.priceType == CONTRACT_ORDER_LIMIT) {//限价
                    priceDisplay = dfDefault.format(MathHelper.round(etPrice, contract.price_index))
                    contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
                    val tipLimit: Double = if (buyOrSellHelper.isBuy) {
                        MathHelper.div(MathHelper.sub(etPrice, lastPrice), MathHelper.round(lastPrice))
                    } else {
                        MathHelper.div(MathHelper.sub(lastPrice, etPrice), MathHelper.round(lastPrice))
                    }
                    if (0.05 < tipLimit) {
                        warning = if (buyOrSellHelper.tradeType == 0) getLineText("sl_str_open_risk_tips") else getLineText("sl_str_close_risk_tips")
                    }
                } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {//市价
                    priceDisplay = getLineText("sl_str_market_price")
                    contractOrder.category = ContractOrder.ORDER_CATEGORY_MARKET
                    val depthDataList = if (buyOrSellHelper.isBuy) {
                        ll_buy_layout.getList()
                    } else {
                        ll_sell_Layout.getList()
                    }
                    val avgPrice = ContractCalculate.CalculateMarketAvgPrice(vol, depthDataList, !buyOrSellHelper.isBuy)
                    val tipLimit: Double = if (buyOrSellHelper.isBuy) {
                        MathHelper.div(MathHelper.sub(avgPrice, MathHelper.round(lastPrice)), MathHelper.round(lastPrice))
                    } else {
                        MathHelper.div(MathHelper.sub(MathHelper.round(lastPrice), avgPrice), MathHelper.round(lastPrice))
                    }
                    if (0.03 < tipLimit) {
                        warning = if (buyOrSellHelper.tradeType == 0) getLineText("sl_str_open_market_risk_tips") else getLineText("sl_str_close_market_risk_tips")
                    }
                } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_BID_PRICE) {
                    priceDisplay = getLineText("sl_str_buy1_price")
                    contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
                } else if (buyOrSellHelper.priceType == CONTRACT_ORDER_ASK_PRICE) {
                    priceDisplay = getLineText("sl_str_sell1_price")
                    contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
                }
                buyOrSellHelper.priceDisplay = priceDisplay
                if (buyOrSellHelper.tradeType == 0) {
                    //警告弹窗
                    buyOrSellHelper.contractOrder = contractOrder
                    val window = ContractTradeConfirmWindow(activity!!, buyOrSellHelper)
                    if (!TextUtils.isEmpty(warning)) {
                        window.showWarning(warning)
                    }
                    window.setOkListener {
                        innerBuyOrSell(contractOrder)
                        window.dismiss()
                    }
                    window.showAtLocation(stv_buy, Gravity.CENTER, 0, 0)
                    KeyBoardUtils.closeKeyBoard(activity!!)
                } else {
                    val title = if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                        "contract_action_marketClosing".localized()
                    } else {
                        "contract_text_limitPositions".localized()
                    }
                    val tt = if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                        "contract_action_marketPrice".localized()
                    } else {
                        "contract_action_limitPrice".localized()
                    }
                    val side: String = if (buyOrSellHelper.isBuy) {
                        "contract_action_buy".localized()
                    } else {
                        "contract_action_sell".localized()
                    }
                    val content = if (buyOrSellHelper.priceType == CONTRACT_ORDER_MARKET) {
                        tt + side + vol + "sl_str_contracts_unit".localized() + contract.base_coin + contract.margin_coin + "trade_contract_title".localized()
                    } else {
                        tt + etPrice + contract.margin_coin + side + vol + "sl_str_contracts_unit".localized() + contract.base_coin + contract.margin_coin + "trade_contract_title".localized()
                    }
                    ContractDialogUtils.showNormalDialog(activity, title, content = content, submitListener = { innerBuyOrSell() }, warnContent = warning)
                }
            } else {//计划
                if (!tradeConfirm) {
                    innerBuyOrSell()
                    return
                }
                val triggerType = LogicContractSetting.getTriggerPriceType(ContractSDKAgent.context)
                var triggerTypeText = ""
                when (triggerType) {
                    1 -> {
                        triggerTypeText = getLineText("sl_str_latest_price")
                    }
                    2 -> {
                        triggerTypeText = getLineText("sl_str_fair_price")
                    }
                    4 -> {
                        triggerTypeText = getLineText("sl_str_index_price")
                    }
                }
                val effect = LogicContractSetting.getStrategyEffectTime(ContractSDKAgent.context)
                val titleColor = if (buyOrSellHelper.isBuy) {
                    resources.getColor(R.color.main_green)
                } else {
                    resources.getColor(R.color.main_red)
                }
                val title = if (buyOrSellHelper.isBuy) {
                    if (buyOrSellHelper.tradeType == 0) getLineText("sl_str_buy_open") + getLineText("sl_str_plan") else getLineText("sl_str_buy_close") + getLineText("sl_str_plan")
                } else {
                    if (buyOrSellHelper.tradeType == 0) getLineText("sl_str_sell_open") + getLineText("sl_str_plan") else getLineText("sl_str_sell_close") + getLineText("sl_str_plan")
                }
                SlDialogHelper.showPlanTreadConfirmDialog(mActivity!!, titleColor, title, contract.symbol, dfDefault.format(MathHelper.round(triggerPrice, contract.price_index)),
                        if (LogicContractSetting.getExecution(ContractSDKAgent.context) == 1) getLineText("contract_action_marketPrice") else dfDefault.format(MathHelper.round(etPrice, contract.price_index)),
                        vol,
                        leverage, triggerTypeText, if (effect == 0) getLineText("sl_str_in_24_hours") else getLineText("sl_str_in_7_days"),
                        object : NewDialogUtils.DialogBottomListener {
                            override fun sendConfirm() {
                                innerBuyOrSell()
                            }
                        })
            }
        }
    }

    private fun innerBuyOrSell(contractOrder: ContractOrder? = null) {
        mContract?.let { contract ->
            val etPrice = et_price.textNull2Zero()
            val order = contractOrder ?: ContractOrder()
            order.instrument_id = contract.instrument_id
            order.nonce = System.currentTimeMillis() / 1000
            order.qty = vol
            if (buyOrSellHelper.tradeType == 0) {//开仓
                order.position_type = GlobalLeverageUtils.currentPositionType
                order.leverage = GlobalLeverageUtils.currLeverage
                order.side = if (buyOrSellHelper.isBuy) {
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
                } else {
                    ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT
                }
            } else {//平仓
                val shortPosition = ContractUserDataAgent.getContractPosition(order.instrument_id, if (buyOrSellHelper.isBuy) ContractPosition.POSITION_TYPE_SHORT else ContractPosition.POSITION_TYPE_LONG)
                if (shortPosition != null) {
                    order.pid = shortPosition.pid
                }
                order.side = if (buyOrSellHelper.isBuy) {
                    ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
                } else {
                    ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
                }
            }
            when (buyOrSellHelper.priceType) {
                CONTRACT_ORDER_LIMIT -> {
                    order.px = etPrice
                    if (isAdvancedLimit) {
                        ///time_in_force若为1(post only) category 传7,其他情况category传1(限价)
                        order.time_in_force = currAdvancedLimit
                        if (currAdvancedLimit == 1) {
                            order.category = ContractOrder.ORDER_CATEGORY_ADVANCED_NORMAL
                        } else {
                            order.category = ContractOrder.ORDER_CATEGORY_NORMAL
                        }
                    } else {
                        order.category = ContractOrder.ORDER_CATEGORY_NORMAL
                    }
                }
                CONTRACT_ORDER_ASK_PRICE -> {
                    val list = ll_sell_Layout.getList()
                    if (list != null && list.isNotEmpty()) {
                        order.px = list[0].price
                    } else {
                        order.px = etPrice
                    }
                    if (isAdvancedLimit) {
                        order.category = ContractOrder.ORDER_CATEGORY_ADVANCED_NORMAL
                        order.time_in_force = currAdvancedLimit
                    } else {
                        order.category = ContractOrder.ORDER_CATEGORY_NORMAL
                    }
                }
                CONTRACT_ORDER_BID_PRICE -> {
                    val list = ll_buy_layout.getList()
                    if (list != null && list.size > 0) {
                        order.px = list[0].price
                    } else {
                        order.px = etPrice
                    }
                    if (isAdvancedLimit) {
                        order.category = ContractOrder.ORDER_CATEGORY_ADVANCED_NORMAL
                        order.time_in_force = currAdvancedLimit
                    } else {
                        order.category = ContractOrder.ORDER_CATEGORY_NORMAL
                    }
                }
                CONTRACT_ORDER_MARKET -> {
                    if (isAdvancedLimit) {
                        order.category = ContractOrder.ORDER_CATEGORY_ADVANCED_NORMAL
                        order.time_in_force = currAdvancedLimit
                    } else {
                        order.category = ContractOrder.ORDER_CATEGORY_MARKET
                    }
                }
                CONTRACT_ORDER_PLAN -> {
                    val triggerPrice = if (TextUtils.isEmpty(et_trigger_price.text.toString())) "0" else et_trigger_price.text.toString()
                    order.px = triggerPrice
                    if (LogicContractSetting.getExecution(ContractSDKAgent.context) == 0) {
                        order.category = ContractOrder.ORDER_CATEGORY_NORMAL
                        order.exec_px = etPrice
                    } else {
                        order.category = ContractOrder.ORDER_CATEGORY_MARKET
                        //市价单 不传执行价格
                        // order.exec_px = price
                    }
                    val triggerType = LogicContractSetting.getTriggerPriceType(ContractSDKAgent.context)
                    when (triggerType) {
                        1 -> {
                            if (MathHelper.round(lastPrice) > MathHelper.round(triggerPrice)) {
                                order.trend = 2
                            } else {
                                order.trend = 1
                            }
                        }
                        2 -> {
                            if (MathHelper.round(tagPrice) > MathHelper.round(triggerPrice)) {
                                order.trend = 2
                            } else {
                                order.trend = 1
                            }
                        }
                        4 -> {
                            if (MathHelper.round(indexPrice) > MathHelper.round(triggerPrice)) {
                                order.trend = 2
                            } else {
                                order.trend = 1
                            }
                        }
                    }
                    order.trigger_type = triggerType
                    val effect = LogicContractSetting.getStrategyEffectTime(ContractSDKAgent.context)
                    order.life_cycle = if (effect == 0) 24 else 168
                }
            }

            val response: IResponse<String> = object : IResponse<String>() {
                override fun onSuccess(data: String) {
                    if (buyOrSellHelper.isBuy) {
                        stv_buy.hideLoading()
                    } else {
                        stv_sell.hideLoading()
                    }
                    NToastUtil.showToast(getLineText("contract_tip_submitSuccess"), false)
                }

                override fun onFail(code: String, msg: String) {
                    if (buyOrSellHelper.isBuy) {
                        stv_buy.hideLoading()
                    } else {
                        stv_sell.hideLoading()
                    }
                    if (code == "CONTRACT_LEVERAGE_MATCH_ERROR" || code == "LEVERAGE_MATCH_ERROR") {
                        GlobalLeverageUtils.loadLeverage()
                    }
                    NToastUtil.showToast(msg, false)
                }

            }
            if (buyOrSellHelper.isBuy) {
                stv_buy.showLoading()
            } else {
                stv_sell.showLoading()
            }
            if (buyOrSellHelper.priceType == CONTRACT_ORDER_PLAN) {
                ContractUserDataAgent.doSubmitPlanOrder(order, response)
            } else {
                ContractUserDataAgent.doSubmitOrder(order, response)
            }
        }
    }

    fun updateDepth(isBuy: Boolean, list: List<DepthData>?) {
        if (isBuy) {
            ll_buy_layout?.initData(list)
        } else {
            ll_sell_Layout?.initData(list)
        }
    }

    override fun onContractSettingChange() {
        updateOrderType(true)
        setVolUnit()
    }

    private fun setVolUnit() {
        val unit: Int = LogicContractSetting.getContractUint(ContractSDKAgent.context)
        tv_volume_unit.text = if (unit == 0) getLineText("sl_str_contracts_unit") else mContract?.base_coin
        tv_coin_name.text = mContract?.quote_coin
        tv_trigger_coin_name.text = mContract?.quote_coin
        updateVol()
    }

    /**
     * 更新数量
     */
    private fun updateVol() {
        var vol: String = et_position.text.toString()
        vol = vol.replace(",", ".")
        if (TextUtils.isEmpty(vol)) {
            mContract?.let { it ->
                val value: String = ContractUtils.CalculateContractBasicValue(
                        ContractCalculate.trans2ContractVol(it, "0", "0", LogicContractSetting.getContractUint(ContractSDKAgent.context)),
                        "0",
                        it)
                tv_volume_value.text = "≈ $value"
            }
        }
        mContract?.let { contract ->
            val unit: Int = LogicContractSetting.getContractUint(ContractSDKAgent.context)
            if (unit == 0) {
                val volUnit = contract.qty_unit
                if (volUnit.contains(".")) {
                    val volIndex = volUnit.length - volUnit.indexOf(".")
                    if (vol.contains(".")) {
                        val index = vol.indexOf(".")
                        if (index + volIndex < vol.length) {
                            vol = vol.substring(0, index + volIndex)
                            et_position.setText(vol)
                            et_position.setSelection(vol.length)
                        }
                    }
                } else {
                    if (vol.contains(".")) {
                        vol = vol.replace(".", "")
                        et_position.setText(vol)
                        et_position.setSelection(vol.length)
                    }
                }
            } else {
                var baseCoinUnit = "0.0001"
                val valueUnit = contract.value_unit
                if (!TextUtils.isEmpty(valueUnit)) {
                    baseCoinUnit = valueUnit
                }
                if (baseCoinUnit.contains(".")) {
                    val volIndex = baseCoinUnit.length - baseCoinUnit.indexOf(".")
                    if (vol.contains(".")) {
                        val index = vol.indexOf(".")
                        if (index + volIndex < vol.length) {
                            vol = vol.substring(0, index + volIndex)
                            et_position.setText(vol)
                            et_position.setSelection(vol.length)
                        }
                    }
                } else {
                    if (vol.contains(".")) {
                        vol = vol.replace(".", "")
                        et_position.setText(vol)
                        et_position.setSelection(vol.length)
                    }
                }
            }

            updateAvailableVol()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LogicContractSetting.getInstance().unregistListener(this)
    }

    override fun doThing(obj: Any?): Boolean {
        if (obj == "updateLeverage") {
            updateLeverUI()
        }
        return true
    }

}