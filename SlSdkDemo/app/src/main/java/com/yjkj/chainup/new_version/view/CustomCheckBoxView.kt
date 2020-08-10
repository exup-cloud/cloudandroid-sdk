package com.yjkj.chainup.new_version.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.SizeUtils
import kotlinx.android.synthetic.main.view_checkbox.view.*
import org.jetbrains.anko.backgroundResource

/**
 * @Author lianshangljl
 * @Date 2019/3/9-2:49 PM
 * @Email buptjinlong@163.com
 * @description
 */
class CustomCheckBoxView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val TAG = CustomCheckBoxView::class.java.simpleName

    var firstContent = ""
    var secondContent = ""
    var thirdContent = ""
    /**
     * 是否 只显示一个 TextView
     */
    var isOnlyShowCenter = false
    /**
     * 不选时是否显示右上角
     */
    var isShowRightTop = true
    /**
     * 是否显示边框
     */
    var isShowClick = true
    /**
     * 只显示一个居中 TextView的Content
     */
    var middleContent = ""
    /**
     * 只显示一个 TextView的颜色
     */
    var middleColor = ColorUtil.getColor(R.color.normal_text_color)
    /**
     * 只显示一个 TextView的字号
     */
    var middleSize = 0f

    var imageViewWith = 0f

    /**
     * 是否选中
     */
    var isChecked = true
        set(value) {
            field = value
            setMainLayoutBg(value)
            setBgForCut(value)
        }
    /**
     * 禁止TouchEvent处理点击事件
     */
    var forbidTouchDeal = false

    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.NewVersionCheckBox)
            firstContent = typedArray.getString(R.styleable.NewVersionCheckBox_firstContent).toString()
            secondContent = typedArray.getString(R.styleable.NewVersionCheckBox_secondContent).toString()
            thirdContent = typedArray.getString(R.styleable.NewVersionCheckBox_thirdContent).toString()
            middleContent = typedArray.getString(R.styleable.NewVersionCheckBox_middleContent).toString()
            isOnlyShowCenter = typedArray.getBoolean(R.styleable.NewVersionCheckBox_isOnlyShowCenter, false)
            isShowRightTop = typedArray.getBoolean(R.styleable.NewVersionCheckBox_isShowRightTop, true)
            isShowClick = typedArray.getBoolean(R.styleable.NewVersionCheckBox_isShowClick, true)
            middleColor = typedArray.getColor(R.styleable.NewVersionCheckBox_middleColor, ContextCompat.getColor(context,R.color.text_color))
            middleSize = typedArray.getDimensionPixelSize(R.styleable.NewVersionCheckBox_middleSize, SizeUtils.dp2px(14f)).toFloat()
            imageViewWith = typedArray.getDimensionPixelSize(R.styleable.NewVersionCheckBox_imageViewWith, 0).toFloat()
            typedArray.recycle()
        }
        initView(context)
    }


    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_checkbox, this, true)
        if (isOnlyShowCenter) {
            tv_parent_content.text = middleContent
            tv_parent_content.setTextColor(middleColor)
            tv_parent_content.textSize = middleSize
            setViewVisible(false)
        } else {
            setViewVisible(true)
            tv_first_content.text = firstContent
            tv_second_content.text = secondContent
            tv_third_content.text = thirdContent
            ll_layout.backgroundResource = R.drawable.bg_add_likes
        }


        if (imageViewWith != 0f) {
            var margin = MarginLayoutParams(cut_view.layoutParams)
            var layoutParams = RelativeLayout.LayoutParams(margin)
            layoutParams.width = imageViewWith.toInt()
            layoutParams.height = imageViewWith.toInt()
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            cut_view.layoutParams = layoutParams
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN && !forbidTouchDeal) {
            isChecked = !isChecked
            setBgForCut(isChecked)
            setMainLayoutBg(isChecked)
        }
        return super.onTouchEvent(event)
    }

    fun setBgForCut(status: Boolean) {
        if (status) {
            cut_view.setImageResource(R.drawable.quotes_selected)
            cut_view.visibility = View.VISIBLE
        } else {
            if (isShowRightTop) {
                cut_view.setImageResource(R.drawable.quotes_unchecked)
            } else {
                cut_view.visibility = View.GONE
            }

        }
    }

    fun setMainLayoutBg(status: Boolean) {
        if (isShowClick){
            ll_layout.setBackgroundResource(if (status) R.drawable.bg_new_select_style else R.drawable.bg_new_unselect_style)
        }else{
            ll_layout.setBackgroundResource(if (status) R.drawable.bg_add_likes else R.drawable.bg_new_unselect_style)
        }
    }


    fun setViewVisible(status: Boolean) {
        tv_first_content.visibility = if (status) View.VISIBLE else View.GONE
        tv_second_content.visibility = if (status) View.VISIBLE else View.GONE
        tv_third_content.visibility = if (status) View.VISIBLE else View.GONE
        tv_parent_content.visibility = if (status) View.GONE else View.VISIBLE
    }

    fun setFirst(firstContent: String) {
        tv_first_content.text = firstContent
    }

    fun setSecond(secondContent: String) {
        tv_second_content.text = secondContent
    }

    fun setThird(thirdContent: String) {
        tv_third_content.text = thirdContent
    }

    fun setMiddle(middleContent: String) {
        tv_parent_content.text = middleContent
    }

    fun setCenterSize(textSize: Float) {
        tv_parent_content.textSize = textSize
    }

    fun setCenterColor(color: Int) {
        tv_parent_content.setTextColor(color)
    }

    fun setThirdColor(color: Int) {
        tv_third_content.setTextColor(color)
    }

    fun setIsNeedDraw(isNeed: Boolean) {
        setBgForCut(isNeed)
    }

}