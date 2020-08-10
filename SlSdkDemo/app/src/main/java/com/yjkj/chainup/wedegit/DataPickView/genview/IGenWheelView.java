package com.yjkj.chainup.wedegit.DataPickView.genview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface IGenWheelView {
    View setup(Context context, int position, View convertView, ViewGroup parent, Object data);
}
