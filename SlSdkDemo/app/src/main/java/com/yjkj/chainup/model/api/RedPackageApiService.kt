package com.yjkj.chainup.model.api

import com.yjkj.chainup.net.api.HttpResult
import com.yjkj.chainup.new_version.redpackage.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Author: Bertking
 * @Date：2019-09-03-10:51
 * @Description: 红包接口
 */
interface RedPackageApiService {
    /**
     * 红包的初识信息
     */
    @POST("red_packet/index")
    fun redPackageInitInfo(@Body requestBody: RequestBody): Observable<HttpResult<RedPackageInitInfo>>

    /**
     * 创建红包
     */
    @POST("red_packet/create_new")
    fun createRedPackage(@Body requestBody: RequestBody): Observable<HttpResult<CreatePackageBean>>

    /**
     * 红包的支付回调
     */
    @POST("red_packet/toPay")
    fun pay4redPackage(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 用户发出红包的统计信息
     */
    @POST("red_packet/grant_record")
    fun getGrantRedPackageInfo(@Body requestBody: RequestBody): Observable<HttpResult<GrantRedPackageInfo>>

    /**
     * 用户发出红包列表
     */
    @POST("red_packet/grant_record_list")
    fun grantRedPackageList(@Body requestBody: RequestBody): Observable<HttpResult<GrantRedPackageListBean>>

    /**
     * 用户发出红包的详情
     */
    @POST("red_packet/grant_record_info")
    fun getRedPackageDetail(@Body requestBody: RequestBody): Observable<HttpResult<RedPackageDetailBean>>


    /**
     * 用户收到红包的统计信息
     */
    @POST("red_packet/receive_record")
    fun getReceiveRedPackageInfo(@Body requestBody: RequestBody): Observable<HttpResult<ReceiveRedPackageInfoBean>>


    /**
     * 用户收到红包列表
     */
    @POST("red_packet/receive_record_list")
    fun receiveRedPackageList(@Body requestBody: RequestBody): Observable<HttpResult<ReceiveRedPackageListBean>>

    /***************************************/
    /**
     * 1.红包的初识信息
     */
    @POST("red_packet/index")
    fun redPackageInitInfo1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 2.创建红包
     */
    @POST("red_packet/create_new")
    fun createRedPackage1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 3.红包支付
     */
    @POST("red_packet/toPay")
    fun pay4redPackage1(@Body requestBody: RequestBody): Observable<ResponseBody>


}