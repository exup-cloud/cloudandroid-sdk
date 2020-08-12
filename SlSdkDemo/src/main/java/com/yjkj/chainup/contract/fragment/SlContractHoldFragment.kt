package com.yjkj.chainup.contract.fragment

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.*
import com.contract.sdk.impl.ContractPositionListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.timmy.tdialog.TDialog
import com.timmy.tdialog.listener.OnBindViewListener
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlAdjustMarginActivity
import com.yjkj.chainup.contract.activity.SlContractStopRateLossActivity
import com.yjkj.chainup.contract.adapter.HoldContractAdapter
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.contract.widget.MyLinearLayoutManager
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.contract.widget.bubble.BubbleSeekBar
import com.yjkj.chainup.contract.widget.pswkeyboard.widget.PopEnterPassword
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.dialog.NewDialogUtils.DialogBottomListener
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_sl_contract_hold.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.text.DecimalFormat
import kotlin.math.min

/**
 * 合约持仓
 */
class SlContractHoldFragment : NBaseFragment() {
    private val sTAG = "SlContractHoldFragment"
    private var adapter: HoldContractAdapter? = null
    private var mList = ArrayList<ContractPosition>()
    private var marginTDialog: TDialog? = null
    private var closePositionTDialog: TDialog? = null
    private var contract: Contract? = null
    private var coinCode: String = ""

    //是否是币种持仓
    private var isCoinHold = false

    override fun setContentView(): Int {
        return R.layout.fragment_sl_contract_hold
    }

