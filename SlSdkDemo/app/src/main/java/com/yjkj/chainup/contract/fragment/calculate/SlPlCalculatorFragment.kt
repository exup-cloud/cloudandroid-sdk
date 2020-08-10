package com.yjkj.chainup.contract.fragment.calculate

import com.contract.sdk.data.ContractOrder
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.widget.SlDialogHelper
import java.text.DecimalFormat


/**
 * 盈亏计算
 */
class SlPlCalculatorFragment : BaseCalculatorFragment(){

    override fun initView() {
        tabIndex = 0
        loadCommonData()
        initListener()
    }
    override fun setContentView(): Int {
       return R.layout.sl_fragment_contract_calculate_item
    }

    override fun doCalculator() {
        val dfNormal: DecimalFormat = NumberUtil.getDecimal(-1)
        val value: Double = ContractCalculate.CalculateContractValue(
                vol,
                openPrice,
                contract)
        val mLeverage =  currLeverage
        val mDirection = currDirectionInfo?.index!!
        val margin = ContractCalculate.CalculateIM(dfNormal.format(value), mLeverage, contract!!)
        val contractOrder = ContractOrder()
        contractOrder.leverage = mLeverage
        contractOrder.qty = vol
        contractOrder.position_type = 1
        contractOrder.px = openPrice
        contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL

        var profitRate = 0.0 //未实现盈亏
        var profitAmount = 0.0 //未实现盈亏额
        if (mDirection == 0) {
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
            profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                    vol,
                    openPrice,
                    extras,
                    contract!!.face_value,
                    contract!!.isReserve)

            profitRate = MathHelper.div(profitAmount, margin) * 100
        }else{
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT

            profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                    vol,
                    openPrice,
                    extras,
                    contract!!.face_value,
                    contract!!.isReserve)
            profitRate = MathHelper.div(profitAmount, margin) * 100
        }
        val marginValue = "<font color=\"#FF9E12\">${dfNormal.format(margin)} </font> ${contract?.margin_coin}"
        val tabList = ArrayList<TabInfo>()
        tabList.add(TabInfo(getLineText("sl_str_take_up_margin"),marginValue))
        tabList.add(TabInfo(getLineText("sl_str_position_value"),dfNormal.format(MathHelper.round(value, contract!!.value_index)) + contract!!.margin_coin))
        tabList.add(TabInfo(getLineText("sl_str_pl"),dfNormal.format(MathHelper.round(profitAmount, contract!!.value_index)) + contract?.margin_coin))
        tabList.add(TabInfo(getLineText("sl_str_profit_rate1"), dfNormal.format(MathHelper.round(profitRate, 2)).toString() + "%"))

        SlDialogHelper.showCalculatorResultDialog(mActivity!!,tabList)

    }


}