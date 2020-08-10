package com.yjkj.chainup.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yjkj.chainup.R;
import com.yjkj.chainup.db.service.CheckUpdateDataService;
import com.yjkj.chainup.manager.LanguageUtil;
import com.yjkj.chainup.model.model.MainModel;
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class CheckUpdateUtil {

    private static final String TAG = "CheckUpdateUtil";

    /**
     * 检查升级
     * AppUpdateBean(build = 0, downloadUrl = null, force = 0, title = null, version = null, content = null)
     *
     * force : 0 - 不强制 ; 1 - 强制升级
     *
     * @param isAutoUpdate true为自动升级，false为手动点击检查更新
     */
    public static void update(final Activity activity, final boolean isAutoUpdate){
        Activity loadingActivity = activity;
        if(isAutoUpdate){
            loadingActivity = null;
        }
        LogUtil.d(TAG,"CheckUpdateUtil==");

        new MainModel().getAppVersion(new NDisposableObserver(loadingActivity,false) {
            @Override
            public void onResponseSuccess(@NotNull JSONObject jsonObject) {
                LogUtil.d(TAG,"CheckUpdateUtil==onResponseSuccess==jsonObject is "+jsonObject);

                String code = jsonObject.optString("code");
                if(!"0".equals(code)){
                    String msg = jsonObject.optString("msg");
                    if(!isAutoUpdate){
                        NToastUtil.showTopToast(true,msg);
                    }
                    return;
                }

                JSONObject data = jsonObject.optJSONObject("data");
                if(null!=data){
                    int build = data.optInt("build");

                    int localVersionCode = UpdateHelper.getLocalVersion(activity);
                    if(localVersionCode<build){
                        boolean isForce = (1==data.optInt("force"));
                        boolean hideDialog = CheckUpdateDataService.getInstance().hideDialog();
                        if(hideDialog && !isForce && isAutoUpdate){
                            return;
                        }
                        showUpdateDialog(activity,data);
                    }else{
                        if(!isAutoUpdate){
                            NToastUtil.showTopToast(true, LanguageUtil.getString(activity,"the_latest_version"));
                        }
                    }
                }
            }

            @Override
            public void onResponseFailure(int code, @Nullable String msg) {
                super.onResponseFailure(code, msg);
                LogUtil.d(TAG,"CheckUpdateUtil==onResponseFailure==code is "+code+",msg is "+msg);
                if(!isAutoUpdate){
                    NToastUtil.showTopToast(false,msg);
                }
            }
        });

    }

    /**
     * 升级的弹窗
     */
    private static Dialog showUpdateDialog(Activity activity, JSONObject data) {
        boolean isForce = (1==data.optInt("force"));
        String downloadUrl = data.optString("downloadUrl");
        String title = data.optString("title");
        String version = data.optString("version");
        String content = data.optString("content");

        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_update,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_content = view.findViewById(R.id.tv_content);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        TextView btn_confirm = view.findViewById(R.id.btn_confirm);

        tv_title.setText(""+title);
        tv_content.setText(""+content);

        btn_cancel.setVisibility(isForce?View.GONE:View.VISIBLE);


        btn_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                CheckUpdateDataService.getInstance().saveData(CheckUpdateDataService.hideDialog);
                dialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!isForce){
                    dialog.dismiss();
                }
                CheckUpdateDataService.getInstance().saveData(0);
                IntentUtil.forwardBrowse(activity,downloadUrl);
            }
        });
        dialog.setView(view);
        dialog.setCancelable(false);
        if(!activity.isFinishing()){
            dialog.show();
        }
        return dialog;
    }

}