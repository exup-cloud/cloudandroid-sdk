package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description 新版的切换币种
 * @author Bertking
 * @date 2019-4-16
 *
 * 主要是进行  选择&搜素币种 not the CoinMap(币对)
 * 入口：
 *  1. 资金流水；
 *  2. 充值,的选择币种;
 *  3. 提现,的选择币种;
 */
@Route(path = RoutePath.SelectCoinActivity)
class SelectCoinActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_search_coin
    }


}