package com.yjkj.chainup.new_version.activity

import android.content.Context
import android.content.Intent
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity

/**
 * @Author: Bertking
 * @Date：2019/5/14-10:26 AM
 * @Description: 资金流水详情
 */
class CashFlowDetailActivity : NBaseActivity() {
    var type = ""

    companion object {

        fun enter2(context: Context) {
            val intent = Intent(context, CashFlowDetailActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun setContentView(): Int {
        return R.layout.activity_cashflow_detail
    }


}
