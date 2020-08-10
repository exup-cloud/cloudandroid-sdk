package com.yjkj.chainup.manager


import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.yjkj.chainup.app.ChainUpApp
import org.json.JSONObject

class SymbolManager private constructor() {

    lateinit var sp: SharedPreferences

    companion object {

        const val OTC_LANGUAGE = "OTC_LANGUAGE"

        /**
         * 资金界面的传值
         */
        const val FUND_COINS = "fund_coins"
        /**
         * 买卖方向
         */
        const val BUY_OR_SELL = "buy_or_sell"

        /**
         * 交易界面的币种
         */
        const val TRADE_SYMBOL = "trade_symbol"

        private var mInstance: SymbolManager? = null

        val instance: SymbolManager
            get() {
                if (mInstance == null) {
                    mInstance = SymbolManager()
                }
                return mInstance!!
            }


    }


    init {
        sp = ChainUpApp.appContext.getSharedPreferences("symbols", MODE_PRIVATE)
    }


    /**************NEW ADD***************/

    /**
     * 获取"交易"页面的币种
     */
    fun getTradeSymbol(): String? {
        return sp.getString(TRADE_SYMBOL, "btcusdt")
    }

    /**
     *  保存"交易"页面的币种
     * @param orientation 买卖方向
     */
    fun saveTradeSymbol(symbol: String?, orientation: Int = 0) {
        val edit = sp.edit()
        edit.putString(TRADE_SYMBOL, symbol)
        edit.putInt(BUY_OR_SELL, orientation)
        edit.apply()
    }


    fun saveOTCLanguage(string: String) {
        sp.edit().putString(OTC_LANGUAGE, string).apply()
    }

    fun getOTCLanguage(): String? {
        return sp.getString(OTC_LANGUAGE, "")
    }


    fun saveFundCoins(accountInfo: JSONObject?) {
        if(accountInfo == null) return
        sp.edit().putString(FUND_COINS, accountInfo.toString()).apply()
    }

    /**
     * @param 具体的币种
     * @return 返回对应币种
     */
    fun getFundCoinByName(coinName: String?): JSONObject {
        val info = sp.getString(FUND_COINS, "") ?: ""
        val json = JSONObject(info)
        val allCoinMap = json.optJSONObject("allCoinMap")
        return allCoinMap?.optJSONObject(coinName)?:JSONObject()
    }

}

