package com.bmtc.sdk.contract.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;




/**
 * Created by zj on 2018/3/1.
 */

public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setView(){

    }

    protected void setViewData(){

    }

    protected void setImmerseLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int statusBarHeight = getStatusBarHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }
    /**
     * 用于获取状态栏的高度。 使用Resource对象获取
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onPause() {
        super.onPause();
       // MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // MobclickAgent.onResume(this);
    }
}
