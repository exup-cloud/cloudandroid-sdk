package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.DepthData;
import com.bmtc.sdk.library.trans.data.Stock;
import com.bmtc.sdk.library.uilogic.LogicContractSetting;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class OrderBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mFlag = 0;
    private String mStockCode;
    private int mContractId = 0;
    private int mPriceIndex = 8;
    private int mVolIndex = 2;

    private double mMaxBuyVol = 0;
    private double mMaxSellVol = 0;

    private List<DepthData> mSells = new ArrayList<>();
    private List<DepthData> mBuys = new ArrayList<>();

    public static class OrderBookViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rlTitle;
        LinearLayout rlContent;

        TextView tvBid;
        TextView tvAsk;
        TextView tvPrice;

        ProgressBar pbBidVolume;
        TextView tvBidNum;
        TextView tvBidVolume;
        TextView tvBidPrice;

        ProgressBar pbAskVolume;
        TextView tvAskNum;
        TextView tvAskVolume;
        TextView tvAskPrice;

        public OrderBookViewHolder(View itemView, int type) {
            super(itemView);

            tvBid = itemView.findViewById(R.id.tv_buy_volume);
            tvAsk = itemView.findViewById(R.id.tv_sell_volume);
            tvPrice = itemView.findViewById(R.id.tv_price);

            rlTitle = itemView.findViewById(R.id.ll_title);
            rlContent = itemView.findViewById(R.id.ll_content);

            pbBidVolume = itemView.findViewById(R.id.pb_bid_volume);
            tvBidNum = itemView.findViewById(R.id.tv_bid_num);
            tvBidVolume = itemView.findViewById(R.id.tv_bid_volume);
            tvBidPrice = itemView.findViewById(R.id.tv_bid_price);

            pbAskVolume = itemView.findViewById(R.id.pb_ask_volume);
            tvAskNum = itemView.findViewById(R.id.tv_ask_num);
            tvAskVolume = itemView.findViewById(R.id.tv_ask_volume);
            tvAskPrice = itemView.findViewById(R.id.tv_ask_price);
        }
    }

    public OrderBookAdapter(Context context) {
        mContext = context;
    }

    public void setData(final List<DepthData> sells, final List<DepthData> buys, String stockCode, int contractId) {

        if (!TextUtils.isEmpty(stockCode)) {
            mStockCode = stockCode;
            Stock stock = null ;//LogicGlobal.getStock(stockCode);
            if (stock != null) {
                mPriceIndex = stock.getPrice_index();
                mVolIndex = stock.getVol_index();
            }
        }

        if (contractId > 0) {
            mContractId = contractId;
            Contract contract = LogicGlobal.getContract(contractId);
            if (contract != null) {
                mPriceIndex = contract.getPrice_index() - 1;
                mVolIndex = contract.getVol_index();
            }

            if (sells != null) {
                mSells.clear();
                mSells.addAll(sells);
                mMaxSellVol = 0;
                for (int i=0; i<mSells.size(); i++) {
                    if (TextUtils.isEmpty(mSells.get(i).getVol())) {
                        continue;
                    }

                    if (MathHelper.round(mSells.get(i).getVol()) > mMaxSellVol) {
                        mMaxSellVol = MathHelper.round(mSells.get(i).getVol());
                    }
                }
            }

            if (buys != null) {
                mBuys.clear();
                mBuys.addAll(buys);
                mMaxBuyVol = 0;
                for (int i=0; i<mBuys.size(); i++) {
                    if (TextUtils.isEmpty(mBuys.get(i).getVol())) {
                        continue;
                    }

                    if (MathHelper.round(mBuys.get(i).getVol()) > mMaxBuyVol) {
                        mMaxBuyVol = MathHelper.round(mBuys.get(i).getVol());
                    }
                }
            }
            return;
        }

        if (sells != null) {
            Collections.sort(sells, new Comparator<DepthData>() {
                @Override
                public int compare(DepthData o1, DepthData o2) {
                    if (MathHelper.round(o1.getPrice(), 8) < MathHelper.round(o2.getPrice(), 8)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            List<DepthData> data = new ArrayList<>();
            for (int i=0; i<sells.size(); i++) {

                if (data.size() > 0) {
                    if (data.get(data.size() - 1).getPriceDemals(8) == sells.get(i).getPriceDemals(8)) {
                        String vol = MathHelper.add2String(data.get(data.size() - 1).getVol(), sells.get(i).getVol());
                        data.get(data.size() - 1).setVol(vol);
                    } else {
                        DepthData depthData = new DepthData();
                        depthData.setVol(sells.get(i).getVol());
                        depthData.setPrice(sells.get(i).getPrice());
                        data.add(depthData);
                    }

                } else {
                    DepthData depthData = new DepthData();
                    depthData.setVol(sells.get(i).getVol());
                    depthData.setPrice(sells.get(i).getPrice());
                    data.add(depthData);
                }
            }

            mSells.clear();
            mSells.addAll(data);

            mMaxSellVol = 0;
            for (int i=0; i<mSells.size(); i++) {
                if (TextUtils.isEmpty(mSells.get(i).getVol())) {
                    continue;
                }

                if (MathHelper.round(mSells.get(i).getVol()) > mMaxSellVol) {
                    mMaxSellVol = MathHelper.round(mSells.get(i).getVol());
                }
            }
        }

        if (buys != null) {
            Collections.sort(buys, new Comparator<DepthData>() {
                @Override
                public int compare(DepthData o1, DepthData o2) {
                    if (MathHelper.round(o1.getPrice(), 8) > MathHelper.round(o2.getPrice(), 8)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            List<DepthData> data = new ArrayList<>();
            for (int i=0; i<buys.size(); i++) {

                if (data.size() > 0) {
                    if (data.get(data.size() - 1).getPriceDemals(8) == buys.get(i).getPriceDemals(8)) {
                        String vol = MathHelper.add2String(data.get(data.size() - 1).getVol(), buys.get(i).getVol());
                        data.get(data.size() - 1).setVol(vol);
                    } else {
                        DepthData depthData = new DepthData();
                        depthData.setVol(buys.get(i).getVol());
                        depthData.setPrice(buys.get(i).getPrice());
                        data.add(depthData);
                    }

                } else {
                    DepthData depthData = new DepthData();
                    depthData.setVol(buys.get(i).getVol());
                    depthData.setPrice(buys.get(i).getPrice());
                    data.add(depthData);
                }
            }

            mBuys.clear();
            mBuys.addAll(data);

            mMaxBuyVol = 0;
            for (int i=0; i<mBuys.size(); i++) {
                if (TextUtils.isEmpty(mBuys.get(i).getVol())) {
                    continue;
                }

                if (MathHelper.round(mBuys.get(i).getVol()) > mMaxBuyVol) {
                    mMaxBuyVol = MathHelper.round(mBuys.get(i).getVol());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(mSells.size(), mBuys.size());
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
        final OrderBookViewHolder itemViewHolder = (OrderBookViewHolder) holder;

        DecimalFormat dfVol = NumberUtil.getDecimal(mVolIndex);
        DecimalFormat dfPrice = NumberUtil.getDecimal(mPriceIndex);

        itemViewHolder.rlTitle.setVisibility(position == 0 ? View.VISIBLE : View.GONE);


        if (!TextUtils.isEmpty(mStockCode)) {
            String []codes = mStockCode.split("/");
            itemViewHolder.tvBid.setText(mContext.getString(R.string.sl_str_amount) + "(" + codes[0] + ")");
            itemViewHolder.tvAsk.setText(mContext.getString(R.string.sl_str_amount) + "(" + codes[0] + ")");
            itemViewHolder.tvPrice.setText(mContext.getString(R.string.sl_str_price) + "(" + codes[1] + ")");
        }

        if (mContractId > 0) {
            Contract contract = LogicGlobal.getContract(mContractId);
            if (contract != null) {
                int unit = LogicContractSetting.getContractUint(mContext);
                itemViewHolder.tvBid.setText(mContext.getString(R.string.sl_str_amount) + "("  + (unit == 0 ? mContext.getString(R.string.sl_str_contracts_unit) : contract.getBase_coin()) + ")");
                itemViewHolder.tvAsk.setText(mContext.getString(R.string.sl_str_amount) + "("  + (unit == 0 ? mContext.getString(R.string.sl_str_contracts_unit) : contract.getBase_coin()) + ")");
                itemViewHolder.tvPrice.setText(mContext.getString(R.string.sl_str_price) + "(" + contract.getQuote_coin() + ")");
            }
        }

        if (mBuys.size() > position) {
            double vol = MathHelper.round(mBuys.get(position).getVol(), mVolIndex);
            double price = MathHelper.round(mBuys.get(position).getPrice(), mPriceIndex);
            itemViewHolder.tvBidVolume.setText(dfVol.format(vol));
            itemViewHolder.tvBidPrice.setText(dfPrice.format(price));
            itemViewHolder.tvBidNum.setText((position + 1) + "");
            itemViewHolder.pbBidVolume.setProgress(100 - (int) (100 * vol / mMaxBuyVol));

            if (mContractId > 0) {
                Contract contract = LogicGlobal.getContract(mContractId);
                if (contract != null) {
                    itemViewHolder.tvBidVolume.setText(ContractCalculate.getVolUnitNoSuffix(contract, vol, price));
                }
            }
        } else {
            itemViewHolder.tvBidVolume.setText("--");
            itemViewHolder.tvBidPrice.setText("--");
            itemViewHolder.tvBidNum.setText("");
            itemViewHolder.pbBidVolume.setProgress(100);
        }

        if (mSells.size() > position) {
            double vol = MathHelper.round(mSells.get(position).getVol(), mVolIndex);
            double price = MathHelper.round(mSells.get(position).getPrice(), mPriceIndex);
            itemViewHolder.tvAskVolume.setText(dfVol.format(vol));
            itemViewHolder.tvAskPrice.setText(dfPrice.format(price));
            itemViewHolder.tvAskNum.setText((position + 1) + "");
            itemViewHolder.pbAskVolume.setProgress((int) (100 * vol / mMaxSellVol));

            if (mContractId > 0) {
                Contract contract = LogicGlobal.getContract(mContractId);
                if (contract != null) {
                    itemViewHolder.tvAskVolume.setText(ContractCalculate.getVolUnitNoSuffix(contract, vol, price));
                }
            }
        } else {
            itemViewHolder.tvAskVolume.setText("--");
            itemViewHolder.tvAskPrice.setText("--");
            itemViewHolder.tvAskNum.setText("");
            itemViewHolder.pbAskVolume.setProgress(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_order_book, parent, false);
        return new OrderBookViewHolder(v, viewType);
    }
}
