package com.yjkj.chainup.manager;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.bean.LoginInfo;
import com.yjkj.chainup.bean.SymbolData;
import com.yjkj.chainup.db.constant.RoutePath;
import com.yjkj.chainup.db.service.UserDataService;
import com.yjkj.chainup.extra_service.arouter.ArouterUtil;
import com.yjkj.chainup.extra_service.eventbus.MessageEvent;
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil;
import com.yjkj.chainup.model.model.MainModel;
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver;
import com.yjkj.chainup.new_version.activity.login.TouchIDFaceIDActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import static com.yjkj.chainup.new_version.activity.login.GesturesPasswordActivity.SET_LOGINANDSET;
import static com.yjkj.chainup.new_version.activity.login.GesturesPasswordActivity.SET_STATUS;
import static com.yjkj.chainup.new_version.activity.login.GesturesPasswordActivity.SET_TOKEN;
import static com.yjkj.chainup.new_version.activity.login.GesturesPasswordActivity.SET_TYPE;


/**
 * TODO 优化...
 */
public class LoginManager {
    public static final String TAG = "LoginManager";


    public static final String SP_SYMBOL = "SYMBOL";

    public static final String SP_LOGIN_PWD = "LOGIN__PWD";

    /**
     * 指纹密码
     */
    public static final String FINGERPRINT_STATE = "fingerprint_state";


    /**
     * 登录信息
     */
    public static final String LOGIN_INFO = "LOGIN_INFO";


    /**
     * 手势密码的错误次数
     */
    public static final String GESTURE_PWD_ERROR_TIMES = "Times_gesture_pwd_error";


    private static volatile LoginManager mInstance;

    //    private UserInfo mUser;
    private SymbolData symbolData;
    private SharedPreferences mPref;


