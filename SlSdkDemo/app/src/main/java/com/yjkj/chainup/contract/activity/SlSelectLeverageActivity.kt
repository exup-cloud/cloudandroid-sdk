package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.utils.ContractUtils
import com.yjkj.chainup.contract.utils.GlobalLeverageUtils
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.bubble.BubbleSeekBar
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.KeyBoardUtils
import com.yjkj.chainup.util.LogUtil
import kotlinx.android.synthetic.main.sl_activity_select_leverage.*

/**
 * 选择合约杠杆
 */
class SlSelectLeverageActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_select_leverage
    }

    private var mContract: Contract? = null
    private var currTabType = 0// 0 逐仓  1 全仓
    private var price = "0.0"
    private var selectLeverage = 0 // 选择杠杆
    private var selectLeverageType = 1 // 选择杠杆
    private var minLeverage = 1
    private var maxLeverage = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
        initListener()
    }


    override fun loadData() {
        super.loadData()
        val contractId = intent.getIntExtra("contractId", 0)
        price = intent.getStringExtra("price")
        selectLeverage = intent.getIntExtra("selectLeverage", 10)
        selectLeverageType = intent.getIntExtra("selectLeverageType", 1)
        LogUtil.d("DEBUG", "当前选择杠杆:${selectLeverage}选择杠杆类型:${selectLeverageType}")
        mContract = ContractPublicDataAgent.getContract(contractId)
        if (mContract == null) {
            finish()
            return
        }
        mContract?.let {
            minLeverage = it.min_leverage.toInt()
            maxLeverage = it.max_leverage.toInt()
        }

        currTabType = when (selectLeverageType) {
            1 -> 0
            else -> 1
        }

        if (!GlobalLeverageUtils.isOpenGlobalLeverage && currTabType == 1) {
            selectLeverage = maxLeverage
        }

    }

    override fun initView() {
        super.initView()
        initAutoTextView()
        switchTabUi()
        initSeekBarUi()
    }

    private fun initAutoTextView() {
        title_layout.title = getLineText("sl_str_switch_lever")
        tv_tab_gradually.onLineText("sl_str_gradually_position")
        tv_tab_full.onLineText("sl_str_full_position")
        tv_leverage_warn.onLineText("sl_select_lever_warn")
    }

    private fun initSeekBarUi() {
        if (!GlobalLeverageUtils.isOpenGlobalLeverage && currTabType == 1) {
            et_input.setText(maxLeverage.toString())
        } else {
            et_input.setText(selectLeverage.toString())
        }
        et_input.setSelection(et_input.text.toString().length)
        seekbar.configBuilder
                .min(minLeverage.toFloat())
                .max(maxLeverage.toFloat())
                .progress(selectLeverage.toFloat())
                .build()
        seekbar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
                et_input.setText(progress.toString())
                et_input.setSelection(progress.toString().length)
            }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
            }

            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
            }
        }

        et_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!GlobalLeverageUtils.isOpenGlobalLeverage && currTabType == 1) {
                    return
                }
                if (TextUtils.isEmpty(s.toString())) {
                    //  et_price.setText("$minLeverage")
                    seekbar.setProgress(minLeverage.toFloat())
                    //   et_price.setSelection(minLeverage.toString().length)
                    return
                }
                val leverage = et_input.text.toString().toInt()
                if (leverage > maxLeverage) {
                    et_input.setText("$maxLeverage")
                    et_input.setSelection(maxLeverage.toString().length)
                }
                if (leverage < minLeverage) {
                    et_input.setText("$minLeverage")
                    et_input.setSelection(minLeverage.toString().length)
                }
                seekbar.setProgress(et_input.text.toString().toFloat())

                if (currTabType == 0) {
                    doCalculateMaxOpen(leverage)
                    if (leverage >= 50) {
                        tv_leverage_warn.visibility = View.VISIBLE
                    } else {
                        tv_leverage_warn.visibility = View.INVISIBLE
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }

    private fun initListener() {
        /**
         * 点击逐仓tab
         */
        tv_tab_gradually.setOnClickListener {
            if (currTabType == 1) {
                currTabType = 0
                selectLeverageType = 1
                switchTabUi()
            }
        }
        /**
         * 点击全仓tab
         */
        tv_tab_full.setOnClickListener {
            if (currTabType == 0) {
                currTabType = 1
                selectLeverageType = 2
                switchTabUi()
            }
        }
        tv_confirm_btn.isEnable(true)
        tv_confirm_btn.textContent = getLineText("common_text_btnConfirm")
        tv_confirm_btn.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                val inputLeverage = et_input.text.toString()
                if (!TextUtils.isEmpty(inputLeverage)) {
                    selectLeverage = if (!GlobalLeverageUtils.isOpenGlobalLeverage && currTabType == 1) {
                        maxLeverage
                    } else {
                        inputLeverage.toInt()
                    }
                    val map = hashMapOf<String, Int>("leverage" to selectLeverage, "leverageType" to selectLeverageType)
                    EventBusUtil.post(MessageEvent(MessageEvent.sl_contract_select_leverage_event, map))
                    finish()
                }
            }

        }
    }

    /**
     * 切换仓位Tab
     */
    private fun switchTabUi() {
        if (currTabType == 0) {//逐仓
            tv_tab_gradually.setBackgroundResource(R.drawable.sl_tab_leverage_gradually_select)
            tv_tab_gradually.isSelected = true
            tv_tab_full.setBackgroundResource(R.drawable.sl_tab_leverage_full_normal)
            tv_tab_full.isSelected = false
            et_input.isEnabled = true
            et_input.setText(selectLeverage.toString())
            doCalculateMaxOpen()
            seekbar.visibility = View.VISIBLE
            if (selectLeverage >= 50) {
                tv_leverage_warn.visibility = View.VISIBLE
            } else {
                tv_leverage_warn.visibility = View.INVISIBLE
            }
        } else {
            tv_tab_gradually.setBackgroundResource(R.drawable.sl_tab_leverage_gradually_normal)
            tv_tab_gradually.isSelected = false
            tv_tab_full.setBackgroundResource(R.drawable.sl_tab_leverage_full_select)
            tv_tab_full.isSelected = true
            et_input.isEnabled = GlobalLeverageUtils.isOpenGlobalLeverage
            et_input.setText(if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                selectLeverage.toString()
            } else {
                maxLeverage.toString()
            })
            KeyBoardUtils.closeKeyBoard(mActivity)
            tv_leverage_des.onLineText("sl_str_leverage_full_des")
            seekbar.visibility = if (GlobalLeverageUtils.isOpenGlobalLeverage) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
            tv_leverage_warn.visibility = View.INVISIBLE
        }
    }


    /**
     * 更新可开可平数量
     * 1.用户设置了价格，根据设置的价格计算可开张数
     * 2.用户没有设置根据合理价格
     */
    private fun doCalculateMaxOpen(leverage: Int = selectLeverage) {
        var longValue = "0"
        var shortValue = "0"
        mContract?.let {
            val contractAccount = ContractUserDataAgent.getContractAccount(it.margin_coin)
            if (contractAccount != null) {
                val dfVol = NumberUtil.getDecimal(it.value_index)
                val longVolume = ContractCalculate.CalculateVolume(
                        dfVol.format(contractAccount.available_vol_real),
                        leverage,
                        ContractUserDataAgent.getContractOrderSize(it.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                        ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_LONG),
                        price,
                        ContractPosition.POSITION_TYPE_LONG,
                        it)

                val shortVolume = ContractCalculate.CalculateVolume(
                        dfVol.format(contractAccount.available_vol_real),
                        leverage,
                        ContractUserDataAgent.getContractOrderSize(it.instrument_id, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                        ContractUserDataAgent.getContractPosition(it.instrument_id, ContractPosition.POSITION_TYPE_SHORT),
                        price,
                        ContractPosition.POSITION_TYPE_SHORT,
                        it)
                longValue = ContractUtils.getVolUnit(this, it, longVolume, MathHelper.round(price))
                shortValue = ContractUtils.getVolUnit(this, it, shortVolume, MathHelper.round(price))
            }
        }
        val highColor = resources.getColor(R.color.text_color_orange)
        val unit = resources.getString(R.string.sl_str_contracts_unit)
        val htmlString = String.format("<font>" + getLineText("sl_str_max_open") + "</font>", "<font color='${highColor}'>" + longValue + "</font>", "<font color='${highColor}'>" + shortValue + "</font>")

        tv_leverage_des.text = Html.fromHtml(htmlString)
    }


    companion object {
        fun show(activity: Activity, contractId: Int = 0, selectLeverage: Int = 10, price: String, leverageType: Int = 1) {
            val intent = Intent(activity, SlSelectLeverageActivity::class.java)
            intent.putExtra("contractId", contractId)
            intent.putExtra("selectLeverage", selectLeverage)
            intent.putExtra("selectLeverageType", leverageType)
            intent.putExtra("price", price)
            activity.startActivity(intent)
        }
    }
}