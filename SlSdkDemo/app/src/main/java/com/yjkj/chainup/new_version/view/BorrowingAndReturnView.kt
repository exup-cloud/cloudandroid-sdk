package com.yjkj.chainup.new_version.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.DecimalDigitsInputFilter
import kotlinx.android.synthetic.main.item_borrowing_and_return_view.view.*

/**
 * @Author lianshangljl
 * @Date 2019-11-09-16:06
 * @Email buptjinlong@163.com
 * @description
 */
class BorrowingAndReturnView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var firstTitle = ""
    var firstContent = ""


    var secondTitle = ""
    var secondContent = ""

    var thirdTitle = ""
    var thirdContent = ""

    var fourthTitle = ""
    var fourthContent = ""


    var fifthTitle = ""
    var fifthContent = ""

    var fifthVisible = false

    var minVolume = ""

    var listener: AllBtnClickListener? = null

    interface AllBtnClickListener {
        fun btnClick()
    }

    init {
        attrs.let {
            var arrayType = context.obtainStyledAttributes(it, R.styleable.BorrowingAndReturnView, 0, 0)
            firstTitle = arrayType.getString(R.styleable.BorrowingAndReturnView_first_title).toString()

            secondTitle = arrayType.getString(R.styleable.BorrowingAndReturnView_second_title).toString()

            thirdTitle = arrayType.getString(R.styleable.BorrowingAndReturnView_third_title).toString()

            fourthTitle = arrayType.getString(R.styleable.BorrowingAndReturnView_fourth_title).toString()

            fifthVisible = arrayType.getBoolean(R.styleable.BorrowingAndReturnView_fifth_visible, false)
            if (fifthVisible) {
                fifthTitle = arrayType.getString(R.styleable.BorrowingAndReturnView_fifth_title).toString()
            }


        }
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_borrowing_and_return_view, this, true)
        tv_first_title?.setTitleContent(firstTitle)
        tv_second_title?.setTitleContent(secondTitle)
        tv_third_title?.setTitleContent(thirdTitle)
        tv_fourth_title?.setTitleContent(fourthTitle)
        if (fifthVisible) {
            tv_fifth_title?.setTitleContent(fifthTitle)
        } else {
            tv_fifth_title?.visibility = View.GONE
        }
        btn_all_amount?.setText(LanguageUtil.getString(context,"common_action_sendall"))

        /**
         * 最小提币数
         */
        et_amount?.isFocusable = true
        et_amount?.isFocusableInTouchMode = true
        et_amount?.setOnFocusChangeListener { v, hasFocus ->
            view_amount_line?.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
        et_amount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                et_amount?.setHintTextColor(ContextCompat.getColor(context, R.color.hint_color))
                et_amount?.setTextColor(ContextCompat.getColor(context, R.color.text_color))
                view_amount_line?.setBackgroundResource(R.color.main_blue)
                tv_prompt?.text = ""
                tv_prompt?.visibility = View.GONE
                if (p0?.isNotEmpty() == true) {
                    minVolume = p0.toString()
                } else {
                    minVolume = "0"
                }

            }
        })
        btn_all_amount?.setOnClickListener {
            if (null != listener) {
                listener?.btnClick()
            }
        }
    }

    fun setReturnError(temp: String) {
        tv_prompt?.text = temp
        tv_prompt?.visibility = View.VISIBLE
        et_amount?.setHintTextColor(ContextCompat.getColor(context, R.color.red))
        et_amount?.setTextColor(ContextCompat.getColor(context, R.color.red))
        view_amount_line?.setBackgroundResource(R.color.red)
    }


    fun setFirst(temp: String) {
        tv_first_title?.setContentText(temp)
    }

    fun setSecond(temp: String) {
        tv_second_title?.setContentText(temp)
    }

    fun setThird(temp: String) {
        tv_third_title?.setContentText(temp)
    }

    fun setFourth(temp: String) {
        tv_fourth_title?.setContentText(temp)
    }

    fun setFifth(temp: String) {
        tv_fifth_title?.setContentText(temp)
    }

    fun setFirstTitleContent(temp: String) {
        tv_first_title?.setTitleContent(temp)
    }

    fun setSecondTitleContent(temp: String) {
        tv_second_title?.setTitleContent(temp)
    }

    fun setThirdTitleContent(temp: String) {
        tv_third_title?.setTitleContent(temp)
    }

    fun setFourthTitleContent(temp: String) {
        tv_fourth_title?.setTitleContent(temp)
    }

    fun setFifthTitleContent(temp: String) {
        tv_fifth_title?.setTitleContent(temp)
    }

    fun setColumeTitle(temp: String) {
        tv_volume_title?.text = temp
    }


    fun setEditHintContent(temp: String) {
        et_amount?.hint = LanguageUtil.getString(context,"withdraw_text_minimumVolume") + temp
    }

    fun setEditHintGiveBackContent(temp: String) {
        et_amount?.hint = temp
    }

    fun setEdittextContent(temp: String) {
        et_amount?.setText(temp)
    }

    var coin = ""
    fun setEditTextCoinContent(temp: String) {
        coin = temp
        tv_coin_name?.text = temp
    }

    fun setGiveEndTextViewContent(temp: String) {
        tv_available_content?.text = "${LanguageUtil.getString(context,"withdraw_text_available")} $temp $coin"
    }


    fun setEndTextViewContent(temp: String) {
        tv_available_content?.text = "${LanguageUtil.getString(context,"leverage_text_canborrow")} $temp $coin"
    }

    fun setEdittextFilter(temp: Int) {
        et_amount?.filters = arrayOf(DecimalDigitsInputFilter(temp))
    }
}