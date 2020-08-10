package com.follow.order.widget.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;

import com.follow.order.FollowOrderSDK;
import com.follow.order.utils.DensityUtil;
import com.follow.order.widget.chart.animation.ChartAnimator;
import com.follow.order.widget.chart.interfaces.dataprovider.LineDataProvider;
import com.follow.order.widget.chart.renderer.LineChartRenderer;
import com.follow.order.widget.chart.utils.ViewPortHandler;


public class CustomLineChartRenderer extends LineChartRenderer {
    Paint bgPaint;
    int padding;
    float radius;

    public CustomLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#80006CFF"));
        padding = DensityUtil.dip2px(5);
        radius = DensityUtil.dip2px(2);
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
//        super.drawValue(c, valueText, x, y, color);
        color = Color.parseColor("#D7DFEE");
        String text = valueText;
        if (!TextUtils.isEmpty(text)) {
            float[] textSize = getTextSize(text);
//            x = x - textSize[0] / 2;
            RectF rectF = new RectF();
            rectF.left = x - padding - textSize[0] / 2;
            rectF.top = y - padding - textSize[1] - textSize[2] / 2;
            rectF.right = x + padding + textSize[0] / 2;
            rectF.bottom = y - textSize[2] / 2;
            c.drawRoundRect(rectF, radius, radius, bgPaint);
        }
//        super.drawValue(c, formatter, value, entry, dataSetIndex, x, y, color);
        mValuePaint.setColor(color);
        c.drawText(text, x, y, mValuePaint);
    }

//    @Override
//    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
//        color = Color.parseColor("#ffffff");
//        String text = formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler);
//        if (!TextUtils.isEmpty(text)) {
//            float[] textSize = getTextSize(text);
////            x = x - textSize[0] / 2;
//            RectF rectF = new RectF();
//            rectF.left = x - padding - textSize[0] / 2;
//            rectF.top = y - padding - textSize[1] - textSize[2] / 2;
//            rectF.right = x + padding + textSize[0] / 2;
//            rectF.bottom = y - textSize[2]/2;
//            c.drawRoundRect(rectF, radius, radius, bgPaint);
//        }
////        super.drawValue(c, formatter, value, entry, dataSetIndex, x, y, color);
//        mValuePaint.setColor(color);
//        c.drawText(text, x, y, mValuePaint);
//    }

    private float[] getTextSize(String text) {
        float[] textSize = new float[3];
        TextPaint paint = new TextPaint();
        float scaledDensity = FollowOrderSDK.ins().getApplication().getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(scaledDensity * 10);
        float width = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float height = fontMetrics.descent - fontMetrics.ascent;
        textSize[0] = width;
        textSize[1] = height;
        textSize[2] = fontMetrics.ascent;
        return textSize;
    }
}
