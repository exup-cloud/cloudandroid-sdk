package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.bmtc.sdk.contract.R;


public class DropKlineWindow extends PopupWindow {

    public static final int KTAB_1MIN = 0;
    public static final int KTAB_30MIN = 1;
    public static final int KTAB_2HOUR = 2;
    public static final int KTAB_4HOUR = 3;
    public static final int KTAB_6HOUR = 4;
    public static final int KTAB_12HOUR = 5;
    public static final int KTAB_1WEEK = 6;

    private RadioButton mTab1Min, mTab30Min, mTab2Hour, mTab4Hour, mTab6Hour, mTab12Hour, mTab1Week;
    private int mTab = 0;

    private Context context;

    private OnDropKlineClickedListener mListener;

    public interface OnDropKlineClickedListener {
        void onKlineDropClick(int tab);
    }

    public DropKlineWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_view_dropdown_kline, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        initView(rootView);
    }


    private void initView(View view) {

        mTab1Min = view.findViewById(R.id.ktab_1min);
        mTab1Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_1MIN;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        mTab30Min = view.findViewById(R.id.ktab_30min);
        mTab30Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_30MIN;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });
        mTab2Hour = view.findViewById(R.id.ktab_2hour);
        mTab2Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_2HOUR;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        mTab4Hour = view.findViewById(R.id.ktab_4hour);
        mTab4Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_4HOUR;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        mTab6Hour = view.findViewById(R.id.ktab_6hour);
        mTab6Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_6HOUR;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        mTab12Hour = view.findViewById(R.id.ktab_12hour);
        mTab12Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_12HOUR;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        mTab1Week = view.findViewById(R.id.ktab_1week);
        mTab1Week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTab = KTAB_1WEEK;
                if (mListener != null) {
                    mListener.onKlineDropClick(mTab);
                }
            }
        });

        updateData();
    }


    private void updateData() {

    }

    public void setOnKlineDropClick(OnDropKlineClickedListener listener) {
        mListener = listener;
    }

}
