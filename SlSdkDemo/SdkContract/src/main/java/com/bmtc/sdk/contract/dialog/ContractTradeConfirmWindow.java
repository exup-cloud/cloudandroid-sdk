package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.uiLogic.LogicBuySell;


public class ContractTradeConfirmWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private Button btnClose;
    private Button btnOk;
    private Button btnCancel;
    private TextView title;
    private RelativeLayout titleParent;
    private Context context;

    private TextView tvContract;

    private TextView tvPrice;
    private TextView tvAmount;

    private TextView tvPriceValue;
    private TextView tvAmountValue;
    private TextView tvLeverageValue;

    private RelativeLayout rlLeverage;
    private View vLeverage;

    private RelativeLayout rlEntrustValue;
    private RelativeLayout rlCost;
    private RelativeLayout rlAval;
    private RelativeLayout rlPosition;

    private TextView tvEntrustValue;
    private TextView tvCost;
    private TextView tvAvailableBalance;
    private TextView tvPosition;
    private TextView tvTagPrice;
    private TextView tvForceClosePrice;
    private TextView tvTagRatioForce;

    private TextView tvEntrustValueCode;
    private TextView tvCostCode;
    private TextView tvAvailableBalanceCode;

    private TextView tvWarning;

    private CheckBox cbNoremind;


    public ContractTradeConfirmWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_contract_trade_confirm, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());


        initView(rootView);
    }

    private void initView(View view) {
        parentFrame = view.findViewById(R.id.ll_frame);
        btnClose = view.findViewById(R.id.btn_close);
        btnOk = view.findViewById(R.id.btn_ok);
        btnCancel = view.findViewById(R.id.btn_cancel);
        title = view.findViewById(R.id.tv_title);
        titleParent = view.findViewById(R.id.title_parent);

        tvContract = view.findViewById(R.id.tv_contract);
        tvPrice = view.findViewById(R.id.tv_price);
        tvAmount = view.findViewById(R.id.tv_amount);
        tvPriceValue = view.findViewById(R.id.tv_price_value);
        tvAmountValue = view.findViewById(R.id.tv_amount_value);
        tvLeverageValue = view.findViewById(R.id.tv_leverage_value);

        rlLeverage = view.findViewById(R.id.rl_leverage);
        vLeverage = view.findViewById(R.id.vLeverageLine);

        rlEntrustValue = view.findViewById(R.id.rl_entrust_value);
        rlCost = view.findViewById(R.id.rl_cost);
        rlAval = view.findViewById(R.id.rl_available_balance);
        rlPosition = view.findViewById(R.id.rl_position);

        tvEntrustValue = view.findViewById(R.id.tv_entrust_value);
        tvCost = view.findViewById(R.id.tv_cost);
        tvAvailableBalance = view.findViewById(R.id.tv_available_balance);
        tvPosition = view.findViewById(R.id.tv_positions);
        tvTagPrice = view.findViewById(R.id.tv_tag_price);
        tvForceClosePrice = view.findViewById(R.id.tv_forced_close_price);
        tvTagRatioForce = view.findViewById(R.id.tv_tag_ratio_force_close);

        tvEntrustValueCode = view.findViewById(R.id.tv_entrust_value_code);
        tvCostCode = view.findViewById(R.id.tv_cost_code);
        tvAvailableBalanceCode = view.findViewById(R.id.tv_available_balance_code);

        tvWarning = view.findViewById(R.id.tv_warning);
        
        cbNoremind = view.findViewById(R.id.cb_not_remind);
        cbNoremind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        view.findViewById(R.id.tv_not_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbNoremind.isChecked()) {
                    cbNoremind.setChecked(false);
                } else {
                    cbNoremind.setChecked(true);
                }
            }
        });

        titleParent.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        btnClose.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    public void showTitle(String str, int color) {
        title.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
        title.setText(str);
        title.setTextColor(color);
    }

    public void showWarning(String str) {
        tvWarning.setVisibility(View.VISIBLE);
        tvWarning.setText(str);
    }

    public void showBtnClose() {
        btnClose.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
    }

    public void showBtnOk(String str) {
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setText(str);
    }

    public void showBtnCancel(String str) {
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(str);
    }

    public void setOrderType(int orderType) {
        if (orderType == LogicBuySell.ORDER_TYPE_MARKET) {
            tvEntrustValue.setText(R.string.sl_str_market_price_simple);
            tvCost.setText(R.string.sl_str_market_price_simple);
            rlEntrustValue.setVisibility(View.GONE);
            rlCost.setVisibility(View.GONE);
            rlAval.setVisibility(View.GONE);
            rlPosition.setVisibility(View.GONE);
        }
    }

    public void setOperationType(int operationType) {
        if (operationType == 2) {
            tvEntrustValue.setText(R.string.sl_str_market_price_simple);
            tvCost.setText(R.string.sl_str_market_price_simple);
            rlEntrustValue.setVisibility(View.GONE);
            rlCost.setVisibility(View.GONE);
            rlAval.setVisibility(View.GONE);
            rlPosition.setVisibility(View.GONE);

            rlLeverage.setVisibility(View.GONE);
            vLeverage.setVisibility(View.GONE);
        }
    }

    public void setInfo(String contract, String price, String priceValue, String amount, String amountValue, String leverage) {
        tvContract.setText(contract);
        tvPrice.setText(price);
        tvPriceValue.setText(priceValue);
        tvAmount.setText(amount);
        tvAmountValue.setText(amountValue);
        tvLeverageValue.setText(leverage);
    }

    public void setData(String entrust, String cost, String available, String positions, String tag, String force, String ratio) {
        tvEntrustValue.setText(entrust);
        tvCost.setText(cost);
        tvAvailableBalance.setText(available);
        tvPosition.setText(positions);
        tvTagPrice.setText(tag);
        tvForceClosePrice.setText(force);
        tvTagRatioForce.setText(ratio);
    }

    public void setCode(String price, String volume, String amount) {
        tvEntrustValueCode.setText(price);
        tvCostCode.setText(volume);
        tvAvailableBalanceCode.setText(amount);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close){
            this.dismiss();
        }
    }

    public boolean getNoremindCheck() {
        return cbNoremind.isChecked();
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

}
