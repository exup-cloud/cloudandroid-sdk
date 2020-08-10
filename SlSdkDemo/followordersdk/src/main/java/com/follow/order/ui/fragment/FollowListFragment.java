package com.follow.order.ui.fragment;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseFragment;
import com.follow.order.bean.FollowBean;
import com.follow.order.bean.FollowCoinBean;
import com.follow.order.bean.ListData;
import com.follow.order.event.FollowRefreshEvent;
import com.follow.order.event.JustShowFollowEvent;
import com.follow.order.event.MenuSelectEvent;
import com.follow.order.presenter.FollowListPresenter;
import com.follow.order.presenter.contract.FollowListContract;
import com.follow.order.ui.activity.FollowSetupActivity;
import com.follow.order.ui.adapter.FollowListAdapter;
import com.follow.order.widget.EmptyLayout;
import com.follow.order.widget.FOLoadMoreView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FollowListFragment extends MVPBaseFragment<FollowListContract.View, FollowListPresenter> implements FollowListContract.View, FollowListAdapter.OnCoinActionListener {

    private View mRootView;
    private TextView tv_only_follow;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv_list;
    private EmptyLayout emptyLayout;
    private FollowListAdapter followAdapter;
    private List<FollowBean> followList;
    private int page = 1;
    private String sort = "1";
    private String style = "";
    private String coin = "";
    private int just_show_follow;

    public static FollowListFragment newInstance(String sort) {
        FollowListFragment fragment = new FollowListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("sort", sort);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            sort = getArguments().getString("sort");
        }
    }

    @Override
    public View getBaseView() {
        mRootView = inflate(R.layout.fragment_follow_list);
        return mRootView;
    }

    @Override
    protected void initView() {
        tv_only_follow = mRootView.findViewById(R.id.tv_only_follow);
        refreshLayout = mRootView.findViewById(R.id.refresh_layout);
        rv_list = mRootView.findViewById(R.id.rv_list);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.fo_refresh_color));

        if (followList == null) {
            followList = new ArrayList<>();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        followAdapter = new FollowListAdapter(followList);
        followAdapter.bindToRecyclerView(rv_list);
        followAdapter.setLoadMoreView(new FOLoadMoreView());
        followAdapter.setEnableLoadMore(true);

        emptyLayout = new EmptyLayout(getActivity(), refreshLayout);
        emptyLayout.showContent();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initListener() {
        tv_only_follow.setOnClickListener(this);
        followAdapter.setOnCoinActionListener(this);
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();
                refresh();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        followAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadmore();

            }
        }, rv_list);
    }

    @Override
    protected void initData() {
        showProgressDialog();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_only_follow) {
            if (just_show_follow == 1) {
                just_show_follow = 0;
            } else {
                just_show_follow = 1;
            }
            JustShowFollowEvent event = new JustShowFollowEvent();
            event.setChecked(just_show_follow);
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void showKolData(ListData<FollowBean> followData) {
        if (page == 1) {
            if (followData == null || followData.getData() == null || followData.getData().isEmpty()) {
                refreshComplete();
                emptyLayout.showEmpty();
                return;
            }
            followList.clear();
            followList.addAll(followData.getData());
            followAdapter.notifyDataSetChanged();
            followAdapter.disableLoadMoreIfNotFullPage(rv_list);
            emptyLayout.showContent();
        } else {
            if (followData == null || followData.getData() == null || followData.getData().isEmpty()) {
                followAdapter.loadMoreEnd();
                return;
            }
            followAdapter.loadMoreEnd();
            followList.addAll(followData.getData());
            followAdapter.notifyDataSetChanged();
            followAdapter.disableLoadMoreIfNotFullPage(rv_list);
        }
    }

    @Override
    public void showEmpty() {
        if (emptyLayout != null) {
            emptyLayout.showEmpty();
        }
    }

    @Override
    public void refreshComplete() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
        if (followAdapter != null) {
            followAdapter.loadMoreEnd();
        }
    }

    @Override
    public void onFollowOrder(FollowBean followBean, FollowCoinBean coinBean) {
        if (coinBean == null) {
            return;
        }
        FollowSetupActivity.start(getActivity(), followBean.getUid(), coinBean.getTrade_currency_id());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void menuToggle(MenuSelectEvent event) {
        if (event == null) {
            return;
        }
        style = event.getStyle();
        coin = event.getCoin();
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void justShowFollow(JustShowFollowEvent event) {
        if (event == null) {
            return;
        }
        just_show_follow = event.getChecked();
        if (just_show_follow == 0) {
            Drawable leftDrawable = FollowOrderSDK.ins().getCustomAttrDrawable(getActivity(), R.attr.fo_home_check_no_drawable);
            tv_only_follow.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        } else {
            tv_only_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fo_home_check_yes, 0, 0, 0);
        }
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void followRefresh(FollowRefreshEvent event) {
        refresh();
    }

    private void refresh() {
        page = 1;
        mPresenter.getKolList(sort, style, coin, just_show_follow, page);
    }

    private void loadmore() {
        page++;
        mPresenter.getKolList(sort, style, coin, just_show_follow, page);
    }

}
