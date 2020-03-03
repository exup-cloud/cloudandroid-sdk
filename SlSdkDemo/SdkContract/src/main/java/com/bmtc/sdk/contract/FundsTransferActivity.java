package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractTransfer;
import com.bmtc.sdk.library.trans.data.SpotCoin;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Created by zj on 2018/3/8.
 */

public class FundsTransferActivity extends BaseActivity {

    private static final int REQUEST_CODE = 500; // 请求码
    private Button mConfirmBtn;
    
    private EditText mAmountEt;
    private ImageView mBackIv;

    private ImageView mTransferIv;
    private TextView mMyWalletTv;
    private TextView mContractAccountTv;

    private RelativeLayout mCoinRl;
    private TextView mCoinTv;
    private ImageView mCoinIv;

    private TextView mAvailableTv;
    private TextView mAllTv;

    private String mCoincode;
    private SpotCoin mSpotCoin;

    private int mType = 1; //1 交易所=>合约  2 合约=>交易所

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateButtonState();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == 0 && data != null) {
                mCoincode = data.getStringExtra("coin_code");
             //   mSpotCoin = LogicGlobal.sGlobalData.getSpotCoin(mCoincode);
                if (mSpotCoin != null) {
                    mCoinTv.setText(mSpotCoin.getName());
                    Glide.with(this)
                            .load(mSpotCoin.getBig())
                            .into(mCoinIv);
                }

                updateData();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_funds_transfer);

        try {
            mCoincode = getIntent().getStringExtra("coin_code");
            if (TextUtils.isEmpty(mCoincode)) {
                mCoincode = "USDT";
            }
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
            mCoincode = getIntent().getStringExtra("coin_code");
            if (TextUtils.isEmpty(mCoincode)) {
                mCoincode = "USDT";
            }
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

        mTransferIv = findViewById(R.id.iv_transfer);
        mTransferIv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if (mType == 1) {
                    mType = 2;
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.addRule(RelativeLayout.RIGHT_OF, R.id.iv_transfer);
                    mMyWalletTv.setLayoutParams(lp);

                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp2.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp2.addRule(RelativeLayout.LEFT_OF, R.id.iv_transfer);
                    mContractAccountTv.setLayoutParams(lp2);
                } else {
                    mType = 1;
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.addRule(RelativeLayout.LEFT_OF, R.id.iv_transfer);
                    mMyWalletTv.setLayoutParams(lp);

                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp2.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp2.addRule(RelativeLayout.RIGHT_OF, R.id.iv_transfer);
                    mContractAccountTv.setLayoutParams(lp2);
                }
                updateData();
            }
        });

        mMyWalletTv = findViewById(R.id.tv_mywallet);
        mContractAccountTv = findViewById(R.id.tv_contract_account);

        mAmountEt = findViewById(R.id.et_amount);
        mAmountEt.addTextChangedListener(mTextWatcher);

        mAvailableTv = findViewById(R.id.tv_available_value);

        mAllTv = findViewById(R.id.tv_available_all);
        mAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mType == 1) {
//                    Account account = BTAccount.getInstance().getActiveAccount();
//                    if (account != null) {
//                        UserAsset asset = account.getUserAsset(mCoincode);
//                        if (asset != null) {
//                            double vol = MathHelper.round(asset.getAvailable_vol(), mSpotCoin == null ? 8 : mSpotCoin.getVol_index());
//                            DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
//                            mAmountEt.setText(decimalFormat.format(vol));
//                        }
//                    }
//                } else {
//                    ContractAccount contractAccount = BTContract.getInstance().getContractAccount(mCoincode);
//                    if (contractAccount != null) {
//                        double vol = MathHelper.round(contractAccount.getCanWithdraw(), mSpotCoin == null ? 8 : mSpotCoin.getVol_index());
//                        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
//                        mAmountEt.setText(decimalFormat.format(vol));
//                    }
//                }

            }
        });

        mConfirmBtn = findViewById(R.id.btn_next);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        mCoinRl = findViewById(R.id.rl_select_coin);
        mCoinRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(FundsTransferActivity.this, SelectCoinActivity.class);
//                intent.putExtra("coin_code", mCoinTv.getText().toString());
//                intent.putExtra("type_transfer", 1);
//                ActivityCompat.startActivityForResult(FundsTransferActivity.this, intent, REQUEST_CODE, null);
            }
        });

        mCoinTv = findViewById(R.id.tv_coin);
        mCoinIv = findViewById(R.id.iv_coin);
     //   mSpotCoin = LogicGlobal.sGlobalData.getSpotCoin(mCoincode);
        if (mSpotCoin != null) {
            mCoinTv.setText(mSpotCoin.getName());
            Glide.with(this)
                    .load(mSpotCoin.getBig())
                    .into(mCoinIv);
        }

        updateButtonState();
        updateData();
    }


    private void confirm() {
        doConfirm();
    }

    private void doConfirm() {
        String amount = mAmountEt.getText().toString();

        ContractTransfer transfer = new ContractTransfer();
        transfer.setCoin_code(mCoincode);
        transfer.setType(mType);
        transfer.setVol(amount);

        mConfirmBtn.setEnabled(false);
        BTContract.getInstance().transferFunds(transfer, new IResponse<Void>() {
            @Override
            public void onResponse(String errno, String message, Void data) {
                mConfirmBtn.setEnabled(true);
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(FundsTransferActivity.this, message);
                    return;
                }
//
//                BTAccount.getInstance().userMe(new IResponse<Account>() {
//                    @Override
//                    public void onResponse(String errno, String message, Account data) {
//                    }
//                });

                BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                    @Override
                    public void onResponse(String errno, String message, List<ContractAccount> data) {
                    }
                });
                ToastUtil.shortToast(FundsTransferActivity.this, getString(R.string.sl_str_transfer_succeed));
                finish();
            }
        });
    }

    private void updateData() {
        if (mType == 1) {
            DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
            String balance = decimalFormat.format(0.0);
//            Account account = BTAccount.getInstance().getActiveAccount();
//            if (account != null) {
//                UserAsset asset = account.getUserAsset(mCoincode);
//                if (asset != null) {
//                    double vol = MathHelper.round(asset.getAvailable_vol(), mSpotCoin == null ? 8 : mSpotCoin.getVol_index());
//                    balance = decimalFormat.format(vol) + mCoincode;
//                }
//            }

            mAvailableTv.setText(balance);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
            String balance = decimalFormat.format(0.0);
            ContractAccount contractAccount = BTContract.getInstance().getContractAccount(mCoincode);
            if (contractAccount != null) {
                double vol = MathHelper.round(contractAccount.getCanWithdraw(), mSpotCoin == null ? 8 : mSpotCoin.getVol_index());
                balance = decimalFormat.format(vol) + mCoincode;
            }
            mAvailableTv.setText(balance);
        }

    }

    private void updateButtonState() {
        String amount = mAmountEt.getText().toString();

        if (mSpotCoin != null) {

            int vol_index = mSpotCoin.getVol_index() + 1;

            if (amount.contains(".")) {
                int index = amount.indexOf(".");
                if (index + vol_index < amount.length()) {
                    amount = amount.substring(0, index + vol_index);
                    mAmountEt.setText(amount);
                    mAmountEt.setSelection(amount.length());
                }
            }
        }

        if (TextUtils.equals(amount, ".")) {
            amount = "0.";
            mAmountEt.setText(amount);
            mAmountEt.setSelection(amount.length());
        }

        mConfirmBtn.setEnabled(true);
    }

}
