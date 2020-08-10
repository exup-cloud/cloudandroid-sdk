package com.yjkj.chainup.wedegit;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.yjkj.chainup.R;

import java.lang.reflect.Field;

public class MyCustomSeekBar extends FrameLayout {

    private SeekBar mSeekBar;

    private Paint paintWhite;
    private Paint paintCustom;
    private int dotRadius = 20;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private ImageView dot4;
    private ImageView dot5;

    public int getProgress() {
        if (mSeekBar != null) {
            return mSeekBar.getProgress();
        }
        return progress;
    }

    public void setProgress(int progress) {
        if (mSeekBar != null)
            mSeekBar.setProgress(progress);
    }

    private int progress;

    private SeekBar.OnSeekBarChangeListener mListener;

    public SeekBar.OnSeekBarChangeListener getListener() {
        return mListener;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener mListener) {
        this.mListener = mListener;
    }

    private boolean isCanScroll = true;

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }


    public MyCustomSeekBar(Context context) {
        super(context);
        init();
    }

    public MyCustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setShader(new Shader());
        paintWhite.setStyle(Paint.Style.FILL);
        paintWhite.setAntiAlias(true);

        paintCustom = new Paint();
        paintCustom.setColor(ContextCompat.getColor(getContext(),R.color.main_color));
        paintCustom.setShader(new Shader());
        paintCustom.setStyle(Paint.Style.FILL);
        paintCustom.setAntiAlias(true);

        mSeekBar = new SeekBar(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mSeekBar.setLayoutParams(params);
        initDot();
        addView(mSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dot1.setSelected(true);
                dot2.setSelected(progress >= 25);
                dot3.setSelected(progress >= 50);
                dot4.setSelected(progress >= 75);
                dot5.setSelected(progress == 100);
                if (mListener != null) {
                    mListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    private void initDot() {
        dot1 = new ImageView(getContext());
        LayoutParams params = new LayoutParams(dotRadius, dotRadius);
        dot1.setLayoutParams(params);
        dot1.setImageResource(R.drawable.bg_dot_seek_bar);
        addView(dot1);
        dot2 = new ImageView(getContext());
        dot2.setLayoutParams(params);
        dot2.setImageResource(R.drawable.bg_dot_seek_bar);
        addView(dot2);
        dot3 = new ImageView(getContext());
        dot3.setLayoutParams(params);
        dot3.setImageResource(R.drawable.bg_dot_seek_bar);
        addView(dot3);
        dot4 = new ImageView(getContext());
        dot4.setLayoutParams(params);
        dot4.setImageResource(R.drawable.bg_dot_seek_bar);
        addView(dot4);
        dot5 = new ImageView(getContext());
        dot5.setLayoutParams(params);
        dot5.setImageResource(R.drawable.bg_dot_seek_bar);
        addView(dot5);
        dot1.setOnClickListener(v ->
                mSeekBar.setProgress(0));
        dot2.setOnClickListener(v -> mSeekBar.setProgress(25));
        dot3.setOnClickListener(v -> mSeekBar.setProgress(50));
        dot4.setOnClickListener(v -> mSeekBar.setProgress(75));
        dot5.setOnClickListener(v -> mSeekBar.setProgress(100));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int start = mSeekBar.getLeft() + mSeekBar.getPaddingLeft();
        int end = mSeekBar.getRight() - mSeekBar.getPaddingEnd();
        int seekBarWidth = mSeekBar.getWidth() - mSeekBar.getPaddingStart() - mSeekBar.getPaddingEnd();
        for (int i = 0; i < getChildCount(); i++) {
            int width = getChildAt(i).getWidth();
            if (i == 0) {
                getChildAt(i).layout(start - width / 2, (getHeight() - getChildAt(i).getHeight()) / 2
                        , start + width / 2, (getHeight() + getChildAt(i).getHeight()) / 2);
            }
            if (i == 1) {
                getChildAt(i).layout(start + seekBarWidth / 4 - width / 2, (getHeight() - getChildAt(i).getHeight()) / 2
                        , start + seekBarWidth / 4 + width / 2, (getHeight() + getChildAt(i).getHeight()) / 2);
            }
            if (i == 2) {
                getChildAt(i).layout(getWidth() / 2 - width / 2, (getHeight() - getChildAt(i).getHeight()) / 2,
                        getWidth() / 2 + width / 2, (getHeight() + getChildAt(i).getHeight()) / 2);
            }
            if (i == 3) {
                getChildAt(i).layout(start + seekBarWidth / 4 * 3 - width / 2, (getHeight() - getChildAt(i).getHeight()) / 2
                        , start + seekBarWidth / 4 * 3 + width / 2, (getHeight() + getChildAt(i).getHeight()) / 2);
            }
            if (i == 4) {
                getChildAt(i).layout(end - width / 2, (getHeight() - getChildAt(i).getHeight()) / 2,
                        end + width / 2, (getHeight() + getChildAt(i).getHeight()) / 2);
            }
            if (i == 5) {
                getChildAt(i).layout(getPaddingStart(), (getHeight() - mSeekBar.getHeight()) / 2, getWidth() - getPaddingEnd(),
                        (getHeight() + mSeekBar.getHeight()) / 2);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanScroll) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            if (Math.abs(x - getPaddingStart()) < 10) {
                mSeekBar.setProgress(0);
            }
            if (Math.abs(x - (getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4)) < 10) {
                mSeekBar.setProgress(25);
            }
            if (Math.abs(x - getWidth() / 2) < 10) {
                mSeekBar.setProgress(50);
            }
            if (Math.abs(x - (getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4 * 3)) < 10) {
                mSeekBar.setProgress(75);
            }
            if (Math.abs(x - (getWidth() - getPaddingEnd())) < 10) {
                mSeekBar.setProgress(100);
            }
        }
        return super.onTouchEvent(event);
    }

    public void setSeekBarDrawable(int color) {
//        if (mSeekBar != null) {
//            mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.bg_seekbar_buy));
//            setSeekBarMaxHeight();
//            mSeekBar.invalidate();
//        }
    }


    private void setSeekBarMaxHeight() {
        try {
            Class<?> class1 = Class.forName("android.widget.ProgressBar");
            Field field = class1.getDeclaredField("mMaxHeight");
            Log.i("field", field.get(mSeekBar) + "");
            field.set(mSeekBar, 10);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
