package com.yjkj.chainup.new_version.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.util.Utils
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2020-06-18-14:21
 * @Email buptjinlong@163.com
 * @description
 */
class ChangeNetworkAdapter(data: List<JSONObject>?) : NBaseAdapter(data, R.layout.item_change_network) {


    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
        helper?.setText(R.id.tv_title, LanguageUtil.getString(mContext, "customSetting_action_host") + (helper?.adapterPosition + 1))

        if (!TextUtils.isEmpty(item?.optString("error"))) {
            helper?.setText(R.id.tv_content, LanguageUtil.getString(mContext, "customSetting_action_unusable"))
        } else {
            if (TextUtils.isEmpty(item?.optString("networkAppapi")) && TextUtils.isEmpty(item?.optString("networkWs"))) {
                helper?.setText(R.id.tv_content, LanguageUtil.getString(mContext, "customSetting_action_testing"))
            } else {
                helper?.setText(R.id.tv_content, "${LanguageUtil.getString(mContext, "customSetting_action_response")}${item?.optString("networkAppapi", "--")
                        ?: "--"}ms/${item?.optString("networkWs", "--") ?: "--"}ms")
            }
        }
        if (TextUtils.isEmpty(PublicInfoDataService.getInstance().newWorkURL)) {
            PublicInfoDataService.getInstance().saveNewWorkURL(Utils.getAPIInsideString(ApiConstants.BASE_URL))
        }
        if (PublicInfoDataService.getInstance().newWorkURL == item?.optString("hostName")) {
            helper?.setGone(R.id.iv_red_dot_mail, true)
        } else {
            helper?.setGone(R.id.iv_red_dot_mail, false)
        }


    }
}