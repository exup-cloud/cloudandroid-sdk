package com.yjkj.chainup.model.api

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Author lianshangljl
 * @Date 2019-09-12-14:10
 * @Email buptjinlong@163.com
 * @description
 */
interface AssetApiService {

    /**
     * 交易账户
     */
    @POST("finance/v5/account_balance")
    fun accountBalance(@Body requestBody: RequestBody): Observable<ResponseBody>


}