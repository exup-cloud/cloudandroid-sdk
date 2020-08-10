package com.follow.order.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * @time: 2020/3/16
 * @author: guodong
 */
public interface FollowOrderListener {

    /**
     * 用户是否登录
     *
     * @return
     */
    boolean checkLogin(Context context);

    /**
     * 跳转登录页面
     */
    void toLogin();

    /**
     * 分享图片
     *
     * @param activity
     * @param bitmap
     */
    void shareBitmap(Activity activity, Bitmap bitmap);

    /**
     * 加载图片
     *
     * @param url
     * @param defImage
     * @param imageView
     */
    void loadImage(String url, int defImage, ImageView imageView);

    /**
     * 获取图片
     *
     * @param url
     * @return
     */
    Bitmap loadBitmap(String url);

    /**
     * 获取二维码bitmap
     *
     * @return
     */
    Bitmap getQrcodeBitmap();

    /**
     * 获取应用logo
     *
     * @return
     */
    Drawable getAppLogo();

    /**
     * 获取应用名称
     *
     * @return
     */
    String getAppName();

    /**
     * 获取kol列表
     *
     * @param sort             排序
     * @param style            筛选
     * @param currency         币种筛选
     * @param just_show_follow 仅显示可跟
     * @param page             分页
     * @param resultListener
     */
    void getKolList(String sort, String style, String currency, int just_show_follow, int page, OnFOResultListener resultListener);

    /**
     * 获取跟单列表
     *
     * @param status
     * @param page
     * @param resultListener
     */
    void getFollowList(int status, int page, OnFOResultListener resultListener);

    /**
     * 获取跟单配置
     *
     * @param master_currency_id
     * @param resultListener
     */
    void getFollowOption(String master_currency_id, OnFOResultListener resultListener);

    /**
     * 获取跟单收益
     *
     * @param resultListener
     */
    void getFollowProfit(OnFOResultListener resultListener);

    /**
     * 获取跟单详情
     *
     * @param follow_id
     * @param resultListener
     */
    void getFollowDetail(String follow_id, OnFOResultListener resultListener);

    /**
     * 获取跟单收益趋势
     *
     * @param follow_id
     * @param resultListener
     */
    void getFollowTrend(String follow_id, OnFOResultListener resultListener);

    /**
     * 获取跟单分享信息
     *
     * @param follow_id
     * @param resultListener
     */
    void getFollowShare(String follow_id, OnFOResultListener resultListener);

    /**
     * 开始跟单
     *
     * @param trade_currency_id
     * @param uid
     * @param exchange
     * @param total
     * @param is_stop_deficit
     * @param stop_deficit
     * @param is_stop_profit
     * @param stop_profit
     * @param symbol            币种
     * @param resultListener
     */
    void startFollow(String trade_currency_id, String uid, String exchange, String total, int is_stop_deficit, String stop_deficit, int is_stop_profit, String stop_profit, String symbol, String currency, String trade_currency, int follow_immediately, OnFOResultListener resultListener);

    /**
     * 结束跟单
     *
     * @param follow_id
     * @param uid
     * @param exchange
     * @param resultListener
     */
    void stopFollow(String follow_id, String uid, String exchange, OnFOResultListener resultListener);

    /**
     * 获取用户对应币种的余额
     *
     * @param coinSymbols 币种名称，大写，多个以逗号分割，如"BTC,USDT"
     */
    void getAccountBalance(String coinSymbols, OnFOResultListener resultListener);


}
