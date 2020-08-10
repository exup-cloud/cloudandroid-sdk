package com.yjkj.chainup.new_version.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.SelectAreaActivity
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.layout_pwd_setting.view.*

/**
 * @Author: Bertking
 * @Date：2018/11/19-2:46 PM
 * @Description:
 */

class PwdSetView @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    var title = ""

    var hint = ""

    var isShowLine = true
    private var isPwdShow: Boolean = true

    private var isNeedArea = false

    private var inputType: Int = InputType.TYPE_TEXT_VARIATION_PASSWORD

    var text = ""

    var onTextListener: OnTextListener? = null


    var chooseAreaView: TextView? = null


    interface OnTextListener {
        fun showText(text: String): String
    }


    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PwdSetView, 0, 0)
            hint = typedArray.getString(R.styleable.PwdSetView_hint) ?: ""
            title = typedArray.getString(R.styleable.PwdSetView_pwdTitle) ?: ""
            isPwdShow = typedArray.getBoolean(R.styleable.PwdSetView_isShowPwd, true)
            isShowLine = typedArray.getBoolean(R.styleable.PwdSetView_isShowLine, true)
            isNeedArea = typedArray.getBoolean(R.styleable.PwdSetView_isNeedArea, false)
            inputType = typedArray.getInt(R.styleable.PwdSetView_android_inputType, InputType.TYPE_TEXT_VARIATION_PASSWORD)
            typedArray.recycle()
        }

        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.layout_pwd_setting, this, true)

        tv_title.text = title

        et_pwd.hint = hint

        et_pwd.inputType = inputType

        chooseAreaView = tv_area

        chooseAreaView?.setOnClickListener {
            (context as Activity).startActivity(Intent(context, SelectAreaActivity::class.java))
        }


        /**
         * 判断地区
         */
        judgeViewState(v_split, isNeedArea)
        judgeViewState(tv_area, isNeedArea)


        /**
         * 判断线
         */
        judgeViewState(v_line, isShowLine)


        /**
         * 判断是否密码
         */
        judgeViewState(iv_hide, isPwdShow)

        if (isPwdShow) {
            Utils.isShowPass(!isPwdShow, iv_hide, et_pwd)
            iv_hide.setOnClickListener({ v ->
                isPwdShow = !isPwdShow
                Utils.isShowPass(isPwdShow, iv_hide, et_pwd)
            })
        }



        et_pwd.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s.toString())) {
                    tv_title.setTextColor(ContextCompat.getColor(context, R.color.main_font_color))
                    et_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                } else {
                    tv_title.setTextColor(ContextCompat.getColor(context, R.color.c_637498))
                    et_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

                }
                if (onTextListener != null) {
                    onTextListener?.showText(s.toString())
                }
                text = s.toString()

            }

        })


    }

    private fun judgeViewState(view: View, isShow: Boolean) {
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}