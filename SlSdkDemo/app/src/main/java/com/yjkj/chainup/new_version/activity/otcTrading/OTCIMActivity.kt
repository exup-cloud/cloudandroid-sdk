package com.yjkj.chainup.new_version.activity.otcTrading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2018/10/15-下午12:02
 * @Email buptjinlong@163.com
 * @description 聊天界面
 */


class OTCIMActivity : NewBaseActivity() {


    companion object {

        val COMPLAINID = "complainId"
        val TYPE = "IM_TYPE"
        val TO_ID = "IM_TO_ID"
        val ORDER_ID = "IM_ORDER_ID"
        val IM_SYMBOL = "IM_SYMBOL"
        val TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT"
        val ORDER_TYPE = "ORDER_TYPE"
        val AMOUNT_SYMBOL = "AMOUNT_SYMBOL"
        val IM_NICKNAME = "IM_NICKNAME"
        val IM_TIME = "IM_TIME"
        val ORDER_ROLE = "ORDER_ROLE"
        val ORDER_TIME = "ORDER_TIME"
        val ISCOMPLAINUSER = "ISCOMPLAINUSER"
        val HEAD_URL = "HEAD_URL"

        val MESSAGE_OTC = 1
        val DETAILS_PROBLEM = 2


        /**
         * 与客服聊天 机器人
         *
         */
        fun newIntent(context: Context, uid: Int, symbol: String, amount: String, orderType: String, amountSymbol: String, time: String, toId: Int
                      , orderId: String, nickName: String, orderTime: Long = 0, isComplainUser: Int, orderRole: String) {
            var intent = Intent(context, OTCIMActivity::class.java)
            intent.putExtra(COMPLAINID, uid)
            intent.putExtra(IM_SYMBOL, symbol)
            intent.putExtra(ORDER_ID, orderId)
            intent.putExtra(TRANSACTION_AMOUNT, amount)
            intent.putExtra(AMOUNT_SYMBOL, amountSymbol)
            intent.putExtra(ORDER_TYPE, orderType)
            intent.putExtra(ORDER_TIME, orderTime)
            intent.putExtra(IM_TIME, time)
            intent.putExtra(TO_ID, toId)
            intent.putExtra(ORDER_ROLE, orderRole)
            intent.putExtra(IM_NICKNAME, nickName)
            intent.putExtra(ISCOMPLAINUSER, isComplainUser)
            intent.putExtra(TYPE, DETAILS_PROBLEM)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otc_im)

    }


}