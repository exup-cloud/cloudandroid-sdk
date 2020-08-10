package com.yjkj.chainup.new_version.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.util.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.com_verify_view.view.*
import java.util.concurrent.TimeUnit

/**
 * @Author: Bertking
 * @Date：2019/3/7-2:20 PM
 * @Description: 手机 & 邮箱 & 谷歌 验证的公共组件
 */
class ComVerifyView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = ComVerifyView::class.java.simpleName

    var countTotalTime = 90

    var verifyType = GOOGLE

    var inputType = InputType.TYPE_CLASS_NUMBER

    var accountValidation = false

    var accountContent = ""

    var countryCode = ""

    fun setValidation(status: Boolean) {
        accountValidation = status
    }

    fun setAccount(account: String) {
        accountContent = account
    }

    fun setCountry(code: String) {
        countryCode = code
    }

    var code = ""

    var text = ""
    var onTextListener: OnTextListener? = null
    var textSize = 16f


    interface OnTextListener {
        fun showText(text: String): String
    }


    companion object {
        const val GOOGLE = 0
        const val MOBILE = 1
        const val EMAIL = -1
        const val IDCard = 2
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ComVerifyView, 0, 0)
            verifyType = typedArray.getInt(R.styleable.ComVerifyView_verify_type, GOOGLE)
            textSize = typedArray.getDimension(R.styleable.ComVerifyView_verify_type_size, resources.getDimension(R.dimen.sp_16))
            inputType = typedArray.getInt(R.styleable.PwdSettingView_android_inputType, InputType.TYPE_CLASS_NUMBER)
            typedArray.recycle()
        }

        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.com_verify_view, this, true)
        et_input_code?.hint = LanguageUtil.getString(context, "login_tip_inputCode")
        et_input_code.setOnFocusChangeListener { v, hasFocus ->
            v_line.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
        tv_code?.text = LanguageUtil.getString(context, "login_action_resendCode")
        et_input_code?.paint?.textSize = textSize
        if (verifyType == GOOGLE) {
            tv_code.text = LanguageUtil.getString(context, "common_action_paste")
        }


        et_input_code.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                code = s.toString()
                if (onTextListener != null) {
                    onTextListener?.showText(s.toString())
                }

            }
        })


        tv_code?.setOnClickListener {
            when (verifyType) {
                GOOGLE -> {
                    ClipboardUtil.paste(et_input_code)
                }

                MOBILE -> {
                    if (accountValidation) {
                        sendMobileVerifyCodeAccount(tv_code, countryCode, accountContent)
                    } else {
                        sendMobileVerifyCode(tv_code)
                    }
                }

                EMAIL -> {
                    if (accountValidation) {
                        sendEmailVerifyCodeAccount(tv_code, accountContent)
                    } else {
                        sendEmailVerifyCode(tv_code)
                    }

                }

            }
        }
    }

    fun setType(type: Int) {
        verifyType = type
        if (type == IDCard) {
            tv_code.visibility = View.GONE
            et_input_code.inputType = InputType.TYPE_CLASS_TEXT
            return
        }
        tv_code.visibility = View.VISIBLE
        tv_code?.text = if (type == GOOGLE) {
            LanguageUtil.getString(context, "common_action_paste")
        } else {
            LanguageUtil.getString(context, "login_action_resendCode")
        }

    }

    var token = UserDataService.getInstance().token
    fun sendVerify(verifyType: Int, accountValidation: Boolean = false, accountContent: String = "", token4last: String = "") {
        if (!TextUtils.isEmpty(token4last)) {
            token = token4last
        }
        when (verifyType) {
            GOOGLE -> {
                ClipboardUtil.paste(et_input_code)
            }

            MOBILE -> {
                if (accountContent.isNotEmpty()) {
                    sendMobileVerifyCodeAccount(tv_code, countryCode, accountContent)
                } else {
                    sendMobileVerifyCode(tv_code)
                }

            }

            EMAIL -> {
                if (accountValidation) {
                    sendEmailVerifyCodeAccount(tv_code, accountContent)
                } else {
                    sendEmailVerifyCode(tv_code)
                }

            }

        }
    }


    fun sendCode(type: Int, account: String, countryCode: String) {
        accountContent = account
        if (accountContent.isNotEmpty()) {
            accountValidation = true
        }
        this.countryCode = countryCode
        Log.d(TAG, "=======发送验证码:countryCode:$countryCode,account:$account=======")
        when (type) {
            MOBILE -> {
                sendMobileVerifyCodeAccount(tv_code, countryCode, account)
            }
            EMAIL -> {
                sendEmailVerifyCodeAccount(tv_code, account)
            }
        }
    }

    var otypeForPhone = AppConstant.MOBILE_LOGIN


    private fun sendMobileVerifyCode(view: TextView) {
        view.isClickable = false
        HttpClient.instance.sendMobileCode(otype = otypeForPhone, token = token ?: return)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Log.d("-----------", it.toString())
                    if (it.isSuccess) {
                        Observable.interval(1, TimeUnit.SECONDS)
                    } else {
                        ToastUtils.toastOnUIThread(it.msg)
                        view.isClickable = true
                        null
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    var disposable: Disposable? = null
                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {
                        view.text = "(${(countTotalTime - t.toInt()).toString() + "s"}) ${LanguageUtil.getString(context, "login_action_resendCode")}"
                        view.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                        if (t.toInt() == countTotalTime) {
                            view.text = LanguageUtil.getString(context, "login_action_resendCode")
                            view.isClickable = true
                            view.setTextColor(ColorUtil.getColor(R.color.main_color))
                            disposable?.dispose()
                        }
                    }
                })
    }

    var otypeForEmail = AppConstant.EMAIL_LOGIN

    private fun sendEmailVerifyCode(view: TextView) {
        view.isClickable = false
        HttpClient.instance.sendEmailCode(otype = otypeForEmail, token = token ?: return)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Log.d("-----------", it.toString())
                    if (it.isSuccess) {
                        Observable.interval(1, TimeUnit.SECONDS)
                    } else {
                        ToastUtils.toastOnUIThread(it.msg)
                        view.isClickable = true
                        null
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    var disposable: Disposable? = null
                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {
                        view.text = "(${(countTotalTime - t.toInt()).toString() + "s"}) ${LanguageUtil.getString(context, "login_action_resendCode")}"
                        view.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                        if (t.toInt() == countTotalTime) {
                            view.text = LanguageUtil.getString(context, "get_code")
                            view.isClickable = true
                            view.setTextColor(ColorUtil.getColor(R.color.main_blue))
                            disposable?.dispose()
                        }
                    }
                })
    }

    fun getCodeNum(): String {
        return et_input_code.text.toString()
    }


    /**
     * 手机验证码
     */
    private fun sendMobileVerifyCodeAccount(view: TextView, countryCode: String, account: String) {
        view.isClickable = false
        HttpClient.instance.sendMobileCode(countryCode, account, otypeForPhone)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Log.d("-----------", it.toString())
                    if (it.isSuccess) {
                        Observable.interval(1, TimeUnit.SECONDS)
                    } else {
                        ToastUtils.toastOnUIThread(it.msg)
                        view.isClickable = true
                        null
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    var disposable: Disposable? = null
                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {
                        view.text = "(${(countTotalTime - t.toInt()).toString() + "s"}) ${LanguageUtil.getString(context, "login_action_resendCode")}"
                        view.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                        if (t.toInt() == countTotalTime) {
                            view.text = LanguageUtil.getString(context, "login_action_resendCode")
                            view.isClickable = true
                            view.setTextColor(ColorUtil.getColor(R.color.main_blue))
                            disposable?.dispose()
                        }
                    }
                })
    }

    /**
     * 邮箱验证码
     */
    private fun sendEmailVerifyCodeAccount(view: TextView, account: String) {
        view.isClickable = false
        HttpClient.instance.sendEmailCode(account, otypeForEmail)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    Log.d("-----------", it.toString())
                    if (it.isSuccess) {
                        Observable.interval(1, TimeUnit.SECONDS)
                    } else {
                        ToastUtils.toastOnUIThread(it.msg)
                        view.isClickable = true
                        null
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    var disposable: Disposable? = null
                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(t: Long) {
                        view.text = "(${(countTotalTime - t.toInt()).toString() + "s"}) ${LanguageUtil.getString(context, "login_action_resendCode")}"
                        view.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                        if (t.toInt() == countTotalTime) {
                            view.text = LanguageUtil.getString(context, "login_action_resendCode")
                            view.isClickable = true
                            view.setTextColor(ColorUtil.getColor(R.color.main_blue))
                            disposable?.dispose()
                        }
                    }
                })
    }

    fun loginTypeToBean(type: String, token: String) {
        setType(type.verifitionType())
        this.otypeForPhone = type.verfitionTypeForPhone()
        if (type != "1") {
            sendVerify(type.verifitionType(), token4last = token)
        }
        et_input_code.hint =  LanguageUtil.getString(context,type.verfitionTypeHint())

    }
}
