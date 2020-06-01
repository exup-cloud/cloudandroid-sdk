package com.bmtc.sdk.contract.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.ContractSettingActivity;
import com.bmtc.sdk.contract.FundsTransferActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.SelectLeverageActivity;
import com.bmtc.sdk.contract.adapter.BuySellContractAdapter;
import com.bmtc.sdk.contract.dialog.ContractPlanConfirmWindow;
import com.bmtc.sdk.contract.dialog.ContractPriceIntroduceWindow;
import com.bmtc.sdk.contract.dialog.ContractTradeConfirmWindow;
import com.bmtc.sdk.contract.dialog.FundsRateWindow;
import com.bmtc.sdk.contract.view.LoadingButton;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.base.BaseFragmentPagerAdapter;
import com.bmtc.sdk.library.common.dialog.PromptWindow;
import com.bmtc.sdk.library.common.pswkeyboard.OnPasswordInputFinish;
import com.bmtc.sdk.library.common.pswkeyboard.widget.PopEnterPassword;
import com.bmtc.sdk.library.common.pswkeyboard.widget.PopEnterSetPassword;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.contract.AdvanceOpenCost;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.trans.data.ContractPosition;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.trans.data.Depth;
import com.bmtc.sdk.library.trans.data.DepthData;
import com.bmtc.sdk.library.trans.data.SpotCoin;
import com.bmtc.sdk.library.uilogic.LogicBuySell;
import com.bmtc.sdk.library.uilogic.LogicContractSetting;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicLanguage;
import com.bmtc.sdk.library.uilogic.LogicSDKState;
import com.bmtc.sdk.library.uilogic.LogicUserState;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NoDoubleClickUtils;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.PreferenceManager;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by zj on 2018/3/1.
 */

