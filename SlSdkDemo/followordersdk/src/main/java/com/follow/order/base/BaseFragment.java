package com.follow.order.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.follow.order.FollowOrderSDK;
import com.follow.order.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseFragment extends Fragment implements View.OnClickListener, OnFragmentVisibilityChangedListener, View.OnAttachStateChangeListener {


    protected BaseActivity activity;
    protected Application app;
    protected boolean isPrepare;
    protected Bundle mSavedInstanceState;
    protected View baseView;


    /**
     * ParentActivity是否可见
     */
    private boolean mParentActivityVisible = false;
    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    private boolean mVisible = false;

    private BaseFragment mParentFragment;
    private List<OnFragmentVisibilityChangedListener> mListenerList;

    public void setOnVisibilityChangedListener(OnFragmentVisibilityChangedListener listener) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) getActivity();

        final Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof MVPBaseFragment) {
            mParentFragment = ((BaseFragment) parentFragment);
            mParentFragment.setOnVisibilityChangedListener(this);
        }
        checkVisibility(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
        if (mParentFragment != null) {
            mParentFragment.setOnVisibilityChangedListener(null);
        }
        super.onDetach();
        checkVisibility(false);
        mParentFragment = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        onActivityVisibilityChanged(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        onActivityVisibilityChanged(false);
    }

    /**
     * ParentActivity可见性改变
     */
    protected void onActivityVisibilityChanged(boolean visible) {
        mParentActivityVisible = visible;
        checkVisibility(visible);
    }

    /**
     * ParentFragment可见性改变
     */
    @Override
    public void onFragmentVisibilityChanged(boolean visible) {
        checkVisibility(visible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        app = FollowOrderSDK.ins().getApplication();
        this.mSavedInstanceState = savedInstanceState;
        if(null == baseView) {
            baseView = getBaseView();
            initView();
        }
        isPrepare = true;

        return baseView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.addOnAttachStateChangeListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        checkVisibility(hidden);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != baseView) {
            ((ViewGroup) baseView.getParent()).removeView(baseView);
        }
    }

    public abstract View getBaseView();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    protected boolean hasInit = false;

    /**
     * 相当于onResume,由于fragment加viewpager生命周期比较复杂所以加入了以下两个方法
     */
    /**
     * 每次可见都执行的方法
     */
    protected void initEveryVisiableData() {
//        if (!hasInit){
////            initView();
////            initListener();
////            initData();
//            hasInit = true;
//        }

        LogUtil.e(this.getClass().getSimpleName() + "initEveryVisiableData");
    }

    /**
     * 每次不可见执行的方法
     */
    protected void initEveryUnVisiableData() {

        LogUtil.e(this.getClass().getSimpleName() + "initEveryUnVisiableData");

    }

    /**
     * Tab切换时会回调此方法。对于没有Tab的页面，{@link Fragment#getUserVisibleHint()}默认为true。
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisibility(isVisibleToUser);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        checkVisibility(true);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        v.removeOnAttachStateChangeListener(this);
        checkVisibility(false);
    }

    /**
     * 检查可见性是否变化
     *
     * @param expected 可见性期望的值。只有当前值和expected不同，才需要做判断
     */
    private void checkVisibility(boolean expected) {
//        if (expected == mVisible) return;
        final boolean parentVisible = mParentFragment == null ? mParentActivityVisible : mParentFragment.isFragmentVisible();
        final boolean superVisible = super.isVisible();
        final boolean hintVisible = getUserVisibleHint();
        final boolean visible = parentVisible && superVisible && hintVisible && !isHidden();
        if (visible != mVisible) {
            mVisible = visible;
            onVisibilityChanged(mVisible);
        }
    }

    public View inflate(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return inflater.inflate(layoutId, null);
    }

    /**
     * 可见性改变
     */
    public void onVisibilityChanged(boolean visible) {
        if (mListenerList != null) {
            for (OnFragmentVisibilityChangedListener mListener : mListenerList) {
                if (mListener != null) {
                    mListener.onFragmentVisibilityChanged(visible);
                }
            }
        }

        if (visible) {
            initEveryVisiableData();
        } else {
            initEveryUnVisiableData();
        }
    }

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    public boolean isFragmentVisible() {
        return mVisible;
    }


    @Override
    public void onClick(View v) {

    }

}
