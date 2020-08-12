package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.PreferenceManager
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import kotlinx.android.synthetic.main.sl_activity_contract_setting.*

/**
 * 合约设置
 */
class SlContractSettingActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_contract_setting
    }

    //仓位展示单位
    private val unitList = ArrayList<TabInfo>()
    private var currUnitInfo: TabInfo? = null
    private var unitDialog: TDialog? = null
    private var originUnitIndex:Int? = 0
    //未实现盈亏
    private val pnlList = ArrayList<TabInfo>()
    private var currPnlInfo: TabInfo? = null
    private var pnlDialog: TDialog? = null
    private var originPnlIndex:Int? = 0
    //有效时间
    private val timeList = ArrayList<TabInfo>()
    private var currTimeInfo: TabInfo? = null
    private var timeDialog: TDialog? = null
    //触发类型
    private val triggerList = ArrayList<TabInfo>()
    private var currTriggerInfo: TabInfo? = null
    private var triggerDialog: TDialog? = null
    private var originTriggerIndex:Int? = 0
    //下单二次确认
    private var tradeConfirm = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
        initListener()
    }


    override fun loadData() {
        unitList.add(TabInfo(getLineText("sl_str_contracts_unit"), 0))
        unitList.add(TabInfo(getLineText("sl_str_coin_unit"), 1))
        currUnitInfo = findTabInfo(unitList, LogicContractSetting.getContractUint(mActivity))
        originUnitIndex = currUnitInfo?.index

        pnlList.add(TabInfo(getLineText("sl_str_fair_price"), 0))
        pnlList.add(TabInfo(getLineText("sl_str_latest_price"), 1))
        currPnlInfo = findTabInfo(pnlList, LogicContractSetting.getPnlCalculate(mActivity))
        originPnlIndex = currPnlInfo?.index

        timeList.add(TabInfo(getLineText("sl_str_in_24_hours"), 0))
        timeList.add(TabInfo(getLineText("sl_str_in_7_days"), 1))
        currTimeInfo = findTabInfo(timeList, LogicContractSetting.getStrategyEffectTime(mActivity))

        triggerList.add(TabInfo(getLineText("sl_str_latest_price"), 1))
        triggerList.add(TabInfo(getLineText("sl_str_fair_price"), 2))
        triggerList.add(TabInfo(getLineText("sl_str_index_price"), 4))
        currTriggerInfo = findTabInfo(triggerList, LogicContractSetting.getTriggerPriceType(mActivity))
        originTriggerIndex = currTriggerInfo?.index

        tradeConfirm =  PreferenceManager.getInstance(mActivity).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true)
    }

    override fun initView() {
        initAutoTextView()
        tv_contracts_unit_value.text = currUnitInfo?.name
        tv_pnl_calculator_value.text = currPnlInfo?.name
        tv_effective_time_value.text = currTimeInfo?.name
        tv_trigger_type_value.text = currTriggerInfo?.name
    }

    private fun initAutoTextView() {
        title_layout.setContentTitle(getLineText("sl_str_contract_settings"))
        tv_contracts_unit_label.onLineText("sl_str_display_unit")
        tv_pnl_calculator_label.onLineText("sl_str_pnl_calculator")
        tv_book_confirm_label.onLineText("sl_str_book_confirm")
        tv_effective_time_label.onLineText("sl_str_strategy_effective_time")
        tv_plan_settings_label.onLineText("sl_str_plan_settings")
    }

    private fun initListener() {
        //触发类型
        rl_trigger_type_layout.setOnClickListener {
            triggerDialog = NewDialogUtils.showNewBottomListDialog(mActivity, triggerList, currTriggerInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currTriggerInfo = triggerList[index]
                    triggerDialog?.dismiss()
                    tv_trigger_type_value.text = currTriggerInfo?.name
                    LogicContractSetting.setTriggerPriceType(mActivity, currTriggerInfo!!.index)
                }
            })
        }
        //有效时间
        rl_effective_time_layout.setOnClickListener {
            timeDialog = NewDialogUtils.showNewBottomListDialog(mActivity, timeList, currTimeInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currTimeInfo = timeList[index]
                    timeDialog?.dismiss()
                    tv_effective_time_value.text = currTimeInfo?.name
                    LogicContractSetting.setStrategyEffectTime(mActivity, currTimeInfo!!.index)
                }
            })
        }
        //仓位展示单位
        rl_display_unit_layout.setOnClickListener {
            unitDialog = NewDialogUtils.showNewBottomListDialog(mActivity, unitList, currUnitInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currUnitInfo = unitList[index]
                    unitDialog?.dismiss()
                    tv_contracts_unit_value.text = currUnitInfo?.name
                    LogicContractSetting.setContractUint(mActivity, currUnitInfo!!.index)
                }
            })
        }
        //未实现盈亏tv_price_hint
        rl_pnl_calculator_layout.setOnClickListener {
            pnlDialog = NewDialogUtils.showNewBottomListDialog(mActivity, pnlList, currPnlInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
                override fun clickItem(index: Int) {
                    currPnlInfo = pnlList[index]
                    pnlDialog?.dismiss()
                    tv_pnl_calculator_value.text = currPnlInfo?.name
                    LogicContractSetting.setPnlCalculate(mActivity, currPnlInfo!!.index)
                }
            })
        }
        //下单二次确认
        switch_book_again.isChecked = tradeConfirm
        switch_book_again.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.getInstance(mActivity).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, isChecked)
            switch_book_again.isChecked = isChecked
            setViewSelect(switch_book_again,isChecked)
        }
        setViewSelect(switch_book_again,tradeConfirm)
    }

    fun setViewSelect(view: View, status: Boolean) {
        if (status) {
            view.setBackgroundResource(R.drawable.open)
        } else {
            view.setBackgroundResource(R.drawable.shut_down)
        }
    }


    override fun finish() {
        super.finish()
        if (currUnitInfo?.index != originUnitIndex || originTriggerIndex!= currTriggerInfo?.index || currPnlInfo?.index  != originPnlIndex) {
            LogicContractSetting.getInstance().refresh()
        }
    }

    companion object {
        fun show(activity: Activity) {
            val intent = Intent(activity, SlContractSettingActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private fun findTabInfo(list: ArrayList<TabInfo>, index: Int = 0): TabInfo {
        for (i in list.indices) {
            if (list[i].index == index) {
                return list[i]
            }
        }
        return list[0]
    }

}