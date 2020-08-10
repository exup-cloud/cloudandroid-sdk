package com.follow.order.presenter.contract;


import android.graphics.Bitmap;

import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.bean.UserFinanceProfileBean;

import java.util.List;

public class OrderDetailContract {
    public interface View extends BaseView {

        void shareBitmap(Bitmap bitmap);

        void showOrderDetail(OrderBean orderBean);

        void showTrendData(List<UserFinanceProfileBean.ProfitHistoryBean> chartData);

        void showFollowShareInfo(OrderShareBean shareData);

        void stopFollowSuccess();
    }

    public interface Presenter extends BasePresenter<View> {


        void getOrderDetail(String follow_id);

        void getTrendData(String follow_id);

        void getFollowShareInfo(String follow_id);

        void stopFollow(String follow_id, String uid, String exchange);

    }
}
