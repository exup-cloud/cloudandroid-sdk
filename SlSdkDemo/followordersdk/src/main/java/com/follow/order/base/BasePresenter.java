package com.follow.order.base;


public interface BasePresenter<V extends BaseView> {
    void attachView(V view);

    V getView();

    void detachView();

    //方便的获取字符串
    String getString(int id);
}