public class BuySellContractFragment extends BaseFragment implements
        BuySellContractAdapter.OnBuySellContractClickedListener,
        LogicUserState.IUserStateListener,
        LogicBuySell.IBuySellListener,
        LogicContractSetting.IContractSettingListener{

    private View m_RootView;
    private AppBarLayout mAppBarLayout;

    private RadioGroup mOrderTypeRg;
    private RadioButton mTabMarketPrice, mTabBidPriceP1, mTabAskPriceP1;
    public RadioButton getMarketPriceTab() { return mTabMarketPrice; }
    private int mOperationType = 1; //1open; 2close

    private TextView mOrderTypeTv;
    public TextView getOrderTypeTv() { return mOrderTypeTv; }
    private ImageView mSelOrderTypeIv;

    private RelativeLayout mLimitPriceRl;
    private RelativeLayout mMarketPriceRl;
    private TextView mMarketPriceTv;
    private TextView mPrice2LimitTv;
    private RelativeLayout mTriggerPriceRl;

    private ImageView mPlanSettingIv;
    private TextView mTriggerPriceUnitTv;
    private EditText mTriggerPriceEt;

    private TextView mPrice2MarketTv;
    private TextView mPriceUnitTv;
    private EditText mPriceEt;
    private TextView mPriceValueTv;

    private RelativeLayout mLeverageRl;
    private TextView mLeverageTv;
    private int mLeverage = 10;

    private TextView mVolumeUnitTv;
    private EditText mVolumeEt;
    private TextView mVolumeValueTv;

    private LoadingButton mBuyBtn;
    private LoadingButton mSellBtn;

    private TextView mAavlValueTv;
    private TextView mFundsTransferTv;
    private ImageView mFundsTransferIv;

    private TextView mIndexPriceTv;
    private TextView mFairPriceTv;
    private ImageView mFairPriceIv;
    private ImageView mPriceIntroIv;
    private TextView mFundsRateTv;
    private ImageView mFundsRateIv;

    private TextView mLongIntro1;
    private TextView mLongValue1;
    private TextView mLongIntro2;
    private TextView mLongValue2;

    private TextView mShortIntro1;
    private TextView mShortValue1;
    private TextView mShortIntro2;
    private TextView mShortValue2;

    private Depth mDepth = new Depth();

    private RecyclerView mSellRv;
    private BuySellContractAdapter mSellAdapter;
    private RecyclerView mBuyRv;
    private BuySellContractAdapter mBuyAdapter;

    private TextView mPriceTv;
    private TextView mAmountTv;

    private int mABShowNum = 6;
    private int mContractId = 1;
    private String mCurrentPrice;
    private String mIndexPrice;
    private String mTagPrice;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private ContractOpenOrdersFragment mContractOpenOrderFragment;
    private ContractEntrustHistoryFragment mContractEntrustHistoryFragment;
    private ContractPlanOrderFragment mContractPlanOrderFragment;
    private HoldContractNowFragment mHoldContractNowFragment;
    private Object mLock = new Object();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_buysell_contract, null);

        LogicBuySell.getInstance().registListener(this);
        LogicUserState.getInstance().registListener(this);
        LogicContractSetting.getInstance().registListener(this);
        setView();

        return m_RootView;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable == mTriggerPriceEt.getEditableText()) {
                updateTriggerPrice();
            }

            if (editable == mPriceEt.getEditableText()) {
                updatePrice();
            }
            if (editable == mVolumeEt.getEditableText()) {
                updateVol();
            }
        }
    };

    private void setView() {

        if (mContractOpenOrderFragment == null) {
            mContractOpenOrderFragment = new ContractOpenOrdersFragment();
            mContractOpenOrderFragment.setType(1);
            mContractOpenOrderFragment.setContractId(0);
        }

        if (mContractEntrustHistoryFragment == null) {
            mContractEntrustHistoryFragment = new ContractEntrustHistoryFragment();
            mContractEntrustHistoryFragment.setType(1);
            mContractEntrustHistoryFragment.setContractId(0);
        }

        if (mContractPlanOrderFragment == null) {
            mContractPlanOrderFragment = new ContractPlanOrderFragment();
            mContractPlanOrderFragment.setType(1);
            mContractPlanOrderFragment.setContractId(0);
        }

        if (mHoldContractNowFragment == null) {
            mHoldContractNowFragment = new HoldContractNowFragment();
            mHoldContractNowFragment.setType(1);
            mHoldContractNowFragment.setContractId(0);
        }

        mViewPager = m_RootView.findViewById(R.id.viewpager);
        mTabLayout = m_RootView.findViewById(R.id.tabs);
        mFragments = new ArrayList<>();
        mFragments.add(mContractOpenOrderFragment);
        mFragments.add(mContractEntrustHistoryFragment);
        mFragments.add(mContractPlanOrderFragment);
        mFragments.add(mHoldContractNowFragment);

        String[] titles = new String[]{
                getString(R.string.sl_str_open_orders),
                getString(R.string.sl_str_order_history),
                getString(R.string.sl_str_plan_entrust),
                getString(R.string.sl_str_holdings_now),
        };

        BaseFragmentPagerAdapter adapter =
                new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, titles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);

        mAppBarLayout = m_RootView.findViewById(R.id.appbar);
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();

                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return true;
                    }
                });
            }
        });

        mOrderTypeRg = m_RootView.findViewById(R.id.rg_order_type);
        mTabMarketPrice = m_RootView.findViewById(R.id.tab_market_price);
        mTabMarketPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOrderType() == LogicBuySell.ORDER_TYPE_MARKET) {
                    setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true);
                } else {
                    setOrderType(LogicBuySell.ORDER_TYPE_MARKET, true);
                }
            }
        });

        mTabBidPriceP1 = m_RootView.findViewById(R.id.tab_buy1);
        mTabBidPriceP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOrderType() == LogicBuySell.ORDER_BID_PRICE) {
                    setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true);
                } else {
                    setOrderType(LogicBuySell.ORDER_BID_PRICE, true);
                }
            }
        });

        mTabAskPriceP1 = m_RootView.findViewById(R.id.tab_sell1);
        mTabAskPriceP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOrderType() == LogicBuySell.ORDER_ASK_PRICE) {
                    setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true);
                } else {
                    setOrderType(LogicBuySell.ORDER_ASK_PRICE, true);
                }
            }
        });

        mLimitPriceRl = m_RootView.findViewById(R.id.rl_limit_price);
        mMarketPriceRl = m_RootView.findViewById(R.id.rl_market_price);
        mMarketPriceTv = m_RootView.findViewById(R.id.tv_market_price);
        mTriggerPriceRl = m_RootView.findViewById(R.id.rl_trigger_price);

        mOrderTypeTv = m_RootView.findViewById(R.id.tv_order_type);
        mOrderTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicBuySell.getInstance().showOrderTypeWindow(getActivity(), mOrderTypeTv, false, true);
            }
        });

        mSelOrderTypeIv = m_RootView.findViewById(R.id.iv_sel_order_type);
        mSelOrderTypeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicBuySell.getInstance().showOrderTypeWindow(getActivity(), mOrderTypeTv, false, true);
            }
        });

        mPlanSettingIv = m_RootView.findViewById(R.id.iv_setting);
        mPlanSettingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PromptWindow window = new PromptWindow(getActivity());
                window.showTitle(getString(R.string.sl_str_plan_entrust));
                window.showTvContent(getString(R.string.sl_str_plan_entrust_intro));
                window.showBtnOk(getString(R.string.sl_str_go_setting));
                window.showBtnClose("");
                window.showBtnCancel(getString(R.string.sl_str_cancel));
                window.showAtLocation(mFundsRateIv, Gravity.CENTER, 0, 0);
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        Intent intent = new Intent(getActivity(), ContractSettingActivity.class);
                        startActivity(intent);

                    }
                });
                window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
                window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
            }
        });
        mTriggerPriceEt = m_RootView.findViewById(R.id.et_trigger_price);
        mTriggerPriceEt.addTextChangedListener(mTextWatcher);
        mTriggerPriceUnitTv = m_RootView.findViewById(R.id.tv_trigger_price_unit);

        mPriceEt = m_RootView.findViewById(R.id.et_price);
        mPriceEt.addTextChangedListener(mTextWatcher);
        mPriceUnitTv = m_RootView.findViewById(R.id.tv_price_unit);
        mPrice2MarketTv = m_RootView.findViewById(R.id.tv_price2market);
        mPrice2MarketTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicContractSetting.setExecution(LogicGlobal.sContext, 1);
                setOrderType(LogicBuySell.ORDER_TYPE_PLAN, false);
            }
        });

        mPrice2LimitTv = m_RootView.findViewById(R.id.tv_price2limit);
        mPrice2LimitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicContractSetting.setExecution(LogicGlobal.sContext, 0);
                setOrderType(LogicBuySell.ORDER_TYPE_PLAN, false);
            }
        });

        mVolumeEt = m_RootView.findViewById(R.id.et_volume);
        mVolumeEt.setTag(0);
        mVolumeEt.addTextChangedListener(mTextWatcher);
        mVolumeUnitTv = m_RootView.findViewById(R.id.tv_volume_unit);


        mPriceValueTv = m_RootView.findViewById(R.id.tv_price_value);
        mVolumeValueTv = m_RootView.findViewById(R.id.tv_volume_value);
        mAavlValueTv = m_RootView.findViewById(R.id.tv_aavl_value);

        mLeverageTv = m_RootView.findViewById(R.id.tv_select_leverage);

        mLeverageRl = m_RootView.findViewById(R.id.rl_leverage);
        mLeverageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage();
            }
        });


        mFundsTransferTv = m_RootView.findViewById(R.id.tv_funds_transfer);
        mFundsTransferTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTransfer();
            }
        });
        mFundsTransferIv = m_RootView.findViewById(R.id.iv_funds_transfer);
        mFundsTransferIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTransfer();
            }
        });

        mBuyBtn = m_RootView.findViewById(R.id.btn_buy);
        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    doBuy();
                }
            }
        });

        mSellBtn = m_RootView.findViewById(R.id.btn_sell);
        mSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    doSell();
                }
            }
        });

        if (SLSDKAgent.slUser!=null) {
            mBuyBtn.setText(mOperationType == 1 ? getString(R.string.sl_str_buy_open_long) : getString(R.string.sl_str_buy_close_short));
            mSellBtn.setText(mOperationType == 1 ? getString(R.string.sl_str_sell_open_short) : getString(R.string.sl_str_sell_close_long));
        } else {
            mBuyBtn.setText(getString(R.string.sl_str_login));
            mSellBtn.setText(getString(R.string.sl_str_login));
        }

        mIndexPriceTv = m_RootView.findViewById(R.id.tv_index_price_value);
        mFairPriceTv = m_RootView.findViewById(R.id.tv_fair_price_value);
        mFairPriceIv = m_RootView.findViewById(R.id.iv_fair_price_value);
        mFairPriceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tips = String.format(getString(R.string.sl_str_fair_price_intro));

                SpannableStringBuilder ssb= new SpannableStringBuilder(tips);
                int length = tips.length();
                int begin = tips.indexOf(" ");

                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sl_colorBlack)), 0, begin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sl_colorTextSelector)),begin, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                final PromptWindow window = new PromptWindow(getActivity());
                window.showTitle(getString(R.string.sl_str_fair_price));
                window.showTvContent(ssb);
                window.showBtnOk(getString(R.string.sl_str_isee));
                window.showBtnClose("");
                window.showAtLocation(mFundsRateIv, Gravity.CENTER, 0, 0);
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
                window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
            }
        });
        mPriceIntroIv = m_RootView.findViewById(R.id.iv_price_intro);
        mPriceIntroIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecimalFormat dfPrice = NumberUtil.getDecimal(2);
                final ContractPriceIntroduceWindow window = new ContractPriceIntroduceWindow(getActivity());
                window.showPrice(dfPrice.format(MathHelper.round(mCurrentPrice)),  dfPrice.format(MathHelper.round(mIndexPrice)) + "/" + dfPrice.format(MathHelper.round(mTagPrice)));
                window.showBtnClose();
                window.showAtLocation(mBuyBtn, Gravity.CENTER, 0, 0);
                window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
            }
        });

        mFundsRateTv = m_RootView.findViewById(R.id.tv_funds_rate_value);
        mFundsRateIv = m_RootView.findViewById(R.id.iv_funds_rate_value);
        mFundsRateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FundsRateWindow window = new FundsRateWindow(getActivity());
                window.setContractId(mContractId);
                window.updateData();
                window.showAtLocation(mFundsRateIv, Gravity.CENTER, 0, 0);

            }
        });

        mLongIntro1 = m_RootView.findViewById(R.id.tv_long_intro1);
        mLongValue1 = m_RootView.findViewById(R.id.tv_long_value1);
        mLongIntro2 = m_RootView.findViewById(R.id.tv_long_intro2);
        mLongValue2 = m_RootView.findViewById(R.id.tv_long_value2);
        mShortIntro1 = m_RootView.findViewById(R.id.tv_short_intro1);
        mShortValue1 = m_RootView.findViewById(R.id.tv_short_value1);
        mShortIntro2 = m_RootView.findViewById(R.id.tv_short_intro2);
        mShortValue2 = m_RootView.findViewById(R.id.tv_short_value2);

        LinearLayoutManager llmSell = new LinearLayoutManager(getActivity());
        llmSell.setOrientation(LinearLayoutManager.VERTICAL);
        mSellRv = m_RootView.findViewById(R.id.rv_list_sell);
        mSellRv.setLayoutManager(llmSell);

        LinearLayoutManager llmBuy = new LinearLayoutManager(getActivity());
        llmBuy.setOrientation(LinearLayoutManager.VERTICAL);
        mBuyRv = m_RootView.findViewById(R.id.rv_list_buy);
        mBuyRv.setLayoutManager(llmBuy);

        LinearLayoutManager llmTrade = new LinearLayoutManager(getActivity());
        llmTrade.setOrientation(LinearLayoutManager.VERTICAL);

        mPriceTv = m_RootView.findViewById(R.id.tv_price);
        mAmountTv = m_RootView.findViewById(R.id.tv_amount);

        int order_type = getOrderType();
        setOrderType(order_type, true);

        int askbid = PreferenceManager.getInstance(getActivity()).getSharedInt(PreferenceManager.PREF_ASKBID, 0);
        setAskBid(askbid);

        BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractAccount> data) {
                updateUserAsset();
                updateInfoValue(true);
            }
        });
    }

    public void gotoTop() {
        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(0);
            }
        }
    }

    public void switchBuySell(int type, RadioButton tabOpen, RadioButton tabClose) {
        mOperationType = type;
        if (tabOpen == null || tabClose == null ||
                mBuyBtn == null || mSellBtn == null ||
                mLongIntro1 == null || mLongIntro2 == null ||
                mLongValue1 == null || mLongValue2 == null ||
                mShortIntro1 == null || mShortIntro2 == null ||
                mShortValue1 == null || mShortValue2 == null ||
                mVolumeEt == null || mPriceEt == null ||
                mAavlValueTv == null || mVolumeValueTv == null
                ) {
            return;
        }

        if (mOperationType == 1) {
            tabOpen.setChecked(true);

            if (SLSDKAgent.slUser!=null) {
                mBuyBtn.setText(getString(R.string.sl_str_buy_open_long));
                mSellBtn.setText(getString(R.string.sl_str_sell_open_short));
            } else {
                mBuyBtn.setText(getString(R.string.sl_str_login));
                mSellBtn.setText(getString(R.string.sl_str_login));
            }
            mBuyBtn.setEnabled(true);
            mSellBtn.setEnabled(true);

            mLongIntro1.setText(getString(R.string.sl_str_entrust_cost));
            mLongValue1.setText("--");
            mLongIntro2.setText(getString(R.string.sl_str_buy_open_up_to));
            mLongValue2.setText("--");

            mShortIntro1.setText(getString(R.string.sl_str_entrust_cost));
            mShortValue1.setText("--");
            mShortIntro2.setText(getString(R.string.sl_str_sell_open_up_to));
            mShortValue2.setText("--");
            mLeverageRl.setVisibility(View.VISIBLE);

        } else if (mOperationType == 2) {
            tabClose.setChecked(true);

            if (SLSDKAgent.slUser!=null) {
                mBuyBtn.setText(getString(R.string.sl_str_buy_close_short));
                mSellBtn.setText(getString(R.string.sl_str_sell_close_long));
            } else {
                mBuyBtn.setText(getString(R.string.sl_str_login));
                mSellBtn.setText(getString(R.string.sl_str_login));
            }
          //  mBuyBtn.setEnabled(false);
          //  mSellBtn.setEnabled(false);

            mLongIntro1.setText(getString(R.string.sl_str_short_position));
            mLongValue1.setText("--");
            mLongIntro2.setText(getString(R.string.sl_str_sell_close_up_to));
            mLongValue2.setText("--");

            mShortIntro1.setText(getString(R.string.sl_str_long_position));
            mShortValue1.setText("--");
            mShortIntro2.setText(getString(R.string.sl_str_buy_close_up_to));
            mShortValue2.setText("--");
            mLeverageRl.setVisibility(View.GONE);
        }

        mVolumeEt.setText("");
        updateUserAsset();
        updateInfoValue(true);
    }

    private void updateTriggerPrice() {
        if (mPriceEt == null && mVolumeEt == null || mPriceValueTv == null) {
            return;
        }

        String price = mTriggerPriceEt.getText().toString();
        price = price.replace(",", ".");

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        String price_unit = contract.getPx_unit();
        if (price_unit.contains(".")) {
            int price_index = price_unit.length() - price_unit.indexOf(".") - 1;
            if (price_index == 1) {
                price_index = 0;
            }

            if (price.contains(".")) {
                int index = price.indexOf(".");
                if (index + price_index < price.length()) {
                    price = price.substring(0, index + price_index);
                    mTriggerPriceEt.setText(price);
                    mTriggerPriceEt.setSelection(price.length());
                }
            }
        } else {
            if (price.contains(".")) {
                price = price.replace(".", "");
                mTriggerPriceEt.setText(price);
                mTriggerPriceEt.setSelection(price.length());
            }
        }
    }

    private void updatePrice() {
        if (mPriceEt == null || mVolumeEt == null || mPriceValueTv == null) {
            return;
        }

        String price = mPriceEt.getText().toString();
        price = price.replace(",", ".");
        if (TextUtils.isEmpty(price) || mContractId == 0) {
            mPriceValueTv.setText("0");
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        String price_unit = contract.getPx_unit();
        if (price_unit.contains(".")) {
            int price_index = price_unit.length() - price_unit.indexOf(".") - 1;
            if (price_index == 1) {
                price_index = 0;
            }

            if (price.contains(".")) {
                int index = price.indexOf(".");
                if (index + price_index < price.length()) {
                    price = price.substring(0, index + price_index);
                    mPriceEt.setText(price);
                    mPriceEt.setSelection(price.length());
                }
            }
        } else {
            if (price.contains(".")) {
                price = price.replace(".", "");
                mPriceEt.setText(price);
                mPriceEt.setSelection(price.length());
            }
        }


        if (price.equals(".") || TextUtils.isEmpty(price)) {
            price = "0";
        }

        double current = MathHelper.round(price, 8);

        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
//        double backUsd = MathHelper.round(LogicGlobal.sGlobalData.getCoin_price_usd(contract.getQuote_coin()), 6);
////        double backCny = backUsd * LogicGlobal.sUsdRateCNY;
////        double current_usd = MathHelper.round(MathHelper.mul(backUsd, current), 2);
////        double current_cny = MathHelper.round(MathHelper.mul(backCny, current), 2);
////        String sUsd = "≈$" + decimalFormat.format(current_usd);
////        String sCNY = "≈￥"+ decimalFormat.format(current_cny);
////        String coin_base = LogicLanguage.isZhEnv(getActivity()) ? sCNY : sUsd;
////
////        mPriceValueTv.setText(coin_base);

        String vol = mVolumeEt.getText().toString();
        vol = vol.replace(",", ".");
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            vol = "0";
        }

        updateInfoValue(true);
    }

    private void updateVol() {
        if (mPriceEt == null || mVolumeEt == null) {
            return;
        }
        String vol = mVolumeEt.getText().toString();
        vol = vol.replace(",", ".");
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            mVolumeValueTv.setText("0");
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int unit = LogicContractSetting.getContractUint(LogicGlobal.sContext);

        if (unit == 0) {
            String vol_unit = contract.getQty_unit();
            if (vol_unit.contains(".")) {
                int vol_index = vol_unit.length() - vol_unit.indexOf(".");
                if (vol.contains(".")) {
                    int index = vol.indexOf(".");
                    if (index + vol_index < vol.length()) {
                        vol = vol.substring(0, index + vol_index);
                        mVolumeEt.setText(vol);
                        mVolumeEt.setSelection(vol.length());
                    }
                }
            } else {
                if (vol.contains(".")) {
                    vol = vol.replace(".", "");
                    mVolumeEt.setText(vol);
                    mVolumeEt.setSelection(vol.length());
                }
            }
        } else {
            String base_coin_unit = "0.0001";
            SpotCoin spotCoin = null ;//= LogicGlobal.sGlobalData.getSpotCoin(contract.getBase_coin());
            if (spotCoin != null) {
                base_coin_unit = spotCoin.getVol_unit();
            }
            if (base_coin_unit.contains(".")) {
                int vol_index = base_coin_unit.length() - base_coin_unit.indexOf(".");
                if (vol.contains(".")) {
                    int index = vol.indexOf(".");
                    if (index + vol_index < vol.length()) {
                        vol = vol.substring(0, index + vol_index);
                        mVolumeEt.setText(vol);
                        mVolumeEt.setSelection(vol.length());
                    }
                }
            } else {
                if (vol.contains(".")) {
                    vol = vol.replace(".", "");
                    mVolumeEt.setText(vol);
                    mVolumeEt.setSelection(vol.length());
                }
            }
        }


        if (vol.equals(".") || TextUtils.isEmpty(vol)) {
            vol = "0";
        }

        String price = mPriceEt.getText().toString();
        price = price.replace(",", ".");
        if (TextUtils.isEmpty(price) || price.equals(".")) {
            price = "0";
        }

        updateInfoValue(false);
    }

    public void  updateInfoValue(boolean priceChange) {
        if (mPriceEt == null || mVolumeEt == null || mVolumeValueTv == null ||
                mLongValue1 == null || mLongValue2 == null||
                mShortValue1 == null || mShortValue2 == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        String price = mPriceEt.getText().toString();
        price = price.replace(",", ".");
        if (TextUtils.isEmpty(price) || price.equals(".")) {
            price = "0";
        }

        String vol = mVolumeEt.getText().toString();
        vol = vol.replace(",", ".");
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            vol = "0";
        }

        String value = ContractCalculate.CalculateContractBasicValue(
                ContractCalculate.trans2ContractVol(contract, vol, price),
                price,
                contract);

        mVolumeValueTv.setText("≈" + value);

        vol = ContractCalculate.trans2ContractVol(contract, vol, price);
        if (mOperationType == 1) {
            ContractOrder contractOrder = new ContractOrder();
            contractOrder.setLeverage(mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage);
            contractOrder.setQty(vol);
            if (getOrderType() == LogicBuySell.ORDER_TYPE_LIMIT) {
                contractOrder.setPx(price);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            } else if (getOrderType() == LogicBuySell.ORDER_TYPE_MARKET) {
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);
            }

            contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);
            AdvanceOpenCost longOpenCost = ContractCalculate.CalculateAdvanceOpenCost(
                    contractOrder,
                    BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG),
                    BTContract.getInstance().getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                    LogicGlobal.getContractBasic(mContractId));

            contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);
            AdvanceOpenCost shortOpenCost = ContractCalculate.CalculateAdvanceOpenCost(
                    contractOrder,
                    BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT),
                    BTContract.getInstance().getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                    LogicGlobal.getContractBasic(mContractId));

            mLongValue1.setText(longOpenCost == null ? "0" : longOpenCost.getFreezAssets() + contract.getMargin_coin());
            mShortValue1.setText(shortOpenCost == null ? "0" : shortOpenCost.getFreezAssets() + contract.getMargin_coin());

            if (!priceChange) {
                return;
            }
            ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
            if (contractAccount != null) {
                DecimalFormat dfVol = NumberUtil.getDecimal(contract.getValue_index());
                double longVolume = ContractCalculate.CalculateVolume(
                        dfVol.format(contractAccount.getAvailable_vol_real()),
                        mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage,
                        BTContract.getInstance().getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                        BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG),
                        getOrderType() == LogicBuySell.ORDER_TYPE_LIMIT ? price : mCurrentPrice,
                        ContractPosition.POSITION_TYPE_LONG,
                        LogicGlobal.getContractBasic(mContractId));

                double shortVolume = ContractCalculate.CalculateVolume(
                        dfVol.format(contractAccount.getAvailable_vol_real()),
                        mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage,
                        BTContract.getInstance().getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                        BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT),
                        getOrderType() == LogicBuySell.ORDER_TYPE_LIMIT ? price : mCurrentPrice,
                        ContractPosition.POSITION_TYPE_SHORT,
                        LogicGlobal.getContractBasic(mContractId));

                mLongValue2.setText(ContractCalculate.getVolUnit(contract, longVolume, MathHelper.round(price)));
                mShortValue2.setText(ContractCalculate.getVolUnit(contract, shortVolume, MathHelper.round(price)));
            } else {
                mLongValue2.setText("0");
                mShortValue2.setText("0");
            }
        } else if (mOperationType == 2) {
            DecimalFormat dfVol = NumberUtil.getDecimal(contract.getValue_index());
            ContractPosition longPosition = BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG);
            if (longPosition != null) {
                double avail = MathHelper.sub(longPosition.getCur_qty(), longPosition.getFreeze_qty());
                mShortValue1.setText(ContractCalculate.getVolUnit(contract, longPosition.getCur_qty(), price));
                mShortValue2.setText(ContractCalculate.getVolUnit(contract, avail, MathHelper.round(price)));
                mSellBtn.setEnabled(avail > 0);
            } else {
                mShortValue1.setText("0");
                mShortValue2.setText("0");
//                if (SLSDKAgent.slUser!=null) {
//                    mSellBtn.setEnabled(false);
//                } else {
//                    mSellBtn.setEnabled(true);
//                }
            }
            ContractPosition shortPosition = BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT);
            if (shortPosition != null) {
                double avail = MathHelper.sub(shortPosition.getCur_qty(), shortPosition.getFreeze_qty());
                mLongValue1.setText(ContractCalculate.getVolUnit(contract, shortPosition.getCur_qty(), price));
                mLongValue2.setText(ContractCalculate.getVolUnit(contract, avail, MathHelper.round(price)));
               // mBuyBtn.setEnabled(avail > 0);
            } else {
                mLongValue1.setText("0");
                mLongValue2.setText("0");
//                if (SLSDKAgent.slUser!=null) {
//                    mBuyBtn.setEnabled(false);
//                } else {
//                    mBuyBtn.setEnabled(true);
//                }
            }
        }
    }

    public void updateContract(ContractTicker data, boolean updatePrice) {
        mContractId = data.getInstrument_id();

        if (mIndexPriceTv == null || mPriceEt == null || mVolumeEt == null ||
                mBuyBtn == null || mSellBtn == null || mAavlValueTv == null ||
                mPriceTv == null || mAmountTv == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfPrice_1 = NumberUtil.getDecimal(contract.getPrice_index()-1);

        mCurrentPrice = dfPrice.format(MathHelper.round(data.getLast_px()));
        mIndexPrice = dfPrice.format(MathHelper.round(data.getIndex_px()));
        mTagPrice = dfPrice.format(MathHelper.round(data.getFair_px()));

        double rise_fall_rate = MathHelper.round(MathHelper.round(data.getChange_rate(), 8) * 100, 2);

        double current = MathHelper.round(data.getLast_px(), contract.getPrice_index());
        double index_price = MathHelper.round(data.getIndex_px(), contract.getPrice_index());
        double fair_price = MathHelper.round(data.getFair_px(), contract.getPrice_index());


        String cur_price = dfPrice_1.format(current);

        mIndexPriceTv.setText(dfPrice.format(index_price));
        mFairPriceTv.setText(dfPrice.format(fair_price));

        mPriceTv.setText(getString(R.string.sl_str_price) + "("  + contract.getQuote_coin() + ")");

        int unit = LogicContractSetting.getContractUint(LogicGlobal.sContext);
        mAmountTv.setText(getString(R.string.sl_str_amount) + "("  + (unit == 0 ? getString(R.string.sl_str_contracts_unit) : contract.getBase_coin()) + ")");


        double rate = MathHelper.mul(data.getFunding_rate(), "100");
        mFundsRateTv.setText(NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)) + "%");

        mPriceUnitTv.setText(contract.getQuote_coin());
        mTriggerPriceUnitTv.setText(contract.getQuote_coin());


        if (updatePrice) {
            mTriggerPriceEt.setText("");

            mPriceEt.setText(cur_price);
            updatePrice();

            mVolumeEt.setText("");
            updateVol();
        }

        updateLeverage();
        updateUserAsset();

        int order_type = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, 0);
        setOrderType(order_type, updatePrice);

        setVolUnit();
    }

    private void updateLeverage() {
        if (mContractId <= 0) {
            return;
        }

        mLeverage = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedInt(PreferenceManager.PREF_LEVERAGE, 10);
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int minLeverage = Integer.parseInt(contract.getMin_leverage());
        int maxLeverage = Integer.parseInt(contract.getMax_leverage());
        int leverage = (mLeverage == 0) ? maxLeverage : mLeverage;
        if (leverage > maxLeverage || leverage < minLeverage) {
            leverage = 100;
            if (leverage <= maxLeverage && leverage >= minLeverage) {
                mLeverage = 0;
            } else {
                leverage = 50;
                if (leverage <= maxLeverage && leverage >= minLeverage) {
                    mLeverage = leverage;
                } else {
                    leverage = 20;
                    if (leverage <= maxLeverage && leverage >= minLeverage) {
                        mLeverage = leverage;
                    } else {
                        leverage = 10;
                        if (leverage <= maxLeverage && leverage >= minLeverage) {
                            mLeverage = leverage;
                        }
                    }
                }
            }
        }

        if (mLeverage == 0) {
            mLeverageTv.setText(getString(R.string.sl_str_full_position) + maxLeverage + "X");
        } else {
            mLeverageTv.setText(getString(R.string.sl_str_gradually_position) + mLeverage + "X");
        }
    }

    public void updateUserAsset() {
        if (mAavlValueTv == null) {
            return;
        }

        if (mContractId <= 0) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
        if (contractAccount == null) {
            mAavlValueTv.setText("0" + " " + contract.getMargin_coin());
            return;
        }

        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        double available = contractAccount.getAvailable_vol_real();
        mAavlValueTv.setText(dfValue.format(available) + " " + contract.getMargin_coin());
    }

    public void updateDepth(int contractId, Depth data) {
        mContractId = contractId;
        mDepth = data;

        if (mContractId <= 0) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        if (mSellRv == null || mBuyRv == null) {
            return;
        }

        if (mSellAdapter == null) {
            mSellAdapter = new BuySellContractAdapter(LogicGlobal.sContext, this);
            mSellAdapter.setData(data.getAsks(), 2, contract.getPrice_index(), mABShowNum, mContractId);
            mSellRv.setAdapter(mSellAdapter);
        } else {
            mSellAdapter.setData(data.getAsks(), 2, contract.getPrice_index(), mABShowNum, mContractId);
            mSellRv.setAdapter(mSellAdapter);
        }

        if (mBuyAdapter == null) {
            mBuyAdapter = new BuySellContractAdapter(LogicGlobal.sContext, this);
            mBuyAdapter.setData(data.getBids(), 1, contract.getPrice_index(), mABShowNum, mContractId);
            mBuyRv.setAdapter(mBuyAdapter);
        } else {
            mBuyAdapter.setData(data.getBids(), 1, contract.getPrice_index(), mABShowNum, mContractId);
            mBuyRv.setAdapter(mBuyAdapter);
        }
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


    /**
     * @param data
     * @param actionType 5 删除 4 插入 2 更新  1 全量
     */
    public  void updateDepthByType(Depth data, int actionType) {
        synchronized(mLock){
            if (mSellRv == null || mBuyRv == null) {
                return;
            }
            Contract contract = LogicGlobal.getContract(mContractId);
            if (contract == null) {
                return;
            }

            if (mSellAdapter == null) {
                mSellAdapter = new BuySellContractAdapter(LogicGlobal.sContext, this);
            }

            if (mBuyAdapter == null) {
                mBuyAdapter = new BuySellContractAdapter(LogicGlobal.sContext, this);
            }

            List<DepthData> rectSellList = new ArrayList<>();
            List<DepthData> rectBindList = new ArrayList<>();

            List<DepthData> bindList = data.getBids();
            List<DepthData> sellList = data.getAsks();
            List<DepthData> oldBuyList = mDepth.getBids();
            List<DepthData> oldSellList = mDepth.getAsks();

            if (actionType == 5) {//删除
                List<DepthData> deleteBuyList = new ArrayList<>();
                for (int i = 0; i < bindList.size(); i++) {
                    DepthData depthData = isContainDepthData(oldBuyList, bindList.get(i));
                    if (depthData != null) {
                        deleteBuyList.add(depthData);
                    }
                }
                oldBuyList.removeAll(deleteBuyList);
                rectBindList.addAll(oldBuyList);

                List<DepthData> deleteSellList = new ArrayList<>();
                for (int i = 0; i < sellList.size(); i++) {
                    DepthData depthData = isContainDepthData(oldSellList, sellList.get(i));
                    if (depthData != null) {
                        deleteSellList.add(depthData);
                    }
                }
                oldSellList.removeAll(deleteSellList);
                rectSellList.addAll(oldSellList);
            } else if (actionType == 4) {//插入
                List<DepthData> addBuyList = new ArrayList<>();
                for (int i = 0; i < bindList.size(); i++) {
                    DepthData depthData = isContainDepthData(oldBuyList, bindList.get(i));
                    if (depthData == null) {
                        addBuyList.add(bindList.get(i));
                    }
                }
                rectBindList.addAll(addBuyList);
                rectBindList.addAll(oldBuyList);

                List<DepthData> addSellList = new ArrayList<>();
                for (int i = 0; i < sellList.size(); i++) {
                    DepthData depthData = isContainDepthData(oldSellList, sellList.get(i));
                    if (depthData == null) {
                        addSellList.add(sellList.get(i));
                    }
                }
                rectSellList.addAll(addSellList);
                rectSellList.addAll(oldSellList);
            } else if(actionType == 2){//更新
                for (int i = 0; i < bindList.size(); i++) {
                    if (Double.valueOf(bindList.get(i).getVol()).compareTo(0.0) <= 0) {
                        DepthData depthData = isContainDepthData(oldBuyList, bindList.get(i));
                        if (depthData != null) {
                            // LogUtil.d("lb","删除:"+bindList.get(i).getVol()+";"+oldBuyList.size());
                            oldBuyList.remove(depthData);
                        }
                    } else {
                        DepthData depthData = isContainDepthData(oldBuyList, bindList.get(i));
                        if (depthData != null) {
                            // LogUtil.d("lb","更新:"+bindList.get(i).getVol()+";"+bindList.get(i).getKey());
                            depthData.setVol(bindList.get(i).getVol());
                            depthData.setPrice(bindList.get(i).getPrice());
                        } else {
                            //LogUtil.d("lb", "增加:" + bindList.get(i).getVol() + ";" + oldBuyList.size());
                            oldBuyList.add(bindList.get(i));
                        }
                    }
                }
                rectBindList.addAll(oldBuyList);

                for (int i = 0; i < sellList.size(); i++) {
                    //若vol为0 则不添加，并移除
                    if (Double.valueOf(sellList.get(i).getVol()).compareTo(0.0) == 0) {
                        DepthData depthData = isContainDepthData(oldSellList, sellList.get(i));
                        if (depthData != null) {
                            oldSellList.remove(depthData);
                        }
                    } else {
                        DepthData depthData = isContainDepthData(oldSellList, sellList.get(i));
                        if (depthData != null) {
                            depthData.setVol(sellList.get(i).getVol());
                            depthData.setPrice(sellList.get(i).getPrice());
                        } else {
                            oldSellList.add(sellList.get(i));
                            //  LogUtil.d("lb", "增加:" + sellList.get(i).getVol() + ";" + oldSellList.size());
                        }
                    }
                }
                rectSellList.addAll(oldSellList);
            }else if(actionType == 1){
                mDepth = data;
                rectSellList.addAll(sellList);
                rectBindList.addAll(bindList);
            }

            mSellAdapter.setData(rectSellList, 2, contract.getPrice_index(), mABShowNum, mContractId);
            mSellRv.setAdapter(mSellAdapter);

            mBuyAdapter.setData(rectBindList, 1, contract.getPrice_index(), mABShowNum, mContractId);
            mBuyRv.setAdapter(mBuyAdapter);
        }
    }


    public void updateOpenOrder(int contractId) {
        if (mContractOpenOrderFragment != null) {
            mContractOpenOrderFragment.setContractId(contractId, false);
        }
        if (mContractEntrustHistoryFragment != null) {
            mContractEntrustHistoryFragment.setContractId(contractId, false);
        }
        if (mContractPlanOrderFragment != null) {
            mContractPlanOrderFragment.setContractId(contractId, false);
        }
        if (mHoldContractNowFragment != null) {
            mHoldContractNowFragment.setContractId(contractId, false);
        }
    }

    public void updatePlanOrder(int contractId) {
        if (mContractPlanOrderFragment != null) {
            mContractPlanOrderFragment.setContractId(contractId, false);
        }
    }

    private void doBuy() {
        if (SLSDKAgent.isLogin()) {
//            if (!SLSDKAgent.slUser.getActive()) {
//                LogicSDKState.getInstance().refresh(LogicSDKState.STATE_BIND);
//                    return;
//            }
        } else {
            LogicSDKState.getInstance().refresh(LogicSDKState.STATE_LOGIN);
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int order_type = getOrderType();
        String vol = TextUtils.isEmpty(mVolumeEt.getText().toString()) ? "0" : mVolumeEt.getText().toString();
        String price = TextUtils.isEmpty(mPriceEt.getText().toString()) ? "0" : mPriceEt.getText().toString();

        if (MathHelper.round(price) <= 0 && order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_price_too_low));
            return;
        }

        vol = ContractCalculate.trans2ContractVol(contract, vol, price);
        if (MathHelper.round(vol) <= 0.0) {
            ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_volume_too_low));
            return;
        }

        interBuy(price, vol, order_type);
    }

    private void interBuy(final String price, final String vol, final int order_type) {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }
        ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
        if (contractAccount == null) {
            String tips = String.format(getString(R.string.sl_str_no_contract_account), contract.getMargin_coin());
            ToastUtil.shortToast(LogicGlobal.sContext, tips);
            return;
        }

        final boolean trade_warn_confirm = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, true);
        boolean trade_confirm = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true);

        if (order_type != LogicBuySell.ORDER_TYPE_PLAN) {

            if (!trade_confirm && !trade_warn_confirm) {
                innerBuy(price, vol, order_type, "");
                return;
            }

            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);

            boolean isZh = LogicLanguage.isZhEnv(LogicGlobal.sContext);
            int accountMode = mLeverage == 0 ? 2 : 1;
            String mode =  accountMode == 1 ? getString(R.string.sl_str_gradually_position) : getString(R.string.sl_str_full_position);
            String leverage = mode + (mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage) + getString(R.string.sl_str_bei);

            String warning = "";
            String price_display = "";
            ContractOrder contractOrder = new ContractOrder();
            contractOrder.setLeverage(mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage);
            contractOrder.setQty(vol);
            contractOrder.setPosition_type(accountMode);
            contractOrder.setSide((mOperationType == 1) ? ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG : ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT);
            if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
                price_display = dfDefault.format(MathHelper.round(price, contract.getPrice_index()));
                contractOrder.setPx(price);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);

                double tip_limit = MathHelper.div(MathHelper.sub(price, mCurrentPrice), MathHelper.round(mCurrentPrice));
                if (0.05 < tip_limit) {
                    warning = (mOperationType == 1) ? getString(R.string.sl_str_open_risk_tips) : getString(R.string.sl_str_close_risk_tips);
                }
            } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
                price_display = getString(R.string.sl_str_market_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);

                double avgPrice = ContractCalculate.CalculateMarketAvgPrice(vol, mDepth.getAsks(), false);
                double tip_limit = MathHelper.div(MathHelper.sub(avgPrice, MathHelper.round(mCurrentPrice)), MathHelper.round(mCurrentPrice));
                if (0.03 < tip_limit) {
                    warning = (mOperationType == 1) ? getString(R.string.sl_str_open_market_risk_tips) : getString(R.string.sl_str_close_market_risk_tips);
                }

            } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
                price_display = getString(R.string.sl_str_buy1_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
                price_display = getString(R.string.sl_str_sell1_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            }

            double contractValue = ContractCalculate.CalculateContractValue(vol, price, contract);
            AdvanceOpenCost longOpenCost = ContractCalculate.CalculateAdvanceOpenCost(
                    contractOrder,
                    BTContract.getInstance().getContractPosition(mContractId, (mOperationType == 1) ? ContractPosition.POSITION_TYPE_LONG : ContractPosition.POSITION_TYPE_SHORT),
                    BTContract.getInstance().getContractOrderSize(mContractId, (mOperationType == 1) ? ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG : ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT),
                    LogicGlobal.getContractBasic(mContractId));

            double available = contractAccount.getAvailable_vol_real();
            double liquidatePrice = ContractCalculate.CalculateOrderLiquidatePrice(
                    contractOrder,
                    BTContract.getInstance().getContractAccount(contract.getMargin_coin()),
                    LogicGlobal.getContractBasic(mContractId));

            double ratio = MathHelper.div(MathHelper.round(mTagPrice), liquidatePrice) * 100;

            ContractPosition contractPosition = BTContract.getInstance().getContractPosition(mContractId, (mOperationType == 1) ? ContractPosition.POSITION_TYPE_LONG : ContractPosition.POSITION_TYPE_SHORT);
            String hold_vol = (contractPosition == null ? "0" : contractPosition.getCur_qty());


            if ((!TextUtils.isEmpty(warning) && trade_warn_confirm) || trade_confirm) {
                final ContractTradeConfirmWindow window = new ContractTradeConfirmWindow(getActivity());
                window.showTitle((mOperationType == 1) ? getString(R.string.sl_str_buy_open) : getString(R.string.sl_str_buy_close), LogicGlobal.sContext.getResources().getColor(R.color.sl_colorGreen));
                window.setInfo(contract.getSymbol(), getString(R.string.sl_str_price) + "(" + contract.getQuote_coin() + ")",
                        price_display,
                        getString(R.string.sl_str_amount) + "(" + getString(R.string.sl_str_contracts_unit) + ")",
                        vol,
                        leverage);

                if (!TextUtils.isEmpty(warning) && trade_warn_confirm) {
                    window.showWarning(warning);
                }

                window.setData(
                        dfDefault.format(MathHelper.round(contractValue, contract.getValue_index())),
                        longOpenCost == null ? "0" : dfDefault.format(MathHelper.round(longOpenCost.getFreezAssets(), contract.getValue_index())),
                        dfDefault.format(MathHelper.round(available, contract.getValue_index())),
                        dfDefault.format(Math.max(0, MathHelper.round((mOperationType == 1) ? MathHelper.add(vol, hold_vol) : MathHelper.sub(hold_vol, vol),contract.getVol_index()))),
                        dfDefault.format(MathHelper.round(mTagPrice, contract.getPrice_index())),
                        dfDefault.format(MathHelper.round(liquidatePrice, contract.getPrice_index())),
                        dfDefault.format(MathHelper.round(ratio, contract.getValue_index())) + "%");

                window.setCode(contract.getMargin_coin(),contract.getMargin_coin(),contract.getMargin_coin());
                window.setOrderType(order_type);
                window.setOperationType(mOperationType);
                window.showBtnOk(getActivity().getString(R.string.sl_str_confirm));
                window.showBtnClose();
                window.showAtLocation(mBuyBtn, Gravity.CENTER, 0, 0);
                final String finalWarning = warning;
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.getNoremindCheck());
                        } else {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                        }

                        innerBuy(price, vol, order_type, "");
                    }
                });
                window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.getNoremindCheck());
                        } else {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                        }
                    }
                });
            } else {
                innerBuy(price, vol, order_type, "");
                return;
            }
        } else {
            if (!trade_confirm) {
                innerBuy(price, vol, order_type, "");
                return;
            }

            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);

            int accountMode = mLeverage == 0 ? 2 : 1;
            String mode =  accountMode == 1 ? getString(R.string.sl_str_gradually_position) : getString(R.string.sl_str_full_position);
            String leverage = mode + (mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage) + getString(R.string.sl_str_bei);

            String trigger_price = TextUtils.isEmpty(mTriggerPriceEt.getText().toString()) ? "0" : mTriggerPriceEt.getText().toString();
            int trigger_type = LogicContractSetting.getTriggerPriceType(LogicGlobal.sContext);
            String trigger_type_text = "";
            if (trigger_type == 0) {
                trigger_type_text = getString(R.string.sl_str_latest_price);
            } else if (trigger_type == 1) {
                trigger_type_text = getString(R.string.sl_str_fair_price);
            } else if (trigger_type == 2) {
                trigger_type_text = getString(R.string.sl_str_index_price);
            }
            int effect = LogicContractSetting.getStrategyEffectTime(LogicGlobal.sContext);

            final ContractPlanConfirmWindow window = new ContractPlanConfirmWindow(getActivity());
            window.showTitle((mOperationType == 1) ? getString(R.string.sl_str_buy_open) + getString(R.string.sl_str_plan) : getString(R.string.sl_str_buy_close) + getString(R.string.sl_str_plan),
                    LogicGlobal.sContext.getResources().getColor(R.color.sl_colorGreen));
            window.setInfo(contract.getSymbol(), dfDefault.format(MathHelper.round(trigger_price, contract.getPrice_index())),
                    (LogicContractSetting.getExecution(LogicGlobal.sContext) == 1) ? getString(R.string.sl_str_market_price_simple) : dfDefault.format(MathHelper.round(price, contract.getPrice_index())),
                    vol);

            window.setData(
                    leverage,
                    trigger_type_text,
                    (effect == 0) ? getString(R.string.sl_str_in_24_hours) : getString(R.string.sl_str_in_7_days) );

            window.showBtnOk(getActivity().getString(R.string.sl_str_confirm));
            window.showBtnClose();
            window.showAtLocation(mBuyBtn, Gravity.CENTER, 0, 0);
            window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                    PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                    innerBuy(price, vol, order_type, "");
                }
            });
            window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                    PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                }
            });
            return;
        }

    }

    private void innerBuy(final String price, final String vol, final int order_type, String pwd) {
        //MobclickAgent.onEvent(LogicGlobal.sContext, "ss_by");
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        ContractOrder order = new ContractOrder();
        order.setInstrument_id(mContractId);
        order.setNonce(System.currentTimeMillis()/1000);
        order.setQty(vol);
        if (mOperationType == 1) {
            order.setPosition_type(mLeverage == 0 ? 2 : 1);
            order.setLeverage(mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage);
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);
        } else {
            ContractPosition shortPosition = BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT);
            if (shortPosition != null) {
                order.setPid(shortPosition.getPid());
            }
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT);
        }

        final String orgText = mBuyBtn.getText();
        mBuyBtn.startLoading("");
        IResponse<String> response = new IResponse<String>() {
            @Override
            public void onResponse(String errno, String message, String data) {
                mBuyBtn.stopLoading(orgText);

                if (TextUtils.equals(errno, BTConstants.ERRNO_PERMISSION_DENIED)) {
                    final PopEnterPassword popEnterPassword = new PopEnterPassword(getActivity());
                    popEnterPassword.showAtLocation(mSellBtn, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popEnterPassword.setOnFinishInput(new OnPasswordInputFinish() {
                        @Override
                        public void inputFinish(String password) {
                            innerBuy(price, vol, order_type, UtilSystem.toMD5(password));
                            popEnterPassword.dismiss();
                        }
                    });
                    return;
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(getActivity(), message);
                    return;
                }

                ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_order_submit_success));

             //   doSetFundPwd();
            }
        };

        if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            order.setPx(price);
            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
            if (mDepth != null && mDepth.getAsks().size() > 0) {
                order.setPx(mDepth.getAsks().get(mDepth.getAsks().size() - 1).getPrice());
            } else {
                order.setPx(price);
            }
            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
            if (mDepth != null && mDepth.getBids().size() > 0) {
                order.setPx(mDepth.getBids().get(0).getPrice());
            } else {
                order.setPx(price);
            }

            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
            order.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);
        } else if (order_type == LogicBuySell.ORDER_TYPE_PLAN) {

            String trigger_price = TextUtils.isEmpty(mTriggerPriceEt.getText().toString()) ? "0" : mTriggerPriceEt.getText().toString();
            order.setPx(trigger_price);
            order.setExec_px(price);
            order.setCategory((LogicContractSetting.getExecution(LogicGlobal.sContext) == 0) ?
                    ContractOrder.ORDER_CATEGORY_NORMAL : ContractOrder.ORDER_CATEGORY_MARKET);

            int trigger_type = LogicContractSetting.getTriggerPriceType(LogicGlobal.sContext);
            int price_type = 0;
            if (trigger_type == 0) {
                price_type = 1;
                if (MathHelper.round(mCurrentPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            } else if (trigger_type == 1) {
                price_type = 2;
                if (MathHelper.round(mTagPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            } else if (trigger_type == 2) {
                price_type = 4;
                if (MathHelper.round(mIndexPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            }
            order.setTrigger_type(price_type);

            int effect = LogicContractSetting.getStrategyEffectTime(LogicGlobal.sContext);
            order.setLife_cycle((effect == 0) ? 24 : 168);

            if (TextUtils.isEmpty(pwd)) {
                BTContract.getInstance().submitPlanOrder(order, response);
            } else {
                BTContract.getInstance().submitPlanOrder(order, pwd, response);
            }
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            BTContract.getInstance().submitOrder(order, response);
        } else {
            BTContract.getInstance().submitOrder(order, pwd, response);
        }
    }

    private void doSell() {
        if (SLSDKAgent.isLogin()) {
//            if (!SLSDKAgent.slUser.getActive()) {
//                LogicSDKState.getInstance().refresh(LogicSDKState.STATE_BIND);
//                return;
//            }
        } else {
            LogicSDKState.getInstance().refresh(LogicSDKState.STATE_LOGIN);
            return;
        }
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int order_type = getOrderType();
        String vol = TextUtils.isEmpty(mVolumeEt.getText().toString()) ? "0" : mVolumeEt.getText().toString();
        String price = TextUtils.isEmpty(mPriceEt.getText().toString()) ? "0" : mPriceEt.getText().toString();

        if (MathHelper.round(price) <= 0 && order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_price_too_low));
            return;
        }
        vol = ContractCalculate.trans2ContractVol(contract, vol, price);
        if (MathHelper.round(vol) <= 0.0) {
            ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_volume_too_low));
            return;
        }

        interSell(price, vol, order_type);
    }

    private void interSell(final String price, final String vol, final int order_type) {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }
        ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
        if (contractAccount == null) {
            String tips = String.format(getString(R.string.sl_str_no_contract_account), contract.getMargin_coin());
            ToastUtil.shortToast(LogicGlobal.sContext, tips);
            return;
        }

        final boolean trade_warn_confirm = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, true);
        boolean trade_confirm = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true);


        if (order_type != LogicBuySell.ORDER_TYPE_PLAN) {

            if (!trade_confirm && !trade_warn_confirm) {
                innerSell(price, vol, order_type, "");
                return;
            }

            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);

            boolean isZh = LogicLanguage.isZhEnv(LogicGlobal.sContext);
            int accountMode = mLeverage == 0 ? 2 : 1;
            String mode =  accountMode == 1 ? getString(R.string.sl_str_gradually_position) : getString(R.string.sl_str_full_position);
            String leverage = mode + (mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage) + getString(R.string.sl_str_bei);

            String warning = "";
            String price_display = "";
            ContractOrder contractOrder = new ContractOrder();
            contractOrder.setLeverage(mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage);
            contractOrder.setQty(vol);
            contractOrder.setPosition_type(accountMode);
            contractOrder.setSide((mOperationType == 1) ? ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT : ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG);
            if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
                price_display = dfDefault.format(MathHelper.round(price, contract.getPrice_index()));
                contractOrder.setPx(price);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);

                double tip_limit = MathHelper.div(MathHelper.sub(mCurrentPrice, price), MathHelper.round(mCurrentPrice));
                if (0.05 < tip_limit) {
                    warning = (mOperationType == 1) ? getString(R.string.sl_str_open_risk_tips) : getString(R.string.sl_str_close_risk_tips);
                }
            } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
                price_display = getString(R.string.sl_str_market_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);

                double avgPrice = ContractCalculate.CalculateMarketAvgPrice(vol, mDepth.getBids(), true);
                double tip_limit = MathHelper.div(MathHelper.sub(MathHelper.round(mCurrentPrice), avgPrice), MathHelper.round(mCurrentPrice));
                if (0.03 < tip_limit) {
                    warning = (mOperationType == 1) ? getString(R.string.sl_str_open_market_risk_tips) : getString(R.string.sl_str_close_market_risk_tips);
                }
            } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
                price_display = getString(R.string.sl_str_buy1_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
                price_display = getString(R.string.sl_str_sell1_price);
                contractOrder.setPx(mCurrentPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            }

            double contractValue = ContractCalculate.CalculateContractValue(vol, price, contract);
            AdvanceOpenCost shortOpenCost = ContractCalculate.CalculateAdvanceOpenCost(
                    contractOrder,
                    BTContract.getInstance().getContractPosition(mContractId, (mOperationType == 1) ? ContractPosition.POSITION_TYPE_SHORT : ContractPosition.POSITION_TYPE_LONG ),
                    BTContract.getInstance().getContractOrderSize(mContractId, (mOperationType == 1) ? ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT : ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG),
                    LogicGlobal.getContractBasic(mContractId));

            double available = contractAccount.getAvailable_vol_real();
            double liquidatePrice = ContractCalculate.CalculateOrderLiquidatePrice(
                    contractOrder,
                    BTContract.getInstance().getContractAccount(contract.getMargin_coin()),
                    LogicGlobal.getContractBasic(mContractId));

            double ratio = MathHelper.div(MathHelper.round(mTagPrice), liquidatePrice) * 100;

            ContractPosition contractPosition = BTContract.getInstance().getContractPosition(mContractId, (mOperationType == 1) ? ContractPosition.POSITION_TYPE_SHORT : ContractPosition.POSITION_TYPE_LONG );
            String hold_vol = (contractPosition == null ? "0" : contractPosition.getCur_qty());

            if ((!TextUtils.isEmpty(warning) && trade_warn_confirm) || trade_confirm) {
                final ContractTradeConfirmWindow window = new ContractTradeConfirmWindow(getActivity());
                window.showTitle((mOperationType == 1) ? getString(R.string.sl_str_sell_open) : getString(R.string.sl_str_sell_close), LogicGlobal.sContext.getResources().getColor(R.color.sl_colorRed));
                window.setInfo(contract.getSymbol(), getString(R.string.sl_str_price) + "(" + contract.getQuote_coin() + ")",
                        price_display,
                        getString(R.string.sl_str_amount) + "(" + getString(R.string.sl_str_contracts_unit) + ")",
                        vol,
                        leverage);

                if (!TextUtils.isEmpty(warning) && trade_warn_confirm) {
                    window.showWarning(warning);
                }

                window.setData(
                        dfDefault.format(MathHelper.round(contractValue, contract.getValue_index())),
                        shortOpenCost == null ? "0" : dfDefault.format(MathHelper.round(shortOpenCost.getFreezAssets(), contract.getValue_index())),
                        dfDefault.format(MathHelper.round(available, contract.getValue_index())),
                        dfDefault.format(Math.max(0, MathHelper.round((mOperationType == 1) ? MathHelper.add(vol, hold_vol) : MathHelper.sub(hold_vol, vol),contract.getVol_index()))),
                        dfDefault.format(MathHelper.round(mTagPrice, contract.getPrice_index())),
                        dfDefault.format(MathHelper.round(liquidatePrice, contract.getPrice_index())),
                        dfDefault.format(MathHelper.round(ratio, contract.getValue_index())) + "%");

                window.setCode(contract.getMargin_coin(),contract.getMargin_coin(),contract.getMargin_coin());
                window.setOrderType(order_type);
                window.setOperationType(mOperationType);
                window.showBtnOk(getActivity().getString(R.string.sl_str_confirm));
                window.showBtnClose();
                window.showAtLocation(mBuyBtn, Gravity.CENTER, 0, 0);
                final String finalWarning = warning;
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.getNoremindCheck());
                        } else {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                        }
                        innerSell(price, vol, order_type, "");
                    }
                });
                window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.getNoremindCheck());
                        } else {
                            PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                        }
                    }
                });
            } else {
                innerSell(price, vol, order_type, "");
                return;
            }

        } else {
            if (!trade_confirm) {
                innerSell(price, vol, order_type, "");
                return;
            }

            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);

            int accountMode = mLeverage == 0 ? 2 : 1;
            String mode =  accountMode == 1 ? getString(R.string.sl_str_gradually_position) : getString(R.string.sl_str_full_position);
            String leverage = mode + (mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage) + getString(R.string.sl_str_bei);

            String trigger_price = TextUtils.isEmpty(mTriggerPriceEt.getText().toString()) ? "0" : mTriggerPriceEt.getText().toString();
            int trigger_type = LogicContractSetting.getTriggerPriceType(LogicGlobal.sContext);
            String trigger_type_text = "";
            if (trigger_type == 0) {
                trigger_type_text = getString(R.string.sl_str_latest_price);//set list map
            } else if (trigger_type == 1) {
                trigger_type_text = getString(R.string.sl_str_fair_price);
            } else if (trigger_type == 2) {
                trigger_type_text = getString(R.string.sl_str_index_price);
            }
            int effect = LogicContractSetting.getStrategyEffectTime(LogicGlobal.sContext);

            final ContractPlanConfirmWindow window = new ContractPlanConfirmWindow(getActivity());
            window.showTitle((mOperationType == 1) ? getString(R.string.sl_str_sell_open) + getString(R.string.sl_str_plan) : getString(R.string.sl_str_sell_close) + getString(R.string.sl_str_plan),
                    LogicGlobal.sContext.getResources().getColor(R.color.sl_colorRed));
            window.setInfo(contract.getSymbol(), dfDefault.format(MathHelper.round(trigger_price, contract.getPrice_index())),
                    (LogicContractSetting.getExecution(LogicGlobal.sContext) == 1) ? getString(R.string.sl_str_market_price_simple) : dfDefault.format(MathHelper.round(price, contract.getPrice_index())),
                    vol);

            window.setData(
                    leverage,
                    trigger_type_text,
                    (effect == 0) ? getString(R.string.sl_str_in_24_hours) : getString(R.string.sl_str_in_7_days) );

            window.showBtnOk(getActivity().getString(R.string.sl_str_confirm));
            window.showBtnClose();
            window.showAtLocation(mSellBtn, Gravity.CENTER, 0, 0);
            window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                    PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                    innerSell(price, vol, order_type, "");
                }
            });
            window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                    PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.getNoremindCheck());
                }
            });
        }
    }

    private void innerSell(final String price, final String vol, final int order_type, String pwd) {
       // MobclickAgent.onEvent(LogicGlobal.sContext, "ss_sl");
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        ContractOrder order = new ContractOrder();
        order.setInstrument_id(mContractId);
        order.setNonce(System.currentTimeMillis()/1000);
        order.setQty(vol);
        if (mOperationType == 1) {
            order.setPosition_type(mLeverage == 0 ? 2 : 1);
            order.setLeverage(mLeverage == 0 ? Integer.parseInt(contract.getMax_leverage()) : mLeverage);
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);
        } else {
            ContractPosition longPosition = BTContract.getInstance().getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG);
            if (longPosition != null) {
                order.setPid(longPosition.getPid());
            }
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG);
        }

        final String orgText = mSellBtn.getText();
        mSellBtn.startLoading("");

        IResponse<String> response =  new IResponse<String>() {
            @Override
            public void onResponse(String errno, String message, String data) {
                mSellBtn.stopLoading(orgText);
                if (TextUtils.equals(errno, BTConstants.ERRNO_PERMISSION_DENIED)) {
                    final PopEnterPassword popEnterPassword = new PopEnterPassword(getActivity());
                    popEnterPassword.showAtLocation(mSellBtn, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popEnterPassword.setOnFinishInput(new OnPasswordInputFinish() {
                        @Override
                        public void inputFinish(String password) {
                            innerSell(price, vol, order_type, UtilSystem.toMD5(password));
                            popEnterPassword.dismiss();
                        }
                    });
                    return;
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(getActivity(), message);
                    return;
                }



                ToastUtil.shortToast(getActivity(), getString(R.string.sl_str_order_submit_success));

              ///  doSetFundPwd();
            }
        };

        if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            order.setPx(price);
            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
            if (mDepth != null && mDepth.getAsks().size() > 0) {
                order.setPx(mDepth.getAsks().get(mDepth.getAsks().size() - 1).getPrice());
            } else {
                order.setPx(price);
            }
            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
            if (mDepth != null && mDepth.getBids().size() > 0) {
                order.setPx(mDepth.getBids().get(0).getPrice());
            } else {
                order.setPx(price);
            }
            order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
        } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
            order.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);
        } else if (order_type == LogicBuySell.ORDER_TYPE_PLAN) {

            String trigger_price = TextUtils.isEmpty(mTriggerPriceEt.getText().toString()) ? "0" : mTriggerPriceEt.getText().toString();
            order.setPx(trigger_price);
            order.setExec_px(price);
            order.setCategory((LogicContractSetting.getExecution(LogicGlobal.sContext) == 0) ?
                    ContractOrder.ORDER_CATEGORY_NORMAL : ContractOrder.ORDER_CATEGORY_MARKET);

            int trigger_type = LogicContractSetting.getTriggerPriceType(LogicGlobal.sContext);
            int price_type = 0;
            if (trigger_type == 0) {
                price_type = 1;
                if (MathHelper.round(mCurrentPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            } else if (trigger_type == 1) {
                price_type = 2;
                if (MathHelper.round(mTagPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            } else if (trigger_type == 2) {
                price_type = 4;
                if (MathHelper.round(mIndexPrice) > MathHelper.round(trigger_price)) {
                    order.setTrend(2);
                } else {
                    order.setTrend(1);
                }
            }
            order.setTrigger_type(price_type);

            int effect = LogicContractSetting.getStrategyEffectTime(LogicGlobal.sContext);
            order.setLife_cycle((effect == 0) ? 24 : 168);

            if (TextUtils.isEmpty(pwd)) {
                BTContract.getInstance().submitPlanOrder(order, response);
            } else {
                BTContract.getInstance().submitPlanOrder(order, pwd, response);
            }
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            BTContract.getInstance().submitOrder(order, response);
        } else {
            BTContract.getInstance().submitOrder(order, pwd, response);
        }
    }

    private void selectLeverage() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SelectLeverageActivity.class);
        intent.putExtra("contractId", mContractId);
        intent.putExtra("leverage", mLeverage);
        startActivity(intent);
    }

    private void gotoTransfer() {
        if (SLSDKAgent.isLogin()) {
//            if (!SLSDKAgent.slUser.getActive()) {
//                LogicSDKState.getInstance().refresh(LogicSDKState.STATE_BIND);
//                return;
//            }
        } else {
            LogicSDKState.getInstance().refresh(LogicSDKState.STATE_LOGIN);
            return;
        }
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), FundsTransferActivity.class);
        intent.putExtra("coin_code", contract.getMargin_coin());
        startActivity(intent);
    }

    private void doSetFundPwd() {
//        LogUtil.d("DEBUG_UI","doSetFundPwd");
//        boolean fundPwdSetted = false;
//        Account account = BTAccount.getInstance().getActiveAccount();
//        if (account != null && account.getAsset_password_effective_time() != -2 ) {
//            fundPwdSetted = true;
//        }
//
//        if (fundPwdSetted) {
//            return;
//        }

        final PopEnterSetPassword popEnterPassword = new PopEnterSetPassword(getActivity());
        popEnterPassword.showAtLocation(m_RootView,Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popEnterPassword.getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AssetPasswordData assetPasswordData = new AssetPasswordData();
//                assetPasswordData.setAsset_password(UtilSystem.toMD5(popEnterPassword.getPassword()));
//
//                BTAccount.getInstance().addAssetPassword(assetPasswordData, new IResponse<Void>() {
//                    @Override
//                    public void onResponse(String errno, String message, Void data) {
//                        if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
//                            ToastUtil.shortToast(LogicGlobal.sContext, message);
//                            return;
//                        }
//
//                        ToastUtil.shortToast(LogicGlobal.sContext, message);
//
//                        BTAccount.getInstance().userMe(new IResponse<Account>() {
//                            @Override
//                            public void onResponse(String errno, String message, Account data) {
//                            }
//                        });
//                    }
//                });

                popEnterPassword.dismiss();
            }
        });
    }

    private int getOrderType() {
        int order_type = PreferenceManager.getInstance(getActivity()).getSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, 0);
        return order_type;
    }

    private void setOrderType(int type, boolean updatePrice) {

        if (type == LogicBuySell.ORDER_TYPE_LIMIT) {
            mOrderTypeTv.setText(R.string.sl_str_limit_entrust);
            mOrderTypeRg.clearCheck();
            mOrderTypeRg.setVisibility(View.VISIBLE);
            mPriceValueTv.setVisibility(View.VISIBLE);
            mLimitPriceRl.setVisibility(View.VISIBLE);
            mMarketPriceRl.setVisibility(View.GONE);
            mTriggerPriceRl.setVisibility(View.GONE);
            mPlanSettingIv.setVisibility(View.GONE);
            mPrice2MarketTv.setVisibility(View.GONE);
            mPrice2LimitTv.setVisibility(View.GONE);
            mMarketPriceTv.setGravity(Gravity.CENTER);

            if (updatePrice) {
                mPriceEt.setText(mCurrentPrice);
                mPriceEt.setHint(R.string.sl_str_price);
            }
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_LIMIT);

        } else if (type == LogicBuySell.ORDER_TYPE_MARKET) {
            mOrderTypeTv.setText(R.string.sl_str_limit_entrust);
            mTabMarketPrice.setChecked(true);
            mMarketPriceTv.setText(R.string.sl_str_market_price);
            mOrderTypeRg.setVisibility(View.VISIBLE);
            mPriceValueTv.setVisibility(View.GONE);
            mLimitPriceRl.setVisibility(View.GONE);
            mMarketPriceRl.setVisibility(View.VISIBLE);
            mTriggerPriceRl.setVisibility(View.GONE);
            mPlanSettingIv.setVisibility(View.GONE);
            mPrice2MarketTv.setVisibility(View.GONE);
            mPrice2LimitTv.setVisibility(View.GONE);
            mMarketPriceTv.setGravity(Gravity.CENTER);

            if (updatePrice) {
                mPriceEt.setText(mCurrentPrice);
                mPriceEt.setHint(R.string.sl_str_price);
            }
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_MARKET);
        } else if (type == LogicBuySell.ORDER_TYPE_PLAN) {

            mOrderTypeTv.setText(R.string.sl_str_plan_entrust);
            mOrderTypeRg.clearCheck();
            mOrderTypeRg.setVisibility(View.GONE);
            mPriceValueTv.setVisibility(View.VISIBLE);
            mMarketPriceTv.setText(R.string.sl_str_market_price);
            mTriggerPriceRl.setVisibility(View.VISIBLE);
            mPlanSettingIv.setVisibility(View.VISIBLE);
            mPrice2MarketTv.setVisibility(View.VISIBLE);
            mPrice2LimitTv.setVisibility(View.VISIBLE);
            mMarketPriceTv.setGravity(Gravity.CENTER_VERTICAL);
            if (updatePrice) {
                mPriceEt.setText("");
                mPriceEt.setHint(R.string.sl_str_execution_price);
            }

            int trigger_type = LogicContractSetting.getTriggerPriceType(LogicGlobal.sContext);
            String trigger_type_text = "";
            if (trigger_type == 0) {
                trigger_type_text = getString(R.string.sl_str_latest_price_simple);
            } else if (trigger_type == 1) {
                trigger_type_text = getString(R.string.sl_str_fair_price_simple);
            } else if (trigger_type == 2) {
                trigger_type_text = getString(R.string.sl_str_index_price_simple);
            }

            mTriggerPriceEt.setHint(getString(R.string.sl_str_trigger_price) + "(" + trigger_type_text + ")");

            int execution = LogicContractSetting.getExecution(LogicGlobal.sContext);
            if (execution == 0) {
                mLimitPriceRl.setVisibility(View.VISIBLE);
                mMarketPriceRl.setVisibility(View.GONE);
            } else if (execution == 1) {
                mLimitPriceRl.setVisibility(View.GONE);
                mMarketPriceRl.setVisibility(View.VISIBLE);
            }
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_PLAN);
        } else if (type == LogicBuySell.ORDER_BID_PRICE) {
            mOrderTypeTv.setText(R.string.sl_str_limit_entrust);
            mTabBidPriceP1.setChecked(true);
            mMarketPriceTv.setText(R.string.sl_str_buy1_price);
            mOrderTypeRg.setVisibility(View.VISIBLE);
            mPriceValueTv.setVisibility(View.GONE);
            mLimitPriceRl.setVisibility(View.GONE);
            mMarketPriceRl.setVisibility(View.VISIBLE);
            mTriggerPriceRl.setVisibility(View.GONE);
            mPlanSettingIv.setVisibility(View.GONE);
            mPrice2MarketTv.setVisibility(View.GONE);
            mPrice2LimitTv.setVisibility(View.GONE);
            mMarketPriceTv.setGravity(Gravity.CENTER);
            if (updatePrice) {
                mPriceEt.setText(mCurrentPrice);
                mPriceEt.setHint(R.string.sl_str_price);
            }
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_BID_PRICE);

        } else if (type == LogicBuySell.ORDER_ASK_PRICE) {
            mOrderTypeTv.setText(R.string.sl_str_limit_entrust);
            mTabAskPriceP1.setChecked(true);
            mMarketPriceTv.setText(R.string.sl_str_sell1_price);
            mOrderTypeRg.setVisibility(View.VISIBLE);
            mPriceValueTv.setVisibility(View.GONE);
            mLimitPriceRl.setVisibility(View.GONE);
            mMarketPriceRl.setVisibility(View.VISIBLE);
            mTriggerPriceRl.setVisibility(View.GONE);
            mPlanSettingIv.setVisibility(View.GONE);
            mPrice2MarketTv.setVisibility(View.GONE);
            mPrice2LimitTv.setVisibility(View.GONE);
            mMarketPriceTv.setGravity(Gravity.CENTER);
            if (updatePrice) {
                mPriceEt.setText(mCurrentPrice);
                mPriceEt.setHint(R.string.sl_str_price);
            }
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_ASK_PRICE);
        }
    }

    public static int s_unit = -1;
    private void setVolUnit() {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int unit = LogicContractSetting.getContractUint(LogicGlobal.sContext);

        mVolumeUnitTv.setText(unit == 0 ? getString(R.string.sl_str_contracts_unit) : contract.getBase_coin());
        mAmountTv.setText(getString(R.string.sl_str_amount) + "("  + (unit == 0 ? getString(R.string.sl_str_contracts_unit) : contract.getBase_coin()) + ")");
        updateInfoValue(true);
    }

    private void setAskBid(int type) {
        if (type == LogicBuySell.ASK_BID_DEFAULT) {
            mSellRv.setVisibility(View.VISIBLE);
            mBuyRv.setVisibility(View.VISIBLE);
            mSellRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 150);
            mBuyRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 150);
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_DEFAULT);
            mABShowNum = 6;
            updateDepth(mContractId, mDepth);
        } else if (type == LogicBuySell.ASK_BID_ASK) {
            mSellRv.setVisibility(View.VISIBLE);
            mBuyRv.setVisibility(View.GONE);
            mSellRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 300);
            mBuyRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 150);
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_ASK);
            mABShowNum = 12;
            updateDepth(mContractId, mDepth);
        } else if (type == LogicBuySell.ASK_BID_BID) {
            mSellRv.setVisibility(View.GONE);
            mBuyRv.setVisibility(View.VISIBLE);
            mSellRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 150);
            mBuyRv.getLayoutParams().height = UtilSystem.dip2px(getActivity(), 300);
            PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_BID);
            mABShowNum = 12;
            updateDepth(mContractId, mDepth);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogicBuySell.getInstance().unregistListener(this);
        LogicUserState.getInstance().unregistListener(this);
        LogicContractSetting.getInstance().unregistListener(this);
    }

