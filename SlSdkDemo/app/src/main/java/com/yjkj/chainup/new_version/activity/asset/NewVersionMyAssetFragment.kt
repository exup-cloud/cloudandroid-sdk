package com.yjkj.chainup.new_version.activity.asset

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.contract.sdk.ContractUserDataAgent
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.fragment.asset.SlContractAssetFragment
import com.yjkj.chainup.contract.utils.ContractUtils
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NVPagerAdapter
import com.yjkj.chainup.new_version.adapter.OTCMyAssetHeatAdapter
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.fragment_new_version_my_asset.*
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @Author lianshangljl
 * @Date 2019/5/14-7:49 PM
 * @Email buptjinlong@163.com
 * @description 我的资产
 *
 * NOTE:币币、法币（B2C）、场外（OTC）、合约、杠杆
 */
class NewVersionMyAssetFragment : NBaseFragment() {

    override fun setContentView() = R.layout.fragment_new_version_my_asset

    val assetlist = ArrayList<JSONObject>()
    var fragments = ArrayList<Fragment>()
    var tabTitles = arrayListOf<String>()


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var contractAssetFragment : SlContractAssetFragment? = null


    /**
     * 场外
     */
    var otcOpen = false

    /**
     * 合约
     */
    var contractOpen = true
    var chooseIndex = 0
    /**
     * b2c
     */
    var b2cOpen = false
    /**
     * 杠杆
     */
    var leverOpen = false

    /**
     * 头部页面
     */
    var adapter4Heat: OTCMyAssetHeatAdapter? = null

    var indexList = ArrayList<String>()

    /*
     *  是否已经登录
     */
    var isLogined = false

