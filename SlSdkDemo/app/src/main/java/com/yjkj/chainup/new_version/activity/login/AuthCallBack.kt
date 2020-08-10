package com.yjkj.chainup.new_version.activity.login

import android.os.Handler
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat

/**
 * @Author: Bertking
 * @Date：2018/11/21-8:11 PM
 * @Description:
 */

class AuthCallBack(handler: Handler) : FingerprintManagerCompat.AuthenticationCallback() {
    var handler = handler

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        handler.obtainMessage(FingerprintActivity.MSG_AUTH_SUCCESS).sendToTarget()
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpMsgId, helpString)
        handler.obtainMessage(FingerprintActivity.MSG_AUTH_HELP, helpMsgId, 0).sendToTarget()

    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        handler.obtainMessage(FingerprintActivity.MSG_AUTH_FAILED).sendToTarget()
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
        super.onAuthenticationError(errMsgId, errString)
        handler.obtainMessage(FingerprintActivity.MSG_AUTH_ERROR, errMsgId, 0).sendToTarget()
    }

}