package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.bmtc.sdk.contract.utils.UtilSystem;
import com.bmtc.sdk.contract.view.bubble.BubbleSeekBar;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractAccount;
import com.contract.sdk.data.ContractOrder;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.impl.IResponse;
import com.contract.sdk.utils.MathHelper;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClosePositionWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private Button btnClose;

    private EditText etPrice;
    private EditText etVolume;

    private TextView tvPriceUnit;
    private TextView tvVolumeUnit;

    private BubbleSeekBar mSeekBar;
    private TextView tvMaxClose;

    private Button btnOk;
    private Button btnCancel;
    private Context context;
    private AnimatorSet mAnimatorSet;
    private static final int DURATION = 700;
    private long mDuration =DURATION ;

    private ContractPosition mContractPosition;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            updatePrice();
            updateVol();
        }
    };

    public ClosePositionWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_close_position, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        initView(rootView);
    }

    public void setContractPosition(ContractPosition position) {
        if (position == null) {
            return;
        }
        mContractPosition = position;

        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractPosition.getInstrument_id());
        if (contract == null) {
            return;
        }

        tvPriceUnit.setText(contract.getQuote_coin());
        tvVolumeUnit.setText(context.getString(R.string.sl_str_contracts_unit));
        tvMaxClose.setText(context.getString(R.string.sl_str_max_close) +
                MathHelper.round(MathHelper.sub(mContractPosition.getCur_qty(), mContractPosition.getFreeze_qty()), 0) +
                context.getString(R.string.sl_str_contracts_unit));

        ContractTicker ticker = ContractPublicDataAgent.INSTANCE.getContractTicker(mContractPosition.getInstrument_id());
        if (ticker != null) {
            etPrice.setText(ticker.getLast_px());
        }
    }

    public void startAnim(){
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "translationY", 300, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(mDuration*3/2)

        );
        mAnimatorSet.start();
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
                final String vol = TextUtils.isEmpty(etVolume.getText().toString()) ? "0" : etVolume.getText().toString();
                final String price = TextUtils.isEmpty(etPrice.getText().toString()) ? "0" : etPrice.getText().toString();

                if (TextUtils.isEmpty(vol)) {
                    ToastUtil.shortToast(context, context.getString(R.string.sl_str_volume_too_low));
                    return;
                }

                closePosition(price, vol, "");
                dismiss();
            }
        });

        btnCancel = view.findViewById(R.id.btn_cancel);

        etPrice = view.findViewById(R.id.et_price);
        etVolume = view.findViewById(R.id.et_volume);
        etPrice.addTextChangedListener(mTextWatcher);
        etVolume.addTextChangedListener(mTextWatcher);

        tvPriceUnit = view.findViewById(R.id.tv_price_unit);
        tvVolumeUnit = view.findViewById(R.id.tv_volume_unit);

        tvMaxClose = view.findViewById(R.id.tv_max_close);
        mSeekBar = view.findViewById(R.id.sb_seekbar);
        mSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

                etVolume.setTag(1);

                double volume = MathHelper.mul(MathHelper.sub(mContractPosition.getCur_qty(),mContractPosition.getFreeze_qty()), MathHelper.div(progress, 100));
                etVolume.setText((int)volume +  "");
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                etVolume.setTag(0);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
    }

    public String getPrice() {
        return etPrice.getText().toString();
    }

    public String getVolume() {
        return etVolume.getText().toString();
    }

    public void showBtnOk(String str) {
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setText(str);
    }

    public void showBtnCancel(String str) {
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(str);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close){
            this.dismiss();
        }
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    private void updatePrice() {

        String price = etPrice.getText().toString();

        price = price.replace(",", ".");
        if (TextUtils.isEmpty(price)) {
            return;
        }

        if (mContractPosition == null) {
            return;
        }

        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractPosition.getInstrument_id());
        if (contract == null) {
            return;
        }

        String price_unit = contract.getPx_unit();
        if (price_unit.contains(".")) {
            int price_index = price_unit.length() - price_unit.indexOf(".") - 1;
            if (price_index == 1) {
                price_index = 0;
            }

            if (price.contains(".")) {
                int index = price.indexOf(".");
                if (index + price_index < price.length()) {
                    price = price.substring(0, index + price_index);
                    etPrice.setText(price);
                }
            }

        } else {
            if (price.contains(".")) {
                price = price.replace(".", "");
                etPrice.setText(price);
            }
        }

        if (price.equals(".")) {
            price = "0";
            etPrice.setText(price);
        }

        etPrice.setSelection(price.length());
    }

    private void updateVol() {
        String vol = etVolume.getText().toString();
        vol = vol.replace(",", ".");
        if (TextUtils.isEmpty(vol)) {
            return;
        }

        if (mContractPosition == null) {
            return;
        }

        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractPosition.getInstrument_id());
        if (contract == null) {
            return;
        }

