package com.yjkj.chainup.new_version.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjkj.chainup.R;
import com.yjkj.chainup.util.GlideUtils;

/**
 * @author Bertking
 * @date 2018/5/18
 * @descriptation 账户界面(公共的ItemView)
 */
public class AccountItemView extends RelativeLayout {

    private int iconId;
    private boolean showRedDot;
    private String title;
    private String statusText;
    private boolean showArrow;
    /******View******/
    private ImageView ivIcon;
    private TextView tvTitle;
    private ImageView ivRedDot;
    private ImageView ivArrowRight;
    private ImageView ivRightIcon;
    private ImageView iv_red_dot_mail;
    private TextView tvState;
    private View vLine;
    private boolean showLine;
    private boolean showIcon;
    private boolean showRightIcon = false;
    private Context context;


    public void setIconId(int iconId) {
        this.iconId = iconId;
        ivIcon.setImageResource(iconId);
    }

    public void setShowRedDot(boolean showRedDot) {
        this.showRedDot = showRedDot;
        ivRedDot.setVisibility(showRedDot ? VISIBLE : GONE);
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
        tvState.setText(statusText);
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        ivArrowRight.setVisibility(showArrow ? VISIBLE : GONE);
    }

    public void setIvRightIcon(String url) {
        GlideUtils.loadImage4OTC(context, url, ivRightIcon);
    }


    public AccountItemView(Context context) {
        this(context, null);
        this.context = context;
    }

    public AccountItemView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        this.context = context;

    }

    public AccountItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccountItemView);

        iconId = typedArray.getResourceId(R.styleable.AccountItemView_icon, 0);

        showRedDot = typedArray.getBoolean(R.styleable.AccountItemView_showRedDot, false);

        title = typedArray.getString(R.styleable.AccountItemView_itemTitle);

        statusText = typedArray.getString(R.styleable.AccountItemView_StatusText);

        showArrow = typedArray.getBoolean(R.styleable.AccountItemView_showArrow, true);

        showLine = typedArray.getBoolean(R.styleable.AccountItemView_showLine, true);

        showIcon = typedArray.getBoolean(R.styleable.AccountItemView_isShowLeftIcon, true);

        showRightIcon = typedArray.getBoolean(R.styleable.AccountItemView_isShowRightIcon, false);

        typedArray.recycle();
        initView(context);

    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_account_item, this, true);
        view.setClickable(true);
        ivIcon = view.findViewById(R.id.iv_icon);
        tvTitle = view.findViewById(R.id.tv_title);
        ivRedDot = view.findViewById(R.id.iv_red_dot);
        ivArrowRight = view.findViewById(R.id.iv_arrow);
        tvState = view.findViewById(R.id.tv_status);
        iv_red_dot_mail = view.findViewById(R.id.iv_red_dot_mail);
        ivRightIcon = view.findViewById(R.id.iv_head);
        vLine = view.findViewById(R.id.v_line);

        if (ivIcon != null) {
            ivIcon.setBackgroundResource(iconId);
            ivIcon.setVisibility(showIcon ? VISIBLE : GONE);
        }
        if (ivRedDot != null) {
            ivRedDot.setVisibility(showRedDot ? VISIBLE : GONE);
        }
        if (tvTitle != null) {
            tvTitle.setText(title);
        }

        if (tvState != null) {
            tvState.setText(statusText);
        }

//        tvState.setVisibility(!showArrow ? VISIBLE : GONE);

        if (ivArrowRight != null) {
            ivArrowRight.setVisibility(showArrow ? VISIBLE : GONE);
        }

        if (vLine != null) {
            vLine.setVisibility(showLine ? VISIBLE : GONE);
        }

        if (showRightIcon) {
            tvState.setVisibility(GONE);
            ivRightIcon.setVisibility(VISIBLE);
        }

    }

    public void showMailRed(boolean status) {
        if (status) {
            iv_red_dot_mail.setVisibility(View.VISIBLE);
        } else {
            iv_red_dot_mail.setVisibility(View.GONE);
        }

    }

}

