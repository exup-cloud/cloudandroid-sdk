package com.yjkj.chainup.contract

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.net.ContractHttpConfig
import com.yjkj.chainup.R
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.net_new.NetUrl
import com.yjkj.chainup.util.SystemUtils

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

}
