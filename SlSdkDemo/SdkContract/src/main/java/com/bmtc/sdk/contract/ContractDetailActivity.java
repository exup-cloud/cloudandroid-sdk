package com.bmtc.sdk.contract;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.fragment.ContractIntroduceFragment;
import com.bmtc.sdk.contract.fragment.FundsRateFragment;
import com.bmtc.sdk.contract.fragment.InsuranceFundFragment;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.base.BaseFragmentPagerAdapter;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractSpot;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicTimer;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class ContractDetailActivity extends BaseActivity implements
        LogicTimer.ITimerListener {

    private int mContractId;
    private int mPageIndex;

    private ImageView mBackIv;
    private TextView mTitleTv;
    private TextView mStockTypeTv;

    private TextView mHoldTv;
    private TextView mVolumeTv;
    private TextView mTrunoverTv;

    private RelativeLayout mLastDayRl;
    private TextView mLastDayTv;
    private RelativeLayout mBkgDayRl;
    private RelativeLayout mForeDayRl;
    private TextView mHighDayTv;
    private TextView mLowDayTv;

    private RelativeLayout mLast30DayRl;
    private TextView mLast30DayTv;
    private RelativeLayout mBkg30DayRl;
    private RelativeLayout mFore30DayRl;
    private TextView mHigh30DayTv;
    private TextView mLow30DayTv;

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private ContractIntroduceFragment mContractIntroduceFragment;
    private InsuranceFundFragment mInsuranceFundFragment;
    private FundsRateFragment mFundsRateFragment;

    private TabLayout mTabLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_contract_detail);

        LogicTimer.getInstance().registListener(this);
        try {
            mContractId = getIntent().getIntExtra("contract_id", 1);
            mPageIndex = getIntent().getIntExtra("page_index", 0);
        } catch (Exception ignored) {}

        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogicTimer.getInstance().unregistListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            mContractId = getIntent().getIntExtra("contract_id", 1);
            mPageIndex = getIntent().getIntExtra("page_index", 0);
        } catch (Exception ignored) {}

        super.onNewIntent(intent);
    }

    @Override
    public void setView() {
        super.setView();
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitleTv = findViewById(R.id.tv_title);
        mStockTypeTv = findViewById(R.id.tv_stock_type);
        mHoldTv = findViewById(R.id.tv_hold_value);
        mVolumeTv = findViewById(R.id.tv_volume_value);
        mTrunoverTv = findViewById(R.id.tv_swap_value);

        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        mLastDayRl = findViewById(R.id.rl_last_day);
        mLastDayTv = findViewById(R.id.tv_last_day);
        mBkgDayRl = findViewById(R.id.rl_bkg_day);
        mForeDayRl = findViewById(R.id.rl_fore_day);
        mHighDayTv = findViewById(R.id.tv_high_day);
        mLowDayTv = findViewById(R.id.tv_low_day);

        mLast30DayRl = findViewById(R.id.rl_last_30day);
        mLast30DayTv = findViewById(R.id.tv_last_30day);
        mBkg30DayRl = findViewById(R.id.rl_bkg_30day);
        mFore30DayRl = findViewById(R.id.rl_fore_30day);
        mHigh30DayTv = findViewById(R.id.tv_high_30day);
        mLow30DayTv = findViewById(R.id.tv_low_30day);

        if (mContractIntroduceFragment == null) {
            mContractIntroduceFragment = new ContractIntroduceFragment();
            mContractIntroduceFragment.setContractId(mContractId);
        }
        if (mInsuranceFundFragment == null) {
            mInsuranceFundFragment = new InsuranceFundFragment();
            mInsuranceFundFragment.setContractId(mContractId);
        }
        if (mFundsRateFragment == null) {
            mFundsRateFragment = new FundsRateFragment();
            mFundsRateFragment.setContractId(mContractId);
        }
        mFragments = new ArrayList<>();
        mFragments.add(mContractIntroduceFragment);
        mFragments.add(mInsuranceFundFragment);
        mFragments.add(mFundsRateFragment);

        String[] titles = new String[]{
                getString(R.string.sl_str_contract_info),
                getString(R.string.sl_str_insurance_fund),
                getString(R.string.sl_str_funds_rate),
        };

        BaseFragmentPagerAdapter adapter =
                new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments, titles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(mPageIndex);

        updateData();
    }

    public void updateData() {
        final Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        final ContractTicker ticker = LogicGlobal.getContractTicker(mContractId);
        if (ticker == null) {
            return;
        }

        final DecimalFormat df = NumberUtil.getDecimal(-1);

        String name = ticker.getName();
        if (name.contains("[")) {
            name = name.substring(0, name.indexOf("["));
        }
        mTitleTv.setText(name);

        if (contract.getArea() == Contract.CONTRACT_BLOCK_USDT) {
            mStockTypeTv.setText("USDT");
        } else if (contract.getArea() == Contract.CONTRACT_BLOCK_SIMULATION){
            mStockTypeTv.setText(R.string.sl_str_simulation);
        } else if (contract.getArea() == Contract.CONTRACT_BLOCK_INNOVATION || contract.getArea() == Contract.CONTRACT_BLOCK_MAIN){
            mStockTypeTv.setText(R.string.sl_str_inverse);
        }

        mHoldTv.setText(ticker.getPosition_size() + getString(R.string.sl_str_contracts_unit));
        mVolumeTv.setText(ticker.getQty_day() + getString(R.string.sl_str_contracts_unit));
        mTrunoverTv.setText(df.format(MathHelper.div(ticker.getQty_day(), ticker.getPosition_size(), 4)));

        mLastDayTv.setText(getString(R.string.sl_str_last) + df.format(MathHelper.round(ticker.getLast_px(), contract.getPrice_index())));
        mLowDayTv.setText(getString(R.string.sl_str_lowp) + df.format(MathHelper.round(ticker.getLow(), contract.getPrice_index())));
        mHighDayTv.setText(getString(R.string.sl_str_highp) + df.format(MathHelper.round(ticker.getHigh(), contract.getPrice_index())));

        mBkgDayRl.post(new Runnable() {
            @Override
            public void run() {

                double highDay = MathHelper.round(ticker.getHigh());
                double lowDay = MathHelper.round(ticker.getLow());
                double openDay = MathHelper.round(ticker.getOpen());
                double lastDay = MathHelper.round(ticker.getLast_px());

                int widthDay = mBkgDayRl.getMeasuredWidth(); // 获取宽度;
                int widthLastDay = mLastDayRl.getMeasuredWidth();

                int intervalDay = (int) (widthDay * (Math.abs(openDay - lastDay)) / (highDay - lowDay));
                int startDay = 0;
                if (openDay < lastDay) {
                    startDay = (int) (widthDay * (openDay - lowDay) / (highDay - lowDay));
                } else {
                    startDay = (int) (widthDay * (lastDay - lowDay) / (highDay - lowDay));
                }
                int lastPosDay = (int) (widthDay * (lastDay - lowDay) / (highDay - lowDay) - widthLastDay / 2);

                RelativeLayout.LayoutParams lp0 = (RelativeLayout.LayoutParams) mLastDayRl.getLayoutParams();
                lp0.setMargins(Math.min(widthDay - widthLastDay, Math.max(lastPosDay, 0)),0,0, UtilSystem.dip2px(ContractDetailActivity.this, 3));
                mLastDayRl.setLayoutParams(lp0);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(intervalDay, UtilSystem.dip2px(ContractDetailActivity.this, 20));
                lp.setMargins(startDay,0,0,0);
                lp.addRule(RelativeLayout.BELOW, R.id.rl_last_day);
                mForeDayRl.setLayoutParams(lp);
                mForeDayRl.setBackgroundColor(getResources().getColor(R.color.sl_colorTextSelector));
            }
        });

        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date dt1 = calendar.getTime();
        BTContract.getInstance().queryContractSpot(mContractId, dt1.getTime() / 1000, dt.getTime() / 1000, 1, "D", new IResponse<List<ContractSpot>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractSpot> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                if (data != null) {
                    double high = 0;
                    double low = Integer.MAX_VALUE;
                    for (int i=0; i<data.size(); i++) {
                        ContractSpot item = data.get(i);
                        if (item == null) {
                            continue;
                        }

                        double h = MathHelper.round(item.getHigh());
                        double l = MathHelper.round(item.getLow());
                        if (h > high) {
                            high = h;
                        }
                        if (l < low) {
                            low = l;
                        }
                    }

                    DecimalFormat df = NumberUtil.getDecimal(-1);
                    mLast30DayTv.setText(getString(R.string.sl_str_last) + df.format(MathHelper.round(ticker.getLast_px(), contract.getPrice_index())));
                    mLow30DayTv.setText(getString(R.string.sl_str_lowp) + df.format(MathHelper.round(low, contract.getPrice_index())));
                    mHigh30DayTv.setText(getString(R.string.sl_str_highp) + df.format(MathHelper.round(high, contract.getPrice_index())));

                    final double high30Day = high;
                    final double low30Day = low;
                    final double highDay = MathHelper.round(ticker.getHigh());
                    final double lowDay = MathHelper.round(ticker.getLow());
                    final double lastDay = MathHelper.round(ticker.getLast_px());

                    mBkg30DayRl.post(new Runnable() {
                        @Override
                        public void run() {


                            int width30Day = mBkg30DayRl.getMeasuredWidth(); // 获取宽度;
                            int widthLastDay = mLast30DayRl.getMeasuredWidth();

                            int intervalDay = (int) (width30Day * (Math.abs(highDay - lowDay)) / (high30Day - low30Day));
                            int startDay = (int) (width30Day * (lowDay - low30Day) / (high30Day - low30Day));

                            int lastPosDay = (int) (width30Day * (lastDay - low30Day) / (high30Day - low30Day) - widthLastDay / 2);

                            RelativeLayout.LayoutParams lp0 = (RelativeLayout.LayoutParams) mLast30DayRl.getLayoutParams();
                            lp0.setMargins(Math.min(width30Day - widthLastDay, Math.max(lastPosDay, 0)),0,0,UtilSystem.dip2px(ContractDetailActivity.this, 3));
                            mLast30DayRl.setLayoutParams(lp0);

                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(intervalDay, UtilSystem.dip2px(ContractDetailActivity.this, 20));
                            lp.setMargins(startDay,0,0,0);
                            lp.addRule(RelativeLayout.BELOW, R.id.rl_last_30day);
                            mFore30DayRl.setLayoutParams(lp);
                            mFore30DayRl.setBackgroundColor(getResources().getColor(R.color.sl_colorTextSelector));
                        }
                    });

                }

            }
        });
    }


    @Override
    public void onTimer(int times) {

    }

    @Override
    public void onBackPressed() {
        boolean isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        if (isVertical) {
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

}
