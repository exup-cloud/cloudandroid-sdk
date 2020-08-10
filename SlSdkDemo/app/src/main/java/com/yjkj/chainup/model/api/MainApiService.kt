package com.yjkj.chainup.model.api

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 *

 * @Description:

 * @Author:         wanghao

 * @CreateDate:     2019-08-28 16:42

 * @UpdateUser:     wanghao

 * @UpdateDate:     2019-08-28 16:42

 * @UpdateRemark:   更新说明

 */
interface MainApiService {


    /**
     * 获取公共接口的信息
     */
    @POST("common/public_info_v4")
    fun public_info_v4(@Body requestBody: RequestBody): Observable<ResponseBody>


    @POST("common/user_info")
    fun user_info(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 1. 合约的公共接口
     */
    @POST("/contract_public_info_v2")
    fun contract_public_info_v2(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 13. 账户余额信息 ：
     */
    @POST("/account_balance")
    fun account_balance(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 新首页 头部币对24小时行情
     */
    @GET("common/header_symbol")
    fun header_symbol(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 交易账户
     */
    @POST("finance/v5/account_balance")
    fun accountBalance(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 新首页 数据
     */
    @POST("common/index")
    fun common_index(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 交易账户
     */
    @POST("/finance/v4/otc_account_list")
    fun otc_account_list(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 新首页 涨跌幅榜和成交量榜单
     */
    @POST("common/trade_list_v4")
    fun trade_list_v4(@Body requestBody: RequestBody): Observable<ResponseBody>

    /****************币币交易相关*******************/

    /**
     * 创建订单(币币)
     * 买卖单
     */
    @POST("order/create")
    fun createOrder(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 取消订单(币币)
     */
    @POST("order/cancel")
    fun cancelOrder(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取当前委托(币币)
     */
    @POST("order/list/new")
    fun getNewEntrust(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取历史委托(币币)
     */
    @POST("v4/order/entrust_history")
    fun getHistoryEntrust4(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 历史委托新
     */
    @POST("order/entrust_search")
    fun getNewEntrustSearch(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 获取委托详情
     */
    @POST("trade/list_by_order")
    fun getEntrustDetail4(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 合约历史委托详情
     */
    @POST("lever/trade/list_by_order")
    fun getLeverEntrustDetail4(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     *  获取交易限制文案
     */
    @POST("order/trade_limit_info")
    fun getTradeLimitInfo(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 登录接口
     */
    @POST("user/login_in")
    fun loginByMobile(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 登录的二次确认
     */
    @POST("user/confirm_login")
    fun confirmLogin(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 注册Step 2
     */
    @POST("user/valid_code")
    fun reg4Step2(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 找回密码 Step 2
     */
    @POST("user/search_step_two")
    fun findPwdStep2(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 指纹或者人脸识别 - 验证本地密码
     */
    @POST("common/check_native_pwd")
    fun checkLocalPwd(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 找回密码 Step 4
     */
    @POST("user/search_step_four")
    fun findPwdStep4(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 注册Step 3
     */
    @POST("user/confirm_pwd")
    fun reg4Step3(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 设置手势密码
     */
    @POST("user/open_handPwd_two")
    fun setHandPwd(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 指纹或者人脸识别 - 验证本地密码
     */
    @POST("/user/open_handPwd_V2")
    fun newOpenHandPwd(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 找回密码 Step 1
     */
    @POST("user/search_step_one")
    fun findPwdStep1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取币种简介
     */
    @POST("common/coinSymbol_introduce")
    fun getCoinIntro(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 注册Step 1
     */
    @POST("user/register")
    fun reg4Step1(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 手势密码登录
     */
    @POST("user/login_handPwd")
    fun handPwdLogin(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     *  获取服务端的用户自选币对
     */
    @POST("optional/list_symbol")
    fun getOptionalSymbol(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 添加/删除自选
     */
    @POST("optional/update_symbol")
    fun addOrDeleteSymbol(@Body requestBody: RequestBody): Observable<ResponseBody>

    /*
     *  查询汇率
     */
    @POST("common/rate")
    fun common_rate(@Body requestBody: RequestBody): Observable<ResponseBody>


    /*******B2C*********/

    /**
     * 法币资产列表
     */
    @POST("/fiat/balance")
    fun fiatBalance(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 充值
     */
    @POST("/fiat/deposit")
    fun fiatDeposit(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 充值记录
     */
    @POST("/fiat/deposit/list")
    fun fiatDepositList(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 充值撤销
     */
    @POST("/fiat/cancel_deposit")
    fun fiatCancelDeposit(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 提现
     */
    @POST("/fiat/withdraw")
    fun fiatWithdraw(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 提现记录
     */
    @POST("fiat/withdraw/list")
    fun fiatWithdrawList(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 提现撤销
     */
    @POST("/fiat/cancel_withdraw")
    fun fiatCancelWithdraw(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 用户提现银行列表
     */
    @POST("/user/bank/user_bank_list")
    fun fiatBankList(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 查询用户提现银行
     */
    @POST("/user/bank/get")
    fun fiatGetBank(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 新增用户提现银行
     * NOTE：...
     */
    @POST("/user/bank/add")
    fun fiatAddBank(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 修改用户提现银行
     */
    @POST("/user/bank/edit")
    fun fiatEditBank(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 删除用户提现银行
     */
    @POST("/user/bank/delete")
    fun fiatDeleteBank(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 查询平台充值银行信息
     */
    @POST("/company/bank/info")
    fun fiatBankInfo(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 查询平台支持提现银行列表
     */
    @POST("/bank/all")
    fun fiatAllBank(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * OSS上传图片
     *图片临时token
     */
    @POST("common/get_image_token")
    fun getImageToken(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 自己服务器上传
     * 上传照片
     * 使用form表单提交
     */
    @POST("common/upload_img")
    fun uploadImg(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 交易加价卖出
     */
    @POST("order/create_overcharge_onekey")
    fun raisePriceSell(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * /notice/detail app公告详情页
     */
    @GET("notice/detail")
    fun getNoticeDetail(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取kv配置
     */
    @GET("common/kv")
    fun getCommonKV(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     *获取App版本信息
     */
    @GET("common/getVersion")
    fun getAppVersion(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取历史委托(币币)
     */
    @POST("/role/index")
    fun getRoleIndex(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 判断用户id与手势密码是否相符
     */
    @POST("common/gesturePwd")
    fun getGesturePwd(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**********杠杆*************/

    /**
     * 取消订单
     */
    @POST("lever/order/cancel")
    fun cancelOrder4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 杠杆下单接口
     */
    @POST("lever/order/create")
    fun createOrder4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 历史委托(杠杆)
     */
    @POST("lever/order/history")
    fun historyOrders4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 当前委托(杠杆)
     */
    @POST("lever/order/list/new")
    fun newOrders4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 根据订单号获取成交记录
     */
    @POST("lever/trade/list_by_order")
    fun orderRecords4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 当前申请(未归还记录)
     */
    @POST("/lever/borrow/new")
    fun borrowNew(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 历史申请(已归还记录)
     */
    @POST("/lever/borrow/history")
    fun borrowHistory(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 杠杆账户列表
     */
    @POST("lever/finance/balance")
    fun getBalanceList(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 归还
     */
    @POST("lever/finance/return")
    fun setReturn(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 借贷
     */
    @POST("lever/finance/borrow")
    fun setBorrow(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 根据币对获取账户信息
     */
    @POST("lever/finance/symbol/balance")
    fun getBalance4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 根据币对获取账户信息
     */
    @POST("lever/finance/transfer")
    fun setTransfer4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 场外资金划转
     */
    @POST("finance/otc_transfer")
    fun transher4OTC(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 资金划转
     */
    @POST("/capital_transfer")
    fun capitalTransfer4Contract(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 划转时获取资金
     */
    @POST("finance/get_account_by_coin")
    fun accountGetCoin4OTC(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 查询明细
     */
    @POST("lever/return/info")
    fun getDetail4Lever(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 查询明细
     */
    @POST("lever/finance/transfer/list")
    fun getTransferList(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     *  首页总接口
     */
    @POST("finance/total_account_balance")
    fun getTotalAsset(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 根据币种查询手续费和提现地址
     */
    @POST("/cost/Getcost")
    fun getCost(@Body requestBody: RequestBody): Observable<ResponseBody>


    /**
     * 获取币对etf净值
     */
    @POST("/etfAct/netValue")
    fun getETFValue(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * ETF免责信息url和域名
     */
    @POST("/etfAct/faqInfo")
    fun getETFInfo(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取用户身份认证所需要的信息
     */
    @POST("security/get_identity_auth_info")
    fun getIdentityAuthInfo(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 认证该用户身份
     */
    @POST("security/identity_auth_info_check")
    fun submitAuthInfoCheck(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取kol列表 get
     */
    @GET("out/follow/chainup/kol/list")
    fun getFollowKolList(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取跟单列表 get
     */
    @GET("out/follow/chainup/follow/list")
    fun getFollowList(@QueryMap map: Map<String, String>): Observable<ResponseBody>


    /**
     * 获取跟单配置 get
     */
    @GET("out/follow/chainup/follow/options")
    fun getFollowOptions(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取跟单收益(跟单列表上的跟单收益信息)
     */
    @GET("out/follow/chainup/follow/profit")
    fun getFollowProfit(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取跟单详情
     */
    @GET("out/follow/chainup/follow/detail")
    fun getFollowDetail(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取跟单收益趋势
     */
    @GET("out/follow/chainup/follow/trend")
    fun getFollowTrend(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 获取跟单分享信息
     */
    @GET("out/follow/chainup/follow/share")
    fun getFollowShare(@QueryMap map: Map<String, String>): Observable<ResponseBody>

    /**
     * 开始跟单
     */
    @POST("inner/follow/set")
    fun getInnerFollowbegin(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 结束跟单
     */
    @POST("inner/follow/stop")
    fun getInnerFollowEnd(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 结束跟单
     */
    @POST("app-increment-api/co/agent/index")
    fun getAgentIndex(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 结束跟单
     */
    @POST("app-increment-api/common/public")
    fun getNoTokenPublic(@Body requestBody: RequestBody): Observable<ResponseBody>





}