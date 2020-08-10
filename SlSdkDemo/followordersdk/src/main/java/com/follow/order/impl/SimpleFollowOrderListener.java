package com.follow.order.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * @time: 2020/3/19
 * @author: guodong
 */
public class SimpleFollowOrderListener implements FollowOrderListener {

    @Override
    public boolean checkLogin(Context context) {
        return false;
    }

    @Override
    public void toLogin() {

    }

    @Override
    public void shareBitmap(Activity activity, Bitmap bitmap) {

    }

    @Override
    public void loadImage(String url, int defImage, ImageView imageView) {

    }

    @Override
    public Bitmap loadBitmap(String url) {
        return null;
    }

    @Override
    public Bitmap getQrcodeBitmap() {
        return null;
    }

    @Override
    public Drawable getAppLogo() {
        return null;
    }

    @Override
    public String getAppName() {
        return null;
    }

    @Override
    public void getKolList(String sort, String style, String currency, int just_show_follow, int page, OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowList(int status, int page, OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowOption(String master_currency_id, OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowProfit(OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowDetail(String follow_id, OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowTrend(String follow_id, OnFOResultListener resultListener) {

    }

    @Override
    public void getFollowShare(String follow_id, OnFOResultListener resultListener) {

    }

    @Override
    public void startFollow(String trade_currency_id, String uid, String exchange, String total, int is_stop_deficit, String stop_deficit, int is_stop_profit, String stop_profit, String symbol, String currency, String trade_currency, int follow_immediately, OnFOResultListener resultListener) {

    }

    @Override
    public void stopFollow(String follow_id, String uid, String exchange, OnFOResultListener resultListener) {

    }

    @Override
    public void getAccountBalance(String coinSymbols, OnFOResultListener resultListener) {

    }

}