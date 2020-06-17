package com.bmtc.sdk.contract.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmtc.sdk.contract.ContractSettingActivity
import com.bmtc.sdk.contract.R
import com.bmtc.sdk.contract.SelectLeverageActivity
import com.bmtc.sdk.contract.adapter.BuySellContractAdapter
import com.bmtc.sdk.contract.adapter.BuySellContractAdapter.OnBuySellContractClickedListener
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.base.BaseFragmentPagerAdapter
import com.bmtc.sdk.contract.dialog.*
import com.bmtc.sdk.contract.uiLogic.LogicBuySell
import com.bmtc.sdk.contract.uiLogic.LogicBuySell.IBuySellListener
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting.IContractSettingListener
import com.bmtc.sdk.contract.utils.*
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.*
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateAdvanceOpenCost
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateContractValue
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateMarketAvgPrice
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateOrderLiquidatePrice
import com.contract.sdk.extra.Contract.ContractCalculate.CalculateVolume
import com.contract.sdk.extra.Contract.ContractCalculate.trans2ContractVol
import com.contract.sdk.impl.ContractAccountListener
import com.contract.sdk.impl.ContractUserStatusListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import kotlinx.android.synthetic.main.sl_fragment_buysell_contract.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * Created by zj on 2018/3/1.
 */
class BuySellContractFragment : BaseFragment(), OnBuySellContractClickedListener, IBuySellListener, IContractSettingListener {

    private var mOperationType = 1 //1open; 2close

