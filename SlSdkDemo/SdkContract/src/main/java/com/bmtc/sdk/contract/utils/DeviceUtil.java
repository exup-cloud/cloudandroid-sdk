package com.bmtc.sdk.contract.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.contract.sdk.utils.NumberUtil;
import com.contract.sdk.utils.StringUtil;

import java.io.File;


/**
 *
 * 包含设备处理方法的工具类
 *
 * @since 1.6
 * @version 1.0.0 2010-11-06
 */
public class DeviceUtil {

	public static final int SDK_VERSION_1_5 = 3;
	public static final int SDK_VERSION_1_6 = 4;
	public static final int SDK_VERSION_2_0 = 5;
	public static final int SDK_VERSION_2_0_1 = 6;
	public static final int SDK_VERSION_2_1 = 7;
	public static final int SDK_VERSION_2_2 = 8;
	public static final int SDK_VERSION_2_3 = 9;
	public static final int SDK_VERSION_2_3_3 = 10;
	public static final int SDK_VERSION_3_0 = 11;
	public static final int SDK_VERSION_3_1 = 12;
	public static final int SDK_VERSION_3_2 = 13;
	public static final int SDK_VERSION_4_0 = 14;
	public static final int SDK_VERSION_4_0_3 = 15;
	public static final int SDK_VERSION_4_1_2 = 16;
	public static final int SDK_VERSION_4_2_2 = 17;

	/**
	 * 获得手机品牌
	 */
	public static String getDeviceBrand() {
		return Build.BRAND;
	}

	/**
	 * 获得设备型号
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    public static String getDeviceModel() {
        return Build.MODEL;
    }

	/**
	 * 获得国际移动设备身份码
	 * @param context
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    @SuppressLint({"MissingPermission", "HardwareIds"})
	public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)) != null ? ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() : null;
    }
	
	/**
	 * 获得国际移动用户识别码
	 * @param context
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    @SuppressLint({"MissingPermission", "HardwareIds"})
	public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)) != null ? ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId() : null;
    }
	
	/**
	 * 获得设备屏幕矩形区域范围
	 * @param context
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    private static Rect getScreenRect(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)) != null ? ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay() : null;
        int w = display.getWidth();
        int h = display.getHeight();
        return new Rect(0, 0, w, h);
    }
	
	/**
	 * 获得设备屏幕宽度
	 * @param context
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    public static int getScreenWidth(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)) != null ? ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay() : null;
        return display.getWidth();
	}
	
	/**
	 * 获得设备屏幕高度
	 * @param context
	 * @return
	 */
	@SuppressWarnings("JavaDoc")
    public static int getScreenHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)) != null ? ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay() : null;
        return display.getHeight();
	}
	
	/**
	 * 获得设备屏幕密度
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.density;
	}
	
	public static int getScreenDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}
	
	/** 
	 * 获得系统版本
	 */
	public static String getSDKVersion(){
		return Build.VERSION.SDK;
	}
	
	public static int getSDKVersionInt(){
		return NumberUtil.toInt(Build.VERSION.SDK);
		//return android.os.Build.VERSION.SDK_INT;
	}
	
	public static boolean isPad(Context context) {
		Rect rect = getScreenRect(context);
		int screenWidth = Math.min(rect.right, rect.bottom);
		return screenWidth >= 800;
	}
	
	/**
     * Returns whether the device has an external storage device which is
     * emulated. If true, the device does not have real external storage, and the directory
     * returned by {@link #()} will be allocated using a portion of
     * the internal storage system.
     *
     * <p>Certain system services, such as the package manager, use this
     * to determine where to install an application.
     *
     * <p>Emulated external storage may also be encrypted - see
     * {@link android.app.admin.DevicePolicyManager#setStorageEncryption(
     * android.content.ComponentName, boolean)} for additional details.
     */
	public static boolean isExternalStorageEmulated() {
		/*
		 * SINCE API: 14
		 * Environment.isExternalStorageEmulated();
		 */
		Boolean isEmulated = (Boolean) ReflectionUtil.tryInvoke(
				Environment.class, "isExternalStorageEmulated", false, new Class[0], new Object[0]);
		
		return isEmulated != null && isEmulated.booleanValue();
	}
	
	public static String getCpuABI() {
		try {
			return Build.CPU_ABI;
		} catch (Throwable tr) {
			return "";
		}
	}
	
	/**
	 * 判断是否开启了自动亮度调节
	 * SINCE API: 8
	 */
	public static boolean isScreenAutoBrightness(Context context) {
	    boolean automicBrightness = false;
	    try {
	        automicBrightness = Settings.System.getInt(context.getContentResolver(),
	                "screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/) == 0x00000001/*Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC*/;
	    } catch (SettingNotFoundException ignored) {

	    }
	    return automicBrightness;
	}
	
	/**
	 * 停止自动亮度调节
	 * SINCE API: 8
	 */
	public static void stopScreenAutoBrightness(Context context) {
	    Settings.System.putInt(context.getContentResolver(),
	    		"screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/,
	    		0x00000000/*Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL*/);
	}
	
	/**
	 * 开启自动亮度调节
	 * SINCE API: 8
	 */
	public static void startScreenAutoBrightness(Context context) {
		Settings.System.putInt(context.getContentResolver(),
	    		"screen_brightness_mode"/*Settings.System.SCREEN_BRIGHTNESS_MODE*/,
	    		0x00000001/*Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC*/);
	}
	
	/**
	 * 获取屏幕的亮度
	 */
	public static int getScreenBrightness(Context context) {
	    int brightness = 0;
	    try {
	    	brightness = Settings.System.getInt(
	        		context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return brightness;
	}
	
	/**
	 * 设置亮度,状态不会保存
	 */
	public static void setScreenBrightness(Activity activity, int brightness) {
	    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
	    lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
	    activity.getWindow().setAttributes(lp);
	}
	
	/**
	 * 保存亮度设置状态
	 */
	public static void saveScreenBrightness(Context context, int brightness) {
	    Uri uri = Settings.System.getUriFor("screen_brightness");
	    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
	            brightness);
	    context.getContentResolver().notifyChange(uri, null);
	}
	
	/**
     * @return the number of bytes available on the filesystem rooted at the given File
     */
    public static long getAvailableBytes(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a few blocks)
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }
	
	/**
     * @return the number of bytes on the filesystem rooted at the given File
     */
    public static long getTotalBytes(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a few blocks)
        long totalBlocks = (long) stat.getBlockCount() - 4;
        return stat.getBlockSize() * totalBlocks;
    }
    
    @SuppressLint({"MissingPermission", "HardwareIds"})
	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return StringUtil.makeSafe(tMgr != null ? tMgr.getLine1Number() : null);
	}
    
    public static boolean isExternalMediaMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * dp转px
     * @param context
     * @param dpValue
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
