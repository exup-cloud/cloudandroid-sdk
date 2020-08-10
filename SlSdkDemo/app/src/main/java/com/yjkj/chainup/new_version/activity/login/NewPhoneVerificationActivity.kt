package com.yjkj.chainup.new_version.activity.login

import android.os.Bundle
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.FindPwd2verifyActivity
import com.yjkj.chainup.new_version.view.ComVerifyView
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.DisplayUtil
import com.yjkj.chainup.util.KeyBoardUtils
import com.yjkj.chainup.util.SoftKeyboardUtil
import kotlinx.android.synthetic.main.activity_new_version_phone_verification.*
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019/3/13-10:26 AM
 * @Email buptjinlong@163.com
 * @description 手机or邮箱验证or谷歌验证
 */
@Route(path = "/login/newphoneverificationactivity")
class NewPhoneVerificationActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_new_version_phone_verification
    }

    /**
     * 安全验证 默认是 google
     *  Google 0
     *  手机   1
     *  邮箱   2
     */
    var statusPosition = 0

    var isLogin = 0
    /**
     * 账号
     */
    var account = ""

    /**
     * 验证码
     */
    var code = ""

    /**
     * token
     */
    var token = ""
    /**
     * 国家码
     */
    var countryCode = "86"

    /**
     * 指纹
     */
    lateinit var fingerprintManager: FingerprintManagerCompat


    companion object {
        const val GOOGLE_VERIFY = 0

        const val MOBiLE_VERIFY = 1

        const val EMAIL_VERIFY = 2

        const val FINDPWDSTEP2_TYPE = 1
        const val CONFIRMLOGIN_TYPE = 2
        const val LOGININFORMATION_TYPE = 3
        const val GETUSERINFO_TYPE = 4
        const val CHECKLOCALPWD_TYPE = 5
        const val REG4STEP2_TYPE = 6
        const val GETTOKEN4PWD_TYPE = 7


        const val SEND_POSITION = "send_position"
        const val SEND_ISLOGIN = "send_islogin"
        const val SEND_ACCOUNT = "send_account"
        const val SEND_TOKEN = "send_token"
        const val SEND_COUNTRYCODE = "send_countryCode"

        /**
         *  @param account 账号
         *  @param position 0 谷歌验证 1 是手机验证 2 邮箱验证
         *  @param isLogin 是否是登录  0 是登录  1  是注册 2 安全验证（忘记密码） 3 重置密码
         *  @param countryCode  国家码
         */

    }


    fun getData() {
        if (intent != null) {
            statusPosition = intent.getIntExtra(SEND_POSITION, 0)
            isLogin = intent.getIntExtra(SEND_ISLOGIN, 0)
            account = intent.getStringExtra(SEND_ACCOUNT)
            token = intent.getStringExtra(SEND_TOKEN)
            countryCode = intent.getStringExtra(SEND_COUNTRYCODE)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
        setTextContent()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UserDataService.getInstance().clearToken()
        }
        return super.onKeyDown(keyCode, event)
    }
    fun setTextContent(){
        tv_send_verification_code?.text = LanguageUtil.getString(this,"login_tip_didSendCode")
        cubtn_view?.setBottomTextContent(LanguageUtil.getString(this,"common_action_next"))
    }

    override fun initView() {

        fingerprintManager = FingerprintManagerCompat.from(this)
        getData()
        setOnClick()
        cubtn_view?.isEnable(false)


        when (statusPosition) {

            GOOGLE_VERIFY -> {
                tv_title?.text =  LanguageUtil.getString(mActivity,"safety_text_googleAuth")
                tv_send_verification_code?.visibility = View.GONE
                cet_view?.setType(ComVerifyView.GOOGLE)
            }


            MOBiLE_VERIFY -> {
                tv_title?.text =  LanguageUtil.getString(mActivity,"safety_text_phoneAuth")
                cet_view?.setType(ComVerifyView.MOBILE)
                when (isLogin) {
                    0 -> {
                        cet_view?.otypeForPhone = AppConstant.MOBILE_LOGIN
                        cet_view?.sendVerify(ComVerifyView.MOBILE, token4last = token)
                    }

                    1 -> {
                        cet_view?.setValidation(true)
                        cet_view?.otypeForPhone = AppConstant.REGISTER_MOBILE
                        cet_view?.sendCode(ComVerifyView.MOBILE, account, countryCode)
                    }

                    2 -> {
                        cet_view?.otypeForPhone = AppConstant.FIND_PWD_MOBILE
                        cet_view?.sendVerify(ComVerifyView.MOBILE, token4last = token)
                    }

                    3 -> {
                        cet_view?.otypeForPhone = AppConstant.CHANGE_PWD
                        cet_view?.sendVerify(ComVerifyView.MOBILE)
                    }
                }
            }

            EMAIL_VERIFY -> {
                tv_title?.text =  LanguageUtil.getString(mActivity,"safety_text_mailAuth")
                cet_view?.setType(ComVerifyView.EMAIL)

                when (isLogin) {
                    0 -> {
                        cet_view?.otypeForEmail = AppConstant.EMAIL_LOGIN
                        cet_view?.sendVerify(ComVerifyView.EMAIL, token4last = token)
                    }
                    1 -> {
                        cet_view?.setValidation(true)
                        cet_view?.otypeForEmail = AppConstant.REGISTER_EMAIL
                        cet_view?.sendCode(ComVerifyView.EMAIL, account, countryCode)
                    }
                    2 -> {
                        cet_view?.otypeForEmail = AppConstant.FIND_PWD_EMAIL
                        cet_view?.sendVerify(ComVerifyView.EMAIL, token4last = token)
                    }
                    3 -> {
                        cet_view?.sendVerify(ComVerifyView.EMAIL)
                    }
                }

            }
        }
        when (isLogin) {
            0 -> {
                cubtn_view?.setContent( LanguageUtil.getString(mActivity,"common_text_btnConfirm"))
            }
            1 -> {
                cubtn_view?.setContent( LanguageUtil.getString(mActivity,"common_action_next"))
            }
            2 -> {
                cubtn_view?.setContent( LanguageUtil.getString(mActivity,"common_action_next"))
                tv_title?.text =  LanguageUtil.getString(mActivity,"login_action_fogetpwdSafety")
            }
        }

    }

    fun setOnClick() {
        iv_cancel?.setOnClickListener {
            UserDataService.getInstance().clearToken()
            finish()
        }

        cet_view?.onTextListener = object : ComVerifyView.OnTextListener {
            override fun showText(text: String): String {
                code = text
                if (code.length == 6) {
                    setLoginStatus()
                    cubtn_view?.isEnable(true)
                } else {
                    cubtn_view?.isEnable(false)
                }

                return text
            }
        }

        cubtn_view?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                setLoginStatus()
            }

        }

    }

    fun setLoginStatus() {
        when (isLogin) {
            0 -> {
                addDisposable(getMainModel().confirmLogin(cet_view.code, getType(statusPosition).toString(), token, consumer = MyNDisposableObserver(CONFIRMLOGIN_TYPE)))
            }
            1 -> {
                addDisposable(getMainModel().reg4Step2(account, cet_view.code, consumer = MyNDisposableObserver(REG4STEP2_TYPE)))

            }
            2 -> {
                addDisposable(getMainModel().findPwdStep2(token, code, consumer = MyNDisposableObserver(FINDPWDSTEP2_TYPE)))
            }
            else -> {
                val bundle = Bundle()
                bundle.putString("account_num", account)
                bundle.putInt("index_status", 0)
                bundle.putString("index_token", token)
                bundle.putString("index_number_code", "")
                bundle.putString("param", "")
                ArouterUtil.navigation("/login/newsetpasswordactivity", bundle)
            }
        }

    }

    inner class MyNDisposableObserver(type: Int) : NDisposableObserver(this,true) {
        var reqType = type

        override fun onResponseSuccess(jsonObject: JSONObject) {
            var json = jsonObject.optJSONObject("data")
            when (reqType) {
                FINDPWDSTEP2_TYPE -> {
                    var isCertificateNumber = json?.optString("isCertificateNumber") ?: "0"
                    var isGoogleAuth = json?.optString("isGoogleAuth") ?: "0"
                    if (isCertificateNumber == "0" && isGoogleAuth == "0") {
                        /**
                         * Directly 跳转至"重置密码"界面
                         */
                        val bundle = Bundle()
                        bundle.putString("account_num", account)
                        bundle.putInt("index_status", 1)
                        bundle.putString("index_token", token)
                        bundle.putString("index_number_code", "")
                        bundle.putString("param", "")
                        ArouterUtil.navigation("/login/newsetpasswordactivity", bundle)
                    } else {
                        /**
                         * 验证 Google , ID
                         */
                        FindPwd2verifyActivity.enter2(token, isCertificateNumber.toInt(), isGoogleAuth.toInt(), account)
                    }
                    finish()
                }
                CONFIRMLOGIN_TYPE -> {
                    /**
                     * {"code":"0","msg":"suc","data":null}
                     */
                    UserDataService.getInstance().saveToken(token)
                    HttpClient.instance.setToken(token)
                    getMainModel().saveUserInfo()
                    addDisposable(getMainModel().getUserInfo(MyNDisposableObserver(GETUSERINFO_TYPE)))
                    /**
                     * 登录成功
                     */
                    DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"login_tip_loginsuccess"), isSuc = true)

                    ArouterUtil.refreshWebview()
                    KeyBoardUtils.closeKeyBoard(this@NewPhoneVerificationActivity)
                    finish()
                }
                LOGININFORMATION_TYPE -> {

                }
                GETUSERINFO_TYPE -> {
                    getUserInfo(json)
                }
                CHECKLOCALPWD_TYPE -> {

                }
                REG4STEP2_TYPE -> {
                    var numberCode = getType(statusPosition).toString()
                    val bundle = Bundle()
                    bundle.putString("account_num", account)
                    bundle.putInt("index_status", 0)
                    bundle.putString("index_token", "")
                    bundle.putString("index_number_code", numberCode)
                    bundle.putString("param", json.toString())
                    ArouterUtil.navigation("/login/newsetpasswordactivity", bundle)
                    finish()
                }
                GETTOKEN4PWD_TYPE -> {

                }
            }


        }

        override fun onResponseFailure(code: Int, msg: String?) {
            super.onResponseFailure(code, msg)

        }

    }


    fun getType(index: Int): Int {
        var type = 1
        when (index) {
            0 -> {
                type = 1
            }
            1 -> {
                type = 2
            }
            2 -> {
                type = 3
            }
        }
        return type
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo(data: JSONObject?) {
        if (data == null) return

        var gesturePwd = data.optString("gesturePwd") ?: ""

        UserDataService.getInstance().saveData(data)

        var focusView = this.currentFocus

        /**
         * 判断 是否设置过手势密码
         */
        if (!TextUtils.isEmpty(gesturePwd) && TextUtils.isEmpty(UserDataService.getInstance().gesturePass)) {
            UserDataService.getInstance().saveGesturePass(gesturePwd)
            SoftKeyboardUtil.hideSoftKeyboard(focusView)
            finish()
            return
        } else if (!TextUtils.isEmpty(gesturePwd) || !TextUtils.isEmpty(UserDataService.getInstance().gesturePass)) {
            SoftKeyboardUtil.hideSoftKeyboard(focusView)
            finish()
            return
        }


        /**
         * 判断是否支持指纹
         */
        if (fingerprintManager.isHardwareDetected) {
            /**
             * 判断是否输入指纹
             */
            if (fingerprintManager.hasEnrolledFingerprints()) {
                if (LoginManager.getInstance().fingerprint == 1) {
                    SoftKeyboardUtil.hideSoftKeyboard(focusView)
                    finish()
                    return
                }

                enter2GUdeGesture(2, LoginManager.getInstance().loginPwd)
            } else {
                getToken4Pwd(LoginManager.getInstance().loginPwd, gesturePwd)
            }
        } else {
            getToken4Pwd(LoginManager.getInstance().loginPwd, gesturePwd)
        }


    }

    private fun enter2GUdeGesture(type: Int, pwd: String, handPwd: String = "") {
        SoftKeyboardUtil.hideSoftKeyboard(mActivity?.currentFocus)

        var bundle = Bundle()
        bundle.putInt("guidegesturetype", type)
        bundle.putString("guidegesturepwd", pwd)
        bundle.putString("guidegesturehandpwd", handPwd)
        ArouterUtil.navigation("/login/guidegesturepwdactivity", bundle)
        finish()
    }


    /**
     * 设置手势密码第一步 获取token
     */
    fun getToken4Pwd(pwd: String, gesturePwd: String) {
        addDisposable(getMainModel().checkLocalPwd(UserDataService.getInstance().userInfo4UserId, pwd, object : NDisposableObserver(this) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var json = jsonObject.optJSONObject("data")
                if (json?.has("isPass")!!) {
                    val isPass = json.optInt("isPass")
                    if (isPass == 1) {
                        val token = json.optString("token") ?: ""
                        if (TextUtils.isEmpty(gesturePwd)) {
                            getToken4Pwd(pwd)
                        } else {
                            var bundle = Bundle()
                            bundle.putInt("SET_TYPE", 1)
                            bundle.putString("SET_TOKEN", token)
                            bundle.putBoolean("SET_STATUS", true)
                            bundle.putBoolean("SET_LOGINANDSET", false)
                            ArouterUtil.navigation("/login/gesturespasswordactivity", bundle)
                        }
                        SoftKeyboardUtil.hideSoftKeyboard(mActivity?.currentFocus)
                        finish()
                    }
                }
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)

            }
        }))
    }

    /**
     * 设置手势密码第一步 获取tokenopen_handPwd_V2
     */
    fun getToken4Pwd(pwd: String) {
        addDisposable(getMainModel().checkLocalPwd(UserDataService.getInstance().userInfo4UserId, pwd, object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var json = jsonObject.optJSONObject("data")
                if (json?.has("isPass")!!) {
                    val isPass = json.optInt("isPass")
                    if (isPass == 1) {
                        val token = json.optString("token") ?: ""
                        val bundle = Bundle()
                        bundle.putInt("SET_TYPE", 0)
                        bundle.putString("SET_TOKEN", token)
                        bundle.putBoolean("SET_STATUS", false)
                        bundle.putBoolean("SET_LOGINANDSET", true)
                        ArouterUtil.navigation("/login/gesturespasswordactivity", bundle)
                        SoftKeyboardUtil.hideSoftKeyboard(mActivity?.currentFocus)
                        finish()
                    }
                }
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
            }

        }))


    }

}