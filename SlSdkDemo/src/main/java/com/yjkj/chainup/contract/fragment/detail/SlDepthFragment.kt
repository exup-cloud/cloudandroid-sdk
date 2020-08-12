package com.yjkj.chainup.contract.fragment.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.DepthData
import com.contract.sdk.extra.dispense.DataDepthHelper
import com.contract.sdk.impl.ContractDepthListener
import com.contract.sdk.utils.MathHelper
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.adapter.ContractDeepAdapter
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.view.depth.DepthMarkView
import com.yjkj.chainup.new_version.view.depth.DepthYValueFormatter
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.DecimalUtil
import kotlinx.android.synthetic.main.sl_fragment_depth.*
import java.lang.Exception
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * 合约深度
 */
class SlDepthFragment : NBaseFragment(){
    override fun setContentView(): Int {
        return R.layout.sl_fragment_depth
    }

    private var contractId : Int? = 0

    private var riseColor = ColorUtil.getMainColorType(isRise = true)
    private var fallColor = ColorUtil.getMainColorType(isRise = false)

    private val riseMinorColor = ColorUtil.getMinorColorType(isRise = true)
    private val fallMinorColor = ColorUtil.getMinorColorType(isRise = false)

    private var isLoading = false

    private val buyList =  ArrayList<DepthData>()
    private val sellList =  ArrayList<DepthData>()

    private var adapter: ContractDeepAdapter?=null

    override fun loadData() {
        super.loadData()
        contractId = arguments?.getInt("contractId",1)
        ContractPublicDataAgent.registerDepthWsListener(this,20,object:ContractDepthListener(){

            override fun onWsContractDepth(contractId: Int, buyList: ArrayList<DepthData>, sellList: ArrayList<DepthData>) {
                if(this@SlDepthFragment.contractId == contractId && mIsVisibleToUser){
                    if(buyList.isNotEmpty()){
                        this@SlDepthFragment.buyList.clear()
                        this@SlDepthFragment.buyList.addAll(buyList)
                        updateDeepUi()
                    }
                    if(sellList.isNotEmpty()){
                        this@SlDepthFragment.sellList.clear()
                        this@SlDepthFragment.sellList.addAll(sellList)
                        updateDeepUi()
                    }
                }
            }

        })
    }


    override fun initView() {
        intiAutoTextView()
        initDepthView()
        initDepthChart()
        adapter = ContractDeepAdapter(mActivity!!)
        recycler_deep.layoutManager = LinearLayoutManager(mActivity)
        recycler_deep.adapter = adapter

        val contract: Contract? = ContractPublicDataAgent.getContract(contractId!!)
        contract?.let {
            val unit: Int = LogicContractSetting.getContractUint(mContext)
            tv_buy_volume_title.text =getLineText("charge_text_volume") + "(" + (if (unit == 0)getLineText("sl_str_contracts_unit") else it.base_coin) + ")"
            tv_sell_volume_title.text = getLineText("charge_text_volume") + "(" + (if (unit == 0) getLineText("sl_str_contracts_unit") else it.base_coin) + ")"
            tv_price_title.text = getLineText("contract_text_price") + "(" + contract.quote_coin+ ")"
        }
        mIsVisibleToUser = true
        updateDeepUi()
    }

    private fun intiAutoTextView() {
        tv_buy_tape_title.onLineText("contract_text_buyMarket")
        tv_sell_tape_title.onLineText("contract_text_sellMarket")
    }

    fun switchContract(contractId : Int = 0){
        clearDepthChart()
        this.contractId = contractId
        //订阅深度
        ContractPublicDataAgent.subscribeDepthWs(contractId)

        updateDeepUi()
    }

