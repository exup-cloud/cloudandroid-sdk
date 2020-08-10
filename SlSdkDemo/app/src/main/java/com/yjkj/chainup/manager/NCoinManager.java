package com.yjkj.chainup.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.net_new.JSONUtil;
import com.yjkj.chainup.util.LogUtil;
import com.yjkj.chainup.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @Description: 币对数据格式化处理
 * @Author: wanghao
 * @CreateDate: 2019-09-09 10:56
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-09-09 10:56
 * @UpdateRemark: 更新说明
 */
public class NCoinManager {

    private static final String TAG = "NCoinManager";

    public static final int defaultPrescion = 4;//默认精度位数

    public static JSONObject getMarketObj() {
        JSONObject market = PublicInfoDataService.getInstance().getMarket(null);
        return market;
    }

    public static JSONArray getMarketSort() {
        JSONArray marketSort = PublicInfoDataService.getInstance().getMarketSort(null);
        return marketSort;
    }

    public static ArrayList<String> getMarketSortList() {
        JSONArray array = getMarketSort();
        return JSONUtil.arrayToStringList(array);
    }

    /*
     * 得到无分组的 所有交易对
     * @param needAllCoin  true返回全部币对数据，否则 只返回isShow=1的数据
     */
    public static JSONArray getMarketArray(boolean needAllCoin) {
        JSONObject market = getMarketObj();
        if (null == market || market.length() <= 0) {
            return null;
        }
        Iterator<String> it = market.keys();

        JSONArray array = new JSONArray();
        while (it.hasNext()) {
            String key = it.next();
            JSONObject value = market.optJSONObject(key);
            Iterator<String> valueIt = value.keys();
            while (valueIt.hasNext()) {
                JSONObject valueObj = value.optJSONObject(valueIt.next());
                if (valueObj != null) {
                    if (needAllCoin) {
                        array.put(valueObj);
                    } else {
                        String isShow = valueObj.optString("isShow", "");
                        if (StringUtil.checkStr(isShow)) {
                            if ("1".equals(isShow)) {
                                array.put(valueObj);
                            }
                        } else {
                            array.put(valueObj);
                        }
                    }
                }

            }
        }
        return array;
    }


    /**
     * 根据 showSymbol 返回 name
     *
     * @param symbol
     * @return
     */
    public static String getMarketName4Symbol(String symbol) {
        if (!StringUtil.checkStr(symbol))
            return "";
        JSONArray jsonArray = getMarketArray(true);
        if (null == jsonArray || jsonArray.length() == 0) {
            return "";
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            if (null != jsonObject && jsonObject.length() > 0) {
                String symbol4Json = jsonObject.optString("showSymbol", "");
                if (symbol.equals(symbol4Json)) {
                    String showName = jsonObject.optString("showName", "");
                    if (StringUtil.checkStr(showName)) {
                        return showName;
                    } else {
                        return jsonObject.optString("name", "");
                    }
                }
            }
        }
        return symbol;
    }

    /**
     * 根据 symbol 返回 JSON
     *
     * @param symbol
     * @return
     */
    public static JSONObject getMarket4Name(String symbol) {
        if (!StringUtil.checkStr(symbol))
            return null;
        JSONArray jsonArray = getMarketArray(true);
        if (null == jsonArray || jsonArray.length() == 0) {
            return null;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            if (null != jsonObject && jsonObject.length() > 0) {
                String symbol4Json = jsonObject.optString("symbol", "");
                if (symbol.equals(symbol4Json)) {
                    return jsonObject;
                }
            }
        }
        return null;
    }


    /*
     *  根据symbol取行情数据
     */
    public static JSONObject getSymbolObj(String symbol) {
        if (!StringUtil.checkStr(symbol)) {
            return null;
        }
        JSONObject market = getMarketObj();
        if (null == market || market.length() <= 0)
            return null;

        Iterator<String> it = market.keys();
        boolean hasFind = false;
        JSONObject symbolObj = null;
        while (it.hasNext() && !hasFind) {
            String key = it.next();
            JSONObject value = market.optJSONObject(key);
            if (value == null) {
                return null;
            }
            Iterator<String> valueKeys = value.keys();
            while (valueKeys.hasNext() && !hasFind) {
                String valueKey = valueKeys.next();
                if (valueKey.contains("/")) {
                    String replace = valueKey.replace("/", "");
                    if (symbol.equalsIgnoreCase(replace)) {
                        hasFind = true;
                        symbolObj = value.optJSONObject(valueKey);
                    }
                }
            }
        }

        return symbolObj;
    }


