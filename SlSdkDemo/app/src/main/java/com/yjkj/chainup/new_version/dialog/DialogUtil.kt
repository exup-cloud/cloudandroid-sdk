package com.yjkj.chainup.new_version.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.coorchice.library.SuperTextView
import com.tencent.mmkv.MMKV
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.base.BindViewHolder
import com.timmy.tdialog.base.TBaseAdapter
import com.timmy.tdialog.list.TListDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.constant.WebTypeEnum
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.*
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.new_version.activity.leverage.NLeverFragment
import com.yjkj.chainup.new_version.fragment.NCVCTradeFragment
import com.yjkj.chainup.treaty.adapter.SelectLevelAdapter
import com.yjkj.chainup.util.*
import com.zyyoona7.popup.EasyPopup
import com.zyyoona7.popup.XGravity
import com.zyyoona7.popup.YGravity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cvctrade.*
import kotlinx.android.synthetic.main.fragment_nlever.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.math.BigDecimal

//Created by $USER_NAME on 2018/10/15.

class DialogUtil {

    interface ConfirmListener {
        fun click(pos: Int = 0)
    }

    companion object {
        fun showDialogWithTitle(context: Context, title: String, content: String, listener: ConfirmListener) {
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_confirm_pay_otc)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        viewHolder?.setText(R.id.tv_title, title)
                        viewHolder?.setText(R.id.tv_content, content)
                    }
                    .addOnClickListener(R.id.btn_cancel, R.id.btn_confirm)
                    .setOnViewClickListener { viewHolder, view, tDialog ->

                        when (view.id) {
                            R.id.btn_cancel -> {
                                tDialog.dismiss()
                            }

                            R.id.btn_confirm -> {
                                tDialog.dismiss()
                                listener.click()
                            }

                        }

                    }
                    .create()
                    .show()
        }

        fun showDialog(context: Context, title: String, listener: ConfirmListener) {
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_remove_black_otc)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        viewHolder?.setText(R.id.tv_title, title)
                        viewHolder?.setText(R.id.btn_confirm, LanguageUtil.getString(context, "common_text_btnConfirm"))
                        viewHolder?.setText(R.id.btn_cancel, LanguageUtil.getString(context, "common_text_btnCancel"))
                    }
                    .addOnClickListener(R.id.btn_cancel, R.id.btn_confirm)
                    .setOnViewClickListener { viewHolder, view, tDialog ->

                        when (view.id) {
                            R.id.btn_cancel -> {
                                tDialog.dismiss()
                            }

                            R.id.btn_confirm -> {
                                tDialog.dismiss()
                                listener.click()
                            }

                        }

                    }
                    .create()
                    .show()
        }

        @Deprecated("暂时不用")
        fun showBottomDialog(context: Context, titles: List<String>, listener: ConfirmListener) {
            TListDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_remove_black_otc)
                    .setScreenWidthAspect(context, 1.0f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelOutside(true)
                    .setAdapter(object : TBaseAdapter<String>(R.layout.dialog_bottom_menu, titles) {
                        override fun onBind(holder: BindViewHolder?, position: Int, t: String?) {
                            holder?.setText(R.id.tv_title, t)
                            holder?.setText(R.id.btn_confirm, LanguageUtil.getString(context, "common_text_btnConfirm"))
                            holder?.setText(R.id.btn_cancel, LanguageUtil.getString(context, "common_text_btnCancel"))
                        }

                    }).setOnAdapterItemClickListener { holder, position, t, tDialog ->
                        when (position) {
                            titles.lastIndex -> {
                                tDialog.dismiss()
                            }

                            else -> {
                                listener.click(pos = position)
                                tDialog.dismiss()
                            }
                        }

                    }.setGravity(Gravity.BOTTOM)
                    .create()
                    .show()

        }

        /**iv_cancel
         *  指纹识别 & 面部识别的Dialog
         */
        fun showFingerprintOrFaceIDDialog(context: Context, title: String = "", tips: String = ""): TDialog {

            return TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_fingerprint_faceid)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        if (title.isNotEmpty()) {
                            viewHolder?.setText(R.id.tv_title, title)
                        }
                        if (tips.isNotEmpty()) {
                            viewHolder?.setText(R.id.tv_tips, tips)
                        }
                        viewHolder?.setText(R.id.tv_title, LanguageUtil.getString(context, "login_text_fingerprint"))
                        viewHolder?.setText(R.id.iv_cancel, LanguageUtil.getString(context, "common_text_btnCancel"))

                    }
                    .addOnClickListener(R.id.iv_cancel)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.iv_cancel -> {
                                tDialog.dismiss()
                            }
                        }

                    }.setOnKeyListener(object : DialogInterface.OnKeyListener {
                        override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                            return true
                        }
                    })
                    .create().show()

        }

        /**
         * 选择杠杆
         */
        fun showSelectLevelDialog(context: Context, contractId: Int, curLever: String, listener: ConfirmListener): TDialog {
            val levels = Contract2PublicInfoManager.getLevelsByContractId(contractId)
            return TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_select_level)
                    .setScreenWidthAspect(context, 1.0f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .addOnClickListener(R.id.tv_cancel_level)
                    .setOnBindViewListener {
                        it.setText(R.id.tv_cancel_level, "common_text_btnCancel")
                        val rvSelectLevel = it.getView<RecyclerView>(R.id.rv_select_level)
                        rvSelectLevel.layoutManager = GridLayoutManager(context, 3)
                        val adapter = SelectLevelAdapter(levels, curLever)
                        rvSelectLevel.adapter = adapter
                        adapter.bindToRecyclerView(rvSelectLevel)
                        rvSelectLevel.setHasFixedSize(true)
                        adapter.setOnItemClickListener { adapter, _, position ->
                            val levelView = adapter?.getViewByPosition(position, R.id.tv_index_price)
                            (levelView as SuperTextView).solid = ContextCompat.getColor(context, R.color.main_color)
                            levelView.textColor = ContextCompat.getColor(context, R.color.white)
                            listener.click(pos = position)
                        }


                    }
                    .setOnViewClickListener { viewHolder, view, tDialog ->
                        when (view.id) {
                            R.id.tv_cancel_level -> {
                                tDialog.dismiss()
                            }
                        }

                    }
                    .setGravity(Gravity.BOTTOM)
                    .create()

        }


        /**
         * 币币界面的popupWindow
         */
        fun createCVCPop(context: Context?, targetView: View, fragment: NCVCTradeFragment) {
            val cvcEasyPopup = EasyPopup.create().setContentView(context, R.layout.popwindow_cvc_entrance)
                    .setFocusAndOutsideEnable(true)
                    .setBackgroundDimEnable(true)
                    .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply()

            val coinMapBean = DataManager.getCoinMapBySymbol(PublicInfoDataService.getInstance().currentSymbol)
            val coin = NCoinManager.getMarketName(coinMapBean.name)

            cvcEasyPopup?.run {
                /* 充值 */
                findViewById<View>(R.id.ll_recharge)?.setOnClickListener {
                    cvcEasyPopup.dismiss()
                    if (!LoginManager.checkLogin(context, true)) {
                        return@setOnClickListener
                    }

                    if (PublicInfoDataService.getInstance().depositeKycOpen && UserDataService.getInstance().authLevel != 1) {
                        NewDialogUtils.KycSecurityDialog(context!!, context?.getString(R.string.common_kyc_chargeAndwithdraw)
                                ?: "", object : NewDialogUtils.DialogBottomListener {
                            override fun sendConfirm() {
                                when (UserDataService.getInstance().authLevel) {
                                    0 -> {
                                        NToastUtil.showTopToast(false, context?.getString(R.string.noun_login_pending))
                                    }

                                    2, 3 -> {
                                        ArouterUtil.greenChannel(RoutePath.RealNameCertificationActivity, null)
                                    }
                                }
                            }
                        })
                        return@setOnClickListener
                    }


                    ArouterUtil.navigation(RoutePath.SelectCoinActivity, Bundle().apply {
                        putInt(ParamConstant.OPTION_TYPE, ParamConstant.RECHARGE)
                        putBoolean(ParamConstant.COIN_FROM, true)
                    })
                }
                findViewById<TextView>(R.id.tv_text_recharge).text = LanguageUtil.getString(context, "coin_text_recharge")
                findViewById<TextView>(R.id.tv_action_transfer).text = LanguageUtil.getString(context, "assets_action_transfer")


                /*划转*/
                val llTransfer = findViewById<View>(R.id.ll_transfer)
                if (PublicInfoDataService.getInstance().otcOpen(null)) {
                    llTransfer.visibility = View.VISIBLE
                } else {
                    llTransfer.visibility = View.GONE
                }
                llTransfer?.setOnClickListener {
                    cvcEasyPopup.dismiss()
                    if (!LoginManager.checkLogin(context, true)) {
                        return@setOnClickListener
                    }
                    ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_BIBI, coin)
                }

                val isHorizontalDepth = PublicInfoDataService.getInstance().isHorizontalDepth
                if (isHorizontalDepth) {
                    findViewById<ImageView>(R.id.iv_change_direction)?.setImageResource(R.drawable.exchange_horizontalversion)
                    findViewById<TextView>(R.id.tv_change_direction).text = LanguageUtil.getString(context, "coin_text_horizontalDish")
                } else {
                    findViewById<ImageView>(R.id.iv_change_direction)?.setImageResource(R.drawable.exchange_verticalversion)
                    findViewById<TextView>(R.id.tv_change_direction).text = LanguageUtil.getString(context, "coin_text_verticalDish")
                }

                findViewById<View>(R.id.ll_change_pan)?.setOnClickListener {
                    PublicInfoDataService.getInstance().setDepthType(!isHorizontalDepth)
                    fragment.run {
                        v_horizontal_depth?.visibility = if (!isHorizontalDepth) View.VISIBLE else View.GONE
                        v_vertical_depth?.visibility = if (!isHorizontalDepth) View.GONE else View.VISIBLE
                        v_vertical_depth?.clearDepthView()
                        v_vertical_depth?.refreshDepthView()
                        v_horizontal_depth?.changeTape(AppConstant.DEFAULT_TAPE)
                    }
                    cvcEasyPopup.dismiss()
                }
            }
            cvcEasyPopup?.showAtAnchorView(targetView, YGravity.ALIGN_TOP, XGravity.ALIGN_RIGHT, -50, 50)
        }

        /**
         * 杠杆界面的popupWindow
         */
        fun createLeverPop(context: Context?, targetView: View, fragment: NLeverFragment, symbol: String = PublicInfoDataService.getInstance().currentSymbol4Lever) {
            val leverEasyPopup = EasyPopup.create().setContentView(context, R.layout.popwindow_lever_entrance)
                    .setFocusAndOutsideEnable(true)
                    .setBackgroundDimEnable(true)
                    .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply()

            leverEasyPopup?.run {
                // 借贷
                findViewById<View>(R.id.ll_borrow)?.setOnClickListener {
                    leverEasyPopup.dismiss()
                    if (!LoginManager.checkLogin(context, true)) {
                        return@setOnClickListener
                    }
                    ArouterUtil.navigation(RoutePath.NewVersionBorrowingActivity, Bundle().apply {
                        putString(ParamConstant.symbol, symbol)
                    })
                }
                findViewById<TextView>(R.id.tv_borrow).text = LanguageUtil.getString(context, "leverage_borrow")
                findViewById<TextView>(R.id.tv_action_transfer).text = LanguageUtil.getString(context, "assets_action_transfer")
                findViewById<TextView>(R.id.tv_return).text = LanguageUtil.getString(context, "leverage_return")

                // 划转
                findViewById<View>(R.id.ll_transfer)?.setOnClickListener {
                    leverEasyPopup.dismiss()
                    if (!LoginManager.checkLogin(context, true)) {
                        return@setOnClickListener
                    }
                    ArouterUtil.navigation(RoutePath.NewVersionTransferActivity, Bundle().apply {
                        putString(ParamConstant.TRANSFERSTATUS, ParamConstant.LEVER_INDEX)
                        putString(ParamConstant.TRANSFERCURRENCY, symbol)
                    })
                }
                // 归还
                findViewById<View>(R.id.ll_return)?.setOnClickListener {
                    leverEasyPopup.dismiss()
                    if (!LoginManager.checkLogin(context, true)) {
                        return@setOnClickListener
                    }
                    ArouterUtil.navigation(RoutePath.CurrencyLendingRecordsActivity, Bundle().apply {
                        putString(ParamConstant.symbol, symbol)
                    })
                }

                val isHorizontalDepth = PublicInfoDataService.getInstance().isHorizontalDepth4Lever
                if (isHorizontalDepth) {
                    findViewById<ImageView>(R.id.iv_change_direction)?.setImageResource(R.drawable.exchange_horizontalversion)
                    findViewById<TextView>(R.id.tv_change_direction).text = LanguageUtil.getString(context, "coin_text_horizontalDish")
                } else {
                    findViewById<ImageView>(R.id.iv_change_direction)?.setImageResource(R.drawable.exchange_verticalversion)
                    findViewById<TextView>(R.id.tv_change_direction).text = LanguageUtil.getString(context, "coin_text_verticalDish")
                }

                findViewById<View>(R.id.ll_change_pan)?.setOnClickListener {
                    PublicInfoDataService.getInstance().setDepthType4Lever(!isHorizontalDepth)
                    fragment.run {
                        v_horizontal_depth_lever?.visibility = if (!isHorizontalDepth) View.VISIBLE else View.GONE
                        v_vertical_depth_lever?.visibility = if (!isHorizontalDepth) View.GONE else View.VISIBLE
                        v_vertical_depth_lever?.clearDepthView()
                        v_vertical_depth_lever?.refreshDepthView()
                        v_horizontal_depth_lever?.changeTape(AppConstant.DEFAULT_TAPE)
                    }
                    leverEasyPopup.dismiss()
                }
            }
            leverEasyPopup?.showAtAnchorView(targetView, YGravity.ALIGN_TOP, XGravity.ALIGN_RIGHT, -50, 50)
        }


        /**
         * ETF规则
         */
        fun showETFRule(context: Context, url: String) {
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_etf_rule)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        viewHolder?.getView<TextView>(R.id.tv_text)?.text = LanguageUtil.getString(context, "etf_text_etfPrompt")
                    }
                    .addOnClickListener(R.id.btn_know, R.id.tv_detail)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.btn_know -> {
                                tDialog.dismiss()
                            }
                            R.id.tv_detail -> {
                                ArouterUtil.greenChannel(RoutePath.ItemDetailActivity, Bundle().apply {
                                    putString(ParamConstant.head_title, context.getString(R.string.etf_text_faq))
                                    putString(ParamConstant.web_url, url)
                                    putInt(ParamConstant.web_type, WebTypeEnum.NORMAL_INDEX.value)
                                })
                            }
                        }

                    }.create().show()
        }

        /**
         * ETF免责声明
         */
        fun showETFStatement(context: Context, domain: String, url: String) {
            val homePageDialogStatus = PublicInfoDataService.getInstance().etfStateDialogStatus
            if (homePageDialogStatus) return
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_etf_statement)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        val tv_text1 = viewHolder?.getView<TextView>(R.id.tv_text1)
                        tv_text1?.text = LanguageUtil.getString(context, "etf_text_disclaimerDetail1").format(domain)

                        viewHolder?.getView<SuperTextView>(R.id.btn_know)?.text = LanguageUtil.getString(context, "etf_text_knowRisk")
                        val tv_text2 = viewHolder?.getView<TextView>(R.id.tv_text2)
                        val fromHtml = Html.fromHtml("${LanguageUtil.getString(context, "etf_text_disclaimerDetail2")}<font color='#2489F1'>${context.getString(R.string.etf_text_faq)}</font>")
                        tv_text2?.text = fromHtml
                    }
                    .addOnClickListener(R.id.btn_know, R.id.tv_text2)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.btn_know -> {
                                tDialog.dismiss()
                                PublicInfoDataService.getInstance().saveETFStateDialogStatus(true)
                            }
                            R.id.tv_text2 -> {
                                ArouterUtil.greenChannel(RoutePath.ItemDetailActivity, Bundle().apply {
                                    putString(ParamConstant.head_title, LanguageUtil.getString(context, "etf_text_faq"))
                                    putString(ParamConstant.web_url, url)
                                    putInt(ParamConstant.web_type, WebTypeEnum.NORMAL_INDEX.value)
                                })
                            }
                        }

                    }.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return@OnKeyListener true
                        }
                        false  //默认返回值
                    }).create().show()
        }

        /**
         * 仓位分享功能
         */
        fun showPositionShareDialog(context: Context, obj: JSONObject?) {
            var ll_share: View? = null
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_position_share)
                    .setScreenWidthAspect(context, 1.0f)
                    .setScreenHeightAspect(context, 1.0f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(true)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        viewHolder?.setText(R.id.tv_contract_deposit_rate, LanguageUtil.getString(context, "contract_deposit_rate"))
                        viewHolder?.setText(R.id.tv_contract_deposit_rate, LanguageUtil.getString(context, ""))
                        ll_share = viewHolder?.getView<View>(R.id.ll_share)
                        obj?.run {
                            val baseSymbol = optString("baseSymbol")
                            val quoteSymbol = optString("quoteSymbol")
                            val contractId = optString("contractId")
                            val pricePrecision = optString("pricePrecision").toIntOrNull() ?: 2
                            val side = optString("side")
                            val unrealisedAmountIndex = optString("unrealisedAmountIndex")
                            val avgPrice = optString("avgPrice")
                            // 标记价格
                            val indexPrice = optString("indexPrice")
                            // 回报率
                            var unrealisedRateIndex = optString("unrealisedRateIndex")

                            val tv_contract_orientation = viewHolder?.getView<TextView>(R.id.tv_contract_orientation)


                            // 合约方向
                            tv_contract_orientation?.backgroundColor = ColorUtil.getMainColorType(side == "BUY")
                            val orderSide = if (side == "BUY") {
                                LanguageUtil.getString(context, "sl_str_open_long")
                            } else {
                                LanguageUtil.getString(context, "sl_str_open_short")
                            }
                            tv_contract_orientation?.text = orderSide
                            // 收益率
                            val tv_contract_profit_rate = viewHolder?.getView<TextView>(R.id.tv_contract_profit_rate)
                            val tv_title = viewHolder?.getView<TextView>(R.id.tv_title)
                            val iv_share = viewHolder?.getView<ImageView>(R.id.iv_share)
                            tv_title?.text = LanguageUtil.getContractShareText(context, unrealisedRateIndex)
                            if (unrealisedRateIndex.contains("-")) {
                                iv_share?.setImageResource(R.drawable.contract_share_cry)
                            } else {
                                iv_share?.setImageResource(R.drawable.contract_share_smile)
                            }

                            unrealisedRateIndex = if (StringUtil.checkStr(unrealisedAmountIndex)) {
                                BigDecimal(unrealisedRateIndex).setScale(2, BigDecimal.ROUND_HALF_DOWN).toPlainString()
                            } else {
                                "0.00"
                            }
                            if (unrealisedRateIndex.contains("-")) {
                                tv_contract_profit_rate?.text = unrealisedRateIndex + "%"
                            } else {
                                tv_contract_profit_rate?.text = "+$unrealisedRateIndex%"
                            }

                            /**
                             * 合约名称
                             */
                            val tv_contract_type_title = viewHolder?.getView<TextView>(R.id.tv_contract_type_title)
                            LogUtil.d(TAG, "===LL:${Contract2PublicInfoManager.getContractTypeText(context, contractId.toInt())}===")
                            tv_contract_type_title?.text = Contract2PublicInfoManager.getContractTypeText(context, contractId.toInt())
                            val tv_contract_symbol = viewHolder?.getView<TextView>(R.id.tv_contract_symbol)
                            tv_contract_symbol?.text = baseSymbol + quoteSymbol


                            val tv_contract_price_title = viewHolder?.getView<TextView>(R.id.tv_contract_price_title)
                            val tv_contract_price = viewHolder?.getView<TextView>(R.id.tv_contract_price)
                            /**
                             * 标记价格
                             */
                            val indexPriceByPrecision = Contract2PublicInfoManager.cutValueByPrecision(indexPrice, pricePrecision)
                            tv_contract_price?.text = indexPriceByPrecision
                            /**
                             * 开仓均价
                             */
                            val tv_open_avg_price_title = viewHolder?.getView<TextView>(R.id.tv_open_avg_price_title)
                            val tv_open_avg_price = viewHolder?.getView<TextView>(R.id.tv_open_avg_price)
                            tv_open_avg_price?.text = Contract2PublicInfoManager.cutValueByPrecision(avgPrice.toString(), pricePrecision)

                            val iv_qrcode = viewHolder?.getView<ImageView>(R.id.iv_qrcode)
                            GlideUtils.loadImage(context, iv_qrcode)

                            val tv_download_tip = viewHolder?.getView<TextView>(R.id.tv_download_tip)
                            tv_download_tip?.text = String.format(context.getString(R.string.sl_str_down_app), context.getString(R.string.app_name))


                        }
                    }
                    .addOnClickListener(R.id.btn_share, R.id.ll_bg)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.btn_share -> {
                                if (ll_share != null) {
                                    var screenshotBitmap: Bitmap? = ScreenShotUtil.getScreenshotBitmap(ll_share)
                                    ZXingUtils.shareImageToWechat(screenshotBitmap, context.getString(R.string.contract_share_label), context)
                                }

                                tDialog.dismiss()
                            }
                            R.id.ll_bg -> {
                                tDialog.dismiss()
                            }
                        }

                    }.create().show()
        }


        fun showAuthorizationDialog(context: Context, gameID: String, gameName: String, gameToken: String) {
            TDialog.Builder((context as AppCompatActivity).supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_auth)
                    .setScreenWidthAspect(context, 0.8f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(false)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        val tv_account = viewHolder?.getView<TextView>(R.id.tv_account)
                        tv_account?.text = UserDataService.getInstance().userAccount

                        val tv_game_name = viewHolder?.getView<TextView>(R.id.tv_game_name)
                        tv_game_name?.text = gameName
                    }
                    .addOnClickListener(R.id.btn_cancel, R.id.btn_confirm, R.id.iv_delete)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.btn_confirm -> {
                                HttpClient.instance
                                        .getGameAuth(gameId = gameID, gameToken = gameToken)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object : NetObserver<Any>() {
                                            override fun onHandleSuccess(bean: Any?) {
                                                tDialog.dismiss()
//                                                ToastUtils.showToast("授权成功，进行跳转")
                                                context.finish()
                                            }

                                            override fun onHandleError(code: Int, msg: String?) {
                                                super.onHandleError(code, msg)
                                                NToastUtil.showToast(msg, false)
                                            }
                                        })
                                MMKV.defaultMMKV().putString("gameId", "")
                            }
                            R.id.btn_cancel, R.id.iv_delete -> {
                                tDialog.dismiss()
                                MMKV.defaultMMKV().putString("gameId", "")
                            }
                        }

                    }.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return@OnKeyListener true
                        }
                        false  //默认返回值
                    }).create().show()
        }


        /**
         * K线行情分享功能
         */
        fun showKLineShareDialog(context: Context) {
            var screenshotBitmap: Bitmap? = null
            screenshotBitmap = ScreenShotUtil.getScreenshotBitmap((context as AppCompatActivity).window?.decorView
                    ?: return)
            var ll_share: View? = null
            TDialog.Builder(context.supportFragmentManager)
                    .setLayoutRes(R.layout.dialog_share_market)
                    .setScreenWidthAspect(context, 1.0f)
                    .setScreenHeightAspect(context, 1.0f)
                    .setGravity(Gravity.CENTER)
                    .setDimAmount(0.8f)
                    .setCancelableOutside(true)
                    .setOnBindViewListener { viewHolder: BindViewHolder? ->
                        val iv_qrcode = viewHolder?.getView<ImageView>(R.id.iv_qrcode)
                        viewHolder?.setText(R.id.tv_title, LanguageUtil.getString(context, "common_share_detail"))
                        viewHolder?.setText(R.id.btn_share, LanguageUtil.getString(context, "common_share_confirm"))

                        GlideUtils.loadImageQr(context, iv_qrcode)
                        if (screenshotBitmap != null) {
                            ll_share = viewHolder?.getView<View>(R.id.ll_share)
                            ll_share?.backgroundDrawable = BitmapDrawable(context.resources,screenshotBitmap)
                        }
                    }
                    .addOnClickListener(R.id.btn_share, R.id.ll_bg)
                    .setOnViewClickListener { _, view, tDialog ->
                        when (view.id) {
                            R.id.btn_share -> {
                                if (ll_share != null) {
                                    var bitmap: Bitmap? = ScreenShotUtil.getScreenshotBitmap(ll_share)
                                    ZXingUtils.shareImageToWechat(bitmap, context.getString(R.string.contract_share_label), context)
                                    tDialog.dismiss()
                                }
                            }
                            R.id.ll_bg -> {
                                tDialog.dismiss()
                            }
                        }

                    }.create().show()
        }

    }


}
