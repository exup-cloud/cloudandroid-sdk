package com.yjkj.chainup.new_version.contract

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.fengniao.news.util.JsonUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yjkj.chainup.R
import com.yjkj.chainup.bean.QuotesData
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.new_version.adapter.HKLineScaleAdapter
import com.yjkj.chainup.new_version.kline.bean.KLineBean
import com.yjkj.chainup.new_version.kline.data.DataManager
import com.yjkj.chainup.new_version.kline.data.KLineChartAdapter
import com.yjkj.chainup.new_version.kline.view.MainKlineViewStatus
import com.yjkj.chainup.new_version.kline.view.vice.ViceViewStatus
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.KLineUtil
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.GZIPUtils
import com.yjkj.chainup.util.WsLinkUtils
import kotlinx.android.synthetic.main.activity_horizon_market_detail.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.math.BigDecimal
import java.net.URI
import java.nio.ByteBuffer

/**
 * @description : 横屏行情的详细界面(合约)
 * @date 2019-5-23
 * @author Bertking
 *
 *  由于合约&币币的ws地址不一样，故暂不共用横屏Activity
 *  TODO 测试性能，再行决定
 */

class HorizonContractMarketDetailActivity : AppCompatActivity() {
    val TAG = HorizonContractMarketDetailActivity::class.java.simpleName
    lateinit var context: Context
    private var curTime = KLineUtil.getCurTime4KLine().values.first()

    lateinit var socketClient: WebSocketClient
    var klineData: ArrayList<KLineBean> = arrayListOf()
    private val adapter by lazy { KLineChartAdapter() }


    var currentContract = Contract2PublicInfoManager.currentContract()
    var symbol = ""


