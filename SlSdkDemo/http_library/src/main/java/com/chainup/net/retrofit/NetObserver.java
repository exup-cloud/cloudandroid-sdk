package com.chainup.net.retrofit;


import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.chainup.net.api.HttpResult;
import com.google.gson.JsonParseException;

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
        onHandleError("network error");
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

    }
}
