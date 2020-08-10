package com.yjkj.chainup.manager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yjkj.chainup.bean.coin.CoinBean
import com.yjkj.chainup.bean.coin.CoinMapBean
import com.yjkj.chainup.db.service.PublicInfoDataService


/**
 * @Author: Bertking
 * @Date：2019-06-29-14:14
 * @Description:
 */
class DataManager {

    val TAG = DataManager::class.java.simpleName

    companion object {

        /**
         * 3ms todo 此处稍后需取消CoinMapBean的依赖
         */
        fun getCoinMapBySymbol(symbol: String?): CoinMapBean {

            var symbolObj = NCoinManager.getSymbolObj(symbol)

            if(null==symbolObj || symbolObj.length()<=0){
                return CoinMapBean()
            }
            val type = object : TypeToken<CoinMapBean>() {
            }.type
            return Gson().fromJson(symbolObj.toString(), type)
        }

        /**
         * 获取showMarket 或者CoinName
         */
        @JvmStatic
        fun getShowMarket(market: String?): String {
            val query = getCoinsFromDB()
            var coinName = market
            for (num in query) {
                if (num.name == market) {
                    coinName = if (num.anotherName.isNotEmpty()) num.anotherName else num.name
                    break
                }
            }
            if (null == coinName) {
                coinName = ""
            }
            return coinName
        }

        /**
         *  返回coinList 数据
         */
        fun getCoinsFromDB(isotcOpen : Boolean = false): ArrayList<CoinBean> {
            var coinList = PublicInfoDataService.getInstance().getCoinList(null)
            var list = ArrayList<CoinBean>()
            if(null!=coinList && coinList.length()>0){
                var keys = coinList.keys()
                while (keys.hasNext()){
                    var value = coinList.optJSONObject(keys.next())
                    val type = object : TypeToken<CoinBean>() {}.type
                    if(isotcOpen){
                        if(1==value.optInt("otcOpen")){
                            list.add(Gson().fromJson(value?.toString(), type))
                        }
                    }else{
                        list.add(Gson().fromJson(value?.toString(), type))
                    }

                }
                list.sortBy { it.sort }
            }
            return list
        }

        /**
         * About 1ms
         * @param coinName 币种名称
         * @return 对应的币种
         */
        fun getCoinByName(coinName: String?): CoinBean? {
            var coinObj = NCoinManager.getCoinObj(coinName)
            val type = object : TypeToken<CoinBean>() {
            }.type
            return Gson().fromJson(coinObj?.toString(), type)
        }

    }
}
