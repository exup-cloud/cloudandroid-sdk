package com.follow.order.bean;

/**
 * @time: 2020/3/18
 * @author: guodong
 */
public class FollowCoinBean {
    private String trade_currency_id;
    private String trade_coin;
    private String limit;
    private String realised_pnl;
    private String max_limit;
    private String min_limit;
    private int follow_status;//0、未跟 1、已跟 2、额度已满

    public String getTrade_currency_id() {
        return trade_currency_id;
    }

    public void setTrade_currency_id(String trade_currency_id) {
        this.trade_currency_id = trade_currency_id;
    }

    public String getTrade_coin() {
        return trade_coin;
    }

    public void setTrade_coin(String trade_coin) {
        this.trade_coin = trade_coin;
    }

    public String getRealised_pnl() {
        return realised_pnl;
    }

    public void setRealised_pnl(String realised_pnl) {
        this.realised_pnl = realised_pnl;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getMax_limit() {
        return max_limit;
    }

    public void setMax_limit(String max_limit) {
        this.max_limit = max_limit;
    }

    public String getMin_limit() {
        return min_limit;
    }

    public void setMin_limit(String min_limit) {
        this.min_limit = min_limit;
    }

    public int getFollow_status() {
        return follow_status;
    }

    public void setFollow_status(int follow_status) {
        this.follow_status = follow_status;
    }
}
