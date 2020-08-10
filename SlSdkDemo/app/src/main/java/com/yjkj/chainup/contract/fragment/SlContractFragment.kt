package com.yjkj.chainup.contract.fragment

import android.os.Handler
import android.support.v4.app.Fragment
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.impl.*
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.listener.OnBindViewListener
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlContractKlineActivity
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.ContractCoinSearchDialog
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.fragment_sl_contract.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * 合约
 */
class SlContractFragment : NBaseFragment() {

    //tab类型  0 开仓  1 平仓  2 持仓
    private var currentIndex = 0
    private var tradeFragment = SlContractTradeFragment()
    private var holdFragment = SlContractHoldFragment()
    private var currentFragment = Fragment()

    //左边币种选择弹窗
    private var selectDialog: ContractCoinSearchDialog? = null
    private var contractAccountDialog: TDialog? = null

    //合约id
    private var mContractId = 0
    var mContract: Contract? = null
    var updateInputPrice: Boolean = true

    private var pushJumpToType = -1


    override fun setContentView(): Int {
        return R.layout.fragment_sl_contract
    }

    override fun initView() {
        initAutoStringView()
        tradeFragment = SlContractTradeFragment()
        holdFragment = SlContractHoldFragment()
        showFragment()
        initListener()
        Handler().postDelayed({
            onContractTickerChanged()
        },200)
    }


    /**
     * 文本动态初始化
     */
    private fun initAutoStringView() {
        rb_open_position.onLineText("contract_text_openAverage")
        rb_close_position.onLineText("sl_str_close")
        rb_hold_position.onLineText("sl_str_position")
    }

