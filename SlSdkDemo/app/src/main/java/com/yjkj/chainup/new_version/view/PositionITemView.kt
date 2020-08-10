package com.yjkj.chainup.new_version.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.layout_position_item.view.*
import org.jetbrains.anko.textColor

/**
 * @Author: Bertking
 * @Date：2019/4/23-7:27 PM
 * @Description:
 */
class PositionITemView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = PositionITemView::class.java.simpleName

    var title = ""
        set(value) {
            field = value
            tv_title?.text = title
        }
    var value = ""
        set(value) {
            field = value
            tv_value?.text = value
        }

    var headTitleColor = 0

    var tailValueColor = 0
        set(value) {
            field = value
            tv_value?.textColor = value
        }

    init {
        attrs?.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.PositionITemView, 0, 0)
            Log.d(TAG, "${typedArray == null}")
            title = typedArray.getString(R.styleable.PositionITemView_headTitle) ?: ""
            value = typedArray.getString(R.styleable.PositionITemView_tailValue) ?: ""
            headTitleColor = typedArray.getColor(R.styleable.PositionITemView_headTitleColor, ContextCompat.getColor(context, R.color.normal_text_color))
            tailValueColor = typedArray.getColor(R.styleable.PositionITemView_tailValueColor, ContextCompat.getColor(context, R.color.text_color))
            typedArray.recycle()
            initView()
        }
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_position_item, this, true)
        tv_title?.text = title
        tv_value?.text = value
        tv_title?.textColor = headTitleColor
        tv_value?.textColor = tailValueColor
    }

    fun setHeadTitle(content:String){
        tv_title?.text = content
    }

}