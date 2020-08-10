package com.yjkj.chainup.new_version.activity.asset

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2019-11-09-10:44
 * @Email buptjinlong@163.com
 * @description  借贷
 */
@Route(path = RoutePath.NewVersionBorrowingActivity)
class NewVersionBorrowingActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_borrowing

}