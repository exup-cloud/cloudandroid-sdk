package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @Author lianshangljl
 * @Date 2019-11-11-20:19
 * @Email buptjinlong@163.com
 * @description
 */
public class LeverDataService {


    private static final String key = "financeBalance";

    private MMKVDb mMMKVDb;

    private static JSONObject leverData;

    private LeverDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static LeverDataService mLeverDataService;

    public static LeverDataService getInstance() {
        if (null == mLeverDataService) {
            mLeverDataService = new LeverDataService();
        }
        return mLeverDataService;
    }

    public void saveData(JSONObject data) {
        if (null != data) {

            mMMKVDb.saveData(key, data.toString());
            leverData = data;
        }
    }

    /*
     * return 最外层的data对象
     */
    public JSONObject getData(JSONObject data) {
        if (null == data) {
            if (null != leverData && leverData.length() > 0)
                return leverData;
            String dataStr = mMMKVDb.getData(key);
            if (StringUtil.checkStr(dataStr)) {
                try {
                    data = new JSONObject(dataStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public JSONObject getLeverMap(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("leverMap");
        }
        return null;
    }





}
