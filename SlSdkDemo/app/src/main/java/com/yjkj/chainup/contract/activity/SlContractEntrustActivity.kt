package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractOrders
import com.contract.sdk.impl.IResponse
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.fragment.SlContractPlanEntrustFragment
import com.yjkj.chainup.contract.fragment.SlContractPriceEntrustFragment
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.ContractEntrustTabWidget
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.NToastUtil
import kotlinx.android.synthetic.main.sl_activity_contract_entrust.*
import kotlin.math.abs

/**
 * 合约当前/历史委托
 */
class SlContractEntrustActivity : NBaseActivity() {
    override fun setContentView() = R.layout.sl_activity_contract_entrust
    //是否是当前委托
    private var isCurrentEntrust = true
    //全部方向/全部类型
    private var sideList = ArrayList<TabInfo>()
    private var mCurrSideInfo: TabInfo? = null
    private var sideDialog: TDialog? = null
    private var typeList = ArrayList<TabInfo>()
    private var mCurrTypeInfo: TabInfo? = null
    //限价和计划委托
    private var entrustList = ArrayList<TabInfo>()
    private var mCurrEntrustInfo: TabInfo? = null
    private var entrustDialog: TDialog? = null
    //合约
    private var contractList = ArrayList<TabInfo>()
    private var mCurrContractInfo: TabInfo? = null
    private var contractDialog: TDialog? = null

