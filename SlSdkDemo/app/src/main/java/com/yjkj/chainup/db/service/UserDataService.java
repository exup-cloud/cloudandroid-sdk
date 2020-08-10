package com.yjkj.chainup.db.service;

import android.text.TextUtils;

import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.data.ContractUser;
import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.manager.LoginManager;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-12 15:43
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-12 15:43
 * @UpdateRemark: 更新说明
 */
public class UserDataService {

    private static final String userDataKey = "userData";
    private static final String SP_GESTURE_PASS = "sp_gesture_pass";
    private static final String userTokenKey = "userToken";
    private static final String showAssets = "showAssets";
    private static final String show_little_assets = "show_little_assets";//隐藏小额资产

    private static String LOGIN_TOKEN = "";
    private MMKVDb mMMKVDb;

    private UserDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static UserDataService mUserDataService;

    public static UserDataService getInstance() {
        if (null == mUserDataService)
            mUserDataService = new UserDataService();
        return mUserDataService;

    }

    public void saveData(JSONObject data) {
        if (null != data) {
            mMMKVDb.saveData(userDataKey, data.toString());
        }

    }

    public JSONObject getUserData() {
        String data = mMMKVDb.getData(userDataKey);
        if (StringUtil.checkStr(data)) {
            try {
                return new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getToken() {
        if (LOGIN_TOKEN.isEmpty()) {
            LOGIN_TOKEN = getTokenData();
            return LOGIN_TOKEN;
        }
        return LOGIN_TOKEN;
    }

    public void saveToken(String token) {
        LOGIN_TOKEN = token;
        setToken(token);
        notifyContractLoginStatusListener(true);
    }

    public void clearToken() {
        LOGIN_TOKEN = "";
        setToken("");
        notifyContractLoginStatusListener(true);
    }

    public void clearTokenByContract() {
        LOGIN_TOKEN = "";
        setToken("");
    }

    /**
     * 合约业务需要，需监听登录回调
     */
    public void notifyContractLoginStatusListener(Boolean isForce) {
        if (TextUtils.isEmpty(LOGIN_TOKEN)) {
            ContractSDKAgent.INSTANCE.exitLogin();
        } else {
            ContractUser user = new ContractUser();
            user.setToken(LOGIN_TOKEN);
            ContractSDKAgent.INSTANCE.setUser(user);
        }
    }

    public void saveGesturePass(String pass) {
        mMMKVDb.saveData(SP_GESTURE_PASS, pass);
        LoginManager.getInstance().saveGesturePwdErrorTimes(5);
    }

    public String getGesturePass() {
        return mMMKVDb.getData(SP_GESTURE_PASS);
    }

    public void clearLoginState() {
        LOGIN_TOKEN = "";
        saveData(new JSONObject());
        notifyContractLoginStatusListener(true);
    }

    public String getGesturePwd() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("gesturePwd");
        return "";
    }

    public String getNickName() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("nickName");
        return "";

    }

    public String getCountryCode() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("countryCode");
        return "";

    }

    public String getEmail() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("email");
        return "";
    }

    public String getInviteCode() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("inviteCode");
        return "";
    }

    public String getInviteUrl() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("inviteUrl");
        return "";
    }

    public String getMobileNumber() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("mobileNumber");
        return "";
    }

    public String getRealName() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("realName");
        return "";
    }

    public int getAuthLevel() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optInt("authLevel");
        return -1;
    }

    public int getGoogleStatus() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optInt("googleStatus");
        return 0;

    }

    public int getIsOpenMobileCheck() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optInt("isOpenMobileCheck");
        return 0;
    }

    public int getIsCapitalPwordSet() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optInt("isCapitalPwordSet");
        return 0;
    }

    public int getAccountStatus() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optInt("accountStatus");
        return 0;

    }

    public boolean isLogined() {
        return StringUtil.checkStr(getToken());
    }


    public String getUserInfo4UserId() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("id");
        return "";
    }

    public String getUserAccount() {
        JSONObject data = getUserData();
        if (null != data)
            return data.optString("userAccount");
        return "";
    }


    /*
     * 资产的隐藏与展示
     */
    public void setShowAssetStatus(boolean isShowAssets) {
        mMMKVDb.saveBooleanData(showAssets, isShowAssets);
    }

    public boolean isShowAssets() {
        return mMMKVDb.getBooleanData(showAssets, true);
    }


    /**
     * 是否隐藏"小额资产"
     */
    public boolean getAssetState() {
        return mMMKVDb.getBooleanData(show_little_assets, false);
    }

    public void saveAssetState(boolean isHide) {
        mMMKVDb.saveBooleanData(show_little_assets, isHide);
    }

    private void setByKey(String key, String object) {
        if (null != object) {
            mMMKVDb.saveData(key, object);
        }
    }

    private String getKeyByKey(String key) {
        if (null != mMMKVDb) {
            return mMMKVDb.getData(key);
        }
        return "";
    }

    private void setToken(String tokenData) {
        setByKey(userTokenKey, tokenData);
    }

    private String getTokenData() {
        return getKeyByKey(userTokenKey);
    }

}
