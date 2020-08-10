package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description : 币对行情的详细界面
 * @date 2019-3-2
 * @author Bertking
 *
 */
@Route(path = RoutePath.MarketDetail4Activity)
class MarketDetail4Activity : NBaseActivity(){

    companion object {
        /**
         * K线刻度
         */
        const val KLINE_SCALE = 1
        /**
         * K线指标
         */
        const val KLINE_INDEX = 2

    }

    override fun setContentView(): Int = R.layout.activity_market_detail4


}
