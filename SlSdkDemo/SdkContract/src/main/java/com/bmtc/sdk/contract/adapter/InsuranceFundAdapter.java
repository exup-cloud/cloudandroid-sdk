package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.InsuranceFund;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class InsuranceFundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mFlag = 0;
    private List<InsuranceFund> mNews = new ArrayList<>();

    private SimpleDateFormat gmtSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static class TradeHistoryViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rlTitle;
        LinearLayout rlContent;

        TextView tvTime;
        TextView tvVolume;

        TextView tvTimeValue;
        TextView tvVolumeValue;

        public TradeHistoryViewHolder(View itemView, int type) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time);
            tvVolume = itemView.findViewById(R.id.tv_remain);

            rlTitle = itemView.findViewById(R.id.ll_title);
            rlContent = itemView.findViewById(R.id.ll_content);

            tvTimeValue = itemView.findViewById(R.id.tv_time_value);
            tvVolumeValue = itemView.findViewById(R.id.tv_remain_value);
        }
    }

    public InsuranceFundAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<InsuranceFund> news) {
        mNews = news;
    }

    public InsuranceFund getItem(int position) {
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

        int contractId = mNews.get(position).getInstrument_id();
        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(contractId);
        if (contract != null) {

            double vol = MathHelper.round(mNews.get(position).getVol(), contract.getValue_index());
            itemViewHolder.tvVolumeValue.setText(NumberUtil.getDecimal(-1).format(vol) + contract.getMargin_coin());
        }


        itemViewHolder.rlTitle.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        itemViewHolder.tvTimeValue.setText(formatTime(mNews.get(position).getTimestamp() * 1000));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_insurance_fund, parent, false);
        return new TradeHistoryViewHolder(v, viewType);
    }

    private String formatTime(long time) {
        Date date = new Date(time);
        return gmtSdf.format(date);
    }
}
