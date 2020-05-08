package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractPosition;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 增加/减少保证金
 */
public class AddMarginWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private Button btnClose;
    private EditText etVolume;

    private Button btnOk;

    private Context context;
    private AnimatorSet mAnimatorSet;
    private static final int DURATION = 700;
    private long mDuration =DURATION ;

    private TextView mHoldingsTv;
    private TextView mMarginsTv;
    private TextView mAvailableTv;
    private TextView mForcedClosePriceTv;
    private TextView mExpectPriceTv;
    private TextView mLimitInfoTv;
    private TextView mAllTv;

    private int mOperateType = 1;   //1增加 2减少
    private double mLiqPrice = 0.0;  //强平价
    private double mMaxIncrease = 0.0;  //最大可增加
    private double mMaxReduce = 0.0;  //最大可减少

    private ContractPosition mContractPosition;

    private RadioButton mTabAddMargin, mTabReduceMargin;

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

    public AddMarginWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_add_margin, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        initView(rootView);
    }

    public void startAnim(){
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "translationY", 300, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(mDuration*3/2)

        );
        mAnimatorSet.start();
    }

    public void setContractPosition(ContractPosition position) {
        if (position == null) {
            return;
        }
        mContractPosition = position;

        Contract contractBasic = LogicGlobal.getContractBasic(mContractPosition.getInstrument_id());
        final Contract contract = LogicGlobal.getContract(mContractPosition.getInstrument_id());
        ContractTicker contractTicker = LogicGlobal.getContractTicker(mContractPosition.getInstrument_id());
        if (contract == null || contractBasic == null || contractTicker == null) {
            return;
        }

        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());

        String balance = dfDefault.format(0.0);
        ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
        if (contractAccount != null) {
            mMaxIncrease = contractAccount.getAvailable_vol_real();
            balance = dfDefault.format(MathHelper.round(mMaxIncrease, contract.getValue_index())) + contract.getMargin_coin();
        }

        mAvailableTv.setText(balance);
        mMarginsTv.setText(dfDefault.format(MathHelper.round(mContractPosition.getIm(), contract.getValue_index())) + contract.getMargin_coin());
        mHoldingsTv.setText(ContractCalculate.getVolUnit(contract, mContractPosition.getCur_qty(), contractTicker.getFair_px()));

        int open_type = mContractPosition.getPosition_type();
        if (open_type == 1) {
            mLiqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                    mContractPosition, null, contractBasic);
        } else if (open_type == 2) {
            if (contractAccount != null) {
                mLiqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                        mContractPosition, contractAccount, contractBasic);
            }
        }


        mLimitInfoTv.setText(context.getString(R.string.sl_str_max_increase) + balance);
        mAllTv.setVisibility(View.VISIBLE);

        double IMR = ContractCalculate.CalculatePositionIMR(mContractPosition, contract, contractBasic);
        double value = ContractCalculate.CalculateContractValue(
                mContractPosition.getCur_qty(),
                mContractPosition.getAvg_cost_px(),
                contract);

        mMaxReduce = Math.min(MathHelper.sub(MathHelper.round(mContractPosition.getIm()), MathHelper.mul(value, IMR)),
                MathHelper.sub(MathHelper.round(mContractPosition.getIm()), MathHelper.mul(value, MathHelper.sub("1", contract.getLiquidation_warn_ratio()))));

        mMaxReduce = Math.max(0, mMaxReduce);
        mForcedClosePriceTv.setText(dfDefault.format(MathHelper.round(mLiqPrice, contract.getPrice_index())) + contract.getQuote_coin());
        mExpectPriceTv.setText(dfDefault.format(MathHelper.round(mLiqPrice, contract.getPrice_index())) + "(0)" + contract.getQuote_coin());

        BTContract.getInstance().calculate(contract.getInstrument_id(),
                64,
                mContractPosition.getPid(),
                (mContractPosition.getPosition_type() == 1) ? true : false,
                new IResponse<ContractPosition>() {
                    @Override
                    public void onResponse(String errno, String message, ContractPosition data) {
                        if (data != null) {
                            mMaxReduce = MathHelper.round(data.getDeductible_margin(), contract.getValue_index());
                        }
                    }
                });
    }

    private void initView(View view) {
        parentFrame = view.findViewById(R.id.ll_frame);
        btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustMargin();
            }
        });

        etVolume = view.findViewById(R.id.et_price);
        etVolume.addTextChangedListener(mTextWatcher);

        mHoldingsTv = view.findViewById(R.id.tv_holdings_value);
        mMarginsTv = view.findViewById(R.id.tv_margins_value);
        mAvailableTv = view.findViewById(R.id.tv_available_value);
        mForcedClosePriceTv = view.findViewById(R.id.tv_forced_close_price_value);
        mExpectPriceTv = view.findViewById(R.id.tv_expect_price_value);
        mLimitInfoTv = view.findViewById(R.id.tv_limit_info);
        mAllTv = view.findViewById(R.id.tv_all);
        mAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contract contract = LogicGlobal.getContract(mContractPosition.getInstrument_id());
                if (contract == null) {
                    return;
                }

                DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
                String balance = dfDefault.format(0.0);
                ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
                if (contractAccount != null) {
                    double vol = contractAccount.getAvailable_vol_real();
                    balance = dfDefault.format(MathHelper.round(vol, contract.getValue_index()));
                }
                etVolume.setText(balance);
            }
        });

        mTabAddMargin = view.findViewById(R.id.tab_add_margin);
        mTabAddMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOperateType = 1;
                mLimitInfoTv.setText(context.getString(R.string.sl_str_max_increase) + mAvailableTv.getText());
                mAllTv.setVisibility(View.VISIBLE);
                etVolume.setHint(R.string.sl_str_add_margin);
                etVolume.setText("");
                updateButtonState();
            }
        });

        mTabReduceMargin = view.findViewById(R.id.tab_reduce_margin);
        mTabReduceMargin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOperateType = 2;

                Contract contract = LogicGlobal.getContract(mContractPosition.getInstrument_id());
                if (contract == null) {
                    return;
                }
                mLimitInfoTv.setText(context.getString(R.string.sl_str_max_reduce) + MathHelper.round(mMaxReduce, contract.getValue_index()) + contract.getMargin_coin() );
                mAllTv.setVisibility(View.GONE);
                etVolume.setHint(R.string.sl_str_reduce_margin);
                etVolume.setText("");
                updateButtonState();
            }
        });

        updateButtonState();
    }

    public String getPrice() {
        return etVolume.getText().toString();
    }

    public void showBtnOk(String str) {
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setText(str);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==  R.id.btn_close){
            this.dismiss();
        }
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    private void adjustMargin() {
        if (mContractPosition == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractPosition.getInstrument_id());
        if (contract == null) {
            return;
        }

        String amount = etVolume.getText().toString();

        btnOk.setEnabled(false);
        BTContract.getInstance().marginOper(
                contract.getInstrument_id(),
                mContractPosition.getPid(),
                amount,
                mOperateType,
                new IResponse<Void>() {
                    @Override
                    public void onResponse(String errno, String message, Void data) {
                        btnOk.setEnabled(true);
                        if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                            ToastUtil.shortToast(LogicGlobal.sContext, message);
                            return;
                        }

                        BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                            @Override
                            public void onResponse(String errno, String message, List<ContractAccount> data) {

                            }
                        });

                        ToastUtil.shortToast(LogicGlobal.sContext, context.getString(R.string.sl_str_adjust_succeed));
                        dismiss();
                    }
                }
        );

    }

    private void updateButtonState() {
        String amount = etVolume.getText().toString();

        if (mContractPosition == null) {
            return;
        }

        Contract contractBasic = LogicGlobal.getContractBasic(mContractPosition.getInstrument_id());
        Contract contract = LogicGlobal.getContract(mContractPosition.getInstrument_id());
        ContractTicker contractTicker = LogicGlobal.getContractTicker(mContractPosition.getInstrument_id());
        if (contract == null || contractBasic == null || contractTicker == null) {
            return;
        }

        int vol_index = contract.getValue_index() + 1;
        if (amount.contains(".")) {
            int index = amount.indexOf(".");
            if (index + vol_index < amount.length()) {
                amount = amount.substring(0, index + vol_index);
                etVolume.setText(amount);
                etVolume.setSelection(amount.length());
            }
        }

        if (TextUtils.equals(amount, ".")) {
            amount = "0.";
            etVolume.setText(amount);
            etVolume.setSelection(amount.length());
        }

        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());

        if (TextUtils.isEmpty(amount)) {
            amount = "0";
            mExpectPriceTv.setText(dfDefault.format(MathHelper.round(mLiqPrice, contract.getPrice_index())) + "(0)" + contract.getQuote_coin());
        }

        btnOk.setEnabled(MathHelper.round(amount) > 0);


        double newIm = 0.0;
        if (mOperateType == 1) {
            if (MathHelper.round(amount) <= mMaxIncrease) {
                mLimitInfoTv.setText(context.getString(R.string.sl_str_max_increase) + mAvailableTv.getText());
                mLimitInfoTv.setTextColor(context.getResources().getColor(R.color.sl_grayText));
                mAllTv.setVisibility(View.VISIBLE);
            } else {
                mLimitInfoTv.setText(context.getString(R.string.sl_str_exceed_max_increase));
                mLimitInfoTv.setTextColor(context.getResources().getColor(R.color.sl_colorRed));
                mAllTv.setVisibility(View.GONE);
            }
            newIm = MathHelper.add(mContractPosition.getIm(), amount);
        } else if (mOperateType == 2) {
            mLimitInfoTv.setTextColor(context.getResources().getColor(R.color.sl_grayText));
            newIm = MathHelper.sub(mContractPosition.getIm(), amount);
        }

        if (MathHelper.round(amount) <= 0) {
            return;
        }

        ContractPosition position = new ContractPosition();
        position.fromJson(mContractPosition.toJson());
        position.setIm(dfValue.format(newIm));

        double liqPrice = 0;  //强平价

        int open_type = mContractPosition.getPosition_type();
        if (open_type == 1) {
            liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                    position, null, contractBasic);
        } else if (open_type == 2) {
//            ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
//            if (contractAccount != null) {
//                liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
//                        position, contractAccount, contractBasic);
//            }
            liqPrice = mLiqPrice;
        }

        double change = liqPrice - mLiqPrice;
        mExpectPriceTv.setText(dfDefault.format(MathHelper.round(liqPrice, contract.getPrice_index())) + "(" + dfDefault.format(MathHelper.round(change, contract.getPrice_index())) + ")" + contract.getQuote_coin());
    }
}
