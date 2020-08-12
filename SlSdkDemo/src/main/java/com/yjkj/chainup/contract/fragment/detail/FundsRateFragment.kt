package com.yjkj.chainup.contract.fragment.detail

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractFundingRate
import com.contract.sdk.impl.IResponse
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.adapter.FundsRateAdapter
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.ToastUtils
import kotlinx.android.synthetic.main.sl_fragment_cotract_detail_other_recycler.*

/**
 *资金费率
 */
class FundsRateFragment : NBaseFragment(){
    override fun setContentView(): Int {
        return R.layout.sl_fragment_cotract_detail_other_recycler
    }

    var contractId  = 0
    var contract: Contract? = null
    var mList = ArrayList<ContractFundingRate>()
    var adapter: FundsRateAdapter?= null

    override fun loadData() {
        super.loadData()
        contractId = activity?.intent?.getIntExtra("contractId", 0)!!
        contract = ContractPublicDataAgent.getContract(contractId)

        loadDataFromNet()
    }


    override fun initView() {
        recycler_fund_layout.layoutManager = LinearLayoutManager(mActivity)
        adapter = FundsRateAdapter(context!!,mList)
        adapter?.bindToRecyclerView(recycler_fund_layout)
        adapter?.emptyView = EmptyForAdapterView(context?:return)
        recycler_fund_layout.adapter = adapter

        adapter?.settlementInterval = contract?.settlement_interval

        initHeaderView()
    }

    private fun initHeaderView() {
        val headerView = LayoutInflater.from(mActivity).inflate(R.layout.sl_item_header_layout,recycler_fund_layout,false)
        adapter?.addHeaderView(headerView)
        headerView.findViewById<TextView>(R.id.tv_header_left).onLineText("kline_text_dealTime")
        headerView.findViewById<TextView>(R.id.tv_header_right).onLineText("sl_str_funds_rate")
        headerView.findViewById<TextView>(R.id.tv_header_center).onLineText("sl_str_funds_rate_interval")
    }

    private fun loadDataFromNet() {
        ContractPublicDataAgent.loadFundingRate(contractId,object:IResponse<MutableList<ContractFundingRate>>(){
            override fun onSuccess(data: MutableList<ContractFundingRate>) {
                if (data != null) {
                    mList.addAll(data)
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onFail(code: String, msg: String) {
                ToastUtils.showToast(ContractSDKAgent.context, msg)
            }

        })
    }

}