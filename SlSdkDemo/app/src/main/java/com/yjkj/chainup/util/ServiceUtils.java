package com.yjkj.chainup.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * @Author lianshangljl
 * @Date 2018/10/18-下午3:59
 * @Email buptjinlong@163.com
 * @description
 */
public class ServiceUtils {
    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (!StringUtil.checkStr(ServiceName))
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

}
