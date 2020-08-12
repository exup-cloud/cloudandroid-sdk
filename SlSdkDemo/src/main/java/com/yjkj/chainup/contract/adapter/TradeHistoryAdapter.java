package com.yjkj.chainup.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractTrade;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;
import com.yjkj.chainup.R;
import com.yjkj.chainup.contract.uilogic.LogicContractSetting;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zj on 2018/3/8.
 */

public class TradeHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractTrade> mNews = new ArrayList<>();

    private SimpleDateFormat gmtSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static class TradeHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvTimeValue;
        TextView tvPriceValue;
        TextView tvVolumeValue;

        public TradeHistoryViewHolder(View itemView, int type) {
            super(itemView);

            tvTimeValue = itemView.findViewById(R.id.tv_time_value);
            tvPriceValue = itemView.findViewById(R.id.tv_price_value);
            tvVolumeValue = itemView.findViewById(R.id.tv_volume_value);
        }
    }

    public TradeHistoryAdapter(Context context) {
        mContext = context;
        gmtSdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void setData(List<ContractTrade> news) {
        mNews = news;
    }

    public ContractTrade getItem(int position) {
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

        if (mContext == null) {
            return;
        }
        DecimalFormat dfVol = NumberUtil.getDecimal(0);
        DecimalFormat dfPrice = NumberUtil.getDecimal(0);
            int contractId = mNews.get(position).getInstrument_id();
            Contract contract = ContractPublicDataAgent.INSTANCE.getContract(contractId);
            if (contract != null) {
                int unit = LogicContractSetting.getContractUint(mContext);
                dfPrice = NumberUtil.getDecimal(contract.getPrice_index() - 1);

                int way = mNews.get(position).getSide();
                double deal_price = MathHelper.round(mNews.get(position).getPx(), 8);
                double deal_volume = MathHelper.round(mNews.get(position).getQty(), 8);

                itemViewHolder.tvPriceValue.setTextColor((way <= 4) ? mContext.getResources().getColor(R.color.main_green): mContext.getResources().getColor(R.color.main_red));
                itemViewHolder.tvPriceValue.setText(dfPrice.format(deal_price));

                itemViewHolder.tvVolumeValue.setText(ContractCalculate.getVolUnitNoSuffix(contract, deal_volume, deal_price,LogicContractSetting.getContractUint(mContext)));
                itemViewHolder.tvTimeValue.setText(formatTime(mNews.get(position).getCreated_at()));
            }else {
                itemViewHolder.tvPriceValue.setText("--");
                itemViewHolder.tvVolumeValue.setText("--");
                itemViewHolder.tvTimeValue.setText("--");
            }




    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_trade_history, parent, false);
        return new TradeHistoryViewHolder(v, viewType);
    }

    private String formatTime(String createTime) {
        try {
            String create_at = createTime;
            create_at = create_at.substring(0, create_at.indexOf(".")) + "Z";
            Date date = gmtSdf.parse(create_at);

            return sdf.format(date);
        } catch (ParseException ignored) {
        }
        return "";
    }
}
