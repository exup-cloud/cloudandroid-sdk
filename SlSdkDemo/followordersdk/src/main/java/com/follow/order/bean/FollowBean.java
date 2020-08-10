package com.follow.order.bean;

import java.util.List;

/**
 * @time: 2020/3/18
 * @author: guodong
 */
public class FollowBean {
    private String kol_id;
    private String uid;
    private String head_img;
    private String nick_name;
    private int is_recommend;
    private String month_realised_pnl;
    private String month_realised_pnl_ratio;
    private String desc;
    private String exchange;
    private int follow_members;
    private String type;
    private List<FollowCoinBean> currency_list;
    private Colors color;

    public String getKol_id() {
        return kol_id;
    }

    public void setKol_id(String kol_id) {
        this.kol_id = kol_id;
    }

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

    public int getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(int is_recommend) {
        this.is_recommend = is_recommend;
    }

    public String getMonth_realised_pnl() {
        return month_realised_pnl;
    }

    public void setMonth_realised_pnl(String month_realised_pnl) {
        this.month_realised_pnl = month_realised_pnl;
    }

    public String getMonth_realised_pnl_ratio() {
        return month_realised_pnl_ratio;
    }

    public void setMonth_realised_pnl_ratio(String month_realised_pnl_ratio) {
        this.month_realised_pnl_ratio = month_realised_pnl_ratio;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public int getFollow_members() {
        return follow_members;
    }

    public void setFollow_members(int follow_members) {
        this.follow_members = follow_members;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FollowCoinBean> getCurrency_list() {
        return currency_list;
    }

    public void setCurrency_list(List<FollowCoinBean> currency_list) {
        this.currency_list = currency_list;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public static class Colors {
        private int month_realised_pnl;
        private int month_realised_pnl_ratio;

        public int getMonth_realised_pnl() {
            return month_realised_pnl;
        }

        public void setMonth_realised_pnl(int month_realised_pnl) {
            this.month_realised_pnl = month_realised_pnl;
        }

        public int getMonth_realised_pnl_ratio() {
            return month_realised_pnl_ratio;
        }

        public void setMonth_realised_pnl_ratio(int month_realised_pnl_ratio) {
            this.month_realised_pnl_ratio = month_realised_pnl_ratio;
        }
    }

}
