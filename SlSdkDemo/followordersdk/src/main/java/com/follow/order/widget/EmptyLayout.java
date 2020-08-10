package com.follow.order.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.utils.DensityUtil;
import com.follow.order.widget.shape.RoundTextView;


public class EmptyLayout {
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_CONTENT = 4;

    private Context mContext;
    private FrameLayout mBackgroundView;
    private ViewGroup mLoadingView;
    private ViewGroup mEmptyView;
    private ViewGroup mErrorView;
    private View mContentView;
    private ImageView iv_empty, iv_error;
    private TextView tv_empty, tv_error;
    private RoundTextView btn_empty_retry, btn_error_retry;
    private View empty_temp_view, error_temp_view, loading_temp_view;
    private ProgressBar progress;
    private LayoutInflater mInflater;
    //    private AnimationDrawable loading_anim;
    private boolean mViewsAdded = false;
    private View.OnClickListener mEmptyButtonClickListener;
    private View.OnClickListener mErrorButtonClickListener;

    private int state = STATE_LOADING;
    private int bg_color = 0;
    private int emptyDrawable = 0;
    private int errorDrawable = 0;
    private int tempHeight = 0;
    private float lineSpacingExtra = 0;
    private String loadingStr;
    private String emptyStr;
    private String errorStr;
    private String emptyButtonTitle;
    private String errorButtonTitle;
    private boolean isShowEmptyButton = false;
    private boolean isShowErrorButton = false;
    private boolean isShowTempView = false;
    private boolean isShowContentView = false;
    private boolean isHideLoading = false;
    private int marginTop = 0;
    private View toplayout;

    public void setEmptyHeight(int dp) {
        if (toplayout != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toplayout.getLayoutParams();
            params.topMargin = dp;
            toplayout.setLayoutParams(params);
        }
    }

    public EmptyLayout(Context context, View contentView) {
        this.mContext = context;
        this.mContentView = contentView;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initDefaultValues();
    }

    public void setBackgroundColor(int color) {
        this.bg_color = color;
    }

    public void setLoadingText(String loadingStr) {
        this.loadingStr = loadingStr;
    }

    public void setEmptyText(String emptyStr) {
        this.emptyStr = emptyStr;
    }

    /**
     * 判断在loadding或empty或error情况，是否显示ContentView
     *
     * @param b
     */
    public void setShowContentView(boolean b) {
        this.isShowContentView = b;
    }

    public void setErrorText(String errorStr) {
        this.errorStr = errorStr;
    }

    public void setEmptyDrawable(int drawable) {
        this.emptyDrawable = drawable;
    }

    public void setErrorDrawable(int drawable) {
        this.errorDrawable = drawable;
    }

    public void setLineSpacingExtra(float lineSpace) {
        this.lineSpacingExtra = lineSpace;
    }

    public void setEmptyButtonText(String emptyButtonTitle) {
        this.emptyButtonTitle = emptyButtonTitle;
    }

    public String getEmptyButtonText() {
        return emptyButtonTitle;
    }

    public void setErrorButtonText(String errorButtonTitle) {
        this.errorButtonTitle = errorButtonTitle;
    }

    public String getErrorButtonText() {
        return errorButtonTitle;
    }

    public void setEmptyButtonShow(boolean isShowEmptyButton) {
        this.isShowEmptyButton = isShowEmptyButton;
    }

    public void setErrorButtonShow(boolean isShowErrorButton) {
        this.isShowErrorButton = isShowErrorButton;
    }

    public void setTempViewShow(boolean isShowTempView) {
        this.isShowTempView = isShowTempView;
    }

    public void hideLoadingView(boolean isHideLoading) {
        this.isHideLoading = isHideLoading;
    }

    public void setTempViewShow(boolean isShowTempView, int tempHeight) {
        this.isShowTempView = isShowTempView;
        this.tempHeight = tempHeight;
    }

