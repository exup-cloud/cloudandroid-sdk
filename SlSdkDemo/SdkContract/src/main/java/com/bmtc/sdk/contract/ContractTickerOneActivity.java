package com.bmtc.sdk.contract;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.contract.common.chart.KChartAdapter;
import com.bmtc.sdk.contract.dialog.DropKlineWindow;
import com.bmtc.sdk.contract.fragment.DepthChartFragment;
import com.bmtc.sdk.contract.fragment.OrderBookFragment;
import com.bmtc.sdk.contract.fragment.TradeHistoryFragment;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.base.BaseFragmentPagerAdapter;
import com.bmtc.sdk.library.common.DataHelper;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Collect;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractSpot;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.trans.data.ContractTrade;
import com.bmtc.sdk.library.trans.data.Depth;
import com.bmtc.sdk.library.trans.data.DepthData;
import com.bmtc.sdk.library.trans.data.KLineEntity;
import com.bmtc.sdk.library.trans.data.SDStockTrade;
import com.bmtc.sdk.library.uilogic.LogicCollects;
import com.bmtc.sdk.library.uilogic.LogicContractSpot;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicLanguage;
import com.bmtc.sdk.library.uilogic.LogicTimer;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.DeviceUtil;
import com.bmtc.sdk.library.utils.LogUtil;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.WebSocketClient;
import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.EntityImpl.CandleImpl;
import com.github.tifezh.kchartlib.chart.KChartView;
import com.github.tifezh.kchartlib.chart.draw.Status;
import com.github.tifezh.kchartlib.chart.formatter.BigDateFormatter;
import com.github.tifezh.kchartlib.chart.formatter.DateFormatter;
import com.github.tifezh.kchartlib.chart.formatter.TimeFormatter;
import com.github.tifezh.kchartlib.chart.formatter.Value6Formatter;
import android.support.design.widget.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 合约详情
 * Created by zj on 2018/3/8.
 */

