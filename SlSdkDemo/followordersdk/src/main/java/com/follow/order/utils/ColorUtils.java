package com.follow.order.utils;

import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;

/**
 * @time: 2020/3/19
 * @author: guodong
 */
public class ColorUtils {

    private static int color_red = FollowOrderSDK.ins().getApplication().getResources().getColor(R.color.fo_red);
    private static int color_green = FollowOrderSDK.ins().getApplication().getResources().getColor(R.color.fo_green);


    public static void setTextColor(TextView tv, int color, int defColor) {
        if (tv == null) {
            return;
        }
        if (color == 0) {
            tv.setTextColor(defColor);
        } else if (color == 1) {
            tv.setTextColor(color_red);
        } else if (color == 2) {
            tv.setTextColor(color_green);
        }
    }
}
