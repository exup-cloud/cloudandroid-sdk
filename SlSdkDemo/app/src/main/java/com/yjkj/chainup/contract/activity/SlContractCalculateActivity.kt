package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.fragment.calculate.SlLiquidationPriceFragment
import com.yjkj.chainup.contract.fragment.calculate.SlPlCalculatorFragment
import com.yjkj.chainup.contract.fragment.calculate.SlProfitRateFragment
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.adapter.PageAdapter
import kotlinx.android.synthetic.main.sl_activity_contract_calculate.*

/**
 * 合约计算器
 */
class SlContractCalculateActivity : NBaseActivity(){
    override fun setContentView(): Int {
       return R.layout.sl_activity_contract_calculate
    }
    private var contractId = 0
    //合约类型
    private var currContractInfo: Contract? = null

    private var plCalculatorFragment = SlPlCalculatorFragment()
    private var liquidationPriceFragment = SlLiquidationPriceFragment()
    private var profitRateFragment = SlProfitRateFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contractId = intent.getIntExtra("contractId",0)
        loadData()
        initView()
        initListener()
    }

    override fun loadData() {
        //合约类型
        val contractBasics: List<Contract> = ContractPublicDataAgent.getContracts()
        if(contractBasics!=null){
            for (i in contractBasics.indices){
                var contract = contractBasics[i]
                if(contract.instrument_id == contractId){
                    currContractInfo = contract
                }
            }
            if(currContractInfo == null && contractBasics.isNotEmpty()){
                currContractInfo = contractBasics[0]
            }
        }

        if(currContractInfo == null){
            finish()
        }
    }
    override fun initView() {
        initAutoTextView()
        tv_contracts_type_value.text = currContractInfo?.getDisplayName(mActivity)
        //tab
        val showTitles = java.util.ArrayList<String>()
        val fragments = java.util.ArrayList<Fragment>()
        showTitles.add(getLineText("sl_str_pl_calculator"))
        showTitles.add(getLineText("sl_str_liquidation_price"))
        showTitles.add(getLineText("sl_str_profit_rate"))
        fragments.add(plCalculatorFragment)
        fragments.add(liquidationPriceFragment)
        fragments.add(profitRateFragment)
        val pageAdapter = PageAdapter(supportFragmentManager, showTitles, fragments)
        vp_layout.adapter = pageAdapter
        vp_layout.offscreenPageLimit = 3
        tl_tab_layout.setViewPager(vp_layout, showTitles.toTypedArray())
        tl_tab_layout.currentTab = 1
        tl_tab_layout.currentTab = 0
        tl_tab_layout.postDelayed(object:Runnable{
            override fun run() {
                doSwitchContract()
            }

        },10)

    }

    private fun initAutoTextView() {
        title_layout.setContentTitle(getLineText("sl_str_contract_calculator"))
        tv_contract_type_label.onLineText("sl_str_contract_type")
    }

    private fun initListener() {
        //合约类型
        rl_contract_type_layout.setOnClickListener {
            SlContractSearchActivity.show(mActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data!=null){
            val instrumentId = data.getIntExtra("instrumentId",0)
            if(instrumentId != currContractInfo?.instrument_id){
                currContractInfo = ContractPublicDataAgent.getContract(instrumentId)
                doSwitchContract()
            }
        }
    }


    private fun doSwitchContract() {
        plCalculatorFragment.switchContract(currContractInfo)
        liquidationPriceFragment.switchContract(currContractInfo)
        profitRateFragment.switchContract(currContractInfo)
        tv_contracts_type_value.text = currContractInfo?.getDisplayName(mActivity)
    }


    companion object {
        fun show(activity: Activity,contractId : Int) {
            val intent = Intent(activity, SlContractCalculateActivity::class.java)
            intent.putExtra("contractId",contractId)
            activity.startActivity(intent)
        }
    }

}