    private var mLeverage = 10
    private var mSellAdapter: BuySellContractAdapter? = null
    private var mBuyAdapter: BuySellContractAdapter? = null
    private var mABShowNum = 6
    private var mContractId = 1
    private var mContract : Contract? = null
    private var mCurrentPrice: String? = null
    private var mIndexPrice: String? = null
    private var mTagPrice: String? = null
    private var mFragments = ArrayList<BaseFragment>()
    private var mContractOpenOrderFragment: ContractOpenOrdersFragment? = null
    private var mContractEntrustHistoryFragment: ContractEntrustHistoryFragment? = null
    private var mContractPlanOrderFragment: ContractPlanOrderFragment? = null
    private var mHoldContractNowFragment: HoldContractNowFragment? = null
    private val mLock = Any()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.sl_fragment_buysell_contract, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogicBuySell.getInstance().registListener(this)
        LogicContractSetting.getInstance().registListener(this)
        setView()
        /**
         * 监听用户状态
         */
        ContractSDKAgent.registerSDKUserStatusListener(this,object : ContractUserStatusListener(){
            /**
             * 合约SDK登录成功
             */
            override fun onContractLoginSuccess() {
                onLogin()
            }

            /**
             * 合约SDK退出登录
             */
            override fun onContractExitLogin() {
                onLogout()
            }

        })
        /**
         * 监听资产变更
         * 订单改变 肯定对应资产改变，所以此处没监听订单改变
         */
        ContractUserDataAgent.registerContractAccountWsListener(this,object :ContractAccountListener(){
            /**
             * 合约账户ws有更新，
             */
            override fun onWsContractAccount(contractAccount: ContractAccount?) {
                updateUserAsset()
                updateInfoValue(true)
            }

        })
    }


    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (editable === et_trigger_price!!.editableText) {
                updateTriggerPrice()
            }
            if (editable === et_price!!.editableText) {
                updatePrice()
            }
            if (editable === et_volume!!.editableText) {
                updateVol()
            }
        }
    }

    private fun setView() {
        if (mContractOpenOrderFragment == null) {
            mContractOpenOrderFragment = ContractOpenOrdersFragment()
            mContractOpenOrderFragment!!.setType(1)
            mContractOpenOrderFragment!!.setContractId(0)
        }
        if (mContractEntrustHistoryFragment == null) {
            mContractEntrustHistoryFragment = ContractEntrustHistoryFragment()
            mContractEntrustHistoryFragment!!.setType(1)
            mContractEntrustHistoryFragment!!.setContractId(0)
        }
        if (mContractPlanOrderFragment == null) {
            mContractPlanOrderFragment = ContractPlanOrderFragment()
            mContractPlanOrderFragment!!.setType(1)
            mContractPlanOrderFragment!!.setContractId(0)
        }
        if (mHoldContractNowFragment == null) {
            mHoldContractNowFragment = HoldContractNowFragment()
            mHoldContractNowFragment!!.setType(1)
            mHoldContractNowFragment!!.setContractId(0)
        }
        mFragments = ArrayList()
        mFragments.add(mContractOpenOrderFragment!!)
        mFragments.add(mContractEntrustHistoryFragment!!)
        mFragments.add(mContractPlanOrderFragment!!)
        mFragments.add(mHoldContractNowFragment!!)
        val titles = arrayOf(
                getString(R.string.sl_str_open_orders),
                getString(R.string.sl_str_order_history),
                getString(R.string.sl_str_plan_entrust),
                getString(R.string.sl_str_holdings_now))
        val adapter: BaseFragmentPagerAdapter<*> = BaseFragmentPagerAdapter(childFragmentManager, mFragments, titles)
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 4
        tabs.setupWithViewPager(viewpager)
        appbar.post(Runnable {
            val layoutParams = appbar.getLayoutParams() as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as AppBarLayout.Behavior?
            behavior!!.setDragCallback(object : DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return true
                }
            })
        })
        tab_market_price.setOnClickListener(View.OnClickListener {
            if (orderType == LogicBuySell.ORDER_TYPE_MARKET) {
                setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true)
            } else {
                setOrderType(LogicBuySell.ORDER_TYPE_MARKET, true)
            }
        })
        tab_buy1.setOnClickListener(View.OnClickListener {
            if (orderType == LogicBuySell.ORDER_BID_PRICE) {
                setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true)
            } else {
                setOrderType(LogicBuySell.ORDER_BID_PRICE, true)
            }
        })
        tab_sell1.setOnClickListener(View.OnClickListener {
            if (orderType == LogicBuySell.ORDER_ASK_PRICE) {
                setOrderType(LogicBuySell.ORDER_TYPE_LIMIT, true)
            } else {
                setOrderType(LogicBuySell.ORDER_ASK_PRICE, true)
            }
        })
        tv_order_type.setOnClickListener { LogicBuySell.getInstance().showOrderTypeWindow(activity, tv_order_type, false, true) }
        iv_sel_order_type.setOnClickListener { LogicBuySell.getInstance().showOrderTypeWindow(activity, tv_order_type, false, true) }
        iv_setting.setOnClickListener(View.OnClickListener {
            val window = PromptWindow(activity)
            window.showTitle(getString(R.string.sl_str_plan_entrust))
            window.showTvContent(getString(R.string.sl_str_plan_entrust_intro))
            window.showBtnOk(getString(R.string.sl_str_go_setting))
            window.showBtnClose("")
            window.showBtnCancel(getString(R.string.sl_str_cancel))
            window.showAtLocation(iv_funds_rate_value, Gravity.CENTER, 0, 0)
            window.btnOk.setOnClickListener {
                window.dismiss()
                val intent = Intent(activity, ContractSettingActivity::class.java)
                startActivity(intent)
            }
            window.btnCancel.setOnClickListener { window.dismiss() }
            window.btnClose.setOnClickListener { window.dismiss() }
        })
        et_trigger_price.addTextChangedListener(mTextWatcher)
        et_price.addTextChangedListener(mTextWatcher)
        tv_price2market.setOnClickListener(View.OnClickListener {
            LogicContractSetting.setExecution(context, 1)
            setOrderType(LogicBuySell.ORDER_TYPE_PLAN, false)
        })
        tv_price2limit.setOnClickListener(View.OnClickListener {
            LogicContractSetting.setExecution(context, 0)
            setOrderType(LogicBuySell.ORDER_TYPE_PLAN, false)
        })
        et_volume.tag = 0
        et_volume.addTextChangedListener(mTextWatcher)
        rl_leverage.setOnClickListener(View.OnClickListener { selectLeverage() })
        tv_funds_transfer.setOnClickListener(View.OnClickListener { gotoTransfer() })
        iv_funds_transfer.setOnClickListener(View.OnClickListener { gotoTransfer() })
        btn_buy.setOnClickListener(View.OnClickListener {
            if (!NoDoubleClickUtils.isDoubleClick()) {
                doBuy()
            }
        })
        btn_sell.setOnClickListener(View.OnClickListener {
            if (!NoDoubleClickUtils.isDoubleClick()) {
                doSell()
            }
        })
        if (ContractSDKAgent.isLogin) {
            btn_buy.setText(if (mOperationType == 1) getString(R.string.sl_str_buy_open_long) else getString(R.string.sl_str_buy_close_short))
            btn_sell.setText(if (mOperationType == 1) getString(R.string.sl_str_sell_open_short) else getString(R.string.sl_str_sell_close_long))
        } else {
            btn_buy.setText(getString(R.string.sl_str_login))
            btn_sell.setText(getString(R.string.sl_str_login))
        }
        iv_fair_price_value.setOnClickListener(View.OnClickListener {
            val tips = String.format(getString(R.string.sl_str_fair_price_intro))
            val ssb = SpannableStringBuilder(tips)
            val length = tips.length
            val begin = tips.indexOf(" ")
            ssb.setSpan(ForegroundColorSpan(resources.getColor(R.color.sl_colorBlack)), 0, begin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(ForegroundColorSpan(resources.getColor(R.color.sl_colorTextSelector)), begin, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val window = PromptWindow(activity)
            window.showTitle(getString(R.string.sl_str_fair_price))
            window.showTvContent(ssb)
            window.showBtnOk(getString(R.string.sl_str_isee))
            window.showBtnClose("")
            window.showAtLocation(iv_funds_rate_value, Gravity.CENTER, 0, 0)
            window.btnOk.setOnClickListener { window.dismiss() }
            window.btnClose.setOnClickListener { window.dismiss() }
        })
        iv_price_intro.setOnClickListener(View.OnClickListener {
            val dfPrice = NumberUtil.getDecimal(2)
            val window = ContractPriceIntroduceWindow(activity)
            window.showPrice(dfPrice.format(MathHelper.round(mCurrentPrice)), dfPrice.format(MathHelper.round(mIndexPrice)) + "/" + dfPrice.format(MathHelper.round(mTagPrice)))
            window.showBtnClose()
            window.showAtLocation(btn_buy, Gravity.CENTER, 0, 0)
            window.btnClose.setOnClickListener { window.dismiss() }
        })
        iv_funds_rate_value.setOnClickListener(View.OnClickListener {
            val window = FundsRateWindow(activity)
            window.setContractId(mContractId)
            window.updateData()
            window.showAtLocation(iv_funds_rate_value, Gravity.CENTER, 0, 0)
        })
        val llmSell = LinearLayoutManager(activity)
        llmSell.orientation = LinearLayoutManager.VERTICAL
        rv_list_sell.layoutManager = llmSell
        val llmBuy = LinearLayoutManager(activity)
        llmBuy.orientation = LinearLayoutManager.VERTICAL
        rv_list_buy.layoutManager = llmBuy
        val llmTrade = LinearLayoutManager(activity)
        llmTrade.orientation = LinearLayoutManager.VERTICAL
        setOrderType(orderType, true)
        val askbid = PreferenceManager.getInstance(activity).getSharedInt(PreferenceManager.PREF_ASKBID, 0)
        setAskBid(askbid)
    }

    fun gotoTop() {
        val behavior = (appbar!!.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is AppBarLayout.Behavior) {
            val appBarLayoutBehavior = behavior
            val topAndBottomOffset = appBarLayoutBehavior.topAndBottomOffset
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.topAndBottomOffset = 0
            }
        }
    }

    fun switchBuySell(type: Int, tabOpen: RadioButton?, tabClose: RadioButton?) {
        mOperationType = type
        if (tabOpen == null || tabClose == null || btn_buy == null || btn_sell == null || tv_long_intro1 == null || tv_long_intro2 == null || tv_long_value1 == null || tv_long_value2 == null || tv_short_intro1 == null || tv_short_intro2 == null || tv_short_value1 == null || tv_short_value2 == null || et_volume == null || et_price == null || tv_aavl_value == null || tv_volume_value == null) {
            return
        }
        if (mOperationType == 1) {
            tabOpen.isChecked = true
            if (ContractSDKAgent.isLogin) {
                btn_buy!!.text = getString(R.string.sl_str_buy_open_long)
                btn_sell!!.text = getString(R.string.sl_str_sell_open_short)
            } else {
                btn_buy!!.text = getString(R.string.sl_str_login)
                btn_sell!!.text = getString(R.string.sl_str_login)
            }
            btn_buy!!.isEnabled = true
            btn_sell!!.isEnabled = true
            tv_long_intro1!!.text = getString(R.string.sl_str_entrust_cost)
            tv_long_value1!!.text = "--"
            tv_long_intro2!!.text = getString(R.string.sl_str_buy_open_up_to)
            tv_long_value2!!.text = "--"
            tv_short_intro1!!.text = getString(R.string.sl_str_entrust_cost)
            tv_short_value1!!.text = "--"
            tv_short_intro2!!.text = getString(R.string.sl_str_sell_open_up_to)
            tv_short_value2!!.text = "--"
            rl_leverage!!.visibility = View.VISIBLE
        } else if (mOperationType == 2) {
            tabClose.isChecked = true
            if (ContractSDKAgent.isLogin) {
                btn_buy!!.text = getString(R.string.sl_str_buy_close_short)
                btn_sell!!.text = getString(R.string.sl_str_sell_close_long)
            } else {
                btn_buy!!.text = getString(R.string.sl_str_login)
                btn_sell!!.text = getString(R.string.sl_str_login)
            }
            //  btn_buy.setEnabled(false);
            //  btn_sell.setEnabled(false);
            tv_long_intro1!!.text = getString(R.string.sl_str_short_position)
            tv_long_value1!!.text = "--"
            tv_long_intro2!!.text = getString(R.string.sl_str_sell_close_up_to)
            tv_long_value2!!.text = "--"
            tv_short_intro1!!.text = getString(R.string.sl_str_long_position)
            tv_short_value1!!.text = "--"
            tv_short_intro2!!.text = getString(R.string.sl_str_buy_close_up_to)
            tv_short_value2!!.text = "--"
            rl_leverage!!.visibility = View.GONE
        }
        et_volume!!.setText("")
        updateUserAsset()
        updateInfoValue(true)
    }

    private fun updateTriggerPrice() {
        if (et_price == null && et_volume == null || tv_price_value == null || mContract == null) {
            return
        }
        var price = et_trigger_price!!.text.toString()
        price = price.replace(",", ".")
        val priceUnit: String = mContract!!.px_unit
        if (priceUnit.contains(".")) {
            var priceIndex = priceUnit.length - priceUnit.indexOf(".") - 1
            if (priceIndex == 1) {
                priceIndex = 0
            }
            if (price.contains(".")) {
                val index = price.indexOf(".")
                if (index + priceIndex < price.length) {
                    price = price.substring(0, index + priceIndex)
                    et_trigger_price!!.setText(price)
                    et_trigger_price!!.setSelection(price.length)
                }
            }
        } else {
            if (price.contains(".")) {
                price = price.replace(".", "")
                et_trigger_price!!.setText(price)
                et_trigger_price!!.setSelection(price.length)
            }
        }
    }

    private fun updatePrice() {
        if (et_price == null || et_volume == null || tv_price_value == null || mContract == null) {
            return
        }
        var price = et_price!!.text.toString()
        price = price.replace(",", ".")
        if (TextUtils.isEmpty(price) || mContractId == 0) {
            tv_price_value!!.text = "0"
        }
        val priceUnit: String = mContract!!.px_unit
        if (priceUnit.contains(".")) {
            var priceIndex = priceUnit.length - priceUnit.indexOf(".") - 1
            if (priceIndex == 1) {
                priceIndex = 0
            }
            if (price.contains(".")) {
                val index = price.indexOf(".")
                if (index + priceIndex < price.length) {
                    price = price.substring(0, index + priceIndex)
                    et_price!!.setText(price)
                    et_price!!.setSelection(price.length)
                }
            }
        } else {
            if (price.contains(".")) {
                price = price.replace(".", "")
                et_price!!.setText(price)
                et_price!!.setSelection(price.length)
            }
        }
        if (price == "." || TextUtils.isEmpty(price)) {
            price = "0"
        }
        val current = MathHelper.round(price, 8)
        val decimalFormat = DecimalFormat("###################.###########", DecimalFormatSymbols(Locale.ENGLISH))
        //        double backUsd = MathHelper.round(LogicGlobal.sGlobalData.getCoin_price_usd(contract.getQuote_coin()), 6);
////        double backCny = backUsd * LogicGlobal.sUsdRateCNY;
////        double current_usd = MathHelper.round(MathHelper.mul(backUsd, current), 2);
////        double current_cny = MathHelper.round(MathHelper.mul(backCny, current), 2);
////        String sUsd = "≈$" + decimalFormat.format(current_usd);
////        String sCNY = "≈￥"+ decimalFormat.format(current_cny);
////        String coin_base = LogicLanguage.isZhEnv(getActivity()) ? sCNY : sUsd;
////
////        tv_price_value.setText(coin_base);
        var vol = et_volume!!.text.toString()
        vol = vol.replace(",", ".")
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            vol = "0"
        }
        updateInfoValue(true)
    }

    private fun updateVol() {
        if (et_price == null || et_volume == null || mContract == null) {
            return
        }
        var vol = et_volume!!.text.toString()
        vol = vol.replace(",", ".")
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            tv_volume_value!!.text = "0"
        }
        val unit = LogicContractSetting.getContractUint(context)
        if (unit == 0) {
            val volUnit: String = mContract!!.qty_unit
            if (volUnit.contains(".")) {
                val volIndex = volUnit.length - volUnit.indexOf(".")
                if (vol.contains(".")) {
                    val index = vol.indexOf(".")
                    if (index + volIndex < vol.length) {
                        vol = vol.substring(0, index + volIndex)
                        et_volume!!.setText(vol)
                        et_volume!!.setSelection(vol.length)
                    }
                }
            } else {
                if (vol.contains(".")) {
                    vol = vol.replace(".", "")
                    et_volume!!.setText(vol)
                    et_volume!!.setSelection(vol.length)
                }
            }
        } else {
            var base_coin_unit = "0.0001"
            //精度可取现货精度
//            val spotCoin: SpotCoin? = null //= LogicGlobal.sGlobalData.getSpotCoin(contract.getBase_coin());
//            if (spotCoin != null) {
//                base_coin_unit = spotCoin.getVol_unit()
//            }
            if (base_coin_unit.contains(".")) {
                val vol_index = base_coin_unit.length - base_coin_unit.indexOf(".")
                if (vol.contains(".")) {
                    val index = vol.indexOf(".")
                    if (index + vol_index < vol.length) {
                        vol = vol.substring(0, index + vol_index)
                        et_volume!!.setText(vol)
                        et_volume!!.setSelection(vol.length)
                    }
                }
            } else {
                if (vol.contains(".")) {
                    vol = vol.replace(".", "")
                    et_volume!!.setText(vol)
                    et_volume!!.setSelection(vol.length)
                }
            }
        }
        if (vol == "." || TextUtils.isEmpty(vol)) {
            vol = "0"
        }
        var price = et_price!!.text.toString()
        price = price.replace(",", ".")
        if (TextUtils.isEmpty(price) || price == ".") {
            price = "0"
        }
        updateInfoValue(false)
    }

    fun updateInfoValue(priceChange: Boolean) {
        if (et_price == null || et_volume == null || tv_volume_value == null || tv_long_value1 == null || tv_long_value2 == null || tv_short_value1 == null || tv_short_value2 == null || mContract == null) {
            return
        }
        var price = et_price!!.text.toString()
        price = price.replace(",", ".")
        if (TextUtils.isEmpty(price) || price == ".") {
            price = "0"
        }
        var vol = et_volume!!.text.toString()
        vol = vol.replace(",", ".")
        if (TextUtils.isEmpty(vol) || mContractId == 0) {
            vol = "0"
        }
        val value: String = ContractUtils.CalculateContractBasicValue(
                context!!,
                trans2ContractVol(mContract, vol, price,LogicContractSetting.getContractUint(activity)),
                price,
                mContract)
        tv_volume_value!!.text = "≈$value"
        vol = trans2ContractVol(mContract, vol, price,LogicContractSetting.getContractUint(activity))
        if (mOperationType == 1) {
            val contractOrder = ContractOrder()
            contractOrder.leverage = if (mLeverage == 0) mContract!!.getMax_leverage().toInt() else mLeverage
            contractOrder.qty = vol
            if (orderType == LogicBuySell.ORDER_TYPE_LIMIT) {
                contractOrder.px = price
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            } else if (orderType == LogicBuySell.ORDER_TYPE_MARKET) {
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_MARKET
            }
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
            val longOpenCost = CalculateAdvanceOpenCost(
                    contractOrder,
                    ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG),
                    ContractUserDataAgent.getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                    mContract)
            contractOrder.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT
            val shortOpenCost = CalculateAdvanceOpenCost(
                    contractOrder,
                    ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT),
                    ContractUserDataAgent.getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                    mContract)
            tv_long_value1!!.text = if (longOpenCost == null) "0" else longOpenCost.freezAssets + mContract!!.margin_coin
            tv_short_value1!!.text = if (shortOpenCost == null) "0" else shortOpenCost.freezAssets + mContract!!.margin_coin
            if (!priceChange) {
                return
            }
            val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(mContract!!.margin_coin)
            if (contractAccount != null) {
                val dfVol = NumberUtil.getDecimal(mContract!!.value_index)
                val longVolume = CalculateVolume(
                        dfVol.format(contractAccount.available_vol_real),
                        if (mLeverage == 0) mContract!!.max_leverage.toInt() else mLeverage,
                        ContractUserDataAgent.getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG),
                        ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG),
                        if (orderType == LogicBuySell.ORDER_TYPE_LIMIT) price else mCurrentPrice,
                        ContractPosition.POSITION_TYPE_LONG,
                        mContract!!)
                val shortVolume = CalculateVolume(
                        dfVol.format(contractAccount.available_vol_real),
                        if (mLeverage == 0) mContract!!.max_leverage.toInt() else mLeverage,
                        ContractUserDataAgent.getContractOrderSize(mContractId, ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT),
                        ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT),
                        if (orderType == LogicBuySell.ORDER_TYPE_LIMIT) price else mCurrentPrice,
                        ContractPosition.POSITION_TYPE_SHORT,
                        mContract!!)
                tv_long_value2.setText(ContractUtils.getVolUnit(activity,mContract!!, longVolume, MathHelper.round(price)))
                tv_short_value2.setText(ContractUtils.getVolUnit(activity,mContract!!, shortVolume, MathHelper.round(price)))
            } else {
                tv_long_value2!!.text = "0"
                tv_short_value2!!.text = "0"
            }
        } else if (mOperationType == 2) {
            val dfVol = NumberUtil.getDecimal(mContract!!.value_index)
            val longPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG)
            if (longPosition != null) {
                val avail = MathHelper.sub(longPosition.cur_qty, longPosition.freeze_qty)
                tv_short_value1.text = ContractUtils.getVolUnit(activity,mContract, longPosition.cur_qty, price)
                tv_short_value2.text = ContractUtils.getVolUnit(activity,mContract, avail, MathHelper.round(price))
                btn_sell!!.isEnabled = avail > 0
            } else {
                tv_short_value1!!.text = "0"
                tv_short_value2!!.text = "0"
                //                if (SLSDKAgent.slUser!=null) {
//                    btn_sell.setEnabled(false);
//                } else {
//                    btn_sell.setEnabled(true);
//                }
            }
            val shortPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT)
            if (shortPosition != null) {
                val avail = MathHelper.sub(shortPosition.cur_qty, shortPosition.freeze_qty)
                tv_long_value1.text = ContractUtils.getVolUnit(activity,mContract, shortPosition.cur_qty, price)
                tv_long_value2.text = ContractUtils.getVolUnit(activity,mContract, avail, MathHelper.round(price))
                // btn_buy.setEnabled(avail > 0);
            } else {
                tv_long_value1!!.text = "0"
                tv_long_value2!!.text = "0"
                //                if (SLSDKAgent.slUser!=null) {
//                    btn_buy.setEnabled(false);
//                } else {
//                    btn_buy.setEnabled(true);
//                }
            }
        }
    }

    fun updateContract(data: ContractTicker, updatePrice: Boolean) {
        mContractId = data.instrument_id
        if(updatePrice){
            mContract = ContractPublicDataAgent.getContract(mContractId)?: return
        }
        if (tv_index_price_value == null || et_price == null || et_volume == null || btn_buy == null || btn_sell == null || tv_aavl_value == null || tv_price == null || tv_amount == null) {
            return
        }
        val dfPrice = NumberUtil.getDecimal(mContract!!.price_index)
        val dfprice1 = NumberUtil.getDecimal(mContract!!.price_index - 1)
        mCurrentPrice = dfPrice.format(MathHelper.round(data.last_px))
        mIndexPrice = dfPrice.format(MathHelper.round(data.index_px))
        mTagPrice = dfPrice.format(MathHelper.round(data.fair_px))
        val rise_fall_rate = MathHelper.round(MathHelper.round(data.change_rate, 8) * 100, 2)
        val current = MathHelper.round(data.last_px, mContract!!.price_index)
        val indexPrice = MathHelper.round(data.index_px, mContract!!.price_index)
        val fairPrice = MathHelper.round(data.fair_px, mContract!!.price_index)
        val curPrice = dfprice1.format(current)
        tv_index_price_value!!.text = dfPrice.format(indexPrice)
        tv_fair_price_value!!.text = dfPrice.format(fairPrice)
        tv_price!!.text = getString(R.string.sl_str_price) + "(" + mContract!!.quote_coin + ")"
        val unit = LogicContractSetting.getContractUint(context)
        tv_amount!!.text = getString(R.string.sl_str_amount) + "(" + (if (unit == 0) getString(R.string.sl_str_contracts_unit) else mContract!!.base_coin) + ")"
        val rate = MathHelper.mul(data.funding_rate, "100")
        tv_funds_rate_value!!.text = NumberUtil.getDecimal(-1).format(MathHelper.round(rate, 4)) + "%"
        tv_price_unit.text = mContract!!.quote_coin
        tv_trigger_price_unit.text = mContract!!.quote_coin
        if (updatePrice) {
            et_trigger_price!!.setText("")
            et_price!!.setText(curPrice)
            updatePrice()
            et_volume!!.setText("")
            updateVol()
        }
        updateLeverage()
        updateUserAsset()
        val order_type = PreferenceManager.getInstance(context).getSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, 0)
        setOrderType(order_type, updatePrice)
        setVolUnit()
    }

    private fun updateLeverage() {
        if (mContractId <= 0) {
            return
        }
        mLeverage = PreferenceManager.getInstance(context).getSharedInt(PreferenceManager.PREF_LEVERAGE, 10)
        val minLeverage: Int = mContract!!.min_leverage.toInt()
        val maxLeverage: Int = mContract!!.max_leverage.toInt()
        var leverage = if (mLeverage == 0) maxLeverage else mLeverage
        if (leverage > maxLeverage || leverage < minLeverage) {
            leverage = 100
            if (leverage <= maxLeverage && leverage >= minLeverage) {
                mLeverage = 0
            } else {
                leverage = 50
                if (leverage <= maxLeverage && leverage >= minLeverage) {
                    mLeverage = leverage
                } else {
                    leverage = 20
                    if (leverage <= maxLeverage && leverage >= minLeverage) {
                        mLeverage = leverage
                    } else {
                        leverage = 10
                        if (leverage <= maxLeverage && leverage >= minLeverage) {
                            mLeverage = leverage
                        }
                    }
                }
            }
        }
        if (mLeverage == 0) {
            tv_select_leverage!!.text = getString(R.string.sl_str_full_position) + maxLeverage + "X"
        } else {
            tv_select_leverage!!.text = getString(R.string.sl_str_gradually_position) + mLeverage + "X"
        }
    }

    fun updateUserAsset() {
        if (tv_aavl_value == null) {
            return
        }
        if (mContractId <= 0) {
            return
        }
        mContract ?: return
        val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(mContract!!.margin_coin)
        if (contractAccount == null) {
            tv_aavl_value!!.text = "0" + " " + mContract!!.getMargin_coin()
            return
        }
        val dfValue = NumberUtil.getDecimal(mContract!!.value_index)
        val available = contractAccount.available_vol_real
        tv_aavl_value!!.text = dfValue.format(available) + " " + mContract!!.margin_coin
    }


    fun updateBuyDepth(data:List<DepthData>) {
        if (mContractId <= 0) {
            return
        }
        if (rv_list_sell == null || rv_list_buy == null) {
            return
        }

        if (mBuyAdapter == null) {
            mBuyAdapter = BuySellContractAdapter(context, this)
            mBuyAdapter?.bindContract(mContract!!)
            mBuyAdapter!!.setData(data, 1,  mContract!!.getPrice_index(), mABShowNum)
            rv_list_buy!!.adapter = mBuyAdapter
        } else {
            mBuyAdapter?.bindContract(mContract!!)
            mBuyAdapter?.setData(data, 1,  mContract!!.price_index, mABShowNum)
            mBuyAdapter?.notifyDataSetChanged()
        }
    }

    fun updateSellDepth(data:List<DepthData>) {
        if (mContractId <= 0) {
            return
        }
        if (rv_list_sell == null || rv_list_buy == null) {
            return
        }

        if (mSellAdapter == null) {
            mSellAdapter = BuySellContractAdapter(context, this)
            mSellAdapter?.bindContract(mContract!!)
            mSellAdapter!!.setData(data, 2, mContract!!.price_index, mABShowNum)
            rv_list_sell!!.adapter = mSellAdapter
        } else {
            mSellAdapter?.bindContract(mContract!!)
            mSellAdapter!!.setData(data, 2,  mContract!!.getPrice_index(), mABShowNum)
            rv_list_sell!!.adapter = mSellAdapter
        }
    }




    fun updateOpenOrder(contractId: Int) {
        if (mContractOpenOrderFragment != null) {
            mContractOpenOrderFragment!!.setContractId(contractId, false)
        }
        if (mContractEntrustHistoryFragment != null) {
            mContractEntrustHistoryFragment!!.setContractId(contractId, false)
        }
        if (mContractPlanOrderFragment != null) {
            mContractPlanOrderFragment!!.setContractId(contractId, false)
        }
        if (mHoldContractNowFragment != null) {
            mHoldContractNowFragment!!.setContractId(contractId, false)
        }
    }

    fun updatePlanOrder(contractId: Int) {
        if (mContractPlanOrderFragment != null) {
            mContractPlanOrderFragment!!.setContractId(contractId, false)
        }
    }

    private fun doBuy() {
        if (!ContractSDKAgent.isLogin) {
            ToastUtil.shortToast(context,"未登录")
            return
        }

        mContract?: return
        val order_type = orderType
        var vol = if (TextUtils.isEmpty(et_volume!!.text.toString())) "0" else et_volume!!.text.toString()
        val price = if (TextUtils.isEmpty(et_price!!.text.toString())) "0" else et_price!!.text.toString()
        if (MathHelper.round(price) <= 0 && order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            ToastUtil.shortToast(activity, getString(R.string.sl_str_price_too_low))
            return
        }
        vol = trans2ContractVol(mContract!!, vol, price,LogicContractSetting.getContractUint(context))
        if (MathHelper.round(vol) <= 0.0) {
            ToastUtil.shortToast(activity, getString(R.string.sl_str_volume_too_low))
            return
        }
        interBuy(price, vol, order_type)
    }

    private fun interBuy(price: String, vol: String, order_type: Int) {
        val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(mContract!!.margin_coin)?:return
        if (contractAccount == null) {
            val tips = java.lang.String.format(getString(R.string.sl_str_no_contract_account), mContract!!.margin_coin)
            ToastUtil.shortToast(context, tips)
            return
        }
        val trade_warn_confirm = PreferenceManager.getInstance(context).getSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, true)
        val trade_confirm = PreferenceManager.getInstance(context).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true)
        if (order_type != LogicBuySell.ORDER_TYPE_PLAN) {
            if (!trade_confirm && !trade_warn_confirm) {
                innerBuy(price, vol, order_type, "")
                return
            }
            val dfDefault = NumberUtil.getDecimal(-1)
            val accountMode = if (mLeverage == 0) 2 else 1
            val mode = if (accountMode == 1) getString(R.string.sl_str_gradually_position) else getString(R.string.sl_str_full_position)
            val leverage = mode + (if (mLeverage == 0) mContract!!.max_leverage.toInt() else mLeverage) + getString(R.string.sl_str_bei)
            var warning = ""
            var priceDisplay: String? = ""
            val contractOrder = ContractOrder()
            contractOrder.leverage = if (mLeverage == 0) mContract!!.getMax_leverage().toInt() else mLeverage
            contractOrder.qty = vol
            contractOrder.position_type = accountMode
            contractOrder.side = if (mOperationType == 1) ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG else ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
            if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
                priceDisplay = dfDefault.format(MathHelper.round(price, mContract!!.getPrice_index()))
                contractOrder.px = price
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
                val tip_limit = MathHelper.div(MathHelper.sub(price, mCurrentPrice), MathHelper.round(mCurrentPrice))
                if (0.05 < tip_limit) {
                    warning = if (mOperationType == 1) getString(R.string.sl_str_open_risk_tips) else getString(R.string.sl_str_close_risk_tips)
                }
            } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
                priceDisplay = getString(R.string.sl_str_market_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_MARKET
                val avgPrice = CalculateMarketAvgPrice(vol,  mSellAdapter?.news, false)
                val tipLimit = MathHelper.div(MathHelper.sub(avgPrice, MathHelper.round(mCurrentPrice)), MathHelper.round(mCurrentPrice))
                if (0.03 < tipLimit) {
                    warning = if (mOperationType == 1) getString(R.string.sl_str_open_market_risk_tips) else getString(R.string.sl_str_close_market_risk_tips)
                }
            } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
                priceDisplay = getString(R.string.sl_str_buy1_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
                priceDisplay = getString(R.string.sl_str_sell1_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            val contractValue = CalculateContractValue(vol, price, mContract)
            val longOpenCost = CalculateAdvanceOpenCost(
                    contractOrder,
                    ContractUserDataAgent.getContractPosition(mContractId, if (mOperationType == 1) ContractPosition.POSITION_TYPE_LONG else ContractPosition.POSITION_TYPE_SHORT),
                    ContractUserDataAgent.getContractOrderSize(mContractId, if (mOperationType == 1) ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG else ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT),
                    mContract)
            val available = contractAccount.available_vol_real
            val liquidatePrice = CalculateOrderLiquidatePrice(
                    contractOrder,
                    contractAccount,
                    mContract!!)
            val ratio = MathHelper.div(MathHelper.round(mTagPrice), liquidatePrice) * 100
            val contractPosition: ContractPosition? =ContractUserDataAgent.getContractPosition(mContractId, if (mOperationType == 1) ContractPosition.POSITION_TYPE_LONG else ContractPosition.POSITION_TYPE_SHORT)
            val hold_vol = if (contractPosition == null) "0" else contractPosition.cur_qty
            if (!TextUtils.isEmpty(warning) && trade_warn_confirm || trade_confirm) {
                val window = ContractTradeConfirmWindow(activity)
                window.showTitle(if (mOperationType == 1) getString(R.string.sl_str_buy_open) else getString(R.string.sl_str_buy_close), context!!.resources.getColor(R.color.sl_colorGreen))
                window.setInfo(mContract!!.symbol, getString(R.string.sl_str_price) + "(" + mContract!!.quote_coin + ")",
                        priceDisplay,
                        getString(R.string.sl_str_amount) + "(" + getString(R.string.sl_str_contracts_unit) + ")",
                        vol,
                        leverage)
                if (!TextUtils.isEmpty(warning) && trade_warn_confirm) {
                    window.showWarning(warning)
                }
                window.setData(
                        dfDefault.format(MathHelper.round(contractValue, mContract!!.value_index)),
                        if (longOpenCost == null) "0" else dfDefault.format(MathHelper.round(longOpenCost.freezAssets,  mContract!!.value_index)),
                        dfDefault.format(MathHelper.round(available,  mContract!!.value_index)),
                        dfDefault.format(max(0.0, MathHelper.round(if (mOperationType == 1) MathHelper.add(vol, hold_vol) else MathHelper.sub(hold_vol, vol),  mContract!!.getVol_index()))),
                        dfDefault.format(MathHelper.round(mTagPrice,  mContract!!.price_index)),
                        dfDefault.format(MathHelper.round(liquidatePrice,   mContract!!.price_index)),
                        dfDefault.format(MathHelper.round(ratio,  mContract!!.value_index)) + "%")
                window.setCode( mContract!!.margin_coin,  mContract!!.margin_coin,  mContract!!.margin_coin)
                window.setOrderType(order_type)
                window.setOperationType(mOperationType)
                window.showBtnOk(activity!!.getString(R.string.sl_str_confirm))
                window.showBtnClose()
                window.showAtLocation(btn_buy, Gravity.CENTER, 0, 0)
                val finalWarning = warning
                window.btnOk.setOnClickListener {
                    window.dismiss()
                    if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.noremindCheck)
                    } else {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                    }
                    innerBuy(price, vol, order_type, "")
                }
                window.btnClose.setOnClickListener {
                    window.dismiss()
                    if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.noremindCheck)
                    } else {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                    }
                }
            } else {
                innerBuy(price, vol, order_type, "")
                return
            }
        } else {
            if (!trade_confirm) {
                innerBuy(price, vol, order_type, "")
                return
            }
            val dfDefault = NumberUtil.getDecimal(-1)
            val accountMode = if (mLeverage == 0) 2 else 1
            val mode = if (accountMode == 1) getString(R.string.sl_str_gradually_position) else getString(R.string.sl_str_full_position)
            val leverage = mode + (if (mLeverage == 0)  mContract!!.max_leverage.toInt() else mLeverage) + getString(R.string.sl_str_bei)
            val triggerPrice = if (TextUtils.isEmpty(et_trigger_price!!.text.toString())) "0" else et_trigger_price!!.text.toString()
            val triggerType = LogicContractSetting.getTriggerPriceType(context)
            var triggerTypeText = ""
            when (triggerType) {
                0 -> {
                    triggerTypeText = getString(R.string.sl_str_latest_price)
                }
                1 -> {
                    triggerTypeText = getString(R.string.sl_str_fair_price)
                }
                2 -> {
                    triggerTypeText = getString(R.string.sl_str_index_price)
                }
            }
            val effect = LogicContractSetting.getStrategyEffectTime(context)
            val window = ContractPlanConfirmWindow(activity)
            window.showTitle(if (mOperationType == 1) getString(R.string.sl_str_buy_open) + getString(R.string.sl_str_plan) else getString(R.string.sl_str_buy_close) + getString(R.string.sl_str_plan),
                    context!!.resources.getColor(R.color.sl_colorGreen))
            window.setInfo( mContract!!.symbol, dfDefault.format(MathHelper.round(triggerPrice,  mContract!!.price_index)),
                    if (LogicContractSetting.getExecution(context) == 1) getString(R.string.sl_str_market_price_simple) else dfDefault.format(MathHelper.round(price,  mContract!!.price_index)),
                    vol)
            window.setData(
                    leverage,
                    triggerTypeText,
                    if (effect == 0) getString(R.string.sl_str_in_24_hours) else getString(R.string.sl_str_in_7_days))
            window.showBtnOk(activity!!.getString(R.string.sl_str_confirm))
            window.showBtnClose()
            window.showAtLocation(btn_buy, Gravity.CENTER, 0, 0)
            window.btnOk.setOnClickListener {
                window.dismiss()
                PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                innerBuy(price, vol, order_type, "")
            }
            window.btnClose.setOnClickListener {
                window.dismiss()
                PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
            }
            return
        }
    }

    private fun innerBuy(price: String, vol: String, order_type: Int, pwd: String) {
        //MobclickAgent.onEvent(context, "ss_by");
        mContract ?: return
        val order = ContractOrder()
        order.instrument_id = mContractId
        order.nonce = System.currentTimeMillis() / 1000
        order.qty = vol
        if (mOperationType == 1) {
            order.position_type = if (mLeverage == 0) 2 else 1
            order.leverage = if (mLeverage == 0) mContract!!.max_leverage.toInt() else mLeverage
            order.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG
        } else {
            val shortPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_SHORT)
            if (shortPosition != null) {
                order.pid = shortPosition.pid
            }
            order.side = ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT
        }
        val orgText = btn_buy!!.text
        btn_buy!!.startLoading("")


        val response: IResponse<String> = object : IResponse<String>() {
            override fun onFail(code: String, msg: String) {
                btn_buy!!.stopLoading(orgText)
                ToastUtil.shortToast(activity, msg)
            }

            override fun onSuccess(data: String) {
                btn_buy!!.stopLoading(orgText)
                ToastUtil.shortToast(activity, getString(R.string.sl_str_order_submit_success))
            }
        }
        when (order_type) {
            LogicBuySell.ORDER_TYPE_LIMIT -> {
                order.px = price
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_ASK_PRICE -> {
                val list =  mSellAdapter?.news
                if (list != null && list.size > 0) {
                    order.px = list[list.size - 1].price
                } else {
                    order.px = price
                }
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_BID_PRICE -> {
                val list =  mBuyAdapter?.news
                if (list != null && list.size > 0) {
                    order.px = list[0].price
                } else {
                    order.px = price
                }
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_TYPE_MARKET -> {
                order.category = ContractOrder.ORDER_CATEGORY_MARKET
            }
            LogicBuySell.ORDER_TYPE_PLAN -> {
                val triggerPrice = if (TextUtils.isEmpty(et_trigger_price!!.text.toString())) "0" else et_trigger_price!!.text.toString()
                order.px = triggerPrice
                order.exec_px = price
                order.category = if (LogicContractSetting.getExecution(context) == 0) ContractOrder.ORDER_CATEGORY_NORMAL else ContractOrder.ORDER_CATEGORY_MARKET
                val triggerType = LogicContractSetting.getTriggerPriceType(context)
                var priceType = 0
                if (triggerType == 0) {
                    priceType = 1
                    if (MathHelper.round(mCurrentPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                } else if (triggerType == 1) {
                    priceType = 2
                    if (MathHelper.round(mTagPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                } else if (triggerType == 2) {
                    priceType = 4
                    if (MathHelper.round(mIndexPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                }
                order.trigger_type = priceType
                val effect = LogicContractSetting.getStrategyEffectTime(context)
                order.life_cycle = if (effect == 0) 24 else 168
                ContractUserDataAgent.doSubmitPlanOrder(order, response)
                return
            }
        }
        ContractUserDataAgent.doSubmitOrder(order, response)
    }

    private fun doSell() {
        if (!ContractSDKAgent.isLogin) {
            ToastUtil.shortToast(activity,"未登录")
            return
        }
        mContract?: return
        val order_type = orderType
        var vol = if (TextUtils.isEmpty(et_volume!!.text.toString())) "0" else et_volume!!.text.toString()
        val price = if (TextUtils.isEmpty(et_price!!.text.toString())) "0" else et_price!!.text.toString()
        if (MathHelper.round(price) <= 0 && order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
            ToastUtil.shortToast(activity, getString(R.string.sl_str_price_too_low))
            return
        }
        vol = trans2ContractVol(mContract!!, vol, price,LogicContractSetting.getContractUint(activity))
        if (MathHelper.round(vol) <= 0.0) {
            ToastUtil.shortToast(activity, getString(R.string.sl_str_volume_too_low))
            return
        }
        interSell(price, vol, order_type)
    }

    private fun interSell(price: String, vol: String, order_type: Int) {
        val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(mContract!!.margin_coin)
        if (contractAccount == null) {
            val tips = java.lang.String.format(getString(R.string.sl_str_no_contract_account), mContract!!.margin_coin)
            ToastUtil.shortToast(context, tips)
            return
        }
        val trade_warn_confirm = PreferenceManager.getInstance(context).getSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, true)
        val trade_confirm = PreferenceManager.getInstance(context).getSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, true)
        if (order_type != LogicBuySell.ORDER_TYPE_PLAN) {
            if (!trade_confirm && !trade_warn_confirm) {
                innerSell(price, vol, order_type, "")
                return
            }
            val dfDefault = NumberUtil.getDecimal(-1)
            val accountMode = if (mLeverage == 0) 2 else 1
            val mode = if (accountMode == 1) getString(R.string.sl_str_gradually_position) else getString(R.string.sl_str_full_position)
            val leverage = mode + (if (mLeverage == 0) mContract!!.getMax_leverage().toInt() else mLeverage) + getString(R.string.sl_str_bei)
            var warning = ""
            var price_display: String? = ""
            val contractOrder = ContractOrder()
            contractOrder.leverage = if (mLeverage == 0) mContract!!.getMax_leverage().toInt() else mLeverage
            contractOrder.qty = vol
            contractOrder.position_type = accountMode
            contractOrder.side = if (mOperationType == 1) ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT else ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
            if (order_type == LogicBuySell.ORDER_TYPE_LIMIT) {
                price_display = dfDefault.format(MathHelper.round(price, mContract!!.getPrice_index()))
                contractOrder.px = price
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
                val tip_limit = MathHelper.div(MathHelper.sub(mCurrentPrice, price), MathHelper.round(mCurrentPrice))
                if (0.05 < tip_limit) {
                    warning = if (mOperationType == 1) getString(R.string.sl_str_open_risk_tips) else getString(R.string.sl_str_close_risk_tips)
                }
            } else if (order_type == LogicBuySell.ORDER_TYPE_MARKET) {
                price_display = getString(R.string.sl_str_market_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_MARKET
                val avgPrice = CalculateMarketAvgPrice(vol, mBuyAdapter!!.news, true)
                val tip_limit = MathHelper.div(MathHelper.sub(MathHelper.round(mCurrentPrice), avgPrice), MathHelper.round(mCurrentPrice))
                if (0.03 < tip_limit) {
                    warning = if (mOperationType == 1) getString(R.string.sl_str_open_market_risk_tips) else getString(R.string.sl_str_close_market_risk_tips)
                }
            } else if (order_type == LogicBuySell.ORDER_BID_PRICE) {
                price_display = getString(R.string.sl_str_buy1_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            } else if (order_type == LogicBuySell.ORDER_ASK_PRICE) {
                price_display = getString(R.string.sl_str_sell1_price)
                contractOrder.px = mCurrentPrice
                contractOrder.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            val contractValue = CalculateContractValue(vol, price, mContract!!)
            val shortOpenCost = CalculateAdvanceOpenCost(
                    contractOrder,
                    ContractUserDataAgent.getContractPosition(mContractId, if (mOperationType == 1) ContractPosition.POSITION_TYPE_SHORT else ContractPosition.POSITION_TYPE_LONG),
                    ContractUserDataAgent.getContractOrderSize(mContractId, if (mOperationType == 1) ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT else ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG),
                    mContract!!)
            val available = contractAccount.available_vol_real
            val liquidatePrice = CalculateOrderLiquidatePrice(
                    contractOrder,
                    contractAccount,
                    mContract!!)
            val ratio = MathHelper.div(MathHelper.round(mTagPrice), liquidatePrice) * 100
            val contractPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(mContractId, if (mOperationType == 1) ContractPosition.POSITION_TYPE_SHORT else ContractPosition.POSITION_TYPE_LONG)
            val hold_vol = if (contractPosition == null) "0" else contractPosition.cur_qty
            if (!TextUtils.isEmpty(warning) && trade_warn_confirm || trade_confirm) {
                val window = ContractTradeConfirmWindow(activity)
                window.showTitle(if (mOperationType == 1) getString(R.string.sl_str_sell_open) else getString(R.string.sl_str_sell_close), activity!!.resources.getColor(R.color.sl_colorRed))
                window.setInfo(mContract!!.symbol, getString(R.string.sl_str_price) + "(" + mContract!!.quote_coin + ")",
                        price_display,
                        getString(R.string.sl_str_amount) + "(" + getString(R.string.sl_str_contracts_unit) + ")",
                        vol,
                        leverage)
                if (!TextUtils.isEmpty(warning) && trade_warn_confirm) {
                    window.showWarning(warning)
                }
                window.setData(
                        dfDefault.format(MathHelper.round(contractValue, mContract!!.value_index)),
                        if (shortOpenCost == null) "0" else dfDefault.format(MathHelper.round(shortOpenCost.freezAssets, mContract!!.value_index)),
                        dfDefault.format(MathHelper.round(available, mContract!!.value_index)),
                        dfDefault.format(Math.max(0.0, MathHelper.round(if (mOperationType == 1) MathHelper.add(vol, hold_vol) else MathHelper.sub(hold_vol, vol), mContract!!.vol_index))),
                        dfDefault.format(MathHelper.round(mTagPrice, mContract!!.price_index)),
                        dfDefault.format(MathHelper.round(liquidatePrice, mContract!!.price_index)),
                        dfDefault.format(MathHelper.round(ratio, mContract!!.value_index)) + "%")
                window.setCode(mContract!!.margin_coin, mContract!!.margin_coin, mContract!!.margin_coin)
                window.setOrderType(order_type)
                window.setOperationType(mOperationType)
                window.showBtnOk(activity!!.getString(R.string.sl_str_confirm))
                window.showBtnClose()
                window.showAtLocation(btn_buy, Gravity.CENTER, 0, 0)
                val finalWarning = warning
                window.btnOk.setOnClickListener {
                    window.dismiss()
                    if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.noremindCheck)
                    } else {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                    }
                    innerSell(price, vol, order_type, "")
                }
                window.btnClose.setOnClickListener {
                    window.dismiss()
                    if (!TextUtils.isEmpty(finalWarning) && trade_warn_confirm) {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_WARN_CONFIRM, !window.noremindCheck)
                    } else {
                        PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                    }
                }
            } else {
                innerSell(price, vol, order_type, "")
                return
            }
        } else {
            if (!trade_confirm) {
                innerSell(price, vol, order_type, "")
                return
            }
            val dfDefault = NumberUtil.getDecimal(-1)
            val accountMode = if (mLeverage == 0) 2 else 1
            val mode = if (accountMode == 1) getString(R.string.sl_str_gradually_position) else getString(R.string.sl_str_full_position)
            val leverage = mode + (if (mLeverage == 0) mContract!!.getMax_leverage().toInt() else mLeverage) + getString(R.string.sl_str_bei)
            val trigger_price = if (TextUtils.isEmpty(et_trigger_price!!.text.toString())) "0" else et_trigger_price!!.text.toString()
            val trigger_type = LogicContractSetting.getTriggerPriceType(context)
            var trigger_type_text = ""
            if (trigger_type == 0) {
                trigger_type_text = getString(R.string.sl_str_latest_price) //set list map
            } else if (trigger_type == 1) {
                trigger_type_text = getString(R.string.sl_str_fair_price)
            } else if (trigger_type == 2) {
                trigger_type_text = getString(R.string.sl_str_index_price)
            }
            val effect = LogicContractSetting.getStrategyEffectTime(context)
            val window = ContractPlanConfirmWindow(activity)
            window.showTitle(if (mOperationType == 1) getString(R.string.sl_str_sell_open) + getString(R.string.sl_str_plan) else getString(R.string.sl_str_sell_close) + getString(R.string.sl_str_plan),
                    context!!.resources.getColor(R.color.sl_colorRed))
            window.setInfo(mContract!!.symbol, dfDefault.format(MathHelper.round(trigger_price, mContract!!.price_index)),
                    if (LogicContractSetting.getExecution(context) == 1) getString(R.string.sl_str_market_price_simple) else dfDefault.format(MathHelper.round(price, mContract!!.price_index)),
                    vol)
            window.setData(
                    leverage,
                    trigger_type_text,
                    if (effect == 0) getString(R.string.sl_str_in_24_hours) else getString(R.string.sl_str_in_7_days))
            window.showBtnOk(activity!!.getString(R.string.sl_str_confirm))
            window.showBtnClose()
            window.showAtLocation(btn_sell, Gravity.CENTER, 0, 0)
            window.btnOk.setOnClickListener {
                window.dismiss()
                PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
                innerSell(price, vol, order_type, "")
            }
            window.btnClose.setOnClickListener {
                window.dismiss()
                PreferenceManager.getInstance(context).putSharedBoolean(PreferenceManager.PREF_TRADE_CONFIRM, !window.noremindCheck)
            }
        }
    }

    private fun innerSell(price: String, vol: String, order_type: Int, pwd: String) {
        // MobclickAgent.onEvent(context, "ss_sl");
        val order = ContractOrder()
        order.instrument_id = mContractId
        order.nonce = System.currentTimeMillis() / 1000
        order.qty = vol
        if (mOperationType == 1) {
            order.position_type = if (mLeverage == 0) 2 else 1
            order.leverage = if (mLeverage == 0) mContract!!.max_leverage.toInt() else mLeverage
            order.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT
        } else {
            val longPosition: ContractPosition? = ContractUserDataAgent.getContractPosition(mContractId, ContractPosition.POSITION_TYPE_LONG)
            if (longPosition != null) {
                order.pid = longPosition.pid
            }
            order.side = ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG
        }
        val orgText = btn_sell!!.text
        btn_sell!!.startLoading("")
        val response: IResponse<String> = object : IResponse<String>() {
                override fun onSuccess(data: String) {
                    btn_sell!!.stopLoading(orgText)
                    ToastUtil.shortToast(activity, getString(R.string.sl_str_order_submit_success))
                }

                override fun onFail(code: String, msg: String) {
                    btn_sell!!.stopLoading(orgText)
                    ToastUtil.shortToast(activity, msg)
                }
        }
        when (order_type) {
            LogicBuySell.ORDER_TYPE_LIMIT -> {
                order.px = price
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_ASK_PRICE -> {
                val list = mSellAdapter!!.news
                if (list != null && list.size > 0) {
                    order.px = list[list.size - 1].price
                } else {
                    order.px = price
                }
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_BID_PRICE -> {
                val list = mBuyAdapter!!.news
                if (list != null && list.size > 0) {
                    order.px = list[0].price
                } else {
                    order.px = price
                }
                order.category = ContractOrder.ORDER_CATEGORY_NORMAL
            }
            LogicBuySell.ORDER_TYPE_MARKET -> {
                order.category = ContractOrder.ORDER_CATEGORY_MARKET
            }
            LogicBuySell.ORDER_TYPE_PLAN -> {
                val triggerPrice = if (TextUtils.isEmpty(et_trigger_price!!.text.toString())) "0" else et_trigger_price!!.text.toString()
                order.px = triggerPrice
                order.exec_px = price
                order.category = if (LogicContractSetting.getExecution(context) == 0) ContractOrder.ORDER_CATEGORY_NORMAL else ContractOrder.ORDER_CATEGORY_MARKET
                val triggerType = LogicContractSetting.getTriggerPriceType(context)
                var priceType = 0
                if (triggerType == 0) {
                    priceType = 1
                    if (MathHelper.round(mCurrentPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                } else if (triggerType == 1) {
                    priceType = 2
                    if (MathHelper.round(mTagPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                } else if (triggerType == 2) {
                    priceType = 4
                    if (MathHelper.round(mIndexPrice) > MathHelper.round(triggerPrice)) {
                        order.trend = 2
                    } else {
                        order.trend = 1
                    }
                }
                order.trigger_type = priceType
                val effect = LogicContractSetting.getStrategyEffectTime(context)
                order.life_cycle = if (effect == 0) 24 else 168
                ContractUserDataAgent.doSubmitPlanOrder(order,response)
                return
            }
        }
        ContractUserDataAgent.doSubmitOrder(order,response)
    }

    private fun selectLeverage() {
        val intent = Intent()
        intent.setClass(activity, SelectLeverageActivity::class.java)
        intent.putExtra("contractId", mContractId)
        intent.putExtra("leverage", mLeverage)
        startActivity(intent)
    }

    private fun gotoTransfer() {
        if (!ContractSDKAgent.isLogin) {
            ToastUtil.shortToast(activity,"未登录")
        } else {
            ToastUtil.shortToast(activity,"需接入方自己实现")
//            val contract: Contract = LogicGlobal.getContract(mContractId) ?: return
//            val intent = Intent()
//            intent.setClass(activity, FundsTransferActivity::class.java)
//            intent.putExtra("coin_code", contract.getMargin_coin())
//            startActivity(intent)
        }

    }


    private val orderType: Int
        private get() = PreferenceManager.getInstance(activity).getSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, 0)

    private fun setOrderType(type: Int, updatePrice: Boolean) {
        if (type == LogicBuySell.ORDER_TYPE_LIMIT) {
            tv_order_type!!.setText(R.string.sl_str_limit_entrust)
            rg_order_type!!.clearCheck()
            rg_order_type!!.visibility = View.VISIBLE
            tv_price_value!!.visibility = View.VISIBLE
            rl_limit_price!!.visibility = View.VISIBLE
            rl_market_price!!.visibility = View.GONE
            rl_trigger_price!!.visibility = View.GONE
            iv_setting!!.visibility = View.GONE
            tv_price2market!!.visibility = View.GONE
            tv_price2limit!!.visibility = View.GONE
            tv_market_price!!.gravity = Gravity.CENTER
            if (updatePrice) {
                et_price!!.setText(mCurrentPrice)
                et_price!!.setHint(R.string.sl_str_price)
            }
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_LIMIT)
        } else if (type == LogicBuySell.ORDER_TYPE_MARKET) {
            tv_order_type!!.setText(R.string.sl_str_limit_entrust)
            tab_market_price!!.isChecked = true
            tv_market_price!!.setText(R.string.sl_str_market_price)
            rg_order_type!!.visibility = View.VISIBLE
            tv_price_value!!.visibility = View.GONE
            rl_limit_price!!.visibility = View.GONE
            rl_market_price!!.visibility = View.VISIBLE
            rl_trigger_price!!.visibility = View.GONE
            iv_setting!!.visibility = View.GONE
            tv_price2market!!.visibility = View.GONE
            tv_price2limit!!.visibility = View.GONE
            tv_market_price!!.gravity = Gravity.CENTER
            if (updatePrice) {
                et_price!!.setText(mCurrentPrice)
                et_price!!.setHint(R.string.sl_str_price)
            }
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_MARKET)
        } else if (type == LogicBuySell.ORDER_TYPE_PLAN) {
            tv_order_type!!.setText(R.string.sl_str_plan_entrust)
            rg_order_type!!.clearCheck()
            rg_order_type!!.visibility = View.GONE
            tv_price_value!!.visibility = View.VISIBLE
            tv_market_price!!.setText(R.string.sl_str_market_price)
            rl_trigger_price!!.visibility = View.VISIBLE
            iv_setting!!.visibility = View.VISIBLE
            tv_price2market!!.visibility = View.VISIBLE
            tv_price2limit!!.visibility = View.VISIBLE
            tv_market_price!!.gravity = Gravity.CENTER_VERTICAL
            if (updatePrice) {
                et_price!!.setText("")
                et_price!!.setHint(R.string.sl_str_execution_price)
            }
            val triggerType = LogicContractSetting.getTriggerPriceType(context)
            var triggerTypeText = ""
            when (triggerType) {
                0 -> {
                    triggerTypeText = getString(R.string.sl_str_latest_price_simple)
                }
                1 -> {
                    triggerTypeText = getString(R.string.sl_str_fair_price_simple)
                }
                2 -> {
                    triggerTypeText = getString(R.string.sl_str_index_price_simple)
                }
            }
            et_trigger_price!!.hint = getString(R.string.sl_str_trigger_price) + "(" + triggerTypeText + ")"
            val execution = LogicContractSetting.getExecution(context)
            if (execution == 0) {
                rl_limit_price!!.visibility = View.VISIBLE
                rl_market_price!!.visibility = View.GONE
            } else if (execution == 1) {
                rl_limit_price!!.visibility = View.GONE
                rl_market_price!!.visibility = View.VISIBLE
            }
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_TYPE_PLAN)
        } else if (type == LogicBuySell.ORDER_BID_PRICE) {
            tv_order_type!!.setText(R.string.sl_str_limit_entrust)
            tab_buy1!!.isChecked = true
            tv_market_price!!.setText(R.string.sl_str_buy1_price)
            rg_order_type!!.visibility = View.VISIBLE
            tv_price_value!!.visibility = View.GONE
            rl_limit_price!!.visibility = View.GONE
            rl_market_price!!.visibility = View.VISIBLE
            rl_trigger_price!!.visibility = View.GONE
            iv_setting!!.visibility = View.GONE
            tv_price2market!!.visibility = View.GONE
            tv_price2limit!!.visibility = View.GONE
            tv_market_price!!.gravity = Gravity.CENTER
            if (updatePrice) {
                et_price!!.setText(mCurrentPrice)
                et_price!!.setHint(R.string.sl_str_price)
            }
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_BID_PRICE)
        } else if (type == LogicBuySell.ORDER_ASK_PRICE) {
            tv_order_type!!.setText(R.string.sl_str_limit_entrust)
            tab_sell1!!.isChecked = true
            tv_market_price!!.setText(R.string.sl_str_sell1_price)
            rg_order_type!!.visibility = View.VISIBLE
            tv_price_value!!.visibility = View.GONE
            rl_limit_price!!.visibility = View.GONE
            rl_market_price!!.visibility = View.VISIBLE
            rl_trigger_price!!.visibility = View.GONE
            iv_setting!!.visibility = View.GONE
            tv_price2market!!.visibility = View.GONE
            tv_price2limit!!.visibility = View.GONE
            tv_market_price!!.gravity = Gravity.CENTER
            if (updatePrice) {
                et_price!!.setText(mCurrentPrice)
                et_price!!.setHint(R.string.sl_str_price)
            }
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_CONTRACT_ORDER_TYPE, LogicBuySell.ORDER_ASK_PRICE)
        }
    }

    private fun setVolUnit() {
        val contract: Contract = ContractPublicDataAgent.getContract(mContractId) ?: return
        val unit = LogicContractSetting.getContractUint(context)
        tv_volume_unit!!.text = if (unit == 0) getString(R.string.sl_str_contracts_unit) else contract.getBase_coin()
        tv_amount!!.text = getString(R.string.sl_str_amount) + "(" + (if (unit == 0) getString(R.string.sl_str_contracts_unit) else contract.getBase_coin()) + ")"
        updateInfoValue(true)
    }

    private fun setAskBid(type: Int) {
        if (type == LogicBuySell.ASK_BID_DEFAULT) {
            rv_list_sell!!.visibility = View.VISIBLE
            rv_list_buy!!.visibility = View.VISIBLE
            rv_list_sell!!.layoutParams.height = UtilSystem.dip2px(activity, 150f)
            rv_list_buy!!.layoutParams.height = UtilSystem.dip2px(activity, 150f)
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_DEFAULT)
            mABShowNum = 6
        //    updateDepth()
        } else if (type == LogicBuySell.ASK_BID_ASK) {
            rv_list_sell!!.visibility = View.VISIBLE
            rv_list_buy!!.visibility = View.GONE
            rv_list_sell!!.layoutParams.height = UtilSystem.dip2px(activity, 300f)
            rv_list_buy!!.layoutParams.height = UtilSystem.dip2px(activity, 150f)
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_ASK)
            mABShowNum = 12
          //  updateDepth()
        } else if (type == LogicBuySell.ASK_BID_BID) {
            rv_list_sell!!.visibility = View.GONE
            rv_list_buy!!.visibility = View.VISIBLE
            rv_list_sell!!.layoutParams.height = UtilSystem.dip2px(activity, 150f)
            rv_list_buy!!.layoutParams.height = UtilSystem.dip2px(activity, 300f)
            PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_ASKBID, LogicBuySell.ASK_BID_BID)
            mABShowNum = 12
          //  updateDepth()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogicBuySell.getInstance().unregistListener(this)
        LogicContractSetting.getInstance().unregistListener(this)
    }
    override fun onSwitchTab(param: Int, stock_code: String) {}
    override fun onDecimalSelected(decimals: Int) {}
    override fun onOrderTypeSelected(type: Int) {
        setOrderType(type, true)
    }

    override fun onAskBidSelected(type: Int) {
        setAskBid(type)
    }

    override fun onBuySellContractClick(depthData: DepthData, showVol: String, flag: Int) {
        val type = orderType
        if (type == LogicBuySell.ORDER_TYPE_PLAN || mContract==null) {
            return
        }
        val dfPirce = NumberUtil.getDecimal(mContract!!.price_index)
        val price = MathHelper.round(depthData.price)
        et_price!!.setText(dfPirce.format(price))
    }

    override fun onBuySellContractVolClick(depthData: DepthData, showVol: String, flag: Int) {
        val type = orderType
        if (type == LogicBuySell.ORDER_TYPE_PLAN || mContract==null) {
            return
        }
        val dfVol = NumberUtil.getDecimal(mContract!!.vol_index)
        et_volume!!.setText(dfVol.format(depthData.vol))
    }

    override fun onContractSettingChange() {
        setVolUnit()
        setOrderType(orderType, false)
    }

    override fun onLeverageChange(leverage: Int, text: String) {
        mLeverage = leverage
        tv_select_leverage!!.text = text
        updateVol()
        //updateInfoValue(true);
        PreferenceManager.getInstance(activity).putSharedInt(PreferenceManager.PREF_LEVERAGE, mLeverage)
    }

    fun onLogin() {
        if (mContractId == 0) {
            return
        }
        if (mOperationType == 1) {
            if (ContractSDKAgent.isLogin) {
                btn_buy!!.setText(getString(R.string.sl_str_buy_open_long))
                btn_sell!!.setText(getString(R.string.sl_str_sell_open_short))
            } else {
                btn_buy!!.setText(getString(R.string.sl_str_login))
                btn_sell!!.setText(getString(R.string.sl_str_login))
            }
        } else if (mOperationType == 2) {
            if (ContractSDKAgent.isLogin) {
                btn_buy!!.setText(getString(R.string.sl_str_buy_close_short))
                btn_sell!!.setText(getString(R.string.sl_str_sell_close_long))
            } else {
                btn_buy!!.setText(getString(R.string.sl_str_login))
                btn_sell!!.setText(getString(R.string.sl_str_login))
            }
        }
        updateOpenOrder(mContractId)
    }

    fun onLogout() {
        et_volume!!.setText("")
        tv_aavl_value!!.text = "0"
        btn_buy!!.text = getString(R.string.sl_str_login)
        btn_sell!!.text = getString(R.string.sl_str_login)
    }

    companion object {
        var s_unit = -1
    }
}