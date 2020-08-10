package com.yjkj.chainup.new_version.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R

/**
 * @description 币种
 * @author Bertking
 * @date 2018-6-7
 *
 * 主要是进行  选择&搜素币种 not the CoinMap(币对)
 * 入口：
 *  1. 资金流水；
 *  2. 充值,的选择币种;
 *  3. 提现,的选择币种;
 *
 */
class CoinActivity : NewBaseActivity() {



    companion object {
        const val SELECTED_COIN = "selected_coin"
        const val SELECTED_ID = "selected_id"
        const val SELECTED_STATUS = "selected_status"
        const val SELECTED_TYPE = "SELECTED_TYPE"
        const val HAS_ALL = "has_all"
        const val OTC_TYPE = "OTC_TYPE"
        const val OTC_CONTRACT = "OTC_CONTRACT"
        const val COIN_REQUEST_CODE = 2018
        /**
         * @param type 币种显示取值  otc  取币对中otcOpen ==1的  contract 目前只有btc
         */
        fun enter4Result(context: Context, selectedCoin: String, needHasAll: Boolean, position: Int, cashStatus: Boolean = false, type: String = "") {
            var intent = Intent(context, CoinActivity::class.java)
            intent.putExtra(SELECTED_COIN, selectedCoin)
            intent.putExtra(HAS_ALL, needHasAll)
            intent.putExtra(SELECTED_STATUS, cashStatus)
            intent.putExtra(SELECTED_ID, position)
            intent.putExtra(SELECTED_TYPE, type)
            (context as Activity).startActivityForResult(intent, COIN_REQUEST_CODE)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_coin)

    }


}
