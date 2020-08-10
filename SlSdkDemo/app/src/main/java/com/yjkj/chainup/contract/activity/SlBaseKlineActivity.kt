package com.yjkj.chainup.contract.activity

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractWsKlineType
import com.contract.sdk.data.KLineData
import com.contract.sdk.extra.dispense.DataKLineHelper
import com.contract.sdk.impl.ContractKlineListener
import com.contract.sdk.utils.MathHelper
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.kline.view.KLineChartView
import com.yjkj.chainup.new_version.activity.MarketDetail4Activity
import com.yjkj.chainup.new_version.adapter.HKLineScaleAdapter
import com.yjkj.chainup.new_version.adapter.KLineScaleAdapter
import com.yjkj.chainup.new_version.kline.bean.KLineBean
import com.yjkj.chainup.new_version.kline.data.DataManager
import com.yjkj.chainup.new_version.kline.data.KLineChartAdapter
import com.yjkj.chainup.new_version.view.CustomCheckBoxView
import com.yjkj.chainup.new_version.view.LabelTextView
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.GlideUtils
import com.yjkj.chainup.util.KLineUtil
import com.yjkj.chainup.util.LogUtil
import org.jetbrains.anko.textColor

abstract class SlBaseKlineActivity : NBaseActivity() {
    //合约id
    protected var contractId = 0
    protected var contract: Contract? = null

    /**
     * 显示指标orScale
     */
    private var isShow = false

    private var showedView: View? = null
    private var klineState = 0

    /*
  * KLine参数数据初始化
  */
    protected var main_index = 0
    protected var vice_index = 0
    protected var curTime = "15min"
    private var cur_time_index = 0
    private var klineScale = ArrayList<String>()
    private var themeMode = PublicInfoDataService.THEME_MODE_DAYTIME
    private var kLineLogo = ""
    private fun initKLineData() {
        main_index = KLineUtil.getMainIndex()
        vice_index = KLineUtil.getViceIndex()
        cur_time_index = KLineUtil.getCurTime4Index()

        curTime = KLineUtil.getCurTime4KLine().values.first()
        klineScale = KLineUtil.getKLineScale()

        themeMode = PublicInfoDataService.getInstance().themeMode
        kLineLogo = PublicInfoDataService.getInstance().getKline_background_logo_img(null, themeMode == PublicInfoDataService.THEME_MODE_DAYTIME)

    }

    protected val timeMap = HashMap<String, ContractWsKlineType>()

    private var klineData: ArrayList<KLineBean> = arrayListOf()
    private val adapter by lazy { KLineChartAdapter() }

