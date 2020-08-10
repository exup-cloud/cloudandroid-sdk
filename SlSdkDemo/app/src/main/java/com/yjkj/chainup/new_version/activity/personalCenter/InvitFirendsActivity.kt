package com.yjkj.chainup.new_version.activity.personalCenter

import android.graphics.Bitmap
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/6/18-9:33 PM
 * @Email buptjinlong@163.com
 * @description
 */
@Route(path = RoutePath.InvitFirendsActivity)
class InvitFirendsActivity : NewBaseActivity() {


    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invit_firends)

    }


}