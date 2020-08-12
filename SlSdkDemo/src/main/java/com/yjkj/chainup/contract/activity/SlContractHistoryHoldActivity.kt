package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.impl.IResponse
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.adapter.HistoryHoldContractAdapter
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NToastUtil
import kotlinx.android.synthetic.main.sl_activity_contract_detail.collapsing_toolbar
import kotlinx.android.synthetic.main.sl_activity_contract_detail.toolbar
import kotlinx.android.synthetic.main.sl_activity_contract_history_hold.*

/**
 * 合约历史持仓
 */
class SlContractHistoryHoldActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_contract_history_hold
    }

    //合约
    private var contractList = ArrayList<TabInfo>()
    private var currContractInfo: TabInfo? = null
    private var contractDialog: TDialog? = null

    private var historyHoldAdapter: HistoryHoldContractAdapter? = null
    private val mList = ArrayList<ContractPosition>()


    private val mLimit = 0
    private var mOffset = 0
    private var isLoading = false


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadData()
        initView()
        initClickListener()
    }

    override fun loadData() {
        super.loadData()
        val contractId = intent.getIntExtra("contractId",0)
        //合约
        val data =  ContractPublicDataAgent.getContractTickers()
        if(data!=null){
            for (i in data.indices) {
                var item = data[i]
                val tabInfo = TabInfo(item.symbol ?:"",item.instrument_id)
                if(contractId == item.instrument_id){
                    currContractInfo = tabInfo
                }
                contractList.add(tabInfo)
            }
        }
        if (contractList.size > 0) {
            currContractInfo = currContractInfo?:contractList[0]
        } else {
            finish()
            return
        }
        updateContractUI()

        loadDataFromNet()

    }


    override fun initView() {
        initAutoTextView()
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        collapsing_toolbar?.let {
            it.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            it.expandedTitleGravity = Gravity.BOTTOM
        }

        historyHoldAdapter = HistoryHoldContractAdapter(mList)
        ll_layout.layoutManager = LinearLayoutManager(mContext)
        historyHoldAdapter?.bindToRecyclerView(ll_layout)
        historyHoldAdapter?.setEmptyView(R.layout.ly_empty_withdraw_address)
        ll_layout.adapter = historyHoldAdapter
//        historyHoldAdapter?.setOnLoadMoreListener {
//            if(!isLoading){
//                loadDataFromNet()
//            }
//        }

        updateContractUI()

    }

    private fun initAutoTextView() {
        collapsing_toolbar.title = getLineText("sl_str_history_hold")
    }

    private fun initClickListener() {
        //选择合约
        ll_tab_contract.setOnClickListener {
            showSelectContractDialog()
        }
    }



    private fun showSelectContractDialog() {
        contractDialog = NewDialogUtils.showNewBottomListDialog(mActivity, contractList, currContractInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                currContractInfo = contractList[index]
                contractDialog?.dismiss()
                contractDialog = null
                updateContractUI()
                loadDataFromNet()
            }
        })
    }

    private fun updateContractUI() {
        tv_tab_contract.text = currContractInfo?.name
        mOffset = 0
    }


    private fun loadDataFromNet() {
        if(mOffset == 0){
            showLoadingDialog()
        }
        ContractUserDataAgent.loadContractPosition(currContractInfo?.index!!,4, mOffset, mLimit,object:IResponse<MutableList<ContractPosition>>(){
            override fun onSuccess(data: MutableList<ContractPosition>) {
                closeLoadingDialog()
                isLoading = false
                if (mOffset == 0) {
                    mList.clear()
                }
                if (data != null && data.isNotEmpty()) {
                    mList.addAll(data)
//                    mOffset = mList.size
                    historyHoldAdapter?.notifyDataSetChanged()
                    historyHoldAdapter?.setEnableLoadMore(true)
                    historyHoldAdapter?.loadMoreComplete()
                }else{
                    historyHoldAdapter?.loadMoreEnd()
                }
                LogUtil.d("DEBUG","仓位数量:"+mList.size)
                historyHoldAdapter?.notifyDataSetChanged()
                historyHoldAdapter?.disableLoadMoreIfNotFullPage()
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
                isLoading = false
                NToastUtil.showToast(msg, false)
                historyHoldAdapter?.loadMoreEnd()
                historyHoldAdapter?.disableLoadMoreIfNotFullPage()
            }

        })
    }


    companion object {
        fun show(activity: Activity, contractId : Int) {
            val intent = Intent(activity, SlContractHistoryHoldActivity::class.java)
            intent.putExtra("contractId",contractId)
            activity.startActivity(intent)
        }
    }


}