package com.yjkj.chainup.net_new;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yjkj.chainup.R;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-27 16:11
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-27 16:11
 * @UpdateRemark: 更新说明
 */
public class NLoadingDialog {

    String loadText = "";

    private NLoadingDialog() {
    }

    private Activity mActivity;

    public NLoadingDialog(Activity activity) {
        this.mActivity = activity;
    }

    public NLoadingDialog(Activity mActivity, String loadText) {
        this.loadText = loadText;
        this.mActivity = mActivity;
    }

    private Dialog dialog;

    public void showLoadingDialog() {
        if (mActivity.isFinishing())
            return;
        if (dialog == null) {
            if (TextUtils.isEmpty(loadText)) {
                dialog = createLoadingDialog(mActivity);
            } else {
                dialog = createLoadingDialog(mActivity, loadText);
            }

        } else if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void setLoadText(String loadText) {
        this.loadText = loadText;
        if (dialog != null) {
            TextView tv_load_text = dialog.findViewById(R.id.tv_load_text);
            tv_load_text.setVisibility(View.VISIBLE);
            tv_load_text.setText(loadText);
        }
    }

    public void closeLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 有取消回调的进度dialog
     *
     * @param context
     * @return dialog
     */
    private static Dialog createLoadingDialog(Activity context) {
        if (context == null || context.isFinishing()) return null;
        try {
            final Dialog dialog = new Dialog(context, R.style.NoBackGroundDialog);
            dialog.show();
            Window window = dialog.getWindow();
            assert window != null;
            window.setGravity(Gravity.CENTER);
            window.setLayout(android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT);
            View view = context.getLayoutInflater().inflate(
                    R.layout.loading_dialog, null);
            window.setContentView(view);//
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Dialog createLoadingDialog(Activity context, String loadText) {
        Dialog dialog = createLoadingDialog(context);
        if (!TextUtils.isEmpty(loadText)) {
            TextView tv_load_text = dialog.findViewById(R.id.tv_load_text);
            tv_load_text.setVisibility(View.VISIBLE);
            tv_load_text.setText(loadText);
        }
        return dialog;
    }
}
