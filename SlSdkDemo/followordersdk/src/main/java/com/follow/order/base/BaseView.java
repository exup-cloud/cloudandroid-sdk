package com.follow.order.base;

import android.content.Context;

public interface BaseView {
    Context getContext();

    BaseActivity getMyActivity();

    void toast(String string);

    void showProgressDialog();

    void dissMissProgressDialog();
}
