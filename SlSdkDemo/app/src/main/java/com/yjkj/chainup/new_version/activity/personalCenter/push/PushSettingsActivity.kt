package com.yjkj.chainup.new_version.activity.personalCenter.push

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author yishanxin
 * @Date 2019/3/27-5:35 PM
 * @Email
 * @description 设置页面
 */
class PushSettingsActivity : NewBaseActivity() {

    companion object {
        fun enter2(context: Context) {
            var intent = Intent()
            intent.setClass(context, PushSettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_settings)

    }


}