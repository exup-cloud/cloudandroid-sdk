package com.yjkj.chainup.contract.adapter

import android.app.Activity
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractAccount
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.activity.SlCoinDetailActivity
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.util.Utils
import java.text.DecimalFormat

class SlContractAssetAdapter (context:Context,data: ArrayList<ContractAccount>) : BaseQuickAdapter<ContractAccount, BaseViewHolder>(R.layout.sl_item_contract_asset, data) {

    var accountEquity= ""
    var walletBalance = ""
    var marginBalance = ""


    init {
        //账户权益
        accountEquity = context.getLineText("contract_assets_account_equity")
        //钱包余额
        walletBalance = context.getLineText("sl_str_wallet_balance")
        //保证金余额
        marginBalance = context.getLineText("sl_str_margin_balance")
    }


    override fun convert(helper: BaseViewHolder?, item: ContractAccount?) {
        item?.let { it->
            //币种名称
            helper?.setText(R.id.tv_coin_name,  it.coin_code)
            val contract: Contract? = ContractPublicDataAgent.getContract(it.contract_id)
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(NCoinManager.getCoinShowPrecision(it.coin_code))
            val freezeVol: Double = MathHelper.round(it.freeze_vol)
            val availableVol: Double = MathHelper.round(it.available_vol)

            helper?.getView<TextView>(R.id.tv_account_equity_label)?.text = accountEquity
            helper?.getView<TextView>(R.id.tv_wallet_balance_label)?.text = walletBalance
            helper?.getView<TextView>(R.id.tv_margin_balance_label)?.text = marginBalance

            var longProfitAmount = 0.0 //多仓位的未实现盈亏
            var shortProfitAmount = 0.0 //空仓位的未实现盈亏
            var positionMargin = 0.0
            val contractPositions: List<ContractPosition>? = ContractUserDataAgent.getCoinPositions(it.coin_code)
            if (contractPositions != null && contractPositions.isNotEmpty()) {
                for (i in contractPositions.indices){
                    val contractPosition = contractPositions[i]
                    val positionContract = ContractPublicDataAgent.getContract(contractPosition.instrument_id)
                    val contractTicker: ContractTicker? = ContractPublicDataAgent.getContractTicker(contractPosition.instrument_id)
                    if (positionContract == null || contractTicker == null) {
                        continue
                    }
                    positionMargin += MathHelper.round(contractPosition.im)
                    if (contractPosition.side === 1) { //开多
                        longProfitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    } else if (contractPosition.side === 2) { //开空
                        shortProfitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    }
                }
            }
            val balance = MathHelper.add(freezeVol, availableVol)
            val packBalance = MathHelper.add(balance, positionMargin)
            var isShowAssets = UserDataService.getInstance().isShowAssets
            val index = contract?.value_index?: 6
            //账户权益
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_normal_balance), dfDefault.format(MathHelper.round(balance + positionMargin + longProfitAmount + shortProfitAmount, index)))
            //钱包余额
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_available_value), dfDefault.format(MathHelper.round(packBalance, index)))
            //保证金余额
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_margin_balance_value), dfDefault.format(MathHelper.round(availableVol, index)))
            //跳转到币种详情
            helper?.getView<LinearLayout>(R.id.rl_header_layout)?.setOnClickListener {
                SlCoinDetailActivity.show(mContext as Activity, item.coin_code)
            }
        }
    }
}
