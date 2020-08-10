package com.yjkj.chainup.new_version.adapter

import com.chad.library.adapter.base.BaseViewHolder
import com.fengniao.news.util.DateUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.StringUtil
import org.json.JSONObject

/**
 * @Author: Bertking
 * @Date：2019-10-25-10:48
 * @Description:充值，提现记录(B2C)
 */
class B2CRecordsAdapter(data: List<JSONObject>?) : NBaseAdapter(data, R.layout.item_record_b2c) {
    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
        item?.run {
            helper?.run {
                val createTimeAt = optString("createdAtTime", "")
                val createTime = if (StringUtil.checkStr(createTimeAt)) {
                    DateUtil.longToString("yyyy/MM/dd HH:mm", createTimeAt.toLong())
                } else {
                    ""
                }
                setText(R.id.tv_date, createTime)

                setText(R.id.tv_state, optString("status_text", ""))

                setText(R.id.tv_amount,
                        BigDecimalUtils.showNormal(optString("amount", "")))
            }

        }
    }
}