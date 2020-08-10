package com.yjkj.chainup.db.service;


import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.bean.coin.CoinMapBean;
import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.db.constant.ParamConstant;
import com.yjkj.chainup.manager.DataManager;
import com.yjkj.chainup.net_new.JSONUtil;
import com.yjkj.chainup.util.DecimalUtil;
import com.yjkj.chainup.util.StringUtil;
import com.yjkj.chainup.util.SystemUtils;
import com.yjkj.chainup.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-09 12:26
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-09 12:26
 * @UpdateRemark: 更新说明
 */
public class PublicInfoDataService {

    private static final String TAG = "PublicInfoDataService";
    private static final String key = "publicInfoV4";
    private static final String REQ_REVIEW = "req_review";
    private static final String CUR_COIN_MAP = "current_coin_map";
    private static final String CUR_COIN_MAP_LEVER = "current_coin_map_lever";
    private static final String SHOW_ASSET_VIEW = "show_asset_view";
    private static final String DEPTH_TYPE = "depth_type";
    public static final String LEVEL_DEPTH_TYPE = "level_depth_type";

    public static final String CHANGE_HOST = "change_host";


    public static final String ONLINE_STRING_TEXT = "online_string_text";

    private static final String HOMEPAGE_SHOW_DIALOG_STATUS = "homepage_show_dialog_status";
    private static final String LEVER_SHOW_DIALOG_STATUS = "lever_show_dialog_status";
    private static final String ETF_STATE_DIALOG_STATUS = "etf_state_dialog_status";

    private static final String BIND_CLIENT_ID = "client_id";

    private static JSONObject cachObj;
    /**
     * app的显示模式
     */
    private static final String SHOW_THEME_MODE = "theme_mode";

    /**
     * 保存 是否cet
     */
    private static final String SAVE_CET_DATA = "save_cet_data";

    /**
     * 0 - 白天模式
     */
    public static final int THEME_MODE_DAYTIME = 0;
    /**
     * 1 - 夜间模式
     */
    public static final int THEME_MODE_NIGHT = 1;


    private MMKVDb mMMKVDb;

    private PublicInfoDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static PublicInfoDataService mPublicInfoDataService;

    public static PublicInfoDataService getInstance() {
        if (null == mPublicInfoDataService) {
            mPublicInfoDataService = new PublicInfoDataService();
        }
        return mPublicInfoDataService;
    }


    public void saveData(JSONObject data) {
        if (null != data) {
            cachObj = data;
            mMMKVDb.saveData(key, data.toString());
        }


    }


    public void saveCetData(String data) {
        if (null != data) {
            mMMKVDb.saveData(SAVE_CET_DATA, data);
        }
    }

    public JSONObject getCetData() {
        JSONObject data = new JSONObject();
        String dataStr = mMMKVDb.getData(SAVE_CET_DATA);
        if (StringUtil.checkStr(dataStr)) {
            dataStr = dataStr.replace("\n", "");
            dataStr = dataStr.replace(" ", "");
            try {
                data = new JSONObject(dataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 获取域名
     *
     * @return
     */
    public String getDoMain() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return "";
        }
        return json.optString("saas_domain", "");
    }

    public String getTextDoMain() {
        JSONObject jsonObject = getCetData();
        if (null == jsonObject || jsonObject.length() == 0) {
            return "";
        }
        return jsonObject.optString("test_list", "");
    }

    /**
     * 获取是否使用本地cet
     *
     * @return
     */
    public String getLinks() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return "";
        }
        return json.optString("links", "");
    }


