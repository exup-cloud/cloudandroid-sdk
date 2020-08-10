package com.follow.order.net;

import com.chainup.net.util.HttpsUtils;
import com.follow.order.FollowOrderSDK;
import com.follow.order.net.interceptor.HttpCacheInterceptor;
import com.follow.order.net.interceptor.HttpHeaderInterceptor;
import com.follow.order.net.log.RequestInterceptor;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

/**
 * Created by wanghui on 17/6/13.
 */

public class OkHttp3Client {
    private static final long DEFAULT_CONNECT_TIMEOUT = 15;
    private static final long DEFAULT_READ_TIMEOUT = 20;
    private static final long DEFAULT_WRITE_TIMEOUT = 20;
    private static final long DEFAULT_CACHE_SIZE = 1024 * 1024 * 10;

    private static OkHttpClient mOkHttpClient = null;

    private OkHttp3Client() {
    }

    private static OkHttpClient init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // 超时设置
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                // 错误重连
                .retryOnConnectionFailure(true)
                // 支持HTTPS
                .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)); //明文Http与比较新的Https

        if (FollowOrderSDK.ins().isDebug()) {
            HttpsUtils.SSLParams ssl =  HttpsUtils.getSslSocketFactory(null,null,null);
            builder.sslSocketFactory(ssl.sSLSocketFactory,ssl.trustManager);
        }
        // 添加各种插入器
        addInterceptor(builder);

        OkHttpClient client = builder.build();
        return client;

    }


    public static OkHttpClient getInstance() {
        if (mOkHttpClient == null) {
            synchronized (OkHttp3Client.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = init();
                }
            }
        }
        return mOkHttpClient;
    }

    private static void addInterceptor(OkHttpClient.Builder builder) {
        // 添加Header
        builder.addInterceptor(new HttpHeaderInterceptor());

        // 添加缓存控制策略
        File cacheDir = FollowOrderSDK.ins().getApplication().getExternalCacheDir();
        Cache cache = new Cache(cacheDir, DEFAULT_CACHE_SIZE);
        builder.cache(cache).addInterceptor(new HttpCacheInterceptor());

        //添加公共的参数
//        builder.addInterceptor(new CommonParamsInterceptor());
        // 添加调试工具
//        builder.networkInterceptors().add(new StethoInterceptor());

        RequestInterceptor logger = new RequestInterceptor();
        if (FollowOrderSDK.ins().isDebug()) {
            builder.addInterceptor(logger);
        }


    }
}
