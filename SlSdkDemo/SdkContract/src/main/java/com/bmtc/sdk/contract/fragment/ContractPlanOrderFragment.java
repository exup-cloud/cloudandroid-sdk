package com.bmtc.sdk.contract.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractPlanHistoryAdapter;
import com.bmtc.sdk.contract.adapter.ContractPlanOrderAdapter;
import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.uilogic.LogicContractOrder;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicLoadAnimation;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

@SuppressLint("ValidFragment")
public class ContractPlanOrderFragment extends BaseFragment implements
       // LogicUserState.IUserStateListener,
        LogicContractOrder.IContractOrderListener,
        LogicWebSocketContract.IWebSocketListener{

    private RelativeLayout mRadioRl;
    private RadioButton mTabNormal, mTabPlanHistory;

    private View m_RootView;
    private ImageView mNoresultIv;
    private TextView mNoresultTv;

    private SmartRefreshLayout mPlanSrl;
    private List<ContractOrder> mPlanList = new ArrayList<>();
    private RecyclerView mPlanRv;
    private LinearLayoutManager mPlanLlm;
    private ContractPlanOrderAdapter mPlanAdapter;

    private int mLimit = 0;
    private int mOffset = 0;

    private SmartRefreshLayout mPlanHistorySrl;
    private List<ContractOrder> mPlanHistoryList = new ArrayList<>();
    private int mPlanHistoryLast;
    private RecyclerView mPlanHistoryRv;
    private LinearLayoutManager mPlanHistorylm;
    private ContractPlanHistoryAdapter mPlanHistoryAdapter;

    private int mPlanHistoryLimit = 10;
    private int mPlanHistoryOffset = 0;
    private boolean mPlanHistoryNomore = false;

    private int mType = 0;  //0normal; 1tradefragment
    private int mTab = 0;   //0normal 1plan
    
    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();
    private SlLoadingDialog mLoadingDialog;
    private boolean mLoadingNormal = false;
    private boolean mLoadingPlan = false;

    private int mContractId = 1;

    public void setType(int type) {
        mType = type;
    }

    public void setTab(int tab) {
        mTab = tab;
    }

    public void setContractId(int contractId) {
        mContractId = contractId;
    }

    public void setContractId(int contractId, boolean loading) {

        mContractId = contractId;

        if (m_RootView == null) {
            return;
        }

        if (loading) {
            mLoadingDialog.show();
        }

        mPlanHistoryNomore = false;
        mPlanHistoryOffset = 0;

        if (mTab == 0) {
            updateNormal(0);
        } else if (mTab == 1) {
            updatePlan();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mTab == 0) {
                updateNormal(0);
            } else if (mTab == 1) {
                updatePlan();
            }
        }
    }

    @Override
    public void onTimer(int times) {
        if (mType == 1 && isForeground()) {
            if (!LogicWebSocketContract.getInstance().isConnected()) {
                if (mTab == 0) {
                    updateNormal(0);
                } else if (mTab == 1) {
                    updatePlan();
                }
            }
        }
    }

    private boolean isForeground() {
        return  getActivity() != null&& isAdded()  && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mType == 0) {
            m_RootView = inflater.inflate(R.layout.sl_fragment_open_order_plan, null);
        } else {
            m_RootView = inflater.inflate(R.layout.sl_fragment_recycle_trade_plan, null);
        }

       // LogicUserState.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);
        LogicContractOrder.getInstance().registListener(this);

        mRadioRl = m_RootView.findViewById(R.id.rl_radio);
        if (mType == 0) {
            mRadioRl.setVisibility(View.GONE);
        } else {
            mRadioRl.setVisibility(View.VISIBLE);
        }

        mPlanSrl = m_RootView.findViewById(R.id.srl_list);
        mPlanHistorySrl = m_RootView.findViewById(R.id.srl_plan);

        mTabNormal = m_RootView.findViewById(R.id.tab_normal);
        mTabNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTab = 0;

                if (SLSDKAgent.slUser!=null) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);
                }
                mPlanSrl.setVisibility(View.VISIBLE);
                mPlanHistorySrl.setVisibility(View.GONE);
                updateNormal(0);
            }
        });

        mTabPlanHistory = m_RootView.findViewById(R.id.tab_plan);
        mTabPlanHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTab = 1;

                if (SLSDKAgent.slUser!=null) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);
                }
                mPlanSrl.setVisibility(View.GONE);
                mPlanHistorySrl.setVisibility(View.VISIBLE);
                updatePlan();
            }
        });

        mLoadingDialog = new SlLoadingDialog(getActivity());
        mNoresultIv = m_RootView.findViewById(R.id.iv_noresult);
        mNoresultTv = m_RootView.findViewById(R.id.tv_noresult);

        mPlanRv = m_RootView.findViewById(R.id.rv_list);
        mPlanLlm = new LinearLayoutManager(LogicGlobal.sContext);
        mPlanLlm.setOrientation(LinearLayoutManager.VERTICAL);
        mPlanRv.setLayoutManager(mPlanLlm);
        mPlanRv.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator)mPlanRv.getItemAnimator()).setSupportsChangeAnimations(false);

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
            mPlanAdapter.setData(mPlanList);
            mPlanRv.setAdapter(mPlanAdapter);
        } else {
            mPlanRv.setAdapter(mPlanAdapter);
        }

        mPlanHistoryRv = m_RootView.findViewById(R.id.rv_plan);
        mPlanHistorylm = new LinearLayoutManager(LogicGlobal.sContext);
        mPlanHistorylm.setOrientation(LinearLayoutManager.VERTICAL);
        mPlanHistoryRv.setLayoutManager(mPlanHistorylm);
        mPlanHistoryRv.setItemAnimator(new DefaultItemAnimator());
        mPlanHistoryRv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPlanHistoryLast + 1 == mPlanHistoryAdapter.getItemCount()) {
                    if (mType == 1) {
                        return;
                    }

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    if (!mPlanHistoryNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mPlanHistoryRv.getParent());
                    }

                    mPlanHistoryRv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updatePlan();
                        }
                    }, 100);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPlanHistoryLast = mPlanHistorylm.findLastVisibleItemPosition();
            }
        });

        if (mPlanHistoryAdapter == null) {
            mPlanHistoryAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
            mPlanHistoryAdapter.setData(mPlanHistoryList);
            mPlanHistoryRv.setAdapter(mPlanHistoryAdapter);
        } else {
            mPlanHistoryRv.setAdapter(mPlanHistoryAdapter);
        }
        
        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mPlanRv.getParent());
        if (mTab == 0) {
            mPlanSrl.setVisibility(View.VISIBLE);
            mPlanHistorySrl.setVisibility(View.GONE);
            updateNormal(0);
        } else if (mTab == 1) {
            mPlanSrl.setVisibility(View.GONE);
            mPlanHistorySrl.setVisibility(View.VISIBLE);
            updatePlan();
        }

        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // LogicUserState.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
        LogicContractOrder.getInstance().unregistListener(this);
    }

    private void updateNormal(final int offset) {
        if (SLSDKAgent.slUser==null || m_RootView == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        List<ContractOrder> orders = BTContract.getInstance().getContractOrder(mContractId);
        if (orders != null) {
            mPlanList.clear();
            mPlanList.addAll(orders);

            if (mPlanAdapter == null) {
                mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
            }

            mPlanAdapter.setData(mPlanList);
            mPlanAdapter.notifyDataSetChanged();

            //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
        }

        if (mLoadingPlan) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        mLoadingPlan = true;
        mTabPlanHistory.setEnabled(false);
        BTContract.getInstance().userPlanOrders(mContractId, offset, mLimit, ContractOrder.ORDER_STATE_APPROVAL|ContractOrder.ORDER_STATE_ENTRUST, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {

                mTabPlanHistory.setEnabled(true);
                mLoadingPlan = false;
                mLoadingDialog.dismiss();

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                mOffset = offset;

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    if (TextUtils.equals(errno, BTConstants.ERRNO_NONETWORK)) {
                        if (mType == 0) {
                            ToastUtil.shortToast(LogicGlobal.sContext, message);
                        }
                    }

                    if (offset == 0) {
//                        mNoresultIv.setVisibility(View.VISIBLE);
//                        mNoresultTv.setVisibility(View.VISIBLE);
//                        clearPlan();
                    }

                    //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
                    return;
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (offset == 0) {
                        mPlanList.clear();
                        mPlanList.addAll(data);
                    } else {
                        mPlanList.addAll(data);
                    }

                    if (mPlanAdapter == null) {
                        mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
                    }

                    mPlanAdapter.setData(mPlanList);
                    mPlanAdapter.notifyDataSetChanged();
                    //mOffset += data.size();

                    //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
                } else {

                    mPlanList.clear();
                    mPlanAdapter.setData(mPlanList);
                    mPlanAdapter.notifyDataSetChanged();

                    mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);

                    //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void updatePlan() {
        if (SLSDKAgent.slUser == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        if (mLoadingPlan) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        final int offset = (mType == 1) ? 0 : mPlanHistoryOffset;

        mLoadingPlan = true;
        mTabNormal.setEnabled(false);
        BTContract.getInstance().userPlanOrders(mContractId, offset, mPlanHistoryLimit, ContractOrder.ORDER_STATE_FINISH, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {

                mTabNormal.setEnabled(true);
                mLoadingPlan = false;
                mLoadingDialog.dismiss();

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                mPlanHistoryOffset = offset;
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    if (TextUtils.equals(errno, BTConstants.ERRNO_NONETWORK)) {
                        if (mType == 0) {
                            ToastUtil.shortToast(LogicGlobal.sContext, message);
                        }
                    }

                    if (mPlanHistoryOffset == 0) {
//                        mNoresultIv.setVisibility(View.VISIBLE);
//                        mNoresultTv.setVisibility(View.VISIBLE);
//                        clearPlan();
                    } else {
                        if (!mPlanHistoryNomore) {
                            mPlanHistoryNomore = true;
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
                    return;
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (mPlanHistoryList == null) {
                        mPlanHistoryList = new ArrayList<>();
                    }

                    if (mPlanHistoryOffset == 0) {
                        mPlanHistoryList.clear();
                        mPlanHistoryList.addAll(data);
                    } else {
                        mPlanHistoryList.addAll(data);
                    }

                    if (mPlanHistoryAdapter == null) {
                        mPlanHistoryAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
                    }

                    mPlanHistoryAdapter.setData(mPlanHistoryList);
                    mPlanHistoryAdapter.notifyDataSetChanged();
                    mPlanHistoryOffset += data.size();

                } else {
                    if (mPlanHistoryOffset == 0) {
                        mPlanHistoryList.clear();
                        mPlanHistoryAdapter.setData(mPlanHistoryList);
                        mPlanHistoryAdapter.notifyDataSetChanged();
                    }

                    mNoresultIv.setVisibility(mPlanHistoryList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPlanHistoryList.size() > 0 ? View.GONE : View.VISIBLE);
                    if (!mPlanHistoryNomore) {
                        mPlanHistoryNomore = true;
                        if (mType == 0 && mPlanHistoryOffset != 0){
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
                }
            }
        });
    }

    private void clearNormal() {
        mPlanList.clear();
        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
        }

        mPlanAdapter.setData(mPlanList);
        mPlanAdapter.notifyDataSetChanged();
    }

    private void clearPlan() {
        if (mPlanHistoryList == null) {
            mPlanHistoryList = new ArrayList<>();
        }

        mPlanHistoryList.clear();

        if (mPlanHistoryAdapter == null) {
            mPlanHistoryAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
        }

        mPlanHistoryAdapter.setData(mPlanHistoryList);
        mPlanHistoryAdapter.notifyDataSetChanged();
    }


    private void updateSingleOrder(ContractOrder order) {

        if (order.getInstrument_id() != mContractId) {
            return;
        }

        if (mPlanList == null) {
            return;
        }
        boolean exist = false;
        for (int i=0; i<mPlanList.size(); i++) {
            ContractOrder item = mPlanList.get(i);
            if (item == null) {
                continue;
            }

            if (item.getOid() == order.getOid()) {
                if (order.getStatus() == ContractOrder.ORDER_STATE_APPROVAL) {
                    mPlanList.set(i, order);
                } else {
                    mPlanList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist && order.getStatus() == ContractOrder.ORDER_STATE_APPROVAL) {
            mPlanList.add(0, order);
        }

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
            mPlanRv.setAdapter(mPlanAdapter);
            mPlanAdapter.setData(mPlanList);
        }

        BTContract.getInstance().setContractPlanOrder(mContractId, mPlanList);
        mPlanAdapter.setData(mPlanList);
        mPlanAdapter.notifyDataSetChanged();

        if (mTab == 0) {
            mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);

            //mCancelAllLl.setVisibility(mPlanList.size() > 3 ? View.VISIBLE : View.GONE);
        }

        // Plan
        if (mPlanHistoryList == null) {
            return;
        }

        exist = false;
        for (int i=0; i<mPlanHistoryList.size(); i++) {
            ContractOrder item = mPlanHistoryList.get(i);
            if (item == null) {
                continue;
            }

            if (item.getOid() == order.getOid()) {
                if (order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
                    mPlanHistoryList.set(i, order);
                } else {
                    mPlanHistoryList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist && order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
            mPlanHistoryList.add(0, order);
        }

        if (mPlanHistoryAdapter == null) {
            mPlanHistoryAdapter = new ContractPlanHistoryAdapter(getActivity());
            mPlanHistoryRv.setAdapter(mPlanHistoryAdapter);
            mPlanHistoryAdapter.setData(mPlanHistoryList);
        }

        mPlanHistoryAdapter.setData(mPlanHistoryList);
        mPlanHistoryAdapter.notifyDataSetChanged();

        if (mTab == 1) {
            mNoresultIv.setVisibility(mPlanHistoryList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mPlanHistoryList.size() > 0 ? View.GONE : View.VISIBLE);
        }
    }

//
//    @Override
//    public void onLogin(Account account) {
//        if (mTab == 0) {
//            updateNormal(0);
//        } else if (mTab == 1) {
//            updatePlan();
//        }
//    }

//    @Override
//    public void onUserMe(Account account) {
//
//    }
//
//    @Override
//    public void onContractAccount(ContractAccount account) {
//
//    }
//
//    @Override
//    public void onLogout(boolean forbidden) {
//        clearNormal();
//        clearPlan();
//    }

    @Override
    public void onContractMessage(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            String action = jsonObject.optString("action");
            String group = jsonObject.optString("group");
            if (TextUtils.isEmpty(group)) {
                return;
            }

            String[] argGroup = group.split(":");
            if (argGroup.length == 1) {
                if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_CUD)) {

                    JSONArray dataArray = jsonObject.optJSONArray("data");
                    if (dataArray == null) {
                        return;
                    }

                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject obj = dataArray.optJSONObject(i);
                        if (obj == null) {
                            continue;
                        }

                        JSONObject orderObj = obj.optJSONObject("order");
                        if (orderObj == null) {
                            continue;
                        }

                        ContractOrder order = new ContractOrder();
                        order.fromJson(orderObj);

                        updateSingleOrder(order);
                    }

                }

            } else {
                return;
            }

        } catch (JSONException ignored) {
        }
    }

    @Override
    public void connectFail(String url, int reCount) {

    }

    @Override
    public void reConnectSuccess(String url, int reCount) {

    }

    @Override
    public void onContractOrderSubmit(ContractOrder order) {
        if (mType == 0) {
            setContractId(mContractId, false);
        }
    }

    @Override
    public void onContractOrderCancel(ContractOrder order) {
        if (mType == 0) {
            setContractId(mContractId, false);
        }
    }
}