    private LoginManager(Context context) {
        mPref = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    public static LoginManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LoginManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public static LoginManager getInstance() {
        if (mInstance == null) {
            mInstance = new LoginManager(ChainUpApp.appContext);
        }
        return mInstance;
    }


    public void saveGesturePwdErrorTimes(int times) {
        mPref.edit().putInt(GESTURE_PWD_ERROR_TIMES, times).apply();
    }

    public int getGesturePwdErrorTimes() {
        return mPref.getInt(GESTURE_PWD_ERROR_TIMES, 5);
    }


    /**
     * 指纹识别
     *
     * @param finterstate
     */
    public void saveFingerprint(int finterstate) {
        mPref.edit().putInt(FINGERPRINT_STATE, finterstate).apply();
    }

    public int getFingerprint() {
        return mPref.getInt(FINGERPRINT_STATE, 0);
    }

    public SymbolData getSymbol() {
        if (symbolData != null) {
            return symbolData;
        }
        String data = mPref.getString(SP_SYMBOL, null);
        symbolData = new Gson().fromJson(data, SymbolData.class);
        return symbolData;
    }

    public void saveLoginPwd(String info) {
        mPref.edit().putString(SP_LOGIN_PWD, info).apply();
    }


    public String getLoginPwd() {
        return mPref.getString(SP_LOGIN_PWD, null);
    }


    /**
     * 保存登录信息
     *
     * @param info
     */
    public void saveLoginInfo(LoginInfo info) {
        mPref.edit().putString(LOGIN_INFO, new Gson().toJson(info, LoginInfo.class)).apply();
    }

    /**
     * 获取登录信息
     * 确保不能为空
     * //TODO 需要验证为什么会报错：Expected BEGIN_OBJECT but was STRING at line 1 column 1
     *
     * @return
     */
    public LoginInfo getLoginInfo() {
        String string = mPref.getString(LOGIN_INFO, null);
        LoginInfo loginInfo;
        if (TextUtils.isEmpty(string)) {
            loginInfo = new LoginInfo();
        } else {
            try {
                loginInfo = new Gson().fromJson(string, LoginInfo.class);
                return loginInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new LoginInfo();
    }

    public static void postValue(boolean b) {
        MessageEvent msgEvent = new MessageEvent(MessageEvent.login_operation_type);
        msgEvent.setMsg_content(b);
        NLiveDataUtil.postValue(msgEvent);
    }

    private static void checkoutPwd(Context context) {
        LoginInfo userInfo = getInstance().getLoginInfo();

        MainModel mainModel = new MainModel();

        Activity a = null;
        if (null != context && context instanceof Activity) {
            a = (Activity) context;
        }
        mainModel.checkLocalPwd(String.valueOf(UserDataService.getInstance().getUserInfo4UserId()), userInfo.getLoginPwd(), new NDisposableObserver(a, false) {
            @Override
            public void onResponseSuccess(@NotNull JSONObject jsonObject) {

                JSONObject json = jsonObject.optJSONObject("data");

                if (json == null || json.isNull("isPass")) {
                    ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                    return;
                }
                int isPass = json.optInt("isPass");
                if (isPass == 0) {
                    if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePass()) || (UserDataService.getInstance().getUserData() != null && !TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd()))) {
                        getInstance().saveFingerprint(0);
                        if (TextUtils.isEmpty(UserDataService.getInstance().getGesturePass())) {
                            UserDataService.getInstance().saveGesturePass(UserDataService.getInstance().getGesturePwd());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt(SET_TYPE, 1);
                        bundle.putString(SET_TOKEN, "");
                        bundle.putBoolean(SET_STATUS, true);
                        bundle.putBoolean(SET_LOGINANDSET, false);
                        ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
                    } else {
                        ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", TouchIDFaceIDActivity.FINGERPRINT);
                    bundle.putBoolean("is_first_login", false);
                    ArouterUtil.navigation("/login/touchidfaceidactivity", bundle);

                }
            }

            @Override
            public void onResponseFailure(int code, @Nullable String msg) {
                super.onResponseFailure(code, msg);
                UserDataService.getInstance().saveData(new JSONObject());
                UserDataService.getInstance().saveGesturePass("");
                ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
            }
        });
    }

    public static boolean checkLogin(Context context, boolean startLogin) {
        boolean isLogin = UserDataService.getInstance().isLogined();
        if (!isLogin) {
            if (startLogin) {
                JSONObject jsonObject = UserDataService.getInstance().getUserData();
                if (null == jsonObject) {
                    ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                    postValue(false);
                    return false;
                }

                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
                String gesturePwd = jsonObject.optString("gesturePwd");
                if (fingerprintManager.isHardwareDetected()) {
                    if (fingerprintManager.hasEnrolledFingerprints() && getInstance().getFingerprint() == 1) {
                        checkoutPwd(context);
                        postValue(false);
                        return false;
                    }
                }

                String gesturePass = UserDataService.getInstance().getGesturePass();
                boolean hasGesture = !TextUtils.isEmpty(gesturePass);

                if (hasGesture || !TextUtils.isEmpty(gesturePwd)) {
                    getInstance().saveFingerprint(0);
                    if (hasGesture) {
                        gesturePwd = gesturePass;
                    } else {
                        UserDataService.getInstance().saveGesturePass(gesturePwd);
                    }

                    String id = UserDataService.getInstance().getUserInfo4UserId();

                    Activity a = null;
                    if (null != context && context instanceof Activity) {
                        a = (Activity) context;
                    }
                    new MainModel().getGesturePwd(id, gesturePwd, new NDisposableObserver(a, false) {
                        @Override
                        public void onResponseSuccess(@NotNull JSONObject jsonObject) {
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (null != data) {
                                String isPass = data.optString("isPass");
                                if ("1".equals(isPass)) {
                                    // 手势密码不为空，进入手势锁编辑界面...
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("SET_TYPE", 1);
                                    bundle.putString("SET_TOKEN", "");
                                    bundle.putBoolean("SET_STATUS", true);
                                    bundle.putBoolean("SET_LOGINANDSET", false);
                                    ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
                                    postValue(false);
                                    return;
                                }
                            }
                            ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                        }

                        @Override
                        public void onResponseFailure(int code, @Nullable String msg) {
                            super.onResponseFailure(code, msg);
                            UserDataService.getInstance().saveData(new JSONObject());
                            UserDataService.getInstance().saveGesturePass("");
                            ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                        }
                    });
                } else {
                    ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
                }
            }
            postValue(false);
            return false;
        }
        postValue(true);
        return true;
    }

}
