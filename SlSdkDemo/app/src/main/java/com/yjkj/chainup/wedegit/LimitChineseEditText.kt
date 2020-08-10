package com.yjkj.chainup.wedegit

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText

/**
 * 限制输入中文的输入框
 */
class LimitChineseEditText :EditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    /**
     * 链接输入法
     */
    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection =
            LimitInputConnection(super.onCreateInputConnection(outAttrs),false)


    class LimitInputConnection(target: InputConnection?, mutable: Boolean) : InputConnectionWrapper(target, mutable) {

        /**
         * 限制文字
         */
        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            if (text.toString().matches(Regex("[\u4e00-\u9fa5]+"))) {
                return false
            }
            return super.commitText(text, newCursorPosition)
        }
    }



}