package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath


/**
 * @date 2018-5-28
 * @description 搜索&添加币对
 *
 *
 * UI :https://lanhuapp.com/web/#/item/board/detail?pid=0ce4b503-1bb6-4195-a2e4-2a5bafc04f0e&project_id=0ce4b503-1bb6-4195-a2e4-2a5bafc04f0e&image_id=48dca8e8-1770-4e32-aeb1-ba89522e07bf
 */
@Route(path = RoutePath.CoinMapActivity)
class CoinMapActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_coin_map

}
