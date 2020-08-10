package com.follow.order.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ssf on 17/2/24.
 */

public class BaseBean<T>{


    public static final int SUCCESS = 0;
    public static final String STATUS_NAME = "code";
    /**
     * status : 1
     * msg :
     * data : 1
     */
    @SerializedName(value = "status",alternate = "code")
    private int code;
    @SerializedName(value = "message",alternate = "msg")
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isCodeInvalid() {
        return code!=SUCCESS;
    }
}
