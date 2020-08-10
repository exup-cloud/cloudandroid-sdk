package com.yjkj.chainup.new_version.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.pwd_setting_view.view.*

/**
 * @Author: Bertking
 * @Date：2019/3/6-8:08 PM
 * @Description: 适用于「密码」 & 「登录选择验证模式」
 */
class PwdSettingView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NewDialogUtils.DialogOnclickListener {

    val TAG = PwdSettingView::class.java.simpleName


    override fun clickItem(data: ArrayList<String>, item: Int) {
        selectPosition = item
        setEditText(list[item])
        tDialog?.dismiss()
        if (onTextListener != null) {
            onTextListener?.returnItem(item)
        }
    }


    private var hintText: String? = ""

    /**
     * 右侧图片
     */
    private var resId = 0

    private var resId2 = 0
    /**
     * 字体颜色
     */

    private var textContentColor = ContextCompat.getColor(context, R.color.text_color)

    private var textContentSize: Float = 16f


    /**
     * 是否能点击
     */
    private var isEditable = true
    /**
     * 是否是选择国家
     */

    private var changeCity = false
    /**
     * 是否显示密码格式
     */
    private var isShowPwd = true

    var textContent: String? = ""
        set(value) {
            field = value
            if (!TextUtils.isEmpty(value)) {
                et_pwd?.setText(value)
            }
        }

    var text = ""


    var onTextListener: OnTextListener? = null

    private var inputType: Int = InputType.TYPE_TEXT_VARIATION_PASSWORD

    interface OnTextListener {
        fun showText(text: String): String

        fun returnItem(item: Int)

        fun onclickImage()
    }

    fun setListener(listener: OnTextListener) {
        this.onTextListener = listener
    }


    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PwdSettingView, 0, 0)
            hintText = typedArray.getString(R.styleable.PwdSettingView_hint_text)
            isShowPwd = typedArray.getBoolean(R.styleable.PwdSettingView_isPwdShow, true)
            isEditable = typedArray.getBoolean(R.styleable.PwdSettingView_isEditable, true)
            changeCity = typedArray.getBoolean(R.styleable.PwdSettingView_changeCity, false)
            textContent = typedArray.getString(R.styleable.PwdSettingView_textContent)
            resId = typedArray.getResourceId(R.styleable.PwdSettingView_icon_res, R.drawable.hide)
            resId2 = typedArray.getResourceId(R.styleable.PwdSettingView_icon_res2, R.drawable.dropdown)
            textContentColor = typedArray.getColor(R.styleable.PwdSettingView_pwd_text_content_color, ContextCompat.getColor(context, R.color.text_color))
            textContentSize = typedArray.getDimension(R.styleable.PwdSettingView_textContentSize, resources.getDimension(R.dimen.sp_16))
            inputType = typedArray.getInt(R.styleable.PwdSettingView_android_inputType, InputType.TYPE_TEXT_VARIATION_PASSWORD)
            typedArray.recycle()
        }
        initView(context)
    }


    var tDialog: TDialog? = null
    var validationClick = true
    fun setvalidationStatus(status: Boolean) {
        validationClick = status
    }

    /**
     * 三种验证方式
     */
    var list = arrayListOf(context.getString(R.string.safety_text_googleAuth), context.getString(R.string.safety_text_phoneAuth),
            context.getString(R.string.safety_text_mailAuth))
    var selectPosition = 0


    fun initView(context: Context) {
        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.pwd_setting_view, this, true)

        et_pwd?.isFocusable = isEditable
        et_pwd?.isFocusableInTouchMode = isEditable

        if (isEditable) {
            v_container.visibility = View.GONE
        } else {
            v_container.visibility = View.VISIBLE
        }
        et_pwd?.hint = hintText

        et_pwd?.setTextColor(textContentColor)
        Log.d(TAG, "==========textContentSize:$textContentSize=========")
        et_pwd?.paint?.textSize = textContentSize

        et_pwd?.inputType = inputType

        if (!TextUtils.isEmpty(textContent)) {
            et_pwd?.setText(textContent)
        }

        if (isShowPwd) {
            iv_image?.visibility = View.VISIBLE
        } else {
            iv_image2?.visibility = View.VISIBLE
        }


        et_pwd?.setOnFocusChangeListener { v, hasFocus ->
            v_line?.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }

        if (isShowPwd) {
            Utils.isShowPass(!isShowPwd, iv_image, et_pwd)
            iv_image?.setOnClickListener { v ->
                isShowPwd = !isShowPwd
                Utils.isShowPass(isShowPwd, iv_image, et_pwd)
                if (onTextListener != null) {
                    onTextListener?.onclickImage()
                }
            }
        } else {
            v_container?.setOnClickListener {
                /**
                 * 选择 验证方式 默认是google
                 */
                if (onTextListener != null) {
                    onTextListener?.onclickImage()
                }
            }
        }

        if (resId > 0) {
            iv_image?.setImageResource(resId)
        }

        if (resId2 > 0) {
            iv_image2?.setImageResource(resId2)
        }

        et_pwd?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (onTextListener != null) {
                    onTextListener?.showText(s.toString())
                }
                text = s.toString()
            }

        })
    }


    fun setImageView(resId: Int) {
        if (resId > 0) {
            iv_image?.setImageResource(resId)
        }
    }

    fun setImageViewVisible(status: Boolean) {
        if (isShowPwd) {
            if (status) {
                iv_image?.visibility = View.VISIBLE
            } else {
                iv_image?.visibility = View.GONE
            }
        } else {
            if (status) {
                iv_image2?.visibility = View.VISIBLE
            } else {
                iv_image2?.visibility = View.GONE
            }
        }

    }

    fun setImageView2Visible(resId: Int) {
        iv_image?.visibility = View.GONE
        iv_image2?.visibility = View.VISIBLE
        iv_image2?.setImageResource(resId)
    }


    fun setSelect(index: Int) {
        selectPosition = index
    }

    fun setEditText(content: String) {
        et_pwd?.setText(content)
    }

    fun setHintEditText(content: String) {
        et_pwd?.hint = content
    }


}