package com.yjkj.chainup.new_version.activity.b2c

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description:资金流水(B2C)
 * @author Bertking
 * @date 2019-10-22 AM
 */
@Route(path = RoutePath.B2CCashFlowActivity)
class B2CCashFlowActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_b2_ccash_flow


}
