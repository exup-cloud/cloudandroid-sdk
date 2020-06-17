package com.bmtc.sdk.contract.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.DepthChartAdapter;
import com.bmtc.sdk.contract.adapter.OrderBookAdapter;
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

public class OrderBookFragment extends BaseFragment {

    private static final int H_CODE_UPDATE = 1;

    private View m_RootView;


    private RecyclerView mRecyclerView;
    private OrderBookAdapter mOrderBookAdapter;
    private int mLastVisibleItem;
    private WrapContentLinearLayoutManager linearLayoutManager;

    private Object lock = new Object();

    private ArrayList<DepthData> buyList = new ArrayList<>();
    private ArrayList<DepthData> sellList = new ArrayList<>();

    private int contractId;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_market_others, null);

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new WrapContentLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mOrderBookAdapter.getItemCount()) {
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

        if (mOrderBookAdapter == null) {
            mOrderBookAdapter = new OrderBookAdapter(getActivity());
            mRecyclerView.setAdapter(mOrderBookAdapter);
        } else {
            mRecyclerView.setAdapter(mOrderBookAdapter);
        }

        return m_RootView;
    }

    public boolean isForeground() {
        return getActivity() != null && isAdded() &&!getHidden() ;
    }

    public boolean isForegroundSpot() {
        return getActivity() != null && isAdded() && !getHidden();
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
        this.contractId = contractId;
        if(!isVisible()){
            return;
        }
        if (mOrderBookAdapter == null) {
            mOrderBookAdapter = new OrderBookAdapter(getActivity());
        }
        buyList.clear();
        buyList.addAll(list);
        mOrderBookAdapter.setData(buyList, sellList, contractId);
        mOrderBookAdapter.notifyDataSetChanged();
    }

    public void setSellData( ArrayList<DepthData> list, int contractId) {
        if(!isVisible()){
            return;
        }
        if (mOrderBookAdapter == null) {
            mOrderBookAdapter = new OrderBookAdapter(getActivity());
        }

        sellList.clear();
        sellList.addAll(list);

        mOrderBookAdapter.setData(buyList, sellList, contractId);
        mOrderBookAdapter.notifyDataSetChanged();
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

}
