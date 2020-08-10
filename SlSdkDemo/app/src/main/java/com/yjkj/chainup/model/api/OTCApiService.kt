package com.yjkj.chainup.model.api

import com.google.gson.JsonObject
import com.yjkj.chainup.bean.PersonAdsBean
import com.yjkj.chainup.bean.UserInfo4OTC
import com.yjkj.chainup.net.api.HttpResult
import com.yjkj.chainup.net_new.NetUrl.biki_monitor_appUrl
import com.yjkj.chainup.new_version.bean.BlackListData
import com.yjkj.chainup.new_version.bean.OTCIMMessageBean
import com.yjkj.chainup.new_version.bean.OTCOrderDetailBean
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @Author: Bertking
 * @Date：2019-09-03-11:04
 * @Description: 场外交易(OTC)接口
 */
interface OTCApiService {
    /**
     *
     * 广告详情
     */
    @POST("otc/v4/wanted_detail")
    fun getADDetail4OTC(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     *  OTC数据
     */
    @POST("otc/public_info")
    fun getOTCPublicInfo(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     *
     * 订单详情数据
     */
    @POST("v4/otc/order_detail")
    fun getOrderDetail4OTC(@Body requestBody: RequestBody): Observable<HttpResult<OTCOrderDetailBean>>

    /**
     * 取消申诉
     */
    @POST("otc/complain_cancel")
    fun cancelComplain4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 取消订单
     */
    @POST("otc/order_cancel")
    fun cancelOrder4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 针对于卖家
     * 确认打币
     */
    @POST("otc/confirm_order")
    fun confirmOrder2Seller4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 针对于买家
     * 确认支付
     */
    @POST("v4/otc/order_payed")
    fun confirmPay2Buyer4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 申诉修改订单状态
     */
    @POST("otc/complain_order")
    fun complain2changeOrderState4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 生成购买的订单(Step 3)
     */
    @POST("v4/otc/buy_order_save")
    fun buyOrderEnd4OTC(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 生成出售的订单(Step 3)
     */
    @POST("v4/otc/sell_order_save")
    fun sellOrderEnd4OTC(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>


    /**
     * 首页 广告
     */
    @POST("otc/search")
    fun mainSearch4OTC(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 获取聊天历史记录
     */
    @POST("chatMsg/message")
    fun gethistoryMessage(@Body requestBody: RequestBody): Observable<HttpResult<ArrayList<OTCIMMessageBean>>>


    /**
     * 查询用户支付方式
     */

    @POST("otc/payment/find")
    fun getUserPayment4OTC(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 新增支付方式
     */
    @POST("otc/payment/add")
    fun addPayment4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 删除支付方式
     */
    @POST("otc/payment/delete")
    fun removePayment4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 支付方式开关设置
     */
    @POST("otc/payment/open")
    fun operatePayment4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 支付方式开关设置
     */
    @POST("otc/payment/update")
    fun updatePayment4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /********OTC支付方式******END******
     *            *
     *  *                   *
     *
     *  *         *          *
     * ********************************/


    /**
     * 获取名单
     */
    @POST("otc/person_relationship")
    fun getRelationShip4OTC(@Body requestBody: RequestBody): Observable<HttpResult<BlackListData>>

    /**
     * 屏蔽 用户 加入黑名单
     */
    @POST("otc/user_contacts")
    fun userContacts4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /***
     * 移除黑名单
     */
    @POST("otc/user_contacts_remove")
    fun removeRelationFromBlack4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 撤销提现(4.0app)
     */
    @POST("otc/consider_price_v4")
    fun considerPrice(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 个人主页用户基本信息显示
     */
    @POST("otc/person_home_page")
    fun getPerson4otc(@Body requestBody: RequestBody): Observable<HttpResult<UserInfo4OTC>>


    /**
     * 场外上架 下方 购买广告列表
     */
    @POST("otc/v4/person_ads")
    fun getPersonAds(@Body requestBody: RequestBody): Observable<HttpResult<PersonAdsBean>>


    /**
     * 购买出售前验证（app4.0）
     */
    @POST("otc/validateAdvert_v4")
    fun getValidateAdvert(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 上传信息(biki专)
     */
    @GET(biki_monitor_appUrl)
    fun loginInformation(@QueryMap map: Map<String, String>): Observable<ResponseBody>


    /**
     * 设置资金密码
     */
    @POST("otc/capital_password/set")
    fun capitalPassword4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 重置资金密码
     */
    @POST("otc/v4/capital_password/reset")
    fun capitalPasswordReset4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /****************************** otc 新加 ********************/
    @POST("otc/v4/person_ads")
    fun getNewPersonAds(@Body requestBody: RequestBody): Observable<ResponseBody>

    @POST("otc/wanted_save")
    fun setWantedSave(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 取消广告
     */
    @POST("otc/close_wanted")
    fun cancelWantend(@Body requestBody: RequestBody): Observable<ResponseBody>

    @POST("otc/v4/wanted_detail_check")
    fun getwantedDetailCheck(@Body requestBody: RequestBody): Observable<ResponseBody>


}