package com.bmtc.sdk.contract.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bmtc.sdk.contract.*
import com.bmtc.sdk.contract.base.BaseFragment
import com.bmtc.sdk.contract.dialog.DropContractMenuWindow
import com.bmtc.sdk.contract.dialog.DropContractWindow
import com.bmtc.sdk.contract.dialog.PromptWindowWide
import com.bmtc.sdk.contract.utils.ToastUtil
import com.bmtc.sdk.contract.utils.UtilSystem
import com.bmtc.sdk.contract.view.scrollview.internal.LogUtils
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.ContractSDKAgent.isLogin
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.*
import com.contract.sdk.extra.sacn.ScanTask
import com.contract.sdk.impl.ContractDepthListener
import com.contract.sdk.impl.ContractNetListener
import com.contract.sdk.impl.ContractTickerListener
import com.contract.sdk.impl.IResponse
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.contract.sdk.utils.SDKLogUtil
import com.contract.sdk.ws.LogicWebSocketContract
import kotlinx.android.synthetic.main.sl_fragment_trade_contract.*
import kotlinx.android.synthetic.main.sl_include_trade_contract_bar.*
import kotlinx.android.synthetic.main.sl_include_trade_contract_bar.iv_menu
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * 合约交易
 * Created by zj on 2018/3/1.
 */
class TradeContractFragment : BaseFragment(), View.OnClickListener {
    private var mFragmentManager: FragmentManager? = null
    private var mBuySellContractFragment: BuySellContractFragment? = null
    private var mContractId = 0
    private var mContract : Contract?=null
    private var mRotate: Animation? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sl_fragment_trade_contract, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        /**
         * 监听Ticker
         */
        ContractPublicDataAgent.registerTickerWsListener(this,object:ContractTickerListener(){
            override fun onWsContractTicker(ticker: ContractTicker) {
                if(ticker.instrument_id == mContractId){
                    updateView(ticker)
                    mBuySellContractFragment?.updateContract(ticker,false)
                }
            }
        })

        /**
         * 监听深度
         */
        ContractPublicDataAgent.registerDepthWsListener(this,6,object : ContractDepthListener(){
            /**
             * 买盘深度更新  降序排列 10 9 8 ...
             */
            override fun onWsContractBuyDepth(contractId: Int, buyList: ArrayList<DepthData>) {
              //  SDKLogUtil.v("libin","买盘onWsContractBuyDepth:${buyList.size}")
                if(contractId == mContractId){
                    mBuySellContractFragment?.updateBuyDepth(buyList)
                }
            }
            /**
             * 卖盘深度更新   升序排列 0 1 2 3...
             */
            override fun onWsContractSellDepth(contractId: Int?, sellList: ArrayList<DepthData>) {
                //SDKLogUtil.v("libin","卖盘onWsContractBuyDepth:${sellList.size}")
                if(contractId == mContractId){
                    mBuySellContractFragment?.updateSellDepth(sellList.reversed())
                }
            }

        })

        ContractSDKAgent.registerNetListener(this,object:ContractNetListener(){
            /**
             * 网络链接成功
             */
            override fun onNetConnected() {
                rl_no_network!!.visibility = View.GONE
            }

            /**
             * 断开网络链接
             */
            override fun onNetLost() {
                rl_no_network!!.visibility = View.VISIBLE
            }

        })

        onContractBasicChanged()
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            if (mContractId == 0) {
                onContractBasicChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ContractPublicDataAgent.unSubscribeDepthWs(mContractId)

    }

    private val isForeground: Boolean
        private get() = activity != null && isAdded && !hidden


