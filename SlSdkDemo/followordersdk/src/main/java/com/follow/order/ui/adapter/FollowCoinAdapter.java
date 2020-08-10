package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.FollowCoinBean;
import com.follow.order.widget.shape.RoundTextView;
import com.follow.order.widget.shape.RoundViewDelegate;

import java.util.List;


public class FollowCoinAdapter extends BaseQuickAdapter<FollowCoinBean, FollowCoinAdapter.FollowViewHolder> {
    private OnCoinActionListener actionListener;

    public FollowCoinAdapter(@Nullable List<FollowCoinBean> data) {
        super(R.layout.fo_item_follow_coin_list, data);
    }

    @Override
    protected void convert(FollowViewHolder holder, final FollowCoinBean bean) {
        if (bean == null) {
            return;
        }
        if (holder.getAdapterPosition() == 0) {
            holder.divideLine.setVisibility(View.GONE);
        } else {
            holder.divideLine.setVisibility(View.VISIBLE);
        }
        holder.tvCoinName.setText(bean.getTrade_coin());
        holder.tvCoinLimit.setText(bean.getLimit());
        RoundViewDelegate delegate = holder.btnCoin.getDelegate();
        if (bean.getFollow_status() == 0) {//未跟
            holder.btnCoin.setText(mContext.getString(R.string.fo_follow_coin_1));
            delegate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.fo_blue));
        } else if (bean.getFollow_status() == 1) {//已跟
            holder.btnCoin.setText(mContext.getString(R.string.fo_follow_coin_2));
            delegate.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_coin_follow_color));
        } else if (bean.getFollow_status() == 2) {//额度已满
            holder.btnCoin.setText(mContext.getString(R.string.fo_follow_coin_3));
            delegate.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_coin_limit_color));
        }

        holder.btnCoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (actionListener != null && bean.getFollow_status() == 0) {
                    actionListener.onFollowOrder(bean);
                }
            }
        });
    }

    public interface OnCoinActionListener {
        void onFollowOrder(FollowCoinBean bean);
    }

    public void setOnCoinActionListener(OnCoinActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public static class FollowViewHolder extends BaseViewHolder {
        View divideLine;
        TextView tvCoinName;
        TextView tvCoinLimit;
        RoundTextView btnCoin;

        public FollowViewHolder(View view) {
            super(view);
            divideLine = view.findViewById(R.id.divide_line);
            tvCoinName = view.findViewById(R.id.tv_coin_name);
            tvCoinLimit = view.findViewById(R.id.tv_coin_limit);
            btnCoin = view.findViewById(R.id.btn_coin);
        }
    }

}
