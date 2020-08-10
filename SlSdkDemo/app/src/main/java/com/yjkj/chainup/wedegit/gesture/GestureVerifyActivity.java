package com.yjkj.chainup.wedegit.gesture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.yjkj.chainup.R;
import com.yjkj.chainup.base.BaseActivity;
import com.yjkj.chainup.bean.LoginInfo;
import com.yjkj.chainup.db.service.UserDataService;
import com.yjkj.chainup.extra_service.arouter.ArouterUtil;
import com.yjkj.chainup.manager.LoginManager;
import com.yjkj.chainup.net.HttpClient;
import com.yjkj.chainup.net.retrofit.NetObserver;
import com.yjkj.chainup.new_version.activity.NewMainActivity;
import com.yjkj.chainup.util.ToastUtils;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 手势绘制/校验界面
 */
public class GestureVerifyActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 手机号码
     */
    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
    /**
     * 意图
     */
    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
    private RelativeLayout mTopLayout;
    private TextView mTextTitle;
    private TextView mTextCancel;
    private ImageView mImgUserLogo;
    private TextView mTextPhoneNumber;
    private TextView mTextTip;
    private RelativeLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextForget;
    private TextView mTextOther;
    private String mParamPhoneNumber;
    private long mExitTime = 0;
    private int mParamIntentCode;
    private String pass;


    private boolean isFromSplash = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_verify);
        setTitle(getString(R.string.safety_text_gesturePassword));
//        ObtainExtraData();
        pass = UserDataService.getInstance().getGesturePass();
        setUpViews();
        setUpListeners();
        isFromSplash = getIntent().getBooleanExtra("fromSplash", false);
    }

    private void ObtainExtraData() {
        mParamPhoneNumber = getIntent().getStringExtra(PARAM_PHONE_NUMBER);
        mParamIntentCode = getIntent().getIntExtra(PARAM_INTENT_CODE, 0);
    }

    private void setUpViews() {
        mTopLayout = findViewById(R.id.top_layout);
        mTextTitle = findViewById(R.id.text_title);
        mTextCancel = findViewById(R.id.text_cancel);
//        mImgUserLogo = (ImageView) findViewById(R.id.user_logo);
        mTextPhoneNumber = findViewById(R.id.text_phone_number);
        mTextTip = findViewById(R.id.text_tip);
        mGestureContainer = findViewById(R.id.gesture_container);
        mTextForget = findViewById(R.id.text_forget_gesture);
        mTextOther = findViewById(R.id.text_other_account);


        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, true, pass,
                new GestureDrawline.GestureCallBack() {

                    private final JSONObject userInfoData = UserDataService.getInstance().getUserData();

                    @Override
                    public void onGestureCodeInput(String inputCode) {
                        Log.d(TAG, "========手势验证:=======" + inputCode);
                    }

                    @Override
                    public void checkedSuccess() {
                        mGestureContentView.clearDrawlineState(0L);
//                        Toast.makeText(GestureVerifyActivity.this, "密码正确", Toast.LENGTH_SHORT).show();


                        LoginInfo loginInfo = LoginManager.getInstance().getLoginInfo();
                        LoginManager.getInstance().saveGesturePwdErrorTimes(5);

                        if (loginInfo == null) {
                            return;
                        }


                        String account = loginInfo.getAccount();

                        String loginPwd = loginInfo.getLoginPwd();


                        if (userInfoData == null) {
                            return;
                        }


                        /**
                         * 手机帐号&登录密码都有
                         */
                        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(loginPwd)) {
                            if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd())) {
                                handPwdLogin(account, loginPwd, UserDataService.getInstance().getGesturePwd());
                            } else {
                                handPwdLogin(account, loginPwd, pass);
                            }
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void checkedFail() {
                        mGestureContentView.clearDrawlineState(1300L);
                        mTextTip.setVisibility(View.VISIBLE);
                        mTextTip.setText(getString(R.string.gesture_pass_error));
                        int errorTimes = LoginManager.getInstance().getGesturePwdErrorTimes();
                        if (errorTimes <= 1) {
                            cleanGesturePwd();
                            mTextTip.setText(getString(R.string.gesture_error_to_login));
                            UserDataService.getInstance().saveGesturePass("");

                        } else {
                            errorTimes--;
                            LoginManager.getInstance().saveGesturePwdErrorTimes(errorTimes);
                            mTextTip.setText(getString(R.string.warn_gesture_pwd_error) + errorTimes);
                        }

                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                    }
                });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
    }


    /**
     * 逻辑暂时不这么走
     *
     * @param loginInfo
     */
    private void inithandPwdLogin(LoginInfo loginInfo) {
        /**
         * 手机
         */
        String mobile = loginInfo.getMobile();
        String mobilePwd = loginInfo.getMobilePwd();
        /**
         * 邮箱
         */
        String email = loginInfo.getEmail();
        String emailPwd = loginInfo.getEmailPwd();

        /**
         * 手势密码
         */
        String gesturePass = UserDataService.getInstance().getGesturePass();

        if (TextUtils.isEmpty(gesturePass)) {
            return;
        } else {
            /**
             * 手机帐号&登录密码都有
             */
            if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(mobilePwd)) {
                handPwdLogin(mobile, mobilePwd, gesturePass);
            } else {
                /**
                 * 手机不行，邮箱上
                 */
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(emailPwd)) {
                    handPwdLogin(email, emailPwd, gesturePass);
                } else {
                    return;
                }

            }
        }
    }

    private void setUpListeners() {
        mTextCancel.setOnClickListener(this);
        mTextForget.setOnClickListener(this);
        mTextOther.setOnClickListener(this);
    }

    private String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            reStart(this);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                finish();
                break;
            case R.id.text_other_account:
                /**
                 * 其他登录方式
                 */
                ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
                this.finish();
                break;
            default:
                break;
        }
    }


    /**
     * 手势密码登录
     */
    private void handPwdLogin(String account, String loginPwd, String handPwd) {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(loginPwd) || TextUtils.isEmpty(handPwd)) {
            return;
        }
        showProgressDialog("");
        HttpClient.Companion.getInstance().handPwdLogin(account, loginPwd, handPwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetObserver<JsonObject>() {

                    @Override
                    protected void onHandleSuccess(JsonObject jsonObject) {
                        cancelProgressDialog();

                        String token = jsonObject.get("token").getAsString();
                        UserDataService.getInstance().saveToken(token);
                        HttpClient.Companion.getInstance().setToken(token);

                        setResult(RESULT_OK);
                        GestureVerifyActivity.this.finish();

                    }

                    @Override
                    protected void onHandleError(int code, String msg) {
                        super.onHandleError(code, msg);
                        cancelProgressDialog();

                        Log.d(TAG, "======手势登录：=====error==" + msg);
                        ToastUtils.showToast(msg);
                    }

                });
    }


    /**
     * 清空用户手势密码
     */
    private void cleanGesturePwd() {
        if (!TextUtils.isEmpty(UserDataService.getInstance().getUserInfo4UserId())) {
            HttpClient.Companion.getInstance().cleanGesturePwd(String.valueOf(UserDataService.getInstance().getUserInfo4UserId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetObserver<Object>() {
                        @Override
                        protected void onHandleSuccess(Object o) {
                            ArouterUtil.navigation("/login/NewVersionLoginActivity", null);
                            finish();
                        }
                    });
        }


    }

    private void reStart(Context context) {
        Intent intent = new Intent(context, NewMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
