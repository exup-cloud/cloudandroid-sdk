package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.ToastUtils
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.item_new_otc_textview.view.*

/**
 * @Author lianshangljl
 * @Date 2019-10-21-10:40
 * @Email buptjinlong@163.com
 * @description
 */
class NewOTCTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var topTextContent = ""
    var bottomTextContent = ""
    var topImageViewVisible = false
    var bottomImageViewVisible = false

    interface OTCTextViewClickListener {
        fun onclickTopImage()
        fun onclickBottomImage()
    }

    var listener: OTCTextViewClickListener? = null

    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NewOTCTextView)
            topTextContent = typedArray.getString(R.styleable.NewOTCTextView_topTextContent) ?: ""
            bottomTextContent = typedArray.getString(R.styleable.NewOTCTextView_bottomTextContent)
                    ?: ""
            topImageViewVisible = typedArray.getBoolean(R.styleable.NewOTCTextView_topImageViewVisible, false)
            bottomImageViewVisible = typedArray.getBoolean(R.styleable.NewOTCTextView_bottomImageViewVisible, false)
            typedArray.recycle()
        }
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.item_new_otc_textview, this, true)
        tv_top_title?.text = topTextContent
        tv_bottom_content?.text = bottomTextContent
        if (topImageViewVisible) {
            iv_top?.visibility = View.VISIBLE
        }
        if (bottomImageViewVisible) {
            iv_bottom?.visibility = View.VISIBLE
        }
        iv_top?.setOnClickListener {
            if (null != listener) {
                listener?.onclickTopImage()
            }
        }
        iv_bottom?.setOnClickListener {
            if (null != listener) {
                listener?.onclickBottomImage()
            }
            Utils.copyString(bottomTextContent)
            ToastUtils.showToast(context?.getString(R.string.common_tip_copySuccess))
        }
    }

    fun setToptitleContent(contentId: String) {
        tv_top_title?.text = LanguageUtil.getString(context, contentId)
    }


    fun setBottomContent(content: String) {
        bottomTextContent = content
        tv_bottom_content?.text = bottomTextContent
    }


}