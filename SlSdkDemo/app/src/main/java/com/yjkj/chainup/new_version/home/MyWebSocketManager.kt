package com.yjkj.chainup.new_version.home

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.WsStatus
import com.rabtman.wsmanager.listener.WsStatusListener
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.util.GZIPUtils
import com.yjkj.chainup.util.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * MyWsManager管理类
 */
class MyWebSocketManager private constructor() {
    private val TAG = this.javaClass.simpleName
    private var mCount = 0
    private var isOpen = false

    interface WsCallback {
        fun onCallback(json: JSONObject)
    }

    private val context: Context? = null
    private val failedCmd = LinkedList<String>()
    private val callbacks = arrayListOf<WsCallback>()

    private fun logMarket(callback: WsCallback) {
//        if (callback is MarketTrendFragment) {
//            LL.e(">>>>>>>" + callback.marketName)
//        } else {
//            LL.e(">>>>>>>" + callback.javaClass.name)
//        }
    }

    @Synchronized
    fun addWsCallback(callback: WsCallback) {
        logMarket(callback)
        if (callback == null) {
            Log.e("jinlong","callback is null ")
            return
        }
        if (callbacks.contains(callback)) {
            Log.e(TAG, "${callback.javaClass.name}  exist in callbacks, index is ${callbacks.indexOf(callback)} ")
            return
        }
        callbacks.add(callback)
        Log.e(TAG, "after add callback size is ${callbacks.size}")
    }

    @Synchronized
    fun removeWsCallback(callback: WsCallback): Boolean {
        logMarket(callback)
        if (callback == null) {
            Log.e("jinlong","callback is null ,no need remove")
            return false
        }
        if (callbacks.size == 0) {
//            LL.e("remove callback failed ,because callbacks size == 0, name is  ${callback.javaClass.name}")
            return false
        }
        var res = callbacks.remove(callback)
//        if (!res) {
//            LL.e("remove callback failed , name is  ${callback.javaClass.name}")
//        }
        Log.e(TAG, "after remove callback size is ${callbacks.size}")
        return res
    }

//    init {
//        initWs()
//    }

    fun initWs(context: Context? = ChainUpApp.appContext) {
        try {
            if (wsManager != null) {
                if (wsManager!!.currentStatus == WsStatus.CONNECTING || wsManager!!.currentStatus == WsStatus.CONNECTED) {
                    Log.e("jinlong","currentStatus is ${wsManager!!.currentStatus} ,  CONNECTING || CONNECTED ,return ")
                    return
                }
            }
            wsManager = WsManager.Builder(context)
                    .client(
                            OkHttpClient().newBuilder()
//                                    .pingInterval(10, TimeUnit.SECONDS)
                                    .retryOnConnectionFailure(true)
                                    .build())
                    .needReconnect(true)
                    .wsUrl(DEF_RELEASE_URL)
                    .needReconnect(true) //是否需要重连
                    .build()
            wsManager?.setWsStatusListener(wsStatusListener)
            wsManager?.startConnect()
            Log.e(TAG, "ws init success, ${wsManager.toString()} startConnect......")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "ws init failed :" + e.message)
        }
    }

    private val wsStatusListener: WsStatusListener = object : WsStatusListener() {
        override fun onOpen(response: Response) {
            Log.e(TAG, "onOpen>>  ${response.toString()}, need send cmd size: ${failedCmd.size} ")
            mCount = 0
            isOpen = true
            if (failedCmd.isNotEmpty()) {
                autoSendFailedCmd()
            }
        }

        override fun onMessage(text: String) {
            Log.e(TAG, "onMessage>> msg : $text")
        }

        override fun onMessage(bytes: ByteString) {
            super.onMessage(bytes)
            if (null == bytes) {
                return
            }
            val data = GZIPUtils.uncompressToString(bytes.toByteArray())
            if (null != data && data.contains("ping")) {
                val replace = data.replace("ping", "pong")
                sendData(replace)
            } else {
                try {
                    val json = JSONObject(data)
//                    if(json.getString("channel") == "market_btcusdt_ticker"){
//                        Log.e("MM",json.toString(2));
//                    }
//                    Log.e(TAG, "onMessage json>>$json")
                    callbacks.forEach {
                        it.onCallback(json)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onReconnect() {
            Log.e(TAG, "onReconnect ing")
        }

        override fun onClosing(code: Int, reason: String) {
            Log.e(TAG, "onClosing>> code = $code, reason = $reason")
            if (wsManager != null) {
                isOpen = false
                wsManager!!.stopConnect()
                wsManager!!.startConnect()
            }
        }

        override fun onClosed(code: Int, reason: String) {
            isOpen = false
            Log.e(TAG, "onClosed>> code = $code, reason = $reason")
        }

        override fun onFailure(t: Throwable?, response: Response?) {
            t?.printStackTrace()
            isOpen = false
            Log.e(TAG, "onFailure>>  resp : ${response?.toString()} , ex : $t")
            if (context == null) {
                return
            }
            if (NetworkUtils.isNetworkAvailable(context)) {
                reConnect()
            }
        }
    }

    private fun autoSendFailedCmd() {
        while (failedCmd.isNotEmpty() && failedCmd.size > 0) {
            try {
                sendData(failedCmd.removeFirst())
            } catch (ex: Exception) {
                Log.e(TAG, "ex : ${ex.message}")
                break
            }
        }

    }

    fun sendData(lst: List<String>, resend: Boolean = false) {
        if (lst == null || lst.isEmpty()) {
            return
        }
//        Log.e(TAG, "resend = $resend , ${lst.size}")
        for (msg in lst) {
            sendData(msg, resend)
        }
    }

    //发送ws数据
    fun sendData(cmd: String, resend: Boolean = false): Boolean {
        if (TextUtils.isEmpty(cmd)) {
            return true
        }
//        if (failedCmd.contains(cmd)) {
//            Log.e(TAG, "exist at failedCmd ,return")
//            return true
//        }

        var isSend = false
        if (wsManager != null && wsManager!!.isWsConnected) {
            isSend = wsManager!!.sendMessage(cmd)
            if (isSend) {
//                Log.e(TAG, "发送成功 msg :$cmd")
            } else {
                failedCmd.add(cmd)
//                Log.e(TAG, "发送失败 msg :$cmd , add to auto send")
            }

        } else {
//            Log.e(TAG, "wsManager : $wsManager , isWsConnected : ${wsManager?.isWsConnected} , isOpen : $isOpen ,发送失败 msg :$cmd , add to auto send")
            failedCmd.add(cmd)
            initWs()
        }

        return isSend
    }

    fun reConnect(isForce: Boolean = false) {
        if (mCount > 5 && !isForce) {
            return
        }
        mCount++
        wsManager?.stopConnect()
        wsManager?.startConnect()
    }

    //断开ws
    fun disconnect() {
        wsManager?.stopConnect()
    }

    companion object {
        private val DEF_RELEASE_URL = ApiConstants.SOCKET_ADDRESS //连接地址
        private var myWebSocketManager: MyWebSocketManager? = null
        private var wsManager: WsManager? = null
        //单例
        val instance: MyWebSocketManager?
            get() {
                if (myWebSocketManager == null) {
                    synchronized(MyWebSocketManager::class.java) {
                        if (myWebSocketManager == null) {
                            myWebSocketManager = MyWebSocketManager()
                        }
                    }
                }
                return myWebSocketManager
            }
    }
}