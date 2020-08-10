package com.yjkj.chainup.new_version.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import kotlinx.android.synthetic.main.item_new_edittext_view.view.*

/**
 * @Author lianshangljl
 * @Date 2019-10-21-11:24
 * @Email buptjinlong@163.com
 * @description
 */
class NewOTCEditTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var topTextContent = ""
    var bottomTextContent = ""
    var edittextRightContent = ""
    var edittextRightImageView = false

    interface OTCEditTextListener {
        fun edittextListener()
    }

    var listener: OTCEditTextListener? = null

    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NewOTCEditTextView)
            topTextContent = typedArray.getString(R.styleable.NewOTCEditTextView_topEditTextContent).toString()
            bottomTextContent = typedArray.getString(R.styleable.NewOTCEditTextView_bottomEditTextContent).toString()
            edittextRightContent = typedArray.getString(R.styleable.NewOTCEditTextView_editTextRightContent).toString()
            edittextRightImageView = typedArray.getBoolean(R.styleable.NewOTCEditTextView_editTextRightIVVisible, false)
            typedArray.recycle()
        }
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_new_edittext_view, this, true)
        if (edittextRightImageView && TextUtils.isEmpty(edittextRightContent)) {
            cet_content.setImageViewVisible(true)
            tv_right_content.visibility = View.GONE
        } else {
            tv_right_content.visibility = View.VISIBLE
            cet_content.setImageViewVisible(false)
        }

        tv_right_content.text = edittextRightContent
        cet_content.setEditText(bottomTextContent)
        tv_top_title.text = topTextContent
        cet_content?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {

                return text
            }

            override fun returnItem(item: Int) {

            }

            override fun onclickImage() {
                if (null != listener) {
                    listener?.edittextListener()
                }
            }

        }

    }

    fun setToptitleContent(contentId: String) {
        tv_top_title?.text = LanguageUtil.getString(context, contentId)
    }

    fun setBottomContent(content: String) {
        bottomTextContent = content
        cet_content.setEditText(bottomTextContent)
    }

    fun setRightContent(content: String) {
        edittextRightContent = content
        tv_right_content.visibility = View.VISIBLE
        cet_content.setImageViewVisible(false)
        tv_right_content.text = edittextRightContent
    }

    fun setRightImageViewVisible() {
        cet_content.setImageViewVisible(false)
    }

}