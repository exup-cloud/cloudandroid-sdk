package com.bmtc.sdk.contract.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractAdapter;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

public class USDTContractFragment extends BaseFragment implements
        View.OnClickListener{

    private View m_RootView;
    private RelativeLayout mNetworkRl;


    private TextView mCurrentTv;
    private ImageView mCurrentSortIv;
    private TextView mChgTv;
    private ImageView mChgSortIv;
    private int mSortState = 0; //0 normal; 1 current down; 2 current up; 3 chg down; 4 chg up
    private Animation mRotate;

    private List<ContractTicker> mContractTickers = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ContractAdapter mContractAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private int mType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_contract_usdt, null);

        mNetworkRl = m_RootView.findViewById(R.id.rl_no_network);
        mNetworkRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        mRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.sl_array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());

        mCurrentTv = m_RootView.findViewById(R.id.tv_current);
        mCurrentSortIv = m_RootView.findViewById(R.id.iv_current_sort);
        mChgTv = m_RootView.findViewById(R.id.tv_chg);
        mChgSortIv = m_RootView.findViewById(R.id.iv_chg_sort);

        mCurrentTv.setOnClickListener(this);
        mCurrentSortIv.setOnClickListener(this);
        mChgTv.setOnClickListener(this);
        mChgSortIv.setOnClickListener(this);

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mContractAdapter.getItemCount()) {
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

        if (mContractAdapter == null) {
            mContractAdapter = new ContractAdapter(getActivity());
            mContractAdapter.setData(mContractTickers);
            mRecyclerView.setAdapter(mContractAdapter);
        } else {
            mRecyclerView.setAdapter(mContractAdapter);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.tv_current || id == R.id.iv_current_sort){
            if (mSortState == 0 || mSortState == 3 || mSortState == 4) {
                mSortState = 1;
                mChgSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_selected_choose);
                mRotate.setFillAfter(true);
                mCurrentSortIv.startAnimation(mRotate);
                setData(null, true);
            } else if (mSortState == 1) {
                mSortState = 2;
                mChgSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_selected_choose);
                mRotate.setFillAfter(false);
                mCurrentSortIv.startAnimation(mRotate);
                setData(null, true);
            } else if (mSortState == 2) {
                mSortState = 0;
                mChgSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mRotate.setFillAfter(false);
                mCurrentSortIv.startAnimation(mRotate);
                setData(null, true);
            }
        }else if(id == R.id.tv_chg || id == R.id.iv_chg_sort ){
            if (mSortState == 0 || mSortState == 1 || mSortState == 2) {
                //current down
                mSortState = 3;
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mChgSortIv.setImageResource(R.drawable.sl_icon_selected_choose);
                mRotate.setFillAfter(true);
                mChgSortIv.startAnimation(mRotate);
                setData(null, true);
            } else if (mSortState == 3){
                mSortState = 4;
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mChgSortIv.setImageResource(R.drawable.sl_icon_selected_choose);
                mRotate.setFillAfter(false);
                mChgSortIv.startAnimation(mRotate);
                setData(null, true);
            } else if (mSortState == 4) {
                mSortState = 0;
                mChgSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mRotate.setFillAfter(false);
                mChgSortIv.startAnimation(mRotate);
                setData(null, true);
            }
        }
    }

    private void sort(final int state) {
        if (mContractTickers == null || mContractTickers.size() == 0) {
            return;
        }

        Collections.sort(mContractTickers, new Comparator<ContractTicker>() {
            @Override
            public int compare(ContractTicker o1, ContractTicker o2) {
                {
                    if (state == 0) {
                        Contract c1 = LogicGlobal.getContract(o1.getInstrument_id());
                        Contract c2 = LogicGlobal.getContract(o2.getInstrument_id());
                        if (c1 == null || c2 == null) {
                            return 1;
                        }
                    } else if (state == 1) {
                        if (MathHelper.round(o1.getLast_px(), 6) >= MathHelper.round(o2.getLast_px(), 6)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (state == 2) {
                        if (MathHelper.round(o1.getLast_px(), 6) >= MathHelper.round(o2.getLast_px(), 6)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (state == 3) {
                        if (MathHelper.round(o1.getChange_rate(), 6) >= MathHelper.round(o2.getChange_rate(), 6)) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (state == 4) {
                        if (MathHelper.round(o1.getChange_rate(), 6) >= MathHelper.round(o2.getChange_rate(), 6)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }

                return 0;
            }
        });
    }

    private void setData(List<ContractTicker> list, boolean sortOnly) {

        if (!sortOnly) {
            if (list == null) {
                return;
            }

            mContractTickers.clear();
            mContractTickers.addAll(list);
        }

        sort(mSortState);

        if (mContractAdapter == null) {
            mContractAdapter = new ContractAdapter(getActivity());
        }

        mContractAdapter.setData(mContractTickers);
        mContractAdapter.notifyDataSetChanged();
    }
    private void removeContractTicker(List<ContractTicker> tickerList ,int id){
        int removeIndex = -1;
        if(tickerList!=null){
            for (int i =0 ; i< tickerList.size() ; i++){
                if(tickerList.get(i).getInstrument_id() == id){
                    removeIndex = id;
                    break;
                }
            }
        }

        if(removeIndex > 0){
            tickerList.remove(removeIndex);
        }
    }
    public void updateTicker(ContractTicker ticker) {
        if (mContractTickers == null || mContractTickers.size() <= 0) {
            return;
        }

        int position = 0;
        int changed = 0;
        if(ticker.getActionType()==4) {//插入
            LogicGlobal.sContractTickers.add(ticker);
        }else if(ticker.getActionType()==5) {//删除
            removeContractTicker(mContractTickers,ticker.getInstrument_id());
        }else if(ticker.getActionType()==2) {//更新
            for (int i=0; i<mContractTickers.size(); i++) {
                ContractTicker item = mContractTickers.get(i);
                if (item == null) {
                    continue;
                }

                if (ticker.getInstrument_id() == item.getInstrument_id()) {
                    if (ticker.getLast_px().compareTo(item.getLast_px()) > 0) {
                        changed = 1;
                    } else if (ticker.getLast_px().compareTo(item.getLast_px()) < 0) {
                        changed = 2;
                    }
                    mContractTickers.set(i, ticker);
                    position = i;
                    break;
                }
            }
        }


        if (mContractAdapter == null) {
            mContractAdapter = new ContractAdapter(getActivity());
        }

        mContractAdapter.setData(mContractTickers, changed, position);
        mContractAdapter.notifyItemChanged(position);
    }

    public void updateTicker(List<ContractTicker> list) {
        if (list == null) {
            return;
        }

        List<ContractTicker> tickers = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            ContractTicker item = list.get(i);
            if (item == null) {
                continue;
            }

            if (!item.isOnline()) {
                continue;
            }

            if (item.getBlock() == Contract.CONTRACT_BLOCK_USDT) {
                tickers.add(item);
            }
        }

        setData(tickers, false);
    }
}
