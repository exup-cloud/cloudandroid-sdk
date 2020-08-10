package com.yjkj.chainup.util

import com.tencent.mmkv.MMKV
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.new_version.kline.view.MainKlineViewStatus
import com.yjkj.chainup.new_version.kline.view.vice.ViceViewStatus
import java.lang.IndexOutOfBoundsException

/**
 * @Author: Bertking
 * @Date：2019/3/19-11:52 AM
 * @Description: K线相关的配置项
 */
object KLineUtil {
    private val mmkv: MMKV = MMKV.mmkvWithID("kline_configuration")
    /**
     * KLine的当前刻度
     */
    private const val CURRENT_TIME = "cur_time"


    private const val CURRENT_TIME_CONTENT = "cur_time_content"
    /**
     * 副图指标
     */
    private const val VICE_INDEX = "vice_index"

    /**
     * 主图指标
     */
    private const val MAIN_INDEX = "main_index"


    /**
     * 获取Kline的刻度
     */
    fun getKLineScale(): ArrayList<String> {
        var list = arrayListOf<String>()
        list.add("1min")
        list.add("5min")
        list.add("15min")
        list.add("30min")
        list.add("60min")
        list.add("1day")
        list.add("1week")
        list.add("1month")


        val klineScaleJSONArray = PublicInfoDataService.getInstance().getKlineScale(null)
        if (klineScaleJSONArray != null && klineScaleJSONArray.length() > 0) {
            list.clear()
            for (i in 0..klineScaleJSONArray.length()) {
                list.add(klineScaleJSONArray.optString(i))
            }
        }

        /**
         * 添加分时
         */
        list.add(0, "line")
        return list
    }


    /**
     *@return 获取KLine的当前刻度
     * TODO 数组越界异常
     */
    fun getCurTime4KLine(): HashMap<Int, String> {
        return try {
            hashMapOf<Int, String>(getCurTime4Index() to getKLineScale()[if (getCurTime4Index() < 0) 0 else getCurTime4Index()])
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            hashMapOf((getCurTime4Index() to getKLineScale()[0]))
        }
    }

    fun setCurTime(curTime: String) {
        mmkv.encode(CURRENT_TIME_CONTENT, curTime)
    }

    fun getCurTime(): String {
        return mmkv.decodeString(CURRENT_TIME_CONTENT, "15min")
    }


    /**
     * @return 获取KLine的当前刻度的下标
     */
    fun getCurTime4Index(): Int {
        return mmkv.decodeInt(CURRENT_TIME, getKLineScale().indexOf("15min"))
    }

    /**
     * 保存KLine的当前刻度
     * @param curPosition KLine的当前刻度下标
     */
    fun setCurTime4KLine(curPosition: Int) {
        mmkv.encode(CURRENT_TIME, curPosition)
    }

    /**
     * 设置副图指标
     * @param status 副图指标
     */
    fun setViceIndex(status: Int) {
        mmkv.encode(VICE_INDEX, status)
    }

    /**
     * 获取副图指标
     * @return 副图指标
     */
    fun getViceIndex(): Int = mmkv.decodeInt(VICE_INDEX, ViceViewStatus.NONE.status)


    /**
     * 设置主图指标
     * @param status 主图指标
     */
    fun setMainIndex(status: Int) {
        mmkv.encode(MAIN_INDEX, status)
    }

    /**
     * 获取主图指标
     * @return 主图指标
     */
    fun getMainIndex(): Int = mmkv.decodeInt(MAIN_INDEX, MainKlineViewStatus.MA.status)

}