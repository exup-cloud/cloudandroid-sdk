package com.yjkj.chainup.contract.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.adapter.ContractDropAdapter
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import kotlinx.android.synthetic.main.fragment_sl_search_coin.*
import android.arch.lifecycle.Observer
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractTicker
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.util.StringUtil
import java.util.*

class SlCoinSearchItemFragment : NBaseFragment() {
    /// 0 USDT 1币本位 2模拟
    private var index = 0
    private var contractDropAdapter: ContractDropAdapter? = null
    private val tickers: ArrayList<ContractTicker> = ArrayList()
    private val localTickers: ArrayList<ContractTicker> = ArrayList()
    override fun setContentView(): Int {
        return R.layout.fragment_sl_search_coin
    }

    override fun initView() {
        index = arguments!!.getInt(ParamConstant.CUR_INDEX)
        contractDropAdapter = ContractDropAdapter(tickers)
        rv_search_coin.layoutManager = LinearLayoutManager(context)
        contractDropAdapter?.bindToRecyclerView(rv_search_coin)
        contractDropAdapter?.setEmptyView(R.layout.item_new_empty)
        rv_search_coin.adapter = contractDropAdapter
        updateData()

        NLiveDataUtil.observeData(this, Observer {
            it?.let {
                if (MessageEvent.coinSearchType == it.msg_type) {
                    var content = it.msg_content
                    if (content is String) {
                        if (StringUtil.checkStr(content)) {
                            tickers.clear()
                            for (index in localTickers.indices){
                                if(localTickers[index].symbol.contains(content.toUpperCase())){
                                    tickers.add(localTickers[index])
                                }
                            }
                            contractDropAdapter?.notifyDataSetChanged()
                        }else{
                            tickers.clear()
                            tickers.addAll(localTickers)
                            contractDropAdapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

    }

    private fun updateData() {
        val data: List<ContractTicker> = ContractPublicDataAgent.getContractTickers()
        if (data != null) {
            for (i in data.indices) {
                var item = data[i]

                when (index) {
                    0 -> {
                        if (item.block == Contract.CONTRACT_BLOCK_USDT) {
                            tickers.add(item)
                            localTickers.add(item)
                        }
                    }
                    1 -> {
                        if (item.block == Contract.CONTRACT_BLOCK_MAIN || item.block == Contract.CONTRACT_BLOCK_INNOVATION) {
                            tickers.add(item)
                            localTickers.add(item)
                        }
                    }
                    2 -> {
                        if (item.block == Contract.CONTRACT_BLOCK_SIMULATION) {
                            tickers.add(item)
                            localTickers.add(item)
                        }
                    }
                }

            }
        }
       // LogUtil.d("DEBUG","侧边栏:${tickers.size}---$index")
        contractDropAdapter?.notifyDataSetChanged()
    }


    companion object {
        @JvmStatic
        fun newInstance(index: Int): SlCoinSearchItemFragment {
            val fg = SlCoinSearchItemFragment()
            val bundle = Bundle()
            bundle.putInt(ParamConstant.CUR_INDEX, index)
            fg.arguments = bundle
            return fg
        }
    }
}