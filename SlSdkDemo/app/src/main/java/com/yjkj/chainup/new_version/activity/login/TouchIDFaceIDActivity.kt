package com.yjkj.chainup.new_version.activity.login

import android.annotation.SuppressLint
import android.app.Activity
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.JsonObject
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.dialog.DialogUtil
import com.yjkj.chainup.util.CryptoObjectHelper
import com.yjkj.chainup.util.DisplayUtil
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_touch_idface_id.*
import org.json.JSONObject

/**
 * @author Bertking
 * @date 2018-11-16
 * @description 指纹识别 & 面部识别(TODO)
 */
@Route(path = "/login/touchidfaceidactivity")
class TouchIDFaceIDActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_touch_idface_id
    }

    var type = FINGERPRINT
    var isFirstLogin = false

    lateinit var fingerprintManager: FingerprintManagerCompat
    var cancellationSignal: CancellationSignal? = null
    var authCallBack: AuthCallBack? = null
    var tDialog: TDialog? = null


    var handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            when (msg?.what) {
                FingerprintActivity.MSG_AUTH_SUCCESS -> {
//                    tv_result.text = "指纹识别成功"
                    ToastUtils.showToast( LanguageUtil.getString(mActivity,"open_suc"))
                    tDialog?.dismiss()
                    tDialog?.dismissAllowingStateLoss()
                    /**
                     * 执行 快捷登录接口
                     */
                    if (isFirstLogin) {
                        /**
                         * 这里是第一次登录 从指纹设置中进入
                         */
                        LoginManager.getInstance().saveFingerprint(1)
                        finish()
                    } else {
                        /**
                         * 指纹登录中进入
                         */
                        val loginInfo = LoginManager.getInstance().loginInfo ?: return
                        val account = loginInfo.account
                        val loginPwd = loginInfo.loginPwd
                        var userInfoData = UserDataService.getInstance().userData
                        var countryCode = userInfoData.optString("countryCode")
                        if (userInfoData != null && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(loginPwd)) {
                            quickLogin(countryCode, account, loginPwd)
                        }
                    }

                }

                FingerprintActivity.MSG_AUTH_FAILED -> {
                    ToastUtils.showToast( LanguageUtil.getString(mActivity,"open_failure"))
                }

                FingerprintActivity.MSG_AUTH_ERROR -> {
//                    tv_result.text = "授权失败"
                    handlerErrorCode(msg.arg1)
                }

                FingerprintActivity.MSG_AUTH_HELP -> {
//                    tv_result.text = "授权帮助"
                    handleHelpCode(msg.arg1)
                }


            }
        }
    }

    companion object {

        const val TYPE = "type"

        /**
         * 指纹识别
         */
        const val FINGERPRINT = 0


        /**
         * 面部识别
         */
        const val FACEID = 1

        const val ISFIRSTLOGIN = "is_first_login"

        const val PATH_KEY = "/login/touchidfaceidactivity"

        fun enter2(type: Int = FINGERPRINT, status: Boolean) {
            val bundle = Bundle()
            bundle.putInt(TYPE, type)
            bundle.putBoolean(ISFIRSTLOGIN, status)
            ArouterUtil.navigation(PATH_KEY, bundle)
        }

    }

    private fun initFingerPrintManager() {
        fingerprintManager = FingerprintManagerCompat.from(this)
        if (!fingerprintManager.isHardwareDetected) {
            DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"hardware_not_recognition"), isSuc = false)
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"no_fingerprints_were_entered"), isSuc = false)
        } else {
            try {
                authCallBack = AuthCallBack(handler)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var cryptoObjectHelper = CryptoObjectHelper()
        if (cancellationSignal == null) {
            cancellationSignal = CancellationSignal()
        }

        if (authCallBack == null) return
        try {
            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, cancellationSignal
                    ?: return, authCallBack
                    ?: return, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
        setTextContent()
    }
    fun setTextContent() {
        ib_back?.text = LanguageUtil.getString(this,"common_text_btnCancel")
        btn_account_login?.text = LanguageUtil.getString(this,"login_action_otherAccount")
    }


    override fun initView() {
        type = intent?.getIntExtra(TYPE, FINGERPRINT) ?: FINGERPRINT
        isFirstLogin = intent?.getBooleanExtra(ISFIRSTLOGIN, false) ?: false
        initFingerPrintManager()


        tDialog = DialogUtil.showFingerprintOrFaceIDDialog(this)


        var userinfo = UserDataService.getInstance().userData
        var mobileNumber = userinfo.optString("mobileNumber")
        var email = userinfo.optString("email")
        when (type) {
            FINGERPRINT -> {
                tv_recognition_title.text =  LanguageUtil.getString(mActivity,"fingerprint_recognition")
                tv_recognition_tips.text =  LanguageUtil.getString(mActivity,"tap_to_fingerprint")
                iv_recognition.setImageResource(R.drawable.login_fingerprintlogin)
            }

            FACEID -> {
                tv_recognition_title.text =  LanguageUtil.getString(mActivity,"face_recongnition")
                tv_recognition_tips.text =  LanguageUtil.getString(mActivity,"tap_to_face")
                iv_recognition.setImageResource(R.drawable.ic_face_recognition)
            }
        }
        if (userinfo != null) {
            if (mobileNumber.isNotEmpty()) {
                tv_recognition_title.text = mobileNumber
            } else if (email.isNotEmpty()) {
                tv_recognition_title.text = email
            }
        }
        initOnClicker()
    }


    override fun onResume() {
        super.onResume()
        if (cancellationSignal == null) {
            var cryptoObjectHelper = CryptoObjectHelper()
            cancellationSignal = CancellationSignal()
            if (authCallBack == null) return
            try {
                fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, cancellationSignal
                        ?: return, authCallBack
                        ?: return, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onPause() {
        super.onPause()
        if (cancellationSignal != null) {
            cancellationSignal?.cancel()
            cancellationSignal = null
        }
    }


    private fun initOnClicker() {
        ib_back.setOnClickListener {
            cancellationSignal?.cancel()
            finish()
        }


        iv_recognition.setOnClickListener {
            when (type) {
                FINGERPRINT -> {
                    var cryptoObjectHelper = CryptoObjectHelper()
                    if (cancellationSignal == null) {
                        cancellationSignal = CancellationSignal()
                    }

                    try {
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, cancellationSignal
                                ?: return@setOnClickListener, authCallBack
                                ?: return@setOnClickListener, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    tDialog?.show()
                }

                FACEID -> {
//                    DialogUtil.showFingerprintOrFaceIDDialog(context, title = "", tips = "")
                }
            }
        }

        tv_title_touch.setOnClickListener {
            when (type) {
                FINGERPRINT -> {
                    var cryptoObjectHelper = CryptoObjectHelper()
                    if (cancellationSignal == null) {
                        cancellationSignal = CancellationSignal()
                    }

                    try {
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, cancellationSignal
                                ?: return@setOnClickListener, authCallBack
                                ?: return@setOnClickListener, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    tDialog?.show()
                }

                FACEID -> {
//                    DialogUtil.showFingerprintOrFaceIDDialog(context, title = "", tips = "")
                }
            }
        }

        /**
         * 账号登录
         */
        btn_account_login.setOnClickListener {
            finish()
            cancellationSignal?.cancel()
            ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
        }
    }


    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"fingerprint_complete"), isSuc = false)
            }
            /**
             * 指纹不准(dirty 脏的)
             */
            FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"fingerprints_too_fuzzy"), isSuc = false)
            }
            /**
             * 指纹不准或者传感器有问题
             */
            FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"fingerprint_blur_or_sensor_blur"), isSuc = false)
            }
            /**
             * 指纹不全
             */
            FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"Incomplete_fingerprint"), isSuc = false)

            }
            /**
             * 太快导致指纹识别不出
             */
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"moving_too_fast"), isSuc = false)
            }
            /**
             * 太慢导致指纹识别不出
             */
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"cannot_read_fingerprint"), isSuc = false)
            }
        }
    }


    fun handlerErrorCode(code: Int) {
        when (code) {
            /**
             * 取消
             */
            FingerprintManager.FINGERPRINT_ERROR_CANCELED -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"defingerprinting"), isSuc = false)
            }

            /**
             * 不可用
             */
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"hardware_error"), isSuc = false)
            }

            /**
             * 错误5次,被锁定
             */
            FingerprintManager.FINGERPRINT_ERROR_LOCKOUT -> {
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"error_more_5"), isSuc = false)
                finish()
            }

            /**
             * 支持但设置的指纹不全
             */
            FingerprintManager.FINGERPRINT_ERROR_NO_SPACE -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"fingerprint_incomplete"), isSuc = false)
            }

            /**
             * 识别超时
             */
            FingerprintManager.FINGERPRINT_ERROR_TIMEOUT -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"fingerprint_identification_timeout"), isSuc = false)
            }

            /**
             * 传感器不能处理当前指纹
             */
            FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                DisplayUtil.showSnackBar(window?.decorView,  LanguageUtil.getString(mActivity,"cannot_handle_the_current_fingerprint"), isSuc = false)
            }


        }
    }


    private fun quickLogin(country: String,
                           mobile: String,
                           loginPword: String) {
        showLoadingDialog()
        HttpClient.instance.quickLogin(countryCode = country,
                mobileNumber = mobile,
                loginPword = loginPword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<JsonObject>() {
                    override fun onHandleSuccess(jsonObject: JsonObject?) {
                        closeLoadingDialog()
                        val token = jsonObject!!.get("token").asString
                        UserDataService.getInstance().saveToken(token)
                        HttpClient.instance.setToken(token)
                        ArouterUtil.refreshWebview()
                        getUserInfo()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        super.onHandleError(code, msg)
                        NToastUtil.showTopToast(false, msg)
                        if (code == ParamConstant.QUICK_LOGIN_FAILURE) {
                            ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
                            finish()
                        }
                        closeLoadingDialog()

                    }
                })


    }


    /**
     * 获取用户信息
     */
    private fun getUserInfo() {

        addDisposable(getMainModel().getUserInfo(object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var json = jsonObject.optJSONObject("data")
                var gesturePwd = json.optString("gesturePwd")
                UserDataService.getInstance().saveData(json)
                if (!TextUtils.isEmpty(gesturePwd) && TextUtils.isEmpty(UserDataService.getInstance().gesturePass)) {
                    UserDataService.getInstance().saveGesturePass(gesturePwd)
                }
            }
        }))


    }
}