    /**
     * 配置深度图基本属性
     */
    @SuppressLint("NewApi")
    private fun initDepthChart() {
        depth_chart?.setNoDataText(getString(R.string.common_tip_nodata))
        depth_chart?.setNoDataTextColor(resources.getColor(R.color.normal_text_color))
        depth_chart?.setTouchEnabled(true)
        /**
         * 图例 的相关设置
         */
        val legend = depth_chart.legend
        legend.isEnabled = false
        /**
         * 是否缩放
         */
        depth_chart?.setScaleEnabled(false)
        /**
         * X,Y同时缩放
         */
        depth_chart?.setPinchZoom(false)

        /**
         * 关闭图表的描述信息
         */
        depth_chart?.description?.isEnabled = false


        // 打开触摸手势
        depth_chart?.setTouchEnabled(true)
        depth_chart.isLongClickable = true
        depth_chart.isNestedScrollingEnabled = false

        /**
         * X
         */
        val xAxis: XAxis = depth_chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        //xAxis.setLabelCount(3, true)
        // 不绘制竖直方向的方格线
        xAxis.setDrawGridLines(false)
        //x坐标轴不可见
        xAxis.isEnabled = true
        //禁止x轴底部标签
        xAxis.setDrawLabels(true)
        //最小的间隔设置
        xAxis.textColor = ColorUtil.getColor(R.color.normal_text_color)
        xAxis.textSize = 10f
        //在绘制时会避免“剪掉”在x轴上的图表或屏幕边缘的第一个和最后一个坐标轴标签项。
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.setDrawAxisLine(false)
        /**
         * Y
         */
        depth_chart.axisRight.isEnabled = true
        depth_chart.axisLeft.isEnabled = false
        /**********左边Y轴********/
        depth_chart.axisLeft.axisMinimum = 0f
        /**********右边Y轴********/
        val yAxis = depth_chart.axisRight
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        //设置Y轴的Label显示在图表的内侧还是外侧，默认外侧
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        //不绘制水平方向的方格线
        yAxis.textColor = ColorUtil.getColor(R.color.normal_text_color)
        yAxis.textSize = 10f
        //设置Y轴显示的label个数
        yAxis.setLabelCount(6, true)
        //控制上下左右坐标轴显示的距离
        depth_chart?.setViewPortOffsets(0f, 15f, 0f, 6f)
        yAxis.valueFormatter = DepthYValueFormatter()


        depth_chart?.setOnClickListener {
            if (depth_chart.marker != null) {
                depth_chart.marker = null
            }
        }

        depth_chart?.setOnLongClickListener {
            val mv = DepthMarkView(mActivity!!, R.layout.layout_depth_marker)
            mv.chartView = depth_chart // For bounds control
            depth_chart?.marker = mv // Set the marker to the ch
            false
        }

    }

    /**
     * 初始化默认数据
     */
    private fun initDepthView() {
    }
    /**
     * 设置lineDataSet  in深度图
     */
    private fun lineDataSet(yData: ArrayList<Entry>, isBuy: Boolean): LineDataSet {
        val lineDataSet: LineDataSet?
        if (isBuy) {
            lineDataSet = LineDataSet(yData, "")
            lineDataSet.color = ColorUtil.getMainColorType()
            lineDataSet.fillColor = ColorUtil.getMainColorType()
            /**
             * 设置折线的颜色
             */
            lineDataSet.color = ColorUtil.getMainColorType()

        } else {
            lineDataSet = LineDataSet(yData, "")
            lineDataSet.color = ColorUtil.getMainColorType(isRise = false)
            lineDataSet.fillColor = ColorUtil.getMainColorType(isRise = false)
            /**
             * 设置折线的颜色
             */
            lineDataSet.color = ColorUtil.getMainColorType(isRise = false)
        }
        /**
         * 是否填充折线以及填充色设置
         */
        lineDataSet.setDrawFilled(true)

        /**
         * 控制MarkView的显示与隐藏
         * 点击是否显示高亮线
         */
        lineDataSet.isHighlightEnabled = true
        lineDataSet.highLightColor = Color.TRANSPARENT


        /**
         * 设置折线的宽度
         */
        lineDataSet.lineWidth = 2.0f


        /**
         * 隐藏每个数据点的值
         */
        lineDataSet.setDrawValues(false)

        /**
         * 数据点是否用小圆圈表示
         */
        lineDataSet.setDrawCircles(false)
        return lineDataSet
    }


