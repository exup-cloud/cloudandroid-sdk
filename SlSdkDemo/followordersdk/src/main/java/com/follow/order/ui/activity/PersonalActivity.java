package com.follow.order.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseActivity;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.bean.PersonalInfoBean;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.presenter.PersonalPresenter;
import com.follow.order.presenter.contract.PersonalContract;
import com.follow.order.ui.adapter.ExchangeAdapter;
import com.follow.order.ui.view.PersonalAssetsView;
import com.follow.order.ui.view.PersonalProfitChartView;
import com.follow.order.utils.DensityUtil;
import com.follow.order.widget.roundimg.RoundedImageView;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class PersonalActivity extends MVPBaseActivity<PersonalContract.View, PersonalPresenter> implements PersonalContract.View, AppBarLayout.OnOffsetChangedListener, ExchangeAdapter.OnExchangeCheckedListener {
    private AppBarLayout appbar_layout;
    private LinearLayout ll_title;
    private TextView tv_title;
    private View line_title, status_view;
    private ImageButton ib_back;
    private ImageView iv_head;
    private RoundedImageView iv_avatar;
    private TextView tv_nick, tv_desc;
    private RecyclerView rv_exchange;
    private PersonalAssetsView assets_view;
    private PersonalProfitChartView profit_chart_view;
    private ExchangeAdapter exchangeAdapter;
    private List<ExchangeBean> exchangeList;
    private String uid, api_id, kol_id;
    private int mMaxScrollSize;
    private int mVerticalOffset;

    public static void start(Context context, String uid, String kol_id) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("kol_id", kol_id);
        context.startActivity(intent);
    }

    @Override
    protected void initStatus() {
        switch (FollowOrderSDK.ins().getTheme()) {
            case LIGHT:
                setTheme(R.style.ThemeLight);
                break;
            case DARK:
                setTheme(R.style.ThemeDark);
                break;
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_personal);
        appbar_layout = findViewById(R.id.appbar_layout);
        status_view = findViewById(R.id.status_view);
        ll_title = findViewById(R.id.ll_title);
        tv_title = findViewById(R.id.tv_title);
        line_title = findViewById(R.id.line_title);
        ib_back = findViewById(R.id.ib_back);
        iv_head = findViewById(R.id.iv_head);
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_nick = findViewById(R.id.tv_nick);
        tv_desc = findViewById(R.id.tv_desc);
        rv_exchange = findViewById(R.id.rv_exchange);
        assets_view = findViewById(R.id.assets_view);
        profit_chart_view = findViewById(R.id.profit_chart_view);

        status_view.getLayoutParams().height = DensityUtil.getStatusBarHeight();
        profit_chart_view.setFromType(PersonalProfitChartView.TYPE_PERSONAL);

        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        StatusBarUtil.setDarkMode(this);


        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv_exchange.setLayoutManager(layoutManager);

        if (exchangeList == null) {
            exchangeList = new ArrayList<>();
        }

        exchangeAdapter = new ExchangeAdapter(exchangeList);
        exchangeAdapter.setOnExchangeCheckedListener(this);
        rv_exchange.setAdapter(exchangeAdapter);
    }

    @Override
    protected void initListener() {
        appbar_layout.addOnOffsetChangedListener(this);
        ib_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        uid = getIntent().getStringExtra("uid");
        kol_id = getIntent().getStringExtra("kol_id");
        mPresenter.getPersonalUserInfo(kol_id);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ib_back) {
            back();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mMaxScrollSize = appBarLayout.getTotalScrollRange();
        if (mVerticalOffset == verticalOffset) {
            return;
        }
        if (Math.abs(mMaxScrollSize) == Math.abs(verticalOffset)) {
            resetTheme();
            ll_title.setBackgroundColor(FollowOrderSDK.ins().getCustomAttrColor(PersonalActivity.this, R.attr.fo_status_bg_color));
            ib_back.setImageDrawable(FollowOrderSDK.ins().getCustomAttrDrawable(PersonalActivity.this, R.attr.fo_back_drawable));
            line_title.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.VISIBLE);
        } else {
            ll_title.setBackgroundColor(Color.TRANSPARENT);
            ib_back.setImageResource(R.drawable.fo_back_dark);
            line_title.setVisibility(View.GONE);
            if (Math.abs(mMaxScrollSize) == Math.abs(mVerticalOffset)) {
                StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
                StatusBarUtil.setDarkMode(this);
                tv_title.setVisibility(View.GONE);
            }
        }
        mVerticalOffset = verticalOffset;
    }

    @Override
    public void onExchangeChecked(ExchangeBean exchange) {
        if (exchange == null) {
            return;
        }
        api_id = exchange.getApi_id();
        mPresenter.getLiveFinanceProfile(uid, api_id);
    }

    @Override
    public void showPersonalUserInfo(PersonalInfoBean infoBean) {
        if (infoBean != null) {
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(infoBean.getHead_img(), FollowOrderSDK.ins().getCustomAttrResId(this, R.attr.fo_avatar_drawable), iv_head);
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(infoBean.getHead_img(), FollowOrderSDK.ins().getCustomAttrResId(this, R.attr.fo_avatar_drawable), iv_avatar);
            tv_nick.setText(infoBean.getNick_name());
            tv_title.setText(infoBean.getNick_name());
            tv_desc.setText(infoBean.getDesc());
        }
    }

    @Override
    public void showExchangeData(List<ExchangeBean> exchangeData) {
        if (exchangeData == null || exchangeData.isEmpty()) {
            return;
        }
        api_id = exchangeData.get(0).getApi_id();
        exchangeData.get(0).setSelected(true);
        exchangeList.clear();
        exchangeList.addAll(exchangeData);
        exchangeAdapter.notifyDataSetChanged();
        rv_exchange.setVisibility(View.VISIBLE);
        mPresenter.getLiveFinanceProfile(uid, api_id);
    }

    @Override
    public void showLiveFinanceProfile(UserFinanceProfileBean financeBean) {
        if (financeBean == null) {
            return;
        }
        if (assets_view != null) {
            assets_view.setAssetsData(financeBean);
        }
        if (profit_chart_view != null) {
            profit_chart_view.setChartData(financeBean.getProfit_history());
        }
    }

    private void resetTheme() {
        switch (FollowOrderSDK.ins().getTheme()) {
            case LIGHT:
                StatusBarUtil.setLightMode(this);
                break;
            case DARK:
                StatusBarUtil.setDarkMode(this);
                break;
        }
        StatusBarUtil.setColorNoTranslucent(this, FollowOrderSDK.ins().getCustomAttrColor(this, R.attr.fo_status_bg_color));

    }
}

