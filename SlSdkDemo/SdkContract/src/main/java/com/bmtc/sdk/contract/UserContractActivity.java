package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.adapter.ContractAssetsAdapter;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractPosition;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicBuySell;
import com.bmtc.sdk.library.uilogic.LogicContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Created by zj on 2018/3/8.
 */

public class UserContractActivity extends BaseActivity implements
        LogicWebSocketContract.IWebSocketListener,
        LogicContractTicker.IContractTickerListener {

    private ImageView mBackIv;


    private CheckBox mCloseEyesCb;
    private TextView mBalanceTv;
    private TextView mEquivalentTv;

    private RelativeLayout mOrdersRl;
    private RelativeLayout mTransferRl;


    private RecyclerView mRecyclerView;
    private ContractAssetsAdapter mContractAssetAdapter;
    private LinearLayoutManager linearLayoutManager;

    private ImageView mNoResultIv;

    private boolean mCloseEye;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_fragment_user_contract);

        LogicWebSocketContract.getInstance().registListener(this);
        LogicContractTicker.getInstance().registListener(this);

        mCloseEye = PreferenceManager.getInstance(this).getSharedBoolean(PreferenceManager.PREF_CLOSEEYE, false);

        try {
        } catch (Exception ignored) {
        }

        LogicWebSocketContract.getInstance().send(LogicWebSocketContract.ACTION_SUBSCRIBE, LogicWebSocketContract.WEBSOCKET_TICKER, 0);

        setView();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        LogicWebSocketContract.getInstance().unregistListener(this);
        LogicContractTicker.getInstance().unregistListener(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
        } catch (Exception ignored) {
        }
    }

    public void setView() {

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mNoResultIv = findViewById(R.id.iv_noresult);

        findViewById(R.id.tv_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserContractActivity.this, FundsFlowActivity.class);
                startActivity(intent);
            }
        });

        mCloseEyesCb = findViewById(R.id.cb_close_eyes);
        mCloseEyesCb.setChecked(mCloseEye);
        mCloseEyesCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCloseEye = isChecked;
                PreferenceManager.getInstance(UserContractActivity.this).putSharedBoolean(PreferenceManager.PREF_CLOSEEYE, mCloseEye);
                updateHeaderData();
            }
        });

        mBalanceTv = findViewById(R.id.tv_balance);
        mEquivalentTv = findViewById(R.id.tv_balance_cny);

        mOrdersRl = findViewById(R.id.rl_orders);
        mOrdersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicBuySell.getInstance().switchTab(3, "0");
                finish();
            }
        });

        mTransferRl = findViewById(R.id.rl_transfer);
        mTransferRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UserContractActivity.this, FundsTransferActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (mContractAssetAdapter == null) {
            mContractAssetAdapter = new ContractAssetsAdapter(this);
            mRecyclerView.setAdapter(mContractAssetAdapter);
            mContractAssetAdapter.setData(BTContract.getInstance().getContractAccountList());
        }

        updateHeaderData();
    }

    private void updateHeaderData() {

        setHeaderView();

        if (!SLSDKAgent.isLogin()) {
            return;
        }

        BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractAccount> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }
                setHeaderView();
            }
        });

        BTContract.getInstance().userPositions(0, 1, 0, 0, new IResponse<List<ContractPosition>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractPosition> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }
                setHeaderView();
            }
        });
    }

    private void setHeaderView() {

        List<ContractAccount> contractAccounts = BTContract.getInstance().getContractAccountList();
        if (contractAccounts == null || contractAccounts.size() == 0) {
            mNoResultIv.setVisibility(View.VISIBLE);
            return;
        }

        mNoResultIv.setVisibility(View.GONE);
        mContractAssetAdapter.setData(contractAccounts);
        mContractAssetAdapter.notifyDataSetChanged();

        if (mCloseEye) {
            //mBasicInfoTv.setText(getString(R.string.str_margin_occupancy_rate) + ":" + "****");
            mBalanceTv.setText("****");
            mEquivalentTv.setText("****");
        } else {
            DecimalFormat decimalTwo = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.ENGLISH));

            double total_balance = calculateTotalBalance();
            double sUsdRateCNY = 1.0;//  TODO  此处取现货人民币费率进行转换
            double total_cny = total_balance * sUsdRateCNY;

            mBalanceTv.setText(decimalTwo.format(total_balance) + " USDT");
            mEquivalentTv.setText("≈" + decimalTwo.format(total_cny) + " CNY");
        }

    }


    static public double calculateTotalBalance() {

        List<ContractAccount> contractAccounts = BTContract.getInstance().getContractAccountList();
        if (contractAccounts == null) {
            return 0;
        }

        double total_balance = 0.0;
        for (int i=0; i<contractAccounts.size(); i++) {
            ContractAccount account = contractAccounts.get(i);
            if (account == null) {
                continue;
            }

            double freeze_vol = MathHelper.round(account.getFreeze_vol());
            double available_vol = MathHelper.round(account.getAvailable_vol());

            double longProfitAmount = 0.0; //多仓位的未实现盈亏
            double shortProfitAmount = 0.0; //空仓位的未实现盈亏

            double position_margin = 0.0;

            List<ContractPosition> contractPositions = BTContract.getInstance().getCoinPositions(account.getCoin_code());
            if (contractPositions != null && contractPositions.size() > 0) {
                for (int j = 0; j < contractPositions.size(); j++) {
                    ContractPosition contractPosition = contractPositions.get(j);
                    if (contractPosition == null) {
                        continue;
                    }

                    Contract positionContract = LogicGlobal.getContract(contractPosition.getInstrument_id());
                    ContractTicker contractTicker = LogicGlobal.getContractTicker(contractPosition.getInstrument_id());
                    if (positionContract == null || contractTicker == null) {
                        continue;
                    }

                    position_margin += MathHelper.round(contractPosition.getIm());

                    if (contractPosition.getSide() == 1) { //开多
                        longProfitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                                contractPosition.getCur_qty(),
                                contractPosition.getAvg_cost_px(),
                                contractTicker.getFair_px(),
                                positionContract.getFace_value(),
                                positionContract.isReserve());
                    } else if (contractPosition.getSide() == 2) { //开空
                        shortProfitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                                contractPosition.getCur_qty(),
                                contractPosition.getAvg_cost_px(),
                                contractTicker.getFair_px(),
                                positionContract.getFace_value(),
                                positionContract.isReserve());
                    }
                }
            }

            double balance = MathHelper.add(freeze_vol, available_vol);

            double total = balance + position_margin + longProfitAmount + shortProfitAmount;
            //TODO 此处应该取现货的费率进行USDT转换
            double rate = 1.0;
            if(TextUtils.equals("USDT",account.getCoin_code())){
                total_balance += total;
            }else {
                total_balance += MathHelper.mul(total, rate);
            }
        }

        return  total_balance;
    }

    @Override
    public void onContractTickerChanged(ContractTicker ticker) {
        if (ticker == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(ticker.getInstrument_id());
        if (contract == null) {
            return;
        }

        if (BTContract.getInstance().getCoinPositions(contract.getMargin_coin()) != null) {
            setHeaderView();
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

                    JSONArray dataArray = jsonObject.optJSONArray("data");
                    if (dataArray == null) {
                        return;
                    }

                    boolean update = false;
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.optJSONObject(i);
                        if (obj == null) {
                            continue;
                        }

                        JSONObject positionObj = obj.optJSONObject("position");
                        if (positionObj == null) {
                            continue;
                        }

                        update = true;
                    }

                    for (int i = dataArray.length() - 1; i >= 0; i--) {
                        JSONObject obj = dataArray.optJSONObject(i);
                        if (obj == null) {
                            break;
                        }

                        JSONObject assetObj = obj.optJSONObject("c_assets");
                        if (assetObj == null) {
                            break;
                        }

                        update = true;
                        break;
                    }

                    if (update) {
                        setHeaderView();
                    }
                }

            } else {
                return;
            }


        } catch (JSONException ignored) {
        }
    }
}
