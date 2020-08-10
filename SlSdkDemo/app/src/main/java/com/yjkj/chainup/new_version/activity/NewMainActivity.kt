package com.yjkj.chainup.new_version.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.didichuxing.doraemonkit.DoraemonKit
import com.igexin.sdk.PushManager
import com.tencent.mmkv.MMKV
import com.yjkj.chainup.BuildConfig
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.contract.fragment.SlContractFragment
import com.yjkj.chainup.db.constant.HomeTabMap
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.RateDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.extra_service.push.RouteApp
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.activity.asset.NewVersionMyAssetFragment
import com.yjkj.chainup.new_version.dialog.DialogUtil
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.home.MyWebSocketManager
import com.yjkj.chainup.new_version.home.NewVersionHomepageFragment
import com.yjkj.chainup.util.CheckUpdateUtil
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.NetUtil
import com.yjkj.chainup.util.UIUtils
import com.yjkj.chainup.ws.WsAgentManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_main.*
import org.json.JSONObject

// TODO 优化
@Route(path = RoutePath.NewMainActivity)
class NewMainActivity : NBaseActivity() {

    override fun setContentView() = R.layout.activity_new_main

    /*
     * 底部tab导航索引，默认为首页
     */
    var curPosition = 0
    var lastPosition = 0

    /**
     * 游戏弹窗
     */
    var gameID = ""
    var gameName = ""
    var gameToken = ""
    var pushUrl = ""

    private var homePageFragment = NewVersionHomepageFragment()
    private var slContractFragment = SlContractFragment()

    private var assetFragment = NewVersionMyAssetFragment()

