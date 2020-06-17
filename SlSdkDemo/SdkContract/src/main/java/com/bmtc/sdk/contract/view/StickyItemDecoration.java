package com.bmtc.sdk.contract.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.bmtc.sdk.contract.utils.UtilSystem;
import com.contract.sdk.ContractSDKAgent;


/**
 * Created by zhoujing on 2018/12/5.
 */

public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    private ISticky mISticky;
    //矩形高度
    private int mRectHeight;
    //文字TextSize
    private int mTextPaintSize;
    private Paint mTxtPaint;
    private Paint mRectPaint;
    //分割线画笔
    private Paint mDividerPaint;

    public StickyItemDecoration(Context context, ISticky iSticky) {
        mISticky=iSticky;
        mRectHeight= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,
                context.getResources().getDisplayMetrics());

        mTextPaintSize=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,
                context.getResources().getDisplayMetrics());
        mTxtPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mTxtPaint.setColor(Color.WHITE);
        mTxtPaint.setTextSize(mTextPaintSize);

        mRectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setColor(Color.parseColor("#181a22"));

        mDividerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setStyle(Paint.Style.FILL);
        mDividerPaint.setColor(Color.parseColor("#10131c"));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount=parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view=parent.getChildAt(i);
            int left=parent.getPaddingLeft();
            int right=parent.getWidth()-parent.getPaddingRight();
            int top=view.getTop()-1;
            int bottom=view.getTop();
            //Item分割线
            c.drawRect(left,top,right,bottom,mDividerPaint);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int childCount=parent.getChildCount();
        int itemCount=state.getItemCount();
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();
        String preGroupTitle;
        String groupTitle="";
        for (int i = 0; i < childCount; i++) {
            View child=parent.getChildAt(i);
            int pos=parent.getChildLayoutPosition(child);
            preGroupTitle=groupTitle;
            groupTitle=mISticky.getGroupTitle(pos);
            //如果当前分组名和之前分组名一样，忽略此次循环
            if (groupTitle.equals(preGroupTitle)) {
                continue;
            }

            //文字的基线，保证显示完全
            int textBaseLine=Math.max(mRectHeight,child.getTop());

            //分组标题
            String title=mISticky.getGroupTitle(pos);

            int viewBottom=child.getBottom();
            //加入限定 防止数组越界
            if (pos + 1 < itemCount) {
                String nextGroupTitle=mISticky.getGroupTitle(pos+1);
                //当分组不一样  并且改组要向上移动时候
                if (!nextGroupTitle.equals(groupTitle) && viewBottom < textBaseLine) {
                    //将上一个往上移动
                    textBaseLine = viewBottom;
                }
            }
            //绘制边框
            c.drawRect(left, textBaseLine - mRectHeight, right, textBaseLine, mRectPaint);

            //绘制文字并且实现文字居中
            int value= (int) Math.abs(mTxtPaint.getFontMetrics().descent
                    + mTxtPaint.getFontMetrics().ascent);
            c.drawText(title, left + UtilSystem.dip2px(ContractSDKAgent.INSTANCE.getContext(), 15),
                    textBaseLine-(mRectHeight-value)/2,
                    mTxtPaint);

            c.drawRect(left,textBaseLine-UtilSystem.dip2px(ContractSDKAgent.INSTANCE.getContext(), 1),right,textBaseLine,mDividerPaint);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildLayoutPosition(view);
        if (mISticky.isFirstPosition(pos)) {
            outRect.top = mRectHeight;
            outRect.bottom = 1;
        }else {
            outRect.bottom = 1;
        }
    }
}