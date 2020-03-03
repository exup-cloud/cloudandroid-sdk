package com.bmtc.sdk.contract.common;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import com.bmtc.sdk.contract.R;
import com.bumptech.glide.Glide;


public class SlLoadingDialog {

    private Context mContext;
    private Dialog dialog;

    private ImageView mImgLoad;

    public SlLoadingDialog(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        dialog = new Dialog(mContext, R.style.progress_dialog);
        dialog.setContentView(R.layout.sl_dialog_loading);

        mImgLoad = dialog.findViewById(R.id.iv_load);
        Glide.with(mContext)
                .load("file:///android_asset/sl_preloading.gif")
                .into(mImgLoad);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
