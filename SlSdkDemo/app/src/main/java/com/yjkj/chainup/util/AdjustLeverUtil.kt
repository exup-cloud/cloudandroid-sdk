package com.yjkj.chainup.util

import android.app.Activity
import android.content.Context
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @Author: Bertking
 * @Date：2019-05-09-11:20
 * @Description:
 */
class AdjustLeverUtil(context: Context, contractId: Int, currentLever: String,listener: AdjustLeverListener) {

    var leverDialog: TDialog? = null
    var listener: AdjustLeverListener? = null


    interface AdjustLeverListener {
        fun adjustSuccess(value: String)
        fun adjustFailed(msg: String)
    }


    init {
        val levels = Contract2PublicInfoManager.getLevelsByContractId(contractId)
        levels.indices.forEach {
            levels[it] = levels[it] + "X"
        }

        leverDialog = NewDialogUtils.showBottomListDialog(context, levels, levels.indexOf(currentLever + "X"), object : NewDialogUtils.DialogOnclickListener {
            override fun clickItem(data: ArrayList<String>, item: Int) {
                leverDialog?.dismissAllowingStateLoss()
                changeLevel(context, contractId, Contract2PublicInfoManager.getLevelsByContractId(contractId)[item],listener)
            }
        })
    }


    /**
     * 修改杠杆
     */
    private fun changeLevel(context: Context, contractId: Int, newLever: String,listener: AdjustLeverListener) {
//        if (!LoginManager.checkLogin(this, false)) return
        HttpClient.instance
                .changeLevel4Contract(contractId = contractId.toString(), newLevel = newLever)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<Any>() {
                    override fun onHandleSuccess(bean: Any?) {
                        listener.adjustSuccess(value = newLever)
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        listener.adjustFailed(msg ?: "")
                        NToastUtil.showTopToast(false, msg)
                    }
                })
    }
}