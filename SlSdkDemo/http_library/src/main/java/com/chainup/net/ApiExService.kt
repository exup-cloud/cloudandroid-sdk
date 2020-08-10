package com.chainup.net

import com.google.gson.JsonObject
import com.chainup.net.api.HttpResult
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ApiExService {


    @POST("app-increment-api/co/trade/income_info")
    fun getLiveInfo(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>
}