package com.yjkj.chainup.contract.widget.pswkeyboard.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.yjkj.chainup.R;


/**
 * 输入支付密码
 *
 * @author lining
 */
public class PopEnterSetPassword extends PopupWindow {

    private SetPasswordView pwdView;

    private View mMenuView;

    private Activity mContext;

    public PopEnterSetPassword(final Activity context) {

        super(context);

        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMenuView = inflater.inflate(R.layout.sl_view_pop_enter_set_password, null);
        if (inflater == null) {
            return;
        }
        pwdView = mMenuView.findViewById(R.id.pwd_view);

        //添加密码输入完成的响应
        pwdView.getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // 监听X关闭按钮
        pwdView.getImgCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.pop_add_ainm);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    public Button getConfirmBtn() {
        return pwdView.getConfirmBtn();
    }

    public String getPassword() {
        return pwdView.getPassword();
    }
}
