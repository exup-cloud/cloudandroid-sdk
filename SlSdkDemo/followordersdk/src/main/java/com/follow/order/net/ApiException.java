package com.follow.order.net;

import android.content.Context;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;


/**
 * Created by wanghui on 17/3/3.
 * <p>
 * 统一处理错误码
 */
public class ApiException extends RuntimeException {
    private static final Context CONTEXT = FollowOrderSDK.ins().getApplication();


    private int errorCode;

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public ApiException(int resultCode) {
        this(resultCode, toApiExceptionMessage(resultCode));
    }

    public ApiException(int resultCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = resultCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 映射服务器返回的自定义错误码，
     * （此时的http状态码在[200, 300) 之间）
     *
     * @param resultCode
     * @return
     */
    private static String toApiExceptionMessage(int resultCode) {
        String message;
        switch (resultCode) {
            default:
                message = CONTEXT.getString(R.string.fo_error_unknow);
        }
        return message;
    }

}
