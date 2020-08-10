package com.yjkj.chainup.contract.widget

import android.content.Context
import android.graphics.Typeface
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.sl_title_bar_layout.view.*

/**
 * 统一标题栏
 * 1.配合NestedScrollView，处理滑动时，标题栏联动问题
 * 2.NestedScrollView需指定app:layout_behavior="@string/appbar_scrolling_view_behavior"
 * 3.最外层用CoordinatorLayout包裹
 * 4.若不考虑标题栏联动问题，可以使用PersonalCenterView控件
 * 5.使用代码如下：
 * <com.yjkj.chainup.contract.widget.SlTitleBarLayout
    android:id="@+id/title_layout"
    app:slTitle=""
    android:layout_width="match_parent"
    app:elevation="0dp"
    android:layout_height="@dimen/dp_105"/>
 */
class SlTitleBarLayout : AppBarLayout {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.SlTitleBarLayout, 0, 0)
            title = typedArray.getString(R.styleable.SlTitleBarLayout_slTitle) ?: ""
            typedArray.recycle()
        }
    }

    var title = ""
        set(value) {
            field = value
            collapsing_toolbar?.title = title
        }


    init {

         layoutInflater.inflate(R.layout.sl_title_bar_layout, this)
        var activity = context as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            activity.finish()
        }
         collapsing_toolbar?.let {
            it.setCollapsedTitleTextColor(ContextCompat.getColor(context, R.color.text_color))
            it.setExpandedTitleColor(ContextCompat.getColor(context, R.color.text_color))
            it.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            it.expandedTitleGravity = Gravity.BOTTOM
        }


    }

}