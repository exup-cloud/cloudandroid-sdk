package com.follow.order.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;


/**
 * AppInfo工具类
 */

public class AppInfoUtil {


    public static String getPackageName(Context context) {
        return context.getPackageName();

    }

    /**
     * 获取版本号
     *
     * @param context
     * @return 获取版本号
     */
    public static int getVersionCode(Context context) {

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return 获取版本名
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 方法描述：获取手机型号
     */
    public static String getPhoneModle(Context context) {
        return android.os.Build.MODEL;
    }

	/**
	 * 方法描述：安装一个Apk
	 *
	 * @return 是否成功
	 */
	public static boolean isAppInstall(Context context, String packname) {
		PackageManager manager = context.getPackageManager();
		@SuppressWarnings("rawtypes")
		List pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = (PackageInfo) pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase(packname)) {
				return true;
			}
		}
		return false;
	}


    /**
     * 将long值大小格式化为有单位的KB MB GB
     *
     * @param context
     * @param size
     * @return 格式化后的KB MB GB
     */
    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取手机自带可用内存rom.
     */
    public static long getAvailRom(Context context) {
        File path = Environment.getDataDirectory();
        // StatFs用于获取一个
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        //return Formatter.formatFileSize(context, blockSize * availableBlocks);
        return blockSize * availableBlocks;
    }

    /**
     * 获取手机总内存rom.
     */
    public static long getTotalRom(Context context) {
        File path = Environment.getDataDirectory();
        // StatFs用于获取一个
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();// 区别
        return blockSize * availableBlocks;
        //return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获取总运存大小 RAM
     *
     * @param context
     * @return 获取总运存大小 RAM
     */
    public static long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(e.getMessage() + "FADA:");
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
        return initial_memory;
    }

    /**
     * 获取可用运存大小RAM
     *
     * @param context
     * @return 可用运存大小RAM
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        System.out.println("可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem;
    }

    /**
     * Sdcard的可用空间
     *
     * @param context
     * @return Sdcard的可用空间
     */
    public static long getAvailSD(Context context) {
        // 判断是否有插入存储卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();// 获取sd路径
            // StatFs:检索有关整体上的一个文件系统的空间信息
            StatFs stat = new StatFs(path.getPath());
            // 一个文件系统的块大小，以字节为单位。
            long blockSize = stat.getBlockSize();
            // SD卡中可用的块数
            long availableBlocks = stat.getAvailableBlocks();
            //return Formatter.formatFileSize(context, blockSize* availableBlocks);
            return blockSize * availableBlocks;
        } else {
            return 0;
        }
    }

    /**
     * Sdcard的总空间
     *
     * @return Sdcard的总空间
     */
    public static long getALLAvailSdSize(Context context) {
        // 判断是否有插入存储卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            // StatFs:检索有关整体上的一个文件系统的空间信息
            StatFs stat = new StatFs(path.getPath());
            // 一个文件系统的块大小，以字节为单位。
            long blockSize = stat.getBlockSize();
            // SD卡中可用的块数
            long availableBlocks = stat.getBlockCount();
            //return Formatter.formatFileSize(context, blockSize* availableBlocks);
            return blockSize * availableBlocks;
        } else {
            return 0;
        }
    }

}
