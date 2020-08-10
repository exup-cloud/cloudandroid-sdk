package com.yjkj.chainup.new_version.view

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.bean.AssetScreenBean
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.new_version.activity.CashFlow4Activity
import com.yjkj.chainup.new_version.activity.asset.NewVersionAssetOptimizeDetailFragment
import com.yjkj.chainup.new_version.activity.asset.NewVersionContractBillActivity
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.accet_header_view.view.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019-08-22-14:41
 * @Email buptjinlong@163.com
 * @description
 */
class NewAssetTopView @JvmOverloads constructor(
        context: Activity,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /**
     * bibi 是币币
     * bibao 是币宝
     * fabi 是otc
     * b2c
     */
    private var param_index: String = ""
    /**
     * 隐藏小额资产
     */
    private var isLittleAssetsShow = false

    var assetScreen = AssetScreenBean("", "")

    init {
        initView(context)
    }

    var listener: selecetTransferListener? = null

    interface selecetTransferListener {
        fun selectTransfer(param_index: String)
        fun leverageFilter(temp: String)
        fun fiatFilter(temp: String)
        fun bibiFilter(temp: String)
        fun b2cFilter(temp: String)
        fun selectWithdrawal(temp: String)
        fun selectRedEnvelope(temp: String)
    }

    fun initView(context: Activity) {
        LayoutInflater.from(context).inflate(R.layout.accet_header_view, this, true)
        setRefreshViewData()
        setSelectClick(context)
        tv_contract_text_orderMargin?.text = LanguageUtil.getString(context,"contract_text_orderMargin")
        tv_assets_action_chargeCoin?.text = LanguageUtil.getString(context,"assets_action_chargeCoin")
        tv_assets_action_withdraw?.text = LanguageUtil.getString(context,"assets_action_withdraw")
        tv_noun_order_paymentTerm?.text = LanguageUtil.getString(context,"noun_order_paymentTerm")
        tv_assets_action_transfer?.text = LanguageUtil.getString(context,"assets_action_transfer")
        tv_redpacket_redpacket?.text = LanguageUtil.getString(context,"redpacket_redpacket")
        tv_assets_action_journalaccount?.text = LanguageUtil.getString(context,"assets_action_journalaccount")
        tv_assets_action_contractNote?.text = LanguageUtil.getString(context,"assets_action_contractNote")
        tv_withdraw_text_available?.text = LanguageUtil.getString(context,"withdraw_text_available")
        tv_contract_text_positionMargin?.text = LanguageUtil.getString(context,"contract_text_positionMargin")
        tv_contract_text_orderMargin?.text = LanguageUtil.getString(context,"contract_text_orderMargin")
        tv_leverage_borrow?.text = LanguageUtil.getString(context,"leverage_borrow")
        fragment_my_asset_order_hide?.text = LanguageUtil.getString(context,"assets_action_privacy")
        et_search?.hint = LanguageUtil.getString(context,"assets_action_search")
        tv_contract_coupon.onLineText("contract_swap_gift")
    }

    fun setAssetOrderHide(status: Boolean) {
        isLittleAssetsShow = status
        fragment_my_asset_order_hide?.isChecked = isLittleAssetsShow
        et_search?.setText("")
    }

    fun setRefreshViewData() {
        isLittleAssetsShow = UserDataService.getInstance().assetState
        fragment_my_asset_order_hide?.isChecked = isLittleAssetsShow
        et_search?.setText("")
    }

    fun clearEdittext() {
        et_search?.setText("")
    }

    fun setSelectClick(context: Activity) {

        /**
         * 是否隐藏小额资产
         */
        fragment_my_asset_order_hide?.setOnClickListener {
            var message = MessageEvent(MessageEvent.refresh_trans_type)
            NLiveDataUtil.postValue(message)

        }
        /**
         *  充币
         */
        ll_top_up_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            if (param_index == ParamConstant.BIBI_INDEX) {
                if (PublicInfoDataService.getInstance().depositeKycOpen && UserDataService.getInstance().authLevel != 1) {
                    NewDialogUtils.KycSecurityDialog(context, context.getString(R.string.common_kyc_chargeAndwithdraw), object : NewDialogUtils.DialogBottomListener {
                        override fun sendConfirm() {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    NToastUtil.showTopToast(false, context.getString(R.string.noun_login_pending))
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
            } else {
                if (realNameCertification()) {
                    ArouterUtil.navigation(RoutePath.SelectCoinActivity, Bundle().apply {
                        putInt(ParamConstant.OPTION_TYPE, ParamConstant.RECHARGE)
                        putString(ParamConstant.ASSET_ACCOUNT_TYPE, ParamConstant.B2C_ACCOUNT)
                        putBoolean(ParamConstant.COIN_FROM, true)
                    })
                }

            }

        }
        /**
         *  提币
         */
        ll_otc_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            if (param_index == ParamConstant.BIBI_INDEX) {
                if (null != listener) {
                    listener?.selectWithdrawal(param_index)
                }
            } else {
                if (realNameCertification()) {
                    ArouterUtil.navigation(RoutePath.SelectCoinActivity, Bundle().apply {
                        putInt(ParamConstant.OPTION_TYPE, ParamConstant.WITHDRAW)
                        putString(ParamConstant.ASSET_ACCOUNT_TYPE, ParamConstant.B2C_ACCOUNT)
                        putBoolean(ParamConstant.COIN_FROM, true)
                    })
                }

            }
        }
        /**
         * 借贷
         */
        ll_loan_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            if (PublicInfoDataService.getInstance().hasShownLeverStatusDialog()) {
                skipCoinMap4Lever()
            } else {
                NewDialogUtils.showLeverDialog(context, listener = object : NewDialogUtils.DialogTransferBottomListener {
                            override fun sendConfirm() {
                                skipCoinMap4Lever()
                            }
                            override fun showCancel() {
                            }
                        })
            }
        }

        /**
         *  收款方式
         */
        ll_payment_methods_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            ArouterUtil.greenChannel(RoutePath.PaymentMethodActivity, null)
        }
        /**
         *  划转
         */
        ll_transfer_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            if (null != listener) {
                /**
                 * 杠杆
                 */
                if (ParamConstant.LEVER_INDEX == param_index) {
                    if (PublicInfoDataService.getInstance().hasShownLeverStatusDialog()) {
                        listener?.selectTransfer(param_index)
                    } else {
                        NewDialogUtils.showLeverDialog(context,
                                listener = object : NewDialogUtils.DialogTransferBottomListener {
                                    override fun sendConfirm() {
                                        listener?.selectTransfer(param_index)
                                    }

                                    override fun showCancel() {

                                    }
                                })

                    }
                } else {
                    listener?.selectTransfer(param_index)
                }
            }
        }
        /**
         *  资金流水
         */
        ll_funds_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            when (param_index) {
                ParamConstant.BIBI_INDEX -> {
                    CashFlow4Activity.enter2(context, ParamConstant.TYPE_DEPOSIT)
                }
                ParamConstant.FABI_INDEX -> {
                    CashFlow4Activity.enter2(context, ParamConstant.TYPE_OTC_TRANSFER)
                }
                ParamConstant.B2C_INDEX -> {
                    ArouterUtil.navigation(RoutePath.B2CCashFlowActivity, null)
                }
                ParamConstant.LEVER_INDEX -> {
                    ArouterUtil.navigation(RoutePath.LeverDrawRecordActivity, null)
                }
            }
        }
        /**
         *  合约
         */
        ll_contract_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            NewVersionContractBillActivity.enter2(context)
        }
        /**
         *  红包
         */
        ll_red_envelope_layout?.setOnClickListener {
            if (Utils.isFastClick())return@setOnClickListener
            if (null != listener) {
                listener?.selectRedEnvelope(param_index)
            }
        }

        /**
         * 监听搜索编辑框
         */
        et_search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 如果adapter不为空的话就根据编辑框中的内容来过滤数据
                if (TextUtils.isEmpty(s)) {
                    NewVersionAssetOptimizeDetailFragment.liveDataCleanForEditText.postValue(param_index)
                } else {
                    if (null != listener) {
                        when (param_index) {
                            ParamConstant.BIBI_INDEX -> {
                                listener?.bibiFilter(s.toString())
                            }
                            ParamConstant.FABI_INDEX -> {
                                listener?.fiatFilter(s.toString())
                            }
                            ParamConstant.B2C_INDEX -> {
                                listener?.b2cFilter(s.toString())
                            }
                            ParamConstant.LEVER_INDEX -> {
                                listener?.leverageFilter(s.toString())
                            }
                        }
                    }
                }
                et_search?.isFocusable = true
                et_search?.isFocusableInTouchMode = true
            }
        })
        et_search?.setOnFocusChangeListener { v, hasFocus ->
            et_search?.isFocusable = true
            et_search?.isFocusableInTouchMode = true
        }

    }

    /**
     * 设置页面
     */
    fun initNorMalView(index: String?) {
        param_index = index ?: ""
        assetScreen.index4Asset = param_index
        when (param_index) {
            ParamConstant.BIBI_INDEX -> {

                ll_payment_methods_layout?.visibility = View.GONE
                ll_contract_layout?.visibility = View.GONE
                if (PublicInfoDataService.getInstance().isRedPacketOpen(null)) {
                    ll_red_envelope_layout?.visibility = View.VISIBLE
                } else {
                    ll_red_envelope_layout?.visibility = View.GONE
                }
            }
            ParamConstant.FABI_INDEX -> {
                ll_payment_methods_layout?.visibility = View.VISIBLE
                ll_otc_layout?.visibility = View.GONE
                ll_top_up_layout?.visibility = View.GONE
                ll_contract_layout?.visibility = View.GONE
            }
            ParamConstant.CONTRACT_INDEX -> {
                ll_payment_methods_layout?.visibility = View.GONE
                ll_contract_layout?.visibility = View.VISIBLE
                ll_contract_coupon_layout?.visibility = if (PublicInfoDataService.getInstance().contractCouponOpen(null)) View.VISIBLE else View.GONE
                v_top_line?.visibility = View.VISIBLE
                ll_otc_layout?.visibility = View.GONE
                ll_top_up_layout?.visibility = View.GONE
                ll_funds_layout?.visibility = View.GONE
                rl_search_layout?.visibility = View.GONE
            }
            ParamConstant.B2C_INDEX -> {
                ll_payment_methods_layout?.visibility = View.GONE
                ll_contract_layout?.visibility = View.GONE
                v_top_line?.visibility = View.GONE
                // 划转
                ll_transfer_layout?.visibility = View.GONE
                ll_otc_layout?.visibility = View.VISIBLE
                ll_top_up_layout?.visibility = View.VISIBLE
                ll_funds_layout?.visibility = View.VISIBLE
                rl_search_layout?.visibility = View.VISIBLE
            }
            ParamConstant.LEVER_INDEX -> {
                ll_loan_layout?.visibility = View.VISIBLE
                ll_top_up_layout?.visibility = View.GONE
                ll_otc_layout?.visibility = View.GONE
            }
        }
    }

    fun setRefreshAdapter() {
        if (param_index == "contract") {
            var mcanUseBalance = Contract2PublicInfoManager.cutDespoitByPrecision(symbol4Contract.optString("canUseBalance"))
            var mpositionMargin = Contract2PublicInfoManager.cutDespoitByPrecision(symbol4Contract.optString("positionMargin"))
            var morderMargin = Contract2PublicInfoManager.cutDespoitByPrecision(symbol4Contract.optString("orderMargin"))

            var isShowAssets = UserDataService.getInstance().isShowAssets
            Utils.assetsHideShow(isShowAssets, tv_contract_balance, mcanUseBalance)
            Utils.assetsHideShow(isShowAssets, tv_contract_position_margin_content, mpositionMargin)
            Utils.assetsHideShow(isShowAssets, tv_contract_order_margin_content, morderMargin)
        }
    }

    var symbol4Contract: JSONObject = JSONObject()
    /**
     * 合约 账户余额
     */
    fun initAdapterView(list: JSONArray) {
        if (list.length() <= 0) {
            return
        }
        ll_contract_content_layout.visibility = View.VISIBLE
        /**
         * 可用余额
         */
        var symbol = list.optJSONObject(0)
        symbol4Contract = symbol

        setRefreshAdapter()

    }

    fun realNameCertification(): Boolean {
        if (UserDataService.getInstance().authLevel != 1) {
            NewDialogUtils.OTCTradingOnlyPermissionsDialog(context, object : NewDialogUtils.DialogBottomListener {
                override fun sendConfirm() {
                    when (UserDataService.getInstance().authLevel) {
                        0 -> {
                            ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                        }

                        2, 3 -> {
                            ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                        }
                    }
                }

            }, context.getString(R.string.otc_please_cert))
            return false
        }
        return true
    }

    fun skipCoinMap4Lever(){
        ArouterUtil.navigation(RoutePath.CoinMapActivity, Bundle().apply {
            putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER, true)
            putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER_UNREFRESH, true)
        })
    }

}