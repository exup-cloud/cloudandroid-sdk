package com.yjkj.chainup.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Bertking
 * @Date：2019/3/6-10:56 AM
 * @Description: 常用时间工具类
 */
class TimeUtil private constructor() {
    private val formatDate: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val formatDateTime: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    companion object {
        @JvmStatic
        val instance: TimeUtil by lazy {
            TimeUtil()
        }
    }

    /**
     * @return 年月日
     */
    private fun formatDate(date: Date) = formatDate.format(date)

    /**
     * @return 年月日 时分秒
     */
    private fun formatDateTime(date: Date) = formatDateTime.format(date)

    /**
     * @param millisecond 毫秒
     * @return  年月日
     */
    fun getFormatDate(millisecond: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millisecond
        return formatDate(calendar.time)
    }

    /**
     * @param millisecond 毫秒
     * @return 年月日 时分秒
     */
    fun getFormatDateTime(millisecond: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millisecond
        return formatDateTime(calendar.time)
    }

    fun getTime(time: String?): String {
        return if (time == null) {
            ""
        } else {
            if (StringUtil.checkStr(time)) {
                when (time.length) {
                    10 -> {
                        getFormatDateTime(time?.toLong() * 1000L)
                    }
                    13 -> {
                        getFormatDateTime(time?.toLong())
                    }
                    else -> {
                        ""
                    }
                }
            } else {
                ""
            }
        }

    }


}