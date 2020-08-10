package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.OrderBean;
import com.follow.order.ui.activity.OrderDetailActivity;
import com.follow.order.utils.ClickUtil;
import com.follow.order.utils.ColorUtils;
import com.follow.order.widget.roundimg.RoundedImageView;

import java.util.List;


public class OrderListAdapter extends BaseQuickAdapter<OrderBean, OrderListAdapter.FollowViewHolder> {

    public OrderListAdapter(@Nullable List<OrderBean> data) {
        super(R.layout.fo_item_order_list, data);
    }

    @Override
    protected void convert(FollowViewHolder holder, final OrderBean bean) {
        if (bean == null) {
            return;
        }
        if (bean.getUser() != null) {
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(bean.getUser().getHead_img(), FollowOrderSDK.ins().getCustomAttrResId(mContext, R.attr.fo_avatar_drawable), holder.ivOrderAvatar);
            holder.tvOrderNick.setText(bean.getUser().getNick_name());
        }

        if (bean.getFollow() != null) {
            holder.tvOrderTime.setText(bean.getFollow().getStart_time());
            holder.tvOrderItemAsset.setText(bean.getFollow().getPrincipal());
            holder.tvOrderItemProfit.setText(bean.getFollow().getPnl());
            holder.tvOrderItemRate.setText(bean.getFollow().getPnl_ratio());
            holder.tvOrderDay.setText(bean.getFollow().getFollow_days());
            if (bean.getColor() != null) {
                ColorUtils.setTextColor(holder.tvOrderItemProfit, bean.getColor().getPnl(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_text_1_color));
                ColorUtils.setTextColor(holder.tvOrderItemRate, bean.getColor().getPnl_ratio(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_text_1_color));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                if (bean.getFollow() == null) {
                    return;
                }
                OrderDetailActivity.start(mContext, bean.getFollow().getFollow_id(), bean.getFollow().getCurrency());
            }
        });

        holder.addOnClickListener(R.id.iv_order_share);
    }


    public static class FollowViewHolder extends BaseViewHolder {
        RoundedImageView ivOrderAvatar;
        TextView tvOrderNick;
        TextView tvOrderTime;
        ImageView ivOrderShare;
        TextView tvOrderItemAsset;
        TextView tvOrderItemProfit;
        TextView tvOrderItemRate;
        TextView tvOrderDay;


        public FollowViewHolder(View view) {
            super(view);
            ivOrderAvatar = view.findViewById(R.id.iv_order_avatar);
            tvOrderNick = view.findViewById(R.id.tv_order_nick);
            tvOrderTime = view.findViewById(R.id.tv_order_time);
            ivOrderShare = view.findViewById(R.id.iv_order_share);
            tvOrderItemAsset = view.findViewById(R.id.tv_order_item_asset);
            tvOrderItemProfit = view.findViewById(R.id.tv_order_item_profit);
            tvOrderItemRate = view.findViewById(R.id.tv_order_item_rate);
            tvOrderDay = view.findViewById(R.id.tv_order_day);

        }
    }

}
