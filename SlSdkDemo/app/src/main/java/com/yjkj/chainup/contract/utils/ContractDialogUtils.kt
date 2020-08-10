package com.yjkj.chainup.contract.utils

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.base.BindViewHolder
import com.yjkj.chainup.R

/**
 * @author ZhongWei
 * @time 2020/7/7 18:49
 * @description DialogUtils
 **/
object ContractDialogUtils {

    /**
     * 显示正常的提示框
     * 合约平仓买卖提示框
     */
    fun showNormalDialog(context: Context?, titleKey: String? = "", submitKey: String? = "common_text_btnConfirm".localized(), cancelKey: String? = "common_text_btnCancel".localized(), content: String? = "",
                         submitListener: (() -> Unit?)? = null, cancelListener: (() -> Unit?)? = null, warnContent: String? = "") {
        if (context == null || context !is AppCompatActivity) {
            return
        }
        if (context.isDestroyed || context.isFinishing) {
            return
        }
        TDialog.Builder(context.supportFragmentManager)
                .setLayoutRes(R.layout.sl_dialog_view_normal)
                .setScreenWidthAspect(context, 0.8f)
                .setGravity(Gravity.CENTER)
                .setDimAmount(0.8f)
                .setCancelableOutside(false)
                .setOnBindViewListener { viewHolder: BindViewHolder? ->
                    viewHolder?.let {
                        if (!TextUtils.isEmpty(titleKey)) {
                            it.setGone(R.id.tv_title, true)
                                    .setText(R.id.tv_title, titleKey)
                        }
                        if (!TextUtils.isEmpty(submitKey)) {
                            it.setText(R.id.tv_confirm_btn, submitKey)
                        }
                        if (!TextUtils.isEmpty(cancelKey)) {
                            it.setGone(R.id.tv_cancel_btn, true)
                                    .setText(R.id.tv_cancel_btn, cancelKey)
                        }
                        if (!TextUtils.isEmpty(content)) {
                            it.setGone(R.id.tv_content, true)
                                    .setText(R.id.tv_content, Html.fromHtml(content))
                        }
                        if (!TextUtils.isEmpty(warnContent)) {
                            it.setGone(R.id.tv_warn, true)
                                    .setText(R.id.tv_warn, Html.fromHtml(warnContent))
                        }
                    }
                }
                .addOnClickListener(R.id.tv_confirm_btn, R.id.tv_cancel_btn)
                .setOnViewClickListener { _, view, tDialog ->
                    when (view.id) {
                        R.id.tv_cancel_btn -> {
                            cancelListener?.invoke()
                            tDialog.dismiss()
                        }
                        R.id.tv_confirm_btn -> {
                            submitListener?.invoke()
                            tDialog.dismiss()
                        }
                    }
                }
                .create()
                .show()
    }

}