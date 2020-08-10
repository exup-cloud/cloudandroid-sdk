package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.Utils
import com.yjkj.chainup.wedegit.DataPickView.DatePickDialog
import com.yjkj.chainup.wedegit.DataPickView.bean.DateType
import kotlinx.android.synthetic.main.layout_select_date.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Bertking
 * @Date：2019-05-27-16:47
 * @Description:
 */
class SelectDateView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val TAG = SelectDateView::class.java.simpleName

    var beginTime: String = ""
    var endTime: String = ""
    var dateListener: IDateValue? = null


    interface IDateValue {
        fun returnValue(startTime: String, endTimes: String)
    }

    init {
        attrs?.let {
        }
        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.layout_select_date, this, true)
        initDate()
        pet_start_time?.setEditText(LanguageUtil.getString(context,"filter_date_start"))
        pet_end_time?.setEditText(LanguageUtil.getString(context,"filter_date_end"))
        pet_start_time?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {
                return text
            }

            override fun returnItem(item: Int) {

            }

            override fun onclickImage() {
                showDatePickDialog(0, Utils.parseServerTime(beginTime), pet_start_time)
            }

        }
        pet_end_time?.onTextListener = object : PwdSettingView.OnTextListener {
            override fun showText(text: String): String {
                return text
            }

            override fun returnItem(item: Int) {

            }

            override fun onclickImage() {
                showDatePickDialog(1, Utils.parseServerTime(endTime), pet_end_time)
            }

        }
    }


    fun initDate() {
        dateListener?.returnValue(beginTime, endTime)
    }

    fun resetTime() {
        beginTime = ""
        endTime = ""
        pet_start_time.setEditText(LanguageUtil.getString(context,"filter_date_start"))
        pet_end_time.setEditText(LanguageUtil.getString(context,"filter_date_end"))
    }


    /**
     * 显示 选择日历dialog
     */
    private fun showDatePickDialog(index: Int, date: Date? = null, view: PwdSettingView) {
        val dialog = DatePickDialog(context)
        //设置上下年分限制
        dialog.setYearLimt(10)
        if (date != null) {
            dialog.setStartDate(date)
        }
        //设置标题
        //设置类型 这里选择年月日
        dialog.setType(DateType.TYPE_YMD)
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd")
        //设置选择回调
        dialog.setOnChangeLisener { date ->
            var message = ""
            try {
                message = SimpleDateFormat("yyyy-MM-dd").format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            when (index) {
                0 -> {
                    beginTime = message
                }
                1 -> {
                    endTime = message
                }
            }
            view.setEditText(message)

            dateListener?.returnValue(beginTime, endTime)
        }
        //设置点击确定按钮回调
        dialog.setOnSureLisener(null)
        dialog.show()
    }


}