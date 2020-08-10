package com.follow.order.ui.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseFragment;
import com.follow.order.bean.ListData;
import com.follow.order.bean.OrderBean;
import com.follow.order.bean.OrderShareBean;
import com.follow.order.presenter.OrderListPresenter;
import com.follow.order.presenter.contract.OrderListContract;
import com.follow.order.ui.adapter.OrderListAdapter;
import com.follow.order.ui.view.OrderShareView;
import com.follow.order.utils.DensityUtil;
import com.follow.order.widget.EmptyLayout;
import com.follow.order.widget.FOLoadMoreView;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends MVPBaseFragment<OrderListContract.View, OrderListPresenter> implements OrderListContract.View {

    private View mRootView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv_list;
    private OrderShareView order_share_view;
    private LinearLayout fo_share_content;
    private EmptyLayout emptyLayout;
    private OrderListAdapter orderAdapter;
    private List<OrderBean> orderList;
    private int status;
    private int page = 1;

    public static OrderListFragment newInstance(int status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            status = getArguments().getInt("status");
        }
    }

    @Override
    public View getBaseView() {
        mRootView = inflate(R.layout.fragment_order_list);
        return mRootView;
    }

    @Override
    protected void initView() {
        refreshLayout = mRootView.findViewById(R.id.refresh_layout);
        rv_list = mRootView.findViewById(R.id.rv_list);
        order_share_view = mRootView.findViewById(R.id.order_share_view);
        fo_share_content = mRootView.findViewById(R.id.fo_share_content);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.fo_refresh_color));

        if (orderList == null) {
            orderList = new ArrayList<>();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        orderAdapter = new OrderListAdapter(orderList);
        orderAdapter.bindToRecyclerView(rv_list);
        orderAdapter.setLoadMoreView(new FOLoadMoreView());
        orderAdapter.setEnableLoadMore(true);

        emptyLayout = new EmptyLayout(getActivity(), refreshLayout);
        emptyLayout.setTempViewShow(true, DensityUtil.dip2px(150));
        emptyLayout.showContent();

    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        orderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadmore();
            }
        }, rv_list);
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();
                refresh();
            }
        });
        orderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_order_share) {
                    OrderBean bean = orderList.get(position);
                    if (bean != null && bean.getFollow() != null) {
                        mPresenter.getFollowShareInfo(bean.getFollow().getFollow_id());
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        showProgressDialog();
        refresh();
        hasInit = true;
    }

    @Override
    protected void initEveryVisiableData() {
        super.initEveryVisiableData();
        if (hasInit) {
            refresh();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refresh() {
        page = 1;
        mPresenter.getFollowList(status, page);
    }

    private void loadmore() {
        page++;
        mPresenter.getFollowList(status, page);
    }

    @Override
    public void showOrderData(ListData<OrderBean> orderData) {
        if (page == 1) {
            if (orderData == null || orderData.getData() == null || orderData.getData().isEmpty()) {
                refreshComplete();
                emptyLayout.showEmpty();
                return;
            }
            orderList.clear();
            orderList.addAll(orderData.getData());
            orderAdapter.notifyDataSetChanged();
            orderAdapter.disableLoadMoreIfNotFullPage(rv_list);
            emptyLayout.showContent();
        } else {
            if (orderData == null || orderData.getData() == null || orderData.getData().isEmpty()) {
                orderAdapter.loadMoreEnd(true);
                return;
            }
            orderAdapter.loadMoreEnd();
            orderList.addAll(orderData.getData());
            orderAdapter.notifyDataSetChanged();
            orderAdapter.disableLoadMoreIfNotFullPage(rv_list);
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
        if (orderAdapter != null) {
            orderAdapter.loadMoreComplete();
        }
    }

    @Override
    public void showFollowShareInfo(OrderShareBean shareData) {
        if (shareData != null) {
            if (order_share_view != null) {
                order_share_view.setShareOrderData(shareData, new OrderShareView.OnShareImageLoadListener() {
                    @Override
                    public void imageLoadComplete() {
                        mPresenter.share(fo_share_content);
                    }
                });
            }
        }
    }

    @Override
    public void shareBitmap(Bitmap bitmap) {
        FollowOrderSDK.ins().getFollowOrderProxy().shareBitmap(getActivity(), bitmap);
    }
}
