package com.yjkj.chainup.new_version.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.yjkj.chainup.R
import com.yjkj.chainup.util.ToastUtils
import kotlinx.android.synthetic.main.layout_com_header.view.*
import org.jetbrains.anko.imageResource

/**
 * @Author: Bertking
 * @Date：2019-05-23-11:18
 * @Description:
 */
class ComHeaderView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {

    val TAG = ComHeaderView::class.java.simpleName


    var subTitle = ""
        set(value) {
            field = value
            tv_title?.text = value
            tv_sub_title?.text = value
        }

    var rightTitle = ""
        set(value) {
            field = value
            tv_right?.text = value
        }
    var rightIcon = 0
        set(value) {
            field = value
            iv_right?.imageResource = rightIcon
        }

    var showRightText = false
        set(value) {
            field = value
            tv_right?.visibility = if (value) View.VISIBLE else View.GONE
        }
    var showRightIcon = false
        set(value) {
            field = value
            iv_right?.visibility = if (value) View.VISIBLE else View.GONE
        }


    init {

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ComHeaderView, 0, 0)

            subTitle = typedArray.getString(R.styleable.ComHeaderView_sub_title).toString()

            rightTitle = typedArray.getString(R.styleable.ComHeaderView_right_title).toString()
            rightIcon = typedArray.getResourceId(R.styleable.ComHeaderView_right_icon, 0)

            showRightText = typedArray.getBoolean(R.styleable.ComHeaderView_show_right_text, false)
            showRightIcon = typedArray.getBoolean(R.styleable.ComHeaderView_show_right_icon, false)
            typedArray.recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.layout_com_header, this, true)

        tv_title?.text = subTitle
        tv_sub_title?.text = subTitle

        tv_right?.text = rightTitle
        iv_right?.imageResource = rightIcon

        iv_right?.visibility = if (showRightIcon) View.VISIBLE else View.GONE

        tv_right?.visibility = if (showRightText) View.VISIBLE else View.GONE

        iv_close?.setOnClickListener {
            ToastUtils.showToast("哈哈哈哈")
            (context as AppCompatActivity).finish()
        }

        ly_appbar?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            Log.d(TAG, "=====滑动距离:${Math.abs(verticalOffset)}======")
            if (Math.abs(verticalOffset) >= 140) {
                tv_title?.visibility = View.VISIBLE
                tv_sub_title?.visibility = View.GONE
            } else {
                tv_title?.visibility = View.GONE
                tv_sub_title?.visibility = View.VISIBLE
            }
        }

    }

}