    public void setEmptyButtonClickListener(View.OnClickListener emptyButtonClickListener) {
        this.mEmptyButtonClickListener = emptyButtonClickListener;
        this.mErrorButtonClickListener = emptyButtonClickListener;
        if (btn_empty_retry != null) {
            btn_empty_retry.setOnClickListener(emptyButtonClickListener);
        }
        if (mEmptyView != null) {
            mEmptyView.setOnClickListener(emptyButtonClickListener);
        }
        if (mErrorView != null) {
            mErrorView.setOnClickListener(emptyButtonClickListener);
        }
        if (iv_empty != null) {
            iv_empty.setOnClickListener(emptyButtonClickListener);
        }
        if (iv_error != null) {
            iv_error.setOnClickListener(emptyButtonClickListener);
        }

    }

    public void setErrorButtonClickListener(View.OnClickListener errorButtonClickListener) {
        this.mErrorButtonClickListener = errorButtonClickListener;
        this.mEmptyButtonClickListener = errorButtonClickListener;
        if (btn_error_retry != null) {
            btn_error_retry.setOnClickListener(errorButtonClickListener);
        }
        if (iv_error != null) {
            iv_error.setOnClickListener(errorButtonClickListener);
        }
        if (tv_error != null) {
            tv_error.setOnClickListener(errorButtonClickListener);
        }
    }

    public void showLoading() {
        this.state = STATE_LOADING;
        changeState();
    }

    public void showEmpty() {
        this.state = STATE_EMPTY;
        changeState();
    }

    public void showError() {
        this.state = STATE_ERROR;
        changeState();
    }

    public void showContent() {
        this.state = STATE_CONTENT;
        changeState();
    }


    public void setMargin(int top) {
        this.marginTop = top;
        if (mBackgroundView != null && mBackgroundView.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBackgroundView.getLayoutParams();
            params.topMargin = top;
            mBackgroundView.setLayoutParams(params);
        }
    }

    /**
     * 初始化基本值
     */
    private void initDefaultValues() {
        bg_color = Color.TRANSPARENT;
        emptyDrawable = FollowOrderSDK.ins().getCustomAttrResId(mContext, R.attr.fo_empty_drawable);
        errorDrawable = 0;
        loadingStr = mContext.getString(R.string.fo_data_loading);
        emptyStr = mContext.getString(R.string.fo_empty_data_text);
        errorStr = mContext.getString(R.string.fo_net_error_text);
        emptyButtonTitle = mContext.getString(R.string.fo_empty_button_retry);
        errorButtonTitle = mContext.getString(R.string.fo_empty_button_retry);
        lineSpacingExtra = DensityUtil.dip2px(5);
        isShowEmptyButton = false;
        isShowErrorButton = false;
        isShowTempView = false;
        isHideLoading = false;
    }

