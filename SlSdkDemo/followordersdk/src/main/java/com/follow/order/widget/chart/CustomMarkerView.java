package com.follow.order.widget.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.utils.DensityUtil;
import com.follow.order.widget.chart.charts.Chart;
import com.follow.order.widget.chart.components.MarkerView;
import com.follow.order.widget.chart.data.Entry;
import com.follow.order.widget.chart.highlight.Highlight;
import com.follow.order.widget.chart.utils.MPPointF;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;



public class CustomMarkerView extends MarkerView {

    private final TextView kLineDate;
    private final TextView kLinePrice;
    private Paint mPaint;
    private MPPointF mOffset2 = new MPPointF();
    private Paint mSPaint;
    private MYMarkerViewCallBack mMarkerViewCallBack;
    private Paint bitmapPaint;
    private Bitmap pointBitmap;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        kLineDate = (TextView) findViewById(R.id.kline_date);
        kLinePrice = (TextView) findViewById(R.id.kline_price);
        pointBitmap = BitmapFactory.decodeResource(getResources(), FollowOrderSDK.ins().getCustomAttrResId(context, R.attr.fo_chart_point_drawable));

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.fo_green));
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);

        mSPaint = new Paint();
        mSPaint.setStyle(Paint.Style.FILL);
        mSPaint.setColor(Color.parseColor("#aaffffff"));
        mSPaint.setStrokeWidth(2);
        mSPaint.setAntiAlias(true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (mMarkerViewCallBack == null) {
            float x = e.getX();
            float y = e.getY();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String format = simpleDateFormat.format(date);
            kLineDate.setText(format);
            kLinePrice.setText("价格：" + new BigDecimal(y).toString());
        } else {
            mMarkerViewCallBack.setText(kLineDate, kLinePrice, e);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {

        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    float x;
    float y;


    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF offset = getOffset();
        mOffset2.x = offset.x;
        mOffset2.y = offset.y;

        Chart chart = getChartView();

        float width = getWidth();
        float height = getHeight();

        if (posX + mOffset2.x < 0) {
            mOffset2.x = -posX;
        } else if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            mOffset2.x = chart.getWidth() - posX - width;
        }
        //如果小于图标
        if (posY + mOffset2.y < 0) {
            mOffset2.y = 0;
        } else if (chart != null && posY + height + mOffset2.y > chart.getHeight()) {
            //如果超过图片
            mOffset2.y = chart.getHeight() - posY - height;
        }

        return mOffset2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path;
        float slength = (float) (DensityUtil.dip2px(5) / Math.sin(Math.toRadians(60)));
        float left = 0;
        float top = 0;
        if (mOffset2.y != 0) {
            left = Math.abs(mOffset2.x);
            top = getHeight();
            path = new Path();
            path.moveTo(0, 0);
            path.lineTo(getWidth(), 0);
            path.lineTo(getWidth(), getHeight() - DensityUtil.dip2px(5));
            path.lineTo((float) (Math.abs(mOffset2.x) + slength / 2), getHeight() - DensityUtil.dip2px(5));
            path.lineTo(Math.abs(mOffset2.x), getHeight());
            path.lineTo((float) (Math.abs(mOffset2.x) - slength / 2), getHeight() - DensityUtil.dip2px(5));
            path.lineTo(0, getHeight() - DensityUtil.dip2px(5));
            path.lineTo(0, 0);
        } else {
            left = Math.abs(mOffset2.x);
            top = 0;
            path = new Path();
            path.moveTo(0, DensityUtil.dip2px(5));
            path.lineTo(Math.abs(mOffset2.x) - slength / 2, DensityUtil.dip2px(5));
            path.lineTo(Math.abs(mOffset2.x), 0);
            path.lineTo(Math.abs(mOffset2.x) + slength / 2, DensityUtil.dip2px(5));
            path.lineTo(getWidth(), DensityUtil.dip2px(5));
            path.lineTo(getWidth(), getHeight());
            path.lineTo(0, getHeight());
            path.lineTo(0, DensityUtil.dip2px(5));
        }
        canvas.drawBitmap(pointBitmap, left-pointBitmap.getWidth()/2, top-pointBitmap.getHeight()/2, bitmapPaint);
//        canvas.drawPath(path, mSPaint);
//        canvas.drawPath(path, mPaint);
        super.onDraw(canvas);

    }

    public void setMarkerViewCallBack(MYMarkerViewCallBack markerViewCallBack) {
        this.mMarkerViewCallBack = markerViewCallBack;
    }

    public interface MYMarkerViewCallBack {
        void setText(TextView dateTv, TextView priceTv, Entry entry);
    }
}
