package com.yjkj.chainup.new_version.activity.personalCenter

import android.os.Bundle
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @author bertking
 * @date 2018,5,21
 * @description 安全设置
 * TODO 代码待优化....
 */
@Route(path = RoutePath.SafetySettingActivity)
class SafetySettingActivity : NewBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_setting)

    }



}
