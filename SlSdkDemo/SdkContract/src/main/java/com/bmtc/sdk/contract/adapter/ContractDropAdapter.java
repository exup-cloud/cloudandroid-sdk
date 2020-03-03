package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/7.
 */

public class ContractDropAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private int mFlag = 0;
    private List<ContractTicker> mNews = new ArrayList<>();

    private OnContractDropClickedListener mListener;

    public interface OnContractDropClickedListener {
        void onContractDropClick(int contractId);
    }

    public static class SpotViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlContent;

        TextView tvContractName;
        TextView tvContractChg;

        public SpotViewHolder(View itemView, int type) {
            super(itemView);

            rlContent = itemView.findViewById(R.id.rl_content);

            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvContractChg = itemView.findViewById(R.id.tv_contract_chg);
        }
    }

    public ContractDropAdapter(Context context, OnContractDropClickedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(List<ContractTicker> news) {
        mNews = news;
    }


    public ContractTicker getItem(int position) {
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
        final SpotViewHolder itemViewHolder = (SpotViewHolder) holder;

        Contract contract = LogicGlobal.getContract(mNews.get(position).getInstrument_id());
        if (contract == null) {
            return;
        }

        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfRate = NumberUtil.getDecimal(2);

        String name = mNews.get(position).getDisplayName(mContext);
        itemViewHolder.tvContractName.setText(name);

        double chg = MathHelper.round(Double.parseDouble(mNews.get(position).getChange_rate()) * 100, 2);


        itemViewHolder.tvContractChg.setTextColor((chg >= 0) ? mContext.getResources().getColor(R.color.sl_colorGreen): mContext.getResources().getColor(R.color.sl_colorRed));
        itemViewHolder.tvContractChg.setText((chg >= 0) ? ("+" + dfRate.format(chg) + "%") : (dfRate.format(chg) + "%"));

        itemViewHolder.tvContractName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });
        itemViewHolder.tvContractChg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });
        itemViewHolder.rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });

    }


    private void doClick(int contract_id) {
        if (mListener != null) {
            mListener.onContractDropClick(contract_id);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_drop, parent, false);
        return new SpotViewHolder(v, viewType);
    }
}
