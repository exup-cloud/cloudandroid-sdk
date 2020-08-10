package com.follow.order.presenter;

import com.follow.order.base.BasePresenterImpl;
import com.follow.order.bean.MenuBean;
import com.follow.order.bean.TipBean;
import com.follow.order.net.RxCallback;
import com.follow.order.net.RxRetrofitClient;
import com.follow.order.presenter.contract.FollowOrderContract;

import java.util.List;

public class FollowOrderPresenter extends BasePresenterImpl<FollowOrderContract.View> implements FollowOrderContract.Presenter {

    @Override
    public void getMenuData() {
        RxRetrofitClient.getInstance().getKolStyle(new RxCallback<List<MenuBean>>(getView().getMyActivity()) {
            @Override
            public void onSuccess(List<MenuBean> menuData) {
                getView().showMenuData(menuData);
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }

    @Override
    public void getCoinList() {
        RxRetrofitClient.getInstance().getKolCoinList(new RxCallback<List<MenuBean>>(getView().getMyActivity()) {
            @Override
            public void onSuccess(List<MenuBean> coinBean) {
                getView().showCoinList(coinBean);
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }

    @Override
    public void getCommonDialog() {
        RxRetrofitClient.getInstance().getCommonDialog(new RxCallback<TipBean>(getView().getMyActivity()) {
            @Override
            public void onSuccess(TipBean tipData) {
                getView().showCommonDialog(tipData);
            }

            @Override
            public void onFinished() {
                super.onFinished();
            }
        });
    }
}
