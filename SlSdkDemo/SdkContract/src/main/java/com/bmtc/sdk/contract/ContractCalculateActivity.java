package com.bmtc.sdk.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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


import com.bmtc.sdk.contract.fragment.ContractCalculateFragment;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class ContractCalculateActivity extends BaseActivity implements View.OnClickListener{


    private ImageView mBackIv;

    private TextView mContractType;
    private ImageView mSelContractType;

    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;

    private ContractCalculateFragment mPLCalculateFragment;
    private ContractCalculateFragment mLiquidationPriceFragment;
    private ContractCalculateFragment mProfitRateFragment;

    private TextView mPlCalculateTv;
    private TextView mLiquidationPriceTv;
    private TextView mProfitRateTv;

    private View mTabLine;
    private boolean mScrolling = false;

    private int mContractId;

    private ListView mContractListView;
    private PopupWindow mContractWindow;
    private DropContractAdapter mContractAdapter;
    private View mContractPopupView;
    private Animation mRotate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_contract_calculate);

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

        if (mPLCalculateFragment == null) {
            mPLCalculateFragment = new ContractCalculateFragment();
            mPLCalculateFragment.setType(0);
            mPLCalculateFragment.setContractId(mContractId);
        }

        if (mLiquidationPriceFragment == null) {
            mLiquidationPriceFragment = new ContractCalculateFragment();
            mLiquidationPriceFragment.setType(1);
            mLiquidationPriceFragment.setContractId(mContractId);
        }

        if (mProfitRateFragment == null) {
            mProfitRateFragment = new ContractCalculateFragment();
            mProfitRateFragment.setType(2);
            mProfitRateFragment.setContractId(mContractId);
        }

        mFragmentManager = getSupportFragmentManager();

        mPlCalculateTv = findViewById(R.id.pl_calculate_tv);
        mLiquidationPriceTv = findViewById(R.id.liquidation_price_tv);
        mProfitRateTv = findViewById(R.id.profit_rate_tv);
        mPlCalculateTv.setOnClickListener(this);
        mLiquidationPriceTv.setOnClickListener(this);
        mProfitRateTv.setOnClickListener(this);

        mTabLine = findViewById(R.id.tab_line);

        mViewPager.setAdapter(new SampleFragmentPagerAdapter(mFragmentManager));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mScrolling) {
                    refrushToolBarUnderLine(position, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                refrushToolBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrolling = state == 1 || state == 2;
            }
        });

        refrushToolBar(0);

        mTabLine.postDelayed(new Runnable() {
            @Override
            public void run() {
                refrushToolBarUnderLine(0, 0);
            }
        },200);
    }


    private void refrushToolBar(int pos){
        if (pos == 0){
            mPlCalculateTv.setTextColor(getResources().getColor(R.color.sl_colorTextSelector));
            mLiquidationPriceTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
            mProfitRateTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
        } else if(pos == 1){
            mPlCalculateTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
            mLiquidationPriceTv.setTextColor(getResources().getColor(R.color.sl_colorTextSelector));
            mProfitRateTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
        } else if(pos == 2){
            mPlCalculateTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
            mLiquidationPriceTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
            mProfitRateTv.setTextColor(getResources().getColor(R.color.sl_colorTextSelector));
        }
    }

    private int dp(final int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void refrushToolBarUnderLine(int pos, float offset){
        int nLeft = mTabLine.getLeft();
        int width = mTabLine.getWidth()/3;
        int nOffset = (int)((pos + offset) * width) + (width - dp(45))/2 - nLeft;
        mTabLine.scrollTo(-nOffset, 0);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.pl_calculate_tv){
            mViewPager.setCurrentItem(0);
        }else if(id == R.id.liquidation_price_tv){
            mViewPager.setCurrentItem(1);
        }else if(id == R.id.profit_rate_tv){
            mViewPager.setCurrentItem(2);
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        public SampleFragmentPagerAdapter(FragmentManager fm ) {
            super(fm);
            mFragments.add(mPLCalculateFragment);
            mFragments.add(mLiquidationPriceFragment);
            mFragments.add(mProfitRateFragment);
        }

        @Override
        public Fragment getItem(int i) {
            return  mFragments.get(i);
        }

        @Override
        public int getCount() {
            if(null == mFragments) return 0;
            return mFragments.size();
        }
    }

    private void updateData() {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract != null) {
            mContractType.setText(contract.getDisplayName(this));
        }

        if (mPLCalculateFragment != null) {
            mPLCalculateFragment.setContractId(mContractId);
        }

        if (mLiquidationPriceFragment != null) {
            mLiquidationPriceFragment.setContractId(mContractId);
        }

        if (mProfitRateFragment != null) {
            mProfitRateFragment.setContractId(mContractId);
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
