package com.follow.order.bean;

/**
 * @time: 2020/3/26
 * @author: guodong
 */
public class OrderShareBean {

    /**
     * nick_name : 也白扯
     * head_img : https://static.heyuedi.net/default_head.jpg
     * pnl_ratio : 10%
     * total_pnl_ratio : $4000
     * follow_days : 3
     * color : {"pnl_ratio":2}
     * exchange : {"name":"Huobi","desc":"就开始来得及刻录机","img":"https://cdn-qn.heyuedi.net/Withdraw.png","qrcode":"https://cdn-qn.heyuedi.net//mp/qrcode/heyuedi_hq.jpg"}
     */

    private String nick_name;
    private String head_img;
    private String pnl_ratio;
    private String total_pnl_ratio;
    private String total_pnl;
    private String follow_days;
    private ColorBean color;
    private ExchangeBean exchange;

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getPnl_ratio() {
        return pnl_ratio;
    }

    public void setPnl_ratio(String pnl_ratio) {
        this.pnl_ratio = pnl_ratio;
    }

    public String getTotal_pnl_ratio() {
        return total_pnl_ratio;
    }

    public void setTotal_pnl_ratio(String total_pnl_ratio) {
        this.total_pnl_ratio = total_pnl_ratio;
    }

    public String getFollow_days() {
        return follow_days;
    }

    public void setFollow_days(String follow_days) {
        this.follow_days = follow_days;
    }

    public String getTotal_pnl() {
        return total_pnl;
    }

    public void setTotal_pnl(String total_pnl) {
        this.total_pnl = total_pnl;
    }

    public ColorBean getColor() {
        return color;
    }

    public void setColor(ColorBean color) {
        this.color = color;
    }

    public ExchangeBean getExchange() {
        return exchange;
    }

    public void setExchange(ExchangeBean exchange) {
        this.exchange = exchange;
    }

    public static class ColorBean {
        /**
         * pnl_ratio : 2
         */

        private int pnl_ratio;

        public int getPnl_ratio() {
            return pnl_ratio;
        }

        public void setPnl_ratio(int pnl_ratio) {
            this.pnl_ratio = pnl_ratio;
        }
    }

    public static class ExchangeBean {
        /**
         * name : Huobi
         * desc : 就开始来得及刻录机
         * img : https://cdn-qn.heyuedi.net/Withdraw.png
         * qrcode : https://cdn-qn.heyuedi.net//mp/qrcode/heyuedi_hq.jpg
         */

        private String name;
        private String desc;
        private String img;
        private String qrcode;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }
    }
}
