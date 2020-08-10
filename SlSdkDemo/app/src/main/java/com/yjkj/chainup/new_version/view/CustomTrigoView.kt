package com.yjkj.chainup.new_version.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.yjkj.chainup.R
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.SizeUtils

/**
 * @Author lianshangljl
 * @Date 2019/3/18-10:42 AM
 * @Email buptjinlong@163.com
 * @description  自定义 右上角蓝色 三角形
 */
class CustomTrigoView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    var bgColor = ColorUtil.getColor(R.color.main_blue)
    var bgWidth = SizeUtils.dp2px(30.5f).toFloat()
    var bgHeight = SizeUtils.dp2px(31f).toFloat()
    /**
     * 是否需要绘制三角
     */
    var isNeedDraw = true
        set(value) {
            field = value
            invalidate()
        }


    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomTrigoView, 0, 0)
            bgColor = typedArray.getColor(R.styleable.CustomTrigoView_bgTrigoColor, ColorUtil.getColor(R.color.main_blue))
            bgWidth = typedArray.getDimension(R.styleable.CustomTrigoView_bgWidth, SizeUtils.dp2px(30.5f).toFloat())
            bgHeight = typedArray.getDimension(R.styleable.CustomTrigoView_bgHeight, SizeUtils.dp2px(31f).toFloat())
            typedArray.recycle()
        }
    }

    /**
     * 设置点击按钮
     */
    fun setBgColor(status: Boolean) {
        bgColor = if (status) {
            ColorUtil.getColor(R.color.main_blue)
        } else {
            ColorUtil.getColor(R.color.tabbar_icon_color)
        }
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var p = Paint()
        p.color = bgColor
        var path = Path()
        if (isNeedDraw) {
            path.moveTo(SizeUtils.dp2px(1f).toFloat(), SizeUtils.dp2px(1f).toFloat())
            path.lineTo(bgWidth, SizeUtils.dp2px(1f).toFloat())
            path.lineTo(bgWidth, bgHeight)
            path.close()
            canvas?.drawPath(path, p)
        } else {
            path.reset()
        }

    }

}