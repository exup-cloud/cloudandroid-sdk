package com.follow.order.bean;

import java.util.List;

/**
 * @time: 2020/4/1
 * @author: guodong
 */
public class TipBean {
    private String id;
    private int is_show;
    private String title;
    private String content;
    private List<ButtonBean> btns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIs_show() {
        return is_show;
    }

    public void setIs_show(int is_show) {
        this.is_show = is_show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ButtonBean> getBtns() {
        return btns;
    }

    public void setBtns(List<ButtonBean> btns) {
        this.btns = btns;
    }

    public static class ButtonBean {
        private String title;
        private String color;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
