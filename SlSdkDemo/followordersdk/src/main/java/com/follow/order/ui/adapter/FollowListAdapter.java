package com.follow.order.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.FollowBean;
import com.follow.order.bean.FollowCoinBean;
import com.follow.order.ui.activity.PersonalActivity;
import com.follow.order.utils.ClickUtil;
import com.follow.order.utils.ColorUtils;
import com.follow.order.widget.roundimg.RoundedImageView;

import java.util.List;


public class FollowListAdapter extends BaseQuickAdapter<FollowBean, FollowListAdapter.FollowViewHolder> {
    private OnCoinActionListener actionListener;

    public FollowListAdapter(@Nullable List<FollowBean> data) {
        super(R.layout.fo_item_follow_list, data);
    }

    @Override
    protected void convert(FollowViewHolder holder, final FollowBean bean) {
        if (bean == null) {
            return;
        }
        FollowOrderSDK.ins().getFollowOrderProxy().loadImage(bean.getHead_img(), FollowOrderSDK.ins().getCustomAttrResId(mContext, R.attr.fo_avatar_drawable), holder.ivAvatar);
        holder.tvNickname.setText(bean.getNick_name());
        if (!TextUtils.isEmpty(bean.getExchange())) {
            holder.tvExchange.setVisibility(View.VISIBLE);
            holder.tvExchange.setText(bean.getExchange());
        } else {
            holder.tvExchange.setVisibility(View.GONE);
        }
        holder.tvIntro.setText(bean.getDesc());
        if (bean.getIs_recommend() == 1) {
            holder.tvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fo_recommend, 0);
        } else {
            holder.tvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.tvPriceType.setText(bean.getType());
        String followCount = "";
        SpannableString spannableString = null;
        if (bean.getFollow_members() == 10) {
            followCount = bean.getFollow_members() + mContext.getString(R.string.fo_follow_text_9);
            spannableString = new SpannableString(followCount);
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), String.valueOf(bean.getFollow_members()).length(), followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), String.valueOf(bean.getFollow_members()).length(), followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (bean.getFollow_members() < 10) {
            followCount = bean.getFollow_members() + mContext.getString(R.string.fo_follow_text_8);
            spannableString = new SpannableString(followCount);
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), String.valueOf(bean.getFollow_members()).length(), followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), String.valueOf(bean.getFollow_members()).length(), followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (bean.getFollow_members() > 10) {
            followCount = bean.getFollow_members() + mContext.getString(R.string.fo_follow_text_10);
            spannableString = new SpannableString(followCount);
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), followCount.length() - 1, followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), followCount.length() - 1, followCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.tv_follow_count.setText(spannableString);
        holder.tv_profit.setText(bean.getMonth_realised_pnl());
        holder.tv_profit_rate.setText(bean.getMonth_realised_pnl_ratio());
        if (bean.getColor() != null) {
            ColorUtils.setTextColor(holder.tv_profit_rate, bean.getColor().getMonth_realised_pnl_ratio(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_text_1_color));
            ColorUtils.setTextColor(holder.tv_profit, bean.getColor().getMonth_realised_pnl(), FollowOrderSDK.ins().getCustomAttrColor(mContext, R.attr.fo_text_1_color));
        }
        if (holder.getAdapterPosition() % 2 == 0) {
            holder.llIntro.setBackgroundResource(R.drawable.fo_bg_list_yellow);
        } else {
            holder.llIntro.setBackgroundResource(R.drawable.fo_bg_list_blue);
        }
        if (TextUtils.isEmpty(bean.getDesc())) {
            holder.llIntro.setVisibility(View.GONE);
        } else {
            holder.llIntro.setVisibility(View.VISIBLE);
        }
        if (bean != null && bean.getCurrency_list() != null && bean.getCurrency_list().size() > 0) {
            holder.rlCoinTitle.setVisibility(View.VISIBLE);
            holder.rvCoin.setVisibility(View.VISIBLE);
            FollowCoinAdapter coinAdapter = new FollowCoinAdapter(bean.getCurrency_list());
            coinAdapter.setOnCoinActionListener(new FollowCoinAdapter.OnCoinActionListener() {
                @Override
                public void onFollowOrder(FollowCoinBean coinBean) {
                    if (actionListener != null) {
                        actionListener.onFollowOrder(bean, coinBean);
                    }
                }
            });
            holder.rvCoin.setAdapter(coinAdapter);
        } else {
            holder.rlCoinTitle.setVisibility(View.GONE);
            holder.rvCoin.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                PersonalActivity.start(mContext, bean.getUid(), bean.getKol_id());
            }
        });

    }

    public interface OnCoinActionListener {
        void onFollowOrder(FollowBean followBean, FollowCoinBean coinBean);
    }

    public void setOnCoinActionListener(OnCoinActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public static class FollowViewHolder extends BaseViewHolder {
        RoundedImageView ivAvatar;
        TextView tvNickname;
        TextView tvExchange;
        TextView tvPriceType;
        LinearLayout llIntro;
        TextView tv_profit_rate;
        TextView tv_profit;
        TextView tv_follow_count;
        TextView tvIntro;
        RelativeLayout rlCoinTitle;
        RecyclerView rvCoin;

        public FollowViewHolder(View view) {
            super(view);
            ivAvatar = view.findViewById(R.id.iv_avatar);
            tvNickname = view.findViewById(R.id.tv_nickname);
            tvExchange = view.findViewById(R.id.tv_exchange);
            tvPriceType = view.findViewById(R.id.tv_price_type);
            llIntro = view.findViewById(R.id.ll_intro);
            tv_profit_rate = view.findViewById(R.id.tv_profit_rate);
            tv_profit = view.findViewById(R.id.tv_profit);
            tv_follow_count = view.findViewById(R.id.tv_follow_count);
            tvIntro = view.findViewById(R.id.tv_intro);
            rlCoinTitle = view.findViewById(R.id.rl_coin_title);
            rvCoin = view.findViewById(R.id.rv_coin);

            if (rvCoin != null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rvCoin.setLayoutManager(layoutManager);
            }
        }
    }

}
