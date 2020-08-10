package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-09-09 20:03
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-09-09 20:03
 * @UpdateRemark: 更新说明
 */
public class RateDataService {

    private static final String rate_key = "rates_json";
    private MMKVDb mMMKVDb;

    private RateDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static RateDataService mUserDataService;

    private static JSONObject cachObj;

    public static RateDataService getInstance() {
        if (null == mUserDataService)
            mUserDataService = new RateDataService();
        return mUserDataService;
    }

    /*
     * 存汇率json数据
     */
    public void saveData(JSONObject data) {
        if(null!=data){
            cachObj = data;
            mMMKVDb.saveData(rate_key, data.toString());
        }
    }

    public JSONObject getValue() {
        if(null!=cachObj){
            return cachObj;
        }
        String value = mMMKVDb.getData(rate_key);
        if (StringUtil.checkStr(value)) {
            try {
                return cachObj = new JSONObject(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     * 得到不同国家的汇率
     */
    public JSONObject getRate(String country) {
        JSONObject rate = getValue();
        if (null != rate) {
            return rate.optJSONObject(country);
        }
        return null;
    }
}
