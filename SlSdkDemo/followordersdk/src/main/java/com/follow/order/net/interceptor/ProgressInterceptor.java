package com.follow.order.net.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by wanghui on 17/6/21.
 */

public class ProgressInterceptor implements Interceptor {
//    private ProgressResponseBody.ProgressListener mProgressListener;
//
//    public ProgressInterceptor(ProgressResponseBody.ProgressListener progressListener) {
//        this.mProgressListener = progressListener;
//    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String url = originalResponse.request().url().toString();
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(url,originalResponse.body()))
                .build();

    }
}
