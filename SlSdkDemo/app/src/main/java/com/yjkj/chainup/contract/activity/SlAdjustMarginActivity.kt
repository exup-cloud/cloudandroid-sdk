package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.SoftKeyboardUtil
import com.yjkj.chainup.util.ToastUtils
import kotlinx.android.synthetic.main.sl_activity_adjust_margin.*
import kotlin.math.abs
import kotlin.math.min

/**
 * 调整保证金
 */
class SlAdjustMarginActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_adjust_margin
    }

    private var contract: Contract? = null
    private var contractTicker: ContractTicker? = null
    private var contractAccount: ContractAccount? = null

    private var selectLeverage = 0 // 选择杠杆
    private var mPosition = ContractPosition()
    private var mMaxMargin = 0.0 //最大保证金
    private var mMinMargin = 0.0 //最小保证金

    private var dfValue = NumberUtil.getDecimal(-1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPosition = intent.getParcelableExtra("position")
        contract = ContractPublicDataAgent.getContract(mPosition.instrument_id)
        contractTicker = ContractPublicDataAgent.getContractTicker(mPosition.instrument_id)
        if (contract == null || contractTicker == null) {
            finish()
            return
        }
        loadData()
        initView()
        initListener()
    }


    override fun loadData() {
        super.loadData()
        contractAccount = ContractUserDataAgent.getContractAccount(contract!!.margin_coin
                ?: "")
        dfValue = NumberUtil.getDecimal(contract!!.value_index)
        //计算保证金范围
        doCalculateMaxMargin()
        doCalculateMinMargin()
    }


    override fun initView() {
        super.initView()
        initAutoTextView()
        tv_expect_price_hint.text = getLineText("sl_str_adjust_liquidation_price") + "(" + contract!!.quote_coin + ")"
        tv_expect_price.text = dfValue.format(doCalculateLiqPrice(mPosition))
        updateMarginRangeUi()
        updatePriceAndBtnUi()
    }

    private fun initAutoTextView() {
        title_layout.title = getLineText("sl_str_adjust_margins")
        tv_adjust_lever_after.onLineText("sl_str_adjust_lever_after")
        tv_margin_amount_title.onLineText("sl_str_margin_amount")
        et_deposit_amount.hint = getLineText("sl_str_margin_amount")
        tv_confirm_btn.textContent = getLineText("common_text_btnConfirm")
    }


    private fun initListener() {
        tv_confirm_btn.isEnable(false)
        tv_confirm_btn.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                doAdjustMarginRequest()
            }
        }
        /**
         * 保证金数量
         */
        et_deposit_amount.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        updatePriceAndBtnUi()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })
    }

    /**
     * 提交保证金修改
     */
    private fun doAdjustMarginRequest() {
        var amount = et_deposit_amount.text.toString()
        if (TextUtils.isEmpty(amount) || amount.toDouble() <= 0) {
            return
        }

        var adjustMargin  =  MathHelper.sub(amount,mPosition.im)
        if(adjustMargin.toDouble().compareTo(0.0) == 0){
            return
        }
        var operateType = if (adjustMargin > 0) 1 else 2
        tv_confirm_btn.showLoading()

        ContractUserDataAgent.doAdjustMargin(mPosition.instrument_id,mPosition.pid,abs(adjustMargin).toString(),operateType,
        object: IResponse<String>(){
            override fun onSuccess(data: String) {
                tv_confirm_btn.hideLoading()
                ToastUtils.showToast(mActivity, getString(R.string.sl_str_adjust_succeed))
                SoftKeyboardUtil.hideSoftKeyboard(mActivity.currentFocus)
                this@SlAdjustMarginActivity.finish()
            }

            override fun onFail(code: String, msg: String) {
                tv_confirm_btn.hideLoading()
                ToastUtils.showToast(mActivity, msg)
            }
        })
    }

    /**
     * 更新强平价格和按钮状态
     */
    private fun updatePriceAndBtnUi() {
        var amount = et_deposit_amount.text.toString()
        val volIndex = (contract?.value_index ?: 0) + 1
        if (TextUtils.isEmpty(amount)) {
            amount = "0"
        } else if (TextUtils.equals(amount, ".")) {
            amount = "0."
        } else if (amount.contains(".")) {
            val index = amount.indexOf(".")
            if (index + volIndex < amount.length) {
                amount = amount.substring(0, index + volIndex)
                et_deposit_amount.setText(amount)
                et_deposit_amount.setSelection(amount.length)
            }
        }
        LogUtil.d("DEBUG", "updatePriceAndBtnUi2 保证金:$amount")
        //计算杠杆
        doDealLeverage(amount)
        if (MathHelper.round(amount) in mMinMargin..mMaxMargin) {
            var adjustMargin  = abs(MathHelper.sub(amount,mPosition.im))
            //保证金没改变
            if(adjustMargin.toDouble().compareTo(0.0) == 0){
                tv_confirm_btn.isEnable(false)
            }else{
                tv_confirm_btn.isEnable(true)
            }


            tv_lever.text = selectLeverage.toString()

            //计算强平价格
            val newPosition = ContractPosition()
            newPosition.fromJson(mPosition.toJson())
            newPosition.im = dfValue.format(amount.toDouble())
            tv_expect_price.text = doCalculateLiqPrice(newPosition).toString()
        } else {
            tv_confirm_btn.isEnable(false)
            tv_lever.text = "--"
            tv_expect_price.text = "--"
        }

    }

    /**
     * 杠杆和保证金之间的联动
     * 1.逐仓：杠杆 = 仓位价值/（仓位保证金+未实现盈亏）
     * 2.全仓：杠杆 = 仓位价值/（仓位保证金+未实现盈亏+（可用余额-该币种其他全仓亏损））
     */
    private fun doDealLeverage(amount: String)  {
        //仓位价值
        val value = ContractCalculate.CalculateContractValue(
                mPosition.cur_qty,
                mPosition.avg_cost_px,
                contract)
        //未实现盈亏
        var profitAmount = doCalculateProfitAmount()
        //根据保证金，计算杠杆
        if (mPosition.position_type == 1) {//逐仓
            selectLeverage = NumberUtil.roundUp(MathHelper.div(value, amount.toDouble() + profitAmount))
            if(selectLeverage > contract!!.max_leverage.toInt()){
                selectLeverage = contract!!.max_leverage.toInt()
            }
            LogUtil.d("DEBUG", "doDealLinkage 逐仓当前杠杆:$selectLeverage")
        } else {
            selectLeverage = contract!!.max_leverage.toInt()
//                    //可用余额
//                    val availableVol = contractAccount?.available_vol_real ?: 0.0
//                    //该币种其他全仓亏损
//                    val unrealisedVol = contractAccount?.unrealised_vol ?: 0.0
//                    NumberUtil.roundUp(MathHelper.div(value, amount.toDouble() + profitAmount + availableVol - unrealisedVol))
        }
    }


    /**
     * 计算强平价格
     */
    private fun doCalculateLiqPrice(info: ContractPosition): Double {
        var mLiqPrice = 0.0 //强平价
        info.let {
            val openType: Int = it.position_type
            if (openType == 1) {
                mLiqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                        it, null, contract!!)
            } else if (openType == 2) {
                if (contractAccount != null) {
                    mLiqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                            it, contractAccount, contract!!)
                }
            }
        }
        LogUtil.d("DEBUG", "计算强平价：$mLiqPrice")
        return mLiqPrice
    }

    /**
     * 更新保证金范围UI
     */
    private fun updateMarginRangeUi() {
        tv_margin_range.text = String.format(getLineText("sl_str_margin_range"), mMinMargin, mMaxMargin, contract!!.margin_coin)
        et_deposit_amount.setText(MathHelper.round(mPosition.im, contract?.value_index?:-1).toString())

    }

    /**
     * 计算未实现盈亏
     */
    private fun doCalculateProfitAmount(): Double {
        var profitAmount = 0.0 //未实现盈亏
        mPosition.let {
            val pnlCalculate: Int = LogicContractSetting.getPnlCalculate(mContext)
            when (it.side) {
                1 -> {
                    //多仓
                    profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                            it.cur_qty,
                            it.avg_cost_px,
                            if (pnlCalculate == 0) contractTicker?.fair_px else contractTicker?.last_px,
                            contract?.face_value,
                            contract!!.isReserve)
                }
                2 -> {
                    //空仓
                    profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                            it.cur_qty,
                            it.avg_cost_px,
                            if (pnlCalculate == 0) contractTicker?.fair_px else contractTicker?.last_px,
                            contract?.face_value,
                            contract!!.isReserve)
                }
            }
        }
        return profitAmount
    }

    /**
     * 保证金最小值
     * 当前仓位保证金-最大可减少保证金额
     */
    private fun doCalculateMinMargin() {
        mPosition.let {
            var maxReduce =  doCalculateCanMinMargin()
            mMinMargin = MathHelper.round(MathHelper.sub(it.im, maxReduce.toString()), contract!!.value_index)
            LogUtil.d("DEBUG", "最大可减少保证金:$maxReduce ;保证金最小值mMinMargin2：$mMinMargin")
            updateMarginRangeUi()
    }}

    /**
     * 计算最大可减少 = 当前仓位保证金- 合约价值*开仓保证金率 + min(未实现盈亏,0)
     */
    private fun doCalculateCanMinMargin() : Double{
        var maxReduce = 0.0 //最大可减少
        //仓位的开仓保证金率
        val IMR = ContractCalculate.CalculatePositionIMR(mPosition, contract!!)
        //合约价值
        val value = ContractCalculate.CalculateContractValue(
                mPosition.cur_qty,
                mPosition.avg_cost_px,
                contract)
        LogUtil.d("DEBUG","合约价值:$value;仓位的开仓保证金率:$IMR")
        //未实现盈亏
        var profitAmount = 0.0 //未实现盈亏额
        when(mPosition.side){
            1 ->{
                //多仓
                profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                        mPosition.cur_qty,
                        mPosition.avg_cost_px,
                        contractTicker?.fair_px,
                        contract!!.face_value,
                        contract!!.isReserve)
                val p: Double = MathHelper.add(mPosition.cur_qty,mPosition.close_qty)
                val plus: Double = MathHelper.mul(
                        MathHelper.round(mPosition.tax),
                        MathHelper.div(MathHelper.round(mPosition.cur_qty), p))
            }
            2 ->{
                //空仓
                profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                        mPosition.cur_qty,
                        mPosition.avg_cost_px,
                        contractTicker?.fair_px ,
                        contract!!.face_value,
                        contract!!.isReserve)

                val p: Double = MathHelper.add(mPosition.cur_qty, mPosition.close_qty)
                val plus = MathHelper.mul(
                        MathHelper.round(mPosition.tax),
                        MathHelper.div(MathHelper.round(mPosition.cur_qty), p))
            }
            else ->{ }
        }

        profitAmount = min(profitAmount,0.0)
        LogUtil.d("DEBUG","未实现盈亏:$profitAmount")
        maxReduce = MathHelper.add(MathHelper.sub(mPosition.im.toDouble(),value*IMR),profitAmount)

        return MathHelper.round(maxReduce, contract!!.value_index)
    }

    /**
     * 保证金最大值
     * 当前仓位保证金+最大可增加保证金额
     */
    private fun doCalculateMaxMargin() {
        mPosition.let {
            if (contractAccount != null) {
                var availableVolReal = contractAccount?.available_vol_real
                mMaxMargin = MathHelper.round(MathHelper.add(availableVolReal.toString(), it.im), contract!!.value_index)
                LogUtil.d("DEBUG", "保证金最大值mMaxMargin：$mMaxMargin")
            }

        }
    }

    companion object {
        fun show(activity: Activity, position: ContractPosition) {
            val intent = Intent(activity, SlAdjustMarginActivity::class.java)
            intent.putExtra("position", position)
            activity.startActivity(intent)
        }
    }
}