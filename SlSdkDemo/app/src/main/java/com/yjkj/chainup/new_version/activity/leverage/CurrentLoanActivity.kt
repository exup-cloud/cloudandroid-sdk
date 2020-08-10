package com.yjkj.chainup.new_version.activity.leverage

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * 当前借贷
 * CurrentLoan4LeverAdapter
 */
@Route(path = RoutePath.CurrentLoanActivity)
class CurrentLoanActivity : NBaseActivity() {

    override fun setContentView() = R.layout.activity_current_loan

}
