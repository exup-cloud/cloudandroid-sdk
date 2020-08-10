package com.yjkj.chainup.model.datamanager

import com.yjkj.chainup.BuildConfig
import com.yjkj.chainup.model.NDataHandler
import com.yjkj.chainup.net_new.HttpHelper
import com.yjkj.chainup.net_new.HttpParams
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

open class BaseDataManager {

    var httpHelper: HttpHelper

    init {
        this.httpHelper = HttpHelper.instance
    }

    protected fun <T> changeIOToMainThread(observable: Observable<T>?, consumer: DisposableObserver<T>?): Disposable? {
        if (observable == null || consumer == null) {
            if (BuildConfig.DEBUG) {
                throw NullPointerException("consumer or observable not be null")
            }
            return null
        }
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(consumer)
    }


    fun getBaseReqBody(maps : TreeMap<String, String>?=null) : RequestBody{
        var paramMaps = maps
        if(null==maps || maps.isEmpty()){
            paramMaps = HttpParams.getInstance(0).build()
        }
        var aa = NDataHandler.encryptParams(paramMaps!!)
        return getReqBody(aa)
    }

    fun getBaseMaps() : TreeMap<String, String>{
        var paramMaps = HttpParams.getInstance(0).build()
        return paramMaps
    }

    /*
     * 参数值转为json串
     */
    private fun getReqBody(mapParams: Map<String, String>): RequestBody {
        val json = JSONObject(mapParams)
        return json.toString().toRequestBody("application/json;charset=UTF-8".toMediaTypeOrNull())
    }
}
