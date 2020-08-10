package com.yjkj.chainup.new_version.activity.asset

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2019/5/16-10:11 AM
 * @Email buptjinlong@163.com
 * @description 提币
 */
@Route(path = RoutePath.WithdrawActivity)
class WithdrawActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_withdraw


}