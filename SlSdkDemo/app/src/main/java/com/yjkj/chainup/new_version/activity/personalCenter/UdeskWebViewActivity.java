package com.yjkj.chainup.new_version.activity.personalCenter;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yjkj.chainup.R;
import com.yjkj.chainup.db.constant.RoutePath;

@Route(path = RoutePath.UdeskWebViewActivity)
public class UdeskWebViewActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udesk_webview);

    }


}
