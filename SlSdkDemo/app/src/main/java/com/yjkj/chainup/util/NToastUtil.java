package com.yjkj.chainup.util;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;

import org.json.JSONObject;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-26 20:05
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-26 20:05
 * @UpdateRemark: 更新说明
 */
public class NToastUtil {

    private static Toast toast = null;
    /**
     * 上一次时间
     */
    private static long lastTime = 0;
    /**
     * 当前时间
     */
    private static long curTime = 0;
    /**
     * 之前显示的内容
     */
    private static String oldMsg;

    /**
     * 提示信息
     */
    public static void showToast(final String text, final boolean isLongTime) {
        final Context context = ChainUpApp.appContext;
        final int y = ScreenUtil.getHeight() / 7;//ScreenUtil.getHeight(context)/8;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            show(context, text, isLongTime, y);
        } else {
            Looper.prepare();
            show(context, text, isLongTime, y);
            Looper.loop();
        }

    }

    private static void show(Context context, String text, final boolean isLongTime, final int y) {
        curTime = System.currentTimeMillis();
        int duration = isLongTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        JSONObject jsonObject = null;//MessageUtil.getMessage();
        if(null!=jsonObject){
            text = jsonObject.optString(text,text);
        }
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
            oldMsg = text;
        } else {
            if(text.equals(oldMsg)){
                if (curTime - lastTime < duration) {
                    lastTime = System.currentTimeMillis();
                    return;
                }
            }

            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.setGravity(Gravity.BOTTOM, 0, y);
        lastTime = System.currentTimeMillis();
        toast.show();
    }

    /*
     * 自定义view的toast
     */
    public static void showTopToast(boolean isSuccess, String content) {
        if (!StringUtil.checkStr(content) || "网络异常".equalsIgnoreCase(content))
            return;
        final Context context = ChainUpApp.app;
        View view = LayoutInflater.from(context).inflate(R.layout.toast_x_y, null);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//设置Toast可以布局到系统状态栏的下面
        int w = ScreenUtil.getWidth();
        int h = ScreenUtil.dip2px(context, 70.0f);

        RelativeLayout root_ll = view.findViewById(R.id.root_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        root_ll.setLayoutParams(params);

        TextView text = view.findViewById(R.id.text);
        JSONObject jsonObject = null;//MessageUtil.getMessage();
        if(null!=jsonObject){
            content = jsonObject.optString(content,content);
        }
        text.setText(content+"");
        if (isSuccess) {
            root_ll.setBackgroundColor(ContextUtil.getColor(R.color.feedback_success));
        } else {
            root_ll.setBackgroundColor(ContextUtil.getColor(R.color.feedback_error));
        }

        Toast toast = new Toast(context);
        toast.setView(view);
        //toast.getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//设置Toast可以布局到系统状态栏的下面

        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /*
     * toast居中展示
     */
    public static void showCenterToast(String content) {
        if (!StringUtil.checkStr(content))
            return;

        final Context context = ChainUpApp.app;
        Toast centerToast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.center_toast,null);
        TextView text = view.findViewById(R.id.text);

        text.setText(content);
        centerToast.setView(view);
        centerToast.setGravity(Gravity.CENTER,0,0);
        centerToast.setDuration(Toast.LENGTH_LONG);
        centerToast.show();
    }
}
