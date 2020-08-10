package com.follow.order.presenter;

import android.text.TextUtils;

import com.follow.order.FollowOrderSDK;
import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.BaseBean;
import com.follow.order.bean.FollowOptionBean;
import com.follow.order.bean.UserCoinBalanceBean;
import com.follow.order.impl.OnFOResultListener;
import com.follow.order.impl.OnSimpleFOResultListener;
import com.follow.order.net.RxCallback;
import com.follow.order.net.RxRetrofitClient;
import com.follow.order.presenter.contract.FollowSetupContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class FollowSetupPresenter extends BasePresenterImpl<FollowSetupContract.View> implements FollowSetupContract.Presenter {

    @Override
    public void getFollowOption(String master_currency_id) {
        getView().showProgressDialog();
        FollowOrderSDK.ins().getFollowOrderProxy().getFollowOption(master_currency_id, new OnSimpleFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                try {
                    result = FollowOrderSDK.ins().decryptResponse(result);
                    BaseBean<FollowOptionBean> baseBean = new Gson().fromJson(result, new TypeToken<BaseBean<FollowOptionBean>>() {
                    }.getType());
                    if (baseBean != null) {
                        if (baseBean.getCode() == BaseBean.SUCCESS) {
                            getView().showFollowOption(baseBean.getData());
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
                getView().dissMissProgressDialog();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }
                super.onFailed(code, message);

            }
        });
    }

    @Override
    public void getAccountBalance(final String coinSymbols) {
        FollowOrderSDK.ins().getFollowOrderProxy().getAccountBalance(coinSymbols, new OnFOResultListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    UserCoinBalanceBean balanceBean = new Gson().fromJson(result, new TypeToken<UserCoinBalanceBean>() {
                    }.getType());
                    if (balanceBean != null && balanceBean.getAllCoinMap() != null) {
                        UserCoinBalanceBean.InfoBean infoBean = balanceBean.getAllCoinMap().get(coinSymbols);
                        if (infoBean != null) {
                            getView().showAccountBalance(infoBean.getNormal_balance());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int code, String message) {

            }
        });
    }

    @Override
    public void convertUsdt(String symbol, String amount) {
        RxRetrofitClient.getInstance().convertUsdt(symbol, amount, new RxCallback<HashMap<String, String>>(getView().getMyActivity()) {
            @Override
            public void onSuccess(HashMap<String, String> object) {
                if (object != null) {
                    String price = object.get("price");
                    getView().showUsdt(price);
                }
            }

            @Override
            public void onFinished() {
                super.onFinished();
//                    getView().dissMissProgressDialog();
            }
        });
    }

    @Override
    public void startFollow(String trade_currency_id, String uid, String exchange, String total, int is_stop_deficit, String stop_deficit, int is_stop_profit, String stop_profit, int follow_immediately, String symbol, String currency, String trade_currency) {
        getView().showProgressDialog();
        FollowOrderSDK.ins().getFollowOrderProxy().startFollow(trade_currency_id, uid, exchange, total, is_stop_deficit, stop_deficit, is_stop_profit, stop_profit, symbol, currency, trade_currency, follow_immediately, new OnFOResultListener() {
            @Override
            public void onSuccess(String result) {
                getView().dissMissProgressDialog();
                getView().startFollowSuccess();
            }

            @Override
            public void onFailed(int code, String message) {
                getView().dissMissProgressDialog();
                if (!TextUtils.isEmpty(message)) {
                    getView().toast(message);
                }

            }
        });
    }
}
