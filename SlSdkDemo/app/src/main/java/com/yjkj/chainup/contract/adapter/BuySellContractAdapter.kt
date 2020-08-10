package com.yjkj.chainup.contract.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.contract.sdk.data.Contract
import com.contract.sdk.data.DepthData
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.util.NToastUtil
import java.lang.Exception
import java.util.*
import kotlin.math.max

class BuySellContractAdapter(private val mContext: Context, private val mListener: OnBuySellContractClickedListener?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mFlag = 1 //1 buy ;2 sell
    private var mDecimals = 8
    private var mVolIndex = 2
    private var mPriceIndex = 8
    private var mMaxVol = 0.0
    private var mShowNum = 6
    private val mShowNews: MutableList<DepthData> = arrayListOf()
     val mList = ArrayList<DepthData>()
    private var mContract: Contract? = null

    private val colorRed =  mContext.resources.getColor(R.color.main_red)
    private val colorGreen = mContext.resources.getColor(R.color.main_green)

    interface OnBuySellContractClickedListener {
        fun onBuySellContractClick(depthData: DepthData?, showVol: String?, flag: Int)
        fun onBuySellContractVolClick(depthData: DepthData?, showVol: String?, flag: Int)
    }

    class BuySellContractHolder(var vRoot: View, type: Int) : RecyclerView.ViewHolder(vRoot) {
        var tvVolume: TextView
        var tvPrice: TextView
        var pbVolume: ProgressBar
        var rootView : View

        init {
            pbVolume = vRoot.findViewById(R.id.pb_volume)
            tvVolume = vRoot.findViewById(R.id.tv_volume)
            tvPrice = vRoot.findViewById(R.id.tv_price)
            rootView = vRoot
        }
    }

    fun setData(list: List<DepthData>?, flag: Int, show_num: Int, contract: Contract?) {
        try {
            mFlag = flag
            mShowNum = show_num
            mContract = contract
            if (show_num == 0) {
                mShowNews.clear()
                notifyDataSetChanged()
                return
            }
            if (mContract != null) {
                mVolIndex = mContract!!.vol_index
                mPriceIndex = mContract!!.price_index - 1
                mDecimals = mContract!!.price_index
            }
            if (list != null) {
                mList.clear()
                mList.addAll(list)
                mMaxVol = 0.0
                mShowNews.clear()
                var index = 0
                for (i in mList.indices) {
                    val item = mList[i]
                    if (index < show_num) {
                        val vol = item.vol.toDouble()
                        if (vol > 0) {
                            if (vol > mMaxVol) {
                                mMaxVol = vol
                            }
                            index++
                            mShowNews.add(mList[i])
                        }
                    } else {
                        break
                    }
                }

                if (mFlag == 1) {
                    for (i in mShowNews.size until show_num) {
                        val depthData = DepthData()
                        depthData.price = "0"
                        depthData.vol = 0
                        mShowNews.add(depthData)
                    }
                } else if (mFlag == 2) {
                    val emptyNum = show_num - mShowNews.size
                    for (i in 0 until emptyNum) {
                        val depthData = DepthData()
                        depthData.price = "0"
                        depthData.vol = 0
                        mShowNews.add( depthData)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return mShowNews.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as BuySellContractHolder
        val data = if (mFlag == 1)  mShowNews[position] else  mShowNews[mShowNum - position - 1]
        if (data.price.compareTo("0") == 0 || data.vol == 0) {
            itemViewHolder.tvVolume.text = "--,--"
            itemViewHolder.tvPrice.text = "--,--"
            itemViewHolder.pbVolume.progress = 0
            itemViewHolder.tvPrice.setTextColor(if(mFlag == 1) colorGreen else colorRed)
            itemViewHolder.pbVolume.progressDrawable =  if (mFlag == 1) mContext.resources.getDrawable(R.drawable.sl_buy_progress) else mContext.resources.getDrawable(R.drawable.sl_sell_progress)
//            itemViewHolder.tvPrice.setOnClickListener(null)
//            itemViewHolder.tvVolume.setOnClickListener(null)
            return
        }
        val dfPrice = NumberUtil.getDecimal(mPriceIndex)
        val maxVol = mMaxVol
        val vol = MathHelper.round(data.vol.toString(), mVolIndex)
        val price = MathHelper.round(data.price, mPriceIndex)
        itemViewHolder.tvVolume.text = ContractCalculate.getVolUnitNoSuffix(mContract, vol, price, LogicContractSetting.getContractUint(mContext))
        itemViewHolder.tvPrice.text = dfPrice.format(price)
        itemViewHolder.pbVolume.progress = 100 - (100 * vol / maxVol).toInt()
        itemViewHolder.tvPrice.setTextColor(if(mFlag == 1) colorGreen else colorRed)
        itemViewHolder.pbVolume.progressDrawable =  if (mFlag == 1) mContext.resources.getDrawable(R.drawable.sl_buy_progress) else mContext.resources.getDrawable(R.drawable.sl_sell_progress)
        itemViewHolder.rootView.setOnClickListener(View.OnClickListener {
            if (mListener == null) {
                return@OnClickListener
            }
            mListener.onBuySellContractClick(data, "0", mFlag)
        })
//        itemViewHolder.tvVolume.setOnClickListener(View.OnClickListener {
//            if (mListener == null) {
//                return@OnClickListener
//            }
//            mListener.onBuySellContractVolClick(data, "0", mFlag)
//        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sl_item_buy_sell_contract, parent, false)
        return BuySellContractHolder(v, viewType)
    }

}