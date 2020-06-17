package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class HoldContractHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractPosition> mNews = new ArrayList<>();

    public static class HoldContractHistoryHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvOpenType;
        TextView tvOpenPrice;
        TextView tvGains;
        TextView tvGainsBalance;
        TextView tvAmountLiquidated;
        TextView tvTagPrice;
        TextView tvForceClosePrice;
        TextView tvMargins;

        public HoldContractHistoryHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvOpenType = itemView.findViewById(R.id.tv_open_type);
            tvOpenPrice = itemView.findViewById(R.id.tv_open_price_value);
            tvGains = itemView.findViewById(R.id.tv_gains_value);
            tvGainsBalance = itemView.findViewById(R.id.tv_gains_balance_value);
            tvAmountLiquidated = itemView.findViewById(R.id.tv_amount_liquidated_value);
            tvTagPrice = itemView.findViewById(R.id.tv_tag_price_value);
            tvForceClosePrice = itemView.findViewById(R.id.tv_forced_close_price_value);
            tvMargins = itemView.findViewById(R.id.tv_margins_value);
        }
    }

    public HoldContractHistoryAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractPosition> news) {
        if (news == null) {
            return;
        }
        mNews = news;
    }

    public ContractPosition getItem(int position) {
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
        final HoldContractHistoryHolder itemViewHolder = (HoldContractHistoryHolder) holder;

        final Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mNews.get(position).getInstrument_id());
        if (contract == null) {
            return;
        }

        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());

        ContractTicker contractTicker = ContractPublicDataAgent.INSTANCE.getContractTicker(mNews.get(position).getInstrument_id());

        int position_type = mNews.get(position).getSide();
        if (position_type == 1) { //多仓
            itemViewHolder.tvType.setText(R.string.sl_str_buy_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);

        } else if (position_type == 2) { //空仓
            itemViewHolder.tvType.setText(R.string.sl_str_sell_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);
        }

        int open_type = mNews.get(position).getPosition_type();
        if (open_type == 1) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_gradually_position);
        } else if (open_type == 2) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_full_position);
        }

        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvOpenPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getAvg_open_px(), contract.getPrice_index())) + contract.getQuote_coin());

        double profitRate = MathHelper.round(mNews.get(position).getRealised_pnl());
        double profit = MathHelper.round(mNews.get(position).getEarnings());
        itemViewHolder.tvGains.setText(dfDefault.format(MathHelper.round(profitRate, contract.getValue_index())));
        itemViewHolder.tvGains.setTextColor(profitRate >= 0.0 ? mContext.getResources().getColor(R.color.sl_colorGreen) : mContext.getResources().getColor(R.color.sl_colorRed));
        itemViewHolder.tvGainsBalance.setText(dfDefault.format(MathHelper.round(profit, contract.getValue_index())) + contract.getMargin_coin());

        itemViewHolder.tvAmountLiquidated.setText(dfVol.format(MathHelper.round(mNews.get(position).getClose_qty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvTagPrice.setText((contractTicker != null ? dfDefault.format(MathHelper.round(contractTicker.getFair_px(), contract.getPrice_index())) : "0")  + contract.getQuote_coin());
        itemViewHolder.tvForceClosePrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getAvg_close_px(), contract.getPrice_index())) + contract.getQuote_coin());
        itemViewHolder.tvMargins.setText(dfDefault.format(MathHelper.round(mNews.get(position).getOim(), contract.getValue_index())) + contract.getMargin_coin());
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_hold_contract_history, parent, false);
        return new HoldContractHistoryHolder(v, viewType);
    }
}
