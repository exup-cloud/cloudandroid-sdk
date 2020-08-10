package com.yjkj.chainup.net.api

import com.google.gson.JsonObject
import com.yjkj.chainup.bean.*
import com.yjkj.chainup.bean.address.AddressBean
import com.yjkj.chainup.bean.coin.InitInfo
import com.yjkj.chainup.bean.coin.RateBean
import com.yjkj.chainup.bean.dev.MessageBean
import com.yjkj.chainup.bean.dev.NoticeBean
import com.yjkj.chainup.bean.fund.CashFlowBean
import com.yjkj.chainup.new_version.bean.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author Bertking
 * @2018-6-11
 * TODO :
 * 1. 登录接口目前后端是不区分(email,mobile)
 */
interface ApiService {

    /**
     * 手机注册
     */
    @POST("user/reg_mobile")
    fun regMobile(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 邮箱注册
     */
    @POST("user/reg_email")
    fun regEmail(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 手机短信验证码
     */
    @POST("v4/common/smsValidCode")
    fun sendMobileVerifyCode(@Body requestBody: RequestBody): Observable<HttpResult<String>>


    /**
     * 邮箱验证码
     */
    @POST("v4/common/emailValidCode")
    fun sendEmailVerifyCode(@Body requestBody: RequestBody): Observable<HttpResult<String>>

    /**
     * 邮箱登录
     */
    @POST("user/login_in")
    fun loginByEmail(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 获取公共协议
     */
    @GET("common/terms")
    fun getCommonTerms(@QueryMap map: Map<String, String>): Observable<HttpResult<List<TermsBean>>>



    /**
     * 忘记密码 step 2
     */
    @POST("user/reset_password_step_two")
    fun resetPwdStep2(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 忘记密码 step 3
     */
    @POST("user/reset_password_step_three")
    fun resetPwdStep3(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 修改登录密码
     */
    @POST("user/password_update")
    fun changeLoginPwd(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 获取初始信息
     */
    @POST("common/public_info")
    fun getInitInfo(@Body requestBody: RequestBody): Observable<HttpResult<InitInfo>>


    /**
     * 查询汇率
     */
    @POST("common/rate")
    fun getRate(@Body requestBody: RequestBody): Observable<HttpResult<RateBean>>


    /**
     * 修改昵称
     */
    @POST("user/nickname_update")
    fun editNickname(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 退出登录
     */
    @POST("user/login_out")
    fun logout(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * google验证前校验:获取Google的key
     */
    @POST("user/toopen_google_authenticator")
    fun getGoogleKey(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>


    /**
     * 绑定谷歌认证
     */
    @POST("user/google_verify")
    fun bindGoogleVerify(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 关闭Google认证
     */
    @POST("user/close_google_verify")
    fun unbindGoogleVerify(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 关闭手机认证
     */
    @POST("user/close_mobile_verify")
    fun unbindMobileVerify(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 关闭手机认证
     */
    @POST("user/open_mobile_verify")
    fun openMobileVerify(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /************认证相关*****END*********/


    /**
     * 修改手机号
     */
    @POST("user/mobile_update")
    fun changeMobile(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 修改邮箱
     */
    @POST("user/email_update")
    fun changeEmail(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 绑定邮箱
     */
    @POST("user/email_bind_save")
    fun bindEmail(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 绑定手机
     */
    @POST("user/mobile_bind_save")
    fun bindMobile(@Body requestBody: RequestBody): Observable<HttpResult<Any>>



    /**
     * 获取充值地址
     */
    @POST("finance/get_charge_address")
    fun getChargeAddress(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>


    /**
     * 提现操作
     */
    @POST("finance/do_withdraw_v4")
    fun doWithdraw(@Body requestBody: RequestBody): Observable<HttpResult<AuthBean>>


    /**
     * 钱包地址列表 addr/address_list
     */
    @POST("addr/address_list")
    fun getAddressList(@Body requestBody: RequestBody): Observable<HttpResult<AddressBean>>

    /**
     * 添加地址 addr/add_withdraw_addr
     */
    @POST("addr/add_withdraw_addr_v4")
    fun addWithdrawAddress(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 删除钱包地址 addr/delete_withdraw_addr
     */
    @POST("addr/delete_withdraw_addr")
    fun delWithdrawAddress(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 获取消息(消息中心)
     */
    @POST("message/user_message")
    fun getMessages(@Body requestBody: RequestBody): Observable<HttpResult<MessageBean>>

    /**
     * 获取公告
     */
    @POST("notice/notice_info_list")
    fun getNotices(@Body requestBody: RequestBody): Observable<HttpResult<NoticeBean>>

    /**
     * 上传照片
     * 使用form表单提交
     */
    @POST("common/upload_img")
    fun uploadImg(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 实名认证
     */
    @POST("user/v4/auth_realname")
    fun authVerify(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 开启手势密码:第一步
     */
    @POST("user/open_handPwd_one")
    fun openHandPwd(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>



    /**
     * 关闭手势密码
     */
    @POST("user/close_handPwd_verify")
    fun closeHandPwd(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 手势密码登录
     */
    @POST("user/login_handPwd")
    fun handPwdLogin(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 帮助中心
     */
    @POST("cms/list")
    fun getHelpCenterList(@Body requestBody: RequestBody): Observable<HttpResult<ArrayList<HelpCenterBean>>>

    /**
     * 获取kv配置
     */
    @GET("common/kv")
    fun getCommonKV(@QueryMap map: Map<String, String>): Observable<HttpResult<JsonObject>>

    /**
     * 清空用户手势密码
     */
    @POST("user/clean_handPwd")
    fun cleanGesturePwd(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 关于我们
     */
    @GET("common/aboutUS")
    fun getAboutUs(@QueryMap map: Map<String, String>): Observable<HttpResult<ArrayList<AboutUSBean>>>


    @GET("api/getVersion")
    fun checkVersion(@Query("time") time: String): Observable<HttpResult<VersionData>>

    @FormUrlEncoded
    @POST("api/rq_info_add_submit")
    fun feedback(@FieldMap map: Map<String, String>): Observable<HttpResult<String>>

    /**
     * 上传二维码
     */
    @POST("common/upload_img_base64")
    fun uploadImg4OTC(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>


    /**
     * 场外资金划转
     */
    @POST("finance/otc_transfer")
    fun transher4OTC(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 划转时获取资金
     */
    @POST("finance/get_account_by_coin")
    fun accountGetCoin4OTC(@Body requestBody: RequestBody): Observable<HttpResult<OTCGetCoinBean>>

    /**
     * 发起提问  场外 申诉
     */
    @POST("/question/create_problem")
    fun createProblem4OTC(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 根据 订单状态查询场外订单
     */
    @POST("order/otc/bystatus_v4")
    fun byStatus4OTC(@Body requestBody: RequestBody): Observable<HttpResult<OTCOrderBean>>


    /**
     * 提问详情页
     */
    @POST("question/details_problem")
    fun getDetailsProblem(@Body requestBody: RequestBody): Observable<HttpResult<OTCIMDetailsProblemBean>>


    /**
     * 追加提问  与人工服务聊天
     */
    @POST("question/reply_create")
    fun getReplyCreate(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 新的资金流水 其他交易
     */
    @POST("record/ex_transfer_list_v4")
    fun otherTransList4V2(@Body requestBody: RequestBody): Observable<HttpResult<CashFlowBean>>

    /**
     * 找回密码 Step 3
     */
    @POST("user/search_step_three")
    fun findPwdStep3(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 指纹或者人脸识别 - 登录
     */
    @POST("user/login_AI")
    fun quickLogin(@Body requestBody: RequestBody): Observable<HttpResult<JsonObject>>

    /**
     * 获取极验
     */
    @POST("common/tartCaptcha")
    fun getTartCaptcha(@Body requestBody: RequestBody): Observable<HttpResult<String>>

    /**
     *图片临时token
     */
    @POST("common/get_image_token")
    fun getImageToken(@Body requestBody: RequestBody): Observable<HttpResult<ImageTokenBean>>

    /**
     * 场内资金流水-场景列表
     */
    @POST("record/ex_transfer_scene_v4")
    fun getCashFlowScene(@Body requestBody: RequestBody): Observable<HttpResult<CashFlowSceneBean>>


    /**
     * 场内资金流水列表
     */
    @POST("record/ex_transfer_list_v4")
    fun getCashFlowList(@Body requestBody: RequestBody): Observable<HttpResult<CashFlowBean>>


    /**
     * 撤销提现(4.0app)
     */
    @POST("finance/cancel_withdraw")
    fun cancelWithdraw(@Body requestBody: RequestBody): Observable<HttpResult<Any>>



    /**
     * 站内信未读条数接口
     */
    @POST("message/get_no_read_message_count")
    fun getReadMessageCount(@Body requestBody: RequestBody): Observable<HttpResult<ReadMessageCountBean>>

    /**
     * 确定已读
     */
    @POST("message/message_update_status")
    fun updateMessageStatus(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 实名制认证接口
     */
    @POST("kyc/Api/getToken")
    fun AccountCertification(@Body requestBody: RequestBody): Observable<HttpResult<AccountCertificationBean>>


    /**
     * 获取kyc配置
     */
    @POST("kyc/config")
    fun getKYCConfig(@Body requestBody: RequestBody): Observable<HttpResult<KYCBean>>

    /**
     * 实名制认证第四个文案
     */
    @POST("kyc/Api/getUploadImgCopywriting")
    fun AccountCertificationLanguage(@Body requestBody: RequestBody): Observable<HttpResult<AccountCertificationLanguageBean>>

    /**
     *  获取分享好友页面
     */
    @POST("common/getInvitationImgs")
    fun getInvitationImg(@Body requestBody: RequestBody): Observable<HttpResult<InvitationImgBean>>

    /**
     *  资金划转接口
     */
    @POST("app/co_transfer")
    fun doAssetExchange(@Body requestBody: RequestBody): Observable<HttpResult<Any>>


    /**
     * 游戏授权
     */
    @POST("/game/appplay")
    fun getGameAuth(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 修改登录密码V4
     */
    @POST("user/password_update_v4")
    fun changeLoginPwdV4(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    /**
     * 上传照片
     * 使用form表单提交
     */
    @Multipart
    @POST("uploadFile")
    fun uploadZip(@Part file: MultipartBody.Part): Observable<HttpResult<Any>>

    @POST("appPush/userPushSwitch")
    fun getPush(@QueryMap map: Map<String, String>): Observable<HttpResult<PushItem>>

    @POST("appPush/saveAppPushDevice")
    fun bindToken(@Body requestBody: RequestBody): Observable<HttpResult<Any>>

    @POST("appPush/saveAppPushUser")
    fun saveAppPushU(@Body requestBody: RequestBody): Observable<HttpResult<Any>>
}

