package com.yjkj.chainup.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.follow.order.impl.FollowOrderListener;
import com.follow.order.impl.OnFOResultListener;
import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.db.constant.RoutePath;
import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.db.service.UserDataService;
import com.yjkj.chainup.extra_service.arouter.ArouterUtil;
import com.yjkj.chainup.manager.LoginManager;

import org.json.JSONObject;

/**
 * @time: 2020/3/19
 * @author: guodong
 */
public class FollowOrderImpl implements FollowOrderListener {

    /**
     * 检查登录
     *
     * @param context
     * @return
     */
    @Override
    public boolean checkLogin(Context context) {
        return LoginManager.checkLogin(context, true);
    }

    /**
     * 跳转登录
     */
    @Override
    public void toLogin() {
        UserDataService.getInstance().clearToken();
        JSONObject userinfo = UserDataService.getInstance().getUserData();
        if (null == userinfo) {
            ArouterUtil.navigation(RoutePath.NewVersionLoginActivity, null);
        } else {
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ChainUpApp.appContext);
            if (fingerprintManager.isHardwareDetected()) {
                /**
                 * 判断是否输入指纹
                 */
                if (fingerprintManager.hasEnrolledFingerprints() && LoginManager.getInstance().getFingerprint() == 1) {
                    ArouterUtil.navigation(RoutePath.NewVersionLoginActivity, null);
                } else if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePass()) || !TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("SET_TYPE", 1);
                    bundle.putString("SET_TOKEN", "");
                    bundle.putBoolean("SET_STATUS", true);
                    bundle.putBoolean("SET_LOGINANDSET", false);
                    ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
                } else {
                    ArouterUtil.navigation(RoutePath.NewVersionLoginActivity, null);
                }
            } else if (!TextUtils.isEmpty(UserDataService.getInstance().getGesturePass()) || !TextUtils.isEmpty(UserDataService.getInstance().getGesturePwd())) {
                Bundle bundle = new Bundle();
                bundle.putInt("SET_TYPE", 1);
                bundle.putString("SET_TOKEN", "");
                bundle.putBoolean("SET_STATUS", true);
                bundle.putBoolean("SET_LOGINANDSET", false);
                ArouterUtil.navigation("/login/gesturespasswordactivity", bundle);
            } else {
                ArouterUtil.navigation(RoutePath.NewVersionLoginActivity, null);
            }
        }
    }

    /**
     * 分享图片
     *
     * @param activity
     * @param bitmap
     */
    @Override
    public void shareBitmap(Activity activity, Bitmap bitmap) {
        ZXingUtils.shareImageToWechat(bitmap, "", activity);
    }

    /**
     * 加载图片
     *
     * @param url
     * @param defImage
     * @param imageView
     */
    @Override
    public void loadImage(String url, int defImage, ImageView imageView) {
        Glide.with(ChainUpApp.appContext).load(url).placeholder(defImage).into(imageView);
    }

    /**
     * 获取bitmap
     *
     * @param url
     * @return
     */
    @Override
    public Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(ChainUpApp.appContext).asBitmap().load(url)
                    .centerCrop()
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 获取分享二维码
     *
     * @return
     */
    @Override
    public Bitmap getQrcodeBitmap() {
        String imgUrl = PublicInfoDataService.getInstance().getSharingPage(null);
        return ZXingUtils.createQRImage(imgUrl,  SizeUtils.dp2px(40f),SizeUtils.dp2px(40f));
    }

    /**
     * 交易所logo
     *
     * @return
     */
    @Override
    public Drawable getAppLogo() {
        return ChainUpApp.appContext.getResources().getDrawable(R.mipmap.ic_launcher);
    }

    /**
     * 交易所名称
     *
     * @return
     */
    @Override
    public String getAppName() {
        return ChainUpApp.appContext.getString(R.string.app_name);
    }

    /**
     * 获取kol列表
     *
     * @param sort             排序
     * @param style            筛选
     * @param currency         筛选
     * @param just_show_follow 仅显示可跟
     * @param page             分页
     * @param resultListener
     */
    @Override
    public void getKolList(String sort, String style, String currency, int just_show_follow, int page, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowKolList(sort, style, currency, String.valueOf(just_show_follow), String.valueOf(page), resultListener);
    }

    /**
     * 获取跟单列表
     *
     * @param status
     * @param page
     * @param resultListener
     */
    @Override
    public void getFollowList(int status, int page, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowList(String.valueOf(status), String.valueOf(page), resultListener);
    }

    /**
     * 获取跟单配置
     *
     * @param master_currency_id
     * @param resultListener
     */
    @Override
    public void getFollowOption(String master_currency_id, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowOptions(master_currency_id, resultListener);
    }

    /**
     * 获取跟单收益(跟单列表上的跟单收益信息)
     *
     * @param resultListener
     */
    @Override
    public void getFollowProfit(OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowProfit(resultListener);
    }

    /**
     * 获取跟单详情
     *
     * @param follow_id
     * @param resultListener
     */
    @Override
    public void getFollowDetail(String follow_id, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowDetail(follow_id, resultListener);
    }

    /**
     * 获取跟单收益趋势
     *
     * @param follow_id
     * @param resultListener
     */
    @Override
    public void getFollowTrend(String follow_id, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowTrend(follow_id, resultListener);
    }

    /**
     * 获取跟单分享信息
     *
     * @param follow_id
     * @param resultListener
     */
    @Override
    public void getFollowShare(String follow_id, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getFollowShare(follow_id, resultListener);
    }

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
     * @param symbol
     * @param currency           币种
     * @param follow_immediately 已有持仓是否继续跟单
     * @param resultListener
     */
    @Override
    public void startFollow(String trade_currency_id, String uid, String exchange, String total, int is_stop_deficit, String stop_deficit, int is_stop_profit, String stop_profit, String symbol, String currency, String trade_currency, int follow_immediately, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getInnerFollowbegin(trade_currency_id, total, String.valueOf(is_stop_deficit), stop_deficit, String.valueOf(is_stop_profit), stop_profit, symbol, currency, trade_currency, String.valueOf(follow_immediately), resultListener);
    }

    /**
     * 结束跟单
     *
     * @param follow_id
     * @param uid
     * @param exchange
     * @param resultListener
     */
    @Override
    public void stopFollow(String follow_id, String uid, String exchange, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getInnerFollowEnd(follow_id, resultListener);
    }

    /**
     * 获取用户对应币种的余额
     *
     * @param coinSymbols
     */
    @Override
    public void getAccountBalance(String coinSymbols, OnFOResultListener resultListener) {
        FollowOrderImplPresenter.Companion.getAccountBalance(coinSymbols, resultListener);
    }

}
