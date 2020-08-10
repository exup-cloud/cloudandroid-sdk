package com.follow.order.net;

import android.app.Activity;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseActivity;
import com.follow.order.utils.CommonUtils;
import com.follow.order.utils.ToastUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 暴露给最上层的网络请求回调处理类
 *
 * @param <T>
 */
public abstract class RxCallback<T> implements Observer<T> {

    public Activity mActivity;
    public Disposable mDisposable;

    public RxCallback(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        if (!NetworkUtil.isNetworkAvailable()) {
            //没网，我们不再做网络请求了。
            CommonUtils.unRegisterDisposable(mDisposable);
            onComplete();
            return;
        }

        if (mActivity != null) {
            if (mActivity instanceof MVPBaseActivity) {
                ((MVPBaseActivity) mActivity).registerSubscriber(mDisposable);
            }
        }
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public final void onComplete() {
        if (mActivity != null) {
            if (mActivity instanceof MVPBaseActivity) {
                ((MVPBaseActivity) mActivity).removeSubscriber(mDisposable);
            }
        }
        if (mActivity == null || mActivity.isFinishing()) {
            try {
                onFinished();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
        onFinished();
    }

    @Override
    public final void onError(Throwable e) {
        e.printStackTrace();
        String errorMsg;
        int errorCode = -1;
        if (e instanceof IOException) {
            if (e instanceof SocketTimeoutException) {

                errorMsg = FollowOrderSDK.ins().getString(R.string.fo_error_timeout);
            } else {
                /** 没有网络 */
//            errorMsg = "Please check your network status";
                errorMsg = FollowOrderSDK.ins().getString(R.string.fo_error_api);
            }

        } else if (e instanceof HttpException) {

            /** 网络异常，http 请求失败，即 http 状态码不在 [200, 300) 之间, such as: "server internal error". */
            errorMsg = ((HttpException) e).response().message();
            errorMsg = FollowOrderSDK.ins().getString(R.string.fo_error_api);
        } else if (e instanceof ApiException) {
            errorCode = ((ApiException) e).getErrorCode();
            /** 网络正常，http 请求成功，服务器返回逻辑错误 */
            errorMsg = e.getMessage();
            if (dealSpecialAPIError((ApiException) e)) {
                onComplete();
                return;
            }
        } else {
            /** 其他未知错误 */
//            errorMsg = !TextUtils.isEmpty(e.getMessage()) ? e.getMessage() : "unknown error";
            errorMsg = FollowOrderSDK.ins().getString(R.string.fo_error_api);
        }

        if (!onFailed(errorMsg, errorCode)) {
            try {
                final String finalErrorMsg = errorMsg;
                if (mActivity != null && !mActivity.isFinishing()) {
                    ToastUtil.updateUI(finalErrorMsg);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        onFinished();
    }

    /**
     * 成功返回结果时被调用
     *
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 成功或失败到最后都会调用
     */
    public void onFinished() {

    }

    /**
     * @param errorMsg
     * @return 为true的话，将不再处理error。
     */
    public boolean onFailed(String errorMsg, int errorCode) {
        return false;
    }

    /**
     * 是否需要开启错误处理流程
     *
     * @param e
     * @return
     */
    public boolean isBeginErrorFlow(Throwable e) {
        return true;

    }

    private boolean dealSpecialAPIError(ApiException e) {
        boolean isFinish = false;
        if (e.getErrorCode() == 10) {//登录
            FollowOrderSDK.ins().getFollowOrderProxy().toLogin();
            isFinish = true;
        }
        return isFinish;
    }


}