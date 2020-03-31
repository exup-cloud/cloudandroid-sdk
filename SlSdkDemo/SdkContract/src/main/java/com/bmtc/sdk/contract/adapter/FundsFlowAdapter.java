package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractCashBook;
import com.bmtc.sdk.library.trans.data.SpotCoin;
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

public class FundsFlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractCashBook> mNews = new ArrayList<>();

    public static class FundsFlowHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvAmount;
        TextView tvFee;
        TextView tvBalance;
        TextView tvTime;

        public FundsFlowHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type_value);
            tvAmount = itemView.findViewById(R.id.tv_amount_value);
            tvFee = itemView.findViewById(R.id.tv_fee_value);
            tvBalance = itemView.findViewById(R.id.tv_balance_value);
            tvTime = itemView.findViewById(R.id.tv_time_value);
        }
    }

    public FundsFlowAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractCashBook> news) {
        if (news == null) {
            mNews.clear();
            return;
        }
        mNews = news;
    }

    public ContractCashBook getItem(int position) {
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
        final FundsFlowHolder itemViewHolder = (FundsFlowHolder) holder;

        Contract contract = LogicGlobal.getContract(mNews.get(position).getContract_id());
        if (contract == null) {
            contract = LogicGlobal.getContract(mNews.get(position).getCoin_code());
            if (contract == null) {
                return;
            }
        }


        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());

        itemViewHolder.tvAmount.setText(dfDefault.format(MathHelper.round(mNews.get(position).getDeal_count(), 6)));
        itemViewHolder.tvFee.setText(dfDefault.format(MathHelper.round(mNews.get(position).getFee(), 6)));
        itemViewHolder.tvBalance.setText(dfDefault.format(MathHelper.round(mNews.get(position).getLast_assets(), 6)));

        int action = mNews.get(position).getAction();
        switch (action) {
            case 1:
                itemViewHolder.tvType.setText(R.string.sl_str_buy_open);
                break;
            case 2:
                itemViewHolder.tvType.setText(R.string.sl_str_buy_close);
                break;
            case 3:
                itemViewHolder.tvType.setText(R.string.sl_str_sell_close);
                break;
            case 4:
                itemViewHolder.tvType.setText(R.string.sl_str_sell_open);
                break;
            case 5:
            case 7:
                itemViewHolder.tvType.setText(R.string.sl_str_transfer_bb2contract);
                break;
            case 6:
            case 8:
                itemViewHolder.tvType.setText(R.string.sl_str_transfer_contract2bb);
                break;
            case 9:
                itemViewHolder.tvType.setText(R.string.sl_str_transferim_position2contract);
                break;
            case 10:
                itemViewHolder.tvType.setText(R.string.sl_str_transferim_contract2position);
                break;
            case 11:
                itemViewHolder.tvType.setText(R.string.sl_str_position_fee);
                break;
            default:
                itemViewHolder.tvType.setText("--");
                break;
        }

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_funds_flow, parent, false);
        return new FundsFlowHolder(v, viewType);
    }
}
