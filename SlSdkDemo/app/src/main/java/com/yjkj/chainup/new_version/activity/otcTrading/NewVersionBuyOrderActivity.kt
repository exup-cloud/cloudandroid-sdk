package com.yjkj.chainup.new_version.activity.otcTrading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/18-4:04 PM
 * @Email buptjinlong@163.com
 * @description  出售订单
 */
class NewVersionBuyOrderActivity : NewBaseActivity() {


    var orderId = ""

    companion object {
        val ORDERID = "orderId"

        fun enter2(context: Context, orderId: String) {
            var intent = Intent(context, NewVersionBuyOrderActivity::class.java)
            intent.putExtra(ORDERID, orderId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_buy_order)

    }


}