    /**
     * 获取是否使用本地cet
     *
     * @return
     */
    public boolean getAndroidOnline() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return false;
        }
        return json.optBoolean("android_on", false);
    }


    /**
     * 返回首页广告位新的
     */
    public JSONObject getCustomConfig(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("custom_config");
        }
        return null;
    }

    /**
     * 获取cet下载地址
     *
     * @return
     */
    public String getCetUrl() {
        return "https://chainup-ui.oss-cn-beijing.aliyuncs.com/ioscer.cer";
    }

    /**
     * 获取使用哪个cet
     *
     * @return
     */
    public String getCet() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return "";
        }
        return json.optString("saas_cer_fileName", "");
    }

    /**
     * 获取特殊列表
     *
     * @return
     */
    public ArrayList<JSONObject> getSpecialList() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return null;
        }
        JSONArray jsonArray = json.optJSONArray("special_list");
        if (null == jsonArray || jsonArray.length() == 0) {
            return null;
        }
        return JSONUtil.arrayToList(jsonArray);
    }

    /**
     * 获取特殊列表
     *
     * @return
     */
    public ArrayList<JSONObject> getTextList() {
        JSONObject json = getCetData();
        if (null == json || json.length() == 0) {
            return null;
        }
        JSONArray jsonArray = json.optJSONArray("test_list");
        if (null == jsonArray || jsonArray.length() == 0) {
            return null;
        }
        return JSONUtil.arrayToList(jsonArray);
    }


    public String getCerName() {
        ArrayList<JSONObject> textList = getTextList();
        for (JSONObject json : textList) {
            if (null != json && json.length() > 0) {
                if (json.optString("host").equals(Utils.getAPIHostInsideString(ChainUpApp.appContext.getApplicationContext().getString(R.string.baseUrl)))) {
                    return json.optString("saas_cer_fileName");
                }
            }
        }
        return "";
    }

    public String getTestDomain() {
        ArrayList<JSONObject> textList = getTextList();
        if (null == textList) return "";
        for (JSONObject json : textList) {
            if (null != json && json.length() > 0) {
                if (json.optString("host").equals(Utils.getAPIHostInsideString(ChainUpApp.appContext.getApplicationContext().getString(R.string.baseUrl)))) {
                    return json.optString("saas_domain");
                }
            }
        }
        return "";
    }


    /*
     * return 最外层的data对象
     */
    public JSONObject getData(JSONObject data) {
        if (null == data) {
            if (null != cachObj && cachObj.length() > 0)
                return cachObj;
            String dataStr = mMMKVDb.getData(key);
            if (StringUtil.checkStr(dataStr)) {
                try {
                    return cachObj = new JSONObject(dataStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            cachObj = data;
        }
        return data;
    }


    /**
     * 存储网络数据数据
     *
     * @param
     * @return
     */
    public void saveOnlineText(String data) {
        if (null != data) {
            mMMKVDb.saveData(ONLINE_STRING_TEXT, data);
        }
    }

    /**
     * 获取网络数据
     *
     * @param
     * @return
     */
    public JSONObject getOnlineText() {
        String onlineText = mMMKVDb.getData(ONLINE_STRING_TEXT);
        if (StringUtil.checkStr(onlineText)) {
            try {
                return new JSONObject(onlineText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /*
     * 传最外层的data对象C
     * return coinList
     */
    public @Nullable
    JSONObject getCoinList(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("coinList");
        }
        return null;
    }

    /**
     * 从币列表
     *
     * @param data
     * @return
     */
    public JSONObject getFollowCoinList(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("followCoinList");
        }
        return null;
    }

    public JSONObject getAppPersonalIcon(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("app_personal_icon");
        }
        return new JSONObject();
    }

    /**
     * 根据${name}查找对应{name}下的从币对象
     *
     * @param name 主币名称
     * @return
     */
    public JSONObject getFollowCoinJSONObjectByMainCoinName(String name) {
        JSONObject json = getFollowCoinList(getData(null));
        if (null != json) {
            return json.optJSONObject(name);
        }
        return new JSONObject();
    }

    /**
     * 根据主链币返回其对应的从币列表
     *
     * @param name
     * @return
     */
    public ArrayList<JSONObject> getFollowCoinsByMainCoinName(String name) {
        JSONObject followCoinListJSONObject = getFollowCoinJSONObjectByMainCoinName(name);
        ArrayList<JSONObject> objs = new ArrayList<>();
        if (followCoinListJSONObject != null) {
            Iterator<String> keys = followCoinListJSONObject.keys();
            Log.d(TAG, "=====PUBLICE:=====" + keys);
            while (keys.hasNext()) {
                objs.add(followCoinListJSONObject.optJSONObject(keys.next()));
            }
        }
        return DecimalUtil.sortByMultiOptions(objs, "sort", "mainChainName", false);

    }


    public JSONObject getCoinByName(String name) {
        JSONObject json = getCoinList(getData(null));
        if (null != json) {
            return json.optJSONObject(name);
        }
        return new JSONObject();
    }


    /*
     * emailOptCode
     */
    public @Nullable
    JSONObject getEmailOptCode(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("emailOptCode");
        }
        return null;
    }

    /*
     * smsOptCode
     */
    public @Nullable
    JSONObject getSmsOptCode(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("smsOptCode");
        }
        return null;
    }

    /*
     *  klineColor
     */
    public @Nullable
    JSONObject getKlineColor(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("klineColor");
        }
        return null;
    }

    /*
     *  rate
     */
    public @Nullable
    JSONObject getRate(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("rate");
        }
        return null;
    }


    /*
     *lan
     */
    public @Nullable
    JSONObject getLan(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("lan");
        }
        return null;
    }

    public ArrayList<JSONObject> getLanList() {
        JSONObject jsonObject = getLan(null);

        if (null == jsonObject)
            return new ArrayList<JSONObject>();

        JSONArray jsonArray = jsonObject.optJSONArray("lanList");

        if (null == jsonArray) {
            return new ArrayList<JSONObject>();
        }
        ArrayList<JSONObject> lanlist = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            lanlist.add(jsonArray.optJSONObject(i));
        }
        return lanlist;
    }

    /*
     * klineScale
     */
    public @Nullable
    JSONArray getKlineScale(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONArray("klineScale");
        }
        return null;
    }

    /*
     *  marketSort
     */
    public @Nullable
    JSONArray getMarketSort(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONArray("marketSort");
        }
        return null;
    }

    public ArrayList<String> getMarketSortList(@Nullable JSONObject data) {
        data = getData(data);
        JSONArray sortList = new JSONArray();
        ArrayList<String> marketSortList = new ArrayList<>();
        if (null != data) {
            sortList = data.optJSONArray("marketSort");
        }
        if (null == sortList) {
            return marketSortList;
        }
        if (sortList.length() > 0) {
            for (int i = 0; i < sortList.length(); i++) {
                marketSortList.add(sortList.optString(i));
            }
        }
        return marketSortList;
    }

    public @Nullable
    JSONObject getSafeWithdraw(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("update_safe_withdraw");
        }
        return null;
    }

    /*
     *  market
     */
    public @Nullable
    JSONObject getMarket(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("market");
        }
        return null;
    }

    /*
     * 按组取币对
     *
     *  @param isShow true 返回 isShow对应的币对
     */
    public @Nullable
    ArrayList<JSONObject> getSymbols(@Nullable String marketName) {
        if (null == marketName)
            return null;
        JSONObject market = getMarket(null);
        if (null != market && market.length() > 0) {
            ArrayList<JSONObject> list = JSONUtil.jsonObjtoList(market.optJSONObject(marketName));
            ArrayList<JSONObject> newList = new ArrayList<JSONObject>();
            if (null == list) {
                return newList;
            }
            for (JSONObject it : list) {
                try {
                    it.put("isFirst", false);
                    newList.add(it);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return newList;
        }
        return null;
    }

    /*
     * app_logo_list
     */
    public @Nullable
    JSONObject getApp_logo_list(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("app_logo_list");
        }
        return null;
    }

    /*
     * app_logo_list_new
     *
     * return string[0]=logo_black
     */
    public @Nullable
    String[] getApp_logo_list_new(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            data = data.optJSONObject("app_logo_list_new");
            if (null != data) {
                String logo_black = data.optString("logo_black", "");
                String logo_white = data.optString("logo_white", "");
                return new String[]{logo_black, logo_white};
            }
        }
        return null;
    }

    /**
     * 是否开启币种简介
     */
    public boolean isSymbolProfile(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("symbol_profile"));
        return false;
    }

    /**
     * coinsymbol_introduce_names
     * 币种简介
     */
    public ArrayList<String> getCoinsymbolIntroduceNames(@Nullable JSONObject data) {
        ArrayList<String> list = new ArrayList<>();
        data = getData(data);
        if (null != data) {
            JSONArray introduceNames = data.optJSONArray("coinsymbol_introduce_names");
            if (introduceNames != null && introduceNames.length() > 0) {
                for (int i = 0; i < introduceNames.length(); i++) {
                    list.add(introduceNames.optString(i));
                }
            }
        }
        return list;
    }


    /*
     * kline_background_logo_img
     */
    public String getKline_background_logo_img(JSONObject data, Boolean isDaytime) {
        data = getData(data);
        if (null != data) {
            JSONObject jsonObject = data.optJSONObject("kline_background_logo_img");
            if (jsonObject != null) {
                if (isDaytime) {
                    return jsonObject.optString("app_img", "");
                } else {
                    return jsonObject.optString("app_img_night", "");
                }
            }
        }
        return "";
    }


    /**
     * 获取logo
     */
    public String getLogo4Service(Boolean isDaytime) {
        if (isDaytime) {
            return "https://saas-oss.oss-cn-hongkong.aliyuncs.com/upload/20200225201410334.png";
        } else {
            return "https://saas-oss.oss-cn-hongkong.aliyuncs.com/upload/20200225201425685.png";
        }
    }

    /**
     * 获取是否有场外
     */
    public boolean otcOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("otcOpen"));
        return false;
    }

    /**
     * 交易限制是否开启
     */
    public boolean isHasTradeLimitOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("has_trade_limit_open"));
        return false;
    }

    /**
     * 在线客服
     */
    public String getOnlineService(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("online_service_url");
        return "";
    }

    /**
     * 使用哪种方法上传图片
     */
    public String getUploadImgType(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("app_upload_img_type");
        return "";
    }

    /**
     * 配置的国际码
     */
    public String getDefaultCountryCodeReal(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("default_country_code_real");
        return "";
    }


    /**
     * 获取 需要屏蔽的国家
     *
     * @param data
     * @return
     */
    public ArrayList<String> getLimitCountryList(@Nullable JSONObject data) {
        data = getData(data);
        ArrayList<String> limitCountry = new ArrayList<>();
        if (null != data) {
            JSONArray jsonArray = data.optJSONArray("limitCountryList");
            if (null != jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    limitCountry.add(jsonArray.optString(i));
                }
            }
        }
        return limitCountry;
    }

    /**
     * 获取默认国家
     */
    public String getDefaultCountryCode(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optString("default_country_code");
        }
        return "";
    }

    /**
     * 获取合约经纪人说明
     */
    public String getAgentUrl(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optString("co_agent_noticeUrl");
        }
        return "";
    }


    /**
     * 获取B2C开关
     */
    public boolean getB2CSwitchOpen(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optString("fiat_trade_open", "") == "1";
        }
        return true;
    }

    /**
     * 获取注册短信
     */
    public String getUserRegType(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optString("user_reg_type", "");
        }
        return "";
    }


    /**
     * 获取是否开启极验
     * 0 - 无
     * 1 - 阿里(APP暂无)
     * 2 - 极验
     */
    public int getVerifyType(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optInt("verificationType");
        return 0;
    }

    /**
     * 获取开启第三方身份校验
     */
    public boolean isInterfaceSwitchOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("interfaceSwitch"));
        return false;
    }


    /**
     * 获取是否有合约
     */
    public boolean contractOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("contractOpen"));
        return false;
    }

    /**
     * 获取是否有合约赠金
     */
    public boolean contractCouponOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data){
            JSONObject  coCouponSwitchObject =  data.optJSONObject("coCouponSwitch");
            if(coCouponSwitchObject!=null){
                return "1".equals(coCouponSwitchObject.optString("status"));
            }
        }
        return false;
    }
    /**
     * 获取合约赠金配置
     */
    public String getContractCouponUrl(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data){
            JSONObject  coCouponSwitchObject =  data.optJSONObject("coCouponSwitch");
            if(coCouponSwitchObject!=null){
                return  coCouponSwitchObject.optString("url");
            }
        }
        return "";
    }

    /**
     * 获取红包开关
     */
    public boolean isRedPacketOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("red_packet_open"));
        return false;
    }

    public boolean isEnforceGoogleAuth(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("is_enforce_google_auth"));
        return false;
    }

    public boolean appIndexAssetsOpen(JSONObject data) {
        data = getData(data);
        if (null != data)
            return "0".equals(data.optString("appIndex_assets_open"));
        return false;
    }


    /**
     * 获取 kyc配置
     * <p>
     * 限制各个模块开关枚举
     *
     * @param data
     * @return
     */
    public String getkycLimitConfig(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optString("kycLimitConfig", "");
        }
        return null;
    }

    /**
     * 币币提现开关
     *
     * @return
     */
    public Boolean getWithdrawKycOpen() {
        String keyLimit = getkycLimitConfig(null);
        if (null == keyLimit || keyLimit.length() <= 0) return false;
        try {
            JSONObject json = new JSONObject(keyLimit);
            if (json != null) {
                return "1".equals(json.optString("withdraw_kyc_open"));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 币币交易开关
     *
     * @return
     */
    public Boolean getExchangeTradeKycOpen() {
        String keyLimit = getkycLimitConfig(null);
        if (null == keyLimit || keyLimit.length() <= 0) return false;
        try {
            JSONObject json = new JSONObject(keyLimit);
            if (json != null) {
                return "1".equals(json.optString("exchange_trade_kyc_open"));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 杠杆交易开关
     *
     * @return
     */
    public Boolean getLeverTradeKycOpen() {
        String keyLimit = getkycLimitConfig(null);
        if (null == keyLimit || keyLimit.length() <= 0) return false;
        try {
            JSONObject json = new JSONObject(keyLimit);
            if (json != null) {
                return "1".equals(json.optString("lever_trade_kyc_open"));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 币币充值开关
     *
     * @return
     */
    public Boolean getDepositeKycOpen() {
        String keyLimit = getkycLimitConfig(null);
        if (null == keyLimit || keyLimit.length() <= 0) return false;
        try {
            JSONObject json = new JSONObject(keyLimit);
            if (json != null) {
                return "1".equals(json.optString("deposite_kyc_open"));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 合约划转
     *
     * @return
     */
    public Boolean getContractTransferKycOpen() {
        String keyLimit = getkycLimitConfig(null);
        if (null == keyLimit || keyLimit.length() <= 0) return false;
        try {
            JSONObject json = new JSONObject(keyLimit);
            if (json != null) {
                return "1".equals(json.optString("contract_transfer_kyc_open"));
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 自选币对开关
     */
    public boolean isOptionalSymbolServerOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("optional_symbol_server_open"));
        return false;
    }


    public void setCurrentCoinMapName(@Nullable String coinMap) {
        if (!TextUtils.isEmpty(coinMap)) {
            mMMKVDb.saveData(CUR_COIN_MAP, coinMap);
        }
    }

    /**
     * TODO 此处需要成哥改一下
     * 对应publicInfo
     *
     * @return
     */
    public CoinMapBean getCurrentCoinMap() {
        String coinMapName = mMMKVDb.getData(CUR_COIN_MAP);
        return DataManager.Companion.getCoinMapBySymbol(coinMapName);
    }

    public void setShowAssetStatus(Boolean status) {
        mMMKVDb.saveBooleanData(SHOW_ASSET_VIEW, status);
    }

    public Boolean getShowAssetStatus() {
        return mMMKVDb.getBooleanData(SHOW_ASSET_VIEW, false);
    }


    /**
     * 存储所有的币对信息第一次
     *
     * @param data
     */
    public void saveReqData(String data) {
        mMMKVDb.saveData(REQ_REVIEW, data);
    }

    public JSONObject getReqData() {
        String data = mMMKVDb.getData(REQ_REVIEW);
        if (!TextUtils.isEmpty(data)) {
            try {
                return new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        } else {
            return new JSONObject();
        }
    }


    /**
     * 买卖盘的方向
     */
    public void setDepthType(Boolean isHorizontal) {
        mMMKVDb.saveBooleanData(DEPTH_TYPE, isHorizontal);
    }

    public Boolean isHorizontalDepth() {
        return mMMKVDb.getBooleanData(DEPTH_TYPE, true);
    }


    /**
     * 买卖盘的方向
     */
    public void saveNewWorkURL(String isHorizontal) {
        mMMKVDb.saveData(CHANGE_HOST, isHorizontal);
    }

    public String getNewWorkURL() {
        return mMMKVDb.getData(CHANGE_HOST);
    }


    /**
     * 杠杆部分买卖方向
     *
     * @param isHorizontal
     */
    public void setDepthType4Lever(boolean isHorizontal) {
        mMMKVDb.saveBooleanData(LEVEL_DEPTH_TYPE, isHorizontal);

    }

    public boolean isHorizontalDepth4Lever() {
        return mMMKVDb.getBooleanData(LEVEL_DEPTH_TYPE, true);
    }


    /**
     * app的显示模式
     */
    public void setThemeMode(int mode) {
        mMMKVDb.saveIntData(SHOW_THEME_MODE, mode);
        if (mode == THEME_MODE_NIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public int getThemeMode() {
        return mMMKVDb.getIntData(SHOW_THEME_MODE, THEME_MODE_DAYTIME);
    }

    public void setCurrentSymbol(String symbol) {
        mMMKVDb.saveData(CUR_COIN_MAP, symbol);
    }


    public String getCurrentSymbol() {
        String data = mMMKVDb.getData(CUR_COIN_MAP);
        if (!StringUtil.checkStr(data)) {
            return "btcusdt";
        } else {
            return data;
        }
    }

    public void setCurrentSymbol4Lever(String symbol) {
        mMMKVDb.saveData(CUR_COIN_MAP_LEVER, symbol);
    }

    public String getCurrentSymbol4Lever() {
        String data = mMMKVDb.getData(CUR_COIN_MAP_LEVER);
        if (!StringUtil.checkStr(data)) {
            return "";
        } else {
            return data;
        }
    }


    /**
     * 所有场外开启的币对
     */
    /*
     * 得到无分组的 所有交易对
     */
    public ArrayList<String> getCoinArray() {
        JSONObject market = getCoinList(null);
        if (null == market) {
            return null;
        }

        Iterator<String> it = market.keys();
        ArrayList<String> array = new ArrayList<>();
        while (it.hasNext()) {
            String key = it.next();
            JSONObject value = market.optJSONObject(key);
            if (value.optInt("otcOpen") == 1) {
                array.add(value.optString("name"));
            }
        }
        return array;
    }

    /**
     * 是否用新版本
     */
    public boolean getOpenOrderCollect(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("open_order_collect"));
        return false;
    }


    /**
     * 用户角色开关
     * "0":关闭，"1":开启
     */
    public boolean isUserRoleLevel(@Nullable JSONObject data) {
        /*data = getData(data);
        if (null != data)
            return "1".equals(data.optString("user_role_level_open"));*/
        return false;
    }

    public String getThemeModeNew() {
        String theme = "day";
        if (getThemeMode() == 1) {
            theme = "night";
        }
        return theme;
    }


    /**
     * 首页弹窗的文案
     */
    public String getHomePageDialogTitle(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("popWindow_txt", "");
        return "";
    }


    public void saveHomePageDialogStatus(boolean isShow) {
        mMMKVDb.saveBooleanData(HOMEPAGE_SHOW_DIALOG_STATUS, isShow);
    }

    public boolean getHomePageDialogStatus() {
        return mMMKVDb.getBooleanData(HOMEPAGE_SHOW_DIALOG_STATUS, false);
    }

    public String getLeverDialogURL(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data) {
            JSONObject protocol_url_list = data.optJSONObject("protocol_url_list");
            if (protocol_url_list != null) {
                String el_gr = protocol_url_list.optString("el_GR", "");
                String ko_KR = protocol_url_list.optString("ko_KR", "");
                String en_US = protocol_url_list.optString("en_US", "");
                String zh_CN = protocol_url_list.optString("zh_CN", "");
                String ja_JP = protocol_url_list.optString("ja_JP", "");

                if (SystemUtils.isZh()) {
                    return zh_CN;
                } else if (SystemUtils.isKorea()) {
                    return ko_KR;
                } else if (SystemUtils.isTW()) {
                    return el_gr;
                } else if (SystemUtils.isJapanese()) {
                    return ja_JP;
                } else {
                    return en_US;
                }
            }

        }
        return "";
    }

    public void saveLeverDialogStatus(boolean isShow) {
        mMMKVDb.saveBooleanData(LEVER_SHOW_DIALOG_STATUS, isShow);
    }

    public boolean getLeverDialogStatus() {
        return mMMKVDb.getBooleanData(LEVER_SHOW_DIALOG_STATUS, false);
    }

    /**
     * 是否显示弹窗
     */
    public boolean hasShownLeverStatusDialog() {
        String leverDialogURL = PublicInfoDataService.getInstance().getLeverDialogURL(null);
        if (StringUtil.checkStr(leverDialogURL)) {
            return getLeverDialogStatus();
        } else {
            return true;
        }
    }


    /**
     * 杠杆开关
     * "0":关闭，"1":开启
     */
    public boolean isLeverOpen(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("lever_open"));
        return true;
    }

    public void saveCoinInfo4B2C(String symbol) {
        mMMKVDb.saveData(ParamConstant.B2C_SYMBOL, symbol);
    }

    public String getCoinInfo4B2c() {
        return mMMKVDb.getData(ParamConstant.B2C_SYMBOL);
    }

    /**
     * 是否使用网络语言包
     */
    public boolean isGetLanguageFromNet() {
        return false;
    }

    public void saveETFStateDialogStatus(boolean isShow) {
        mMMKVDb.saveBooleanData(ETF_STATE_DIALOG_STATUS, isShow);
    }

    public boolean getETFStateDialogStatus() {
        return mMMKVDb.getBooleanData(ETF_STATE_DIALOG_STATUS, false);
    }

    /**
     * 分享二维码链接
     *
     * @param data
     * @return
     */
    public String getSharingPage(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("sharingPage", "");
        return "";
    }

    /**
     * 新旧合约的开关
     */
    public boolean isNewContract(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return "1".equals(data.optString("isNewContract", "1"));
        return true;
    }

    /**
     * 是否显示首页资产
     *
     * @return
     */
    public boolean getAppIndexAssetsOpen(JSONObject data) {
        data = getData(data);
        if (null != data)
            return "0".equals(data.optString("appIndex_assets_open", ""));
        return true;
    }

    /**
     * 获取语言
     */
    public JSONObject getLocalesList(JSONObject data) {
        data = getData(data);
        if (null != data) {
            return data.optJSONObject("locales");
        }
        return null;
    }


    /**
     * 获取 客户Id
     */
    public String getCompanyId(JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("companyId", "");
        return "";
    }
    /**
     * 获取 资金费率
     */
    public String getfundRate(JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optString("fundRate", "");
        return "";
    }

    public void saveClientID(String data) {
        if (null != data) {
            mMMKVDb.saveData(BIND_CLIENT_ID, data);
        }
    }

    public String getClientID() {
        return mMMKVDb.getData(BIND_CLIENT_ID);
    }

    /**
     * 获取是否开启极验
     * 0 - 关
     * 1 - 开
     */
    public Boolean getPushStatus(@Nullable JSONObject data) {
        data = getData(data);
        if (null != data)
            return data.optInt("appPushSwitch") == 1;
        return false;
    }

}
