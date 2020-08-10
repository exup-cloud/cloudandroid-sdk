package com.yjkj.chainup.db;

import com.tencent.mmkv.MMKV;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-09 15:56
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-09 15:56
 * @UpdateRemark: 更新说明
 */
public class MMKVDb {

    private MMKV mMMKV;
    public MMKVDb(){
        mMMKV = MMKV.defaultMMKV();
    }

    public void saveData(String key,String value){
        mMMKV.encode(key,value);  //写入缓存
        //mMMKV.putString(key,value);  // 写入SD卡
    }

    public String getData(String key){
        //return mMMKV.getString(key,"");
        return mMMKV.decodeString(key,"");
    }

    public void saveBooleanData(String key,boolean value){
        mMMKV.encode(key,value);
    }

    public void saveIntData(String key,int value){
        mMMKV.encode(key,value);
    }

    public int getIntData(String key,int defValue){
        return mMMKV.getInt(key,defValue);
    }

    public boolean getBooleanData(String key,boolean defValue){
        return mMMKV.getBoolean(key,defValue);
    }

    public void removeValueForKey(String key){
        mMMKV.removeValueForKey(key);
    }

    public void removeValuesForKeys(String[] keys){
        mMMKV.removeValuesForKeys(keys);
    }


    public void clearMemoryCache(){
        mMMKV.clearMemoryCache();
    }

}
