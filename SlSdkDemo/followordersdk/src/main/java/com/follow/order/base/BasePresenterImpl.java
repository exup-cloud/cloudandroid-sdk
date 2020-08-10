package com.follow.order.base;

import java.lang.reflect.Proxy;

/**
 *
 */

public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {
    private V mView;
    private Class<?>[] interfaces;

    @Override
    public void attachView(V view) {
        mView = view;
        interfaces = view.getClass().getInterfaces();
    }

    /**
     * 解决 mview为空，省去外部判断null
     *
     * @return
     */
    @Override
    public V getView() {
        return (V) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new NoOpProxy(mView));
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public String getString(int id) {
        return getView().getMyActivity().getString(id);
    }
}
