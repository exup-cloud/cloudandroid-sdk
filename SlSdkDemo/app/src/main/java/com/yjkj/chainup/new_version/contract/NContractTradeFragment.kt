package com.yjkj.chainup.new_version.contract


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fengniao.news.util.JsonUtils
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.base.BindViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.bean.QuotesData
import com.yjkj.chainup.bean.TransactionData
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NContractCurrentEntrustAdapter
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_contract.*
import kotlinx.android.synthetic.main.fragment_contract_trade.*
import kotlinx.android.synthetic.main.item_depth_contract.view.*
import kotlinx.android.synthetic.main.layout_contract_trade.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.math.BigInteger
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * @author Bertking
 * @description 合约的"交易"(4.0)
 * @date 2019-9-12
 */
class NContractTradeFragment : NBaseFragment() {
    override fun setContentView() = R.layout.fragment_contract_trade

    /**
     * 标记
     */
    private var isEditMode = false


    var dialog: TDialog? = null

    private lateinit var socketClient: WebSocketClient
    private var currentSymbol = ""
    private var lastSymbol = ""

    private var contractId = 3
    var currentContract: ContractBean? = null
    private var currentLevel = ""
    var initOrderPrice = ""

    //价格类型
    var priceType = CONTRACT_LIMIT

    var itemWidth = 1


    /**
     * 活动委托的adapter
     */
    var adapter = NContractCurrentEntrustAdapter(arrayListOf())

    /**
     * 卖盘的item
     */
    private var sellViewList = mutableListOf<View>()
    /**
     * 买盘的item
     */
    private var buyViewList = mutableListOf<View>()

    var tapeLevel: Int = 0

    var tapeDialog: TDialog? = null

    override fun initView() {
        contractId = Contract2PublicInfoManager.currentContractId()
        currentContract = Contract2PublicInfoManager.currentContract("") ?: return
        tv_tape_amount_title?.text =  LanguageUtil.getString(context,"charge_text_volume") + "(${ LanguageUtil.getString(context,"contract_text_volumeUnit")})"

        rv_current_entrust?.setHasFixedSize(true)
        rv_current_entrust?.layoutManager = LinearLayoutManager(context)
        adapter.bindToRecyclerView(rv_current_entrust ?: return)
        rv_current_entrust?.adapter = adapter
        adapter.setEmptyView(EmptyForAdapterView(context?: return))
        adapter.setOnItemChildClickListener { adapter, view, position ->
            if (adapter?.data?.isNotEmpty() == true) {
                try {
                    val item = adapter.data[position] as JSONObject
                    val status = item.optString("status")
                    val orderId = item.optString("orderId")
                    val contractId = item.optString("contractId")

                    when (status) {
                        "0", "1", "3" -> {
                            cancelOrder(orderId, contractId, position)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * 全部委托
         */
        ll_all_entrust_order?.setOnClickListener {
            if (!LoginManager.checkLogin(activity, true)) return@setOnClickListener
            val bundle = Bundle().apply {
                putString("contractId", contractId.toString())
            }
            ArouterUtil.navigation("/contract/ContractCurrentEntrustActivity", bundle)
        }

        initDepthView()

        stv_sell?.run {
            solid = ColorUtil.getMainColorType(false)
            text =  LanguageUtil.getString(context,"contract_action_short")
        }

        stv_buy?.run {
            solid = ColorUtil.getMainColorType()
            text =  LanguageUtil.getString(context,"contract_action_long")
        }

        tv_lever?.text = "${currentContract?.maxLeverageLevel}X"

        /**
         * 选择杠杆
         */
        ll_lever?.setOnClickListener {
            if (!LoginManager.checkLogin(context!!, true)) return@setOnClickListener

            if (TextUtils.isEmpty(currentLevel)) currentLevel = currentContract?.maxLeverageLevel.toString()

            AdjustLeverUtil(context!!, contractId, currentLevel, object : AdjustLeverUtil.AdjustLeverListener {
                override fun adjustSuccess(value: String) {
                    currentLevel = value
                    tv_lever?.text = currentLevel + "X"
                    Log.d(TAG, "==当前杠杆==$currentLevel")
                    getInitOrderInfo(et_position?.text.toString(), currentLevel)
                }

                override fun adjustFailed(msg: String) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView, msg, false)
                }

            })

        }


        /**
         * 限价 & 市价
         */
        tv_order_type?.setOnClickListener {
            dialog = NewDialogUtils.showBottomListDialog(context!!, arrayListOf(LanguageUtil.getString(context,"contract_action_limitPrice"), LanguageUtil.getString(context,"contract_action_marketPrice")), priceType - 1, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    tv_order_type?.text = data[item]
                    dialog?.dismiss()
                    when (item) {
                        0 -> {
                            priceType = CONTRACT_LIMIT
                            v_market_trade_tip?.visibility = View.GONE
                            ll_price?.visibility = View.VISIBLE
                        }
                        1 -> {
                            priceType = CONTRACT_MARKET
                            v_market_trade_tip?.visibility = View.VISIBLE
                            ll_price?.visibility = View.GONE
                        }
                    }
                    getInitOrderInfo(lever = currentLevel)
                }
            })
        }


        tv_coin_name?.text = currentContract?.quoteSymbol

        et_price?.setOnClickListener {
            LoginManager.checkLogin(activity, true)
        }
        et_position?.setOnClickListener {
            LoginManager.checkLogin(activity, true)
        }

        /**
         * 监听仓位输入框
         */

        et_position?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getInitOrderInfo(s.toString(), currentLevel)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })


