package com.yjkj.chainup.new_version.activity.asset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019/5/16-9:55 PM
 * @Email buptjinlong@163.com
 * @description 确认提币页面
 */
class ConfirmWithdrawActivity : NewBaseActivity() {


    var addressStatus = false
    var addressLabel = ""
    var addrTag = ""
    var addrContent = ""
    var addressId = ""
    var showSymbol = ""
    var symbol = ""
    var amount = ""
    var actualaMount = ""
    var fee = ""

    companion object {
        const val ADDRESSLABEL = "addressLabel"
        const val ADDRESSID = "addressId"
        const val ADDRTAG = "addrTag"
        const val ADDRCONTENT = "addrContent"
        const val SHOWSYMBOL = "showSymbol"
        const val SYMBOL = "Symbol"
        const val AMOUNT = "amount"
        const val FEE = "fee"
        const val ADDRESSSTATUS = "addressStatus"
        const val ACTUALAMOUNT = "actualaMount"
        fun enter(context: Context, addressId: String?, addressLabel: String?,
                  addrTag: String?, addrContent: String?, showSymbol: String?,
                  amount: String?, fee: String?, addressStatus: Boolean, symbol: String?, actualaMount: String?) {
            var intent = Intent()
            intent.setClass(context, ConfirmWithdrawActivity::class.java)
            intent.putExtra(ADDRESSLABEL, addressLabel)
            intent.putExtra(ADDRTAG, addrTag)
            intent.putExtra(ADDRCONTENT, addrContent)
            intent.putExtra(SHOWSYMBOL, showSymbol)
            intent.putExtra(SYMBOL, symbol)
            intent.putExtra(ADDRESSID, addressId)
            intent.putExtra(AMOUNT, amount)
            intent.putExtra(FEE, fee)
            intent.putExtra(ADDRESSSTATUS, addressStatus)
            intent.putExtra(ACTUALAMOUNT, actualaMount)
            context.startActivity(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_withdraw)

    }

}