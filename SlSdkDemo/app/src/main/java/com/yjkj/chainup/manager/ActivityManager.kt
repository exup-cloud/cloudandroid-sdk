package com.yjkj.chainup.manager

import android.app.Activity
import java.util.*

/**
 * @Author: Bertking
 * @Date：2018/11/26-10:34 AM
 * @Description:
 */
var activityStack: Stack<Activity> = Stack()

class ActivityManager {

    companion object {


        private fun popActFromStack(activity: Activity) {
            activity.finish()
            if (activityStack.isNotEmpty()) {
                activityStack.remove(activity)
            }
        }


        fun pushAct2Stack(activity: Activity) {
            activityStack.add(activity)
        }


        fun getCurActivity(): Activity? {
            return if (activityStack.isNotEmpty()) {
                activityStack.lastElement()
            } else {
                null
            }
        }

        fun popAllActFromStack() {
            if (activityStack.isNotEmpty()) {
                for (activity in activityStack) {
                    activity.finish()
                }
                activityStack.clear()
            }
        }

        fun popGivenActivity(clazz: Class<Activity>) {
            if (activityStack.isNotEmpty()) {
                for (activity in activityStack) {
                    if (activity.javaClass == (clazz)) {
                        popActFromStack(activity)
                    }
                }
            }
        }

    }
}