    private var priceEntrustFragment = SlContractPriceEntrustFragment.newInstance(true)
    private var planEntrustFragment = SlContractPlanEntrustFragment.newInstance(true)
    private var currFragment = Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
        Handler().postDelayed({
            doFilter()
        },500)
    }

    override fun initView() {
        initAutoTextView()
        updateContractUI()
        showFragment()
        initClickListener()
        doSwitchTabSwitch()
    }

    private fun initAutoTextView(){
        tv_tab_entrust.onLineText("sl_str_limit_entrust")
        tv_cancel_orders.onLineText("sl_str_cancel_orders")
    }

    override fun loadData() {
        //合约
        if (loadContractData()) return

        // 限价/计划委托
        entrustList.add(TabInfo(getLineText("sl_str_limit_entrust"), 0))
        entrustList.add(TabInfo(getLineText("sl_str_plan_entrust"), 1))
        mCurrEntrustInfo = entrustList[0]
        //方向和类型
        sideList.add(TabInfo(getLineText("sl_str_contract_side_none"), 0))
        sideList.add(TabInfo(getLineText("sl_str_buy_open"), 1))
        sideList.add(TabInfo(getLineText("sl_str_buy_close"), 2))
        sideList.add(TabInfo(getLineText("sl_str_sell_close"), 3))
        sideList.add(TabInfo(getLineText("sl_str_sell_open"), 4))
        mCurrSideInfo = sideList[0]
        typeList.add(TabInfo(getLineText("sl_str_order_type_none"), 0))
        typeList.add(TabInfo(getLineText("sl_str_order_complete"), 1))
        typeList.add(TabInfo(getLineText("sl_str_user_canceled"), 2))
        typeList.add(TabInfo(getLineText("sl_str_system_canceled"), 3))
        typeList.add(TabInfo(getLineText("sl_str_order_part_filled"), 4))
        mCurrTypeInfo = typeList[0]
    }

    /**
     * 加载合约列表
     */
    private fun loadContractData(): Boolean {
        val contractId = intent.getIntExtra("contractId",0)
       val data =  ContractPublicDataAgent.getContractTickers()
        if(data!=null){
            for (i in data.indices) {
                var item = data[i]
                val tabInfo = TabInfo(item.symbol,item.instrument_id)
                if(contractId == item.instrument_id){
                    mCurrContractInfo = tabInfo
                }
                contractList.add(tabInfo)
            }
        }
        if (contractList.size > 0) {
            mCurrContractInfo = mCurrContractInfo?:contractList[0]
        } else {
            finish()
            return true
        }
        return false
    }

    /**
     * 是否展示“全部撤销订单”,
     */
    fun updateCancelUI(isShow:Boolean = false){
        tv_cancel_orders.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    private fun initClickListener() {
        /**
         * 取消全部订单
         */
        tv_cancel_orders.setOnClickListener {
            var list  = ArrayList<ContractOrder>()
            if(mCurrEntrustInfo?.index == 0){//限价
                list.addAll(priceEntrustFragment.getList())
            }else{//计划委托
                list.addAll(planEntrustFragment.getList())
            }
            if(list.size > 0){
               NewDialogUtils.showDialog(mActivity,getLineText("sl_str_cancel_all_order_tips"),false,object: NewDialogUtils.DialogBottomListener {
                   override fun sendConfirm() {
                        doCancelAllOrder(list)
                   }
               },getLineText("common_text_tip"))
            }


        }
        ic_close?.setOnClickListener { finish() }
        //选择合约
        ll_tab_contract.setOnClickListener {
            showSelectContractDialog()
        }
        //选择 限价/计划委托
        ll_tab_entrust.setOnClickListener {
            showSelectEntrustDialog()
        }
        //选择方向
        ll_tab_side_layout.setOnClickListener {
            showSelectSideDialog()
        }
        sub_tab_layout.bindTabListener(object : ContractEntrustTabWidget.ContractEntrustTabListener {
            override fun onTab(index: Int) {
                    isCurrentEntrust = index == 0
                    doSwitchTabSwitch()
                    doFilter()
            }

        })
        //监听滑动
        ly_appbar?.addOnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) >= 130) {
                if (tv_title?.visibility == View.GONE) {
                    tv_title?.visibility = View.VISIBLE
                    //  sub_tab_layout?.visibility = View.GONE
                }
            } else {
                if (tv_title?.visibility == View.VISIBLE) {
                    tv_title?.visibility = View.GONE
                    // sub_tab_layout?.visibility = View.VISIBLE
                }
            }
        }

    }

    /**
     * 取消所有订单
     */
    private fun doCancelAllOrder(list: ArrayList<ContractOrder>) {
        val orders = ContractOrders()
        orders.contract_id = mCurrContractInfo?.index!!

        for (i in list.indices){
            orders.orders.add(list[i])
        }
        showLoadingDialog()
        val response: IResponse<MutableList<Long>> = object : IResponse<MutableList<Long>>() {
            override fun onSuccess(data: MutableList<Long>) {
                closeLoadingDialog()
                if (data != null && data.isNotEmpty()) {
                    NToastUtil.showToast(getLineText("sl_str_some_orders_cancel_failed"),false)
                }else{
                    NToastUtil.showToast(getLineText("sl_str_cancel"),false)
                }
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
                NToastUtil.showToast(msg,false)
            }
        }

        if(mCurrEntrustInfo?.index == 0) {//限价
            ContractUserDataAgent.doCancelOrders(orders, response)
        }else{
            ContractUserDataAgent.doCancelPlanOrders(orders, response)
        }
    }

    private fun showSelectContractDialog() {
        contractDialog = NewDialogUtils.showNewBottomListDialog(mActivity, contractList, mCurrContractInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                mCurrContractInfo = contractList[index]
                contractDialog?.dismiss()
                updateContractUI()
                doFilter()
            }
        })
    }

    private fun showSelectEntrustDialog() {
        entrustDialog = NewDialogUtils.showNewBottomListDialog(mActivity, entrustList, mCurrEntrustInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                mCurrEntrustInfo = entrustList[index]
                entrustDialog?.dismiss()
                updateEntrustUI()
                showFragment()
                doFilter()
            }
        })
    }

    private fun showSelectSideDialog() {
        val list = ArrayList<TabInfo>()
        var index = if (isCurrentEntrust) {
            list.addAll(sideList)
            mCurrSideInfo!!.index
        } else {
            list.addAll(typeList)
            mCurrTypeInfo!!.index
        }
        sideDialog = NewDialogUtils.showNewBottomListDialog(mActivity, list, index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                if (isCurrentEntrust) {
                    mCurrSideInfo = sideList[index]
                } else {
                    mCurrTypeInfo = typeList[index]
                }

                sideDialog?.dismiss()
                updateSideUI()
                doFilter()
            }
        })
    }


    private fun updateContractUI() {
        tv_tab_contract.text = mCurrContractInfo?.name
    }

    private fun updateSideUI() {
        if (isCurrentEntrust) {
            tv_tab_side.text = mCurrSideInfo?.name
        } else {
            tv_tab_side.text = mCurrTypeInfo?.name
        }
    }

    private fun updateEntrustUI() {
        tv_tab_entrust.text = mCurrEntrustInfo?.name
    }

    /**
     * 切换当前和历史委托
     */
    private fun doSwitchTabSwitch() {
        if (isCurrentEntrust) {
            tv_title.onLineText("contract_text_currentEntrust")
        } else {
            tv_title.onLineText("contract_text_historyCommision")
            updateCancelUI(false)
        }
        priceEntrustFragment.setIsCurrentEntrust(isCurrentEntrust)
        planEntrustFragment.setIsCurrentEntrust(isCurrentEntrust)
        updateSideUI()
    }

    private fun showFragment() {
        val transaction = supportFragmentManager!!.beginTransaction()
        currFragment = if (mCurrEntrustInfo?.index == 0) {
            if (!priceEntrustFragment.isAdded) {
                transaction.hide(currFragment).add(R.id.fragment_container, priceEntrustFragment, "0")
            } else {
                transaction.hide(currFragment).show(priceEntrustFragment)
            }
            priceEntrustFragment
        } else {
            if (!planEntrustFragment.isAdded) {
                transaction.hide(currFragment).add(R.id.fragment_container, planEntrustFragment, "1")
            } else {
                transaction.hide(currFragment).show(planEntrustFragment)
            }
            planEntrustFragment
        }
        transaction.commitNow()
    }


    fun doFilter() {
        if(planEntrustFragment == null){
            return
        }
        val contractId = mCurrContractInfo?.index?:0
        if (mCurrEntrustInfo?.index == 0) {//限价
            if (isCurrentEntrust) {
                priceEntrustFragment.doFilter(contractId, mCurrSideInfo!!.index, mCurrTypeInfo!!.index,isCurrentEntrust)
            } else {
                priceEntrustFragment.doFilter(contractId, mCurrSideInfo!!.index, mCurrTypeInfo!!.index,isCurrentEntrust)
            }
        } else {
            if (isCurrentEntrust) {
                planEntrustFragment.doFilter(contractId, mCurrSideInfo!!.index, mCurrTypeInfo!!.index,isCurrentEntrust)
            } else {
                planEntrustFragment.doFilter(contractId, mCurrSideInfo!!.index, mCurrTypeInfo!!.index,isCurrentEntrust)
            }
        }
    }

    companion object {
        fun show(activity: Activity, contractId: Int = 0, entrustIndex: Int = 0) {
            val intent = Intent(activity, SlContractEntrustActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("contractId", contractId)
            bundle.putInt("entrustIndex", entrustIndex)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}