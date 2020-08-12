package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractOrder
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.TimeFormatUtils
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.utils.*
import kotlinx.android.synthetic.main.sl_activity_contract_entrust_detail.*
import kotlinx.android.synthetic.main.sl_activity_contract_entrust_detail.tv_deal_price
import kotlinx.android.synthetic.main.sl_activity_contract_entrust_detail.tv_deal_price_value
import kotlinx.android.synthetic.main.sl_activity_contract_entrust_detail.tv_volume_value
import java.text.DecimalFormat
import kotlin.math.abs

/**
 * 委托详情
 */
class SlContractEntrustDetailActivity : NBaseActivity(){
    override fun setContentView(): Int {
      return R.layout.sl_activity_contract_entrust_detail
    }
    private var contractOrder : ContractOrder?=null
    private var list = ArrayList<ContractOrder>()

    private  var isForceCloseOrder:Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initView()
    }

    override fun loadData() {
        contractOrder = intent.extras?.getParcelable("order")
        if(contractOrder == null){
            finish()
        }
//        val contractJson = UtilSystem.readAssertResource(mActivity, "contractOrderDetail.json")
//        list.clear()
//        list.addAll(Gson().fromJson<List<ContractOrder>>(contractJson, object : TypeToken<List<ContractOrder?>?>() {}.type))
        isForceCloseOrder = contractOrder?.isForceCloseOrder
        loadDataFromNet()
    }

    private fun loadDataFromNet() {
        showLoadingDialog()
        ContractUserDataAgent.loadOrderTrades(contractOrder?.instrument_id!!, contractOrder?.oid?:0,object: IResponse<List<ContractOrder>>(){
            override fun onSuccess(data: List<ContractOrder>) {
                closeLoadingDialog()
                if (data != null && data.isNotEmpty()) {
                    list.addAll(data)
                    updateContractDetail()
                }
            }
            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
            }

        })
    }

    override fun initView() {
        initAutoStringView()
        contractOrder?.let {
            val contract: Contract = ContractPublicDataAgent.getContract(it.instrument_id)
                    ?: return
            //合约名称
            val symbol =  contract.symbol
            tv_contract_name.text = symbol
            //方向
            when (it.side) {
                ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG -> {
                    tv_type.onLineText("sl_str_buy_open")
                    tv_type.setTextColor(resources.getColor(R.color.main_green))
                    title_layout.title = getLineText("otc_action_buy")+symbol
                }
                ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT -> {
                    tv_type.onLineText("sl_str_sell_open")
                    tv_type.setTextColor(resources.getColor(R.color.main_red))
                    title_layout.title = getLineText("contract_action_sell")+symbol
                }
                ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT -> {
                    tv_type.onLineText("sl_str_buy_close")
                    tv_type.setTextColor(resources.getColor(R.color.main_green))
                    title_layout.title = getLineText("otc_buy")+symbol
                }
                ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG -> {
                    tv_type.onLineText("sl_str_sell_close")
                    tv_type.setTextColor(resources.getColor(R.color.main_red))
                    title_layout.title = getLineText("contract_action_sell")+symbol
                }
                else -> {
                }
            }
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)
            val dfVol: DecimalFormat = NumberUtil.getDecimal(contract.vol_index)
            //成交量
            tv_volume_value.text = dfVol.format(MathHelper.round(it.cum_qty))
            //成交均价
            if(isForceCloseOrder!!){
                tv_deal_price_value.text = "--"
            }else{
                tv_deal_price_value.text = dfDefault.format(MathHelper.round(it.avg_px, contract.price_index))
            }
            tv_deal_price.text = getLineText("contract_text_dealAverage")+" (${contract.quote_coin})"
            //手续费
            tv_fee.text =  getLineText("sl_str_fee")+" (${contract.margin_coin})"

        }
    }

    private fun initAutoStringView(){
        tv_volume.onLineText("sl_str_make_volume_unit")
    }

    fun updateContractDetail(){
        contractOrder?.let {
            val contract: Contract = ContractPublicDataAgent.getContract(it.instrument_id)
                    ?: return
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(-1)
            val dfVol: DecimalFormat = NumberUtil.getDecimal(contract.vol_index)
            val layoutInflater = LayoutInflater.from(mActivity)
            var totalFee = 0.00
            list.forEach { item ->
                val itemView = layoutInflater?.inflate(R.layout.sl_item_contract_entrust_detail, ll_warp_layout, false)
                ll_warp_layout.addView(itemView)
                //成交时间
                findChildView(R.id.tv_deal_time,itemView)?.text = TimeFormatUtils.timeStampToDate(TimeFormatUtils.getUtcTimeToMillis(item.created_at), "yyyy-MM-dd  HH:mm:ss")
                findChildView(R.id.tv_deal_time_title,itemView)?.onLineText("sl_str_deal_time")
                //成交价格
                if(isForceCloseOrder!!){
                    findChildView(R.id.tv_item_deal_price_value,itemView)?.text = "--"
                }else{
                    findChildView(R.id.tv_item_deal_price_value,itemView)?.text = dfDefault.format(MathHelper.round(item.px, contract.price_index))
                }
                findChildView(R.id.tv_item_deal_price,itemView)?.text  = getLineText("sl_str_deal_price")+" (${contract.quote_coin})"
                //成交数量
                findChildView(R.id.tv_item_volume_value,itemView)?.text = dfVol.format(MathHelper.round(item.qty))
                findChildView(R.id.tv_item_volume_title,itemView)?.onLineText("sl_str_make_volume_unit")
                //成交金额
                val value: Double = ContractCalculate.CalculateContractValue(
                        item.qty,
                        item.px,
                        contract)
                if(isForceCloseOrder!!){
                    findChildView(R.id.tv_item_deal_avg_price_value,itemView)?.text = "--"
                }else{
                    findChildView(R.id.tv_item_deal_avg_price_value,itemView)?.text = dfDefault.format(MathHelper.round(value, contract.value_index))
                }
                findChildView(R.id.tv_item_deal_avg_price,itemView)?.text = getLineText("sl_str_deal_money")+" (${contract.margin_coin})"
                findChildView(R.id.tv_item_fee_title,itemView)?.onLineText("sl_str_fee")
                //手续费
                if(!TextUtils.isEmpty(item.take_fee)&& item.take_fee > "0"){
                    totalFee+= item.take_fee.toDouble()
                    findChildView(R.id.tv_item_fee_value,itemView)?.text = item.take_fee
                }else  if(!TextUtils.isEmpty(item.make_fee)){
                    var makeFee = abs(item.make_fee.toDouble())
                    totalFee+= makeFee
                    findChildView(R.id.tv_item_fee_value,itemView)?.text = makeFee.toString()
                }else{
                    findChildView(R.id.tv_item_fee_value,itemView)?.text = item.take_fee
                }
            }
            tv_fee_value.text = dfDefault.format(totalFee)
        }
    }

     private fun  findChildView(resId:Int, targetView: View?):TextView?{
        return targetView?.findViewById(resId)
    }


    companion object{
        fun show(activity: Activity,order: ContractOrder){
            val intent = Intent(activity,SlContractEntrustDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("order",order)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }
}