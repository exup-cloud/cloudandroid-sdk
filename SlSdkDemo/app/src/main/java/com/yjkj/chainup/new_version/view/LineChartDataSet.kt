package com.yjkj.chainup.new_version.view

import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.yjkj.chainup.R
import com.yjkj.chainup.app.ChainUpApp

/**
 * @Author lianshangljl
 * @Date 2019-08-12-11:26
 * @Email buptjinlong@163.com
 * @description
 */
class LineChartDataSet(values: List<Entry>, label: String? = null) : LineDataSet(values, label) {

    init {
        mode = Mode.CUBIC_BEZIER
        setDrawCircles(false)
        setDrawValues(false)
        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(ChainUpApp.appContext, R.drawable.shape_line_chart_backgroud)
        color = ContextCompat.getColor(ChainUpApp.appContext, R.color.main_blue)
    }
}