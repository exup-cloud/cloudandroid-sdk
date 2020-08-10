package com.yjkj.chainup.contract.fragment.detail

import android.text.TextUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractIndex
import com.contract.sdk.impl.IResponse
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import kotlinx.android.synthetic.main.sl_fragment_cotract_introduce.*

/**
 * 合约基本信息
 */
class ContractIntroduceFragment : NBaseFragment(){
    override fun setContentView(): Int {
        return R.layout.sl_fragment_cotract_introduce
    }

    var contractId  = 0
    var contract: Contract? = null

    override fun loadData() {
        super.loadData()
        contractId = activity?.intent?.getIntExtra("contractId", 0)!!
        contract = ContractPublicDataAgent.getContract(contractId)
    }


    override fun initView() {
        initAutoTextView()
        contract?.let {
            //合约标的
            tv_contract_underlying.text = it.base_coin
            //保证金币种
            tv_margin_coin.text =it.margin_coin
            //合约属性
            tv_contract_property.text = if (it.isReserve) getLineText("sl_str_reserve_contract") else getLineText("sl_str_positive_contract")
            //合约大小
            tv_contract_size.text = "1" + getLineText("sl_str_contracts_unit") + "=" + it.face_value + it.price_coin
            //最高杠杆
            tv_max_leverage.text = it.max_leverage + getString(R.string.sl_str_bei)
            //指数来源
            loadIndexSource()
        }
    }

    private fun initAutoTextView() {
        tv_contract_underlying_label.onLineText("sl_str_contract_underlying")
        tv_margin_coin_label.onLineText("sl_str_margin_coin")
        tv_contract_property_label.onLineText("sl_str_contract_property")
        tv_contract_size_label.onLineText("sl_str_contract_size")
        tv_max_leverage_label.onLineText("sl_str_max_leverage")
        tv_index_source_label.onLineText("sl_str_index_source")
    }

    private fun loadIndexSource() {
        ContractPublicDataAgent.loadIndexes(object: IResponse<MutableList<ContractIndex>>(){
            override fun onSuccess(data: MutableList<ContractIndex>) {
                if (data != null) {
                    var indexSource = ""
                    for (i in data.indices) {
                        val index: ContractIndex = data[i]
                        if (index.index_id === contract!!.index_id) {
                            for (j in 0 until index.market.size) {
                                indexSource += index.market[j]
                                if (j < index.market.size - 1) {
                                    indexSource += " , "
                                }
                            }
                        }
                    }
                    tv_index_source?.text = indexSource
                }
            }

        })
    }


}