    /**
     * 初始化深度图
     */
    private fun initDepthChartView() {
        if(depth_chart == null){
            return
        }
        try {

            val sells = ArrayList<DepthData>()
            val buys = ArrayList<DepthData>()
            var minCount = min(buyList.size, sellList.size)
            //取从小到大前20
            sells.addAll(sellList.subList(0, min(20, minCount)))
            //取从大到小前20
            buys.addAll(buyList.subList(0, min(20, minCount)))
            minCount = sells.size
            //买
            val valuesBuy = ArrayList<Entry>()
            var maxBuyVol = 0.0
            var maxBuyPrice = MathHelper.round(buys[0].price, 6)
            var minBuyPrice = MathHelper.round(buys.last().price, 6)

            //卖
            val valuesSell = ArrayList<Entry>()
            var maxSellVol = 0.0
            var maxSellPrice =  MathHelper.round(sells.last().price, 6)
            var minSellPrice =  MathHelper.round(sells[0].price, 6)


            var closePrice = (maxBuyPrice + minSellPrice) / 2
            for (i in 0 until minCount) {
                    var info = buys[i]
                    maxBuyVol += info.vol.toDouble()
                    valuesBuy.add(0,Entry(((minCount-i)).toFloat(), maxBuyVol.toFloat(), info.price))
            }

            for (i in 0 until minCount) {
                    var info = sells[i]
                    val vol = MathHelper.round(info.vol.toString(), 6)
                    val price = MathHelper.round(info.price, 6)
                    maxSellVol += info.vol.toDouble()
                    valuesSell.add(Entry(((i+minCount)).toFloat(), maxSellVol.toFloat(), price))
            }


            val maxVol = max(maxBuyVol, maxSellVol)

            val yAxis = depth_chart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = maxVol.toFloat() * 1.1f

            val buyLineDataSet = lineDataSet(valuesBuy, true)
            val sellLineDataSet = lineDataSet(valuesSell, false)
            //        LineData表示一个LineChart的所有数据(即一个LineChart中所有折线的数据)
            val lineData = LineData(buyLineDataSet, sellLineDataSet)

            depth_chart?.data = lineData
            depth_chart?.invalidate()

            tv_buy_price?.text = minBuyPrice.toString()
            tv_sell_price?.text = maxSellPrice.toString()
            tv_close_price?.text = DecimalUtil.cutValueByPrecision("$closePrice", 2)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清理深度
     */
    fun clearDepthChart() {
        depth_chart?.clear()
        depth_chart?.notifyDataSetChanged()
        depth_chart?.invalidate()

        tv_buy_price.text = "0.00"
        tv_sell_price.text = "0.00"
        tv_close_price.text = "0.00"
    }


    private fun updateDeepUi() {
        if (buyList.size >= 2 && sellList.size >= 2) {
            initDepthChartView()
        }
        if(buyList.isEmpty() && sellList.isEmpty()){
            DataDepthHelper.instance?.getDepthSource(count = 20) { buyList, sellList ->
                if(buyList.isNotEmpty()){
                    this@SlDepthFragment.buyList.clear()
                    this@SlDepthFragment.buyList.addAll(buyList)
                    updateDeepUi()
                }
                if(sellList.isNotEmpty()){
                    this@SlDepthFragment.sellList.clear()
                    this@SlDepthFragment.sellList.addAll(buyList)
                    updateDeepUi()
                }
            }
        }
        adapter?.setData(sellList,buyList, contractId!!)
        adapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(contractId: Int) =
                SlDepthFragment().apply {
                    arguments = Bundle().apply {
                        putInt("contractId",contractId)
                    }
                }
    }


}