        /**
         * 监听限价输入框
         */
        et_price?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getInitOrderInfo(et_position?.text.toString(), currentLevel)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isEditMode = true
            }

        })


        /**
         * 下单("买入/做多")
         * 如果是"市价单",「价格」使用 init_take_order接口返回的price字段
         */
        stv_buy?.setOnClickListener {
            if (!LoginManager.checkLogin(activity, true)) return@setOnClickListener
            if (priceType == CONTRACT_LIMIT) {
                if (TextUtils.isEmpty(et_position?.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,"contract_tip_pleaseInputPosition"), isSuc = false)
                    return@setOnClickListener
                }

                if (TextUtils.isEmpty(et_price?.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,"contract_tip_pleaseInputPrice"), isSuc = false)
                    return@setOnClickListener
                }
                // 限制仓位最大数量
                if (BigDecimalUtils.compareTo(et_position?.text.toString(), currentContract?.maxOrderVolume.toString()) > 0) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_max_volume").format(currentContract?.maxOrderVolume.toString())
                            , isSuc = false)
                    return@setOnClickListener
                }

                getInitOrderInfo(lever = currentLevel, isShowDialog = true, isBuy = true)
            } else {
                if (TextUtils.isEmpty(et_position.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,"contract_tip_pleaseInputPosition"), isSuc = false)
                    return@setOnClickListener
                }
                // 限制仓位最大数量
                if (BigDecimalUtils.compareTo(et_position?.text.toString(), currentContract?.maxOrderVolume.toString()) > 0) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_max_volume").format(currentContract?.maxOrderVolume.toString())
                            , isSuc = false)
                    return@setOnClickListener
                }

                getInitOrderInfo(lever = currentLevel, isShowDialog = true, isBuy = true)

            }
        }

        /**
         * 下单("卖出/做空")
         * 如果是"市价单",「价格」使用 init_take_order接口返回的price字段
         */
        stv_sell?.setOnClickListener {
            if (!LoginManager.checkLogin(activity, true)) return@setOnClickListener
            if (priceType == CONTRACT_LIMIT) {
                if (TextUtils.isEmpty(et_position?.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_tip_pleaseInputPosition"), isSuc = false)
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(et_price?.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_tip_pleaseInputPrice"), isSuc = false)
                    return@setOnClickListener
                }
                // 限制仓位最大数量
                if (BigDecimalUtils.compareTo(et_position?.text.toString(), currentContract?.maxOrderVolume.toString()) > 0) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_max_volume").format(currentContract?.maxOrderVolume.toString())
                            , isSuc = false)
                    return@setOnClickListener
                }
                getInitOrderInfo(lever = currentLevel, isShowDialog = true, isBuy = false)

            } else {
                if (TextUtils.isEmpty(et_position.text)) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_tip_pleaseInputPosition"), isSuc = false)
                    return@setOnClickListener
                }
                // 限制仓位最大数量
                if (BigDecimalUtils.compareTo(et_position?.text.toString(), currentContract?.maxOrderVolume.toString()) > 0) {
                    DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_max_volume").format(currentContract?.maxOrderVolume.toString())
                            , isSuc = false)
                    return@setOnClickListener
                }
                getInitOrderInfo(lever = currentLevel, isShowDialog = true, isBuy = false)

            }
        }

        /**
         * 风险提示
         */
        csrv_risk?.setOnClickListener {
            NewDialogUtils.showSingleDialog(context!!,  LanguageUtil.getString(context,"contract_text_autoReduceDesc"), object : NewDialogUtils.DialogBottomListener {
                override fun sendConfirm() {
                }

            },  LanguageUtil.getString(context,"contract_tip_autoReduce"),  LanguageUtil.getString(context,"alert_common_iknow"))

        }

        /**
         * 改变盘口的样式
         *
         * TODO 和 币币搞成公共的
         */
        ib_tape?.setOnClickListener {
            tapeDialog = NewDialogUtils.showBottomListDialog(context!!, arrayListOf(LanguageUtil.getString(context,"contract_text_defaultMarket"), LanguageUtil.getString(context,"contract_text_buyMarket"), LanguageUtil.getString(context,"contract_text_sellMarket")), tapeLevel, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    tapeDialog?.dismiss()
                    tapeLevel = item
                    when (item) {
                        AppConstant.DEFAULT_TAPE -> {
                            ll_buy_price?.visibility = View.VISIBLE
                            ll_sell_price?.visibility = View.VISIBLE
                            ColorUtil.setTapeIcon(ib_tape, AppConstant.DEFAULT_TAPE)
                            initDepthView()
                        }

                        AppConstant.BUY_TAPE -> {
                            ll_buy_price?.visibility = View.VISIBLE
                            ll_sell_price?.visibility = View.GONE
                            ColorUtil.setTapeIcon(ib_tape, AppConstant.BUY_TAPE)
                            initDepthView(10)
                        }

                        AppConstant.SELL_TAPE -> {
                            ll_buy_price?.visibility = View.GONE
                            ll_sell_price?.visibility = View.VISIBLE
                            ColorUtil.setTapeIcon(ib_tape, AppConstant.SELL_TAPE)
                            initDepthView(10)
                        }
                    }
                    refreshDepthView(transactionData)
                }
            })
        }

        observeContract()
    }


    companion object {
        const val CONTRACT_LIMIT = 1
        const val CONTRACT_MARKET = 2
        /**
         * 买入/做多
         */
        const val SELL_SIDE = "SELL"
        /**
         * 卖出/做空
         */
        const val BUY_SIDE = "BUY"
    }


    /**
     *  下单
     */
    private fun takeOrder(volume: String = "1", price: String, side: String = "BUY") {
        addDisposable(getContractModel().takeOrder4Contract(contractId = contractId.toString()
                , volume = volume,
                price = price,
                orderType = priceType,
                side = side,
                level = currentLevel,
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        et_price?.setText("")
                        DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"contract_tip_submitSuccess"))
                        getOrderList4Contract()
                        getInitOrderInfo(currentLevel)
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        DisplayUtil.showSnackBar(activity?.window?.decorView, msg, isSuc = false)
                    }

                }))
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "=========onFragmentResume==========")
//        getAccount4Contract()
        loopPriceRiskPosition()

        currentSymbol = currentContract?.symbol.toString().toLowerCase()
        tv_coin_name?.text = currentContract?.quoteSymbol
        if (!LoginManager.checkLogin(context, false)) {
            et_price?.isFocusableInTouchMode = false
            et_position?.isFocusableInTouchMode = false
            tv_lever?.text = "${currentContract?.maxLeverageLevel.toString()}X"
        } else {
            Log.d(TAG, "=====456")
            getInitOrderInfo()
            if (et_position?.isFocusableInTouchMode?.not() == true) {
                et_position?.run {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    requestFocus()
                    findFocus()
                }

            }
            if (et_price?.isFocusableInTouchMode?.not() == true) {
                et_price?.run {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    requestFocus()
                    findFocus()
                }

            }
        }

        et_price?.filters = arrayOf(DecimalDigitsInputFilter(currentContract?.pricePrecision
                ?: 4))
