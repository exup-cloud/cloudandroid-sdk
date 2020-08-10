package com.yjkj.chainup.new_version.contract

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import com.fengniao.news.util.JsonUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.yjkj.chainup.R
import com.yjkj.chainup.bean.QuotesData
import com.yjkj.chainup.db.constant.HomeTabMap
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.new_version.activity.NewBaseActivity
import com.yjkj.chainup.new_version.adapter.KLineScaleAdapter
import com.yjkj.chainup.new_version.adapter.NVPagerAdapter
import com.yjkj.chainup.new_version.adapter.SelectContractAdapter
import com.yjkj.chainup.new_version.bean.FlagBean
import com.yjkj.chainup.new_version.dialog.DialogUtil
import com.yjkj.chainup.new_version.fragment.DealtRecordFragment
import com.yjkj.chainup.new_version.fragment.DepthFragment
import com.yjkj.chainup.new_version.kline.bean.KLineBean
import com.yjkj.chainup.new_version.kline.data.DataManager
import com.yjkj.chainup.new_version.kline.data.KLineChartAdapter
import com.yjkj.chainup.new_version.kline.view.MainKlineViewStatus
import com.yjkj.chainup.new_version.kline.view.vice.ViceViewStatus
import com.yjkj.chainup.new_version.view.CustomCheckBoxView
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.activity_contract_market_detail.*
import kotlinx.android.synthetic.main.market_info_kline_panel.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.math.BigDecimal
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

/**
 * @description : 币对行情的详细界面(合约)
 * @date 2019-5-20
 * @author Bertking
 *
 * PS: TODO 该界面跟币币的详情相似度为95%
 */
class ContractMarketDetailActivity : NewBaseActivity() {

    /**
     * 显示指标orScale
     */
    var isShow = false

    var showedView: View? = null
    var klineState = 0


    private var titles: ArrayList<String> = arrayListOf()
    private val fragments = arrayListOf<Fragment>()


    var currentContract = Contract2PublicInfoManager.currentContract()


    private var klineData: ArrayList<KLineBean> = arrayListOf()
    private val adapter by lazy { KLineChartAdapter() }

    private var curTime = KLineUtil.getCurTime4KLine().values.first()

    lateinit var socketClient: WebSocketClient


