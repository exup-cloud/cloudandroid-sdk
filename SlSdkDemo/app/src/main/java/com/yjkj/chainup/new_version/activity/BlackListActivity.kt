package com.yjkj.chainup.new_version.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R

/***
 * @date 2018-10-15
 * @author Bertking
 * @description 屏蔽的人页面
 */
class BlackListActivity : NewBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black_list)
    }

    companion object {
        fun enter2(context: Context) {
            context.startActivity(Intent(context, BlackListActivity::class.java))
        }
    }


}
