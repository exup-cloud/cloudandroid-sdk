package com.yjkj.chainup.new_version.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.follow.order.FollowOrderSDK
import com.gcssloop.widget.PagerGridLayoutManager
import com.gcssloop.widget.PagerGridSnapHelper
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.db.constant.*
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.*
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.manager.SymbolManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net_new.JSONUtil
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.new_version.adapter.NVPagerAdapter
import com.yjkj.chainup.new_version.adapter.NewHomePageServiceAdapter
import com.yjkj.chainup.new_version.adapter.NewhomepageTradeListAdapter
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.GlideImageLoader
import com.yjkj.chainup.util.*
import com.yjkj.chainup.wedegit.VerticalTextview4ChainUp
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_new_version_homepage.*
import kotlinx.android.synthetic.main.item_home_page_asset_view.*
import kotlinx.android.synthetic.main.item_new_version_homepage_unlogin.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

/**
 * @Author lianshangljl
 * @Date 2019/5/5-2:54 PM
 * @Email buptjinlong@163.com
 * @description 首页
 */
class NewVersionHomepageFragment : NBaseFragment(), MarketWsData.RefreshWSListener {

    val getTopDataReqType = 1 // 首页顶部行情数据请求
    val homepageReqType = 2 // 首页数据请求
    val accountBalanceReqType = 5 //总账户资产请求

    /**
     * 是否开启场外
     */
    private var otcOpen = false

    private var leverOpen = false
    /**
     * 是否开启合约
     */
    private var contractOpen = false

    private var isAssetsShow = true

    var contractTotal: Double = 0.0
    var contractReturn = false
    var totalBalance: String = "0"
    var totalBalanceSymbol = "BTC"

    /**
     * 功能服务
     */
    private var serviceAdapter: NewHomePageServiceAdapter? = null


    var accountLogo = 0
    var defaultBanner = 0
    var defaultHome = 0


    /*
     *  是否已经登录
     */
    var isLogined = false

    override fun setContentView() = R.layout.fragment_new_version_homepage

    override fun initView() {

        otcOpen = PublicInfoDataService.getInstance().otcOpen(null)
        leverOpen = PublicInfoDataService.getInstance().isLeverOpen(null)
        contractOpen = PublicInfoDataService.getInstance().contractOpen(null)


        setTopBar()
        setOnClick()

        accountLogo = R.drawable.home_personal

        LogUtil.d(TAG, "切换语言==NewVersionHomepageFragment==")

        when (ApiConstants.HOME_PAGE_STYLE) {
            ParamConstant.DEFAULT_HOME_PAGE -> {
                accountLogo = R.drawable.home_personal
                defaultBanner = R.drawable.banner_king
                defaultHome = R.drawable.home
                iv_home_page?.setBackgroundResource(defaultHome)


            }
            ParamConstant.INTERNATIONAL_HOME_PAGE -> {
                accountLogo = R.drawable.home_personal_king
                defaultBanner = R.drawable.banner_king
                defaultHome = R.drawable.home_king
                iv_home_page?.setBackgroundResource(defaultHome)

                var linearParams = iv_home_page?.layoutParams
                linearParams?.width = SizeUtils.dp2px(142f)
                linearParams?.height = SizeUtils.dp2px(110f)
                iv_home_page?.layoutParams = linearParams
            }

        }


        iv_personal_logo?.setImageResource(accountLogo)
        setTextContext()
    }


    fun setTextContext() {
        tv_my_asset?.text = LanguageUtil.getString(context, "home_text_assets")
        tv_after_login?.text = LanguageUtil.getString(context, "home_action_notLogin")
        btn_login?.text = LanguageUtil.getString(context, "login_action_login")
        tv_title?.text = LanguageUtil.getString(context, "home_text_assets")
        tv_total_asset?.text = LanguageUtil.getString(context, "assets_text_total")
    }


