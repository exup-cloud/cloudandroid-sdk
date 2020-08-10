package com.follow.order.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.utils.ToastUtil;
import com.follow.order.utils.TradeProgressUtils;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected BaseActivity activity;
    private TradeProgressUtils tradeProgressUtils;
    private boolean mNeedInitScreen = true;
    private boolean mNeedInit = true;


    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (mNeedInitScreen) {
            // 去掉标题栏
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //屏蔽所有页面的横屏
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
                boolean result = fixOrientation();
            }
        }
        super.onCreate(savedInstanceState);
        initStatus();
        activity = this;
        if (mNeedInit) {
            /**
             *  初始化View
             */
            initView();
            /**
             *  初始化监听
             */
            initListener();

            /**
             * 请求处理数据
             */
            initData();
        }

    }

    protected void initStatus() {
        switch (FollowOrderSDK.ins().getTheme()) {
            case LIGHT:
                setTheme(R.style.ThemeLight);
                StatusBarUtil.setLightMode(this);
                break;
            case DARK:
                setTheme(R.style.ThemeDark);
                StatusBarUtil.setDarkMode(this);
                break;
        }
        StatusBarUtil.setColorNoTranslucent(this, FollowOrderSDK.ins().getCustomAttrColor(this, R.attr.fo_status_bg_color));

    }


    @Override
    protected void onDestroy() {
        missProgressDialog();
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void back() {
        finish();
    }

    public View inflate(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return inflater.inflate(layoutId, null);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public void onClick(View v) {

    }

    protected void setNeedInit(boolean needeedInit) {
        this.mNeedInit = needeedInit;
    }

    protected void setNeedInitScreen(boolean needeedInitScreen) {
        this.mNeedInitScreen = needeedInitScreen;
    }


    /**
     * 初始化布局和布局的控件
     */
    protected abstract void initView();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    int progressDialogShowTimes = 0;

    public void showProgressDialog() {
        if (!isFinishing()) {
            if (progressDialogShowTimes < 1) {
                getProgressDialog().showProgress(activity);
            }
            progressDialogShowTimes++;

        }
    }

    public void dissMissProgressDialog() {
        if (!isFinishing()) {
            if (progressDialogShowTimes >= 1) {
                progressDialogShowTimes--;
            }

            if (progressDialogShowTimes < 1) {
                missProgressDialog();
            }
        }
    }

    public void missProgressDialog() {
        getProgressDialog().dismiss();
    }

    public TradeProgressUtils getProgressDialog() {
        if (tradeProgressUtils == null)
            tradeProgressUtils = new TradeProgressUtils();
        return tradeProgressUtils;
    }

    public void toast(String toastContent) {
        if (TextUtils.isEmpty(toastContent)) {
            return;
        }
        ToastUtil.updateUI(activity, toastContent);
    }

    public BaseActivity getMyActivity() {
        return activity;
    }

}