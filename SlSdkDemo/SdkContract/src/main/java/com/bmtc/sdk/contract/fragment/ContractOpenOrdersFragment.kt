package com.bmtc.sdk.contract.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.adapter.ContractOpenOrderAdapter
import com.bmtc.sdk.contract.adapter.ContractPlanOrderAdapter
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.common.SlLoadingDialog
import com.bmtc.sdk.contract.dialog.PromptWindow
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation
import com.bmtc.sdk.contract.utils.ToastUtil
import com.bmtc.sdk.contract.utils.UtilSystem
import com.bmtc.sdk.contract.view.scrollview.internal.LogUtils
import com.contract.sdk.ContractPublicDataAgent.getContract
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractSDKAgent.isLogin
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.ContractUserDataAgent.registerContractOrderWsListener
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.data.ContractOrders
import com.contract.sdk.impl.ContractOrderListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.SDKLogUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import java.util.*

/**
 * Created by zj on 2018/3/1.
 */
class ContractOpenOrdersFragment : BaseFragment() {
    private var m_RootView: View? = null
    private var mTabNormal: RadioButton? = null
    private var mTabPlan: RadioButton? = null
    private var mNoresultIv: ImageView? = null
    private var mNoresultTv: TextView? = null
    private var mCancelAllBtn: Button? = null
    private var mCancelAllLl: LinearLayout? = null
    private var mNormalSrl: SmartRefreshLayout? = null
    private val mOrderList: MutableList<ContractOrder> = ArrayList()
    private var mNormalRv: RecyclerView? = null
    private var mNormalLlm: LinearLayoutManager? = null
    private var mNormalAdapter: ContractOpenOrderAdapter? = null
    private var mPlanSrl: SmartRefreshLayout? = null
    private val mPlanList: MutableList<ContractOrder> = ArrayList()
    private var mPlanRv: RecyclerView? = null
    private var mPlanLlm: LinearLayoutManager? = null
    private var mPlanAdapter: ContractPlanOrderAdapter? = null
    private val mLimit = 10
    private var mOffset = 0
    private var mType = 0 //0normal; 1tradefragment
    private var mContractId = 0
    private var mTab = 0 //0normal 1plan
    private val mLoadingPage = LogicLoadAnimation()
    private var mLoadingDialog: SlLoadingDialog? = null
    private var mLoadingNormal = false
    private var mLoadingPlan = false
    fun setType(type: Int) {
        mType = type
    }

    fun setContractId(contractId: Int) {
        mContractId = contractId
    }

    fun setContractId(contractId: Int, loading: Boolean) {
        mContractId = contractId
        if (m_RootView == null) {
            return
        }
        if (contractId == 0) {
            mCancelAllBtn!!.setText(R.string.sl_str_cancel_all_orders)
        } else {
            val contract = getContract(mContractId)
            if (contract != null) {
                val text = String.format(getString(R.string.sl_str_cancel_all_orders_single), contract.symbol)
                mCancelAllBtn!!.text = text
            } else {
                mCancelAllBtn!!.setText(R.string.sl_str_cancel_all_orders)
            }
        }
        if (loading) {
            mLoadingDialog!!.show()
        }
        if (mTab == 0) {
            updateNormal(0)
        } else if (mTab == 1) {
            updatePlan(0)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            if (mTab == 0) {
                updateNormal(0)
            } else if (mTab == 1) {
                updatePlan(0)
            }
        }
    }

