package com.follow.order;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.TypedValue;

import com.follow.order.constant.FOTheme;
import com.follow.order.impl.FollowOrderListener;
import com.follow.order.impl.SimpleFollowOrderListener;
import com.follow.order.net.RxCallback;
import com.follow.order.net.RxRetrofitClient;
import com.follow.order.ui.activity.FollowOrderActivity;
import com.follow.order.utils.DensityUtil;
import com.follow.order.utils.DeviceUtil;
import com.follow.order.utils.LogUtil;
import com.follow.order.utils.RSAUtils;
import com.follow.order.utils.ShareUtil;

import java.util.HashMap;

public class FollowOrderSDK {
    private static volatile FollowOrderSDK instance = null;
    private Application application;
    private boolean isDebug = false;
    private FOTheme curTheme = FOTheme.LIGHT;
    private FollowOrderListener orderProxy;
    public static final boolean IS_ENCRY = true;

    public static FollowOrderSDK ins() {
        if (instance == null) {
            synchronized (FollowOrderSDK.class) {
                if (instance == null) {
                    instance = new FollowOrderSDK();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        this.application = application;
        DensityUtil.init(application);
        ShareUtil.initUtil(application);
        setDebug(true);
        initHttp();
    }

    public Application getApplication() {
        return application;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        LogUtil.setDebug(isDebug);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public String getString(int resId) {
        return getApplication().getString(resId);
    }

    public void setTheme(FOTheme theme) {
        this.curTheme = theme;
    }

    public FOTheme getTheme() {
        return curTheme;
    }

    public void setFollowOrderProxy(FollowOrderListener proxy) {
        this.orderProxy = proxy;
    }

    public FollowOrderListener getFollowOrderProxy() {
        if (orderProxy == null) {
            orderProxy = new SimpleFollowOrderListener();
        }
        return orderProxy;
    }

    public void toFollowOrderView(Activity activity) {
        if (getFollowOrderProxy().checkLogin(activity)) {
            FollowOrderActivity.start(activity);
        }
    }

    public void initHttp() {
        RxRetrofitClient.getInstance().getPublicKey(new RxCallback<HashMap<String, String>>(null) {
            @Override
            public void onSuccess(HashMap<String, String> result) {
                if (result != null) {
                    FollowOrderSDK.ins().setPublicKey(result.get("publicKey"));
                    FollowOrderSDK.ins().setPrivateKey(result.get("privateKey"));
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }

    public void setPublicKey(String publicKey) {
        ShareUtil.putString("fo_public_key", publicKey);
    }

    public void setPrivateKey(String privateKey) {
        ShareUtil.putString("fo_private_key", privateKey);
    }

    public String getPublicKey() {
        return ShareUtil.getString("fo_public_key", "");
    }

    public String getPrivateKey() {
        return ShareUtil.getString("fo_private_key", "");
    }

    public String getDeviceId(Context context) {
        return DeviceUtil.getDeviceId(context);
    }

    public int getCustomAttrColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        Resources resources = application.getResources();
        try {
            theme.resolveAttribute(attr, typedValue, true);
            return ResourcesCompat.getColor(resources, typedValue.resourceId, null); // 获取颜色值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Drawable getCustomAttrDrawable(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        Resources resources = application.getResources();
        try {
            theme.resolveAttribute(attr, typedValue, true);
            return ResourcesCompat.getDrawable(resources, typedValue.resourceId, null); // 获取图片
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCustomAttrResId(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        Resources resources = application.getResources();
        try {
            theme.resolveAttribute(attr, typedValue, true);
            return typedValue.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 解密返回数据
     *
     * @param response
     * @return
     */
    public String decryptResponse(String response) {
        if (IS_ENCRY && !TextUtils.isEmpty(response)) {
            response = RSAUtils.publicDecrypt(response, RSAUtils.getPublicKey(FollowOrderSDK.ins().getPublicKey()));
            LogUtil.json(response);
            return response;
        }
        LogUtil.json(response);
        return response;
    }

    /**
     * 加密参数
     *
     * @param params
     * @return
     */
    public String encryptParams(String params) {
        if (IS_ENCRY && !TextUtils.isEmpty(params)) {
            LogUtil.d("加密前=" + params);
            params = RSAUtils.publicEncrypt(params, RSAUtils.getPublicKey(FollowOrderSDK.ins().getPublicKey()));
//            LogUtil.d("加密=" + params);
//            LogUtil.d("解密=" + FollowOrderSDK.ins().decryptParams(params));
            return params;
        }
        return params;
    }

    public String decryptParams(String params) {
        if (IS_ENCRY && !TextUtils.isEmpty(params)) {
            return RSAUtils.privateDecrypt(params, RSAUtils.getPrivateKey(FollowOrderSDK.ins().getPrivateKey()));
        }
        return params;
    }
}
