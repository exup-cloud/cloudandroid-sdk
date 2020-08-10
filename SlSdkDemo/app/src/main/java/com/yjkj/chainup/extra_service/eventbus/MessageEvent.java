package com.yjkj.chainup.extra_service.eventbus;

public class MessageEvent {

    public static final int data_req_error = 1;   //数据请求失败，可用作全局处理

    public static final int collect_data_type = 2;   //自选或收藏数据事件

    public static final int hometab_switch_type = 3;   //首页tab切换事件

    public static final int color_rise_fall_type = 4;   //涨跌幅颜色

    public static final int symbol_switch_type = 5;   //切换币种事件

    public static final int TRANSFER_TYPE = 6;   //买or卖事件类型

    public static final int assetTabType = 7;   //资产页面tab切换

    public static final int coinSearchType = 8;   //侧边栏币对搜索

    public static final int closeLeftCoinSearchType = 9;   //关闭侧边栏事件

    public static final int login_operation_type = 10;   //登录操作事件

    public static final int coin_payment = 11; //币币交易或者法币交易交易

    public static final int fait_trading = 12; //去交易

    public static final int left_coin_contract_type = 13; //合约侧边栏

    public static final int refresh_trans_type = 14; // 资产页面刷新

    public static final int refresh_local_trans_type = 15; // 资产页面局部刷新

    public static final int refresh_local_b2c_trans_type = 16; // 资产页面局部刷新

    public static final int refresh_local_coin_trans_type = 17; // 资产页面局部刷新

    public static final int refresh_local_b2c_coin_trans_type = 18; // 资产页面局部刷新


    public static final int refresh_local_contract_type = 19; // 资产页面合约局部刷新

    public static final int refresh_local_lever_type = 20; // 资产页面 杠杆

    public static final int DEPTH_LEVEL_TYPE = 21; //深度刻度
    public static final int DEPTH_DATA_TYPE = 22; //深度图的数据

    public static final int CREATE_ORDER_TYPE = 23; //下单通知

    // 币币 or lever
    public static final int TAB_TYPE = 24 ;

    public static final int into_transfer_activity = 25; //进入划转页面

    public static final int into_my_asset_activity = 26; //是否全开

    //public static final int live_contract_asset_beanList = 28; //首页跳转


    public static final int coinTrade_tab_type = 29; //币币交易页面tab

    public static final int coinTrade_topTab_type = 30; //币币交易页面顶部币币tab

    public static final int leverTrade_topTab_type = 31; //币币交易页面顶部杠杆tab

    public static final int assetsTab_type = 32; //币币交易页面顶部杠杆tab

    public static final int assets_activity_finish_event = 33; //首页跳转

    public static final int contract_switch_type = 37;   //合约tab切换事件
    public static final int market_switch_type = 38;   //合约tab切换事件
    public static final int login_bind_type = 40;   //登录操作事件
    public static final int market_switch_curTime = 399;   //合约tab切换事件
    public static final int webview_refresh_type = 41;   //从h5页面跳转登录 刷新

    public static final int sl_contract_select_leverage_event = 400;   //合约切换杠杆
    public static final int sl_contract_left_coin_type = 401;//切换合约币种
    public static final int sl_contract_switch_time_type = 402;//切换K线区间

    private MessageEvent() {
    }

    private Object msg_content;//事件内容
    private int msg_type;//事件类型
    private boolean isLever;//是否是杠杆

    public MessageEvent(int msg_type) {
        this.msg_type = msg_type;
    }


    public MessageEvent(int msg_type, Object msg_content) {
        this.msg_content = msg_content;
        this.msg_type = msg_type;
    }

    public MessageEvent(boolean isLever) {
        this.isLever = isLever;
    }

    public MessageEvent( int msg_type,Object msg_content, boolean isLever) {
        this.msg_type = msg_type;
        this.msg_content = msg_content;
        this.isLever = isLever;
    }

    public MessageEvent setMsg_content(Object msg_content) {
        this.msg_content = msg_content;
        return this;
    }

    public Object getMsg_content() {
        return msg_content;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public boolean isLever() {
        return isLever;
    }

    public void setLever(boolean lever) {
        isLever = lever;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "msg_content=" + msg_content +
                ", msg_type=" + msg_type +
                ", isLever="  +isLever+
                '}';
    }
}