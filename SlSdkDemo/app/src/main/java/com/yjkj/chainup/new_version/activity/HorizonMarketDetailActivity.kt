package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.home.MyWebSocketManager

/**
 * @description : 横屏行情的详细界面
 * @date 2019-3-16
 * @author Bertking
 *
 */
@Route(path = RoutePath.HorizonMarketDetailActivity)
class HorizonMarketDetailActivity : NBaseActivity(){

    override fun setContentView(): Int {
        return R.layout.activity_horizon_market_detail
    }


}
