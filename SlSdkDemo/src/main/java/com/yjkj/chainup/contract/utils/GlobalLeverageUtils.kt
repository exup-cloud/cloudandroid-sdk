package com.yjkj.chainup.contract.utils

import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.GlobalLeverage
import com.contract.sdk.impl.IResponse
import com.yjkj.chainup.contract.listener.SLDoListener
import com.yjkj.chainup.util.ToastUtils

/**
 * @author ZhongWei
 * @time 2020/7/21 13:45
 * @description 全局杠杆工具类
 **/
object GlobalLeverageUtils {

    /**
     * 是否开启全局杠杆
     */
    var isOpenGlobalLeverage = false

    /**
     * 当前选中合约
     */
    var contract: Contract? = null

    /**
     * 当前杠杆
     */
    var currLeverage: Int = 10

    /**
     * 杠杆类型
     */
    var currentPositionType: Int = 1

    /**
     * 更新杠杆UI
     */
    var updateLeverageListener: SLDoListener? = null

    /**
     * 绑定合约
     */
    fun bindContract(contract: Contract?): GlobalLeverageUtils {
        this.contract = contract
        loadDefaultLeverage()
        loadLeverage()
        return this
    }

    /**
     * 获取并设置杠杆
     */
    fun loadLeverage() {
        if (!isOpenGlobalLeverage) {
            return
        }
        if (ContractSDKAgent.isLogin) {
            contract?.let {
                ContractUserDataAgent.loadGlobalLeverage(it.instrument_id, UploadLeverageResponse(it.instrument_id))
            }
        }
    }

    /**
     * 设置杠杆
     */
    fun uploadLeverage(instrument_id: Int, leverage: Int, positionType: Int) {
        if (!isOpenGlobalLeverage) {
            return
        }
        ContractUserDataAgent.setGlobalLeverage(instrument_id, leverage, positionType, object : IResponse<MutableList<GlobalLeverage>>() {
            override fun onSuccess(data: MutableList<GlobalLeverage>) {
            }

            override fun onFail(code: String, msg: String) {
                super.onFail(code, msg)
                ToastUtils.showToast(msg)
            }
        })
    }

    /**
     * 设置杠杆回调
     */
    class UploadLeverageResponse(private val instrumentId: Int, private val isUpdate: Boolean = true) : IResponse<MutableList<GlobalLeverage>>() {

        override fun onSuccess(data: MutableList<GlobalLeverage>) {
            contract?.apply {
                if (instrument_id != instrumentId || !isUpdate) {
                    return
                }
                if (data.isNullOrEmpty()) {
                    uploadLeverage(instrument_id, currLeverage, currentPositionType)
                    return
                }
                val item = data[0]
                currLeverage = item.config_value.toInt()
                currentPositionType = item.position_type
                saveLeverage()
                updateLeverageListener?.doThing("updateLeverage")
            }
        }

        override fun onFail(code: String, msg: String) {
            super.onFail(code, msg)
            ToastUtils.showToast(msg)
        }
    }

    /**
     * 加载杠杆
     */
    private fun loadDefaultLeverage() {
        contract?.let {
            val minLeverage = it.min_leverage.toInt()
            val maxLeverage = it.max_leverage.toInt()
            val defaultLeverage = it.default_leverage
            val leverageKey = if (ContractSDKAgent.isLogin) {
                "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverage"
            } else {
                "${PreferenceManager.PREF_LEVERAGE}#${it.instrument_id}#leverage"
            }
            val leverageTypeKey = if (ContractSDKAgent.isLogin) {
                "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverageType"
            } else {
                "${PreferenceManager.PREF_LEVERAGE}#${it.instrument_id}#leverageType"
            }
            val localLeverage = PreferenceManager.getInstance(ContractSDKAgent.context).getSharedInt(leverageKey, 0)
            currentPositionType = PreferenceManager.getInstance(ContractSDKAgent.context).getSharedInt(leverageTypeKey, 1)
            currLeverage = when {
                localLeverage == 0 && defaultLeverage > 0 -> {
                    defaultLeverage
                }
                localLeverage in minLeverage..maxLeverage -> {
                    localLeverage
                }
                else -> {
                    minLeverage
                }
            }
        }
    }

    /**
     * 保存当前杠杆
     */
    fun saveLeverage() {
        contract?.let {
            val leverageKey = if (ContractSDKAgent.isLogin) {
                "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverage"
            } else {
                "${PreferenceManager.PREF_LEVERAGE}#${it.instrument_id}#leverage"
            }
            val leverageTypeKey = if (ContractSDKAgent.isLogin) {
                "${PreferenceManager.PREF_LEVERAGE}#${ContractSDKAgent.user.uid}#${it.instrument_id}#leverageType"
            } else {
                "${PreferenceManager.PREF_LEVERAGE}#${it.instrument_id}#leverageType"
            }
            PreferenceManager.getInstance(ContractSDKAgent.context).putSharedInt(leverageKey, currLeverage)
            PreferenceManager.getInstance(ContractSDKAgent.context).putSharedInt(leverageTypeKey, currentPositionType)
        }
    }

}