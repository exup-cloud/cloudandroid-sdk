package com.yjkj.chainup.model.model

import android.text.TextUtils
import com.yjkj.chainup.model.api.ContractApiService
import com.yjkj.chainup.model.datamanager.BaseDataManager
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import okhttp3.ResponseBody

/**
 * @Author: Bertking
 * @Date：2019-09-04-11:27
 * @Description: 合约具体请求
 */
class ContractModel : BaseDataManager() {

    /**
     * 1. 合约的公共接口
     */
    fun getPublicInfo4Contract(consumer: DisposableObserver<ResponseBody>): Disposable? {
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getPublicInfo4Contract1(getBaseReqBody()), consumer)
    }

    /**
     *
     * 2. 获取创建订单初始化信息（need login）
     *
     * @param contractId   合约id
     * @param volume   用户输入数量（不输入默认按照1算）
     * @param price    用户输入价格（不输入默认按照最新成交价，如果最新成交价为空，取币对开盘价格）
     * @param level   杠杆倍数(只有在选择杠杆的时候必填)
     * @param orderType 1:限价 2：市价
     *
     */
    fun getInitTakeOrderInfo4Contract(contractId: String,
                                      volume: String = "1",
                                      price: String,
                                      level: String = "",
                                      orderType: Int,
                                      consumer: DisposableObserver<ResponseBody>): Disposable? {

        val map = getBaseMaps()
        map["contractId"] = contractId
        map["volume"] = volume
        map["price"] = price
        if (!TextUtils.isEmpty(level)) {
            map["level"] = level
        }
        map["orderType"] = orderType.toString()
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getInitTakeOrderInfo4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 3. 创建订单
     *
     * @param contractId  合约id
     * @param volume  下单数量
     * @param price   下单价格
     * @param orderType (1：限价单   2：市价单)
     * @param copType (1：全仓    2：逐仓) 平仓时此参数不传
     * @param side  (BUY:买     SELL:卖)
     * @param closeType  (0:开仓单，1：平仓单)
     * @param level  杠杆倍数
     *
     */
    fun takeOrder4Contract(contractId: String,
                           volume: String,
                           price: String,
                           orderType: Int,
                           copType: String = "2",
                           side: String,
                           closeType: String = "0",
                           level: String,
                           positionId: String = "",
                           consumer: DisposableObserver<ResponseBody>): Disposable? {

        val map = getBaseMaps()
        map["contractId"] = contractId
        map["volume"] = volume
        map["price"] = price
        map["orderType"] = orderType.toString()
        map["copType"] = copType
        map["side"] = side
        map["closeType"] = closeType
        map["level"] = level
        if (!TextUtils.isEmpty(positionId)) {
            map["positionId"] = positionId
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).takeOrder4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 4. 取消订单
     * @param orderId 订单id
     * @param contractId 合约id
     */
    fun cancelOrder4Contract(orderId: String,
                             contractId: String,
                             consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["orderId"] = orderId
            this["contractId"] = contractId
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).cancelOrder4Contract1(getBaseReqBody(map)), consumer)

    }


    /**
     * 5. 获取合约订单列表
     * @param consumer
     * @return
     */
    fun getOrderList4Contract(contractId: String, page: String = "1", pageSize: String = "100", side: String = "", consumer: DisposableObserver<ResponseBody>): Disposable? {
        var paramMaps = getBaseMaps()
        paramMaps["contractId"] = contractId
        paramMaps["page"] = page
        paramMaps["pageSize"] = pageSize
        paramMaps["side"] = side
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getOrderList4Contract1(getBaseReqBody(paramMaps)), consumer)
    }


    /**
     * 7. 标记价格(needless 登录)
     * @param contractId 合约id
     */
    fun getTagPrice4Contract(contractId: String,
                             consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["contractId"] = contractId
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getTagPrice4Contract1(getBaseReqBody(map)), consumer)
    }

    /**
     * 8. 修改杠杆倍数
     */
    fun changeLevel4Contract(contractId: String, newLevel: String,
                             consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["contractId"] = contractId
            this["leverageLevel"] = newLevel
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).changeLevel4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 9. 追加保证金 ：
     * @param contractId  合约id
     * @param amount 追加数量(转出为负数)
     */
    fun transferMargin4Contract(positionId: String,
                                contractId: String,
                                amount: String,
                                consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps().apply {
            this["contractId"] = contractId
            this["amount"] = amount
            this["positionId"] = positionId
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).transferMargin4Contract1(getBaseReqBody(map)), consumer)
    }

    /**
     * 10. 用户持仓信息 ：
     *
     * 合约id不填查询的为仓位列表
     *  5s刷新
     */
    fun getPosition4Contract(contractId: String = "",
                             consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map =getBaseMaps().apply {
            this["contractId"] = contractId
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getPosition4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 11. 用户未平仓合约 ：
     * 页面20s请求
     */
    fun holdContractList4Contract(consumer: DisposableObserver<ResponseBody>): Disposable? {
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).holdContractList4Contract1(getBaseReqBody()), consumer)
    }


    /**
     * 12. 资金划转 ：
     *
     * @param fromType 转出账户type
     * @param toType   转入账户type
     * @param amount   划转金额
     * @param bond     保证金币种
     *
     */
    fun capitalTransfer4Contract(fromType: String,
                                 toType: String,
                                 amount: String,
                                 bond: String?,
                                 consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        map["fromType"] = fromType
        map["toType"] = toType
        map["amount"] = amount
        if (null != bond) {
            map["bond"] = bond!!

        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).capitalTransfer4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 13. 账户余额 ：
     */
    fun getAccountBalance4Contract(
            consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getAccountBalance4Contract1(getBaseReqBody(map)), consumer)
    }

    /**
     * 15.风险评估(need登录)
     */
    fun getRiskLiquidationRate(contractId: String = "",
                               consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        map["contractId"] = contractId
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getRiskLiquidationRate1(getBaseReqBody(map)), consumer)
    }

    /**
     * 获取历史委托(合约)
     * @param symbol 合约系列
     * @param contractType 合约类型（0 永续合约、1 周合约、2 次周合约、3 月合约、4 季度合约）
     * @param pageSize default 5
     * @param page  default 1
     * @param side 委托方向，BUY买入 SELL卖出 ,不传全部
     * @param isShowCanceled 是否展示已取消的订单，0表示不展示，1表示展示，默认1
     * @param startTime 年月日，禁止输入时分秒：2019-04-22
     * @param endTime 年月日，禁止输入时分秒：2019-04-22
     * @param action 开平仓动作
     */
    fun getHistoryEntrust4Contract(symbol: String,
                                   contractType: String,
                                   pageSize: String = "5",
                                   page: String = "1",
                                   isShowCanceled: String = "1",
                                   side: String = "",
                                   orderType: String = "",
                                   startTime: String = "",
                                   endTime: String = "",
                                   action: String = "",
                                   consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        map["symbol"] = symbol
        map["pageSize"] = pageSize
        map["page"] = page
        map["isShowCanceled"] = isShowCanceled

        if (!TextUtils.isEmpty(side)) {
            map["side"] = side
        }

        if (!TextUtils.isEmpty(orderType)) {
            map["orderType"] = orderType
        }

        map["contractType"] = contractType

        if (!TextUtils.isEmpty(startTime)) {
            map["startTime"] = startTime
        }

        if (!TextUtils.isEmpty(endTime)) {
            map["endTime"] = endTime
        }
        if (!TextUtils.isEmpty(action)) {
            map["action"] = action
        }
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getHistoryEntrust4Contract1(getBaseReqBody(map)), consumer)
    }


    /**
     * 16.合约资金流水(need登录)
     *
     * @param item 流水类型
     * @param childItem 子级流水类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页数
     * @param pageSize 每页几条
     *
     */
    fun getBusinessTransferList(item: String = "", childItem: String = "", startTime: String = "", endTime: String = "", page: Int = 1, pageSize: Int = 1000,
                                consumer: DisposableObserver<ResponseBody>): Disposable? {
        val map = getBaseMaps()
        map["item"] = item
        map["childItem"] = childItem
        map["startTime"] = startTime
        map["endTime"] = endTime
        map["page"] = page.toString()
        map["pageSize"] = pageSize.toString()
        return changeIOToMainThread(httpHelper.getContractUrlService(ContractApiService::class.java).getBusinessTransferList1(getBaseReqBody(map)), consumer)
    }


}