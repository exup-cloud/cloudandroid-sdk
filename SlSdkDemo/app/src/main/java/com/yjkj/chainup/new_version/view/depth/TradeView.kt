package com.yjkj.chainup.new_version.view.depth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.ParamConstant.*
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.model.model.MainModel
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.new_version.activity.leverage.NLeverFragment
import com.yjkj.chainup.new_version.activity.leverage.TradeFragment
import com.yjkj.chainup.new_version.dialog.DialogUtil
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.fragment.NCVCTradeFragment
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cvctrade.*
import kotlinx.android.synthetic.main.trade_amount_view.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * @Author: Bertking
 * @Date：2019/3/7-5:43 PM
 * @Description: 交易量的View
 */
class TradeView @JvmOverloads constructor(context: Context,
                                          attrs: AttributeSet? = null,
                                          defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = TradeView::class.java.simpleName


    private val delayTime = 100L
    //交易类型
    var transactionType = ParamConstant.TYPE_BUY

    //价格类型
    var priceType = 0

    //可用余额
    var canUseMoney: String = "0"
    var inputPrice: String = ""
    var inputQuantity: String = ""

    var priceScale = 2

    var volumeScale = 2

    private var isPriceLongClick: Boolean = false
    private var isStartPriceSubClick = false
    private var isStartPricePlusClick = false
    var disposable: CompositeDisposable? = null
    var mainModel: MainModel? = null

    var dialog: TDialog? = null
    var coinMapData: JSONObject? = NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
        set(value) {
            Log.d(TAG, "=======调用SET=====value is $value")
            field = value
            synchronized(this) {
                priceScale = value?.optInt("price", 2) ?: 2
                volumeScale = value?.optInt("volume", 2) ?: 2
            }

            tv_coin_name?.text = NCoinManager.getMarketCoinName(showAnoterName(value))

            LogUtil.d(TAG, "TradeView==coinMapData==priceScale is $priceScale,volumeScale is $volumeScale")

            getAvailableBalance()

            /**
             * 设置 RadioButton 的选中效果
             */
            for (i in 0 until rg_trade.childCount step 2) {
                val radioButton = rg_trade?.getChildAt(i) as RadioButton
                radioButton.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                radioButton.backgroundResource = R.color.transparent
            }
            // et_price?.setText("")
            et_volume?.setText("")
            if (!StringUtil.checkStr(et_price?.text.toString())) {
                tv_convert_price?.text = "--"
            }

            tv_transaction_money?.text = "--"
        }


    fun setPrice() {

        synchronized(this) {
            priceScale = coinMapData?.optInt("price", 2) ?: 2
            volumeScale = coinMapData?.optInt("volume", 2) ?: 2
        }


        et_price?.filters = arrayOf(DecimalDigitsInputFilter(priceScale))
        if (transactionType == ParamConstant.TYPE_BUY && priceType == ParamConstant.TYPE_MARKET) {
            et_volume?.filters = arrayOf(DecimalDigitsInputFilter(priceScale))
            LogUtil.d(TAG, "setPrice()==市价数量精度")
        } else {
            et_volume?.filters = arrayOf(DecimalDigitsInputFilter(volumeScale))
        }
    }

    private fun showAnoterName(jsonObject: JSONObject?): String {
        return NCoinManager.showAnoterName(jsonObject)
    }

    /**
     * 获取可用余额
     */

    fun getAvailableBalance() {

        if (!LoginManager.checkLogin(context, false)) {
            /**
             * 可用余额
             */
            tv_available_balance?.text = LanguageUtil.getString(context, "assets_text_available") + "--"
            return
        }


    }

    fun setTextContent() {
        tv_order_type?.text = LanguageUtil.getString(context, "contract_action_limitPrice")
        tv_transaction_text?.text = LanguageUtil.getString(context, "transaction_text_tradeSum")
        et_price?.hint = LanguageUtil.getString(context, "contract_text_price")

    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ComVerifyView, 0, 0)
            typedArray.recycle()
        }


        LogUtil.d(TAG, "TradeView==init==priceScale is $priceScale,volumeScale is $volumeScale")
        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.trade_amount_view, this, true)

        setTextContent()

        ll_tag?.setOnClickListener {
            NewDialogUtils.showSingleDialog(context!!, LanguageUtil.getString(context, "etf_text_networthPrompt"), object : NewDialogUtils.DialogBottomListener {
                override fun sendConfirm() {
                }

            }, "", LanguageUtil.getString(context, "alert_common_iknow"))
        }

        observeData()

        NLiveDataUtil.observeData(this.context as NewMainActivity, Observer {
            if (it == null) return@Observer
            if (MessageEvent.login_operation_type == it.msg_type) {
                operator4PriceVolume(context)
            }
        })


        tv_order_type?.setOnClickListener {
            dialog = NewDialogUtils.showBottomListDialog(context, arrayListOf(LanguageUtil.getString(context, "contract_action_limitPrice"), LanguageUtil.getString(context, "contract_action_marketPrice")), priceType, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    tv_order_type?.text = data[item]
                    dialog?.dismiss()
                    var showCoinName = NCoinManager.getMarketShowCoinName(coinMapData?.optString("showName", ""))
                    when (item) {
                        0 -> {
                            priceType = TYPE_LIMIT
                            v_market_trade_tip?.visibility = View.GONE
                            ll_price?.visibility = View.VISIBLE
                            tv_convert_price?.visibility = View.VISIBLE
                            ll_transaction?.visibility = View.VISIBLE
                            if (transactionType == TYPE_BUY) {
                                et_volume?.hint = LanguageUtil.getString(context, "transaction_tip_buyVolume")
                            } else {
                                et_volume?.hint = LanguageUtil.getString(context, "common_text_sellVolume")
                            }
                            tv_coin_name?.text = "$showCoinName"
                            getAvailableBalance()
                        }
                        1 -> {
                            priceType = TYPE_MARKET
                            v_market_trade_tip?.visibility = View.VISIBLE
                            ll_price?.visibility = View.GONE
                            tv_convert_price?.visibility = View.INVISIBLE
                            ll_transaction?.visibility = View.INVISIBLE
                            if (transactionType == TYPE_BUY) {
                                et_volume?.hint = LanguageUtil.getString(context, "transaction_text_tradeSum")
                            } else {
                                et_volume?.hint = LanguageUtil.getString(context, "common_text_sellVolume")
                            }
                            tv_coin_name?.text = "$showCoinName"
                            getAvailableBalance()
                        }
                    }
                }
            })
        }



        getAvailableBalance()

        // cbtn_create_order?.normalBgColor = ColorUtil.getMainColorType()

        operator4Price(context)


        /**
         * 交易额
         */
        if (priceType == TYPE_MARKET) {
            ll_transaction?.visibility = View.INVISIBLE
            tv_convert_price?.visibility = View.INVISIBLE

        } else {
            ll_transaction?.visibility = View.VISIBLE
            tv_transaction_money?.visibility = View.VISIBLE
            tv_transaction_money?.text = "--"
            tv_convert_price?.visibility = View.VISIBLE
        }


        /**
         * 交易量百分比
         * TODO 代码优化
         */
        rg_trade?.setOnCheckedChangeListener { group, checkedId ->
            Log.d(TAG, "=======rg_trade初始化...==========" + checkedId)

            /**
             * 设置 RadioButton 的选中效果
             */
            for (i in 0 until rg_trade.childCount step 2) {
                val radioButton = rg_trade?.getChildAt(i) as RadioButton
                radioButton.setTextColor(ColorUtil.getCheck4ColorStateList())
                radioButton.background = ColorUtil.getCheck4StateListDrawable()
            }

            if (checkedId > -1) {
                if (!LoginManager.checkLogin(context, true)) {
                    group.clearCheck()
                    return@setOnCheckedChangeListener
                }
            }

            when (checkedId) {
                R.id.rb_1st -> {
                    adjustRatio("0.25")
                }

                R.id.rb_2nd -> {
                    adjustRatio("0.50")
                }

                R.id.rb_3rd -> {
                    adjustRatio("0.75")
                }

                R.id.rb_4th -> {
                    adjustRatio("1.0")

                }
                else -> {
                    adjustRatio("0.25")
                }
            }
        }

        operator4PriceVolume(context)

        addTextListener()

        cbtn_create_order?.isEnable(true)
        cbtn_create_order?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                if (!LoginManager.checkLogin(context, true)) return

                var status = if (TradeFragment.currentIndex == LEVER_INDEX_TAB) PublicInfoDataService.getInstance().leverTradeKycOpen else PublicInfoDataService.getInstance().exchangeTradeKycOpen

                if (status && UserDataService.getInstance().authLevel != 1) {
                    NewDialogUtils.KycSecurityDialog(context!!, context?.getString(R.string.common_kyc_trading)
                            ?: "", object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    NToastUtil.showTopToast(false, context?.getString(R.string.noun_login_pending))
                                }

                                2, 3 -> {
                                    ArouterUtil.greenChannel(RoutePath.RealNameCertificationActivity, null)
                                }
                            }
                        }
                    })
                    return
                }


                /**
                 * 限价交易
                 */
                if (priceType == TYPE_LIMIT) {
                    if (TextUtils.isEmpty(inputPrice)) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "contract_tip_pleaseInputPrice"))
                        return
                    }

                    if (TextUtils.isEmpty(inputQuantity)) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "transfer_tip_emptyVolume"))
                        return
                    }


                    val limitPriceMin = coinMapData?.optString("limitPriceMin")
                    if (BigDecimalUtils.compareTo(inputPrice, limitPriceMin) < 0) {
                        val msg = LanguageUtil.getString(context, "common_tip_limitMinTransactionPrice") + BigDecimalUtils.showSNormal(limitPriceMin)
                        NToastUtil.showTopToast(false, msg)
                        return
                    }

                    val limitVolumeMin = coinMapData?.optString("limitVolumeMin")
                    if (BigDecimalUtils.compareTo(inputQuantity, limitVolumeMin) < 0) {
                        val msg = LanguageUtil.getString(context, "common_tip_limitMaxTransactionVolume") + BigDecimalUtils.showSNormal(limitVolumeMin)
                        NToastUtil.showTopToast(false, msg)
                        return
                    }

                    if (transactionType == TYPE_SELL) {
                        if (BigDecimalUtils.compareTo(canUseMoney, inputQuantity) < 0) {
                            // DisplayUtil.showSnackBar(this@TradeView.rootView, LanguageUtil.getString(context,R.string.common_tip_balanceNotEnough), isSuc = false)
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_balanceNotEnough"))
                            return
                        }
                    } else {

                    }
                }

                /**
                 * 现价交易
                 */
                if (priceType == TYPE_MARKET) {
                    if (TextUtils.isEmpty(inputQuantity)) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "transfer_tip_emptyVolume"))
                        return
                    }
                    val marketBuyMin = coinMapData?.optString("marketBuyMin")
                    val marketSellMin = coinMapData?.optString("marketSellMin")

                    /**
                     * 市价交易
                     * 在市价交易的前提下，无论买入or卖出，使用的是et_volume,So the context is inputQuantity
                     */
                    if (transactionType == TYPE_BUY) {

                        Log.d(TAG, "=======市价买:交易额$inputQuantity,最小买入量:${marketBuyMin}========")
                        /**
                         * 最小价格
                         */
                        if (BigDecimalUtils.compareTo(inputQuantity, marketBuyMin) < 0) {
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_limitMinTransactionPrice") + BigDecimalUtils.showSNormal(marketBuyMin))
                            return
                        }

                        if (BigDecimalUtils.compareTo(canUseMoney, inputQuantity) < 0) {
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_balanceNotEnough"))
                            return
                        }

                    } else {
                        /**
                         * 最小交易量
                         */
                        if (BigDecimalUtils.compareTo(inputQuantity, marketSellMin) < 0) {
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_limitMaxTransactionVolume") + BigDecimalUtils.showSNormal(marketSellMin))
                            return
                        }

                        if (BigDecimalUtils.compareTo(canUseMoney, inputQuantity) < 0) {
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_balanceNotEnough"))
                            return
                        }

                    }
                }

                createOrder()
            }
        }
    }

    /**
     * 处理price,volume的事件&登录状态的关系
     */
    private fun operator4PriceVolume(context: Context) {
        priceScale = coinMapData?.optInt("price", 2) ?: 2
        volumeScale = coinMapData?.optInt("volume", 2) ?: 2
        setPrice()
        if (!LoginManager.checkLogin(context, false)) {
            tv_transaction_money?.text = "--"
            et_price?.isFocusableInTouchMode = false
            et_volume?.isFocusableInTouchMode = false
        } else {
            if (et_volume?.isFocusableInTouchMode?.not() == true) {
                et_volume?.isFocusable = true
                et_volume?.isFocusableInTouchMode = true
                et_volume?.requestFocus()
                et_volume?.findFocus()
            }
            if (et_price?.isFocusableInTouchMode?.not() == true) {
                et_price?.isFocusable = true
                et_price?.isFocusableInTouchMode = true
                et_price?.requestFocus()
                et_price?.findFocus()
            }
        }

        et_price?.setOnClickListener {
            LoginManager.checkLogin(context, true)
        }
        et_volume?.setOnClickListener {
            LoginManager.checkLogin(context, true)
        }


        /**
         * 「价格」输入框的背景变换
         */
        et_price?.setOnFocusChangeListener { _, hasFocus ->
            ll_price?.setBackgroundResource(if (hasFocus) R.drawable.bg_trade_et_focused else R.drawable.bg_trade_et_unfocused)
        }

        /**
         * 「交易量」输入框的背景变换
         */
        et_volume?.setOnFocusChangeListener { _, hasFocus ->
            ll_volume?.setBackgroundResource(if (hasFocus) R.drawable.bg_trade_et_focused else R.drawable.bg_trade_et_unfocused)
        }

        if (priceType == TYPE_MARKET) {
            ll_transaction?.visibility = View.INVISIBLE
            tv_convert_price?.visibility = View.INVISIBLE
        } else {
            ll_transaction?.visibility = View.VISIBLE
            tv_convert_price?.visibility = View.VISIBLE
        }
    }

    /**
     * 调节可用余额的比例
     */
    private fun adjustRatio(radio: String) {
        if (TextUtils.isEmpty(canUseMoney)) return
        when (priceType) {
            /**
             * 限价
             */
            TYPE_LIMIT -> {
                val price = et_price?.text.toString()
                if (transactionType == TYPE_BUY) {
                    val consume = BigDecimalUtils.mul(canUseMoney, radio, priceScale).toString()
                    if (!TextUtils.isEmpty(price)) {
                        val volume = BigDecimalUtils.div(consume, price, volumeScale).toPlainString()
                        et_volume?.setText(volume)
                    }
                    if (TextUtils.isEmpty(inputPrice) || inputPrice == "0") {
                        tv_transaction_money?.text = "--"
                    } else {
                        tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(consume) + showMarket()}"
                    }
//                et_volume?.setSelection(et_volume?.text.toString().trim().length)
                } else {
                    val volume = BigDecimalUtils.mul(canUseMoney, radio, volumeScale).toPlainString()
                    et_volume?.setText(volume)
//                et_volume?.setSelection(et_volume?.text.toString().trim().length)
                    var consume = "0"
                    if (TextUtils.isEmpty(price)) {
                        consume = BigDecimalUtils.mul(volume, "0", priceScale).toString()
                    } else {
                        consume = BigDecimalUtils.mul(volume, price, priceScale).toString()
                    }

                    tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(consume) + showMarket()}"
                }
            }

            /**
             * 市价
             */
            TYPE_MARKET -> {
                if (transactionType == TYPE_BUY) {
                    val consume = BigDecimalUtils.mul(canUseMoney, radio, priceScale).toPlainString()
                    et_volume?.setText(consume)
//                    et_volume?.setSelection(et_volume?.text.toString().trim().length)
                    tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(consume) + showMarket()}"
                } else {
                    val volume = BigDecimalUtils.mul(canUseMoney, radio, volumeScale).toPlainString()
                    et_volume?.setText(volume)
//                    et_volume?.setSelection(et_volume?.text.toString().trim().length)

                    tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(volume) + showCoinName()}"

                }
            }

        }


    }

    /**
     * 价格操作(加 + 减 -)
     */
    private fun operator4Price(context: Context) {
        /**
         * 价格 （-）
         */
        tv_sub?.setOnTouchListener { _, event ->
            isPriceLongClick = true
            isStartPriceSubClick = true

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!LoginManager.checkLogin(context, true)) return@setOnTouchListener false
                    val unit = if (transactionType == ParamConstant.TYPE_SELL &&
                            priceType == TYPE_MARKET) {
                        (1 / Math.pow(10.0, volumeScale.toDouble())).toString()
                    } else {
                        (1 / Math.pow(10.0, priceScale.toDouble())).toString()
                    }
                    Log.d(TAG, "=======price:减的单位量unit:$unit===")
                    if (TextUtils.isEmpty(unit)) return@setOnTouchListener false
                    if (inputPrice.isEmpty()) {
                        et_price?.setText("")
                        tv_convert_price?.text = ""
                        return@setOnTouchListener true
                    }
                    if (BigDecimal(inputPrice).toFloat() > 0f) {
                        inputPrice = BigDecimalUtils.sub(inputPrice, unit).toPlainString()
                        et_price?.setText(BigDecimalUtils.subZeroAndDot(inputPrice))
                        tv_convert_price?.text = RateManager.getCNYByCoinMap(coinMapData, inputPrice)
                    } else {
                        et_price?.setText("")
                        tv_convert_price?.text = ""
                        return@setOnTouchListener true
                    }

                    doAsync {
                        while (isPriceLongClick) {
                            Thread.sleep(delayTime)
                            if (!isStartPriceSubClick) continue

                            inputPrice = try {
                                if (BigDecimal(inputPrice).toFloat() > 0f) {
                                    BigDecimalUtils.sub(inputPrice, unit).toPlainString()
                                } else {
                                    ""
                                }
                            } catch (e: NumberFormatException) {
                                ""
                            }
                            uiThread {
                                et_price?.setText(BigDecimalUtils.subZeroAndDot(inputPrice))
                                tv_convert_price?.text = RateManager.getCNYByCoinMap(coinMapData, inputPrice)
                            }

                        }
                    }
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isPriceLongClick = false
                    isStartPriceSubClick = false
                }
            }


            true
        }

        /**
         * 价格 （+）
         */
        tv_add?.setOnTouchListener { _, event ->

            isPriceLongClick = true
            isStartPricePlusClick = true

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!LoginManager.checkLogin(context, true)) return@setOnTouchListener false

                    val unit = if (transactionType == TYPE_SELL && priceType == TYPE_MARKET) {
                        (1 / Math.pow(10.0, volumeScale.toDouble())).toString()
                    } else {
                        (1 / Math.pow(10.0, priceScale.toDouble())).toString()
                    }
                    Log.d(TAG, "=======price:加的单位量unit:$unit===")
                    if (TextUtils.isEmpty(unit)) return@setOnTouchListener false

                    inputPrice = BigDecimalUtils.add(inputPrice, unit).toPlainString()
                    et_price?.setText(BigDecimalUtils.subZeroAndDot(inputPrice))

                    tv_convert_price?.text = RateManager.getCNYByCoinMap(coinMapData, inputPrice)

                    doAsync {
                        while (isPriceLongClick) {
                            Thread.sleep(delayTime)
                            if (!isStartPricePlusClick) continue
                            inputPrice = try {
                                BigDecimalUtils.add(inputPrice, unit).toPlainString()
                            } catch (e: NumberFormatException) {
                                ""
                            }

                            uiThread {
                                et_price?.setText(BigDecimalUtils.subZeroAndDot(inputPrice))
                                tv_convert_price?.text = RateManager.getCNYByCoinMap(coinMapData, inputPrice)
                            }

                        }
                    }
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isPriceLongClick = false
                    isStartPricePlusClick = false
                }
            }

            true
        }
    }


    private fun addTextListener() {

        /**
         * 价格
         */
        et_price?.filters = arrayOf(DecimalDigitsInputFilter(priceScale))
        et_price?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                Log.d(TAG, "======价格输入框的内容==afterTextChanged:${s.toString()}=======")

//                et_price?.setSelection(et_price.text.length)

//                /**
//                 * 设置 RadioButton 的选中效果
//                 */
//                for (i in 0 until rg_trade.childCount step 2) {
//                    val radioButton = rg_trade?.getChildAt(i) as RadioButton
//                    radioButton.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
//                    radioButton.backgroundResource = R.color.transparent
//                }

                if (priceType == TYPE_MARKET || TextUtils.isEmpty(s) || s.toString() == "0.") {
                    tv_convert_price?.visibility = View.INVISIBLE
                } else {
                    Log.d(TAG, "=======可见===========")
                    tv_convert_price?.visibility = View.VISIBLE
                    tv_convert_price?.text = RateManager.getCNYByCoinMap(coinMapData, s.toString())
                }

                if (s?.startsWith(".") == true) {
                    et_price?.text?.clear()
                    Log.d(TAG, "=======1===========")
                }

                inputPrice = s.toString()

                if (inputPrice.startsWith(".")) {
                    inputPrice = "0"
                }

                Log.d(TAG, "==========inputPrice:$inputPrice")

                if (transactionType == TYPE_BUY) {
                    if (priceType == TYPE_LIMIT) {
                        if (TextUtils.isEmpty(inputPrice) || TextUtils.isEmpty(inputQuantity)) {
                            tv_transaction_money?.text = "--"
                        } else {
                            //计算总金额
                            var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                            money = DecimalUtil.cutValueByPrecision(money, priceScale)

                            tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(money) + showMarket()}"
                        }
                    } else {
                        //买入且市价交易状态下，价格未知,交易额不显示
//                        tv_transaction_money?.text = SymbolInterceptUtils.interceptData(canUseMoney, coinMapData?.optString("symbol", ""), "price") + marketName
                    }
                } else {
                    //当市价卖出状态时，此输入框输入的是商品数量（即币对前半段）
                    if (priceType == TYPE_LIMIT) {
                        if (TextUtils.isEmpty(inputPrice) || TextUtils.isEmpty(inputQuantity)) {
                            tv_transaction_money?.text = "--"
                        } else {
                            var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                            money = DecimalUtil.cutValueByPrecision(money, priceScale)

                            tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(money) + showMarket()}"
                        }

                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /**
         * 数量
         */
        et_volume?.filters = arrayOf(DecimalDigitsInputFilter(volumeScale))
        et_volume?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s?.startsWith(".") == true) {
                    et_price?.text?.clear()
                }

                Log.d(TAG, "======数量输入框的内容:${s.toString()},${volumeScale}=======")

                inputQuantity = s.toString()

                /**
                 * 清除 RadioButton 的选中效果
                 */
                if (s.toString().length < volumeScale + 2) {
                    for (i in 0 until rg_trade.childCount step 2) {
                        val radioButton = rg_trade?.getChildAt(i) as RadioButton
                        radioButton.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                        radioButton.backgroundResource = R.color.transparent
                    }
                }

