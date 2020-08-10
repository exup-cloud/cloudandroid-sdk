package com.follow.order.bean;

/**
 * @time: 2020/3/23
 * @author: guodong
 */
public class FollowProfitBean {
    private String total_capital;
    private String total_realised_pnl;
    private String total_realised_pnl_ratio;
    private Colors color;

    public String getTotal_capital() {
        return total_capital;
    }

    public void setTotal_capital(String total_capital) {
        this.total_capital = total_capital;
    }

    public String getTotal_realised_pnl() {
        return total_realised_pnl;
    }

    public void setTotal_realised_pnl(String total_realised_pnl) {
        this.total_realised_pnl = total_realised_pnl;
    }

    public String getTotal_realised_pnl_ratio() {
        return total_realised_pnl_ratio;
    }

    public void setTotal_realised_pnl_ratio(String total_realised_pnl_ratio) {
        this.total_realised_pnl_ratio = total_realised_pnl_ratio;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public static class Colors {
        private int total_realised_pnl;
        private int total_realised_pnl_ratio;

        public int getTotal_realised_pnl() {
            return total_realised_pnl;
        }

        public void setTotal_realised_pnl(int total_realised_pnl) {
            this.total_realised_pnl = total_realised_pnl;
        }

        public int getTotal_realised_pnl_ratio() {
            return total_realised_pnl_ratio;
        }

        public void setTotal_realised_pnl_ratio(int total_realised_pnl_ratio) {
            this.total_realised_pnl_ratio = total_realised_pnl_ratio;
        }
    }
}