    private var fragmentManager: FragmentManager? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        fragmentManager = supportFragmentManager
        MyWebSocketManager.instance?.initWs()
        loadData()
        getIntentData()
        RouteApp.getInstance().execApp(pushUrl, this)
        DoraemonKit.disableUpload()
        DoraemonKit.install(application, null, "cb190f56cf")
        DoraemonKit.setAwaysShowMainIcon(false)
        DoraemonKit.setDebug(BuildConfig.DEBUG)
        DoraemonKit.show()
        NetUtil.registerNetConnChangedReceiver(this)
        NetUtil.addNetConnChangedListener(object : NetUtil.Companion.NetConnChangedListener {
            override fun onNetConnChanged(connectStatus: NetUtil.Companion.ConnectStatus) {
                Log.e("LogUtils", "onNetConnChanged ${connectStatus.name}")
                WsAgentManager.instance.changeNotice(connectStatus)
            }
        })
    }

    fun getIntentData() {
        /**
         * 游戏弹窗
         */
        gameID = intent?.getStringExtra("gameId") ?: ""
        gameName = intent?.getStringExtra("gameName") ?: ""
        gameToken = intent?.getStringExtra("gameToken") ?: ""
        pushUrl = intent?.getStringExtra("pushUrl") ?: ""

        if (!TextUtils.isEmpty(gameID)) {
            if (LoginManager.checkLogin(this, true)) {
                DialogUtil.showAuthorizationDialog(this, gameID, gameName, gameToken)
            }
        }

        MMKV.defaultMMKV().putString("gameId", gameID)
    }


    private var fragmentList = arrayListOf<NBaseFragment>()
    private var mImageViewList = ArrayList<Int>()
    private var mTextviewList = ArrayList<String>()
    private var contractOpen = true
    private fun initTabsData(data: JSONObject?) {

        fragmentList.add(homePageFragment)
        mImageViewList.add(R.drawable.bg_homepage_tab)
        mTextviewList.add(LanguageUtil.getString(this, "mainTab_text_home"))
        fragmentList.add(slContractFragment)
        mImageViewList.add(R.drawable.bg_contract_tab)
        mTextviewList.add(LanguageUtil.getString(this, "mainTab_text_contract"))
        HomeTabMap.initMaps(data)
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onResume() {
        super.onResume()
        NewDialogUtils.showHomePageDialog(this)
        loginToken()
    }

    override fun initView() {

        showTabs()

    }

    private fun showTabs() {
        bottomtab_group?.setData(mImageViewList, mTextviewList, this)
        for (i in 0 until fragmentList.size) {
            var fg = fragmentList[i]
            val transaction = fragmentManager?.beginTransaction()
            transaction?.add(R.id.fragment_container, fg, fg.javaClass.name)?.commitAllowingStateLoss()
        }
        setCurrentItem()
    }


    override fun onClick(view: View) {
        super.onClick(view)
        var tag = view.tag
        if (tag is Int) {
            curPosition = tag
            if (lastPosition != curPosition) {
                for (i in 0 until fragmentList.size) {
                    fragmentList[i].refreshOkhttp(lastPosition)
                }
                lastPosition = curPosition
            }
            setCurrentItem()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }


    override fun onMessageEvent(event: MessageEvent) {
        super.onMessageEvent(event)
        if (event.msg_type == MessageEvent.coin_payment) {
            if (MessageEvent.contract_switch_type == event.msg_type) {
                /**
                 * 跳转合约
                 */
                curPosition = 1
                setCurrentItem()
            }
        } else if (MessageEvent.login_bind_type == event.msg_type) {
            LogUtil.e("LogUtils", "登录监听 ${UserDataService.getInstance().token}  [] ${PushManager.getInstance().getClientid(this)}")
            HttpClient.instance.bindToken(PushManager.getInstance().getClientid(this)).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                    }, {
                        it.printStackTrace()
                    })
        }
    }



    private var exitTime = 0L
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            UIUtils.showToast(LanguageUtil.getString(this, "exit_remind"))
            exitTime = System.currentTimeMillis()
            return
        }
        HttpClient.instance.setToken("")
        super.onBackPressed()
    }

    private fun setCurrentItem() {
        bottomtab_group?.showCurTabView(curPosition)
        for (i in 0 until fragmentList.size) {
            val transaction = fragmentManager?.beginTransaction()
            var fg = fragmentList[i]
            if (i == curPosition) {
                mActivity?.runOnUiThread {
                    transaction?.show(fg)?.commitAllowingStateLoss()
                }
            } else {
                if (!fg.isHidden) {
                    transaction?.hide(fg)?.commitAllowingStateLoss()
                }
            }
        }
    }


    override fun loadData() {
        super.loadData()

        var catchObj = PublicInfoDataService.getInstance().getData(null)
        if (null != catchObj && catchObj.length() > 0) {
            initTabsData(catchObj)
        } else {
            addDisposable(getMainModel().public_info_v4(MyNDisposableObserver(mActivity)))
        }

        CheckUpdateUtil.update(mActivity, true)
    }

    /*
    *  当前页面若为一个请求时，可不用重写内部类
    * */
    inner class MyNDisposableObserver(activity: Activity) : NDisposableObserver(activity, false) {
        override fun onResponseSuccess(jsonObject: JSONObject) {
            var data = jsonObject.optJSONObject("data")
            if (null != data && data.length() > 0) {
                var rate = data.optJSONObject("rate")
                RateDataService.getInstance().saveData(rate)
            }

            PublicInfoDataService.getInstance().saveData(data)
            initTabsData(data)
        }

        override fun onResponseFailure(code: Int, msg: String?) {
            super.onResponseFailure(code, msg)
            var cachObj = PublicInfoDataService.getInstance().getData(null)
            initTabsData(cachObj)
        }
    }

    var hasCommmitBikiUserInfo = false
    fun loginToken() {
        if (hasCommmitBikiUserInfo)
            return
        var token = UserDataService.getInstance().token
        if (getString(R.string.applicationId) == "com.chainup.exchange.bikicoin" && !TextUtils.isEmpty(token)) {
            hasCommmitBikiUserInfo = true
            addDisposable(getOTCModel().loginInformation(token, object : NDisposableObserver(null, false) {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                }
            }))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NLiveDataUtil.removeObservers()
        WsAgentManager.instance.stopWs()
        NetUtil.unregisterNetConnChangedReceiver(this)
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
    }


}


