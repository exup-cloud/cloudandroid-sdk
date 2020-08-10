package com.yjkj.chainup.new_version.activity.leverage

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * 借贷记录详情
 */
@Route(path = RoutePath.BorrowRecordsActivity)
class BorrowRecordsActivity : NBaseActivity() {


    override fun setContentView() = R.layout.activity_borrow_records


}
