package com.follow.order.impl;

/**
 * @time: 2020/3/25
 * @author: guodong
 */
public interface OnFOResultListener {

    void onSuccess(String result);

    void onFailed(int code, String message);
}
