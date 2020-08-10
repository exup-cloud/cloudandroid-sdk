package com.yjkj.chainup.app

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.support.annotation.RequiresApi
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import com.alibaba.android.arouter.launcher.ARouter
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.net.ContractHttpConfig
import com.follow.order.FollowOrderSDK
import com.follow.order.constant.FOTheme
import com.igexin.sdk.PushManager
import com.tencent.mmkv.MMKV
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.CommonConstant
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.push.DemoPushService
import com.yjkj.chainup.extra_service.push.HandlePushIntentService
import com.yjkj.chainup.manager.DataInitService
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net_new.NetUrl
import com.yjkj.chainup.new_version.view.ForegroundCallbacksObserver
import com.yjkj.chainup.util.*
import com.yjkj.chainup.wedegit.ForegroundCallbacks
import com.yjkj.chainup.ws.WsAgentManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @Author: Bertking
 * @Date：2019/3/6-10:52 AM
 * @Description:
 */
class ChainUpApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    val TAG = ChainUpApp::class.java.simpleName
    private var appCount = 0
    private var appStateChangeListener: AppStateChangeListener? = null
    private var currentState: Int = 0
    val STATE_FOREGROUND = 0
    val STATE_BACKGROUND = 1

    companion object {
        lateinit var appContext: Context
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        appContext = this
        // com.getui.demo.ChainUpPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this, DemoPushService::class.java)
        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this, HandlePushIntentService::class.java)
        PushManager.getInstance().setPrivacyPolicyStrategy(this, false)
        appStateChangeListener = getAppStateChangeListener()
        registerActivityLifecycleCallbacks(this)
        //合约增加
        MMKV.initialize(this)
        ARouter.init(this)
        WsAgentManager.instance.socketUrl(ApiConstants.SOCKET_ADDRESS,SystemUtil().isMainProcessFun(this))
//        DoraemonKit.install(this, null, "05e2863eb603b51b6e6b49e1b6447051")
        FollowOrderSDK.ins().init(this)
        FollowOrderSDK.ins().isDebug = true
        FollowOrderSDK.ins().followOrderProxy = FollowOrderImpl()
        setTheme()
        initAppStatusListener()
        webViewSetPath(this)
        //合约SDK初始化  主进程才实例化
        if(TextUtils.equals(getString(R.string.applicationId),getProcessName(this))){
            val contractHttpConfig = ContractHttpConfig()
            contractHttpConfig.aesSecret = "lMYQry09AeIt6PNO"
            contractHttpConfig.prefixHeader = "ex"
            contractHttpConfig.contractUrl = NetUrl.getcontractUrl()+"fe-cov2-api/swap/"
            contractHttpConfig.contractWsUrl = NetUrl.getContractSocketUrl()
            contractHttpConfig.headerParams = SystemUtils.getHeaderParams()
            contractHttpConfig.wsSignLength = 128
            //是否是合约云SDK
            ContractSDKAgent.isContractCloudSDK = false
            //合约SDK Http配置初始化
            ContractSDKAgent.httpConfig = contractHttpConfig
            //通知合约SDK语言环境
            ContractSDKAgent.isZhEnv = SystemUtils.isZh()
            //合约SDK 必须设置 在最后调用
            ContractSDKAgent.init(this)
            UserDataService.getInstance().token
            //延迟2秒初始化合约token
            UserDataService.getInstance().notifyContractLoginStatusListener(false)
        }
    }

    private fun getProcessName(context: Context): String? {
        val am: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses ?: return null
        for (proInfo in runningApps) {
            if (proInfo.pid == Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName
                }
            }
        }
        return null
    }

    private fun initAppStatusListener() {

        ForegroundCallbacks.init(this).addListener(object : ForegroundCallbacks.Listener {
            override fun onBecameForeground() {

                ForegroundCallbacksObserver.getInstance().ForegroundListener()
            }

            override fun onBecameBackground() {

                ForegroundCallbacksObserver.getInstance().CallBacksListener()
            }
        })
    }

    private fun setTheme() {
        val themeMode = PublicInfoDataService.getInstance().themeMode
        when (themeMode) {
            PublicInfoDataService.THEME_MODE_DAYTIME -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                FollowOrderSDK.ins().theme = FOTheme.LIGHT
            }

            PublicInfoDataService.THEME_MODE_NIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                FollowOrderSDK.ins().theme = FOTheme.DARK
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "==========onConfigurationChanged==========")
        LocalManageUtil.setApplicationLanguage(applicationContext)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "========现在横屏===")
        }
        val isZhEnv = SystemUtils.isZh()
        //通知合约SDK语言环境
        ContractSDKAgent.isZhEnv = isZhEnv
        if (isZhEnv) {
            Log.d(TAG, "====中文")
            LocalManageUtil.saveSelectLanguage(this, "zh_CN")
        } else if (SystemUtils.isVietNam()) {
            Log.d(TAG, "====英文")
            LocalManageUtil.saveSelectLanguage(this, "vi_VN")
        }
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        Log.d(TAG, "========onActivityCreated===")
    }

    override fun onActivityStarted(p0: Activity?) {
        Log.d(TAG, "========onActivityStarted===")

        if (appCount == 0) {
            currentState = STATE_FOREGROUND
            appStateChangeListener?.appTurnIntoForeground()
        }
        appCount++

    }

    override fun onActivityPaused(p0: Activity?) {
        Log.d(TAG, "========onActivityPaused===")
    }

    override fun onActivityResumed(p0: Activity?) {
        Log.d(TAG, "========onActivityResumed===")
    }

    override fun onActivityStopped(p0: Activity?) {
        Log.d(TAG, "========onActivityStopped===")
        appCount--
        if (appCount == 0) {
            currentState = STATE_BACKGROUND
            appStateChangeListener?.appTurnIntoBackGround()
        }
    }

    override fun onActivityDestroyed(p0: Activity?) {
        Log.d(TAG, "========onActivityDestroyed===")
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
        Log.d(TAG, "========onActivitySaveInstanceState===")
    }

    interface AppStateChangeListener {
        fun appTurnIntoForeground()
        fun appTurnIntoBackGround()
    }

    private fun getAppStateChangeListener() = object : AppStateChangeListener {
        override fun appTurnIntoBackGround() {
            Log.d(TAG, "========appTurnIntoBackGround===")
            restart()
        }

        override fun appTurnIntoForeground() {
            Log.d(TAG, "========appTurnIntoForeground===")
            startTime()
        }
    }
    var subscribe: Disposable? = null//保存订阅者
    fun startTime() {

        Log.e("LogUtils", "startTime time")
        restart()
        subscribe = io.reactivex.Observable.interval(0L,CommonConstant.rateLoopTime, TimeUnit.SECONDS)//按时间间隔发送整数的Observable
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程修改UI
                .subscribe {
                    val intent = Intent(this, DataInitService::class.java)
                    if (it > 1L) {
                        intent.putExtra("isFirst", true)
                    }
                    startService(intent)
                }
    }
    /**
     * 结束计时,重新开始
     */
    fun restart() {
        Log.e("LogUtils", "dispose ${subscribe}")
        if (subscribe != null ) {
            subscribe?.dispose()//取消订阅
            Log.e("LogUtils", "dispose time")
        }
    }

    private fun webViewSetPath(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!SystemUtil().isMainProcessFun(this)) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(SystemV2Utils.getProcessName(context))
            }
        }
    }

}
