package com.follow.order.presenter.contract;


import android.graphics.Bitmap;

import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.ListData;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;

public class OrderListContract {
    public interface View extends BaseView {
        void showOrderData(ListData<OrderBean> orderData);

        void showEmpty();

        void refreshComplete();

        void showFollowShareInfo(OrderShareBean shareData);

        void shareBitmap(Bitmap bitmap);
    }

    public interface Presenter extends BasePresenter<View> {
        void getFollowList(int status, int page);

        void getFollowShareInfo(String follow_id);
    }
}