    /**
     * @return
     */

    public static String getDefaultThresholdForSort(String symbol) {
        if (TextUtils.isEmpty(symbol)) {
            return "0.1";
        }
        JSONObject marketAll = getMarketObj();
        if (null == marketAll || marketAll.length() == 0) {
            return "0.1";
        }
        Iterator<String> it = marketAll.keys();
        boolean hasFind = false;
        String threshold = "0.1";
        while (it.hasNext() && !hasFind) {
            String key = it.next();
            JSONObject value = marketAll.optJSONObject(key);
            Iterator<String> valueKeys = value.keys();
            while (valueKeys.hasNext() && !hasFind) {
                String valueKey = valueKeys.next();
                if (valueKey.contains("/")) {
                    String replace = valueKey.replace("/", "");
                    if (symbol.equalsIgnoreCase(replace)) {
                        hasFind = true;
                        threshold = value.optString("defaultThreshold", "0.1");
                        return threshold;
                    }
                }
            }
        }
        return "0.1";
    }


    /*
     * 展示别名
     */
    public static @NonNull
    String showAnoterName(@Nullable JSONObject jsonObject) {
        if (null == jsonObject)
            return "";
        String name = jsonObject.optString("name", "");
        String showName = jsonObject.optString("showName");
        if (StringUtil.checkStr(showName))
            return showName;
        return name;
    }

    /*
     * 展示别名
     */
    public static @NonNull
    String showAnoterName(@Nullable String name, @Nullable String showName) {
        if (StringUtil.checkStr(showName))
            return showName;
        return name != null ? name : "";
    }