public class ContractTickerOneActivity extends BaseActivity implements
        View.OnClickListener,
        LogicTimer.ITimerListener,
        LogicWebSocketContract.IWebSocketListener {

    private int mContractId;
    private String mContractName;
    private Boolean mFullScreen;

    private CheckBox mCollectCb;
    private TextView mCollectTv;
    private ImageView mBackIv;
    private ImageView mShareIv;
    private TextView mTitleTv;
    private TextView mStockTypeTv;
    private TextView mAvgPriceTv;
    private TextView mUsdPriceTv;
    private TextView mRiseFallTv;
    private TextView mHighTv;
    private TextView mLowTv;
    private TextView mCirculationTv;
    private TextView mTotalValueTv;
    private ImageView mNextIv;

    private ImageView mFSCloseIv;
    private TextView mFSTitleTv;
    private TextView mFSAvgPriceTv;
    private TextView mFSUsdPriceTv;
    private TextView mFSRiseFallTv;
    private TextView mFSHighTv;
    private TextView mFSLowTv;
    private TextView mFSVolumTv;
    private ImageView mFSSelStockIv;

    private RadioButton mTabTime, mTab5Min, mTab15Min, mTab60Min, mTab1Day;
    private RadioButton mTabTimeFS, mTab5MinFS, mTab15MinFS, mTab60MinFS, mTab1DayFS;
    private int mCurrentTab;
    private RadioButton mTabMA, mTabEma, mTabBoll, mTabSar, mTabVol, mTabMACD, mTabKDJ, mTabRSI, mTabMTM, mTabWR, mTabCCI;

    private ImageView mFullScreenIv;
    private TextView mMoreTv;
    private TextView mMoreFSTv;

    private LinearLayout mSelectLL;
    private TextView mOpenCloseTv;
    private TextView mHighLowTv;
    private TextView mChgVolTv;
    private TextView mDateTv;

    private KChartView mKChartView;
    private KChartAdapter mKChartAdapter;

    private int mCurrentId;

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private OrderBookFragment mOrderBookFragment = new OrderBookFragment();
    private TradeHistoryFragment mTradeHistoryFragment = new TradeHistoryFragment();
    private DepthChartFragment mDepthChartFragment = new DepthChartFragment();
    //private CoinIntroduceFragment mCoinIntroduceFragment = new CoinIntroduceFragment();

    private TabLayout mTabLayout;
    private Button mSellBtn;
    private Button mBuyBtn;

    private ListView mSpotListView;
    private PopupWindow mSpotWindow;
    private DropContractAdapter mSpotAdapter;
    private View mSpotPopupView;

    private Animation mRotate;

    private Depth mDepth = new Depth();
    private List<SDStockTrade> mStockList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_contract_ticker_one);

        LogicTimer.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);
        try {
            mContractId = getIntent().getIntExtra("contract_id", 1);
            mFullScreen = getIntent().getBooleanExtra("full_screen", false);
        } catch (Exception ignored) {
        }

        setView();
        sendData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogicTimer.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            mContractId = getIntent().getIntExtra("contract_id", 1);
        } catch (Exception ignored) {
        }

        super.onNewIntent(intent);
    }

    @Override
    public void setView() {
        super.setView();
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }
        mContractName = contract.getSymbol();

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        mCollectTv = findViewById(R.id.tv_collect);

        mCollectCb = findViewById(R.id.cb_collect);
        Collect collect = LogicCollects.getInstance().get(contract.getSymbol());
        if (collect != null) {
            mCollectTv.setText(R.string.sl_str_added);
            mCollectTv.setTextColor(getResources().getColor(R.color.sl_colorYellowNormal));
            mCollectCb.setChecked(true);
        } else {
            mCollectTv.setText(R.string.sl_str_optional);
            mCollectTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
            mCollectCb.setChecked(false);
        }

        mCollectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Contract contract = LogicGlobal.getContract(mContractId);
                if (contract == null) {
                    return;
                }

                if (isChecked) {
                   // MobclickAgent.onEvent(LogicGlobal.sContext, "ss_ce");
                    Collect collect = new Collect();
                    collect.setName(contract.getSymbol());
                    collect.setTime(System.currentTimeMillis());
                    LogicCollects.getInstance().add(collect);
                    mCollectTv.setText(R.string.sl_str_added);
                    mCollectTv.setTextColor(getResources().getColor(R.color.sl_colorYellowNormal));
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_add_favorites_succeed));

                } else {
                    LogicCollects.getInstance().remove(contract.getSymbol());
                    mCollectTv.setText(R.string.sl_str_optional);
                    mCollectTv.setTextColor(getResources().getColor(R.color.sl_whiteText));
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_cancel_favorites_succeed));
                }
            }
        });

        mBuyBtn = findViewById(R.id.btn_buy);
        mBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   LogicBuySell.getInstance().switchTab(3, Integer.toString(mContractId));
                finish();
            }
        });
        mSellBtn = findViewById(R.id.btn_sell);
        mSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LogicBuySell.getInstance().switchTab(3, Integer.toString(mContractId));
                finish();
            }
        });

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareIv = findViewById(R.id.iv_share);
        mShareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dView = getWindow().getDecorView();
                dView.setDrawingCacheEnabled(false);
                dView.setDrawingCacheEnabled(true);
                //dView.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
                if (bitmap != null) {
//                    ShareDialog shareDialog = new ShareDialog(ContractTickerOneActivity.this);
//                    shareDialog.setType(1);
//                    shareDialog.setBmp(bitmap);
//                    shareDialog.show();
                }
            }
        });
        mTitleTv = findViewById(R.id.tv_title);
        mStockTypeTv = findViewById(R.id.tv_stock_type);
        mAvgPriceTv = findViewById(R.id.tv_last_price);
        mUsdPriceTv = findViewById(R.id.tv_usd_price);

        mRiseFallTv = findViewById(R.id.tv_rise_fall);
        mHighTv = findViewById(R.id.tv_high_value);
        mLowTv = findViewById(R.id.tv_low_value);
        mCirculationTv = findViewById(R.id.tv_volume_value);
        mTotalValueTv = findViewById(R.id.tv_total_value_value);
        mNextIv = findViewById(R.id.iv_next);
        mNextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(ContractTickerOneActivity.this, ContractDetailActivity.class);
                itnt.putExtra("contract_id", mContractId);
                startActivity(itnt);
            }
        });

        findViewById(R.id.ll_spot_ticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(ContractTickerOneActivity.this, ContractDetailActivity.class);
                itnt.putExtra("contract_id", mContractId);
                startActivity(itnt);
            }
        });

        mFSCloseIv = findViewById(R.id.iv_fs_close);
        mFSCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        });
        mFSTitleTv = findViewById(R.id.tv_fs_title);

        mFSAvgPriceTv = findViewById(R.id.tv_fs_last_price);
        mFSRiseFallTv = findViewById(R.id.tv_fs_rise_fall);
        mFSUsdPriceTv = findViewById(R.id.tv_fs_usd_price);
        mFSHighTv = findViewById(R.id.tv_fs_high);
        mFSLowTv = findViewById(R.id.tv_fs_low);
        mFSVolumTv = findViewById(R.id.tv_fs_volum);

        mRotate = AnimationUtils.loadAnimation(this, R.anim.sl_array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());

        mSelectLL = findViewById(R.id.ll_select);
        mSelectLL.setVisibility(View.GONE);
        mOpenCloseTv = findViewById(R.id.tv_open_close);
        mHighLowTv = findViewById(R.id.tv_high_low);
        mChgVolTv = findViewById(R.id.tv_chg_vol);
        mDateTv = findViewById(R.id.tv_date);

        mMoreTv = findViewById(R.id.tv_more);
        mMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DropKlineWindow window = new DropKlineWindow(ContractTickerOneActivity.this);
                window.setFocusable(true);
                window.showAsDropDown(findViewById(R.id.ll_tab), 0, 2);
                window.setOnKlineDropClick(new DropKlineWindow.OnDropKlineClickedListener() {
                    @Override
                    public void onKlineDropClick(int tab) {
                        window.dismiss();
                        doClick(tab);
                    }
                });
            }
        });

        mMoreFSTv = findViewById(R.id.tv_more_full_screen);
        mMoreFSTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DropKlineWindow window = new DropKlineWindow(ContractTickerOneActivity.this);
                window.setFocusable(true);
                window.showAsDropDown(findViewById(R.id.ll_tab_full_screen), 0, -2 - 2 * findViewById(R.id.ll_tab_full_screen).getHeight());
                window.setOnKlineDropClick(new DropKlineWindow.OnDropKlineClickedListener() {
                    @Override
                    public void onKlineDropClick(int tab) {
                        window.dismiss();
                        doClick(tab);
                    }
                });
            }
        });

        mFullScreenIv = findViewById(R.id.iv_full_screen);
        mFullScreenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
                if (isVertical) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
            }
        });

        mTabTime = findViewById(R.id.ktab_time);
        mTab5Min = findViewById(R.id.ktab_5min);
        mTab15Min = findViewById(R.id.ktab_15min);
        mTab60Min = findViewById(R.id.ktab_60min);
        mTab1Day = findViewById(R.id.ktab_1day);

        mTabTimeFS = findViewById(R.id.ktab_time_full_screen);
        mTab5MinFS = findViewById(R.id.ktab_5min_full_screen);
        mTab15MinFS = findViewById(R.id.ktab_15min_full_screen);
        mTab60MinFS = findViewById(R.id.ktab_60min_full_screen);
        mTab1DayFS = findViewById(R.id.ktab_1day_full_screen);

        mTabMA = findViewById(R.id.mtab_ma);
        mTabEma = findViewById(R.id.mtab_ema);
        mTabBoll = findViewById(R.id.mtab_boll);
        mTabSar = findViewById(R.id.mtab_sar);
        mTabVol = findViewById(R.id.mtab_vol);
        mTabMACD = findViewById(R.id.mtab_macd);
        mTabKDJ = findViewById(R.id.mtab_kdj);
        mTabRSI = findViewById(R.id.mtab_rsi);
        mTabMTM = findViewById(R.id.mtab_mtm);
        mTabWR = findViewById(R.id.mtab_wr);
        mTabCCI = findViewById(R.id.mtab_cci);

        mTabTime.setOnClickListener(this);
        mTab5Min.setOnClickListener(this);
        mTab15Min.setOnClickListener(this);
        mTab60Min.setOnClickListener(this);
        mTab1Day.setOnClickListener(this);
        mTabTimeFS.setOnClickListener(this);
        mTab5MinFS.setOnClickListener(this);
        mTab15MinFS.setOnClickListener(this);
        mTab60MinFS.setOnClickListener(this);
        mTab1DayFS.setOnClickListener(this);

        mTabMA.setOnClickListener(this);
        mTabEma.setOnClickListener(this);
        mTabBoll.setOnClickListener(this);
        mTabSar.setOnClickListener(this);
        mTabVol.setOnClickListener(this);
        mTabMACD.setOnClickListener(this);
        mTabKDJ.setOnClickListener(this);
        mTabRSI.setOnClickListener(this);
        mTabMTM.setOnClickListener(this);
        mTabWR.setOnClickListener(this);
        mTabCCI.setOnClickListener(this);

        SlLoadingDialog dialog = new SlLoadingDialog(this);

        mKChartView = findViewById(R.id.kchart_view);
        mKChartView.setProgressDlg(dialog.getDialog(), 300);
        mKChartAdapter = new KChartAdapter();
        mKChartView.setAdapter(mKChartAdapter);
        mKChartView.setMainDrawLine(true);
        mKChartView.setChildDraw(1);
        mKChartView.setValueFormatter(new Value6Formatter(contract.getPrice_index()));
        mKChartView.setDateTimeFormatter(new TimeFormatter());
        mKChartView.setGridRows(2);
        mKChartView.setGridColumns(4);
        mKChartView.setRefreshListener(new KChartView.KChartRefreshListener() {
            @Override
            public void onLoadMoreBegin(KChartView chart) {
                switchChart(mCurrentId, false);
            }
        });

        mKChartView.setOnClickViewListener(new BaseKChartView.OnClickViewListener() {
            @Override
            public void onClickView(int main, int index) {
                if (main == 0) {
                    if (index == 0) {
                        mTabMA.setChecked(true);
                    } else if (index == 1) {
                        mTabEma.setChecked(true);
                    } else if (index == 2) {
                        mTabBoll.setChecked(true);
                    } else if (index == 3) {
                        mTabSar.setChecked(true);
                    }
                } else if (main == 1) {
                    if (index == 0) {
                        mTabVol.setChecked(true);
                    } else if (index == 1) {
                        mTabMACD.setChecked(true);
                    } else if (index == 2) {
                        mTabKDJ.setChecked(true);
                    } else if (index == 3) {
                        mTabRSI.setChecked(true);
                    } else if (index == 4) {
                        mTabMTM.setChecked(true);
                    } else if (index == 5) {
                        mTabWR.setChecked(true);
                    } else if (index == 6) {
                        mTabCCI.setChecked(true);
                    }
                }
            }
        });

        mFragments = new ArrayList<>();
        mFragments.add(mOrderBookFragment);
        mFragments.add(mTradeHistoryFragment);
        mFragments.add(mDepthChartFragment);
        //mFragments.add(mCoinIntroduceFragment);

        String[] titles = new String[]{
                getString(R.string.sl_str_order_book),
                getString(R.string.sl_str_trade_history),
                getString(R.string.sl_str_depth_chart),
                //getString(R.string.str_coin_introduce),
        };

        BaseFragmentPagerAdapter adapter =
                new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments, titles);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);

        doClick(R.id.ktab_5min);

        if (mFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.ll_spot_ticker).setVisibility(View.GONE);
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            findViewById(R.id.tabs).setVisibility(View.GONE);
            findViewById(R.id.viewpager).setVisibility(View.GONE);
            findViewById(R.id.ll_buy_sell).setVisibility(View.GONE);
            findViewById(R.id.ll_tab).setVisibility(View.GONE);
            findViewById(R.id.rl_title).setVisibility(View.GONE);
            findViewById(R.id.rl_index).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_tab_full_screen).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_title_full_screen).setVisibility(View.VISIBLE);
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapse_toolbar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(0);
            collapsingToolbar.setLayoutParams(params);

            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) mKChartView.getLayoutParams();
            rlParams.height = DeviceUtil.getScreenHeight(this) - dp(116);
            mKChartView.setLayoutParams(rlParams);

            mKChartView.hideVolDraw();
            mTabVol.setChecked(true);

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.ll_spot_ticker).setVisibility(View.VISIBLE);
            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            findViewById(R.id.tabs).setVisibility(View.VISIBLE);
            findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_buy_sell).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_tab).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_title).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_index).setVisibility(View.GONE);
            findViewById(R.id.ll_tab_full_screen).setVisibility(View.GONE);
            findViewById(R.id.rl_title_full_screen).setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapse_toolbar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            collapsingToolbar.setLayoutParams(params);

            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) mKChartView.getLayoutParams();
            rlParams.height = dp(340);
            mKChartView.setLayoutParams(rlParams);

            mKChartView.showVolDraw();
            mTabVol.setChecked(false);
        }
    }

    private int dp(final int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void sendData() {

        if (mContractId <= 0) {
            return;
        }

        LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_DEPTH, mContractId);
        LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TICKER, 0);

        BTContract.getInstance().tickers(mContractId, new IResponse<List<ContractTicker>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractTicker> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                if (data != null && data.size() > 0) {
                    updateTickerData(data.get(0));
                }
            }
        });

        BTContract.getInstance().queryDepth(mContractId, 20, new IResponse<Depth>() {
            @Override
            public void onResponse(String errno, String message, Depth data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                mDepth = data;

                Depth datatemp = new Depth();
                if (data != null) {
                    if (data.getBids().size() > 0) {
                        datatemp.setBids(data.getBids().subList(0, Math.min(10, data.getBids().size())));
                    } else {
                        datatemp.setBids(data.getBids());
                    }

                    if (data.getAsks().size() > 0) {
                        datatemp.setAsks(data.getAsks().subList(0, Math.min(10, data.getAsks().size())));
                    } else {
                        datatemp.setAsks(data.getAsks());
                    }
                }

                mOrderBookFragment.setData(datatemp, "", mContractId);
                mDepthChartFragment.setData(data, "", mContractId);
            }
        });

        BTContract.getInstance().trades(mContractId, new IResponse<List<ContractTrade>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractTrade> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                List<SDStockTrade> list = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < Math.min(10, data.size()); i++) {
                        ContractTrade trade = data.get(i);
                        if (trade == null) {
                            continue;
                        }

                        SDStockTrade stockTrade = new SDStockTrade();
                        stockTrade.setCreated_at(trade.getCreated_at());
                        stockTrade.setPx(trade.getPx());
                        stockTrade.setQty(trade.getQty());
                        stockTrade.setFee(trade.getTake_fee());
                        stockTrade.setSide(trade.getSide() > 4 ? 2 : 1);
                        stockTrade.setContractId(trade.getInstrument_id());
                        list.add(stockTrade);
                    }
                }
                mStockList.clear();
                mStockList.addAll(list);
                mTradeHistoryFragment.setData(list);
            }
        });

    }

    private void updateTickerData(ContractTicker realtime_ticker) {

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null || realtime_ticker == null) {
            return;
        }

        if (mTitleTv == null || mFSTitleTv == null || mAvgPriceTv == null ||
                mFSAvgPriceTv == null || mUsdPriceTv == null || mFSUsdPriceTv == null ||
                mRiseFallTv == null || mFSRiseFallTv == null || mFSHighTv == null ||
                mFSLowTv == null || mFSVolumTv == null || mHighTv == null ||
                mLowTv == null || mCirculationTv == null || mTotalValueTv == null) {
            return;
        }

        String name = realtime_ticker.getName();
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

        mFSTitleTv.setText(name);

        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfRate = NumberUtil.getDecimal(2);

        double rise_fall_rate = MathHelper.round(Double.parseDouble(realtime_ticker.getChange_rate()) * 100, 2);
        double rise_fall_value = MathHelper.round(realtime_ticker.getChange_value(), contract.getPrice_index());
        String sRate = (rise_fall_rate >= 0) ? (dfRate.format(rise_fall_rate) + "%") : (dfRate.format(rise_fall_rate) + "%");
        String sValue = (rise_fall_value >= 0) ? ("+" + dfPrice.format(rise_fall_value)) : (dfPrice.format(rise_fall_value));
        int color = (rise_fall_rate >= 0) ? getResources().getColor(R.color.sl_colorGreen): getResources().getColor(R.color.sl_colorRed);

        double current = MathHelper.round(realtime_ticker.getLast_px(), contract.getPrice_index());
        double fairPrice = MathHelper.round(realtime_ticker.getFair_px(), contract.getPrice_index());
        double indexPrice = MathHelper.round(realtime_ticker.getIndex_px(), contract.getPrice_index());



        mAvgPriceTv.setText(NumberUtil.getDecimal(contract.getPrice_index() - 1).format(current));
        mAvgPriceTv.setTextColor(color);
        mFSAvgPriceTv.setText(NumberUtil.getDecimal(contract.getPrice_index() - 1).format(current));
        mFSAvgPriceTv.setTextColor(color);

