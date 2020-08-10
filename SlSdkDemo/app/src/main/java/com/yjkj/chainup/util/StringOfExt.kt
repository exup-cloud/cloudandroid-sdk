package com.yjkj.chainup.util

import android.content.pm.PackageManager
import android.view.View
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.new_version.view.ComVerifyView
import okio.Buffer
import java.io.EOFException

fun String.numToScalePer(): String {
    return StringBuffer(this).append("%").toString()
}

fun String.totalNumToDigDown(symbol: String = "USDT"): String {
    val coinPrecision = NCoinManager.getCoinShowPrecision(symbol)
    return BigDecimalUtils.divForDown(this, coinPrecision).toPlainString()
}


fun String.verifitionType(): Int {
    return when (this) {
        "1" -> ComVerifyView.GOOGLE
        "2" -> ComVerifyView.MOBILE
        "3" -> ComVerifyView.EMAIL
        "4" -> ComVerifyView.IDCard
        else -> ComVerifyView.GOOGLE
    }
}

fun String.verfitionTypeForPhone(): Int {
    return when (this) {
        "1" -> AppConstant.MOBILE_LOGIN
        "2" -> AppConstant.MOBILE_LOGIN
        "3" -> AppConstant.EMAIL_LOGIN
        else -> AppConstant.MOBILE_LOGIN
    }
}

fun String.verfitionTypeCheck(): String {
    return when (this) {
        "1" -> "googleCode"
        "2" -> "smsCode"
        "3" -> "emailCode"
        "4" -> "idCardCode"
        else -> ""
    }
}

fun String.verfitionTypeHint(): String {
    return when (this) {
        "1" -> "common_tip_googleAuth"
        "2" -> "personal_tip_inputPhoneCode"
        "3" -> "personal_tip_inputMailCode"
        "4" -> "personal_tip_inputIdnumber"
        else -> ""
    }
}

fun Buffer.isProbablyUtf8Of(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}

fun Boolean.pushOpenStatus(): String {
    return LanguageUtil.getString(ChainUpApp.appContext, when (this) {
        true -> "personal_text_safeSettingOpen"
        else -> "personal_text_safeSettingOff"
    })
}

fun Boolean.visiable(): Int {
    return when (this) {
        true -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

fun Boolean.visiableOrGone(): Int {
    return when (this) {
        true -> View.VISIBLE
        else -> View.GONE
    }
}

fun String.getHostByUrl(): String {
    val host = "https://" + this.substring(this.indexOf(".") + 1, this.length - 1)
    return host
}

fun IntArray.permissionIsGranted(): Boolean {
    if (this.isNotEmpty()) {
        this.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    return false
}