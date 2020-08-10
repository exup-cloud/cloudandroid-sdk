package com.yjkj.chainup.net.api;

import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.util.Utils;

/**
 * @author bertking
 */
public class ApiConstants {



    /**
     * Test
     */
    public static final String TEST_URL = "https://test.365os.com/exchange-app-api/";

    public static final String TEST_SOCKET_ADDRESS = "ws://stagingqa.365os.com/kline-api/ws";


//    /**
//     * HiEX
//     */
//    public static final String HiEX_URL = "https://appapi.hiex.pro/exchange-app-api/";
//    public static final String HiEX_SOCKET_ADDRESS = "wss://ws.hiex.pro/kline-api/ws";
//


    /**
     * 改进HiEX
     */
    public static final String HiEX_URL = "https://appapi100.hiex.pro/exchange-app-api/";
    public static final String HiEX_SOCKET_ADDRESS = "wss://ws100.hiex.pro/kline-api/ws";


    /**
     * 改进HiEX -200
     */
    public static final String HiEX_URL200 = "http://appapi200.hiex.pro/exchange-app-api/";
    public static final String HiEX_SOCKET_ADDRESS200 = "ws://ws200.hiex.pro/kline-api/ws";

    /**
     * 改进HiEX -200
     */
    public static final String HiEX_URL300 = "https://appapi300.hiex.pro/exchange-app-api/";
    public static final String HiEX_SOCKET_ADDRESS300 = "wss://ws300.hiex.pro/kline-api/ws";


    /**
     * ChainDown
     */
    public static String CHAINDOWN_URL = "https://www.chaindown.com/exchange-app-api/";
    public static String CHAINDOWN_SOCKET_ADDRESS = "wss://ws.chaindown.com/kline-api/ws";


    /**
     * 币涨
     */
    public static final String BTRISE_URL = "https://api.btrise.com/exchange-app-api/";
    public static final String BTRISE_SOCKET_ADDRESS = "ws://ws.btrise.com/kline-api/ws";

    /**
     * 获取是否saas
     */
    public static String EX_CHAINUP_BUNDLE_VERSION = ChainUpApp.appContext.getApplicationContext().getString(R.string.exChainupBundleVersion);
    public static String APP_SWITCH_SAAS = ChainUpApp.appContext.getApplicationContext().getString(R.string.appswitchsaas);
    public static String HOME_PAGE_SERVICE = ChainUpApp.appContext.getApplicationContext().getString(R.string.homepageService);

//    /**
//     * URL
//     */
//    public static String BASE_URL = BTRISE_URL;
//    /**
//     * WebSocket地址
//     */
//    public static String SOCKET_ADDRESS =BTRISE_SOCKET_ADDRESS
    /**
     * 现用 以上都是给用户使用的 url
     * URL
     */
    public static String BASE_URL = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.baseUrl));  //线上环境
    /**
     * OTC http
     */
    public static String BASE_OTC_URL = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.otcBaseUrl));
    /**
     * WebSocket地址
     */
    public static String SOCKET_ADDRESS = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.socketAddress));  //线上环境

    /**
     * OTC WebSocket地址
     */
    public static String SOCKET_OTC_ADDRESS = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.otcSocketAddress));

    /**
     * 合约地址
     */
    public static String CONTRACT_URL = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.contractUrl));  //线上环境

    /**
     * 合约 WebSocket地址
     */
    public static String SOCKET_CONTRACT_ADDRESS = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.contractSocketAddress));

    /**
     * 合约 WebSocket地址
     */
    public static String RED_PACKAGE_ADDRESS = Utils.returnAPIUrl(ChainUpApp.appContext.getApplicationContext().getString(R.string.redPackageUrl));
    /**
     * 获取 app加载首页模板
     */
    public static String HOME_VIEW_STATUS = ChainUpApp.appContext.getApplicationContext().getString(R.string.homeViewStatus);

    /**
     * 获取 app首页样式
     */
    public static String HOME_PAGE_STYLE = ChainUpApp.appContext.getApplicationContext().getString(R.string.homePageStyle);



}
