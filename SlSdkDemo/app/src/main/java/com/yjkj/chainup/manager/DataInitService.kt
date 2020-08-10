package com.yjkj.chainup.manager

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.yjkj.chainup.db.service.OTCPublicInfoDataService
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.RateDataService
import com.yjkj.chainup.model.model.MainModel
import com.yjkj.chainup.model.model.OTCModel
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.Utils
import org.json.JSONObject


/**
 *
 * @Author: Bertking
 * @Date：2018/12/7-9:02 PM
 * @Description:  数据初始化统一放在此处理
 */
class DataInitService(name: String = "DataInitService") : IntentService(name) {

    val TAG = "RatesService"

    val rate_data_req_type = 1
    val publicinfo_req_type = 2
    val otc_publicinfo_req_type = 3
    var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG, "onCreate()==")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d(TAG, "onStartCommand==")
        isServiceRunning = true

        val isFirst = intent?.getBooleanExtra("isFirst", true)
        if (isFirst != null && isFirst) {
            public_info_v4()
        }
        getRateInfo()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(p0: Intent?) {
        LogUtil.d(TAG, "onHandleIntent==")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = true
    }


    /**
     * 获取"汇率"
     */
    private fun getRateInfo() {
        MainModel().common_rate(MyNDisposableObserver(rate_data_req_type))
    }

    /**
     * public_info_v4  接口数据
     */
    private fun public_info_v4() {
        MainModel().public_info_v4(MyNDisposableObserver(publicinfo_req_type))
    }

    /*
     * OTC public_info 数据
     */
    private fun getOTCPublicInfo() {
        OTCModel().getOTCPublicInfo(MyNDisposableObserver(otc_publicinfo_req_type))
    }

    private inner class MyNDisposableObserver(type: Int) : NDisposableObserver() {

        val reqType = type
        override fun onResponseSuccess(jsonObject: JSONObject) {
            LogUtil.d(TAG, "onResponseSuccess==reqType is $reqType,jsonObject is $jsonObject")
            if (rate_data_req_type == reqType) {
                var data = jsonObject.optJSONObject("data")
                LogUtil.d("RatesService", "RatesService===data is $data")
                if (null != data && data.length() > 0) {
                    var rate = data.optJSONObject("rate")
                    RateDataService.getInstance().saveData(rate)
                    loopReq()
                }
            } else if (publicinfo_req_type == reqType) {
                var data = jsonObject.optJSONObject("data")
                Thread(Runnable {
                    try {
                        val jsonObject = data.optJSONObject("locales")

                        if (null != jsonObject && jsonObject.length() > 0) {
                            val text = jsonObject.optString(LanguageUtil.getSelectLanguage())
                            var jsonFile = Utils.getJSONLastNews(text)
                            PublicInfoDataService.getInstance().saveOnlineText(jsonFile)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }).start()
                PublicInfoDataService.getInstance().saveData(data)
                if (null != data && data.length() > 0) {
                    var contractOpen = data.optString("contractOpen", "0")
                    var isNewContract = data.optString("isNewContract", "1")
                    var otcOpen = data.optString("otcOpen", "0")
                    var rate = data.optJSONObject("rate")
                    RateDataService.getInstance().saveData(rate)
                    if (!TextUtils.isEmpty(contractOpen) && "1" == contractOpen && (TextUtils.isEmpty(isNewContract) || "1" != isNewContract)) {
                        Contract2PublicInfoManager.getContractPublicInfo()
                    }
                    if (!TextUtils.isEmpty(otcOpen) && "1" == otcOpen) {
                        getOTCPublicInfo()
                    }
                }
            } else if (otc_publicinfo_req_type == reqType) {
                var data = jsonObject.optJSONObject("data")
                OTCPublicInfoDataService.getInstance().saveData(data)

            }
        }

    }

    private fun loopReq() {

    }


}
