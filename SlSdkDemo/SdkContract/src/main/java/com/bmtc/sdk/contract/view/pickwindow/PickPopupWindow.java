package com.bmtc.sdk.contract.view.pickwindow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;

import java.util.List;

/**
 * Created by chenxi-pc on 2016/6/3.
 */
public class PickPopupWindow extends PopupWindow implements PickerView.onSelectListener {

    public interface PickListener{
        void onPickData(String text, Object data);
    }
    private PickListener mPickListener;
    private Activity    mParentActivity;
    private List<Pair<String, Object>> mSelectList;


    public PickPopupWindow(Activity activity, List<Pair<String, Object>> selectList, Integer defaultIndex, PickListener pickListener) {
        mParentActivity = activity;
        mSelectList = selectList;
        mPickListener = pickListener;

        initView(defaultIndex);
    }

    private void initView(Integer defaultIndex) {
        LayoutInflater mInflater = LayoutInflater.from(mParentActivity);
        View view = mInflater.inflate(R.layout.sl_view_pick_popwin, null, true);
        this.setContentView(view);// 设置显示的视图
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置窗体的宽度
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置窗体的高度
        this.setFocusable(true);// 设置窗体可点击

        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);


        final PickerView pickerView = view.findViewById(R.id.pv_gender);
        if (pickerView != null) {
            pickerView.setData(mSelectList);
            pickerView.setSelected(defaultIndex);
            pickerView.setOnSelectListener(this);
        } else {
            return;
        }


        TextView btnClose = view.findViewById(R.id.tv_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        TextView btnComplete = view.findViewById(R.id.tv_complete);
        if (btnComplete != null) {
            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPickListener != null) {

                        mPickListener.onPickData(pickerView.getCurrentText(), pickerView.getCurrentData());
                    }
                    dismiss();
                }
            });
        }

    }

    @Override
    public void onSelect(String text, Object data) {
    }


}
