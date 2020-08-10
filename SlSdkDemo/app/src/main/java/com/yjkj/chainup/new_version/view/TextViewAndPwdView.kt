package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.item_textview_pwd.view.*

/**
 * @Author lianshangljl
 * @Date 2019/5/14-10:18 AM
 * @Email buptjinlong@163.com
 * @description
 */
class TextViewAndPwdView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var titleContent = ""
    var hintContent = ""

    var listener: OnTextListener? = null
    var pwdContent = ""
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
        }
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_textview_pwd, this, true)
        tv_title?.text = titleContent
        cet_view?.setHintEditText(hintContent)
        cet_view?.isFocusable = true
        cet_view?.isFocusableInTouchMode = true

        cet_view?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {
                pwdContent = text
                if (listener != null) {
                    listener?.showText(text)
                }
                return text
            }

            override fun returnItem(item: Int) {
            }

            override fun onclickImage() {
            }

        }

    }


    fun setTitle(title: String) {
        tv_title?.text = title
    }

    fun setEditHint(content:String){
        cet_view?.setHintEditText(content)
    }
}