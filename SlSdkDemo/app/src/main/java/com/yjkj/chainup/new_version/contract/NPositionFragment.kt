package com.yjkj.chainup.new_version.contract

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NPositionAdapter
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.fragment_position.*
import org.json.JSONObject

/**
 * @Author: Bertking
 * @Date：2019-09-12-16:03
 * @Description:合约的"仓位"
 */

class NPositionFragment : NBaseFragment() {
    override fun setContentView() = R.layout.fragment_position

    var adapter = NPositionAdapter(arrayListOf())
    var userPositionList: ArrayList<JSONObject> = arrayListOf()


    override fun onResume() {
        super.onResume()
        getPosition4Contract()
    }

    override fun initView() {
        onSelectClick()
        rv_position?.setHasFixedSize(true)
        rv_position?.layoutManager = LinearLayoutManager(context)
        adapter.bindToRecyclerView(rv_position)
        rv_position?.adapter = adapter
        adapter.setEmptyView(EmptyForAdapterView(context ?: return))

        adapter.setOnItemChildClickListener { adapter, view, position ->
            val jsonObject = adapter?.data?.get(position) as JSONObject
            val contractId = jsonObject.optString("contractId")
            val leverageLevel = jsonObject.optString("leverageLevel")
            val quoteSymbol = jsonObject.optString("quoteSymbol")
            val id = jsonObject.optString("id")

            when (view?.id) {
                R.id.btn_adjust_lever -> {
                    // 调整杠杆
                    if (Contract2PublicInfoManager.isPureHoldPosition()) {
                        AdjustLeverUtil(context
                                ?: return@setOnItemChildClickListener, contractId.toInt(), leverageLevel, object : AdjustLeverUtil.AdjustLeverListener {
                            override fun adjustSuccess(value: String) {
                                if (adapter.getViewByPosition(position, R.id.btn_adjust_deposit) != null) {
                                    (adapter.getViewByPosition(position, R.id.btn_adjust_deposit) as TextView).text = LanguageUtil.getString(context, "contract_action_editLever") + "(" + value + "x)"
                                }
                                Log.d(OnItemChildClickListener.TAG, "==当前杠杆==$value")
                                getPosition4Contract()
                            }

                            override fun adjustFailed(msg: String) {
                                //DisplayUtil.showSnackBar(activity?.window?.decorView, msg, false)
                                NToastUtil.showTopToast(false, msg)
                            }
                        })
                    } else {
                        // 限价平仓
                        NewDialogUtils.closePositionByLimit(context!!, quoteSymbol,
                                object : NewDialogUtils.DialogBottomAloneListener {
                                    override fun returnContent(content: String) {
                                        /**
                                         * 返回内容格式：price / volume
                                         */
                                        val split = content.split("/")
                                        takeOrder(jsonObject, price = split[0], vol = split[1], orderType = 1)
                                    }
                                })
                    }
                }

                // 市价平仓
                R.id.btn_take_order -> {
                    takeOrder(jsonObject, orderType = 2)
                }
                // 保证金
                R.id.tv_deposit -> {
                    NewDialogUtils.adjustDepositDialog(context!!, jsonObject,
                            object : NewDialogUtils.DialogBottomAloneListener {
                                override fun returnContent(content: String) {
                                    transferMargin4Contract(id, contractId, content)
                                }
                            })


                }
            }
        }
    }


    fun onSelectClick() {
        swipe_refresh?.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                getPosition4Contract()
            }
        })

    }


    /**
     * 用户持仓信息
     */
    private fun getPosition4Contract() {
//        if (!LoginManager.checkLogin(activity, false)) {
//            adapter.setNewData(arrayListOf())
//            return
//        }
        LogUtil.d(TAG, "getPosition4Contract1")
        addDisposable(getContractModel().getPosition4Contract(
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(jsonObject: JSONObject) {
                        swipe_refresh?.isRefreshing = false
                        jsonObject.optJSONObject("data").run {
                            userPositionList.clear()
                            val jsonArray = optJSONArray("positions")
                            if (jsonArray != null && jsonArray.length() != 0) {
                                for (i in 0 until jsonArray.length()) {
                                    userPositionList.add(jsonArray.optJSONObject(i))
                                }
                                val contractId = Contract2PublicInfoManager.currentContractId()
                                val filterIndexed = userPositionList.filterIndexed { index, position ->
                                    position.optString("contractId").toIntOrNull() ?: -1 == contractId
                                }
                                userPositionList.removeAll(filterIndexed)
                                userPositionList.addAll(0, filterIndexed)
                                adapter.setNewData(userPositionList)

                            }
                        }
                    }

                    override fun onResponseFailure(code: Int, msg: String?) {
                        super.onResponseFailure(code, msg)
                        swipe_refresh?.isRefreshing = false
                    }
                }))
    }

    /**
     *   限价平仓 orderType =1
     *   市价平仓 orderType =2
     */
    private fun takeOrder(jsonObject: JSONObject, price: String = "", vol: String = "", orderType: Int) {
        val side = jsonObject.optString("side")
        val volume = jsonObject.optString("volume")
        val indexPrice = jsonObject.optString("indexPrice")
        val contractId = jsonObject.optString("contractId")
        val leverageLevel = jsonObject.optString("leverageLevel")
        val id = jsonObject.optString("id")

        addDisposable(getContractModel().takeOrder4Contract(
                contractId = contractId.toString()
                , volume = if (TextUtils.isEmpty(price)) {
            volume.toString()
        } else {
            vol
        }, price = if (TextUtils.isEmpty(price)) {
            indexPrice.toString()
        } else {
            price
        },
                orderType = orderType,
                side = if (side == "BUY") "SELL" else "BUY",
                closeType = "1",
                level = leverageLevel.toString(),
                positionId = id.toString(),
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        NToastUtil.showTopToast(true, LanguageUtil.getString(context, "contract_tip_submitSuccess"))
                        getPosition4Contract()
                    }
                }))
    }


    /**
     * 追加保证金
     */
    private fun transferMargin4Contract(positionId: String, contractId: String, amount: String) {
        if (!LoginManager.checkLogin(context, false)) return
        addDisposable(getContractModel().transferMargin4Contract(
                positionId = positionId,
                contractId = contractId,
                amount = amount,
                consumer = object : NDisposableObserver() {
                    override fun onResponseSuccess(data: JSONObject) {
                        getPosition4Contract()
                        NToastUtil.showTopToast(true, LanguageUtil.getString(context, "contract_modify_the_success"))
                    }

                }))
    }
}

