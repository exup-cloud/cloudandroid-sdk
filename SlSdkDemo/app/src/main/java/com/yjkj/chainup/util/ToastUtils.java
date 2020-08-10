package com.yjkj.chainup.util;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;


public class ToastUtils {

    private static String oldMsg;
    private static long time;

    public static void showToast(Context context, String msg) {
        if (!msg.equals(oldMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        } else {
            // 显示内容一样时，只有间隔时间大于2秒时才显示
            if (System.currentTimeMillis() - time > 2000) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }
        }
        oldMsg = msg;
    }


    public static void showToast(String msg) {
        showToast(ChainUpApp.appContext, msg);
    }

    /**
     * @param string
     */
    public static void toastOnUIThread(String string) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(string);
            }
        });
    }


    /**
     * 自定 带图片的toast
     *
     * @param context
     * @param msg
     * @param resId
     */
    public static void showCusToast(Context context, String msg, int resId) {
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_customize_toast, null);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(resId);
        TextView tvText = view.findViewById(R.id.tv_text);
        tvText.setText(msg);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
