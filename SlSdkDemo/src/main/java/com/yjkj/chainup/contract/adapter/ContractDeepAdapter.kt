package com.yjkj.chainup.contract.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.DepthData
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import java.util.*
import kotlin.math.max

/**
 * Created by zj on 2018/3/8.
 */
class ContractDeepAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContractId = 0
    private var mPriceIndex = 8
    private var mVolIndex = 2
    private var mMaxBuyVol = 0.0
    private var mMaxSellVol = 0.0
    private val mSells = ArrayList<DepthData>()
    private val mBuys = ArrayList<DepthData>()
    private var contract: Contract? = null

    class OrderBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pbBidVolume: ProgressBar = itemView.findViewById(R.id.pb_bid_volume)
        var tvBidVolume: TextView = itemView.findViewById(R.id.tv_bid_volume)
        var tvBidPrice: TextView = itemView.findViewById(R.id.tv_bid_price)
        var pbAskVolume: ProgressBar = itemView.findViewById(R.id.pb_ask_volume)
        var tvAskVolume: TextView = itemView.findViewById(R.id.tv_ask_volume)
        var tvAskPrice: TextView = itemView.findViewById(R.id.tv_ask_price)
    }

    fun setData(sells: List<DepthData>?, buys: List<DepthData>?, contractId: Int) {
        try {
            if (contractId > 0) {
                mContractId = contractId
                contract = ContractPublicDataAgent.getContract(contractId)
                if (contract != null) {
                    mPriceIndex = contract!!.price_index - 1
                    mVolIndex = contract!!.vol_index
                }
                mSells.clear()
                var sellIndex = 0
                if (sells != null) {
                    mMaxSellVol = 0.0
                    for (i in sells.indices) {
                        if (sells[i].vol == 0) {
                            continue
                        }
                        if (sellIndex >= 20) {
                            break
                        }
                        mSells.add(sells[i])
                        sellIndex++
                        if (sells[i].vol > mMaxSellVol) {
                            mMaxSellVol = sells[i].vol.toDouble()
                        }
                    }
                }
                mBuys.clear()
                var buyIndex = 0
                if (buys != null) {
                    mMaxBuyVol = 0.0
                    for (i in buys.indices) {
                        if (buys[i].vol == 0) {
                            continue
                        }
                        if (buyIndex >= 20) {
                            break
                        }
                        buyIndex++
                        mBuys.add(buys[i])
                        if (buys[i].vol > mMaxBuyVol) {
                            mMaxBuyVol = buys[i].vol.toDouble()
                        }
                    }
                }

                val addSellCount = 20 - mSells.size
                if (addSellCount > 0) {
                    for (i in 0 until addSellCount) {
                        val data = DepthData()
                        data.price = "0"
                        data.vol = 0
                        mSells.add(data)
                    }
                }

                val addBuyCount = 20 - mBuys.size
                if (addBuyCount > 0) {
                    for (i in 0 until addBuyCount) {
                        val data = DepthData()
                        data.price = "0"
                        data.vol = 0
                        mBuys.add(data)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun getItemCount(): Int {
        return max(mSells.size, mBuys.size)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as OrderBookViewHolder
        val dfVol = NumberUtil.getDecimal(mVolIndex)
        val dfPrice = NumberUtil.getDecimal(mPriceIndex)
        if (mBuys.size > position) {
            if (mBuys[position].vol == 0) {
                itemViewHolder.tvBidVolume.text = "--"
                itemViewHolder.tvBidPrice.text = "--"
                itemViewHolder.pbBidVolume.progress = 100
            } else {
                val vol = MathHelper.round(mBuys[position].vol.toString(), mVolIndex)
                val price = MathHelper.round(mBuys[position].price, mPriceIndex)
                itemViewHolder.tvBidVolume.text = dfVol.format(vol)
                itemViewHolder.tvBidPrice.text = dfPrice.format(price)
                itemViewHolder.pbBidVolume.progress = 100 - (100 * vol / mMaxBuyVol).toInt()
                // LogUtil.d("lb","mMaxSellVol:"+mMaxSellVol+";vol:"+vol+":index:"+position);
                if (mContractId > 0) {
                    if (contract != null) {
                        itemViewHolder.tvBidVolume.text = ContractCalculate.getVolUnitNoSuffix(contract, vol, price, LogicContractSetting.getContractUint(mContext))
                    }
                }
            }
        } else {
            itemViewHolder.tvBidVolume.text = "--"
            itemViewHolder.tvBidPrice.text = "--"
            itemViewHolder.pbBidVolume.progress = 100
        }
        if (mSells.size > position) {
            if (mSells[position].vol == 0) {
                itemViewHolder.tvAskVolume.text = "--"
                itemViewHolder.tvAskPrice.text = "--"
                itemViewHolder.pbAskVolume.progress = 0
            } else {
                val vol = MathHelper.round(mSells[position].vol.toString(), mVolIndex)
                val price = MathHelper.round(mSells[position].price, mPriceIndex)
                itemViewHolder.tvAskVolume.text = dfVol.format(vol)
                itemViewHolder.tvAskPrice.text = dfPrice.format(price)
                itemViewHolder.pbAskVolume.progress = (100 * vol / mMaxSellVol).toInt()
                if (mContractId > 0) {
                    if (contract != null) {
                        itemViewHolder.tvAskVolume.text = ContractCalculate.getVolUnitNoSuffix(contract, vol, price,LogicContractSetting.getContractUint(mContext))
                    }
                }
            }
        } else {
            itemViewHolder.tvAskVolume.text = "--"
            itemViewHolder.tvAskPrice.text = "--"
            itemViewHolder.pbAskVolume.progress = 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sl_item_order_book, parent, false)
        return OrderBookViewHolder(v)
    }

}