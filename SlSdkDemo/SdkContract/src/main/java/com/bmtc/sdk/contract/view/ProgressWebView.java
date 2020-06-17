package com.bmtc.sdk.contract.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.core.graphics.ColorUtils;

import com.bmtc.sdk.contract.HtmlActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.bmtc.sdk.contract.utils.UtilSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;


/**
 * Created by zj on 2017/10/18.
 */

public class ProgressWebView extends LinearLayout {
    private WebView mWebView;

    private ProgressBar mProgressBar;

    private Context mContext;

    private String url;

    public ProgressWebView(Context context) {
        this(context, null);
    }


    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.sl_view_web_progress, this);
        mWebView = view.findViewById(R.id.web_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void loadUrl(String url, String method, String body) {
        if (url == null) {
            url = "http://www.baidu.com";
        }
        initWebview(url, method, body);
    }


    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebview(String url, String method, String body) {

        mWebView.addJavascriptInterface(this, "bridge");
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置可以访问文件
        //webSettings.setAllowFileAccess(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置默认缩放方式尺寸是far
        //webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setDefaultFontSize(16);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);

        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (TextUtils.equals(method, "POST")) {
            try {
                mWebView.postUrl(url, body.getBytes("UTF8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        } else {
            mWebView.loadUrl(url);
        }

        // 设置WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true
                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        // 设置WebChromeClient
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        mWebView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键

                        mWebView.goBack(); // 后退

                        // webview.goForward();//前进
                        return true; // 已处理
                    }
                }
                return false;
            }
        });
    }

    @JavascriptInterface
    public void navigate(String object){
        Log.d("WebView" , "navigate() called!\n"
                + object + "\n");
        try {
            JSONObject jsonObject = new JSONObject(object);

            String url = jsonObject.optString("url");
            String method = jsonObject.optString("method");
            String body = jsonObject.optString("body");
            String title = jsonObject.optString("title");
            String rightText = jsonObject.optString("rightText");
            String rightLink = jsonObject.optString("rightLink");

            Intent intent = new Intent(mContext, HtmlActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            intent.putExtra("method", method);
            intent.putExtra("rightText", rightText);
            intent.putExtra("rightLink", rightLink);
            mContext.startActivity(intent);

        } catch (JSONException ignored) {

        }
    }

    @JavascriptInterface
    public void shareMessage(String object){
        Log.d("WebView" , "share() called!\n"
                + object + "\n");
        try {
            JSONObject jsonObject = new JSONObject(object);

            String body = jsonObject.optString("body");
            String imageUrl = jsonObject.optString("imageUrl");
            String title = jsonObject.optString("title");
            String link = jsonObject.optString("shareLink");

//            ShareDialog shareDialog = new ShareDialog(mContext);
//            shareDialog.setType(2);
//            shareDialog.setMessage(imageUrl, link, title, body);
//            shareDialog.show();

        } catch (JSONException ignored) {

        }
    }

    @JavascriptInterface
    public void SaveImage(String object){
        Log.d("WebView" , "SaveImage() called!\n"
                + object + "\n");
        try {
            JSONObject jsonObject = new JSONObject(object);
            String imageUrl = jsonObject.optString("imageUrl");
            String succeedCallback = jsonObject.optString("succeedCallback");
            String failCallBack = jsonObject.optString("failCallBack");


        } catch (JSONException ignored) {

        }

    }

    @JavascriptInterface
    public double exchange(String object){
        Log.d("WebView" , "exchange() called!\n"
                + object + "\n");

        try {
            JSONObject jsonObject = new JSONObject(object);

            String fromCode = jsonObject.optString("fromCode");
            String toCode = jsonObject.optString("toCode");

            String stock_code = toCode + "/" + fromCode;
//            SpotTicker ticker = LogicGlobal.getSpotTicker(stock_code);
//            double price = 0.0;
//            if (ticker != null) {
//                price = MathHelper.round(ticker.getLast_price(), 8);
//            } else {
//                String stock_code_reserve = fromCode + "/" + toCode;
//                SpotTicker ticker_reserve = LogicGlobal.getSpotTicker(stock_code_reserve);
//                if (ticker_reserve != null) {
//                    price = MathHelper.div("1.0", ticker_reserve.getLast_price(), 8);
//                }
//            }

          // return price;
            return 0.00;

        } catch (JSONException ignored) {

        }
        return 0.0;
    }

    @JavascriptInterface
    public void multiExchange(String object){
        Log.d("WebView" , "multiExchange() called!\n"
                + object + "\n");

//        final double result = Exchange.webExchange(object);
//
//        try {
//            JSONObject jsonObject = new JSONObject(object);
//
//            final String succeedCallback = jsonObject.optString("succeedCallback");
//            final String failCallBack = jsonObject.optString("failCallBack");
//
//            mWebView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
//                    String jsInvoke = String.format("javascript:%s('%s')", succeedCallback, decimalFormat.format(result));
//                    mWebView.loadUrl(jsInvoke);
//                }
//            }, 100);
//
//        } catch (JSONException ignored) {
//
//        }

    }

    @JavascriptInterface
    public void Copy2Clipboard(String data){
        Log.d("WebView" , "Copy2Clipboard() called!\n"
                + data + "\n");

        try {
            JSONObject jsonObject = new JSONObject(data);
            String cyInfo = jsonObject.optString("cyInfo");

            ClipboardManager cm = (ClipboardManager) mContext.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Label", cyInfo);
            if (cm != null) { cm.setPrimaryClip(clipData); }
            ToastUtil.shortToast(mContext, mContext.getResources().getString(R.string.sl_str_copy_succeed));

        } catch (JSONException ignored) {

        }
    }

    @JavascriptInterface
    public void toast(String data){
        Log.d("WebView" , "toast() called!\n"
                + data + "\n");

        try {
            JSONObject jsonObject = new JSONObject(data);
            String cyInfo = jsonObject.optString("content");
            ToastUtil.shortToast(mContext, cyInfo);

        } catch (JSONException ignored) {

        }
    }

    @JavascriptInterface
    public String Version(){
        Log.d("WebView" , "Version() called!\n");
        return UtilSystem.getVersionName(mContext);
    }

    @JavascriptInterface
    public void jumpTo(String object){
        Log.d("WebView" , "jumpTo() called!\n");
        try {
            JSONObject jsonObject = new JSONObject(object);
            int to = jsonObject.optInt("to");
            String coin_code = jsonObject.optString("coin_code");

            if (to == 1) {
//                Account account = BTAccount.getInstance().getActiveAccount();
//                if (account != null) {
//                    if (TextUtils.isEmpty(account.getPhone())) {
//                        if (account.getStatus() == 1) {     //not active
//                            Intent intent = new Intent(mContext, BindActivity.class);
//                            mContext.startActivity(intent);
//                        } else {
//                            Intent intent = new Intent();
//                            intent.putExtra("coin_code", coin_code);
//                            intent.setClass(mContext, DepositActivity.class);
//                            mContext.startActivity(intent);
//                        }
//                    } else {
//                        Intent intent = new Intent();
//                        intent.putExtra("coin_code", coin_code);
//                        intent.setClass(mContext, DepositActivity.class);
//                        mContext.startActivity(intent);
//                    }
//                }
            } else if (to == 2) {
//                if (!BTAccount.getInstance().isLogin()) {
//                    BTAccount.getInstance().doLogin(mContext, "");
//                    return;
//                }
//
//                final Contract contract = LogicGlobal.getContract(coin_code);
//                if (contract == null) {
//                    return;
//                }
//
//                ContractAccount contractAccount = BTContract.getInstance().getContractAccount(coin_code);
//                if (contractAccount == null) {
//
//                    String title = String.format(mContext.getString(R.string.str_open_contract_account), coin_code);
//                    final PromptWindowWide window = new PromptWindowWide(mContext);
//                    window.showTitle(title);
//                    window.showTvContent(mContext.getString(R.string.str_risk_disclosure_notice));
//                    window.showBtnOk(mContext.getString(R.string.str_open_contract_account_btn));
//                    window.showAtLocation(this, Gravity.CENTER, 0, 0);
//
//                    window.getBtnOk().setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            window.dismiss();
//
//                            BTContract.getInstance().createContractAccount(contract.getContract_id(), new IResponse<Void>() {
//                                @Override
//                                public void onResponse(String errno, String message, Void data) {
//                                    if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
//                                        ToastUtil.shortToast(LogicGlobal.sContext, message);
//                                        return;
//                                    }
//
//                                    ToastUtil.shortToast(LogicGlobal.sContext, mContext.getString(R.string.str_account_created_successfully));
//                                    BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
//                                        @Override
//                                        public void onResponse(String errno, String message, List<ContractAccount> data) {
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                    });
//                }
            } else if (to == 3) {
//                Intent intent = new Intent(mContext, BindPhoneActivity.class);
//                mContext.startActivity(intent);
            } else if (to == 4) {
               // BTAccount.getInstance().doLogin(mContext, "");
            }else if(to == 5){
//                Intent intent = new Intent(mContext, KYCVerifyActivity.class);
//                mContext.startActivity(intent);
            }

            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }

        } catch (JSONException ignored) {

        }

    }

    public boolean canBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return false;
        }
        return true;
    }


}
