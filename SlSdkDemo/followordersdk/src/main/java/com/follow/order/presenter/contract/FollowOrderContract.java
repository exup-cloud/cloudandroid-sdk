package com.follow.order.presenter.contract;


import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.MenuBean;
import com.follow.order.bean.TipBean;

import java.util.List;

public class FollowOrderContract {
    public interface View extends BaseView {
        void showMenuData(List<MenuBean> menuData);

        void showCoinList(List<MenuBean> coinData);

        void showCommonDialog(TipBean tipData);
    }

    public interface Presenter extends BasePresenter<View> {

        void getMenuData();

        void getCoinList();

        void getCommonDialog();
    }
}
