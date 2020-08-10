package com.follow.order.net.interceptor;


import com.follow.order.FollowOrderSDK;
import com.follow.order.utils.DeviceUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求公共头信息插入器
 */
public class HttpHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("device-id", DeviceUtil.getDeviceId(FollowOrderSDK.ins().getApplication()))
                .build();
        return chain.proceed(request);
    }
}