package com.yjkj.chainup.contract.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.impl.ContractTickerListener
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.new_version.kline.view.MainKlineViewStatus
import com.yjkj.chainup.new_version.kline.view.vice.ViceViewStatus
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.*
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_24h_vol
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_coin_map
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_fair_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_index_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_last_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.tv_usd_price
import kotlinx.android.synthetic.main.sl_activity_contract_k_line_h.v_kline
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.text.DecimalFormat


/**
 * 合约K线（横板）
 */
class SlContractHKlineActivity : SlBaseKlineActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_contract_k_line_h
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        isLandscape = true
        super.onCreate(savedInstanceState)

          loadData()
          initView()
          initListener()
    }


     override fun loadData() {
         loadCommonData()
         val ticker = ContractPublicDataAgent.getContractTicker(contractId)
         ticker?.let {
             updateTickerData(it)
         }
    }


     override fun initView() {
         initAutoTextView()
         initCommonView()
        action4KLineIndex()
    }

    private fun initAutoTextView() {
        tv_rose_title?.onLineText("contract_text_upsdowns")
        tv_high_price_title?.onLineText("sl_str_fair_price")
        tv_low_price_title?.onLineText("sl_str_index_price")
        tv_main_title?.onLineText("kline_action_main")
        tv_vice_title?.onLineText("kline_action_assistant")
    }


    /**
     * K线的指标处理
     */
    private fun action4KLineIndex() {
        when (KLineUtil.getMainIndex()) {
            MainKlineViewStatus.MA.status -> {
                setKLineViewIndexStatus(position = 0)
            }

            MainKlineViewStatus.BOLL.status -> {
                setKLineViewIndexStatus(position = 1)
            }

            MainKlineViewStatus.NONE.status -> {
                setKLineViewIndexStatus(position = 2)
            }
        }

        when (KLineUtil.getViceIndex()) {
            ViceViewStatus.MACD.status -> {
                setKLineViewIndexStatus(isMain = false, position = 0)
            }

            ViceViewStatus.KDJ.status -> {
                setKLineViewIndexStatus(isMain = false, position = 1)
            }

            ViceViewStatus.RSI.status -> {
                setKLineViewIndexStatus(isMain = false, position = 2)

            }

            ViceViewStatus.WR.status -> {
                setKLineViewIndexStatus(isMain = false, position = 3)

            }

            ViceViewStatus.NONE.status -> {
                setKLineViewIndexStatus(isMain = false, position = 4)
            }
        }


        rg_main?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_ma -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.MA)
                    KLineUtil.setMainIndex(MainKlineViewStatus.MA.status)
                }
                R.id.rb_boll -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.BOLL)
                    KLineUtil.setMainIndex(MainKlineViewStatus.BOLL.status)
                }

                R.id.rb_hide_main -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.NONE)
                    KLineUtil.setMainIndex(MainKlineViewStatus.NONE.status)
                }
            }
        }


        rg_vice?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_macd -> {
                    v_kline?.setChildDraw(0)
                    KLineUtil.setViceIndex(ViceViewStatus.MACD.status)

                }
                R.id.rb_kdj -> {
                    v_kline?.setChildDraw(1)
                    KLineUtil.setViceIndex(ViceViewStatus.KDJ.status)
                }

                R.id.rb_rsi -> {
                    v_kline?.setChildDraw(2)
                    KLineUtil.setViceIndex(ViceViewStatus.RSI.status)
                }

                R.id.rb_wr -> {
                    v_kline?.setChildDraw(3)
                    KLineUtil.setViceIndex(ViceViewStatus.WR.status)
                }

                R.id.rb_hide_vice -> {
                    v_kline?.hideChildDraw()
                    KLineUtil.setViceIndex(ViceViewStatus.NONE.status)
                }
            }
        }
    }

    private fun setKLineViewIndexStatus(isMain: Boolean = true, position: Int = 0) {
        val parent = if (isMain) rg_main else rg_vice
        parent?.let {
            for (i in 0 until it.childCount) {
                if (i == position) {
                    (it.getChildAt(position) as RadioButton).isChecked = true
                } else {
                    (it.getChildAt(i) as RadioButton).isChecked = false
                }
            }
        }
    }

    /**
     * 更新合约ticker数据
     */
    private fun updateTickerData(ticker: ContractTicker){
        if(contract == null || tv_coin_map == null){
            finish()
            return
        }
        ticker.let {
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
            val sRate = if (riseFallRate >= 0) "+" +dfRate.format(riseFallRate) + "%" else dfRate.format(riseFallRate) + "%"
            val sValue = if (riseFallValue >= 0) "+" + dfPrice.format(riseFallValue) else dfPrice.format(riseFallValue)
            val color = if (riseFallRate >= 0) resources.getColor(R.color.main_green) else resources.getColor(R.color.main_red)

            val current: Double = MathHelper.round(it.last_px, contract!!.price_index)
            val fairPrice: Double = MathHelper.round(it.fair_px, contract!!.price_index)
            val indexPrice: Double = MathHelper.round(it.index_px, contract!!.price_index)

            //最新成交价
            tv_last_price.text = NumberUtil.getDecimal(contract!!.price_index-1).format(current)
            tv_last_price.textColor = color

            //换算成人民币
            tv_usd_price.text =  RateManager.getCNYByCoinName(contract!!.quote_coin, it.last_px, precision = 2)
            //涨跌额
//            tv_rose_vol.text = sValue
//            tv_rose_vol.textColor = color
            //涨跌幅
            tv_rose_rate.text = sRate
            tv_rose_rate.textColor = color
            //合理价格
            tv_fair_price.text = dfPrice.format(fairPrice)
            //指数价格
            tv_index_price.text = dfPrice.format(indexPrice)
            //资金费率
//            val rate = MathHelper.mul(it.funding_rate, "100")
//            tv_funds_rate.text = NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)).toString() + "%"
            //24h量
            val amount24: Double = MathHelper.round(it.qty24, contract!!.vol_index)
            tv_24h_vol.text = NumberUtil.getBigVolum(mContext, dfVol, amount24)
        }
    }

    private fun initListener() {

        ContractPublicDataAgent.registerTickerWsListener(this,object:ContractTickerListener(){
            override fun onWsContractTicker(ticker: ContractTicker) {
                updateTickerData(ticker)
            }

        })

        iv_close?.setOnClickListener {
            finish()
        }
    }


    companion object{
        fun show(activity: Activity, contractId: Int){
            val intent = Intent(activity,SlContractHKlineActivity::class.java)
            intent.putExtra("contractId",contractId)
            activity.startActivity(intent)
        }
    }

}