    companion object {
        private const val CURRENT_TIME = "curTime"

        fun enter2(context: Context, curTime: String) {
            val intent = Intent(context, HorizonContractMarketDetailActivity::class.java)
            intent.putExtra(CURRENT_TIME, curTime)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        context = this
        setContentView(R.layout.activity_horizon_market_detail)
        v_kline?.adapter = adapter
        v_kline?.startAnimation()
        v_kline?.justShowLoading()

        initKLineScale()

        action4KLineIndex()

        tv_rose_title?.text = LanguageUtil.getString(context,"contract_text_upsdowns")
        tv_high_price_title?.text = LanguageUtil.getString(context,"kline_text_high")
        tv_low_price_title?.text = LanguageUtil.getString(context,"kline_text_low")

        tv_main_title?.text = LanguageUtil.getString(context,"kline_action_main")
        tv_vice_title?.text = LanguageUtil.getString(context,"kline_action_assistant")
        tv_rose_title?.text = LanguageUtil.getString(context,"contract_text_upsdowns")


        ContractMarketDetailActivity.liveData4Contract.observe(this, Observer {
            /**
             * 获取最近24H行情
             */
            if (::socketClient.isInitialized && socketClient.isOpen) {
                sendMsg(WsLinkUtils.tickerFor24HLink(currentContract?.symbol.toString().toLowerCase(), isSub = false))

                sendMsg(WsLinkUtils.tickerFor24HLink(it?.symbol.toString().toLowerCase()))
                /**
                 * 获取K线历史
                 */
                sendMsg(WsLinkUtils.getKLineHistoryLink(it?.symbol.toString().toLowerCase(), curTime).json)
            } else {
                initSocket()
            }

            currentContract = it!!
            symbol = currentContract?.symbol?.toLowerCase() ?: ""

            runOnUiThread {
                tv_coin_map?.text = currentContract?.baseSymbol + currentContract?.quoteSymbol + " " + Contract2PublicInfoManager.getContractType(context, currentContract?.contractType, currentContract?.settleTime)
                tv_close_price?.text = "--"

                tv_rose?.text = "--"
                tv_high_price?.text = "--"
                tv_low_price?.text = "--"
                tv_24h_vol?.text = "--"
            }
        })


        iv_close?.setOnClickListener {
            finish()
        }
    }

    /**
     * 渲染24H行情数据
     */
    private fun render24H(tick: QuotesData.Tick) {
        runOnUiThread {
            if (tick.rose >= 0) {
                tv_close_price?.textColor = ColorUtil.getMainColorType()
            } else {
                tv_close_price?.textColor = ColorUtil.getMainColorType(isRise = false)
            }

            tv_close_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick?.close, currentContract?.pricePrecision
                    ?: 2)
//            tv_converted_close_price?.text = RateManager.getCNYByCoinName(currentContract?.baseSymbol
//                    ?: "", tick.close, isLogo = false)
            RateManager.getRoseText(tv_rose, tick.rose.toString())

            tv_high_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.high, currentContract?.pricePrecision
                    ?: 2)
            tv_low_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.low, currentContract?.pricePrecision
                    ?: 2)
            tv_24h_vol?.text = BigDecimalUtils.showDepthVolume(tick.vol)
        }
    }

    private fun setKLineViewIndexStatus(isMain: Boolean = true, position: Int = 0) {
//        val parent = if (isMain) rg_main else rg_vice
//        for (i in 0 until (parent!!.childCount ?: 0)) {
//            if (i == position) {
//                (parent.getChildAt(position) as RadioButton).isChecked = true
//            } else {
//                (parent.getChildAt(i) as RadioButton).isChecked = false
//            }
//        }
    }

    /**
     * K线的指标处理
     */
    private fun action4KLineIndex() {

        when (KLineUtil.getMainIndex()) {
            MainKlineViewStatus.MA.status -> {
                setKLineViewIndexStatus(position = 0)
            }

            MainKlineViewStatus.BOLL.status -> {
                setKLineViewIndexStatus(position = 1)
            }

            MainKlineViewStatus.NONE.status -> {
                setKLineViewIndexStatus(position = 2)
            }
        }

        when (KLineUtil.getViceIndex()) {
            ViceViewStatus.MACD.status -> {
                setKLineViewIndexStatus(isMain = false, position = 0)
            }

            ViceViewStatus.KDJ.status -> {
                setKLineViewIndexStatus(isMain = false, position = 1)
            }

            ViceViewStatus.RSI.status -> {
                setKLineViewIndexStatus(isMain = false, position = 2)

            }

            ViceViewStatus.WR.status -> {
                setKLineViewIndexStatus(isMain = false, position = 3)

            }

            ViceViewStatus.NONE.status -> {
                setKLineViewIndexStatus(isMain = false, position = 4)
            }
        }


        rg_main?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_ma -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.MA)
                    KLineUtil.setMainIndex(MainKlineViewStatus.MA.status)
                }
                R.id.rb_boll -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.BOLL)
                    KLineUtil.setMainIndex(MainKlineViewStatus.BOLL.status)
                }

                R.id.rb_hide_main -> {
                    v_kline?.changeMainDrawType(MainKlineViewStatus.NONE)
                    KLineUtil.setMainIndex(MainKlineViewStatus.NONE.status)
                }
            }
        }


        rg_vice?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_macd -> {
                    v_kline?.setChildDraw(0)
                    KLineUtil.setViceIndex(ViceViewStatus.MACD.status)

                }
                R.id.rb_kdj -> {
                    v_kline?.setChildDraw(1)
                    KLineUtil.setViceIndex(ViceViewStatus.KDJ.status)
                }

                R.id.rb_rsi -> {
                    v_kline?.setChildDraw(2)
                    KLineUtil.setViceIndex(ViceViewStatus.RSI.status)
                }

                R.id.rb_wr -> {
                    v_kline?.setChildDraw(3)
                    KLineUtil.setViceIndex(ViceViewStatus.WR.status)
                }

                R.id.rb_hide_vice -> {
                    v_kline?.hideChildDraw()
                    KLineUtil.setViceIndex(ViceViewStatus.NONE.status)
                }
            }
        }
    }

    /**
     * 处理K线刻度
     */
    private fun initKLineScale() {
        rv_kline_scale?.isLayoutFrozen = true
        rv_kline_scale?.setHasFixedSize(true)
        val klineScale = KLineUtil.getKLineScale()
        val layoutManager = GridLayoutManager(context, klineScale.size)
        layoutManager.isAutoMeasureEnabled = false
        rv_kline_scale?.layoutManager = layoutManager
        val adapter = HKLineScaleAdapter(klineScale)
        rv_kline_scale?.adapter = adapter
        adapter.bindToRecyclerView(rv_kline_scale ?: return)
        /**
         * 分时线
         */
        v_kline?.setMainDrawLine(KLineUtil.getCurTime4Index() == 0)

        adapter.setOnItemClickListener { viewHolder, view, position ->
            /**
             * 分时线
             */
            v_kline?.setMainDrawLine(position == 0)
            if (position != KLineUtil.getCurTime4Index()) {
                for (i in 0 until klineScale.size) {
                    val textView = viewHolder.getViewByPosition(i, R.id.tv_scale) as TextView
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    textView.setTextColor(ColorUtil.getColor(R.color.normal_text_color))
                }
                val textView = viewHolder.getViewByPosition(position, R.id.tv_scale) as TextView
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.kline_item_selected_shape)
                textView.setTextColor(ColorUtil.getColor(R.color.text_color))
                KLineUtil.setCurTime4KLine(position)
                switchKLineScale(klineScale[position])
            }
        }
    }

    /**
     * 初始化WebSocket
     */
    private fun initSocket() {
        socketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_CONTRACT_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.i(TAG, "onOpen")
                //K线历史数据
                sendMsg(WsLinkUtils.getKLineHistoryLink(symbol, curTime).json)
                //获取最近24H行情
                sendMsg(WsLinkUtils.tickerFor24HLink(symbol))
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.i(TAG, "onClose")
            }


            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                val data = GZIPUtils.uncompressToString(bytes?.array())
                if (!data.isNullOrBlank()) {
                    if (data.contains("ping")) {
                        val replace = data.replace("ping", "pong")
                        sendMsg(replace)
                    }
                    handleData(data)
                }

            }

            override fun onMessage(message: String?) {
                Log.i(TAG, "onMessage")
            }

            override fun onError(ex: Exception?) {
                Log.i(TAG, "onError")
            }
        }

        Thread(Runnable
        {
            socketClient.connect()
            Log.i(TAG, "connect")
        }).start()
    }

    /**
     * 处理 24H,KLine数据
     */
    fun handleData(data: String) {
        try {
            var jsonObj = JSONObject(data)
            if (!jsonObj.isNull("tick")) {
                /**
                 * 24H行情
                 */
                if (jsonObj.getString("channel") == WsLinkUtils.tickerFor24HLink(symbol = symbol, isChannel = true)) {
                    Log.d(TAG, "====24H行情:$jsonObj")
                    val quotesData = JsonUtils.convert2Quote(jsonObj.toString())
                    render24H(quotesData.tick)
                }


                /**
                 * 最新K线数据
                 */
                if (jsonObj.getString("channel") == WsLinkUtils.getKlineNewLink(symbol, curTime).channel) {
                    Log.d(TAG, "=======最新K线：========$jsonObj")

                    doAsync {
                        /**
                         * 这里需要处理：重复添加的问题
                         */
                        val kLineEntity = KLineBean()
                        val data = jsonObj.optJSONObject("tick")
                        val newDate = data.optLong("id")
                        kLineEntity.id = newDate
                        kLineEntity.openPrice = BigDecimal(data.optString("open")).toFloat()
                        kLineEntity.closePrice = BigDecimal(data.optString("close")).toFloat()
                        kLineEntity.highPrice = BigDecimal(data.optString("high")).toFloat()
                        kLineEntity.lowPrice = BigDecimal(data.optString("low")).toFloat()
                        kLineEntity.volume = BigDecimal(data.optString("vol")).toFloat()

                        Log.d(TAG, "===123:====" + kLineEntity.id + ",: == " + (newDate.toString()).toLong())
                        try {
                            val dateCompare = newDate.compareTo(klineData.last().id)
                            when (dateCompare) {
                                0 -> {
                                    Log.d("======calculate======", "重复")
                                    klineData[klineData.lastIndex] = kLineEntity
                                    DataManager.calculate(klineData)
                                    adapter.changeItem(klineData.lastIndex, kLineEntity)
                                }

                                -1 -> {
                                    Log.d("======calculate======", "脏数据。。。")
                                }

                                1 -> {
                                    Log.d("======calculate======", "新加")
                                    klineData.add(kLineEntity)
                                    DataManager.calculate(klineData)
                                    runOnUiThread {
                                        adapter.addItem(kLineEntity)
                                        v_kline?.startAnimation()
                                        v_kline?.refreshEnd()
                                    }
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }
            }


            if (!jsonObj.isNull("data")) {
                /**
                 * 请求(req) ----> K线历史数据
                 * 即：K线图的历史数据
                 *
                 * channel ---> channel":"market_ltcusdt_kline_1week
                 */
                Log.d(TAG, "====历史K线：$jsonObj")
                handlerKLineHistory(data)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 处理K线历史数据
     * @param data K线历史数据
     */
    private fun handlerKLineHistory(data: String) {
        doAsync {
            val json = JSONObject(data)
            val type = object : TypeToken<ArrayList<KLineBean>>() {
            }.type
            val gson = GsonBuilder().setPrettyPrinting().create()
            klineData = gson.fromJson(json.getJSONArray("data").toString(), type)
            DataManager.calculate(klineData)
            uiThread {
                adapter.addFooterData(klineData)
                v_kline?.startAnimation()
                v_kline?.refreshEnd()
            }
            /**
             * 获取最新K线数据
             */
            sendMsg(WsLinkUtils.getKlineNewLink(symbol, curTime).json)
        }
    }

    /**
     * 切换K线刻度
     * @param kLineScale K线刻度
     */
    private fun switchKLineScale(kLineScale: String) {
        if (curTime != kLineScale) {
            /**
             * 取消订阅
             */
            sendMsg(WsLinkUtils.getKlineNewLink(symbol, curTime, false).json)
            curTime = kLineScale
            /**
             * 请求历史
             */
            sendMsg(WsLinkUtils.getKLineHistoryLink(symbol, curTime).json)
            /**
             * 订阅
             */
            sendMsg(WsLinkUtils.getKlineNewLink(symbol, curTime).json)
        }
    }

    /**
     * Ws 发送消息
     */
    private fun sendMsg(msg: String) {
        Log.i(TAG, "sendMsg = $msg")
        if (::socketClient.isInitialized) {
            Log.d(TAG, "=====sendMsg===" + socketClient.isOpen)
            if (socketClient.isOpen) {
                try {
                    socketClient.send(msg)
                } catch (e: WebsocketNotConnectedException) {
                    e.printStackTrace()
                }
            }
        } else {
            initSocket()
        }
    }


    override fun onStop() {
        super.onStop()
        if (::socketClient.isInitialized && socketClient.isOpen) {
            socketClient.closeBlocking()
            socketClient.close()
        }
    }
}
