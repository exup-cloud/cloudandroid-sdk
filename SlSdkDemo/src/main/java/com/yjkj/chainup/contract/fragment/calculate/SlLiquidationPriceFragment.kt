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
 * 强平价格
 */
class SlLiquidationPriceFragment : BaseCalculatorFragment(){
    override fun initView() {
        tabIndex = 1
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

        val contractOrder = ContractOrder()
        contractOrder.instrument_id = contract!!.instrument_id
        contractOrder.leverage = mLeverage
        contractOrder.qty = vol
        contractOrder.position_type = 1
        contractOrder.px = openPrice
        contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
        if (mDirection == 0) {
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
        } else if (mDirection == 1) {
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT
        }

        val liquidationPrice = ContractCalculate.CalculateOrderLiquidatePrice(contractOrder, null, contract!!)
        val IMR = ContractCalculate.CalculateIMR(dfNormal.format(value), contract!!)
        val MMR = ContractCalculate.CalculateMMR(dfNormal.format(value), contract!!)

        val marginValue = "<font color=\"#FF9E12\">${dfNormal.format(MathHelper.round(liquidationPrice, contract!!.price_index)) } </font> ${contract?.quote_coin}"
        val tabList = ArrayList<TabInfo>()
        tabList.add(TabInfo(getLineText("sl_str_forced_close_price"),marginValue))
        tabList.add(TabInfo(getLineText("sl_str_position_value"),dfNormal.format(MathHelper.round(value, contract!!.value_index)) + contract!!.margin_coin))
        tabList.add(TabInfo(getLineText("sl_str_initial_margin_rate"),IMR.toString()))
        tabList.add(TabInfo(getLineText("sl_str_maintenance_margin_rate"),MMR.toString()))

        SlDialogHelper.showCalculatorResultDialog(mActivity!!,tabList)

    }

}