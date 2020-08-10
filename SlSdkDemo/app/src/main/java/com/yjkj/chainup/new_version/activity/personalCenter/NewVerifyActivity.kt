package com.yjkj.chainup.new_version.activity.personalCenter

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2019/3/31-6:23 PM
 * @Email buptjinlong@163.com
 * @description  验证页面
 */
@Route(path = RoutePath.NewVerifyActivity)
class NewVerifyActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_verify_mobile_mail_google
    }


}