    private var rv_kline_scale: RecyclerView? = null
    private var tv_scale: LabelTextView? = null
    private var tv_indicator: LabelTextView? = null
    private var ly_kline_panel: View? = null
    private var v_kline: KLineChartView? = null
    private var iv_logo: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContractPublicDataAgent.registerKlineWsListener(this, object : ContractKlineListener() {
            /**
             * 更新某一条
             */
            override fun onWsContractKlineChange(type: ContractWsKlineType, id: Int, data: KLineData) {
                if (id == contractId && TextUtils.equals(timeMap[curTime]?.name, type.name)) {
                    if (!klineData.isNullOrEmpty()) {
                        //if(klineData.last().id == data.timestamp){
                        val newItem = buildKLineBean(data)
                        klineData[klineData.lastIndex] = newItem
                        DataManager.calculate(klineData)
                        adapter.changeItem(klineData.lastIndex, newItem)
                        // }
                    }

                }
            }

            /**
             * 新增
             */
            override fun onWsContractKlineAdd(type: ContractWsKlineType, id: Int, data: ArrayList<KLineData>) {
                if (id == contractId && TextUtils.equals(timeMap[curTime]?.name, type.name)) {
                    val line = buildKLineBeanList(data)
                    klineData.addAll(line)
                    DataManager.calculate(klineData)
                    if (!klineData.isNullOrEmpty()) {
                        //if(data[0].timestamp > klineData.last().id){
                        adapter.addItems(line)
                        v_kline?.startAnimation()
                        v_kline?.refreshComplete()
                        //  }
                    } else {
                        adapter.addFooterData(klineData)
                        v_kline?.startAnimation()
                        v_kline?.refreshComplete()
                    }
                }
            }

            /**
             * 次数可能K线数据有异常，需从新API接口走一次全量
             */
            override fun doKLineApiRequest() {
                //ws断线重连后，重新初始化K线数据
                loadKlineDataFromNet(false, true)
            }

        })
    }

    fun loadCommonData() {
        contractId = intent.getIntExtra("contractId", 1)
        contract = ContractPublicDataAgent.getContract(contractId)
        if (contract == null) {
            finish()
            return
        }
        initKLineData()

        timeMap["line"] = ContractWsKlineType.WEBSOCKET_BIN1M
        timeMap["1min"] = ContractWsKlineType.WEBSOCKET_BIN1M
        timeMap["5min"] = ContractWsKlineType.WEBSOCKET_BIN5M
        timeMap["15min"] = ContractWsKlineType.WEBSOCKET_BIN15M
        timeMap["30min"] = ContractWsKlineType.WEBSOCKET_BIN30M
        timeMap["60min"] = ContractWsKlineType.WEBSOCKET_BIN1H
        timeMap["4h"] = ContractWsKlineType.WEBSOCKET_BIN4H
        timeMap["1day"] = ContractWsKlineType.WEBSOCKET_BIN1D
        timeMap["1week"] = ContractWsKlineType.WEBSOCKET_BIN1W
        timeMap["1month"] = ContractWsKlineType.WEBSOCKET_BIN30D
        //订阅Ticker
        ContractPublicDataAgent.subscribeTickerWs(contractId)
        /**
         * 加载K线数据
         */
        loadKlineDataFromNet(true)
    }

    fun initCommonView() {
        rv_kline_scale = findViewById(R.id.rv_kline_scale)
        tv_scale = findViewById(R.id.tv_scale)
        tv_indicator = findViewById(R.id.tv_indicator)
        ly_kline_panel = findViewById(R.id.ly_kline_panel)
        iv_logo = findViewById(R.id.iv_logo)
        v_kline = findViewById(R.id.v_kline)

        tv_indicator?.onLineText("kline_text_scale")

        if (isLandscape) {
            initHKLineScale()
        } else {
            initKLineScale()
        }
        GlideUtils.load(this, kLineLogo, iv_logo, RequestOptions())

        v_kline?.adapter = adapter
        v_kline?.isScrollEnable = true
        v_kline?.startAnimation()
        v_kline?.justShowLoading()

        v_kline?.setRefreshListener(object : KLineChartView.KChartRefreshListener {
            override fun onLoadMoreBegin(chart: KLineChartView) {
                LogUtil.d("DEBUG", "onLoadMoreBegin:")
                //加载下一屏数据
                loadKlineDataFromNet(forward = false, cleanCache = false)
            }

        })
    }

    fun initCommonListener() {
        /**
         * KLine刻度
         */
        tv_scale?.setOnClickListener {
            if (isShow) {
                showedView = rv_kline_scale
                klineState = MarketDetail4Activity.KLINE_SCALE
            }
            isShow = !isShow
            rv_kline_scale?.visibility = if (isShow) View.GONE else View.VISIBLE
            tv_scale?.run {
                labelBackgroundColor = ColorUtil.getColor(if (isShow) R.color.normal_icon_color else R.color.main_blue)
                textColor = ColorUtil.getColor(if (isShow) R.color.normal_text_color else R.color.text_color)
            }
        }
        /**
         * KLine指标
         */
        tv_indicator?.setOnClickListener {
            if (isShow) {
                showedView = ly_kline_panel
                klineState = MarketDetail4Activity.KLINE_INDEX
            }
            isShow = !isShow
            ly_kline_panel?.visibility = if (isShow) View.GONE else View.VISIBLE

            tv_indicator?.run {
                labelBackgroundColor = ColorUtil.getColor(if (isShow) R.color.normal_icon_color else R.color.main_blue)
                textColor = ColorUtil.getColor(if (isShow) R.color.normal_text_color else R.color.text_color)
            }
        }

    }

    /**
     * 处理K线刻度
     */
    protected fun initKLineScale() {
        rv_kline_scale?.isLayoutFrozen = true
        rv_kline_scale?.setHasFixedSize(true)
        tv_scale?.text = if (cur_time_index == 0) "line" else curTime

        val layoutManager = GridLayoutManager(mActivity, 4)
        layoutManager.isAutoMeasureEnabled = false
        rv_kline_scale?.layoutManager = layoutManager
        val adapter = KLineScaleAdapter(klineScale)
        rv_kline_scale?.adapter = adapter
        adapter.bindToRecyclerView(rv_kline_scale ?: return)
        /**
         * 分时线
         */
        v_kline?.setMainDrawLine(cur_time_index == 0)

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
     * 处理K线刻度
     */
    private fun initHKLineScale() {
        rv_kline_scale?.isLayoutFrozen = true
        rv_kline_scale?.setHasFixedSize(true)
        val klineScale = KLineUtil.getKLineScale()
        val layoutManager = GridLayoutManager(mContext, klineScale.size)
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
     * 切换K线刻度
     * @param kLineScale K线刻度
     */
    protected fun switchKLineScale(kLineScale: String?) {

        if (!TextUtils.equals(curTime, kLineScale)) {
            timeMap[curTime]?.let {
                //先取消旧的订阅
                ContractPublicDataAgent.unSubscribeKlineWs(contractId, it)
            }
            curTime = kLineScale ?: "15m"
            loadKlineDataFromNet()
            if(isLandscape){
                //横屏切换 需改变竖屏的时间区间
                EventBusUtil.post(MessageEvent(MessageEvent.sl_contract_switch_time_type,curTime))
            }
        }

    }

    private fun buildKLineBean(originData: KLineData): KLineBean {
        val newData = KLineBean()
        newData.id = originData.timestamp
        newData.openPrice = originData.open.toFloat()
        newData.closePrice = originData.close.toFloat()
        newData.highPrice = originData.high.toFloat()
        newData.lowPrice = originData.low.toFloat()
        newData.volume = MathHelper.round(originData.qty, 2).toFloat()
        return newData
    }

    private fun buildKLineBeanList(originData: ArrayList<KLineData>): ArrayList<KLineBean> {
        val newList = ArrayList<KLineBean>()
        originData?.forEach { data ->
            newList.add(buildKLineBean(data))
        }
        return newList
    }

    /**
     * 第一次通过接口获取K线数据
     */
    protected fun loadKlineDataFromNet(forward: Boolean = true, cleanCache: Boolean = false) {
        if (forward) {
            showLoadingDialog()
        }


        DataKLineHelper.loadKLineData(contractId, timeMap[curTime]
                ?: ContractWsKlineType.WEBSOCKET_BIN15M, forward, cleanCache, object : DataKLineHelper.KLineDataUpdateListener {
            /**
             * 分屏加载数据
             */
            override fun onLoadSplitData(id: Int, time: ContractWsKlineType, moreData: ArrayList<KLineData>?) {
                if (contractId == id && TextUtils.equals(time.name, timeMap[curTime]?.name)) {
                    if (moreData != null && moreData.isNotEmpty()) {
                        if (isDestroyed) {
                            return
                        }
                        val newList = buildKLineBeanList(moreData);
                        klineData.addAll(0, newList)
                        DataManager.calculate(newList)
                        mActivity.runOnUiThread {
                            adapter.addItems(0, newList)
                            v_kline?.refreshComplete()
                        }
                    }
                }
            }

            /**
             * 初始化K线数据
             */
            override fun onInitData(id: Int, time: ContractWsKlineType, data: ArrayList<KLineData>?) {
                closeLoadingDialog()
                if (contractId == id && TextUtils.equals(time.name, timeMap[curTime]?.name)) {
                    if (data != null && data.isNotEmpty()) {
                        if (isDestroyed) {
                            return
                        }
                        val newList = buildKLineBeanList(data);
                        initKLineData(newList)
                    }
                }
            }

            override fun onLoadFail(errno: String?, message: String?) {
                closeLoadingDialog()
                v_kline?.refreshComplete()
            }

        })
    }

    private fun initKLineData(data: List<KLineBean>?) {
        if (data != null) {
            klineData.clear()
            klineData.addAll(data)
        }
        DataManager.calculate(klineData)
        adapter.addFooterData(klineData)
        v_kline?.startAnimation()
        v_kline?.refreshComplete()
        if (v_kline?.minScrollX != null) {
            if (klineData.size < 30) {
                v_kline?.scrollX = 0
            } else {
                v_kline?.scrollX = v_kline!!.minScrollX
            }
        }
        timeMap[curTime]?.let {
            //订阅K线
            ContractPublicDataAgent.subscribeKlineWs(contractId, it)
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

                    (if (klineState == MarketDetail4Activity.KLINE_SCALE) tv_scale else tv_indicator)?.run {
                        labelBackgroundColor = ColorUtil.getColor(R.color.normal_icon_color)
                        textColor = ColorUtil.getColor(R.color.normal_text_color)
                    }

                } else {
                    isShow = true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}