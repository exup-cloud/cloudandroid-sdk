package com.yjkj.chainup.model.model

import com.yjkj.chainup.model.api.RedPackageApiService
import com.yjkj.chainup.model.datamanager.BaseDataManager
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody

/**
 * @Author: Bertking
 * @Date：2019-09-04-11:14
 * @Description: 红包具体请求
 */
class RedPackageModel : BaseDataManager() {


    /**
     * 1.红包的初识信息
     */
    fun redPackageInitInfo(consumer: DisposableObserver<ResponseBody>): Disposable? {
        return changeIOToMainThread(httpHelper.getRedPackageUrlService(RedPackageApiService::class.java).redPackageInitInfo1(getBaseReqBody()), consumer)
    }


    /**
     * 2.创建红包
     * @param type 0.普通红包 1.拼手气红包
     * @param coinSymbol 红包币种
     * @param amount 红包额度
     * @param count 红包数量
     * @param tip 红包祝福语
     * @param onlyNew 1.只针对新用户 0.不做限制
     *
     */
    fun createRedPackage(type: Int = 0, coinSymbol: String, amount: String, count: String, tip: String, onlyNew: Int, consumer: DisposableObserver<ResponseBody>): Disposable? {
        var paramMaps = getBaseMaps()
        paramMaps["type"] = type.toString()
        paramMaps["coinSymbol"] = coinSymbol
        paramMaps["amount"] = amount
        paramMaps["tip"] = tip
        paramMaps["count"] = count
        paramMaps["onlyNew"] = onlyNew.toString()
        return changeIOToMainThread(httpHelper.
                getRedPackageUrlService(RedPackageApiService::class.java).
                createRedPackage1(getBaseReqBody(paramMaps)), consumer)
    }


    /**
     * 3.红包的支付回调
     */
    fun payCallback4redPackage(orderNum: String, googleCode: String, smsAuthCode: String, consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        map["orderNum"] = orderNum
        map["googleCode"] = googleCode
        map["smsAuthCode"] = smsAuthCode
        return changeIOToMainThread(httpHelper.getRedPackageUrlService(RedPackageApiService::class.java).pay4redPackage1(getBaseReqBody(map)), consumer)
    }

}