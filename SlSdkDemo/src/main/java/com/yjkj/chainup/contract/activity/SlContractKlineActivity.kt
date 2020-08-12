package com.yjkj.chainup.contract.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.widget.RadioButton
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.impl.ContractTickerListener
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.data.bean.TabEntity
import com.yjkj.chainup.contract.fragment.detail.SlDealRecordFragment
import com.yjkj.chainup.contract.fragment.detail.SlDepthFragment
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.ContractCoinSearchDialog
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.new_version.kline.view.MainKlineViewStatus
import com.yjkj.chainup.new_version.kline.view.vice.ViceViewStatus
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.new_version.view.LabelRadioButton
import com.yjkj.chainup.util.KLineUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.market_info_kline_panel.*
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_boll
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_hide_main
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_hide_vice
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_kdj
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_ma
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_macd
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_rsi
import kotlinx.android.synthetic.main.market_info_kline_panel.rb_wr
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.*
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_24h_vol
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_coin_map
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_fair_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_index_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_last_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_rose_rate
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.tv_usd_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line.v_kline
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.text.DecimalFormat


/**
 * 合约K线
 */
class SlContractKlineActivity : SlBaseKlineActivity(){
    override fun setContentView(): Int = R.layout.sl_activity_contract_k_line

    private var depthFragment: SlDepthFragment? = null
    private var dealtRecordFragment: SlDealRecordFragment? = null
    private var currentFragment = Fragment()
    private var currentIndex = 0


    /**
     * 主图指标的子view
     */
    private val mainViewStatusViews: ArrayList<RadioButton?> by lazy(LazyThreadSafetyMode.NONE) {
        arrayListOf<RadioButton?>(rb_ma, rb_boll, rb_hide_main)

    }

    /**
     * 副图指标的子view
     */
    private val viceViewStatusViews: ArrayList<RadioButton?> by lazy(LazyThreadSafetyMode.NONE) {
        arrayListOf<RadioButton?>(rb_macd, rb_kdj, rb_wr, rb_rsi, rb_hide_vice)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadData()
        initView()
        initListener()
    }

    override fun loadData() {
        loadCommonData()
        if (contract == null) {
            finish()
            return
        }
        updateTickerData(ContractPublicDataAgent.getContractTicker(contractId))
    }

    override fun initView() {
        super.initView()
        initAutoTextView()
        initCommonView()
        action4KLineIndex()
        initDepthAndDeals()

        btn_sell.textContent = "<font> ${getLineText("sl_str_sell_open")} <small>${getLineText("sl_str_sell_open_short")}</small> </font>"
        btn_buy.textContent = "<font> ${getLineText("sl_str_buy_open")} <small>${getLineText("sl_str_buy_open_long")}</small> </font>"

    }

    private fun initAutoTextView() {
        tv_rose_vol_label.onLineText("sl_str_rose_vol")
        tv_fair_price_label.onLineText("sl_str_fair_price")
        tv_index_price_label.onLineText("sl_str_index_price")
        tv_rose_rate_label.onLineText("sl_str_rose_rate")
        tv_funds_rate_label.onLineText("sl_str_funds_rate")
        tv_24h_vol_label.onLineText("sl_str_24h_vol")

        tv_main?.onLineText("kline_action_main")
        tv_vice?.onLineText("kline_action_assistant")
    }

    /**
     * 深度和交易记录
     */
    private fun initDepthAndDeals() {
        depthFragment = SlDepthFragment.newInstance(contractId)
        dealtRecordFragment = SlDealRecordFragment.newInstance(contractId)

        val tabEntityList = java.util.ArrayList<CustomTabEntity>()
        tabEntityList.add(TabEntity(getLineText("kline_action_depth"), 0, 0))
        tabEntityList.add(TabEntity(getLineText("kline_action_dealHistory"), 0, 0))

        stl_depth_dealt.setTabData(tabEntityList)
        stl_depth_dealt.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                currentIndex = position
                showFragment()
            }

