package com.yjkj.chainup.util;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.yjkj.chainup.db.constant.RoutePath;
import com.yjkj.chainup.extra_service.arouter.ArouterUtil;
import com.yjkj.chainup.new_version.activity.ItemDetailActivity;

/**
 * @Author lianshangljl
 * @Date 2019/7/18-10:23 AM
 * @Email buptjinlong@163.com
 * @description
 */
public class jsLoginHandler {

    private ItemDetailActivity mainActivity;
    private Context context;
    private WebView webView;

    public jsLoginHandler(Context context, WebView webView, ItemDetailActivity mainActivity) {
        this.context = context;
        this.webView = webView;
        this.mainActivity = mainActivity;
    }


    /**
     * js调用安卓方法
     *
     * @param dataForService
     */
    @JavascriptInterface
    public void postMessage(String dataForService) {
        if (dataForService.equals("webLogin")) {
            ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null);
            if(null!=mainActivity)
                mainActivity.finish();
        }
    }


    /**
     * 调用 js方法
     *
     * @param path
     */
    public void updataJs(String path) {
        final String encrySucceed = "javascript:androidFn" + "('" + path + "','" + "')";
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(encrySucceed);
            }
        });
    }

}
