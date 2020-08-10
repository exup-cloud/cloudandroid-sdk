package com.follow.order.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.TabEntity;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.utils.CommonUtils;
import com.follow.order.utils.DensityUtil;
import com.follow.order.utils.StringUtil;
import com.follow.order.widget.chart.CustomLineChart;
import com.follow.order.widget.chart.CustomMarkerView;
import com.follow.order.widget.chart.components.XAxis;
import com.follow.order.widget.chart.components.YAxis;
import com.follow.order.widget.chart.data.Entry;
import com.follow.order.widget.chart.data.LineData;
import com.follow.order.widget.chart.data.LineDataSet;
import com.follow.order.widget.chart.formatter.IFillFormatter;
import com.follow.order.widget.chart.formatter.ValueFormatter;
import com.follow.order.widget.chart.interfaces.dataprovider.LineDataProvider;
import com.follow.order.widget.chart.interfaces.datasets.ILineDataSet;
import com.follow.order.widget.chart.utils.Utils;
import com.follow.order.widget.tab.CommonTabLayout;
import com.follow.order.widget.tab.listener.CustomTabEntity;
import com.follow.order.widget.tab.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;


public class PersonalProfitChartView extends LinearLayout {
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_ORDER = 2;
    private CommonTabLayout tab_trend;
    private CustomLineChart lineChart;
    private List<UserFinanceProfileBean.ProfitHistoryBean> historyList;
    private boolean hasMax;
    private boolean hasMin;
    private int fromType;

    public PersonalProfitChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tab_trend = findViewById(R.id.tab_trend);
        lineChart = findViewById(R.id.line_chart);

        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        initLineChart();

        tab_trend.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public boolean onTabSelectBefore(int position) {
                return false;
            }

            @Override
            public void onTabSelect(int position) {
                if (lineChart == null || historyList == null || historyList.get(position) == null) {
                    return;
                }
                lineChart.clear();
                setEmptyView();
                setRecordLineChartData(historyList.get(position));
            }

