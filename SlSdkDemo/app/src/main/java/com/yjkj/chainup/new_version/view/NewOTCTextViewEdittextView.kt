package com.yjkj.chainup.new_version.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.DecimalDigitsInputFilter
import kotlinx.android.synthetic.main.item_new_otc_edittext_textview.view.*

/**
 * @Author lianshangljl
 * @Date 2019-10-21-19:37
 * @Email buptjinlong@163.com
 * @description
 */
class NewOTCTextViewEdittextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var topTextContent = ""
    var bottomRightTextContent = ""
    var hintContent = ""
    var bottomMaxLength = 10
    var inputer: Boolean = false


    interface OTCEdittextChangeListener {
        fun returnEdittextContent(content: String)
    }

    var listener: OTCEdittextChangeListener? = null


    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NewOTCTextViewEdittextView)
            topTextContent = typedArray.getString(R.styleable.NewOTCTextViewEdittextView_topEditTextviewcontent).toString()
            bottomRightTextContent = typedArray.getString(R.styleable.NewOTCTextViewEdittextView_bottomRightEditTextviewContent).toString()
            hintContent = typedArray.getString(R.styleable.NewOTCTextViewEdittextView_bottomhintContent).toString()
            bottomMaxLength = typedArray.getInteger(R.styleable.NewOTCTextViewEdittextView_bottomMaxLength, 10)
            inputer = typedArray.getBoolean(R.styleable.NewOTCTextViewEdittextView_inputer, false)
            typedArray.recycle()
        }
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_new_otc_edittext_textview, this, true)
        tv_top_title?.text = topTextContent
        tv_right_content?.text = bottomRightTextContent
        cet_content?.hint = hintContent


        cet_content?.isFocusable = true
        cet_content?.isFocusableInTouchMode = true
        cet_content?.setOnFocusChangeListener { v, hasFocus ->
            v_line?.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
        if (inputer) {
            cet_content?.inputType = InputType.TYPE_CLASS_TEXT
        }

        cet_content?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var value = s.toString()
                if (value.length > bottomMaxLength) {
                    cet_content?.setText(value.substring(0, bottomMaxLength))
                    cet_content?.setSelection(bottomMaxLength)
                    return
                }

                if (null != listener) {
                    listener?.returnEdittextContent(s.toString())
                }
            }

        })


    }

    fun setEdittextFilter(temp: Int) {
        cet_content?.filters = arrayOf(DecimalDigitsInputFilter(temp))
    }


    fun setEditTextContent(content: String) {
        cet_content?.setText(content)
    }
    fun setEditTextHintContent(contextId: String) {
        cet_content?.hint = LanguageUtil.getString(context, contextId)
    }

    fun setToptitleContent(contextId: String) {
        tv_top_title?.text = LanguageUtil.getString(context, contextId)
    }

    fun setErrorEdittext(content: String = "", status: Boolean) {
        if (status) {
            tv_error_content?.visibility = View.VISIBLE
            tv_error_content?.text = content
            v_line?.setBackgroundResource(R.color.red)
        } else {
            tv_error_content?.visibility = View.GONE
            v_line?.setBackgroundResource(R.color.new_edit_line_color)
        }
    }

    fun normalErrorEdittext(status: Boolean) {
        if (status) {
            v_line?.setBackgroundResource(R.color.red)
        } else {
            v_line?.setBackgroundResource(R.color.new_edit_line_color)
        }
    }


    fun setRightContent(content: String) {
        bottomRightTextContent = content
        tv_right_content?.text = bottomRightTextContent
    }


}