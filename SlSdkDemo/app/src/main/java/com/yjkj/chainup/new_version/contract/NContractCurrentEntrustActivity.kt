package com.yjkj.chainup.new_version.contract

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NContractCurrentEntrustAdapter
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.DisplayUtil
import kotlinx.android.synthetic.main.activity_contract_current_entrust.*
import kotlinx.android.synthetic.main.activity_contract_history_entrust.iv_back
import kotlinx.android.synthetic.main.activity_contract_history_entrust.ly_appbar
import kotlinx.android.synthetic.main.activity_contract_history_entrust.tv_sub_title
import kotlinx.android.synthetic.main.activity_contract_history_entrust.tv_title
import org.json.JSONObject
import kotlin.math.abs

/**
 * @author Bertking
 * @date 2019-9-3
 * @description 当前委托(合约)
 */
@Route(path = "/contract/ContractCurrentEntrustActivity")
class NContractCurrentEntrustActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_contract_current_entrust

    @JvmField
    @Autowired(name = "contractId")
    var contractId = ""

    var page = 1
    var isScrollstatus = true
    /**
     * 活动委托的adapter
     */
    var adapter = NContractCurrentEntrustAdapter(arrayListOf())

    override fun initView() {
        tv_sub_title?.text = LanguageUtil.getString(mActivity,"contract_text_currentEntrust")
        tv_history_order?.text = LanguageUtil.getString(mActivity,"contract_text_historyCommision")

        ly_appbar?.addOnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) >= 140) {
                tv_title?.visibility = View.VISIBLE
                tv_sub_title?.visibility = View.GONE
            } else {
                tv_title?.visibility = View.GONE
                tv_sub_title?.visibility = View.VISIBLE
            }
        }

        tv_history_order?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("contractId", contractId)
            ArouterUtil.navigation("/contract/ContractHistoryEntrustActivity", bundle)
        }

        iv_back?.setOnClickListener {
            finish()
        }

        initRefresh()
        rv_cur_entrust_contract?.setHasFixedSize(true)
        rv_cur_entrust_contract?.layoutManager = LinearLayoutManager(mActivity)
        adapter.bindToRecyclerView(rv_cur_entrust_contract ?: return)
        rv_cur_entrust_contract?.adapter = adapter
        adapter.setEmptyView(EmptyForAdapterView(this))
        adapter.setOnItemChildClickListener { adapter, view, position ->
            if (adapter?.data?.isNotEmpty() == true) {
                try {
                    val item = adapter.data[position] as JSONObject
                    val status = item.optString("status")
                    val orderId = item.optString("orderId")
                    val contractId = item.optString("contractId")
                    when (status) {
                        "0", "1", "3" -> {
                            cancelOrder(orderId, contractId, position)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        getOrderList4Contract()
    }


    private fun initRefresh() {
        swipe_refresh?.setOnRefreshListener {
            page = 1
            isScrollstatus = true
            getOrderList4Contract()
        }

        rv_cur_entrust_contract?.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            var lastVisibleItem = 0
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var layoutManager: LinearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter?.itemCount && isScrollstatus) {
                    page += 1
                    getOrderList4Contract()
                }
            }

        })


    }


    /**
     * 取消订单
     */
    private fun cancelOrder(orderId: String, contractId: String, pos: Int) {
        addDisposable(getContractModel().cancelOrder4Contract(contractId = contractId, orderId = orderId,
                consumer = object : NDisposableObserver(this,true) {
                    override fun onResponseSuccess(data: JSONObject) {
                        DisplayUtil.showSnackBar(window?.decorView, LanguageUtil.getString(mActivity,"cancel_order_suc"))
                        adapter.remove(pos)
                        getOrderList4Contract()
                    }
                }))
    }


    /**
     * 活动委托列表
     */
    private fun getOrderList4Contract() {
        if (!LoginManager.checkLogin(this, false)) return
        addDisposable(getContractModel().getOrderList4Contract(contractId = contractId,
                consumer = object : NDisposableObserver(mActivity) {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        jsonObject.optJSONObject("data")?.run {
                            val jsonArray = optJSONArray("orderList") ?: return@run
                            val orderList = arrayListOf<JSONObject>()
                            if (jsonArray.length() != 0) {
                                for (i in 0 until jsonArray.length()) {
                                    orderList.add(jsonArray.optJSONObject(i))
                                }
                                adapter.replaceData(orderList)
                            } else {
                                adapter.replaceData(arrayListOf())
                            }
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        swipe_refresh?.isRefreshing = false
                        DisplayUtil.showSnackBar(window?.decorView, msg, isSuc = false)
                    }
                }))
    }

}
