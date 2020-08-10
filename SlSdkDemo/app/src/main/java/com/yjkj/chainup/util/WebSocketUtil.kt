package com.yjkj.chainup.util

import android.util.Log
import com.yjkj.chainup.net.api.ApiConstants
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

/**
 * @Author: Bertking
 * @Date：2019/3/18-6:16 PM
 * @Description:
 */
class WebSocketUtil(msg:String) : WebSocketClient(URI(ApiConstants.SOCKET_ADDRESS)) {
    val TAG = WebSocketUtil::class.java.simpleName
    var onMessage: OnMessage? = null

    var msg = msg
        set(value) {
            field = value
            if (isOpen) {
                this.send(value)
            } else {
                this.reconnect()
            }
        }

    override fun onOpen(handshakedata: ServerHandshake?) = try {
        this.send(msg)
    } catch (exception: WebsocketNotConnectedException) {
        exception.printStackTrace()
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d(TAG, "=====onClose===code:$code,reason:$reason")
    }

    override fun onMessage(bytes: ByteBuffer?) {
        super.onMessage(bytes)

        val retDataStr = GZIPUtils.uncompressToString(bytes?.array())
        if (!retDataStr.isNullOrBlank()) {
            if (retDataStr.contains("ping")) {
                val replace = retDataStr.replace("ping", "pong")
                this.send(replace)
            }
        }
        onMessage?.msg(retDataStr)
    }

    override fun onMessage(message: String?) {
    }

    override fun onError(ex: Exception?) {
        Log.d(TAG, "=====onError===exception:${ex?.printStackTrace()}")

    }


    interface OnMessage {
        fun msg(data: String)
    }
}