//                inputQuantity = if (TextUtils.isEmpty(s)) {
//                    "0"
//                } else {
//                    s.toString()
//                }

                Log.d(TAG, "==========inputQuantity:$inputQuantity")

                if (inputQuantity.startsWith(".")) {
                    inputQuantity = "0"
                }

                if (transactionType == TYPE_BUY) {
                    if (priceType == TYPE_LIMIT) {
                        //交易额
                        if (TextUtils.isEmpty(inputPrice) || TextUtils.isEmpty(inputQuantity)) {
                            tv_transaction_money?.text = "--"
                        } else {
                            var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                            money = DecimalUtil.cutValueByPrecision(money, priceScale)
                            tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(money) + showMarket()}"
                        }
                    }
                } else {
                    if (priceType == TYPE_LIMIT) {
                        if (TextUtils.isEmpty(inputQuantity) || TextUtils.isEmpty(inputPrice)) {
                            tv_transaction_money?.text = "--"
                        } else {
                            //交易额
                            var money = BigDecimalUtils.mul(inputPrice, inputQuantity).toPlainString()
                            money = DecimalUtil.cutValueByPrecision(money, priceScale)
                            tv_transaction_money?.text = "${BigDecimalUtils.showSNormal(money) + showMarket()}"
                        }

                    }
                }
