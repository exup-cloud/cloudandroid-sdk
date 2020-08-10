package com.follow.order.presenter;

import com.chainup.http.HttpClientExt;
import com.chainup.net.retrofit.NetObserver;
import com.chainup.net.util.JsonUtils;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.bean.PersonalInfoBean;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.net.RxCallback;
import com.follow.order.net.RxRetrofitClient;
import com.follow.order.presenter.contract.PersonalContract;
import com.follow.order.utils.LogUtil;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PersonalPresenter extends BasePresenterImpl<PersonalContract.View> implements PersonalContract.Presenter {

    private boolean isChainup = false;

    @Override
    public void getPersonalUserInfo(final String kol_id) {
        getView().showProgressDialog();
        RxRetrofitClient.getInstance().getPersonalUserInfo(kol_id, new RxCallback<PersonalInfoBean>(getView().getMyActivity()) {
            @Override
            public void onSuccess(PersonalInfoBean infoBean) {
                getView().showPersonalUserInfo(infoBean);
                isChainup = infoBean.isChainUpUID();
                if (isChainup) {
                    getLiveFinanceProfile(kol_id, infoBean.getChainup_uid());
                } else {
                    getExchangeApiList(infoBean.getUid());
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
                getView().dissMissProgressDialog();
            }
        });
    }

    @Override
    public void getExchangeApiList(String uid) {

        RxRetrofitClient.getInstance().getExchangeApiList(uid, new RxCallback<List<ExchangeBean>>(getView().getMyActivity()) {
            @Override
            public void onSuccess(List<ExchangeBean> data) {
                getView().showExchangeData(data);
            }

            @Override
            public void onFinished() {
                super.onFinished();
                getView().dissMissProgressDialog();
            }
        });
    }

    @Override
    public void getLiveFinanceProfile(String uid, String api_id) {
        if(isChainup){
            HttpClientExt.Companion.getInstance().getLiveInfo(api_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetObserver<JsonObject>() {
                        @Override
                        protected void onHandleError(int code, String msg) {
                            super.onHandleError(code, msg);
                        }

                        @Override
                        protected void onHandleSuccess(JsonObject jsonObject) {
                            LogUtil.d("LogUtils", "onHandleSuccess " + jsonObject.toString());
                            String any = jsonObject.toString();
                            UserFinanceProfileBean bean = JsonUtils.INSTANCE.jsonToBean(any, UserFinanceProfileBean.class);
                            if (bean != null) {
                                getView().showLiveFinanceProfile(bean);
                            }
                        }
                    });
        }else{
            RxRetrofitClient.getInstance().getLiveFinanceProfile(uid, api_id, new RxCallback<UserFinanceProfileBean>(getView().getMyActivity()) {
                @Override
                public void onSuccess(UserFinanceProfileBean userFinanceProfileBean) {

                    getView().showLiveFinanceProfile(userFinanceProfileBean);
                }

                @Override
                public void onFinished() {
                    super.onFinished();
                    getView().dissMissProgressDialog();
                }
            });
        }
    }

}
