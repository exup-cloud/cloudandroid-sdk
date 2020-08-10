package com.follow.order.bean;

import java.util.List;

public class UserFinanceProfileBean {

    /**
     * balance : 8160
     * profit : 9
     * profit_ratio : 0.00
     * victory_ratio : 0.00
     * max_retreat_ratio : 0
     * live_duration : 1
     * trading_frequency : 0
     * position_avg_duration : 14809min
     * distribution : [{"coin":"btc","amount":"2.1156231","worth":"7927.74","ratio":"0.9715"},{"coin":"etc","amount":"25.91813829","worth":"125.85","ratio":"0.0154"}]
     * profit_history : [{"time":"2018-12-26 15:51:48","profit":"9","profit_ratio":"0.00"},{"time":"2018-12-26 15:17:15","profit":"0","profit_ratio":"0.00"}]
     */
    private String exchange;
    private Notice notice;
    private int subscribe;
    private ProfileBean profile;
    private Colors colors;
    private List<ProfitHistoryBean> profit_history;

    public Colors getColors() {
        return colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public List<ProfitHistoryBean> getProfit_history() {
        return profit_history;
    }

    public void setProfit_history(List<ProfitHistoryBean> profit_history) {
        this.profit_history = profit_history;
    }

    public ProfileBean getProfile() {
        return profile;
    }

    public void setProfile(ProfileBean profile) {
        this.profile = profile;
    }

    public int getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe = subscribe;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public static class Notice {
        private String desc;
        private String bgcolor;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getBgcolor() {
            return bgcolor;
        }

        public void setBgcolor(String bgcolor) {
            this.bgcolor = bgcolor;
        }
    }

    public static class ProfileBean {
        private String total;
        private String profit;
        private String profit_ratio;
        private String week_profit;
        private String week_profit_ratio;
        private String victory_ratio;
        private String live_duration;
        private String trading_frequency;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getProfit_ratio() {
            return profit_ratio;
        }

        public void setProfit_ratio(String profit_ratio) {
            this.profit_ratio = profit_ratio;
        }

        public String getWeek_profit() {
            return week_profit;
        }

        public void setWeek_profit(String week_profit) {
            this.week_profit = week_profit;
        }

        public String getWeek_profit_ratio() {
            return week_profit_ratio;
        }

        public void setWeek_profit_ratio(String week_profit_ratio) {
            this.week_profit_ratio = week_profit_ratio;
        }

        public String getVictory_ratio() {
            return victory_ratio;
        }

        public void setVictory_ratio(String victory_ratio) {
            this.victory_ratio = victory_ratio;
        }

        public String getLive_duration() {
            return live_duration;
        }

        public void setLive_duration(String live_duration) {
            this.live_duration = live_duration;
        }

        public String getTrading_frequency() {
            return trading_frequency;
        }

        public void setTrading_frequency(String trading_frequency) {
            this.trading_frequency = trading_frequency;
        }
    }

    public static class Colors {
        /**
         * time : 2018-12-26 15:51:48
         * profit : 9
         * profit_ratio : 0.00
         */

        private int profit;
        private int profit_ratio;
        private int week_profit;
        private int week_profit_ratio;

        public int getProfit() {
            return profit;
        }

        public void setProfit(int profit) {
            this.profit = profit;
        }

        public int getProfit_ratio() {
            return profit_ratio;
        }

        public void setProfit_ratio(int profit_ratio) {
            this.profit_ratio = profit_ratio;
        }

        public int getWeek_profit() {
            return week_profit;
        }

        public void setWeek_profit(int week_profit) {
            this.week_profit = week_profit;
        }

        public int getWeek_profit_ratio() {
            return week_profit_ratio;
        }

        public void setWeek_profit_ratio(int week_profit_ratio) {
            this.week_profit_ratio = week_profit_ratio;
        }
    }

    public static class Chart {
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ProfitHistoryBean {
        private String title;
        private String max;
        private String min;
        private String unit;
        private List<ChartRecord> data;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public List<ChartRecord> getData() {
            return data;
        }

        public void setData(List<ChartRecord> data) {
            this.data = data;
        }
    }

    public static class ChartRecord {
        private String time;
        private String value;
        private boolean isMax;
        private boolean isMin;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isMax() {
            return isMax;
        }

        public void setMax(boolean max) {
            isMax = max;
        }

        public boolean isMin() {
            return isMin;
        }

        public void setMin(boolean min) {
            isMin = min;
        }
    }

    public static class DataBean {
        private String title;
        private String value;
        private int color;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
