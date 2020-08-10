package com.yjkj.chainup.extra_service.push

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.igexin.sdk.GTIntentService
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage
import com.yjkj.chainup.R
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.contract.activity.SlContractKlineActivity
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net_new.JSONUtil
import io.karn.notify.Notify
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Serializable


/**
 * Created by zhaopan on 2018/6/15.
 */

//class ChainUpPushService : Service() {
//
//    override fun onCreate() {
//        super.onCreate()
//
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)
//
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        return GTServiceManager.getInstance().onBind(intent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        GTServiceManager.getInstance().onDestroy()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        GTServiceManager.getInstance().onLowMemory()
//    }
//}

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br></br>
 * onReceiveMessageData 处理透传消息<br></br>
 * onReceiveClientId 接收 cid <br></br>
 * onReceiveOnlineState cid 离线上线通知 <br></br>
 * onReceiveCommandResult 各种事件处理回执 <br></br>
 */
class HandlePushIntentService : GTIntentService() {

    override fun onReceiveServicePid(context: Context, pid: Int) {
        Log.e(TAG, "HandlePush_onReceiveServicePid -> pid = $pid")

    }

    //应用收到透传数据.
    override fun onReceiveMessageData(context: Context, msg: GTTransmitMessage) {
        val data = String(msg.payload)
        Log.d(TAG, "HandlePush_onReceiveMessageData>>> messageId:" + msg.messageId + ", taskId:" + msg.taskId + ", payloadId:" + msg.payloadId + ", payloadData:" + data)
        try {
            val payload = JSONUtil.objectFromJson(data, PushPayloadData::class.java)
            Log.d(TAG, "HandlePush_onReceiveMessageData>>> pushUrl:" + payload.parseRouteUrl())
            Notify.with(context).content {
                title = payload.title
                text = payload.message
            }.header {
                icon = R.mipmap.ic_launcher
            }.meta {
                val intent = Intent(context, PushControll::class.java).apply {
                    putExtra("pushPlayUrl", payload.parseRouteUrl())
                }
                clickIntent = PendingIntent.getActivity(context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
            }
                    .show()
        } catch (exce: Exception) {
            exce.printStackTrace()
        }
    }

    override fun onReceiveClientId(context: Context, clientid: String) {
        Log.e(TAG, "HandlePush_onReceiveClientId -> clientid = $clientid")
        HttpClient.instance.bindToken(clientid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    it.printStackTrace()
                })
    }

    override fun onReceiveOnlineState(context: Context, online: Boolean) {
        Log.e(TAG, "HandlePush_onReceiveOnlineState -> online = $online")
    }

    override fun onReceiveCommandResult(context: Context, cmdMessage: GTCmdMessage) {
        Log.d(TAG, "HandlePush_onReceiveCommandResult" + cmdMessage.action)
    }

    //收到push消息.
    override fun onNotificationMessageArrived(context: Context, msg: GTNotificationMessage) {
        Log.d(TAG, "HandlePush_onNotificationMessageArrived " + msg.messageId + ", " + msg.taskId + ", " + msg.title + ", " + msg.content)
//        ChainApp.app.component.settingManager().updateRedDot(KEY_1_MINE, KEY_2_MESSAGE, num = PushMsgQueue.msgCount()+1)
    }

    //在通知栏上点击消息
    override fun onNotificationMessageClicked(context: Context, msg: GTNotificationMessage) {
        Log.d(TAG, "HandlePush_onNotificationMessageClicked" + msg.messageId + ", " + msg.taskId + ", " + msg.title + ", " + msg.content)
    }
}

data class PushPayloadData(@SerializedName("title") var title: String,
                           @SerializedName("message") var message: String? = "",
                           @SerializedName("url") var url: String? = "",
                           @SerializedName("native") var contract_address: String? = "1") : Serializable {
    fun parseRouteUrl(): String? {
        return url
    }
}
//
//
//
//object PushMsgQueue {
//    private const val TAG = "PushMsgQueue"
//    private val pushMsgQueue = LinkedList<PushPayloadData>()
//    private var unlockStatus = false
//
//    fun registerAuthEvent(){
//        Log.d(TAG, "registerAuthEvent")
//        RxBus.get().removeStickyEvent(AuthEvent::class.java)
//        RxBus.get().toObservableSticky(AuthEvent::class.java)
//                .onErrorReturn {
//                    return@onErrorReturn AuthEvent(false, AuthType.UNLOCK)
//                }
//                .subscribe({ authEvent ->
//                    Log.d(TAG, "HandlePush_AuthEvent=" + authEvent.unlockSucceed)
//                    unlockStatus = authEvent.unlockSucceed
//                    handleMessages()
//                }, {
//                    Timber.e(TAG, it.message)
//                })
//    }
//
//    fun msgCount(): Int = pushMsgQueue.size
//
//    fun add(data: PushPayloadData) {
//        if(unlockStatus){
//            handleMessage(data)
//        } else {
//            pushMsgQueue.addLast(data)
//        }
//    }
//
//    fun poll(): PushPayloadData {
//        return pushMsgQueue.removeFirst()
//    }
//
//    private fun handleMessages(){
//        // 当收到解锁成功事件后, 立即处理用户已点击过的消息.
//        while (unlockStatus && !pushMsgQueue.isEmpty()) {
//            handleMessage(poll())
//        }
//    }
//
//    fun handleMessage(data: PushPayloadData) {
//        Log.d(TAG, "HandlePush handle payload=" + data)
//        RxBus.get().postSticky(data)
//    }
//}
//
//fun Activity.receiveAndHandlePushPayloadData() {
//    RxBus.get().removeStickyEvent(PushPayloadData::class.java)
//    RxBus.get().toObservableSticky(PushPayloadData::class.java)
//            .onErrorReturn {
//                return@onErrorReturn PushPayloadData(action = "")
//            }
//            .compose(RxUtil.applySchedulersToObservable())
//            .subscribe({
//                Log.d(MainActivity.TAG, "receive push payload data: " + it.toString())
//                handlePushMsg(this, it)
//            }, {
//                Timber.e(MainActivity.TAG, it)
//            })
//}

val ACTION_BACK_UP = "back_up"
val ACTION_TRANSACTION = "transaction"
val ACTION_MESSAGE_CENTER = "message_center"

//fun handlePushMsg(activity: Activity, payload: PushPayloadData) {
//    when (payload.action) {
//
//    }
//}
class RouteApp private constructor() {
    companion object {
        @Volatile
        var instances: RouteApp? = null
        val ROUTE_HOME: String = "home"

        val ROUTE_MARKET: String = "market"
        val ROUTE_SLCONTRACT: String = "slContract/"
        val ROUTE_SLCONTRACT_DETAIL: String = ROUTE_SLCONTRACT + "/detail"
        fun getInstance(): RouteApp {
            if (instances == null) {
                synchronized(RouteApp::class) {
                    if (instances == null) {
                        instances = RouteApp()
                    }
                }
            }
            return instances!!
        }
    }

    fun execApp(routeUrl: String, activity: Activity) {
        if (routeUrl.isEmpty()) {
            return
        }
        val url = Uri.parse(routeUrl)
        val path = url.path
        if (path.isNullOrEmpty()) {
            return
        }
        if (url.authority == ROUTE_HOME) {
            val newPath = path.substring(path.indexOf("/") + 1, path.length)
            when (newPath) {
                ROUTE_MARKET -> {
                    Log.e("LogUtils", "跳转 行情币对ETF")
                    val name = url.getQueryParameter("name") ?: ""
                    if (name.isNotEmpty()) {
                        Handler().postDelayed({
                            var messageEvent = MessageEvent(MessageEvent.market_switch_type)
                            messageEvent.msg_content = name
                            EventBusUtil.post(messageEvent)
                        }, 1000)
                    }
                }
                ROUTE_SLCONTRACT_DETAIL -> {
                    Log.e("LogUtils", "跳转 合约K线")
                    try {
                        val contractId = url.getQueryParameter("contractId") ?: ""
                        if (contractId.isNotEmpty()) {
                            SlContractKlineActivity.show(activity, contractId.toInt())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun sendTestPush(pushUrl: String = "chainup://home/market?name=ETF") {
        Notify.with(ChainUpApp.appContext).content {
            title = "测试"
            text = "描述信息"
        }.meta {
            val intent = Intent(ChainUpApp.appContext, PushControll::class.java).apply {
                putExtra("pushPlayUrl", pushUrl)
            }
            clickIntent = PendingIntent.getActivity(ChainUpApp.appContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }
                .show()
    }
}

