package com.yjkj.chainup.manager

import android.util.Log
import com.yjkj.chainup.util.GZIPUtils
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

/**
 * @Author: Bertking
 * @Date：2019/1/25-10:40 AM
 * @Description:
 */
class MyWebSocketClient(url: String) : WebSocketClient(URI(url)) {


    interface ISocketData {
        fun getSocketData(data: String)
    }

    init {
        connect()
    }

    var msg: String = ""
        set(value) {
            field = value
            if(this.isOpen){
                this.send(msg)
            }
        }

    var iSocketData: ISocketData? = null

    constructor(msg: String, url: String) : this(url) {
        this.msg = msg
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        this.send(msg)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
    }

    override fun onMessage(message: String?) {
    }

    override fun onMessage(bytes: ByteBuffer?) {
        super.onMessage(bytes)

        if (bytes == null) return
        val data = GZIPUtils.uncompressToString(bytes.array())
        if (!data.isNullOrBlank()) {
            if (data.contains("ping")) {
                val replace = data.replace("ping", "pong")
                this.send(replace)
            } else {
                // TODO 我们需要的数据
                iSocketData?.getSocketData(data)
                Log.d("", "===result$data===")
            }

        }

    }

    override fun onError(ex: Exception?) {
    }



}