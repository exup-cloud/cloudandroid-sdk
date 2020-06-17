package com.bmtc.sdk.contract.data;



import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zj on 2018/4/8.
 */

public class Collect {
    private String name;
    private long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("name", name);
            jsonObj.put("time", time);
        } catch (JSONException ignored) {
        }
        return jsonObj;
    }

    public void fromJson(JSONObject jsonObject) {

        if(jsonObject == null) return;

        name = jsonObject.optString("name");
        time = jsonObject.optLong("time");
    }

    public String toString(){

        JSONObject jsonObj = toJson();
        if (jsonObj == null)
            return "";
        else
            return jsonObj.toString();
    }
}
