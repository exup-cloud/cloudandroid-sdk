package com.bmtc.sdk.contract.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.adapter.ContractPlanHistoryAdapter
import com.bmtc.sdk.contract.adapter.ContractPlanOrderAdapter
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.common.SlLoadingDialog
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation
import com.bmtc.sdk.contract.utils.ToastUtil
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.impl.ContractOrderListener
import com.contract.sdk.impl.IResponse
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import java.util.*

/**
 * Created by zj on 2018/3/1.
 */
@SuppressLint("ValidFragment")
class ContractPlanOrderFragment : BaseFragment() {
    private var mRadioRl: RelativeLayout? = null
    private var mTabNormal: RadioButton? = null
    private var mTabPlanHistory: RadioButton? = null
    private var m_RootView: View? = null
    private var mNoresultIv: ImageView? = null
    private var mNoresultTv: TextView? = null
    private var mPlanSrl: SmartRefreshLayout? = null
    private val mPlanList: MutableList<ContractOrder> = ArrayList()
    private var mPlanRv: RecyclerView? = null
    private var mPlanLlm: LinearLayoutManager? = null
    private var mPlanAdapter: ContractPlanOrderAdapter? = null
    private val mLimit = 0
    private var mOffset = 0
    private var mPlanHistorySrl: SmartRefreshLayout? = null
    private var mPlanHistoryList: MutableList<ContractOrder> = ArrayList()
    private var mPlanHistoryLast = 0
    private var mPlanHistoryRv: RecyclerView? = null
    private var mPlanHistorylm: LinearLayoutManager? = null
    private var mPlanHistoryAdapter: ContractPlanHistoryAdapter? = null
    private val mPlanHistoryLimit = 10
    private var mPlanHistoryOffset = 0
    private var mPlanHistoryNomore = false
    private var mType = 0 //0normal; 1tradefragment
    private var mTab = 0 //0normal 1plan
    private val mLoadingPage = LogicLoadAnimation()
    private var mLoadingDialog: SlLoadingDialog? = null
    private val mLoadingNormal = false
    private var mLoadingPlan = false
    private var mContractId = 1
    fun setType(type: Int) {
        mType = type
    }

    fun setTab(tab: Int) {
        mTab = tab
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
        mPlanHistoryNomore = false
        mPlanHistoryOffset = 0
        if (mTab == 0) {
            updateNormal(0)
        } else if (mTab == 1) {
            updatePlan()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            if (mTab == 0) {
                updateNormal(0)
            } else if (mTab == 1) {
                updatePlan()
            }
        }
    }

