package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by ChenLiheng on 2016/4/25.
 * desc：通用提示框组件
 */
public class PromptWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout parentFrame;
    private ImageView ivCaution;
    private Button btnClose;
    private TextView tvContent;
    private Button btnOk;
    private Button btnCancel;
    private TextView title;
    private RelativeLayout titleParent;
    private Context context;
    private AnimatorSet mAnimatorSet;
    private static final int DURATION = 700;
    private long mDuration =DURATION ;


    public PromptWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_dialog_prompt, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //this.setAnimationStyle(R.style.popwin_anim_style);

        //this.setAnimationStyle(R.style.PopWinAnim);
        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        //backgroundAlpha(1f);

        //添加pop窗口关闭事件
        //this.setOnDismissListener(new poponDismissListener());
        initView(rootView);
    }

    public void startAnim(){
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "translationY", 300, 0).setDuration(mDuration),
                ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(mDuration*3/2)

        );
        mAnimatorSet.start();
    }

    private void initView(View view) {
        parentFrame = view.findViewById(R.id.ll_frame);
        ivCaution = view.findViewById(R.id.iv_caution);
        btnClose = view.findViewById(R.id.btn_close);
        tvContent = view.findViewById(R.id.tv_content);
        btnOk = view.findViewById(R.id.btn_ok);
        btnCancel = view.findViewById(R.id.btn_cancel);
        title = view.findViewById(R.id.tv_title);
        titleParent = view.findViewById(R.id.title_parent);

        titleParent.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        btnClose.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    public void showCaution() {
        ivCaution.setVisibility(View.VISIBLE);
    }

    public void showTitle(String str) {
        title.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
        title.setText(str);
    }

    public void showBtnClose(String str) {
        btnClose.setVisibility(View.VISIBLE);
        titleParent.setVisibility(View.VISIBLE);
        btnClose.setText(str);
    }

    public void showTvContent(String str) {
        tvContent.setVisibility(View.VISIBLE);
        tvContent.setText(str);
    }

    public void centerTvContent() {
        tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void showTvContent(SpannableStringBuilder ssb) {
        tvContent.setVisibility(View.VISIBLE);
        tvContent.setText(ssb);
    }

    public void showBtnOk(String str) {
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setText(str);
    }

    public void showBtnCancel(String str) {
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setText(str);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_close){
            this.dismiss();
        }
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

}
