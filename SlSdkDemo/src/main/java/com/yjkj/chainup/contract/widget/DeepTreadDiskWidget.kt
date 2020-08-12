package com.yjkj.chainup.contract.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.contract.sdk.data.Contract
import com.contract.sdk.data.DepthData
import com.contract.sdk.extra.dispense.DataDepthHelper
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.contract.adapter.BuySellContractAdapter
import com.yjkj.chainup.contract.adapter.BuySellContractAdapter.OnBuySellContractClickedListener
import java.util.*

/**
 * 深度交易盘面
 */
class DeepTreadDiskWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle), OnBuySellContractClickedListener {
    private val buySellContractAdapter: BuySellContractAdapter
    private var uiType = 1 //1买 2卖
    private var contract: Contract? = null
    private var listener: OnBuySellContractClickedListener? = null
    private var showNum = 5
    fun bindContract(contract: Contract?, listener: OnBuySellContractClickedListener?) {
        this.listener = listener
        this.contract = contract
    }

    fun initData(list: List<DepthData>?) {
        var list = list
        if (list == null) {
            list = ArrayList()
        }
        if (uiType == 1) {
            buySellContractAdapter.setData(list, uiType, showNum, contract)
        } else {
            buySellContractAdapter.setData(list, uiType, showNum, contract)
        }
        buySellContractAdapter.notifyDataSetChanged()
    }

    fun getList(): List<DepthData>?{
        return buySellContractAdapter.mList
    }

    /**
     * 更改盘面类型
     * @param type
     */
    fun updateDeepType(type: Int) {
        when (type) {
            AppConstant.DEFAULT_TAPE -> showNum = 5
            AppConstant.SELL_TAPE -> showNum = if (uiType == 1) {
                0
            } else {
                10
            }
            AppConstant.BUY_TAPE -> showNum = if (uiType == 1) {
                10
            } else {
                0
            }
        }
        if (showNum == 0) {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            }
        } else {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
            }

            DataDepthHelper.instance?.getDepthSource(showNum){buyList, sellList ->
                if(uiType == 1){
                    buySellContractAdapter.setData(buyList, uiType, showNum, contract)
                }else{
                    buySellContractAdapter.setData(sellList, uiType, showNum, contract)
                }
            }

            buySellContractAdapter.notifyDataSetChanged()
        }
    }

    override fun onBuySellContractClick(depthData: DepthData?, showVol: String?, flag: Int) {
        if (listener != null) {
            listener!!.onBuySellContractClick(depthData, showVol, flag)
        }
    }

    override fun onBuySellContractVolClick(depthData: DepthData?, showVol: String?, flag: Int) {
        if (listener != null) {
            listener!!.onBuySellContractVolClick(depthData, showVol, flag)
        }
    }



    init {
        uiType = tag.toString().toInt()
        buySellContractAdapter = BuySellContractAdapter(context, this)
        layoutManager = LinearLayoutManager(context)
        buySellContractAdapter.setData(ArrayList(), uiType, showNum, contract)
        adapter = buySellContractAdapter
    }
}