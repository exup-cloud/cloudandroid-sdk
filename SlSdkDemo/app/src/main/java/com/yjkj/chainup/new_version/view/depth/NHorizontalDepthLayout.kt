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
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.ParamConstant.TYPE_LIMIT
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.DataManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.new_version.activity.leverage.NLeverFragment
import com.yjkj.chainup.new_version.activity.leverage.TradeFragment
import com.yjkj.chainup.new_version.fragment.NCVCTradeFragment
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.SymbolInterceptUtils
import kotlinx.android.synthetic.main.depth_horizontal_layout.view.*
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import kotlinx.android.synthetic.main.trade_amount_view.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author: Bertking
 * @Date：2019-09-06-11:15
 * @Description:
 */
class NHorizontalDepthLayout @JvmOverloads constructor(context: Context,
                                                       attrs: AttributeSet? = null,
                                                       defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = NHorizontalDepthLayout::class.java.simpleName


    var dialog: TDialog? = null

    var tapeDialog: TDialog? = null

    var tapeLevel: Int = 0

    var depth_level = 0

    val depthLevels = arrayListOf<String>()

    var transactionData: JSONObject? = null


    var coinMapData: JSONObject? = NCoinManager.getSymbolObj(PublicInfoDataService.getInstance().currentSymbol)
        set(value) {
            field = value
            trade_amount_view?.coinMapData = value ?: return
            trade_amount_view?.setPrice()
            judgeDepthLevel(isInited = true)
            setDepth(context)
        }

    /**
     * 卖盘的item
     */
    private var sellViewList = mutableListOf<View>()

    /**
     * 买盘的item
     */
    private var buyViewList = mutableListOf<View>()


    init {

        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.depth_horizontal_layout, this, true)

        setDepth(context)

        tv_contract_text_price?.text = LanguageUtil.getString(context, "contract_text_price")
        tv_charge_text_volume?.text = LanguageUtil.getString(context, "charge_text_volume")
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
                        val jsonObject = it.msg_content as JSONObject
                        transactionData = jsonObject
                        refreshDepthView(jsonObject)
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

                    tapeLevel = if (it.isLever) NLeverFragment.tapeLevel else NCVCTradeFragment.tapeLevel
                    changeTape(tapeLevel, false)
                    setDepth(context)
                }

                MessageEvent.symbol_switch_type -> {
                    if (null != it.msg_content) {
                        val symbol = it.msg_content as String
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

                    val curDepthLevel = judgeDepthLevel()
                    Log.d(TAG, "=======curDepth:${curDepthLevel},click:$item=====")

                    if (curDepthLevel != item) {
                        tv_change_depth?.text = LanguageUtil.getString(context, "kline_action_depth") + data[item]
                        depth_level = data[item].toInt()
                        /**
                         * 切记：这里需要给后端传的仅仅是下标,深度有3个维度，0、1、2
                         * 详情：http://wiki.365os.com/pages/viewpage.action?pageId=2261055
                         */
                        NLiveDataUtil.postValue(MessageEvent(MessageEvent.DEPTH_LEVEL_TYPE, item, TradeFragment.currentIndex == ParamConstant.LEVER_INDEX_TAB))

                    }
                }
            })

        }


        initDetailView()

        /**
         * 改变盘口的样式
         */
        ib_tape?.setOnClickListener {
            tapeDialog = NewDialogUtils.showBottomListDialog(context, arrayListOf(LanguageUtil.getString(context, "contract_text_defaultMarket"), LanguageUtil.getString(context, "contract_text_buyMarket"), LanguageUtil.getString(context, "contract_text_sellMarket")), tapeLevel, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    tapeDialog?.dismiss()
                    tapeLevel = item
                    if (TradeFragment.currentIndex == ParamConstant.LEVER_INDEX_TAB) {
                        NLeverFragment.tapeLevel = tapeLevel
                    } else {
                        NCVCTradeFragment.tapeLevel = tapeLevel
                    }
                    changeTape(item)
                }
            })
        }

    }

    private fun setDepth(context: Context) {
        depthLevels.clear()
        val depthData = coinMapData?.optString("depth")
        if (!TextUtils.isEmpty(depthData)) {
            val depths = depthData?.split(",") ?: emptyList()
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
        tv_change_depth?.text = LanguageUtil.getString(context, "kline_action_depth") + depth_level
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

    fun changeTape(item: Int, needData: Boolean = true) {
        when (item) {
            AppConstant.DEFAULT_TAPE -> {
                ll_buy_price?.visibility = View.VISIBLE
                ll_sell_price?.visibility = View.VISIBLE
                v_tape_line?.visibility = View.VISIBLE
                ColorUtil.setTapeIcon(ib_tape, AppConstant.DEFAULT_TAPE)
                initDetailView()
            }

            AppConstant.BUY_TAPE -> {
                ll_buy_price?.visibility = View.VISIBLE
                ll_sell_price?.visibility = View.GONE
                v_tape_line?.visibility = View.GONE
                ColorUtil.setTapeIcon(ib_tape, AppConstant.BUY_TAPE)
                initDetailView(10)
            }

            AppConstant.SELL_TAPE -> {
                ll_buy_price?.visibility = View.GONE
                v_tape_line?.visibility = View.GONE
                ll_sell_price?.visibility = View.VISIBLE
                ColorUtil.setTapeIcon(ib_tape, AppConstant.SELL_TAPE)
                initDetailView(10)
            }
        }
        if (needData) {
            refreshDepthView(transactionData)
        }
    }

    /**
     * 买卖盘
     *
     * 初始化交易详情记录view
     */
    fun initDetailView(items: Int = 5) {
        sellViewList.clear()
        buyViewList.clear()

        if (ll_buy_price?.childCount ?: 0 > 0) {
            (ll_buy_price as LinearLayout).removeAllViews()
        }

        if (ll_sell_price?.childCount ?: 0 > 0) {
            (ll_sell_price as LinearLayout).removeAllViews()
        }

        val pricePrecision = coinMapData?.optInt("price", 2) ?: 2

        for (i in 0 until items) {
            /**
             * 卖盘
             */
            val sell_layout: View = context.layoutInflater.inflate(R.layout.item_transaction_detail, null)

            sell_layout.tv_price_item?.textColor = ColorUtil.getMainColorType(isRise = false)
            NLiveDataUtil.observeForeverData {
                if (null != it && MessageEvent.color_rise_fall_type == it.msg_type) {
                    sell_layout.tv_price_item?.textColor = ColorUtil.getMainColorType(isRise = false)
                }
            }

            sell_layout.setOnClickListener {
                val result = sell_layout.tv_price_item?.text.toString()
                click2Data(result)
            }
            sellViewList.add(sell_layout)

            /**
             * 买盘
             */
            val buy_layout: View = context.layoutInflater.inflate(R.layout.item_transaction_detail, null)

            buy_layout.tv_price_item?.textColor = ColorUtil.getMainColorType()
            NLiveDataUtil.observeForeverData {
                if (null != it && MessageEvent.color_rise_fall_type == it.msg_type) {
                    buy_layout.tv_price_item?.textColor = ColorUtil.getMainColorType()
                }
            }

            buy_layout.setOnClickListener {
                val result = buy_layout.tv_price_item?.text.toString()
                click2Data(result)
            }
            buyViewList.add(buy_layout)
        }


        buyViewList.forEach {
            ll_buy_price?.addView(it)
        }

        sellViewList.forEach {
            ll_sell_price?.addView(it)
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
     * 更新买卖盘的数据
     */
    fun refreshDepthView(data: JSONObject?) {
        data?.run {
            val tick = this.optJSONObject("tick")
            /**
             * 卖盘交易量最大的
             */
            val askList = arrayListOf<JSONArray>()
            val asks = tick.optJSONArray("asks")
            for (i in 0 until asks.length()) {
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

            /**
             * 买盘交易量最大的
             */
            val buyMaxVolJson = buyList.maxBy {
                it.optDouble(1)
            }
            val buyMaxVol = buyMaxVolJson?.optDouble(1) ?: 1.0
            Log.d(TAG, "========buyMAX:$buyMaxVol=======")

            val maxVol = Math.max(askMaxVol, buyMaxVol)

            Log.d(TAG, "========maxVol:$maxVol=========")

            sellTape(askList, maxVol)
            buyTape(buyList, maxVol)
        }


    }

    /**
     * 卖盘
     */
    private fun sellTape(list: ArrayList<JSONArray>, maxVol: Double) {
        list.sortByDescending {
            it.optDouble(0)
        }

        for (i in 0 until sellViewList.size) {
            /**
             * 卖盘
             */
            if (list.size > sellViewList.size) {
                val subList = list.subList(list.size - sellViewList.size, list.size)
                if (subList.isNotEmpty()) {
                    /*****深度背景色START****/
                    sellViewList[i].fl_bg_item.backgroundColor = ColorUtil.getMinorColorType(isRise = false)
                    val layoutParams = sellViewList[i].fl_bg_item.layoutParams
                    val curVolume = subList[i].optDouble(1)
                    val width = (curVolume / maxVol) * measuredWidth * 0.4
                    Log.d(TAG, "=======sell==curVolume is $curVolume,maxVolume is $maxVol,showBgwidth is $width，itemWidth is ${measuredWidth * 0.4}")
                    layoutParams.width = width.toInt()
                    sellViewList[i].fl_bg_item.layoutParams = layoutParams
                    /*****深度背景色END****/
                    sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                            subList[i].optString(0).replace("\"", "").trim(),
                            depth_level,
                            "price")
                    sellViewList[i].tv_quantity_item.text = BigDecimalUtils.showDepthVolume(subList[i].optString(1))
                }
            } else {
                Log.d(TAG, "======VVV=======")
                val temp = sellViewList.size - list.size
                sellViewList[i].tv_price_item.text = "--"
                sellViewList[i].tv_quantity_item.text = "--"
                sellViewList[i].ll_item.backgroundColor = ColorUtil.getColor(R.color.transparent)
                if (i >= temp) {
                    /*****深度背景色START****/
                    sellViewList[i].fl_bg_item.backgroundColor = ColorUtil.getMinorColorType(isRise = false)
                    val layoutParams = sellViewList[i].fl_bg_item.layoutParams
                    val width = (list[i - temp].optDouble(1) / maxVol) * measuredWidth * 0.4
                    layoutParams.width = width.toInt()
                    sellViewList[i].fl_bg_item.layoutParams = layoutParams
                    /*****深度背景色END****/
                    sellViewList[i].tv_price_item.text = SymbolInterceptUtils.interceptData(
                            list[i - temp].optString(0).replace("\"", "").trim(),
                            depth_level,
                            "price")
                    sellViewList[i].tv_quantity_item.text = BigDecimalUtils.showDepthVolume(list[i - temp].optString(1))

                } else {
                    sellViewList[i].run {
                        tv_price_item.text = "--"
                        tv_quantity_item.text = "--"
                        fl_bg_item.setBackgroundResource(R.color.transparent)
                    }

                }
            }


        }
    }

    /**
     * 买盘
     */
    private fun buyTape(list: ArrayList<JSONArray>, maxVol: Double) {

        /**
         * 买盘取最大
         */
        list.sortByDescending {
            it.optDouble(0)
        }

        for (i in 0 until buyViewList.size) {
            /**
             * 买盘
             */
            if (list.size > i) {
                /*****深度背景色START****/
                buyViewList[i].fl_bg_item.backgroundColor = ColorUtil.getMinorColorType()
                val layoutParams = buyViewList[i].fl_bg_item.layoutParams
                val width = (list[i].optDouble(1) / maxVol) * measuredWidth * 0.4
                layoutParams.width = width.toInt()
                buyViewList[i].fl_bg_item.layoutParams = layoutParams

                /*****深度背景色END****/
                buyViewList[i].tv_price_item.text =
                        SymbolInterceptUtils.interceptData(
                                list[i].optString(0).replace("\"", "").trim(),
                                depth_level,
                                "price")
                buyViewList[i].tv_quantity_item.text = BigDecimalUtils.showDepthVolume(list[i].optString(1).trim())
            } else {
                buyViewList[i].run {
                    tv_price_item.text = "--"
                    tv_quantity_item.text = "--"
                    fl_bg_item.setBackgroundResource(R.color.transparent)
                }

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