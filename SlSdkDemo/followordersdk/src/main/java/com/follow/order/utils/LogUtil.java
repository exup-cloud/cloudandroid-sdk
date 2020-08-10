/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.follow.order.utils;

import android.text.TextUtils;
import android.util.Log;

import com.follow.order.utils.log.DLOG;


/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Author: wyouflf
 * Date: 13-7-24
 * Time: 下午12:23
 */
public class LogUtil {

    public static String customTagPrefix = "fosdk_log";
    private static boolean mDebug;

    private LogUtil() {
    }

    public static void setDebug(boolean debug) {
        mDebug = debug;
        DLOG.init(mDebug);
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void json(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.json(tag, content);
    }

    public static void json(String tag, String content) {
        if (!mDebug) return;
        DLOG.json(tag, content);
    }

    public static void d(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.d(tag, content, tr);
    }

    public static void d(String tag, String content) {
        if (!mDebug) return;

        DLOG.d(tag, content);
    }

    public static void e(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.e(tag, content, tr);
    }

    public static void i(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.i(tag, content, tr);
    }

    public static void v(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.v(tag, content, tr);
    }

    public static void w(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        DLOG.w(tag, tr);
    }


    public static void wtf(String content) {
        if (!mDebug) return;
        String tag = generateTag();

        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        if (!mDebug) return;
        String tag = generateTag();

        Log.wtf(tag, tr);
    }

}
