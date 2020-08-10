package com.yjkj.chainup.new_version.activity.otcTrading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/22-10:29 AM
 * @Email buptjinlong@163.com
 * @description  申诉
 */

/**
 * 问题类型
 * 1.意见与建议 2.充值提现 3.交易相关 4.安全相关 5.个人信息 6.实名认证,7.场外申诉,8.对方未放行,9.买方未付款
 * 此处只用7和8  默认7
 */
const val SELECTITEM = "7"
const val OTC_COMPLAIN_ORDER = "otc_complain_order"
const val OTC_TRADING_TYPE = "otc_trading_type"
const val OTC_COMPLAINT_TYPE = "otc_complaint_type"
const val OTC_COMPLAINT_SYMBOL = "otc_complaint_symbol"

class NewComplaintActivity : NewBaseActivity() {

    companion object {
        /**
         * orderId 订单id
         */
        fun enter2(context: Context, orderId: String, tradingType: Boolean, complaintType: String, symbol: String) {
            var intent = Intent(context, NewComplaintActivity::class.java)
            intent.putExtra(OTC_COMPLAIN_ORDER, orderId)
            intent.putExtra(OTC_TRADING_TYPE, tradingType)
            intent.putExtra(OTC_COMPLAINT_TYPE, complaintType)
            intent.putExtra(OTC_COMPLAINT_SYMBOL, symbol)
            context.startActivity(intent)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_complaint)

    }

}