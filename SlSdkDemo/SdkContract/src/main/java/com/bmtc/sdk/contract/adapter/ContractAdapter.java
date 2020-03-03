package com.bmtc.sdk.contract.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmtc.sdk.contract.ContractTickerOneActivity;
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

public class ContractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private int mFlag = 0;
    private List<ContractTicker> mNews = new ArrayList<>();

    public static class SpotViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlContent;

        ImageView ivCollect;
        TextView tvContractName;
        TextView tvContractVol;
        TextView tvContractCurrent;
        TextView tvContractCurrentUsd;
        TextView tvContractChg;

        public SpotViewHolder(View itemView, int type) {
            super(itemView);

            rlContent = itemView.findViewById(R.id.rl_content);

            ivCollect = itemView.findViewById(R.id.iv_collect);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvContractVol = itemView.findViewById(R.id.tv_contract_vol);
            tvContractCurrent = itemView.findViewById(R.id.tv_contract_current);
            tvContractCurrentUsd = itemView.findViewById(R.id.tv_contract_current_usd);
            tvContractChg = itemView.findViewById(R.id.tv_contract_chg);
        }
    }

    public ContractAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractTicker> news) {
        mNews = news;
    }

    private int mChanged = 0;
    private int mPos = 0;
    public void setData(List<ContractTicker> news, int changed, int pos) {
        mNews = news;
        mChanged = changed;
        mPos = pos;
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

        if (mContext == null) {
            mContext = LogicGlobal.sContext;
        }

        if (mPos == position) {
            if (mChanged == 1) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(itemViewHolder.rlContent, "backgroundColor",
                        mContext.getResources().getColor(R.color.sl_colorGreenTrans), mContext.getResources().getColor(R.color.sl_transparent));
                colorAnim.setDuration(1000);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setRepeatCount(0);
                colorAnim.start();
            } else if (mChanged == 2) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(itemViewHolder.rlContent, "backgroundColor",
                        mContext.getResources().getColor(R.color.sl_colorRedTrans), mContext.getResources().getColor(R.color.sl_transparent));
                colorAnim.setDuration(1000);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.setRepeatCount(0);
                colorAnim.start();
            }
        }


        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfRate = NumberUtil.getDecimal(2);

        //不显示收藏
//        Collect collect = LogicCollects.getInstance().get(mNews.get(position).getName());
//        itemViewHolder.ivCollect.setVisibility(collect == null ? View.GONE : View.VISIBLE);

        String name = mNews.get(position).getName();
        if (name.contains(" [")) {
            name = name.substring(0, name.indexOf(" ["));
        }

        if (name.contains("[")) {
            name = name.substring(0, name.indexOf("["));
        }
        itemViewHolder.tvContractName.setText(name);

        double vol = MathHelper.round(mNews.get(position).getQty_day(), contract.getVol_index());
        itemViewHolder.tvContractVol.setText(mContext.getString(R.string.sl_str_vol) + " " + NumberUtil.getBigVolum(mContext, dfVol, vol));

        double chg = MathHelper.round(Double.parseDouble(mNews.get(position).getChange_rate()) * 100, 2);

//        double backUsd = MathHelper.round(LogicGlobal.sGlobalData.getCoin_price_usd(contract.getQuote_coin()), 6);
//        double backCny = backUsd * LogicGlobal.sUsdRateCNY;

//        double current_usd = MathHelper.round(mNews.get(position).getLast_px(), contract.getPrice_index());
//        double current_cny = MathHelper.round(MathHelper.mul(current_usd, backCny), 2);

//        String sUsd = (TextUtils.equals(contract.getQuote_coin(), "USDT") ? "$" : "") + dfPrice.format(current_usd);
//        String sCNY = "￥"+ dfRate.format(current_cny);

      //  itemViewHolder.tvContractCurrent.setText(sUsd);
        itemViewHolder.tvContractCurrent.setTextColor((chg >= 0) ? mContext.getResources().getColor(R.color.sl_colorGreen): mContext.getResources().getColor(R.color.sl_colorRed));
       // itemViewHolder.tvContractCurrentUsd.setText(sCNY);


        itemViewHolder.tvContractChg.setBackgroundResource((chg >= 0) ? R.drawable.sl_bg_corner_green : R.drawable.sl_bg_corner_red);
        itemViewHolder.tvContractChg.setText((chg >= 0) ? ("+" + dfRate.format(chg) + "%") : (dfRate.format(chg) + "%"));

        itemViewHolder.tvContractName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });
        itemViewHolder.tvContractVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });
        itemViewHolder.tvContractCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick(mNews.get(position).getInstrument_id());
            }
        });
        itemViewHolder.tvContractCurrentUsd.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("contract_id", contract_id);
        intent.setClass(mContext, ContractTickerOneActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract, parent, false);
        return new SpotViewHolder(v, viewType);
    }
}
