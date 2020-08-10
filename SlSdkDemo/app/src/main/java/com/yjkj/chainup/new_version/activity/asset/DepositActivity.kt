package com.yjkj.chainup.new_version.activity.asset

import android.content.Context
import android.content.Intent
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity

/**
 * @Author: Bertking
 * @Date：2019/4/17-16:26 AM
 * @Description: 充币4.0
 */
class DepositActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_deposit

    var symbol = ""
    var showSymbol = ""


    companion object {
        const val RECHAEGE_SYMBOL = "RECHAEGE_SYMBOL"
        fun enter2(context: Context, symbol: String?) {
            val intent = Intent(context, DepositActivity::class.java)
            intent.putExtra(RECHAEGE_SYMBOL, symbol)
            context.startActivity(intent)
        }
    }


}
