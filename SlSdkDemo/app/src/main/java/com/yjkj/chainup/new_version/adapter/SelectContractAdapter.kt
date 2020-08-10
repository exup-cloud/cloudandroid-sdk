package com.yjkj.chainup.new_version.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.util.BigDecimalUtil
import com.yjkj.chainup.util.StringUtil

/**
 * @Author: Bertking
 * @Date：2019-05-14-20:05
 * @Description: 选择合约的Adapter（4.0）
 */
class SelectContractAdapter(var data: ArrayList<ContractBean>) :
        BaseQuickAdapter<ContractBean, BaseViewHolder>(R.layout.item_choose_contract, data) {

    val TAG = SelectContractAdapter::class.java.simpleName

    override fun convert(helper: BaseViewHolder?, item: ContractBean?) {
        val name = item?.baseSymbol + item?.quoteSymbol
        if (helper?.adapterPosition == 0) {
            helper?.setGone(R.id.tv_contract, true)
            helper?.setGone(R.id.v_line_flag, true)
            helper?.setText(R.id.tv_contract, name)
        } else {
            val lastName = data[helper?.adapterPosition!! - 1].baseSymbol + data[helper?.adapterPosition!! - 1].quoteSymbol
            if (lastName != name) {
                helper?.setText(R.id.tv_contract, name)
                helper?.setGone(R.id.tv_contract, true)
                helper?.setGone(R.id.v_line_flag, true)
            } else {
                helper?.setGone(R.id.tv_contract, false)
                helper?.setGone(R.id.v_line_flag, false)
            }
        }
        helper?.setText(R.id.tv_contract_type, Contract2PublicInfoManager.getContractType(mContext,item?.id))


        val closePrice: String = if (TextUtils.isEmpty(item?.closePrice)) {
            "--"
        } else {
            Contract2PublicInfoManager.cutValueByPrecision(item?.closePrice!!, item?.pricePrecision
                    ?: 4)
        }
        helper?.setText(R.id.tv_close_price, closePrice)

        var rose = item?.rose


        if (-1==BigDecimalUtil.compareSize(rose,"0.0")) {
            helper?.setTextColor(R.id.tv_close_price, ColorUtil.getMainColorType(isRise = false))
        } else {
            helper?.setTextColor(R.id.tv_close_price, ColorUtil.getMainColorType())
        }


    }

}