//                et_volume?.setSelection(et_volume?.text?.length ?: 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    /**
     * 下单
     */
    fun createOrder() {
        cbtn_create_order?.showLoading()

        val side = if (transactionType == TYPE_BUY) {
            "BUY"
        } else {
            "SELL"
        }

        val type = if (priceType == TYPE_LIMIT) {
            1
        } else {
            2
        }
        //市价情况下：买则表示交易额，卖表示总个数；限价下:买卖个数
        val volume = inputQuantity

        //限价模式下表示价格，市价无意义
        val price = inputPrice

        Log.d(TAG, "=disposable:===${disposable == null} ,${mainModel == null}======")
        (disposable ?: CompositeDisposable()).add((mainModel
                ?: MainModel()).createOrder(side, type, volume, price, coinMapData?.optString("symbol", "")
                ?: return, isLever = TradeFragment.currentIndex == LEVER_INDEX_TAB, consumer = object : NDisposableObserver(true) {
            override fun onResponseSuccess(data: JSONObject) {
                cbtn_create_order?.hideLoading()
                NToastUtil.showTopToast(true, LanguageUtil.getString(context, "contract_tip_submitSuccess"))
                NLiveDataUtil.postValue(MessageEvent(MessageEvent.CREATE_ORDER_TYPE, true, TradeFragment.currentIndex == LEVER_INDEX_TAB))
                //刷新委托列表
                getAvailableBalance()
                /**
                 * 设置 RadioButton 的选中效果
                 */
                for (i in 0 until rg_trade.childCount step 2) {
                    val radioButton = rg_trade?.getChildAt(i) as RadioButton
                    radioButton.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                    radioButton.backgroundResource = R.color.transparent
                }
                rg_trade.clearCheck()
                et_volume?.text?.clear()
                et_volume?.invalidate()
                et_volume?.setText("")
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
                cbtn_create_order?.hideLoading()
            }
        })!!)

    }


    private fun observeData() {

        NLiveDataUtil.observeData((this.context as NewMainActivity), Observer<MessageEvent> {
            if (null == it)
                return@Observer

            if (TradeFragment.currentIndex == CVC_INDEX_TAB) {
                if (it.isLever) {
                    return@Observer
                }
            } else {
                if (!it.isLever) {
                    return@Observer
                }
            }

            when (it.msg_type) {
                MessageEvent.TAB_TYPE -> {
                    coinMapData = if (it.isLever) {
                        NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol4Lever)
                    } else {
                        NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
                    }

                    if (it.isLever) {
                        buyOrSell(NLeverFragment.tradeOrientation, it.isLever)
                    } else {
                        buyOrSell(NCVCTradeFragment.tradeOrientation, it.isLever)
                    }

                }

                MessageEvent.symbol_switch_type -> {
                    if (null != it.msg_content) {
                        val symbol = it.msg_content as String
                        if (symbol != coinMapData?.optString("symbol", "")) {
                            coinMapData = NCoinManager.getSymbolObj(symbol)
                            tv_coin_name?.text = "${showCoinName()}"
                            tv_convert_price?.text = "--"
                            tv_transaction_money?.text = "--"
                            getAvailableBalance()
                        }
                    }
                }

            }
        })
    }

    private fun showCoinName(): String? {
        return NCoinManager.getMarketShowCoinName(showAnoterName(coinMapData))
    }

    private fun showMarket(): String? {
        return NCoinManager.getMarketName(showAnoterName(coinMapData))
    }

    /**
     * 买入 & 卖出
     */
    fun buyOrSell(transferType: Int, isLever: Boolean = false) {
        if (isLever) {
            coinMapData = NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol4Lever)
        } else {
            coinMapData = NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
        }

        setPrice()

        if (priceType == TYPE_LIMIT) {
            v_market_trade_tip?.visibility = View.GONE
            ll_price?.visibility = View.VISIBLE
            tv_convert_price?.visibility = View.VISIBLE
            tv_convert_price?.text = "--"
            ll_transaction?.visibility = View.VISIBLE
            tv_transaction_money?.text = "--"
            if (transactionType == TYPE_BUY) {
                et_volume?.hint = LanguageUtil.getString(context, "transaction_tip_buyVolume")
            } else {
                et_volume?.hint = LanguageUtil.getString(context, "common_text_sellVolume")
            }
            getAvailableBalance()
        } else {
            v_market_trade_tip?.visibility = View.VISIBLE
            ll_price?.visibility = View.GONE
            tv_convert_price?.visibility = View.INVISIBLE
            ll_transaction?.visibility = View.INVISIBLE
            tv_transaction_money?.text = "--"
            if (transactionType == TYPE_BUY) {
                et_volume?.hint = LanguageUtil.getString(context, "transaction_text_tradeSum")
            } else {
                et_volume?.hint = LanguageUtil.getString(context, "common_text_sellVolume")
            }
            getAvailableBalance()
        }

        var price_close = et_price?.text?.toString()
        LogUtil.d(TAG, "========buyOrSell======price_close is $price_close")
        tv_convert_price?.text = "${RateManager.getCNYByCoinMap(coinMapData, price_close)}"

        transactionType = transferType

        /**
         * 切换方向清空数量
         */
        rg_trade?.clearCheck()
        et_volume?.text?.clear()


        if (transferType == TYPE_BUY) {
            if (isLever) {
                cbtn_create_order?.textContent = "${LanguageUtil.getString(context, "contract_action_buy")}/${LanguageUtil.getString(context, "contract_action_long")}"
            } else {
                cbtn_create_order?.textContent = LanguageUtil.getString(context, "contract_action_buy")
            }

            cbtn_create_order?.normalBgColor = ColorUtil.getMainColorType()

            /**
             * 币种
             */
            tv_coin_name?.text =
                    if (priceType == TYPE_LIMIT) {
                        "${showCoinName()}"
                    } else {
                        "${showMarket()}"
                    }

            if (priceType == TYPE_LIMIT) {
                et_volume?.hint = LanguageUtil.getString(context, "transaction_tip_buyVolume")
            } else {
                et_volume?.hint = LanguageUtil.getString(context, "transaction_text_tradeSum")
            }
            /**
             * 设置 RadioButton 的选中效果
             */
            for (i in 0 until rg_trade.childCount step 2) {
                val radioButton = rg_trade?.getChildAt(i) as RadioButton
                radioButton.setTextColor(ColorUtil.getCheck4ColorStateList())
                radioButton.background = ColorUtil.getCheck4StateListDrawable()
            }

        } else {
            if (isLever) {
                cbtn_create_order?.textContent = "${LanguageUtil.getString(context, "contract_action_sell")}/${LanguageUtil.getString(context, "contract_action_short")}"
            } else {
                cbtn_create_order?.textContent = LanguageUtil.getString(context, "contract_action_sell")
            }

            cbtn_create_order?.normalBgColor = ColorUtil.getMainColorType(false)

            /**
             * 币种
             */
            tv_coin_name?.text = "${showCoinName()}"

            et_volume?.hint = LanguageUtil.getString(context, "common_text_sellVolume")

            /**
             * 设置 RadioButton 的选中效果
             */
            for (i in 0 until rg_trade.childCount step 2) {
                val radioButton = rg_trade?.getChildAt(i) as RadioButton
                radioButton.setTextColor(ColorUtil.getCheck4ColorStateList(isRise = false))
                radioButton.background = ColorUtil.getCheck4StateListDrawable(isRise = false)
            }
        }
    }

    fun changeSellOrBuyData(data: JSONObject) {
        LogUtil.d("getAvailableBalance", "getAvailableBalance==data is $data")

        val countCoinBalance = data.optString("countCoinBalance")
        val baseCoinBalance = data.optString("baseCoinBalance")
        if (transactionType == TYPE_BUY) {
            val coinName = NCoinManager.getMarketName(coinMapData?.optString("name", ""))
            var precision = NCoinManager.getCoinShowPrecision(coinName)
            if (TradeFragment.currentIndex == LEVER_INDEX_TAB) {
                precision = 8
            }
            canUseMoney = DecimalUtil.cutValueByPrecision(countCoinBalance
                    ?: "0", precision)

            NCoinManager.getMarketByName(showCoinName())
            tv_available_balance?.text = LanguageUtil.getString(context, "assets_text_available") + " $canUseMoney ${showMarket()}"

            tv_coin_name?.text = if (priceType == TYPE_LIMIT) "${showCoinName()}" else "${showMarket()}"
        } else {
            val coinName = NCoinManager.getMarketCoinName(coinMapData?.optString("name", ""))
            var precision = NCoinManager.getCoinShowPrecision(coinName)

            if (TradeFragment.currentIndex == LEVER_INDEX_TAB) {
                precision = 8
            }

            canUseMoney = DecimalUtil.cutValueByPrecision(baseCoinBalance
                    ?: "0", precision)
            tv_coin_name?.text = "${showCoinName()}"
            tv_available_balance?.text = LanguageUtil.getString(context, "assets_text_available") + " $canUseMoney ${showCoinName()}"
        }
    }

}




