package com.follow.order.widget.chart.interfaces.dataprovider;

import com.follow.order.widget.chart.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
