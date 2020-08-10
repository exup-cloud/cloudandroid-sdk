package com.follow.order.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.utils.ColorUtils;
import com.follow.order.widget.roundimg.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @time: 2020/3/20
 * @author: guodong
 */
public class OrderShareView extends LinearLayout {
    private TextView tvShareProfitRate;
    private TextView tvShareDay;
    private RoundedImageView ivAvatar;
    private TextView tvShareNick;
    private TextView tvShareTotalRate;
    private TextView tvShareTotalProfit;
    private ImageView ivShareLogo;
    private ImageView ivShareQrcode;
    private TextView tvShareExchange;
    private OnShareImageLoadListener loadListener;


    public OrderShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvShareProfitRate = findViewById(R.id.tv_share_profit_rate);
        tvShareDay = findViewById(R.id.tv_share_day);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvShareNick = findViewById(R.id.tv_share_nick);
        tvShareTotalRate = findViewById(R.id.tv_share_total_rate);
        tvShareTotalProfit = findViewById(R.id.tv_share_total_profit);
        ivShareLogo = findViewById(R.id.iv_share_logo);
        ivShareQrcode = findViewById(R.id.iv_share_qrcode);
        tvShareExchange = findViewById(R.id.tv_share_exchange);
    }

    public void setShareOrderData(OrderShareBean shareData, OnShareImageLoadListener loadListener) {
        this.loadListener = loadListener;
        setShareOrderData(shareData);
    }

    public void setShareOrderData(OrderShareBean shareData) {
        if (shareData == null) {
            return;
        }
        tvShareProfitRate.setText(shareData.getPnl_ratio());
        tvShareDay.setText(shareData.getFollow_days());
        tvShareNick.setText(shareData.getNick_name());
        tvShareTotalRate.setText(shareData.getTotal_pnl_ratio());
        tvShareTotalProfit.setText(shareData.getTotal_pnl());
        if (shareData.getColor() != null) {
            ColorUtils.setTextColor(tvShareProfitRate, shareData.getColor().getPnl_ratio(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
        }
        ivShareLogo.setImageDrawable(FollowOrderSDK.ins().getFollowOrderProxy().getAppLogo());
        tvShareExchange.setText(FollowOrderSDK.ins().getFollowOrderProxy().getAppName());
        loadImage(shareData.getHead_img());

    }

    public void loadImage(final String avatar_url) {
        Observable.create(new ObservableOnSubscribe<List<Bitmap>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Bitmap>> emitter) throws Exception {
                Bitmap avatar = FollowOrderSDK.ins().getFollowOrderProxy().loadBitmap(avatar_url);
                Bitmap bitmap = FollowOrderSDK.ins().getFollowOrderProxy().getQrcodeBitmap();
                List<Bitmap> bitmapList = new ArrayList<>();
                bitmapList.add(avatar);
                bitmapList.add(bitmap);
                emitter.onNext(bitmapList);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Bitmap>>() {
                    @Override
                    public void accept(List<Bitmap> bitmapList) throws Exception {
                        if (bitmapList != null && bitmapList.size() == 2 && ivAvatar != null && ivShareLogo != null && ivShareQrcode != null) {
                            ivAvatar.setImageBitmap(bitmapList.get(0));
                            ivShareQrcode.setImageBitmap(bitmapList.get(1));
                            if (loadListener != null) {
                                loadListener.imageLoadComplete();
                            }
                        }

                    }
                });
    }

    public interface OnShareImageLoadListener {
        void imageLoadComplete();
    }
}
