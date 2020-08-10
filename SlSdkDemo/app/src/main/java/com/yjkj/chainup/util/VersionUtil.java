package com.yjkj.chainup.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @Author lianshangljl
 * @Date 2020-02-28-17:14
 * @Email buptjinlong@163.com
 * @description
 */
public class VersionUtil {

    /**
     * 获取制定包名应用的版本的versionCode
     * @param context
     * @param
     * @return
     */
    public static int getVersionCode(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
