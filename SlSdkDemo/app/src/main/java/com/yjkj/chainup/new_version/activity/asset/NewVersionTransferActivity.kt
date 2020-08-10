package com.yjkj.chainup.new_version.activity.asset

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.utils.MathHelper
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.db.constant.HomeTabMap
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.ActivityManager
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.DataManager
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.manager.*
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.CoinActivity
import com.yjkj.chainup.new_version.contract.ContractFragment
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.new_version.view.PwdSettingView
import com.yjkj.chainup.new_version.view.TRANSFER
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.DecimalDigitsInputFilter
import com.yjkj.chainup.util.NToastUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_version_transfer.*
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019/5/17-5:40 PM
 * @Email buptjinlong@163.com
 * @description 划转
 */
@Route(path = RoutePath.NewVersionTransferActivity)
class NewVersionTransferActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_new_version_transfer
    }


    @JvmField
    @Autowired(name = ParamConstant.TRANSFERSTATUS)
    var transferStatus = ""

    /**
     * 划转到的账户类型
     */
    var selectTransferPosition = 0

    var transferList = arrayListOf<String>()

    /**
     * 划转顺序  true  正向 币币到别的账户
     */
    var transferSequence = true
    var beanContract: ContractAccount? = null
    var bean: JSONObject? = null
    var amount = ""

    @JvmField
    @Autowired(name = ParamConstant.TRANSFERSYMBOL)
    var transferSymbol = ""

    @JvmField
    @Autowired(name = ParamConstant.TRANSFERCURRENCY)
    var transferCurrency = ""

    @JvmField
    @Autowired(name = ParamConstant.FROMBORROW)
    var fromBorrow = false


    var contractStatus = false

    var leverCoinList: ArrayList<String> = arrayListOf()
    var leverCoinListShowName: ArrayList<String> = arrayListOf()


    var leverDialog: TDialog? = null
    var selectLeverItem = 0

    /**
     * 选择币种时 的位置
     */
    var selectId = 0

    var otcTitle = ""


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        collapsing_toolbar?.run {
            setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.text_color))
            collapsing_toolbar?.setExpandedTitleColor(ContextCompat.getColor(mActivity, R.color.text_color))
            collapsing_toolbar?.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            collapsing_toolbar?.expandedTitleGravity = Gravity.BOTTOM
            collapsing_toolbar?.title = LanguageUtil.getString(this@NewVersionTransferActivity, "assets_action_transfer")
        }
        if (PublicInfoDataService.getInstance().getB2CSwitchOpen(null)) {
            otcTitle = LanguageUtil.getString(this, "assets_text_otc_forotc")
        } else {
            otcTitle = LanguageUtil.getString(this, "assets_text_otc")
        }

        tv_recharge_record?.text = LanguageUtil.getString(this, "transfer_text_record")
        tv_begin_title?.text = LanguageUtil.getString(this, "transfer_text_from")
        tv_end_title?.text = LanguageUtil.getString(this, "transfer_text_to")
        tv_all_title?.text = LanguageUtil.getString(this, "common_action_sendall")
        tv_symbol_title?.text = LanguageUtil.getString(this, "common_text_coinsymbol")
        tv_number_title?.text = LanguageUtil.getString(this, "charge_text_volume")
        et_number?.hint = LanguageUtil.getString(this, "transfer_tip_emptyVolume")
        cbtn_confirm?.setContent(LanguageUtil.getString(this, "common_text_btnConfirm"))

        if (PublicInfoDataService.getInstance().contractOpen(null)) {
            transferList.add(LanguageUtil.getString(this, "assets_text_contract"))
        }
        ActivityManager.pushAct2Stack(this@NewVersionTransferActivity)

        /**
         * 只有一个杠杆账户
         * 如果用户点击取消，返回上一页(脑残逻辑)
         */
        if (transferList.contains(LanguageUtil.getString(this, "leverage_asset")) && transferList.size == 1 && PublicInfoDataService.getInstance().hasShownLeverStatusDialog()) {
            NewDialogUtils.showLeverDialog(this,
                    listener = object : NewDialogUtils.DialogTransferBottomListener {
                        override fun sendConfirm() {

                        }

                        override fun showCancel() {
                            finish()
                        }

                    })
        }


        loadData()
        initView()
        setClickListener()
    }

    private fun doCoinVerifyTips(): Boolean {
        val contractAccount = ContractUserDataAgent.getContractAccount(transferSymbol)
        if (contractAccount == null) {
            NToastUtil.showTopToast(false, LanguageUtil.getString(this, "sl_str_transfer_tips"))
            beanContract = null
            return false
        }
        return true
    }


    override fun loadData() {
        contractStatus = PublicInfoDataService.getInstance().contractOpen(null)
    }


    override fun initView() {
        cbtn_confirm?.isEnable(false)
        tv_begin_content?.text = LanguageUtil.getString(this, "assets_text_exchange")

        iv_change_account_arr_down?.visibility = View.GONE

        when (transferStatus) {
            ParamConstant.TRANSFER_BIBI -> {
                if (PublicInfoDataService.getInstance().isLeverOpen(null)) {
                    selectTransferPosition = 1
                } else {
                    selectTransferPosition = 0
                }
                et_number?.filters = arrayOf(DecimalDigitsInputFilter(NCoinManager.getCoinShowPrecision(transferSymbol)))
                var coinlist = DataManager.getCoinsFromDB(true)
                coinlist.sortedBy { it.sort }

                if (transferSymbol.isEmpty()) {
                    transferSymbol = coinlist[0].name
                    accountGetCoin4OTC(transferSymbol)
                } else {
                    coinlist.forEach {
                        if (transferSymbol == it.name) {
                            accountGetCoin4OTC(transferSymbol)
                            if (PublicInfoDataService.getInstance().otcOpen(null)) {
                                tv_end_content?.text = otcTitle
                            } else {
                                tv_end_content?.text = LanguageUtil.getString(this, "assets_text_contract")
                            }
                            psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                            tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
                            return
                        }
                    }
                    transferSymbol = coinlist[0].name
                    accountGetCoin4OTC(transferSymbol)
                }
                if (PublicInfoDataService.getInstance().otcOpen(null)) {
                    tv_end_content?.text = otcTitle
                } else {
                    tv_end_content?.text = LanguageUtil.getString(this, "assets_text_contract")
                }
                psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
            }
            ParamConstant.TRANSFER_OTC -> {
                if (PublicInfoDataService.getInstance().isLeverOpen(null)) {
                    selectTransferPosition = 1
                } else {
                    selectTransferPosition = 0
                }
                et_number?.filters = arrayOf(DecimalDigitsInputFilter(NCoinManager.getCoinShowPrecision(transferSymbol)))
                var coinlist = DataManager.getCoinsFromDB(true)
                coinlist.sortedBy { it.sort }

                if (transferSymbol.isEmpty()) {
                    transferSymbol = coinlist[0].name
                    accountGetCoin4OTC(transferSymbol)
                } else {
                    var status = false
                    coinlist.forEach {
                        if (transferSymbol == it.name) {
                            accountGetCoin4OTC(transferSymbol)
                            tv_end_content?.text = otcTitle
                            accountGetCoin4OTC(transferSymbol)
                            psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                            tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
                            return
                        }
                    }
                    transferSymbol = coinlist[0].name
                    accountGetCoin4OTC(transferSymbol)
                }

                tv_end_content?.text = otcTitle
                accountGetCoin4OTC(transferSymbol)
                psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
            }
            ParamConstant.TRANSFER_CONTRACT -> {
                if (PublicInfoDataService.getInstance().isLeverOpen(null)) {
                    if (PublicInfoDataService.getInstance().otcOpen(null)) {
                        selectTransferPosition = 2
                    } else {
                        selectTransferPosition = 1
                    }
                } else if (PublicInfoDataService.getInstance().otcOpen(null)) {
                    selectTransferPosition = 1
                } else {
                    selectTransferPosition = 0
                }

                if (transferSymbol.isEmpty()) {
                    //加载列表 第0个为默认
                }
                //获取币币
                accountGetCoin4Contract(transferSymbol)
                //获取合约
                getAccount4Contract()
                tv_end_content?.text = LanguageUtil.getString(this, "assets_text_contract")
                psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
            }
            ParamConstant.LEVER_INDEX -> {
                if (transferSequence) {
                    et_number?.filters = arrayOf(DecimalDigitsInputFilter(NCoinManager.getCoinShowPrecision(transferSymbol)))
                } else {
                    et_number?.filters = arrayOf(DecimalDigitsInputFilter(ParamConstant.NORMAL_PRECISION))
                }

                selectTransferPosition = 0
                tv_currency_title?.visibility = View.VISIBLE
                psv_currency?.visibility = View.VISIBLE
                tv_end_content?.text = LanguageUtil.getString(this, "leverage_asset")
                psv_currency?.setEditText(NCoinManager.getShowMarketName(transferCurrency))
                getBalanceList()
            }
        }


        et_number?.isFocusable = true
        et_number?.isFocusableInTouchMode = true
        et_number?.setOnFocusChangeListener { v, hasFocus ->
            v_number_line?.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
    }


    fun setClickListener() {
        iv_transfer_change?.setOnClickListener {
            transferSequence = !transferSequence
            iv_change_account_arr_up?.visibility = View.GONE
            iv_change_account_arr_down?.visibility = View.GONE
            iv_change_account_arr_up?.visibility = View.GONE
            iv_change_account_arr_down?.visibility = View.GONE
            var startCotent = tv_begin_content?.text.toString()
            var endContent = tv_end_content?.text.toString()
            tv_begin_content?.text = endContent
            tv_end_content?.text = startCotent
            when (transferStatus) {
                ParamConstant.TRANSFER_BIBI, ParamConstant.TRANSFER_OTC -> {
                    if (transferSequence) {
                        setMoreNumberContent(bean?.optString("exNormal", "") ?: "")
                    } else {
                        setMoreNumberContent(bean?.optString("otcNormal", "") ?: "")
                    }
                }
                ParamConstant.TRANSFER_CONTRACT -> {
                    getAccount4Contract()
                }
                ParamConstant.LEVER_INDEX -> {
                    var normalBalance = "0"
                    if (transferSequence) {
                        when (selectLeverItem) {
                            0 -> {
                                normalBalance = symbolJSONObject.optString("baseExNormalBalance", "")
                            }
                            1 -> {
                                normalBalance = symbolJSONObject.optString("quoteEXNormalBalance", "")
                            }
                        }
                        et_number?.filters = arrayOf(DecimalDigitsInputFilter(NCoinManager.getCoinShowPrecision(transferSymbol)))
                    } else {
                        when (selectLeverItem) {
                            0 -> {
                                normalBalance = symbolJSONObject.optString("baseCanTransfer", "")
                            }
                            1 -> {
                                normalBalance = symbolJSONObject.optString("quoteCanTransfer", "")
                            }
                        }
                        et_number?.filters = arrayOf(DecimalDigitsInputFilter(ParamConstant.NORMAL_PRECISION))
                    }
                    et_number?.setText("")
                    setMoreNumberContent(normalBalance)
                }

            }

        }
        et_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var value = s.toString()
                amount = s.toString()
                if (amount.isNotEmpty()) {
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length - 1)
                    }
                    if (BigDecimalUtils.compareTo(value, "0") == 0) {
                        cbtn_confirm?.isEnable(false)
                    } else {
                        cbtn_confirm?.isEnable(true)
                    }
                } else {
                    cbtn_confirm?.isEnable(false)
                }
            }

        })

        /**
         * 这里监听币种
         */
        psv_symbol?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {
                return text
            }

            override fun returnItem(item: Int) {

            }

            override fun onclickImage() {
                when (transferStatus) {
                    ParamConstant.TRANSFER_BIBI, ParamConstant.TRANSFER_OTC -> {
                        CoinActivity.enter4Result(this@NewVersionTransferActivity, transferSymbol, false, 0, false, CoinActivity.OTC_TYPE)
                    }
                    ParamConstant.TRANSFER_CONTRACT -> {
                        CoinActivity.enter4Result(this@NewVersionTransferActivity, transferSymbol, false, 0, false, CoinActivity.OTC_CONTRACT)
                    }
                    ParamConstant.LEVER_INDEX -> {
                        leverDialog = NewDialogUtils.showBottomListDialog(this@NewVersionTransferActivity, leverCoinListShowName, selectLeverItem, object : NewDialogUtils.DialogOnclickListener {
                            override fun clickItem(data: ArrayList<String>, item: Int) {
                                et_number?.setText("")
                                selectLeverItem = item
                                transferSymbol = leverCoinList[item]
                                if (transferSequence) {
                                    setEdittextFilter(NCoinManager.getCoinShowPrecision(transferSymbol))
                                } else {
                                    setEdittextFilter(ParamConstant.NORMAL_PRECISION)
                                }
                                psv_symbol?.setEditText(NCoinManager.getShowMarket(leverCoinList[item]))
                                tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
                                setLeverMaxTransferContent()
                                leverDialog?.dismiss()
                            }
                        })
                    }
                }
            }
        }

        /**
         * 这里监听币对
         */
        psv_currency?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {
                return text
            }

            override fun returnItem(item: Int) {

            }

            override fun onclickImage() {
                ArouterUtil.navigation4Result(RoutePath.CoinMapActivity, Bundle().apply {
                    putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER, true)
                    putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER_UNREFRESH, false)
                }, mActivity, ParamConstant.BORROW_TYPE)
            }
        }


        /**
         * 全部
         */
        tv_all_title?.setOnClickListener {

            when (transferStatus) {
                ParamConstant.TRANSFER_BIBI, ParamConstant.TRANSFER_OTC -> {
                    if (transferSequence) {
                        amount = bean?.optString("exNormal", "") ?: ""
                    } else {
                        amount = bean?.optString("otcNormal", "") ?: ""
                    }
                    et_number?.setText(BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(amount, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString()))
                }
                ParamConstant.TRANSFER_CONTRACT -> {
                    if (transferSequence) {
                        amount = bean?.optString("exNormal", "") ?: ""
                    } else {
                        amount = beanContract?.getCanWithdraw(1.05).toString()
                    }
                    et_number?.setText(BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(amount, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString()))
                }
                ParamConstant.LEVER_INDEX -> {
                    var normalBalance = ""
                    var leverBalance = ""
                    when (selectLeverItem) {
                        0 -> {
                            normalBalance = symbolJSONObject.optString("baseExNormalBalance", "")
                            leverBalance = symbolJSONObject.optString("baseCanTransfer", "")
                        }
                        1 -> {
                            normalBalance = symbolJSONObject.optString("quoteEXNormalBalance", "")
                            leverBalance = symbolJSONObject.optString("quoteCanTransfer", "")
                        }
                    }
                    if (transferSequence) {
                        amount = normalBalance
                    } else {
                        amount = leverBalance
                    }
                    if (transferSequence) {
                        et_number?.setText(BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(amount, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString()))
                    } else {
                        et_number?.setText(BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(amount, ParamConstant.NORMAL_PRECISION).toPlainString()))
                    }
                }
            }

        }

        cbtn_confirm?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                if (BigDecimalUtils.compareTo(moreBalance, amount) == 1 || BigDecimalUtils.compareTo(moreBalance, amount) == 0) {
                    when (transferStatus) {
                        ParamConstant.TRANSFER_BIBI, ParamConstant.TRANSFER_OTC -> {
                            if (transferSequence) {
                                transher4OTC(ParamConstant.BIBI_ACCOUNT, ParamConstant.OTC_ACCOUNT, amount, transferSymbol)
                            } else {
                                transher4OTC(ParamConstant.OTC_ACCOUNT, ParamConstant.BIBI_ACCOUNT, amount, transferSymbol)
                            }
                        }
                        ParamConstant.TRANSFER_CONTRACT -> {
                            if (!doCoinVerifyTips()) {
                                return
                            }
                            if (transferSequence) {
                                accountCapitalTransfer(transferSymbol, "wallet_to_contract", amount)
                            } else {
                                accountCapitalTransfer(transferSymbol, "contract_to_wallet", amount)
                            }
                        }
                        ParamConstant.LEVER_INDEX -> {
                            var coin = ""
                            when (selectLeverItem) {
                                0 -> {
                                    coin = symbolJSONObject.optString("baseCoin", "")
                                }
                                1 -> {
                                    coin = symbolJSONObject.optString("quoteCoin", "")
                                }
                            }
                            if (transferSequence) {
                                accountTransfer4Lever(ParamConstant.BIBI_ACCOUNT, ParamConstant.LEVERAGE_ACCOUNT, amount, coin, transferCurrency)
                            } else {
                                accountTransfer4Lever(ParamConstant.LEVERAGE_ACCOUNT, ParamConstant.BIBI_ACCOUNT, amount, coin, transferCurrency)
                            }
                        }
                    }
                } else {
                    NToastUtil.showTopToast(false, LanguageUtil.getString(mActivity, "common_tip_balanceNotEnough"))
                }

            }
        }

        /**
         * 合约增加
         */
        tv_contract_coupon_tips.setOnClickListener {
            NewDialogUtils.showSingleDialog(this@NewVersionTransferActivity,getString(R.string.contract_tips_experienceGold),object: NewDialogUtils.DialogBottomListener{
                override fun sendConfirm() {
                }

            },getString(R.string.contract_swap_gift),"")
        }
    }

    var tDialog: TDialog? = null



    /**
     * 初始化数据  币币转otc
     */
    private fun initData(t: JSONObject) {
        if (!isFinishing && !isDestroyed) {
            if (transferSequence) {
                setMoreNumberContent(t.optString("exNormal", ""))
            } else {
                setMoreNumberContent(t.optString("otcNormal", ""))
            }
        }
    }

    /**
     * 初始化数据  币币转合约
     */
    private fun initContractAccount() {
        if (!isFinishing && !isDestroyed) {
            doCoinVerifyTips()
            tv_contract_coupon_tips.visibility = View.GONE
            if (transferSequence) {
                //币币
                setMoreNumberContent(bean?.optString("exNormal", "") ?: "0")
            } else {
                //合约
                if (beanContract != null) {
                    setMoreNumberContent(beanContract?.getCanWithdraw(1.05).toString())
                    //当划划出账户为合约账户，且当前选择的币种体验金余额不为0时 显示合约增加提示文案
                    if (PublicInfoDataService.getInstance().contractCouponOpen(null) && MathHelper.round(beanContract!!.bonus_vol) > 0) {
                        tv_contract_coupon_tips.visibility = View.VISIBLE
                        tv_contract_coupon_tips.text = "(" + getLineText("contract_tips_noExperience") + " "+BigDecimalUtils.divForDown(beanContract!!.bonus_vol, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString() + beanContract!!.coin_code + ")"
                    }
                } else {
                    setMoreNumberContent("0")
                }
            }
        }
    }

    var moreBalance = "0"
    fun setMoreNumberContent(balance: String) {
        moreBalance = balance
        when (transferStatus) {
            ParamConstant.TRANSFER_BIBI, ParamConstant.TRANSFER_OTC, ParamConstant.TRANSFER_CONTRACT -> {
                tv_max_more_number_content?.text = LanguageUtil.getString(mActivity, "transfer_tip_maxTransfer") + BigDecimalUtils.divForDown(balance, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString() + NCoinManager.getShowMarket(transferSymbol)
            }
            ParamConstant.LEVER_INDEX -> {
                if (transferSequence) {
                    tv_max_more_number_content?.text = LanguageUtil.getString(mActivity, "transfer_tip_maxTransfer") + BigDecimalUtils.divForDown(balance, NCoinManager.getCoinShowPrecision(transferSymbol)).toPlainString() + NCoinManager.getShowMarket(transferSymbol)
                } else {
                    tv_max_more_number_content?.text = LanguageUtil.getString(mActivity, "transfer_tip_maxTransfer") + BigDecimalUtils.divForDown(balance, ParamConstant.NORMAL_PRECISION).toPlainString() + NCoinManager.getShowMarket(transferSymbol)
                }
            }
        }

    }

    /**
     * 划转成功
     */
    fun transferSuc() {
        NewDialogUtils.showNormalTransferDialog(this, LanguageUtil.getString(mActivity, "transfer_text_guideTransaction"), object : NewDialogUtils.DialogTransferBottomListener {
            override fun showCancel() {
                if (null != fromBorrow && fromBorrow) {
                    ArouterUtil.navigation(RoutePath.NewVersionBorrowingActivity, Bundle().apply {
                        putString(ParamConstant.symbol, NCoinManager.getName4Symbol(transferCurrency))
                    })
                }
                finish()
            }

            override fun sendConfirm() {
                /**
                 * 这里去交易
                 */
                if (transferStatus == ParamConstant.TRANSFER_CONTRACT) {
                    /**
                     * 合约
                     */
                    if (transferSequence) {
                        /**
                         * 币币到合约
                         */
                        forwardContractTab()
                        ContractFragment.liveData4Contract.postValue(Contract2PublicInfoManager.currentContract(""))
                    } else {
                        /**
                         * 合约到币币
                         */
                        val messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
                        val bundle = Bundle()
                        val homeTabType = HomeTabMap.maps.get(HomeTabMap.coinTradeTab) ?: 2
                        bundle.putInt(ParamConstant.homeTabType, homeTabType)//跳转到币币交易页面
                        bundle.putInt(ParamConstant.transferType, ParamConstant.TYPE_BUY)
                        bundle.putString(ParamConstant.symbol, NCoinManager.getSymbol(transferSymbol))
                        messageEvent.msg_content = bundle
                        messageEvent.isLever = false
                        EventBusUtil.post(messageEvent)
                    }
                } else if (transferStatus == ParamConstant.LEVER_INDEX) {
                    /**
                     * 这里跳转杠杆页面
                     */
                    val messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
                    val bundle = Bundle()
                    val homeTabType = HomeTabMap.maps.get(HomeTabMap.coinTradeTab) ?: 2
                    bundle.putInt(ParamConstant.homeTabType, homeTabType)//跳转到币币交易页面
                    bundle.putInt(ParamConstant.transferType, ParamConstant.TYPE_BUY)
                    bundle.putString(ParamConstant.symbol, transferCurrency)
                    if (transferSequence) {
                        bundle.putInt(ParamConstant.COIN_TRADE_TAB_INDEX, ParamConstant.LEVER_INDEX_TAB)
                    } else {
                        bundle.putInt(ParamConstant.COIN_TRADE_TAB_INDEX, ParamConstant.CVC_INDEX_TAB)
                    }

                    messageEvent.msg_content = bundle
                    /**
                     * transferSequence = true 币币到杠杆  跳转到杠杆
                     * transferSequence = false 杠杆到币币 跳转到币币
                     */
                    messageEvent.isLever = transferSequence
                    EventBusUtil.post(messageEvent)
                    ActivityManager.popAllActFromStack()

                } else {
                    /**
                     * 法币
                     */
                    if (transferSequence) {
                        /**
                         * 币币到法币
                         */
                        setEvent(ParamConstant.TYPE_FAIT, transferSymbol)
                    } else {
                        /**
                         * 法币到币币
                         */
                        val messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
                        val bundle = Bundle()
                        val homeTabType = HomeTabMap.maps.get(HomeTabMap.coinTradeTab) ?: 2
                        bundle.putInt(ParamConstant.homeTabType, homeTabType)//跳转到币币交易页面
                        bundle.putInt(ParamConstant.transferType, ParamConstant.TYPE_BUY)
                        bundle.putString(ParamConstant.symbol, NCoinManager.getSymbol(transferSymbol))
                        messageEvent.msg_content = bundle
                        messageEvent.isLever = false
                        EventBusUtil.post(messageEvent)
                    }
                }

                assetsActivityFinish()

                finish()
            }
        }, "", LanguageUtil.getString(mActivity, "transfer_action_goTransaction"))
    }

    /*
     * 通知资产页面finish
     */
    private fun assetsActivityFinish() {
        var msgEvent = MessageEvent(MessageEvent.assets_activity_finish_event)
        EventBusUtil.post(msgEvent)
    }

    fun setEvent(position: Int, content: String) {
        var message = MessageEvent(MessageEvent.coin_payment)
        var json = JSONObject()
        json.put("position", position)
        json.put("content", content)
        message.msg_content = json
        EventBusUtil.post(message)
    }


    private fun forwardContractTab() {
        var messageEvent = MessageEvent(MessageEvent.hometab_switch_type)

        var bundle = Bundle()
        val homeTabType = HomeTabMap.maps.get(HomeTabMap.contractTab) ?: 3
        bundle.putInt(ParamConstant.homeTabType, homeTabType)
        messageEvent.msg_content = bundle
        EventBusUtil.post(messageEvent)
    }

    var symbolJSONObject = JSONObject()

    /**
     * 获取杠杆账户列表
     */
    fun getBalanceList() {
        addDisposable(getMainModel().getBalance4Lever(transferCurrency, object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                symbolJSONObject = jsonObject.optJSONObject("data")

                initLeverTransfer()
            }

        }))
    }

    var moreNumber = ""
    var leverMoreNumber = ""

    fun initLeverTransfer() {
        selectLeverItem = 0
        leverCoinList.clear()
        leverCoinListShowName.clear()
        leverCoinList.add(symbolJSONObject.optString("baseCoin"))
        leverCoinListShowName.add(NCoinManager.getShowMarket(symbolJSONObject.optString("baseCoin")))
        leverCoinList.add(symbolJSONObject.optString("quoteCoin"))
        leverCoinListShowName.add(NCoinManager.getShowMarket(symbolJSONObject.optString("quoteCoin")))

        transferSymbol = leverCoinList[selectLeverItem]
        if (transferSequence) {
            setEdittextFilter(NCoinManager.getCoinShowPrecision(transferSymbol))
        } else {
            setEdittextFilter(ParamConstant.NORMAL_PRECISION)
        }


        psv_currency?.setEditText(NCoinManager.getShowMarketName(symbolJSONObject.optString("name", "")))
        psv_symbol?.setEditText(NCoinManager.getShowMarket(symbolJSONObject.optString("baseCoin", "")))

        psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
        tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
        setLeverMaxTransferContent()
    }

    /**
     * 杠杆情况下 切换基准货币和计价货币
     */
    fun setLeverMaxTransferContent() {
        when (selectLeverItem) {
            ParamConstant.GOOGLE_TYPE -> {
                if (transferSequence) {
                    moreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("baseExNormalBalance"), NCoinManager.getCoinShowPrecision(leverCoinList[selectLeverItem])).toPlainString()
                    leverMoreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("baseCanTransfer"), NCoinManager.getCoinShowPrecision(leverCoinList[selectLeverItem])).toPlainString()
                } else {
                    moreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("baseExNormalBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()
                    leverMoreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("baseCanTransfer"), ParamConstant.NORMAL_PRECISION).toPlainString()
                }

            }
            ParamConstant.MOBILE_TYPE -> {
                if (transferSequence) {
                    moreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("quoteEXNormalBalance"), NCoinManager.getCoinShowPrecision(leverCoinList[selectLeverItem])).toPlainString()
                    leverMoreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("quoteCanTransfer"), NCoinManager.getCoinShowPrecision(leverCoinList[selectLeverItem])).toPlainString()
                } else {
                    moreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("quoteEXNormalBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()
                    leverMoreNumber = BigDecimalUtils.divForDown(symbolJSONObject.optString("quoteCanTransfer"), ParamConstant.NORMAL_PRECISION).toPlainString()
                }
            }
        }
        if (transferSequence) {
            setMoreNumberContent(moreNumber)
        } else {
            setMoreNumberContent(leverMoreNumber)
        }
    }


    /**
     * 划转资金
     */
    fun transher4OTC(fromAccount: String, toAccount: String, amount: String, coinSymbol: String) {

        addDisposable(getMainModel().transher4OTC(fromAccount, toAccount, amount, coinSymbol, object : NDisposableObserver(this, true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                et_number?.setText("")
                accountGetCoin4OTC(transferSymbol)
                transferSuc()
            }
        }))
    }

    /**
     * 合约划转
     */
    fun accountCapitalTransfer(coinSymbol: String, transferType: String, amount: String) {
        cbtn_confirm?.showLoading()
        HttpClient.instance
                .doAssetExchange(coinSymbol = coinSymbol, transferType = transferType, amount = amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<Any>() {
                    override fun onHandleSuccess(bean: Any?) {
                        cbtn_confirm?.hideLoading()
                        et_number?.setText("")
                        transferSuc()
                        ContractUserDataAgent.getContractAccounts(true)
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        cbtn_confirm?.hideLoading()
                        super.onHandleError(code, msg)
                        NToastUtil.showToast(msg, false)
                    }
                })
    }

    /**
     * 杠杆划转
     */
    fun accountTransfer4Lever(fromAccount: String,
                              toAccount: String,
                              amount: String,
                              coinSymbol: String,
                              symbol: String) {
        addDisposable(getMainModel().setTransfer4Lever(fromAccount, toAccount, amount, coinSymbol, symbol, object : NDisposableObserver(this, true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {

                getBalanceList()
                transferSuc()
            }

        }))
    }


    /**
     * 合约账户余额
     */
    fun getAccount4Contract() {
        if (!UserDataService.getInstance().isLogined) {
            return
        }
        beanContract = ContractUserDataAgent.getContractAccount(transferSymbol)
        initContractAccount()
    }

    /**
     * 划转资金  获取
     */
    fun accountGetCoin4OTC(coin: String) {
        if (!UserDataService.getInstance().isLogined) {
            return
        }

        addDisposable(getMainModel().accountGetCoin4OTC(coin, object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var t = jsonObject.optJSONObject("data")
                if (t != null) {
                    bean = t
                    initData(t)
                }
            }

        }))
    }

    fun accountGetCoin4Contract(coin: String) {
        if (!UserDataService.getInstance().isLogined) {
            return
        }

        addDisposable(getMainModel().accountGetCoin4OTC(coin, object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var t = jsonObject.optJSONObject("data")
                if (t != null) {
                    bean = t
                    initContractAccount()
                }
            }

        }))
    }

    fun setEdittextFilter(temp: Int) {
        et_number?.filters = arrayOf(DecimalDigitsInputFilter(temp))
    }

    /**
     * 接收币种
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ParamConstant.BORROW_TYPE) {
                transferCurrency = data?.getStringExtra(ParamConstant.symbol) ?: ""
                tv_currency_title?.visibility = View.VISIBLE
                psv_currency?.visibility = View.VISIBLE
                for (temp in 0 until transferList.size) {
                    if (LanguageUtil.getString(mActivity, "leverage_asset") == transferList[temp]) {
                        selectTransferPosition = temp
                    }
                }
                transferStatus = ParamConstant.LEVER_INDEX
                if (transferSequence) {
                    tv_end_content?.text = transferList[selectTransferPosition]
                } else {
                    tv_begin_content?.text = transferList[selectTransferPosition]
                }

                getBalanceList()
            } else {
                var position = 0
                if (data != null) {
                    position = data.getIntExtra(CoinActivity.SELECTED_ID, 0)
                    var coin = data.getStringExtra(CoinActivity.SELECTED_COIN)
                            ?: LanguageUtil.getString(mActivity, "b2c_text_changecoin")
                    transferSymbol = coin
                    setEdittextFilter(NCoinManager.getCoinShowPrecision(transferSymbol))
                }
                selectId = position
                et_number?.filters = arrayOf(DecimalDigitsInputFilter(NCoinManager.getCoinShowPrecision(transferSymbol)))
                psv_symbol?.setEditText(NCoinManager.getShowMarket(transferSymbol))
                tv_number_coin?.text = NCoinManager.getShowMarket(transferSymbol)
                //因为加入合约，此处特殊处理
                if (transferStatus == ParamConstant.TRANSFER_CONTRACT) {
                    if (transferSequence) {
                        accountGetCoin4OTC(transferSymbol)
                    } else {
                        getAccount4Contract()
                    }
                    doCoinVerifyTips()
                } else {
                    //非合约。走原来逻辑
                    accountGetCoin4OTC(transferSymbol)
                }

            }
            et_number?.setText("")
        }


    }

}