    private void initView() {
        if (mLoadingView == null) {
            mLoadingView = (ViewGroup) mInflater.inflate(R.layout.fo_loading_layout, null);
            progress = mLoadingView.findViewById(R.id.progress);
            loading_temp_view = mLoadingView.findViewById(R.id.loading_temp_view);
        }

        if (mEmptyView == null) {
            mEmptyView = (ViewGroup) mInflater.inflate(R.layout.fo_empty_layout, null);
            iv_empty = mEmptyView.findViewById(R.id.iv_empty);
            tv_empty = mEmptyView.findViewById(R.id.tv_empty_desc);
            btn_empty_retry = mEmptyView.findViewById(R.id.btn_empty_retry);
            empty_temp_view = mEmptyView.findViewById(R.id.empty_temp_view);
            toplayout = mEmptyView.findViewById(R.id.toplayout);

            if (mEmptyButtonClickListener != null) {
                btn_empty_retry.setOnClickListener(mEmptyButtonClickListener);
                mEmptyView.setOnClickListener(mEmptyButtonClickListener);
                iv_empty.setOnClickListener(mEmptyButtonClickListener);
            }
        }

        if (mErrorView == null) {
            mErrorView = (ViewGroup) mInflater.inflate(R.layout.fo_error_layout, null);
            iv_error = mErrorView.findViewById(R.id.iv_error);
            tv_error = mErrorView.findViewById(R.id.tv_error_desc);
            btn_error_retry = mErrorView.findViewById(R.id.btn_empty_retry);
            error_temp_view = mErrorView.findViewById(R.id.error_temp_view);

            if (mErrorButtonClickListener != null) {
                btn_error_retry.setOnClickListener(mErrorButtonClickListener);
                iv_error.setOnClickListener(mErrorButtonClickListener);
                tv_error.setOnClickListener(mErrorButtonClickListener);
                mErrorView.setOnClickListener(mErrorButtonClickListener);
            }
        }

        if (emptyDrawable == 0) {
            iv_empty.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(emptyDrawable);
        }
        tv_empty.setText(emptyStr);
        tv_empty.setLineSpacing(lineSpacingExtra, 1f);
        btn_empty_retry.setText(emptyButtonTitle);

        if (errorDrawable == 0) {
            iv_error.setVisibility(View.GONE);
        } else {
            iv_error.setVisibility(View.VISIBLE);
            iv_error.setImageResource(errorDrawable);
        }
        tv_error.setText(errorStr);
        btn_error_retry.setText(errorButtonTitle);

        if (isShowEmptyButton) {
            btn_empty_retry.setVisibility(View.VISIBLE);
        } else {
            btn_empty_retry.setVisibility(View.GONE);
        }

        if (isShowErrorButton) {
            btn_error_retry.setVisibility(View.VISIBLE);
        } else {
            btn_error_retry.setVisibility(View.GONE);
        }

        if (isShowTempView) {
            loading_temp_view.setVisibility(View.VISIBLE);
            empty_temp_view.setVisibility(View.VISIBLE);
            error_temp_view.setVisibility(View.VISIBLE);
            if (tempHeight > 0) {
                loading_temp_view.getLayoutParams().height = tempHeight;
                empty_temp_view.getLayoutParams().height = tempHeight;
                error_temp_view.getLayoutParams().height = tempHeight;
            }
        } else {
            loading_temp_view.setVisibility(View.GONE);
            empty_temp_view.setVisibility(View.GONE);
            error_temp_view.setVisibility(View.GONE);
        }

        if (isHideLoading) {
            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }

        if (!mViewsAdded) {
            mBackgroundView = new FrameLayout(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.topMargin = marginTop;
            lp.gravity = Gravity.CENTER;
            mBackgroundView.setBackgroundColor(bg_color);
            mBackgroundView.setLayoutParams(lp);

            if (mLoadingView != null) {
                mBackgroundView.addView(mLoadingView);
            }

            if (mEmptyView != null) {
                mBackgroundView.addView(mEmptyView);
            }

            if (mErrorView != null) {
                mBackgroundView.addView(mErrorView);
            }

            mViewsAdded = true;

            if (mContentView != null) {
                ((ViewGroup) mContentView.getParent()).addView(mBackgroundView);
            }

        }

    }

    private void changeState() {
        initView();

        if (mContentView == null)
            return;

        switch (state) {
            case STATE_LOADING:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.VISIBLE);
                    if (isHideLoading) {
                        progress.setVisibility(View.GONE);
                    } else {
                        progress.setVisibility(View.VISIBLE);
                    }
                }

                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                mContentView.setVisibility(isShowContentView ? View.VISIBLE : View.GONE);
                break;

            case STATE_EMPTY:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                mContentView.setVisibility(isShowContentView ? View.VISIBLE : View.GONE);

                break;
            case STATE_ERROR:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.VISIBLE);
                }

                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.VISIBLE);
                }
                mContentView.setVisibility(isShowContentView ? View.VISIBLE : View.GONE);

                break;
            case STATE_CONTENT:
                if (mBackgroundView != null) {
                    mBackgroundView.setVisibility(View.GONE);
                }
                mContentView.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }
    }
}
