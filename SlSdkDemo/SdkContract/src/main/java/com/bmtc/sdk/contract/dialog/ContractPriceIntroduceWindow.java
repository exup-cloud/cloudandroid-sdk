package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;


public class ContractPriceIntroduceWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private Button btnClose;
    private TextView title;
    private RelativeLayout titleParent;
    private Context context;

    private TextView tvTradePrice;
    private TextView tvOtherPrice;



    public ContractPriceIntroduceWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_contract_price_introduce, null);
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
        title = view.findViewById(R.id.tv_title);
        titleParent = view.findViewById(R.id.title_parent);

        tvTradePrice = view.findViewById(R.id.tv_trade_price_value);
        tvOtherPrice = view.findViewById(R.id.tv_other_price_value);

    }

    public void showTitle(String str, int color) {
        title.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
        title.setText(str);
        title.setTextColor(color);
    }

    public void showPrice(String price, String otherPrice) {

        tvTradePrice.setText(price);
        tvOtherPrice.setText(otherPrice);
    }

    public void showBtnClose() {
        btnClose.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
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

}
