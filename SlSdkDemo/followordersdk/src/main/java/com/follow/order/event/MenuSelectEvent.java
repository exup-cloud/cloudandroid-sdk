package com.follow.order.event;

/**
 * @time: 2020/3/15
 * @author: guodong
 */
public class MenuSelectEvent {

    private String style;
    private String coin;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
