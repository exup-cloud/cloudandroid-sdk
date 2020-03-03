package com.bmtc.sdk.contract.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.ContractCalculateActivity;
import com.bmtc.sdk.contract.ContractOrderActivity;
import com.bmtc.sdk.contract.ContractSettingActivity;
import com.bmtc.sdk.contract.ContractTickerOneActivity;
import com.bmtc.sdk.contract.HtmlActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.dialog.DropContractMenuWindow;
import com.bmtc.sdk.contract.dialog.DropContractWindow;
import com.bmtc.sdk.contract.dialog.PromptWindowWide;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.trans.data.Depth;
import com.bmtc.sdk.library.trans.data.DepthData;
import com.bmtc.sdk.library.uilogic.LogicContractOrder;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicLanguage;
import com.bmtc.sdk.library.uilogic.LogicSDKState;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NetworkUtil;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.PreferenceManager;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 合约交易
 * Created by zj on 2018/3/1.
 */

public class TradeContractFragment extends BaseFragment implements
        LogicContractOrder.IContractOrderListener,
        LogicWebSocketContract.IWebSocketListener,
        View.OnClickListener {

    private View m_RootView;
    private RelativeLayout mNetworkRl;

    private RadioButton mTabOpenPosition, mTabClosePosition;
    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;

    private BuySellContractFragment mBuySellContractFragment;

    private RelativeLayout mStockRl;
    private TextView mStockCodeTv;
    private TextView mStockTypeTv;
    private ImageView mSelStockIv;
    private TextView mStockPriceTv;
    private TextView mStockRateTv;
    private RelativeLayout mRateRl;
    private ImageView mMenuIv;

    private int mContractId;

    private DrawerLayout mDrawerLayout;
    private TextView mTradeRecordTv;
    private TextView mContractSettingTv;
    private TextView mContractGuideTv;
    private TextView mContractCalculatorTv;

    private Animation mRotate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_trade_contract, null);

        LogicContractOrder.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);
        initViews(inflater, m_RootView);


        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            showGuide(mContractId);
            if (mContractId > 0) {
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_DEPTH, mContractId);
                LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TRADE, mContractId);
            } else {
                onContractBasicChanged();
            }
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TICKER, mContractId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogicContractOrder.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
    }

    private boolean isForeground() {
        return  getActivity() != null && isAdded()  && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, getActivity());
    }

    @Override
    public void onTimer(int times) {
        if (isForeground()) {
            if (!LogicWebSocketContract.getInstance().isConnected()) {
                updateData(mContractId, false);
            } else {
                createContractAccount(mContractId);
            }
        }

        if (getActivity() != null && isAdded()) {

            if (NetworkUtil.isConnected(LogicGlobal.sContext)) {
                mNetworkRl.setVisibility(View.GONE);
            } else {
                mNetworkRl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initViews(LayoutInflater inflater, View rootView) {
        mViewPager = rootView.findViewById(R.id.vp_list);

        mNetworkRl = m_RootView.findViewById(R.id.rl_no_network);
        mNetworkRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            }
        });


        mTabOpenPosition = m_RootView.findViewById(R.id.tab_open_position);
        mTabOpenPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBuySell(1);
            }
        });

        mTabClosePosition = m_RootView.findViewById(R.id.tab_close_position);
        mTabClosePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBuySell(2);
            }
        });

        if (mBuySellContractFragment == null) {
            mBuySellContractFragment = new BuySellContractFragment();
        }

        mRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.sl_array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());

        mStockRl = rootView.findViewById(R.id.rl_title);
        mStockCodeTv = rootView.findViewById(R.id.tv_stock_code);
        mStockCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mStockTypeTv = rootView.findViewById(R.id.tv_stock_type);
        mStockTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mSelStockIv = rootView.findViewById(R.id.iv_sel_stock_code);
        mSelStockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mStockPriceTv = rootView.findViewById(R.id.tv_stock_price);
        mStockRateTv = rootView.findViewById(R.id.tv_stock_rate);
        mRateRl = rootView.findViewById(R.id.rl_rate);
        mRateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContractId > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("contract_id", mContractId);
                    intent.setClass(getActivity(), ContractTickerOneActivity.class);
                    startActivity(intent);
                }
            }
        });

        mMenuIv = rootView.findViewById(R.id.iv_menu);
        mMenuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuWindow();
            }
        });

        mDrawerLayout = rootView.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mTradeRecordTv = rootView.findViewById(R.id.tv_trade_record);
        mTradeRecordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ContractOrderActivity.class);
