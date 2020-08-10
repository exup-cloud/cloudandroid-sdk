package com.yjkj.chainup.bean;

import java.io.Serializable;

/**
 * @Author lianshangljl
 * @Date 2019/1/29-4:41 PM
 * @Email buptjinlong@163.com
 * @description
 */
public class EntrustBean implements Serializable {
    private static final long serialVersionUID = -7060210544600464281L;

    private int position;
    private String content;

    public EntrustBean(int position, String content) {
        this.position = position;
        this.content = content;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
