package com.follow.order.presenter;

import android.text.TextUtils;

import com.follow.order.FollowOrderSDK;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.BaseBean;
import com.follow.order.bean.FollowBean;
import com.follow.order.bean.ListData;
import com.follow.order.impl.OnSimpleFOResultListener;
import com.follow.order.presenter.contract.FollowListContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class FollowListPresenter extends BasePresenterImpl<FollowListContract.View> implements FollowListContract.Presenter {

    @Override
    public void getKolList(String sort, String style, String currency, int just_show_follow, int page) {
        FollowOrderSDK.ins().getFollowOrderProxy().getKolList(sort, style, currency, just_show_follow, page, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                getView().refreshComplete();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<ListData<FollowBean>> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<ListData<FollowBean>>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showKolData(baseBean.getData());
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

}
