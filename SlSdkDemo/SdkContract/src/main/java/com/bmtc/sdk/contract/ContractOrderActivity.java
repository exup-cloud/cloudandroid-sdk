package com.bmtc.sdk.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bmtc.sdk.contract.fragment.ContractEntrustHistoryFragment;
import com.bmtc.sdk.contract.fragment.ContractOpenOrdersFragment;
import com.bmtc.sdk.contract.fragment.ContractPlanOrderFragment;
import com.bmtc.sdk.contract.fragment.ContractTradeRecordFragment;
import com.bmtc.sdk.contract.fragment.HoldContractHistoryFragment;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.base.BaseFragmentPagerAdapter;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.UtilSystem;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 合约交易记录
 * Created by zj on 2018/3/8.
 */

public class ContractOrderActivity extends BaseActivity {


    private ImageView mBackIv;

    private TextView mContractType;
    private ImageView mSelContractType;

    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;

    private ContractOpenOrdersFragment mContractOpenOrderFragment;
    private ContractEntrustHistoryFragment mContractEntrustHistoryFragment;
    private ContractPlanOrderFragment mContractPlanOrderFragment;
    private ContractPlanOrderFragment mContractPlanHistoryFragment;
    private ContractTradeRecordFragment mContractTradeRecordFragment;
    private HoldContractHistoryFragment mHoldContractHistoryFragment;

    private List<Fragment> mFragments;
    private SlidingTabLayout mTabLayout;


    private int mContractId;

