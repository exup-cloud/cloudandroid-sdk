package com.yjkj.chainup.new_version.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.GlideUtils
import com.yjkj.chainup.util.SoftKeyboardUtil
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.item_personal_center_title.view.*

/**
 * @Author lianshangljl
 * @Date 2019/3/27-12:04 PM
 * @Email buptjinlong@163.com
 * @description   头部文件
 */
class PersonalCenterView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = PersonalCenterView::class.java.simpleName

    var title = ""
    var name = ""
    var account = ""
    var rightTvTitle = ""
    var showTitle = true
    var showRightIc = false
    var showRightTv = false
    var showRightIc2 = false
    var listener: MyProfileListener? = null
    var finishListener: MyPorfileFinishListener? = null
    var rightIcon = 0

    var mContext = context

    interface MyProfileListener {
        fun onclickHead()
        fun onclickRightIcon()
        fun onclickName()
        fun onRealNameCertificat()
    }

    interface MyPorfileFinishListener {
        fun onclickFinish()
    }


    init {
        attrs.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.CustomPersonalTitle)
            showTitle = typedArray.getBoolean(R.styleable.CustomPersonalTitle_showTitle, true)
            showRightIc = typedArray.getBoolean(R.styleable.CustomPersonalTitle_showRightIc, false)
            showRightIc2 = typedArray.getBoolean(R.styleable.CustomPersonalTitle_showRightIc2, false)
            showRightTv = typedArray.getBoolean(R.styleable.CustomPersonalTitle_showRightTv, false)
            if (showRightTv) {
                rightTvTitle = typedArray.getString(R.styleable.CustomPersonalTitle_rightTitle).toString()
            }
            rightIcon = typedArray.getResourceId(R.styleable.CustomPersonalTitle_personalRightIcon, R.drawable.screening)
            title = typedArray.getString(R.styleable.CustomPersonalTitle_personalTitle).toString()
        }
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_personal_center_title, this, true)
        tv_title?.visibility = if (showTitle) View.VISIBLE else View.GONE
        rl_personal_layoyt?.visibility = if (showTitle) View.GONE else View.VISIBLE
        tv_title?.text = title
        tv_noun_login_pending?.text = LanguageUtil.getString(context,"noun_login_pending")
        tv_title_for_top?.text = title
        right_icon?.setImageResource(rightIcon)
        iv_back?.setOnClickListener {
            if (finishListener != null) {
                finishListener?.onclickFinish()
            } else {
                var activity = context as Activity
                //SoftKeyboardUtils.hideSoftKeyboard(activity)
                val view = activity.getCurrentFocus()
                SoftKeyboardUtil.hideSoftKeyboard(view)

                activity.finish()
            }

        }
        iv_personal_head?.setOnClickListener {
            if (listener != null) {
                listener?.onclickHead()
            }
        }
        if (!showRightIc) {
            right_icon?.visibility = View.GONE
        }
        if (!showRightIc2) {
            right_icon2.visibility = View.GONE
        }
        if (showRightTv) {
            tv_right_title.text = rightTvTitle
        } else {
            tv_right_title.visibility = View.GONE
        }

        right_icon?.setOnClickListener {
            if (listener != null && !Utils.isFastClick()) {
                listener?.onclickRightIcon()
            }


        }
        tv_name?.setOnClickListener { if (listener != null) listener?.onclickName() }
        tv_phone?.setOnClickListener { if (listener != null) listener?.onclickName() }
        tv_right_title?.setOnClickListener { if (listener != null) listener?.onclickRightIcon() }
        ll_certification?.setOnClickListener {
            if (!Utils.isFastClick()){
                if (listener != null) listener?.onRealNameCertificat()
            }
        }
        ll_certificationing?.setOnClickListener {
            if (!Utils.isFastClick()){
                if (listener != null) listener?.onRealNameCertificat() }
            }

    }

    /**
     * 设置右侧icon
     */
    fun rightIcon(icon: Int) {
        right_icon?.setImageResource(icon)
    }

    fun rightIcon2(icon: Int) {
        right_icon2?.setImageResource(icon)
    }

    fun setContentTitle(title: String) {
        tv_title?.text = title
        tv_title_for_top?.text = title
    }


    fun setRightTitle(rightText: String) {
        tv_right_title?.text = rightText
    }

    fun setRightVisible(status: Boolean) {
        if (status) {
            right_icon?.visibility = View.VISIBLE
        } else {
            right_icon?.visibility = View.GONE
        }
    }

    /**
     * 设置账号
     */
    fun setAccountContent(account: String) {
        tv_phone?.text = account
    }

    /**
     * 设置昵称
     */
    fun setUserName(name: String) {
        tv_name?.text = name
        tv_name?.visibility = View.VISIBLE
    }

    fun setNoLogin() {
        tv_name?.visibility = View.GONE
        ll_certification?.visibility = View.GONE
        ll_certificationing?.visibility = View.GONE
        tv_phone?.text = LanguageUtil.getString(context, "noun_login_notLogin")
    }

    fun slidingShowTitle(status: Boolean) {
        if (status) {
            tv_title_for_top?.visibility = View.GONE
            tv_title?.visibility = View.VISIBLE
        } else {
            tv_title_for_top?.visibility = View.VISIBLE
            tv_title?.visibility = View.GONE
        }
    }

    /**
     * 设置头像
     */

    fun setUserHeat(url: String) {
        GlideUtils.loadImage4OTC(mContext, url, iv_personal_head)
    }


    /**
     * 设置认证图片
     * @param status  0 未认证 1 认证中 2已认证
     */

    fun setCertification(status: Int) {
        when (status) {
            0 -> {
                iv_certification?.setImageResource(R.drawable.personal_notcertified)
                tv_certification?.text = LanguageUtil.getString(context, "personal_text_unverified")
                ll_certification?.setBackgroundResource(R.drawable.bg_personal_unauthorized)
                tv_certification?.setTextColor(ColorUtil.getColor(R.color.real_unauthorize_color))
                ll_certification?.visibility = View.VISIBLE
                ll_certificationing?.visibility = View.GONE
            }
            1 -> {
                iv_certification?.setImageResource(R.drawable.personal_notcertified)
                tv_certification?.text = LanguageUtil.getString(context, "personal_text_verified")
                ll_certification?.setBackgroundResource(R.drawable.bg_personal_unauthorized)
                tv_certification?.setTextColor(ColorUtil.getColor(R.color.real_unauthorize_color))
                ll_certificationing?.visibility = View.VISIBLE
                ll_certification?.visibility = View.GONE
            }
            2 -> {
                iv_certification?.setImageResource(R.drawable.personal_certified)
                tv_certification?.text = LanguageUtil.getString(context, "personal_text_verified")
                ll_certification?.setBackgroundResource(R.drawable.bg_personal_authorized)
                tv_certification?.setTextColor(ColorUtil.getColor(R.color.main_blue))
                ll_certification?.visibility = View.VISIBLE
                ll_certificationing?.visibility = View.GONE
            }
        }
    }

}