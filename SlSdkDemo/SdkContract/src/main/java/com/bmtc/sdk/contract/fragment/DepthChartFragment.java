package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.DepthChartAdapter;
import com.bmtc.sdk.contract.base.BaseFragment;
import com.bmtc.sdk.contract.utils.UtilSystem;
import com.contract.sdk.data.DepthData;
import com.contract.sdk.utils.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by zj on 2018/3/1.
 */

public class DepthChartFragment extends BaseFragment {

    private View m_RootView;


    private RecyclerView mRecyclerView;
    private DepthChartAdapter mDepthChartAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<DepthData> buyList = new ArrayList<>();
    private ArrayList<DepthData> sellList = new ArrayList<>();

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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mDepthChartAdapter.getItemCount()) {
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

        if (mDepthChartAdapter == null) {
            mDepthChartAdapter = new DepthChartAdapter(getActivity());
            mRecyclerView.setAdapter(mDepthChartAdapter);
        } else {
            mRecyclerView.setAdapter(mDepthChartAdapter);
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
    }

    public void setBuyData( ArrayList<DepthData> list, int contractId) {
        if(!isVisible()){
            return;
        }
        if (mDepthChartAdapter == null) {
            mDepthChartAdapter = new DepthChartAdapter(getActivity());
        }
        buyList.clear();
        buyList.addAll(list);
        mDepthChartAdapter.setData(buyList, sellList, contractId);
        mDepthChartAdapter.notifyDataSetChanged();
    }

    public void setSellData( ArrayList<DepthData> list, int contractId) {
        if(!isVisible()){
            return;
        }
        if (mDepthChartAdapter == null) {
            mDepthChartAdapter = new DepthChartAdapter(getActivity());
        }

        sellList.clear();
        sellList.addAll(list);

        mDepthChartAdapter.setData(buyList, sellList, contractId);
        mDepthChartAdapter.notifyDataSetChanged();
    }
}
