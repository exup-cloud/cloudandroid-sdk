package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractOpenOrderAdapter;
import com.bmtc.sdk.contract.adapter.ContractPlanOrderAdapter;
import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.common.pswkeyboard.OnPasswordInputFinish;
import com.bmtc.sdk.library.common.pswkeyboard.widget.PopEnterPassword;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.trans.data.ContractOrders;
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

public class ContractOpenOrdersFragment extends BaseFragment implements
        LogicContractOrder.IContractOrderListener,
        LogicWebSocketContract.IWebSocketListener{

    private View m_RootView;

    private RadioButton mTabNormal, mTabPlan;

    private ImageView mNoresultIv;
    private TextView mNoresultTv;
    private Button mCancelAllBtn;
    private LinearLayout mCancelAllLl;

    private SmartRefreshLayout mNormalSrl;
    private List<ContractOrder> mOrderList = new ArrayList<>();
    private RecyclerView mNormalRv;
    private LinearLayoutManager mNormalLlm;
    private ContractOpenOrderAdapter mNormalAdapter;

    private SmartRefreshLayout mPlanSrl;
    private List<ContractOrder> mPlanList = new ArrayList<>();
    private RecyclerView mPlanRv;
    private LinearLayoutManager mPlanLlm;
    private ContractPlanOrderAdapter mPlanAdapter;

    private int mLimit = 10;
    private int mOffset = 0;

    private int mType = 0;  //0normal; 1tradefragment
    private int mContractId;
    private int mTab = 0;   //0normal 1plan


    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();
    private SlLoadingDialog mLoadingDialog;
    private boolean mLoadingNormal = false;
    private boolean mLoadingPlan = false;

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

        if (contractId == 0) {
            mCancelAllBtn.setText(R.string.sl_str_cancel_all_orders);
        } else {
            Contract contract = LogicGlobal.getContract(mContractId);
            if (contract != null) {
                String text = String.format(getString(R.string.sl_str_cancel_all_orders_single), contract.getSymbol());
                mCancelAllBtn.setText(text);
            } else {
                mCancelAllBtn.setText(R.string.sl_str_cancel_all_orders);
            }
        }

        if (loading) {
            mLoadingDialog.show();
        }

        if (mTab == 0) {
            updateNormal(0);
        } else if (mTab == 1) {
            updatePlan(0);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mTab == 0) {
                updateNormal(0);
            } else if (mTab == 1) {
                updatePlan(0);
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
                    updatePlan(0);
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
            m_RootView = inflater.inflate(R.layout.sl_fragment_open_order_trade_plan, null);
        }

        //LogicUserState.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);
        LogicContractOrder.getInstance().registListener(this);

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
                updateNormal(mOffset);
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
                updatePlan(mOffset);
            }
        });

        mLoadingDialog = new SlLoadingDialog(getActivity());
        mCancelAllLl = m_RootView.findViewById(R.id.ll_cancel_all);
        mCancelAllBtn = m_RootView.findViewById(R.id.btn_cancel_all);
        mCancelAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAll();
            }
        });

        mNoresultIv = m_RootView.findViewById(R.id.iv_noresult);
        mNoresultTv = m_RootView.findViewById(R.id.tv_noresult);

        mNormalRv = m_RootView.findViewById(R.id.rv_list);
        mNormalLlm = new LinearLayoutManager(LogicGlobal.sContext);
        mNormalLlm.setOrientation(LinearLayoutManager.VERTICAL);
        mNormalRv.setLayoutManager(mNormalLlm);
        mNormalRv.setItemAnimator(new DefaultItemAnimator());


        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractOpenOrderAdapter(getActivity());
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
        ((SimpleItemAnimator)mPlanRv.getItemAnimator()).setSupportsChangeAnimations(false);

        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
            mPlanAdapter.setData(mPlanList);
            mPlanRv.setAdapter(mPlanAdapter);
        } else {
            mPlanRv.setAdapter(mPlanAdapter);
        }

        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mNormalRv.getParent());
        updateNormal(mOffset);

        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  LogicUserState.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
        LogicContractOrder.getInstance().unregistListener(this);
    }

    private void updateNormal(final int offset) {

        if (SLSDKAgent.slUser == null || m_RootView == null) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        List<ContractOrder> orders = BTContract.getInstance().getContractOrder(mContractId);
        if (orders != null) {
            mOrderList.clear();
            mOrderList.addAll(orders);

            if (mNormalAdapter == null) {
                mNormalAdapter = new ContractOpenOrderAdapter(getActivity());
            }

            mNormalAdapter.setData(mOrderList);
            mNormalAdapter.notifyDataSetChanged();

            if (mCancelAllLl != null) {
                if (mType == 1) {
                    mNormalRv.setPadding(0,0,0,
                            mOrderList.size() >= 2 ? UtilSystem.dip2px(getActivity(), 50) : 0);
                }
                mCancelAllLl.setVisibility(mOrderList.size() >= 2 ? View.VISIBLE : View.GONE);
            }
        }

        if (mLoadingNormal) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        mLoadingNormal = true;
        BTContract.getInstance().userOrders(mContractId, offset, mLimit, ContractOrder.ORDER_STATE_APPROVAL | ContractOrder.ORDER_STATE_ENTRUST, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {

                mLoadingNormal = false;
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
                        mNoresultIv.setVisibility(View.VISIBLE);
                        mNoresultTv.setVisibility(View.VISIBLE);
                        clearNormal();
                    }

                    if (mCancelAllLl != null) {
                        if (mType == 1) {
                            mNormalRv.setPadding(0,0,0,
                                    mOrderList.size() >= 2 ? UtilSystem.dip2px(getActivity(), 50) : 0);
                        }
                        mCancelAllLl.setVisibility(mOrderList.size() >= 2 ? View.VISIBLE : View.GONE);
                    }
                    return;
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (offset == 0) {
                        mOrderList.clear();
                        mOrderList.addAll(data);
                    } else {
                        mOrderList.addAll(data);
                    }

                    if (mNormalAdapter == null) {
                        mNormalAdapter = new ContractOpenOrderAdapter(getActivity());
                    }

                    mNormalAdapter.setData(mOrderList);
                    mNormalAdapter.notifyDataSetChanged();
                    //mOffset += data.size();

                    if (mCancelAllLl != null) {
                        if (mType == 1) {
                            mNormalRv.setPadding(0,0,0,
                                    mOrderList.size() >= 2 ? UtilSystem.dip2px(getActivity(), 50) : 0);
                        }
                        mCancelAllLl.setVisibility(mOrderList.size() >= 2 ? View.VISIBLE : View.GONE);
                    }
                } else {

                    mOrderList.clear();
                    mNormalAdapter.setData(mOrderList);
                    mNormalAdapter.notifyDataSetChanged();

                    mNoresultIv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);

                    if (mCancelAllLl != null) {
                        if (mType == 1) {
                            mNormalRv.setPadding(0,0,0,
                                    mOrderList.size() >= 2 ? UtilSystem.dip2px(getActivity(), 50) : 0);
                        }
                        mCancelAllLl.setVisibility(mOrderList.size() >= 2 ? View.VISIBLE : View.GONE);
                    }
                }
            }
        });
    }

    private void updatePlan(final int offset) {

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

            mCancelAllLl.setVisibility(mPlanList.size() >= 2 ? View.VISIBLE : View.GONE);
        }

        if (mLoadingPlan) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        mLoadingPlan = true;
        BTContract.getInstance().userPlanOrders(mContractId, offset, mLimit, ContractOrder.ORDER_STATE_APPROVAL|ContractOrder.ORDER_STATE_ENTRUST, new IResponse<List<ContractOrder>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractOrder> data) {

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
                        mNoresultIv.setVisibility(View.VISIBLE);
                        mNoresultTv.setVisibility(View.VISIBLE);
                        clearPlan();
                    }

                    mCancelAllLl.setVisibility(mPlanList.size() >= 2 ? View.VISIBLE : View.GONE);
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

                    mCancelAllLl.setVisibility(mPlanList.size() >= 2 ? View.VISIBLE : View.GONE);
                } else {

                    mPlanList.clear();
                    mPlanAdapter.setData(mPlanList);
                    mPlanAdapter.notifyDataSetChanged();

                    mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);

                    mCancelAllLl.setVisibility(mPlanList.size() >= 2 ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void cancelAll() {
//        final PromptWindow window = new PromptWindow(getActivity());
//        window.showTitle(getActivity().getString(R.string.sl_str_tips));
//        window.showTvContent(getActivity().getString(R.string.sl_str_cancel_all_order_tips));
//        window.showBtnOk(getActivity().getString(R.string.sl_str_confirm));
//        window.showBtnCancel(getActivity().getString(R.string.sl_str_cancel));
//        window.showAtLocation(mCancelAllBtn, Gravity.CENTER, 0, 0);
//        window.getBtnOk().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                window.dismiss();
//                if (mTab == 0) {
//                    doCancelAll("");
//                } else if (mTab == 1) {
//                    doCancelAllPlan("");
//                }
//            }
//        });
//        window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                window.dismiss();
//            }
//        });
    }

    private void doCancelAll(String pwd) {
        final List<ContractOrder> orderList = new ArrayList<>();
        orderList.addAll(mOrderList);

        ContractOrders orders = new ContractOrders();
        orders.setContract_id(mContractId);
        for (int i=0; i<orderList.size(); i++){
            ContractOrder item = orderList.get(i);
            if (item == null) {
                continue;
            }

            orders.getOrders().add(item);
        }

        IResponse<List<Long>> response = new IResponse<List<Long>>() {
            @Override
            public void onResponse(String errno, String message, List<Long> data) {
                if (TextUtils.equals(errno, BTConstants.ERRNO_PERMISSION_DENIED)) {
                    final PopEnterPassword popEnterPassword = new PopEnterPassword(getActivity());
                    popEnterPassword.showAtLocation(mCancelAllBtn, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popEnterPassword.setOnFinishInput(new OnPasswordInputFinish() {
                        @Override
                        public void inputFinish(String password) {
                            doCancelAll(UtilSystem.toMD5(password));
                            popEnterPassword.dismiss();
                        }
                    });
                    return;
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                    return;
                }

                if (data != null && data.size() > 0) {
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_some_orders_cancel_failed));
                }


                BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                    @Override
                    public void onResponse(String errno, String message, List<ContractAccount> data) {
                    }
                });
            }
        };

        if (TextUtils.isEmpty(pwd)) {
            BTContract.getInstance().cancelOrders(orders, response);
        } else {
            BTContract.getInstance().cancelOrders(orders, pwd, response);
        }
    }


    private void doCancelAllPlan(String pwd) {
        final List<ContractOrder> orderList = new ArrayList<>();
        orderList.addAll(mPlanList);

        ContractOrders orders = new ContractOrders();
        orders.setContract_id(mContractId);
        for (int i=0; i<orderList.size(); i++){
            ContractOrder item = orderList.get(i);
            if (item == null) {
                continue;
            }

            orders.getOrders().add(item);
        }

        IResponse<List<Long>> response = new IResponse<List<Long>>() {
            @Override
            public void onResponse(String errno, String message, List<Long> data) {
                if (TextUtils.equals(errno, BTConstants.ERRNO_PERMISSION_DENIED)) {
                    final PopEnterPassword popEnterPassword = new PopEnterPassword(getActivity());
                    popEnterPassword.showAtLocation(mCancelAllBtn, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popEnterPassword.setOnFinishInput(new OnPasswordInputFinish() {
                        @Override
                        public void inputFinish(String password) {
                            doCancelAllPlan(UtilSystem.toMD5(password));
                            popEnterPassword.dismiss();
                        }
                    });
                    return;
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                    return;
                }

                if (data != null && data.size() > 0) {
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_some_orders_cancel_failed));
                }


                BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                    @Override
                    public void onResponse(String errno, String message, List<ContractAccount> data) {
                    }
                });
            }
        };

        if (TextUtils.isEmpty(pwd)) {
            BTContract.getInstance().cancelPlanOrders(orders, response);
        } else {
            BTContract.getInstance().cancelPlanOrders(orders, pwd, response);
        }
    }

    private void clearNormal() {
        mOrderList.clear();
        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractOpenOrderAdapter(getActivity());
        }

        mNormalAdapter.setData(mOrderList);
        mNormalAdapter.notifyDataSetChanged();
    }

    private void clearPlan() {
        mPlanList.clear();
        if (mPlanAdapter == null) {
            mPlanAdapter = new ContractPlanOrderAdapter(getActivity());
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
                if (order.getStatus() == ContractOrder.ORDER_STATE_APPROVAL || order.getStatus() == ContractOrder.ORDER_STATE_ENTRUST) {
                    mOrderList.set(i, order);
                } else {
                    mOrderList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist && (order.getStatus() == ContractOrder.ORDER_STATE_APPROVAL || order.getStatus() == ContractOrder.ORDER_STATE_ENTRUST)) {
            mOrderList.add(0, order);
        }

        if (mNormalAdapter == null) {
            mNormalAdapter = new ContractOpenOrderAdapter(getActivity());
            mNormalRv.setAdapter(mNormalAdapter);
            mNormalAdapter.setData(mOrderList);
        }

        BTContract.getInstance().setContractOrder(mContractId, mOrderList);
        mNormalAdapter.setData(mOrderList);
        mNormalAdapter.notifyDataSetChanged();

        if (mTab == 0) {
            mNoresultIv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mOrderList.size() > 0 ? View.GONE : View.VISIBLE);

            if (mType == 1) {
                mNormalRv.setPadding(0,0,0,
                        mOrderList.size() >= 2 ? UtilSystem.dip2px(getActivity(), 50) : 0);
            }
            mCancelAllLl.setVisibility(mOrderList.size()>= 2 ? View.VISIBLE : View.GONE);
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

        if (mTab == 1) {
            mNoresultIv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);
            mNoresultTv.setVisibility(mPlanList.size() > 0 ? View.GONE : View.VISIBLE);

            mCancelAllLl.setVisibility(mPlanList.size() >= 2 ? View.VISIBLE : View.GONE);
        }
    }

//    @Override
//    public void onLogin(Account account) {
//        updateNormal(mOffset);
//    }
//
//    @Override
//    public void onUserMe(Account account) {
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
            setContractId(mContractId, true);
        }
    }

    @Override
    public void onContractOrderCancel(ContractOrder order) {
        if (mType == 0) {
            setContractId(mContractId, true);
        }
    }
}
