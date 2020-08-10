package com.yjkj.chainup.manager

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import com.tencent.mmkv.MMKV
import com.yjkj.chainup.R
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.bean.NetworkLanguage
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NetworkUtils
import org.json.JSONObject
import java.util.*

/**
 * @Author: Bertking
 * @Date：2019-07-18-18:59
 * @Description:
 */
object LanguageUtil {

    private val TAG = LanguageUtil::class.java.simpleName

    private val SELECTED_LANGUAGE = "language_select"

    private val mmkv = MMKV.mmkvWithID("local_language")

    var systemCurrentLocal = Locale.getDefault()


    fun saveLanguage(currentLan: String) {
        mmkv.encode(SELECTED_LANGUAGE, currentLan)
    }


    @JvmStatic
    fun getSelectLanguage(): String {
        var select = mmkv.decodeString(SELECTED_LANGUAGE, "") ?: ""

        if (systemCurrentLocal.language.contains("zh")) {
            return mmkv.decodeString(SELECTED_LANGUAGE, "zh_CN")
        }

        if (systemCurrentLocal.language.contains("en")) {
            return mmkv.decodeString(SELECTED_LANGUAGE, "en_US")
        }

        if (systemCurrentLocal.language.contains("ko")) {
            return mmkv.decodeString(SELECTED_LANGUAGE, "ko_KR")
        }

        if (systemCurrentLocal.language.contains("ja")) {
            return mmkv.decodeString(SELECTED_LANGUAGE, "ja_JP")
        }
        if (!TextUtils.isEmpty(select)) return select

        var lan: String? = ""
        val languageBean = PublicInfoDataService.getInstance().getLan(null)
        if (languageBean != null) {
            lan = languageBean.optString("defLan")
        }

        if (TextUtils.isEmpty(lan)) {
            return mmkv.decodeString(SELECTED_LANGUAGE, "en_US")
        }

        return lan!!
    }

    /**
     * 亏损5%以下：这点亏损小爷还承受得起

    亏损5%-10%：小赌怡情，大赌伤身。

    亏损11%-20%：我还会回来再战的！

    亏损21%-50%：币圈一天，人间一年。

    亏损50%以上：生死看淡，不服就干！

    盈利5%以下：不输就是赢。

    盈利5%-10%：小赚一笔。

    盈利11%-20%：这个水平马马虎虎。

    盈利21-50%：耶稣也阻止不了我，我说的！

    盈利50%以上：老夫从来都是一把唆！
     */
    fun getContractShareText(context: Context, rate: String): String {
        LogUtil.d(TAG, "rate:$rate")
        val negative = rate.contains("-")
        var rates = 0.0
        if (negative) {
            rates = rate.replace("-", "").toDouble()
        } else {
            rates = rate.toDouble()
        }



        return when (rates) {
            in 0.0..5.0 -> {
                if (negative) {
                    context.getString(R.string.common_share_losePrompt5)
                } else {
                    context.getString(R.string.common_share_winPrompt5)
                }
            }

            in 5.0000000000001..10.0 -> {
                if (negative) {
                    context.getString(R.string.common_share_losePrompt10)
                } else {
                    context.getString(R.string.common_share_winPrompt10)
                }
            }

            in 10.0000000000001..20.0 -> {
                if (negative) {
                    context.getString(R.string.common_share_losePrompt20)
                } else {
                    context.getString(R.string.common_share_winPrompt20)

                }
            }

            in 20.0000000000001..50.0 -> {
                if (negative) {
                    context.getString(R.string.common_share_losePrompt50)
                } else {
                    context.getString(R.string.common_share_winPrompt50)
                }
            }

            else -> {
                if (negative) {
                    context.getString(R.string.common_share_losePrompt100)
                } else {
                    context.getString(R.string.common_share_winPrompt100)
                }
            }
        }

    }

    /**
     * 获取多语言文案
     */
    @JvmStatic
    fun getString(context: Context?, key: String): String {
        var netString = getNetString(context, key)
        return if (netString.isBlank()) {
            getLocalString(context, key)
        } else {
            netString.languageTextFormat()
        }

    }

    private fun getLocalString(context: Context?, key: String): String {
        return try {
            var id = context?.resources?.getIdentifier(key, "string", ChainUpApp.appContext.packageName)
                    ?: 0
            if (context == null) {
                ChainUpApp.appContext.getString(id)
            } else {
                context.getString(id)
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            /**
             * 若找不到直接将key显示出来
             */
            key
        }
    }

    /**
     * TODO 具体实现
     */
    private fun getNetString(context: Context?, key: String): String {
        var saveString = NetworkLanguage().getLanguageJson()
        if (saveString == null || saveString.length() == 0) return getLocalString(context, key)
        var netText = saveString.optString(key, "")
        return netText.languageTextFormat()
    }

    fun String.languageTextFormat(): String {
        if (this.contains("%@")) {
            return this.replace("%@", "%s")
        }
        return this
    }
}