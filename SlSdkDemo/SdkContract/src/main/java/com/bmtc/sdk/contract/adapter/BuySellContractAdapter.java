package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.trans.data.DepthData;
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

public class BuySellContractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mFlag = 1; //1 buy ;2 sell
    private int mDecimals = 8;
    private int mVolIndex = 2;
    private int mPriceIndex = 8;
    private double mMaxVol = 0;
    private int mShowNum = 6;
    private List<DepthData> mNews = new ArrayList<>();
    private OnBuySellContractClickedListener mListener;
    private Contract mContract;

    public interface OnBuySellContractClickedListener {
        void onBuySellContractClick(DepthData depthData, String showVol, int flag);
        void onBuySellContractVolClick(DepthData depthData, String showVol, int flag);
    }
    public static class BuySellContractHolder extends RecyclerView.ViewHolder {

        View vRoot;

        TextView tvVolume;
        TextView tvPrice;
        ProgressBar pbVolume;

        public BuySellContractHolder(View itemView, int type) {
            super(itemView);

            vRoot = itemView;
            pbVolume = itemView.findViewById(R.id.pb_volume);
            tvVolume = itemView.findViewById(R.id.tv_volume);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }

    public BuySellContractAdapter(Context context, OnBuySellContractClickedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setData(final List<DepthData> news, final int flag, final int decimals, final int show_num, int contractId) {

        mFlag = flag;
        mDecimals = decimals;
        mShowNum = show_num;

        if (news != null) {
            Collections.sort(news, new Comparator<DepthData>() {
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
            for (int i=0; i<news.size(); i++) {

                if (data.size() > 0) {
                    if (data.get(data.size() - 1).getPriceDemals(decimals) == news.get(i).getPriceDemals(decimals)) {
                        String vol = MathHelper.add2String(data.get(data.size() - 1).getVol(), news.get(i).getVol());
                        data.get(data.size() - 1).setVol(vol);
                    } else {
                        DepthData depthData = new DepthData();
                        depthData.setVol(news.get(i).getVol());
                        depthData.setPrice(news.get(i).getPrice());
                        data.add(depthData);
                    }

                } else {
                    DepthData depthData = new DepthData();
                    depthData.setVol(news.get(i).getVol());
                    depthData.setPrice(news.get(i).getPrice());
                    data.add(depthData);
                }
            }

            mNews.clear();

            if (mFlag == 1) {
                if (data.size() > show_num) {
                    mNews.addAll(data.subList(0, show_num));
                } else {
                    mNews.addAll(data);
                }
            } else if (mFlag == 2) {
                if (data.size() > show_num) {
                    mNews.addAll(data.subList(data.size()-show_num, data.size()));
                } else {
                    mNews.addAll(data);
                    int emptyNum = show_num - data.size();
                    for (int i=0; i<emptyNum; i++) {
                        DepthData depthData = new DepthData();
                        depthData.setPrice("");
                        depthData.setVol("");
                        mNews.add(0, depthData);
                    }
                }
            }

            mMaxVol = 0;
            for (int i=0; i<mNews.size(); i++) {
                if (TextUtils.isEmpty(mNews.get(i).getVol())) {
                    continue;
                }

                if (MathHelper.round(mNews.get(i).getVol(), 8) > mMaxVol) {
                    mMaxVol = MathHelper.round(mNews.get(i).getVol(), mDecimals);
                }
            }
        }


        mContract = LogicGlobal.getContract(contractId);
        if (mContract != null) {
            mVolIndex = mContract.getVol_index();
            mPriceIndex = mContract.getPrice_index() - 1;
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(mNews.size(), mShowNum);
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
        final BuySellContractHolder itemViewHolder = (BuySellContractHolder) holder;

        if (TextUtils.isEmpty(mNews.get(position).getVol()) || TextUtils.isEmpty(mNews.get(position).getPrice())) {
            itemViewHolder.vRoot.setVisibility(View.GONE);
            return;
        }

        DecimalFormat dfPirce = NumberUtil.getDecimal(mPriceIndex);

        double maxVol = mMaxVol;
        double vol = MathHelper.round(mNews.get(position).getVol(), mVolIndex);
        double price = MathHelper.round(mNews.get(position).getPrice(), mPriceIndex);

        if (mContract != null) {
            List<ContractOrder> contractOrders = BTContract.getInstance().getContractOrder(mContract.getInstrument_id());
            if (contractOrders != null) {
                for (int i=0; i<contractOrders.size(); i++) {
                    ContractOrder order = contractOrders.get(i);
                    if (order == null) {
                        continue;
                    }
                    double orderPrice = MathHelper.round(order.getPx(), mPriceIndex);
                    if (orderPrice == price) {
                        itemViewHolder.tvVolume.setTextColor(mContext.getResources().getColor(R.color.sl_colorYellowNormal));
                    } else {
                        itemViewHolder.tvVolume.setTextColor(mContext.getResources().getColor(R.color.sl_grayText));
                    }
                }
            }
        }

        itemViewHolder.tvVolume.setText(ContractCalculate.getVolUnitNoSuffix(mContract, vol, price));
        itemViewHolder.tvPrice.setText(dfPirce.format(price));
        itemViewHolder.pbVolume.setProgress(100 - (int) (100 * vol / maxVol));

        int color = (mFlag == 1) ? mContext.getResources().getColor(R.color.sl_colorGreen): mContext.getResources().getColor(R.color.sl_colorRed);
        itemViewHolder.tvPrice.setTextColor(color);

        Drawable drawable = (mFlag == 1) ? mContext.getResources().getDrawable(R.drawable.sl_buy_progress): mContext.getResources().getDrawable(R.drawable.sl_sell_progress);
        itemViewHolder.pbVolume.setProgressDrawable(drawable);

        itemViewHolder.tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) {
                    return;
                }

                if (mFlag == 1) {
                    String total = "0";
                    for (int i=0; i<=position; i++) {
                        total = MathHelper.add2String(total, mNews.get(i).getVol());
                    }

                    mListener.onBuySellContractClick(mNews.get(position), total, mFlag);
                } else {
                    String total = "0";
                    for (int i=position; i<mNews.size(); i++) {
                        total = MathHelper.add2String(total, mNews.get(i).getVol());
                    }

                    mListener.onBuySellContractClick(mNews.get(position), total, mFlag);
                }
            }
        });
        itemViewHolder.tvVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener == null) {
                    return;
                }

                if (mFlag == 1) {
                    String total = "0";
                    for (int i=0; i<=position; i++) {
                        total = MathHelper.add2String(total, mNews.get(i).getVol());
                    }

                    mListener.onBuySellContractVolClick(mNews.get(position), total, mFlag);
                } else {
                    String total = "0";
                    for (int i=position; i<mNews.size(); i++) {
                        total = MathHelper.add2String(total, mNews.get(i).getVol());
                    }

                    mListener.onBuySellContractVolClick(mNews.get(position), total, mFlag);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_buy_sell_contract, parent, false);
        return new BuySellContractHolder(v, viewType);
    }
}
