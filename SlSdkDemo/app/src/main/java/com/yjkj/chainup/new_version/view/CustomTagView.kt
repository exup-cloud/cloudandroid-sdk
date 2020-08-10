package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.view_tage_custom.view.*

/**
 * @Author lianshangljl
 * @Date 2020-02-10-13:26
 * @Email buptjinlong@163.com
 * @description
 */
class CustomTagView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var showCircular = false

    init {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomTagView, 0, 0)
            showCircular = typedArray.getBoolean(R.styleable.CustomTagView_showCircular, false)
            typedArray.recycle()
        }
        initView()
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_tage_custom, this, true)
        if (showCircular) {
            ll_tag_layout?.setBackgroundResource(R.drawable.bg_tag_circular_bead)
        } else {
            ll_tag_layout?.setBackgroundResource(R.drawable.bg_tag_uncircular_bead)
        }
    }

    fun setTextViewContent(content: String) {
        tv_tag_content?.text = content
    }


}