package com.bmtc.sdk.contract.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.adapter.HoldContractAdapter
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.common.SlLoadingDialog
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation
import com.bmtc.sdk.contract.utils.NoDoubleClickUtils
import com.bmtc.sdk.contract.view.scrollview.internal.LogUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.impl.ContractPositionListener
import com.contract.sdk.impl.ContractTickerListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.SDKLogUtil
import com.contract.sdk.ws.LogicWebSocketContract
import kotlinx.android.synthetic.main.sl_fragment_recycle_trade.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by zj on 2018/3/1.
 */
@SuppressLint("ValidFragment")
class HoldContractNowFragment : BaseFragment() {
    private var mPositionList = ArrayList<ContractPosition>()
    private var mHoldContractAdapter: HoldContractAdapter? = null
    private var mLastVisibleItem = 0
    private var linearLayoutManager: LinearLayoutManager? = null
    private val mLoadingPage = LogicLoadAnimation()
    private var mLoadingDialog: SlLoadingDialog? = null
    private var mLoading = false
    private var mType = 0 //0normal; 1tradefragment
    private var mContractId = 1
    private var mContract : Contract?=null
    fun setType(type: Int) {
        mType = type
    }

    fun setContractId(contractId: Int) {
        mContractId = contractId
    }

    fun setContractId(contractId: Int, loading: Boolean) {
        mContractId = contractId
        mContract = ContractPublicDataAgent.getContract(mContractId)
        if (loading) {
            mLoadingDialog!!.show()
        }
        updateData(true)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint && !NoDoubleClickUtils.isDoubleClick()) {
            updateData(true)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ContractPublicDataAgent.registerTickerWsListener(this,object:ContractTickerListener(){
            /**
             * 合约Ticker更新
             */
            override fun onWsContractTicker(ticker: ContractTicker) {
                mHoldContractAdapter?.notifyDataSetChanged()
            }
        })

        ContractUserDataAgent.registerContractPositionWsListener(this,object:ContractPositionListener(){
            /**
             * 合约仓位更新
             */
            override fun onWsContractPosition() {
                mLoading = false
                SDKLogUtil.d(ContractSDKAgent.sTAG,"仓位有更新")
                mPositionList.clear()
                mPositionList.addAll(ContractUserDataAgent.getCoinPositions(mContractId))
                mHoldContractAdapter?.notifyDataSetChanged()

                iv_noresult!!.visibility = if (mPositionList!!.size > 0) View.GONE else View.VISIBLE
                tv_noresult!!.visibility = if (mPositionList!!.size > 0) View.GONE else View.VISIBLE
            }

        })
        mLoadingDialog = SlLoadingDialog(activity)
        if (mType == 1) {
            tv_noresult.setText(R.string.sl_str_none)
        }
        linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        rv_list.layoutManager = linearLayoutManager
        rv_list.itemAnimator = DefaultItemAnimator()
//        rv_list.setOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mHoldContractAdapter!!.itemCount) {
//                    if (mLoadingPage.IsLoadingShow()) {
//                        return
//                    }
//                    rv_list.postDelayed(Runnable { updateData(true) }, 100)
//                }
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                mLastVisibleItem = linearLayoutManager!!.findLastVisibleItemPosition()
//            }
//        })
        if (mHoldContractAdapter == null) {
            mHoldContractAdapter = HoldContractAdapter(activity!!)
            mHoldContractAdapter!!.setData(mPositionList)
            rv_list.adapter = mHoldContractAdapter
        } else {
            rv_list.adapter = mHoldContractAdapter
        }
        mLoadingPage.ShowLoadAnimation(activity, rv_list.getParent() as ViewGroup)
        updateData(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.sl_fragment_recycle_trade, null)
    }

    private fun updateData(update: Boolean) {
        if (mLoading) {
            return
        }
        if (!ContractSDKAgent.isLogin) {
            mLoadingDialog!!.dismiss()
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        mContract ?: return
        mLoading = true
        /**
         * 回调传null  则会触发 onWsContractPosition
         */
        ContractUserDataAgent.loadContractPosition(mContract!!.margin_coin,1,0,0,null)
    }

    private fun clearData() {
        if (mPositionList == null) {
            mPositionList = ArrayList()
        }
        mPositionList!!.clear()
        if (mHoldContractAdapter == null) {
            mHoldContractAdapter = HoldContractAdapter(activity!!)
        }
        mHoldContractAdapter!!.setData(mPositionList)
        mHoldContractAdapter!!.notifyDataSetChanged()
    }
}