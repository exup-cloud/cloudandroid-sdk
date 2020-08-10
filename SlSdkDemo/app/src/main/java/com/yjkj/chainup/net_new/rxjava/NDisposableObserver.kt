package com.yjkj.chainup.net_new.rxjava

import android.app.Activity
import android.os.Bundle
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.text.TextUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.JSONUtil
import com.yjkj.chainup.net_new.NLoadingDialog
import com.yjkj.chainup.new_version.activity.login.TouchIDFaceIDActivity
import com.yjkj.chainup.util.ContextUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NToastUtil
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 *

 * @Description:

 * @Author:         wanghao

 * @CreateDate:     2019-08-06 19:20

 * @UpdateUser:     wanghao

 * @UpdateDate:     2019-08-06 19:20

 * @UpdateRemark:   更新说明

 */
abstract class NDisposableObserver : DisposableObserver<ResponseBody> {

    private val TAG = "MyDisposableObserver"

    private val server_errorCode = -2
    private val net_errorCode = -1

    constructor(showToast: Boolean = false) {
        isShowToast = showToast
    }

    var mActivity: Activity? = null
    private var isShowToast = false

    constructor(activity: Activity?, showToast: Boolean = false) {
        this.mActivity = activity
        if (showToast != null) {
            this.isShowToast = showToast
        }

        this.showLoadingDialog()
    }

    /*
     * toast展示开关控制，默认展示
     */
    fun setShowToast(isShowToast: Boolean) {
        this.isShowToast = isShowToast
    }

    override fun onNext(responseBody: ResponseBody) {
        LogUtil.d(TAG, "MyDisposableObserver==onNext==t is " + responseBody)
        closeLoadingDialog()
        var jsonObj = JSONUtil.parse(responseBody, isShowToast)
        if (null != jsonObj) {
            val code = jsonObj.optString("code")

            if ("0".equals(code, true)) {
                onResponseSuccess(jsonObj)
            } else {
                var msg = jsonObj.optString("msg")
                onResponseFailure(jsonObj.optInt("code"), msg)
            }
        } else {
            onResponseFailure(-1, "json is error")
        }
    }

    override fun onComplete() {
        closeLoadingDialog()
    }

    override fun onError(e: Throwable) {
        closeLoadingDialog()

        if (e is HttpException) {
            val code = e.code()
            val message = e.message
            onResponseFailure(code, message)
        } else if (e is SocketTimeoutException) {
            onResponseFailure(net_errorCode, ContextUtil.getString(R.string.network_connection_is_out_of_time))
        } else if (e is IOException) {
            onResponseFailure(net_errorCode, ContextUtil.getString(R.string.network_is_exception))
        } else {
            //server Error
            onResponseFailure(net_errorCode, ContextUtil.getString(R.string.Server_error_please_try_again_later))
        }
    }

    abstract fun onResponseSuccess(jsonObject: JSONObject)

    /*
     * 公共错误请求码，可在此处理
     */
    open fun onResponseFailure(code: Int, msg: String?) {
        if (isShowToast) {
            NToastUtil.showTopToast(false, msg)
        }
        if (code == 10021 || code == 10002 || code == 3 || code == ParamConstant.QUICK_LOGIN_FAILURE) {
            UserDataService.getInstance().clearToken()
            val userinfo = UserDataService.getInstance().userData
            if (null == userinfo) {
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
            } else {
                val fingerprintManager = FingerprintManagerCompat.from(ChainUpApp.appContext)
                if (fingerprintManager.isHardwareDetected) {
                    /**
                     * 判断是否输入指纹
                     */
                    if (fingerprintManager.hasEnrolledFingerprints() && LoginManager.getInstance().fingerprint == 1) {
                        val bundle = Bundle()
                        bundle.putInt("type", TouchIDFaceIDActivity.FINGERPRINT)
                        bundle.putBoolean("is_first_login", false)
                        ArouterUtil.navigation("/login/touchidfaceidactivity", bundle)
                    } else if (!TextUtils.isEmpty(UserDataService.getInstance().gesturePass) || !TextUtils.isEmpty(UserDataService.getInstance().gesturePwd)) {
                        val bundle = Bundle()
                        bundle.putInt("SET_TYPE", 1)
                        bundle.putString("SET_TOKEN", "")
                        bundle.putBoolean("SET_STATUS", true)
                        bundle.putBoolean("SET_LOGINANDSET", false)
                        ArouterUtil.navigation("/login/gesturespasswordactivity", bundle)
                    } else {
                        ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
                    }
                } else if (!TextUtils.isEmpty(UserDataService.getInstance().gesturePass) || !TextUtils.isEmpty(UserDataService.getInstance().gesturePwd)) {

                    val bundle = Bundle()
                    bundle.putInt("SET_TYPE", 1)
                    bundle.putString("SET_TOKEN", "")
                    bundle.putBoolean("SET_STATUS", true)
                    bundle.putBoolean("SET_LOGINANDSET", false)
                    ArouterUtil.navigation("/login/gesturespasswordactivity", bundle)
                } else {
                    ArouterUtil.navigation("/login/NewVersionLoginActivity", null)
                }
            }

            //            Intent intent = new Intent(ChainUpApp.appContext, NewVersionLoginActivity.class);
            //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            ChainUpApp.appContext.startActivity(intent);

        }
    }


    private var mLoadingDialog: NLoadingDialog? = null
    private fun showLoadingDialog() {
        closeLoadingDialog()
        if (null != mActivity) {
            if (null == mLoadingDialog) {
                mLoadingDialog = NLoadingDialog(mActivity)
            }
            mLoadingDialog!!.showLoadingDialog()
        }
    }

    private fun closeLoadingDialog() {
        if (null != mActivity) {
            if (null != mLoadingDialog) {
                mLoadingDialog!!.closeLoadingDialog()
                mLoadingDialog = null
            }
        }
    }

}