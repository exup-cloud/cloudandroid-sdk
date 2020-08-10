package com.yjkj.chainup.new_version.activity.personalCenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity
import com.yjkj.chainup.new_version.adapter.OTCChangeLanguageAdapter

/**
 * @Author lianshangljl
 * @Date 2018/10/13-下午5:37
 * @Email buptjinlong@163.com
 * @description 选择国家
 */
class OTCChooseLanguageActivity : NewBaseActivity() {

    var adapter: OTCChangeLanguageAdapter? = null

    companion object {
        fun newIntent(context: Context) {
            context.startActivity(Intent(context, OTCChooseLanguageActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otc_choose_countries)

    }



}