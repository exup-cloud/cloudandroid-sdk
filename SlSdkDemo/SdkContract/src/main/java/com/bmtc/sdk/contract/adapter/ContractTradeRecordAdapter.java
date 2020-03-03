package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractTrade;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;

import java.text.DateFormat;
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

public class ContractTradeRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractTrade> mNews = new ArrayList<>();

    public static class ContractTradeRecordHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvTime;
        TextView tvVolume;
        TextView tvPrice;
        TextView tvValue;
        TextView tvCommissionRate;

        public ContractTradeRecordHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvVolume = itemView.findViewById(R.id.tv_volume_value);
            tvPrice = itemView.findViewById(R.id.tv_price_value);
            tvValue = itemView.findViewById(R.id.tv_value_value);
            tvCommissionRate = itemView.findViewById(R.id.tv_commission_rate_value);
        }
    }

    public ContractTradeRecordAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractTrade> news) {
        if (news == null) {
            mNews.clear();
            return;
        }
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
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ContractTradeRecordHolder itemViewHolder = (ContractTradeRecordHolder) holder;
        Contract contract = LogicGlobal.getContract(mNews.get(position).getInstrument_id());
        if (contract == null) {
            return;
        }
        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());

        int way = mNews.get(position).getSide();
        if (way == ContractTrade.CONTRACT_ORDER_WAY_BUY_OPEN_LONG) {
            itemViewHolder.tvType.setText(R.string.sl_str_buy_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);
        } else if (way == ContractTrade.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT) {
            itemViewHolder.tvType.setText(R.string.sl_str_sell_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);
        } else if (way == ContractTrade.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
            itemViewHolder.tvType.setText(R.string.sl_str_buy_close);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);
        } else if (way == ContractTrade.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
            itemViewHolder.tvType.setText(R.string.sl_str_sell_close);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);
        }

        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getQty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvPrice.setText(mNews.get(position).getPx() + contract.getQuote_coin());
        double value = ContractCalculate.CalculateContractValue(
                mNews.get(position).getQty(),
                mNews.get(position).getPx(),
                contract);
        itemViewHolder.tvValue.setText(dfDefault.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());
        itemViewHolder.tvCommissionRate.setText(NumberUtil.getDecimal(-1).format(MathHelper.sub(mNews.get(position).getTake_fee(), mNews.get(position).getMake_fee()))+ contract.getMargin_coin());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            String create_at = mNews.get(position).getCreated_at();
            create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
            Date date = sdf.parse(create_at);

            DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            itemViewHolder.tvTime.setText(gmtFormat.format(date));
        } catch (ParseException ignored) {
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_trade_record, parent, false);
        return new ContractTradeRecordHolder(v, viewType);
    }
}
