package com.follow.order.bean;

public class PersonalInfoBean {

    private String uid;
    private String nick_name;
    private String head_img;
    private String desc;
    private String chainup_uid;
    private String exchange;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getChainup_uid() {
        return chainup_uid;
    }

    public void setChainup_uid(String chainup_uid) {
        this.chainup_uid = chainup_uid;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public boolean isChainUpUID() {
        if (chainup_uid != null && !chainup_uid.isEmpty() && !chainup_uid.equals("0")) {
            return true;
        }
        return false;
    }
}
