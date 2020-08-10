package com.yjkj.chainup.new_version.bean

/**
 * @Author: Bertking
 * @Date：2019-05-22-11:20
 * @Description:
 */
class FlagBean(var isContract: Boolean, var symbol: String, var baseSymbol: String, var quotesSymbol: String, var pricePrecision: Int = 0, var volumePrecision: Int = 0) {
    override fun toString(): String {
        return "FlagBean(isContract=$isContract, symbol='$symbol', baseSymbol='$baseSymbol', quotesSymbol='$quotesSymbol')"
    }

}