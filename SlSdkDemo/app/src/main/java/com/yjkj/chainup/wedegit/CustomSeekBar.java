package com.yjkj.chainup.wedegit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yjkj.chainup.R;


public class CustomSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    private Paint paintWhite;
    private Paint paintBlue;
    private float dotRadius = 10f;

    private RectF rectF = new RectF(10, 10, 10, 10);

    private boolean isCanScroll = true;

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public CustomSeekBar(Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setShader(new Shader());
        paintWhite.setStyle(Paint.Style.FILL);
        paintWhite.setAlpha(255);
        paintWhite.setAntiAlias(true);
        paintBlue = new Paint();
        paintBlue.setColor(getContext().getResources().getColor(R.color.colorAccent));
        paintBlue.setShader(new Shader());
        paintBlue.setStyle(Paint.Style.FILL);
        paintBlue.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(getPaddingStart(), getHeight() / 2, dotRadius, paintBlue);

        if (getProgress() >= 25) {
            canvas.drawCircle(getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4, getHeight() / 2, dotRadius, paintBlue);
        } else {
            canvas.drawCircle(getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4, getHeight() / 2, dotRadius, paintWhite);
        }
        if (getProgress() >= 50) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, dotRadius, paintBlue);
        } else {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, dotRadius, paintWhite);
        }

        if (getProgress() >= 75) {
            canvas.drawCircle(getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4 * 3, getHeight() / 2, dotRadius, paintBlue);
        } else {
            canvas.drawCircle(getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4 * 3, getHeight() / 2, dotRadius, paintWhite);
        }

        if (getProgress() == 100) {
            canvas.drawCircle(getWidth() - getPaddingEnd(), getHeight() / 2, dotRadius, paintBlue);
        } else {
            canvas.drawCircle(getWidth() - getPaddingEnd(), getHeight() / 2, dotRadius, paintWhite);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanScroll) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            if (Math.abs(x - getPaddingStart()) < 10) {
                setProgress(0);
            }
            if (Math.abs(x - (getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4)) < 10) {
                setProgress(25);
            }
            if (Math.abs(x - getWidth() / 2) < 10) {
                setProgress(50);
            }
            if (Math.abs(x - (getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 4 * 3)) < 10) {
                setProgress(75);
            }
            if (Math.abs(x - (getWidth() - getPaddingEnd())) < 10) {
                setProgress(100);
            }
        }
        return super.onTouchEvent(event);
    }
}
