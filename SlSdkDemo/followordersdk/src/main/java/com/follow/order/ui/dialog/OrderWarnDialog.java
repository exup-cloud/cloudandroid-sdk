package com.follow.order.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.follow.order.R;


public class OrderWarnDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv_content, tv_know;

    public OrderWarnDialog(Context context) {
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

        setContentView(R.layout.dialog_order_warn);
        tv_content = findViewById(R.id.tv_content);
        tv_know = findViewById(R.id.tv_know);

        tv_know.setOnClickListener(this);

    }

    public void setContent(String content) {
        tv_content.setText(content);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_know) {
            dismiss();
        }
    }

}
