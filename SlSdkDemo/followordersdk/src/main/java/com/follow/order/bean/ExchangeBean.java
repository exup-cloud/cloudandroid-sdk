package com.follow.order.bean;

public class ExchangeBean {
    private String api_id;
    private String exchange;
    private String exchange_name;
    private String selected_icon;
    private String unselected_icon;
    private boolean isSelected;

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange_name() {
        return exchange_name;
    }

    public void setExchange_name(String exchange_name) {
        this.exchange_name = exchange_name;
    }

    public String getSelected_icon() {
        return selected_icon;
    }

    public void setSelected_icon(String selected_icon) {
        this.selected_icon = selected_icon;
    }

    public String getUnselected_icon() {
        return unselected_icon;
    }

    public void setUnselected_icon(String unselected_icon) {
        this.unselected_icon = unselected_icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
