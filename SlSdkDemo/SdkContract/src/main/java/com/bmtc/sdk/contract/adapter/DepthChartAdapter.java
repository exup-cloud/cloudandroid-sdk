package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.DepthData;
import com.contract.sdk.utils.MathHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;


/**
 * Created by zj on 2018/3/8.
 */

public class DepthChartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mFlag = 0;
    private List<DepthData> mSells = new ArrayList<>();
    private List<DepthData> mBuys = new ArrayList<>();

    private int mContractId;

    public static class DepthChartViewHolder extends RecyclerView.ViewHolder {

        LineChart chart_buy;
        LineChart chart_sell;

        TextView tvBidMin;
        TextView tvBidMax;
        TextView tvAskMin;
        TextView tvAskMax;

        public DepthChartViewHolder(View itemView, Context context) {
            super(itemView);

            chart_buy = itemView.findViewById(R.id.chart_buy);
            chart_buy.setDrawGridBackground(false);
            chart_buy.getDescription().setEnabled(false);
            chart_buy.setTouchEnabled(false);
            chart_buy.setDragEnabled(false);
            chart_buy.setScaleEnabled(false);
            chart_buy.setPinchZoom(false);
            chart_buy.setDrawMarkers(false);

            chart_buy.animateX(2500);
            Legend l = chart_buy.getLegend();
            l.setForm(Legend.LegendForm.LINE);

            chart_sell = itemView.findViewById(R.id.chart_sell);
            chart_sell.setDrawGridBackground(false);
            chart_sell.getDescription().setEnabled(false);
            chart_sell.setTouchEnabled(false);
            chart_sell.setDragEnabled(false);
            chart_sell.setScaleEnabled(false);
            chart_sell.setPinchZoom(false);
            chart_sell.setDrawMarkers(false);

            chart_sell.animateX(2500);
            Legend l2 = chart_sell.getLegend();
            l2.setForm(Legend.LegendForm.LINE);

            tvBidMin = itemView.findViewById(R.id.tv_bid_min);
            tvBidMax = itemView.findViewById(R.id.tv_bid_max);
            tvAskMin = itemView.findViewById(R.id.tv_ask_min);
            tvAskMax = itemView.findViewById(R.id.tv_ask_max);
        }
    }

    public DepthChartAdapter(Context context) {
        mContext = context;
    }

    public void setData(final List<DepthData> sells, final List<DepthData> buys, int contractId) {

        if (sells != null) {
            Collections.sort(sells, new Comparator<DepthData>() {
                @Override
                public int compare(DepthData o1, DepthData o2) {
                    if (MathHelper.round(o1.getPrice(), 6) > MathHelper.round(o2.getPrice(), 6)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            mSells.clear();
            mSells.addAll(sells);
        }

        if (buys != null) {
            Collections.sort(buys, new Comparator<DepthData>() {
                @Override
                public int compare(DepthData o1, DepthData o2) {
                    if (MathHelper.round(o1.getPrice(), 6) > MathHelper.round(o2.getPrice(), 6)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            mBuys.clear();
            mBuys.addAll(buys);
        }

        mContractId = contractId;
    }

    @Override
    public int getItemCount() {
        return 1;
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
        final DepthChartViewHolder itemViewHolder = (DepthChartViewHolder) holder;
        if (mContext == null || mContext.getResources() == null) {
            return;
        }

        setData(itemViewHolder.chart_buy, itemViewHolder.tvBidMin, itemViewHolder.tvBidMax, mContext.getResources().getColor(R.color.sl_colorGreen), true);
        setData(itemViewHolder.chart_sell, itemViewHolder.tvAskMin, itemViewHolder.tvAskMax, mContext.getResources().getColor(R.color.sl_colorRed), false);
    }

    private void setData(final LineChart chart, TextView tvMin, TextView tvMax, final int fill_color, final boolean left) {

        ArrayList<Entry> values;

        ArrayList<Entry> valuesBuy = new ArrayList<>();
        double maxBuyVol = 0.0;
        double maxBuyPrice = 0.0;
        double minBuyPrice = 0.0;
        for (int i = mBuys.size() - 1; i >= 0; i--) {
            double vol = MathHelper.round(mBuys.get(i).getVol(), 6);
            double price = MathHelper.round(mBuys.get(i).getPrice(), 6);

            if (price > maxBuyPrice) maxBuyPrice = price;
            if (price < minBuyPrice || minBuyPrice == 0.0) minBuyPrice = price;
            maxBuyVol += vol;

            valuesBuy.add(0, new Entry((float) price, (float) maxBuyVol));
        }
        while (valuesBuy.size() < 2) {
            valuesBuy.add(new Entry((float) maxBuyPrice, (float) maxBuyVol));
        }

        ArrayList<Entry> valuesSell = new ArrayList<>();
        double maxSellVol = 0.0;
        double maxSellPrice = 0.0;
        double minSellPrice = 0.0;
        for (int i = 0; i < mSells.size(); i++) {
            double vol = MathHelper.round(mSells.get(i).getVol(), 6);
            double price = MathHelper.round(mSells.get(i).getPrice(), 6);

            if (price > maxSellPrice) maxSellPrice = price;
            if (price < minSellPrice || minSellPrice == 0.0) minSellPrice = price;
            maxSellVol += vol;

            valuesSell.add(new Entry((float) price, (float) maxSellVol));
        }

        while (valuesSell.size() < 2) {
            valuesSell.add(new Entry((float) maxSellPrice, (float) maxSellVol));
        }

        final double maxVol = Math.max(maxBuyVol, maxSellVol);
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));

        if (left) {
            tvMin.setText(decimalFormat.format(minBuyPrice));
            tvMax.setText(decimalFormat.format(maxBuyPrice));
            values = valuesBuy;
        } else {
            tvMin.setText(decimalFormat.format(minSellPrice));
            tvMax.setText(decimalFormat.format(maxSellPrice));
            values = valuesSell;
        }

        final ArrayList<Entry> finalValues = values;
        final double finalMaxBuyPrice = maxBuyPrice;
        final double finalMaxBuyPrice1 = maxBuyPrice;
        final double finalMinBuyPrice = minBuyPrice;
        final double finalMaxSellPrice = maxSellPrice;
        final double finalMinSellPrice = minSellPrice;
        final double finalMinSellPrice1 = minSellPrice;

        LineDataSet set1;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)chart.getData().getDataSetByIndex(0);
            set1.setValues(finalValues);

            XAxis xAxis = chart.getXAxis();
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMaximum((float) maxVol);
            leftAxis.setAxisMinimum((float) 0);

            if (mContractId > 0) {
                Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractId);
                if (contract != null) {
                    leftAxis.setValueFormatter(new DefaultAxisValueFormatter(contract.getVol_index()));
                }
            }

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setAxisMaximum((float) maxVol);
            rightAxis.setAxisMinimum((float) 0);
            if (left) {
                xAxis.setAxisMaximum((float) (finalMaxBuyPrice + (finalMaxBuyPrice1 - finalMinBuyPrice) * 0.05));
                xAxis.setAxisMinimum((float) finalMinBuyPrice);
                chart.getAxisRight().setEnabled(false);

            } else {
                xAxis.setAxisMaximum((float) finalMaxSellPrice);
                xAxis.setAxisMinimum((float) (finalMinSellPrice - (finalMaxSellPrice - finalMinSellPrice1) * 0.05));
                chart.getAxisLeft().setEnabled(false);
            }

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(finalValues, "");
            set1.setValueFormatter(new StackedValueFormatter(false, "", 6));
            set1.setDrawIcons(false);

            set1.disableDashedLine();
            set1.setColor(fill_color);
            set1.setLineWidth(1f);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                set1.setFillColor(fill_color);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLabels(false);
            xAxis.setAxisLineWidth(0.5f);
            xAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.sl_depthChartLine));

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.setLabelCount(5, true);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            leftAxis.setYOffset(-5.0f);
            if (mContractId > 0) {
                Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractId);
                if (contract != null) {
                    leftAxis.setValueFormatter(new DefaultAxisValueFormatter(contract.getVol_index()));
                }
            }

            leftAxis.setAxisMaximum((float) maxVol);
            leftAxis.setAxisMinimum((float) 0);
            leftAxis.setDrawZeroLine(false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawLimitLinesBehindData(false);
            leftAxis.setAxisLineWidth(0.5f);
            leftAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.sl_depthChartLine));
            leftAxis.setTextColor(ContextCompat.getColor(mContext, R.color.sl_grayText));

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.removeAllLimitLines();
            rightAxis.setDrawLabels(false);

            rightAxis.setAxisMaximum((float) maxVol);
            rightAxis.setAxisMinimum((float) 0);
            rightAxis.setDrawZeroLine(false);
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawLimitLinesBehindData(false);
            rightAxis.setAxisLineWidth(0.5f);
            rightAxis.setAxisLineColor(ContextCompat.getColor(mContext, R.color.sl_depthChartLine));
            rightAxis.setTextColor(ContextCompat.getColor(mContext, R.color.sl_grayText));

            if (left) {
                xAxis.setAxisMaximum((float) (finalMaxBuyPrice + (finalMaxBuyPrice1 - finalMinBuyPrice) * 0.05));
                xAxis.setAxisMinimum((float) finalMinBuyPrice);

                chart.getAxisRight().setEnabled(false);

            } else {
                xAxis.setAxisMaximum((float) finalMaxSellPrice);
                xAxis.setAxisMinimum((float) (finalMinSellPrice - (finalMaxSellPrice - finalMinSellPrice1) * 0.05));

                chart.getAxisLeft().setEnabled(false);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_depth_chart, parent, false);
        return new DepthChartViewHolder(v, mContext);
    }
    public List<DepthData> getSells() {
        return mSells;
    }

    public List<DepthData> getBuys() {
        return mBuys;
    }
}
