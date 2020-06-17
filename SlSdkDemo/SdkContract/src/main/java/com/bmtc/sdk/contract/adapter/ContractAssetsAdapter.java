package com.bmtc.sdk.contract.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractAccount;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zj on 2018/3/6.
 */

public class ContractAssetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractAccount> mNews = new ArrayList<>();

    public static class HoldCoinsViewHolder extends RecyclerView.ViewHolder {

        View vRoot;

        private TextView tvName;
        private TextView tvBalance;
        private TextView tvAvailable;
        private TextView tvMargin;
        private TextView tvFloatingGains;
        private TextView tvPositionsMargin;
        private TextView tvEntrustMargin;

        public HoldCoinsViewHolder(View itemView, int type) {
            super(itemView);

            vRoot = itemView;

            tvName = itemView.findViewById(R.id.tv_name);
            tvBalance = itemView.findViewById(R.id.tv_balance);
            tvAvailable = itemView.findViewById(R.id.tv_available_value);
            tvMargin = itemView.findViewById(R.id.tv_margin_balance_value);
            tvFloatingGains = itemView.findViewById(R.id.tv_floating_gains_value);
            tvPositionsMargin = itemView.findViewById(R.id.tv_positions_margin_value);
            tvEntrustMargin = itemView.findViewById(R.id.tv_entrust_margin_value);
        }
    }

    public ContractAssetsAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractAccount> news) {
        if (news == null) {
            return;
        }
        Collections.sort(news, new Comparator<ContractAccount>() {
            @Override
            public int compare(ContractAccount o1, ContractAccount o2) {
                if (o1.getCoin_code().equals("USDT")) {
                    return -1;
                } else if (o2.getCoin_code().equals("USDT")){
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        mNews = news;
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
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HoldCoinsViewHolder itemViewHolder = (HoldCoinsViewHolder) holder;

        itemViewHolder.tvName.setText(mNews.get(position).getCoin_code());

        DecimalFormat dfDefault = NumberUtil.getDecimal(6);

        ContractAccount contractAccount = ContractUserDataAgent.INSTANCE.getContractAccount(mNews.get(position).getCoin_code());
        if (contractAccount != null) {
            double freeze_vol = MathHelper.round(contractAccount.getFreeze_vol());
            double available_vol = MathHelper.round(contractAccount.getAvailable_vol());

            double longProfitAmount = 0.0; //多仓位的未实现盈亏
            double shortProfitAmount = 0.0; //空仓位的未实现盈亏

            double position_margin = 0.0;

            List<ContractPosition> contractPositions = ContractUserDataAgent.INSTANCE.getCoinPositions(mNews.get(position).getCoin_code(),false);
            if (contractPositions != null && contractPositions.size() > 0) {
                for (int i = 0; i < contractPositions.size(); i++) {
                    ContractPosition contractPosition = contractPositions.get(i);
                    if (contractPosition == null) {
                        continue;
                    }

                    Contract positionContract = ContractPublicDataAgent.INSTANCE.getContract(contractPosition.getInstrument_id());
                    ContractTicker contractTicker = ContractPublicDataAgent.INSTANCE.getContractTicker(contractPosition.getInstrument_id());
                    if (positionContract == null || contractTicker == null) {
                        continue;
                    }

                    position_margin += MathHelper.round(contractPosition.getIm());

                    if (contractPosition.getSide() == 1) { //开多
                        longProfitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                                contractPosition.getCur_qty(),
                                contractPosition.getAvg_cost_px(),
                                contractTicker.getFair_px(),
                                positionContract.getFace_value(),
                                positionContract.isReserve());
                    } else if (contractPosition.getSide() == 2) { //开空
                        shortProfitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                                contractPosition.getCur_qty(),
                                contractPosition.getAvg_cost_px(),
                                contractTicker.getFair_px(),
                                positionContract.getFace_value(),
                                positionContract.isReserve());
                    }
                }
            }

            double balance = MathHelper.add(freeze_vol, available_vol);

            double pack_balance = MathHelper.add(balance, position_margin);

            double margin_rate = MathHelper.div(freeze_vol + position_margin, balance + position_margin + longProfitAmount + shortProfitAmount) * 100;

            double profit = longProfitAmount + shortProfitAmount;
            profit = Math.min(0, profit);

            //mBasicInfoTv.setText(getString(R.string.str_margin_occupancy_rate) + " : " + dfDefault.format(MathHelper.round(margin_rate, contract.getValue_index())) + "%");
            itemViewHolder.tvBalance.setText(dfDefault.format(MathHelper.round(balance + position_margin + longProfitAmount + shortProfitAmount, 6)));
            itemViewHolder.tvAvailable.setText(dfDefault.format(MathHelper.round(pack_balance, 6)));
            itemViewHolder.tvMargin.setText(dfDefault.format(MathHelper.round(available_vol, 6)));
            itemViewHolder.tvFloatingGains.setText(dfDefault.format(MathHelper.round(longProfitAmount + shortProfitAmount, 6)));
            itemViewHolder.tvPositionsMargin.setText(dfDefault.format(MathHelper.round(position_margin, 6)));
            itemViewHolder.tvEntrustMargin.setText(dfDefault.format(MathHelper.round(freeze_vol, 6)));

        }

        itemViewHolder.vRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(itemViewHolder.vRoot, position);
            }
        });
    }

    private void doClick(View view, int position) {
//        Intent intent = new Intent();
//        intent.putExtra("coin_code", mNews.get(position).getCoin_code());
//        intent.setClass(mContext, ContractPositionsActivity.class);
//        mContext.startActivity(intent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_assets, parent, false);
        return new HoldCoinsViewHolder(v, viewType);
    }
}
