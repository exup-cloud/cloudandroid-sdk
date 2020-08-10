package com.follow.order.bean;

import java.util.List;

/**
 * @time: 2020/3/23
 * @author: guodong
 */
public class FollowPositionBean {

    private List<MasterPosition> master_position;

    private List<Position> position;

    public List<MasterPosition> getMaster_position() {
        return master_position;
    }

    public void setMaster_position(List<MasterPosition> master_position) {
        this.master_position = master_position;
    }

    public List<Position> getPosition() {
        return position;
    }

    public void setPosition(List<Position> position) {
        this.position = position;
    }

    public static class MasterPosition {
        private String desc;
        private String avg_price;
        private String pnl_ratio;
        private Color color;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getAvg_price() {
            return avg_price;
        }

        public void setAvg_price(String avg_price) {
            this.avg_price = avg_price;
        }

        public String getPnl_ratio() {
            return pnl_ratio;
        }

        public void setPnl_ratio(String pnl_ratio) {
            this.pnl_ratio = pnl_ratio;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    public static class Position {
        private String desc;
        private String avg_price;
        private String pnl_ratio;
        private String stop_profit_price;
        private String stop_deficit_price;
        private Color color;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getAvg_price() {
            return avg_price;
        }

        public void setAvg_price(String avg_price) {
            this.avg_price = avg_price;
        }

        public String getPnl_ratio() {
            return pnl_ratio;
        }

        public void setPnl_ratio(String pnl_ratio) {
            this.pnl_ratio = pnl_ratio;
        }

        public String getStop_profit_price() {
            return stop_profit_price;
        }

        public void setStop_profit_price(String stop_profit_price) {
            this.stop_profit_price = stop_profit_price;
        }

        public String getStop_deficit_price() {
            return stop_deficit_price;
        }

        public void setStop_deficit_price(String stop_deficit_price) {
            this.stop_deficit_price = stop_deficit_price;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    public static class Color {
        private int desc;

        public int getDesc() {
            return desc;
        }

        public void setDesc(int desc) {
            this.desc = desc;
        }
    }

}