//    @Override
//    public void onLogin(Account account) {
//        if (mContractId == 0) {
//            return;
//        }
//
//        if (mOperationType == 1) {
//            if (BTAccount.getInstance().isLogin()) {
//                mBuyBtn.setText(getString(R.string.str_buy_open_long));
//                mSellBtn.setText(getString(R.string.str_sell_open_short));
//            } else {
//                mBuyBtn.setText(getString(R.string.str_login));
//                mSellBtn.setText(getString(R.string.str_login));
//            }
//        } else if (mOperationType == 2) {
//            if (BTAccount.getInstance().isLogin()) {
//                mBuyBtn.setText(getString(R.string.str_buy_close_short));
//                mSellBtn.setText(getString(R.string.str_sell_close_long));
//            } else {
//                mBuyBtn.setText(getString(R.string.str_login));
//                mSellBtn.setText(getString(R.string.str_login));
//            }
//        }
//
//        updateOpenOrder(mContractId);
//    }
//
//    @Override
//    public void onUserMe(Account account) {
//    }
//
//    @Override
//    public void onContractAccount(ContractAccount account) {
//        if (mContractId == 0) {
//            return;
//        }
//
//        if (account != null){
//            updateUserAsset();
//        }
//
//        updateInfoValue(true);
//    }
//
//    @Override
//    public void onLogout(boolean forbidden) {
//        mVolumeEt.setText("");
//        mAavlValueTv.setText("0");
//        mBuyBtn.setText(getString(R.string.str_login));
//        mSellBtn.setText(getString(R.string.str_login));
//    }

    @Override
    public void onSwitchTab(int param, String stock_code) {

    }

    @Override
    public void onDecimalSelected(int decimals) {

    }

    @Override
    public void onOrderTypeSelected(int type) {
        setOrderType(type, true);
    }

    @Override
    public void onAskBidSelected(int type) {
        setAskBid(type);
    }

    @Override
    public void onBuySellContractClick(DepthData depthData, String showVol, int flag) {
        int type = getOrderType();
        if (type == LogicBuySell.ORDER_TYPE_PLAN) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return ;
        }

        DecimalFormat dfPirce = NumberUtil.getDecimal(contract.getPrice_index());
        double price = MathHelper.round(depthData.getPrice());
        mPriceEt.setText(dfPirce.format(price));
    }

    @Override
    public void onBuySellContractVolClick(DepthData depthData, String showVol, int flag) {
        int type = getOrderType();
        if (type == LogicBuySell.ORDER_TYPE_PLAN) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return ;
        }

        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());
        double volum = MathHelper.round(depthData.getVol());
        mVolumeEt.setText(dfVol.format(volum));
    }

    @Override
    public void onContractSettingChange() {
        setVolUnit();
        setOrderType(getOrderType(), false);
    }

    @Override
    public void onLeverageChange(int leverage, String text) {
        mLeverage = leverage;
        mLeverageTv.setText(text);
        updateVol();
        //updateInfoValue(true);
        PreferenceManager.getInstance(getActivity()).putSharedInt(PreferenceManager.PREF_LEVERAGE, mLeverage);
    }

    @Override
    public void onLogin() {
        if (mContractId == 0) {
            return;
        }

        if (mOperationType == 1) {
            if (SLSDKAgent.isLogin()) {
                mBuyBtn.setText(getString(R.string.sl_str_buy_open_long));
                mSellBtn.setText(getString(R.string.sl_str_sell_open_short));
            } else {
                mBuyBtn.setText(getString(R.string.sl_str_login));
                mSellBtn.setText(getString(R.string.sl_str_login));
            }
        } else if (mOperationType == 2) {
            if (SLSDKAgent.isLogin()) {
                mBuyBtn.setText(getString(R.string.sl_str_buy_close_short));
                mSellBtn.setText(getString(R.string.sl_str_sell_close_long));
            } else {
                mBuyBtn.setText(getString(R.string.sl_str_login));
                mSellBtn.setText(getString(R.string.sl_str_login));
            }
        }

        BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractAccount> data) {
                updateUserAsset();
                updateInfoValue(true);
            }
        });

        updateOpenOrder(mContractId);


    }

    @Override
    public void onLogout() {
        mVolumeEt.setText("");
        mAavlValueTv.setText("0");
        mBuyBtn.setText(getString(R.string.sl_str_login));
        mSellBtn.setText(getString(R.string.sl_str_login));
    }
}
