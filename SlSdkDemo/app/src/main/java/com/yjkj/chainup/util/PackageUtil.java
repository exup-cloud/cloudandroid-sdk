package com.yjkj.chainup.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yjkj.chainup.app.ChainUpApp;

public class PackageUtil {

	/*
	 * 获取该程序的安装包路径
	 */
	public static String getPackagePath(Context context) {
		// PackageManager pm = context.getPackageManager();
		return context.getPackageResourcePath();
	}

	/*
	 * 获取当前程序路径
	 */
	public static String getCurApplicationPath(Context context) {
		// PackageManager pm = context.getPackageManager();
		return context.getFilesDir().getAbsolutePath();
	}

	/*
	 * 得到应用的版本号
	 */
	public static String getVersionName() {
		try {
			Context context = ChainUpApp.appContext;
			PackageManager manager = context.getPackageManager();
			if(null!=manager){
				PackageInfo info = manager.getPackageInfo(context.getPackageName(),
						0);
				if(null!=info){
					return info.versionName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/*
	 * 得到应用的版本号
	 */
	public static int getVersionCode() {
		try {
			Context context = ChainUpApp.appContext;
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * 得到应用的渠道名称
	 */
	public static String getChannelName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			if(null!=packageManager){
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						return applicationInfo.metaData.getString("UMENG_CHANNEL");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/*
	 * 得到应用的渠道名称
	 */
	public static String getApplicationName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			if(null!=packageManager){
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					return (String) packageManager.getApplicationLabel(applicationInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
