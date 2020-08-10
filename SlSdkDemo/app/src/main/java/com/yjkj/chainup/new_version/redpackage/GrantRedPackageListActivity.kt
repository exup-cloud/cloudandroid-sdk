package com.yjkj.chainup.new_version.redpackage

import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author: Bertking
 * @Date：2019/7/1-14:26 AM
 * @Description: 发出红包的列表
 *
 * item_grant_red_package
 * GrantAdapter
 */
class GrantRedPackageListActivity : NewBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grant_red_package_list)
    }

}
