package com.yjkj.chainup.new_version.activity.leverage

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * 历史借贷
 */
@Route(path = RoutePath.HistoryLoanActivity)
class HistoryLoanActivity : NBaseActivity() {


    override fun setContentView() = R.layout.activity_history_loan

}
