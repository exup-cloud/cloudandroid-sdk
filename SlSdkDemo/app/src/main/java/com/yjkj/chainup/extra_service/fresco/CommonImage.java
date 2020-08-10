package com.yjkj.chainup.extra_service.fresco;

import android.content.Context;
import android.util.AttributeSet;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

public class CommonImage extends SimpleDraweeView {//SimpleDraweeView
    public CommonImage(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public CommonImage(Context context) {
        super(context);
    }

    public CommonImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CommonImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
