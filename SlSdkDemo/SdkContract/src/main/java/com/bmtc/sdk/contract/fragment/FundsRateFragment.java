package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.FundsRateAdapter;
import com.bmtc.sdk.contract.base.BaseFragment;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.ContractFundingRate;
import com.contract.sdk.data.ContractOrder;
import com.contract.sdk.impl.IResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

public class FundsRateFragment extends BaseFragment {

    private View m_RootView;

    private List<ContractFundingRate> mTradeList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private FundsRateAdapter mFundsRateAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private int mContractId;

    public void setContractId(int contractId) {
        mContractId = contractId;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_market_others, null);

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mFundsRateAdapter == null) {
            mFundsRateAdapter = new FundsRateAdapter(getActivity());
            mFundsRateAdapter.setData(mTradeList);
            mRecyclerView.setAdapter(mFundsRateAdapter);
        } else {
            mRecyclerView.setAdapter(mFundsRateAdapter);
        }

        updateData();
        return m_RootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateData() {
        if (m_RootView == null) {
            return;
        }

        ContractPublicDataAgent.INSTANCE.loadFundingRate(mContractId, new IResponse<List<ContractFundingRate>>() {
            @Override
            public void onSuccess(@NotNull List<ContractFundingRate> data) {
                if (data != null) {
                    mTradeList.clear();
                    mTradeList.addAll(data);

                    if (mFundsRateAdapter == null) {
                        mFundsRateAdapter = new FundsRateAdapter(getActivity());
                    }

                    mFundsRateAdapter.setData(mTradeList);
                    mFundsRateAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                ToastUtil.shortToast(getContext(), msg);
            }
        });

    }
}
