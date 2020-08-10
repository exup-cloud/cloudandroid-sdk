package com.follow.order.presenter;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.follow.order.FollowOrderSDK;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.BaseBean;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.impl.OnFOResultListener;
import com.follow.order.impl.OnSimpleFOResultListener;
import com.follow.order.presenter.contract.OrderDetailContract;
import com.follow.order.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderDetailPresenter extends BasePresenterImpl<OrderDetailContract.View> implements OrderDetailContract.Presenter {

    @Override
    public void getOrderDetail(String follow_id) {
        getView().showProgressDialog();
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowDetail(follow_id, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<OrderBean> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<OrderBean>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showOrderDetail(baseBean.getData());
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

    @Override
    public void getTrendData(String follow_id) {
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowTrend(follow_id, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<List<UserFinanceProfileBean.ProfitHistoryBean>> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<List<UserFinanceProfileBean.ProfitHistoryBean>>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showTrendData(baseBean.getData());
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
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }
                super.onFailed(code, message);
            }
        });
    }

    @Override
    public void getFollowShareInfo(String follow_id) {
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

    @Override
    public void stopFollow(String follow_id, String uid, String exchange) {
        getView().showProgressDialog();
        FollowOrderSDK.ins().getFollowOrderProxy().stopFollow(follow_id, uid, exchange, new OnFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                getView().stopFollowSuccess();
            }

            @Override
            public void onFailed(int code, String message) {
                getView().dissMissProgressDialog();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }

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
