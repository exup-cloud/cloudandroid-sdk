package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.yjkj.chainup.R


/**
 * @Author lianshangljl
 * @Date 2020-05-04-21:34
 * @Email buptjinlong@163.com
 * @description
 */
class TwoLayersTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var titleView = ""
    var contentView = ""
    var isShowLandR = true
    var showLand = 0

     var txTitle: TextView? = null
    var txContext: TextView? = null

    init {
        attrs.let {
            var typeArray = context.obtainStyledAttributes(it, R.styleable.TextViewtwoWayView, 0, 0)
            titleView = typeArray.getString(R.styleable.TextViewtwoWayView_titleView).toString()
            contentView = typeArray.getString(R.styleable.TextViewtwoWayView_contentView).toString()
            isShowLandR = typeArray.getBoolean(R.styleable.TextViewtwoWayView_isShowLandR, true)
            showLand = typeArray.getInteger(R.styleable.TextViewtwoWayView_showGravity, 0)
        }
        initView(context)
    }

    fun initView(context: Context) {

        LayoutInflater.from(context).inflate(when (showLand) {
            0 -> R.layout.item_two_layers_left
            1 -> R.layout.item_two_layers_left_center
            else -> {
                R.layout.item_two_layers
            }

        }, this)
        txTitle = findViewById(R.id.tv_title)
        txContext = findViewById(R.id.tv_content)

        txTitle?.text = titleView
        txContext?.text = contentView
    }

    private fun initViewType() {
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        when (showLand) {
            0 -> {
                lp.gravity = Gravity.START
            }
            1 -> {
                lp.gravity = Gravity.START
            }
            2 -> {
                lp.gravity = Gravity.END
            }
        }
        this.apply {
            layoutParams = lp
            orientation = LinearLayout.VERTICAL
        }
    }

    fun setTitleContent(content: String) {
        titleView = content
        txTitle?.text = titleView
    }

    fun setContentText(content: String) {
        contentView = content
        txContext?.text = contentView
    }

}