    private ListView mContractListView;
    private PopupWindow mContractWindow;
    private DropContractAdapter mContractAdapter;
    private View mContractPopupView;
    private Animation mRotate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_contract_orders);

        try {
            mContractId = getIntent().getIntExtra("contractId", 0);
        } catch (Exception ignored) {}
        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        try {
            mContractId = getIntent().getIntExtra("contractId", 0);
        } catch (Exception ignored) {}
    }

    @Override
    public void setView() {
        super.setView();
        mRotate = AnimationUtils.loadAnimation(this, R.anim.array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mContractType = findViewById(R.id.tv_contract_type);
        mContractType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mSelContractType = findViewById(R.id.iv_sel_contract_type);
        mSelContractType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract != null) {
            mContractType.setText(contract.getDisplayName(this));
        }
        mViewPager = findViewById(R.id.vp_list);

        if (mContractOpenOrderFragment == null) {
            mContractOpenOrderFragment = new ContractOpenOrdersFragment();
            mContractOpenOrderFragment.setType(0);
            mContractOpenOrderFragment.setContractId(mContractId);
        }

        if (mContractEntrustHistoryFragment == null) {
            mContractEntrustHistoryFragment = new ContractEntrustHistoryFragment();
            mContractEntrustHistoryFragment.setType(0);
            mContractEntrustHistoryFragment.setContractId(mContractId);
        }

        if (mContractPlanOrderFragment == null) {
            mContractPlanOrderFragment = new ContractPlanOrderFragment();
            mContractPlanOrderFragment.setType(0);
            mContractPlanOrderFragment.setTab(0);
            mContractPlanOrderFragment.setContractId(mContractId);
        }

        if (mContractPlanHistoryFragment == null) {
            mContractPlanHistoryFragment = new ContractPlanOrderFragment();
            mContractPlanHistoryFragment.setType(0);
            mContractPlanHistoryFragment.setTab(1);
            mContractPlanHistoryFragment.setContractId(mContractId);
        }

        if (mContractTradeRecordFragment == null) {
            mContractTradeRecordFragment = new ContractTradeRecordFragment();
            mContractTradeRecordFragment.setContractId(mContractId);
        }

        if (mHoldContractHistoryFragment == null) {
            mHoldContractHistoryFragment = new HoldContractHistoryFragment();
            mHoldContractHistoryFragment.setContractId(mContractId);
        }

        mFragmentManager = getSupportFragmentManager();

        mTabLayout = findViewById(R.id.tab_layout);

        String[] titles = new String[]{
                getString(R.string.sl_str_open_orders),
                getString(R.string.sl_str_order_history),
                getString(R.string.sl_str_open_plan),
                getString(R.string.sl_str_plan_history),
                getString(R.string.sl_str_record),
                getString(R.string.sl_str_holdings_history)
        };

        mFragments = new ArrayList<>();
        mFragments.add(mContractOpenOrderFragment);
        mFragments.add(mContractEntrustHistoryFragment);
        mFragments.add(mContractPlanOrderFragment);
        mFragments.add(mContractPlanHistoryFragment);
        mFragments.add(mContractTradeRecordFragment);
        mFragments.add(mHoldContractHistoryFragment);

        BaseFragmentPagerAdapter adapter =
                new BaseFragmentPagerAdapter(mFragmentManager, mFragments, titles);

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(6);

        mTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);

    }


    private void updateData() {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract != null) {
            mContractType.setText(contract.getDisplayName(this));
        }

        if (mContractOpenOrderFragment != null) {
            mContractOpenOrderFragment.setContractId(mContractId, true);
        }

        if (mContractEntrustHistoryFragment != null) {
            mContractEntrustHistoryFragment.setContractId(mContractId, true);
        }

        if (mContractPlanOrderFragment != null) {
            mContractPlanOrderFragment.setContractId(mContractId, true);
        }

        if (mContractPlanHistoryFragment != null) {
            mContractPlanHistoryFragment.setContractId(mContractId, true);
        }

        if (mContractTradeRecordFragment != null) {
            mContractTradeRecordFragment.setContractId(mContractId);
        }

        if (mHoldContractHistoryFragment != null) {
            mHoldContractHistoryFragment.setContractId(mContractId);
        }
    }

    private void showSpotWindow() {
        final List<Contract> contractBasics = LogicGlobal.getOnlineContractBasic();
        if (contractBasics == null || contractBasics.size() <= 0) {
            return;
        }

        Collections.sort(contractBasics, new Comparator<Contract>() {
            @Override
            public int compare(Contract c1, Contract c2) {
                if (c1.getInstrument_id() > c2.getInstrument_id()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        if (mContractWindow != null && mContractWindow.isShowing()) {
            mContractWindow.dismiss();
        }

        int itemAccountId = R.layout.sl_item_drop_text;
        mContractAdapter = new DropContractAdapter(this, itemAccountId, contractBasics);

        mContractPopupView = LayoutInflater.from(this).inflate(R.layout.sl_view_dropdown, null);
        mContractListView = mContractPopupView.findViewById(R.id.lv_list);
        mContractListView.setAdapter(mContractAdapter);
        mContractListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mContractWindow != null) {

                    mContractId = contractBasics.get(i).getInstrument_id();
                    updateData();
                    mContractWindow.dismiss();
                }
            }
        });

        int listPadding = UtilSystem.dip2px(this, 5);
        int itemHeight = UtilSystem.dip2px(this, 40);
        int windowHeight = itemHeight * contractBasics.size() + listPadding * 2;
        int min = itemHeight + listPadding * 2;
        int max = itemHeight * 10;
        if (windowHeight > max) {
            windowHeight = max;
        } else if (windowHeight < min) {
            windowHeight = min;
        }

        mContractWindow = new PopupWindow(mContractPopupView, (int)(mContractType.getWidth() * 1.5), windowHeight);
        mContractWindow.setOutsideTouchable(true);
        mContractWindow.setBackgroundDrawable(new BitmapDrawable());
        mContractWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mRotate.setFillAfter(false);
                mSelContractType.startAnimation(mRotate);
            }
        });

        mRotate.setFillAfter(true);
        mSelContractType.startAnimation(mRotate);

        mContractWindow.setFocusable(true);
        mContractWindow.showAsDropDown(mContractType, -mContractType.getWidth()/2, 2);
    }

    class DropContractAdapter extends ArrayAdapter<Contract> {

        private Context mContext;
        private int mResId;
        private List<Contract> mItems;

        public DropContractAdapter(Context context, int textViewResourceId, List<Contract> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropContractAdapter.DropSpotViewHolder holder;
            if (convertView == null) {
                holder = new DropContractAdapter.DropSpotViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvStockCode = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropContractAdapter.DropSpotViewHolder)convertView.getTag();
            if (getItem(position) != null) {
                holder.tvStockCode.setText(getItem(position).getDisplayName(mContext));
            }
            return convertView;
        }

        class DropSpotViewHolder {
            TextView tvStockCode;
        }
    }
}
