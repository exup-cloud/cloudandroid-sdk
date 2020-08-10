package com.yjkj.chainup.util;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;

import static com.yjkj.chainup.app.ChainUpApp.appContext;


public class UIUtils {


    public static void showToast(String msg) {
        ToastUtils.showToast(appContext, msg);
    }


    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //沉浸式状态栏
    public static void translucentBar(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            //设置状态栏透明，activity全屏显示
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN:Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            // SYSTEM_UI_FLAG_LAYOUT_STABLE:防止系统栏隐藏时内容区域大小发生变化
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            setStatusBarColor(activity, statusColor);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int statusColor) {
        activity.getWindow().setStatusBarColor(statusColor);
    }

    //设置虚拟按键透明
    public static void translucentNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | localLayoutParams.flags);
        }
    }


    //显示或隐藏状态栏
    public static void isShowStatusBar(Activity activity, boolean show) {
//        显示和隐藏方法最好对应使用，这些方法都是不同版本时期使用的，优先级可能不同，不对号使用可能不会产生作用
        if (show) {
//            如果是动态且频繁的操作状态栏，建议使用方法1，SYSTEM_UI_FLAG_LAYOUT_STABLE标签能防止内容区域大小发生变化引起画面晃动
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            方法2
//            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
//            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            activity.getWindow().setAttributes(attr);
            //如果不注释下面这句话，状态栏将把界面挤下去
            /*getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/
//            方法3
            //下面方法也能实现显示状态栏效果，但是过度不是很自然
//            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    //该属性可是实现沉浸式的状态栏，点击状态栏，状态栏出现后一段时间后会自动消失，常用于视频和游戏
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            方法2  该方法会使虚拟按键也变成透明
//            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//            //隐藏状态栏
//            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            activity.getWindow().setAttributes(lp);
//            activity.getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //方法3
            //下面方法也能实现显示状态栏效果，但是过度不是很自然
//            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
