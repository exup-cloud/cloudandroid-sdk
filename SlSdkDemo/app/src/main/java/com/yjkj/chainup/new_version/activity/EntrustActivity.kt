package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2020-03-10-16:07
 * @Email buptjinlong@163.com
 * @description
 */
@Route(path = RoutePath.EntrustActivity)
class EntrustActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_entrust

}