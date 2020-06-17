package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractOrder;
import com.contract.sdk.data.DepthData;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

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
    private  DecimalFormat dfPirce;

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


    public void bindContract(Contract contract) {
        mContract = contract;
        if (mContract != null) {
            mVolIndex = mContract.getVol_index();
            mPriceIndex = mContract.getPrice_index() - 1;
            dfPirce = NumberUtil.getDecimal(mPriceIndex);
        }
    }

    public List<DepthData> getNews() {
        return mNews;
    }

    public void setData(final List<DepthData> news, final int flag, final int decimals, final int show_num) {

        mFlag = flag;
        mDecimals = decimals;
        mShowNum = show_num;
        mNews = news;

        mMaxVol = 0;
        for (int i = 0; i < mNews.size(); i++) {
            if (mNews.get(i).getVol() > mMaxVol) {
                mMaxVol = MathHelper.round(mNews.get(i).getVol(), mDecimals);
            }
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

        if (mNews.get(position).getVol() == 0 || TextUtils.isEmpty(mNews.get(position).getPrice())) {
            //itemViewHolder.vRoot.setVisibility(View.GONE);
            return;
        }



        double maxVol = mMaxVol;
        double vol = MathHelper.round(mNews.get(position).getVol(), mVolIndex);
        double price = MathHelper.round(mNews.get(position).getPrice(), mPriceIndex);

        itemViewHolder.tvVolume.setText(ContractCalculate.getVolUnitNoSuffix(mContract, vol, price, LogicContractSetting.getContractUint(mContext)));
        itemViewHolder.tvPrice.setText(dfPirce.format(price));
        itemViewHolder.pbVolume.setProgress(100 - (int) (100 * vol / maxVol));

        int color = (mFlag == 1) ? mContext.getResources().getColor(R.color.sl_colorGreen) : mContext.getResources().getColor(R.color.sl_colorRed);
        itemViewHolder.tvPrice.setTextColor(color);

        Drawable drawable = (mFlag == 1) ? mContext.getResources().getDrawable(R.drawable.sl_buy_progress) : mContext.getResources().getDrawable(R.drawable.sl_sell_progress);
        itemViewHolder.pbVolume.setProgressDrawable(drawable);

        itemViewHolder.tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null) {
                    return;
                }
                int total = 0;
                for (int i = 0; i <= position; i++) {
                    total = total + mNews.get(i).getVol();
                }

                mListener.onBuySellContractClick(mNews.get(position), total + "", mFlag);
            }
        });
        itemViewHolder.tvVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener == null) {
                    return;
                }

                int total = 0;
                for (int i = 0; i <= position; i++) {
                    total = total + mNews.get(i).getVol();
                }

                mListener.onBuySellContractVolClick(mNews.get(position), total + "", mFlag);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_buy_sell_contract, parent, false);
        return new BuySellContractHolder(v, viewType);
    }
}