//                intent.putExtra("contractId", mContractId);
//                startActivity(intent);
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        mContractSettingTv = rootView.findViewById(R.id.tv_contract_setting);
        mContractSettingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ContractSettingActivity.class);
//                startActivity(intent);
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        mContractGuideTv = rootView.findViewById(R.id.tv_contract_guide);
        mContractGuideTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Account account = BTAccount.getInstance().getActiveAccount();
//                if (account != null) {
//                    Intent intent = new Intent(getActivity(), HtmlActivity.class);
//                    if (LogicLanguage.isZhEnv(getActivity())) {
//                        intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE);
//                    } else {
//                        intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE);
//                    }
//                    intent.putExtra("title", getString(R.string.sl_str_contract_guide));
//                    startActivity(intent);
//                }
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        mContractCalculatorTv = rootView.findViewById(R.id.tv_contract_calculate);
        mContractCalculatorTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ContractCalculateActivity.class);
//                intent.putExtra("contractId", mContractId);
//                startActivity(intent);
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        mFragmentManager = getChildFragmentManager();

        mViewPager.setAdapter(new SampleFragmentPagerAdapter(mFragmentManager));
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0);
    }

    public void switchBuySell(int type) {
        if (mBuySellContractFragment != null) {
            mBuySellContractFragment.gotoTop();
            mBuySellContractFragment.switchBuySell(type, mTabOpenPosition, mTabClosePosition);
        }
    }

    private void changeContractId(int contractId) {
        if (contractId == mContractId) {
            return;
        }

        mContractId = contractId;

        if (mContractId > 0 && isForeground()) {
            if(!LogicWebSocketContract.getInstance().isConnected()){
                LogicWebSocketContract.getInstance().connect();
            }
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TICKER, mContractId);
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_DEPTH, mContractId);
            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TRADE, mContractId);
        }
    }

    public void updateStock(int contractId) {
        if (contractId <=0 || contractId == mContractId) {
            return;
        }

        changeContractId(contractId);
        updateData(contractId, true);
    }

    private void updateData(int contractId, boolean updatePrice) {
        if (contractId <= 0) {
            return;
        }
        updateSpot(contractId, updatePrice);
        updateDepth(contractId, updatePrice);
        createContractAccount(contractId);

        if (updatePrice) {
            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updateOpenOrder(contractId);
            }
        }
    }

    private boolean mCreateAccount = false;
    private void createContractAccount(final int contractId) {
        if (isForeground() && SLSDKAgent.isLogin() && !mCreateAccount && BTContract.getInstance().getAccountsRequested()) {
            Contract contract = LogicGlobal.getContract(contractId);
            if (contract == null) {
                return;
            }

            ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
            if (contractAccount == null) {

                mCreateAccount = true;
                String title = String.format(getString(R.string.sl_str_open_contract_account), contract.getMargin_coin());
                final PromptWindowWide window = new PromptWindowWide(getActivity());
                window.showTitle(title);
                window.showTvContent(getString(R.string.sl_str_risk_disclosure_notice));
                window.showBtnOk(getString(R.string.sl_str_open_contract_account_btn));
                window.showAtLocation(mViewPager, Gravity.CENTER, 0, 0);
                window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mCreateAccount = false;
                    }
                });
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        mCreateAccount = false;

                        BTContract.getInstance().createContractAccount(contractId, new IResponse<Void>() {
                            @Override
                            public void onResponse(String errno, String message, Void data) {
                                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                                    return;
                                }

                                ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_account_created_successfully));

                                showGuide(mContractId);

                                BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                                    @Override
                                    public void onResponse(String errno, String message, List<ContractAccount> data) {
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    private void updateSpot(int contractId, final boolean updatePrice) {
        ContractTicker ticker = LogicGlobal.getContractTicker(contractId);
        if (getActivity() != null && isAdded() && ticker != null) {
            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updateContract(ticker, updatePrice);
            }
            updateView(ticker);
        }

        BTContract.getInstance().tickers(contractId, new IResponse<List<ContractTicker>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractTicker> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                if (getActivity() != null && isAdded() && data != null && data.size() > 0) {
                    if (mBuySellContractFragment != null) {
                        mBuySellContractFragment.updateContract(data.get(0), updatePrice);
                    }
                    updateView(data.get(0));
                }

            }
        });
    }

    private void updateDepth(final int contractId, boolean refresh) {

        if (getActivity() != null && isAdded()) {
            if (mBuySellContractFragment != null && refresh) {
                Depth depth = new Depth();
                depth.setBids(new ArrayList<DepthData>());
                depth.setAsks(new ArrayList<DepthData>());
                mBuySellContractFragment.updateDepth(contractId, depth);
            }
        }

        BTContract.getInstance().queryDepth(contractId, 20, new IResponse<Depth>() {
            @Override
            public void onResponse(String errno, String message, Depth data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                if (getActivity() != null && isAdded()) {
                    if (mBuySellContractFragment != null) {
                        mBuySellContractFragment.updateDepth(contractId, data);
                    }
                }
            }
        });
    }

    private void updateView(ContractTicker data) {
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return ;
        }

        String name = contract.getSymbol();
        if (name.contains("[")) {
            name = name.substring(0, name.indexOf("["));
        }
        mStockCodeTv.setText(name);
        if (contract.getArea() == Contract.CONTRACT_BLOCK_USDT) {
            mStockTypeTv.setText("USDT");
        } else if (contract.getArea() == Contract.CONTRACT_BLOCK_SIMULATION){
            mStockTypeTv.setText(R.string.sl_str_simulation);
        } else if (contract.getArea() == Contract.CONTRACT_BLOCK_INNOVATION || contract.getArea() == Contract.CONTRACT_BLOCK_MAIN){
            mStockTypeTv.setText(R.string.sl_str_inverse);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));

        double rise_fall_rate = MathHelper.round(Double.parseDouble(data.getChange_rate()) * 100, 2);
        double rise_fall_value = MathHelper.round(data.getChange_value(), 2);
        String sRate = (rise_fall_rate >= 0) ? ("+" + decimalFormat.format(rise_fall_rate) + "%") : (decimalFormat.format(rise_fall_rate) + "%");
        int color = (rise_fall_rate >= 0) ? LogicGlobal.sContext.getResources().getColor(R.color.sl_colorGreen): LogicGlobal.sContext.getResources().getColor(R.color.sl_colorRed);


        double current_usd = MathHelper.round(data.getLast_px(), contract.getPrice_index());


        String text = NumberUtil.getDecimal(contract.getPrice_index()-1).format(current_usd);
