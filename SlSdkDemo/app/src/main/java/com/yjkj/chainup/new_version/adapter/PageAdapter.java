package com.yjkj.chainup.new_version.adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.yjkj.chainup.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bertking
 * @Date：2019-08-22-14:36
 * @Description:
 */
public class PageAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PageAdapter";
    private ArrayList<String> titles;
    private List<Fragment> mFragments;
    public PageAdapter(FragmentManager fm,ArrayList<String> titles, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.mFragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return null == mFragments ? 0 : mFragments.size();
    }

    /*@Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(null==titles || titles.isEmpty()){
            return super.getPageTitle(position);
        }
        return titles.get(position);
    }*/

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
