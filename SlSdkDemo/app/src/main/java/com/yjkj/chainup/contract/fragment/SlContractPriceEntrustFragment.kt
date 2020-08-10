package com.yjkj.chainup.contract.fragment

import android.os.Bundle
import android.text.TextUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.impl.ContractOrderListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlContractEntrustActivity
import com.yjkj.chainup.contract.adapter.ContractPriceEntrustAdapter
import com.yjkj.chainup.contract.widget.MyLinearLayoutManager
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.LogUtil
import kotlinx.android.synthetic.main.fragment_sl_contract_hold.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.json.JSONObject

/**
 * 合约限价委托
 */
class SlContractPriceEntrustFragment : NBaseFragment() {
    private var adapter: ContractPriceEntrustAdapter? = null
    private var mList = ArrayList<ContractOrder>()

    //是否是当前委托
    private var isCurrentEntrust = true

    private var contract: Contract? = null
    private var mLimit = 0
    private var mOffset = 0
    //方向
    private var direction = 0
    //类型
    private var type = 0

    override fun setContentView(): Int {
        return R.layout.fragment_sl_contract_hold
    }

    override fun initView() {
        isCurrentEntrust = arguments?.getBoolean("isCurrentEntrust")?:true

        rv_hold_contract.layoutManager = MyLinearLayoutManager(context)
        adapter = ContractPriceEntrustAdapter(context!!,mList)
        adapter?.bindToRecyclerView(rv_hold_contract)
        adapter?.emptyView = EmptyForAdapterView(context?:return)
        adapter?.setIsCurrentEntrust(isCurrentEntrust)
        rv_hold_contract.adapter = adapter


        ContractUserDataAgent.registerContractOrderWsListener(this,object:ContractOrderListener(){
            /**
             * 合约订单更新
             */
            override fun onWsContractOrder(contractId: Int) {
                if(contractId != contract?.instrument_id){
                    return
                }
                if (mOffset == 0) {
                    mList.clear()
                }
                    if (isCurrentEntrust) {
                        filterSide(ContractUserDataAgent.getContractOrder(contractId))
                    } else {
                        filterType(ContractUserDataAgent.getContractOrder(contractId))
                    }

                adapter?.notifyDataSetChanged()
                updateAllCancelUi()
            }

        })
    }



    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (isCurrentEntrust) {
                mOffset = 0
                loadDataFromNet()
            }
        }
    }

    fun setIsCurrentEntrust(isCurrentEntrust: Boolean = true) {
        this.isCurrentEntrust = isCurrentEntrust
        adapter?.setIsCurrentEntrust(isCurrentEntrust)
    }

    fun bindContract(contract: Contract,resetData:Boolean = false) {
        mOffset = 0
        this.contract = contract
        if(resetData){
            mList.clear()
            adapter?.notifyDataSetChanged()
        }
        loadDataFromNet()
    }

    fun loadDataFromNet() {
        if (mActivity == null || contract == null || isHidden) {
            return
        }
        if (!ContractSDKAgent.isLogin) {
            return
        }
//        val contractJson = UtilSystem.readAssertResource(mActivity, "contractOrder.json")
//        list.clear()
//        list.addAll(Gson().fromJson<List<ContractOrder>>(contractJson, object : TypeToken<List<ContractOrder?>?>() {}.type))
//        adapter?.notifyDataSetChanged()


        if(isCurrentEntrust){
          ContractUserDataAgent.getContractOrder(contract?.instrument_id!!,true)
            closeLoadingDialog()
        }else{
            ContractUserDataAgent.loadContractOrder(contract?.instrument_id!!,ContractOrder.ORDER_STATE_FINISH,mOffset,mLimit,
                    object: IResponse<MutableList<ContractOrder>>(){
                        override fun onSuccess(data: MutableList<ContractOrder>) {
                            closeLoadingDialog()
                            if (mOffset == 0) {
                                mList.clear()
                            }
                            if (data != null && data.isNotEmpty()) {
                                if (isCurrentEntrust) {
                                    filterSide(data)
                                } else {
                                    filterType(data)
                                }
                            }

                            adapter?.notifyDataSetChanged()
                            updateAllCancelUi()
                        }

                        override fun onFail(code: String, msg: String) {
                            closeLoadingDialog()
                        }

                    })
        }

    }

    private fun filterSide(data: List<ContractOrder>) {
        if (direction == 0) {
            mList.addAll(data)
            return
        }

        for (i in data.indices) {
            val item = data[i]
            var directionFind = false
            if (direction == 0) {
                directionFind = true
            } else {
                if (item.side == direction) {
                    directionFind = true
                }
            }
            if (directionFind) {
                mList.add(item)
            }
        }
    }

    private fun filterType(data: List<ContractOrder>) {
        if (type == 0) {
            mList.addAll(data)
            return
        }

        for (i in data.indices) {
            val item = data[i]
            var typeFind = false
            val errno: Int = item.errno
            val doneVol: Double = MathHelper.round(item.cum_qty, 8)
            when (type) {
                0 -> typeFind = true
                1 -> {
                    //已完成
                    if (errno == ContractOrder.ORDER_ERRNO_NOERR) {
                        typeFind = true
                    }
                }
                2 -> {
                    //用户取消
                    if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
                        if (doneVol <= 0) {
                            typeFind = true
                        }
                    }
                }
                4 -> {
                    //部分成交
                    if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
                        if (doneVol > 0) {
                            typeFind = true
                        }
                    }
                }
                else -> {//系统取消
                    when (errno) {
                        ContractOrder.ORDER_ERRNO_NOERR -> {
                        }
                        ContractOrder.ORDER_ERRNO_CANCEL -> {
                        }
                        else -> {
                            typeFind = true
                        }
                    }
                }
            }


            if (typeFind) {
                mList.add(item)
            }
        }
    }

    /**
     * 更新是否展示 全部取消按钮
     */
    fun updateAllCancelUi() {
        if (isCurrentEntrust) {
            if (mActivity != null && mActivity is SlContractEntrustActivity) {
                val activity: SlContractEntrustActivity = mActivity as SlContractEntrustActivity
                activity.updateCancelUI(mList.size > 0)
            }
        }
    }


    /**
     * 筛选
     */
    fun doFilter(contractId: Int = 0, direction: Int = 0, type: Int = 0, isCurrentEntrust: Boolean = true) {
        if (contractId == 0) {
            return
        }
        if (contractId != contract?.instrument_id) {
            val info = ContractPublicDataAgent.getContract(contractId) ?: return
            contract = info
        }
        setIsCurrentEntrust(isCurrentEntrust)
        this.direction = direction
        this.type = type
        mOffset = 0
        LogUtil.d("DEBUG", "限价doFilter：$contractId ;isCurrentEntrust:$isCurrentEntrust ;isHide:$isHidden")
        showLoadingDialog()
        loadDataFromNet()
    }

    fun getList(): ArrayList<ContractOrder> {
        return mList
    }

    companion object {
        fun newInstance(isCurrentEntrust: Boolean = true): SlContractPriceEntrustFragment {
            val fg = SlContractPriceEntrustFragment()
            val bundle = Bundle()
            bundle.putBoolean("isCurrentEntrust", isCurrentEntrust)
            fg.arguments = bundle
            return fg
        }
    }
}