package com.bmtc.sdk.contract.uiLogic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bmtc.sdk.contract.R;
import com.bumptech.glide.Glide;


public class LogicLoadAnimation {
    private ViewGroup mParent;
    private View mRootView;
    private ImageView mImgLoad;
    private Context mContext;

    private void ShowLoadAnimation(LayoutInflater inflater, ViewGroup parent)
    {
        if(mRootView != null || mParent != null) return;

        mParent = parent;
        mRootView = inflater.inflate(R.layout.sl_view_loading, mParent, false);
        parent.addView(mRootView);

        mImgLoad = mRootView.findViewById(R.id.iv_load);
        Glide.with(mContext)
                .load("file:///android_asset/sl_preloading.gif")
                .into(mImgLoad);
    }

    public void setBackgroundColor( int color )
    {
        if (mRootView == null) {
            return;
        }

        mRootView.setBackgroundColor(color);
    }

    public void ShowLoadAnimation(Context activity, ViewGroup parent)
    {
        mContext = activity;
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ShowLoadAnimation(inflater, parent);
    }

    public boolean IsLoadingShow(){
        return mRootView != null;
    }

    public void Layout( View refrence )
    {
        if(mRootView == null || refrence == null) return;

        ViewGroup.MarginLayoutParams src = (ViewGroup.MarginLayoutParams) refrence.getLayoutParams();
        ViewGroup.MarginLayoutParams dst = (ViewGroup.MarginLayoutParams) mRootView.getLayoutParams();
        dst.setMargins(src.leftMargin, src.topMargin, src.rightMargin, src.bottomMargin);
        mRootView.requestLayout();

        mRootView.getLayoutParams().width = refrence.getLayoutParams().width;
        mRootView.getLayoutParams().height = refrence.getLayoutParams().height;
    }

    public void Layout( int cx, int cy )
    {
        if(mRootView == null) return;

        mRootView.getLayoutParams().width = cx;
        mRootView.getLayoutParams().height = cy;
    }

    private int m_nDuration = 500;

    public void HideImmediately()
    {
        ClearResource();

        if (mParent == null || mRootView==null){
            return;
        }

        mParent.removeView(mRootView);
        mParent = null;
        mRootView = null;
    }

    private void ClearResource() {

        if (null != mImgLoad){
            Drawable d = mImgLoad.getDrawable();
            if (d != null) d.setCallback(null);
            mImgLoad.setImageDrawable(null);
            mImgLoad.setBackgroundDrawable(null);
        }
    }

    public void ExitLoadAnimation(){

        ClearResource();
        synchronized (this){
            if (mParent == null || mRootView==null){
                return;
            }
        }

        HideImmediately();


    }
}