    companion object {
        /**
         * K线刻度
         */
        const val KLINE_SCALE = 1
        /**
         * K线指标
         */
        const val KLINE_INDEX = 2


        val liveData4Contract = MutableLiveData<ContractBean>()


        fun enter2(context: Context) {
            val intent = Intent(context, ContractMarketDetailActivity::class.java)
            context.startActivity(intent)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_market_detail)
        context = this
        initHeaderData()
        initDepthAndDeals()
        setDepthSymbol(currentContract)
        liveData4Contract.observe(this, Observer<ContractBean> {
            Log.d(TAG, "=====Previous:${currentContract?.symbol},Now:${it?.symbol}=======")
            if (currentContract?.symbol != it?.symbol) {
                setDepthSymbol(currentContract)
                /**
                 * 获取最近24H行情
                 */
                sendMsg(WsLinkUtils.tickerFor24HLink(currentContract?.symbol.toString().toLowerCase(), isSub = false))
                sendMsg(WsLinkUtils.tickerFor24HLink(it?.symbol.toString().toLowerCase()))
                /**
                 * 获取K线历史
                 */
                sendMsg(WsLinkUtils.getKLineHistoryLink(it?.symbol.toString().toLowerCase(), curTime).json)

                currentContract = it

                setDepthSymbol(currentContract)

                runOnUiThread {
                    initHeaderData()
                }


            }
        })


        selectContractList()

        /**
         * 禁止DrawerLayout侧边滑动
         */
        ly_drawer?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        v_kline?.adapter = adapter
        v_kline?.startAnimation()
        v_kline?.justShowLoading()



        btn_buy?.setOnClickListener {
            /*NewMainActivity.liveData4Position.postValue(
                    if (PublicInfoDataService.getInstance().otcOpen(null)) {
                        4
                    } else {
                        3
                    }

            )*/
            hometab_switch()
            ContractFragment.liveData4Contract.postValue(currentContract)
            finish()
        }

        btn_sell?.setOnClickListener {
            /*NewMainActivity.liveData4Position.postValue(if (PublicInfoDataService.getInstance().otcOpen(null)) {
                4
            } else {
                3
            })*/

            hometab_switch()

            ContractFragment.liveData4Contract.postValue(currentContract)
            finish()
        }


        /**
         * 横屏KLine
         */
        tv_landscape?.setOnClickListener {
            HorizonContractMarketDetailActivity.enter2(context, "15min")
            liveData4Contract.postValue(currentContract)
        }

        ib_back?.setOnClickListener {
            finish()
        }
        ib_share?.setOnClickListener {
            DialogUtil.showKLineShareDialog(context = context)
        }

        /**
         * 切换币对
         */
        tv_coin_map?.setOnClickListener {
            ly_drawer?.openDrawer(GravityCompat.START)
            selectContractList()
            initSocket2()
        }

        /**
         * KLine刻度
         */
        tv_scale?.text = if (KLineUtil.getCurTime4KLine().keys.first() == 0) "line" else curTime
        tv_scale?.setOnClickListener {
            isShow = !isShow
            if (isShow) {
                rv_kline_scale?.visibility = View.GONE
                tv_scale?.labelBackgroundColor = ColorUtil.getColor(R.color.normal_icon_color)
                tv_scale?.textColor = ColorUtil.getColor(R.color.normal_text_color)
            } else {
                rv_kline_scale?.visibility = View.VISIBLE
                showedView = rv_kline_scale
                klineState = KLINE_SCALE
                tv_scale?.labelBackgroundColor = ColorUtil.getColor(R.color.main_blue)
                tv_scale?.textColor = ColorUtil.getColor(R.color.text_color)
            }
        }

        /**
         * KLine指标
         */
        tv_indicator?.setOnClickListener {
            isShow = !isShow
            if (isShow) {
                ly_kline_panel?.visibility = View.GONE
                tv_indicator?.labelBackgroundColor = ColorUtil.getColor(R.color.normal_icon_color)
                tv_indicator?.textColor = ColorUtil.getColor(R.color.normal_text_color)
            } else {
                ly_kline_panel?.visibility = View.VISIBLE
                showedView = ly_kline_panel
                klineState = KLINE_INDEX
                tv_indicator?.labelBackgroundColor = ColorUtil.getColor(R.color.main_blue)
                tv_indicator?.textColor = ColorUtil.getColor(R.color.text_color)
            }
        }

        initViewColor()

        initKLineScale()


        action4KLineIndex()
    }

    private fun hometab_switch() {
        var messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
        var bundle = Bundle()
        val homeTabType = HomeTabMap.maps.get(HomeTabMap.contractTab) ?: 3
        bundle.putInt(ParamConstant.homeTabType,homeTabType)
        //NLiveDataUtil.postValue(messageEvent)
        messageEvent.msg_content = bundle
        EventBusUtil.post(messageEvent)
    }


    private fun initHeaderData() {
        tv_coin_map?.text = currentContract?.baseSymbol + currentContract?.quoteSymbol + " " + Contract2PublicInfoManager.getContractType(this, currentContract?.contractType, currentContract?.settleTime)
        tv_close_price?.text = "--"
        tv_converted_close_price?.text = "--"
        tv_rose?.text = "--"
        tv_high_price?.text = "--"
        tv_low_price?.text = "--"
        tv_24h_vol?.text = "--"
    }


    override fun onResume() {
        super.onResume()
        setDepthSymbol(currentContract)
        initSocket()
    }

