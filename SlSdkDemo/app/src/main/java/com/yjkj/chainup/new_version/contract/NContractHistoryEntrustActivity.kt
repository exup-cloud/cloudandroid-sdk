package com.yjkj.chainup.new_version.contract

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NContractHistoryEntrustAdapter
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.new_version.view.ScreeningPopupWindowView
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.util.DisplayUtil
import kotlinx.android.synthetic.main.activity_contract_history_entrust.*
import org.json.JSONObject
import kotlin.math.abs

/**
 * @author Bertking
 * @date 2019-9-3
 * @description 历史委托(合约)
 */
@Route(path = "/contract/ContractHistoryEntrustActivity")
class NContractHistoryEntrustActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_contract_history_entrust

    @JvmField
    @Autowired(name = "contractId")
    var contractId = ""
    var contractBean: ContractBean? = null
    var isShowCanceled = "1"
    var side: String = ""
    var typeForSelect: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var symbol: String = ""
    var contractType: String = ""
    var positionsMainType: String = ""
    var adapter: NContractHistoryEntrustAdapter? = null
    var orderBeanList: ArrayList<JSONObject> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }


    override fun initView() {

        tv_sub_title?.text = LanguageUtil.getString(mActivity,"contract_text_historyCommision")

        contractBean = Contract2PublicInfoManager.getContractByContractId(contractId.toInt())
        symbol = contractBean?.baseSymbol + contractBean?.quoteSymbol
        contractType = contractBean?.contractType.toString()
        iv_back?.setOnClickListener {
            finish()
        }

        setSupportActionBar(anim_toolbar)
        ly_appbar?.addOnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) >= 140) {
                tv_title?.visibility = View.VISIBLE
                tv_sub_title?.visibility = View.GONE
            } else {
                tv_title?.visibility = View.GONE
                tv_sub_title?.visibility = View.VISIBLE
            }
        }
        right_icon?.setOnClickListener {
            if (spw_layout?.visibility == View.VISIBLE) {
                spw_layout?.visibility = View.GONE
            } else {
                spw_layout?.visibility = View.VISIBLE
                if (isfrist) {
                    isfrist = false
                    spw_layout?.setMage()
                }
            }
        }
        spw_layout.contractScreeningListener = object : ScreeningPopupWindowView.ContractScreeningListener {
            override fun confirmContractScreening(status: Boolean, series: String, contractTimer: Int, tradingType: Int, priceType: Int, begin: String, end: String, positionsType: Int) {
                var trading = ""
                var priceTypeString = ""
                when (tradingType) {
                    1 -> {
                        trading = "BUY"
                    }
                    2 -> {
                        trading = "SELL"
                    }
                }
                when (priceType) {
                    1 -> {
                        priceTypeString = "1"
                    }
                    2 -> {
                        priceTypeString = "2"
                    }
                }
                isShowCanceled = if (status) "0" else "1"
                side = trading
                typeForSelect = priceTypeString
                startTime = begin
                endTime = end
                positionsMainType = when (positionsType) {
                    1 -> "OPEN"
                    2 -> "CLOSE"
                    else -> ""
                }

                contractType = contractTimer.toString()
                if (series.isNotEmpty()) {
                    symbol = series
                }

                getHistoryEntrust4Contract()
            }
        }

        initAdapterView()
    }


    fun initAdapterView() {
        adapter = NContractHistoryEntrustAdapter(orderBeanList)
        rv_history_entrust?.setHasFixedSize(true)
        rv_history_entrust?.layoutManager = LinearLayoutManager(mActivity)
        adapter?.bindToRecyclerView(rv_history_entrust ?: return)
        rv_history_entrust?.adapter = adapter
        adapter?.setEmptyView(EmptyForAdapterView(this))
    }

    override fun onResume() {
        super.onResume()
        getHistoryEntrust4Contract()
    }

    var isfrist = true
    /**
     * 获取历史委托(合约)
     * @param symbol 合约系列
     * @param contractType 合约类型（0 永续合约、1 周合约、2 次周合约、3 月合约、4 季度合约）
     * @param pageSize default 5
     * @param page  default 1
     * @param side 委托方向，BUY买入 SELL卖出 ,不传全部
     * @param isShowCanceled 是否展示已取消的订单，0表示不展示，1表示展示，默认1
     * @param startTime 年月日，禁止输入时分秒：2019-04-22
     * @param endTime 年月日，禁止输入时分秒：2019-04-22
     */
    private fun getHistoryEntrust4Contract() {
        if (!LoginManager.checkLogin(this, false)) return

        if (Contract2PublicInfoManager.isPureHoldPosition()) {
            positionsMainType = ""
        } else {
            // 分仓模式 CLOSE平  OPEN开
            positionsMainType
        }

        addDisposable(getContractModel().getHistoryEntrust4Contract(symbol = symbol,
                contractType = contractType,
                pageSize = "1000",
                page = "1",
                isShowCanceled = isShowCanceled,
                side = side,
                orderType = typeForSelect,
                startTime = startTime,
                endTime = endTime,
                action = positionsMainType,
                consumer = object : NDisposableObserver(mActivity) {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        jsonObject.optJSONObject("data")?.run {
                            val jsonArray = optJSONArray("orderList") ?: return
                            val orderList = arrayListOf<JSONObject>()
                            if (jsonArray.length() != 0) {
                                for (i in 0 until jsonArray.length()) {
                                    orderList.add(jsonArray.optJSONObject(i))
                                }
                                adapter?.replaceData(orderList)
                            } else {
                                orderBeanList.clear()
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        DisplayUtil.showSnackBar(window?.decorView, msg, isSuc = false)
                        orderBeanList.clear()
                        adapter?.notifyDataSetChanged()
                    }
                }))
    }


}

