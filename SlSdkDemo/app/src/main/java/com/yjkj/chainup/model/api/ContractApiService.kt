package com.yjkj.chainup.model.api

import com.yjkj.chainup.net.api.HttpResult
import com.yjkj.chainup.treaty.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @Author: Bertking
 * @Date：2019-09-03-10:45
 * @Description: 合约接口
 */
interface ContractApiService {

    /**
     * 1. 合约的公共接口
     */
    @POST("/contract_public_info_v2")
    fun getPublicInfo4Contract(@Body requestBody: RequestBody): Observable<HttpResult<ContractPublicInfoBean>>


    /**
     * 2. 获取创建订单初始化信息
     */
    @POST("/init_take_order")
    fun getInitTakeOrderInfo4Contract(@Body requestBody: RequestBody): Observable<HttpResult<InitTakeOrderBean>>


    /**
     * 3. 创建订单
     */
    @POST("/take_order")
    fun takeOrder4Contract(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 4. 取消订单
     */
    @POST("/cancel_order")
    fun cancelOrder4Contract(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 7. 标记价格
     */
    @POST("/tag_price")
    fun getTagPrice4Contract(@Body requestBody: RequestBody): Observable<HttpResult<TagPriceBean>>

    /**
     * 8. 修改杠杆倍数
     */
    @POST("/change_level")
    fun changeLevel4Contract(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 10. 用户持仓信息 ：
     */
    @POST("/user_position")
    fun getPosition4Contract(@Body requestBody: RequestBody): Observable<HttpResult<UserPositionBean>>

    /**
     * 12. 资金划转 ：
     */
    @POST("/capital_transfer")
    fun capitalTransfer4Contract(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 15.风险评估
     */
    @POST("/get_liquidation_rate")
    fun getRiskLiquidationRate(@Body requestBody: RequestBody): Observable<HttpResult<LiquidationRateBean>>

    /**
     *17合约流水
     */
    @POST("/business_transaction_list_v2")
    fun getBusinessTransferList(@Body requestBody: RequestBody): Observable<HttpResult<ContractCashFlowBean>>


    /**********************************合约改版接口**************************************************/

    /**
     * 1. 合约的公共接口
     */
    @POST("/contract_public_info_v2")
    fun getPublicInfo4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 2. 获取创建订单初始化信息
     */
    @POST("/init_take_order")
    fun getInitTakeOrderInfo4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 3. 创建订单(any)
     */
    @POST("/take_order")
    fun takeOrder4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 4. 取消订单(any)
     */
    @POST("/cancel_order")
    fun cancelOrder4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 5. 订单列表(合约当前委托)
     */
    @POST("/order_list_new")
    fun getOrderList4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 7. 标记价格
     */
    @POST("/tag_price")
    fun getTagPrice4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 8. 修改杠杆倍数(any)
     */
    @POST("/change_level")
    fun changeLevel4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 9. 追加保证金(any)
     */
    @POST("/transfer_margin")
    fun transferMargin4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 10. 用户持仓信息(any)
     */
    @POST("/user_position")
    fun getPosition4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 11. 用户未平仓合约 ：
     */
    @POST("/hold_contract_list")
    fun holdContractList4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 12. 资金划转(any)
     */
    @POST("/capital_transfer")
    fun capitalTransfer4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 13. 账户余额信息 ：
     */
    @POST("/account_balance")
    fun getAccountBalance4Contract1(@Body requestBody: RequestBody):Observable<ResponseBody>


    /**
     * 15.风险评估
     */
    @POST("/get_liquidation_rate")
    fun getRiskLiquidationRate1(@Body requestBody: RequestBody):Observable<ResponseBody>


    /**
     * 16 获取历史委托(合约)
     */
    @POST("/order_list_history")
    fun getHistoryEntrust4Contract1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     *17合约流水
     */
    @POST("/business_transaction_list_v2")
    fun getBusinessTransferList1(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     *  资金划转接口
     */
    @POST("app/co_transfer")
    fun doAssetExchange(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

//    /**
//     *  app资产信息
//     */
//    @GET("acocunt/normal")
//    fun doAcocuntNormal(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

}