    /*
     *
     * 根据marketName 得到对应分组数据
     */
    public synchronized static ArrayList<JSONObject> getMarketByName(String marketName) {
        if (!StringUtil.checkStr(marketName))
            return new ArrayList<JSONObject>();

        JSONObject jsonObject = getMarketObj();

        if (null == jsonObject)
            return new ArrayList<JSONObject>();
        jsonObject = jsonObject.optJSONObject(marketName);


        if (null == jsonObject) {
            return new ArrayList<JSONObject>();
        }

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            JSONObject jsonObj = jsonObject.optJSONObject(keys.next());


            if (null != jsonObj && jsonObj.length() > 0) {
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

                String isShow = jsonObj.optString("isShow");

                if (getIsOverCharge(jsonObj.optString("name"))) {
                    try {
                        jsonObj.put("newcoinFlag", 4);
                        if (StringUtil.checkStr(isShow)) {
                            if ("1".equalsIgnoreCase(isShow)) {
                                list.add(jsonObj);
                            }
                        } else {
                            list.add(jsonObj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (StringUtil.checkStr(isShow)) {
                        if ("1".equalsIgnoreCase(isShow)) {
                            list.add(jsonObj);
                        }
                    } else {
                        list.add(jsonObj);
                    }
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

        return list;
    }


    public static boolean getIsOverCharge(String name) {
        boolean isOverstatus = false;
        String coinName = getMarketShowCoinName(name);
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        JSONObject bean = jsonObject.optJSONObject(coinName);
        if (null != bean && bean.optInt("isOvercharge") == 1) {
            isOverstatus = true;
        }
        return isOverstatus;
    }


    /*
     *根据币名找symbol
     */
    public static String getSymbol(String coinName) {
        if (!StringUtil.checkStr(coinName))
            return "";
        JSONObject market = getMarketObj();
        if (null == market || market.length() <= 0)
            return coinName;

        JSONArray marketSort = getMarketSort();
        if (null == marketSort || marketSort.length() <= 0) {
            return coinName;
        }

        for (int i = 0; i < marketSort.length(); i++) {
            String marketName = marketSort.optString(i);
            JSONObject obj = market.optJSONObject(marketName);
            if (null != obj && obj.length() > 0) {
                String coin = coinName.toUpperCase() + "/" + marketName;
                JSONObject symbolObj = obj.optJSONObject(coin);
                if (null != symbolObj) {
                    String symbol = symbolObj.optString("symbol");
                    return symbol;
                }
            }
        }

        return coinName;
    }

    /*
     *  交易对匹配 ，获取name 值
     */
    public static synchronized ArrayList<JSONObject> getSymbols(JSONArray topSymbol) {


        if (null == topSymbol || topSymbol.length() <= 0)
            return null;

        JSONArray marketArray = getMarketArray(false);

        if (null == marketArray || marketArray.length() <= 0)
            return null;

        ArrayList<JSONObject> selectTopSymbol = new ArrayList<JSONObject>();


        for (int i = 0; i < topSymbol.length(); i++) {
            JSONObject topSymbolObj = topSymbol.optJSONObject(i);
            String topSymbolObjName = topSymbolObj.optString("symbol");

            for (int j = 0; j < marketArray.length(); j++) {
                JSONObject marketObj = marketArray.optJSONObject(j);

                String symbol = marketObj.optString("symbol");

                if (null != symbol) {
                    if (symbol.equalsIgnoreCase(topSymbolObjName)) {
                        try {
                            topSymbolObj.put("name", marketObj.optString("name"));
                            topSymbolObj.put("showName", showAnoterName(marketObj));
                            selectTopSymbol.add(topSymbolObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return selectTopSymbol;
    }

    /*
     * 搜索本地币对数据
     */
    public static ArrayList<JSONObject> getSearchData(ArrayList<JSONObject> symbols, String keyWords) {
        if (!StringUtil.checkStr(keyWords) || null == symbols || symbols.size() <= 0)
            return null;

        ArrayList<JSONObject> searchData = new ArrayList<JSONObject>();
        for (int i = 0; i < symbols.size(); i++) {
            JSONObject jsonObject = symbols.get(i);
            String name = jsonObject.optString("name");
            if (null != name && name.contains("/")) {
                String[] split = name.split("/");
                if (split[0].contains(keyWords.toUpperCase())) {
                    searchData.add(jsonObject);
                }
            }
        }
        return searchData;
    }


    /*
     * 根据币对分组名找对应别名展示
     */
    public static String getCoinShowTitle(String marketSort) {
        if (!StringUtil.checkStr(marketSort)) {
            return null;
        }

        JSONObject jsonObject = PublicInfoDataService.getInstance().getMarket(null);

        if (null == jsonObject || jsonObject.length() <= 0)
            return marketSort;
        JSONObject data = jsonObject.optJSONObject(marketSort);
        if (null != data && data.length() > 0) {
            Iterator<String> it = data.keys();

            boolean hasFind = false;
            while (it.hasNext() && !hasFind) {
                JSONObject value = data.optJSONObject(it.next());
                if (null != value && value.length() > 0) {
                    String name = showAnoterName(value);
                    if (null != name && name.contains("/")) {
                        hasFind = true;
                        String aa = name.split("/")[1];
                        return aa;
                    }
                }
            }
        }
        return marketSort;
    }

    /**
     * @return 返回开启OTC的币种名称
     */
    public static ArrayList<String> getMarkets4OTC() {
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {

            ArrayList<String> coinList = new ArrayList<String>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String otcOpen = value.optString("otcOpen", "0");
                    String name = value.optString("name", "");
                    if (otcOpen != null && "1".equalsIgnoreCase(otcOpen)) {
                        coinList.add(name);
                    }
                }
            }
            return coinList;
        }
        return null;
    }

    /**
     * @return 返回开启OTC的币种名称
     */
    public static ArrayList<String> getMarketsShowName4OTC() {
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {

            ArrayList<String> coinList = new ArrayList<String>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String otcOpen = value.optString("otcOpen", "0");
                    String name = value.optString("showName", "");
                    if (otcOpen != null && "1".equalsIgnoreCase(otcOpen)) {
                        coinList.add(name);
                    }
                }
            }
            return coinList;
        }
        return null;
    }


    /**
     * 获取showName
     */
    public static String getShowMarket(String name) {
        if (!StringUtil.checkStr(name))
            return "";
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String name4Data = value.optString("name", "");
                    if (name != null && name.equals(name4Data)) {
                        return value.optString("showName", "");
                    }
                }
            }
        }
        return name;
    }

    /**
     * 处理此处主要是因为历史委托筛选用户填币对搜索，本身用户填的是showName需要后台自己处理但是ios自己处理了，所以本地才做这个。。。。。。
     */
    public static String setShowNameGetName(String name) {
        if (!StringUtil.checkStr(name))
            return name;
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String name4Data = value.optString("showName", "");
                    if (name != null && name.equalsIgnoreCase(name4Data)) {
                        return value.optString("name", "");
                    }
                }
            }
        }
        return name;
    }


    /**
     * 获取数据的coinTag
     *
     * @param coinName
     * @return
     */
    public static String getCoinTag4CoinName(String coinName) {
        if (!StringUtil.checkStr(coinName))
            return "";
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        String coinTag = "";
        if (null != jsonObject && jsonObject.length() > 0) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String name4Data = value.optString("name", "");
                    if (coinName != null && coinName.equals(name4Data)) {
                        return value.optString("coinTag", "");
                    }
                }
            }
        }
        return coinTag;
    }


    /**
     * 获取showName
     */
    public static String getName4Symbol(String name) {
        String showName = name;
        JSONObject jsonObject = PublicInfoDataService.getInstance().getMarket(null);
        if (null == jsonObject || jsonObject.length() <= 0)
            return showName;
        Iterator<String> its = jsonObject.keys();
        while (its.hasNext()) {
            JSONObject data = jsonObject.optJSONObject(its.next());
            JSONObject object = data.optJSONObject(name);
            if (null != object && object.length() > 0) {
                String name4Data = object.optString("symbol");
                if (StringUtil.checkStr(name4Data)) {
                    return name4Data;
                } else {
                    return showName;
                }
            }
        }
        return showName;
    }

    /**
     * 获取name
     */
    public static String getNameForSymbol(String symbol) {
        JSONObject jsonObject = PublicInfoDataService.getInstance().getMarket(null);
        if (null == jsonObject || jsonObject.length() <= 0)
            return "";
        Iterator<String> its = jsonObject.keys();
        while (its.hasNext()) {
            JSONObject data = jsonObject.optJSONObject(its.next());
            Iterator<String> itsMarket = data.keys();
            while (itsMarket.hasNext()) {
                JSONObject dataMarket = data.optJSONObject(itsMarket.next());
                if (symbol.equals(dataMarket.optString("symbol"))) {
                    return dataMarket.optString("name");
                }
            }
        }
        return "";
    }


    /**
     * @return 返回开启OTC的币种数据
     * otcOpen==1
     */
    public static ArrayList<JSONObject> getCoins4OTC() {
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {

            ArrayList<JSONObject> coinList = new ArrayList<JSONObject>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String otcOpen = value.optString("otcOpen", "0");
                    if (otcOpen != null && "1".equalsIgnoreCase(otcOpen)) {
                        coinList.add(value);
                    }
                }
            }
            return coinList;
        }
        return null;
    }


    /*
     * 返回精度值
     */
    public static int getCoinShowPrecision(String coinName) {
        JSONObject jsonObject = getCoinObj(coinName);
        if (null != jsonObject) {
            return jsonObject.optInt("showPrecision");
        }
        return defaultPrescion;
    }

    /*
     * 根据coinName 返回 coinList 中的一个JSONObject
     */
    public static JSONObject getCoinObj(String coinName) {
        if (!StringUtil.checkStr(coinName)) {
            return null;
        }

        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject) {
            return jsonObject.optJSONObject(coinName.toUpperCase());
        }
        return null;
    }

    /*
     * 根据coinName判断是否优先存在于market里，并返回对应symbol
     * todo  测试
     */
    public static String isExistMarket(String coinName) {
        if (!StringUtil.checkStr(coinName))
            return "";

        JSONObject market = getMarketObj();
        if (null == market || market.length() <= 0) {
            return "";
        }

        Iterator<String> keys = market.keys();
        boolean hasFind = false;
        while (keys.hasNext() && !hasFind) {
            String key = keys.next();
            JSONObject value = market.optJSONObject(key);

            Iterator<String> keys2 = value.keys();
            while (keys2.hasNext() && !hasFind) {
                String coinMarketName = keys2.next();
                JSONObject obj = value.optJSONObject(coinMarketName);
                if (null != obj && obj.length() > 0) {
                    hasFind = true;
                    return obj.optString("symbol");
                }
            }
        }
        return "";
    }

    /**
     * 判断币对是否存在
     *
     * @param exchangeSymbol
     * @return
     */
    public static String returnExistMarket(String exchangeSymbol) {
        if (!StringUtil.checkStr(exchangeSymbol))
            return "";

        JSONObject market = getMarketObj();
        if (null == market || market.length() <= 0) {
            return "";
        }
        Iterator<String> keys = market.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject value = market.optJSONObject(key);

            Iterator<String> keys2 = value.keys();
            while (keys2.hasNext()) {
                String coinMarketName = keys2.next();
                if (coinMarketName.equals(exchangeSymbol)) {
                    JSONObject obj = value.optJSONObject(coinMarketName);
                    if (null != obj && obj.length() > 0) {
                        return obj.optString("symbol");
                    }
                }
            }
        }
        return "";
    }

    /*
     *  重写原DataManager方法
     */
    public static Pair<String, String> getShowName(String market, String second) {
        JSONArray array = getMarketArray(true);
        if (null != array && array.length() > 0) {
            String coin_market = market + "/" + second;
            for (int i = 0; i < array.length(); i++) {
                String name = array.optJSONObject(i).optString("name");
                if (coin_market.equals(name)) {
                    String showName = array.optJSONObject(i).optString("showName");
                    if (StringUtil.checkStr(showName) && showName.contains("/")) {
                        String[] split = showName.split("/");
                        Pair<String, String> p = new Pair<String, String>(split[0], split[1]);
                        return p;
                    }

                }
            }
        }
        return new Pair<String, String>(market, second);
    }


    /*
     *  根据market 获取coinName
     */
    public static String getMarketCoinName(String name) {
        if (StringUtil.checkStr(name) && name.contains("/")) {
            return name.split("/")[0];
        }
        return "";
    }

    /*
     *  根据market得到marketName
     */
    public static String getMarketName(String name) {
        if (StringUtil.checkStr(name) && name.contains("/")) {
            return name.split("/")[1];
        }
        return "";
    }

    /*
     * 根据showName 显示币对CoinName的别名
     */
    public static String getMarketShowCoinName(String showName) {
        if (StringUtil.checkStr(showName) && showName.contains("/")) {
            return showName.split("/")[0];
        }
        return "";
    }


    /**
     * @return
     */

    public static int getMarketForSort(String market) {
        JSONObject marketAll = getMarketObj();
        if (null == marketAll || marketAll.length() == 0) {
            return 0;
        }
        String coin = getMarketName(market);
        JSONObject json = marketAll.optJSONObject(coin);
        if (null != json && json.length() > 0) {
            JSONObject marketJson = json.optJSONObject(market);
            if (null != marketJson && marketJson.length() > 0) {
                int symbolInt = marketJson.optInt("sort", 0);
                return symbolInt;
            }
        }
        return 0;
    }


    /*
     * 根据symbol 显示币对CoinName的别名
     */
    public static String getMarketShowCoinName2(String symbol) {
        String showName = showAnoterName(getSymbolObj(symbol));
        if (StringUtil.checkStr(showName) && showName.contains("/")) {
            return showName.split("/")[0];
        }
        return symbol;

    }

    /**
     * 获取showName
     */
    public static String getShowMarketName(String name) {
        String showName = name;
        JSONObject jsonObject = PublicInfoDataService.getInstance().getMarket(null);
        if (null == jsonObject || jsonObject.length() <= 0)
            return showName;
        Iterator<String> its = jsonObject.keys();
        while (its.hasNext()) {
            JSONObject data = jsonObject.optJSONObject(its.next());
            JSONObject object = data.optJSONObject(name);
            if (null != object && object.length() > 0) {
                String name4Data = object.optString("showName");
                if (StringUtil.checkStr(name4Data)) {
                    return name4Data;
                } else {
                    return showName;
                }
            }
        }
        return showName;
    }

    public static ArrayList<JSONObject> getLeverMapList(JSONObject data) {
        if (null != data) {
            Iterator<String> keys = data.keys();
            ArrayList<JSONObject> arrayList = new ArrayList<>();
            try {
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject volume = data.optJSONObject(key);
                    if (null != volume && volume.length() > 0) {
                        volume.put("sort", getMarketForSort(volume.optString("name")));
                        arrayList.add(volume);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }
        return new ArrayList<JSONObject>();
    }


    /**
     * 根据数据获取key
     */
    public static ArrayList<String> getKeyList(JSONObject jsonObject) {
        if (null != jsonObject) {
            return new ArrayList<String>();
        }
        Iterator<String> keys = jsonObject.keys();
        ArrayList<String> arrayList = new ArrayList<>();

        while (keys.hasNext()) {
            String key = keys.next();
            arrayList.add(key);
        }
        return arrayList;
    }

    /*
     * 获取杠杆侧边栏的币对分组数据
     *  @param 为空则返回isOpenLever=1的所有数据
     */
    public static ArrayList<JSONObject> getLeverGroupList(String market) {
        JSONObject marketObj = getMarketObj();
        if (null == marketObj || marketObj.length() <= 0)
            return null;

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        Iterator<String> keys = marketObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject value = marketObj.optJSONObject(key);
            if (null != value) {
                Iterator<String> keys2 = value.keys();
                while (keys2.hasNext()) {
                    String key2 = keys2.next();
                    JSONObject value2 = value.optJSONObject(key2);
                    if (null != value2) {
                        String isOpenLever = value2.optString("isOpenLever");
                        if ("1".equals(isOpenLever)) {
                            if (StringUtil.checkStr(market)) {
                                String name = showAnoterName(value2);
                                if (null != name && name.contains("/")) {
                                    if (market.equals(name.split("/")[1])) {
                                        list.add(value2);
                                    }
                                }
                            } else {
                                list.add(value2);
                            }
                        }
                    }
                }
            }
        }
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    return o1.optInt("newcoinFlag") - o2.optInt("newcoinFlag");
                }
            });
        }
        return list;
    }

    /*
     * 获取杠杆侧边栏的币对分组title
     */
    public static ArrayList<String> getLeverGroup() {
        ArrayList<JSONObject> list = getLeverGroupList(null);
        if (null != list && list.size() > 0) {
            ArrayList<String> titles = new ArrayList<String>();
            String lastMarketName = "";
            for (int i = 0; i < list.size(); i++) {
                JSONObject value = list.get(i);
                if (null != value) {
                    String name = showAnoterName(value);
                    String marketName = getMarketName(name);
                    if (!lastMarketName.equals(marketName)) {
                        lastMarketName = marketName;
                        if (!titles.contains(marketName)) {
                            titles.add(marketName);
                        }
                    }
                }
            }
            return titles;
        }
        return null;
    }

    public static String setsymbolNameGetShowName(String name) {
        if (!StringUtil.checkStr(name))
            return name;
        JSONObject jsonObject = PublicInfoDataService.getInstance().getCoinList(null);
        if (null != jsonObject && jsonObject.length() > 0) {
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject value = jsonObject.optJSONObject(key);
                if (null != value && value.length() > 0) {
                    String name4Data = value.optString("showName", "");
                    String nameGet = value.optString("name", "");
                    if (name != null && (name.equalsIgnoreCase(name4Data) || name.equalsIgnoreCase(nameGet))) {
                        return name4Data;
                    }
                }
            }
        }
        return name;
    }


}
