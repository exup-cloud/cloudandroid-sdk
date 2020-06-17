package com.bmtc.sdk.contract.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
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
import com.bmtc.sdk.contract.base.BaseFragment;
import com.bmtc.sdk.contract.view.ISticky;
import com.bmtc.sdk.contract.view.StickyItemDecoration;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.utils.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

public class CoinContractFragment extends BaseFragment implements
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_contract_coin, null);

        mNetworkRl = m_RootView.findViewById(R.id.rl_no_network);
        mNetworkRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        mRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.array_rotate);
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
        mRecyclerView.addItemDecoration(new StickyItemDecoration(getActivity(), new ISticky() {
            @Override
            public boolean isFirstPosition(int pos) {
                return pos==0||
                        (!mContractTickers.get(pos).isMain() && mContractTickers.get(pos-1).isMain());
            }

            @Override
            public String getGroupTitle(int pos) {
                return mContractTickers.get(pos).isMain() ? getString(R.string.sl_str_main) : getString(R.string.sl_str_new);
            }
        }));

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
        if(id ==  R.id.tv_current || id == R.id.iv_current_sort){
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
        }else if(id == R.id.tv_chg || id == R.id.iv_chg_sort){
            if (mSortState == 0 || mSortState == 1 || mSortState == 2) {
                //current down
                mSortState = 3;
                mCurrentSortIv.setImageResource(R.drawable.sl_icon_not_choose);
                mChgSortIv.setImageResource(R.drawable.sl_icon_selected_choose);
                mRotate.setFillAfter(true);
                mChgSortIv.startAnimation(mRotate);
                setData(null, true);
            } else if (mSortState == 3) {
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
                if (o1.isMain() && !o2.isMain()) {
                    return -1;
                }

                if (!o1.isMain() && o2.isMain()) {
                    return 1;
                }

                {
                    if (state == 0) {
                        Contract c1 = ContractPublicDataAgent.INSTANCE.getContract(o1.getInstrument_id());
                        Contract c2 = ContractPublicDataAgent.INSTANCE.getContract(o2.getInstrument_id());
                        if (c1 == null || c2 == null) {
                            return 1;
                        }
//                        if (c1.getRank() > c2.getRank()) {
//                            return 1;
//                        } else if (c1.getRank() < c2.getRank()) {
//                            return -1;
//                        } else {
//                            return 0;
//                        }
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

            if (item.getBlock() == Contract.CONTRACT_BLOCK_MAIN) {
                tickers.add(item);
            }

            if (item.getBlock() == Contract.CONTRACT_BLOCK_INNOVATION) {
                tickers.add(item);
            }
        }

        setData(tickers, false);
    }
}
