package com.yjkj.chainup.contract.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.data.ContractWsKlineType
import com.contract.sdk.data.KLineData
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.fragment.detail.ContractIntroduceFragment
import com.yjkj.chainup.contract.fragment.detail.FundsRateFragment
import com.yjkj.chainup.contract.fragment.detail.InsuranceFundFragment
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.new_version.adapter.PageAdapter
import kotlinx.android.synthetic.main.sl_activity_contract_detail.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 合约详情
 */
class SlContractDetailActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_contract_detail
    }

    var contractId = 0
    var tabIndex = 0
    var contract: Contract? = null

    private var contractIntroduceFragment = ContractIntroduceFragment()
    private var insuranceFundFragment = InsuranceFundFragment()
    private var fundsRateFragment = FundsRateFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
        updateUi()
    }


    override fun initView() {
        super.initView()
        initAutoTextView()
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        collapsing_toolbar?.let {
            it.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            it.expandedTitleGravity = Gravity.TOP
        }
        //tab
        val showTitles = java.util.ArrayList<String>()
        val fragments = java.util.ArrayList<Fragment>()
        showTitles.add(getLineText("sl_str_contract_info"))
        showTitles.add(getLineText("sl_str_insurance_fund"))
        showTitles.add(getLineText("sl_str_funds_rate"))
        fragments.add(contractIntroduceFragment)
        fragments.add(insuranceFundFragment)
        fragments.add(fundsRateFragment)
        val pageAdapter = PageAdapter(supportFragmentManager, showTitles, fragments)
        vp_layout.adapter = pageAdapter
        vp_layout.offscreenPageLimit = 3
        tl_tab_layout.setViewPager(vp_layout, showTitles.toTypedArray())
        tl_tab_layout.currentTab = 1
        tl_tab_layout.currentTab = tabIndex
    }

    private fun initAutoTextView() {
        collapsing_toolbar.title = getLineText("sl_str_funds_rate")
        tv_hold_vol.onLineText("sl_str_open_interest_unit")
        tv_volume.onLineText("sl_str_vol_unit")
        tv_swap_label.onLineText("sl_str_turnover_rate")

        tv_market_changes_info_label.onLineText("sl_str_market_changes_info")
        tv_volatility_interval.onLineText("sl_str_volatility_interval")
        tv_volatility_interval_30day.onLineText("sl_str_volatility_interval_30days")
    }

    override fun loadData() {
        super.loadData()
        contractId = intent.getIntExtra("contractId", 0)
        tabIndex = intent.getIntExtra("tabIndex", 0)
        contract = ContractPublicDataAgent.getContract(contractId)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi() {
        contract?.let {
            val ticker: ContractTicker = ContractPublicDataAgent.getContractTicker(contractId) ?: return
            val df: DecimalFormat = NumberUtil.getDecimal(-1)

            //合约名称
            var name: String = ticker.symbol
            if (name.contains("[")) {
                name = name.substring(0, name.indexOf("["))
            }
            tv_contract_name.text = name
            //总持仓量
            tv_hold_vol_value.text = ticker.position_size
            //成交量
            tv_volume_value.text = ticker.qty24
            //换手比
            tv_swap_value.text = df.format(MathHelper.div(ticker.qty24, ticker.position_size, 4))
            //日波动区间
            tv_low_day.text = getLineText("sl_str_lowp") + df.format(MathHelper.round(ticker.low, it.price_index))
            tv_high_day.text = getLineText("sl_str_highp") + df.format(MathHelper.round(ticker.high, it.price_index))
            tv_last_day.text = getLineText("sl_str_last") + df.format(MathHelper.round(ticker.last_px, it.price_index))
            //日计算进度
            updateDayProgress(ticker)
            //30日计算进度
            update30DayProgress(ticker, it)
        }
    }

    private fun update30DayProgress(ticker: ContractTicker, it: Contract) {
        val dt = Date()
        val calendar = Calendar.getInstance()
        calendar.time = dt
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val dt1 = calendar.time
        showLoadingDialog()
        ContractPublicDataAgent.loadContractSpot(contractId,dt1.time / 1000, dt.time / 1000, ContractWsKlineType.WEBSOCKET_BIN1D,
        object :IResponse<MutableList<KLineData>>(){
            override fun onSuccess(data: MutableList<KLineData>) {
                closeLoadingDialog()
                if (data != null) {
                    var high = 0.0
                    var low = Int.MAX_VALUE.toDouble()
                    for (i in data.indices) {
                        val item: KLineData = data[i]
                        val h = item.high
                        val l = item.low
                        if (h > high) {
                            high = h
                        }
                        if (l < low) {
                            low = l
                        }
                    }
                    val df = NumberUtil.getDecimal(-1)
                    tv_last_30day.text = getLineText("sl_str_last") + df.format(MathHelper.round(ticker.last_px, it.price_index))
                    tv_low_30day.text = getLineText("sl_str_lowp") + df.format(MathHelper.round(low.toDouble(), it.price_index))
                    tv_high_30day.text = getLineText("sl_str_highp") + df.format(MathHelper.round(high.toDouble(), it.price_index))
                    val high30Day = high
                    val low30Day = low
                    val highDay = MathHelper.round(ticker.high)
                    val lowDay = MathHelper.round(ticker.low)
                    val lastDay = MathHelper.round(ticker.last_px)
                    val width30Day: Int = rl_bkg_30day.measuredWidth // 获取宽度;
                    val widthLastDay: Int = rl_last_30day.measuredWidth
                    val intervalDay = (width30Day * abs(highDay - lowDay) / (high30Day - low30Day)).toInt()
                    val startDay = (width30Day * (lowDay - low30Day) / (high30Day - low30Day)).toInt()
                    val lastPosDay = (width30Day * (lastDay - low30Day) / (high30Day - low30Day) - widthLastDay / 2).toInt()
                    val lp0 = rl_last_30day.layoutParams as LinearLayout.LayoutParams
                    //   LogUtil.d("lb","${lastPosDay} -  ${(width30Day - widthLastDay)} ")
                    lp0.setMargins(min(width30Day - widthLastDay, max((lastPosDay*0.85).toInt(), 0)),  UtilSystem.dip2px(mActivity, 20.0f), 0,0)
                    rl_last_30day.layoutParams = lp0
                    val lp = FrameLayout.LayoutParams(intervalDay, UtilSystem.dip2px(mActivity, 5.0f))
                    lp.setMargins(startDay, 0, 0, 0)
                    rl_fore_30day.layoutParams = lp
                }
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
            }
        })
    }

    private fun updateDayProgress(ticker: ContractTicker): Boolean {
        return tv_low_day.post {
            val highDay = MathHelper.round(ticker.high)
            val lowDay = MathHelper.round(ticker.low)
            val openDay = MathHelper.round(ticker.open)
            val lastDay = MathHelper.round(ticker.last_px)

            val widthDay: Int = rl_bkg_day.measuredWidth // 获取宽度;
            val widthLastDay: Int = rl_last_day.measuredWidth

            val intervalDay = (widthDay * Math.abs(openDay - lastDay) / (highDay - lowDay)).toInt()
            var startDay = 0

            startDay = if (openDay < lastDay) {
                (widthDay * (openDay - lowDay) / (highDay - lowDay)).toInt()
            } else {
                (widthDay * (lastDay - lowDay) / (highDay - lowDay)).toInt()
            }
            //最近价格偏移
            val lastPosDay = (widthDay * (lastDay - lowDay) / (highDay - lowDay) - widthLastDay / 2).toInt()
            val lp0 = rl_last_day.layoutParams as LinearLayout.LayoutParams
            lp0.setMargins(min(widthDay - widthLastDay, max(lastPosDay, 0)), UtilSystem.dip2px(mActivity, 3.0f), 0,0)
            //进度
            val lp = FrameLayout.LayoutParams(intervalDay, UtilSystem.dip2px(mActivity, 5.0f))
            lp.setMargins(startDay, 0, 0, 0)
            rl_fore_day.layoutParams = lp
        }
    }


    companion object {
        fun show(activity: Activity, contractId: Int = 0, tabIndex: Int = 0) {
            val intent = Intent(activity, SlContractDetailActivity::class.java)
            intent.putExtra("contractId", contractId)
            intent.putExtra("tabIndex", tabIndex)
            activity.startActivity(intent)
        }
    }

}