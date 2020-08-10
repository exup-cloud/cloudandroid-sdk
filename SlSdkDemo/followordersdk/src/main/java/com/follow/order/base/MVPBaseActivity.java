package com.follow.order.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;

import io.reactivex.disposables.Disposable;


/**
 *
 */

public abstract class MVPBaseActivity<V extends BaseView, T extends BasePresenterImpl<V>> extends BaseActivity implements BaseView {
    public T mPresenter;
    private LinkedList<Disposable> subscriberLinkedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
        subscriberLinkedList = new LinkedList<Disposable>();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    public void dissMissProgressDialog() {
        super.dissMissProgressDialog();
    }

    @Override
    public void toast(String toastContent) {
        super.toast(toastContent);
    }

    @Override
    public BaseActivity getMyActivity() {
        return super.getMyActivity();
    }

    @Override
    public void finish() {
        clearSubscribers();
        super.finish();
    }

    /**
     * 注册请求回调
     *
     * @param subscriber
     */
    public void registerSubscriber(Disposable subscriber) {
        if (subscriberLinkedList != null) {
            subscriberLinkedList.add(subscriber);
        }
    }

    /**
     * 移除请求回调
     */
    public void removeSubscriber(Disposable subscriber) {
        if (subscriberLinkedList != null && subscriberLinkedList.contains(subscriber)) {
            subscriberLinkedList.remove(subscriber);
        }
    }

    /*
     * 清空请求回调
     */
    public void clearSubscribers() {
        if (subscriberLinkedList != null && !subscriberLinkedList.isEmpty()) {
            /**
             * UPDATE by wanghui 2018/4/27上午10:00
             * 创建临时列表，防止下面remove出异常
             */
            LinkedList<Disposable> tempDisposable = new LinkedList<Disposable>(subscriberLinkedList);
            for (Disposable subscriber : tempDisposable) {
                if (subscriber != null && !subscriber.isDisposed()) {
                    subscriber.dispose();
                    subscriberLinkedList.remove(subscriber);
                }
            }
        }
    }
}
