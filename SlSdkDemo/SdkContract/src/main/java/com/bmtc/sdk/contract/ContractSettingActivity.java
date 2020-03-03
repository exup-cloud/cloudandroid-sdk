package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.uilogic.LogicContractSetting;

/**
 *  合约设置
 * Created by zj on 2018/3/8.
 */

public class ContractSettingActivity extends BaseActivity {

    private ImageView mBackIv;

    private RadioButton mTabContractUnit, mTabCoinUnit;
    private RadioButton mTabFairPrice, mTabLatestPrice;
    private RadioButton mTab24hours, mTab7days;
    private RadioButton mTabExecutionLimitPrice, mTabExecutionMarketPrice;
    private RadioButton mTabTriggerLatestPrice, mTabTriggerFairPrice, mTabTriggerIndexPrice;

    private int mTradeUnit = 0;
    private int mPnlCalculate = 0;
    private int mExecution = 0; //0限价 1市价

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_contract_setting);

        mTradeUnit = LogicContractSetting.getContractUint(this);
        mPnlCalculate = LogicContractSetting.getPnlCalculate(this);
        mExecution = LogicContractSetting.getExecution(this);

        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTradeUnit != LogicContractSetting.getContractUint(this) ||
                mPnlCalculate != LogicContractSetting.getPnlCalculate(this) ||
                mExecution != LogicContractSetting.getExecution(this)) {
            LogicContractSetting.getInstance().refresh();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void setView() {
        super.setView();

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTabContractUnit = findViewById(R.id.tab_contracts_unit);
        mTabContractUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setContractUint(ContractSettingActivity.this, 0);
            }
        });
        mTabCoinUnit = findViewById(R.id.tab_coin_unit);
        mTabCoinUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setContractUint(ContractSettingActivity.this, 1);
            }
        });

        if (LogicContractSetting.getContractUint(this) == 0) {
            mTabContractUnit.setChecked(true);
        } else {
            mTabCoinUnit.setChecked(true);
        }

        mTabFairPrice = findViewById(R.id.tab_fair_price);
        mTabFairPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setPnlCalculate(ContractSettingActivity.this, 0);
            }
        });

        mTabLatestPrice = findViewById(R.id.tab_latest_price);
        mTabLatestPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setPnlCalculate(ContractSettingActivity.this, 1);
            }
        });

        if (LogicContractSetting.getPnlCalculate(this) == 0) {
            mTabFairPrice.setChecked(true);
        } else {
            mTabLatestPrice.setChecked(true);
        }

        mTab24hours = findViewById(R.id.tab_24hours);
        mTab24hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setStrategyEffectTime(ContractSettingActivity.this, 0);
            }
        });

        mTab7days = findViewById(R.id.tab_7days);
        mTab7days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setStrategyEffectTime(ContractSettingActivity.this, 1);
            }
        });

        if (LogicContractSetting.getStrategyEffectTime(this) == 0) {
            mTab24hours.setChecked(true);
        } else {
            mTab7days.setChecked(true);
        }

        mTabExecutionLimitPrice = findViewById(R.id.tab_execution_limit);
        mTabExecutionLimitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setExecution(ContractSettingActivity.this, 0);
            }
        });

        mTabExecutionMarketPrice = findViewById(R.id.tab_execution_market);
        mTabExecutionMarketPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setExecution(ContractSettingActivity.this, 1);
            }
        });

        if (LogicContractSetting.getExecution(this) == 0) {
            mTabExecutionLimitPrice.setChecked(true);
        } else {
            mTabExecutionMarketPrice.setChecked(true);
        }

        mTabTriggerLatestPrice = findViewById(R.id.tab_trigger_last_price);
        mTabTriggerLatestPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setTriggerPriceType(ContractSettingActivity.this, 0);
            }
        });

        mTabTriggerFairPrice = findViewById(R.id.tab_trigger_fair_price);
        mTabTriggerFairPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setTriggerPriceType(ContractSettingActivity.this, 1);
            }
        });

        mTabTriggerIndexPrice = findViewById(R.id.tab_trigger_index_price);
        mTabTriggerIndexPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogicContractSetting.setTriggerPriceType(ContractSettingActivity.this, 2);
            }
        });


        int triggerPriceType = LogicContractSetting.getTriggerPriceType(this);

        if (triggerPriceType == 0) {
            mTabTriggerLatestPrice.setChecked(true);
        } else if (triggerPriceType == 1) {
            mTabTriggerFairPrice.setChecked(true);
        } else if (triggerPriceType == 2) {
            mTabTriggerIndexPrice.setChecked(true);
        }
    }
}