//        double backUsd = MathHelper.round(LogicGlobal.sGlobalData.getCoin_price_usd(contract.getQuote_coin()), 6);
//        double backCny = backUsd * LogicGlobal.sUsdRateCNY;
//        double current_usd = MathHelper.round(MathHelper.mul(backUsd, current), 2);
//        double current_cny = MathHelper.round(MathHelper.mul(backCny, current), 2);
//        String sUsd = "≈$" + dfRate.format(current_usd);
//        String sCNY = "≈￥" + dfRate.format(current_cny);

      //  String coin_base = LogicLanguage.isZhEnv(LogicGlobal.sContext) ? sCNY : sUsd;

//        mUsdPriceTv.setText(coin_base);
//        mFSUsdPriceTv.setText(coin_base);

        mRiseFallTv.setText(sValue + "   " + sRate);
        mRiseFallTv.setTextColor(color);
        mFSRiseFallTv.setText(sRate);
        mFSRiseFallTv.setTextColor(color);


        double amount24 = MathHelper.round(realtime_ticker.getQty24(), contract.getVol_index());
        double position_size = MathHelper.round(realtime_ticker.getPosition_size(), contract.getValue_index());

        mFSHighTv.setText(dfPrice.format(fairPrice));
        mFSLowTv.setText(dfPrice.format(indexPrice));
        mFSVolumTv.setText(dfVol.format(amount24));

        mHighTv.setText(dfPrice.format(fairPrice));
        mLowTv.setText(dfPrice.format(indexPrice));

        double rate = MathHelper.mul(realtime_ticker.getFunding_rate(), "100");
        mCirculationTv.setText(NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)) + "%");

        mTotalValueTv.setText(dfVol.format(amount24));
    }

    private void updateMChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().getMinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpotMinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(true);
                mKChartView.setDateTimeFormatter(new TimeFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshEnd();

                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update1MinKChartData(final boolean forward) {

        if (forward || LogicContractSpot.getInstance().getMinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }

        LogicContractSpot.getInstance().updateSpotMinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new TimeFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update5MinKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get5MinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot5MinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new TimeFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update15MinKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get15MinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot15MinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new TimeFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update30MinKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get30MinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot30MinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new TimeFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update60MinKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get60MinuteLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot60MinuteData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new BigDateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update2HourKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get2HourLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot2HourData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new BigDateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update4HourKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get4HourLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }

        LogicContractSpot.getInstance().updateSpot4HourData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new BigDateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update6HourKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get6HourLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot6HourData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new BigDateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void update12HourKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().get12HourLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpot12HourData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new BigDateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void updateDayKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().getDayLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpotDayData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new DateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    private void updateWeekKChartData(final boolean forward) {
        if (forward || LogicContractSpot.getInstance().getWeekLine(mContractName).size() < 1000) {
            mKChartView.showLoading();
        }
        LogicContractSpot.getInstance().updateSpotWeekData(mContractId, forward, new IResponse<List<KLineEntity>>() {
            @Override
            public void onResponse(String errno, String message, List<KLineEntity> data) {

                mKChartView.setMainDrawLine(false);
                mKChartView.setDateTimeFormatter(new DateFormatter());
                mKChartAdapter.resetData(data);
                mKChartView.refreshComplete();
                if (forward) {
                    mKChartView.setScrollX(mKChartView.getMinScrollX());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        doClick(view.getId());
    }


    private void doClick(int id) {
        if (mCurrentId != id) {
            if (mCurrentId == R.id.ktab_time || mCurrentId == R.id.ktab_time_full_screen) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1M, mContractId);
            } else if (mCurrentId == R.id.ktab_5min || mCurrentId == R.id.ktab_5min_full_screen) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN5M, mContractId);
            } else if (mCurrentId == R.id.ktab_15min || mCurrentId == R.id.ktab_15min_full_screen) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN15M, mContractId);
            } else if (mCurrentId == R.id.ktab_60min || mCurrentId == R.id.ktab_60min_full_screen) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1H, mContractId);
            } else if (mCurrentId == R.id.ktab_1day || mCurrentId == R.id.ktab_1day_full_screen) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1D, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_1MIN) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1M, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_30MIN) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN30M, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_2HOUR) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN2H, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_4HOUR) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN4H, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_6HOUR) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN6H, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_12HOUR) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN12H, mContractId);
            } else if (mCurrentId == DropKlineWindow.KTAB_1WEEK) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1W, mContractId);
            }
        }

        mCurrentId = id;
        switchChart(id, true);
        if (id == R.id.ktab_time || id == R.id.ktab_time_full_screen) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1M, mContractId);
            if (mCurrentTab == R.id.ktab_time)
                mCurrentTab = R.id.ktab_time;
            setMoreChecked(false, "");
            mTabTime.setChecked(true);
            mTabTimeFS.setChecked(true);
        } else if (id == R.id.ktab_5min || id == R.id.ktab_5min_full_screen) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN5M, mContractId);
            if (mCurrentTab == R.id.ktab_5min)
                mCurrentTab = R.id.ktab_5min;
            setMoreChecked(false, "");
            mTab5Min.setChecked(true);
            mTab5MinFS.setChecked(true);
        } else if (id == R.id.ktab_15min || id == R.id.ktab_15min_full_screen) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN15M, mContractId);
            if (mCurrentTab == R.id.ktab_15min)
                mCurrentTab = R.id.ktab_15min;
            setMoreChecked(false, "");
            mTab15Min.setChecked(true);
            mTab15MinFS.setChecked(true);
        } else if (id == R.id.ktab_60min || id == R.id.ktab_60min_full_screen) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1H, mContractId);
            if (mCurrentTab == R.id.ktab_60min)
                mCurrentTab = R.id.ktab_60min;
            setMoreChecked(false, "");
            mTab60Min.setChecked(true);
            mTab60MinFS.setChecked(true);
        } else if (id == R.id.ktab_1day || id == R.id.ktab_1day_full_screen) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1D, mContractId);
            if (mCurrentTab == R.id.ktab_1day)
                mCurrentTab = R.id.ktab_1day;
            setMoreChecked(false, "");
            mTab1Day.setChecked(true);
            mTab1DayFS.setChecked(true);
        } else if (id == DropKlineWindow.KTAB_1MIN) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN1M, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_1MIN)
                mCurrentTab = DropKlineWindow.KTAB_1MIN;
            setMoreChecked(true, getString(R.string.sl_str_1min));
        } else if (id == DropKlineWindow.KTAB_30MIN) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN30M, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_30MIN)
                mCurrentTab = DropKlineWindow.KTAB_30MIN;
            setMoreChecked(true, getString(R.string.sl_str_30min));
        } else if (id == DropKlineWindow.KTAB_2HOUR) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN2H, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_2HOUR)
                mCurrentTab = DropKlineWindow.KTAB_2HOUR;
            setMoreChecked(true, getString(R.string.sl_str_2hour));
        } else if (id == DropKlineWindow.KTAB_4HOUR) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN4H, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_4HOUR)
                mCurrentTab = DropKlineWindow.KTAB_4HOUR;
            setMoreChecked(true, getString(R.string.sl_str_4hour));
        } else if (id == DropKlineWindow.KTAB_6HOUR) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN6H, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_6HOUR)
                mCurrentTab = DropKlineWindow.KTAB_6HOUR;
            setMoreChecked(true, getString(R.string.sl_str_6hour));
        } else if (id == DropKlineWindow.KTAB_12HOUR) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN12H, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_12HOUR)
                mCurrentTab = DropKlineWindow.KTAB_12HOUR;
            setMoreChecked(true, getString(R.string.sl_str_12hour));
        } else if (id == DropKlineWindow.KTAB_1WEEK) {
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_BIN12H, mContractId);
            if (mCurrentTab == DropKlineWindow.KTAB_1WEEK)
                mCurrentTab = DropKlineWindow.KTAB_1WEEK;
            setMoreChecked(true, getString(R.string.sl_str_1week));
        }
    }


    private void switchChart(int id, boolean forward) {
        if(id == R.id.ktab_time || id == R.id.ktab_time_full_screen){
            updateMChartData(forward);
        }else if(id == R.id.ktab_5min || id == R.id.ktab_5min_full_screen){
            update5MinKChartData(forward);
        }else if(id == R.id.ktab_15min || id == R.id.ktab_15min_full_screen){
            update15MinKChartData(forward);
        }else if(id == R.id.ktab_30min /**|| id == R.id.ktab_30min_full_screen**/){
            update30MinKChartData(forward);
        }else if(id == R.id.ktab_60min || id == R.id.ktab_60min_full_screen){
            update60MinKChartData(forward);
        }else if(id == R.id.ktab_1day || id == R.id.ktab_1day_full_screen){
            updateDayKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_1MIN){
            update1MinKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_30MIN){
            update30MinKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_2HOUR){
            update2HourKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_4HOUR){
            update4HourKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_6HOUR){
            update6HourKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_12HOUR){
            update12HourKChartData(forward);
        }else if(id == DropKlineWindow.KTAB_1WEEK){
            updateWeekKChartData(forward);
        }else if(id ==  R.id.mtab_ma){
            mKChartView.changeMainDrawType(Status.MA);
        }else if(id ==  R.id.mtab_ema){
            mKChartView.changeMainDrawType(Status.EMA);
        }else if(id ==  R.id.mtab_boll){
            mKChartView.changeMainDrawType(Status.BOLL);
        }else if(id ==  R.id.mtab_sar){
            mKChartView.changeMainDrawType(Status.SAR);
        }else if(id ==  R.id.mtab_vol){
            mKChartView.setChildDraw(0);
        }else if(id ==  R.id.mtab_macd){
            mKChartView.setChildDraw(1);
        }else if(id ==  R.id.mtab_kdj){
            mKChartView.setChildDraw(2);
        }else if(id ==  R.id.mtab_rsi){
            mKChartView.setChildDraw(3);
        }else if(id ==  R.id.mtab_mtm){
            mKChartView.setChildDraw(4);
        }else if(id ==  R.id.mtab_wr){
            mKChartView.setChildDraw(5);
        }else if(id ==  R.id.mtab_cci){
            mKChartView.setChildDraw(6);
        }
    }

    public void setMoreChecked(boolean checked, String text) {

        if (checked) {
            mTabTime.setChecked(false);
            mTab5Min.setChecked(false);
            mTab15Min.setChecked(false);
            mTab60Min.setChecked(false);
            mTab1Day.setChecked(false);
            mTabTimeFS.setChecked(false);
            mTab5MinFS.setChecked(false);
            mTab15MinFS.setChecked(false);
            mTab60MinFS.setChecked(false);
            mTab1DayFS.setChecked(false);

            mMoreTv.setText(text);
            mMoreFSTv.setText(text);
            mMoreTv.setTextColor(getResources().getColor(R.color.sl_colorTextSelector));
            mMoreTv.setBackgroundResource(R.drawable.sl_border_underline_blue_more);
            mMoreFSTv.setTextColor(getResources().getColor(R.color.sl_colorTextSelector));
            mMoreFSTv.setBackgroundResource(R.drawable.sl_border_underline_blue_more);
        } else {

            mMoreTv.setText(R.string.sl_str_more);
            mMoreFSTv.setText(R.string.sl_str_more);
            mMoreTv.setTextColor(getResources().getColor(R.color.sl_grayText));
            mMoreTv.setBackgroundResource(R.drawable.sl_icon_more);
            mMoreFSTv.setTextColor(getResources().getColor(R.color.sl_grayText));
            mMoreFSTv.setBackgroundResource(R.drawable.sl_icon_more);
        }
    }

    @Override
    public void onTimer(int times) {
        if (!LogicWebSocketContract.getInstance().isConnected()) {
            sendData();
            switchChart(mCurrentId, true);
        }
    }

    public void onDrawSelectText(CandleImpl data) {
        KLineEntity entity = (KLineEntity) data;

        mSelectLL.setVisibility(View.VISIBLE);

        int color = (entity.getChg() >= 0) ? getResources().getColor(R.color.sl_colorGreen) : getResources().getColor(R.color.sl_colorRed);
        mOpenCloseTv.setTextColor(color);
        mHighLowTv.setTextColor(color);
        mChgVolTv.setTextColor(color);

        DecimalFormat decimalFormat = new DecimalFormat(" ##0.00000000", new DecimalFormatSymbols(Locale.ENGLISH));
        mOpenCloseTv.setText(getString(R.string.str_o) + decimalFormat.format(entity.getOpenPrice()) + "\n" + getString(R.string.str_c) + decimalFormat.format(entity.getClosePrice()));
        mHighLowTv.setText(getString(R.string.str_h) + decimalFormat.format(entity.getHighPrice()) + "\n" + getString(R.string.str_l) + decimalFormat.format(entity.getLowPrice()));
        mChgVolTv.setText(getString(R.string.str_ch) + " " + entity.getChg() + "%" + "\n" + getString(R.string.str_v) + " " + entity.getVolume());

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd \nHH:mm");
        mDateTv.setText(fmt.format(entity.getDatetime()));
    }

    public void onReleaseLongPress() {
        mSelectLL.setVisibility(View.GONE);
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

    @Override
    public void onContractMessage(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            Contract contract = LogicGlobal.getContract(mContractId);
            if (contract == null) {
                return;
            }

            final String coinCode = contract.getSymbol();

            String group = jsonObject.optString("group");
            if (TextUtils.isEmpty(group)) {
                return;
            }

            String[] argGroup = group.split(":");
            if (argGroup.length == 2) {

                if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_TICKER)) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj == null) {
                        return;
                    }

                    int contractId = dataObj.optInt("contract_id");
                    if (mContractId != contractId) {
                        return;
                    }


                    ContractTicker originTicker = LogicGlobal.getContractTicker(mContractId);
                    if (originTicker == null) {
                        return;
                    }
                    originTicker.verifyFromJson(dataObj);
                    updateTickerData(originTicker);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_DEPTH)) {

                    if (mContractId != Integer.parseInt(argGroup[1])) {
                        return;
                    }

                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj == null) {
                        return;
                    }
                    int actionType = jsonObject.optInt("action");
                    Depth depth = new Depth();
                    depth.fromJson(dataObj);
                    if(actionType == 1){
                        mDepth.fromJson(dataObj);
                    }


                    if (mOrderBookFragment.isForeground()) { mOrderBookFragment.updateDateByType(depth,mDepth.clone(), actionType); }
                    if (mDepthChartFragment.isForeground()) { mDepthChartFragment.updateDateByType(depth, actionType); }
                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_TRADE)) {
                    if (mContractId != Integer.parseInt(argGroup[1])) {
                        return;
                    }

                    List<SDStockTrade> stockList = new ArrayList<>();
                    JSONArray tradeArray = jsonObject.optJSONArray("data");
                    if (tradeArray != null) {
                        for (int i = 0; i < tradeArray.length(); i++) {
                            JSONObject obj = tradeArray.getJSONObject(i);
                            if (obj == null) {
                                continue;
                            }

                            ContractTrade trade = new ContractTrade();
                            trade.fromJson(obj);

                            SDStockTrade stockTrade = new SDStockTrade();
                            stockTrade.setCreated_at(trade.getCreated_at());
                            stockTrade.setPx(trade.getPx());
                            stockTrade.setQty(trade.getQty());
                            stockTrade.setFee(trade.getTake_fee());
                            stockTrade.setSide(trade.getSide() > 4 ? 2 : 1);
                            stockTrade.setContractId(trade.getInstrument_id());
                            stockList.add(stockTrade);
                        }
                    }
                    mStockList.addAll(0, stockList);
                    if (mStockList.size() > 0) {
                        mStockList = mStockList.subList(0, Math.min(10, mStockList.size()));
                    }

                    mTradeHistoryFragment.setData(mStockList);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN1M)) {
                    if (mCurrentTab != DropKlineWindow.KTAB_1MIN && mCurrentTab != R.id.ktab_time) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().getMinuteLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN5M)) {
                    if (mCurrentTab != R.id.ktab_5min) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get5MinuteLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN15M)) {
                    if (mCurrentTab != R.id.ktab_15min) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get15MinuteLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN30M)) {
                    if (mCurrentTab != DropKlineWindow.KTAB_30MIN) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get30MinuteLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN1H)) {
                    if (mCurrentTab != R.id.ktab_60min) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get60MinuteLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN2H)) {
                    if (mCurrentTab != DropKlineWindow.KTAB_2HOUR) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get2HourLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_BIN4H)) {
                    if (mCurrentTab != DropKlineWindow.KTAB_4HOUR) {
                        return;
                    }
                    updateKLine(
                            jsonObject,
                            Integer.parseInt(argGroup[1]),
                            LogicContractSpot.getInstance().get4HourLine(coinCode),
                            mKChartAdapter,
                            mKChartView);

                } else {
                    return;
                }

            } else {
                return;
            }


        } catch (JSONException ignored) {
        }
    }

    @Override
    public void connectFail(String url, int reCount) {

    }

    @Override
    public void reConnectSuccess(String url, int reCount) {

    }

    private void updateKLine(JSONObject jsonObj, int contractId, List<KLineEntity> minuteLine, KChartAdapter adapter, KChartView view) {
        if (mContractId != contractId || minuteLine == null) {
            return;
        }

        try {
            JSONArray dataArray = jsonObj.optJSONArray("data");
            if (dataArray == null) {
                return;
            }

            List<KLineEntity> line = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                if (obj == null) {
                    continue;
                }

                ContractSpot item = new ContractSpot();
                item.fromJson(obj);

                KLineEntity entity = new KLineEntity();
                Date dt = new Date(item.getTimestamp() * 1000);
                entity.date = dt;
                entity.Open = (float) MathHelper.round(item.getOpen(), 8);
                entity.Close = (float)MathHelper.round(item.getClose(), 8);
                entity.High = (float)MathHelper.round(item.getHigh(), 8);
                entity.Low = (float)MathHelper.round(item.getLow(), 8);
                entity.Volume = (float)MathHelper.round(item.getQty(), 2);
                entity.Chg = (float)MathHelper.round(Double.parseDouble(item.getChange_rate()) * 100, 2);

                line.add(entity);
            }

            if (line.size() > 0 && minuteLine.size() > 0) {
                if (line.get(0).getDatetime().getTime() == minuteLine.get(minuteLine.size() - 1).getDatetime().getTime()) {
                    minuteLine.remove(minuteLine.size() - 1);
                }
            }
            minuteLine.addAll(line);
            DataHelper.calculate(minuteLine);

            adapter.resetData(minuteLine);
            view.refreshComplete();
        } catch (JSONException ignored) {
        }
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
            holder = (DropContractAdapter.DropSpotViewHolder) convertView.getTag();
            if (getItem(position) != null) {
                holder.tvStockCode.setText(getItem(position).getSymbol());
            }
            return convertView;
        }

        class DropSpotViewHolder {
            TextView tvStockCode;
        }
    }
}
