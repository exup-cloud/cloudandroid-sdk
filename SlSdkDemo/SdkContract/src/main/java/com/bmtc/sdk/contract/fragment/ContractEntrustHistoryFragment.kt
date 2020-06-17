package com.bmtc.sdk.contract.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.adapter.ContractEntrustHistoryAdapter
import com.bmtc.sdk.contract.adapter.ContractPlanHistoryAdapter
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.common.SlLoadingDialog
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation
import com.bmtc.sdk.contract.utils.ToastUtil
import com.contract.sdk.ContractSDKAgent.isLogin
import com.contract.sdk.ContractUserDataAgent.loadContractOrder
import com.contract.sdk.ContractUserDataAgent.loadContractPlanOrder
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.impl.IResponse
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import java.util.*

/**
 * Created by zj on 2018/3/1.
 */
@SuppressLint("ValidFragment")
class ContractEntrustHistoryFragment : BaseFragment() {
    private var mTabNormal: RadioButton? = null
    private var mTabPlan: RadioButton? = null
    private var m_RootView: View? = null
    private var mNoresultIv: ImageView? = null
    private var mNoresultTv: TextView? = null
    private var mNormalSrl: SmartRefreshLayout? = null
    private var mOrderList= ArrayList<ContractOrder>()
    private var mNormalLast = 0
    private var mNormalRv: RecyclerView? = null
    private var mNormalLlm: LinearLayoutManager? = null
    private var mNormalAdapter: ContractEntrustHistoryAdapter? = null
    private val mNormalLimit = 10
    private var mNormalOffset = 0
    private var mNormalNomore = false
    private var mPlanSrl: SmartRefreshLayout? = null
    private var mPlanList = ArrayList<ContractOrder>()
    private var mPlanLast = 0
    private var mPlanRv: RecyclerView? = null
    private var mPlanLlm: LinearLayoutManager? = null
    private var mPlanAdapter: ContractPlanHistoryAdapter? = null
    private val mPlanLimit = 10
    private var mPlanOffset = 0
    private var mPlanNomore = false
    private var mType = 0 //0normal; 1tradefragment
    private var mTab = 0 //0normal 1plan
    private val mLoadingPage = LogicLoadAnimation()
    private var mLoadingDialog: SlLoadingDialog? = null
    private var mLoadingNormal = false
    private var mLoadingPlan = false
    private var mContractId = 1
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
        if (loading) {
            mLoadingDialog!!.show()
        }
        mNormalNomore = false
        mNormalOffset = 0
        mPlanNomore = false
        mPlanOffset = 0
        if (mTab == 0) {
            updateNormal()
        } else if (mTab == 1) {
            updatePlan()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            if (mTab == 0) {
                updateNormal()
            } else if (mTab == 1) {
                updatePlan()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        m_RootView = if (mType == 0) {
            inflater.inflate(R.layout.sl_fragment_open_order_plan, null)
        } else {
            inflater.inflate(R.layout.sl_fragment_recycle_trade_plan, null)
        }
        mNormalSrl = m_RootView?.findViewById(R.id.srl_list)
        mPlanSrl = m_RootView?.findViewById(R.id.srl_plan)
        mTabNormal = m_RootView?.findViewById(R.id.tab_normal)
        mTabNormal?.setOnClickListener(View.OnClickListener {
            mTab = 0
            mNoresultIv!!.visibility = View.GONE
            mNoresultTv!!.visibility = View.GONE
            mNormalSrl?.visibility = View.VISIBLE
            mPlanSrl?.visibility = View.GONE
            updateNormal()
        })
        mTabPlan = m_RootView?.findViewById(R.id.tab_plan)
        mTabPlan?.setOnClickListener(View.OnClickListener {
            mTab = 1
            mNoresultIv!!.visibility = View.GONE
            mNoresultTv!!.visibility = View.GONE
            mNormalSrl?.visibility = View.GONE
            mPlanSrl?.visibility = View.VISIBLE
            updatePlan()
        })
        mLoadingDialog = SlLoadingDialog(activity)
        mNoresultIv = m_RootView?.findViewById(R.id.iv_noresult)
        mNoresultTv = m_RootView?.findViewById(R.id.tv_noresult)
        mNormalRv = m_RootView?.findViewById(R.id.rv_list)
        mNormalLlm = LinearLayoutManager(context)
        mNormalLlm!!.orientation = LinearLayoutManager.VERTICAL
        mNormalRv?.layoutManager = mNormalLlm
        mNormalRv?.itemAnimator = DefaultItemAnimator()
        mNormalRv?.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mNormalLast + 1 == mNormalAdapter!!.itemCount) {
                    if (mType == 1) {
                        return
                    }
                    if (mLoadingPage.IsLoadingShow()) {
                        return
                    }
                    if (!mNormalNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(activity, mNormalRv?.parent as ViewGroup)
                    }
                    mNormalRv?.postDelayed(Runnable { updateNormal() }, 100)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mNormalLast = mNormalLlm!!.findLastVisibleItemPosition()
            }
        })
        if (mNormalAdapter == null) {
            mNormalAdapter = ContractEntrustHistoryAdapter(context)
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
        mPlanRv?.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPlanLast + 1 == mNormalAdapter!!.itemCount) {
                    if (mType == 1) {
                        return
                    }
                    if (mLoadingPage.IsLoadingShow()) {
                        return
                    }
                    if (!mPlanNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(activity, mPlanRv?.parent as ViewGroup)
                    }
                    mPlanRv?.postDelayed(Runnable { updatePlan() }, 100)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mPlanLast = mPlanLlm!!.findLastVisibleItemPosition()
            }
        })
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanHistoryAdapter(context)
            mPlanAdapter!!.setData(mPlanList)
            mPlanRv?.adapter = mPlanAdapter
        } else {
            mPlanRv?.adapter = mPlanAdapter
        }
        mLoadingPage.ShowLoadAnimation(activity, mNormalRv?.parent as ViewGroup)
        updateNormal()
        return m_RootView
    }

    private fun updateNormal() {
        if (!isLogin) {
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
        val offset = if (mType == 1) 0 else mNormalOffset
        mLoadingNormal = true
        loadContractOrder(mContractId, ContractOrder.ORDER_STATE_FINISH, offset, mNormalLimit, object : IResponse<MutableList<ContractOrder>>() {
            override fun onSuccess(data: MutableList<ContractOrder>) {
                mLoadingNormal = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mNormalOffset = offset
                if (data != null && data.size > 0) {
                    mNoresultIv!!.visibility = View.GONE
                    mNoresultTv!!.visibility = View.GONE
                    if (mOrderList == null) {
                        mOrderList = ArrayList()
                    }
                    if (mNormalOffset == 0) {
                        mOrderList!!.clear()
                        mOrderList!!.addAll(data)
                    } else {
                        mOrderList!!.addAll(data)
                    }
                    if (mNormalAdapter == null) {
                        mNormalAdapter = ContractEntrustHistoryAdapter(context)
                    }
                    mNormalAdapter!!.setData(mOrderList)
                    mNormalAdapter!!.notifyDataSetChanged()
                    mNormalOffset += data.size
                } else {
                    if (mNormalOffset == 0) {
                        mOrderList!!.clear()
                        mNormalAdapter!!.setData(mOrderList)
                        mNormalAdapter!!.notifyDataSetChanged()
                    }
                    mNoresultIv!!.visibility = if (mOrderList!!.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mOrderList!!.size > 0) View.GONE else View.VISIBLE
                    if (!mNormalNomore) {
                        mNormalNomore = true
                        if (mType == 0 && mNormalOffset != 0) {
                            ToastUtil.shortToast(context, context!!.resources.getString(R.string.sl_str_no_more_data))
                        }
                    }
                }
            }

            override fun onFail(code: String, msg: String) {
                mLoadingNormal = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mNormalOffset = offset
                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }
                if (mNormalOffset == 0) {
                    mNoresultIv!!.visibility = View.VISIBLE
                    mNoresultTv!!.visibility = View.VISIBLE
                    clearNormal()
                } else {
                    if (!mNormalNomore) {
                        mNormalNomore = true
                        ToastUtil.shortToast(context, context!!.resources.getString(R.string.sl_str_no_more_data))
                    }
                }
            }


        })
    }

    private fun updatePlan() {
        if (!isLogin) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        if (mLoadingPlan) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation()
            }
            return
        }
        val offset = if (mType == 1) 0 else mPlanOffset
        mLoadingPlan = true
        loadContractPlanOrder(mContractId, ContractOrder.ORDER_STATE_FINISH, offset, mPlanLimit, object : IResponse<MutableList<ContractOrder>>() {
            override fun onSuccess(data: MutableList<ContractOrder>) {
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mPlanOffset = offset
                if (data != null && data.size > 0) {
                    mNoresultIv!!.visibility = View.GONE
                    mNoresultTv!!.visibility = View.GONE
                    if (mPlanList == null) {
                        mPlanList = ArrayList()
                    }
                    if (mPlanOffset == 0) {
                        mPlanList!!.clear()
                        mPlanList!!.addAll(data)
                    } else {
                        mPlanList!!.addAll(data)
                    }
                    if (mPlanAdapter == null) {
                        mPlanAdapter = ContractPlanHistoryAdapter(context)
                    }
                    mPlanAdapter!!.setData(mPlanList)
                    mPlanAdapter!!.notifyDataSetChanged()
                    mPlanOffset += data.size
                } else {
                    if (mPlanOffset == 0) {
                        mPlanList!!.clear()
                        mPlanAdapter!!.setData(mPlanList)
                        mPlanAdapter!!.notifyDataSetChanged()
                    }
                    mNoresultIv!!.visibility = if (mPlanList!!.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mPlanList!!.size > 0) View.GONE else View.VISIBLE
                    if (!mPlanNomore) {
                        mPlanNomore = true
                        if (mType == 0 && mPlanOffset != 0) {
                            ToastUtil.shortToast(context, context!!.resources.getString(R.string.sl_str_no_more_data))
                        }
                    }
                }
            }

            override fun onFail(code: String, msg: String) {
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mPlanOffset = offset
                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }
                if (mPlanOffset == 0) {
                    mNoresultIv!!.visibility = View.VISIBLE
                    mNoresultTv!!.visibility = View.VISIBLE
                    clearPlan()
                } else {
                    if (!mPlanNomore) {
                        mPlanNomore = true
                        ToastUtil.shortToast(context, context!!.resources.getString(R.string.sl_str_no_more_data))
                    }
                }
            }
        })
    }

    private fun clearNormal() {
        if (mOrderList == null) {
            mOrderList = ArrayList()
        }
        mOrderList!!.clear()
        if (mNormalAdapter == null) {
            mNormalAdapter = ContractEntrustHistoryAdapter(context)
        }
        mNormalAdapter!!.setData(mOrderList)
        mNormalAdapter!!.notifyDataSetChanged()
    }

    private fun clearPlan() {
        if (mPlanList == null) {
            mPlanList = ArrayList()
        }
        mPlanList!!.clear()
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanHistoryAdapter(context)
        }
        mPlanAdapter!!.setData(mPlanList)
        mPlanAdapter!!.notifyDataSetChanged()
    }

    private fun updateSingleOrder(order: ContractOrder) {
        if (mOrderList == null) {
            return
        }
        if (order.instrument_id != mContractId) {
            return
        }
        var exist = false
        for (i in mOrderList!!.indices) {
            val item = mOrderList!![i] ?: continue
            if (item.oid == order.oid) {
                if (order.status == ContractOrder.ORDER_STATE_FINISH) {
                    mOrderList!![i] = order
                } else {
                    mOrderList!!.removeAt(i)
                }
                exist = true
                break
            }
        }
        if (!exist && order.status == ContractOrder.ORDER_STATE_FINISH) {
            mOrderList!!.add(0, order)
        }
        if (mNormalAdapter == null) {
            mNormalAdapter = ContractEntrustHistoryAdapter(activity)
            mNormalRv!!.adapter = mNormalAdapter
            mNormalAdapter!!.setData(mOrderList)
        }
        mNormalAdapter!!.setData(mOrderList)
        mNormalAdapter!!.notifyDataSetChanged()
        if (mTab == 0) {
            mNoresultIv!!.visibility = if (mOrderList!!.size > 0) View.GONE else View.VISIBLE
            mNoresultTv!!.visibility = if (mOrderList!!.size > 0) View.GONE else View.VISIBLE
        }
        // Plan
        if (mPlanList == null) {
            return
        }
        exist = false
        for (i in mPlanList!!.indices) {
            val item = mPlanList!![i] ?: continue
            if (item.oid == order.oid) {
                if (order.status == ContractOrder.ORDER_STATE_FINISH) {
                    mPlanList!![i] = order
                } else {
                    mPlanList!!.removeAt(i)
                }
                exist = true
                break
            }
        }
        if (!exist && order.status == ContractOrder.ORDER_STATE_FINISH) {
            mPlanList!!.add(0, order)
        }
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanHistoryAdapter(activity)
            mPlanRv!!.adapter = mPlanAdapter
            mPlanAdapter!!.setData(mPlanList)
        }
        mPlanAdapter!!.setData(mPlanList)
        mPlanAdapter!!.notifyDataSetChanged()
        if (mTab == 1) {
            mNoresultIv!!.visibility = if (mPlanList!!.size > 0) View.GONE else View.VISIBLE
            mNoresultTv!!.visibility = if (mPlanList!!.size > 0) View.GONE else View.VISIBLE
        }
    }
}