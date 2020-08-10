package com.yjkj.chainup.new_version.activity.b2c

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description:银行账户列表(B2C)
 * @author Bertking
 * @date 2019-10-24 AM
 *
 *
 *
 */
@Route(path = RoutePath.B2CBankListActivity)
class B2CBankListActivity : NBaseActivity() {

    override fun setContentView() = R.layout.activity_bank_list

}
