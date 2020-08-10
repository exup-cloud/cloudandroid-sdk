package com.yjkj.chainup.new_version.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.item_textview_edittext.view.*
import android.text.InputFilter


/**
 * @Author lianshangljl
 * @Date 2019/4/2-11:12 AM
 * @Email buptjinlong@163.com
 * @description  textview + edittext
 */
class TextViewAddEditTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var titleContent = ""
    var hintContent = ""

    var listener: OnTextListener? = null
    var pwdContent = ""
    private var textContentSize: Float = 14f
    var isPwdView = false

    interface OnTextListener {
        fun showText(text: String): String
    }

    init {
        attrs.let {
            var typeArray = context.obtainStyledAttributes(it, R.styleable.TextViewAddEditTextView, 0, 0)
            titleContent = typeArray.getString(R.styleable.TextViewAddEditTextView_title_content).toString()
            hintContent = typeArray.getString(R.styleable.TextViewAddEditTextView_hint_content).toString()
            isPwdView = typeArray.getBoolean(R.styleable.TextViewAddEditTextView_ispwdview, false)
            textContentSize = typeArray.getDimension(R.styleable.TextViewAddEditTextView_textContentSize4Edit, resources.getDimension(R.dimen.sp_14))
        }
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_textview_edittext, this, true)
        tv_title?.text = titleContent
        cet_view?.isFocusable = true
        cet_view?.isFocusableInTouchMode = true
        if (isPwdView) {
            cet_view?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        cet_view?.paint?.textSize = textContentSize
        cet_view?.hint = hintContent
        cet_view?.setOnFocusChangeListener { v, hasFocus ->
            cet_view_line.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
        cet_view?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (listener != null) {
                    listener?.showText(s.toString())
                }
                pwdContent = s.toString()
            }

        })
    }

    fun setFocusable() {

    }


    fun setTitle(title: String) {
        tv_title?.text = title
    }

    fun setEditText(content: String) {
        cet_view?.hint = content
    }

    fun setEdittextMaxLength(max: Int) {
        cet_view.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
    }

}
