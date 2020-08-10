package com.yjkj.chainup.wedegit.circleviewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class CirclePagerAdapter extends PagerAdapter {
    private List<ImageView> mList;

    public CirclePagerAdapter() {
        mList = new ArrayList<>();
    }

    public void setImage(List<ImageView> list) {
        if (mList == null) mList = new ArrayList<>();
        mList.clear();
        mList.addAll(list);
    }

    @Override
    public int getCount() {
        if (mList.size() < 2) return mList.size();
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        position %= mList.size();
        if (position < 0) {
            position = mList.size() + position;
        }
        ImageView imageView = mList.get(position);
        ViewParent parent = imageView.getParent();
        if (parent != null) {
            ViewGroup group = (ViewGroup) parent;
            group.removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
