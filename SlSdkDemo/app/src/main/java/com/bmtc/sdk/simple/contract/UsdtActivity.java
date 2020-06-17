package com.bmtc.sdk.simple.contract;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;


import com.bmtc.sdk.contract.fragment.CoinContractFragment;
import com.bmtc.sdk.contract.fragment.TradeContractFragment;
import com.bmtc.sdk.contract.fragment.USDTContractFragment;
import com.bmtc.sdk.simple.R;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.impl.IResponse;
import com.contract.sdk.utils.SDKLogUtil;

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
        SDKLogUtil.INSTANCE.d("DEBUG","from:"+from);
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
        transaction.commitNow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryContractData();
            }
        },1000);
    }

    private void queryContractData() {
        List<ContractTicker> data =   ContractPublicDataAgent.INSTANCE.getContractTickers();

        if(from == 0){
            usdtContractFragment.updateTicker(data);
        }else if(from == 1){
            coinContractFragment.updateTicker(data);
        }else {
            tradeContractFragment.updateStock(data.get(0).getInstrument_id());
        }
    }
}
