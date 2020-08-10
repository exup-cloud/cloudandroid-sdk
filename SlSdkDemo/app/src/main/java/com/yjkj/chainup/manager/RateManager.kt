package com.yjkj.chainup.manager

import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.yjkj.chainup.bean.coin.CoinMapBean
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.RateDataService
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.DecimalUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.StringUtil
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @Author: Bertking
 * @Date：2018/12/8-2:56 PM
 * @Description: 处理汇率的存储&计算and so on...
 */
var TAG = RateManager::class.java.simpleName

class RateManager {

    companion object {
        /**
         * 获取精度
         */
        const val coin_precision = "coin_precision"

        const val coin_fiat_precision = "coin_fiat_precision"

        /**
         * 货币符号
         */
        const val lang_logo = "lang_logo"

        /**
         * 真实货币
         */
        const val lang_coin = "lang_coin"

        /*
         * 法币默认精度
         */
        const val default_precision = 2

        /**
         * 根据法币获取精度
         *  @param isOTC 是否为场外法币，true为场外法币，false为币币法币精度
         */
        fun getFiat4Coin(coin: String?, isOTC: Boolean = true): Int {

            var rate = getRateBylang_coin(coin)
            if (null != rate) {
                var aa = ""
                if (isOTC) {
                    aa = rate?.optString("coin_fiat_precision") ?: ""
                    if (!StringUtil.checkStr(aa)) {
                        aa = rate?.optString("coin_precision") ?: ""
                    }
                } else {
                    aa = rate?.optString("coin_precision") ?: ""
                }
                if (StringUtil.isNumeric(aa)) {
                    return aa!!.toInt()
                }

            }
            /*var lang_logo = rate?.optString(lang_logo)
            var lang_coin = rate?.optString(lang_coin)
            LogUtil.d(TAG,"getFiat4Coin==coin is $coin,lang_logo is $lang_logo,lang_coin is $lang_coin,coin == lang_logo  is ${coin.equals(lang_logo,ignoreCase = true) },coin == lang_coin is ${coin.equals(lang_coin,ignoreCase = true)}")
            if (coin == lang_logo || coin == lang_coin) {
                LogUtil.d(TAG,"getFiat4Coin==coin is $coin,isOTC is $isOTC")
                var aa = ""
                if(isOTC){
                    aa = rate?.optString("coin_fiat_precision")?:""
                    if(!StringUtil.checkStr(aa)){
                        aa = rate?.optString("coin_precision")?:""
                    }
                }else{
                    aa = rate?.optString("coin_precision")?:""
                }
                LogUtil.d(TAG,"getFiat4Coin==coin is $coin,isOTC is $isOTC,aa is $aa")
                if (StringUtil.isNumeric(aa)) {
                    return aa!!.toInt()
                }
            }*/
            return default_precision
        }

        private fun getRateBylang_coin(lang_coin: String?): JSONObject? {
            var coin = lang_coin
            if (!StringUtil.checkStr(coin)) {
                coin = "CNY"
            }

            var rate = PublicInfoDataService.getInstance().getRate(null)
            if (null != rate) {
                var keys = rate.keys()
                while (keys.hasNext()) {
                    var key = keys.next()
                    var value = rate.optJSONObject(key)
                    if (null != value) {
                        var lang_coin = value.optString("lang_coin")
                        var lang_logo = value.optString("lang_logo")
                        if (coin == lang_coin || coin == lang_logo) {
                            return value
                        }
                    }
                }
            }
            return null
        }

        /**
         * @param "ko_KR" , "en_US" , "zh_CN"
         * @return 返回对应的语言的币种汇率
         * @isFiatTrade true为取common/public_info_v4  的rate值
         */

        private fun getRatesByLanguage(isFiatTrade: Boolean = false): JSONObject? {
            val language = LanguageUtil.getSelectLanguage()
            var jsonObject: JSONObject? = null
            if (isFiatTrade) {
                jsonObject = PublicInfoDataService.getInstance().getRate(null)
                jsonObject = jsonObject?.optJSONObject(language)
            } else {
                jsonObject = RateDataService.getInstance().getRate(language)
            }

            //LogUtil.d("RatesService","language is $language , jsonObject is $jsonObject")

            if (null == jsonObject || jsonObject.length() <= 0) {
                jsonObject = RateDataService.getInstance().getRate("en_US")
            }
            return jsonObject
        }


        /**
         * 根据货币符号返回
         */
        fun getRatesByPayCoin(fiat: String?): Int {
            var jsonObject = RateDataService.getInstance().value
            var rate: JSONObject = jsonObject?.optJSONObject("rate") ?: return default_precision

            var it: Iterator<String> = rate.keys()
            var precision = default_precision
            while (it.hasNext()) {
                var key = it.next()
                var value = rate.optJSONObject(key)
                var lang_coin = value?.optString("lang_coin")
                if (lang_coin == fiat) {
                    var coin_precision = value?.optString("coin_precision")
                    if (StringUtil.isNumeric(coin_precision))
                        return coin_precision?.toInt() ?: default_precision
                }
            }
            return default_precision
        }


        /**
         * 根据CoinMap获取计算结果
         */
        fun getCNYByCoinMap(coinMapBean: CoinMapBean?, param: String?): String {
            var split = coinMapBean?.name?.split("/")
            if (null == split || split.size <= 1) {
                return ""
            }
            return getCNYByCoinName(split[1], param)
        }


        /**
         * 根据CoinMap获取计算结果
         */
        fun getCNYByCoinMap(coinMapBean: JSONObject?, param: String?): String {
            var name = coinMapBean?.optString("name")
            if (StringUtil.checkStr(name) && name!!.contains("/")) {
                var split = name.split("/")
                return getCNYByCoinName(split[1], param)
            }
            return ""
        }


        fun getRose(rose: Double): Double {
            return if (rose == 0.0) {
                0.00
            } else {
                return if (rose > 0) rose * 100 - 0.005 else rose * 100 + 0.005
            }
        }

        fun getRoseTrend(rose: String?): Int {
            if (!StringUtil.isNumeric(rose)) return 0
            return BigDecimalUtils.divForDown(BigDecimal(rose).multiply(BigDecimal("100")).toPlainString(), 2).compareTo(BigDecimal("0"))
        }

        /**
         * 涨跌幅
         * 服务器会返回"0,78"数据格式
         */
        fun getRoseText(textView: TextView?, rose: String?) {
            if (!StringUtil.isNumeric(rose)) return
            val roseValue = BigDecimalUtils.divForDown(BigDecimal(rose).multiply(BigDecimal("100")).toPlainString(), 2)
            val compareTo = roseValue.compareTo(BigDecimal("0"))
            when (compareTo) {
                -1 -> {
                    textView?.text = "${roseValue.toPlainString()}%"
                }

                0 -> {
                    textView?.text = roseValue.toPlainString() + "%"
                }

                1 -> {
                    textView?.text = "+${roseValue.toPlainString()}%"
                }
            }
        }

        fun getRoseText4Kline(rose: String?): String {
            if (!StringUtil.isNumeric(rose)) return ""
            var lines = rose ?: ""
            val roseValue = BigDecimalUtils.divForDown(BigDecimal(rose).multiply(BigDecimal("100")).toPlainString(), 2)
            val compareTo = BigDecimalUtils.compareTo(roseValue.toPlainString(), "0")
            when (compareTo) {
                -1 -> {
                    lines = "${roseValue.toPlainString()}%"
                }

                0 -> {
                    lines = roseValue.toPlainString() + "%"
                }

                1 -> {
                    lines = "+${roseValue.toPlainString()}%"
                }
            }
            return lines
        }

        fun getAbsoluteText4Kline(rose: String): String {
            if (!StringUtil.isNumeric(rose)) return ""
            var lines = rose
            val compareTo = BigDecimalUtils.compareTo(rose, "0")
            when (compareTo) {
                -1, 0 -> {
                    lines = rose
                }

                1 -> {
                    lines = "+${rose}"
                }
            }
            return lines
        }


        /**
         * 涨跌幅
         */
        fun getRoseText(rose: Double): String {
            return String.format("%.2f", getRose(rose)) + "%"
        }


        /**
         * @param 币种
         * @return 币种对应的汇率
         * About 0~3ms
         */
        fun getRatesByCoinName(coinName: String?): String {
            val jsonObject = getRatesByLanguage()

            val value = jsonObject?.optString(coinName)

            if (StringUtil.checkStr(value)) {
                return value!!
            }
            return "0.0"
        }


        /**
         * 获取法币的精度
         * @return 货币精度
         * About 0~2ms
         */
        fun getCurrencyPrecision(): Int {
            var jsonObject = getRatesByLanguage()
            if (null != jsonObject) {
                var value = jsonObject.optString(coin_precision)
                if (StringUtil.checkStr(value)) {
                    return value.toInt()
                }
            }
            return default_precision
        }

        /**
         * @Defult $
         * 获取 货币的符号
         * @return 货币符号
         */
        fun getCurrencySign(): String {

            var jsonObject = getRatesByLanguage()
            if (null != jsonObject) {
                var value = jsonObject.optString(lang_logo)
                if (StringUtil.checkStr(value)) {
                    return value
                }
            }
            return "$"
        }


        /**
         * 获取 货币名称(不推荐使用)
         * @return 货币名称
         */
        fun getCurrencyLang(): String {
            var jsonObject = getRatesByLanguage()
            if (null != jsonObject) {
                var value = jsonObject.optString(lang_coin)
                if (StringUtil.checkStr(value)) {
                    return value
                }
            }
            return "USD"
        }


        /**
         * 根据coinName自动和汇率进行计算(推荐使用)
         * @param param 收盘价
         * @param isLogo  false 返回结果为(≈ 具体数字 CNY)
         * @return 返回计算后的结果(形式："≈ ¥ 具体数字" )
         * About  5ms
         */
        fun getCNYByCoinName(coinName: String?, close: String?, isLogo: Boolean = true, isOnlyResult: Boolean = false, precision: Int = -1): String {
            /**
             * 货币名称
             */
            var coinLogo = getCurrencySign()
            /**
             * 货币单位
             */
            var coinLang = getCurrencyLang()


            /**
             * 货币精度
             */
            var precision = if (precision == -1) getCurrencyPrecision() else precision
            /**
             * 汇率
             */
            val rate = getRatesByCoinName(coinName)

            Log.d(TAG, "precison is $precision,coinLogo is $coinLogo,rate is $rate,coinLang is $coinLang")


            if (TextUtils.isEmpty(close) || close == "--") {
                return if (isOnlyResult) {
                    "--"
                } else {
                    if (isLogo) {
                        "≈ $coinLogo--"
                    } else {
                        "≈ --$coinLang"
                    }
                }
            } else {
                val string = BigDecimalUtils.mul(close, rate).toPlainString()
                val intercept = DecimalUtil.cutValueByPrecision(string, precision.toInt())
                Log.d(TAG, "string is $string,intercept is $intercept")

//                BigDecimalUtils.showSNormal(BigDecimalUtils.intercept(string, precision.toInt()).toString())
                return if (isOnlyResult) {
                    intercept
                } else {
                    if (isLogo) {
                        "≈ $coinLogo$intercept"
                    } else {
                        "≈ $intercept$coinLang"
                    }
                }
            }
        }

    }


}


