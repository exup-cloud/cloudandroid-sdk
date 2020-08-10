package com.follow.order.presenter;

import android.text.TextUtils;

import com.follow.order.FollowOrderSDK;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.BaseBean;
import com.follow.order.bean.FollowProfitBean;
import com.follow.order.impl.OnSimpleFOResultListener;
import com.follow.order.presenter.contract.MyOrderContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class MyOrderPresenter extends BasePresenterImpl<MyOrderContract.View> implements MyOrderContract.Presenter {

    @Override
    public void getFollowProfit() {
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowProfit(new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
//                    getView().dissMissProgressDialog();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<FollowProfitBean> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<FollowProfitBean>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showFollowProfit(baseBean.getData());
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
//                    getView().dissMissProgressDialog();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }
                super.onFailed(code, message);
            }
        });
    }
}
