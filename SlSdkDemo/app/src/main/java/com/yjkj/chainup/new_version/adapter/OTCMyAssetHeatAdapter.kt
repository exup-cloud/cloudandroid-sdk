package com.yjkj.chainup.new_version.adapter

import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.Utils
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2018/10/26-下午3:15
 * @Email buptjinlong@163.com
 * @description
 */
open class OTCMyAssetHeatAdapter(data: ArrayList<JSONObject>) : BaseQuickAdapter<JSONObject
        , BaseViewHolder>(R.layout.item_otc_my_asset_heat, data) {

    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
        var car = helper?.getView<RelativeLayout>(R.id.activity_my_asset_total_asset_layout)
        var title = item?.optString("title") ?: ""
        val type = item?.getString("balanceType") ?: ""

        when (type) {
            ParamConstant.BIBI_INDEX -> {
                helper?.setText(R.id.activity_my_asset_total_asset_content, LanguageUtil.getString(mContext, "assets_text_exchange") + LanguageUtil.getString(mContext, "assets_text_total") + "(${NCoinManager.getShowMarket("BTC")})".replace("${NCoinManager.getShowMarket("BTC")}", item?.optString("totalBalanceSymbol")
                        ?: ""))
                car?.setBackgroundResource(R.drawable.assets_exchange)
            }
            ParamConstant.LEVER_INDEX -> {
                helper?.setText(R.id.activity_my_asset_total_asset_content, LanguageUtil.getString(mContext, "leverage_asset") + LanguageUtil.getString(mContext, "assets_text_total") + "(${NCoinManager.getShowMarket("BTC")})".replace("${NCoinManager.getShowMarket("BTC")}", item?.optString("totalBalanceSymbol")
                        ?: ""))
                car?.setBackgroundResource(R.drawable.assets_exchange)
            }
            ParamConstant.B2C_INDEX -> {
                helper?.setText(R.id.activity_my_asset_total_asset_content, LanguageUtil.getString(mContext, "assets_text_otc") + LanguageUtil.getString(mContext, "assets_text_total") + "(${NCoinManager.getShowMarket("BTC")})".replace("${NCoinManager.getShowMarket("BTC")}", item?.optString("totalBalanceSymbol")
                        ?: ""))
                car?.setBackgroundResource(R.drawable.assets_otc)
            }
            ParamConstant.FABI_INDEX -> {
                val otcText = if (PublicInfoDataService.getInstance().getB2CSwitchOpen(null)) {
                    LanguageUtil.getString(mContext, "home_text_otcTotal_forotc")
                } else {
                    LanguageUtil.getString(mContext, "home_text_otcTotal")
                }

                helper?.setText(R.id.activity_my_asset_total_asset_content, otcText + "(${NCoinManager.getShowMarket("BTC")})".replace("${NCoinManager.getShowMarket("BTC")}", item?.optString("totalBalanceSymbol")
                        ?: ""))
                car?.setBackgroundResource(R.drawable.assets_otc)
            }
            ParamConstant.CONTRACT_INDEX -> {
                helper?.setText(R.id.activity_my_asset_total_asset_content, LanguageUtil.getString(mContext, "home_text_contractTotal") + "(${NCoinManager.getShowMarket("BTC")})".replace("${NCoinManager.getShowMarket("BTC")}", item?.optString("totalBalanceSymbol")
                        ?: ""))
                car?.setBackgroundResource(R.drawable.assets_contract)
            }

        }

        /**
         * 收盘价的汇率换算结果
         */
        val result = RateManager.getCNYByCoinName(item?.optString("totalBalanceSymbol"), item?.optString("totalBalance"))
        /**
         * 一下是隐藏资产
         */
        var mtotalBalance = BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(item?.optString("totalBalance"), 8).toPlainString())
        var isShowAssets = UserDataService.getInstance().isShowAssets

        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.activity_my_asset), mtotalBalance)
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.activity_my_asset_tv_assets_rmb), result)


    }

}