    override fun loadData() {
        super.loadData()
        ContractPublicDataAgent.registerTickerWsListener(this,object:ContractTickerListener(){
            /**
             * 合约Ticker更新
             */
            override fun onWsContractTicker(ticker: ContractTicker) {
                if (isHidden || !isVisible || !isResumed) {
                    return
                }
                if(ticker.instrument_id != mContractId){
                    return
                }
                tradeFragment.updateContractTicker(ticker, false)
                holdFragment.updateContractTicker()
                updateHeaderView(ticker)
            }

        })

        ContractSDKAgent.registerContractSDKListener(object:ContractSDKListener(){
            override fun sdkInitSuccess() {
                onContractTickerChanged()
            }

        })

        ContractUserDataAgent.registerContractAccountWsListener(this,object : ContractAccountListener(){
            /**
             * 合约账户ws有更新，
             */
            override fun onWsContractAccount(contractAccount: ContractAccount?) {
                Handler().postDelayed({
                    tradeFragment.updateAvailableVol()
                    tradeFragment.updateUserAssetUI()
                }, 1000)

                updateHoldTabCount()
            }

        })


        ContractUserDataAgent.registerContractPositionWsListener(this,object:ContractPositionListener(){
            /**
             * 合约仓位更新
             */
            override fun onWsContractPosition(instrumentId:Int?) {
                tradeFragment.updateAvailableVol()
                tradeFragment.updateUserAssetUI()
                updateHoldTabCount()
            }

        })

        ContractSDKAgent.registerSDKUserStatusListener(this,object:ContractUserStatusListener(){
            /**
             * 合约SDK登录成功
             */
            override fun onContractLoginSuccess() {
//                if (isVisible) {
//                    createContractAccount()
//                }
                tradeFragment.updateBtnUI()
            }

            /**
             * 合约SDK退出登录
             */
            override fun onContractExitLogin() {
                UserDataService.getInstance().clearTokenByContract()
                LoginManager.checkLogin(ContractSDKAgent.context, true)
                rb_hold_position.text = getLineText("sl_str_position")
                contractAccountDialog?.dismiss()
                tradeFragment.updateBtnUI()
            }

        })
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            hasShowCreateContractDialog = false
            ContractPublicDataAgent.subscribeDepthWs(mContractId)
            createContractAccount()
        }
    }


    var hasShowCreateContractDialog = false

    /**
     * 创建合约账户
     */
    fun createContractAccount() {
        if (hasShowCreateContractDialog) {
            return
        }
        if (mContractId == 0) {
            return
        }
        if (UserDataService.getInstance().isLogined) {
            val contract: Contract = ContractPublicDataAgent.getContract(mContractId) ?: return
            val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(contract.margin_coin)
            if (contractAccount == null) {
                hasShowCreateContractDialog = true
                contractAccountDialog = SlDialogHelper.showCreateContractAccountDialog(activity!!, OnBindViewListener { viewHolder ->
                    viewHolder?.let {
                        it.getView<TextView>(R.id.tv_title).onLineText("sl_str_risk_disclosure")
                        it.getView<TextView>(R.id.tv_content).text = getLineText("sl_str_risk_disclosure_notice", true)
                        it.getView<TextView>(R.id.tv_confirm_btn).onLineText("sl_str_open_contract_account_btn")

                        it.getView<TextView>(R.id.tv_content).movementMethod = ScrollingMovementMethod.getInstance()
                        it.getView<TextView>(R.id.tv_confirm_btn).setOnClickListener {
                            contractAccountDialog?.dismiss()

                            doCreateContractAccount()
                        }
                    }
                })
            } else {
                hasShowCreateContractDialog = true
            }
        }
    }

    private fun doCreateContractAccount() {
        ContractUserDataAgent.doCreateContractAccount(mContractId,object:IResponse<String>(){
            override fun onSuccess(data: String) {
                ToastUtils.showToast(ContractSDKAgent.context, getLineText("sl_str_account_created_successfully"))
            }

            override fun onFail(code: String, msg: String) {
                NToastUtil.showToast(msg, false)
            }

        })
    }

    /**
     * 更新头部View
     */
    private fun updateHeaderView(ticker: ContractTicker) {
        if (tv_contract == null) {
            return
        }
        mContract?.let {
            //合约名称
            tv_contract.text = it.getDisplayName(mActivity)
            val decimalFormat = DecimalFormat("###################.###########", DecimalFormatSymbols(Locale.ENGLISH))
            val dfPrice = NumberUtil.getDecimal(it.price_index-1)
            val riseFallRate: Double = MathHelper.round(ticker.change_rate.toDouble() * 100, 2)
            val sRate = if (riseFallRate >= 0) "+" + NumberUtil.getDecimal(2).format(riseFallRate) + "%" else NumberUtil.getDecimal(2).format(riseFallRate) + "%"
            val color = ColorUtil.getMainColorType(riseFallRate >= 0)
            //最新成交价
            tv_last_price.text = dfPrice.format(MathHelper.round(ticker.last_px))
            tv_last_price.textColor = color
            //百分比
            tv_rate.text = sRate
            tv_rate.textColor = color
            tv_rate.backgroundResource = ColorUtil.getContractRateDrawable(riseFallRate >= 0)
            // LogUtil.d("lb","ticker.last_px:"+ticker.last_px)
        }

    }


    private fun initListener() {
        /**
         * 切换开仓 平仓 持仓
         */
        rg_buy_sell?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_open_position -> {
                    currentIndex = 0
                    tradeFragment.doSwitchTab(currentIndex)
                    showFragment()
                }
                R.id.rb_close_position -> {
                    currentIndex = 1
                    tradeFragment.doSwitchTab(currentIndex)
                    showFragment()
                }
                R.id.rb_hold_position -> {
                    if (LoginManager.checkLogin(mContext, true)) {
                        currentIndex = 2
                        showFragment()
                    } else {
                        rg_buy_sell?.check(R.id.rb_open_position)
                    }
                }
            }
        }
        /**
         * 侧边栏
         */
        tv_contract.setOnClickListener {
            showLeftCoinWindow()
        }
        iv_more.setOnClickListener {
            SlDialogHelper.createContractSetting(activity, iv_more, mContractId)
        }
        /**
         * K线
         */
        ib_kline.setOnClickListener {
            SlContractKlineActivity.show(mActivity!!, mContractId)
        }
    }


    /*
     * 处理线程跟发消息线程一致
     * 子类重载
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    override fun onMessageEvent(event: MessageEvent) {
        when (event.msg_type) {
            MessageEvent.sl_contract_left_coin_type -> {
                if (event.msg_content is ContractTicker) {
                    LogUtil.d("DEBUG", "--切换合约--" + event.msg_content)
                    val contractTicker = event.msg_content as ContractTicker
                    if (contractTicker.instrument_id != mContractId) {
                        mContractId = contractTicker.instrument_id
                        mContract = ContractPublicDataAgent.getContract(mContractId)
                        //订阅深度
                        ContractPublicDataAgent.subscribeDepthWs(mContractId)
                        //先走缓存
                        updateInputPrice = true
                        updateContractUi(contractTicker, true)
                        //在走接口
                        loadContractTickerData()
                        hasShowCreateContractDialog = false
                        createContractAccount()
                    }
                }
            }
        }
    }


    /**
     * 更新仓位tab数量
     */
    private fun updateHoldTabCount() {
        val count = holdFragment.getPositionCount()
        if (count > 0) {
            rb_hold_position.text = Html.fromHtml("<font> ${getLineText("sl_str_position")} <small>[$count]</small> </font>")
        } else {
            rb_hold_position.text = getLineText("sl_str_position")
        }
    }


    private fun showLeftCoinWindow() {
        if (Utils.isFastClick())
            return
        selectDialog = ContractCoinSearchDialog()
        selectDialog?.showDialog(childFragmentManager, "SlContractFragment")
    }

    private fun showFragment() {
        val transaction = childFragmentManager.beginTransaction()
        if (currentIndex == 2) {
            if (!holdFragment.isAdded) {
                transaction.hide(currentFragment).add(R.id.fragment_container, holdFragment, "2")
            } else {
                transaction.hide(currentFragment).show(holdFragment)
            }
            currentFragment = holdFragment
        } else {
            if (currentFragment is SlContractTradeFragment) {
                return
            }
            if (!tradeFragment.isAdded) {
                transaction.hide(currentFragment).add(R.id.fragment_container, tradeFragment, "1")
            } else {
                transaction.hide(currentFragment).show(tradeFragment)
            }
            currentFragment = tradeFragment
        }
        transaction.commitNow()
    }

    /**
     * 通过通知跳转到
     * 0 跳转到合约交易页面
     * 1 跳转到合约K线页面
     */
    fun pushJumpToContract(contractId: Int, type: Int) {
        LogUtil.d("DEBUG","----------pushJumpToContract------contractId:$contractId  isAdded:$isAdded-----")
        if (contractId == 0) {
            return
        }
        if(!isAdded){
            return
        }
        pushJumpToType = type
        mContractId = contractId
        val tickerList = ContractPublicDataAgent.getContractTickers()
        var contractTicker : ContractTicker?=null
        if (tickerList.isNotEmpty()) {
            contractTicker =  ContractPublicDataAgent.getContractTicker(contractId)
        }
        if(contractTicker!=null){
            //通知切换合约
            var msgEvent = MessageEvent(MessageEvent.sl_contract_left_coin_type)
            msgEvent.msg_content = contractTicker
            EventBusUtil.post(msgEvent)
            if(pushJumpToType == 1){
                pushJumpToType = -1
                ContractPublicDataAgent.subscribeDepthWs(mContractId)
                SlContractKlineActivity.show(activity!!,mContractId)
            }
        }else{
            //需要等待数据初始化完成 才能跳转
        }
    }

    /**
     * Ticker初始化更新回调
     */
     fun onContractTickerChanged() {
        val tickerList = ContractPublicDataAgent.getContractTickers()
        if (activity == null || !isAdded || tickerList.isNullOrEmpty()) {
            return
        }
        //mContractId为0 则默认取第0个合约
        var ticker: ContractTicker? = null
        if (mContractId == 0) {
            updateInputPrice = true
            if (tickerList.isNotEmpty()) {
                ticker = tickerList[0]
                mContractId = ticker.instrument_id
                mContract = ContractPublicDataAgent.getContract(mContractId)
            }
        } else {
            updateInputPrice = false
            ticker = ContractPublicDataAgent.getContractTicker(mContractId)
        }
        LogUtil.d("DEBUG", "--------------onContractTickerChanged:$mContractId  ${ticker?.instrument_id}---------------")
        //订阅ticker
        ContractPublicDataAgent.subscribeAllTickerWebSocket()
        ticker?.let {
            if(pushJumpToType > -1){
                ContractPublicDataAgent.subscribeDepthWs(mContractId)
                //处理通知栏跳转
                if(pushJumpToType == 1){
                    pushJumpToType = -1
                    mContract = ContractPublicDataAgent.getContract(mContractId)
                    SlContractKlineActivity.show(activity!!,mContractId)
                }
            }else if (!isHidden ) {
                //订阅深度
                ContractPublicDataAgent.subscribeDepthWs(mContractId)
            }
            updateContractUi(ticker, true)
        }
    }

    /**
     * 获取合约ticker
     */
    private fun loadContractTickerData() {
        if(pushJumpToType > -1){
            ContractSDKAgent.tickers(0)
        }else{
            ContractSDKAgent.tickers(mContractId)
        }

    }

    private fun updateContractUi(ticker: ContractTicker, resetData: Boolean = false) {
        mContract?.let {
            tradeFragment.bindContract(it, resetData)
            holdFragment.bindCoinCode(it, resetData)
        }
        tradeFragment.updateContractTicker(ticker, updateInputPrice)
        updateHeaderView(ticker)
        updateHoldTabCount()
    }


}