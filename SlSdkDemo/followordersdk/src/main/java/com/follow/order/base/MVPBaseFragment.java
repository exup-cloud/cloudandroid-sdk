package com.follow.order.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.follow.order.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;


public abstract class MVPBaseFragment<V extends BaseView, T extends BasePresenterImpl<V>> extends BaseFragment implements BaseView {
    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void showProgressDialog() {
        if (activity == null) return;
        activity.showProgressDialog();
    }

    @Override
    public void dissMissProgressDialog() {
        if (activity == null) return;
        activity.dissMissProgressDialog();
    }

    @Override
    public void toast(String string) {
        if (TextUtils.isEmpty(string)) {
            return;
        }
        ToastUtil.updateUI(activity, string);
    }

    @Override
    public BaseActivity getMyActivity() {
        return activity;
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
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
