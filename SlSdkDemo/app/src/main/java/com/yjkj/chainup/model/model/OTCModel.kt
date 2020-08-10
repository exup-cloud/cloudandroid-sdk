package com.yjkj.chainup.model.model

import android.provider.Settings
import android.text.TextUtils
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.model.api.OTCApiService
import com.yjkj.chainup.model.datamanager.BaseDataManager
import com.yjkj.chainup.util.StringUtil
import com.yjkj.chainup.util.SystemUtils
import com.yjkj.chainup.util.UpdateHelper
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @Author lianshangljl
 * @Date 2019-09-04-12:01
 * @Email buptjinlong@163.com
 * @description
 */
class OTCModel : BaseDataManager() {


    /**
     * 获取otc 大接口
     */
    fun getOTCPublicInfo(consumer: DisposableObserver<ResponseBody>): Disposable? {

        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getOTCPublicInfo(getBaseReqBody()), consumer)
    }

    /**
     * 查询用户支付方式
     * @param isOpen 1/0 不填查询全部，填写根据条件查询
     */
    fun getUserPayment4OTC(isOpen: String = "", consumer: DisposableObserver<ResponseBody>): Disposable? {
        val paramMaps = getBaseMaps().apply {
            this["isOpen"] = isOpen
        }
        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getUserPayment4OTC(getBaseReqBody(paramMaps)), consumer)
    }

    /**
     * 首页 广告
     * * @param id 支付方式id
     * @param side 交易类型（出售 OR 求购
     * @param symbol 交易币种
     * @param isBlockTrade 是否是大宗交易，默认0
     * @param payCoin 支付币种
     * @param payment 支付方式
     * @param sort    排序方式
     * @param numberCode 国家数字编码
     * @param pageSize    页大小
     * @param price    页大小
     * @param page 页号
     */
    fun getmainSearch4OTC(side: String = "", symbol: String = "", isBlockTrade: String = "", payCoin: String = "", payment: String = "",
                          sort: String = "", numberCode: String = "", pageSize: Int = 20, page: Int = 1, price: String = "", consumer: DisposableObserver<ResponseBody>): Disposable? {
        val paramMaps = getBaseMaps().apply {
            if (!TextUtils.isEmpty(side)) {
                this["side"] = side
            }
            this["symbol"] = symbol
            if (!TextUtils.isEmpty(isBlockTrade)) {
                this["isBlockTrade"] = isBlockTrade
            }
            if (!TextUtils.isEmpty(payCoin)) {
                this["payCoin"] = payCoin
            }
            if (!TextUtils.isEmpty(payment)) {
                this["payments"] = payment
            }
            if (!TextUtils.isEmpty(sort)) {
                this["sort"] = sort
            }
            if (!TextUtils.isEmpty(numberCode)) {
                this["numberCode"] = numberCode
            }
            if (pageSize != -1) {
                this["pageSize"] = pageSize.toString()
            }
            if (page != -1) {
                this["page"] = page.toString()
            }
            if (!TextUtils.isEmpty(price)) {
                this["price"] = price
            }
        }


        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).mainSearch4OTC(getBaseReqBody(paramMaps)), consumer)
    }

    /**
     * 获取参考价
     */
    fun considerPrice(baseSymbol: String, coinSymbol: String, consumer: DisposableObserver<ResponseBody>): Disposable? {
        val paramMaps = getBaseMaps()

        paramMaps["baseSymbol"] = baseSymbol
        paramMaps["coinSymbol"] = coinSymbol

        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).considerPrice(getBaseReqBody(paramMaps)), consumer)
    }

    /**
     * 购买出售前验证（app4.0）
     */
    fun getValidateAdvert(advertId: String?, advertType: String?, consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            if(StringUtil.checkStr(advertId)){
                this["advertId"] = advertId!!
            }
            if(StringUtil.checkStr(advertType)){
                this["advertType"] = advertType!!
            }
        }

        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getValidateAdvert(getBaseReqBody(map)), consumer)
    }

    private fun getMonitorMap(): TreeMap<String, String> {
        val map = TreeMap<String, String>()
        map["timestamp"] = System.currentTimeMillis().toString()
        return map

    }

    /**
     * 上传信息(biki专)
     */
    fun loginInformation(newToken: String = "", consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getMonitorMap()
        map["org"] = "4be77ac8-7f9d-4940-b438-0203cfad37ca"
        if (!TextUtils.isEmpty(newToken)) {
            map["identity"] = newToken
        }
        if (!TextUtils.isEmpty(Settings.System.getString(ChainUpApp.appContext.contentResolver, Settings.System.ANDROID_ID))) {
            map["device"] = Settings.System.getString(ChainUpApp.appContext.contentResolver, Settings.System.ANDROID_ID)
        }

        map["language"] = SystemUtils.getSystemLanguage()
        map["appVersion"] = UpdateHelper.getLocalVersion(ChainUpApp.appContext).toString()
        map["os"] = "ADNROID"
        map["osVersion"] = SystemUtils.getSystemVersion()
        map["deviceType"] = SystemUtils.getSystemModel()
        map["acceptLanguage"] = SystemUtils.getSystemLanguage()
        map["channel"] = "AppStore"
        map["network"] = SystemUtils.getAPNType(ChainUpApp.appContext)
        var tokenmap = map
        map["token"] = SystemUtils.requestSign(tokenmap, "18w7WMAPMykEx9RwvWWYtAYeuj1sKckJeH")
        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).loginInformation(map), consumer)
    }

    /**
     * 取消广告
     */
    fun cancelWantend(advertId: String = "0", consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["advertId"] = advertId
        }
        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).cancelWantend(getBaseReqBody(map)), consumer)
    }

    /**
     * 发布广告
     */
    fun setWantedSave(coin: String = "", side: String = "", payCoin: String = "", volume: String = "",
                      price: String = "", priceRate: String = "", priceRateType: String = "",
                      minTrade: String = "", maxTrade: String = "", limitTime: String = "", dealVolume: String = "",
                      days: String = "", payments: ArrayList<JSONObject> = arrayListOf(),
                      description: String = "", autoReply: String = "",consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["coin"] = coin
            this["side"] = side
            this["payCoin"] = payCoin
            this["volume"] = volume
            this["price"] = price
            this["priceRate"] = priceRate
            this["priceRateType"] = priceRateType
            this["minTrade"] = minTrade
            this["maxTrade"] = maxTrade
            this["limitTime"] = limitTime
            this["dealVolume"] = dealVolume
            this["days"] = days
            this["description"] = description
            this["autoReply"] = autoReply
            var jsonArray = JSONArray()
            if (payments.size > 0) {
                payments.forEach {
                    jsonArray.put(it)
                }
                this["payments"] = jsonArray.toString()
            } else {
                this["payments"] = ""
            }
        }


        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).setWantedSave(getBaseReqBody(map)),consumer)
    }


    /**
     * 发布前判断
     */
    fun getwantedDetailCheck(consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getwantedDetailCheck(getBaseReqBody(map)), consumer)
    }


    /**
     * 获取服务端的用户自选币对
     * @param uid
     * @param adType 广告类型，不填默认buy
     * @param closeHide 不填默认显示全部，0显示全部，1隐藏关闭广告
     */
    fun getNewPersonalAds(uid: String, adType: String = "", closeHide: String = "", page: String = "1", pageSize: String = "1000", consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["uid"] = uid
            if (!TextUtils.isEmpty(adType)) {
                this["adType"] = adType
            }
            if (!TextUtils.isEmpty(closeHide)) {
                this["closeHide"] = closeHide
            }
            this["page"] = page
            this["pageSize"] = pageSize
        }

        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getNewPersonAds(getBaseReqBody(map)), consumer)
    }

    /**
     * 广告详情
     * @param advertId 广告id
     */
    fun getADDetail4OTC(advertId: String, consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["advertId"] = advertId
        }

        return changeIOToMainThread(httpHelper.getOtcBaseUrlService(OTCApiService::class.java).getADDetail4OTC(getBaseReqBody(map)), consumer)
    }
}