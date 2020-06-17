package com.bmtc.sdk.simple

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bmtc.sdk.contract.PNLShareActivity
import com.bmtc.sdk.contract.UserContractActivity
import com.bmtc.sdk.contract.utils.ShareToolUtil
import com.bmtc.sdk.contract.utils.ToastUtil
import com.bmtc.sdk.simple.contract.UsdtActivity
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractSDKAgent
import com.contract.sdk.data.ContractUser
import com.contract.sdk.impl.ContractSDKListener
import com.contract.sdk.impl.ContractUserStatusListener
import com.contract.sdk.net.ContractHttpConfig
import com.contract.sdk.utils.SDKLogUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initSLSDKAgent()
    }

    private fun initViews() {
        tv_usdt_contract.setOnClickListener(this)
        tv_coin_contract.setOnClickListener(this)
        tv_trade_contract.setOnClickListener(this)
        tv_contract_account.setOnClickListener(this)
        //tv_login.setOnClickListener(this)
        tv_share.setOnClickListener(this)
        tv_share2.setOnClickListener(this)
        tv_simulation_login.setOnClickListener(this)
    }

    /**
     * 初始化SDK 可在Application中进行初始化
     */
    private fun initSLSDKAgent() {
        val contractHttpConfig = ContractHttpConfig()
        contractHttpConfig.prefixHeader = "ex"
        contractHttpConfig.contractUrl = "http://swapapi.nmghhzy.cn/"//设置HTTP接口请求域名
        contractHttpConfig.contractWsUrl = "wss://swapws.hongyachina.com.cn/realTime" //设置合约ws
        //合约SDK Http配置初始化
        ContractSDKAgent.httpConfig = contractHttpConfig
        //是否是合约云SDK
        ContractSDKAgent.isContractCloudSDK = true
        //通知合约SDK语言环境 目前只支持中英文
        ContractSDKAgent.isZhEnv = true
        //合约SDK 必须设置 在最后调用
        ContractSDKAgent.init(this.application)
        //控制日志输出
        ContractSDKAgent.logEnabled = true


        /**
         * 注册监听
         */
        ContractSDKAgent.sdkListener = object : ContractSDKListener{
            override fun sdkInitFail(message: String) {
               ToastUtil.shortToast(this@MainActivity, "SDK初始化失败:$message")
            }

            /**
             * SDK初始化成功后，可获取合约市场Ticker等相关数据
             */
            override fun sdkInitSuccess() {
                ToastUtil.shortToast(this@MainActivity, "SDK初始化成功")

                //订阅所有Ticker
                ContractPublicDataAgent.subscribeAllTickerWebSocket()
            }
        }

        /**
         * 监听登录
         */
        ContractSDKAgent.registerSDKUserStatusListener(this,object:ContractUserStatusListener(){
            /**
             * 合约SDK登录成功
             */
            override fun onContractLoginSuccess() {
                tv_login_status.text = "已登录"
            }

            /**
             * 合约SDK退出登录
             */
            override fun onContractExitLogin() {
                tv_login_status.text = "未登录"
            }

        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_usdt_contract -> UsdtActivity.show(this@MainActivity, 0)
            R.id.tv_coin_contract -> UsdtActivity.show(this@MainActivity, 1)
            R.id.tv_trade_contract -> UsdtActivity.show(this@MainActivity, 2)
            R.id.tv_contract_account -> if (ContractSDKAgent.isLogin) {
                val intent = Intent(this@MainActivity, UserContractActivity::class.java)
                startActivity(intent)
            } else {
                ToastUtil.shortToast(this, "需要先登录")
            }
//            R.id.tv_login -> {
//                // LoginActivity.show(MainActivity.this);
//                val userAgent = System.getProperty("http.agent")
//                SDKLogUtil.d("libin", "userAgent:$userAgent")
//            }
            R.id.tv_share -> {
                PNLShareActivity.show(this@MainActivity, "", 0)
            }
            R.id.tv_share2 -> {
                val inflater = LayoutInflater.from(this@MainActivity)
                val shotView = inflater.inflate(com.bmtc.sdk.contract.R.layout.sl_pnl_share, null)
                val warpLayout = findViewById<LinearLayout>(R.id.ll_share_placeholder_layout)
                warpLayout.addView(shotView)
                //获取数据 渲染UI 可参考PNLShareActivity类的createItemView方法
                Handler().postDelayed({
                    val dView: View = warpLayout
                    dView.isDrawingCacheEnabled = true
                    dView.buildDrawingCache()
                    val bitmap = Bitmap.createBitmap(dView.drawingCache)
                    ShareToolUtil.sendLocalShare(this@MainActivity, bitmap)
                }, 2000)
            }
            R.id.tv_simulation_login ->{
                if(!ContractSDKAgent.isLogin){
                    //构造用户user相关信息
                    val user = ContractUser()
                    user.token = "648dee29eca7034f29fc2d94038b32ee"
                    user.expiredTs = "1765075396772000"
                    user.accessKey = "3d2907dd-5ee6-4dfa-b43f-c7180a38ea51"
                    //绑定user对象
                    ContractSDKAgent.user = user
                }

            }
        }
    }

}