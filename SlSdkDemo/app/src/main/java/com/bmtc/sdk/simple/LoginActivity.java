package com.bmtc.sdk.simple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Account;
import com.bmtc.sdk.library.trans.data.CDLogin;
import com.bmtc.sdk.library.trans.data.SLUser;
import com.bmtc.sdk.library.uilogic.LogicUserState;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.LogUtil;
import com.bmtc.sdk.library.utils.LoginHelper;
import com.bmtc.sdk.library.utils.PreferenceManager;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_user;
    private EditText et_pwd;
    private Button bt_login;

    public static final String sTokenKey = "tokenKey";

    private String token;
    private String name;
    private String pwd;


    public static void show(Activity activity){
        Intent in = new Intent(activity,LoginActivity.class);
        activity.startActivity(in);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        token = PreferenceManager.getString(LoginActivity.this,LoginActivity.sTokenKey,"");
    }

    private void initView(){
        et_user = findViewById(R.id.et_user);
        et_pwd = findViewById(R.id.et_pwd);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                String name = et_user.getText().toString();
                String pwd = et_pwd.getText().toString();

                if(TextUtils.isEmpty(name)){
                    ToastUtil.shortToast(LoginActivity.this,"用户名不能为空!");
                    return;
                }

                if(TextUtils.isEmpty(pwd)){
                    ToastUtil.shortToast(LoginActivity.this,"密码不能为空!");
                    return;
                }
                name = "+86 "+name;
                CDLogin cdLogin = new CDLogin();
                long  nonce = System.currentTimeMillis() * 1000;
                String md5 = UtilSystem.toMD5(UtilSystem.toMD5(pwd)+nonce);
                cdLogin.setPassword(md5);
                cdLogin.setPwdMd5(UtilSystem.toMD5(pwd));
                cdLogin.setStatus(1);
                cdLogin.setNonce(nonce);
                cdLogin.setPhone(name);
                cdLogin.setArea_code("+86");

                LoginHelper.doCheckLogin(this,cdLogin,new IResponse<String>(){

                    @Override
                    public void onResponse(String errno, String message, String token) {
                        if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                            ToastUtil.shortToast(LoginActivity.this, message);
                            return;
                        }

                        PreferenceManager.putStringAsyn(LoginActivity.this,sTokenKey,token);
                        LogUtil.d(SLSDKAgent.TAG,"token:"+SLSDKAgent.slUser.getToken());
                        LogicWebSocketContract.getInstance().authenticate();
                        LoginActivity.this.finish();
                        LogicUserState.getInstance().refresh(LogicUserState.STATE_LOGIN);
                        ToastUtil.shortToast(LoginActivity.this, "登录成功");
                    }
                });

                break;
        }
    }
}