    private fun initViews() {
        rl_no_network.setOnClickListener(View.OnClickListener { startActivity(Intent("android.settings.WIFI_SETTINGS")) })
        tab_open_position?.setOnClickListener(View.OnClickListener { switchBuySell(1) })
        tab_close_position.setOnClickListener(View.OnClickListener { switchBuySell(2) })
        if (mBuySellContractFragment == null) {
            mBuySellContractFragment = BuySellContractFragment()
        }
        mRotate = AnimationUtils.loadAnimation(activity, R.anim.array_rotate)
        mRotate?.setInterpolator(LinearInterpolator())
        tv_stock_code.setOnClickListener(View.OnClickListener { showSpotWindow() })
        tv_stock_type.setOnClickListener(View.OnClickListener { showSpotWindow() })
        iv_sel_stock_code.setOnClickListener(View.OnClickListener { showSpotWindow() })
        rl_rate.setOnClickListener(View.OnClickListener {
            if (mContractId > 0) {
                val intent = Intent()
                intent.putExtra("contract_id", mContractId)
                intent.setClass(activity, ContractTickerOneActivity::class.java)
                startActivity(intent)
            }
        })
        iv_menu.setOnClickListener(View.OnClickListener { showMenuWindow() })
        mFragmentManager = childFragmentManager
        vp_list.adapter = SampleFragmentPagerAdapter(mFragmentManager)
        vp_list.offscreenPageLimit = 1
        vp_list.currentItem = 0
    }

    fun switchBuySell(type: Int) {
        if (mBuySellContractFragment != null) {
            mBuySellContractFragment!!.gotoTop()
            mBuySellContractFragment!!.switchBuySell(type, tab_open_position, tab_close_position)
        }
    }

    private fun changeContractId(contractId: Int) {
        mContractId = contractId
        mContract = ContractPublicDataAgent.getContract(contractId)
        if (mContractId > 0) {
            ContractPublicDataAgent.subscribeTickerWs(contractId)
            ContractPublicDataAgent.subscribeDepthWs(mContractId)
        }
    }

    fun updateStock(contractId: Int) {
        changeContractId(contractId)
        updateData(contractId, true)
    }

    private fun updateData(contractId: Int, updatePrice: Boolean) {
        if (contractId <= 0) {
            return
        }
        updateSpot(contractId, updatePrice)
        createContractAccount(contractId)
        if (updatePrice) {
            if (mBuySellContractFragment != null) {
                mBuySellContractFragment!!.updateOpenOrder(contractId)
            }
        }
    }

    private var mCreateAccount = false
    private fun createContractAccount(contractId: Int) {
        if (isForeground && ContractSDKAgent.isLogin && !mCreateAccount) {
             mContract ?: return
            val contractAccount: ContractAccount? = ContractUserDataAgent.getContractAccount(mContract!!.margin_coin)
            if (contractAccount == null) {
                mCreateAccount = true
                val title = String.format(getString(R.string.sl_str_open_contract_account), mContract!!.margin_coin)
                val window = PromptWindowWide(activity)
                window.showTitle(title)
                window.showTvContent(getString(R.string.sl_str_risk_disclosure_notice))
                window.showBtnOk(getString(R.string.sl_str_open_contract_account_btn))
                window.showAtLocation(vp_list, Gravity.CENTER, 0, 0)
                window.setOnDismissListener { mCreateAccount = false }
                window.btnOk.setOnClickListener {
                    window.dismiss()
                    mCreateAccount = false
                    ContractUserDataAgent.doCreateContractAccount(contractId, object : IResponse<String>() {
                        override fun onSuccess(data: String) {
                            ToastUtil.shortToast(context, getString(R.string.sl_str_account_created_successfully))
                        }

                        override fun onFail(code: String, msg: String) {
                            ToastUtil.shortToast(context, msg)
                        }
                    })
                }
            }
        }
    }

    private fun updateSpot(contractId: Int, updatePrice: Boolean) {
        val ticker: ContractTicker? = ContractPublicDataAgent.getContractTicker(contractId)
        if (activity != null && isAdded && ticker != null) {
            if (mBuySellContractFragment != null) {
                mBuySellContractFragment!!.updateContract(ticker, updatePrice)
            }
            updateView(ticker)
        }
    }


