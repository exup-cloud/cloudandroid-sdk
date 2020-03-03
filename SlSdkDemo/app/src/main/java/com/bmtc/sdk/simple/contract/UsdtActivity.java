package com.bmtc.sdk.simple.contract;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.bmtc.sdk.contract.fragment.CoinContractFragment;
import com.bmtc.sdk.contract.fragment.TradeContractFragment;
import com.bmtc.sdk.contract.fragment.USDTContractFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.utils.LogUtil;
import com.bmtc.sdk.simple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * usdt合约
 */
public class UsdtActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private  USDTContractFragment usdtContractFragment;
    private CoinContractFragment coinContractFragment;
    private TradeContractFragment tradeContractFragment;

    private int from = 0 ; // 0 USDT 1 币本位  2 合约交易

    public static void show(Activity activity,int from){
        Intent intent = new Intent(activity,UsdtActivity.class);
        intent.putExtra("from",from);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_usdt);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        from = getIntent().getIntExtra("from",from);
        LogUtil.d("DEBUG","from:"+from);
        if(from == 0){
            usdtContractFragment = new USDTContractFragment();
            transaction
                    .add(R.id.fl_layout, usdtContractFragment);
        }else if(from == 1){
            coinContractFragment = new CoinContractFragment();
            transaction
                    .add(R.id.fl_layout, coinContractFragment);
        }else {
            tradeContractFragment = new TradeContractFragment();
            transaction
                    .add(R.id.fl_layout, tradeContractFragment);
        }
        transaction.commit();

        queryContractData();
    }

    private void queryContractData() {
        BTContract.getInstance().tickers(0, new IResponse<List<ContractTicker>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractTicker> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }
                if (data != null) {
                        if(from == 0){
                            usdtContractFragment.updateTicker(data);
                        }else if(from == 1){
                            coinContractFragment.updateTicker(data);
                        }else {
                            tradeContractFragment.updateStock(data.get(0).getInstrument_id());
                        }
                }
            }
        });
    }
}
