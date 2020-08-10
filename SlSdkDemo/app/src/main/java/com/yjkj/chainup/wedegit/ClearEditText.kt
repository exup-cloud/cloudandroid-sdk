package com.yjkj.chainup.wedegit

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import com.yjkj.chainup.R

//带清除功能的输入框
class ClearEditText : EditText {

    private lateinit var clearImg: Drawable
    var searchImg: Drawable? = null


    constructor(context: Context?) : super(context) {
        init()
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }


    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        setClearIconVisible(hasFocus() && text!!.length > 0)
    }


    private fun init() {
        clearImg = context.getDrawable(R.drawable.delete)!!
        searchImg = context.getDrawable(R.drawable.search)!!
    }

    var searchBoolean: Boolean = false

    fun setSearch() {
        setCompoundDrawablesWithIntrinsicBounds(searchImg, null, null, null)
        searchBoolean = true
    }

    //给图片设置监听事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
//            val eventX = event.rawX
//            val eventY = event.rawY
//            val rect = Rect()
//            //获取可见范围
//            getGlobalVisibleRect(rect)
//            rect.left = rect.right - 100 - paddingEnd
//            rect.right = rect.right - paddingEnd
//            if (rect.contains(eventX.toInt(), eventY.toInt())) {
//                setText("")
//            }
            val isClean = event.x > width - totalPaddingRight && event.x < width - paddingRight
            if (isClean) {
                setText("")
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        setClearIconVisible(focused && length() > 0)
    }


    fun setClearIconVisible(visible: Boolean) {
        if (visible) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, clearImg, null)
        } else {
            if (searchBoolean) {
                setCompoundDrawablesWithIntrinsicBounds(searchImg, null, null, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }


}




