package com.follow.order.ui.fragment;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.follow.order.R;
import com.follow.order.base.BaseFragment;
import com.follow.order.event.MenuEvent;
import com.follow.order.widget.CustomViewPager;
import com.follow.order.widget.MyFragmentPageAdapter;
import com.follow.order.widget.tab.SlidingScaleTabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class FollowOrderFragment extends BaseFragment {

    private View mRootView;
    private SlidingScaleTabLayout followTabLayout;
    private CustomViewPager vpFollow;
    private TextView tv_menu;
    private FollowPagerAdapter pagerAdapter;
    private String[] titles;

    @Override
    public View getBaseView() {
        mRootView = inflate(R.layout.fragment_follow_order);
        return mRootView;
    }

    @Override
    protected void initView() {
        followTabLayout = mRootView.findViewById(R.id.follow_tab_layout);
        vpFollow = mRootView.findViewById(R.id.vp_follow);
        tv_menu = mRootView.findViewById(R.id.tv_menu);

        titles = getResources().getStringArray(R.array.fo_tab_follow_array);
        pagerAdapter = new FollowPagerAdapter(getChildFragmentManager(), titles);
        vpFollow.setAdapter(pagerAdapter);
        followTabLayout.setViewPager(vpFollow);
    }

    @Override
    protected void initListener() {
        tv_menu.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_menu) {
            EventBus.getDefault().post(new MenuEvent());
        }
    }

    class FollowPagerAdapter extends MyFragmentPageAdapter {
        private FragmentManager mFm;
        private String[] mTitles;

        public FollowPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mFm = fm;
            mTitles = titles;
            List<String> tags = new ArrayList<>();
            for (String title : titles) {
                tags.add(title + "FollowPagerAdapter");
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
                if (TextUtils.equals(title, getString(R.string.fo_tab_item_1))) {
                    fragment = FollowListFragment.newInstance("1");
                } else if (TextUtils.equals(title, getString(R.string.fo_tab_item_2))) {
                    fragment = FollowListFragment.newInstance("2");
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
