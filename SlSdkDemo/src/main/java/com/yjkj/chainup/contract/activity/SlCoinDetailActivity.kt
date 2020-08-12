package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.data.ContractTicker
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.impl.ContractPositionListener
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.fragment.SlContractHoldFragment
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.sl_activity_coin_detail.*
import kotlinx.android.synthetic.main.sl_activity_contract_detail.collapsing_toolbar
import kotlinx.android.synthetic.main.sl_activity_contract_detail.toolbar
import java.text.DecimalFormat

/**
 * 合约币种详情
 */
class SlCoinDetailActivity : NBaseActivity(){
    override fun setContentView(): Int {
        return R.layout.sl_activity_coin_detail
    }
    var coinCode = "USDT"
    private  var holdFragment = SlContractHoldFragment()

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        coinCode = intent.getStringExtra("coinCode")
        initView()
        loadData()
        updateAssetHeaderUi()
    }

    override fun initView() {
        initAutoTextView()
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        collapsing_toolbar?.let {
            it.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleColor(ContextCompat.getColor(mActivity, R.color.text_color))
            it.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            it.expandedTitleGravity = Gravity.TOP
            it.title = coinCode
        }

        val transaction = supportFragmentManager!!.beginTransaction()
        transaction.add(R.id.fragment_container,holdFragment,"0000")
        transaction.commitAllowingStateLoss()

        tv_coin_title.text =  coinCode+getLineText("sl_str_position")


        ContractUserDataAgent.registerContractPositionWsListener(this,object : ContractPositionListener(){
            override fun onWsContractPosition(instrumentId:Int?) {
                updateAssetHeaderUi()
            }
        })
    }

    private fun initAutoTextView() {
        tv_account_equity_label.onLineText("contract_assets_account_equity")
        tv_available_label.onLineText("sl_str_wallet_balance")
        tv_margin_balance_label.onLineText("sl_str_margin_balance")
        tv_floating_gains_label.onLineText("sl_tv_floating_gains")
        tv_positions_margin_label.onLineText("contract_text_positionMargin")
        tv_entrust_margin_label.onLineText("contract_text_orderMargin")
    }

    override fun loadData() {
        super.loadData()
        holdFragment.bindCoinCode(coinCode)
        updateAssetHeaderUi()
    }


    private fun updateAssetHeaderUi() {
        val account = ContractUserDataAgent?.getContractAccount(coinCode)
        account?.let {
            val dfDefault: DecimalFormat = NumberUtil.getDecimal(NCoinManager.getCoinShowPrecision(it.coin_code))
            val freezeVol: Double = MathHelper.round(it.freeze_vol)
            val availableVol: Double = MathHelper.round(it.available_vol)

            var longProfitAmount = 0.0 //多仓位的未实现盈亏
            var shortProfitAmount = 0.0 //空仓位的未实现盈亏
            var positionMargin = 0.0
            val contractPositions: List<ContractPosition>? = ContractUserDataAgent.getCoinPositions(it.coin_code,true)

            if (contractPositions != null && contractPositions.isNotEmpty()) {
                for (i in contractPositions.indices){
                    val contractPosition = contractPositions[i]
                    val positionContract = ContractPublicDataAgent.getContract(contractPosition.instrument_id)
                    val contractTicker: ContractTicker? = ContractPublicDataAgent.getContractTicker(contractPosition.instrument_id)
                    if (positionContract == null || contractTicker == null) {
                        continue
                    }
                    positionMargin += MathHelper.round(contractPosition.im)
                    if (contractPosition.side === 1) { //开多
                        longProfitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    } else if (contractPosition.side === 2) { //开空
                        shortProfitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                                contractPosition.cur_qty,
                                contractPosition.avg_cost_px,
                                contractTicker.fair_px,
                                positionContract.face_value,
                                positionContract.isReserve)
                    }
                }
            }
            val balance = MathHelper.add(freezeVol, availableVol)
            val packBalance = MathHelper.add(balance, positionMargin)
            var isShowAssets = UserDataService.getInstance().isShowAssets
            val index = NCoinManager.getCoinShowPrecision(it.coin_code)
            //账户权益
            Utils.assetsHideShow(isShowAssets, tv_balance, dfDefault.format(MathHelper.round(balance + positionMargin + longProfitAmount + shortProfitAmount, index)))
            //钱包余额
            Utils.assetsHideShow(isShowAssets,tv_available_value, dfDefault.format(MathHelper.round(packBalance, index)))
            //保证金余额
            Utils.assetsHideShow(isShowAssets, tv_margin_balance_value, dfDefault.format(MathHelper.round(availableVol, index)))
            //未实现盈亏额
            Utils.assetsHideShow(isShowAssets, tv_floating_gains_value, dfDefault.format(MathHelper.round(longProfitAmount + shortProfitAmount, index)))
            //仓位保证金
            Utils.assetsHideShow(isShowAssets, tv_positions_margin_value, dfDefault.format(MathHelper.round(positionMargin, index)))
            //委托保证金
            Utils.assetsHideShow(isShowAssets, tv_entrust_margin_value, dfDefault.format(MathHelper.round(freezeVol, index)))
        }

    }


    companion object {
        fun show(activity: Activity,coinCode:String) {
            val intent = Intent(activity, SlCoinDetailActivity::class.java)
            intent.putExtra("coinCode",coinCode)
            activity.startActivity(intent)
        }
    }


}