package com.yjkj.chainup.model.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * @Author lianshangljl
 * @Date 2020-06-18-18:02
 * @Email buptjinlong@163.com
 * @description
 */
interface SpeedApiService {
    /**
     * 获取接口是否通
     */
    @GET("common/health")
    fun getHealth(@QueryMap map: Map<String, String>): Observable<ResponseBody>
}