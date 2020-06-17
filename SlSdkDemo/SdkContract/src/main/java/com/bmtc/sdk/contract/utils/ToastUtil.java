package com.bmtc.sdk.contract.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

/**
 * Created by ChenLiheng on 2016/3/31.
 * desc：
 */
public class ToastUtil {
    public static void shortToast(Context context, int resId) {
        toast(context, context.getString(resId), null, Toast.LENGTH_SHORT);
    }

    public static void shortToast(Context context, int resId, Drawable icon) {
        toast(context, context.getString(resId), icon, Toast.LENGTH_SHORT);
    }

    public static void shortToast(Context context, CharSequence text, Drawable icon) {
        toast(context, text, icon, Toast.LENGTH_SHORT);
    }

    public static void shortToast(Context context, CharSequence text) {
        toast(context, text, null, Toast.LENGTH_SHORT);
    }

    public static void longToast(Context context, int resId) {
        toast(context, context.getString(resId), null, Toast.LENGTH_LONG);
    }

    public static void longToast(Context context, int resId, Drawable icon) {
        toast(context, context.getString(resId), icon, Toast.LENGTH_LONG);
    }

    public static void longToast(Context context, CharSequence text) {
        toast(context, text, null, Toast.LENGTH_LONG);
    }


    private static void toast(Context context, CharSequence text, Drawable icon, int duration) {
        Toast.makeText(context, text, duration).show();
    }
}
