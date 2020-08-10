package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.net_new.JSONUtil;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Description: otc/public_info  接口的数据
 * @Author: wanghao
 * @CreateDate: 2019-10-23 15:52
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-10-23 15:52
 * @UpdateRemark: 更新说明
 */
public class OTCPublicInfoDataService {

    /*
     * key的命名跟接口名保持一致，/用下划线 _ 代替
     */
    private static final String key = "otc_public_info";

    private MMKVDb mMMKVDb;

    private OTCPublicInfoDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static OTCPublicInfoDataService mOTCPublicInfoDataService;

    private static JSONObject cachObj;

    public static OTCPublicInfoDataService getInstance() {
        if (null == mOTCPublicInfoDataService) {
            mOTCPublicInfoDataService = new OTCPublicInfoDataService();
        }
        return mOTCPublicInfoDataService;
    }


    public void saveData(JSONObject data) {
        if (null != data) {
            mMMKVDb.saveData(key, data.toString());
            cachObj = data;
        }
    }

    public JSONObject getData() {
        if (null != cachObj)
            return cachObj;
        String value = mMMKVDb.getData(key);
        if (StringUtil.checkStr(value)) {
            try {
                return cachObj = new JSONObject(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ArrayList<JSONObject> getPayments() {
        JSONObject data = getData();
        if (null != data) {
            return JSONUtil.arrayToList(data.optJSONArray("payments"));
        }
        return null;
    }

    public ArrayList<JSONObject> getPaycoins() {
        JSONObject data = getData();
        if (null != data) {
            return JSONUtil.arrayToList(data.optJSONArray("paycoins"));
        }
        return new ArrayList<>();
    }

    public ArrayList<JSONObject> getCountryNumberInfos() {
        JSONObject data = getData();
        if (null != data) {
            return JSONUtil.arrayToList(data.optJSONArray("countryNumberInfo"));
        }
        return null;
    }

    public JSONObject getPersonalIcon() {
        JSONObject data = getData();
        if (data == null || data.length() <= 0) {
            return new JSONObject();
        }
        return data.optJSONObject("app_personal_icon");
    }


    public String getDefaultCoin() {
        JSONObject data = getData();
        if (null != data) {
            return data.optString("defaultCoin");
        }
        return null;
    }

    public String getotcDefaultPaycoin() {
        JSONObject data = getData();
        if (null != data) {
            return data.optString("otcDefaultPaycoin");
        }
        return null;
    }

    public String getDafaultCoin() {
        JSONObject data = getData();
        if (null != data) {
            return data.optString("defaultSeach");
        }
        return null;
    }


    /**
     * 获取payments
     */
    public ArrayList<JSONObject> getPaymentsListData(JSONArray data) {
        if (data == null || data.length() <= 0) {
            return new ArrayList<>();
        }
        return JSONUtil.arrayToList(data);
    }

    public String getwindControlSwitch() {
        JSONObject data = getData();
        if (null != data) {
            return data.optString("wind_control_switch", "");
        }
        return "0";
    }

}
