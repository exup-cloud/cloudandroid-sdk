package com.yjkj.chainup.wedegit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjkj.chainup.R;
import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.util.GlideUtils;
import com.yjkj.chainup.util.LogUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-10-25 11:33
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-10-25 11:33
 * @UpdateRemark: 更新说明
 */
public class NTabNavView extends LinearLayout {

    private static final String TAG = "NTabNavView";

    private Context context;

    public NTabNavView(Context context) {
        super(context);
        this.context = context;
    }

    public NTabNavView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public NTabNavView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public NTabNavView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    private ArrayList<View> views;

    public void setData(ArrayList<Integer> imgIDs, ArrayList<String> titles, OnClickListener l) {
        if (null == imgIDs || null == titles || imgIDs.size() != titles.size()) {
            return;
        }
        int count = imgIDs.size();
        if (count <= 0)
            return;
        this.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        views = new ArrayList<>();
        this.post(new Runnable() {
            @Override
            public void run() {
                int itemW = NTabNavView.this.getMeasuredWidth() / count;
                int itemH = NTabNavView.this.getMeasuredHeight();

                LogUtil.d(TAG, "setData==itemW is " + itemW + ",itemH is " + itemH);
                for (int i = 0; i < count; i++) {
                    View view = inflater.inflate(R.layout.item_main_home_tab, null);

                    View item_view_ll = view.findViewById(R.id.item_tab_view_ll);

                    LayoutParams params = new LayoutParams(itemW, itemH);
                    params.gravity = Gravity.CENTER;
                    item_view_ll.setLayoutParams(params);

                    ImageView imageview = item_view_ll.findViewById(R.id.imageview);
                    TextView textview = item_view_ll.findViewById(R.id.textview);
                    imageview.setSelected(0 == i);
                    textview.setSelected(0 == i);
                    setBottonTab(imageview, imgIDs.get(i));
                    textview.setText(titles.get(i));
                    item_view_ll.setTag(i);
                    item_view_ll.setOnClickListener(l);
                    views.add(view);
                    NTabNavView.this.addView(view);
                }
            }
        });
    }

    private void setBottonTab(ImageView imageView, int id) {
        JSONObject appPersonalIcon = PublicInfoDataService.getInstance().getAppPersonalIcon(null);
        if (appPersonalIcon == null) return;
        if (PublicInfoDataService.getInstance().getThemeMode() == 0) {
            if (id == R.drawable.bg_homepage_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_home_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_home_default_daytime"), appPersonalIcon.optString("tabbar_home_selected"), imageView);
                }


            }
            if (id == R.drawable.bg_market_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_quotes_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_quotes_default_daytime"), appPersonalIcon.optString("tabbar_quotes_selected"), imageView);
                }

            }
            if (id == R.drawable.bg_trade_tab) {

                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_exchange_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_exchange_default_daytime"), appPersonalIcon.optString("tabbar_exchange_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_otc_tab) {

                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_fiat_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_fiat_default_daytime"), appPersonalIcon.optString("tabbar_fiat_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_contract_tab) {

                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_contract_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_contract_default_daytime"), appPersonalIcon.optString("tabbar_contract_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_asset_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_assets_default_daytime"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(appPersonalIcon.optString("tabbar_assets_default_daytime"), appPersonalIcon.optString("tabbar_assets_selected"), imageView);

                }
            }
        } else {
            if (id == R.drawable.bg_homepage_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_home_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_home_default_night"), appPersonalIcon.optString("tabbar_home_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_market_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_quotes_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_quotes_default_night"), appPersonalIcon.optString("tabbar_quotes_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_trade_tab) {

                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_exchange_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_exchange_default_night"), appPersonalIcon.optString("tabbar_exchange_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_otc_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_fiat_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_fiat_default_night"), appPersonalIcon.optString("tabbar_fiat_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_contract_tab) {

                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_contract_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_contract_default_night"), appPersonalIcon.optString("tabbar_contract_selected"), imageView);
                }
            }
            if (id == R.drawable.bg_asset_tab) {
                if (TextUtils.isEmpty(appPersonalIcon.optString("tabbar_assets_default_night"))) {
                    imageView.setBackgroundResource(id);
                } else {
                    addSeletorFromNet(
                            appPersonalIcon.optString("tabbar_assets_default_night"), appPersonalIcon.optString("tabbar_assets_selected"), imageView);
                }
            }
        }


    }


    /**
     * 从网络获取图片 给 ImageView 设置 selector
     *
     * @param normalUrl 获取默认图片的链接
     * @param pressUrl  获取点击图片的链接
     * @param imageView 点击的 view
     */
    public static void addSeletorFromNet(final String normalUrl, final String pressUrl, final ImageView imageView) {
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... params) {
                StateListDrawable drawable = new StateListDrawable();

                Drawable normal = loadImageFromNet(normalUrl);
                Drawable press = loadImageFromNet(pressUrl);
                drawable.addState(new int[]{android.R.attr.state_selected}, press);
                drawable.addState(new int[]{-android.R.attr.state_selected}, normal);
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                imageView.setBackgroundDrawable(drawable);
            }
        }.execute();
    }


    /**
     * 从网络获取图片
     *
     * @param netUrl 获取图片的链接
     * @return 返回一个 drawable 类型的图片
     */
    private static Drawable loadImageFromNet(String netUrl) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(netUrl).openStream(), "netUrl.jpg");
        } catch (IOException e) {
            Log.e("jinlong", e.getMessage());
        }

        return drawable;
    }


    public void showCurTabView(int curIndex) {
        if (null == views || views.size() <= 0)
            return;
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            View imageview = view.findViewById(R.id.imageview);
            View textview = view.findViewById(R.id.textview);
            imageview.setSelected(i == curIndex);
            textview.setSelected(i == curIndex);
        }
    }

}
