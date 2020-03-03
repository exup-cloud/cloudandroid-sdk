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


public class ContractPlanConfirmWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private Button btnClose;
    private Button btnOk;
    private Button btnCancel;
    private TextView title;
    private RelativeLayout titleParent;
    private Context context;

    private TextView tvContract;

    private TextView tvTriggerPrice;
    private TextView tvExecutionPrice;
    private TextView tvAmountValue;

    private TextView tvLeverageValue;

    private TextView tvTriggerType;
    private TextView tvTriggerTime;

    private CheckBox cbNoremind;


    public ContractPlanConfirmWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_contract_plan_confirm, null);
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
        tvAmountValue = view.findViewById(R.id.tv_amount_value);
        tvTriggerPrice = view.findViewById(R.id.tv_trigger_price_value);
        tvExecutionPrice = view.findViewById(R.id.tv_execution_price_value);

        tvLeverageValue = view.findViewById(R.id.tv_leverage_value);
        tvTriggerType = view.findViewById(R.id.tv_trigger_price_type);
        tvTriggerTime = view.findViewById(R.id.tv_str_trigger_time_value);
        
        cbNoremind = view.findViewById(R.id.cb_not_remind);
        cbNoremind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

        ((TextView)view.findViewById(R.id.tv_amount)).setText(context.getString(R.string.sl_str_amount) + "(" + context.getString(R.string.sl_str_contracts_unit) + ")");

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

    public void setInfo(String contract, String triggerPrice, String executionPrice, String amountValue) {
        tvContract.setText(contract);
        tvTriggerPrice.setText(triggerPrice);
        tvExecutionPrice.setText(executionPrice);
        tvAmountValue.setText(amountValue);
    }

    public void setData(String leverage, String triggerType, String triggerTime) {
        tvLeverageValue.setText(leverage);
        tvTriggerType.setText(triggerType);
        tvTriggerTime.setText(triggerTime);
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
