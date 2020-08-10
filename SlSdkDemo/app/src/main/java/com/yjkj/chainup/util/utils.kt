package com.yjkj.chainup.util

import android.app.ActivityManager
import android.content.Context

class SystemV2Utils {
    companion object {
        fun getProcessName(context: Context?): String? {
            if (context == null) return null
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfo = manager.runningAppProcesses
            processInfo.forEach {
                if (it.pid == android.os.Process.myPid()) {
                    return it.processName
                }
            }
            return null
        }
    }
}