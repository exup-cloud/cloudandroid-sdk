package com.follow.order.widget.chart.interfaces.dataprovider;

import com.follow.order.widget.chart.components.YAxis;
import com.follow.order.widget.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