    /**
     * K线的指标处理
     */
    private fun action4KLineIndex() {

        when (KLineUtil.getMainIndex()) {
            MainKlineViewStatus.MA.status -> {
                rb_ma?.isLabelEnable = true
                rb_hide_main?.isChecked = false


            }

            MainKlineViewStatus.BOLL.status -> {
                rb_boll?.isLabelEnable = true
                rb_hide_main?.isChecked = false
            }

            MainKlineViewStatus.NONE.status -> {
                rb_hide_main?.isChecked = true
            }
        }

        when (KLineUtil.getViceIndex()) {
            ViceViewStatus.MACD.status -> {
                rb_macd?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.KDJ.status -> {
                rb_kdj?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.RSI.status -> {
                rb_rsi?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.WR.status -> {
                rb_wr?.isLabelEnable = true
                rb_hide_vice?.isChecked = false
            }

            ViceViewStatus.NONE.status -> {
                rb_hide_vice?.isChecked = true
            }
        }


        rb_ma.setOnClickListener {
            rb_ma?.isLabelEnable = true
            rb_boll?.isLabelEnable = false
            rb_hide_main?.isChecked = false


            v_kline?.changeMainDrawType(MainKlineViewStatus.MA)
            KLineUtil.setMainIndex(MainKlineViewStatus.MA.status)
        }

        rb_boll.setOnClickListener {
            rb_boll?.isLabelEnable = true
            rb_ma?.isLabelEnable = false
            rb_hide_main?.isChecked = false

            v_kline?.changeMainDrawType(MainKlineViewStatus.BOLL)
            KLineUtil.setMainIndex(MainKlineViewStatus.BOLL.status)
        }


        rb_hide_main.setOnClickListener {
            rb_hide_main?.isChecked = true

            rb_boll?.isChecked = false
            rb_boll?.isLabelEnable = false

            rb_ma?.isChecked = false
            rb_ma?.isLabelEnable = false
            v_kline?.changeMainDrawType(MainKlineViewStatus.NONE)
            KLineUtil.setMainIndex(MainKlineViewStatus.NONE.status)
        }

        /**
         * -----------副图--------------
         */
        rb_macd?.setOnClickListener {
            rb_macd?.isChecked = true
            rb_macd?.isLabelEnable = true

            rb_kdj?.isChecked = false
            rb_kdj?.isLabelEnable = false

            rb_wr?.isChecked = false
            rb_wr?.isLabelEnable = false

            rb_rsi?.isChecked = false
            rb_rsi?.isLabelEnable = false

            rb_hide_vice?.isChecked = false


            v_kline?.setChildDraw(0)
            KLineUtil.setViceIndex(ViceViewStatus.MACD.status)
        }

        rb_kdj?.setOnClickListener {

            rb_macd?.isChecked = false
            rb_macd?.isLabelEnable = false

            rb_kdj?.isChecked = true
            rb_kdj?.isLabelEnable = true

            rb_wr?.isChecked = false
            rb_wr?.isLabelEnable = false

            rb_rsi?.isChecked = false
            rb_rsi?.isLabelEnable = false

            rb_hide_vice?.isChecked = false


            v_kline?.setChildDraw(1)
            KLineUtil.setViceIndex(ViceViewStatus.KDJ.status)
        }


        rb_wr?.setOnClickListener {

            rb_macd?.isChecked = false
            rb_macd?.isLabelEnable = false

            rb_kdj?.isChecked = false
            rb_kdj?.isLabelEnable = false

            rb_wr?.isChecked = true
            rb_wr?.isLabelEnable = true

            rb_rsi?.isChecked = false
            rb_rsi?.isLabelEnable = false

            rb_hide_vice?.isChecked = false

            v_kline?.setChildDraw(3)
            KLineUtil.setViceIndex(ViceViewStatus.WR.status)
        }

        rb_rsi.setOnClickListener {

            rb_macd?.isChecked = false
            rb_macd?.isLabelEnable = false

            rb_kdj?.isChecked = false
            rb_kdj?.isLabelEnable = false

            rb_wr?.isChecked = false
            rb_wr?.isLabelEnable = false

            rb_rsi?.isChecked = true
            rb_rsi?.isLabelEnable = true

            rb_hide_vice?.isChecked = false


            v_kline?.setChildDraw(2)
            KLineUtil.setViceIndex(ViceViewStatus.RSI.status)
        }

        rb_hide_vice?.setOnClickListener {

            rb_macd?.isChecked = false
            rb_macd?.isLabelEnable = false

            rb_kdj?.isChecked = false
            rb_kdj?.isLabelEnable = false

            rb_wr?.isChecked = false
            rb_wr?.isLabelEnable = false

            rb_rsi?.isChecked = false
            rb_rsi?.isLabelEnable = false

            rb_hide_vice?.isChecked = true

            v_kline?.hideChildDraw()
            KLineUtil.setViceIndex(ViceViewStatus.NONE.status)
        }

    }

    private fun initSocket() {
        socketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_CONTRACT_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                /**
                 * 获取最近24H行情
                 */
                sendMsg(WsLinkUtils.tickerFor24HLink(currentContract?.symbol.toString().toLowerCase()))
                /**
                 * 获取K线历史
                 */
                sendMsg(WsLinkUtils.getKLineHistoryLink(currentContract?.symbol.toString().toLowerCase(), curTime).json)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "====onClose:--code:$code,$reason:$reason=======")
            }

            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                if (bytes == null) return
                val data = GZIPUtils.uncompressToString(bytes.array())
                if (!data.isNullOrBlank()) {
                    if (data.contains("ping")) {
                        val replace = data.replace("ping", "pong")
                        sendMsg(replace)
                    } else {
                        handleData(data)
                    }
                }
            }

            override fun onMessage(message: String?) {
            }

            override fun onError(ex: Exception?) {
                Log.d(TAG, "====onError:${ex?.printStackTrace()}=======")
            }
        }
        socketClient.connect()
    }


    private fun initViewColor() {
        btn_buy?.backgroundColor = ColorUtil.getMainColorType()
        btn_sell?.backgroundColor = ColorUtil.getMainColorType(isRise = false)

        tv_close_price?.textColor = ColorUtil.getMainColorType()
        tv_rose?.textColor = ColorUtil.getMainColorType()
    }

    private fun initDepthAndDeals() {
        titles.add(LanguageUtil.getString(context,"kline_action_depth"))
        titles.add(LanguageUtil.getString(context,"kline_action_dealHistory"))
        fragments.add(DepthFragment.newInstance(viewPager = vp_depth_dealt))
        fragments.add(DealtRecordFragment.newInstance(viewPager = vp_depth_dealt))
        val vpAdapter = NVPagerAdapter(supportFragmentManager, titles, fragments)
        vp_depth_dealt?.adapter = vpAdapter
        vp_depth_dealt?.setScrollable(false)
        stl_depth_dealt?.setViewPager(vp_depth_dealt)
        vp_depth_dealt?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // 这里必须调用resetHeight，否则不会重新计算高度
                vp_depth_dealt?.resetHeight(position)
            }
        })
    }

    /**
     * 处理K线刻度
     */
    private fun initKLineScale() {
        rv_kline_scale?.isLayoutFrozen = true
        rv_kline_scale?.setHasFixedSize(true)
        val klineScale = KLineUtil.getKLineScale()
        val layoutManager = GridLayoutManager(context, 4)
        layoutManager.isAutoMeasureEnabled = false
        rv_kline_scale?.layoutManager = layoutManager
        val adapter = KLineScaleAdapter(klineScale)
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
                    val boxView = viewHolder?.getViewByPosition(i, R.id.cbtn_view) as CustomCheckBoxView
                    boxView.setCenterColor(ColorUtil.getColor(R.color.normal_text_color))
                    boxView.setCenterSize(12f)
                    boxView.setIsNeedDraw(false)
                    boxView.isChecked = false
                }
                val boxView = viewHolder?.getViewByPosition(position, R.id.cbtn_view) as CustomCheckBoxView
                boxView.isChecked = true
                boxView.setIsNeedDraw(true)
                boxView.setCenterSize(12f)
                boxView.setCenterColor(ColorUtil.getColor(R.color.text_color))
                KLineUtil.setCurTime4KLine(position)
                switchKLineScale(klineScale[position])
                tv_scale?.text = if (position == 0) "line" else curTime

            } else {
                val boxView = viewHolder?.getViewByPosition(position, R.id.cbtn_view) as CustomCheckBoxView
                boxView.isChecked = true
                boxView.setIsNeedDraw(true)
                boxView.setCenterSize(12f)
                boxView.setCenterColor(ColorUtil.getColor(R.color.text_color))
                KLineUtil.setCurTime4KLine(position)
                switchKLineScale(klineScale[position])
            }
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
            sendMsg(WsLinkUtils.getKlineNewLink(currentContract?.symbol.toString().toLowerCase()
                    , curTime, false).json)
            curTime = kLineScale
            /**
             * 请求历史
             */
            sendMsg(WsLinkUtils.getKLineHistoryLink(currentContract?.symbol.toString().toLowerCase(), curTime).json)
            /**
             * 订阅
             */
            sendMsg(WsLinkUtils.getKlineNewLink(currentContract?.symbol.toString().toLowerCase(), curTime).json)
        }
    }


    /**
     * 处理 24H,KLine数据
     */
    fun handleData(data: String) {
        Log.d(TAG, "=======data:$data=======")
        try {
            val jsonObj = JSONObject(data)
            if (!jsonObj.isNull("tick")) {
                /**
                 * 24H行情
                 */
                if (jsonObj.getString("channel") == WsLinkUtils.tickerFor24HLink(currentContract?.symbol.toString().toLowerCase(),isChannel = true)) {
                    Log.d(TAG, "====24H行情:$jsonObj")
                    val quotesData = JsonUtils.convert2Quote(jsonObj.toString())
                    render24H(quotesData.tick)
                }


                /**
                 * 最新K线数据
                 */
                if (jsonObj.getString("channel") == WsLinkUtils.getKlineNewLink(currentContract?.symbol.toString().toLowerCase(), curTime).channel) {
                    Log.d(TAG, "=======最新K线：========$jsonObj")

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
     * 渲染24H行情数据
     */
    private fun render24H(tick: QuotesData.Tick) {
        runOnUiThread {
            if (tick.rose >= 0.0) {
                tv_close_price?.textColor = ColorUtil.getMainColorType()
            } else {
                tv_close_price?.textColor = ColorUtil.getMainColorType(isRise = false)
            }

            tv_close_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.close, currentContract?.pricePrecision
                    ?: 2)
            /**
             * 收盘价的法币换算
             */
//            tv_converted_close_price?.text = RateManager.getCNYByCoinName(currentContract?.quoteSymbol
//                    ?: "", tick.close, isLogo = false)

            Log.d(TAG, "=coin:${currentContract?.baseSymbol},close:${tick.close}===========")

            RateManager.getRoseText(tv_rose, tick.rose.toString())

            tv_high_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.high, currentContract?.pricePrecision
                    ?: 2)
            tv_low_price?.text = Contract2PublicInfoManager.cutValueByPrecision(tick.low, currentContract?.pricePrecision
                    ?: 2)
            tv_24h_vol?.text = BigDecimalUtils.showDepthVolume(tick.vol)
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
            sendMsg(WsLinkUtils.getKlineNewLink(currentContract?.symbol?.toLowerCase()
                    ?: "", curTime).json)
        }
    }


    /**
     * WebSocket 发送消息
     */
    private fun sendMsg(msg: String) {
        Log.i(TAG, msg)
        if (::socketClient.isInitialized) {
            Log.d(TAG, "=====sendMsg===" + socketClient.isOpen)
            if (socketClient.isOpen) {
                try {
                    socketClient.send(msg)
                } catch (e: WebsocketNotConnectedException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    socketClient.connectBlocking()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            initSocket()
        }
    }

    /**
     * 处理 点击控件外，隐藏
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "========dispatchTouchEvent=======")
            val viewRect = Rect()
            showedView?.getGlobalVisibleRect(viewRect)

            if (!viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                if (showedView?.visibility == View.VISIBLE) {
                    val alphaAnimation = AlphaAnimation(1f, 0f)
                    alphaAnimation.duration = 100
                    isShow = false
                    showedView?.startAnimation(alphaAnimation)
                    showedView?.visibility = View.GONE

                    if (klineState == KLINE_SCALE) {
                        tv_scale?.labelBackgroundColor = ColorUtil.getColor(R.color.normal_icon_color)
                        tv_scale?.textColor = ColorUtil.getColor(R.color.normal_text_color)
                    } else {
                        tv_indicator?.labelBackgroundColor = ColorUtil.getColor(R.color.normal_icon_color)
                        tv_indicator?.textColor = ColorUtil.getColor(R.color.normal_text_color)
                    }
                } else {
                    isShow = true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    val contractList = Contract2PublicInfoManager.getAllContracts()
    lateinit var selectContractAdapter: SelectContractAdapter

    private fun selectContractList() {
        selectContractAdapter = SelectContractAdapter(contractList)
        selectContractAdapter.setOnItemClickListener { adapter, view, position ->
            liveData4Contract.postValue(adapter?.data?.get(position) as ContractBean)
            ly_drawer?.closeDrawer(GravityCompat.START)
            if (::mSocketClient.isInitialized && mSocketClient.isOpen) {
                mSocketClient.closeBlocking()
                mSocketClient.close()
            }

        }
        rv_contract?.adapter = selectContractAdapter
        rv_contract?.layoutManager = LinearLayoutManager(this)
        selectContractAdapter.bindToRecyclerView(rv_contract)
        rv_contract?.setHasFixedSize(true)
        selectContractAdapter.setEmptyView(EmptyForAdapterView(this))
        selectContractAdapter.notifyDataSetChanged()
    }


    private fun setDepthSymbol(bean: ContractBean?) {

    }


    private lateinit var mSocketClient: WebSocketClient


    private fun initSocket2() {
        mSocketClient = object : WebSocketClient(URI(ApiConstants.SOCKET_CONTRACT_ADDRESS)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.i(TAG, "onOpen")
                subMessage()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.i(TAG, "onClose$reason")
            }

            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                if (bytes == null) return
                val data = GZIPUtils.uncompressToString(bytes.array())
                if (!data.isNullOrBlank()) {
                    if (data.contains("ping")) {
                        val replace = data.replace("ping", "pong")
                        Log.d(TAG, "=====lllllllllllll=======$replace")
                        mSocketClient.send(replace)
                    } else {
                        handleContractData(data)
                    }
                }
            }

            override fun onMessage(message: String?) {
                Log.i(TAG, "onMessage")
            }

            override fun onError(ex: Exception?) {
                Log.i(TAG, "onError" + ex?.printStackTrace())
            }

        }

        mSocketClient.connect()

    }

    fun subMessage() {
        if (contractList == null || contractList.isEmpty()) return
        if (!::mSocketClient.isInitialized || !mSocketClient.isOpen) {
            return
        }
        if (contractList.isNotEmpty()) {
            contractList.forEach {
                try {
                    Log.d(TAG, "====线程${Thread.currentThread().name},symbol:${it.symbol}=====")
                    try {
                        mSocketClient.send(WsLinkUtils.tickerFor24HLink(it.symbol?.toLowerCase()
                                ?: return))
                    } catch (e: WebsocketNotConnectedException) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun handleContractData(data: String) {
        try {
            val json = JSONObject(data)
            Log.d(TAG, "====json:=$json")
            if (!json.isNull("tick")) {
                val symbol = json.getString("channel").replace("market_", "").replace("_ticker", "")
                contractList.forEach {
                    if (it.symbol.equals(symbol, ignoreCase = true)) {

                        var tick = json.optJSONObject("tick")

                        if (it.closePrice != tick.optString("close")) {
                            it.closePrice = tick.optString("close")
                            it.rose = tick.optString("close")
                            runOnUiThread {
                                selectContractAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
