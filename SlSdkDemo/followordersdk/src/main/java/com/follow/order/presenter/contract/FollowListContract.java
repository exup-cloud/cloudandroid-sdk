package com.follow.order.presenter.contract;


import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.FollowBean;
import com.follow.order.bean.ListData;

public class FollowListContract {
    public interface View extends BaseView {

        void showKolData(ListData<FollowBean> followData);

        void showEmpty();

        void refreshComplete();
    }

    public interface Presenter extends BasePresenter<View> {

        void getKolList(String sort, String style, String coin, int just_show_follow, int page);

    }
}