            @Override
            public void onTabReselect(int position) {
                if (lineChart == null) {
                    return;
                }
                lineChart.animateXY(1000, 2000);
            }
        });
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
        if (fromType == TYPE_PERSONAL) {//个人实盘
            lineChart.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_menu_bg_color));
        } else if (fromType == TYPE_ORDER) {//我的跟单
            lineChart.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_status_bg_color));
        }

    }

    public void setChartData(List<UserFinanceProfileBean.ProfitHistoryBean> chartData) {
        if (chartData == null) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        historyList = chartData;
        if (historyList != null && historyList.size() > 0) {
            lineChart.setVisibility(View.VISIBLE);
            tab_trend.setVisibility(View.VISIBLE);
            ArrayList<CustomTabEntity> tabEntitys = new ArrayList<>();
            for (int i = 0; i < historyList.size(); i++) {
                UserFinanceProfileBean.ProfitHistoryBean tabEntry = historyList.get(i);
                TabEntity tabEntity = new TabEntity(tabEntry.getTitle(), 0, 0);
                tabEntitys.add(tabEntity);
            }
            tab_trend.setTabData(tabEntitys);
            tab_trend.setCurrentTab(0);
            if (lineChart != null && lineChart.getData() != null) {
                lineChart.getData().clearValues();
            }
            setRecordLineChartData(historyList.get(0));
        } else {
//            tab_trend.setVisibility(View.GONE);
//            lineChart.setVisibility(View.GONE);
            setVisibility(View.GONE);
        }
    }

    private void initLineChart() {
        lineChart.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_menu_bg_color));
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setExtraOffsets(0, 0, 0, 10);
        lineChart.setNoDataTextColor(ContextCompat.getColor(getContext(), R.color.fo_desc_color));
        lineChart.setNoDataText(getContext().getString(R.string.fo_empty_data_text));
        lineChart.setBorderWidth(1);
        lineChart.setBorderColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));

        CustomMarkerView mm = new CustomMarkerView(getContext(), R.layout.layout_custom_marker_view);
        mm.setChartView(lineChart);
        mm.setMarkerViewCallBack(new CustomMarkerView.MYMarkerViewCallBack() {
            @Override
            public void setText(TextView dateTv, TextView priceTv, Entry entry) {
                try {
                    if (entry != null) {
                        UserFinanceProfileBean.ChartRecord data = (UserFinanceProfileBean.ChartRecord) entry.getData();
                        dateTv.setText(getContext().getString(R.string.personal_label_9) + data.getTime());
                        if (tab_trend != null && historyList != null && historyList.size() > 0) {
                            String result = "";
                            if (TextUtils.equals("%", historyList.get(tab_trend.getCurrentTab()).getUnit())) {
                                result = data.getValue() + historyList.get(tab_trend.getCurrentTab()).getUnit();
                            } else {
                                result = historyList.get(tab_trend.getCurrentTab()).getUnit() + data.getValue();
                            }
                            priceTv.setText(historyList.get(tab_trend.getCurrentTab()).getTitle() + "：" + result);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        lineChart.setMarker(mm);

        XAxis topAxis = lineChart.getXAxis();
        topAxis.setPosition(XAxis.XAxisPosition.TOP);
        topAxis.setAxisLineWidth(0.5f);
        topAxis.setAxisLineColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        topAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelCount(5, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setYOffset(DensityUtil.dip2px(3));
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setDrawAxisLine(false);
        xAxis.setGridColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        xAxis.setAxisLineColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        xAxis.setTextColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_text_color));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int position = (int) value;
                String format = null;
                try {
                    if (lineChart != null && lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
                        LineDataSet set = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                        List<Entry> values = set.getValues();
                        if (position >= values.size()) {
                            return value + "";
                        }
                        Entry entry = values.get(position);
                        UserFinanceProfileBean.ChartRecord data = (UserFinanceProfileBean.ChartRecord) entry.getData();
                        format = data.getTime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(format)) {
                    format = position + "";
                }
                return format;
            }
        });

        YAxis yRightAxis = lineChart.getAxisRight();
        yRightAxis.setAxisLineWidth(0.5f);
        yRightAxis.setXOffset(DensityUtil.dip2px(4));
        yRightAxis.setDrawGridLines(false);
        yRightAxis.setGridColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        yRightAxis.setAxisLineColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        yRightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setLabelCount(5);
        yAxis.setAxisLineWidth(0.5f);
        yAxis.setGridLineWidth(0.5f);
        yAxis.setXOffset(DensityUtil.dip2px(3));
        yAxis.setDrawAxisLine(false);
        yAxis.setGridColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        yAxis.setAxisLineColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_line_color));
        yAxis.setTextColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_text_color));
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int position = (int) value;
                if (historyList != null && tab_trend != null) {
                    UserFinanceProfileBean.ProfitHistoryBean historyBean = historyList.get(tab_trend.getCurrentTab());
                    if (TextUtils.equals("%", historyBean.getUnit())) {
                        return StringUtil.formatNum(value) + historyBean.getUnit();
                    } else {
                        return historyBean.getUnit() + position;
                    }
                }
                return position + "";
                //                if (tab_trend.getCurrentTab() == 0) {
