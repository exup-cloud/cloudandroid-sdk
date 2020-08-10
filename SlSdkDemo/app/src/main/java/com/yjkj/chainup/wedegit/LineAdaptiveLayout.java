package com.yjkj.chainup.wedegit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjkj.chainup.R;
import com.yjkj.chainup.bean.OTCInitInfoBean;
import com.yjkj.chainup.new_version.bean.CashFlowSceneBean;
import com.yjkj.chainup.util.LineSelectOnclickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lianshangljl
 * @Date 2018/11/19-2:13 PM
 * @Email buptjinlong@163.com
 * @description 新资金流水
 */
public class LineAdaptiveLayout extends ViewGroup {
    /**
     * 所有标签
     */
    List<Object> lables;
    /**
     * 选中标签
     */
    private List<CashFlowSceneBean.Scene> lableSelected = new ArrayList<>();
    private List<String> lableForCommissionedSelected = new ArrayList<>();
    private List<View> selectView = new ArrayList<>();
    //自定义属性
    private int LEFT_RIGHT_SPACE; //dip
    private int ROW_SPACE;
    private boolean aLineShow = true;

    public LineAdaptiveLayout(Context context) {
        this(context, null);
    }

    public LineAdaptiveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineAdaptiveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineBreakLayout);
        LEFT_RIGHT_SPACE = ta.getDimensionPixelSize(R.styleable.LineBreakLayout_leftAndRightSpace, DisplayUtils.dip2px(context, 23));
        ROW_SPACE = ta.getDimensionPixelSize(R.styleable.LineBreakLayout_rowSpace, DisplayUtils.dip2px(context, 15));
        ta.recycle(); //回收
        // ROW_SPACE=20   LEFT_RIGHT_SPACE=40
    }

    private boolean selectstatus = false;

    /**
     * 添加标签
     *
     * @param lables      标签集合
     * @param add         是否追加
     * @param aLineShow   是否单行显示
     * @param isReset     是否重置  如果是重置默认第一个是选中状态
     * @param isClickable 是否可点击
     */
    public void setLables(ArrayList<CashFlowSceneBean.Scene> lables, boolean add, final Boolean aLineShow, boolean isReset, boolean isClickable) {
        this.aLineShow = aLineShow;
        if (this.lables == null) {
            this.lables = new ArrayList<>();
        }
        if (add) {
            this.lables.addAll(lables);
        } else {
            this.lables.clear();
            this.lables.addAll(lables);
            removeAllViews();
        }
        if (lables != null && lables.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (final CashFlowSceneBean.Scene lable : lables) {
                //获取标签布局
                View tv = inflater.inflate(R.layout.item_new_screening_label, null);
                selectView.add(tv);
                //这里放入对应的标签字段
                TextView textView = tv.findViewById(R.id.tv_parent_content);
                textView.setText(lable.getKeyText());
                //设置选中效果
                if (isReset) {
                    if (lables.get(0).equals(lable)) {
                        //选中
                        selectstatus = true;
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        lableForCommissionedSelected.add(lable.getKeyText());
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                } else {
                    if (lableForCommissionedSelected.contains(lable.getKeyText())) {
                        //选中
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        selectstatus = true;
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(0);
                        selectstatus = false;
                    }
                }
                if (isClickable) {
                    tv.setEnabled(true);
                } else {
                    tv.setEnabled(false);
                }

                //点击标签后，重置选中效果
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lineSelectOnclickListener != null) {
                            lineSelectOnclickListener.selectMsgIndex(lable.getKey());
                            if (selectstatus) {
                                lineSelectOnclickListener.sendOnclickMsg();
                            }
                        }
                        if (isClickable) {
                            for (View checkBox : selectView) {
                                ImageView imageView = checkBox.findViewById(R.id.cut_view);
                                RelativeLayout frameLayout = checkBox.findViewById(R.id.ll_layout);
                                if (checkBox.equals(tv)) {
                                    if (imageView.getVisibility() == View.VISIBLE) {
                                        frameLayout.setBackgroundResource(0);
                                        imageView.setVisibility(GONE);
                                    } else {
                                        imageView.setVisibility(VISIBLE);
                                        frameLayout.setBackgroundResource(R.drawable.bg_new_select_style);
                                    }
                                } else {
                                    imageView.setVisibility(GONE);
                                    frameLayout.setBackgroundResource(0);
                                }
                            }
                            lableSelected.clear();
                            lableSelected.add(lable);
                        }

                    }
                });

                //将标签添加到容器中
                addView(tv);
            }
        }
    }


    /**
     * 添加标签 for 历史委托
     *
     * @param lables      标签集合
     * @param add         是否追加
     * @param aLineShow   是否单行显示
     * @param isReset     是否重置  如果是重置默认第一个是选中状态
     * @param isClickable 是否可点击
     */
    public void setLablesForCommissioned(ArrayList<String> lables, boolean add, final Boolean aLineShow, boolean isReset, boolean isClickable) {
        this.aLineShow = aLineShow;
        if (this.lables == null) {
            this.lables = new ArrayList<>();
        }
        if (add) {
            this.lables.addAll(lables);
        } else {
            this.lables.clear();
            this.lables.addAll(lables);
            removeAllViews();
        }
        if (lables != null && lables.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (final String lable : lables) {
                //获取标签布局
                View tv = inflater.inflate(R.layout.item_new_screening_label, null);
                selectView.add(tv);
                //这里放入对应的标签字段
                TextView textView = tv.findViewById(R.id.tv_parent_content);
                textView.setText(lable);
                //设置选中效果
                if (isReset) {
                    if (lables.get(0).equals(lable)) {
                        //选中
                        selectstatus = true;
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        lableForCommissionedSelected.add(lable);
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                } else {
                    if (lableForCommissionedSelected.contains(lable)) {
                        //选中
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        selectstatus = true;
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                }
                if (isClickable) {
                    tv.setEnabled(true);
                } else {
                    tv.setEnabled(false);
                }

                //点击标签后，重置选中效果
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lineSelectOnclickListener != null) {
                            lineSelectOnclickListener.selectMsgIndex(lable);
                            if (selectstatus) {
                                lineSelectOnclickListener.sendOnclickMsg();
                            }
                        }
                        if (isClickable) {
                            for (View checkBox : selectView) {
                                ImageView imageView = checkBox.findViewById(R.id.cut_view);
                                RelativeLayout frameLayout = checkBox.findViewById(R.id.ll_layout);
                                if (checkBox.equals(tv)) {
                                    if (imageView.getVisibility() == View.VISIBLE) {
                                        frameLayout.setBackgroundResource(0);
                                        imageView.setVisibility(GONE);
                                    } else {
                                        imageView.setVisibility(VISIBLE);
                                        frameLayout.setBackgroundResource(R.drawable.bg_new_select_style);
                                    }
                                } else {
                                    imageView.setVisibility(GONE);
                                    frameLayout.setBackgroundResource(0);
                                }
                            }
                            lableForCommissionedSelected.clear();
                            lableForCommissionedSelected.add(lable);
                        }

                    }
                });

                //将标签添加到容器中
                addView(tv);
            }
        }
    }


    /**
     * 添加标签 for 法币类型
     *
     * @param lables      标签集合
     * @param add         是否追加
     * @param aLineShow   是否单行显示
     * @param isReset     是否重置  如果是重置默认第一个是选中状态
     * @param isClickable 是否可点击
     */
    public void setLablesForFiatType(ArrayList<OTCInitInfoBean.Paycoins> lables, boolean add, final Boolean aLineShow, boolean isReset, boolean isClickable) {
        this.aLineShow = aLineShow;
        if (this.lables == null) {
            this.lables = new ArrayList<>();
        }
        if (add) {
            this.lables.addAll(lables);
        } else {
            this.lables.clear();
            this.lables.addAll(lables);
            removeAllViews();
        }
        if (lables != null && lables.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (final OTCInitInfoBean.Paycoins lable : lables) {
                //获取标签布局
                View tv = inflater.inflate(R.layout.item_new_screening_label, null);
                selectView.add(tv);
                //这里放入对应的标签字段
                TextView textView = tv.findViewById(R.id.tv_parent_content);
                textView.setText(lable.getTitle());
                //设置选中效果
                if (isReset) {
                    if (lables.get(0).equals(lable)) {
                        //选中
                        selectstatus = true;
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        lableForCommissionedSelected.add(lable.getTitle());
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                } else {
                    if (lableForCommissionedSelected.contains(lable.getTitle())) {
                        //选中
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        selectstatus = true;
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                }
                if (isClickable) {
                    tv.setEnabled(true);
                } else {
                    tv.setEnabled(false);
                }

                //点击标签后，重置选中效果
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lineSelectOnclickListener != null) {
                            lineSelectOnclickListener.selectMsgIndex(lable.getKey());
                            if (selectstatus) {
                                lineSelectOnclickListener.sendOnclickMsg();
                            }
                        }
                        if (isClickable) {
                            for (View checkBox : selectView) {
                                ImageView imageView = checkBox.findViewById(R.id.cut_view);
                                RelativeLayout frameLayout = checkBox.findViewById(R.id.ll_layout);
                                if (checkBox.equals(tv)) {
                                    if (imageView.getVisibility() == View.VISIBLE) {
                                        frameLayout.setBackgroundResource(0);
                                        imageView.setVisibility(GONE);
                                    } else {
                                        imageView.setVisibility(VISIBLE);
                                        frameLayout.setBackgroundResource(R.drawable.bg_new_select_style);
                                    }
                                } else {
                                    imageView.setVisibility(GONE);
                                    frameLayout.setBackgroundResource(0);
                                }
                            }
                            lableForCommissionedSelected.clear();
                            lableForCommissionedSelected.add(lable.getTitle());
                        }

                    }
                });

                //将标签添加到容器中
                addView(tv);
            }
        }
    }

    /**
     * 添加标签 for 支付方式
     *
     * @param lables      标签集合
     * @param add         是否追加
     * @param aLineShow   是否单行显示
     * @param isReset     是否重置  如果是重置默认第一个是选中状态
     * @param isClickable 是否可点击
     */
    public void setLablesPaymentType(ArrayList<OTCInitInfoBean.PaymentBean> lables, boolean add, final Boolean aLineShow, boolean isReset, boolean isClickable) {
        this.aLineShow = aLineShow;
        if (this.lables == null) {
            this.lables = new ArrayList<>();
        }
        if (add) {
            this.lables.addAll(lables);
        } else {
            this.lables.clear();
            this.lables.addAll(lables);
            removeAllViews();
        }
        if (lables != null && lables.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (final OTCInitInfoBean.PaymentBean lable : lables) {
                //获取标签布局
                View tv = inflater.inflate(R.layout.item_new_screening_label, null);
                selectView.add(tv);
                //这里放入对应的标签字段
                TextView textView = tv.findViewById(R.id.tv_parent_content);
                textView.setText(lable.getTitle());
                //设置选中效果
                if (isReset) {
                    if (lables.get(0).equals(lable)) {
                        //选中
                        selectstatus = true;
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        lableForCommissionedSelected.add(lable.getTitle());
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);
                        selectstatus = false;
                    }
                } else {
                    if (lableForCommissionedSelected.contains(lable.getTitle())) {
                        //选中
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        selectstatus = true;
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);

                        selectstatus = false;
                    }
                }
                if (isClickable) {
                    tv.setEnabled(true);
                } else {
                    tv.setEnabled(false);
                }

                //点击标签后，重置选中效果
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lineSelectOnclickListener != null) {
                            lineSelectOnclickListener.selectMsgIndex(lable.getKey());
                            if (selectstatus) {
                                lineSelectOnclickListener.sendOnclickMsg();
                            }
                        }
                        if (isClickable) {
                            for (View checkBox : selectView) {
                                ImageView imageView = checkBox.findViewById(R.id.cut_view);
                                RelativeLayout RelativeLayout = checkBox.findViewById(R.id.ll_layout);
                                if (checkBox.equals(tv)) {
                                    if (imageView.getVisibility() == View.VISIBLE) {
                                        RelativeLayout.setBackgroundResource(0);
                                        imageView.setVisibility(GONE);
                                    } else {
                                        imageView.setVisibility(VISIBLE);
                                        RelativeLayout.setBackgroundResource(R.drawable.bg_new_select_style);
                                    }
                                } else {
                                    imageView.setVisibility(GONE);
                                    RelativeLayout.setBackgroundResource(0);
                                }
                            }
                            lableForCommissionedSelected.clear();
                            lableForCommissionedSelected.add(lable.getTitle());
                        }

                    }
                });

                //将标签添加到容器中
                addView(tv);
            }
        }
    }


    /**
     * 添加标签 for 选择国家
     *
     * @param lables      标签集合
     * @param add         是否追加
     * @param aLineShow   是否单行显示
     * @param isReset     是否重置  如果是重置默认第一个是选中状态
     * @param isClickable 是否可点击
     */
    public void setLablesForCountry(ArrayList<OTCInitInfoBean.CountryNumberInfo> lables, boolean add, final Boolean aLineShow, boolean isReset, boolean isClickable) {
        this.aLineShow = aLineShow;
        if (this.lables == null) {
            this.lables = new ArrayList<>();
        }
        if (add) {
            this.lables.addAll(lables);
        } else {
            this.lables.clear();
            this.lables.addAll(lables);
            removeAllViews();
        }
        if (lables != null && lables.size() > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (final OTCInitInfoBean.CountryNumberInfo lable : lables) {
                //获取标签布局
                View tv = inflater.inflate(R.layout.item_new_screening_label, null);
                selectView.add(tv);
                //这里放入对应的标签字段
                TextView textView = tv.findViewById(R.id.tv_parent_content);
                textView.setText(lable.getTitle());
                //设置选中效果
                if (isReset) {
                    if (lables.get(0).equals(lable)) {
                        //选中
                        selectstatus = true;
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_select_style);
                        lableForCommissionedSelected.add(lable.getTitle());
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);

                        selectstatus = false;
                    }
                } else {
                    if (lableForCommissionedSelected.contains(lable.getTitle())) {
                        //选中
                        tv.findViewById(R.id.cut_view).setVisibility(VISIBLE);
                        selectstatus = true;
                    } else {
                        //未选中
                        tv.findViewById(R.id.cut_view).setVisibility(GONE);
                        tv.findViewById(R.id.ll_layout).setBackgroundResource(R.drawable.bg_new_unselect_style);

                        selectstatus = false;
                    }
                }
                if (isClickable) {
                    tv.setEnabled(true);
                } else {
                    tv.setEnabled(false);
                }

                //点击标签后，重置选中效果
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lineSelectOnclickListener != null) {
                            lineSelectOnclickListener.selectMsgIndex(lable.getNumberCode());
                            if (selectstatus) {
                                lineSelectOnclickListener.sendOnclickMsg();
                            }
                        }
                        if (isClickable) {
                            for (View checkBox : selectView) {
                                ImageView imageView = checkBox.findViewById(R.id.cut_view);
                                RelativeLayout frameLayout = checkBox.findViewById(R.id.ll_layout);
                                if (checkBox.equals(tv)) {
                                    if (imageView.getVisibility() == View.VISIBLE) {
                                        frameLayout.setBackgroundResource(0);
                                        imageView.setVisibility(GONE);
                                    } else {
                                        imageView.setVisibility(VISIBLE);
                                        frameLayout.setBackgroundResource(R.drawable.bg_new_select_style);
                                    }
                                } else {
                                    imageView.setVisibility(GONE);
                                    frameLayout.setBackgroundResource(0);
                                }
                            }
                            lableForCommissionedSelected.clear();
                            lableForCommissionedSelected.add(lable.getTitle());
                        }

                    }
                });

                //将标签添加到容器中
                addView(tv);
            }
        }
    }


    private LineSelectOnclickListener lineSelectOnclickListener;

    public void setLineSelectOncilckListener(LineSelectOnclickListener lineSelectOncilckListener) {
        this.lineSelectOnclickListener = lineSelectOncilckListener;
    }


    public void clearLables() {
        if (lables == null) {
            return;
        }
        this.lables.clear();
        this.lableSelected.clear();
        removeAllViews();
    }


    public CashFlowSceneBean.Scene getLables() {
        return lableSelected.size() > 0 ? lableSelected.get(0) : new CashFlowSceneBean.Scene("", "");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //为所有的标签childView计算宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //获取高的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //建议的高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //布局的宽度采用建议宽度（match_parent或者size），如果设置wrap_content也是match_parent的效果
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int height = 0;
        if (heightMode == MeasureSpec.AT_MOST) {
            //如果高度模式为EXACTLY（match_perent或者size），则使用建议高度
            height = heightSize;
        } else {
            //其他情况下（AT_MOST、UNSPECIFIED）需要计算计算高度
            int childCount = getChildCount();
            if (childCount <= 0) {
                height = 0;   //没有标签时，高度为0
            } else {
                int row = 1;  // 标签行数
                int widthSpace = width;// 当前行右侧剩余的宽度
                for (int i = 0; i < childCount; i++) {
                    View view = getChildAt(i);
                    if (i == 0) {
                        height = view.getMeasuredHeight();
                    }
                    //获取标签宽度
                    int childW = view.getMeasuredWidth();
                    if (widthSpace >= childW) {
                        //如果剩余的宽度大于此标签的宽度，那就将此标签放到本行
                        widthSpace -= childW;
                    } else {
                        if (aLineShow) {
                            break;
                        } else {
                            height += view.getMeasuredHeight();
                            row++;    //增加一行
                            //如果剩余的宽度不能摆放此标签，那就将此标签放入一行
                            widthSpace = width - childW;
                        }
                    }
                    //减去标签左右间距
                    widthSpace -= LEFT_RIGHT_SPACE;
                }
                height += (row - 1) * ROW_SPACE;
                //由于每个标签的高度是相同的，所以直接获取第一个标签的高度即可
                int childH = getChildAt(0).getMeasuredHeight();
                //最终布局的高度=标签高度*行数+行距*(行数-1)
//                height = (childH * row) + ROW_SPACE * (row - 1);

            }
        }

        //设置测量宽度和测量高度
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = 0;
        int right = 0;   // 标签相对于布局的右侧位置
        int botom = 0;       // 标签相对于布局的底部位置
        boolean isChangeLine = false;
        int beforeHight = 0;
        int lineTop = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            //右侧位置=本行已经占有的位置+当前标签的宽度
            right += childW;
            //底部位置=已经摆放的行数*（标签高度+行距）+当前标签高度
            botom = lineTop + childH;

            // 如果右侧位置已经超出布局右边缘，跳到下一行
            if (right > (r - LEFT_RIGHT_SPACE)) {
                if (aLineShow) {
                    return;
                }
                row++;
                right = childW;
                botom = beforeHight + childH;
                lineTop = botom - childH;
            }
            if (row == 0) {
                beforeHight = childH + ROW_SPACE;
            } else {
                beforeHight = botom + ROW_SPACE;
            }
            childView.layout(right - childW, botom - childH, right, botom);

            right += LEFT_RIGHT_SPACE;
        }
    }
}
