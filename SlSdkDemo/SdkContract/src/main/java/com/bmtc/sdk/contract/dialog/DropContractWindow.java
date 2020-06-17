package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.ContractDropAdapter;
import com.bmtc.sdk.contract.uiLogic.LogicCollects;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractTicker;

import java.util.ArrayList;
import java.util.List;

public class DropContractWindow extends PopupWindow implements ContractDropAdapter.OnContractDropClickedListener{

    private RadioButton mTabUSDT, mTabMain, mTabSimulation, mTabOptional;
    private int mTab = 0;

    private List<ContractTicker> mContractTickers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ContractDropAdapter mContractAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private Context context;

    private OnDropClickedListener mListener;

    public interface OnDropClickedListener {
        void onContractDropClick(int contractId);
    }

    public DropContractWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_view_dropdown_contract, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //this.setAnimationStyle(R.style.popwin_anim_style);

        //this.setAnimationStyle(R.style.PopWinAnim);
        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        //backgroundAlpha(1f);

        //添加pop窗口关闭事件
        //this.setOnDismissListener(new poponDismissListener());
        initView(rootView);
    }


    private void initView(View view) {

        mTabOptional = view.findViewById(R.id.tab_optional);
        mTabOptional.setVisibility(LogicCollects.getInstance().hasContractTicker() ? View.VISIBLE:View.GONE);
        mTabOptional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = 5;
                mTabUSDT.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabMain.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
                mTabSimulation.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabOptional.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
                updateData();
            }
        });

        mTabUSDT = view.findViewById(R.id.tab_usdt);
        mTabUSDT.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
        mTabUSDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = 0;
                mTabUSDT.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
                mTabMain.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabSimulation.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabOptional.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                updateData();
            }
        });

        mTabMain = view.findViewById(R.id.tab_inverse);
        mTabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = 1;
                mTabUSDT.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabMain.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
                mTabSimulation.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabOptional.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                updateData();
            }
        });

        mTabSimulation = view.findViewById(R.id.tab_simulation);
        mTabSimulation.setVisibility(ContractPublicDataAgent.INSTANCE.getSimulationContract().size()>0 ? View.VISIBLE:View.INVISIBLE);
        mTabSimulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = 3;
                mTabUSDT.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabMain.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                mTabSimulation.setBackgroundColor(context.getResources().getColor(R.color.sl_colorDefault));
                mTabOptional.setBackgroundColor(context.getResources().getColor(R.color.sl_transparent));
                updateData();
            }
        });
        mRecyclerView = view.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (mContractAdapter == null) {
            mContractAdapter = new ContractDropAdapter(context, this);
            mContractAdapter.setData(mContractTickers);
            mRecyclerView.setAdapter(mContractAdapter);
        } else {
            mRecyclerView.setAdapter(mContractAdapter);
        }

        updateData();
    }

    private void setData(List<ContractTicker> list, boolean sortOnly) {
        if (list == null) {
            return;
        }

        mContractTickers.clear();
        mContractTickers.addAll(list);

        sort();

        if (mContractAdapter == null) {
            mContractAdapter = new ContractDropAdapter(context, this);
        }

        mContractAdapter.setData(mContractTickers);
        mContractAdapter.notifyDataSetChanged();
    }

    private void sort() {
//        if (mContractTickers == null || mContractTickers.size() == 0) {
//            return;
//        }
//
//        Collections.sort(mContractTickers, new Comparator<ContractTicker>() {
//            @Override
//            public int compare(ContractTicker o1, ContractTicker o2) {
//
//                Contract c1 = LogicGlobal.getContract(o1.getContract_id());
//                Contract c2 = LogicGlobal.getContract(o2.getContract_id());
//                if (c1 == null || c2 == null) {
//                    return 1;
//                }
//                if (c1.getRank() > c2.getRank()) {
//                    return 1;
//                } else {
//                    return -1;
//                }
//            }
//        });
    }

    private void updateData() {


        List<ContractTicker> data = ContractPublicDataAgent.INSTANCE.getContractTickers();
        if (data != null) {
            List<ContractTicker> tickers = new ArrayList<>();
            for (int i=0; i<data.size(); i++) {
                ContractTicker item = data.get(i);
                if (item == null) {
                    continue;
                }

                if (mTab == 0) {
                    if (item.getBlock() == Contract.CONTRACT_BLOCK_USDT) {
                        tickers.add(item);
                    }
                } else if (mTab == 1) {
                    if (item.getBlock() == Contract.CONTRACT_BLOCK_MAIN || item.getBlock() == Contract.CONTRACT_BLOCK_INNOVATION) {
                        tickers.add(item);
                    }
                } else if (mTab == 3) {
                    if (item.getBlock() == Contract.CONTRACT_BLOCK_SIMULATION) {
                        tickers.add(item);
                    }
                } else if (mTab == 5) {
                    if (LogicCollects.getInstance().get(item.getSymbol()) != null) {
                        tickers.add(item);
                    }
                }
            }
            setData(tickers, false);
        }
    }

    public void setOnContractDropClick(OnDropClickedListener listener) {
        mListener = listener;
    }
    @Override
    public void onContractDropClick(int contractId) {
        if (mListener != null) {
            mListener.onContractDropClick(contractId);
        }
    }
}
