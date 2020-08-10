package com.yjkj.chainup.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lianshangljl
 * @Date 2020-03-31-12:23
 * @Email buptjinlong@163.com
 * @description
 */
public class KlineDepth {


    /**
     * // 买盘,按价格由大到小排序
     */
    private List<DepthBean> bids = new ArrayList<>();
    /**
     * 卖盘,按价格由小到大排序
     */
    private List<DepthBean> asks = new ArrayList<>();


    public void clear() {
        bids.clear();
        asks.clear();
    }

    public List<DepthBean> getBids() {
        if (bids == null) {
            bids = new ArrayList<>();
        }
        return bids;
    }

    public void setBids(List<DepthBean> bids) {
        this.bids = bids;
    }

    public List<DepthBean> getAsks() {
        if (asks == null) {
            asks = new ArrayList<>();
        }
        return asks;
    }

    public void setAsks(List<DepthBean> asks) {
        this.asks = asks;
    }


    public void fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        if (asks == null) {
            asks = new ArrayList<>();
        }
        asks.clear();
        JSONArray array_sells = jsonObject.optJSONArray("asks");
        try {
            if (array_sells != null) {
                for (int i = 0; i < array_sells.length(); i++) {
                    JSONArray obj = array_sells.getJSONArray(i);
                    if (obj == null) {
                        continue;
                    }

                    DepthBean item = new DepthBean();
                    int count = obj.length();
                    if (count == 2) {
                        for (int k = 0; k < obj.length(); k++) {
                            if (k == 0) {
                                item.setPrice(obj.getString(0));
                            } else if (k == 1) {
                                item.setVol(obj.getString(1));
                            }
                        }
                    }
                    asks.add(item);
                }
            }
        } catch (JSONException ignored) {
        }

        if (bids == null) {
            bids = new ArrayList<>();
        }
        bids.clear();
        JSONArray array_buys = jsonObject.optJSONArray("buys");
        try {
            if (array_buys != null) {
                for (int i = 0; i < array_buys.length(); i++) {
                    JSONArray obj = array_buys.getJSONArray(i);
                    if (obj == null) {
                        continue;
                    }
                    DepthBean item = new DepthBean();
                    int count = obj.length();
                    if (count == 2) {
                        for (int k = 0; k < obj.length(); k++) {
                            if (k == 0) {
                                item.setPrice(obj.getString(0));
                            } else if (k == 1) {
                                item.setVol(obj.getString(1));
                            }
                        }
                    }
                    bids.add(item);
                }
            }
        } catch (JSONException ignored) {
        }
    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray array_buys = new JSONArray();
            for (int i = 0; i < bids.size(); i++) {
                array_buys.put(bids.get(i).toJson());
            }
            jsonObject.put("buys", array_buys);

            JSONArray array_sells = new JSONArray();
            for (int i = 0; i < asks.size(); i++) {
                array_sells.put(asks.get(i).toJson());
            }
            jsonObject.put("asks", array_sells);

        } catch (JSONException ignored) {
        }
        return jsonObject;
    }

    public String toString() {

        JSONObject jsonObj = toJson();
        if (jsonObj == null)
            return "";
        else
            return jsonObj.toString();
    }


    public KlineDepth clone() {
        KlineDepth depth = new KlineDepth();
        List<DepthBean> cloneBids = new ArrayList<>();
        for (DepthBean depthData : bids) {
            cloneBids.add(depthData);
        }
        List<DepthBean> cloneSells = new ArrayList<>();
        for (DepthBean depthData : asks) {
            cloneSells.add(depthData);
        }
        depth.setBids(cloneBids);
        depth.setAsks(cloneSells);
        return depth;
    }

}
