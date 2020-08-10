package com.yjkj.chainup.new_version.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R


/**
 * @description:资金流水(4.0)
 * @author Bertking
 * @date 2019-5-15 AM
 *
 */

class CashFlow4Activity : NewBaseActivity() {


    companion object {
        const val TRANSACTIONSCENE = "TRANSACTIONSCENE"
        fun enter2(context: Context, transactionScene: String) {
            val intent = Intent(context, CashFlow4Activity::class.java)
            intent.putExtra(TRANSACTIONSCENE, transactionScene)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_flow4)

    }



}
