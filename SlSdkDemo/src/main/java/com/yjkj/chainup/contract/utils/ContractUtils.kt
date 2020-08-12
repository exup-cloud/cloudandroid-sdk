package com.yjkj.chainup.contract.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.RateManager.Companion.getRatesByCoinName

object ContractUtils {
    /**
     * 计算合约总资产
     * @param coinType 当前资产展示单位 如：BTC USDT ETH等
     * @return
     */
    fun calculateTotalBalance(coinType: String?): Double {
        val contractAccount = ContractUserDataAgent.getContractAccounts() ?: return 0.0
        var total_balance = 0.0
        for (i in contractAccount.indices) {
            val account = contractAccount[i]

            if (account == null || ContractPublicDataAgent.isVirtualCoin(account.coin_code)) {
                continue
            }
            val freeze_vol = MathHelper.round(account.freeze_vol)
            val available_vol = MathHelper.round(account.available_vol)
            var longProfitAmount = 0.0 //多仓位的未实现盈亏
            var shortProfitAmount = 0.0 //空仓位的未实现盈亏
            var position_margin = 0.0
            val contractPositions = ContractUserDataAgent.getCoinPositions(account.coin_code)
            if (contractPositions != null && contractPositions.size > 0) {
                for (j in contractPositions.indices) {
                    val contractPosition = contractPositions[j]
                    val positionContract = ContractPublicDataAgent.getContract(contractPosition.instrument_id)
                    val contractTicker = ContractPublicDataAgent.getContractTicker(contractPosition.instrument_id)
                    if (positionContract == null || contractTicker == null) {
                        continue
                    }
                    position_margin += MathHelper.round(contractPosition.im)
                    if (contractPosition.side == 1) { //开多

                        longProfitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    } else if (contractPosition.side == 2) { //开空
                        shortProfitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    }
                }
            }
            val balance = MathHelper.add(freeze_vol, available_vol)
            val total = balance + position_margin + longProfitAmount + shortProfitAmount
            total_balance += if (TextUtils.equals(coinType, account.coin_code)) {
                total
            } else {
                val btcRate = java.lang.Double.valueOf(getRatesByCoinName(coinType))
                val coinRate = java.lang.Double.valueOf(getRatesByCoinName(account.coin_code))
                MathHelper.div(MathHelper.mul(total, coinRate), btcRate)
            }
        }
        return total_balance
    }

    fun getHoldVolUnit(contract: Contract?): String? {
        if (contract == null) {
            return LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
        }
        val unit = LogicContractSetting.getContractUint(ContractSDKAgent.context)
        if (unit == 0) {
            return LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
        } else if (unit == 1) {
            return if (contract.isReserve) {
                contract.base_coin
            } else {
                contract.base_coin
            }
        }
        return LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
    }


    fun getVolUnit(context: Context?, contract: Contract?, vol: String?, price: String?): String? {
        if (contract == null) {
            return "0"
        }
        val dfVol0 = NumberUtil.getDecimal(contract.vol_index)
        val dfVol = NumberUtil.getDecimal(-1)
        val unit = LogicContractSetting.getContractUint(context)
        if (unit == 0) {
            return dfVol0.format(MathHelper.round(vol)) + LanguageUtil.getString(context, "sl_str_contracts_unit")
        } else if (unit == 1) {
            return if (contract.isReserve) {
                val bVol = MathHelper.div(MathHelper.mul(vol, contract.face_value), MathHelper.round(price))
                dfVol.format(bVol) + contract.base_coin
            } else {
                val bVol = MathHelper.mul(vol, contract.face_value)
                dfVol.format(bVol) + contract.base_coin
            }
        }
        return "0"
    }

    fun getVolUnit(context: Context?, contract: Contract?, vol: Double, price: Double): String {
        if (contract == null) {
            return "0"
        }
        val dfVol0 = NumberUtil.getDecimal(contract.vol_index)
        val dfVol = NumberUtil.getDecimal(-1)
        val unit = LogicContractSetting.getContractUint(context)
        if (unit == 0) {
            return dfVol0.format(vol) + LanguageUtil.getString(context, "sl_str_contracts_unit")
        } else if (unit == 1) {
            return if (contract.isReserve) {
                val bVol = MathHelper.div(MathHelper.mul(vol, MathHelper.round(contract.face_value)), price)
                dfVol.format(bVol) + contract.base_coin
            } else {
                val bVol = MathHelper.mul(vol, MathHelper.round(contract.face_value))
                dfVol.format(bVol) + contract.base_coin
            }
        }
        return "0"
    }

    // CalculateContractBasicValue 通过量和价格计算合约的基础比价值
// coinUnit true 单位为张 false 单位和BaseCoin单位一致
    fun CalculateContractBasicValue(
            vol: String?,
            price: String?,
            contract: Contract?): String {
        val unit = LogicContractSetting.getContractUint(ContractSDKAgent.context)
        if (MathHelper.round(vol) <= 0.0 || MathHelper.round(price) <= 0.0 || contract == null) {
            return "0" + if (unit == 0) contract?.base_coin else LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
        }
        var amount: Double
        amount = if (contract.isReserve) {
            MathHelper.div(vol, price)
        } else {
            MathHelper.round(vol)
        }
        val dfVol = NumberUtil.getDecimal(contract.vol_index)
        val dfValue = NumberUtil.getDecimal(-1)
        return if (unit == 0) {
            amount = MathHelper.mul(amount, MathHelper.round(contract.face_value))
            dfValue.format(amount) + contract.base_coin
        } else {
            if (contract.isReserve) {
                dfVol.format(MathHelper.round(vol)) + LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
            } else {
                dfVol.format(amount) + LanguageUtil.getString(ContractSDKAgent.context, "sl_str_contracts_unit")
            }
        }
    }


    fun safeOpenUrl(context: Context, url: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val content_url = Uri.parse(url)
        intent.data = content_url
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }


    /**
     * 是否中文
     * @param context
     * @return
     */
    fun isZhEnv(context: Context?): Boolean {
        val language = LanguageUtil.getSelectLanguage()
        return TextUtils.equals(language, "zh_CN") || TextUtils.equals("zh", language)
    }

}