    private val isForeground: Boolean
        private get() = activity != null && isAdded && !hidden

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        m_RootView = if (mType == 0) {
            inflater.inflate(R.layout.sl_fragment_open_order_plan, null)
        } else {
            inflater.inflate(R.layout.sl_fragment_recycle_trade_plan, null)
        }
        /**
         * 订单监听
         */
        ContractUserDataAgent.registerContractOrderWsListener(this, object : ContractOrderListener() {
            override fun onWsContractOrder() {
                if (mTab == 0) {
                    mPlanList.clear()
                    mPlanList.addAll(ContractUserDataAgent.getContractPlanOrder(mContractId))
                    mPlanAdapter?.setData(mPlanList)
                    mPlanAdapter?.notifyDataSetChanged()

                    mNoresultIv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                }
            }

        })
        mRadioRl = m_RootView?.findViewById(R.id.rl_radio)
        if (mType == 0) {
            mRadioRl?.visibility = View.GONE
        } else {
            mRadioRl?.visibility = View.VISIBLE
        }
        mPlanSrl = m_RootView?.findViewById(R.id.srl_list)
        mPlanHistorySrl = m_RootView?.findViewById(R.id.srl_plan)
        mTabNormal = m_RootView?.findViewById(R.id.tab_normal)
        mTabNormal?.setOnClickListener(View.OnClickListener {
            mTab = 0
            if (ContractSDKAgent.isLogin) {
                mNoresultIv!!.visibility = View.GONE
                mNoresultTv!!.visibility = View.GONE
            }
            mPlanSrl?.visibility = View.VISIBLE
            mPlanHistorySrl?.visibility = View.GONE
            updateNormal(0)
        })
        mTabPlanHistory = m_RootView?.findViewById(R.id.tab_plan)
        mTabPlanHistory?.setOnClickListener(View.OnClickListener {
            mTab = 1
            if (ContractSDKAgent.isLogin) {
                mNoresultIv!!.visibility = View.GONE
                mNoresultTv!!.visibility = View.GONE
            }
            mPlanSrl?.visibility = View.GONE
            mPlanHistorySrl?.visibility = View.VISIBLE
            updatePlan()
        })
        mLoadingDialog = SlLoadingDialog(activity)
        mNoresultIv = m_RootView?.findViewById(R.id.iv_noresult)
        mNoresultTv = m_RootView?.findViewById(R.id.tv_noresult)
        mPlanRv = m_RootView?.findViewById(R.id.rv_list)
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
        mPlanHistoryRv = m_RootView?.findViewById(R.id.rv_plan)
        mPlanHistorylm = LinearLayoutManager(context)
        mPlanHistorylm!!.orientation = LinearLayoutManager.VERTICAL
        mPlanHistoryRv?.layoutManager = mPlanHistorylm
        mPlanHistoryRv?.itemAnimator = DefaultItemAnimator()
        mPlanHistoryRv?.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPlanHistoryLast + 1 == mPlanHistoryAdapter!!.itemCount) {
                    if (mType == 1) {
                        return
                    }
                    if (mLoadingPage.IsLoadingShow()) {
                        return
                    }
                    if (!mPlanHistoryNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(activity, mPlanHistoryRv?.parent as ViewGroup)
                    }
                    mPlanHistoryRv?.postDelayed(Runnable { updatePlan() }, 100)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mPlanHistoryLast = mPlanHistorylm!!.findLastVisibleItemPosition()
            }
        })
        if (mPlanHistoryAdapter == null) {
            mPlanHistoryAdapter = ContractPlanHistoryAdapter(context)
            mPlanHistoryAdapter!!.setData(mPlanHistoryList)
            mPlanHistoryRv?.adapter = mPlanHistoryAdapter
        } else {
            mPlanHistoryRv?.adapter = mPlanHistoryAdapter
        }
        mLoadingPage.ShowLoadAnimation(activity, mPlanRv?.parent as ViewGroup)
        if (mTab == 0) {
            mPlanSrl?.visibility = View.VISIBLE
            mPlanHistorySrl?.visibility = View.GONE
            updateNormal(0)
        } else if (mTab == 1) {
            mPlanSrl?.visibility = View.GONE
            mPlanHistorySrl?.visibility = View.VISIBLE
            updatePlan()
        }
        return m_RootView
    }

    private fun updateNormal(offset: Int) {
        if (!ContractSDKAgent.isLogin || m_RootView == null) {
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
        mLoadingPlan = true
        mTabPlanHistory!!.isEnabled = false

        ContractUserDataAgent.loadContractPlanOrder(mContractId, ContractOrder.ORDER_STATE_APPROVAL or ContractOrder.ORDER_STATE_ENTRUST
                , offset, mLimit, object : IResponse<MutableList<ContractOrder>>() {
            override fun onSuccess(data: MutableList<ContractOrder>) {
                mTabPlanHistory!!.isEnabled = true
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

                    //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
                } else {
                    mPlanList!!.clear()
                    mPlanAdapter!!.setData(mPlanList)
                    mPlanAdapter!!.notifyDataSetChanged()
                    mNoresultIv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mPlanList.size > 0) View.GONE else View.VISIBLE

                    //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
                }
            }

            override fun onFail(code: String, msg: String) {
                mTabPlanHistory!!.isEnabled = true
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mOffset = offset
                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }
            }

        })
    }

    private fun updatePlan() {
        if (!ContractSDKAgent.isLogin) {
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
        val offset = if (mType == 1) 0 else mPlanHistoryOffset
        mLoadingPlan = true
        mTabNormal!!.isEnabled = false

        ContractUserDataAgent.loadContractPlanOrder(mContractId, ContractOrder.ORDER_STATE_FINISH, offset, mPlanHistoryLimit, object : IResponse<MutableList<ContractOrder>>() {
            override fun onSuccess(data: MutableList<ContractOrder>) {
                mTabNormal!!.isEnabled = true
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mPlanHistoryOffset = offset

                if (data != null && data.size > 0) {
                    mNoresultIv!!.visibility = View.GONE
                    mNoresultTv!!.visibility = View.GONE
                    if (mPlanHistoryList == null) {
                        mPlanHistoryList = ArrayList()
                    }
                    if (mPlanHistoryOffset == 0) {
                        mPlanHistoryList!!.clear()
                        mPlanHistoryList!!.addAll(data)
                    } else {
                        mPlanHistoryList!!.addAll(data)
                    }
                    if (mPlanHistoryAdapter == null) {
                        mPlanHistoryAdapter = ContractPlanHistoryAdapter(context)
                    }
                    mPlanHistoryAdapter!!.setData(mPlanHistoryList)
                    mPlanHistoryAdapter!!.notifyDataSetChanged()
                    mPlanHistoryOffset += data.size
                } else {
                    if (mPlanHistoryOffset == 0) {
                        mPlanHistoryList!!.clear()
                        mPlanHistoryAdapter!!.setData(mPlanHistoryList)
                        mPlanHistoryAdapter!!.notifyDataSetChanged()
                    }
                    mNoresultIv!!.visibility = if (mPlanHistoryList!!.size > 0) View.GONE else View.VISIBLE
                    mNoresultTv!!.visibility = if (mPlanHistoryList!!.size > 0) View.GONE else View.VISIBLE
                    if (!mPlanHistoryNomore) {
                        mPlanHistoryNomore = true
                        if (mType == 0 && mPlanHistoryOffset != 0) {
                            ToastUtil.shortToast(context, context?.resources?.getString(R.string.sl_str_no_more_data))
                        }
                    }
                }
            }

            override fun onFail(code: String, msg: String) {
                mTabNormal!!.isEnabled = true
                mLoadingPlan = false
                mLoadingDialog!!.dismiss()
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation()
                }
                mPlanHistoryOffset = offset

                if (mType == 0) {
                    ToastUtil.shortToast(context, msg)
                }
                if (mPlanHistoryOffset == 0) {
//                        mNoresultIv.setVisibility(View.VISIBLE);
//                        mNoresultTv.setVisibility(View.VISIBLE);
//                        clearPlan();
                } else {
                    if (!mPlanHistoryNomore) {
                        mPlanHistoryNomore = true
                        ToastUtil.shortToast(context, context?.resources?.getString(R.string.sl_str_no_more_data))
                    }
                }
            }

        })

    }

    private fun clearNormal() {
        mPlanList!!.clear()
        if (mPlanAdapter == null) {
            mPlanAdapter = ContractPlanOrderAdapter(activity)
        }
        mPlanAdapter!!.setData(mPlanList)
        mPlanAdapter!!.notifyDataSetChanged()
    }

    private fun clearPlan() {
        if (mPlanHistoryList == null) {
            mPlanHistoryList = ArrayList()
        }
        mPlanHistoryList!!.clear()
        if (mPlanHistoryAdapter == null) {
            mPlanHistoryAdapter = ContractPlanHistoryAdapter(context)
        }
        mPlanHistoryAdapter!!.setData(mPlanHistoryList)
        mPlanHistoryAdapter!!.notifyDataSetChanged()
    }


}