package com.follow.order.widget.tab.listener;

public interface OnTabSelectListener {
    //返回true就不再处理
    boolean onTabSelectBefore(int position);

    void onTabSelect(int position);

    void onTabReselect(int position);
}