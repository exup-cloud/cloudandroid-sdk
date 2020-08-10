package com.yjkj.chainup.new_version.redpackage

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author: Bertking
 * @Date：2019/6/29-16:26 AM
 * @Description: 发红包界面
 *
 * Dialog:dialog_create_red_package
 */

@Route(path = RoutePath.CreateRedPackageActivity)
class CreateRedPackageActivity : NewBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_red_package)

    }



}
