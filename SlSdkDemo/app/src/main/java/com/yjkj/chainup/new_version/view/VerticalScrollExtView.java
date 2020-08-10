package com.yjkj.chainup.new_version.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VerticalScrollExtView extends NestedScrollView {
    private float mDownPosX = 0.0f;
    private float mDownPosY = 0.0f;

    public VerticalScrollExtView(Context context) {
        super(context);
    }

    public VerticalScrollExtView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public VerticalScrollExtView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mDownPosX = x;
            this.mDownPosY = y;
        } else if (action == 2 && Math.abs(x - this.mDownPosX) > Math.abs(y - this.mDownPosY)) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
