package com.bmtc.sdk.contract.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting
import com.contract.sdk.data.Contract
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil

object ContractUtils {

    fun getHoldVolUnit(context: Context,contract: Contract?): String? {
        if (contract == null) {
            return context?.getString(R.string.sl_str_contracts_unit)
        }
        val unit = LogicContractSetting.getContractUint(context)
        if (unit == 0) {
            return context?.getString(R.string.sl_str_contracts_unit)
        } else if (unit == 1) {
            return if (contract.isReserve) {
                contract.base_coin
            } else {
                contract.base_coin
            }
        }
        return context?.getString(R.string.sl_str_contracts_unit)
    }


    fun getVolUnit(context: Context?, contract: Contract?, vol: String?, price: String?): String? {
        if (contract == null) {
            return "0"
        }
        val dfVol0 = NumberUtil.getDecimal(contract.vol_index)
        val dfVol = NumberUtil.getDecimal(-1)
        val unit = LogicContractSetting.getContractUint(context)
        if (unit == 0) {
            return dfVol0.format(MathHelper.round(vol)) + context?.getString(R.string.sl_str_contracts_unit)
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
            return dfVol0.format(vol) + context?.getString(R.string.sl_str_contracts_unit)
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
            context: Context,
            vol: String?,
            price: String?,
            contract: Contract?): String {
        val unit = LogicContractSetting.getContractUint(context)
        if (MathHelper.round(vol) <= 0.0 || MathHelper.round(price) <= 0.0 || contract == null) {
            return "0" + if (unit == 0) contract?.base_coin else context?.getString(R.string.sl_str_contracts_unit)
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
                dfVol.format(MathHelper.round(vol)) + context?.getString(R.string.sl_str_contracts_unit)
            } else {
                dfVol.format(amount) + context?.getString(R.string.sl_str_contracts_unit)
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
    fun getColorAtAlpha(color: Int, alpha: Int): Int {
        require(!(alpha < 0 || alpha > 255)) { "The alpha should be 0 - 255." }
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }


}