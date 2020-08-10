package com.yjkj.chainup.new_version.redpackage

import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author: Bertking
 * @Date：2019/6/29-16:26 AM
 * @Description: 收到红包的列表界面
 *
 */
class ReceiveRedPackageListActivity : NewBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_red_package_list)
    }
}
