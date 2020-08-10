package com.follow.order.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseActivity;
import com.follow.order.bean.FollowPositionBean;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.presenter.OrderDetailPresenter;
import com.follow.order.presenter.contract.OrderDetailContract;
import com.follow.order.ui.adapter.PositionTeacherAdapter;
import com.follow.order.ui.adapter.PositionUserAdapter;
import com.follow.order.ui.dialog.FollowStopDialog;
import com.follow.order.ui.dialog.OrderWeightDialog;
import com.follow.order.ui.view.OrderShareView;
import com.follow.order.ui.view.PersonalProfitChartView;
import com.follow.order.utils.ClickUtil;
import com.follow.order.utils.ColorUtils;
import com.follow.order.widget.roundimg.RoundedImageView;
import com.follow.order.widget.shape.RoundTextView;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends MVPBaseActivity<OrderDetailContract.View, OrderDetailPresenter> implements OrderDetailContract.View {
    private ImageButton ibBack;
    private ImageButton ibShare;
    private RoundedImageView ivOrderAvatar;
    private TextView tvTitle;
    private TextView tvOrderNick;
    private TextView tvOrderExchange;
    private TextView tvOrderPriceType;
    private TextView tvLive;
    private TextView tvOrderTime;
    private TextView tvOrderAsset;
    private TextView tvOrderProfit;
    private TextView tvOrderRate;
    private TextView tvOrderState;
    private TextView tvOrderDivide;
    private TextView tvOrderPlatform;
    private TextView tvOrderCoin;
    private TextView tvOrderAmount;
    private TextView tvOrderDuration;
    private TextView tvOrderStopLoss;
    private TextView tvOrderStopProfit;
    private LinearLayout llOrderWeight;
    private TextView tvOrderWeight;
    private RecyclerView rvPositionTeacher, rvPositionUser;
    private TextView tvTeacherEmptyPosition;
    private TextView tvUserEmptyPosition;
    private RoundTextView btnOrderStop;
    private LinearLayout ll_follow_status, ll_stop_profit;
    private PersonalProfitChartView profit_chart_view;
    private OrderShareView order_share_view;
    private LinearLayout fo_share_content;
    private PositionTeacherAdapter teacherAdapter;
    private PositionUserAdapter userAdapter;
    private List<FollowPositionBean.MasterPosition> teacherList;
    private List<FollowPositionBean.Position> userList;
    private String follow_id, currency;
    private OrderBean orderBean;

    public static void start(Context context, String follow_id, String currency) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("follow_id", follow_id);
        intent.putExtra("currency", currency);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_order_detail);
        ibBack = findViewById(R.id.ib_back);
        ibShare = findViewById(R.id.ib_share);
        ivOrderAvatar = findViewById(R.id.iv_order_avatar);
        tvTitle = findViewById(R.id.tv_title);
        tvOrderNick = findViewById(R.id.tv_order_nick);
        tvOrderExchange = findViewById(R.id.tv_order_exchange);
        tvOrderPriceType = findViewById(R.id.tv_order_price_type);
        tvLive = findViewById(R.id.tv_live);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvOrderAsset = findViewById(R.id.tv_order_asset);
        tvOrderProfit = findViewById(R.id.tv_order_profit);
        tvOrderRate = findViewById(R.id.tv_order_rate);
        tvOrderState = findViewById(R.id.tv_order_state);
        tvOrderDivide = findViewById(R.id.tv_order_divide);
        tvOrderPlatform = findViewById(R.id.tv_order_platform);
        tvOrderCoin = findViewById(R.id.tv_order_coin);
        tvOrderAmount = findViewById(R.id.tv_order_amount);
        tvOrderDuration = findViewById(R.id.tv_order_duration);
        tvOrderStopLoss = findViewById(R.id.tv_order_stop_loss);
        tvOrderStopProfit = findViewById(R.id.tv_order_stop_profit);
        llOrderWeight = findViewById(R.id.ll_order_weight);
        tvOrderWeight = findViewById(R.id.tv_order_weight);
        rvPositionTeacher = findViewById(R.id.rv_position_teacher);
        rvPositionUser = findViewById(R.id.rv_position_user);
        tvTeacherEmptyPosition = findViewById(R.id.tv_teacher_empty_position);
        tvUserEmptyPosition = findViewById(R.id.tv_user_empty_position);
        btnOrderStop = findViewById(R.id.btn_order_stop);
        ll_stop_profit = findViewById(R.id.ll_stop_profit);
        ll_follow_status = findViewById(R.id.ll_follow_status);
        profit_chart_view = findViewById(R.id.profit_chart_view);
        order_share_view = findViewById(R.id.order_share_view);
        fo_share_content = findViewById(R.id.fo_share_content);

        profit_chart_view.setFromType(PersonalProfitChartView.TYPE_ORDER);

        if (teacherList == null) {
            teacherList = new ArrayList<>();
        }

        if (userList == null) {
            userList = new ArrayList<>();
        }

        LinearLayoutManager teacherLayoutManager = new LinearLayoutManager(this);
        teacherLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPositionTeacher.setLayoutManager(teacherLayoutManager);

        teacherAdapter = new PositionTeacherAdapter(teacherList);
        rvPositionTeacher.setAdapter(teacherAdapter);

        LinearLayoutManager userLayoutManager = new LinearLayoutManager(this);
        userLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPositionUser.setLayoutManager(userLayoutManager);

        userAdapter = new PositionUserAdapter(userList);
        rvPositionUser.setAdapter(userAdapter);
    }

    @Override
    protected void initListener() {
        ibBack.setOnClickListener(this);
        ibShare.setOnClickListener(this);
        tvLive.setOnClickListener(this);
        llOrderWeight.setOnClickListener(this);
        btnOrderStop.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        follow_id = getIntent().getStringExtra("follow_id");
        currency = getIntent().getStringExtra("currency");
        tvTitle.setText(getString(R.string.fo_order_detail_title, currency));
        mPresenter.getOrderDetail(follow_id);
        mPresenter.getTrendData(follow_id);
        mPresenter.getFollowShareInfo(follow_id);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ib_back) {
            back();
        } else if (v.getId() == R.id.ib_share) {
            mPresenter.share(fo_share_content);
        } else if (v.getId() == R.id.tv_live) {
            if (ClickUtil.isFastDoubleClick()) {
                return;
            }
            if (orderBean != null && orderBean.getUser() != null) {
                PersonalActivity.start(OrderDetailActivity.this, orderBean.getUser().getUid(), orderBean.getUser().getKol_id());
            }
        } else if (v.getId() == R.id.ll_order_weight) {
            showWeightDialog();
        } else if (v.getId() == R.id.btn_order_stop) {
            if (ClickUtil.isFastDoubleClick()) {
                return;
            }
            if (orderBean == null || orderBean.getUser() == null || orderBean.getFollow() == null) {
                return;
            }
            showFollowStopDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void shareBitmap(Bitmap bitmap) {
        FollowOrderSDK.ins().getFollowOrderProxy().shareBitmap(OrderDetailActivity.this, bitmap);
    }

    @Override
    public void showOrderDetail(OrderBean orderBean) {
        if (orderBean == null) {
            return;
        }
        this.orderBean = orderBean;
        if (orderBean.getUser() != null) {
            FollowOrderSDK.ins().getFollowOrderProxy().loadImage(orderBean.getUser().getHead_img(), FollowOrderSDK.ins().getCustomAttrResId(this, R.attr.fo_avatar_drawable), ivOrderAvatar);
            tvOrderNick.setText(orderBean.getUser().getNick_name());
            tvOrderExchange.setText(orderBean.getUser().getExchange());
        }
        if (orderBean.getFollow() != null) {
            tvOrderPriceType.setText(orderBean.getFollow().getFollow_type());
            tvOrderTime.setText(orderBean.getFollow().getStart_time());
            tvOrderState.setText(orderBean.getFollow().getFollow_status_desc());
            if (orderBean.getFollow().getFollow_status() == 1) {//跟单中
                ll_follow_status.setVisibility(View.VISIBLE);
            } else if (orderBean.getFollow().getFollow_status() == 3) {//跟单结束
                ll_follow_status.setVisibility(View.GONE);
            }
            tvOrderAsset.setText(orderBean.getFollow().getPrincipal());
            tvOrderProfit.setText(orderBean.getFollow().getPnl());
            tvOrderRate.setText(orderBean.getFollow().getPnl_ratio());
            tvOrderDivide.setText(orderBean.getFollow().getCommission());
            tvOrderPlatform.setText(FollowOrderSDK.ins().getFollowOrderProxy().getAppName());
            tvOrderCoin.setText(orderBean.getFollow().getCurrency());
            tvOrderAmount.setText(orderBean.getFollow().getPrincipal());
            tvOrderDuration.setText(orderBean.getFollow().getFollow_days());
            tvOrderStopLoss.setText(orderBean.getFollow().getStop_deficit() + "%");
            if (!TextUtils.equals("0", orderBean.getFollow().getStop_profit())) {
                ll_stop_profit.setVisibility(View.VISIBLE);
                tvOrderStopProfit.setText(orderBean.getFollow().getStop_profit() + "%");
            } else {
                ll_stop_profit.setVisibility(View.INVISIBLE);
            }
            tvOrderWeight.setText(orderBean.getFollow().getFollow_ratio());
        }
        if (orderBean.getColor() != null) {
            ColorUtils.setTextColor(tvOrderProfit, orderBean.getColor().getPnl(), FollowOrderSDK.ins().getCustomAttrColor(this, R.attr.fo_text_1_color));
            ColorUtils.setTextColor(tvOrderRate, orderBean.getColor().getPnl_ratio(), FollowOrderSDK.ins().getCustomAttrColor(this, R.attr.fo_text_1_color));
        }
        if (orderBean.getPosition() != null) {
            List<FollowPositionBean.MasterPosition> masterPosition = orderBean.getPosition().getMaster_position();
            if (masterPosition != null && masterPosition.size() > 0) {
                teacherList.clear();
                teacherList.addAll(masterPosition);
                teacherAdapter.notifyDataSetChanged();
                rvPositionTeacher.setVisibility(View.VISIBLE);
                tvTeacherEmptyPosition.setVisibility(View.GONE);
            } else {
                rvPositionTeacher.setVisibility(View.GONE);
                tvTeacherEmptyPosition.setVisibility(View.VISIBLE);
            }

            List<FollowPositionBean.Position> userPosition = orderBean.getPosition().getPosition();
            if (userPosition != null && userPosition.size() > 0) {
                userList.clear();
                userList.addAll(userPosition);
                userAdapter.notifyDataSetChanged();
                rvPositionUser.setVisibility(View.VISIBLE);
                tvUserEmptyPosition.setVisibility(View.GONE);
            } else {
                rvPositionUser.setVisibility(View.GONE);
                tvUserEmptyPosition.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void showTrendData(List<UserFinanceProfileBean.ProfitHistoryBean> chartData) {
        if (chartData == null) {
            return;
        }
        if (profit_chart_view != null) {
            profit_chart_view.setChartData(chartData);
        }
    }

    @Override
    public void showFollowShareInfo(OrderShareBean shareData) {
        if (order_share_view != null) {
            order_share_view.setShareOrderData(shareData);
        }
    }

    @Override
    public void stopFollowSuccess() {
        back();
    }

    private void showWeightDialog() {
        OrderWeightDialog weightDialog = new OrderWeightDialog(this);
        weightDialog.show();
    }

    private void showFollowStopDialog() {
        FollowStopDialog stopDialog = new FollowStopDialog(this);
        stopDialog.setOnDialogClickListener(new FollowStopDialog.OnDialogClickListener() {
            @Override
            public void onStopFollow() {
                if (orderBean != null && orderBean.getUser() != null && orderBean.getFollow() != null) {
                    mPresenter.stopFollow(follow_id, orderBean.getUser().getUid(), orderBean.getFollow().getFollow_exchange());
                }
            }
        });
        stopDialog.show();
    }


}
