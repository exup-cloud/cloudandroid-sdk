package com.yjkj.chainup.wedegit;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yjkj.chainup.util.LogUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @Author lianshangljl
 * @Date 2018/11/12-8:30 PM
 * @Email buptjinlong@163.com
 * @description  自己计算高度
 */
public class WrapContentViewPager extends ViewPager {

    private static final String TAG = "WrapContentViewPager";
    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private int current;
    private int height = 0;
    /**
     * 是否可滑动
     */
    private boolean scrollable = true;

    /**
     * 保存position与对于的View
     */
    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<Integer, View>();

    public WrapContentViewPager(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mChildrenViews.size() > current) {
            View child = mChildrenViews.get(current);
            if (child != null) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                height = child.getMeasuredHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void resetHeight(int current) {  //,int height
        this.current = current;
        try {
            if (mChildrenViews.size() > current) {
                if(getLayoutParams() instanceof  CoordinatorLayout.LayoutParams){
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, height);
                    } else {
                        layoutParams.height = height;
                    }
                    setLayoutParams(layoutParams);
                }else if(getLayoutParams() instanceof  LinearLayout.LayoutParams){
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new LinearLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, height);
                    } else {
                        layoutParams.height = height;
                    }
                    setLayoutParams(layoutParams);
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 保存position与对于的View
     */
    public void setObjectForPosition(View view, int position) {
        mChildrenViews.put(position, view);
    }


    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(scrollable){
            return super.onInterceptTouchEvent(ev);
        }else {
            return scrollable && super.onInterceptTouchEvent(ev);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(scrollable){
            return super.onTouchEvent(event);
        }else {
            return scrollable && super.onTouchEvent(event);
        }
    }
}