            override fun onTabReselect(position: Int) {}
        })
        showFragment()
    }


    /**
     * K线的指标处理
     */
    private fun action4KLineIndex() {

        when (main_index) {
            MainKlineViewStatus.MA.status -> {
                rb_ma?.isLabelEnable = true
                rb_hide_main?.isChecked = false
            }

            MainKlineViewStatus.BOLL.status -> {
                rb_boll?.isLabelEnable = true
                rb_hide_main?.isChecked = false
            }

            MainKlineViewStatus.NONE.status -> {
                rb_hide_main?.isChecked = true
            }
        }

        when (vice_index) {
            ViceViewStatus.MACD.status -> {
                rb_macd?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.KDJ.status -> {
                rb_kdj?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.RSI.status -> {
                rb_rsi?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.WR.status -> {
                rb_wr?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.NONE.status -> {
                rb_hide_vice?.isChecked = true
            }
        }

        mainViewStatusViews.forEach {
            it?.setOnClickListener {
                val index = mainViewStatusViews.indexOf(it)
                (mainViewStatusViews[0] as LabelRadioButton?)?.isLabelEnable = (index == 0)
                (mainViewStatusViews[1] as LabelRadioButton?)?.isLabelEnable = (index == 1)
                mainViewStatusViews[2]?.isChecked = (index == 2)
                when (index) {
                    MainKlineViewStatus.MA.status -> {
                        v_kline?.changeMainDrawType(MainKlineViewStatus.MA)
                        KLineUtil.setMainIndex(MainKlineViewStatus.MA.status)
                    }

                    MainKlineViewStatus.BOLL.status -> {
                        v_kline?.changeMainDrawType(MainKlineViewStatus.BOLL)
                        KLineUtil.setMainIndex(MainKlineViewStatus.BOLL.status)
                    }

                    MainKlineViewStatus.NONE.status -> {
                        v_kline?.changeMainDrawType(MainKlineViewStatus.NONE)
                        KLineUtil.setMainIndex(MainKlineViewStatus.NONE.status)
                    }
                }
            }
        }

        /**
         * -----------副图--------------
         */
        viceViewStatusViews.forEach {
            it?.setOnClickListener {
                val index = viceViewStatusViews.indexOf(it)
                (viceViewStatusViews[0] as LabelRadioButton?)?.isLabelEnable = (index == 0)
                (viceViewStatusViews[1] as LabelRadioButton?)?.isLabelEnable = (index == 1)
                (viceViewStatusViews[2] as LabelRadioButton?)?.isLabelEnable = (index == 2)
                (viceViewStatusViews[3] as LabelRadioButton?)?.isLabelEnable = (index == 3)
                viceViewStatusViews[4]?.isChecked = (index == 4)

                when (index) {
                    ViceViewStatus.MACD.status -> {
                        v_kline?.setChildDraw(0)
                        KLineUtil.setViceIndex(ViceViewStatus.MACD.status)
                    }

                    ViceViewStatus.KDJ.status -> {
                        v_kline?.setChildDraw(1)
                        KLineUtil.setViceIndex(ViceViewStatus.KDJ.status)
                    }

                    ViceViewStatus.WR.status -> {
                        v_kline?.setChildDraw(2)
                        KLineUtil.setViceIndex(ViceViewStatus.WR.status)
                    }

                    ViceViewStatus.RSI.status -> {
                        v_kline?.setChildDraw(3)
                        KLineUtil.setViceIndex(ViceViewStatus.RSI.status)
                    }
                    else -> {
                        v_kline?.hideChildDraw()
                        KLineUtil.setViceIndex(ViceViewStatus.NONE.status)
                    }
                }

            }
        }
    }

    /**
     * 更新合约ticker数据
     */
    private fun updateTickerData(ticker: ContractTicker?) {
        if (contract == null || tv_coin_map == null) {
            finish()
            return
        }
        ticker?.let {
            if(ticker.instrument_id != contractId){
                return
            }
            //合约名称
            tv_coin_map.text = ContractPublicDataAgent.getContract(contractId)?.getDisplayName(mActivity)
            val dfVol: DecimalFormat = NumberUtil.getDecimal(contract!!.vol_index)
            val dfPrice: DecimalFormat = NumberUtil.getDecimal(contract!!.price_index)
            val dfRate: DecimalFormat = NumberUtil.getDecimal(2)

            val riseFallRate: Double = MathHelper.round(it.change_rate.toDouble() * 100, 2)
            val riseFallValue: Double = MathHelper.round(it.change_value, contract!!.price_index)
            val sRate = if (riseFallRate >= 0) "+" + dfRate.format(riseFallRate) + "%" else dfRate.format(riseFallRate) + "%"
            val sValue = if (riseFallValue >= 0) "+" + dfPrice.format(riseFallValue) else dfPrice.format(riseFallValue)
            val color = if (riseFallRate >= 0) resources.getColor(R.color.main_green) else resources.getColor(R.color.main_red)

            val current: Double = MathHelper.round(it.last_px, contract!!.price_index)
            val fairPrice: Double = MathHelper.round(it.fair_px, contract!!.price_index)
            val indexPrice: Double = MathHelper.round(it.index_px, contract!!.price_index)

            //最新成交价
            tv_last_price.text = NumberUtil.getDecimal(contract!!.price_index-1).format(current)
            tv_last_price.textColor = color

            //换算成人民币
            tv_usd_price.text = RateManager.getCNYByCoinName(contract!!.quote_coin, it.last_px, precision = 2)
            //涨跌额
            tv_rose_vol.text = sValue
            tv_rose_vol.textColor = color
            //涨跌幅
            tv_rose_rate.text = sRate
            tv_rose_rate.textColor = color
            //合理价格
            tv_fair_price.text = dfPrice.format(fairPrice)
            //指数价格
            tv_index_price.text = dfPrice.format(indexPrice)
            //资金费率
            val rate = MathHelper.mul(it.funding_rate, "100")
            tv_funds_rate.text = NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)).toString() + "%"
            //24h量
            val amount24: Double = MathHelper.round(it.qty24, contract!!.vol_index)
            tv_24h_vol.text = NumberUtil.getBigVolum(mContext, dfVol, amount24)
        }
    }

    private fun initListener() {
        ContractPublicDataAgent.registerTickerWsListener(this,object:ContractTickerListener(){
            /**
             * 合约Ticker更新
             */
            override fun onWsContractTicker(ticker: ContractTicker) {
                updateTickerData(ticker)
            }

        })
        initCommonListener()
        /**
         * 点击侧边栏
         */
        ll_coin_map.setOnClickListener {
            if (Utils.isFastClick())
                return@setOnClickListener
            val selectDialog = ContractCoinSearchDialog()
            selectDialog.showDialog(supportFragmentManager, "SlContractKlineActivity")
        }
        /**
         * 跳转到合约详情
         */
        rl_header_warp_layout.setOnClickListener {
            SlContractDetailActivity.show(mActivity, contractId, 0)
        }
        ib_back.setOnClickListener {
            finish()
        }

        /**
         * 横屏KLine
         */
        tv_landscape?.setOnClickListener {
            SlContractHKlineActivity.show(mActivity, contractId)
        }
        /**
         * 买入开多
         */
        btn_buy?.isEnable(true)
        btn_buy?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                var messageEvent = MessageEvent(MessageEvent.contract_switch_type)
                EventBusUtil.post(messageEvent)
                finish()

            }
        }
        /**
         * 卖出开空
         */
        btn_sell?.isEnable(true)
        btn_sell?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                var messageEvent = MessageEvent(MessageEvent.contract_switch_type)
                EventBusUtil.post(messageEvent)
                finish()

            }
        }
    }

    private fun showFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment = if (currentIndex == 0) {
            if (!depthFragment!!.isAdded) {
                transaction.hide(currentFragment).add(R.id.fragment_container_layout, depthFragment!!, "1")
            } else {
                transaction.hide(currentFragment).show(depthFragment!!)
            }
            depthFragment!!
        } else {
            if (!dealtRecordFragment!!.isAdded) {
                transaction.hide(currentFragment).add(R.id.fragment_container_layout, dealtRecordFragment!!, "2")
            } else {
                transaction.hide(currentFragment).show(dealtRecordFragment!!)
            }
            dealtRecordFragment!!
        }
        transaction.commitNowAllowingStateLoss()
    }



    companion object {
        fun show(activity: Activity, contractId: Int) {
            val intent = Intent(activity, SlContractKlineActivity::class.java)
            intent.putExtra("contractId", contractId)
            activity.startActivity(intent)
        }
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    override fun onMessageEvent(messageEvent: MessageEvent) {
        when (messageEvent.msg_type) {
            MessageEvent.sl_contract_switch_time_type ->{
                val selectTime = messageEvent.msg_content as String
                if(!TextUtils.equals(selectTime,curTime)){
                    curTime = selectTime
                    Handler().postDelayed({
                        initKLineScale()
                        loadKlineDataFromNet()
                    },1000)
                }
            }
            MessageEvent.sl_contract_left_coin_type -> {
                if (messageEvent.msg_content is ContractTicker) {
                    LogUtil.d("DEBUG", "--切换合约--" + messageEvent.msg_content)
                    val contractTicker = messageEvent.msg_content as ContractTicker
                    if (contractTicker.instrument_id != contractId) {
                        contractId = contractTicker.instrument_id
                        ContractPublicDataAgent.subscribeTradeWs(contractId)
                        contract = ContractPublicDataAgent.getContract(contractId)
                        //更新ticker
                        updateTickerData(ContractPublicDataAgent.getContractTicker(contractId))
                        //更新K线
                        loadKlineDataFromNet()
                        //更新深度
                        depthFragment?.switchContract(contractId)
                        //更新成交记录
                        dealtRecordFragment?.switchContract(contractId)
                    }
                }
            }
        }
    }

}
