package com.yjkj.chainup.contract.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.contract.sdk.data.Contract
import com.yjkj.chainup.R

/**
 * 合约搜索
 */
class SlContractSearchAdapter(data:ArrayList<Contract>) : BaseQuickAdapter<Contract, BaseViewHolder>(R.layout.sl_item_contract_search,data){

    override fun convert(helper: BaseViewHolder?, item: Contract?) {
       helper?.let {
           it.setText(R.id.tv_name,item?.getDisplayName(mContext))
       }
    }

}