package com.yjkj.chainup.new_version.activity.personalCenter

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath


/**
 * @author Bertking
 * @description 个人资料
 * @date 2018-6-5
 */

@Route(path = RoutePath.PersonalInfoActivity)
class PersonalInfoActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_personal_info
    }



}

