package com.bmtc.sdk.contract.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by zj on 2017/10/18.
 */

public class BaseFragment extends Fragment{

    private boolean mHidden = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
       // MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
       // MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mHidden = !isVisibleToUser;
    }

    public boolean getHidden() {
        BaseFragment fragment = (BaseFragment) getParentFragment();
        if (fragment != null && fragment.getHidden()) {
            return true;
        }

        return mHidden;
    }

}
