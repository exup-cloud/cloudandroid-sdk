package com.yjkj.chainup.contract.fragment.calculate

import android.text.TextUtils
import android.view.View
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.data.Contract
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlSelectLeverageActivity
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.PreferenceManager
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton.OnBottonListener
import com.yjkj.chainup.util.ToastUtils
import kotlinx.android.synthetic.main.sl_fragment_contract_calculate_item.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseCalculatorFragment : NBaseFragment(){
    protected var tabIndex = 0
    //方向
    protected val directionList = ArrayList<TabInfo>()
    protected var currDirectionInfo: TabInfo? = null
    protected var directionDialog: TDialog? = null
    //杠杆倍数
    protected var currLeverage = 10
    //杠杆类型
    protected var currentPositionType = 1

    protected var currCalculateTypeInfo: TabInfo? = null

    //参与计算内容
    var vol = ""
    var openPrice = ""
    var extras = ""
    var contract: Contract?=null

    abstract fun doCalculator()

    fun loadCommonData() {
        initAutoTextView()
        //方向
        directionList.add(TabInfo(getLineText("sl_str_long"),0))
        directionList.add(TabInfo(getLineText("sl_str_short"),1))
        currDirectionInfo = directionList[0]
        tv_direction_value.text = currDirectionInfo?.name

        //杠杆倍数
        initLeverageData()
        initExtrasUi()
    }

    private fun initAutoTextView() {
        tv_direction_label.onLineText("sl_str_direction")
        tv_lever_label.onLineText("contract_text_lever")
        tv_position_title.onLineText("contract_text_position")
        et_position.hint = getLineText("contract_text_position")
        tv_open_price_title.onLineText("contract_open_position_price")
        et_open_price.hint = getLineText("contract_text_price")
        tv_extras_title.onLineText("sl_str_close_position_price")
        et_extras.hint = getLineText("contract_text_price")
        tv_tips_label.onLineText("common_text_tip")
        tv_contract_calculator_tips.onLineText("sl_str_contract_calculator_tips")
        btn_calculate.textContent = getLineText("sl_str_calculator")
        tv_position_symbol.onLineText("sl_str_contracts_unit")
    }


    protected fun initListener(){
        //方向
        rl_direction_layout.setOnClickListener {
            directionDialog = NewDialogUtils.showNewBottomListDialog(mActivity!!, directionList, currDirectionInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currDirectionInfo = directionList[index]
                    directionDialog?.dismiss()
                    tv_direction_value.text = currDirectionInfo?.name
                }
            })
        }
        //杠杆
        rl_leverage_layout.setOnClickListener {
            val triggerType = LogicContractSetting.getTriggerPriceType(mActivity)
            val ticker = ContractPublicDataAgent.getContractTicker(contract!!.instrument_id)
            ticker?.let {
                val price = when(triggerType){
                    1 -> it.last_px
                    2-> it.fair_px
                    4 -> it.index_px
                    else -> it.last_px
                }
                SlSelectLeverageActivity.show(mActivity!!, contract!!.instrument_id, currLeverage, price,currentPositionType)
            }
        }
        //计算
        btn_calculate.isEnable(true)
        btn_calculate.listener = object : OnBottonListener {
            override fun bottonOnClick() {
                vol = et_position.text.toString()
                openPrice = et_open_price.text.toString()
                extras = et_extras.text.toString()

                if(tabIndex == 1){
                    if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice)) {
                        ToastUtils.showToast(mActivity, getLineText("sl_str_miss_param"))
                        return
                    }
                }else{
                    if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice) || TextUtils.isEmpty(extras)) {
                        ToastUtils.showToast(mActivity, getLineText("sl_str_miss_param"))
                        return
                    }
                }

                doCalculator()
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    override fun onMessageEvent(event: MessageEvent) {
        when (event.msg_type) {
            MessageEvent.sl_contract_select_leverage_event -> {
                if (event.msg_content != null && event.msg_content is HashMap<*, *>) {
                    val map = event.msg_content as HashMap<String,Int>
                    currLeverage = map["leverage"]!!
                    currentPositionType = map["leverageType"]!!
                    tv_leverage_value?.text = "${currLeverage}X"
                }
            }
        }
    }

    private fun initLeverageData() {
        contract?.let {
            val minLeverage = it.min_leverage.toInt()
            val maxLeverage = it.max_leverage.toInt()
            val defaultLeverage = it.default_leverage
            val defaultPositionType = it.position_type
            val leverageKey = "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverage"
            val leverageTypeKey = "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverageType"
            val localLeverage = PreferenceManager.getInstance(ContractSDKAgent.context).getSharedInt(leverageKey, 0)
            val localLeverageType = PreferenceManager.getInstance(ContractSDKAgent.context).getSharedInt(leverageTypeKey, 0)
            currLeverage = when {
                localLeverage == 0 && defaultLeverage > 0 -> {
                    defaultLeverage
                }
                localLeverage in minLeverage..maxLeverage -> {
                    localLeverage
                }
                else -> {
                    minLeverage
                }
            }
            currentPositionType = when (localLeverageType) {
                0 -> {
                    defaultPositionType
                }
                else -> {
                    localLeverageType
                }
            }
        }
        tv_leverage_value?.text = "${currLeverage}X"
    }

    /**
     * 切换合约
     */
    fun switchContract(contract : Contract?){
        this.contract = contract
        //切换合约后 需初始化杠杆
        initLeverageData()
        initExtrasUi()
    }


    private fun initExtrasUi() {
        if(tv_open_price_symbol == null){
            return
        }
        tv_open_price_symbol.text = contract?.quote_coin
        when (this.tabIndex) {
            0 -> {
                rl_extras_layout.visibility = View.VISIBLE
                tv_extras_title.onLineText("sl_str_close_position_price")
                et_extras.hint = getLineText("contract_text_price")
                tv_extras_symbol.text =  contract?.quote_coin
            }
            1 -> {
                rl_extras_layout.visibility = View.GONE
            }
            2 -> {
                rl_extras_layout.visibility = View.VISIBLE
                tv_extras_title.text = currCalculateTypeInfo?.name
                tv_extras_symbol.text =  contract?.margin_coin
                if(currCalculateTypeInfo?.index == 0){
                    tv_extras_symbol.text =  contract?.margin_coin
                }else{
                    tv_extras_symbol.text =  "%"
                }
            }
        }
    }

}