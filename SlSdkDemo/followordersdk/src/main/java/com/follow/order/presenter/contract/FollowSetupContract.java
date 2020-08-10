package com.follow.order.presenter.contract;


import com.follow.order.base.BasePresenter;
import com.follow.order.base.BaseView;
import com.follow.order.bean.FollowOptionBean;

public class FollowSetupContract {
    public interface View extends BaseView {
        void showFollowOption(FollowOptionBean optionBean);

        void showAccountBalance(String balance);

        void showUsdt(String usdt);

        void startFollowSuccess();
    }

    public interface Presenter extends BasePresenter<View> {

        void getFollowOption(String trade_currency_id);

        void getAccountBalance(String coinSymbols);

        void convertUsdt(String currency, String amount);

        void startFollow(String trade_currency_id, String uid, String exchange, String total, int is_stop_deficit, String stop_deficit, int is_stop_profit, String stop_profit, int follow_continue, String symbol, String currency, String trade_currency);
    }
}
