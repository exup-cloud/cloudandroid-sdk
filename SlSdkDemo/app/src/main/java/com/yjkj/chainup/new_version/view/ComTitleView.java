package com.yjkj.chainup.new_version.view;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjkj.chainup.R;

/**
 * @author Bertking
 * @date 2018/5/14
 */
public class ComTitleView extends RelativeLayout {
    public static final String TAG = ComTitleView.class.getSimpleName();
    private String title;
    private String rightText;
    private ImageView ibBack;


    private TextView tvTitle;
    private TextView tvMenu;
    private int backIcon;
    private boolean showBackIcon;
    private float titleSize;
    private ImageView ivRight;
    private ImageView ivRight2nd;
    private int rightImg;
    private int right2Img;
    private boolean showRightImg;
    private boolean showRight2Img;

    public ComTitleView(Context context) {
        this(context, null);
    }

    public ComTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ComTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //加载自定义的属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ComTitleView);
        backIcon = typedArray.getResourceId(R.styleable.ComTitleView_backIcon, R.drawable.ic_back);
        title = typedArray.getString(R.styleable.ComTitleView_title);
        showBackIcon = typedArray.getBoolean(R.styleable.ComTitleView_showBackIcon, true);
        titleSize = typedArray.getDimension(R.styleable.ComTitleView_titleSize, getResources().getDimension(R.dimen.sp_18));
        rightText = typedArray.getString(R.styleable.ComTitleView_rightText);
        rightImg = typedArray.getResourceId(R.styleable.ComTitleView_rightImg, R.drawable.ic_mail_filter);
        showRightImg = typedArray.getBoolean(R.styleable.ComTitleView_showRightImg, false);

        right2Img =  typedArray.getResourceId(R.styleable.ComTitleView_right2Img, R.drawable.ic_mail_filter);
        showRight2Img = typedArray.getBoolean(R.styleable.ComTitleView_showRight2Img, false);

        typedArray.recycle();
        initView(context);
    }


    public void setShowBackIcon(boolean showBackIcon) {
        this.showBackIcon = showBackIcon;
        ibBack.setVisibility(showBackIcon ? VISIBLE : GONE);
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
        ibBack.setImageResource(backIcon);
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);

    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        tvMenu.setText(rightText);
    }


    private void initView(Context context) {

        View view =
                LayoutInflater.from(context).inflate(R.layout.com_title_view, this, true);


        ibBack = view.findViewById(R.id.ib_back);
        if (ibBack != null) {
            ibBack.setImageResource(backIcon);
            ibBack.setVisibility(showBackIcon ? VISIBLE : GONE);
            ibBack.setOnClickListener(v -> ((Activity) context).finish());
        }


        tvTitle = view.findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.getPaint().setTextSize(titleSize);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            } else {
                tvTitle.setText("");
            }
        }



        tvMenu = view.findViewById(R.id.tv_menu);
        if (tvMenu != null) {
            if (!TextUtils.isEmpty(rightText)) {
                tvMenu.setText(rightText);
            } else {
                tvMenu.setText("");
            }
        }


        ivRight = view.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setVisibility(showRightImg?VISIBLE:GONE);
            ivRight.setImageResource(rightImg);
        }



        ivRight2nd = view.findViewById(R.id.iv_right2nd);
        if (ivRight2nd != null) {
            ivRight2nd.setVisibility(showRight2Img?VISIBLE:GONE);
            ivRight2nd.setImageResource(right2Img);
        }



    }

}

