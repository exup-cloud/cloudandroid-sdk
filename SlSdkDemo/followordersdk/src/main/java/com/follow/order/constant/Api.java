package com.follow.order.constant;


/**
 * Created by Win7 on 2015/12/18.
 */
public class Api {
    public static String HOSTNAME = "https://chainupapi.1trade.vip/";//正式地址

    public static final String FO_TEST_PUBLICKEY = "api/rsa/publickey";
    //获取kol列表筛选菜单
    private static final String API_KOL_STYLE = "api/kol/style";
    //带单币种列表
    private static final String API_KOL_COINLIST = "api/kol/currencylist";
    //个人主页获取用户信息
    private static final String API_KOL_INFO = "api/kol/info";
    //USDT转换
    private static final String API_FOLLOW_USDT = "api/follow/usdt";
    //用户接入的交易所
    private static final String API_LIVE_APILIST = "api/live/apilist";
    //资产
    private static final String API_LIVE_INFO = "api/live/info";
    //提示弹窗
    private static final String API_COMMON_DIALOG = "api/common/dialog";

    public static String getUrlPublicKey() {
        return HOSTNAME + FO_TEST_PUBLICKEY;
    }

    public static String getUrlKolStyle() {
        return HOSTNAME + API_KOL_STYLE;
    }

    public static String getUrlKolCoinList() {
        return HOSTNAME + API_KOL_COINLIST;
    }

    public static String getUrlFollowUsdt() {
        return HOSTNAME + API_FOLLOW_USDT;
    }

    public static String getUrlPersonalInfo() {
        return HOSTNAME + API_KOL_INFO;
    }

    public static String getUrlApilist() {
        return HOSTNAME + API_LIVE_APILIST;
    }

    public static String getUrlLiveInfo() {
        return HOSTNAME + API_LIVE_INFO;
    }

    public static String getUrlCommonDialog() {
        return HOSTNAME + API_COMMON_DIALOG;
    }

}