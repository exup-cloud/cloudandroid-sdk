package com.yjkj.chainup.treaty.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.Utils
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019/1/23-10:27 AM
 * @Email buptjinlong@163.com
 * @description
 */
open class HoldContractAssterAdapter(data: ArrayList<JSONObject>) : BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_new_contract_asset_otc, data) {

    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {

        helper?.setText(R.id.tv_coin_name, item?.optString("contractSeries") + " " + Contract2PublicInfoManager.getContractType(mContext, item?.optInt("contractType"), item?.optString("settleTime")) + " (${item?.optString("leverageLevel")}X)")

        helper?.setText(R.id.tv_normal_title, LanguageUtil.getString(mContext,"contract_text_realisedPNL"))
        helper?.setText(R.id.tv_lock_title, LanguageUtil.getString(mContext,"contract_text_unrealisedPNL"))
        helper?.setText(R.id.tv_equivalent, LanguageUtil.getString(mContext,"contract_text_positionNumber") + "(${LanguageUtil.getString(mContext,"contract_text_volumeUnit")})")

        var mrealisedAmount = Contract2PublicInfoManager.cutDespoitByPrecision(item?.optString("realisedAmount").toString())
        var munrealisedAmount = Contract2PublicInfoManager.cutDespoitByPrecision(item?.optString("unrealisedAmount").toString())
        var isShowAssets = UserDataService.getInstance().isShowAssets

        var mvolume = ""
        if (item?.optString("side") == "BUY"){
            helper?.getView<TextView>(R.id.tv_equivalent_content)?.setTextColor(ColorUtil.getMainColorType(true))
            mvolume = "+" + BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(item?.optString("volume"), 8).toPlainString())
        }else{
            helper?.getView<TextView>(R.id.tv_equivalent_content)?.setTextColor(ColorUtil.getMainColorType(false))
            mvolume = "-" + BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(item?.optString("volume"), 8).toPlainString())
        }

        Utils.assetsHideShow(isShowAssets,helper?.getView(R.id.tv_normal_balance),mrealisedAmount)
        Utils.assetsHideShow(isShowAssets,helper?.getView(R.id.tv_lock_balance),munrealisedAmount)

        Utils.assetsHideShow(isShowAssets,helper?.getView(R.id.tv_equivalent_content),mvolume)
    }

}