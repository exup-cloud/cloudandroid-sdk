package com.yjkj.chainup.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;

import com.yjkj.chainup.app.ChainUpApp;


public class ContextUtil {

    public static String getString(int stringId) {
        return ChainUpApp.app.getString(stringId);
    }

    public static int getColor(int colorId) {
        return ContextCompat.getColor(ChainUpApp.app, colorId);
    }

    public static Drawable getResource(int drawableId) {
        return ContextCompat.getDrawable(ChainUpApp.app, drawableId);
    }

    /**
     * 检查是否重复跳转，不需要则重写方法并返回true
     */
    private String mActivityJumpTag;        //activity跳转tag
    private long mClickTime;                //activity跳转时间
    protected boolean checkDoubleClick(Intent intent) {

        // 默认检查通过
        boolean result = true;
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        } else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        } else {
            return true;
        }

        if (tag.equals(mActivityJumpTag) && mClickTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false;
        }

        // 记录启动标记和时间
        mActivityJumpTag = tag;
        mClickTime = SystemClock.uptimeMillis();
        return result;
    }

    public static void startService(Context context, Class<?> cls){

        //开启服务做兼容处理
        Intent intent = new Intent(context,cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
            context.startService(intent);
        }else {
            context.startService(intent);
        }
    }
}
