package com.yjkj.chainup.util;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;

/**
 * Created by Bertking on 2018/6/8.
 */
public class ViewUtils {

    /**
     * @param view
     * @param text
     * @param isSuc 是否是成功的状态
     */
    public static void showSnackBar(View view, String text, boolean isSuc) {
        if(view!=null){
            TSnackbar snackbar = TSnackbar
                    .make(view, text, TSnackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,SizeUtils.dp2px(64));
            snackbarView.setLayoutParams(layoutParams);
            TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);

            if (isSuc) {
                snackbarView.setBackgroundColor(ContextCompat.getColor(ChainUpApp.appContext, R.color.feedback_success));
            } else {
                snackbarView.setBackgroundColor(ContextCompat.getColor(ChainUpApp.appContext, R.color.feedback_error));
            }

            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16f);
            snackbar.show();
        }
    }


//
//    var snackbar = TSnackbar
//            .make(v_container, "手机格式错误，请重新输入", TSnackbar.LENGTH_SHORT)
//    val snackbarView = snackbar.view
////            snackbarView.setBackgroundColor(Color.parseColor("#ffef5a61"))
//            snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
//
//    val textView = snackbarView.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text)
//            textView.setTextColor(Color.WHITE)
//    textView.gravity = Gravity.CENTER
//    textView.textSize = 18f
//            snackbar.show()
//

}
