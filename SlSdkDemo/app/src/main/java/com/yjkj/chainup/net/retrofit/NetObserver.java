package com.yjkj.chainup.net.retrofit;


import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.db.constant.ParamConstant;
import com.yjkj.chainup.db.service.UserDataService;
import com.yjkj.chainup.extra_service.arouter.ArouterUtil;
import com.yjkj.chainup.manager.LanguageUtil;
import com.yjkj.chainup.manager.LoginManager;
import com.yjkj.chainup.net.api.HttpResult;
import com.yjkj.chainup.util.NetworkUtils;

import org.json.JSONObject;

import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class NetObserver<T> implements Observer<HttpResult<T>> {
    public boolean isReportError = true;

    public NetObserver() {
        this.isReportError = true;
    }

    public NetObserver(boolean isReportError) {
        this.isReportError = isReportError;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(HttpResult<T> value) {
        if (value.isSuccess()) {
            if (value.hasData()) {
                T t = value.getData();
                onHandleSuccess(t);
                onHandleSuccess(t, value.getMsg());
            } else {
                onHandleSuccess(null);
            }
        } else {
            Log.e("dddd", "Throwable" + value.toString());
            if (isReportError) {
                if (TextUtils.isDigitsOnly(value.getCode())) {
                    onHandleError(Integer.parseInt(value.getCode()), value.getMsg());
                } else {
                    onHandleError(-1, value.getMsg());
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e("dddd", "Throwable" + e.toString());
        if (!NetworkUtils.isNetworkAvailable(ChainUpApp.appContext)) {
            onHandleError(LanguageUtil.getString(ChainUpApp.appContext,"warn_net_disconnect"));
            return;
        }
        if (e instanceof SocketException) {
            onHandleError(LanguageUtil.getString(ChainUpApp.appContext,"warn_net_exception"));
        } else if (e instanceof TimeoutException) {
            onHandleError(LanguageUtil.getString(ChainUpApp.appContext,"warn_request_timeout"));
        } else if (e instanceof JsonParseException) {
            Log.d("=====AA11===", e.getMessage());
            onHandleError(LanguageUtil.getString(ChainUpApp.appContext,"warn_data_parse_failed"));
        } else {
            onHandleError(LanguageUtil.getString(ChainUpApp.appContext,"warn_net_exception"));
        }
    }

    @Override
    public void onComplete() {

    }

    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
//        UIUtils.showToast(msg);
        onHandleError(-1, msg);
    }

    protected void onHandleSuccess(T t, String msg) {

    }

    protected void onHandleError(int code, String msg) {
        if (code == 10021 || code == 10002 || code == 3 || code == ParamConstant.QUICK_LOGIN_FAILURE) {
            UserDataService.getInstance().clearToken();
            JSONObject userinfo = UserDataService.getInstance().getUserData();
            if (null == userinfo) {
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
            } else {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ChainUpApp.appContext);
                if (fingerprintManager.isHardwareDetected()) {
                    /**
                     * 判断是否输入指纹
                     */
                    if (fingerprintManager.hasEnrolledFingerprints() && LoginManager.getInstance().getFingerprint() == 1) {
                        ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
                    } else if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePass()) || !TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd())) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("SET_TYPE", 1);
                        bundle.putString("SET_TOKEN", "");
                        bundle.putBoolean("SET_STATUS", true);
                        bundle.putBoolean("SET_LOGINANDSET", false);
                        ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
                    } else {
                        ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
                    }
                } else if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePass()) || !TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd())) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("SET_TYPE", 1);
                    bundle.putString("SET_TOKEN", "");
                    bundle.putBoolean("SET_STATUS", true);
                    bundle.putBoolean("SET_LOGINANDSET", false);
                    ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
                } else {
                    ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
                }
            }

//            Intent intent = new Intent(ChainUpApp.appContext, NewVersionLoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ChainUpApp.appContext.startActivity(intent);

        }
    }
}
