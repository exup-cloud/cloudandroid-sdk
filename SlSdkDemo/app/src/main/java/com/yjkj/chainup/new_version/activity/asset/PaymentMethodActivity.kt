package com.yjkj.chainup.new_version.activity.asset

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @date 2018-10-13
 * @author Bertking
 * @description 收款方式
 */
@Route(path = RoutePath.PaymentMethodActivity)
class PaymentMethodActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_payment_method
    }


}
