package com.yjkj.chainup.manager

import com.yjkj.chainup.net.api.ApiConstants

/**
 * @Author: Bertking
 * @Date：2019/1/25-10:33 AM
 * @Description:
 */

class SocketUtil {
    companion object {
        private var mInstance: MyWebSocketClient? = null
        val instance: MyWebSocketClient
            get() {
                if (mInstance == null) {
                    mInstance = MyWebSocketClient(ApiConstants.SOCKET_CONTRACT_ADDRESS)
                }
                return mInstance!!
            }
    }

}