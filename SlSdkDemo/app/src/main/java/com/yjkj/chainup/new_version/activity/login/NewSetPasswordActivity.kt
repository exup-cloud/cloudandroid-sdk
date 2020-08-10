package com.yjkj.chainup.new_version.activity.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.bean.RegStep2Bean
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.ActivityManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.InnerBrowserActivity
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.new_version.view.PwdSettingView
import com.yjkj.chainup.util.DisplayUtil
import com.yjkj.chainup.util.StringUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_new_version_set_pwd.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * @Author lianshangljl
 * @Date 2019/3/13-3:23 PM
 * @Email buptjinlong@163.com
 * @description 设置密码 or 重置密码
 */
@Route(path = "/login/newsetpasswordactivity")
class NewSetPasswordActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_new_version_set_pwd
    }


    var bean: RegStep2Bean? = null

    var account = ""
    var pwdContent = ""
    var pwdAgainContent = ""
    var index = 0
    var token = ""
    var numberCode = ""
    /**
     * 安全验证 默认是 google
     *  Google 0
     *  手机   1
     *  邮箱   2
     */
    var securityVerificationType = 0

    companion object {
        /**
         * @param account 账号
         * @param INDEX_STATUS 设置密码 or 重置密码  0 设置密码 1 重置密码
         */
        private const val ACCOUNT_NUM = "account_num"
        private const val INDEX_STATUS = "index_status"
        private const val INDEX_TOKEN = "index_token"
        private const val INDEX_NUMBER_CODE = "index_number_code"

        private const val PARAM = "param"

    }


    fun getData() {
        if (intent != null) {
            account = intent.getStringExtra(ACCOUNT_NUM) ?: ""
            token = intent.getStringExtra(INDEX_TOKEN) ?: ""
            numberCode = intent.getStringExtra(INDEX_NUMBER_CODE) ?: ""
            index = intent.getIntExtra(INDEX_STATUS, 0)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
    }


    override fun initView() {
        getData()
        tv_info?.text = LanguageUtil.getString(this,"register_tip_agreement")
        tv_terms_service?.text = LanguageUtil.getString(this,"register_action_agreement")

        cet_pwd_view?.isFocusable = true
        cet_pwd_view?.isFocusableInTouchMode = true
        cet_pwd_view?.setHintEditText(LanguageUtil.getString(this,"register_tip_inputPassword"))
        cet_pwd_again_view?.isFocusable = true
        cet_pwd_again_view?.isFocusableInTouchMode = true
        cet_pwd_again_view?.setHintEditText(LanguageUtil.getString(this,"register_tip_repeatPassword"))
        cet_pwd_invite_code_view?.setHintEditText(LanguageUtil.getString(this,"invite_code_hint"))
        cubtn_view?.isEnable(false)


        /**
         * 配置是
         */
        when (index) {
            0 -> {
                tv_title?.text =  LanguageUtil.getString(mActivity,"register_action_setPassword")
                cet_pwd_invite_code_view?.visibility = View.VISIBLE
                cubtn_view?.setContent( LanguageUtil.getString(mActivity,"register_action_register"))
                bean = intent?.getParcelableExtra(PARAM)
                Log.d(TAG, "======配置项:${bean?.toString()}=======")
                if (bean?.invitationCodeRequired == 0) {
                    cet_pwd_invite_code_view?.setHintEditText( LanguageUtil.getString(mActivity,"invite_code_hint"))
                } else {
                    cet_pwd_invite_code_view?.setHintEditText( LanguageUtil.getString(mActivity,"register_text_inviteCode"))
                }
                /**
                 * 点击 服务条款
                 */
                tv_terms_service?.setOnClickListener {
                    InnerBrowserActivity.enter2(this,  LanguageUtil.getString(mActivity,"register_action_agreement"), bean?.url
                            ?: "")
                }

            }
            1 -> {
                tv_title?.text =  LanguageUtil.getString(mActivity,"login_action_resetPassword")
                tv_info?.visibility = View.GONE
                cet_pwd_invite_code_view?.visibility = View.GONE
                tv_terms_service?.visibility = View.GONE
                cubtn_view?.setContent( LanguageUtil.getString(mActivity,"common_text_btnConfirm"))
            }
        }
        setOnclick()
        removeToken()
    }

    fun setOnclick() {

        iv_cancel?.setOnClickListener { finish() }

        /**
         * 监听第一个密码edittext
         */
        cet_pwd_view?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun onclickImage() {

            }

            override fun returnItem(item: Int) {

            }

            override fun showText(text: String): String {
                pwdContent = text
                if (pwdContent.isNotEmpty() && pwdAgainContent.isNotEmpty()) {
                    cubtn_view?.isEnable(true)
                } else {
                    cubtn_view?.isEnable(false)
                }
                return text
            }

        }


        /**
         * 监听第二个密码edittext
         */
        cet_pwd_again_view?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun onclickImage() {

            }

            override fun returnItem(item: Int) {

            }

            override fun showText(text: String): String {
                pwdAgainContent = text
                if (pwdContent.isNotEmpty() && pwdAgainContent.isNotEmpty()) {
                    cubtn_view?.isEnable(true)
                } else {
                    cubtn_view?.isEnable(false)
                }
                return text
            }

        }


        /**
         *  点击确认登录
         */
        cubtn_view?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                /**
                 * 判断密码是否相同
                 */
                if (pwdContent == pwdAgainContent) {
                    if (!StringUtils.checkPass(pwdAgainContent)) {
                        DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"login_tip_passwordRequire"), isSuc = false)
                        return
                    }
                } else {
                    DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"login_tip_passwordNotMatch"), isSuc = false)
                    return
                }
                when (index) {
                    /**
                     * 注册
                     */
                    0 -> {
                        /**
                         * 服务器返回 字段 如果是1 邀请码必填
                         * 0 邀请码选填
                         */
                        if (bean?.invitationCodeRequired == 1) {
                            if (TextUtils.isEmpty(cet_pwd_invite_code_view.text.trim())) {
                                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"common_tip_inputInviteCode"), false)
                                return
                            } else {
                                reg4Step3(account, pwdContent, cet_pwd_invite_code_view.text.toString())
                            }
                        } else {
                            reg4Step3(account, pwdContent, cet_pwd_invite_code_view.text.toString())
                        }
                    }
                    /**
                     * 忘记密码
                     */
                    1 -> {
                        findPwdStep4(token, pwdContent)
                    }
                }
            }
        }


    }

    /**
     * 找回密码 Step 4 实际上的第三步  新版本第三步删除了，只保留了第一步、第二步、第三步
     */
    private fun findPwdStep4(token: String, loginPword: String = "") {
        addDisposable(getMainModel().findPwdStep4(token, loginPword, object : NDisposableObserver(this,true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                closeLoadingDialog()
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
                var userInfo = LoginManager.getInstance().loginInfo
                userInfo.loginPwd = loginPword
                LoginManager.getInstance().saveLoginInfo(userInfo)
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"common_tip_editSuccess"), isSuc = true)
                finish()
            }

        }))

    }


    /**
     * 注册Step 3?
     *
     * @param registerCode 注意这里填的是"手机或者邮箱验证码"
     */
    private fun reg4Step3(registerCode: String, loginPwd: String, invitedCode: String = "") {
        addDisposable(getMainModel().reg4Step3(registerCode = registerCode, loginPword = loginPwd, newPassword = loginPwd, invitedCode = invitedCode, consumer = object : NDisposableObserver(this,true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                closeLoadingDialog()

                LoginManager.getInstance(this@NewSetPasswordActivity)
                        .saveLoginPwd(loginPwd)

                UserDataService.getInstance().saveGesturePass("")
                UserDataService.getInstance().clearToken()
                UserDataService.getInstance().clearLoginState()
                LoginManager.getInstance().saveFingerprint(0)

                /**
                 * 保存信息
                 */
//                        val loginInfo = LoginManager.getInstance().loginInfo
//                        loginInfo.loginPwd = loginPwd
//                        LoginManager.getInstance().saveLoginInfo(loginInfo)

                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"common_tip_registerSuccess"), isSuc = true)

                finish()
                ActivityManager.popAllActFromStack()
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
            }

        }))

    }

    var disposable: Disposable? = null
    /**
     * 5分钟后删除token并销毁页面
     */
    fun removeToken() {
        Observable.interval(5, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onNext(aLong: Long) {
                        Log.d("====onNext=====", "=====count:===along:$aLong")
                        if (disposable != null && !disposable?.isDisposed!!) {
                            disposable?.dispose()
                        }
                        finish()
                        ActivityManager.popAllActFromStack()

                        DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"new_register_time_out"), isSuc = false)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("=========", "====onSubscribe====")
                        disposable = d
                    }


                    override fun onError(e: Throwable) {
                        Log.d("========", "===onError")

                    }

                    override fun onComplete() {
                        Log.d("========", "===onComplete")

                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    fun cancel() {
        if (disposable != null && !disposable?.isDisposed()!!) {
            disposable?.dispose()
        }
    }


}