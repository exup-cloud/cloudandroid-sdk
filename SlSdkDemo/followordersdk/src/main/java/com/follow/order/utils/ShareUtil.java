package com.follow.order.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.follow.order.FollowOrderSDK;


/**
 * Created by hot on 2017/6/19.
 */

public class ShareUtil {

    private static final String SP_NAME = AppInfoUtil.getPackageName(FollowOrderSDK.ins().getApplication());
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;


    /**
     * 初始化操作数据
     *
     * @param context
     */
    public static void initUtil(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    //======================================判断常用方法======================================//

    /**
     * 判断某个String是否是空值，目前默认  null  空串
     *
     * @param empty
     * @return
     */
    public static boolean isUnNromalEmpty(String empty) {
        if (TextUtils.isEmpty(empty)) {
            return false;
        }
        return true;
    }

    /**
     * @param key 判断某个key在SP文件中对应的值是否为空
     * @return
     */
    public static boolean isUnNromalEmptyByKey(String key) {
        return isUnNromalEmpty(getString(key));
    }

    /**
     * 判断某个key在SP文件中对应的值是否与目标值相等， 如果sp文件中没有该key，则用设定的默认值与目标值比较
     *
     * @param key
     * @param defaut
     * @param aimValue
     * @return
     */
    public static boolean equals(String key, String defaut, String aimValue) {
        return TextUtils.equals(getString(key, defaut), aimValue);
    }

    /**
     * 判断某个key在SP文件中对应的值是否与目标值相等， 如果sp文件中没有该key，则默认对应的值为 ""
     *
     * @param key
     * @param aimValue
     * @return
     */
    public static boolean equals(String key, String aimValue) {
        return equals(key, "", aimValue);
    }


    //======================================END 常用方法==================================//


    //===============一些为普通常用方法
    public static boolean putBoolean(String key, boolean value) {
        try {
            editor.putBoolean(key, value);
            editor.commit();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description:
     * @author liliwei
     * @create 2013-8-20 下午4:55:51
     * @updateTime 2013-8-20 下午4:55:51
     */
    public static boolean getBoolean(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }


    public static boolean putFloat(String key, float value) {
        try {
            editor.putFloat(key, value);
            editor.commit();

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static float getFloat(String key, float value) {
        return sp.getFloat(key, value);

    }

    public static float getFloat(String key) {
        return getFloat(key, 0f);

    }

    public static boolean putInt(String key, int value) {
        try {
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static int getInt(String key, int value) {
        return sp.getInt(key, value);

    }

    public static int getInt(String key) {
        return getInt(key, 0);

    }

    public static boolean putLong(String key, Long value) {
        try {
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static long getLong(String key, Long value) {
        return sp.getLong(key, value);

    }

    public static long getLong(String key) {
        return sp.getLong(key, 0l);

    }

    public static boolean putString(String key, String value) {
        try {
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static String getString(String key, String value) {
        return sp.getString(key, value);

    }

    public static String getString(String key) {
        return getString(key, "");

    }

    public static void removeShare(String key) {
        editor.remove(key);
        editor.commit();
    }


}
