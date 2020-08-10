package com.yjkj.chainup.new_version.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.extra_service.arouter.ArouterUtil


/**
 * @author Bertking
 * @date 2018-11-19
 * @description 找回密码的step 2 -- 身份认证
 */
@Route(path = "/login/findpwdverifyactivity")
class FindPwd2verifyActivity : NewBaseActivity() {


    companion object {

        const val TOKEN = "token"
        const val HAVE_ID = "id"
        const val HAVE_GOOGLE = "google"
        const val ACCOUNT_CONTENT = "account_content"
        const val PATH_KEY = "/login/findpwdverifyactivity"

        fun enter2(token: String, haveID: Int, haveGoogle: Int, account: String = "") {
            var bundle = Bundle()
            bundle.putString(TOKEN, token)
            bundle.putInt(HAVE_ID, haveID)
            bundle.putInt(HAVE_GOOGLE, haveGoogle)
            bundle.putString(ACCOUNT_CONTENT, account)
            ArouterUtil.navigation(PATH_KEY, bundle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findpwd2verify)
        context = this


    }

}
