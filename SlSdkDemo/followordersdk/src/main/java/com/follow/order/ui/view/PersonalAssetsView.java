package com.follow.order.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.utils.ColorUtils;

public class PersonalAssetsView extends LinearLayout {
    private TextView tv_balance;
    private TextView tv_profit_ratio;
    private TextView tv_profit;
    private TextView tv_week_ratio;
    private TextView tv_week_profit;
    private TextView tv_victory_ratio;
    private TextView tv_trading_frequency;
    private TextView tv_live_duration;

    public PersonalAssetsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_balance = findViewById(R.id.tv_balance);
        tv_profit_ratio = findViewById(R.id.tv_profit_ratio);
        tv_profit = findViewById(R.id.tv_profit);
        tv_week_ratio = findViewById(R.id.tv_week_ratio);
        tv_week_profit = findViewById(R.id.tv_week_profit);
        tv_victory_ratio = findViewById(R.id.tv_victory_ratio);
        tv_trading_frequency = findViewById(R.id.tv_trading_frequency);
        tv_live_duration = findViewById(R.id.tv_live_duration);
    }

    public void setAssetsData(UserFinanceProfileBean assetsData) {
        if (assetsData == null || assetsData.getProfile() == null) {
            return;
        }
        tv_balance.setText(assetsData.getProfile().getTotal());
        tv_profit.setText(assetsData.getProfile().getProfit());
        tv_profit_ratio.setText(assetsData.getProfile().getProfit_ratio());
        tv_week_ratio.setText(assetsData.getProfile().getWeek_profit_ratio());
        tv_week_profit.setText(assetsData.getProfile().getWeek_profit());
        tv_victory_ratio.setText(assetsData.getProfile().getVictory_ratio());
        tv_trading_frequency.setText(assetsData.getProfile().getTrading_frequency());
        tv_live_duration.setText(assetsData.getProfile().getLive_duration());

        UserFinanceProfileBean.Colors colors = assetsData.getColors();
        if (colors != null) {
            ColorUtils.setTextColor(tv_profit, colors.getProfit(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
            ColorUtils.setTextColor(tv_profit_ratio, colors.getProfit_ratio(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
            ColorUtils.setTextColor(tv_week_profit, colors.getWeek_profit(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
            ColorUtils.setTextColor(tv_week_ratio, colors.getWeek_profit_ratio(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
        }
    }
}
