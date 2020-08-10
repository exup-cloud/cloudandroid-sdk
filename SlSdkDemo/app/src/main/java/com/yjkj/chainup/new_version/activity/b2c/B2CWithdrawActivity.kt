package com.yjkj.chainup.new_version.activity.b2c

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description:提现(B2C)
 * @author Bertking
 * @date 2019-10-23 AM
 */
@Route(path = RoutePath.B2CWithdrawActivity)
class B2CWithdrawActivity : NBaseActivity() {

    override fun setContentView() = R.layout.activity_b2_cwithdraw

}
