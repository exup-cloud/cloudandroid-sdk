package com.yjkj.chainup.contract.fragment.asset

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.ContractAccount
import com.timmy.tdialog.listener.OnBindViewListener
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.activity.SlContractAssetRecordActivity
import com.yjkj.chainup.contract.adapter.SlContractAssetAdapter
import com.yjkj.chainup.contract.utils.ContractUtils
import com.yjkj.chainup.contract.utils.PreferenceManager
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.contract.widget.SlDialogHelper
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.NewAssetTopView
import com.yjkj.chainup.util.LogUtil
import kotlinx.android.synthetic.main.accet_header_view.view.*
import kotlinx.android.synthetic.main.sl_fragment_contract_asset.*

/**
 * 合约资产类
 */
class SlContractAssetFragment : NBaseFragment() {
    override fun setContentView(): Int {
        return R.layout.sl_fragment_contract_asset
    }

    var adapterHoldContract: SlContractAssetAdapter? = null
    val mList =  ArrayList<ContractAccount>()
    /**
     * 隐藏小额资产
     */
    private var isLittleAssetsShow = false
    var assetHeadView: NewAssetTopView? = null
    //是否展示合约购买对话框
    var isShowContractBuyDialog = false

    override fun initView() {
        isShowContractBuyDialog = PreferenceManager.getBoolean(mActivity,"isShowContractBuyDialog",true)

        assetHeadView = NewAssetTopView(activity!!, null, 0)
        assetHeadView?.initNorMalView(ParamConstant.CONTRACT_INDEX)
        initHoldContractAdapter()
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_trans_type == it?.msg_type) {
                isLittleAssetsShow = !isLittleAssetsShow
                UserDataService.getInstance().saveAssetState(isLittleAssetsShow)
                assetHeadView?.setAssetOrderHide(isLittleAssetsShow)
            }else if(MessageEvent.refresh_local_contract_type == it?.msg_type){
                LogUtil.d("DEBUG","刷新合约资产列表2")
                setRefreshAdapter()
            }
        })
        //划转
        assetHeadView?.ll_transfer_layout?.setOnClickListener {
            val list = ContractUserDataAgent.getContractAccounts()
            if(list == null || list.size == 0){
                showOpenContractDialog()
            }else{
                ArouterUtil.navigation(RoutePath.NewVersionTransferActivity, Bundle().apply {
                    putString(ParamConstant.TRANSFERSTATUS, ParamConstant.TRANSFER_CONTRACT)
                    putString(ParamConstant.TRANSFERSYMBOL, "USDT")
                })
            }
        }
        //资金明细
        assetHeadView?.ll_contract_layout?.setOnClickListener {
           val list = ContractUserDataAgent.getContractAccounts()
            if(list == null || list.size == 0){
                showOpenContractDialog()
            }else{
                SlContractAssetRecordActivity.show(context as Activity)
            }
        }
        //合约赠金
        assetHeadView?.ll_contract_coupon_layout?.setOnClickListener {
            val httpUrl = PublicInfoDataService.getInstance().getContractCouponUrl(null)
            if (!TextUtils.isEmpty(httpUrl)) {
                var bundle = Bundle()
                bundle.putString(ParamConstant.head_title, getLineText("contract_swap_gift"))
                bundle.putString(ParamConstant.web_url, httpUrl)
                ArouterUtil.greenChannel(RoutePath.ItemDetailActivity, bundle)
            }
        }
    }

    override fun fragmentVisibile(isVisibleToUser: Boolean) {
        super.fragmentVisibile(isVisibleToUser)
         if(isVisibleToUser){
             val list = ContractUserDataAgent.getContractAccounts()
             if(list != null && list.size >= 0) {
                 if(isShowContractBuyDialog){
                     isShowContractBuyDialog = false
                     PreferenceManager.putBoolean(mActivity,"isShowContractBuyDialog",isShowContractBuyDialog)
                     val totalBalance = ContractUtils.calculateTotalBalance("USDT")
                     if(totalBalance == 0.0){
                         showBuyContractDialog()
                     }
                 }
             }


         }
    }

    /**
     * 开通合约对话框
     */
    private fun showOpenContractDialog() {
        SlDialogHelper.showSimpleCreateContractDialog(mActivity!!, OnBindViewListener { viewHolder ->
            viewHolder?.let {
                it.getView<TextView>(R.id.tv_cancel_btn).onLineText("common_text_btnCancel")

                it.setImageResource(R.id.iv_logo,R.drawable.sl_create_contract)
                it.setText(R.id.tv_text,getLineText("sl_str_open_contract_tips"))
                it.setText(R.id.tv_confirm_btn,getLineText("sl_str_to_open"))
            }

        },object : NewDialogUtils.DialogBottomListener {
            override fun sendConfirm() {
//                doCreateContractAccount()
                var messageEvent = MessageEvent(MessageEvent.contract_switch_type)
                EventBusUtil.post(messageEvent)
            }

        })
    }
    /**
     * 合约购买对话框
     */
    private fun showBuyContractDialog() {
        SlDialogHelper.showSimpleCreateContractDialog(mActivity!!, OnBindViewListener { viewHolder ->
            viewHolder?.let {
                it.getView<TextView>(R.id.tv_cancel_btn).onLineText("common_text_btnCancel")

                it.setImageResource(R.id.iv_logo,R.drawable.sl_reminders_buy_logo)
                it.setText(R.id.tv_text,getLineText("sl_str_str_open_quick_deposit_tips"))
                it.setText(R.id.tv_confirm_btn,getLineText("sl_str_quick_buy"))
            }
        },object : NewDialogUtils.DialogBottomListener {
            override fun sendConfirm() {
                ArouterUtil.navigation(RoutePath.NewVersionTransferActivity, Bundle().apply {
                    putString(ParamConstant.TRANSFERSTATUS, ParamConstant.TRANSFER_CONTRACT)
                    putString(ParamConstant.TRANSFERSYMBOL, "USDT")
                })
            }

        })
    }


    fun setRefreshAdapter() {
        assetHeadView?.setRefreshAdapter()
        refreshViewData()
    }


    private fun refreshViewData() {
        val contractAccounts: List<ContractAccount>? = ContractUserDataAgent.getContractAccounts()
        mList.clear()
        if(contractAccounts!=null){
            mList.addAll(contractAccounts)
        }
        adapterHoldContract?.notifyDataSetChanged()
        assetHeadView?.setRefreshViewData()
    }



    /**
     * 合约未平仓合约
     */
    private fun initHoldContractAdapter() {
        adapterHoldContract = SlContractAssetAdapter(context!!,mList)
        if (assetHeadView?.parent != null) {
            (assetHeadView?.parent as ViewGroup).removeAllViews()
        }
        adapterHoldContract?.setHeaderView(assetHeadView)

        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapterHoldContract?.bindToRecyclerView(rc_contract ?: return)

        rc_contract?.adapter = adapterHoldContract
        adapterHoldContract?.setOnItemClickListener { adapter, view, position ->

        }
    }

}