    private fun updateView(data: ContractTicker) {
        mContract?: return
        var name = mContract!!.symbol
        if (name.contains("[")) {
            name = name.substring(0, name.indexOf("["))
        }
        tv_stock_code!!.text = name
        if (mContract!!.area == Contract.CONTRACT_BLOCK_USDT) {
            tv_stock_type!!.text = "USDT"
        } else if (mContract!!.area == Contract.CONTRACT_BLOCK_SIMULATION) {
            tv_stock_type!!.setText(R.string.sl_str_simulation)
        } else if (mContract!!.area == Contract.CONTRACT_BLOCK_INNOVATION || mContract!!.area == Contract.CONTRACT_BLOCK_MAIN) {
            tv_stock_type!!.setText(R.string.sl_str_inverse)
        }
        val decimalFormat = DecimalFormat("###################.###########", DecimalFormatSymbols(Locale.ENGLISH))
        val riseFallRate = MathHelper.round(data.change_rate.toDouble() * 100, 2)
        val rise_fall_value = MathHelper.round(data.change_value, 2)
        val sRate = if (riseFallRate >= 0) "+" + decimalFormat.format(riseFallRate) + "%" else decimalFormat.format(riseFallRate) + "%"
        val color: Int = if (riseFallRate >= 0) resources.getColor(R.color.sl_colorGreen) else resources.getColor(R.color.sl_colorRed)
        val current_usd = MathHelper.round(data.last_px, mContract!!.price_index)
        val text = NumberUtil.getDecimal(mContract!!.price_index - 1).format(current_usd)
        //        text += LogicLanguage.isZhEnv(getActivity()) ? sCNY : sUsd;
//        text += " " + sRate;
        tv_stock_price!!.setTextColor(color)
        tv_stock_price!!.text = text
        rl_rate!!.setBackgroundResource(if (riseFallRate >= 0) R.drawable.sl_bg_corner_green_small else R.drawable.sl_bg_corner_red_small)
        tv_stock_rate!!.text = sRate
    }

    override fun onClick(view: View) {
        when (view.id) {
            else -> {
            }
        }
    }

    fun doSwitchPage(index: Int) {
        if (mBuySellContractFragment != null) {
            mBuySellContractFragment!!.switchBuySell(index + 1, tab_open_position, tab_close_position)
        }
    }

    fun onContractBasicChanged() {
        if(mContractId > 0){
            return
        }
        val contractBasics: List<Contract> = ContractPublicDataAgent.getContracts()
        if (contractBasics == null || contractBasics.isEmpty()) {
            return
        }
        mContract =  contractBasics[0]
        mContractId =mContract!!.instrument_id
        changeContractId(mContractId)
        updateData(mContractId, true)
    }

    inner class SampleFragmentPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        private val mFragments: MutableList<Fragment>? = ArrayList()
        override fun getItem(i: Int): Fragment {
            return mFragments!![i]
        }

        override fun getCount(): Int {
            return mFragments?.size ?: 0
        }

        init {
            mFragments!!.add(mBuySellContractFragment!!)
        }
    }

    private fun showSpotWindow() {
        val window = DropContractWindow(activity)
        window.isFocusable = true
        window.showAsDropDown(rl_title, 0, 2)
        window.setOnContractDropClick { contractId ->
            if (window != null) {
                window.dismiss()
                changeContractId(contractId)
                updateData(contractId, true)
            }
        }
    }

    private fun showMenuWindow() {
        val window = DropContractMenuWindow(activity)
        window.isFocusable = true
        window.showAsDropDown(iv_menu, -2, 2)
        window.setOnContractMenuClick { tab ->
            if (window != null) {
                window.dismiss()
                if (tab == 0) { //交易记录
                    if (!isLogin) {
                       ToastUtil.shortToast(activity,"未登录")
                    } else {
                        val intent = Intent(activity, ContractOrderActivity::class.java)
                        intent.putExtra("contractId", mContractId)
                        startActivity(intent)
                    }
                } else if (tab == 1) { //合约设置
                    val intent = Intent(activity, ContractSettingActivity::class.java)
                    startActivity(intent)
                } else if (tab == 2) {
                    val intent = Intent(activity, HtmlActivity::class.java)
//                    if (LogicLanguage.isZhEnv(activity)) {
//                        intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE)
//                    } else {
//                        intent.putExtra("url", BTConstants.BTURL_CONTRACT_GUIDE)
//                    }
                    intent.putExtra("title", getString(R.string.sl_str_contract_guide))
                    startActivity(intent)
                } else if (tab == 3) {
                    val intent = Intent(activity, ContractCalculateActivity::class.java)
                    intent.putExtra("contractId", mContractId)
                    startActivity(intent)
                } else if (tab == 4) {
//                        Intent intent = new Intent(getActivity(), SwitchLineActivity.class);
//                        startActivity(intent);
                }
            }
        }
    }
}