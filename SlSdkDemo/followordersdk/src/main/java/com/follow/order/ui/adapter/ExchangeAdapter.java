package com.follow.order.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.utils.DensityUtil;

import java.util.List;


public class ExchangeAdapter extends BaseQuickAdapter<ExchangeBean, ExchangeAdapter.ExchangeViewHolder> {
    private OnExchangeCheckedListener checkedListener;
    private int item_width;

    public ExchangeAdapter(@Nullable List<ExchangeBean> data) {
        super(R.layout.fo_personal_exchange, data);
        item_width = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(19)) / 3;
    }

    @Override
    protected void convert(@NonNull ExchangeViewHolder holder, final ExchangeBean bean) {
        if (bean == null) {
            return;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.exchange_item.getLayoutParams();
        params.width = item_width;
        holder.tv_exchange.setText(bean.getExchange_name());
        if (bean.isSelected()) {
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(bean.getSelected_icon(), 0, holder.iv_exchange);
            holder.tv_exchange.setTextColor(ContextCompat.getColor(mContext, R.color.fo_blue));
            holder.exchange_item.setBackground(FollowOrderSDK.ins().getCustomAttrDrawable(mContext, R.attr.fo_exchange_sel_drawable));
        } else {
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(bean.getUnselected_icon(), 0, holder.iv_exchange);
            holder.tv_exchange.setTextColor(ContextCompat.getColor(mContext, R.color.fo_exchange_color));
            holder.exchange_item.setBackground(FollowOrderSDK.ins().getCustomAttrDrawable(mContext, R.attr.fo_exchange_nor_drawable));
        }

        holder.exchange_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.isSelected()) {
                    return;
                }
                if (checkedListener != null) {
                    checkedListener.onExchangeChecked(bean);
                }
                for (ExchangeBean item : getData()) {
                    item.setSelected(false);
                }
                bean.setSelected(true);
                notifyDataSetChanged();
            }
        });
    }

    static class ExchangeViewHolder extends BaseViewHolder {
        LinearLayout exchange_item;
        ImageView iv_exchange;
        TextView tv_exchange;

        public ExchangeViewHolder(@NonNull View view) {
            super(view);
            exchange_item = view.findViewById(R.id.exchange_item);
            iv_exchange = view.findViewById(R.id.iv_exchange);
            tv_exchange = view.findViewById(R.id.tv_exchange);
        }
    }

    public interface OnExchangeCheckedListener {
        void onExchangeChecked(ExchangeBean exchange);
    }

    public void setOnExchangeCheckedListener(OnExchangeCheckedListener checkedListener) {
        this.checkedListener = checkedListener;
    }
}
