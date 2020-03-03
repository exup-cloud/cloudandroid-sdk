package com.bmtc.sdk.contract.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractEntrustHistoryAdapter;
import com.bmtc.sdk.contract.adapter.ContractPlanHistoryAdapter;
import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.ContractOrder;
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
public class ContractEntrustHistoryFragment extends BaseFragment implements
        LogicWebSocketContract.IWebSocketListener{

    private RadioButton mTabNormal, mTabPlan;

    private View m_RootView;
    private ImageView mNoresultIv;
    private TextView mNoresultTv;

    private SmartRefreshLayout mNormalSrl;
    private List<ContractOrder> mOrderList = new ArrayList<>();
    private int mNormalLast;
    private RecyclerView mNormalRv;
    private LinearLayoutManager mNormalLlm;
    private ContractEntrustHistoryAdapter mNormalAdapter;

    private int mNormalLimit = 10;
    private int mNormalOffset = 0;
    private boolean mNormalNomore = false;

    private SmartRefreshLayout mPlanSrl;
    private List<ContractOrder> mPlanList = new ArrayList<>();
    private int mPlanLast;
    private RecyclerView mPlanRv;
    private LinearLayoutManager mPlanLlm;
    private ContractPlanHistoryAdapter mPlanAdapter;

    private int mPlanLimit = 10;
    private int mPlanOffset = 0;
    private boolean mPlanNomore = false;

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

        mNormalNomore = false;
        mNormalOffset = 0;
        mPlanNomore = false;
        mPlanOffset = 0;

        if (mTab == 0) {
            updateNormal();
        } else if (mTab == 1) {
            updatePlan();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mTab == 0) {
                updateNormal();
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
                    updateNormal();
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

      //  LogicUserState.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);

        mNormalSrl = m_RootView.findViewById(R.id.srl_list);
        mPlanSrl = m_RootView.findViewById(R.id.srl_plan);

        mTabNormal = m_RootView.findViewById(R.id.tab_normal);
        mTabNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTab = 0;

                mNoresultIv.setVisibility(View.GONE);
                mNoresultTv.setVisibility(View.GONE);
                mNormalSrl.setVisibility(View.VISIBLE);
                mPlanSrl.setVisibility(View.GONE);
                updateNormal();
            }
        });

        mTabPlan = m_RootView.findViewById(R.id.tab_plan);
        mTabPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTab = 1;

                mNoresultIv.setVisibility(View.GONE);
                mNoresultTv.setVisibility(View.GONE);
                mNormalSrl.setVisibility(View.GONE);
                mPlanSrl.setVisibility(View.VISIBLE);
                updatePlan();
            }
        });

        mLoadingDialog = new SlLoadingDialog(getActivity());
        mNoresultIv = m_RootView.findViewById(R.id.iv_noresult);
        mNoresultTv = m_RootView.findViewById(R.id.tv_noresult);

        mNormalRv = m_RootView.findViewById(R.id.rv_list);
        mNormalLlm = new LinearLayoutManager(LogicGlobal.sContext);
        mNormalLlm.setOrientation(LinearLayoutManager.VERTICAL);
        mNormalRv.setLayoutManager(mNormalLlm);
        mNormalRv.setItemAnimator(new DefaultItemAnimator());
        mNormalRv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mNormalLast + 1 == mNormalAdapter.getItemCount()) {
                    if (mType == 1) {
                        return;
                    }

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    if (!mNormalNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mNormalRv.getParent());
                    }

                    mNormalRv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateNormal();
                        }
                    }, 100);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mNormalLast = mNormalLlm.findLastVisibleItemPosition();
            }
        });

        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractEntrustHistoryAdapter(LogicGlobal.sContext);
            mNormalAdapter.setData(mOrderList);
            mNormalRv.setAdapter(mNormalAdapter);
        } else {
            mNormalRv.setAdapter(mNormalAdapter);
        }

        mPlanRv = m_RootView.findViewById(R.id.rv_plan);
        mPlanLlm = new LinearLayoutManager(LogicGlobal.sContext);
        mPlanLlm.setOrientation(LinearLayoutManager.VERTICAL);
        mPlanRv.setLayoutManager(mPlanLlm);
        mPlanRv.setItemAnimator(new DefaultItemAnimator());
        mPlanRv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPlanLast + 1 == mNormalAdapter.getItemCount()) {
                    if (mType == 1) {
                        return;
                    }

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    if (!mPlanNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mPlanRv.getParent());
                    }

                    mPlanRv.postDelayed(new Runnable() {
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
                mPlanLast = mPlanLlm.findLastVisibleItemPosition();
            }
        });

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
            mPlanAdapter.setData(mPlanList);
            mPlanRv.setAdapter(mPlanAdapter);
        } else {
            mPlanRv.setAdapter(mPlanAdapter);
        }
        
        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mNormalRv.getParent());
        updateNormal();

        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //LogicUserState.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
    }

    private void updateNormal() {
        if (SLSDKAgent.slUser == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        if (mLoadingNormal) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        final int offset = (mType == 1) ? 0 : mNormalOffset;

        mLoadingNormal = true;
        BTContract.getInstance().userOrders(mContractId, offset, mNormalLimit, ContractOrder.ORDER_STATE_FINISH, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {
                mLoadingNormal = false;
                mLoadingDialog.dismiss();

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                mNormalOffset = offset;
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    if (TextUtils.equals(errno, BTConstants.ERRNO_NONETWORK)) {
                        if (mType == 0) {
                            ToastUtil.shortToast(LogicGlobal.sContext, message);
                        }
                    }

                    if (mNormalOffset == 0) {
                        mNoresultIv.setVisibility(View.VISIBLE);
                        mNoresultTv.setVisibility(View.VISIBLE);
                        clearNormal();
                    } else {
                        if (!mNormalNomore) {
                            mNormalNomore = true;
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
                    return;
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (mOrderList == null) {
                        mOrderList = new ArrayList<>();
                    }

                    if (mNormalOffset == 0) {
                        mOrderList.clear();
                        mOrderList.addAll(data);
                    } else {
                        mOrderList.addAll(data);
                    }

                    if (mNormalAdapter == null) {
                        mNormalAdapter = new ContractEntrustHistoryAdapter(LogicGlobal.sContext);
                    }

                    mNormalAdapter.setData(mOrderList);
                    mNormalAdapter.notifyDataSetChanged();
                    mNormalOffset += data.size();

                } else {
                    if (mNormalOffset == 0) {
                        mOrderList.clear();
                        mNormalAdapter.setData(mOrderList);
                        mNormalAdapter.notifyDataSetChanged();
                    }

                    mNoresultIv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
                    if (!mNormalNomore) {
                        mNormalNomore = true;
                        if (mType == 0 && mNormalOffset != 0){
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
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

        final int offset = (mType == 1) ? 0 : mPlanOffset;

        mLoadingPlan = true;
        BTContract.getInstance().userPlanOrders(mContractId, offset, mPlanLimit, ContractOrder.ORDER_STATE_FINISH, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {
                mLoadingPlan = false;
                mLoadingDialog.dismiss();

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                mPlanOffset = offset;
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    if (TextUtils.equals(errno, BTConstants.ERRNO_NONETWORK)) {
                        if (mType == 0) {
                            ToastUtil.shortToast(LogicGlobal.sContext, message);
                        }
                    }

                    if (mPlanOffset == 0) {
                        mNoresultIv.setVisibility(View.VISIBLE);
                        mNoresultTv.setVisibility(View.VISIBLE);
                        clearPlan();
                    } else {
                        if (!mPlanNomore) {
                            mPlanNomore = true;
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
                    return;
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (mPlanList == null) {
                        mPlanList = new ArrayList<>();
                    }

                    if (mPlanOffset == 0) {
                        mPlanList.clear();
                        mPlanList.addAll(data);
                    } else {
                        mPlanList.addAll(data);
                    }

                    if (mPlanAdapter == null) {
                        mPlanAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
                    }

                    mPlanAdapter.setData(mPlanList);
                    mPlanAdapter.notifyDataSetChanged();
                    mPlanOffset += data.size();

                } else {
                    if (mPlanOffset == 0) {
                        mPlanList.clear();
                        mPlanAdapter.setData(mPlanList);
                        mPlanAdapter.notifyDataSetChanged();
                    }

                    mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
                    if (!mPlanNomore) {
                        mPlanNomore = true;
                        if (mType == 0 && mPlanOffset != 0){
                            ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getResources().getString(R.string.sl_str_no_more_data));
                        }
                    }
                }
            }
        });
    }

    private void clearNormal() {
        if (mOrderList == null) {
            mOrderList = new ArrayList<>();
        }

        mOrderList.clear();

        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractEntrustHistoryAdapter(LogicGlobal.sContext);
        }

        mNormalAdapter.setData(mOrderList);
        mNormalAdapter.notifyDataSetChanged();
    }

    private void clearPlan() {
        if (mPlanList == null) {
            mPlanList = new ArrayList<>();
        }

        mPlanList.clear();

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanHistoryAdapter(LogicGlobal.sContext);
        }

        mPlanAdapter.setData(mPlanList);
        mPlanAdapter.notifyDataSetChanged();
    }


    private void updateSingleOrder(ContractOrder order) {
        if (mOrderList == null) {
            return;
        }

        if (order.getInstrument_id() != mContractId) {
            return;
        }

        boolean exist = false;

        for (int i=0; i<mOrderList.size(); i++) {
            ContractOrder item = mOrderList.get(i);
            if (item == null) {
                continue;
            }

            if (item.getOid() == order.getOid()) {
                if (order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
                    mOrderList.set(i, order);
                } else {
                    mOrderList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist && order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
            mOrderList.add(0, order);
        }

        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractEntrustHistoryAdapter(getActivity());
            mNormalRv.setAdapter(mNormalAdapter);
            mNormalAdapter.setData(mOrderList);
        }

        mNormalAdapter.setData(mOrderList);
        mNormalAdapter.notifyDataSetChanged();

        if (mTab == 0) {
            mNoresultIv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
        }
        // Plan
        if (mPlanList == null) {
            return;
        }

        exist = false;
        for (int i=0; i<mPlanList.size(); i++) {
            ContractOrder item = mPlanList.get(i);
            if (item == null) {
                continue;
            }

            if (item.getOid() == order.getOid()) {
                if (order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
                    mPlanList.set(i, order);
                } else {
                    mPlanList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist && order.getStatus() == ContractOrder.ORDER_STATE_FINISH) {
            mPlanList.add(0, order);
        }

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanHistoryAdapter(getActivity());
            mPlanRv.setAdapter(mPlanAdapter);
            mPlanAdapter.setData(mPlanList);
        }

        mPlanAdapter.setData(mPlanList);
        mPlanAdapter.notifyDataSetChanged();

        if (mTab == 1) {
            mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
        }
    }

//
//    @Override
//    public void onLogin(Account account) {
//        updateNormal();
//    }
//
//    @Override
//    public void onUserMe(Account account) {
//
//    }
//
//    @Override
//    public void onContractAccount(ContractAccount account) {
//
//    }

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
}
