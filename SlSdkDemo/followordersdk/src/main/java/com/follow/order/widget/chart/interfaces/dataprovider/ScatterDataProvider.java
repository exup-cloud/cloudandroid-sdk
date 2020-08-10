package com.follow.order.widget.chart.interfaces.dataprovider;

import com.follow.order.widget.chart.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