//        text += LogicLanguage.isZhEnv(getActivity()) ? sCNY : sUsd;
//        text += " " + sRate;
        mStockPriceTv.setTextColor(color);
        mStockPriceTv.setText(text);

        mRateRl.setBackgroundResource(rise_fall_rate >= 0 ? R.drawable.sl_bg_corner_green_small : R.drawable.sl_bg_corner_red_small);
        mStockRateTv.setText(sRate);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    public void doSwitchPage(int index) {
        if (mBuySellContractFragment != null) {
            mBuySellContractFragment.switchBuySell(index + 1, mTabOpenPosition, mTabClosePosition);
        }
    }


    public void onContractBasicChanged() {
        List<Contract> contractBasics = LogicGlobal.getOnlineContractBasic();
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

        for (int i=0; i<contractBasics.size(); ) {
            changeContractId(contractBasics.get(i).getInstrument_id());
            updateData(mContractId, true);
            break;
        }
    }

    @Override
    public void onContractOrderSubmit(ContractOrder order) {

        if (!LogicWebSocketContract.getInstance().isConnected()) {
            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updateOpenOrder(mContractId);
                mBuySellContractFragment.updateUserAsset();
                mBuySellContractFragment.updateInfoValue(true);
            }

            if (mContractId == order.getInstrument_id()) {
                updateDepth(mContractId, false);
            }
        } else {

            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updatePlanOrder(mContractId);
            }
        }
    }

    @Override
    public void onContractOrderCancel(ContractOrder order) {

        if (!LogicWebSocketContract.getInstance().isConnected()) {

            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updateOpenOrder(mContractId);
                mBuySellContractFragment.updateUserAsset();
                mBuySellContractFragment.updateInfoValue(true);
            }

            if (order == null) {
                updateDepth(mContractId, false);
                return;
            }

            if (mContractId == order.getInstrument_id()) {
                updateDepth(mContractId, false);
            }
        } else {

            if (mBuySellContractFragment != null) {
                mBuySellContractFragment.updatePlanOrder(mContractId);
            }
        }
    }

    @Override
    public void onContractMessage(String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);

            String action = jsonObject.optString("action");
            String group = jsonObject.optString("group");
            if (TextUtils.isEmpty(group)) {
                return;
            }

            String[] argGroup = group.split(":");
            if (argGroup.length == 1) {
                if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_CUD)) {

                    if (mViewPager != null) {
                        mViewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mBuySellContractFragment != null) {
                                    mBuySellContractFragment.updateInfoValue(true);
                                }
                            }
                        }, 500);
                    }

                }

            } else if (argGroup.length == 2) {
                if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_TICKER)) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj == null) {
                        return;
                    }
                    if (mContractId != Integer.parseInt(argGroup[1]) ) {
                        return;
                    }

                    ContractTicker originTicker =  LogicGlobal.getContractTicker(mContractId);
                    if(originTicker == null){
                        return;
                    }
                    originTicker.verifyFromJson(dataObj);
                    if (mBuySellContractFragment != null) {
                        mBuySellContractFragment.updateContract(originTicker, false);
                    }
                    updateView(originTicker);

                } else if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_DEPTH)) {

                    if (mContractId != Integer.parseInt(argGroup[1]) ) {
                        if (isForeground()) {
                            LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, LogicWebSocketContract.WEBSOCKET_DEPTH, Integer.parseInt(argGroup[1]));
                        }
                        return;
                    }

                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj == null) {
                        return;
                    }
                    int actionType = jsonObject.optInt("action");
                    Depth depth = new Depth();
                    depth.fromJson(dataObj);
                    if (mBuySellContractFragment != null) {
                        mBuySellContractFragment.updateDepthByType(depth, actionType);
                    }

                }else if(TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_TRADE)){
                    if (mContractId != Integer.parseInt(argGroup[1]) ) {
                        return;
                    }
                    int actionType = jsonObject.optInt("action");
                    if(actionType == 4){
                        ContractTicker originTicker =  LogicGlobal.getContractTicker(mContractId);
                        if(originTicker == null){
                            return;
                        }
                        JSONArray dataObj = jsonObject.optJSONArray("data");
                        if (dataObj == null || dataObj.length() == 0) {
                            return;
                        }
                        originTicker.verifyFromJson(dataObj.optJSONObject(0));
                        if (mBuySellContractFragment != null) {
                            mBuySellContractFragment.updateContract(originTicker, false);
                        }
                        updateView(originTicker);
                    }
                } else {
                    if (TextUtils.isEmpty(action) && isForeground()) {
                        LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_UNSUBSCRIBE, argGroup[0], Integer.parseInt(argGroup[1]));
                    }
                }

            } else {
                return;
            }

        } catch (JSONException ignored) {
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        public SampleFragmentPagerAdapter(FragmentManager fm ) {
            super(fm);
            mFragments.add(mBuySellContractFragment);
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

    private void showSpotWindow() {

        final DropContractWindow window = new DropContractWindow(getActivity());
        window.setFocusable(true);
        window.showAsDropDown(mStockRl, 0, 2);
        window.setOnContractDropClick(new DropContractWindow.OnDropClickedListener() {
            @Override
            public void onContractDropClick(int contractId) {
                if (window != null) {
                    window.dismiss();
                    changeContractId(contractId);
                    updateData(contractId, true);
                }
            }
        });
    }

    private void showMenuWindow() {

        final DropContractMenuWindow window = new DropContractMenuWindow(getActivity());
        window.setFocusable(true);
        window.showAsDropDown(mMenuIv, -2, 2);
        window.setOnContractMenuClick(new DropContractMenuWindow.OnDropContractMenuClickedListener() {
            @Override
            public void onContractMenuClick(int tab) {
                if (window != null) {
                    window.dismiss();
                    if (tab == 0) {//交易记录
                        if (!SLSDKAgent.isLogin()) {
                            LogicSDKState.getInstance().refresh(LogicSDKState.STATE_LOGIN);
                        } else {
                            Intent intent = new Intent(getActivity(), ContractOrderActivity.class);
                            intent.putExtra("contractId", mContractId);
                            startActivity(intent);
                        }
                    } else if (tab == 1) {//合约设置
                        Intent intent = new Intent(getActivity(), ContractSettingActivity.class);
                        startActivity(intent);
                    } else if (tab == 2) {
                        Intent intent = new Intent(getActivity(), HtmlActivity.class);
                        if (LogicLanguage.isZhEnv(getActivity())) {
                            intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE);
                        } else {
                            intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE);
                        }
                        intent.putExtra("title", getString(R.string.sl_str_contract_guide));
                        startActivity(intent);
                    } else if (tab == 3) {
                        Intent intent = new Intent(getActivity(), ContractCalculateActivity.class);
                        intent.putExtra("contractId", mContractId);
                        startActivity(intent);
                    } else if (tab == 4) {
//                        Intent intent = new Intent(getActivity(), SwitchLineActivity.class);
//                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void showGuide(final int contractId) {
        mMenuIv.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean guide = PreferenceManager.getInstance(LogicGlobal.sContext).getSharedBoolean(PreferenceManager.PREF_CONTRACT_DEPOSIT_GUIDE, true);
                if (guide && isForeground()) {
                    if (mMenuIv != null) {
                        final Contract contract = LogicGlobal.getContract(contractId);
                        if (contract == null) {
                            return;
                        }

                        ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
                        if (contractAccount == null) {
                            return;
                        }

//                        Account account = BTAccount.getInstance().getActiveAccount();
//                        if (account != null) {
//                            double bb_balance = account.getBalance("USD");
//                            double ct_balance = UserContractActivity.calculateTotalBalance();
//
//                            if (bb_balance <= 0.0 && ct_balance <= 0.0) {
//                                String tips = String.format(getString(R.string.sl_str_contract_deposit_guide1));
//
//                                SpannableStringBuilder ssb= new SpannableStringBuilder(tips);
//                                int length = tips.length();
//                                int begin1 = tips.indexOf(" ");
//                                int end1 = tips.indexOf("2");
//                                int begin2 = tips.lastIndexOf(" ");
//                                int end2 = tips.indexOf("3");
//
//                                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grayText)), begin1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                ssb.setSpan(new AbsoluteSizeSpan(14, true), begin1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grayText)),begin2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                ssb.setSpan(new AbsoluteSizeSpan(14, true),begin2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                                final PromptWindow window = new PromptWindow(getActivity());
//                                window.showTitle(getString(R.string.sl_str_contract_deposit_title_guide1));
//                                window.showTvContent(ssb);
//                                window.showBtnOk(getString(R.string.sl_str_quick_deposit));
//                                window.showBtnCancel(getString(R.string.sl_str_cancel));
//                                window.showAtLocation(mMenuIv, Gravity.CENTER, 0, 0);
//                                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        LogicBuySell.getInstance().switchTab(5, "");
//                                        window.dismiss();
//                                    }
//                                });
//                                window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        window.dismiss();
//                                    }
//                                });
//
//                                PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_CONTRACT_DEPOSIT_GUIDE, false);
//
//                            } else if (bb_balance > 0.0 && ct_balance <= 0.0) {
//
//                                final String content = String.format(getString(R.string.sl_str_contract_deposit_guide2), contract.getMargin_coin());
//                                final PromptWindow window = new PromptWindow(getActivity());
//                                window.showTitle(getString(R.string.sl_str_contract_deposit_title_guide2));
//                                window.showTvContent(content);
//                                window.showBtnOk(getString(R.string.sl_str_transfer));
//                                window.showBtnCancel(getString(R.string.sl_str_cancel));
//                                window.showAtLocation(mMenuIv, Gravity.CENTER, 0, 0);
//                                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Account account = BTAccount.getInstance().getActiveAccount();
//                                        if (account != null) {
//                                            if (TextUtils.isEmpty(account.getPhone())) {
//                                                if (account.getStatus() == 1) {     //not active
//                                                    Intent intent = new Intent(getActivity(), BindActivity.class);
//                                                    startActivity(intent);
//                                                } else {
//                                                    Intent intent = new Intent();
//                                                    intent.putExtra("coin_code", contract.getMargin_coin());
//                                                    intent.setClass(getActivity(), FundsTransferActivity.class);
//                                                    startActivity(intent);
//                                                }
//                                            } else {
//                                                Intent intent = new Intent();
//                                                intent.setClass(getActivity(), FundsTransferActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        } else {
//                                            BTAccount.getInstance().doLogin(getActivity(), "");
//                                        }
//                                        window.dismiss();
//                                    }
//                                });
//                                window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        window.dismiss();
//                                    }
//                                });
//
//                               PreferenceManager.getInstance(LogicGlobal.sContext).putSharedBoolean(PreferenceManager.PREF_CONTRACT_DEPOSIT_GUIDE, false);
//                            } else {
//                                return;
//                            }
//
//                        } else {
//                            return;
//                        }

                    }
                }
            }
        }, 1000);

    }
}