//        et_volume?.filters = arrayOf(DecimalDigitsInputFilter(currentContract?.pricePrecision ?: 20))
        loopOrderList4Contract()
        Log.d(TAG, "========Level:${currentLevel}=======")

        Log.d(TAG, "========合约ID:$contractId====")
        initSocket()

    }

    private fun observeContract() {
        ContractFragment.liveData4Contract.observe(this, Observer<ContractBean> {
            isEditMode = false
            if (it == null) return@Observer
            if (contractId != it?.id) {
                contractId = it?.id ?: 3
                lastSymbol = currentSymbol
                currentContract = it
                currentSymbol = it.symbol ?: ""
                /**
                 *  这里将杠杆清空，是因为切换合约时，需要获取服务端的杠杆，不能向后端传
                 */
                /**
                 *  这里将杠杆清空，是因为切换合约时，需要获取服务端的杠杆，不能向后端传
                 */
                currentLevel = ""
                loopOrderList4Contract()
                loopTagPrice()
                loopRiskFactor()
                if (Contract2PublicInfoManager.isPureHoldPosition()) {
                    loopPosition4Contract()
                } else {
                    // 分仓模式下，仓位隐藏
                    cl_position_info?.visibility = View.GONE
                }
                Log.d(TAG, "====123====")
                getInitOrderInfo()
            }
            et_position?.text?.clear()
            et_price?.text?.clear()
            tv_coin_name?.text = currentContract?.quoteSymbol
            subCurrentContractMsg()
        })
    }

    /**
     * 订单初始化信息
     */
    fun getInitOrderInfo(volume: String = et_position?.text.toString(), lever: String = "", isShowDialog: Boolean = false, isBuy: Boolean = true) {
        if (!LoginManager.checkLogin(context!!, false)) return
        val price = if (priceType == CONTRACT_LIMIT) {
            et_price?.text.toString()
        } else {
            ""
        }

        if (!TextUtils.isEmpty(volume) && volume.toBigInteger() == BigInteger.ZERO) return

        addDisposable(getContractModel().getInitTakeOrderInfo4Contract(
                contractId = contractId.toString(),
                volume = volume,
                price = price,
                level = lever,
                orderType = priceType,
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        val data = jsonObject.optJSONObject("data")
                        data?.run {
                            initOrderPrice = optString("price")
                            currentLevel = optString("level")

                            val canUseBalance = optString("canUseBalance")
                            val orderPriceValue = optString("orderPriceValue")
                            val sellOrderCost = optString("sellOrderCost")
                            val buyOrderCost = optString("buyOrderCost")

                            tv_lever?.text = currentLevel + "X"


                            /**
                             * 可用余额
                             */
                            val canUseBalanceByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(canUseBalance.toString())
                            tv_available_balance?.text =
                                     LanguageUtil.getString(context,"withdraw_text_available") + " $canUseBalanceByPrecision BTC"

                            if (priceType == CONTRACT_LIMIT) {
                                if (TextUtils.isEmpty(price) || TextUtils.isEmpty(volume)) {
                                    initCostAndBalance(isHideBalance = false)
                                    return
                                }
                            } else {
                                if (TextUtils.isEmpty(volume)) {
                                    initCostAndBalance(isHideBalance = false)
                                    return
                                }
                            }


                            /**
                             * 委托价值
                             */
                            val orderPriceValueByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(orderPriceValue)
                            tv_entrust_value?.text =  LanguageUtil.getString(context,"contract_text_entrustValue") + " $orderPriceValueByPrecision BTC"

                            /**
                             * 卖出成本
                             */
                            val sellOrderCostByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(sellOrderCost)
                            tv_sell_cost?.text = "$sellOrderCostByPrecision BTC"

                            /**
                             * 买入成本
                             */
                            val buyOrderCostByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(buyOrderCost.toString())
                            tv_buy_cost?.text = "$buyOrderCostByPrecision BTC"

                            if (isShowDialog) {
                                orderDialog(data, orientation = isBuy)
                            }
                        }
                    }
                }))
    }


    /**
     * 每5s调用一次接口
     */
    private fun loopPriceRiskPosition() {
        addDisposable(Observable.interval(0, 5, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()))
    }

    private fun getObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {
            override fun onComplete() {
            }

            override fun onNext(t: Long) {
                loopTagPrice()
                loopRiskFactor()
                if (Contract2PublicInfoManager.isPureHoldPosition()) {
                    loopPosition4Contract()
                } else {
                    // 分仓模式下，仓位隐藏
                    cl_position_info?.visibility = View.GONE
                }
            }

            override fun onError(e: Throwable) {
            }
        }

    }

    /**
     * 轮询标记价格接口
     */
    fun loopTagPrice() {
        addDisposable(getContractModel().getTagPrice4Contract(contractId = contractId.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        jsonObject.optJSONObject("data").run {
                            val indexPrice = optString("indexPrice")
                            val tagPrice = optString("tagPrice")
                            val pricePrecision = currentContract?.pricePrecision ?: 2

                            tv_index_price?.text = DecimalUtil.cutValueByPrecision(indexPrice, pricePrecision)
                            tv_tag_price?.text = DecimalUtil.cutValueByPrecision(tagPrice, pricePrecision)
                        }

                    }
                }))
    }


    /**
     * 轮询风险系数接口
     */
    fun loopRiskFactor() {
        if (!LoginManager.checkLogin(activity, false)) {
            initCostAndBalance()
            return
        }
        addDisposable(getContractModel().getRiskLiquidationRate(contractId = contractId.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        jsonObject.optJSONObject("data").run {
                            val liquidationRate = optString("liquidationRate")
                            csrv_risk?.riskFactor = liquidationRate?.toFloat() ?: 0f
                        }
                    }
                }))
    }

    private fun initCostAndBalance(isHideBalance: Boolean = true) {
        tv_sell_cost?.text = "--"
        tv_buy_cost?.text = "--"
        if (isHideBalance) {
            tv_available_balance?.text = "${ LanguageUtil.getString(context,"withdraw_text_available")} --"
        }
        tv_entrust_value?.text = "${ LanguageUtil.getString(context,"contract_text_entrustValue")} --"
    }

    /**
     * 轮询用户持仓信息
     */
    private fun loopPosition4Contract() {
        if (!LoginManager.checkLogin(activity, false)) {
            cl_position_info?.visibility = View.GONE
            return
        }
        cl_position_info?.visibility = View.VISIBLE
        addDisposable(getContractModel().getPosition4Contract(contractId = contractId.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        data.optJSONObject("data").run {
                            val positions = this.optJSONArray("positions")
                            if (positions != null && positions?.length() != 0) {
                                val position = positions.optJSONObject(0)
                                val unrealisedRateIndex = position.optString("unrealisedRateIndex")
                                val volume = position.optString("volume")
                                val side = position.optString("side")
                                val liquidationPrice = position.optString("liquidationPrice")

                                tv_realised_rate_value?.text = "$unrealisedRateIndex%"

                                /**
                                 * 仓位数量
                                 */
                                tv_position_amount_value?.run {
                                    text = volume
                                    textColor = ColorUtil.getMainColorType(side == "BUY")
                                }

                                /**
                                 * 强平价格
                                 */
                                val liquidationPriceByPrecision = DecimalUtil.cutValueByPrecision(liquidationPrice, currentContract?.pricePrecision
                                        ?: 2)
                                tv_liquidation_price_value?.text = (liquidationPriceByPrecision)
                            } else {
                                tv_realised_rate_value?.text = ("--")
                                tv_position_amount_value?.text = ("--")
                                tv_liquidation_price_value?.text = ("--")
                            }
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        super.onResponseFailure(code, msg)
                        tv_realised_rate_value?.text = ("--")
                        tv_position_amount_value?.text = ("--")
                        tv_liquidation_price_value?.text = ("--")
                    }

                }))
    }

    /**
     * 下单前的确认框
     * @param orientation 买卖方向 true-BUY ; false-SELL
     */
    fun orderDialog(jsonObject: JSONObject?, orientation: Boolean = true) {
        TDialog.Builder(childFragmentManager)
                .setLayoutRes(R.layout.dialog_contract_take_order)
                .setScreenWidthAspect(context, 0.8f)
                .setGravity(Gravity.CENTER)
                .setDimAmount(0.8f)
                .setCancelableOutside(false)
                .setOnBindViewListener { viewHolder: BindViewHolder? ->

                    viewHolder?.run {
                        getView<TextView>(R.id.tv_title)?.text =
                                if (orientation) {
                                    if (priceType == CONTRACT_LIMIT)  LanguageUtil.getString(context,"contract_text_limitPriceBuy") else  LanguageUtil.getString(context,"contract_text_marketPriceBuy")
                                } else {
                                    if (priceType == CONTRACT_LIMIT)  LanguageUtil.getString(context,"contract_text_limitPriceSell") else  LanguageUtil.getString(context,"contract_text_marketPriceSell")
                                }

                        getView<TextView>(R.id.tv_contract_name)?.text =
                                currentContract?.baseSymbol + currentContract?.quoteSymbol + " ${Contract2PublicInfoManager.getContractType(context!!, currentContract?.contractType, currentContract?.settleTime)}"

                        /**
                         * 合约价格精度（价格精度根据这个截取）
                         */
                        jsonObject?.run {
                            val price = optString("price")
                            val buyOrderCost = optString("buyOrderCost")
                            val sellOrderCost = optString("sellOrderCost")
                            val contractConfig = optJSONObject("contractConfig")
                            val quoteSymbol = contractConfig.optString("quoteSymbol")
                            val canUseBalance = contractConfig.optString("canUseBalance")
                            val orderPriceValue = contractConfig.optString("orderPriceValue")

                            if (priceType == CONTRACT_LIMIT) {
                                val cutPriceByPrecision = DecimalUtil.cutValueByPrecision(price, currentContract?.pricePrecision
                                        ?: 2)
                                getView<TextView>(R.id.tv_price)?.text = cutPriceByPrecision
                            } else {
                                getView<TextView>(R.id.tv_price)?.text =  LanguageUtil.getString(context,"contract_action_marketPrice")
                            }

                            getView<TextView>(R.id.tv_coin_name)?.text = quoteSymbol

                            /**
                             * 可用余额
                             */
                            getView<TextView>(R.id.tv_available_balance_title)?.text =  LanguageUtil.getString(context,"withdraw_text_available") + "(BTC)"
                            val cutBalanceByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(canUseBalance)
                            getView<TextView>(R.id.tv_available_balance)?.text = cutBalanceByPrecision

                            /**
                             * 标记价格
                             */
                            getView<TextView>(R.id.tv_tag_price_title)?.text =  LanguageUtil.getString(context,"contract_text_markPrice") +
                                    "(${quoteSymbol})"
                            getView<TextView>(R.id.tv_tag_price)?.text = tv_tag_price?.text

                            /**
                             * 委托价值
                             */
                            getView<TextView>(R.id.tv_value_title)?.text =  LanguageUtil.getString(context,"contract_text_value") + "(BTC)"
                            val orderPriceValueByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(
                                    orderPriceValue
                            )
                            getView<TextView>(R.id.tv_entrust_value)?.text = orderPriceValueByPrecision


                            /**
                             * 成本
                             */
                            getView<TextView>(R.id.tv_cost_title)?.text =  LanguageUtil.getString(context,"contract_text_value") + "(BTC)"
                            getView<TextView>(R.id.tv_cost)?.text =
                                    Contract2PublicInfoManager.cutDespoitByPrecision(
                                            if (orientation) {
                                                buyOrderCost
                                            } else {
                                                sellOrderCost
                                            }
                                    )
                            /**
                             * 杠杆倍数
                             */
                            getView<TextView>(R.id.tv_lever)?.text = currentLevel + "X"
                        }

                    }
                }
                .addOnClickListener(R.id.tv_cancel, R.id.tv_confirm)
                .setOnViewClickListener { viewHolder, view, tDialog ->
                    when (view.id) {
                        R.id.tv_cancel -> {
                            tDialog.dismiss()
                        }
                        R.id.tv_confirm -> {
                            takeOrder(et_position?.text.toString(), et_price?.text.toString(), side = if (orientation) "BUY" else "SELL")
                            tDialog.dismiss()
                        }
                    }
                }
                .create()
                .show()

    }


    private fun initSocket() {
        Log.d(TAG, "==initSocket===")
        socketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_CONTRACT_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.i(TAG, "onOpen")
                subCurrentContractMsg()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.i(TAG, "onClose$reason")
            }

            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                if (bytes == null) return
                val data = GZIPUtils.uncompressToString(bytes.array())
                if (!data.isNullOrBlank()) {
                    if (data.contains("ping")) {
                        val replace = data.replace("ping", "pong")
                        Log.d(TAG, "=====lilllie=======$replace")
                        sendMsg(replace)
                    } else {
                        handleData(data)
                    }
                }
            }

            override fun onMessage(message: String?) {
                Log.i(TAG, "onMessage")
            }

            override fun onError(ex: Exception?) {
                Log.i(TAG, "onError" + ex?.printStackTrace())
            }

        }
        try {
            socketClient.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 订阅当前合约的24H行情&深度
     */
    fun subCurrentContractMsg() {
        Log.d(TAG, "======last = $lastSymbol,current = $currentSymbol============")
        if (currentSymbol == lastSymbol) {
            sendMsg(WsLinkUtils.tickerFor24HLink(currentSymbol.toLowerCase()))
            sendMsg(WsLinkUtils.getDepthLink(currentSymbol.toLowerCase()).json)
        } else {
            if (!lastSymbol.isNullOrBlank()) {
                sendMsg(WsLinkUtils.tickerFor24HLink(lastSymbol?.toLowerCase()!!, false))
                sendMsg(WsLinkUtils.getDepthLink(lastSymbol?.toLowerCase()!!, false).json)
                clearDepthView()
            }
            sendMsg(WsLinkUtils.tickerFor24HLink(currentSymbol.toLowerCase()))
            sendMsg(WsLinkUtils.getDepthLink(currentSymbol.toLowerCase()).json)
        }

    }

    var transactionData: TransactionData? = null

    /**
     * TODO 优化
     * json. LanguageUtil.getString(context,"channel") == WsLinkUtils.getDepthLink(currentSymbol.toLowerCase().toString()).channel
     * 这些判断不能少，即使WS取消订阅，但未知情况还是会...
     */
    fun handleData(data: String) {
        Log.d(TAG, "======合约WS返回数据====$data")
        doAsync {
            val json = JSONObject(data)
            val tickJson = json.optJSONObject("tick") ?: return@doAsync
            if (json.getString("channel") == WsLinkUtils.getDepthLink(currentSymbol.toLowerCase().toString()).channel) {
                if (tickJson.has("buys")) {
                    Log.d(TAG, "=====深度=====$data")
                    /**
                     * 深度
                     */
                    transactionData = JsonUtils.jsonToBean(data, TransactionData::class.java)
                    /**
                     *卖盘取最小
                     */
                    transactionData?.tick?.asks?.sortByDescending { it.get(0).asDouble }
                    /**
                     * 买盘取最大
                     */
                    transactionData?.tick?.buys?.sortByDescending { it.get(0).asDouble }
                    Log.d(TAG, "========111=====" + transactionData?.tick?.asks)

                    uiThread {
                        refreshDepthView(transactionData)
                    }
                }
            } else {
                Log.d(TAG, "=====24H行情=====$data")
                /**
                 * 24H
                 */
                if (json.getString("channel") == WsLinkUtils.tickerFor24HLink(currentSymbol.toLowerCase(), isChannel = true)) {
                    val tick = JsonUtils.jsonToBean(tickJson.toString(), QuotesData.Tick::class.java)
                    if (TextUtils.isEmpty(tick.vol)) return@doAsync
                    uiThread {
                        tv_close_price?.text = "--"
                        val closePriceByPrecision = DecimalUtil.cutValueByPrecision(tick.close, currentContract?.pricePrecision
                                ?: 2)
                        ContractFragment.liveData4ClosePrice.postValue(hashMapOf(closePriceByPrecision to tick.rose))
                        /**
                         * 价格默认：标记价格
                         */
                        if (!isEditMode) {
                            et_price?.run {
                                setText(closePriceByPrecision)
                                setSelection(et_price?.text?.length ?: 0)
                            }

                        }
                    }
                }
            }


        }
    }

    /**
     * 买卖盘
     *
     * 初始化交易详情记录view
     */
    private fun initDepthView(items: Int = 5) {
        sellViewList.clear()
        buyViewList.clear()

        if (ll_buy_price?.childCount ?: 0 > 0) {
            (ll_buy_price as LinearLayout).removeAllViews()
        }

        if (ll_sell_price?.childCount ?: 0 > 0) {
            (ll_sell_price as LinearLayout).removeAllViews()
        }

        val pricePrecision = currentContract?.pricePrecision ?: 2
        for (i in 0 until items) {
            /**
             * 卖盘
             */
            val view: View = layoutInflater.inflate(R.layout.item_depth_contract, null).apply {
                tv_price?.textColor = ColorUtil.getMainColorType(isRise = false)
                /**
                 * 点击 买卖盘，将价格显示在 "买入价"中
                 */
                setOnClickListener {
                    val result = tv_price?.text.toString()
                    if (!TextUtils.isEmpty(result) && result != "--" && result != "null") {
                        Log.d(TAG, "=====result:$result=====")
                        et_price?.run {
                            setText(DecimalUtil.cutValueByPrecision(result, pricePrecision))
                            setSelection(et_price?.text?.length ?: 0)
                        }
                    }
                }
            }

            ll_sell_price?.addView(view)
            sellViewList.add(view)

            /***********/

            /**
             * 买盘
             */
            val view1: View = layoutInflater.inflate(R.layout.item_depth_contract, null).apply {
                tv_price?.textColor = ColorUtil.getMainColorType()
                tv_amount?.textColor = ColorUtil.getMainColorType()
                setOnClickListener {
                    val result = tv_price?.text.toString()
                    if (!TextUtils.isEmpty(result) && result != "--" && result != "null") {
                        Log.d(TAG, "=====result:$result=====")
                        et_price?.run {
                            setText(DecimalUtil.cutValueByPrecision(result, pricePrecision))
                            setSelection(et_price?.text?.length ?: 0)
                        }
                    }
                }
            }

            ll_buy_price?.addView(view1)
            buyViewList.add(view1)
        }


        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        sellViewList[0].ll_item.measure(w, w)
        sellViewList[0].ll_item.post {
            itemWidth = sellViewList[0].ll_item.measuredWidth
            Log.d(TAG, "========itemWidth:$itemWidth=======")
        }
    }


    /**
     * 更新买卖盘的数据
     */
    private fun refreshDepthView(transactionData: TransactionData?) {
        if (transactionData == null) return
        val tick: TransactionData.Tick = transactionData.tick ?: return
        /**
         * 卖盘交易量最大的
         */
        try {
            val askMaxVolJson = tick.asks.maxBy { it.get(1).asDouble }
            val askMaxVol = askMaxVolJson?.get(1)?.asDouble

            /**
             * 买盘交易量最大的
             */
            val buyMaxVolJson = tick.buys.maxBy { it.get(1).asDouble }
            val buyMaxVol = buyMaxVolJson?.get(1)?.asDouble

            for (i in 0 until sellViewList.size) {
                /**
                 * 卖盘
                 */
                if (tick.asks.size > sellViewList.size) {
                    /**
                     * 移除大值
                     */
                    val subList = tick.asks.subList(tick.asks.size - sellViewList.size, tick.asks.size)

                    /*****深度背景色START****/
                    sellViewList[i].fl_bg_item.setBackgroundResource(R.color.entrust_sell_color)
                    val layoutParams = sellViewList[i].fl_bg_item.layoutParams
                    val width = (subList[i].get(1).asDouble / askMaxVol!!) * itemWidth
                    layoutParams.width = width.toInt()
                    Log.d(TAG, "====1111=width:${width.toInt()}=======")
                    sellViewList[i].fl_bg_item.layoutParams = layoutParams

                    /*****深度背景色END****/

                    sellViewList[i].tv_price.text =
                            Contract2PublicInfoManager.cutValueByPrecision(subList[i].get(0).asString, currentContract?.pricePrecision
                                    ?: 2)
                    sellViewList[i].tv_position.text = BigDecimalUtils.formatNumber(subList[i].get(1).toString())

                } else {

                    val temp = sellViewList.size - tick.asks.size
                    sellViewList[i].tv_price?.text = "--"
                    sellViewList[i].tv_position?.text = "--"
                    if (i >= temp) {
                        /*****深度背景色START****/
                        sellViewList[i].fl_bg_item.setBackgroundResource(R.color.entrust_sell_color)
                        val layoutParams = sellViewList[i].fl_bg_item.layoutParams
                        val width = (tick.asks[i - temp].get(1).asDouble / askMaxVol!!) * itemWidth
                        Log.d(TAG, "=========${tick.asks[i - temp].get(1).asDouble}=,${askMaxVol}==,${itemWidth}=======")
                        layoutParams.width = width.toInt()
                        Log.d(TAG, "====1111=width:${width.toInt()}=======")
                        sellViewList[i].fl_bg_item.layoutParams = layoutParams
                        /*****深度背景色END****/

                        /**
                         * 价格
                         */
                        sellViewList[i].tv_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.asks[i - temp].get(0).asString, currentContract?.pricePrecision
                                ?: 2)

                        /**
                         * 仓位
                         */
                        sellViewList[i].tv_position?.text = BigDecimalUtils.formatNumber(tick.asks[i - temp].get(1).asString)


                    } else {
                        clearDepthView(SELL_SIDE)
                    }
                }

                /**
                 * 买盘
                 */
                if (tick.buys.size > i) {
                    buyViewList[i].run {
                        /*****深度背景色START****/
                        fl_bg_item.setBackgroundResource(R.color.entrust_buy_color)
                        val layoutParams = fl_bg_item.layoutParams
                        val width = (tick.buys[i].get(1).asDouble / buyMaxVol!!) * itemWidth
                        layoutParams.width = width.toInt()
                        fl_bg_item.layoutParams = layoutParams
                        /*****深度背景色END****/
                        val price4DepthBuy = tick.buys[i].get(0).asString
                        tv_price.text = DecimalUtil.cutValueByPrecision(price4DepthBuy, currentContract?.pricePrecision
                                ?: 2)
                        tv_position.text = BigDecimalUtils.formatNumber(tick.buys[i].get(1).asString)
                    }
                } else {
                    buyViewList[i].run {
                        tv_price.text = "--"
                        tv_position.text = "--"
                        fl_bg_item.setBackgroundResource(R.color.transparent)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     *
     * 重置买卖盘的数据
     *
     */
    fun clearDepthView(side: String = "") {
        when (side) {
            SELL_SIDE -> {
                sellViewList.forEach {
                    clearDepthItem(it)
                }
            }

            BUY_SIDE -> {
                buyViewList.forEach {
                    clearDepthItem(it)
                }
            }

            else -> {
                sellViewList.forEach {
                    clearDepthItem(it)
                }
                buyViewList.forEach {
                    clearDepthItem(it)
                }
            }
        }


    }

    private fun clearDepthItem(it: View) {
        if (isAdded) {
            runOnUiThread {
                it.tv_position?.text = "--"
                it.tv_amount?.text = "--"
                it.tv_price?.text = "--"
                it.fl_bg_item?.setBackgroundResource(R.color.transparent)
            }
        }
    }

    /**
     * 活动委托列表
     */
    private fun getOrderList4Contract() {
        if (!LoginManager.checkLogin(activity, false)) {
            return
        }
        addDisposable(getContractModel().getOrderList4Contract(contractId = contractId.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        var json = data.optJSONObject("data")
                        val jsonArray = json.optJSONArray("orderList")
                        val orderList = arrayListOf<JSONObject>()
                        if (jsonArray.length() != 0) {
                            for (i in 0 until jsonArray.length()) {
                                orderList.add(jsonArray.optJSONObject(i))
                            }
                            adapter.replaceData(orderList)
                            loopOrder()
                        } else {
                            adapter.replaceData(arrayListOf())
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        DisplayUtil.showSnackBar(activity?.window?.decorView, msg, isSuc = false)
                    }
                }))
    }

    /**
     * 取消订单
     */
    private fun cancelOrder(orderId: String, contractId: String, pos: Int) {
        addDisposable(getContractModel().cancelOrder4Contract(contractId = contractId, orderId = orderId,
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        DisplayUtil.showSnackBar(activity?.window?.decorView,  LanguageUtil.getString(context,"cancel_order_suc"))
                        adapter.remove(pos)
                        getOrderList4Contract()
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        DisplayUtil.showSnackBar(activity?.window?.decorView, msg, isSuc = false)
                    }
                }))
    }

    /**
     * 每5s调用一次接口
     */
    private fun loopOrder() {
        addDisposable(Observable.interval(0, 5, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getOrderObserver()))
    }

    private fun getOrderObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {
            override fun onComplete() {
            }

            override fun onNext(t: Long) {
                loopOrderList4Contract()
            }

            override fun onError(e: Throwable) {
            }
        }

    }

    /**
     * Ws 发送消息
     */
    private fun sendMsg(msg: String) {
        Log.i(TAG, "sendMsg = $msg")
        if (::socketClient.isInitialized) {
            Log.d(TAG, "=====sendMsg===" + socketClient.isOpen)
            if (socketClient.isOpen) {
                try {
                    socketClient.send(msg)
                } catch (e: WebsocketNotConnectedException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    socketClient.connect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Log.d(TAG, "=====sendMsg===1=" + socketClient.isOpen)
        } else {
            initSocket()
        }
    }


    /**
     * 活动委托列表
     */
    private fun loopOrderList4Contract() {
        if (!LoginManager.checkLogin(activity, false)) {
            return
        }
        addDisposable(getContractModel().getOrderList4Contract(contractId = contractId.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        jsonObject.optJSONObject("data").run {
                            val jsonArray = optJSONArray("orderList")
                            val orderList = arrayListOf<JSONObject>()
                            if (jsonArray.length() != 0) {
                                for (i in 0 until jsonArray.length()) {
                                    orderList.add(jsonArray.optJSONObject(i))
                                }
                                adapter.replaceData(orderList)
                            } else {
                                adapter.replaceData(arrayListOf())
                            }
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        DisplayUtil.showSnackBar(activity?.window?.decorView, msg, isSuc = false)
                    }
                }))
    }


}

