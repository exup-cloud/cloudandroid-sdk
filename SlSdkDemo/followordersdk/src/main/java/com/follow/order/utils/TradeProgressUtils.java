package com.follow.order.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.follow.order.R;


public class TradeProgressUtils {

    private Dialog builder;

    public void showProgress(Activity activity) {
        //自定义布局
        View layout = activity.getLayoutInflater().inflate(R.layout.fo_dialog_loading, null);
        final View dialog_circle = layout.findViewById(R.id.dialog_circle);
        builder = new Dialog(activity, R.style.fo_loading_dialog);
        builder.setContentView(layout);
        if (!activity.isFinishing()) {
            builder.show();
        }
        builder.setCanceledOnTouchOutside(false);

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fo_anim_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        dialog_circle.startAnimation(animation);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialog_circle.clearAnimation();
            }
        });

    }

    public Dialog getDilaog() {
        return builder;
    }

    public void dismiss() {
        try {
            if (builder != null)
                builder.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
