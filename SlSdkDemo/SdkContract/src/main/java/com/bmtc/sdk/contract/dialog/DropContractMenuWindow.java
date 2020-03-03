package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bmtc.sdk.contract.R;


public class DropContractMenuWindow extends PopupWindow {

    private RelativeLayout mTradeRecordTv, mContractSetting, mContractGuide, mContractCalculate, mSwitchLine;

    private Context context;

    private OnDropContractMenuClickedListener mListener;

    public interface OnDropContractMenuClickedListener {
        void onContractMenuClick(int tab);
    }

    public DropContractMenuWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_view_dropdown_contract_menu, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        initView(rootView);
    }


    private void initView(View view) {

        mTradeRecordTv = view.findViewById(R.id.tv_trade_record);
        mTradeRecordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractMenuClick(0);
                }
            }
        });

        mContractSetting = view.findViewById(R.id.tv_contract_setting);
        mContractSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractMenuClick(1);
                }
            }
        });

        mContractGuide = view.findViewById(R.id.tv_contract_guide);
        mContractGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractMenuClick(2);
                }
            }
        });

        mContractCalculate = view.findViewById(R.id.tv_contract_calculate);
        mContractCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractMenuClick(3);
                }
            }
        });

        mSwitchLine = view.findViewById(R.id.tv_switch_line);
        mSwitchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractMenuClick(4);
                }
            }
        });

        updateData();
    }


    private void updateData() {

    }

    public void setOnContractMenuClick(OnDropContractMenuClickedListener listener) {
        mListener = listener;
    }

}
