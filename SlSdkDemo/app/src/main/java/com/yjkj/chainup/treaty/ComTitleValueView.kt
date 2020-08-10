package com.yjkj.chainup.treaty

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.layout_com_title_value.view.*
import org.jetbrains.anko.textColor

/**
 * @Author: Bertking
 * @Date：2019/1/10-6:46 PM
 * @Description:
 */

class ComTitleValueView @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var topTitle = ""
    var bottomValue = ""
    var topTitleColor = ContextCompat.getColor(context, R.color.new_text_color)
    var bottomValueColor = ContextCompat.getColor(context, R.color.main_font_color)
    var isShowTips = false
    var isRollback = false
    var onTipsListener: OnTipsListener? = null

    var topGravity = Gravity.CENTER
    var bottomGravity = Gravity.CENTER

    var liveData: MutableLiveData<KeyValueBean>


    interface OnTipsListener {
        fun onClick()
    }


    init {
        attrs?.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.ComTitleValueView, 0, 0)
            topTitle = typedArray.getString(R.styleable.ComTitleValueView_topTitle).toString()
            bottomValue = typedArray.getString(R.styleable.ComTitleValueView_bottomValue).toString()
            topTitleColor = typedArray.getColor(R.styleable.ComTitleValueView_topTitleColor, ContextCompat.getColor(context, R.color.new_text_color))
            bottomValueColor = typedArray.getColor(R.styleable.ComTitleValueView_bottomValueColor, ContextCompat.getColor(context, R.color.main_font_color))
            isShowTips = typedArray.getBoolean(R.styleable.ComTitleValueView_isShowTips, false)
            isRollback = typedArray.getBoolean(R.styleable.ComTitleValueView_isRollback, false)
            bottomGravity = typedArray.getInt(R.styleable.ComTitleValueView_bottomGravity, Gravity.CENTER)
            topGravity = typedArray.getInt(R.styleable.ComTitleValueView_topGravity, Gravity.CENTER)
            typedArray.recycle()
            initView()
        }
        liveData = MutableLiveData()
    }

    fun initView() {
        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.layout_com_title_value, this, true)

        ll_bottom.gravity =  Gravity.CENTER_VERTICAL or bottomGravity
        ll_top.gravity =  Gravity.CENTER_VERTICAL or topGravity

        if (isRollback) {
            tv_title?.text = bottomValue
            tv_value?.text = topTitle

            tv_title?.textColor = bottomValueColor
            tv_title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            tv_value?.textColor = topTitleColor
            tv_value?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

            if (isShowTips) {
                iv_top_tips.visibility = View.GONE
                iv_bottom_tips.visibility = View.VISIBLE
                iv_bottom_tips.setOnClickListener { v ->
                    onTipsListener?.onClick()
                }
            } else {
                iv_top_tips.visibility = View.GONE
                iv_bottom_tips.visibility = View.GONE
            }

        } else {
            tv_title?.text = topTitle
            tv_value?.text = bottomValue
            tv_title?.textColor = topTitleColor
            tv_title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            tv_value?.textColor = bottomValueColor
            tv_value?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)

            if (isShowTips) {
                iv_top_tips.visibility = View.VISIBLE
                iv_bottom_tips.visibility = View.GONE
                iv_top_tips.setOnClickListener { v ->
                    onTipsListener?.onClick()
                }
            } else {
                iv_top_tips.visibility = View.GONE
                iv_bottom_tips.visibility = View.GONE
            }
        }
    }


    fun setBottom(bottom: String) {
        this.bottomValue = bottom
        if (isRollback) {
            tv_title?.text = bottomValue
        } else {
            tv_value?.text = bottomValue
        }

    }

    fun setBottomColor(color: Int) {
        this.bottomValueColor = color
        if (isRollback) {
            tv_title?.textColor = color
        } else {
            tv_value?.textColor = color
        }
    }


    fun setTopText(top: String) {
        tv_title?.text = top
    }

    fun setTopParams(){
        val lp = tv_title.layoutParams as LinearLayout.LayoutParams
        lp.gravity=Gravity.LEFT
        tv_title.layoutParams = lp
    }


}


data class KeyValueBean(var topValue: String, var bottomValue: String)
