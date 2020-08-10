package com.yjkj.chainup.new_version.activity.personalCenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/4/1-10:54 AM
 * @Email buptjinlong@163.com
 * @description  绑定手机号 or 修改手机号
 */
class BindMobileOrEmailActivity : NewBaseActivity() {

    var bindType = 0
    var validationType = "VALIDATION_BIND"
    /**
     * 账号
     */
    var accountNumber = ""
    var country = ""
    var areaCode = ""


    companion object {
        /**
         * 手机号
         */
        const val MOBILE_TYPE = 0
        /**
         * 邮箱
         */
        const val MAIL_TYPE = 1
        const val VERIFY_TYPE = "VERIFY_TYPE"
        const val BIND_OR_CHANGE = "BIND_OR_CHANGE"
        const val VALIDATION_BIND = "VALIDATION_BIND"
        const val VALIDATION_CHANGE = "VALIDATION_CHANGE"

        /**
         *  绑定账号或者修改
         * @param type 端 0 手机 1 邮箱
         * @param validationType 手机或者邮箱
         */
        fun enter2(context: Context, type: Int, validationType: String) {
            var intent = Intent()
            intent.setClass(context, BindMobileOrEmailActivity::class.java)
            intent.putExtra(VERIFY_TYPE, type)
            intent.putExtra(BIND_OR_CHANGE, validationType)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_mobile_or_email)

    }

}