    private var isFromAssetsActivity = false
    open fun setFromAssetsActivity(isFromAssetsActivity: Boolean) {
        this.isFromAssetsActivity = isFromAssetsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            chooseIndex = it.getInt(ParamConstant.CHOOSE_INDEX, 0)
        }
    }

    override fun fragmentVisibile(isVisibleToUser: Boolean) {
        super.fragmentVisibile(isVisibleToUser)
        if (isVisibleToUser) {
            isLogined = UserDataService.getInstance().isLogined
            if (isLogined) {
                adapter4Heat?.notifyDataSetChanged()
                setAssetViewVisible()
                getAccountBalance()
            }
        }
    }

    var versionAssetStatus = false
    fun activityRefresh(status: Boolean) {
        versionAssetStatus = status
        var message = MessageEvent(MessageEvent.into_my_asset_activity, versionAssetStatus)
        NLiveDataUtil.postValue(message)
    }


    fun refresh4Homepage() {
        adapter4Heat?.notifyDataSetChanged()
        if (UserDataService.getInstance().isLogined) {
            getAccountBalance()
        }
    }

    var isShowAssets = true
    private fun setAssetViewVisible() {
        isShowAssets = UserDataService.getInstance().isShowAssets
        Utils.showAssetsSwitch(isShowAssets, iv_hide_asset)
    }

    fun setSelectClick() {

        /**
         * 点击隐藏或者显示资金
         */
        iv_hide_asset.setOnClickListener {
            isShowAssets = !isShowAssets
            UserDataService.getInstance().setShowAssetStatus(isShowAssets)
            setAssetViewVisible()

            adapter4Heat?.notifyDataSetChanged()
            for (fragment in fragments) {
                if(fragment is SlContractAssetFragment){
                    contractAssetFragment?.setRefreshAdapter()
                }else{
                    (fragment as NewVersionAssetOptimizeDetailFragment).setRefreshAdapter()
                }
            }
        }
    }

    fun refresh() {
        adapter4Heat?.notifyDataSetChanged()
    }

    override fun initView() {
        setSelectClick()

        contractOpen = PublicInfoDataService.getInstance().contractOpen(null)

        var jsonObject = JSONObject()
        jsonObject.put("title", LanguageUtil.getString(context,"otc_bibi_account"))
        jsonObject.put("totalBalanceSymbol", "BTC")
        jsonObject.put("totalBalance", "0")
        jsonObject.put("balanceType", ParamConstant.BIBI_INDEX)
        assetlist.add(jsonObject)

        if (contractOpen) {
            var jsonObject = JSONObject()
            jsonObject.put("title", LanguageUtil.getString(context,"assets_text_contract"))
            jsonObject.put("totalBalanceSymbol", "USDT")
            jsonObject.put("totalBalance", "0")
            jsonObject.put("balanceType", ParamConstant.CONTRACT_INDEX)
            assetlist.add(jsonObject)
        }


        if (titleStatus) {
            rl_title_layout?.visibility = View.GONE
        }


        tabTitles.add(LanguageUtil.getString(context,"assets_text_exchange"))
        indexList.add(ParamConstant.BIBI_INDEX)

        if (contractOpen) {
            tabTitles.add(LanguageUtil.getString(context,"assets_text_contract"))
            indexList.add(ParamConstant.CONTRACT_INDEX)
        }

        tv_title?.text = tabTitles[0]
        for (i in 0 until tabTitles.size) {
            if(indexList[i] == ParamConstant.CONTRACT_INDEX){
                contractAssetFragment = SlContractAssetFragment()
                fragments.add(contractAssetFragment!!)
                updateContractAccount()
            }else{
                fragments.add(NewVersionAssetOptimizeDetailFragment.newInstance(tabTitles[i], i, indexList[i]))
            }
        }

        adapter4Heat = OTCMyAssetHeatAdapter(assetlist)
        activity_my_asset_rv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        activity_my_asset_rv?.adapter = adapter4Heat
//        var snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(activity_my_asset_rv ?: return)
        activity_my_asset_rv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var adapterNowPos = 0
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var l: LinearLayoutManager = activity_my_asset_rv.layoutManager as LinearLayoutManager
                adapterNowPos = l.findFirstCompletelyVisibleItemPosition()

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                /**new State 一共有三种状态
                 * SCROLL_STATE_IDLE：目前RecyclerView不是滚动，也就是静止
                 * SCROLL_STATE_DRAGGING：RecyclerView目前被外部输入如用户触摸输入。
                 * SCROLL_STATE_SETTLING：RecyclerView目前动画虽然不是在最后一个位置外部控制。
                //这里进行加载更多数据的操作 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    vp_otc_asset?.currentItem = adapterNowPos
                }
            }

        })

        val marketPageAdapter = NVPagerAdapter(childFragmentManager, tabTitles.toMutableList(), fragments)
        vp_otc_asset?.adapter = marketPageAdapter
        vp_otc_asset?.offscreenPageLimit = tabTitles.size
        vp_otc_asset?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewpagePosotion = position
                tv_title?.text = tabTitles[position]
                activity_my_asset_rv?.smoothScrollToPosition(position)
            }
        })

    }

    override fun onMessageEvent(event: MessageEvent) {
        super.onMessageEvent(event)
        if (MessageEvent.assetsTab_type == event.msg_type) {
            val msg_content = event.msg_content
            if (null != msg_content && msg_content is Bundle) {
                val vpPos = msg_content.getInt(ParamConstant.assetTabType)
                setViewPagePosition(vpPos)
            }
        }
    }


    var accountBean: JSONObject = JSONObject()


    var isFristRequest = true

    /**
     * 获取账户信息 bibi
     */
    private fun getAccountBalance() {
        var loadingActivity = activity
        if (!isFristRequest) {
            loadingActivity = null
        }
        addDisposable(getMainModel().accountBalance(object : NDisposableObserver(loadingActivity) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                closeLoadingDialog()
                isFristRequest = false

                var json = jsonObject.optJSONObject("data")
                vp_otc_asset ?: return
                activity_my_asset_rv ?: return
                accountBean = json
                assetlist.get(0).put("totalBalance", json.optString("totalBalance") ?: "")
                assetlist.get(0).put("totalBalanceSymbol", json.optString("totalBalanceSymbol")
                        ?: "")
                vp_otc_asset?.currentItem = viewpagePosotion
                activity_my_asset_rv?.smoothScrollToPosition(viewpagePosotion)
                if (contractOpen) {
                    getContractAccount()
                }

                refresh()

                var message = MessageEvent(MessageEvent.refresh_local_coin_trans_type)
                message.msg_content = json
                NLiveDataUtil.postValue(message)

            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
                isFristRequest = false
            }


        }))
    }

    private fun getContractAccount() {
        ContractUserDataAgent.getContractAccounts(true)
    }

    var titleStatus = false
    var viewpagePosotion = 0

    /**
     * 更新账户信息  合约
     */
    private fun updateContractAccount() {
        val totalBalanceSymbol = "BTC"
        val totalBalance = ContractUtils.calculateTotalBalance(totalBalanceSymbol)
        try{
            assetlist.get(1).put("totalBalance", totalBalance)
            assetlist.get(1).put("totalBalanceSymbol", totalBalanceSymbol)
        }catch (e: Exception){
            e.printStackTrace()
        }

        //刷新header
        refresh()
        //通知列表刷新
        contractAssetFragment?.setRefreshAdapter()
    }



    fun hideTitle(status: Boolean) {
        titleStatus = status
        rl_title_layout?.visibility = View.GONE
    }

    fun setViewPagePosition(position: Int) {
        chooseIndex = position
        viewpagePosotion = position
        vp_otc_asset?.currentItem = viewpagePosotion
    }



}