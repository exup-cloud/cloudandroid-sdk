package com.yjkj.chainup.new_version.activity.asset

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.bean.AssetScreenBean
import com.yjkj.chainup.db.constant.HomeTabMap
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.*
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.OTCAssetAdapter
import com.yjkj.chainup.new_version.adapter.OTCFundAdapter
import com.yjkj.chainup.new_version.contract.ContractFragment
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.NewAssetTopView
import com.yjkj.chainup.treaty.adapter.HoldContractAssterAdapter
import com.yjkj.chainup.util.DecimalUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.StringUtil
import kotlinx.android.synthetic.main.fragment_bibi_asset.*
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_INDEX = "param_index"

/**
 * @Author lianshangljl
 * @Date 2019/6/9-11:25 AM
 * @Email buptjinlong@163.com
 * @description
 */
class NewVersionAssetOptimizeDetailFragment : NBaseFragment() {

    override fun initView() {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param_index = it.getString(ARG_INDEX)
            param2 = it.getInt(ARG_PARAM2)
        }
        when (param_index) {
            ParamConstant.FABI_INDEX -> {
                otcDialogList.addAll(arrayListOf(LanguageUtil.getString(context, "assets_action_transfer"), LanguageUtil.getString(context, "assets_action_transaction")))
            }
            ParamConstant.B2C_INDEX -> {
                otcDialogList.addAll(arrayListOf(LanguageUtil.getString(context, "assets_action_chargeCoin"), LanguageUtil.getString(context, "assets_action_withdraw")))
            }
            ParamConstant.LEVER_INDEX -> {
                otcDialogList.addAll(arrayListOf(LanguageUtil.getString(context, "leverage_borrow"), LanguageUtil.getString(context, "assets_action_transfer")))
            }
        }
        bibiDialogList.addAll(arrayListOf(LanguageUtil.getString(context, "assets_action_chargeCoin"), LanguageUtil.getString(context, "assets_action_transfer"), LanguageUtil.getString(context, "assets_action_transaction"), LanguageUtil.getString(context, "assets_action_withdraw")))

