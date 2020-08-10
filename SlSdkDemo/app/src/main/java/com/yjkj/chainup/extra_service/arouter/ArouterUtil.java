package com.yjkj.chainup.extra_service.arouter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.yjkj.chainup.BuildConfig;
import com.yjkj.chainup.db.constant.ParamConstant;
import com.yjkj.chainup.db.constant.RoutePath;
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil;
import com.yjkj.chainup.extra_service.eventbus.MessageEvent;
import com.yjkj.chainup.util.Utils;

/*
 * alibaba arouter
 */
public class ArouterUtil {

    public static void init(Application context) {
        if (BuildConfig.DEBUG) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
            ARouter.printStackTrace();
        }
        ARouter.init(context); // As early as possible, it is recommended to initialize in the Application
    }

    /*
     * Arouter注入方法
     */
    public static void inject(Object object) {
        ARouter.getInstance().inject(object);
    }


    /*
     * 拦截器模式路由,如登录
     */
    public static void navigation(String path, Bundle bundle) {
        forward(true, path, bundle);
    }

    /**
     * ARouter实现startActivityForResult
     * @param path
     * @param bundle
     * @param targetActivity
     * @param requestCode
     */
    public static void navigation4Result(String path, Bundle bundle, Activity targetActivity, int requestCode){
        ARouter.getInstance().build(path).with(bundle).navigation(targetActivity,requestCode);
    }


    /*
     * 无拦截器模式路由
     */

    public static void greenChannel(String path, Bundle bundle) {
        forward(false, path, bundle);
    }

    private static void forward(boolean isNavigation,String path, Bundle bundle) {
        if (isNavigation) {
            ARouter.getInstance().build(path).with(bundle).navigation();
        } else {
            ARouter.getInstance().build(path).with(bundle).greenChannel().navigation();
        }
    }

    /*
     * A,B,C,D跳转，D跳往A
     */
    public static void greenChannelWithFlags(String path,Bundle bundle){
        int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK;
        ARouter.getInstance().build(path).with(bundle).withFlags(flag).greenChannel().navigation();
    }


    /*
     * KLine页面跳转
     */
    public synchronized static void forwardKLine(String symbol) {
        if(!Utils.isFastClick()){
            Bundle bundle = new Bundle();
            bundle.putString(ParamConstant.symbol, symbol);
            bundle.putString(ParamConstant.TYPE,ParamConstant.BIBI_INDEX);
            ArouterUtil.navigation(RoutePath.MarketDetail4Activity, bundle);
        }
    }

    /*
     * KLine页面跳转
     */
    public synchronized static void forwardKLine(String symbol,String type) {
        if(!Utils.isFastClick()){
            Bundle bundle = new Bundle();
            bundle.putString(ParamConstant.symbol, symbol);
            bundle.putString(ParamConstant.TYPE,type);
            ArouterUtil.navigation(RoutePath.MarketDetail4Activity, bundle);
        }
    }



    /*
     * 跳转到设置或修改密码界面
     */
    public static void forwardModifyPwdPage(String taskType, String taskFrom) {
        Bundle bundle = new Bundle();
        bundle.putString(ParamConstant.taskType, taskType);
        bundle.putString(ParamConstant.taskFrom, taskFrom);
        ArouterUtil.navigation(RoutePath.SetOrModifyPwdActivity, bundle);
    }

    /**
     * 划转页面
     */
    public static void forwardTransfer(String transferStatus, String transferSymbol) {
        Bundle bundle = new Bundle();
        bundle.putString(ParamConstant.TRANSFERSTATUS, transferStatus);
        bundle.putString(ParamConstant.TRANSFERSYMBOL, transferSymbol);
        ArouterUtil.navigation(RoutePath.NewVersionTransferActivity, bundle);
    }

    public static void refreshWebview() {
        MessageEvent msg_event = new MessageEvent(MessageEvent.webview_refresh_type);
        EventBusUtil.post(msg_event);
    }
}
