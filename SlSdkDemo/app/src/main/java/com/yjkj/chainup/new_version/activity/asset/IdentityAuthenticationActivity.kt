package com.yjkj.chainup.new_version.activity.asset

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2020-03-18-14:21
 * @Email buptjinlong@163.com
 * @description
 */
@Route(path = RoutePath.IdentityAuthenticationActivity)
class IdentityAuthenticationActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_identity_authentication_layout


}