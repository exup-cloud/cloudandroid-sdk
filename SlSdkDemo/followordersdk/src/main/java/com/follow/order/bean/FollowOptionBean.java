package com.follow.order.bean;

/**
 * @time: 2020/3/23
 * @author: guodong
 */
public class FollowOptionBean {
    private String kol_commission;
    private String user_commission;
    private String trade_mode;
    private int trade_type;//1、币本位 2、金本位 3、金本位/币本位
    private String exchange;
    private String max_limit;
    private String min_limit;
    private String current_price;
    private String balance;
    private String follow_ratio;
    private String currency;
    private String symbol;
    private String tips;
    private String trade_currency;
    private ProfitBean target_profit;

    public String getKol_commission() {
        return kol_commission;
    }

    public void setKol_commission(String kol_commission) {
        this.kol_commission = kol_commission;
    }

    public String getUser_commission() {
        return user_commission;
    }

    public void setUser_commission(String user_commission) {
        this.user_commission = user_commission;
    }

    public String getTrade_mode() {
        return trade_mode;
    }

    public void setTrade_mode(String trade_mode) {
        this.trade_mode = trade_mode;
    }

    public int getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(int trade_type) {
        this.trade_type = trade_type;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
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

    public String getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFollow_ratio() {
        return follow_ratio;
    }

    public void setFollow_ratio(String follow_ratio) {
        this.follow_ratio = follow_ratio;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ProfitBean getTarget_profit() {
        return target_profit;
    }

    public void setTarget_profit(ProfitBean target_profit) {
        this.target_profit = target_profit;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTrade_currency() {
        return trade_currency;
    }

    public void setTrade_currency(String trade_currency) {
        this.trade_currency = trade_currency;
    }

    public static class ProfitBean {

        /**
         * stop_profit : 40
         * stop_profit_force : 0
         * stop_profit_max : 0
         * stop_profit_min : 0
         * stop_profit_offset : 0
         * stop_deficit : 40
         * stop_deficit_force : 1
         * stop_deficit_max : 0
         * stop_deficit_min : 0
         * stop_deficit_offset : 0
         */

        private int stop_profit;
        private int stop_profit_force;
        private int stop_profit_max;
        private int stop_profit_min;
        private int stop_profit_offset;
        private int stop_deficit;
        private int stop_deficit_force;
        private int stop_deficit_max;
        private int stop_deficit_min;
        private int stop_deficit_offset;

        public int getStop_profit() {
            return stop_profit;
        }

        public void setStop_profit(int stop_profit) {
            this.stop_profit = stop_profit;
        }

        public int getStop_profit_force() {
            return stop_profit_force;
        }

        public void setStop_profit_force(int stop_profit_force) {
            this.stop_profit_force = stop_profit_force;
        }

        public int getStop_profit_max() {
            return stop_profit_max;
        }

        public void setStop_profit_max(int stop_profit_max) {
            this.stop_profit_max = stop_profit_max;
        }

        public int getStop_profit_min() {
            return stop_profit_min;
        }

        public void setStop_profit_min(int stop_profit_min) {
            this.stop_profit_min = stop_profit_min;
        }

        public int getStop_profit_offset() {
            return stop_profit_offset;
        }

        public void setStop_profit_offset(int stop_profit_offset) {
            this.stop_profit_offset = stop_profit_offset;
        }

        public int getStop_deficit() {
            return stop_deficit;
        }

        public void setStop_deficit(int stop_deficit) {
            this.stop_deficit = stop_deficit;
        }

        public int getStop_deficit_force() {
            return stop_deficit_force;
        }

        public void setStop_deficit_force(int stop_deficit_force) {
            this.stop_deficit_force = stop_deficit_force;
        }

        public int getStop_deficit_max() {
            return stop_deficit_max;
        }

        public void setStop_deficit_max(int stop_deficit_max) {
            this.stop_deficit_max = stop_deficit_max;
        }

        public int getStop_deficit_min() {
            return stop_deficit_min;
        }

        public void setStop_deficit_min(int stop_deficit_min) {
            this.stop_deficit_min = stop_deficit_min;
        }

        public int getStop_deficit_offset() {
            return stop_deficit_offset;
        }

        public void setStop_deficit_offset(int stop_deficit_offset) {
            this.stop_deficit_offset = stop_deficit_offset;
        }
    }
}
