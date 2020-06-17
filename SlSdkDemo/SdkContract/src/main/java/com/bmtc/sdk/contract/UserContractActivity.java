package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.adapter.ContractAssetsAdapter;
import com.bmtc.sdk.contract.base.BaseActivity;
import com.bmtc.sdk.contract.uiLogic.LogicBuySell;
import com.bmtc.sdk.contract.utils.PreferenceManager;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractAccount;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.impl.ContractAccountListener;
import com.contract.sdk.impl.ContractPositionListener;
import com.contract.sdk.impl.ContractTickerListener;
import com.contract.sdk.impl.IResponse;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.ws.LogicWebSocketContract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public class UserContractActivity extends BaseActivity  {

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


        mCloseEye = PreferenceManager.getInstance(this).getSharedBoolean(PreferenceManager.PREF_CLOSEEYE, false);

        try {
        } catch (Exception ignored) {
        }


        /**
         * Ticker监听
         */
        ContractPublicDataAgent.INSTANCE.registerTickerWsListener(this, new ContractTickerListener() {
            @Override
            public void onWsContractTicker(@NotNull ContractTicker ticker) {
                setHeaderView();
            }
        });
        /**
         * 监听账户资产变动
         */
        ContractUserDataAgent.INSTANCE.registerContractAccountWsListener(this, new ContractAccountListener() {
            @Override
            public void onWsContractAccount(@Nullable ContractAccount contractAccount) {
                setHeaderView();
            }
        });
        /**
         * 监听仓位变化
         */
        ContractUserDataAgent.INSTANCE.registerContractPositionWsListener(this, new ContractPositionListener() {
            @Override
            public void onWsContractPosition() {
                setHeaderView();
            }
        });

        setView();
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
                //TODO 接入方 自己实现划转需求
//                Intent intent = new Intent();
//                intent.setClass(UserContractActivity.this, FundsTransferActivity.class);
//                startActivity(intent);
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
            mContractAssetAdapter.setData(ContractUserDataAgent.INSTANCE.getContractAccounts(true));
        }

        updateHeaderData();
    }

    private void updateHeaderData() {

        setHeaderView();

        if (!ContractSDKAgent.INSTANCE.isLogin()) {
            return;
        }
        //加载一次全量仓位
        ContractUserDataAgent.INSTANCE.getCoinPositions("",true);
    }

    private void setHeaderView() {

        List<ContractAccount> contractAccounts = ContractUserDataAgent.INSTANCE.getContractAccounts(false);
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

        List<ContractAccount> contractAccounts = ContractUserDataAgent.INSTANCE.getContractAccounts(false);
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

            List<ContractPosition> contractPositions = ContractUserDataAgent.INSTANCE.getCoinPositions(account.getCoin_code(),false);
            if (contractPositions != null && contractPositions.size() > 0) {
                for (int j = 0; j < contractPositions.size(); j++) {
                    ContractPosition contractPosition = contractPositions.get(j);
                    if (contractPosition == null) {
                        continue;
                    }

                    Contract positionContract = ContractPublicDataAgent.INSTANCE.getContract(contractPosition.getInstrument_id());
                    ContractTicker contractTicker = ContractPublicDataAgent.INSTANCE.getContractTicker(contractPosition.getInstrument_id());
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

}
