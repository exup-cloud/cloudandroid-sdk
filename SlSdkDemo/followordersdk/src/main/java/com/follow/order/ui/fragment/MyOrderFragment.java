package com.follow.order.ui.fragment;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseFragment;
import com.follow.order.bean.FollowProfitBean;
import com.follow.order.presenter.MyOrderPresenter;
import com.follow.order.presenter.contract.MyOrderContract;
import com.follow.order.utils.ColorUtils;
import com.follow.order.widget.CustomViewPager;
import com.follow.order.widget.MyFragmentPageAdapter;
import com.follow.order.widget.tab.SlidingScaleTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyOrderFragment extends MVPBaseFragment<MyOrderContract.View, MyOrderPresenter> implements MyOrderContract.View {

    private View mRootView;
    private LinearLayout ll_follow_data;
    private TextView tv_order_asset, tv_order_profit, tv_order_profit_rate;
    private SlidingScaleTabLayout orderTabLayout;
    private CustomViewPager vpOrder;
    private OrderPagerAdapter pagerAdapter;
    private String[] titles;

    @Override
    public View getBaseView() {
        mRootView = inflate(R.layout.fragment_my_order);
        return mRootView;
    }

    @Override
    protected void initView() {
        ll_follow_data = mRootView.findViewById(R.id.ll_follow_data);
        tv_order_asset = mRootView.findViewById(R.id.tv_order_asset);
        tv_order_profit = mRootView.findViewById(R.id.tv_order_profit);
        tv_order_profit_rate = mRootView.findViewById(R.id.tv_order_profit_rate);
        orderTabLayout = mRootView.findViewById(R.id.order_tab_layout);
        vpOrder = mRootView.findViewById(R.id.vp_order);

        titles = getResources().getStringArray(R.array.fo_tab_order_array);
        pagerAdapter = new OrderPagerAdapter(getChildFragmentManager(), titles);
        vpOrder.setAdapter(pagerAdapter);
        orderTabLayout.setViewPager(vpOrder);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEveryVisiableData() {
        super.initEveryVisiableData();
        mPresenter.getFollowProfit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showFollowProfit(FollowProfitBean infoBean) {
        if (infoBean != null) {
            tv_order_asset.setText(infoBean.getTotal_capital());
            tv_order_profit.setText(infoBean.getTotal_realised_pnl());
            tv_order_profit_rate.setText(infoBean.getTotal_realised_pnl_ratio());

            if (infoBean.getColor() != null) {
                ColorUtils.setTextColor(tv_order_profit, infoBean.getColor().getTotal_realised_pnl(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
                ColorUtils.setTextColor(tv_order_profit_rate, infoBean.getColor().getTotal_realised_pnl_ratio(), FollowOrderSDK.ins().getCustomAttrColor(getContext(), R.attr.fo_text_1_color));
            }

            if (TextUtils.isEmpty(infoBean.getTotal_capital())) {
                ll_follow_data.setVisibility(View.GONE);
            } else {
                ll_follow_data.setVisibility(View.VISIBLE);
            }
        } else {
            ll_follow_data.setVisibility(View.GONE);
        }
    }


    class OrderPagerAdapter extends MyFragmentPageAdapter {
        private FragmentManager mFm;
        private String[] mTitles;

        public OrderPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mFm = fm;
            mTitles = titles;
            List<String> tags = new ArrayList<>();
            for (String title : titles) {
                tags.add(title + "OrderPagerAdapter");
            }
            setTagName(tags);
        }

        /**
         * Return the Fragment associated with a specified positionTab.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFm.findFragmentByTag(tagNames.get(position));
            if (fragment == null) {
                String title = mTitles[position];
                if (TextUtils.equals(title, getString(R.string.fo_tab_order_item_1))) {
                    fragment = OrderListFragment.newInstance(1);
                } else if (TextUtils.equals(title, getString(R.string.fo_tab_order_item_2))) {
                    fragment = OrderListFragment.newInstance(3);
                }
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return tagNames.size();
        }
    }


}
