package com.yjkj.chainup.new_version.redpackage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity


/**
 * @Author: Bertking
 * @Date：2019/7/3-14:26 AM
 * @Description: 红包详情
 */
class RedPackageDetailActivity : NewBaseActivity() {



    companion object {
        const val SIGN = "sign"
        private const val IS_GRANT = "is_grant"
        fun enter2(context: Context, sign: String, isGrant: Boolean = true) {
            val intent = Intent(context, RedPackageDetailActivity::class.java)
            intent.putExtra(SIGN, sign)
            intent.putExtra(IS_GRANT, isGrant)
            context.startActivity(intent)
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_red_package_detail)
    }

}
