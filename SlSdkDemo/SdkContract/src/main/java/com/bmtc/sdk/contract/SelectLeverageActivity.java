package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.common.dialog.PromptWindow;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.uilogic.LogicContractSetting;
import com.bmtc.sdk.library.uilogic.LogicGlobal;

import java.util.HashMap;
import java.util.Map;

/**
 * 杠杆倍数
 * Created by zj on 2018/3/8.
 */

public class SelectLeverageActivity extends BaseActivity {

    private RelativeLayout mFull100Rl;
    private RelativeLayout mGradully100Rl;
    private RelativeLayout mGradully50Rl;
    private RelativeLayout mGradully20Rl;
    private RelativeLayout mGradully10Rl;

    private TextView mFull100Tv;
    private TextView mGradully100Tv;
    private TextView mGradully50Tv;
    private TextView mGradully20Tv;
    private TextView mGradully10Tv;

    private ImageView mFull100Iv;
    private ImageView mGradully100Iv;
    private ImageView mGradully50Iv;
    private ImageView mGradully20Iv;
    private ImageView mGradully10Iv;

    private ImageView mHelpIv;
    private ImageView mBackIv;

    private int mContractId;
    private int mLeverage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_import_account);

        try {
            mContractId = getIntent().getIntExtra("contractId", 0);
            mLeverage = getIntent().getIntExtra("leverage", 0);
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
            mLeverage = getIntent().getIntExtra("leverage", 0);
        } catch (Exception ignored) {}
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

        mHelpIv = findViewById(R.id.iv_help);
        mHelpIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PromptWindow window = new PromptWindow(SelectLeverageActivity.this);
                window.showTitle(getString(R.string.sl_str_full_gradually));
                window.showTvContent(getString(R.string.sl_str_full_gradually_intro));
                window.showBtnOk(getString(R.string.sl_str_isee));
                window.showBtnClose("");
                window.showAtLocation(mHelpIv, Gravity.CENTER, 0, 0);
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

        mFull100Tv = findViewById(R.id.tv_full_100);
        mGradully100Tv = findViewById(R.id.tv_gradually_100);
        mGradully50Tv = findViewById(R.id.tv_gradually_50);
        mGradully20Tv = findViewById(R.id.tv_gradually_20);
        mGradully10Tv = findViewById(R.id.tv_gradually_10);

        mFull100Tv.setText(getString(R.string.sl_str_full_position) + "100X");
        mGradully100Tv.setText(getString(R.string.sl_str_gradually_position) + "100X");
        mGradully50Tv.setText(getString(R.string.sl_str_gradually_position) + "50X");
        mGradully20Tv.setText(getString(R.string.sl_str_gradually_position) + "20X");
        mGradully10Tv.setText(getString(R.string.sl_str_gradually_position) + "10X");

        mFull100Iv = findViewById(R.id.iv_full_100);
        mGradully100Iv = findViewById(R.id.iv_gradually_100);
        mGradully50Iv = findViewById(R.id.iv_gradually_50);
        mGradully20Iv = findViewById(R.id.iv_gradually_20);
        mGradully10Iv = findViewById(R.id.iv_gradually_10);

        mFull100Rl = findViewById(R.id.rl_full_100);
        mFull100Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage(mFull100Iv);
                LogicContractSetting.getInstance().refreshLeverage(0, mFull100Tv.getText().toString());
                finish();
            }
        });
        mGradully100Rl = findViewById(R.id.rl_gradually_100);
        mGradully100Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage(mGradully100Iv);
                LogicContractSetting.getInstance().refreshLeverage(100, mGradully100Tv.getText().toString());
                finish();
            }
        });
        mGradully50Rl = findViewById(R.id.rl_gradually_50);
        mGradully50Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage(mGradully50Iv);
                LogicContractSetting.getInstance().refreshLeverage(50, mGradully50Tv.getText().toString());
                finish();
            }
        });
        mGradully20Rl = findViewById(R.id.rl_gradually_20);
        mGradully20Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage(mGradully20Iv);
                LogicContractSetting.getInstance().refreshLeverage(20, mGradully20Tv.getText().toString());
                finish();
            }
        });
        mGradully10Rl = findViewById(R.id.rl_gradually_10);
        mGradully10Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLeverage(mGradully10Iv);
                LogicContractSetting.getInstance().refreshLeverage(10, mGradully10Tv.getText().toString());
                finish();
            }
        });

        mGradully100Rl.setVisibility(View.GONE);
        mGradully50Rl.setVisibility(View.GONE);
        mGradully20Rl.setVisibility(View.GONE);
        mGradully10Rl.setVisibility(View.GONE);

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int minLeverage = Integer.parseInt(contract.getMin_leverage());
        int maxLeverage = Integer.parseInt(contract.getMax_leverage());

        Map<Integer, ImageView> leverageMap = new HashMap<>();
        leverageMap.put(0, mFull100Iv);
        leverageMap.put(100, mGradully100Iv);
        leverageMap.put(50, mGradully50Iv);
        leverageMap.put(20, mGradully20Iv);
        leverageMap.put(10, mGradully10Iv);

        mFull100Tv.setText(getString(R.string.sl_str_full_position) + maxLeverage + "X");
        if (100 <= maxLeverage && 100 >= minLeverage) mGradully100Rl.setVisibility(View.VISIBLE);
        if (50 <= maxLeverage && 50 >= minLeverage) mGradully50Rl.setVisibility(View.VISIBLE);
        if (20 <= maxLeverage && 20 >= minLeverage) mGradully20Rl.setVisibility(View.VISIBLE);
        if (10 <= maxLeverage && 10 >= minLeverage) mGradully10Rl.setVisibility(View.VISIBLE);

        ImageView imageView = leverageMap.get(mLeverage);
        selectLeverage(imageView);
    }

    private void selectLeverage(ImageView imageView) {
        mFull100Iv.setVisibility(View.GONE);
        mGradully100Iv.setVisibility(View.GONE);
        mGradully50Iv.setVisibility(View.GONE);
        mGradully20Iv.setVisibility(View.GONE);
        mGradully10Iv.setVisibility(View.GONE);

        imageView.setVisibility(View.VISIBLE);
    }

}
