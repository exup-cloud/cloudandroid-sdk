package com.yjkj.chainup.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.yjkj.chainup.R
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.db.service.ColorDataService
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil

/**
 * @Author: Bertking
 * @Date：2019/3/9-2:15 PM
 * @Description:
 */
object ColorUtil {

    val TAG = "ColorUtil"

    const val COLOR_SELECT = "color_selected"

    /**
     * 绿涨红跌
     */
    const val GREEN_RISE = 0
    /**
     * 红涨绿跌
     */
    const val RED_RISE = 1


    fun getColor(context: Context, colorId: Int) =
            ContextCompat.getColor(context, colorId)

    fun getColor(colorId: Int) = getColor(ChainUpApp.appContext, colorId)


    fun getMainGreen() : Int{
        return getColor(R.color.main_green)
    }

    fun getMainRed() : Int{
        return getColor(R.color.main_red)
    }

    /**
     * 红涨绿跌 OR 绿涨红跌
     * 0 ---- 绿涨红跌
     * 1 ---- 红涨绿跌
     *
     */
    fun getColorType(): Int {
        return ColorDataService.getInstance().colorType
    }

    /**
     *获取主要颜色(红绿)
     * @param isRise 是否是上涨状态
     */
    fun getMainColorType(isRise: Boolean = true): Int {
        var colorSelect = getColorType()
        LogUtil.d(TAG,"getMainColorType==isRise is $isRise,colorSelect is $colorSelect")
        val mainGreen = getColor(R.color.main_green)
        val mainRed = getColor(R.color.main_red)

        if(colorSelect == GREEN_RISE){
            if(isRise){
                return mainGreen
            }
            return mainRed
        }else{
            if(isRise){
                return mainRed
            }
            return mainGreen
        }

    }

    /**
     *获取主要颜色(红绿)
     * @param isRise 是否是上涨状态
     */
    fun getMainColorType(colorSelect:Int, isRise: Boolean = true): Int {
        val mainGreen =
                getColor(R.color.main_green)
        val mainRed = getColor(R.color.main_red)
        return if (colorSelect == GREEN_RISE) {
            if (isRise) {
                mainGreen
            } else {
                mainRed
            }
        } else {
            if (isRise) {
                mainRed
            } else {
                mainGreen
            }
        }
    }




    /**
     * 获取次要颜色(带透明度的红绿)
     * @param isRise 是否是上涨状态
     */
    fun getMinorColorType(isRise: Boolean = true): Int {
        var colorSelect = getColorType()

        val minorGreen = getColor(R.color.main_green_15)
        val minorRed = getColor(R.color.main_red_15)

        return if (colorSelect == GREEN_RISE) {
            if (isRise) {
                minorGreen
            } else {
                minorRed
            }
        } else {
            if (isRise) {
                minorRed
            } else {
                minorGreen
            }
        }

    }

    /**
     * 交易界面(买卖TAB drawable)，特殊处理
     */
    fun getOrientationTabDrawable(isBuy: Boolean = true): Int {
        var colorSelect = getColorType()
        /*PublicInfoManager.liveData4Color.observeForever {
            colorSelect = it!!
        }*/

        val drawableGreen = R.drawable.bg_buy_line
        val drawableRed = R.drawable.bg_sell_line
        return if (colorSelect == GREEN_RISE) {
            if (isBuy) {
                drawableGreen
            } else {
                drawableRed
            }
        } else {
            if (isBuy) {
                drawableRed
            } else {
                drawableGreen
            }
        }
    }

    /**
     * 合约交易界面(百分比背景 drawable)
     */
    fun getContractRateDrawable(isRise: Boolean = true): Int {
        var colorSelect = getColorType()
        /*PublicInfoManager.liveData4Color.observeForever {
            colorSelect = it!!
        }*/

        val drawableGreen = R.drawable.sl_border_green_fill
        val drawableRed = R.drawable.sl_border_red_fill
        return if (colorSelect == GREEN_RISE) {
            if (isRise) {
                drawableGreen
            } else {
                drawableRed
            }
        } else {
            if (isRise) {
                drawableRed
            } else {
                drawableGreen
            }
        }
    }


    /**
     * otc 交易页面买卖
     */
    fun getOTCBuyOrSellDrawable(): Int {
        val drawableBlue = R.drawable.bg_otc_buy_or_sell_line

        return drawableBlue
    }


    /**
     * 获取交易界面的交易量比例的ColorStateList
     * TODO 后期添加灵活配置
     */
    fun getCheck4ColorStateList(isRise: Boolean = true): ColorStateList {
        val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
        )

        val colorArray = intArrayOf(
                getMainColorType(isRise),
                getColor(R.color.hint_color)
        )
        return ColorStateList(states, colorArray)
    }

    /**
     * 获取交易界面的交易量比例的StateListDrawable
     * TODO 后期添加灵活配置
     */
    fun getCheck4StateListDrawable(isRise: Boolean = true): StateListDrawable {
        val normalDrawable = GradientDrawable()
        normalDrawable.setColor(getColor(R.color.transparent))

        val checkedDrawable = GradientDrawable()
        checkedDrawable.setColor(getMinorColorType(isRise))

        val stateDrawable = StateListDrawable()
        stateDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedDrawable)
        stateDrawable.addState(intArrayOf(), normalDrawable)
        return stateDrawable
    }




    /**
     * 交易界面(买卖TAB drawable)，特殊处理
     * @param flag 0 默认; 1 买盘 2 卖盘
     */
    fun setTapeIcon(imageView: ImageView, flag: Int = 0) {
        var colorSelect = getColorType()
        /*PublicInfoManager.liveData4Color.observeForever {
            colorSelect = it!!
        }*/
        return if (colorSelect == GREEN_RISE) {
            when (flag) {
                1 -> {
                    imageView.setImageResource(R.drawable.buy_tape)
                }

                2 -> {
                    imageView.setImageResource(R.drawable.sell_tape)
                }

                else -> {
                    imageView.setImageResource(R.drawable.default_tape)
                }
            }

        } else {
            when (flag) {
                1 -> {
                    imageView.setImageResource(R.drawable.sell_tape)
                }

                2 -> {
                    imageView.setImageResource(R.drawable.buy_tape)
                }

                else -> {
                    imageView.setImageResource(R.drawable.reverse_tape)
                }
            }

        }
    }

}