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
import com.bmtc.sdk.contract.adapter.HoldContractHistoryAdapter;
import com.bmtc.sdk.contract.base.BaseFragment;
import com.bmtc.sdk.contract.uiLogic.LogicLoadAnimation;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.impl.ContractUserStatusListener;
import com.contract.sdk.impl.IResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

@SuppressLint("ValidFragment")
public class HoldContractHistoryFragment extends BaseFragment {

    private View m_RootView;
    private ImageView mNoresultIv;
    private TextView mNoresultTv;

    private List<ContractPosition> mPositionList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private HoldContractHistoryAdapter mHoldContractHistoryAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private int mLimit = 10;
    private int mOffset = 0;

    private boolean mNomore = false;
    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();
    private boolean mLoading = false;

    private int mContractId = 1;

    public void setContractId(int contractId) {

        mContractId = contractId;

        if (m_RootView == null) {
            return;
        }

        mNomore = false;
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mHoldContractHistoryAdapter.getItemCount()) {

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    if (!mNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mRecyclerView.getParent());
                    }

                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                        }
                    }, 100);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        if (mHoldContractHistoryAdapter == null) {
            mHoldContractHistoryAdapter = new HoldContractHistoryAdapter(getContext());
            mHoldContractHistoryAdapter.setData(mPositionList);
            mRecyclerView.setAdapter(mHoldContractHistoryAdapter);
        } else {
            mRecyclerView.setAdapter(mHoldContractHistoryAdapter);
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
        if (mLoading) {
            return;
        }

        mLoading = true;

        ContractUserDataAgent.INSTANCE.loadContractPosition(mContractId, 4, mOffset, mLimit, new IResponse<List<ContractPosition>>() {
            @Override
            public void onSuccess(List<ContractPosition> data) {
                mLoading = false;
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                if (data != null && data.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);
                    if (mPositionList == null) {
                        mPositionList = new ArrayList<>();
                    }

                    if (mOffset == 0) {
                        mPositionList.clear();
                        mPositionList.addAll(data);
                    } else {
                        mPositionList.addAll(data);
                    }

                    if (mHoldContractHistoryAdapter == null) {
                        mHoldContractHistoryAdapter = new HoldContractHistoryAdapter(getContext());
                    }

                    mHoldContractHistoryAdapter.setData(mPositionList);
                    mHoldContractHistoryAdapter.notifyDataSetChanged();
                    mOffset += data.size();

                } else {
                    mNoresultIv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
                    if (!mNomore) {
                        mNomore = true;
                        if (mOffset != 0) { ToastUtil.shortToast(getContext(), getContext().getResources().getString(R.string.sl_str_no_more_data)); }
                    }
                }
            }

            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                mLoading = false;
                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }
                ToastUtil.shortToast(getContext(), msg);
                if (mOffset == 0) {
                    //mNoresultIv.setVisibility(View.VISIBLE);
                    //mNoresultTv.setVisibility(View.VISIBLE);
                    //clearData();
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
        if (mPositionList == null) {
            mPositionList = new ArrayList<>();
        }

        mPositionList.clear();

        if (mHoldContractHistoryAdapter == null) {
            mHoldContractHistoryAdapter = new HoldContractHistoryAdapter(getContext());
        }

        mHoldContractHistoryAdapter.setData(mPositionList);
        mHoldContractHistoryAdapter.notifyDataSetChanged();
    }

}