    private val isForeground: Boolean
        private get() = activity != null && isAdded && !hidden

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        m_RootView = if (mType == 0) {
            inflater.inflate(R.layout.sl_fragment_open_order_plan, null)
        } else {
            inflater.inflate(R.layout.sl_fragment_open_order_trade_plan, null)
        }
        //LogicUserState.getInstance().registListener(this);
        /**
         * 订单监听
         */
        registerContractOrderWsListener(this, object : ContractOrderListener() {
            override fun onWsContractOrder() {
                SDKLogUtil.d(ContractSDKAgent.sTAG,"onWsContractOrder订单有更新")
                    if (mTab == 0) {
                        mOrderList?.clear()
                        mOrderList?.addAll(ContractUserDataAgent.getContractOrder(mContractId))
                        mNormalAdapter!!.setData(mOrderList)
                        mNormalAdapter!!.notifyDataSetChanged()

                        mNoresultIv!!.visibility = if (mOrderList.size > 0) View.GONE else View.VISIBLE
                        mNoresultTv!!.visibility = if (mOrderList.size > 0) View.GONE else View.VISIBLE
                        if (mType == 1) {
                            mNormalRv!!.setPadding(0, 0, 0,
                                    if (mOrderList.size >= 2) UtilSystem.dip2px(activity, 50f) else 0)
                        }
                        mCancelAllLl!!.visibility = if (mOrderList.size >= 2) View.VISIBLE else View.GONE
                    }else{
                        mPlanList.clear()
                        mPlanList.addAll(ContractUserDataAgent.getContractPlanOrder(mContractId))
                        mPlanAdapter?.setData(mPlanList)
                        mPlanAdapter?.notifyDataSetChanged()

                        mNoresultIv?.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                        mNoresultTv?.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                        mCancelAllLl?.visibility = if (mPlanList.size >= 2) View.VISIBLE else View.GONE
                    }
            }
        })
        mNormalSrl = m_RootView?.findViewById(R.id.srl_list)
        mPlanSrl = m_RootView?.findViewById(R.id.srl_plan)
        mTabNormal = m_RootView?.findViewById(R.id.tab_normal)
        mTabNormal?.setOnClickListener(View.OnClickListener {
            mTab = 0
            mNoresultIv!!.visibility = View.GONE
            mNoresultTv!!.visibility = View.GONE
            mNormalSrl?.visibility = View.VISIBLE
            mPlanSrl?.visibility = View.GONE
            updateNormal(mOffset)
        })
        mTabPlan = m_RootView?.findViewById(R.id.tab_plan)
        mTabPlan?.setOnClickListener(View.OnClickListener {
            mTab = 1
            mNoresultIv!!.visibility = View.GONE
            mNoresultTv!!.visibility = View.GONE
            mNormalSrl?.visibility = View.GONE
            mPlanSrl?.visibility = View.VISIBLE
            updatePlan(mOffset)
        })
        mLoadingDialog = SlLoadingDialog(activity)
        mCancelAllLl = m_RootView?.findViewById(R.id.ll_cancel_all)
        mCancelAllBtn = m_RootView?.findViewById(R.id.btn_cancel_all)
        mCancelAllBtn?.setOnClickListener(View.OnClickListener { cancelAll() })
        mNoresultIv = m_RootView?.findViewById(R.id.iv_noresult)
        mNoresultTv = m_RootView?.findViewById(R.id.tv_noresult)
        mNormalRv = m_RootView?.findViewById(R.id.rv_list)
        mNormalLlm = LinearLayoutManager(context)
        mNormalLlm!!.orientation = LinearLayoutManager.VERTICAL
        mNormalRv?.layoutManager = mNormalLlm
        mNormalRv?.itemAnimator = DefaultItemAnimator()
        if (mNormalAdapter == null) {
            mNormalAdapter = ContractOpenOrderAdapter(activity)
            mNormalAdapter!!.setData(mOrderList)
            mNormalRv?.adapter = mNormalAdapter
        } else {
            mNormalRv?.adapter = mNormalAdapter
        }
        mPlanRv = m_RootView?.findViewById(R.id.rv_plan)
        mPlanLlm = LinearLayoutManager(context)
        mPlanLlm!!.orientation = LinearLayoutManager.VERTICAL
        mPlanRv?.layoutManager = mPlanLlm
        mPlanRv?.itemAnimator = DefaultItemAnimator()
        (mPlanRv?.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanOrderAdapter(activity)
            mPlanAdapter!!.setData(mPlanList)
            mPlanRv?.adapter = mPlanAdapter
        } else {
            mPlanRv?.adapter = mPlanAdapter
        }
        mLoadingPage.ShowLoadAnimation(activity, mNormalRv?.parent as ViewGroup)
        updateNormal(mOffset)
        return m_RootView
    }

    private fun updateNormal(offset: Int) {
        if (!isLogin || m_RootView == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        if (mLoadingNormal) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        mLoadingNormal = true
        ContractUserDataAgent.loadContractOrder(mContractId,ContractOrder.ORDER_STATE_APPROVAL or ContractOrder.ORDER_STATE_ENTRUST, offset, mLimit, object : IResponse<MutableList<ContractOrder>>() {

            override fun onSuccess(data: MutableList<ContractOrder>) {
                mLoadingNormal = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mOffset = offset

                if (data != null && data.size > 0) {
                    mNoresultIv!!.visibility = View.GONE
                    mNoresultTv!!.visibility = View.GONE
                    if (offset == 0) {
                        mOrderList!!.clear()
                        mOrderList.addAll(data)
                    } else {
                        mOrderList!!.addAll(data)
                    }
                    if (mNormalAdapter == null) {
                        mNormalAdapter = ContractOpenOrderAdapter(activity)
                    }
                    mNormalAdapter!!.setData(mOrderList)
                    mNormalAdapter!!.notifyDataSetChanged()
                    //mOffset += data.size();
                    if (mCancelAllLl != null) {
                        if (mType == 1) {
                            mNormalRv!!.setPadding(0, 0, 0,
                                    if (mOrderList.size >= 2) UtilSystem.dip2px(activity, 50f) else 0)
                        }
                        mCancelAllLl!!.visibility = if (mOrderList.size >= 2) View.VISIBLE else View.GONE
                    }
                } else {
                    mOrderList!!.clear()
                    mNormalAdapter!!.setData(mOrderList)
                    mNormalAdapter!!.notifyDataSetChanged()
                    mNoresultIv!!.visibility = if (mOrderList.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mOrderList.size > 0) View.GONE else View.VISIBLE
                    if (mCancelAllLl != null) {
                        if (mType == 1) {
                            mNormalRv!!.setPadding(0, 0, 0,
                                    if (mOrderList.size >= 2) UtilSystem.dip2px(activity, 50f) else 0)
                        }
                        mCancelAllLl!!.visibility = if (mOrderList.size >= 2) View.VISIBLE else View.GONE
                    }
                }
            }

            override fun onFail(code: String, msg: String) {
                mLoadingNormal = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mOffset = offset
                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }

                if (offset == 0) {
                    mNoresultIv!!.visibility = View.VISIBLE
                    mNoresultTv!!.visibility = View.VISIBLE
                    clearNormal()
                }
                if (mCancelAllLl != null) {
                    if (mType == 1) {
                        mNormalRv!!.setPadding(0, 0, 0,
                                if (mOrderList!!.size >= 2) UtilSystem.dip2px(activity, 50f) else 0)
                    }
                    mCancelAllLl!!.visibility = if (mOrderList!!.size >= 2) View.VISIBLE else View.GONE
                }
            }
        })
    }

    private fun updatePlan(offset: Int) {
        if (!ContractSDKAgent.isLogin  || m_RootView == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        val orders: List<ContractOrder> = ContractUserDataAgent.getContractOrder(mContractId)
        if (orders != null) {
            mPlanList!!.clear()
            mPlanList.addAll(orders)
            if (mPlanAdapter == null) {
                mPlanAdapter = ContractPlanOrderAdapter(activity)
            }
            mPlanAdapter!!.setData(mPlanList)
            mPlanAdapter!!.notifyDataSetChanged()
            mCancelAllLl!!.visibility = if (mPlanList.size >= 2) View.VISIBLE else View.GONE
        }
        if (mLoadingPlan) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        mLoadingPlan = true

        ContractUserDataAgent.loadContractPlanOrder(mContractId,ContractOrder.ORDER_STATE_APPROVAL or ContractOrder.ORDER_STATE_ENTRUST,offset,mLimit,
        object : IResponse<MutableList<ContractOrder>>(){
            override fun onSuccess(data:MutableList<ContractOrder>) {
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mOffset = offset

                if (data != null && data.size > 0) {
                    mNoresultIv!!.visibility = View.GONE
                    mNoresultTv!!.visibility = View.GONE
                    if (offset == 0) {
                        mPlanList!!.clear()
                        mPlanList.addAll(data)
                    } else {
                        mPlanList!!.addAll(data)
                    }
                    if (mPlanAdapter == null) {
                        mPlanAdapter = ContractPlanOrderAdapter(activity)
                    }
                    mPlanAdapter!!.setData(mPlanList)
                    mPlanAdapter!!.notifyDataSetChanged()
                    //mOffset += data.size();
                    mCancelAllLl!!.visibility = if (mPlanList.size >= 2) View.VISIBLE else View.GONE
                } else {
                    mPlanList!!.clear()
                    mPlanAdapter!!.setData(mPlanList)
                    mPlanAdapter!!.notifyDataSetChanged()
                    mNoresultIv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                    mCancelAllLl!!.visibility = if (mPlanList.size >= 2) View.VISIBLE else View.GONE
                }
            }

            override fun onFail(code: String, msg: String) {
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mOffset = offset

                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }

                if (offset == 0) {
                    mNoresultIv!!.visibility = View.VISIBLE
                    mNoresultTv!!.visibility = View.VISIBLE
                    clearPlan()
                }
                mCancelAllLl!!.visibility = if (mPlanList!!.size >= 2) View.VISIBLE else View.GONE
            }

        })

    }

    private fun cancelAll() {
        val window = PromptWindow(activity)
        window.showTitle(activity!!.getString(R.string.sl_str_tips))
        window.showTvContent("确认取消全部订单?")
        window.showBtnOk(activity!!.getString(R.string.sl_str_confirm))
        window.showBtnCancel(activity!!.getString(R.string.sl_str_cancel))
        window.showAtLocation(mCancelAllBtn, Gravity.CENTER, 0, 0)
        window.btnOk.setOnClickListener(View.OnClickListener {
            window.dismiss()
            if (mTab == 0) {
                doCancelAll()
            } else if (mTab == 1) {
                doCancelAllPlan()
            }
        })
        window.btnCancel.setOnClickListener(View.OnClickListener { window.dismiss() })
    }

    private fun doCancelAll() {
        val orderList: MutableList<ContractOrder> = ArrayList()
        orderList.addAll(mOrderList!!)
        val orders = ContractOrders()
        orders.contract_id = mContractId
        for (i in orderList.indices) {
            val item = orderList[i] ?: continue
            orders.orders.add(item)
        }

        ContractUserDataAgent.doCancelOrders(orders,object:IResponse<MutableList<Long>>(){
            override fun onSuccess(data: MutableList<Long>) {
                if (data != null && data.size > 0) {
                    ToastUtil.shortToast(context, getString(R.string.sl_str_some_orders_cancel_failed))
                }
            }

            override fun onFail(code: String, msg: String) {
                ToastUtil.shortToast(context, msg)
            }

        })
    }

    private fun doCancelAllPlan() {
        val orderList: MutableList<ContractOrder> = ArrayList()
        orderList.addAll(mPlanList!!)
        val orders = ContractOrders()
        orders.contract_id = mContractId
        for (i in orderList.indices) {
            val item = orderList[i] ?: continue
            orders.orders.add(item)
        }

        ContractUserDataAgent.doCancelPlanOrders(orders,object:IResponse<MutableList<Long>>(){
            override fun onSuccess(data: MutableList<Long>) {
                if (data != null && data.size > 0) {
                    ToastUtil.shortToast(context, getString(R.string.sl_str_some_orders_cancel_failed))
                }
            }

            override fun onFail(code: String, msg: String) {
                ToastUtil.shortToast(context, msg)
            }

        })
    }

    private fun clearNormal() {
        mOrderList!!.clear()
        if (mNormalAdapter == null) {
            mNormalAdapter = ContractOpenOrderAdapter(activity)
        }
        mNormalAdapter!!.setData(mOrderList)
        mNormalAdapter!!.notifyDataSetChanged()
    }

    private fun clearPlan() {
        mPlanList!!.clear()
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanOrderAdapter(activity)
        }
        mPlanAdapter!!.setData(mPlanList)
        mPlanAdapter!!.notifyDataSetChanged()
    }



}