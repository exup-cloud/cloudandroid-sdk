package com.yjkj.chainup.new_version.activity.personalCenter

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath

/**
 * @Author lianshangljl
 * @Date 2019/5/20-9:29 AM
 * @Email buptjinlong@163.com
 * @description 实名制认证成功页面
 */
@Route(path = RoutePath.RealNameCertificaionSuccessActivity)
class RealNameCertificaionSuccessActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_real_name_success

}
