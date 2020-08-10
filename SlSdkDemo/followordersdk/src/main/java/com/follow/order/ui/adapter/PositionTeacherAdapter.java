package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.FollowPositionBean;
import com.follow.order.utils.ColorUtils;

import java.util.List;


public class PositionTeacherAdapter extends BaseQuickAdapter<FollowPositionBean.MasterPosition, PositionTeacherAdapter.TeacherViewHolder> {

    public PositionTeacherAdapter(@Nullable List<FollowPositionBean.MasterPosition> data) {
        super(R.layout.fo_item_position_teacher, data);
    }

    @Override
    protected void convert(TeacherViewHolder holder, final FollowPositionBean.MasterPosition bean) {
        if (bean == null) {
            return;
        }
        holder.tvTeacherHold.setText(bean.getDesc());
        holder.tvTeacherAvgPrice.setText(bean.getAvg_price());
        holder.tvTeacherProfitRate.setText(bean.getPnl_ratio());
        if (bean.getColor() != null) {
            ColorUtils.setTextColor(holder.tvTeacherHold, bean.getColor().getDesc(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_nickname_color));
        }
    }

    public static class TeacherViewHolder extends BaseViewHolder {
        TextView tvTeacherHold;
        TextView tvTeacherAvgPrice;
        TextView tvTeacherProfitRate;

        public TeacherViewHolder(View view) {
            super(view);
            tvTeacherHold = view.findViewById(R.id.tv_teacher_hold);
            tvTeacherAvgPrice = view.findViewById(R.id.tv_teacher_avg_price);
            tvTeacherProfitRate = view.findViewById(R.id.tv_teacher_profit_rate);
        }
    }

}
