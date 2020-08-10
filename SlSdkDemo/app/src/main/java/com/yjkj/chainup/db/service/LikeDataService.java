package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.manager.NCoinManager;
import com.yjkj.chainup.net_new.JSONUtil;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @Description: 收藏自选数据
 * @Author: wanghao
 * @CreateDate: 2019-08-09 12:26
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-09 12:26
 * @UpdateRemark: 更新说明
 */
public class LikeDataService {

    private static final String TAG = "LikeDataService";

    private static final String collectData = "collectData";

    private MMKVDb mMMKVDb;

    private LikeDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static LikeDataService mLikeDataService;

    public static LikeDataService getInstance() {
        if (null == mLikeDataService) {
            mLikeDataService = new LikeDataService();
        }
        return mLikeDataService;
    }

    /*
     * 收藏/自选数据保存
     */
    public void saveCollecData(String symbol) {

        JSONObject symbolObj = NCoinManager.getSymbolObj(symbol);
//        /**
//         * 处理数据源
//         */
//        if (symbolObj != null) {
//            int newcoinFlag = symbolObj.optInt("newcoinFlag");
//            boolean isEdited = symbolObj.optBoolean("isEdited", false);
//            if(!isEdited){
//                try {
//                    switch (newcoinFlag) {
//                        case 0:
//                            symbolObj.put("newcoinFlag", 1);
//                            break;
//                        case 1:
//                            symbolObj.put("newcoinFlag", 2);
//                            break;
//                        case 2:
//                            symbolObj.put("newcoinFlag", 3);
//                            break;
//                        case 3:
//                            symbolObj.put("newcoinFlag", 0);
//                            break;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        if (null == symbolObj || symbolObj.length() <= 0)
            return;


        try {
            if (symbolObj.optString("newcoinFlag") == "0") {
                symbolObj.put("newcoinFlag", "1");
            }
            JSONArray array = getCollecArray();
            if (null != array) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.optJSONObject(i);
                    if (symbol.equalsIgnoreCase(obj.optString("symbol"))) {
                        return;
                    }
                }
            } else {
                array = new JSONArray();
            }
            array.put(symbolObj);
            mMMKVDb.saveData(collectData, array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 是否已加入自选
     */
    public boolean hasCollect(String symbol) {
        JSONArray array = getCollecArray();
        if (null == array || array.length() <= 0)
            return false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            if (jsonObject.optString("symbol").equalsIgnoreCase(symbol)) {
                return true;
            }
        }
        return false;
    }

    /*
     * 移除自选
     */
    public ArrayList<JSONObject> removeCollect(String symbol) {
        JSONArray array = removeCollectArray(symbol);
        ArrayList<JSONObject> list = JSONUtil.arrayToList(array);
        if (null != list && list.size() > 0) {
            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    int a = o1.optInt("newcoinFlag");
                    int b = o2.optInt("newcoinFlag");
                    return a - b;
                }
            });
            return list;
        }
        return null;
    }

    private JSONArray removeCollectArray(String symbol) {
        JSONArray array = getCollecArray();
        if (null == array || array.length() <= 0)
            return null;

        JSONArray newArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            if (!(jsonObject.optString("symbol").equalsIgnoreCase(symbol))) {
                newArray.put(jsonObject);
            }
        }
        mMMKVDb.saveData(collectData, newArray.toString());
        return newArray;
    }

    /*
     * 清除本地所有币对数据void
     */
    public void clearAllCollect() {
        mMMKVDb.saveData(collectData, "");
    }

    /*
     * 本地搜藏币对数据与market币对，为最终本地自选数据展示
     */
    public synchronized ArrayList<JSONObject> getCollecData(boolean isLever) {
        ArrayList<JSONObject> list = JSONUtil.arrayToList(getCollecArray());
        if (null != list && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObj = list.get(i);
                int newcoinFlag = jsonObj.optInt("newcoinFlag");
                if (!jsonObj.optBoolean("isEdited", false)) {
                    try {
                        switch (newcoinFlag) {
                            case 0:
                                jsonObj.put("newcoinFlag", 1);
                                break;
                            case 1:
                                jsonObj.put("newcoinFlag", 2);
                                break;
                            case 2:
                                jsonObj.put("newcoinFlag", 3);
                                break;
                            case 3:
                                jsonObj.put("newcoinFlag", 0);
                                break;
                        }
                        jsonObj.put("isEdited", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    int a = o1.optInt("newcoinFlag");
                    int b = o2.optInt("newcoinFlag");
                    return a - b;
                }
            });
            if (PublicInfoDataService.getInstance().isLeverOpen(null) && isLever) {
                ArrayList<JSONObject> newList = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject jsonObject = list.get(i);
                        boolean isOpenLever = jsonObject.has("isOpenLever") && !"0".equals(jsonObject.optString("isOpenLever"));
                        boolean multiple = jsonObject.has("multiple") && !"0".equals(jsonObject.optString("multiple"));
                        if (multiple && isOpenLever) {
                            newList.add(jsonObject);
                        }
                    }
                }
                return newList;
            } else {
                return list;
            }
        }
        return null;
    }

    private JSONArray getCollecArray() {
        String values = mMMKVDb.getData(collectData);
        if (StringUtil.checkStr(values)) {
            try {
                JSONArray array = new JSONArray(values);
                JSONArray marketArray = NCoinManager.getMarketArray(false);
                if (null == marketArray || marketArray.length() <= 0)
                    return array;

                JSONArray new_like_array = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    String symbol = array.optJSONObject(i).optString("symbol");
                    for (int j = 0; j < marketArray.length(); j++) {
                        JSONObject jsonObj = marketArray.optJSONObject(j);
                        String market_symbol = jsonObj.optString("symbol");
                        if (StringUtil.checkStr(symbol) && symbol.equals(market_symbol)) {
                            new_like_array.put(jsonObj);
                        }
                    }
                }
                return new_like_array.length() > 0 ? new_like_array : array;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public JSONArray getSymbols() {
        String values = mMMKVDb.getData(collectData);
        if (StringUtil.checkStr(values)) {
            try {
                JSONArray array = new JSONArray(values);
                JSONArray symbols = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    String symbol = array.optJSONObject(i).optString("symbol");
                    symbols.put(symbol);
                }
                return symbols;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
