package com.follow.order.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.follow.order.R;


public class FollowStopDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv_content, tv_cancel, tv_stop;
    private OnDialogClickListener clickListener;

    public FollowStopDialog(Context context) {
        super(context, R.style.CommonDialog);
        setCanceledOnTouchOutside(false);
        getWindow().getAttributes().width = -2;
        getWindow().getAttributes().height = -2;
        getWindow().getAttributes().y = 0;
        getWindow().setGravity(Gravity.CENTER_VERTICAL);
        getWindow().setAttributes(getWindow().getAttributes());
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);
        this.context = context;

        setContentView(R.layout.dialog_follow_stop);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_stop = findViewById(R.id.tv_stop);

        tv_cancel.setOnClickListener(this);
        tv_stop.setOnClickListener(this);

    }

    public void setContent(String content) {
        tv_content.setText(content);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.tv_stop) {
            dismiss();
            if (clickListener != null) {
                clickListener.onStopFollow();
            }
        }
    }


    public interface OnDialogClickListener {

        void onStopFollow();
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
