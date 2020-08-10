package com.yjkj.chainup.new_version.activity.personalCenter

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/24-9:30 AM
 * @Email buptjinlong@163.com
 * @description 实名制认证
 */
@Route(path = RoutePath.RealNameCertificationActivity)
class RealNameCertificationActivity : NewBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realname_certification_choose_countries)

    }


}