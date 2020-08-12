package com.yjkj.chainup.contract.fragment.calculate

import com.contract.sdk.data.ContractOrder
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import kotlinx.android.synthetic.main.sl_fragment_contract_calculate_item.*
import java.text.DecimalFormat


/**
 * 目标收益
 */
class SlProfitRateFragment : BaseCalculatorFragment(){
    //计算类型
    private val calculateTypeList = ArrayList<TabInfo>()
    private var calculateTypeDialog: TDialog? = null

    override fun initView() {
        tabIndex = 2
        loadCommonData()
        initListener()
        tv_calculate_type_value.text = currCalculateTypeInfo?.name
        rl_calculate_type_layout.setOnClickListener {
            calculateTypeDialog = NewDialogUtils.showNewBottomListDialog(mActivity!!, calculateTypeList, currCalculateTypeInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currCalculateTypeInfo = calculateTypeList[index]
                    calculateTypeDialog?.dismiss()
                    currCalculateTypeInfo?.let {
                        tv_calculate_type_value.text = it.name
                        tv_extras_title.text = it.name
                        et_extras.hint = it.name
                        et_extras.setText("")
                        if(it.index == 0){
                            tv_extras_symbol.text =  contract?.margin_coin
                        }else{
                            tv_extras_symbol.text =  "%"
                        }

                    }

                }
            })
        }
    }

    override fun loadData() {
        super.loadData()
        //计算类型
        calculateTypeList.add(TabInfo(getLineText("sl_str_profit_value"),0))
        calculateTypeList.add(TabInfo(getLineText("sl_str_profit_rate"),1))
        currCalculateTypeInfo = calculateTypeList[0]
    }
    override fun setContentView(): Int {
        return R.layout.sl_fragment_contract_calculate_item
    }

    override fun doCalculator() {
        val mLeverage =  currLeverage
        val mDirection = currDirectionInfo?.index!!

        val dfNormal: DecimalFormat = NumberUtil.getDecimal(-1)
        val value: Double = ContractCalculate.CalculateContractValue(
                vol,
                openPrice,
                contract)
        val margin: Double = ContractCalculate.CalculateIM(dfNormal.format(value), mLeverage, contract!!)
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
        val targetPrice = ContractCalculate.CalculateOrderTargetPriceValue(contractOrder, extras, currCalculateTypeInfo!!.index, contract!!)
            val marginValue = "<font color=\"#FF9E12\">${dfNormal.format(margin)} </font> ${contract?.margin_coin}"
            val tabList = ArrayList<TabInfo>()
            tabList.add(TabInfo(getLineText("sl_str_take_up_margin"),marginValue))
            tabList.add(TabInfo(getLineText("sl_str_str_target_close_price"),dfNormal.format(MathHelper.round(targetPrice, contract!!.price_index)) + contract!!.quote_coin))
            SlDialogHelper.showCalculatorResultDialog(mActivity!!,tabList)

    }

}