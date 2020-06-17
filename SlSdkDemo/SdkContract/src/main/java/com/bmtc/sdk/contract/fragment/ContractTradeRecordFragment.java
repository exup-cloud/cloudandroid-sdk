package com.bmtc.sdk.contract.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractTradeRecordAdapter;
import com.bmtc.sdk.contract.base.BaseFragment;
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.ContractTrade;
import com.contract.sdk.impl.IResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

@SuppressLint("ValidFragment")
public class ContractTradeRecordFragment extends BaseFragment {

    private View m_RootView;
    private ImageView mNoresultIv;
    private TextView mNoresultTv;

    private List<ContractTrade> mOrderList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ContractTradeRecordAdapter mContractTradeRecordAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private int mContractId = 1;
    private int mLimit = 20;
    private int mOffset = 0;

    private boolean mNomore = false;
    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();


    public void setContractId(int contractId) {
        mContractId = contractId;

        if (m_RootView == null) {
            return;
        }

        mOffset = 0;
        updateData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_open_order, null);


        mNoresultIv = m_RootView.findViewById(R.id.iv_noresult);
        mNoresultTv = m_RootView.findViewById(R.id.tv_noresult);

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        if (mContractTradeRecordAdapter == null) {
            mContractTradeRecordAdapter = new ContractTradeRecordAdapter(getContext());
            mContractTradeRecordAdapter.setData(mOrderList);
            mRecyclerView.setAdapter(mContractTradeRecordAdapter);
        } else {
            mRecyclerView.setAdapter(mContractTradeRecordAdapter);
        }

        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mRecyclerView.getParent());
        updateData();

        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void updateData() {
        if (!ContractSDKAgent.INSTANCE.isLogin()) {
            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        ContractUserDataAgent.INSTANCE.loadUserTrades(mContractId, mOffset, mLimit, new IResponse<List<ContractTrade>>() {
            @Override
            public void onSuccess(List<ContractTrade> data) {
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (mOrderList == null) {
                        mOrderList = new ArrayList<>();
                    }

                    if (mOffset == 0) {
                        mOrderList.clear();
                        mOrderList.addAll(data);
                    } else {
                        mOrderList.addAll(data);
                    }

                    if (mContractTradeRecordAdapter == null) {
                        mContractTradeRecordAdapter = new ContractTradeRecordAdapter(getContext());
                    }

                    mContractTradeRecordAdapter.setData(mOrderList);
                    mContractTradeRecordAdapter.notifyDataSetChanged();
                    mOffset += data.size();

                } else {
                    clearData();
                    mNoresultIv.setVisibility(View.VISIBLE);
                    mNoresultTv.setVisibility(View.VISIBLE);
                    if (!mNomore) {
                        mNomore = true;
                        if (mOffset != 0) { ToastUtil.shortToast(getContext(), getContext().getResources().getString(R.string.sl_str_no_more_data)); }
                    }
                }
            }
            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }
                ToastUtil.shortToast(getContext(), msg);
                if (mOffset == 0) {
                    mNoresultIv.setVisibility(View.VISIBLE);
                    mNoresultTv.setVisibility(View.VISIBLE);
                    clearData();
                } else {
                    if (!mNomore) {
                        mNomore = true;
                        ToastUtil.shortToast(getContext(), getContext().getResources().getString(R.string.sl_str_no_more_data));
                    }
                }
            }
        });
    }

    private void clearData() {
        if (mOrderList == null) {
            mOrderList = new ArrayList<>();
        }

        mOrderList.clear();

        if (mContractTradeRecordAdapter == null) {
            mContractTradeRecordAdapter = new ContractTradeRecordAdapter(getContext());
        }

        mContractTradeRecordAdapter.setData(mOrderList);
        mContractTradeRecordAdapter.notifyDataSetChanged();
    }
}
