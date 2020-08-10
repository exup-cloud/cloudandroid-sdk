package com.yjkj.chainup.new_version.activity

import android.content.Context
import android.content.Intent
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity

class InnerBrowserActivity : NBaseActivity() {


    companion object {
        const val TITLE = "title"
        const val URL = "url"

        fun enter2(context: Context, title: String, url: String?) {
            var intent = Intent(context, InnerBrowserActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(URL, url)
            context.startActivity(intent)
        }

    }
    override fun setContentView() = R.layout.activity_inner_browser


}