    inner class MyNDisposableObserver(type: Int) : NDisposableObserver() {

        var req_type = type
        override fun onResponseSuccess(jsonObject: JSONObject) {
            closeLoadingDialog()
            if (getTopDataReqType == req_type) {
                recycler_top_24?.visibility = View.VISIBLE
                v_top_line?.visibility = View.VISIBLE
                showTopSymbolsData(jsonObject.optJSONArray("data"))
            } else if (homepageReqType == req_type) {
                showHomepageData(jsonObject.optJSONObject("data"))
            } else if (accountBalanceReqType == req_type) {
                showAccountBalance(jsonObject.optJSONObject("data"))
            }
        }

        override fun onResponseFailure(code: Int, msg: String?) {
            super.onResponseFailure(code, msg)
            if (getTopDataReqType == req_type) {
                recycler_top_24?.visibility = View.GONE
                v_top_line?.visibility = View.GONE
            }
            closeLoadingDialog()
        }
    }


    var homepageData: JSONObject? = null
    /*
     * 首页数据展示
     */
    private fun showHomepageData(data: JSONObject?) {
        LogUtil.d("NewVersionHomepageFragment", "showHomepageData==data is $data")
        if (null == data)
            return
        homepageData = data
        var noticeInfo = data.optJSONObject("noticeInfo")
        var noticeInfoList = data.optJSONArray("noticeInfoList")
        var cmsAppAdvertList = data.optJSONArray("cmsAppAdvertList")
        var cmsAppDataList = data.optJSONArray("cmsAppDataList")

        LogUtil.d("NewVersionHomepageFragment", "showHomepageData==cmsAppAdvertList is $cmsAppAdvertList")
        showGuanggao(noticeInfoList)
        setServiceData(cmsAppDataList)
        showBottomVp(data)

    }


    /*
     * 首页账户币币数据展示
     */
    private fun showAccountBalance(data: JSONObject?) {
        if (null == data)
            return

        totalBalance = data.optString("totalBalance", "")


        accountBalance = "0"
        if (PublicInfoDataService.getInstance().contractOpen(null)) {
            AccountContractBalance()
        } else {
            AccountBalance()
        }

    }

    fun AccountBalance() {
        accountFlat = RateManager.getCNYByCoinName("BTC", totalBalance)
        if (StringUtil.checkStr(totalBalance)) {
            accountBalance = BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(totalBalance, 8).toString())
        }