        contractDialogList.add(LanguageUtil.getString(context, "assets_action_transaction"))
        assetHeadView = NewAssetTopView(activity!!, null, 0)
        assetHeadView?.initNorMalView(param_index)
        dataProcessing()
        initViewData()
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_trans_type == it?.msg_type) {
                isLittleAssetsShow = !isLittleAssetsShow
                UserDataService.getInstance().saveAssetState(isLittleAssetsShow)
                hideLittleAssets()
                assetHeadView?.setAssetOrderHide(isLittleAssetsShow)
            }
        })
    }

    override fun setContentView() = R.layout.fragment_bibi_asset

    private var param1: String? = null
    /**
     * bibi 是币币
     * bibao 是币宝
     * fabi 是otc
     */
    private var param_index: String? = null

    private var param2: Int = 0
    /**
     * 隐藏小额资产
     */
    private var isLittleAssetsShow = false
    var assetHeadView: NewAssetTopView? = null

    var fragments = ArrayList<Fragment>(2)
    var bibiDialogList = arrayListOf<String>()
    var otcDialogList = arrayListOf<String>()
    var contractDialogList = arrayListOf<String>()


    companion object {
        var liveDataFilterForEditText: MutableLiveData<AssetScreenBean> = MutableLiveData()
        var liveDataCleanForEditText: MutableLiveData<String> = MutableLiveData()


        @JvmStatic
        fun newInstance(param1: String, param2: Int, index: String) =
                NewVersionAssetOptimizeDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_INDEX, index)
                        putInt(ARG_PARAM2, param2)
                    }
                }
    }


    var isFrist = true


    fun dataProcessing() {

        /**
         * 清除筛选
         */
        liveDataCleanForEditText.observe(this, Observer<String> {
            if (it == param_index) {
                clearData()
            }
        })

        /**
         * 筛选
         */
        liveDataFilterForEditText.observe(this, Observer<AssetScreenBean> {
            when (it?.index4Asset) {
                ParamConstant.BIBI_INDEX -> {
                    adapter4Fund?.filter?.filter(it?.textContent)
                }
                ParamConstant.FABI_INDEX -> {
                    adapter4Asset?.filter?.filter(it?.textContent)
                }
                ParamConstant.B2C_INDEX -> {
                    adapter4Asset?.filter?.filter(it?.textContent)
                }
                ParamConstant.LEVER_INDEX -> {
                    adapter4Asset?.filter?.filter(it?.textContent)
                }
            }
        })


        /**
         * 场外数据处理
         */

        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_local_trans_type == it?.msg_type && param_index == ParamConstant.FABI_INDEX) {
                if (null != it?.msg_content) {
                    var jsonObject = it.msg_content as JSONObject
                    val allCoinMap = jsonObject?.optJSONArray("allCoinMap")
                    balanceList4OTC.clear()
                    nolittleBalanceList4OTC.clear()

                    for (item in 0 until (allCoinMap?.length() ?: 0)) {
                        var json = allCoinMap?.optJSONObject(item)
                        if (json != null) {
                            balanceList4OTC.add(json)
                            if (json.optString("btcValuation") != null && json.optDouble("btcValuation") > 0.0001) {
                                nolittleBalanceList4OTC.add(json)
                            }
                        }

                    }

                    list4OTC.clear()
                    if (isLittleAssetsShow) {
                        list4OTC.addAll(nolittleBalanceList4OTC)
                    } else {
                        list4OTC.addAll(balanceList4OTC)
                    }
                    if (isFrist) {
                        initOTCView()
                    } else {
                        adapter4Asset?.notifyDataSetChanged()
                    }
                }
            }
        })

        /**
         * 杠杆接收数据
         */
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_local_lever_type == it?.msg_type && param_index == ParamConstant.LEVER_INDEX) {
                //TODO  这里 先按照法币的来写，后期需要该数据
                if (null != it?.msg_content) {
                    var jsonObject = it.msg_content as JSONObject

                    var leverList = NCoinManager.getLeverMapList(jsonObject)
                    leverList.sortBy { it?.optInt("sort") }
                    balanceList4OTC.clear()
                    nolittleBalanceList4OTC.clear()


                    leverList?.forEach {
                        if (null != it) {
                            balanceList4OTC.add(it)
                            if (it.optString("symbolBalance") != null && it.optDouble("symbolBalance") > 0.0001) {
                                nolittleBalanceList4OTC.add(it)
                            }
                        }
                    }

                    list4OTC.clear()
                    if (isLittleAssetsShow) {
                        list4OTC.addAll(nolittleBalanceList4OTC)
                    } else {
                        list4OTC.addAll(balanceList4OTC)
                    }
                    if (isFrist) {
                        initLever()
                    } else {
                        adapter4Asset?.notifyDataSetChanged()
                    }
                }
            }
        })


        /**
         * b2c
         */
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_local_b2c_coin_trans_type == it?.msg_type && param_index == ParamConstant.B2C_INDEX) {
                if (null != it?.msg_content) {
                    var jsonObject = it.msg_content as JSONObject
                    val allCoinMap = jsonObject?.optJSONArray("allCoinMap")

                    balanceList4OTC.clear()
                    nolittleBalanceList4OTC.clear()

                    for (item in 0 until (allCoinMap?.length() ?: 0)) {
                        var json = allCoinMap?.optJSONObject(item)
                        if (json != null) {
                            balanceList4OTC.add(json)
                            if (json.optString("btcValue") != null && json.optDouble("btcValue") > 0.0001) {
                                nolittleBalanceList4OTC.add(json)
                            }
                        }
                    }


                    list4OTC.clear()
                    if (isLittleAssetsShow) {
                        list4OTC.addAll(nolittleBalanceList4OTC)
                    } else {
                        list4OTC.addAll(balanceList4OTC)
                    }
                    if (isFrist) {
                        initB2CView()
                    } else {
                        adapter4Asset?.notifyDataSetChanged()
                    }
                }

            }
        })


        /**
         * 币币数据 详情数据
         */
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_local_coin_trans_type == it?.msg_type && param_index == ParamConstant.BIBI_INDEX) {
                if (null != it?.msg_content) {
                    var jsonObject = it.msg_content as JSONObject
                    var json = jsonObject?.optJSONObject("allCoinMap")
                    var keys: Iterator<String> = json?.keys() as Iterator<String>
                    balancelist.clear()
                    nolittleBalanceList.clear()


                    while (keys.hasNext()) {
                        var coinMap = json?.optJSONObject(keys?.next())
                        balancelist.add(coinMap ?: JSONObject())
                        if (null != coinMap?.optString("allBtcValuatin")) {
                            if (coinMap?.optDouble("allBtcValuatin") > 0.0001) {
                                nolittleBalanceList.add(coinMap ?: JSONObject())
                            }
                        }
                    }
                    balancelist = DecimalUtil.sortByMultiOptions(balancelist, option2 = "coinName")
                    nolittleBalanceList = DecimalUtil.sortByMultiOptions(nolittleBalanceList, option2 = "coinName")

                    listFund.clear()
                    if (isLittleAssetsShow) {
                        listFund.addAll(nolittleBalanceList)
                    } else {
                        listFund.addAll(balancelist)
                    }
                    adapter4Fund?.notifyDataSetChanged()

                }
            }
        })
        intoRefreshTransferView()
    }


    fun intoRefreshTransferView() {
        assetHeadView?.listener = object : NewAssetTopView.selecetTransferListener {

            override fun selectWithdrawal(temp: String) {
                /**
                 * 提币选择
                 */
                if (temp == param_index) {
                    balancelist.forEach { it ->
                        if (it?.optInt("withdrawOpen") == 1) {
                            if (phoneCertification()) return
                            if (PublicInfoDataService.getInstance().withdrawKycOpen && UserDataService.getInstance().authLevel != 1) {
                                NewDialogUtils.KycSecurityDialog(context!!, context?.getString(R.string.common_kyc_chargeAndwithdraw)
                                        ?: "", object : NewDialogUtils.DialogBottomListener {
                                    override fun sendConfirm() {
                                        when (UserDataService.getInstance().authLevel) {
                                            0 -> {
                                                NToastUtil.showTopToast(false, context?.getString(R.string.noun_login_pending))
                                            }

                                            2, 3 -> {
                                                ArouterUtil.greenChannel(RoutePath.RealNameCertificationActivity, null)
                                            }
                                        }
                                    }
                                })
                                return
                            }

                            ArouterUtil.navigation(RoutePath.SelectCoinActivity, Bundle().apply {
                                putInt(ParamConstant.OPTION_TYPE, ParamConstant.WITHDRAW)
                                putBoolean(ParamConstant.COIN_FROM, true)
                            })
                            return
                        }
                    }
                    NToastUtil.showTopToast(false, LanguageUtil.getString(context,"withdraw_tip_notavailable"))
                }
            }

            override fun selectRedEnvelope(temp: String) {
                /**
                 * 点击红包
                 */
                if (param_index == temp) {
                    if (PublicInfoDataService.getInstance().isEnforceGoogleAuth(null)) {
                        if (UserDataService.getInstance().authLevel != 1 || UserDataService.getInstance().googleStatus != 1) {
                            NewDialogUtils.redPackageCondition(context ?: return)
                        }
                    } else {
                        if (UserDataService.getInstance().authLevel != 1 || (UserDataService.getInstance().googleStatus != 1 && UserDataService.getInstance().isOpenMobileCheck != 1)) {
                            NewDialogUtils.redPackageCondition(context ?: return)
                            return
                        }
                    }
                    ArouterUtil.navigation(RoutePath.CreateRedPackageActivity, null)
                }
            }

            override fun leverageFilter(temp: String) {
                adapter4Asset?.filter?.filter(temp)
            }

            override fun fiatFilter(temp: String) {
                adapter4Asset?.filter?.filter(temp)
            }

            override fun bibiFilter(temp: String) {
                adapter4Fund?.filter?.filter(temp)
            }

            override fun b2cFilter(temp: String) {
                adapter4Asset?.filter?.filter(temp)
            }

            override fun selectTransfer(param: String) {
                if (param == param_index) {
                    when (param) {
                        ParamConstant.BIBI_INDEX -> {
                            if (PublicInfoDataService.getInstance().otcOpen(null)) {
                                var list = DataManager.getCoinsFromDB(true)
                                if (list.size == 0) {
                                    NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_not_open_transfer"))
                                    return
                                }
                                list.sortBy { it.sort }
                                ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_BIBI, list[0].name)

                            } else if (PublicInfoDataService.getInstance().contractOpen(null)) {
                                ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_CONTRACT, "BTC")
                                return
                            }
                        }
                        ParamConstant.FABI_INDEX -> {
                            if (balanceList4OTC.size <= 0) {
                                return
                            }
                            ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_OTC, balanceList4OTC[0].optString("coinSymbol"))

                        }
                        ParamConstant.CONTRACT_INDEX -> {
                            ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_CONTRACT, "BTC")
                        }
                        ParamConstant.LEVER_INDEX -> {
                            ArouterUtil.navigation(RoutePath.CoinMapActivity, Bundle().apply {
                                putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER, true)
                                putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER_UNREFRESH, true)
                                putBoolean(ParamConstant.SEARCH_COIN_MAP_FOR_LEVER_INTO_TRANSFER, true)
                            })
                        }
                    }
                }
            }

        }
    }


    var symbol = ""


    fun clearData() {
        when (param_index) {
            ParamConstant.BIBI_INDEX -> {
                if (isLittleAssetsShow) {
                    listFund.clear()
                    listFund.addAll(nolittleBalanceList)
                    adapter4Fund?.notifyDataSetChanged()
                } else {
                    listFund.clear()
                    listFund.addAll(balancelist)
                    adapter4Fund?.notifyDataSetChanged()
                }
            }
            ParamConstant.FABI_INDEX, ParamConstant.B2C_INDEX, ParamConstant.LEVER_INDEX -> {
                if (isLittleAssetsShow) {
                    list4OTC.clear()
                    list4OTC.addAll(nolittleBalanceList4OTC)
                    adapter4Asset?.notifyDataSetChanged()
                } else {
                    list4OTC.clear()
                    list4OTC.addAll(balanceList4OTC)
                    adapter4Asset?.notifyDataSetChanged()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        assetHeadView?.clearEdittext()
    }

    fun setRefreshAdapter() {
        assetHeadView?.setRefreshAdapter()
        refreshViewData()
    }

    fun hideLittleAssets() {
        if (isLittleAssetsShow) {
            //币币账户清理数据
            listFund.clear()
            listFund.addAll(nolittleBalanceList)
            adapter4Fund?.notifyDataSetChanged()

            //法币账户清理数据
            list4OTC.clear()
            list4OTC.addAll(nolittleBalanceList4OTC)
            adapter4Asset?.notifyDataSetChanged()
        } else {
            listFund.clear()
            listFund.addAll(balancelist)
            adapter4Fund?.notifyDataSetChanged()

            list4OTC.clear()
            list4OTC.addAll(balanceList4OTC)
            adapter4Asset?.notifyDataSetChanged()

        }

        adapter4Fund?.setListener(object : OTCFundAdapter.FilterListener {
            override fun getFilterData(list: List<JSONObject>) {
                if (list == null) return
                listFund.clear()
                listFund.addAll(list)
                adapter4Fund?.notifyDataSetChanged()

            }
        })
        adapter4Asset?.setListener(object : OTCAssetAdapter.FilterListener {
            override fun getFilterData(list: ArrayList<JSONObject>) {
                if (list == null) return
                list4OTC.clear()
                list4OTC.addAll(list)
                adapter4Asset?.notifyDataSetChanged()
            }
        })

    }


    fun refreshViewData() {
        isLittleAssetsShow = UserDataService.getInstance().getAssetState()
        adapter4Asset?.notifyDataSetChanged()
        adapter4Fund?.notifyDataSetChanged()
        adapterHoldContract?.notifyDataSetChanged()
        assetHeadView?.setRefreshViewData()
        when (param_index) {
            ParamConstant.CONTRACT_INDEX -> {
                getContractAccount()
                holdContractList4Contract()
            }
        }
    }


    fun initViewData() {
        isLittleAssetsShow = UserDataService.getInstance().getAssetState()
        nolittleBalanceList4OTC.clear()
        balanceList4OTC.clear()
        nolittleBalanceList.clear()
        balancelist.clear()
        list4OTC.clear()
        listFund.clear()
        if (UserDataService.getInstance().isLogined) {
            when (param_index) {
                ParamConstant.BIBI_INDEX -> {
                    initBiBiView()
                }
                ParamConstant.FABI_INDEX -> {
                    initOTCView()
                }
                ParamConstant.B2C_INDEX -> {
                    initB2CView()
                }
                ParamConstant.LEVER_INDEX -> {
                    initLever()
                }
                ParamConstant.CONTRACT_INDEX -> {
                    getContractAccount()
                    holdContractList4Contract()
                }

            }
        }
    }


    var tDialog: TDialog? = null

    var adapter4Asset: OTCAssetAdapter? = null
    var adapter4Fund: OTCFundAdapter? = null
    var adapterHoldContract: HoldContractAssterAdapter? = null
    var nolittleBalanceList4OTC = arrayListOf<JSONObject>()
    var balanceList4OTC = arrayListOf<JSONObject>()
    var nolittleBalanceList = arrayListOf<JSONObject>()
    var balancelist = arrayListOf<JSONObject>()
    var list4OTC = arrayListOf<JSONObject>()
    var listFund = arrayListOf<JSONObject>()


    /**
     * 法币交易adapter
     */
    fun initOTCView() {
        if (activity?.isFinishing ?: return || activity?.isDestroyed ?: return || !isAdded) {
            return
        }

        isFrist = false
        if (isLittleAssetsShow) {
            list4OTC.addAll(nolittleBalanceList4OTC)
        } else {
            list4OTC.addAll(balanceList4OTC)
        }

        adapter4Asset = OTCAssetAdapter(list4OTC)
        var parent = assetHeadView?.parent
        if (null != parent) {
            (parent as ViewGroup).removeAllViews()
        }
        adapter4Asset?.setType(ParamConstant.FABI_INDEX)
        adapter4Asset?.setHeaderView(assetHeadView)
        adapter4Asset?.setHasStableIds(true)
        if (rc_contract == null) return
        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapter4Asset?.bindToRecyclerView(rc_contract ?: return)

        rc_contract?.adapter = adapter4Asset
        rc_contract?.itemAnimator = null

        adapter4Asset?.setOnItemClickListener { adapter, view, position ->

            tDialog = NewDialogUtils.showBottomListDialog(context!!, otcDialogList, 0, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    //越界
                    if (position >= list4OTC.size) {
                        tDialog?.dismiss()
                        adapter4Asset?.notifyDataSetChanged()
                        return
                    }
                    when (item) {
                        /**
                         * 划转
                         */
                        0 -> {

                            var aa: String? = ""
                            if (null != list4OTC[position]?.optString("coinSymbol")) {
                                aa = NCoinManager.getShowMarket(list4OTC[position]?.optString("coinSymbol"))
                            }
                            ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_OTC, aa)

                        }
                        /**
                         * 交易
                         */
                        1 -> {
                            setEvent(ParamConstant.TYPE_FAIT, list4OTC[position].optString("coinSymbol"))
                            assetsActivityFinish()
                        }
                    }
                    tDialog?.dismiss()
                }
            })
        }

        adapter4Asset?.setListener(object : OTCAssetAdapter.FilterListener {
            override fun getFilterData(list: ArrayList<JSONObject>) {
                if (list == null) return
                list4OTC.clear()
                list4OTC.addAll(list)
                adapter4Asset?.notifyDataSetChanged()
            }
        })
    }

    /**
     * lever交易
     */
    fun initLever() {
        if (activity?.isFinishing ?: return || activity?.isDestroyed ?: return || !isAdded) {
            return
        }
        isFrist = false
        if (isLittleAssetsShow) {
            list4OTC.addAll(nolittleBalanceList4OTC)
        } else {
            list4OTC.addAll(balanceList4OTC)
        }
        adapter4Asset = OTCAssetAdapter(list4OTC)
        adapter4Asset?.setType(ParamConstant.LEVER_INDEX)
        adapter4Asset?.setHeaderView(assetHeadView)
        adapter4Asset?.setHasStableIds(true)
        if (rc_contract == null) return
        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapter4Asset?.bindToRecyclerView(rc_contract ?: return)
//        adapter4Asset?.setEmptyView(R.layout.item_new_empty)
        rc_contract?.adapter = adapter4Asset
        rc_contract?.itemAnimator = null
        adapter4Asset?.setOnItemClickListener { adapter, view, position ->


            ArouterUtil.navigation(RoutePath.CurrencyLendingRecordsActivity, Bundle().apply {
                putString(ParamConstant.symbol, list4OTC[position]?.optString("symbol", "")
                        ?: "")
            })
        }

        adapter4Asset?.setListener(object : OTCAssetAdapter.FilterListener {
            override fun getFilterData(list: ArrayList<JSONObject>) {
                if (list == null) return
                list4OTC.clear()
                list4OTC.addAll(list)
                adapter4Asset?.notifyDataSetChanged()
            }
        })
    }


    /**
     * B2C交易adapter
     */
    fun initB2CView() {
        if (activity?.isFinishing ?: return || activity?.isDestroyed ?: return || !isAdded) {
            return
        }
        isFrist = false
        if (isLittleAssetsShow) {
            list4OTC.addAll(nolittleBalanceList4OTC)
        } else {
            list4OTC.addAll(balanceList4OTC)
        }
        adapter4Asset = OTCAssetAdapter(list4OTC)
        adapter4Asset?.setType(ParamConstant.B2C_INDEX)
        adapter4Asset?.setHeaderView(assetHeadView)
        adapter4Asset?.setHasStableIds(true)
        if (rc_contract == null) return
        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapter4Asset?.bindToRecyclerView(rc_contract ?: return)
//        adapter4Asset?.setEmptyView(R.layout.item_new_empty)
        rc_contract?.adapter = adapter4Asset
        rc_contract?.itemAnimator = null





        adapter4Asset?.setOnItemClickListener { adapter, view, position ->

            otcDialogList.clear()
            if (list4OTC[position]?.optInt("depositOpen") == 1) {
                otcDialogList.add(LanguageUtil.getString(context, "assets_action_chargeCoin"))
            }

            if (list4OTC[position]?.optInt("withdrawOpen") == 1) {
                otcDialogList.add(LanguageUtil.getString(context, "assets_action_withdraw"))
            }
            tDialog = NewDialogUtils.showBottomListDialog(context!!, otcDialogList, 0, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    when (data[item]) {
                        /**
                         * 充币
                         */
                        LanguageUtil.getString(context, "assets_action_chargeCoin") -> {
                            if (list4OTC[position].optInt("depositOpen") == 1) {
                                /* 这里b2c充币 */
                                PublicInfoDataService.getInstance().saveCoinInfo4B2C(list4OTC[position]?.optString("symbol"))
                                ArouterUtil.navigation(RoutePath.B2CRechargeActivity, null)
                            } else {
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "warn_no_support_recharge"))
                            }
                        }
                        /**
                         * 提币
                         */
                        LanguageUtil.getString(context, "assets_action_withdraw") -> {
                            if (list4OTC[position]?.optInt("withdrawOpen") == 1) {
                                if (PublicInfoDataService.getInstance().isEnforceGoogleAuth(null)) {
                                    if (UserDataService.getInstance().googleStatus != 1) {
                                        NewDialogUtils.OTCTradingMustPermissionsDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                                            override fun sendConfirm() {
                                                if (UserDataService.getInstance().nickName.isEmpty()) {
                                                    //认证状态 0、审核中，1、通过，2、未通过  3未认证
                                                    ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                                                } else if (UserDataService.getInstance().authLevel != 1) {
                                                    when (UserDataService.getInstance().authLevel) {
                                                        0 -> {
                                                            ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                                                        }

                                                        2, 3 -> {
                                                            ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                                        }
                                                    }
                                                } else {
                                                    ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)

                                                }

                                            }
                                        }, type = 2, title = LanguageUtil.getString(context, "withdraw_tip_bindGoogleFirst"))
                                        return
                                    }
                                } else {
                                    if (UserDataService.getInstance().isOpenMobileCheck != 1 && UserDataService.getInstance().googleStatus != 1) {
                                        NewDialogUtils.OTCTradingPermissionsDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                                            override fun sendConfirm() {
                                                if (UserDataService.getInstance().nickName.isEmpty()) {
                                                    //认证状态 0、审核中，1、通过，2、未通过  3未认证
                                                    ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                                                } else if (UserDataService.getInstance().authLevel != 1) {
                                                    when (UserDataService.getInstance().authLevel) {
                                                        0 -> {
                                                            ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                                                        }

                                                        2, 3 -> {
                                                            ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                                        }
                                                    }
                                                } else {
                                                    ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)

                                                }
                                            }

                                        }, type = 2, title = LanguageUtil.getString(context, "otcSafeAlert_action_bindphoneOrGoogle"))
                                        return
                                    }
                                }
                                /* 这里b2c提币 */
                                PublicInfoDataService.getInstance().saveCoinInfo4B2C(list4OTC[position]?.optString("symbol"))
                                ArouterUtil.navigation(RoutePath.B2CWithdrawActivity, null)
                            } else {
                                //DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,warn_no_support_withdraw), isSuc = false)
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "warn_no_support_withdraw"))
                            }
                        }
                    }
                    tDialog?.dismiss()
                }
            })
        }

        adapter4Asset?.setListener(object : OTCAssetAdapter.FilterListener {
            override fun getFilterData(list: ArrayList<JSONObject>) {
                if (list == null) return
                list4OTC.clear()
                list4OTC.addAll(list)
                adapter4Asset?.notifyDataSetChanged()
            }
        })
    }


    fun setEvent(position: Int, content: String) {
        var message = MessageEvent(MessageEvent.coin_payment)
        var jsonObject = JSONObject()
        jsonObject.put("position", position)
        jsonObject.put("content", content)
        message.msg_content = jsonObject
        EventBusUtil.post(message)
    }

    /**
     * 币币交易 item
     */
    fun initBiBiView() {

        if (isLittleAssetsShow) {
            listFund.addAll(nolittleBalanceList)
        } else {
            listFund.addAll(balancelist)
        }
        adapter4Fund = OTCFundAdapter(data = listFund)
        var parent = assetHeadView?.parent
        if (null != parent) {
            (parent as ViewGroup).removeAllViews()
        }
        adapter4Fund?.setHeaderView(assetHeadView)
        adapter4Fund?.setHasStableIds(true)
        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapter4Fund?.bindToRecyclerView(rc_contract ?: return)

        rc_contract?.adapter = adapter4Fund
        rc_contract?.itemAnimator = null

        adapter4Fund?.setOnItemClickListener { adapter, view, position ->
            bibiDialogList = arrayListOf(LanguageUtil.getString(context, "assets_action_chargeCoin"), LanguageUtil.getString(context, "assets_action_transfer"), LanguageUtil.getString(context, "assets_action_transaction"), LanguageUtil.getString(context, "otc_withdraw"))

            var coin = PublicInfoDataService.getInstance().getCoinByName(listFund[position].optString("coinName", ""))
            var existMarket: String? = null
            if (null != listFund[position].optString("exchange_symbol", "")) {
                existMarket = NCoinManager.returnExistMarket(listFund[position].optString("exchange_symbol", ""))
            }


            bibiDialogList.clear()
            if (listFund[position]?.optInt("withdrawOpen") == 1) {
                bibiDialogList.add(LanguageUtil.getString(context, "assets_action_withdraw"))
            }
            if (coin?.optInt("otcOpen") == 1) {
                bibiDialogList.add(LanguageUtil.getString(context, "assets_action_transfer"))
            }
            if (null != existMarket && existMarket?.isNotEmpty()!!) {
                bibiDialogList.add(LanguageUtil.getString(context, "assets_action_transaction"))
            }
            if (listFund[position]?.optInt("depositOpen") == 1) {
                bibiDialogList.add(LanguageUtil.getString(context, "assets_action_chargeCoin"))
            }


            tDialog = NewDialogUtils.showBottomListDialog(context!!, bibiDialogList, 0, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    //当列表最后一个资产被卖出或者提币成功时，弹出操作弹框已经显示，再去操作此数据引起数组越界
                    if (position >= listFund.size) {
                        tDialog?.dismiss()
                        adapter4Fund?.notifyDataSetChanged()
                        return
                    }
                    when (data[item]) {
                        /**
                         * 充币
                         */
                        LanguageUtil.getString(context, "assets_action_chargeCoin") -> {
                            if (listFund[position]?.optInt("depositOpen") == 1) {
                                if (PublicInfoDataService.getInstance().depositeKycOpen && UserDataService.getInstance().authLevel != 1) {
                                    NewDialogUtils.KycSecurityDialog(context!!, context?.getString(R.string.common_kyc_chargeAndwithdraw)
                                            ?: "", object : NewDialogUtils.DialogBottomListener {
                                        override fun sendConfirm() {
                                            when (UserDataService.getInstance().authLevel) {
                                                0 -> {
                                                    NToastUtil.showTopToast(false, context?.getString(R.string.noun_login_pending))
                                                }

                                                2, 3 -> {
                                                    ArouterUtil.greenChannel(RoutePath.RealNameCertificationActivity, null)
                                                }
                                            }
                                        }
                                    })
                                    return
                                }
                                DepositActivity.enter2(context!!, listFund[position].optString("coinName", ""))
                            } else {
                                //DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,warn_no_support_recharge), isSuc = false)
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "warn_no_support_recharge"))
                            }
                        }
                        /**
                         * 提币
                         */
                        LanguageUtil.getString(context, "assets_action_withdraw") -> {
                            if (listFund[position]?.optInt("withdrawOpen") == 1) {
                                if (phoneCertification()) return
                                if (PublicInfoDataService.getInstance().withdrawKycOpen && UserDataService.getInstance().authLevel != 1) {
                                    NewDialogUtils.KycSecurityDialog(context!!, context?.getString(R.string.common_kyc_chargeAndwithdraw)
                                            ?: "", object : NewDialogUtils.DialogBottomListener {
                                        override fun sendConfirm() {
                                            when (UserDataService.getInstance().authLevel) {
                                                0 -> {
                                                    NToastUtil.showTopToast(false, context?.getString(R.string.noun_login_pending))
                                                }

                                                2, 3 -> {
                                                    ArouterUtil.greenChannel(RoutePath.RealNameCertificationActivity, null)
                                                }
                                            }
                                        }
                                    })
                                    return
                                }
                            } else {
                                //DisplayUtil.showSnackBar(activity?.window?.decorView, LanguageUtil.getString(context,warn_no_support_withdraw), isSuc = false)
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "warn_no_support_withdraw"))
                            }
                        }
                        /**
                         * 划转
                         */
                        LanguageUtil.getString(context, "assets_action_transfer") -> {
                            if (PublicInfoDataService.getInstance().otcOpen(null)) {
                                var coin = PublicInfoDataService.getInstance().getCoinByName(listFund[position].optString("coinName", ""))
                                if (coin != null && coin.optInt("otcOpen") == 1) {
                                    ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_BIBI, listFund[position].optString("coinName", ""))

                                } else {
                                    NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_not_open_transfer"))

                                }
                            } else if (PublicInfoDataService.getInstance().contractOpen(null)) {
                                ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_CONTRACT, "BTC")

                            } else {
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_not_open_transfer"))
                            }


                        }
                        /**
                         * 交易
                         */
                        LanguageUtil.getString(context, "assets_action_transaction") -> {
                            var existMarket: String? = ""
                            if (null != listFund[position].optString("exchange_symbol")) {
                                existMarket = NCoinManager.returnExistMarket(listFund[position].optString("exchange_symbol", "")!!)

                            }

                            if (!StringUtil.checkStr(existMarket)) {
                                NToastUtil.showTopToast(false, LanguageUtil.getString(context, "warn_no_support_trade"))
                                return
                            }

                            setEvent(ParamConstant.TYPE_COIN, "TradingBean")
                            SymbolManager.instance.saveTradeSymbol(existMarket, 0)


                            var messageEvent = MessageEvent(MessageEvent.symbol_switch_type)
                            messageEvent.msg_content = existMarket
                            messageEvent.isLever = false
                            EventBusUtil.post(messageEvent)


                            forwardCoinTradeTab(existMarket)
                            assetsActivityFinish()
                        }
                    }
                    tDialog?.dismiss()
                }
            })

        }
        adapter4Fund?.setListener(object : OTCFundAdapter.FilterListener {
            override fun getFilterData(list: List<JSONObject>) {
                if (list == null) return
                listFund.clear()
                listFund.addAll(list)
                adapter4Fund?.notifyDataSetChanged()
            }
        })
    }

    /*
     * 跳转到币币tab
     */
    private fun forwardCoinTradeTab(symbol: String?) {
        var messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
        val bundle = Bundle()
        val homeTabType = HomeTabMap.maps.get(HomeTabMap.coinTradeTab) ?: 2
        bundle.putInt(ParamConstant.homeTabType, homeTabType)//跳转到币币交易页面
        bundle.putInt(ParamConstant.transferType, ParamConstant.TYPE_BUY)
        bundle.putString(ParamConstant.symbol, symbol)
        messageEvent.msg_content = bundle
        EventBusUtil.post(messageEvent)
    }


    var contractList: ArrayList<JSONObject> = arrayListOf()
    var isFristContract = true
    /**
     * 合约未平仓合约
     */
    fun initHoldContractAdapter(list: ArrayList<JSONObject>) {
        contractList.clear()
        contractList.addAll(list)
        adapterHoldContract = HoldContractAssterAdapter(contractList)
        if (assetHeadView?.parent != null) {
            (assetHeadView?.parent as ViewGroup)?.removeAllViews()
        }
        adapterHoldContract?.setHeaderView(assetHeadView)

        rc_contract?.layoutManager = LinearLayoutManager(context)
        adapterHoldContract?.bindToRecyclerView(rc_contract ?: return)

        rc_contract?.adapter = adapterHoldContract
        adapterHoldContract?.setOnItemClickListener { adapter, view, position ->

            tDialog = NewDialogUtils.showBottomListDialog(context!!, contractDialogList, 0, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    /*NewMainActivity.liveData4Position.postValue(
                            if (PublicInfoDataService.getInstance().otcOpen(null)) {
                                4
                            } else {
                                3
                            }
                    )*/
                    hometab_switch()

                    Contract2PublicInfoManager.currentContractId(contractList[position]?.optInt("contractId"), true)
                    ContractFragment.liveData4Contract.postValue(Contract2PublicInfoManager.currentContract())

                    tDialog?.dismiss()
                }
            })
        }
    }

    private fun hometab_switch() {
        /*var messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
        if (PublicInfoDataService.getInstance().otcOpen(null)) {
            messageEvent.msg_content = 4
        } else {
            messageEvent.msg_content = 3
        }
        NLiveDataUtil.postValue(messageEvent)*/

        var messageEvent = MessageEvent(MessageEvent.hometab_switch_type)
        var bundle = Bundle()
        val homeTabType = HomeTabMap.maps.get(HomeTabMap.contractTab) ?: 3
        bundle.putInt(ParamConstant.homeTabType, homeTabType)
        //NLiveDataUtil.postValue(messageEvent)
        messageEvent.msg_content = bundle
        EventBusUtil.post(messageEvent)

    }

    fun refreshContractAdapter(list: ArrayList<JSONObject>) {
        contractList.clear()
        contractList.addAll(list)
        adapterHoldContract?.notifyDataSetChanged()
    }


    /**
     * 获取账户信息  合约
     */
    private fun getContractAccount() {

        addDisposable(getMainModel().getAccountBalance4Contract(object : NDisposableObserver(activity) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var jsonarray = jsonObject.optJSONArray("data")
                assetHeadView?.initAdapterView(jsonarray)
            }

        }))
    }

    fun setContractBean(symbol4Contract: JSONObject) {
        NLiveDataUtil.observeData(this, Observer {
            if (MessageEvent.refresh_local_contract_type == it?.msg_type) {
                if (null != it?.msg_content) {
                    var json = it?.msg_content as JSONObject
                    assetHeadView?.symbol4Contract = json
                    setRefreshAdapter()
                }
            }

        })
    }

    var isFristRequest = true

    /**
     * 获取账户信息未平仓  合约
     */
    private fun holdContractList4Contract() {
        if (isFristRequest) {
            showLoadingDialog()
        }
        addDisposable(getContractModel().holdContractList4Contract(object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                if (isFristRequest) {
                    isFristRequest = false
                    closeLoadingDialog()
                }
                var json = jsonObject.optJSONArray("data")
                if (null == json || json.length() == 0) {
                    initHoldContractAdapter(arrayListOf())
                    return
                }
                var obj: ArrayList<JSONObject> = ArrayList()
                for (num in 0 until json.length()) {
                    obj.add(json.optJSONObject(num))
                }

                if (isFristContract) {
                    initHoldContractAdapter(obj)
                    isFristContract = false
                } else {
                    refreshContractAdapter(obj)
                }
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
                if (isFristRequest) {
                    isFristRequest = false
                    closeLoadingDialog()
                }
            }
        }))
    }


    fun phoneCertification(type: Int = 2): Boolean {
        if (PublicInfoDataService.getInstance().isEnforceGoogleAuth(null)) {
            if (UserDataService.getInstance().googleStatus != 1) {
                NewDialogUtils.OTCTradingMustPermissionsDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().googleStatus != 1) {
                            ArouterUtil.greenChannel(RoutePath.SafetySettingActivity, null)
                            return
                        }

                        if (UserDataService.getInstance().nickName.isEmpty()) {
                            //认证状态 0、审核中，1、通过，2、未通过  3未认证
                            ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                            return
                        }

                        if (UserDataService.getInstance().authLevel != 1) {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                                }

                                2, 3 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                }
                            }
                            return
                        }

                    }
                }, type = type, title = LanguageUtil.getString(context, "withdraw_tip_bindGoogleFirst"))
                return true
            }
        } else {
            if (UserDataService.getInstance().isOpenMobileCheck != 1 && UserDataService.getInstance().googleStatus != 1) {
                NewDialogUtils.OTCTradingPermissionsDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().googleStatus != 1) {
                            ArouterUtil.greenChannel(RoutePath.SafetySettingActivity, null)
                            return
                        }

                        if (UserDataService.getInstance().nickName.isEmpty()) {
                            //认证状态 0、审核中，1、通过，2、未通过  3未认证
                            //.enter2(context!!)
                            ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                            return
                        }
                        if (UserDataService.getInstance().authLevel != 1) {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                                }

                                2, 3 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                }
                            }
                            return
                        }

                    }

                }, type = 2, title = LanguageUtil.getString(context, "otcSafeAlert_action_bindphoneOrGoogle"))
                return true
            }
        }

        return false
    }

    /*
      * 通知资产页面finish
      */
    private fun assetsActivityFinish() {
        var msgEvent = MessageEvent(MessageEvent.assets_activity_finish_event)
        EventBusUtil.post(msgEvent)
    }


}