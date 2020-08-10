package com.yjkj.chainup.new_version.redpackage.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wx.wheelview.adapter.BaseWheelAdapter
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.new_version.redpackage.bean.RedPackageInitInfo
import com.yjkj.chainup.util.BigDecimalUtils

/**
 * @Author: Bertking
 * @Date：2019-07-02-16:52
 * @Description: 滚轮适配器
 */
class WheelViewAdapter(context: Context) : BaseWheelAdapter<RedPackageInitInfo.Symbol>() {
    val context = context
    override fun bindView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder: ViewHolder
        var v: View

        if (convertView == null) {
            v = View.inflate(context, R.layout.item_wheel_symbol, null)
            viewHolder = ViewHolder(v)
            v.tag = viewHolder
        } else {
            v = convertView
            viewHolder = v.tag as ViewHolder
        }
        val coin = mList[position].coinSymbol ?: ""
        viewHolder.tvCoin.text = NCoinManager.getShowMarket(coin)


        val amount = mList[position].amount.toString()
        if(amount == "-1"){
            viewHolder.tvAmount.text = ""
        }else{
            val intercept = BigDecimalUtils.intercept(amount, NCoinManager.getCoinShowPrecision(coin)).toPlainString()
            viewHolder.tvAmount.text = intercept
        }

        return v
    }

    class ViewHolder(var item: View) {
        var tvCoin: TextView = item.findViewById(R.id.tv_coin) as TextView
        var tvAmount: TextView = item.findViewById(R.id.tv_amount) as TextView
    }
}