package com.follow.order.presenter.contract;


import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.FollowProfitBean;

public class MyOrderContract {
    public interface View extends BaseView {
        void showFollowProfit(FollowProfitBean infoBean);
    }

    public interface Presenter extends BasePresenter<View> {

        void getFollowProfit();
    }
}
