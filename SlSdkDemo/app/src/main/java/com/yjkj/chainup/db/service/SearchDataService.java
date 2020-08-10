package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.manager.NCoinManager;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description:   搜索历史记录数据
 * @Author: wanghao
 * @CreateDate: 2019-08-09 12:26
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-09 12:26
 * @UpdateRemark: 更新说明
 */
public class SearchDataService {

    private static final String searchData = "searchData";

    private MMKVDb mMMKVDb;
    private SearchDataService(){
        mMMKVDb = new MMKVDb();
    }

    private static SearchDataService mLikeDataService;
    public static SearchDataService getInstance(){
        if(null==mLikeDataService){
            mLikeDataService = new SearchDataService();
        }
        return mLikeDataService;
    }

    /*
     * 搜索记录
     */
    public void saveSearchData(String symbol){
        if(!StringUtil.checkStr(symbol))
            return ;

        JSONObject symbolObj = NCoinManager.getSymbolObj(symbol);

        if(null==symbolObj || symbolObj.length()<=0)
            return ;

        JSONArray array = getSearchData();
        if(null==array)
            array = new JSONArray();

        for(int i=0;i<array.length();i++){
            JSONObject jsonObject = array.optJSONObject(i);
            if(jsonObject.optString("symbol").equalsIgnoreCase(symbol)){
                return;
            }
        }
        array.put(symbolObj);
        mMMKVDb.saveData(searchData,array.toString());
    }

    /*
     * 是否存在历史搜索记录
     */
    public boolean hasSearched(String symbol){
        JSONArray array = getSearchData();
        if(null==array || array.length()<=0)
            return false;
        for(int i=0;i<array.length();i++){
            JSONObject jsonObject = array.optJSONObject(i);
            if(jsonObject.optString("symbol").equalsIgnoreCase(symbol)){
                return true;
            }
        }
        return false;
    }

    /*
     * 移除历史记录
     */
    public void removeSearchData(){
        mMMKVDb.removeValueForKey(searchData);
    }

    /*
     * 移除最后一条历史记录
     */
    public void removeLastSearchData(){
        JSONArray array = getSearchData();
        if(null!=array && array.length()>5){
            JSONArray newArray = new JSONArray();
            for(int i=0;i<array.length();i++){
                if(i!=array.length()){
                    JSONObject obj = array.optJSONObject(i);
                    newArray.put(obj);
                    mMMKVDb.saveData(searchData,newArray.toString());
                }
            }
        }
    }


    public JSONArray getSearchData(){
        String values = mMMKVDb.getData(searchData);
        if(StringUtil.checkStr(values)){
            try {
                return new JSONArray(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