//                    return value + "%";
//                } else {
//                    return position + "";
//                }
//                String format = null;
//                try {
//                    if (lineChart != null && lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
//                        LineDataSet set = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
//                        List<Entry> values = set.getValues();
//                        if (position >= values.size()) {
//                            return value + "";
//                        }
//                        Entry entry = values.get(position);
//                        format = entry.getY() + "";
////                        UserFinanceProfileBean.ProfitHistoryBean data = (UserFinanceProfileBean.ProfitHistoryBean) entry.getData();
////                        format = data.getProfit_ratio();
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (TextUtils.isEmpty(format)) {
//                    format = position + "";
//                }
//                return format;
            }
        });

        CommonUtils.setMarkerAutoDismiss(lineChart);
        lineChart.getLegend().setEnabled(false);
    }

    public void unBindChart() {
        CommonUtils.unBindChart(lineChart);
    }

    private void setEmptyView() {
        try {
            lineChart.setData(null);
            lineChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    float valueMin = 0;
    float valueMax = 0;

    public void setRecordLineChartData(final UserFinanceProfileBean.ProfitHistoryBean historyBean) {
        if (historyBean == null) {
            return;
        }
        List<UserFinanceProfileBean.ChartRecord> recordList = historyBean.getData();
        if (recordList == null || recordList.isEmpty()) {
            setEmptyView();
            return;
        }
        if (recordList.size() >= 100) {
            lineChart.setMaxVisibleValueCount(recordList.size() + 1);
        }
        valueMax = TextUtils.isEmpty(historyBean.getMax()) ? 0 : Float.valueOf(historyBean.getMax());
        valueMin = TextUtils.isEmpty(historyBean.getMin()) ? 0 : Float.valueOf(historyBean.getMin());

        ArrayList<Entry> values = new ArrayList<>();
        int size = recordList.size();
        boolean hasMaxPos = false;
        boolean hasMinPos = false;
        for (int i = 0; i < size; i++) {
            UserFinanceProfileBean.ChartRecord bean = recordList.get(i);
            if (bean != null) {
                if (TextUtils.isEmpty(bean.getValue())) {
                    bean.setValue("0");
                }
                float value = Float.parseFloat(bean.getValue());
                if ((value == valueMin && !hasMinPos) || (value == valueMax && !hasMaxPos)) {
                    if (value == valueMax) {
                        hasMaxPos = true;
                        bean.setMax(true);
                    }
                    if (value == valueMin) {
                        hasMinPos = true;
                        bean.setMin(true);
                    }
                    values.add(new Entry(i, value, FollowOrderSDK.ins().getCustomAttrDrawable(getContext(), R.attr.fo_chart_point_drawable), bean));
                } else {
                    values.add(new Entry(i, value, bean));
                }

            }
        }
        lineChart.setViewPortOffsets(getTextWidth(valueMax) + DensityUtil.dip2px(15), DensityUtil.dip2px(20), DensityUtil.dip2px(15), DensityUtil.dip2px(20));
        LineDataSet set1;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, null);
            set1.setDrawIcons(true);
            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(ContextCompat.getColor(getContext(), R.color.fo_blue));
            set1.setCircleColor(ContextCompat.getColor(getContext(), R.color.fo_transparent));

//            set1.setAxisDependency(YAxis.AxisDependency.LEFT);//设置线数据依赖于左侧y轴

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(8f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(10f);
            set1.setValueTextColor(FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_chart_point_text_color));

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setHighLightColor(ContextCompat.getColor(getContext(), R.color.fo_transparent));
//            set1.setDrawHorizontalHighlightIndicator(false);
//            set1.setDrawVerticalHighlightIndicator(false);
            set1.setValueFormatter(new ValueFormatter() {

                @Override
                public String getPointLabel(Entry entry) {
                    UserFinanceProfileBean.ChartRecord record = (UserFinanceProfileBean.ChartRecord) entry.getData();
                    if (record.isMax() || record.isMin()) {
                        if (TextUtils.equals("%", historyBean.getUnit())) {
                            return record.getValue() + historyBean.getUnit();
                        } else {
                            return historyBean.getUnit() + record.getValue();
                        }
                    }
                    return "";
                }
            });
            set1.setDrawValues(true);
//            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    if (valueMin < 0 && valueMax > 0) {
                        return 0;
                    } else if (valueMin < 0 && valueMax < 0) {
                        return 0;
                    }
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = FollowOrderSDK.ins().getCustomAttrDrawable(getContext(), R.attr.fo_chart_fade_drawable);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(ContextCompat.getColor(getContext(), R.color.fo_chart_bg_color));
            }


            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            lineChart.setData(data);
        }
        if (valueMin <= 0) {
            lineChart.getAxisLeft().setDrawZeroLine(true);
        } else {
            lineChart.getAxisLeft().setDrawZeroLine(false);
        }

        float yOffset = StringUtil.getChartYOffset(valueMax, valueMin) / 6;
        lineChart.getAxisLeft().setAxisMinimum(StringUtil.getMin(yOffset,valueMin).floatValue());
        lineChart.getAxisLeft().setAxisMaximum(StringUtil.getMax(yOffset,valueMin,valueMax).floatValue());
//        lineChart.getAxisLeft().setAxisMinimum(valueMin - yOffset);
//        lineChart.getAxisLeft().setAxisMaximum(valueMax + yOffset);
//        lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                int position = (int) value;
//                if (tab_trend.getCurrentTab() == 0) {
//                    return position + "%";
//                } else {
//                    return position + "";
//                }
//            }
//        });
        lineChart.getData().notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.fitScreen();
//        lineChart.invalidate();
        lineChart.animateXY(1000, 2000);
    }

    private float getTextWidth(float value) {
        TextPaint paint = new TextPaint();
        float scaledDensity = FollowOrderSDK.ins().getApplication().getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(scaledDensity * 10);
        float width = paint.measureText(valueMax + "%");
        return width;
    }
}
