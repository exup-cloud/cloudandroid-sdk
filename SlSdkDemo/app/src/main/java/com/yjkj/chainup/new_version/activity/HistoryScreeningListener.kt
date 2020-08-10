package com.yjkj.chainup.new_version.activity

/**
 * @Author lianshangljl
 * @Date 2020-03-10-16:41
 * @Email buptjinlong@163.com
 * @description
 */
interface HistoryScreeningListener {
    fun ConfirmationScreen(status: Boolean, symbolCoin: String, symbolAndUnit: String, tradingType: Int, priceType: Int, begin: String, end: String)
}