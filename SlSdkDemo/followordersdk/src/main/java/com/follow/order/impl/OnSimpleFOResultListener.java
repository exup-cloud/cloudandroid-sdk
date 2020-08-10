package com.follow.order.impl;

import com.follow.order.FollowOrderSDK;

/**
 * @time: 2020/4/7
 * @author: guodong
 */
public class OnSimpleFOResultListener implements OnFOResultListener {
    @Override
    public void onSuccess(String result) {

    }

    @Override
    public void onFailed(int code, String message) {
        if (code == 10) {//登录
            FollowOrderSDK.ins().getFollowOrderProxy().toLogin();
        }
    }
}
