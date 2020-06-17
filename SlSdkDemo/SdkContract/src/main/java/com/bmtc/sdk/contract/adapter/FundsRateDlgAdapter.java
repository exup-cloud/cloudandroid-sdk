package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.contract.sdk.data.ContractFundingRate;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zj on 2018/3/8.
 */

public class FundsRateDlgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mFlag = 0;
    private List<ContractFundingRate> mNews = new ArrayList<>();

    private SimpleDateFormat gmtSdf = new SimpleDateFormat("MM/dd HH:mm:ss");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static class TradeHistoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rlTitle;
        LinearLayout rlContent;

        TextView tvTimeValue;
        TextView tvFundingRateValue;

        public TradeHistoryViewHolder(View itemView, int type) {
            super(itemView);


            rlTitle = itemView.findViewById(R.id.ll_title);
            rlContent = itemView.findViewById(R.id.ll_content);

            tvTimeValue = itemView.findViewById(R.id.tv_time_value);
            tvFundingRateValue = itemView.findViewById(R.id.tv_funding_rate_value);
        }
    }

    public FundsRateDlgAdapter(Context context) {
        mContext = context;
        gmtSdf.setTimeZone(TimeZone.getTimeZone("GMT+16:00"));
    }

    public void setData(List<ContractFundingRate> news) {
        mNews = news;
    }

    public ContractFundingRate getItem(int position) {
        return mNews.get(position);
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TradeHistoryViewHolder itemViewHolder = (TradeHistoryViewHolder) holder;

        double rate = MathHelper.mul(mNews.get(position).getRate(), "100");
        itemViewHolder.tvFundingRateValue.setText(NumberUtil.getDecimal(4).format(rate) + "%");

        itemViewHolder.tvTimeValue.setText(formatTime(mNews.get(position).getTimestamp() * 1000));

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_funding_rate_dlg, parent, false);
        return new TradeHistoryViewHolder(v, viewType);
    }

    private String formatTime(long time) {

        Date date = new Date(time);
        return gmtSdf.format(date);
    }
}