        setAssetViewVisible()

    }

    fun AccountContractBalance() {
        if (!StringUtil.checkStr(totalBalance) || !StringUtil.isDoubleNum(totalBalance) || !contractReturn) {
            AccountBalance()
            return
        }

        var temp = BigDecimalUtils.add(totalBalance, contractTotal.toString()).toPlainString()

        accountFlat = RateManager.getCNYByCoinName("BTC", temp)
        if (StringUtil.checkStr(totalBalance)) {
            accountBalance = BigDecimalUtils.showSNormal(BigDecimalUtils.divForDown(temp, 8).toString())
        }

        setAssetViewVisible()
    }


    private fun showAdvertising(isShow: Boolean) {
        if (null != vtc_advertising?.textList && vtc_advertising?.textList!!.size > 0) {
            if (isShow) {
                vtc_advertising?.startAutoScroll()
            } else {
                vtc_advertising?.stopAutoScroll()
            }
        }
    }

    private fun getAllAccounts() {
        isLogined = UserDataService.getInstance().isLogined

        if (null == homepageData) {
            getHomepageData()
        }
        setAssetViewVisible()

        if (isLogined) {
            if (PublicInfoDataService.getInstance().getAppIndexAssetsOpen(null)) {
                ic_unlogin_layout?.visibility = View.GONE
                ic_login_layout?.visibility = View.GONE
            } else {
                tv_total_asset?.text = LanguageUtil.getString(context, "assets_text_total")
                ic_unlogin_layout?.visibility = View.GONE
                ic_login_layout?.visibility = View.VISIBLE
                getAccountBalance()
            }
        } else {
            ic_unlogin_layout?.visibility = View.VISIBLE
            ic_login_layout?.visibility = View.GONE
        }
    }


    fun setTopBar() {
        var logo_black = ""
        var logo_white = ""
        iv_market_logo.visibility = View.GONE
        var app_logo_list_new = PublicInfoDataService.getInstance().getApp_logo_list_new(null)
        if (null != app_logo_list_new && app_logo_list_new.size > 0) {
            logo_black = app_logo_list_new[0]
            logo_white = app_logo_list_new[1]
            LogUtil.d(TAG, "logo_black is $logo_black,logo_white is $logo_white")
            if (StringUtil.isHttpUrl(logo_black)) {
                LogUtil.d(TAG, "logo_black is $logo_black")
                iv_market_logo.visibility = View.VISIBLE
                GlideUtils.loadImageHeader(mActivity, logo_black, iv_market_logo)
            }
        }

        ns_layout?.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->

            val distance = resources.getDimension(R.dimen.dp_64)
            // 1f 为不透明，0f 为完全透明，
            Log.d(TAG, "========alpha:${scrollY / distance}=====")

            var themeMode = PublicInfoDataService.getInstance().themeMode
            if ((1 - scrollY / distance) < (0.0001)) {
                if (PublicInfoDataService.THEME_MODE_DAYTIME == themeMode) {
                    if (StringUtil.isHttpUrl(logo_white)) {
                        GlideUtils.loadImageHeader(mActivity, logo_white, iv_market_logo)
                    }
                } else {
                    if (StringUtil.isHttpUrl(logo_black)) {
                        GlideUtils.loadImageHeader(mActivity, logo_black, iv_market_logo)
                    }
                }
                iv_personal_logo?.setImageResource(accountLogo)
            } else {
                if (PublicInfoDataService.THEME_MODE_DAYTIME == themeMode) {
                    if (StringUtil.isHttpUrl(logo_black)) {
                        GlideUtils.loadImageHeader(mActivity, logo_black, iv_market_logo)
                    }
                } else {
                    if (StringUtil.isHttpUrl(logo_black)) {
                        GlideUtils.loadImageHeader(mActivity, logo_black, iv_market_logo)
                    }
                }
                iv_personal_logo?.setImageResource(accountLogo)
            }
            rl_top_layout_new?.alpha = scrollY / distance - 1
        }

    }

    /*
     * 资产tab跳转
     */
    private fun homeAssetstab_switch(type: Int) {
        var msgEvent = MessageEvent(MessageEvent.hometab_switch_type)
        var bundle = Bundle()
        var homeTabType = HomeTabMap.maps.get(HomeTabMap.assetsTab) ?: 4
        bundle.putInt(ParamConstant.homeTabType, homeTabType)
        bundle.putInt(ParamConstant.assetTabType, type)
        msgEvent.msg_content = bundle
        EventBusUtil.post(msgEvent)
    }

    /*
     * 首页底部tab跳转的处理
     */
    private fun homeTabSwitch(tabType: Int?) {

    }

    /*
     * 跳转至 NewVersionMyAssetActivity
     */
    private fun forwardAssetsActivity(type: Int) {
        var bundle = Bundle()
        bundle.putInt(ParamConstant.assetTabType, type)//跳转到币币交易页面
        ArouterUtil.navigation(RoutePath.NewVersionMyAssetActivity, bundle)
    }


    fun setOnClick() {

        /**
         * 隐藏或者显示资产
         */
        iv_hide_asset?.setOnClickListener {
            isAssetsShow = !isAssetsShow
            UserDataService.getInstance().setShowAssetStatus(isAssetsShow)
            setAssetViewVisible()
        }

        /**
         * 个人中心
         */
        iv_personal_logo?.setOnClickListener {
            ArouterUtil.navigation(RoutePath.PersonalCenterActivity, null)
        }

        /**
         * 登录
         */
        btn_login?.setOnClickListener {
            LoginManager.checkLogin(activity, true)
        }

        /**
         * 币币账户
         */
        rl_into_asset?.setOnClickListener {

            if (contractOpen && otcOpen) {
                forwardAssetsActivity(0)
            } else {
                homeAssetstab_switch(AssetsEnum.COIN_ACCOUNT.value)
            }
        }





        if (SystemUtils.isZh()) {
            rl_red_envelope_entrance.setImageResource(R.drawable.redenvelope)
        } else {
            rl_red_envelope_entrance.setImageResource(R.drawable.redenvelope_english)
        }


    }



    /*
     * 首页顶部symbol 24小时行情展示
     */
    var selectTopSymbol: ArrayList<JSONObject>? = null

    private fun showTopSymbolsData(topSymbol: JSONArray?) {

    }


    private var mMarketWsData: MarketWsData? = null
    private fun initSocket() {
        if (null == mMarketWsData)
            mMarketWsData = MarketWsData()

        mMarketWsData!!.initSocket(selectTopSymbol, this)
    }

    override fun onRefreshWS(pos: Int) {

    }

    override fun fragmentVisibile(isVisible: Boolean) {
        super.fragmentVisibile(isVisible)
        var mainActivity = activity
        if (mainActivity != null) {
            if (mainActivity is NewMainActivity) {
                if (isVisible && mainActivity.curPosition == 0) {
                    changeFragmentVisible()
                    getAllAccounts()
                    initSocket()
                    showAdvertising(true)
                    setAssetViewVisible()
                }
            }
        }

    }


    /**
     * 是否显示24小时行情
     */
    fun setTopViewVisible(isShow: Boolean) {
        if (isShow) {
            recycler_top_24?.visibility = View.VISIBLE
            v_top_line?.visibility = View.VISIBLE
        } else {
            recycler_top_24?.visibility = View.GONE
            v_top_line?.visibility = View.GONE
        }
    }

    /**
     * 获取顶部symbol 24小时行情
     */
    fun getTopData() {

    }

    override fun refreshOkhttp(position: Int) {
        if (position == 0) {
            getTopData()
        }
    }

    fun showBottomVp(data: JSONObject) {

    }

    fun changeFragmentVisible() {

    }


    /**
     * 设置资金显示
     */

    fun setAssetViewVisible() {
        if (!isLogined) {
            return
        }
        isAssetsShow = UserDataService.getInstance().isShowAssets

        Utils.showAssetsSwitch(isAssetsShow, iv_hide_asset)
        //币币
        Utils.assetsHideShow(isAssetsShow, tv_total_content, accountBalance)
        Utils.assetsVisible(isAssetsShow, tv_total_coin)
        Utils.assetsHideShow(isAssetsShow, tv_equivalent_content, accountFlat)


    }


    /**
     * 广告根据数据加载
     * 有数据显示，无数据隐藏
     */
    private fun showGuanggao(noticeInfoList: JSONArray?) {

    }

    private fun forwardWeb(jsonObject: JSONObject?) {

    }


    private fun forUdeskWebView() {

    }

    /**
     * 获取 首页数据
     */
    private fun getHomepageData() {
        showLoadingDialog()
        var disposable = getMainModel().common_index(MyNDisposableObserver(homepageReqType))
        addDisposable(disposable!!)
    }

    /**
     * 如果服务器没有返回服务数据
     * 服务功能这里整体GONE
     */
    private fun setServiceView() {
        recycler_center_service_layout?.visibility = View.GONE
        view_login_line?.visibility = View.GONE
    }

    private var servicePageSize = 0

    /**
     *
     *  从服务器获取功能服务数据后填充
     */

    private fun setServiceData(cmsAppDataList: JSONArray?) {

        if (null == cmsAppDataList || cmsAppDataList.length() <= 0) {
            setServiceView()
            return
        }
        var serviceDatas = JSONUtil.arrayToList(cmsAppDataList)
        var json = PublicInfoDataService.getInstance().getCustomConfig(null)
        if (json == null || TextUtils.isEmpty(json.optString("home_tool_vertical", "")) || json.optString("home_tool_vertical", "") == "1") {
            var mLayoutManager = GridLayoutManager(context, 4)
            rl_controller?.visibility = View.GONE
            recycler_center_service?.layoutManager = mLayoutManager
            (recycler_center_service?.itemAnimator as DefaultItemAnimator?)?.supportsChangeAnimations = false
        } else {
            var linearParams = recycler_center_service_layout?.layoutParams
            linearParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            linearParams?.height = SizeUtils.dp2px(99f)
            recycler_center_service_layout?.layoutParams = linearParams


            var linearParams2 = recycler_center_service?.layoutParams
            linearParams2?.width = ViewGroup.LayoutParams.MATCH_PARENT
            linearParams2?.height = SizeUtils.dp2px(88f)
            recycler_center_service?.layoutParams = linearParams2

            var mLayoutManager = PagerGridLayoutManager(1, 4, PagerGridLayoutManager.HORIZONTAL)
            mLayoutManager?.setPageListener(object : PagerGridLayoutManager.PageListener {
                override fun onPageSelect(pageIndex: Int) {
                    //todo  这里是第几页 +1
                    if (servicePageSize <= 1) {
                        return
                    }
                    when (pageIndex) {
                        0 -> {
                            iv_frist?.setBackgroundResource(R.drawable.item_bg_4_homepage_select)
                            iv_second?.setBackgroundResource(R.drawable.item_bg_4_homepage_unselect)

                        }
                        1 -> {
                            iv_frist?.setBackgroundResource(R.drawable.item_bg_4_homepage_unselect)
                            iv_second?.setBackgroundResource(R.drawable.item_bg_4_homepage_select)
                        }
                    }
                }

                override fun onPageSizeChanged(pageSize: Int) {
                    servicePageSize = pageSize

                }
            })
            if (serviceDatas.size > 4) {
                rl_controller?.visibility = View.VISIBLE
            } else {
                rl_controller?.visibility = View.GONE
            }
            var snapHelper = PagerGridSnapHelper()
            if (recycler_center_service?.onFlingListener == null) {
                snapHelper.attachToRecyclerView(recycler_center_service ?: return)
            }
            recycler_center_service?.layoutManager = mLayoutManager

        }


        serviceAdapter = NewHomePageServiceAdapter(serviceDatas)

        serviceAdapter?.setOnItemClickListener { adapter, view, position ->

            var obj = serviceDatas.get(position)

            var httpUrl = obj.optString("httpUrl")
            var nativeUrl = obj.optString("nativeUrl")

            LogUtil.d(TAG, "httpUrl is $httpUrl , nativeUrl is $nativeUrl")
            if (TextUtils.isEmpty(httpUrl)) {
                if (StringUtil.checkStr(nativeUrl) && nativeUrl.contains("?")) {
                    enter2Activity(nativeUrl?.split("?"))
                }
            } else {
                if (httpUrl == PublicInfoDataService.getInstance().getOnlineService(null)) {
                    forUdeskWebView()
                } else {
                    forwardWeb(obj)
                }
            }
        }

        recycler_center_service?.adapter = serviceAdapter
    }


    /**
     * 对应的服务
     */
    fun enter2Activity(temp: List<String>?) {

        if (null == temp || temp.size <= 0)
            return

        when (temp[0]) {
            "coinmap_market" -> {
                /**
                 * 行情
                 */
                var tabType = HomeTabMap.maps[HomeTabMap.marketTab]
                homeTabSwitch(tabType)
            }
            "coinmap_trading" -> {
                /**
                 * 币对交易页
                 */
                SymbolManager.instance.saveTradeSymbol(temp[1])
                var tabType = HomeTabMap.maps[HomeTabMap.coinTradeTab]
                homeTabSwitch(tabType)
            }
            "coinmap_details" -> {
                /**
                 * 币对详情页
                 * MarketDetailActivity
                 */
                if (!TextUtils.isEmpty(temp[1])) {
                    ArouterUtil.forwardKLine(temp[1])
                } else {
                    NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_hasNoCoinPair"))
                }
            }
            "otc_buy" -> {
                /**
                 *场外交易-购买
                 */
                /*if (LoginManager.checkLogin(activity, true)) {
                }*/
                if (otcOpen) {
                    var tabType = HomeTabMap.maps[HomeTabMap.otccoinTradeTab]
                    homeTabSwitch(tabType)
                } else {
                    NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_notSupportOTC"))
                }
            }
            "otc_sell" -> {
                /**
                 * 场外交易-出售
                 */
                if (otcOpen) {
                    var tabType = HomeTabMap.maps[HomeTabMap.otccoinTradeTab]
                    homeTabSwitch(tabType)
                } else {
                    NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_notSupportOTC"))
                }
            }

            "order_record" -> {
                /**
                 *订单记录
                 */

                if (LoginManager.checkLogin(activity, true)) {
                    if (otcOpen) {
                        ArouterUtil.greenChannel(RoutePath.NewOTCOrdersActivity, null)
                    } else {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_notSupportOTC"))
                    }
                }
            }
            "account_transfer" -> {
                /**
                 * 账户划转
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    ArouterUtil.forwardTransfer(ParamConstant.TRANSFER_BIBI, "BTC")
                }
            }
            "otc_account" -> {
                /**
                 *资产-场外账户
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    if (otcOpen) {
                        if (contractOpen) {
                            forwardAssetsActivity(1)
                        } else {
                            homeAssetstab_switch(1)
                        }
                    } else {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "common_tip_notSupportOTC"))

                    }
                }
            }
            "contract_follow_order" -> {
                /**
                 * 跟单页面
                 */
                FollowOrderSDK.ins().toFollowOrderView(activity)
            }

            "coin_account" -> {
                /**
                 * 资产-币币账户
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    if (contractOpen && otcOpen) {
                        forwardAssetsActivity(0)
                    } else {
                        homeAssetstab_switch(0)
                    }

                }

            }
            "safe_set" -> {
                /**
                 *安全设置
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)
                }
            }
            "safe_money" -> {
                /**
                 * 安全设置-资金密码
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    if (UserDataService.getInstance()?.authLevel != 1) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_please_cert"))
                        return
                    }
                    if (UserDataService.getInstance().isCapitalPwordSet == 0) {
                        ArouterUtil.forwardModifyPwdPage(ParamConstant.SET_PWD, ParamConstant.FROM_OTC)
                    } else {
                        ArouterUtil.forwardModifyPwdPage(ParamConstant.RESET_PWD, ParamConstant.FROM_OTC)
                    }
                }
            }
            "personal_information" -> {
                /**
                 *个人资料
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    ArouterUtil.greenChannel(RoutePath.PersonalInfoActivity, null)
                }

            }
            "personal_invitation" -> {
                /**
                 *个人资料-邀请码
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    ArouterUtil.greenChannel(RoutePath.InvitFirendsActivity, null)
                }

            }
            "collection_way" -> {
                /**
                 *收款方式
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    if (UserDataService.getInstance().authLevel != 1) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_please_cert"))
                        return
                    }

                    val capitalPwordSet = UserDataService.getInstance().isCapitalPwordSet
                    if (capitalPwordSet == 0) {
                        NToastUtil.showTopToast(false, LanguageUtil.getString(context, "otc_please_set_pwd"))
                        return
                    }

                    ArouterUtil.greenChannel(RoutePath.PaymentMethodActivity, null)
                }
            }
            "real_name" -> {
                /**
                 *实名认证
                 */
                if (LoginManager.checkLogin(activity, true)) {
                    //认证状态 0、审核中，1、通过，2、未通过  3未认证
                    when (UserDataService.getInstance().authLevel) {
                        0 -> {
                            NToastUtil.showTopToast(false, LanguageUtil.getString(context, "noun_login_pending"))


                        }
                        1 -> {
                            NToastUtil.showTopToast(true, LanguageUtil.getString(context, "personal_text_verified"))
                        }
                        /**
                         * 审核未通过
                         */
                        2 -> {
                            ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                        }

                        3 -> {
                            ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                        }
                    }
                }
            }
            "contract_transaction" -> {
                /**
                 * 去合约交易页面
                 */
                forwardContractTab()
            }

            "market_etf" -> {
                /**
                 * ETF列表
                 */
                forwardMarketTab("ETF")
            }

            /**
             * 合约经纪人
             * TODO 这里需要确定key
             */
            "config_contract_agent_key" -> {
                ArouterUtil.navigation(RoutePath.ContractAgentActivity, null)
            }


        }
    }

    private fun forwardContractTab() {
        var messageEvent = MessageEvent(MessageEvent.contract_switch_type)
        EventBusUtil.post(messageEvent)
    }

    private fun forwardMarketTab(coin: String) {
        var messageEvent = MessageEvent(MessageEvent.market_switch_type)
        messageEvent.msg_content = coin
        EventBusUtil.post(messageEvent)
    }

    /**
     * 获取账户信息
     */
    var accountBalance = ""
    var accountFlat = ""
    private fun getAccountBalance() {
        var disposable = getMainModel().getTotalAsset(MyNDisposableObserver(accountBalanceReqType))
        addDisposable(disposable!!)

    }


}