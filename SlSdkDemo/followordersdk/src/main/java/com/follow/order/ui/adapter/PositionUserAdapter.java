package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.FollowPositionBean;
import com.follow.order.utils.ColorUtils;

import java.util.List;


public class PositionUserAdapter extends BaseQuickAdapter<FollowPositionBean.Position, PositionUserAdapter.UserViewHolder> {

    public PositionUserAdapter(@Nullable List<FollowPositionBean.Position> data) {
        super(R.layout.fo_item_position_user, data);
    }

    @Override
    protected void convert(UserViewHolder holder, final FollowPositionBean.Position bean) {
        if (bean == null) {
            return;
        }

        holder.tvUserHold.setText(bean.getDesc());
        holder.tvUserAvgPrice.setText(bean.getAvg_price());
        holder.tvUserProfitRate.setText(bean.getPnl_ratio());
        holder.tvUserStopLoss.setText(bean.getStop_deficit_price());
        if (TextUtils.isEmpty(bean.getStop_deficit_price()) && TextUtils.isEmpty(bean.getStop_profit_price())) {
            holder.ll_stop.setVisibility(View.GONE);
        } else {
            holder.ll_stop.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(bean.getStop_profit_price())) {
                holder.tvUserStopProfit.setVisibility(View.GONE);
            } else {
                holder.tvUserStopProfit.setVisibility(View.VISIBLE);
                holder.tvUserStopProfit.setText(bean.getStop_profit_price());
            }
            if (TextUtils.isEmpty(bean.getStop_deficit_price())) {
                holder.tvUserStopLoss.setVisibility(View.GONE);
            } else {
                holder.tvUserStopLoss.setVisibility(View.VISIBLE);
                holder.tvUserStopLoss.setText(bean.getStop_deficit_price());
            }
        }

        if (bean.getColor() != null) {
            ColorUtils.setTextColor(holder.tvUserHold, bean.getColor().getDesc(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_nickname_color));
        }
    }

    public static class UserViewHolder extends BaseViewHolder {
        TextView tvUserHold;
        TextView tvUserAvgPrice;
        TextView tvUserProfitRate;
        TextView tvUserStopProfit;
        TextView tvUserStopLoss;
        LinearLayout ll_stop;

        public UserViewHolder(View view) {
            super(view);
            tvUserHold = view.findViewById(R.id.tv_user_hold);
            tvUserAvgPrice = view.findViewById(R.id.tv_user_avg_price);
            tvUserProfitRate = view.findViewById(R.id.tv_user_profit_rate);
            tvUserStopProfit = view.findViewById(R.id.tv_user_stop_profit);
            tvUserStopLoss = view.findViewById(R.id.tv_user_stop_loss);
            ll_stop = view.findViewById(R.id.ll_stop);
        }
    }

}
