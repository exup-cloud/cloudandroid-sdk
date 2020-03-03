package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.TradeHistoryAdapter;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.trans.data.SDStockTrade;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

public class TradeHistoryFragment extends BaseFragment {

    private View m_RootView;

    private List<SDStockTrade> mTradeList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private TradeHistoryAdapter mTradeHistoryAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private int mType = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_market_others, null);

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mTradeHistoryAdapter.getItemCount()) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //updateData(mType);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        if (mTradeHistoryAdapter == null) {
            mTradeHistoryAdapter = new TradeHistoryAdapter(getActivity());
            mTradeHistoryAdapter.setData(mTradeList);
            mRecyclerView.setAdapter(mTradeHistoryAdapter);
        } else {
            mRecyclerView.setAdapter(mTradeHistoryAdapter);
        }

        return m_RootView;
    }

    public boolean isForeground() {
        return getActivity() != null && isAdded() && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setData(List<SDStockTrade> list) {

        mTradeList.clear();
        mTradeList.addAll(list);

        if (mTradeHistoryAdapter == null) {
            mTradeHistoryAdapter = new TradeHistoryAdapter(getActivity());
        }

        mTradeHistoryAdapter.setData(mTradeList);
        mTradeHistoryAdapter.notifyDataSetChanged();
    }
}
