package com.yjkj.chainup.ws

import android.util.Log
import com.yjkj.chainup.bean.kline.WsLinkBean
import com.yjkj.chainup.util.GZIPUtils
import com.yjkj.chainup.util.NetUtil
import com.yjkj.chainup.util.WsLinkUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WsAgentManager private constructor() {
    var mOkHttpClient: OkHttpClient? = null
    var mWebSocket: WebSocket? = null
    var serverUrl: String = ""
    private val TAG = this.javaClass.simpleName
    private var mCount = 0
    private var isAppStopWs = true
    private val callbacks = arrayListOf<WsResultCallback>()
    var subscribePong: Disposable? = null//保存订阅者
    private val mapSubCallbacks = hashMapOf<String, HashMap<String, WsLinkBean>>()
    private val subCallbacks = hashMapOf<String, WsResultCallback>()


    interface WsResultCallback {
        fun onWsMessage(json: String)
    }

    companion object {
        var wsAgentManager: WsAgentManager? = null
        const val WEBSOCKET_tickerFor24HLink = "tickerFor24HLink" // 24H的行情
        const val WEBSOCKET_getDepthLink = "depthLink" //深度盘口
        val instance: WsAgentManager
            get() {
                if (wsAgentManager == null)
                    wsAgentManager = WsAgentManager()
                return wsAgentManager!!
            }
    }

    init {
        mOkHttpClient = OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

    }

    fun socketUrl(socketUrl: String, isMainThread: Boolean = true) {
        Log.e(TAG, "socketUrl()")
        this.serverUrl = socketUrl
        if (isMainThread) {
            initWS()
        }
    }

    fun sendMessage(message: HashMap<String, String>, callback: WsResultCallback?) {
        if (!isConnection()) {
            initWS()
        }
        if (callback != null) {
            val key = callback.javaClass.simpleName
            if (mapSubCallbacks.containsKey(key)) {
                when (key) {
                    "NCVCTradeFragment" -> {
                        val symbol = message.get("symbol") as String
                        val step = if (message.containsKey("step")) message.get("step") as String else "0"
                        val team = mapSubCallbacks.get(key) as HashMap
                        val temp = if (team.containsKey("depthLink")) (team.get("depthLink") as WsLinkBean).symbol else ""
                        val tempStep = if (team.containsKey("depthLink")) (team.get("depthLink") as WsLinkBean).step else ""
                        val symbolEquls = temp != symbol
                        val symbolStep = tempStep != step && tempStep.isNotEmpty()
                        val reSend = symbolEquls || symbolStep
                        Log.e(TAG, "判断当前是否 symbol != ${symbolEquls}  step  old ${tempStep} new ${step}  != ${symbolStep} ")
                        if (team.isNotEmpty() && team.size != 0 && reSend) {
                            unbind(callback, false, symbolEquls, symbolStep)
                        }
                        val isReSend = temp.isEmpty() || reSend
                        if (isReSend) {
                            Log.e(TAG, "bind ws set")
                            val ticker = WsLinkUtils.tickerFor24HLinkBean(symbol)
                            val depthLink = WsLinkUtils.getDepthLink(symbol, true, if (step.isEmpty()) "0" else step)
                            val map = hashMapOf("ticker24H" to ticker, "depthLink" to depthLink)
                            mapSubCallbacks.put(key, map)
                            // ticker
                            if (!symbolStep || symbolEquls) {
                                sendData(ticker)
                            }
                            // depthLink
                            sendData(depthLink)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun sendData(wsLinkBean: WsLinkBean) {
        if (isConnection()) {
            val send = mWebSocket?.send(wsLinkBean.json)
            Log.e(TAG, "sendData ${send}  data =  ${wsLinkBean.channel}  ")
        }
    }

    private fun sendData(wsLinkBean: String) {
        if (isConnection()) {
            val sendStatus = mWebSocket?.send(wsLinkBean)
            Log.d(TAG, "ws sendData sendStatus ${sendStatus}")
        } else {
            Log.e(TAG, "ws sendData  处于断线状态 无法发送 ")
        }
    }


    fun unbind(callback: WsResultCallback?, isStop: Boolean = true, isSymbol: Boolean = false, step: Boolean = false) {
        Log.e(TAG, "unbind ws unset")
        if (callback != null) {
            val key = callback.javaClass.simpleName
            if (mapSubCallbacks.containsKey(key)) {
                when (key) {
                    "NCVCTradeFragment" -> {
                        val map = mapSubCallbacks.get(key) as HashMap
                        if (map.contains("depthLink")) {
                            val bean = map.get("depthLink") as WsLinkBean
                            val symbol = bean.symbol
                            val ticker = WsLinkUtils.tickerFor24HLinkBean(symbol, false)
                            val depthLink = WsLinkUtils.getDepthLink(symbol, false, bean.step)
                            if (!step || isSymbol) {
                                sendData(ticker)
                            }
                            sendData(depthLink)
                        }

                    }
                }
                if (isStop) {
                    removeWsCallback(callback)
                }
            }
        }
    }

    fun reConnection() {
        Log.e(TAG, "WS 是否不重连 ${isAppStopWs}")
        mWebSocket = null
        if (!isAppStopWs) {
            if (mCount <= 3) {
                Log.e(TAG, "WS 是重连")
                initWS()
                mCount++
            } else {
                Log.e(TAG, "WS 不重连")
            }
        }
    }

    fun changeNotice(status: NetUtil.Companion.ConnectStatus) {
        if (status != NetUtil.Companion.ConnectStatus.NO_NETWORK) {
            if (!isConnection()) {
                resetParams()
                stopPong()
                reConnection()
            }
        }
    }

    fun resetParams() {
        mCount = 0
    }

    @Synchronized
    fun addWsCallback(callback: WsResultCallback) {
        if (!isConnection()) {
            initWS()
        }
        val key = callback.javaClass.simpleName
        if (mapSubCallbacks.contains(key)) {
            Log.e(TAG, "${callback.javaClass.name}  exist in callbacks, index is ${callbacks.indexOf(callback)} ")
            return
        }
        mapSubCallbacks.put(key, hashMapOf())
        subCallbacks.put(key, callback)
        Log.e(TAG, "after add callback size is ${mapSubCallbacks.size}")
    }

    @Synchronized
    fun removeWsCallback(callback: WsResultCallback): Boolean {
        val key = callback.javaClass.simpleName
        if (mapSubCallbacks.size == 0) {
            Log.e(TAG, "remove callback failed ,because callbacks size == 0, name is  ${callback.javaClass.name}")
            return false
        }
        mapSubCallbacks.set(key, hashMapOf())
        return true
    }

    private fun initWS() {
        isAppStopWs = false
        Log.d(TAG, "initWS()  ${isAppStopWs}")
        val request = Request.Builder().url(this.serverUrl).build()
        mOkHttpClient?.newWebSocket(request, wsEventList)
        initPong()

    }

    fun isConnection(): Boolean {
        if (mWebSocket != null) {
            return true
        }
        return false
    }

    private var wsEventList = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d(TAG, "onOpen() data =  ${response.code}")
            mWebSocket = webSocket
            resetParams()
            if (mapSubCallbacks.containsKey("NCVCTradeFragment")) {
                val map = mapSubCallbacks.get("NCVCTradeFragment") as HashMap
                if (map.contains("depthLink")) {
                    Log.d(TAG, "onOpen() 准备重新 set")
                    val bean = map.get("depthLink") as WsLinkBean
                    val symbol = bean.symbol
                    val ticker = WsLinkUtils.tickerFor24HLinkBean(symbol, true)
                    val depthLink = WsLinkUtils.getDepthLink(symbol, true, bean.step)
                    sendData(ticker)
                    sendData(depthLink)
                } else {
                    Log.d(TAG, "onOpen() 没有找到之前的订阅  无法set")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            if (bytes.size == 0) {
                return
            }
            val data = GZIPUtils.uncompressToString(bytes.toByteArray())
            Log.v(TAG, "onMessage() data =  ${data}")
            if (null != data && data.contains("ping")) {
                val replace = data.replace("ping", "pong")
                sendData(replace)
            } else {
                try {
                    val json = JSONObject(data)
                    subCallbacks.forEach {
                        it.value.onWsMessage(data)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e(TAG, "onFailure() data =  ${t.message} response ${response?.code}")
            t.printStackTrace()
            reConnection()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.e(TAG, "onClosed() data =  ${code}")
            reConnection()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.e(TAG, "onClosing() data =  ${code}")
            reConnection()
        }
    }

    fun stopWs() {
        isAppStopWs = true
        stopPong()
        mWebSocket?.cancel()
        mWebSocket?.close(1000, null)
        mapSubCallbacks.clear()

    }

    private fun initPong() {
        if (subscribePong == null || (subscribePong != null && subscribePong?.isDisposed != null && subscribePong?.isDisposed!!)) {
            subscribePong = Observable.interval(20, TimeUnit.SECONDS)//按时间间隔发送整数的Observable
                    .observeOn(AndroidSchedulers.mainThread())//切换到主线程修改UI
                    .subscribe {
                        sendData(WsLinkUtils.pongBean().json)
                    }
        }
    }

    private fun stopPong() {
        subscribePong?.dispose()
    }


}