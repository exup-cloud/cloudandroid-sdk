package com.yjkj.chainup.wedegit.circleviewpager;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.yjkj.chainup.R;

import java.util.ArrayList;
import java.util.List;

public class CircleViewPager extends FrameLayout {
    private ViewPager mViewPager;
    private CirclePagerAdapter mAdapter;

    //图片对应的圆点合集
    private List<ImageView> mDotList;
    //圆点切换时间间隔
    private int interval;

    //选中时的圆点资源
    private int mSelectDotRes;

    //未选中是的圆点资源
    private int mNormalDotRes;

    //圆点半径
    private float mDotWidth;

    //是否循环
    private boolean isLoop;

    private LinearLayout linearDot;

    private OnPageClickListener mOnPageClickListener;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            mHandler.postDelayed(this, interval);
        }
    };

    public CircleViewPager(@NonNull Context context) {
        this(context, null);
    }

    public CircleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleViewPager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    //初始化
    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleViewPager);
            mSelectDotRes = typedArray.getResourceId(R.styleable.CircleViewPager_selectPointRes, R.drawable.ic_select_dot);
            mNormalDotRes = typedArray.getResourceId(R.styleable.CircleViewPager_selectPointRes, R.drawable.ic_normal_dot);
            mDotWidth = typedArray.getDimension(R.styleable.CircleViewPager_dotWidth, 20);
            interval = typedArray.getInteger(R.styleable.CircleViewPager_interval, 3000);
            typedArray.recycle();
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_pager_layout, this);
        linearDot = view.findViewById(R.id.linear_dot);
        mViewPager = view.findViewById(R.id.view_pager);
        mDotList = new ArrayList<>();
    }

    //初始化viewpager
    private void setViewPagerAdapter() {
        mAdapter = new CirclePagerAdapter();
        mViewPager.setAdapter(mAdapter);
        setTouchListener();
        setPageChangeListener();
    }


    public void setImgUrl(List<String> list) {
        if (list == null) return;
        List<String> mList = new ArrayList<>();
        mList.addAll(list);
        initDot(list.size());
        //防止图片数量等于2时出现空白页
        if (list.size() == 2) {
            mList.addAll(list);
        }
        List<ImageView> imgList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {

        }
        for (int i = 0; i < mList.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getContext()).load(mList.get(i)).into(imageView);
            if (mOnPageClickListener != null) {
                int finalI = i;
                imageView.setOnClickListener(v ->
                        mOnPageClickListener.onPageClick(finalI));
            }
            imgList.add(imageView);
        }
        setViewPagerAdapter();
        mAdapter.setImage(imgList);
        mAdapter.notifyDataSetChanged();
        if (list.size() > 1) {
            //防止刷新数据后，还显示以前的图片，重新setAdapter
            startCircleViewPager();
        }
    }


    //设置图片
    public void setImage(List<ImageView> list) {
        if (list == null) return;
        List<ImageView> mList = new ArrayList<>();
        mList.addAll(list);
        setViewPagerAdapter();
        initDot(list.size());
        mAdapter.setImage(mList);
        mAdapter.notifyDataSetChanged();
        if (list.size() > 1) {
            //防止刷新数据后，还显示以前的图片，重新setAdapter
            startCircleViewPager();
        }
    }

    //初始化指示器
    private void initDot(int count) {
        linearDot.removeAllViews();
        mDotList.clear();
        for (int i = 0; i < count; i++) {
            ImageView imgDot = new ImageView(getContext());
            imgDot.setImageResource(mNormalDotRes);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) mDotWidth, (int) mDotWidth);
            if (i == count - 1)
                params.setMargins(0, 0
                        , 0, 0);
            else
                params.setMargins(0, 0
                        , (int) (mDotWidth / 1.5), 0);
            linearDot.addView(imgDot, params);
            mDotList.add(imgDot);
        }
        selectionDot(0);
    }

    //选中指示器
    private void selectionDot(int index) {
        if (index > (linearDot.getChildCount() - 1)) return;
        for (int i = 0; i < linearDot.getChildCount(); i++) {
            int res = i == index ? mSelectDotRes : mNormalDotRes;
            ((ImageView) linearDot.getChildAt(i)).setImageResource(res);
        }
    }

    private void setPageChangeListener() {
        mViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        selectionDot(position % mDotList.size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
    }

    private void setTouchListener() {
        mViewPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    requestDisallowInterceptTouchEvent(true);
                case MotionEvent.ACTION_MOVE:
                    stopCircleViewPager();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    startCircleViewPager();
                    requestDisallowInterceptTouchEvent(false);
                default:
                    break;
            }
            return false;
        });
    }

    //开始轮播
    private void startCircleViewPager() {
        if (isLoop) return;
        mHandler.postDelayed(mRunnable, interval);
        isLoop = true;
    }

    //停止轮播
    public void stopCircleViewPager() {
        if (!isLoop) return;
        mHandler.removeCallbacks(mRunnable);
        isLoop = false;
    }


//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        startCircleViewPager();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        stopCircleViewPager();
//    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setmSelectDotRes(int mSelectDotRes) {
        this.mSelectDotRes = mSelectDotRes;
    }

    public void setmNormalDotRes(int mNormalDotRes) {
        this.mNormalDotRes = mNormalDotRes;
    }

    public void setmDotWidth(float mDotWidth) {
        this.mDotWidth = mDotWidth;
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }

    public void setOnPageClickListener(OnPageClickListener mOnPageClickListener) {
        this.mOnPageClickListener = mOnPageClickListener;
    }
}
