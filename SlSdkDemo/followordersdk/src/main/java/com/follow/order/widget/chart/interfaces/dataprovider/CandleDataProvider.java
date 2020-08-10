package com.follow.order.widget.chart.interfaces.dataprovider;

import com.follow.order.widget.chart.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
