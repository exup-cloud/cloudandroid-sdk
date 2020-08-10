package com.follow.order.widget.chart.interfaces.dataprovider;

import com.follow.order.widget.chart.components.YAxis.AxisDependency;
import com.follow.order.widget.chart.data.BarLineScatterCandleBubbleData;
import com.follow.order.widget.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
