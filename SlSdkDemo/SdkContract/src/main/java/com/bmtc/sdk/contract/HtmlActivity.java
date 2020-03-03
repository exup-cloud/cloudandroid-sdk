package com.bmtc.sdk.contract;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.view.ProgressWebView;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.utils.LogUtil;


/**
 * Created by zj on 2017/10/18.
 */

public class HtmlActivity extends BaseActivity {
    private ProgressWebView progressWebView;
    private ImageView imgBack;
    private LinearLayout layout;
    private TextView titleTv;
    private TextView rightTv;
    private String url = "";
    private String title;

    private String method = "";
    private String body = "";
    private String rightText = "";
    private String rightLink = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_html);
        //this.setImmerseLayout(findViewById(R.id.layout_search_title));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
            url = getIntent().getStringExtra("url");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("title"))) {
            title = getIntent().getStringExtra("title");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("method"))) {
            method = getIntent().getStringExtra("method");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("body"))) {
            body = getIntent().getStringExtra("body");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("rightText"))) {
            rightText = getIntent().getStringExtra("rightText");
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra("rightLink"))) {
            rightLink = getIntent().getStringExtra("rightLink");
        }
        setView();
        setViewData();
    }

    @Override
    public void setView() {
        super.setView();
        progressWebView = findViewById(R.id.wb);
        titleTv = findViewById(R.id.tv_title);

        rightTv = findViewById(R.id.tv_right);
        rightTv.setVisibility(TextUtils.isEmpty(rightText) ? View.INVISIBLE : View.VISIBLE);
        rightTv.setText(rightText);

        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HtmlActivity.this, HtmlActivity.class);
                intent.putExtra("url", rightLink);
                intent.putExtra("title", rightText);
                startActivity(intent);
            }
        });


        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setViewData() {
        super.setViewData();
        //设置资讯标题
        if (!TextUtils.isEmpty(title)){
            titleTv.setText(title);
        }else{
            titleTv.setText("");
        }
        //接收一个url用于加载网页
        LogUtil.d("DEBUG",url);
        progressWebView.loadUrl(url, method, body);
    }

}
