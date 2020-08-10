package com.yjkj.chainup.app;

import com.yjkj.chainup.R;
import com.yjkj.chainup.util.ContextUtil;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-26 19:47
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-26 19:47
 * @UpdateRemark: 更新说明
 */
public class AppConfig {

    public static final int cacheSize = 10 * 1024 * 1024;
    public static final long read_time = 15 * 10000;
    public static final long write_time = 15 * 10000;
    public static final long connect_time = 15 * 10000;

    public static final String app_name = ContextUtil.getString(R.string.app_name);
    public static String app_ver = "1.0.0";
    public static String down_cl = "guanfang";


    public static final boolean needUmengStatistics = false;//开发阶段为false,上线后改为true
    public static final boolean IS_DEBUG = true;//Log日志开关，true为打开日志,上线需要关闭改为false
    public static final boolean isOpenLeakCanary = false; //LeakCanary 内存泄漏检测工具，上线需改为false
    public static final boolean isBuglyOpen = true; //Bugly日志统计 工具，上线需改为true
    public static final boolean isFirebaseAnalyticsOpen = true; //Google firebas 工具，上线需改为true


    public static final String default_host = "https://www.baidu.com/";
}
