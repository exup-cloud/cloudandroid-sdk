package com.yjkj.chainup.new_version.kline.view

import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.Log
import com.yjkj.chainup.R
import com.yjkj.chainup.kline.view.KLineChartView
import com.yjkj.chainup.new_version.kline.base.IChartViewDraw
import com.yjkj.chainup.new_version.kline.base.IValueFormatter
import com.yjkj.chainup.new_version.kline.bean.VolumeBean
import com.yjkj.chainup.new_version.kline.formatter.BigValueFormatter
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.DisplayUtil


/**
 * @Author: Bertking
 * @Date：2019/3/12-3:35 PM
 * @Description:
 */
class VolumeView(view: KLineChartView) : IChartViewDraw<VolumeBean>, IFallRiseColor {


    val TAG = VolumeView::class.java.simpleName

    private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val fallPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val risePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint4MA5 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint4MA10 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint4Vol = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pillarWidth = 0

    var isLine = false

    init {
        val context = view.context
        mLinePaint.color = ContextCompat.getColor(context, R.color.main_blue)
        pillarWidth = DisplayUtil.dip2px(6)
        setVolTextColor(ColorUtil.getColor(R.color.chart_vol))
    }


    override fun drawTranslated(lastPoint: VolumeBean?, curPoint: VolumeBean, lastX: Float, curX: Float, canvas: Canvas, view: BaseKLineChartView, position: Int) {

        Log.d("======drawTranslated==", "" + curPoint.volume)
        /**
         * draw 交易量的直方图
         */
        drawHistogram(canvas, curPoint, lastPoint, curX, view, position)

        /**
         * draw 交易量的 MA5，MA10
         */
        if (isLine) {

        } else {
            if (lastPoint!!.volume4MA5 != 0f) {
                view.drawVolLine(canvas, paint4MA5, lastX, lastPoint.volume4MA5, curX, curPoint.volume4MA5)
            }
            if (lastPoint.volume4MA10 != 0f) {
                view.drawVolLine(canvas, paint4MA10, lastX, lastPoint.volume4MA10, curX, curPoint.volume4MA10)
            }
        }

    }

    override fun drawText(canvas: Canvas, view: BaseKLineChartView, position: Int, x: Float, y: Float) {

        val point = view.getItem(position) as VolumeBean
        var text = "VOL:" + getValueFormatter().format(point.volume) + "     "
        canvas.drawText(text, x + DisplayUtil.dip2px(5f), y, paint4Vol)

        if (isLine) {

        } else {
            var textLen = x
            textLen += view.textPaint.measureText(text)
            text = "MA5:" + getValueFormatter().format(point.volume4MA5) + "     "
            canvas.drawText(text, textLen, y, paint4MA5)
            textLen += paint4MA5.measureText(text)
            text = "MA10:" + getValueFormatter().format(point.volume4MA10)
            canvas.drawText(text, textLen, y, paint4MA10)
        }


    }

    override fun getMaxValue(point: VolumeBean): Float {
        return maxOf(point.volume, point.volume4MA5, point.volume4MA10)
    }

    override fun getMinValue(point: VolumeBean): Float {
        return minOf(point.volume, point.volume4MA5, point.volume4MA10)
    }

    override fun getValueFormatter(): IValueFormatter {
        return BigValueFormatter()
    }

    override fun setTextSize(textSize: Float) {
        this.paint4MA5.textSize = textSize
        this.paint4MA10.textSize = textSize
        this.paint4Vol.textSize = textSize
    }

    override fun setLineWidth(width: Float) {
        this.paint4MA5.strokeWidth = width
        this.paint4MA10.strokeWidth = width
    }

    override fun setFallRiseColor(riseColor: Int, fallColor: Int) {
        fallPaint.color = fallColor
        risePaint.color = riseColor
    }


    /**
     * 设置 MA5 线的颜色
     * @param color
     */
    fun setVolTextColor(color: Int) {
        this.paint4Vol.color = color
    }

    /**
     * 设置 MA5 线的颜色
     * @param color
     */
    fun setMa5Color(color: Int) {
        this.paint4MA5.color = color
    }

    /**
     * 设置 MA10 线的颜色
     * @param color
     */
    fun setMa10Color(color: Int) {
        this.paint4MA10.color = color
    }


    /**
     * 画深度的直方图
     * @param canvas
     * @param curPoint
     * @param lastPoint
     * @param curX
     * @param view
     * @param position
     */
    private fun drawHistogram(
            canvas: Canvas, curPoint: VolumeBean, lastPoint: VolumeBean?, curX: Float,
            view: BaseKLineChartView, position: Int) {

        val r = (pillarWidth / 2).toFloat()
        val top = view.getVolY(curPoint.volume)
        val bottom = view.volRect.bottom


        Log.d("======drawHistogram=", "" + (curX - r) + " :::t " + top + " ::: " + (curX + r) + " ::: " + bottom)

        if (isLine) {
            if (curPoint.closePrice >= curPoint.openPrice) {//涨
                if (top == bottom.toFloat()) {
                    canvas.drawRect(curX - r, top - 1, curX + r, bottom.toFloat(), mLinePaint)
                } else {
                    canvas.drawRect(curX - r, top, curX + r, bottom.toFloat(), mLinePaint)
                }
            } else {
                if (top == bottom.toFloat()) {
                    canvas.drawRect(curX - r, top - 1, curX + r, bottom.toFloat(), mLinePaint)
                } else {
                    canvas.drawRect(curX - r, top, curX + r, bottom.toFloat(), mLinePaint)
                }
            }

        } else {
            if (curPoint.closePrice >= curPoint.openPrice) {//涨
                if (top == bottom.toFloat()) {
                    canvas.drawRect(curX - r, top - 1, curX + r, bottom.toFloat(), risePaint)
                } else {
                    canvas.drawRect(curX - r, top, curX + r, bottom.toFloat(), risePaint)
                }
            } else {
                if (top == bottom.toFloat()) {
                    canvas.drawRect(curX - r, top - 1, curX + r, bottom.toFloat(), fallPaint)
                } else {
                    canvas.drawRect(curX - r, top, curX + r, bottom.toFloat(), fallPaint)
                }
            }
        }

    }

}