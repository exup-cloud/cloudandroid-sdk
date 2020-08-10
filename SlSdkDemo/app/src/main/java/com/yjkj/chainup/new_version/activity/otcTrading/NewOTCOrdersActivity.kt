package com.yjkj.chainup.new_version.activity.otcTrading

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/9-11:34 AM
 * @Email buptjinlong@163.com
 * @description 我的订单
 */
@Route(path = RoutePath.NewOTCOrdersActivity)
class NewOTCOrdersActivity : NewBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_otc_orders)

    }


}