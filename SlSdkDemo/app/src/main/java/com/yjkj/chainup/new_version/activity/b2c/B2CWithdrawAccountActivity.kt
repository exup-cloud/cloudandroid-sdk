package com.yjkj.chainup.new_version.activity.b2c

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description:添加&修改&查看提现账户(B2C)
 * @author Bertking
 * @date 2019-10-24 AM
 */
@Route(path = RoutePath.B2CWithdrawAccountActivity)
class B2CWithdrawAccountActivity : NBaseActivity() {



    override fun setContentView() = R.layout.activity_b2_cadd_withdraw_account



}