    override fun initView() {
        adapter = HoldContractAdapter(mList)
        rv_hold_contract.layoutManager = MyLinearLayoutManager(context)
        rv_hold_contract.adapter = adapter
        adapter?.bindToRecyclerView(rv_hold_contract)
        adapter?.emptyView = EmptyForAdapterView(context ?: return)
        ContractUserDataAgent.registerContractPositionWsListener(this,object:ContractPositionListener(){
            /**
             * 合约仓位更新
             */
            override fun onWsContractPosition(instrumentId:Int?) {
                if(isVisible){
                    mList.clear()
                    if (isCoinHold) {
                        mList.addAll(ContractUserDataAgent.getCoinPositions(coinCode))
                    }else{
                        contract?.let {
                            mList.addAll(ContractUserDataAgent.getCoinPositions(it.instrument_id))
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }

        })
//        val contractJson = UtilSystem.readAssertResource(context, "contractHoldNow.json")
//        list.addAll(Gson().fromJson<List<ContractPosition>>(contractJson, object : TypeToken<List<ContractPosition?>?>() {}.type))
//        adapter?.notifyDataSetChanged()
        adapter?.bindListener(object : HoldContractAdapter.OnItemClickedListener {
            /**
             * 调整保证金
             */
            override fun doTransferMargin(info: ContractPosition) {
                mActivity?.let { SlAdjustMarginActivity.show(it, info) }
            }

            /**
             * 平仓
             */
            override fun doClosePosition(info: ContractPosition) {
                showClosePositionDialog(info)
            }

            /**
             * 止盈止损
             */
            override fun doStopRateLoss(info: ContractPosition) {
                mActivity?.let {
                    SlContractStopRateLossActivity.show(it, info)
                }

            }

        })

        loadDataFromNet()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (marginTDialog != null) {
            marginTDialog?.dismiss()
            marginTDialog = null
        }

        if (closePositionTDialog != null) {
            closePositionTDialog?.dismiss()
            closePositionTDialog = null
        }


    }

    fun getPositionCount(): Int {
        val list = ContractUserDataAgent.getCoinPositions(contract?.instrument_id?:0) ?: return 0
        var count = 0
        list.forEach {
            if (it.instrument_id == contract?.instrument_id) {
                count++
            }
        }
        return count
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadDataFromNet()
        }
    }

    fun updateContractTicker() {
        if (isHidden) {
            return
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     * 绑定合约
     */
    fun bindCoinCode(contract: Contract, resetData: Boolean = false) {
        this.contract = contract
        if (resetData) {
            mList.clear()
            adapter?.notifyDataSetChanged()
        }
        loadDataFromNet()
    }

    /**
     * 绑定coinCode
     */
    fun bindCoinCode(coinCode: String) {
        this.coinCode = coinCode
        isCoinHold = true
        loadDataFromNet()
    }


    private fun loadDataFromNet() {
        if (isHidden || !isAdded || !UserDataService.getInstance().isLogined) {
            mList.clear()
            adapter?.notifyDataSetChanged()
            return
        }


        val coin = if (isCoinHold) coinCode else contract?.margin_coin?:return

        ContractUserDataAgent.loadContractPosition(coin, 1, 0, 0, null)

    }


    /**
     * 弹出平仓对话框
     */
    private fun showClosePositionDialog(info: ContractPosition) {
        closePositionTDialog = SlDialogHelper.showClosePositionDialog(mActivity!!, OnBindViewListener {
            val contract: Contract = ContractPublicDataAgent.getContract(info.instrument_id)
                    ?: return@OnBindViewListener
            val etPrice = it.getView<EditText>(R.id.et_price)
            val etVolume = it.getView<EditText>(R.id.et_volume)
            val btnClosePositionMarket = it.getView<TextView>(R.id.btn_close_position_market)
            val btnClosePosition = it.getView<CommonlyUsedButton>(R.id.btn_close_position)

            it.getView<TextView>(R.id.tv_title).onLineText("sl_str_close")
            it.getView<TextView>(R.id.tv_cancel).onLineText("common_text_btnCancel")
            it.getView<TextView>(R.id.tv_price_label).onLineText("contract_text_price")
            it.getView<TextView>(R.id.tv_base_symbol).onLineText("sl_str_contracts_unit")
            etPrice.hint = getLineText("contract_text_price")
            it.getView<TextView>(R.id.tv_position_label).onLineText("contract_text_position")
            etVolume.hint = getLineText("contract_text_position")
            btnClosePositionMarket.onLineText("contract_action_marketClosing")
            btnClosePosition.textContent = getLineText("sl_str_close")

            //滑块
            val seekBar = it.getView<BubbleSeekBar>(R.id.seek_layout)
            //价格单位
            it.getView<TextView>(R.id.tv_price_unit).text = contract.quote_coin
            //价格 默认显示最新价格
            val ticker: ContractTicker? = ContractPublicDataAgent.getContractTicker(info.instrument_id)
            if (ticker != null) {
                etPrice.setText(ticker.last_px)
            }

            //数量 默认展示最大可平数
            val allQty = MathHelper.round(info.cur_qty, 0)
            val maxClosePosVol = MathHelper.round(MathHelper.sub(info.cur_qty, info.freeze_qty), 0)
            etVolume.setText("${allQty.toInt()}")
            seekBar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
                override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
                    val vol = (allQty * progress / 100).toInt().toString()
                    etVolume.setText(vol)
                    etVolume.setSelection(vol.length)
                }

                override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
                }

                override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
                }
            }
            etVolume.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var vol = etVolume.text.toString()
                    if (TextUtils.isEmpty(vol)) {
                        seekBar.setProgress(0.0f)
                    } else {
                        if (vol.toFloat() > allQty) {
                            vol = allQty.toInt().toString()
                            etVolume.setText(vol)
                            etVolume.setSelection(vol.length)
                        }
                        if (allQty > 0.0) {
                            seekBar.setProgress(min((vol.toFloat() / allQty * 100).toFloat(), 100.0f))
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            etPrice.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var price = etPrice.text.toString()
                    if (TextUtils.isEmpty(price)) {
                        return
                    }
                    val priceUnit = contract.px_unit
                    if (priceUnit.contains(".")) {
                        var priceIndex = priceUnit.length - priceUnit.indexOf(".") - 1
                        if (priceIndex == 1) {
                            priceIndex = 0
                        }

                        if (price.contains(".")) {
                            val index = price.indexOf(".")
                            if (index + priceIndex < price.length) {
                                price = price.substring(0, index + priceIndex)
                                etPrice.setText(price)
                            }
                        }
                    } else {
                        if (price.contains(".")) {
                            price = price.replace(".", "")
                            etPrice.setText(price)
                        }
                    }
                    if (price == ".") {
                        price = "0"
                        etPrice.setText(price)
                    }
                    etPrice.setSelection(price.length)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            //平仓
            btnClosePosition.isEnable(true)
            btnClosePosition.listener = object : CommonlyUsedButton.OnBottonListener {
                override fun bottonOnClick() {
                    val vol = etVolume.text.toString()
                    val price = etPrice.text.toString()
                    if (preVerifyClosePositionInput(vol, price)) {
                        closePositionTDialog?.dismiss()
                        closePositionTDialog = null
                        //弹出确认对话框 限价7300 USDT买入100 张 BTCUSDT合约
                        val type = if (info.side === 1) getLineText("sl_str_sell_close") else getLineText("sl_str_buy_close")
                        val tips = getLineText("sl_str_limit_price_tips")
                        val content = "$tips<font color=\"#FF9E12\"> $price </font> ${contract.margin_coin}$type<font color=\"#FF9E12\">$vol</font> ${ContractUtils.getHoldVolUnit(contract)} ${contract.getDisplayName(mActivity)}"
                        NewDialogUtils.showNormalDialog(mActivity!!, content, object : DialogBottomListener {
                            override fun sendConfirm() {
                                //如果平仓量 大于可平仓量，则取消全部订单 在执行平仓
                                if (vol.toInt() > maxClosePosVol) {
                                    val orderList = ContractUserDataAgent.getContractOrder(info)
                                    if (orderList != null && orderList.isNotEmpty()) {
                                        val orders = ContractOrders()
                                        orders.contract_id = info.instrument_id
                                        orders.orders = orderList
                                        ContractUserDataAgent.doCancelOrders(orders,object: IResponse<MutableList<Long>>(){
                                            override fun onSuccess(data: MutableList<Long>) {
                                                doClosePositionRequest(btnClosePosition, info, vol, price, "")
                                            }

                                            override fun onFail(code: String, msg: String) {
                                                btnClosePosition.hideLoading()
                                                closeLoadingDialog()
                                                super.onFail(code, msg)
                                            }

                                        })
                                        return
                                    }
                                }
                                doClosePositionRequest(btnClosePosition, info, vol, price, "")
                            }
                        }, getLineText("contract_text_limitPositions"), getLineText("contract_text_limitPositions"))

                    }
                }
            }

            //市价全平
            btnClosePositionMarket.setOnClickListener {
                closePositionTDialog?.dismiss()
                closePositionTDialog = null

                val vol = etVolume.text.toString()
                val price = etPrice.text.toString()
                if (!preVerifyClosePositionInput(vol, price)) {
                    return@setOnClickListener
                }

                //弹出确认对话框   市价买入4000张 BTCUSDT合约
                val type = if (info.side === 1) getLineText("sl_str_sell_close") else getLineText("sl_str_buy_close")
                val tips = getLineText("contract_action_marketPrice")

//                val amountCanbeLiquidated: Double = MathHelper.sub(info.cur_qty, info.freeze_qty)
                val dfVol: DecimalFormat = NumberUtil.getDecimal(contract.vol_index)
                val content = "$tips$type<font color=\"#FF9E12\"> ${vol} </font>  ${ContractUtils.getHoldVolUnit(contract)} ${contract.getDisplayName(mActivity)}"

                NewDialogUtils.showNormalDialog(mActivity!!, content, object : DialogBottomListener {
                    override fun sendConfirm() {
                        //如果平仓量 大于可平仓量，则取消全部订单 在执行平仓
                        if (vol.toInt() > maxClosePosVol) {
                            //市价全平前，需要先撤销该仓位的未成交状态的平仓委托单，
                            val orderList = ContractUserDataAgent.getContractOrder(info)
                            if (orderList != null && orderList.size > 0) {
                                //全部撤单
                                doCancelAllEntrustOrders(btnClosePositionMarket, vol, info, orderList, "")
                                return
                            }
                        }
                        realClosePositionByMarketPrice(btnClosePositionMarket, vol, info, "")
                    }
                }, getLineText("contract_action_marketClosing"), getLineText("contract_action_marketClosing"), "")
//
//                    if (orderList != null && orderList.size > 0) {
////                        //弹出撤单弹窗
////                        SlDialogHelper.showCancelOpenOrdersDialog(mActivity!!, object : DialogBottomListener {
////                            override fun sendConfirm() {
////                                ///全部撤单
////                                doCancelAllEntrustOrders(btnClosePositionMarket, info, orderList, "")
////                            }
////                        }, info, orderList)
//                        //直接撤单
//                        doCancelAllEntrustOrders(btnClosePositionMarket, info, orderList, "")
//                    }else{
//
//                    }
            }
        })
    }

    /**
     * 取消全部委托订单
     */
    private fun doCancelAllEntrustOrders(view: View, vol: String, info: ContractPosition, orderList: List<ContractOrder>?, pwd: String) {
        val orders = ContractOrders()
        orders.contract_id = info.instrument_id
        orders.orders = orderList
        val response: IResponse<MutableList<Long>> = object : IResponse<MutableList<Long>>(){
            override fun onSuccess(data: MutableList<Long>) {
                closeLoadingDialog()
                if (data != null && data.isNotEmpty()) {
                    ToastUtils.showToast(ContractSDKAgent.context, getLineText("sl_str_some_orders_cancel_failed"))
                } else {
                    //真正的执行市价全平
                    val newPosition = ContractUserDataAgent.getContractPosition(info.instrument_id, info.side)
                    if (newPosition != null) {
                        realClosePositionByMarketPrice(view, vol, newPosition, "")
                    }
                    //撤单成功，可执行市价全平
//                NewDialogUtils.showNormalDialog(context!!, getLineText("sl_str_cancel_and_market_price"),
//                        object : DialogBottomListener {
//                            override fun sendConfirm() {
//                                //真正的执行市价全平
//                                val newPosition = BTContract.instance?.getContractPosition(info.instrument_id, info.side)
//                                if (newPosition != null) {
//                                    realClosePositionByMarketPrice(view, newPosition, "")
//                                }
//                            }
//                        },
//                        getLineText("contract_text_marketPriceFlat"), getLineText("contract_text_marketPriceFlat"), getLineText("common_text_btnCancel"))
                }
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
                ToastUtils.showToast(ContractSDKAgent.context, msg)
            }
        }


        showLoadingDialog(getLineText("sl_str_cancelling_orders"))
        ContractUserDataAgent.doCancelOrders(orders, response)
    }

    /**
     * 真正的执行市价全平
     */
    private fun realClosePositionByMarketPrice(view: View, vol: String, position: ContractPosition?, pwd: String) {
        showLoadingDialog(getLineText("sl_str_load_text"))
        if (position == null || view == null) {
            return
        }
        val contract = ContractPublicDataAgent.getContract(position.instrument_id) ?: return
        val dfVol = NumberUtil.getDecimal(contract.vol_index)
        val order = ContractOrder()
        order.instrument_id = position.instrument_id
        order.nonce = System.currentTimeMillis()
        order.qty = vol
        if (position.side === 1) {
            order.pid = position.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        } else {
            order.pid = position.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }
        order.category = ContractOrder.ORDER_CATEGORY_MARKET

        val response : IResponse<String> = object :IResponse<String>(){
            override fun onSuccess(data: String) {
                closeLoadingDialog()
                ToastUtils.showToast(ContractSDKAgent.context, getLineText("contract_tip_closeOrderSuccess"))
            }

            override fun onFail(code: String, msg: String) {
                closeLoadingDialog()
                ToastUtils.showToast(ContractSDKAgent.context, msg)
            }

        }

        ContractUserDataAgent.doSubmitOrder(order,response)
    }


    /**
     * 提交平仓
     */
    private fun doClosePositionRequest(btnClosePosition: CommonlyUsedButton, info: ContractPosition, vol: String, price: String, password: String? = "") {
        btnClosePosition.showLoading()
        val order = ContractOrder()
        order.instrument_id = info.instrument_id
        order.nonce = System.currentTimeMillis()
        order.qty = vol
        if (info.side === 1) {
            order.pid = info.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        } else {
            order.pid = info.pid
            order.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }

        order.px = price
        order.category = ContractOrder.ORDER_CATEGORY_NORMAL


        val response : IResponse<String> = object :IResponse<String>(){
            override fun onSuccess(data: String) {
                btnClosePosition.hideLoading()
                closeLoadingDialog()
                ToastUtils.showToast(ContractSDKAgent.context, getLineText("contract_tip_closeOrderSuccess"))
            }

            override fun onFail(code: String, msg: String) {
                btnClosePosition.hideLoading()
                closeLoadingDialog()
                ToastUtils.showToast(ContractSDKAgent.context, msg)
            }

        }

        ContractUserDataAgent.doSubmitOrder(order,response)

    }

    /**
     * 校验平仓输入
     */
    private fun preVerifyClosePositionInput(vol: String, price: String): Boolean {
        if (TextUtils.isEmpty(vol) || vol.toInt() == 0) {
            ToastUtils.showToast(getLineText("sl_str_volume_too_low"))
            return false
        }
        if (TextUtils.isEmpty(price) || vol.toInt() == 0) {
            ToastUtils.showToast(getLineText("sl_str_price_too_low"))
            return false
        }
        return true
    }



}