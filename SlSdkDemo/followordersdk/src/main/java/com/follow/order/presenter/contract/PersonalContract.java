package com.follow.order.presenter.contract;


import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.bean.PersonalInfoBean;
import com.follow.order.bean.UserFinanceProfileBean;

import java.util.List;

public class PersonalContract {
    public interface View extends BaseView {

        void showPersonalUserInfo(PersonalInfoBean infoBean);

        void showExchangeData(List<ExchangeBean> exchangeData);

        void showLiveFinanceProfile(UserFinanceProfileBean userFinanceProfileBean);

    }

    public interface Presenter extends BasePresenter<View> {

        void getPersonalUserInfo(String uid);

        void getExchangeApiList(String uid);

        void getLiveFinanceProfile(String uid, String api_id);

    }
}
