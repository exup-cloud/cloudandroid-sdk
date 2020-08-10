package com.yjkj.chainup.new_version.contract


import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.widget.RadioButton
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.NLanguageUtil
import com.yjkj.chainup.wedegit.CoinContractDialogFg
import kotlinx.android.synthetic.main.fragment_contract.*
import org.jetbrains.anko.textColor


/**
 * @author Bertking
 * @description 合约4.0
 * @date 2019-4-28
 *                     DisplayUtil.showSnackBar(activity?.window?.decorView, "交易")
 */
class ContractFragment : NBaseFragment() {
    override fun setContentView() = R.layout.fragment_contract
    private var isHoldPosition = false
    lateinit var currentContract: ContractBean
    fun isCurrentContractInitialzed() = ::currentContract.isInitialized



    companion object {
        @JvmField
        val liveData4Contract = MutableLiveData<ContractBean>()

        @JvmField
        val liveData4ClosePrice = MutableLiveData<HashMap<String, Double>>()
    }



    override fun initView() {
        rb_trade?.text  = LanguageUtil.getString(context,"assets_action_transaction")
        rb_hold_position?.text  = LanguageUtil.getString(context,"contract_action_holdMargin")

        observeData()
        currentContract = Contract2PublicInfoManager.currentContract() ?: return

        //observeData()
        /**
         * 合约类型 + 交割时间
         */
        if (isCurrentContractInitialzed()){
            tv_contract?.text = currentContract.baseSymbol + currentContract.quoteSymbol + " " + Contract2PublicInfoManager.getContractType(context!!,currentContract.contractType, currentContract.settleTime)

        }

        liveData4Contract.observe(this, Observer<ContractBean> {
            tv_contract?.text = it?.baseSymbol + it?.quoteSymbol + " " + Contract2PublicInfoManager.getContractType(context!!,it?.contractType, it?.settleTime)
            tv_close_price?.text = "--"
            Log.d(TAG, "======LiveData:${it?.symbol}========")
            Contract2PublicInfoManager.currentContractId(it?.id, isSave = true)
        })


        /**
         * 进入K线界面
         */
        ib_kline?.setOnClickListener {
            ContractMarketDetailActivity.enter2(context!!)
            ContractMarketDetailActivity.liveData4Contract.postValue(Contract2PublicInfoManager.currentContract())
        }


        /**
         * 切换合约
         */
        ll_contract?.setOnClickListener {

            showLeftCoin()

        }


        childFragmentManager.beginTransaction()
                ?.add(R.id.fragment_container, NContractTradeFragment(), NContractTradeFragment::class.java.simpleName)
                ?.commitAllowingStateLoss()//添加targetFragment

        /**
         * 切换 买入 or 卖出
         */
        rg_buy_sell?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_trade -> {
                    childFragmentManager.beginTransaction()
                            ?.add(R.id.fragment_container, NContractTradeFragment(), NContractTradeFragment::class.java.simpleName)
                            ?.commitAllowingStateLoss()//添加targetFragment
                }

                R.id.rb_hold_position -> {
                    isHoldPosition = true
                    if (LoginManager.checkLogin(context!!, true)) {
                        isHoldPosition = false
                        childFragmentManager.beginTransaction()?.replace(R.id.fragment_container, NPositionFragment(), NPositionFragment::class.java.simpleName)?.commitAllowingStateLoss()//添加targetFragment
                    }
                }
            }
        }


        liveData4ClosePrice.observeForever {
            tv_close_price?.text = it?.keys?.first()!!
            tv_close_price?.textColor = ColorUtil.getMainColorType(it.values?.first() >= 0)
        }
    }

    private fun observeData(){
        NLiveDataUtil.observeData(this,Observer<MessageEvent>{
            if(MessageEvent.left_coin_contract_type == it?.msg_type){
                var msgContent = it.msg_content;
                if(null!=msgContent && msgContent is ContractBean){

                    var contractBean = msgContent as ContractBean

                    tv_contract?.text = contractBean?.baseSymbol + contractBean?.quoteSymbol + " " + Contract2PublicInfoManager.getContractType(context!!,contractBean?.contractType, contractBean?.settleTime)
                    tv_close_price?.text = "--"
                    Log.d(TAG, "======LiveData:${contractBean?.symbol}========")
                    Contract2PublicInfoManager.currentContractId(contractBean?.id, isSave = true)
                    NLiveDataUtil.removeObservers()
                }
            }
        })
    }

    /*
     * 侧边栏展示
     */
    var mCoinContractDialogFg : CoinContractDialogFg?=null
    private fun showLeftCoin() {
        mCoinContractDialogFg?:CoinContractDialogFg().showDialog(childFragmentManager,"ContractFragment")
    }


    override fun onResume() {
        super.onResume()
        if (isHoldPosition) {

            isHoldPosition = false
            (rg_buy_sell?.getChildAt(0) as RadioButton).isChecked = false
            (rg_buy_sell?.getChildAt(1) as RadioButton).isChecked = true
            childFragmentManager.beginTransaction()?.replace(R.id.fragment_container, NPositionFragment(), NPositionFragment::class.java.simpleName)?.commitAllowingStateLoss()//添加targetFragment

            if (LoginManager.checkLogin(context!!, false)) {
//                isHoldPosition = false
//                (rg_buy_sell?.getChildAt(0) as RadioButton).isChecked = false
//                (rg_buy_sell?.getChildAt(1) as RadioButton).isChecked = true
//                childFragmentManager.beginTransaction()?.replace(R.id.fragment_container, NPositionFragment(), NPositionFragment::class.java.simpleName)?.commitAllowingStateLoss()//添加targetFragment
//
            } else {
                (rg_buy_sell?.getChildAt(0) as RadioButton).isChecked = true
                (rg_buy_sell?.getChildAt(1) as RadioButton).isChecked = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        liveData4ClosePrice.removeObservers(this)
    }





}
