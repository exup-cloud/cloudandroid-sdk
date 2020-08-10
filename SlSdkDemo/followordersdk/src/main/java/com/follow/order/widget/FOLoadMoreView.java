package com.follow.order.widget;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.follow.order.R;

/**
 * @time: 2020/3/23
 * @author: guodong
 */
public class FOLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.fo_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
