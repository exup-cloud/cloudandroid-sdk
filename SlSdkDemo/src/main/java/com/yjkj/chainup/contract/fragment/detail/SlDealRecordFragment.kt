package com.yjkj.chainup.contract.fragment.detail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.ContractTrade
import com.contract.sdk.extra.dispense.DataTradeHelper
import com.contract.sdk.impl.ContractTradeListener
import com.contract.sdk.impl.IResponse
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.adapter.TradeHistoryAdapter
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.util.ColorUtil
import kotlinx.android.synthetic.main.sl_fragment_deal_record.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

/**
 * 成交记录
 */
class SlDealRecordFragment : NBaseFragment() {
    override fun setContentView(): Int {
        return R.layout.sl_fragment_deal_record
    }

    private var contractId = 0

    private var riseColor = ColorUtil.getMainColorType(isRise = true)
    private var fallColor = ColorUtil.getMainColorType(isRise = false)

    private val riseMinorColor = ColorUtil.getMinorColorType(isRise = true)
    private val fallMinorColor = ColorUtil.getMinorColorType(isRise = false)

    private var mList = ArrayList<ContractTrade>()
    private var adapter: TradeHistoryAdapter? = null

    override fun initView() {
        initAutoTextView()
        adapter = TradeHistoryAdapter(mActivity)
        adapter?.setData(mList)
        recycler_deep.layoutManager = LinearLayoutManager(mActivity)
        recycler_deep.adapter = adapter
        contractId = arguments?.getInt("contractId", 1) ?: 1
        ContractPublicDataAgent.subscribeTradeWs(contractId)
        ContractPublicDataAgent.registerTradeWsListener(this, object : ContractTradeListener() {
            /**
             * 初始化
             * @param allData 全量
             */
            override fun onWsContractTrade(id: Int, allData: ArrayList<ContractTrade>) {
                if (contractId == id) {
                    mList.clear()
                    mList.addAll(allData)
                    adapter?.notifyDataSetChanged()
                }
            }

        }, 20)
    }

    private fun initAutoTextView() {
        tv_time_title.onLineText("kline_text_dealTime")
        tv_price_title.onLineText("contract_text_price")
        tv_amount_title.onLineText("charge_text_volume")
    }


    fun switchContract(contractId: Int = 0) {
        this.contractId = contractId
        mList.clear()
        if (isAdded) {
            adapter?.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(contractId: Int) =
                SlDealRecordFragment().apply {
                    arguments = Bundle().apply {
                        putInt("contractId", contractId)
                    }
                }
    }


}