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
import com.bmtc.sdk.contract.adapter.DepthChartAdapter;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.trans.data.Depth;
import com.bmtc.sdk.library.trans.data.DepthData;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by zj on 2018/3/1.
 */

public class DepthChartFragment extends BaseFragment {

    private View m_RootView;

    private Depth mDepth = new Depth();

    private RecyclerView mRecyclerView;
    private DepthChartAdapter mDepthChartAdapter;
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

    public boolean isForeground() {
        return getActivity() != null && isAdded() && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, "com.ggex.bmtc.ui.activity.ContractTickerOneActivity");
    }
    public boolean isForegroundSpot() {
        return getActivity() != null && isAdded() && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, "com.ggex.bmtc.ui.activity.SpotTickerOneActivity");
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setData(Depth depth, String stockCode, int contractId) {

        mDepth = depth;

        if (mDepthChartAdapter == null) {
            mDepthChartAdapter = new DepthChartAdapter(getActivity());
        }

        mDepthChartAdapter.setData(mDepth.getAsks(), mDepth.getBids(), stockCode, contractId);
        mDepthChartAdapter.notifyDataSetChanged();

    }

    /**
     * @param depth  5 删除 4 插入 2 更新
     * @param actionType
     */
    public void updateDateByType(Depth depth, int actionType) {
        List<DepthData> updateBuyList = depth.getBids();
        List<DepthData> updateSellList = depth.getAsks();

        List<DepthData> oldBuyList = mDepthChartAdapter.getBuys();
        List<DepthData> oldSellList = mDepthChartAdapter.getSells();
        if (actionType == 5) {
            List<DepthData> deleteBuyList = new ArrayList<>();
            for (int i = 0; i < updateBuyList.size(); i++) {
                DepthData depthData = isContainDepthData(oldBuyList, updateBuyList.get(i));
                if (depthData != null) {
                    deleteBuyList.add(depthData);
                }
            }
            oldBuyList.removeAll(deleteBuyList);

            List<DepthData> deleteSellList = new ArrayList<>();
            for (int i = 0; i < updateSellList.size(); i++) {
                DepthData depthData = isContainDepthData(oldSellList, updateSellList.get(i));
                if (depthData != null) {
                    deleteSellList.add(depthData);
                }
            }
            oldSellList.removeAll(deleteSellList);
        }else if(actionType == 4){
            List<DepthData> addBuyList = new ArrayList<>();
            for (int i = 0; i < updateBuyList.size(); i++) {
                DepthData depthData = isContainDepthData(oldBuyList, updateBuyList.get(i));
                if (depthData == null) {
                    addBuyList.add(updateBuyList.get(i));
                }
            }
            oldBuyList.addAll(0,addBuyList) ;

            List<DepthData> addSellList = new ArrayList<>();
            for (int i = 0; i < updateSellList.size(); i++) {
                DepthData depthData = isContainDepthData(oldSellList, updateSellList.get(i));
                if (depthData == null) {
                    addSellList.add(updateSellList.get(i));
                }
            }
            oldSellList.addAll(0,addSellList) ;
        }else if(actionType == 2){
            for (int i = 0; i < updateBuyList.size(); i++) {
                if (Double.valueOf(updateBuyList.get(i).getVol()).compareTo(0.0) <= 0) {
                    DepthData depthData = isContainDepthData(oldBuyList, updateBuyList.get(i));
                    if (depthData != null) {
                        //LogUtil.d("lb","删除:"+bindList.get(i).getVol()+";"+bindList.get(i).getKey());
                        oldBuyList.remove(depthData);
                    }
                } else {
                    DepthData depthData = isContainDepthData(oldBuyList, updateBuyList.get(i));
                    if (depthData != null) {
                        // LogUtil.d("lb","更新:"+bindList.get(i).getVol()+";"+bindList.get(i).getKey());
                        depthData.setVol(updateBuyList.get(i).getVol());
                        depthData.setPrice(updateBuyList.get(i).getPrice());
                    } else {
                        // LogUtil.d("lb","增加:"+bindList.get(i).getVol() + ";" + oldSellList.size()+";"+bindList.get(i).getPrice());
                        oldBuyList.add(0,updateBuyList.get(i));
                    }
                }
            }

            for (int i = 0; i < updateSellList.size(); i++) {
                //若vol为0 则不添加，并移除
                if (Double.valueOf(updateSellList.get(i).getVol()).compareTo(0.0) == 0) {
                    DepthData depthData = isContainDepthData(oldSellList, updateSellList.get(i));
                    if (depthData != null) {
                        oldSellList.remove(depthData);
                    }
                } else {
                    DepthData depthData = isContainDepthData(oldSellList, updateSellList.get(i));
                    if (depthData != null) {
                        depthData.setVol(updateSellList.get(i).getVol());
                        depthData.setPrice(updateSellList.get(i).getPrice());
                    } else {
                        oldSellList.add(0,updateSellList.get(i));
                        //  LogUtil.d("lb", "增加卖:" + updateSellList.get(i).getVol()+ ";" + updateSellList.get(i).getPrice() + ";" + updateSellList.get(i).getKey());
                    }
                }
            }
        }else if(actionType == 1){
            mDepth = depth;
            oldBuyList.clear();
            oldBuyList.addAll(updateBuyList);

            oldSellList.clear();
            oldSellList.addAll(updateSellList);
        }
        Collections.sort(oldBuyList, new Comparator<DepthData>() {
            @Override
            public int compare(DepthData o1, DepthData o2) {
                if (MathHelper.round(o1.getPrice(), 8) < MathHelper.round(o2.getPrice(), 8)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        Collections.sort(oldSellList, new Comparator<DepthData>() {
            @Override
            public int compare(DepthData o1, DepthData o2) {
                if (MathHelper.round(o1.getPrice(), 8) < MathHelper.round(o2.getPrice(), 8)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        List<DepthData> buyList = new ArrayList<>();
        int buyCount = oldBuyList.size();
        // LogUtil.d("lb", "oldBuyList.size:" + oldBuyList.size());
        for (int i = 0 ; i < 10 ; i++){
            if(i < buyCount){
                buyList.add(oldBuyList.get(i));
            }
        }
        oldBuyList.clear();
        oldBuyList.addAll(buyList);

        List<DepthData> sellList = new ArrayList<>();
        int sellCount = oldSellList.size();
        for (int i = 0 ; i < 10 ; i++){
            if(i < sellCount){
                sellList.add(oldSellList.get(i));
            }
        }
        oldSellList.clear();
        oldSellList.addAll(sellList);

        mDepthChartAdapter.notifyDataSetChanged();
    }

    private DepthData isContainDepthData(List<DepthData> sellList, DepthData data) {
        if (sellList == null || data == null) {
            return null;
        }
        for (int i = 0; i < sellList.size(); i++) {
            if (data.getKey() == sellList.get(i).getKey()) {
                return sellList.get(i);
            }
        }

        return null;
    }
}
