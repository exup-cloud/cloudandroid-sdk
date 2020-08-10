package com.yjkj.chainup.new_version.activity.otcTrading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/24-10:03 AM
 * @Email buptjinlong@163.com
 * @description 我的订单
 */
class MineOrderActivity : NewBaseActivity() {

    var status = ""

    companion object {
        const val ORDER_STATUS = "ORDER_STATUS"
        fun enter2(context: Context, status: String) {
            var intent = Intent()
            intent.setClass(context, MineOrderActivity::class.java)
            intent.putExtra(ORDER_STATUS, status)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_order_activity)
    }

}