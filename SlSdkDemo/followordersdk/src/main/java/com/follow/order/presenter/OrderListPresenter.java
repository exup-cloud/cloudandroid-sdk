package com.follow.order.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.follow.order.FollowOrderSDK;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.BaseBean;
import com.follow.order.bean.ListData;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.impl.OnSimpleFOResultListener;
import com.follow.order.presenter.contract.OrderListContract;
import com.follow.order.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class OrderListPresenter extends BasePresenterImpl<OrderListContract.View> implements OrderListContract.Presenter {

    @Override
    public void getFollowList(int status, int page) {
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowList(status, page, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                getView().refreshComplete();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<ListData<OrderBean>> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<ListData<OrderBean>>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showOrderData(baseBean.getData());
                        } else {
                            onFailed(baseBean.getCode(), baseBean.getMsg());
                        }
                    } else {
                        getView().showEmpty();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getView().showEmpty();
                }
            }

            @Override
            public void onFailed(int code, String message) {
                getView().dissMissProgressDialog();
                getView().refreshComplete();
                getView().showEmpty();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }
                super.onFailed(code, message);
            }
        });
    }

    @Override
    public void getFollowShareInfo(String follow_id) {
        getView().showProgressDialog();
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowShare(follow_id, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<OrderShareBean> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<OrderShareBean>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showFollowShareInfo(baseBean.getData());
                        } else {
                            onFailed(baseBean.getCode(), baseBean.getMsg());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int code, String message) {
                getView().dissMissProgressDialog();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }
                super.onFailed(code, message);
            }
        });
    }

    public void share(final View shareView) {
        getView().showProgressDialog();
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                final Bitmap bitmapContent = ImageUtils.getViewBitmap(shareView);
                emitter.onNext(bitmapContent);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        getView().dissMissProgressDialog();
                        if (bitmap != null) {
                            getView().shareBitmap(bitmap);
                        }
                    }
                });
    }
}
