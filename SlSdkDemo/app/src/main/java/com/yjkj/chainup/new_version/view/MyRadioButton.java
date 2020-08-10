package com.yjkj.chainup.new_version.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.yjkj.chainup.R;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-09-29 15:39
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-09-29 15:39
 * @UpdateRemark: 更新说明
 */
@SuppressLint("AppCompatCustomView")
public class MyRadioButton extends RadioButton {

    private Drawable drawable;
    public MyRadioButton(Context context) {
        super(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRadioButton);//获取我们定义的属性
        drawable = typedArray.getDrawable(R.styleable.MyRadioButton_drawableTop);
        drawable.setBounds(0, 0, 52, 52);
        setCompoundDrawables(null, drawable, null, null);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
