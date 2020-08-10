package com.yjkj.chainup.wedegit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.yjkj.chainup.R;

public class KLineChartInfoView extends ChartInfoView {


    private TextView tvMa5;
    private TextView tvMa10;
    private TextView tvMa20;
    private TextView tvMa30;
    private TextView mTvVoluePrice;
    private TextView mTvTime;

    public KLineChartInfoView(Context context) {
        this(context, null);
    }

    public KLineChartInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_kline_chart_info, this);

        tvMa5 = findViewById(R.id.tv_ma5_price);
        tvMa10 = findViewById(R.id.tv_ma10_price);
        tvMa20 = findViewById(R.id.tv_ma20_price);
        tvMa30 = findViewById(R.id.tv_ma30_price);
        mTvTime = findViewById(R.id.tv_time);
        mTvVoluePrice = findViewById(R.id.tv_volume_for_kline);
    }

    @Override
    public void setData(String open, String close, String high, String low, String mTvVol, String time) {
        tvMa5.setText(open);
        tvMa10.setText(high);
        tvMa20.setText(low);
        tvMa30.setText(close);
        mTvVoluePrice.setText(mTvVol);
        mTvTime.setText(time);
    }


}
