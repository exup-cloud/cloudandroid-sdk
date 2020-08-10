package com.yjkj.chainup.new_version.kline.formatter

import com.yjkj.chainup.new_version.kline.base.IValueFormatter
import com.yjkj.chainup.util.BigDecimalUtils

/**
 * @Author: Bertking
 * @Date：2019/3/11-11:16 AM
 * @Description:
 */
class ValueFormatter : IValueFormatter {
    override fun format(value: Float): String {
        return BigDecimalUtils.showSNormal(value.toString())
    }
}