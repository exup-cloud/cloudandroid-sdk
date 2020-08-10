package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractCashBook
import com.contract.sdk.impl.IResponse
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.adapter.ContractAssetRecordAdapter
import com.yjkj.chainup.contract.data.bean.TabInfo
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.NToastUtil
import kotlinx.android.synthetic.main.sl_activity_asset_record.*
import kotlinx.android.synthetic.main.sl_activity_contract_detail.collapsing_toolbar
import kotlinx.android.synthetic.main.sl_activity_contract_detail.toolbar

/**
 * 合约资金记录
 */
class SlContractAssetRecordActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_asset_record
    }

    //合约
    private var contractList = ArrayList<TabInfo>()
    private var mCurrContractInfo: TabInfo? = null
    private var contractDialog: TDialog? = null
    //类型
    private var typeList = ArrayList<TabInfo>()
    private var mCurrTypeInfo: TabInfo? = null
    private var typeDialog: TDialog? = null

    private var assetAdapter: ContractAssetRecordAdapter? = null
    private val mList = ArrayList<ContractCashBook>()

    private val mLimit = 0
    private var mOffset = 0
    private var isLoading = false


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadData()
        initView()
        ///
        initClickListener()
    }

    override fun loadData() {
        super.loadData()
        val symbol = intent.getStringExtra("symbol") ?: ""
        val type = intent.getIntExtra("type", 0)
        //合约
        val contractAccounts: List<ContractAccount>? = ContractUserDataAgent.getContractAccounts()
        if (contractAccounts != null && contractAccounts.isNotEmpty()) {
            for (index in contractAccounts.indices) {

                if(TextUtils.equals(contractAccounts[index].coin_code,symbol)){
                    mCurrContractInfo = TabInfo(contractAccounts[index].coin_code, index)
                }
                contractList.add(TabInfo(contractAccounts[index].coin_code, index))
            }
        } else {
            contractList.add(TabInfo("USDT", 0))
        }
        if (mCurrContractInfo == null) {
            mCurrContractInfo = contractList[0]
        }
        updateContractUI()
        //类型
        typeList.add(TabInfo(getLineText("sl_str_order_type_none"), 0))
        typeList.add(TabInfo(getLineText("sl_str_buy_open"), 1))
        typeList.add(TabInfo(getLineText("sl_str_buy_close"), 2))
        typeList.add(TabInfo(getLineText("sl_str_sell_close"), 3))
        typeList.add(TabInfo(getLineText("sl_str_sell_open"), 4))
        typeList.add(TabInfo(getLineText("sl_str_transfer_bb2contract"), 5))
        typeList.add(TabInfo(getLineText("sl_str_transfer_contract2bb"), 6))
        typeList.add(TabInfo(getLineText("sl_str_transferim_position2contract"), 9))
        typeList.add(TabInfo(getLineText("sl_str_transferim_contract2position"), 10))
        typeList.add(TabInfo(getLineText("contract_bonus_Issue"), 12))
        typeList.add(TabInfo(getLineText("sl_str_air_drop"), 21))
        typeList.add(TabInfo(getLineText("contract_asset_recprd_transfer_yunContractIn"), 22))
        typeList.add(TabInfo(getLineText("contract_asset_recprd_transfer_yunContractOut"), 23))
        for (typeItem  in typeList){
            if(typeItem.index == type){
                mCurrTypeInfo = typeItem
                break
            }
        }
        if(mCurrTypeInfo == null){
            mCurrTypeInfo = typeList[0]
        }
        updateTypeUI()
        assetAdapter = ContractAssetRecordAdapter(this,mList)

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

        ll_layout.layoutManager = LinearLayoutManager(this)
        assetAdapter?.bindToRecyclerView(ll_layout)
        assetAdapter?.emptyView = EmptyForAdapterView(this)
        ll_layout.adapter = assetAdapter
//        assetAdapter?.setOnLoadMoreListener {
//            if (!isLoading) {
//                loadDataFromNet()
//            }
//        }

        updateContractUI()
        updateTypeUI()

    }

    private fun initAutoTextView() {
        collapsing_toolbar.title = getLineText("sl_str_asset_record")
    }

    private fun initClickListener() {
        //选择合约
        ll_tab_contract.setOnClickListener {
            showSelectContractDialog()
        }
        //选择类型
        ll_tab_type.setOnClickListener {
            showSelectTypeDialog()
        }
    }

    private fun showSelectTypeDialog() {
        typeDialog = NewDialogUtils.showNewBottomListDialog(mActivity, typeList, mCurrTypeInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                mCurrTypeInfo = typeList[index]
                typeDialog?.dismiss()
                typeDialog = null
                updateTypeUI()
                loadDataFromNet()
            }
        })
    }


    private fun showSelectContractDialog() {
        contractDialog = NewDialogUtils.showNewBottomListDialog(mActivity, contractList, mCurrContractInfo!!.index, object : NewDialogUtils.DialogOnItemClickListener {
            override fun clickItem(index: Int) {
                mCurrContractInfo = contractList[index]
                contractDialog?.dismiss()
                contractDialog = null
                updateContractUI()
                loadDataFromNet()
            }
        })
    }

    private fun updateContractUI() {
        tv_tab_contract.text = mCurrContractInfo?.name
        mOffset = 0
    }

    private fun updateTypeUI() {
        tv_tab_type.text = mCurrTypeInfo?.name
        mOffset = 0
    }

    private fun loadDataFromNet() {
        var action: IntArray? = getTypeAction()
        if (mOffset == 0) {
            showLoadingDialog()
        }
        isLoading = true
        ContractUserDataAgent.loadCashBooks(0,action,mCurrContractInfo?.name, mLimit, mOffset,object: IResponse<List<ContractCashBook>>(){
            override fun onSuccess(data: List<ContractCashBook>) {
                closeLoadingDialog()
                isLoading = false
                if (mOffset == 0) {
                    mList.clear()
                }
                if (data != null && data.isNotEmpty()) {
                    mList.addAll(data)
//                    mOffset = mList.size
                    assetAdapter?.notifyDataSetChanged()
                    assetAdapter?.setEnableLoadMore(true)
                    assetAdapter?.loadMoreComplete()
                } else {
                    assetAdapter?.loadMoreEnd()
                }
                assetAdapter?.notifyDataSetChanged()
                assetAdapter?.disableLoadMoreIfNotFullPage()
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
                isLoading = false
                NToastUtil.showToast(msg, false)
                assetAdapter?.loadMoreEnd()
                assetAdapter?.disableLoadMoreIfNotFullPage()
            }

        })
    }

    private fun getTypeAction(): IntArray? {
        var action1: IntArray? = null
        when (mCurrTypeInfo?.index) {
            1 -> {
                action1 = intArrayOf(1)
            }
            2 -> {
                action1 = intArrayOf(2)
            }
            3 -> {
                action1 = intArrayOf(3)
            }
            4 -> {
                action1 = intArrayOf(4)
            }
            5, 7 -> {
                action1 = intArrayOf(5, 7)
            }
            6, 8 -> {
                action1 = intArrayOf(6, 8)
            }
            9 -> {
                action1 = intArrayOf(9)
            }
            10 -> {
                action1 = intArrayOf(10)
            }
            11 -> {
                action1 = intArrayOf(11)
            }
            12 -> {
                action1 = intArrayOf(12)
            }
            21 -> {
                action1 = intArrayOf(21)
            }
            22 -> {
                action1 = intArrayOf(22)
            }
            23 -> {
                action1 = intArrayOf(23)
            }
        }
        return action1
    }

    companion object {
        fun show(activity: Activity, symbol: String = "USDT", type: Int = 0) {
            val intent = Intent(activity, SlContractAssetRecordActivity::class.java)
            intent.putExtra("symbol",symbol)
            intent.putExtra("type",type)
            activity.startActivity(intent)
        }
    }


}