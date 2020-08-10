package com.yjkj.chainup.net_new;

import com.yjkj.chainup.db.service.UserDataService;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.RequestBody;

public class HttpParams {

    private TreeMap<String,String> map;

    /**
     * 有参数时，如需定义长度使用
     * @param mapLength int
     * @return this
     */
    public static HttpParams getInstance(int mapLength){
        return new HttpParams(mapLength);
    }

    private HttpParams(int mapLength ){
        map = new TreeMap<String,String>();
        map.put("time", String.valueOf(System.currentTimeMillis()));
    }

    public <T>HttpParams put(String key,T o){
        map.put(key,o == null ? "" : o.toString());
        return this;
    }

    public TreeMap<String,String> build(){
        return map;
    }

}
