package com.yjkj.chainup.wedegit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.yjkj.chainup.R

class NarrowItemDecoration : RecyclerView.ItemDecoration {

    var mPaint: Paint = Paint()
    var dividerHeight: Int = 0

    constructor(context: Context) : super() {
        mPaint.color = ContextCompat.getColor(context, R.color.colorDivider)
        dividerHeight = context.resources.getDimensionPixelSize(R.dimen.line_height)
    }


    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect?.bottom = parent!!.context.resources.getDimensionPixelSize(R.dimen.line_height)
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        val childCount: Int = parent!!.childCount
        val left: Int = parent.paddingLeft + parent.context.resources.getDimensionPixelSize(R.dimen.view_margin_normal)
        val right: Int = parent.width - parent.paddingRight
        for (i in 0 until childCount) {
            val view: View = parent.getChildAt(i)
            val top = view.bottom
            val bottom = view.bottom + dividerHeight
            c?.drawRect(left.toFloat(), top.toFloat()
                    , right.toFloat(), bottom.toFloat(), mPaint)
        }
    }

}