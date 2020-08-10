package com.follow.order.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.follow.order.R;


public class FollowTipDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv_title, tv_content, btn_1, btn_2;
    private OnDialogClickListener listener_1, listener_2;

    public FollowTipDialog(Context context) {
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

        setContentView(R.layout.dialog_follow_tip);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);

    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }

    public void setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }
    }

    public TextView getButton1() {
        return btn_1;
    }

    public TextView getButton2() {
        return btn_2;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_1) {
            if (listener_1 != null) {
                listener_1.onClick(v, this);
            }
        } else if (v.getId() == R.id.btn_2) {
            if (listener_2 != null) {
                listener_2.onClick(v, this);
            }
        }
    }

    public void setButton1(String text, OnDialogClickListener clickListener) {
        this.btn_1.setText(text);
        this.btn_1.setVisibility(View.VISIBLE);
        this.listener_1 = clickListener;
        this.btn_1.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
    }

    public void setButton2(String text, OnDialogClickListener clickListener) {
        this.btn_2.setText(text);
        this.btn_2.setVisibility(View.VISIBLE);
        this.listener_2 = clickListener;
        this.btn_2.setOnClickListener(this);
    }

    public void setButtonOnly(String text, String color, OnDialogClickListener clickListener) {
        if (!TextUtils.isEmpty(color) && color.startsWith("#")) {
            try {
                this.btn_2.setTextColor(Color.parseColor(color));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setButtonOnly(text, clickListener);
    }

    public void setButtonOnly(String text, OnDialogClickListener clickListener) {
        this.btn_1.setVisibility(View.GONE);
        this.btn_2.setText(text);
        this.btn_2.setVisibility(View.VISIBLE);
        this.listener_2 = clickListener;
        this.btn_2.setOnClickListener(this);

    }

    public interface OnDialogClickListener {

        public void onClick(View button, FollowTipDialog dialog);
    }
}
