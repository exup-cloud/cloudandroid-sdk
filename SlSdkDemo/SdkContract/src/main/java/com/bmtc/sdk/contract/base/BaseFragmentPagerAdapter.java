package com.bmtc.sdk.contract.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by zj on 2017/10/18.
 */

public class BaseFragmentPagerAdapter<T extends BaseFragment> extends FragmentStatePagerAdapter {

    private String[] mTitles;
    private List<T> mFragmentList;

    public BaseFragmentPagerAdapter(FragmentManager fm,
                                    List<T> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    public BaseFragmentPagerAdapter(FragmentManager fm,
                                    List<T> fragmentList, String[] titles) {
        super(fm);
        mFragmentList = fragmentList;
        mTitles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }

}