//        int unit = LogicContractSetting.getContractUint(LogicGlobal.sContext);
//        if (unit == 0) {
            String vol_unit = contract.getQty_unit();
            if (vol_unit.contains(".")) {
                int vol_index = vol_unit.length() - vol_unit.indexOf(".");
                if (vol.contains(".")) {
                    int index = vol.indexOf(".");
                    if (index + vol_index < vol.length()) {
                        vol = vol.substring(0, index + vol_index);
                        etVolume.setText(vol);
                    }
                }
            } else {
                if (vol.contains(".")) {
                    vol = vol.replace(".", "");
                    etVolume.setText(vol);
                }
            }
//        } else {
//            String base_coin_unit = "0.0001";
//            SpotCoin spotCoin = LogicGlobal.sGlobalData.getSpotCoin(contract.getBase_coin());
//            if (spotCoin != null) {
//                base_coin_unit = spotCoin.getVol_unit();
//            }
//            if (base_coin_unit.contains(".")) {
//                int vol_index = base_coin_unit.length() - base_coin_unit.indexOf(".");
//                if (vol.contains(".")) {
//                    int index = vol.indexOf(".");
//                    if (index + vol_index < vol.length()) {
//                        vol = vol.substring(0, index + vol_index);
//                        etVolume.setText(vol);
//                    }
//                }
//            } else {
//                if (vol.contains(".")) {
//                    vol = vol.replace(".", "");
//                    etVolume.setText(vol);
//                }
//            }
//        }

        if (vol.equals(".")) {
            vol = "0";
            etVolume.setText(vol);
        }

        etVolume.setSelection(vol.length());

        if (etVolume.getTag() != null && (int)etVolume.getTag() == 1) {
            return;
        }

        double progress = MathHelper.div(MathHelper.round(vol), MathHelper.sub(mContractPosition.getCur_qty(),mContractPosition.getFreeze_qty())) * 100;
        progress = (float) Math.min(progress, 100.0);
        mSeekBar.setProgress((float)progress);
    }

    private void closePosition(final String price, String vol, String pwd) {
        if (mContractPosition == null) {
            return;
        }

        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractPosition.getInstrument_id());
        if (contract == null) {
            return;
        }

        final String transVol = vol;

        ContractOrder order = new ContractOrder();
        order.setInstrument_id(mContractPosition.getInstrument_id());
        order.setNonce(System.currentTimeMillis());
        order.setQty(transVol);
        if (mContractPosition.getSide() == 1) {
            order.setPid(mContractPosition.getPid());
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG);
        } else {
            order.setPid(mContractPosition.getPid());
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT);
        }

        order.setPx(price);
        order.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);

        btnOk.setEnabled(false);

        ContractUserDataAgent.INSTANCE.doSubmitOrder(order, new IResponse<String>() {
            @Override
            public void onSuccess(@NotNull String data) {
                btnOk.setEnabled(true);
                ToastUtil.shortToast(context, context.getString(R.string.sl_str_order_submit_success));
            }

            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                btnOk.setEnabled(true);
                ToastUtil.shortToast(context, msg);
            }
        });

    }

}
