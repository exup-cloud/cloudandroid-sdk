package com.follow.order.bean;

/**
 * @time: 2020/3/18
 * @author: guodong
 */
public class OrderBean {
    private UserBean user;
    private ItemBean follow;
    private FollowPositionBean position;
    private Colors color;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public ItemBean getFollow() {
        return follow;
    }

    public void setFollow(ItemBean follow) {
        this.follow = follow;
    }

    public FollowPositionBean getPosition() {
        return position;
    }

    public void setPosition(FollowPositionBean position) {
        this.position = position;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public static class UserBean {
        private String uid;
        private String kol_id;
        private String head_img;
        private String nick_name;
        private String exchange;
        private String api_id;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getHead_img() {
            return head_img;
        }

        public void setHead_img(String head_img) {
            this.head_img = head_img;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getApi_id() {
            return api_id;
        }

        public void setApi_id(String api_id) {
            this.api_id = api_id;
        }

        public String getKol_id() {
            return kol_id;
        }

        public void setKol_id(String kol_id) {
            this.kol_id = kol_id;
        }
    }

    public static class ItemBean {
        private String follow_type;
        private String follow_id;
        private String start_time;
        private String principal;
        private String follow_days;
        private String pnl;
        private String pnl_ratio;
        private int follow_status;//1、跟单中 3、跟单结束
        private String follow_status_desc;
        private String commission;
        private String follow_exchange;
        private String currency;
        private String stop_profit;
        private String stop_deficit;
        private String follow_ratio;

        public String getFollow_id() {
            return follow_id;
        }

        public void setFollow_id(String follow_id) {
            this.follow_id = follow_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getFollow_days() {
            return follow_days;
        }

        public void setFollow_days(String follow_days) {
            this.follow_days = follow_days;
        }

        public String getFollow_type() {
            return follow_type;
        }

        public void setFollow_type(String follow_type) {
            this.follow_type = follow_type;
        }

        public String getPnl() {
            return pnl;
        }

        public void setPnl(String pnl) {
            this.pnl = pnl;
        }

        public String getPnl_ratio() {
            return pnl_ratio;
        }

        public void setPnl_ratio(String pnl_ratio) {
            this.pnl_ratio = pnl_ratio;
        }

        public int getFollow_status() {
            return follow_status;
        }

        public void setFollow_status(int follow_status) {
            this.follow_status = follow_status;
        }

        public String getFollow_status_desc() {
            return follow_status_desc;
        }

        public void setFollow_status_desc(String follow_status_desc) {
            this.follow_status_desc = follow_status_desc;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getFollow_exchange() {
            return follow_exchange;
        }

        public void setFollow_exchange(String follow_exchange) {
            this.follow_exchange = follow_exchange;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStop_profit() {
            return stop_profit;
        }

        public void setStop_profit(String stop_profit) {
            this.stop_profit = stop_profit;
        }

        public String getStop_deficit() {
            return stop_deficit;
        }

        public void setStop_deficit(String stop_deficit) {
            this.stop_deficit = stop_deficit;
        }

        public String getFollow_ratio() {
            return follow_ratio;
        }

        public void setFollow_ratio(String follow_ratio) {
            this.follow_ratio = follow_ratio;
        }

    }

    public static class Colors {
        private int pnl;
        private int pnl_ratio;

        public int getPnl() {
            return pnl;
        }

        public void setPnl(int pnl) {
            this.pnl = pnl;
        }

        public int getPnl_ratio() {
            return pnl_ratio;
        }

        public void setPnl_ratio(int pnl_ratio) {
            this.pnl_ratio = pnl_ratio;
        }
    }
}
