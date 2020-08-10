package com.yjkj.chainup.new_version.activity.b2c

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @description:资金流水详情(B2C)
 * @author Bertking
 * @date 2019-10-23 AM
 */
@Route(path = RoutePath.B2CCashFlowDetailActivity)
class B2CCashFlowDetailActivity : NBaseActivity() {

    @JvmField
    @Autowired(name = "isRecharge")
    var isRecharge = true

    @JvmField
    @Autowired(name = "detail_data")
    var detailData = ""


    override fun setContentView() = R.layout.activity_b2_ccash_flow_detail



}
