package com.yjkj.chainup.new_version.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.MotionEvent
import com.yjkj.chainup.R

/**
 * @Author: Bertking
 * @Date：2019/3/6-2:39 PM
 * @Description: 自定义EditText
 */

class CustomizeEditText @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var textContent = ""

    /**
     *  仅仅为了兼容之前部分代码
     * TODO 优化
     */
    var isShowLine = false
        set(value) {
            field = value
            if (value) {
                setBackgroundResource(R.drawable.et_underline_selector)
            }
        }



    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textContent = text.toString()
        setClearIconVisible(hasFocus() && text?.isNotEmpty() == true)
    }

    var focusedListener = false
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        focusedListener = focused
        if (focusedListener && textContent.isNotEmpty()) {
            if (isShowLine) {
                setBackgroundResource(R.drawable.et_underline_selector)
            }
            setClearIconVisible(true)
        } else {
            if (isShowLine) {
                setBackgroundResource(R.drawable.et_underline_selector)
            }
            setClearIconVisible(false)
        }
    }

    /**
     * clear 事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val isClean = event.x > width - totalPaddingRight && event.x < width - paddingRight
            if (isClean) {
                setText("")
            }
        }
        return super.onTouchEvent(event)
    }


    private fun setClearIconVisible(visible: Boolean) {
        if (visible && focusedListener) {
            val clearImg = context.getDrawable(R.drawable.delete)
            setCompoundDrawablesWithIntrinsicBounds(null, null, clearImg, null)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }


}
