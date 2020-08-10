package com.yjkj.chainup.new_version.view.depth

import android.arch.lifecycle.Observer
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.ParamConstant.TYPE_LIMIT
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.DataManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.new_version.activity.leverage.NLeverFragment
import com.yjkj.chainup.new_version.activity.leverage.TradeFragment
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.fragment.NCVCTradeFragment
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.SymbolInterceptUtils
import kotlinx.android.synthetic.main.depth_horizontal_layout.view.*
import kotlinx.android.synthetic.main.depth_vertical_layout.view.*
import kotlinx.android.synthetic.main.depth_vertical_layout.view.trade_amount_view
import kotlinx.android.synthetic.main.depth_vertical_layout.view.tv_change_depth
import kotlinx.android.synthetic.main.item_depth_buy.view.*
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import kotlinx.android.synthetic.main.trade_amount_view.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @Author: Bertking
 * @Date：2019-09-10-17:27
 * @Description:
 */
class NVerticalDepthLayout @JvmOverloads constructor(context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val TAG = NVerticalDepthLayout::class.java.simpleName
    var dialog: TDialog? = null

    var depth_level = 0
    val depthLevels = arrayListOf<String>()

    var transactionData: JSONObject? = null


    /**
     * 卖盘的item
     */
    private var sellViewList = mutableListOf<View>()

    /**
     * 买盘的item
     */
    private var buyViewList = mutableListOf<View>()


    var coinMapData: JSONObject? = NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
        set(value) {
            field = value
            trade_amount_view?.coinMapData = value
            trade_amount_view?.setPrice()
            judgeDepthLevel(isInited = true)
            setDepth(context)
        }


    init {
        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.depth_vertical_layout, this, true)

        setDepth(context)

        TradeFragment.liveData4DepthData.observe((this.context as NewMainActivity), Observer<MessageEvent> {
            if (null == it) {
                return@Observer
            }

            if (TradeFragment.currentIndex == ParamConstant.CVC_INDEX_TAB) {
                if (it.isLever) {
                    return@Observer
                }
            } else {
                if (!it.isLever) {
                    return@Observer
                }
            }
            when (it.msg_type) {
                MessageEvent.DEPTH_DATA_TYPE -> {
                    if (null != it.msg_content) {
                        transactionData = it.msg_content as JSONObject
                        refreshDepthView()
                    }
                }
            }
        })




        NLiveDataUtil.observeData((this.context as NewMainActivity), Observer<MessageEvent> {
            if (null == it) {
                return@Observer
            }

            if (TradeFragment.currentIndex == ParamConstant.CVC_INDEX_TAB) {
                if (it.isLever) {
                    return@Observer
                }
            } else {
                if (!it.isLever) {
                    return@Observer
                }
            }


            when (it.msg_type) {
                MessageEvent.TAB_TYPE -> {
                    coinMapData = if (it.isLever) {
                        NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol4Lever)
                    } else {
                        NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
                    }
                    setDepth(context)
                    clearDepthView()
                }

                // 切换币种
                MessageEvent.symbol_switch_type -> {
                    if (null != it.msg_content) {
                        var symbol = it.msg_content as String
                        if (symbol != coinMapData?.optString("symbol")) {
                            coinMapData = NCoinManager.getSymbolObj(symbol)
                            judgeDepthLevel(isInited = true)
                            setDepth(context)
                        }
                    }
                }

            }

        })

        /**
         * 选择深度
         */
        tv_change_depth?.setOnClickListener {
            dialog = NewDialogUtils.showBottomListDialog(context, depthLevels, depthLevels.indexOf(depth_level.toString()), object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    dialog?.dismiss()
                    if (judgeDepthLevel() != item) {
                        tv_change_depth?.text = LanguageUtil.getString(context,"kline_action_depth") + data[item]
                        depth_level = data[item].toInt()

                        if (TradeFragment.currentIndex == ParamConstant.LEVER_INDEX_TAB) {
                            NLeverFragment.curDepthIndex = item
                        } else {
                            NCVCTradeFragment.curDepthIndex = item
                        }

                        /**
                         * 切记：这里需要给后端传的仅仅是下标,深度有3个维度，0、1、2
                         * 详情：http://wiki.365os.com/pages/viewpage.action?pageId=2261055
                         * 日了动物园啦...
                         */
                        var isLever = TradeFragment.currentIndex == ParamConstant.LEVER_INDEX_TAB
                        LogUtil.d(TAG, "tv_change_depth==isLever is $isLever,item is $item")
                        NLiveDataUtil.postValue(MessageEvent(MessageEvent.DEPTH_LEVEL_TYPE, item, isLever))

                    }
                }
            })

        }

        initDepthView()

    }


    /**
     * 买卖盘
     * 初始化交易详情记录view
     */
    private fun initDepthView() {

        for (i in 1..5) {
            /**
             * 卖盘
             */
            val view: View = context.layoutInflater.inflate(R.layout.item_depth_sell, null)
//            val layout = view.findViewById<FrameLayout>(R.id.fl_bg_item)
            view.tv_price_item_for_depth.setTextColor(ColorUtil.getMainColorType(false))

            NLiveDataUtil.observeForeverData {
                if (null != it && MessageEvent.color_rise_fall_type == it.msg_type) {
                    view.tv_price_item_for_depth.setTextColor(ColorUtil.getMainColorType(false))
                }
            }

            view.setOnClickListener {
                val result = view.tv_price_item_for_depth?.text.toString()
                click2Data(result)
            }

            ll_sell.addView(view)
            sellViewList.add(view)

            /***********/

            /**
             * 买盘
             */
            val view1: View = context.layoutInflater.inflate(R.layout.item_depth_buy, null)

            view1.tv_price_item_for_depth.setTextColor(ColorUtil.getMainColorType())
            NLiveDataUtil.observeForeverData {
                if (null != it && MessageEvent.color_rise_fall_type == it.msg_type) {
                    view1.tv_price_item_for_depth.setTextColor(ColorUtil.getMainColorType())
                }
            }

            view1.setOnClickListener {
                val result = view1.tv_price_item_for_depth?.text.toString()
                click2Data(result)
            }
            ll_buy.addView(view1)
            buyViewList.add(view1)
        }
    }


    /**
     * 重置买卖盘的数据
     */
    fun clearDepthView() {
        if (sellViewList.isEmpty()) return
        if (buyViewList.isEmpty()) return

        for (i in 0 until 5) {
            sellViewList[i].run {
                tv_price_item_for_depth.text = "--"
                tv_quantity_item_for_depth.text = "--"
                fl_bg_item_for_depth.setBackgroundResource(R.color.transparent)
            }

            buyViewList[i].run {
                tv_price_item_for_depth.text = "--"
                tv_quantity_item_for_depth.text = "--"
                fl_bg_item_for_depth.setBackgroundResource(R.color.transparent)
            }
        }
    }

    private fun click2Data(result: String) {
        if (!TextUtils.isEmpty(result) && result != "--" && result != "null") {
            if (trade_amount_view.priceType == TYPE_LIMIT) {
                val pricePrecision = coinMapData?.optInt("price", 2) ?: 2
                et_price?.setText(BigDecimalUtils.divForDown(result, pricePrecision).toPlainString())
                tv_convert_price?.text = RateManager.getCNYByCoinMap(DataManager.getCoinMapBySymbol(PublicInfoDataService.getInstance().currentSymbol), result)
            }
        }
    }


    /**
     * 买盘交易量最大的
     */


    /**
     * 更新买卖盘的数据
     */
    fun refreshDepthView() {
        LogUtil.d(TAG, "refreshDepthView==transactionData is $transactionData")
        transactionData?.run {
            val tick = this.optJSONObject("tick")
            /**
             * 卖盘交易量最大的
             */
            val askList = arrayListOf<JSONArray>()
            val asks = tick.optJSONArray("asks") ?: return
            for (i in (asks.length() - 1).downTo(0)) {
                askList.add(asks.optJSONArray(i))
            }

            val askMaxVolJson = askList.maxBy {
                it.optDouble(1)
            }
            val askMaxVol = askMaxVolJson?.optDouble(1) ?: 1.0
            Log.d(TAG, "========askMAX:$askMaxVol=======")

            /**
             * 买盘交易量最大的
             */
            val buyList = arrayListOf<JSONArray>()
            val buys = tick.optJSONArray("buys")
            for (i in 0 until buys.length()) {
                buyList.add(buys.optJSONArray(i))
            }

            val buyMaxVolJson = buyList.maxBy {
                it.optDouble(1)
            }
            val buyMaxVol = buyMaxVolJson?.optDouble(1) ?: 1.0
            LogUtil.d(TAG, "========buyMAX:$buyMaxVol=======")

            val maxVol = Math.max(askMaxVol, buyMaxVol)

            LogUtil.d(TAG, "========maxVol:$maxVol=========")


            for (i in 0 until sellViewList.size) {
                /**
                 * 卖盘
                 */
                if (askList.size > sellViewList.size) {
                    val subList = askList.subList(askList.size - sellViewList.size, askList.size).reversed()
                    if (subList.isNotEmpty()) {
                        /**
                         * 移除大值
                         */
                        /*****深度背景色START****/
                        sellViewList[i].fl_bg_item_for_depth?.backgroundColor = ColorUtil.getMinorColorType(isRise = false)

                        val layoutParams = sellViewList[i].fl_bg_item_for_depth?.layoutParams
                        val width = (subList[i].optDouble(1) / maxVol) * measuredWidth / 2
                        layoutParams?.width = width.toInt()
                        LogUtil.d(TAG, "====1111=width:${width.toInt()}=======")

                        sellViewList[i].run {
                            fl_bg_item_for_depth?.layoutParams = layoutParams

                            /*****深度背景色END****/
                            tv_price_item_for_depth?.text =
                                    SymbolInterceptUtils.interceptData(
                                            subList[i].optString(0).trim(),
                                            depth_level,
                                            "price")
                            tv_quantity_item_for_depth?.text =
                                    BigDecimalUtils.showDepthVolume(subList[i].optString(1).trim())
                        }
                    }
                } else {
                    sellViewList[i].run {
                        tv_price_item_for_depth?.text = "--"
                        tv_quantity_item_for_depth?.text = "--"
                        sellViewList[i].fl_bg_item_for_depth?.backgroundColor = ColorUtil.getColor(R.color.transparent)

                    }

                    if (i < askList.size) {
                        /*****深度背景色START****/
                        sellViewList[i].fl_bg_item_for_depth?.backgroundColor = ColorUtil.getMinorColorType(isRise = false)
                        val layoutParams = sellViewList[i].fl_bg_item_for_depth?.layoutParams
                        val width = (askList.reversed()[i].optDouble(1) / maxVol) * measuredWidth / 2
                        layoutParams?.width = width.toInt()
                        LogUtil.d(TAG, "====1111=width:${width.toInt()}=======")
                        sellViewList[i].fl_bg_item_for_depth?.layoutParams = layoutParams

                        /*****深度背景色END****/
                        val price4DepthSell = askList.reversed()[i].optString(0).trim()

                        sellViewList[i].run {
                            tv_price_item_for_depth?.text =
                                    SymbolInterceptUtils.interceptData(
                                            price4DepthSell,
                                            depth_level,
                                            "price")

                            tv_quantity_item_for_depth?.text =
                                    BigDecimalUtils.showDepthVolume(askList.reversed()[i].optString(1).trim())
                        }

                    }
                }

                /**
                 * 买盘
                 */
                if (buyList.size > i) {
                    /*****深度背景色START****/
                    buyViewList[i].fl_bg_item_for_depth?.backgroundColor = ColorUtil.getMinorColorType()

                    val layoutParams = buyViewList[i].fl_bg_item_for_depth.layoutParams
                    val width = (buyList[i].optDouble(1) / maxVol) * measuredWidth / 2
                    layoutParams.width = width.toInt()
                    LogUtil.d(TAG, "==buy==1111=width:${width.toInt()}=======")
                    buyViewList[i].fl_bg_item_for_depth?.layoutParams = layoutParams
                    /*****深度背景色END****/
                    val price4DepthBuy = buyList[i].optString(0).trim()

                    Log.d(TAG, "=======price4Depth:$price4DepthBuy===")
                    buyViewList[i].run {
                        tv_price_item_for_depth?.text =
                                SymbolInterceptUtils.interceptData(
                                        price4DepthBuy,
                                        depth_level,
                                        "price")

                        tv_quantity_item_for_depth?.text =
                                BigDecimalUtils.showDepthVolume(buyList[i].optString(1).trim())
                    }
                } else {
                    buyViewList[i].run {
                        tv_price_item_for_depth?.text = "--"
                        tv_quantity_item_for_depth?.text = "--"
                        fl_bg_item_for_depth?.setBackgroundResource(R.color.transparent)
                    }
                }
            }
        }
    }

    private fun setDepth(context: Context) {
        depthLevels.clear()
        val depth = coinMapData?.optString("depth")
        if (!TextUtils.isEmpty(depth)) {
            val depths = depth?.split(",") ?: emptyList()
            if (depths.isNotEmpty()) {
                depths.forEach {
                    val depth = if (it.contains(".")) {
                        it.replace("0.", "").length.toString()
                    } else {
                        "0"
                    }
                    depthLevels.add(depth)
                }
            }

        }
        val curDepthLevel = judgeDepthLevel()

        if (depthLevels.size > curDepthLevel) {
            depth_level = depthLevels[curDepthLevel].toInt()
        }
        tv_change_depth?.text = LanguageUtil.getString(context,"kline_action_depth") + depth_level
    }


    private fun judgeDepthLevel(isInited: Boolean = false): Int {
        return if (TradeFragment.currentIndex == ParamConstant.LEVER_INDEX_TAB) {
            if (isInited) {
                0
            } else {
                NLeverFragment.curDepthIndex
            }
        } else {
            if (isInited) {
                0
            } else {
                NCVCTradeFragment.curDepthIndex
            }
        }
    }

    fun changeData(data: JSONObject?) {
        if (data != null) {
            trade_amount_view.changeSellOrBuyData(data)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            setDepth(context)
        }
    }


}