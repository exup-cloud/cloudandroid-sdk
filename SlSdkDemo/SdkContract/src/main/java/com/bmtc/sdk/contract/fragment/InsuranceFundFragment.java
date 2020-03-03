package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.InsuranceFundAdapter;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.InsuranceFund;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

public class InsuranceFundFragment extends BaseFragment {

    private View m_RootView;

    private List<InsuranceFund> mTradeList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private InsuranceFundAdapter mInsuranceFundAdapter;
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

        if (mInsuranceFundAdapter == null) {
            mInsuranceFundAdapter = new InsuranceFundAdapter(getActivity());
            mInsuranceFundAdapter.setData(mTradeList);
            mRecyclerView.setAdapter(mInsuranceFundAdapter);
        } else {
            mRecyclerView.setAdapter(mInsuranceFundAdapter);
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

        BTContract.getInstance().riskReserves(mContractId, new IResponse<List<InsuranceFund>>() {
            @Override
            public void onResponse(String errno, String message, List<InsuranceFund> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                    return;
                }

                if (data != null) {
                    mTradeList.clear();
                    mTradeList.addAll(data);

                    if (mInsuranceFundAdapter == null) {
                        mInsuranceFundAdapter = new InsuranceFundAdapter(getActivity());
                    }

                    mInsuranceFundAdapter.setData(mTradeList);
                    mInsuranceFundAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
