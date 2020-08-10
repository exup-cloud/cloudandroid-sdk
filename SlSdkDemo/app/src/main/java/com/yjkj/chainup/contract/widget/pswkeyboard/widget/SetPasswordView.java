package com.yjkj.chainup.contract.widget.pswkeyboard.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjkj.chainup.R;
import com.yjkj.chainup.contract.widget.pswkeyboard.adapter.KeyBoardAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 弹框里面的View
 */
public class SetPasswordView extends RelativeLayout implements View.OnClickListener{

    private Context mContext;

    private VirtualKeyboardView virtualKeyboardView;
    private LinearLayout mPwdEditLl;
    private LinearLayout mPwdEditAgainLl;

    private Button mConfirmBtn;
    private TextView mDifferentPwdTv;

    private TextView[] tvList;      //用数组保存6个TextView，为什么用数组？
    private ImageView[] imgList;      //用数组保存6个TextView，为什么用数组？

    private TextView[] tvListAgain;      //用数组保存6个TextView，为什么用数组？
    private ImageView[] imgListAgain;      //用数组保存6个TextView，为什么用数组？

    private GridView gridView;

    private ImageView imgCancel;

    private ArrayList<Map<String, String>> valueList;

    private int currentIndex = -1;    //用于记录当前输入密码格位置
    private int currentIndexAgain = -1;    //用于记录当前输入密码格位置

    private int mType = 0; // 0第一次输入；1再次输入

    private String mPwd = "";
    private String mPwdAgain = "";

    public SetPasswordView(Context context) {
        this(context, null);
    }

    public SetPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        View view = View.inflate(context, R.layout.sl_view_popup_set_bottom, null);

        virtualKeyboardView = view.findViewById(R.id.virtualKeyboardView);

        mPwdEditLl = view.findViewById(R.id.ll_pwd_edit);
        mPwdEditAgainLl = view.findViewById(R.id.ll_pwd_edit_again);
        mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
        mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);

        mConfirmBtn = view.findViewById(R.id.btn_confirm);
        mConfirmBtn.setEnabled(false);

        mDifferentPwdTv = view.findViewById(R.id.tv_different_pwd);
        imgCancel = view.findViewById(R.id.img_cancel);
        gridView = virtualKeyboardView.getGridView();

        initValueList();

        initView(view);

        setupView();

        addView(view);
    }

    private void initView(View view) {


        tvList = new TextView[6];

        imgList = new ImageView[6];

        tvList[0] = view.findViewById(R.id.tv_pass1);
        tvList[1] = view.findViewById(R.id.tv_pass2);
        tvList[2] = view.findViewById(R.id.tv_pass3);
        tvList[3] = view.findViewById(R.id.tv_pass4);
        tvList[4] = view.findViewById(R.id.tv_pass5);
        tvList[5] = view.findViewById(R.id.tv_pass6);


        imgList[0] = view.findViewById(R.id.img_pass1);
        imgList[1] = view.findViewById(R.id.img_pass2);
        imgList[2] = view.findViewById(R.id.img_pass3);
        imgList[3] = view.findViewById(R.id.img_pass4);
        imgList[4] = view.findViewById(R.id.img_pass5);
        imgList[5] = view.findViewById(R.id.img_pass6);

        tvList[0].setOnClickListener(this);
        tvList[1].setOnClickListener(this);
        tvList[2].setOnClickListener(this);
        tvList[3].setOnClickListener(this);
        tvList[4].setOnClickListener(this);
        tvList[5].setOnClickListener(this);
        imgList[0].setOnClickListener(this);
        imgList[1].setOnClickListener(this);
        imgList[2].setOnClickListener(this);
        imgList[3].setOnClickListener(this);
        imgList[4].setOnClickListener(this);
        imgList[5].setOnClickListener(this);


        tvListAgain = new TextView[6];
        imgListAgain = new ImageView[6];

        tvListAgain[0] = view.findViewById(R.id.tv_pass1_again);
        tvListAgain[1] = view.findViewById(R.id.tv_pass2_again);
        tvListAgain[2] = view.findViewById(R.id.tv_pass3_again);
        tvListAgain[3] = view.findViewById(R.id.tv_pass4_again);
        tvListAgain[4] = view.findViewById(R.id.tv_pass5_again);
        tvListAgain[5] = view.findViewById(R.id.tv_pass6_again);


        imgListAgain[0] = view.findViewById(R.id.img_pass1_again);
        imgListAgain[1] = view.findViewById(R.id.img_pass2_again);
        imgListAgain[2] = view.findViewById(R.id.img_pass3_again);
        imgListAgain[3] = view.findViewById(R.id.img_pass4_again);
        imgListAgain[4] = view.findViewById(R.id.img_pass5_again);
        imgListAgain[5] = view.findViewById(R.id.img_pass6_again);

        tvListAgain[0].setOnClickListener(this);
        tvListAgain[1].setOnClickListener(this);
        tvListAgain[2].setOnClickListener(this);
        tvListAgain[3].setOnClickListener(this);
        tvListAgain[4].setOnClickListener(this);
        tvListAgain[5].setOnClickListener(this);
        imgListAgain[0].setOnClickListener(this);
        imgListAgain[1].setOnClickListener(this);
        imgListAgain[2].setOnClickListener(this);
        imgListAgain[3].setOnClickListener(this);
        imgListAgain[4].setOnClickListener(this);
        imgListAgain[5].setOnClickListener(this);

    }

    // 这里，我们没有使用默认的数字键盘，因为第10个数字不显示.而是空白
    private void initValueList() {

        valueList = new ArrayList<>();

        // 初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }
    }

    private void setupView() {

        // 这里、重新为数字键盘gridView设置了Adapter
        KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(mContext, valueList);
        gridView.setAdapter(keyBoardAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mType == 0) {
                    if (position < 11 && position != 9) {    //点击0~9按钮

                        if (currentIndex >= -1 && currentIndex < 5) {      //判断输入位置————要小心数组越界
                            ++currentIndex;
                            tvList[currentIndex].setText(valueList.get(position).get("name"));

                            tvList[currentIndex].setVisibility(View.INVISIBLE);
                            imgList[currentIndex].setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (position == 11) {      //点击退格键
                            if (currentIndex - 1 >= -1) {      //判断是否删除完毕————要小心数组越界

                                tvList[currentIndex].setText("");

                                tvList[currentIndex].setVisibility(View.VISIBLE);
                                imgList[currentIndex].setVisibility(View.INVISIBLE);

                                currentIndex--;
                            }
                        }
                    }
                } else if (mType == 1) {
                    if (position < 11 && position != 9) {    //点击0~9按钮

                        if (currentIndexAgain >= -1 && currentIndexAgain < 5) {      //判断输入位置————要小心数组越界
                            ++currentIndexAgain;
                            tvListAgain[currentIndexAgain].setText(valueList.get(position).get("name"));

                            tvListAgain[currentIndexAgain].setVisibility(View.INVISIBLE);
                            imgListAgain[currentIndexAgain].setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (position == 11) {      //点击退格键
                            if (currentIndexAgain - 1 >= -1) {      //判断是否删除完毕————要小心数组越界

                                tvListAgain[currentIndexAgain].setText("");

                                tvListAgain[currentIndexAgain].setVisibility(View.VISIBLE);
                                imgListAgain[currentIndexAgain].setVisibility(View.INVISIBLE);

                                currentIndexAgain--;
                            }
                        }
                    }
                }

            }
        });


        tvList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 1) {

                    mType = 1;
                    mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);
                    mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
                    virtualKeyboardView.setVisibility(VISIBLE);

                    mPwd = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        mPwd += tvList[i].getText().toString().trim();
                    }

                    if (!TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdAgain)) {

                        if (TextUtils.equals(mPwd, mPwdAgain)) {
                            mConfirmBtn.setEnabled(true);
                            mDifferentPwdTv.setVisibility(GONE);
                        } else {
                            mConfirmBtn.setEnabled(false);
                            mDifferentPwdTv.setVisibility(VISIBLE);
                        }

                    } else {
                        mConfirmBtn.setEnabled(false);
                        mDifferentPwdTv.setVisibility(GONE);
                    }
                }
            }
        });


        tvListAgain[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 1) {

                    virtualKeyboardView.setVisibility(GONE);

                    mPwdAgain = "";     //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        mPwdAgain += tvListAgain[i].getText().toString().trim();
                    }

                    if (!TextUtils.isEmpty(mPwd) && !TextUtils.isEmpty(mPwdAgain)) {

                        if (TextUtils.equals(mPwd, mPwdAgain)) {
                            mConfirmBtn.setEnabled(true);
                            mDifferentPwdTv.setVisibility(GONE);
                        } else {
                            mConfirmBtn.setEnabled(false);
                            mDifferentPwdTv.setVisibility(VISIBLE);
                        }

                    } else {
                        mConfirmBtn.setEnabled(false);
                        mDifferentPwdTv.setVisibility(GONE);
                    }
                }
            }
        });

        mPwdEditLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 0;
                mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
                mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);
                virtualKeyboardView.setVisibility(VISIBLE);
            }
        });

        mPwdEditAgainLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 1;
                mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);
                mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
                virtualKeyboardView.setVisibility(VISIBLE);
            }
        });
        // 监听键盘上方的返回
        virtualKeyboardView.getLayoutBack().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualKeyboardView.setVisibility(GONE);
            }
        });
    }

    public Button getConfirmBtn() {
        return mConfirmBtn;
    }

    public String getPassword() {
        return mPwd;
    }

    public VirtualKeyboardView getVirtualKeyboardView() {

        return virtualKeyboardView;
    }

    public ImageView getImgCancel() {
        return imgCancel;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_pass1:
            case R.id.tv_pass2:
            case R.id.tv_pass3:
            case R.id.tv_pass4:
            case R.id.tv_pass5:
            case R.id.tv_pass6:
            case R.id.img_pass1:
            case R.id.img_pass2:
            case R.id.img_pass3:
            case R.id.img_pass4:
            case R.id.img_pass5:
            case R.id.img_pass6:
                mType = 0;
                mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
                mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);
                virtualKeyboardView.setVisibility(VISIBLE);
                break;

            case R.id.tv_pass1_again:
            case R.id.tv_pass2_again:
            case R.id.tv_pass3_again:
            case R.id.tv_pass4_again:
            case R.id.tv_pass5_again:
            case R.id.tv_pass6_again:
            case R.id.img_pass1_again:
            case R.id.img_pass2_again:
            case R.id.img_pass3_again:
            case R.id.img_pass4_again:
            case R.id.img_pass5_again:
            case R.id.img_pass6_again:
                mType = 1;
                mPwdEditLl.setBackgroundResource(R.drawable.sl_psw_input_area_bg);
                mPwdEditAgainLl.setBackgroundResource(R.drawable.sl_psw_input_area_focus_bg);
                virtualKeyboardView.setVisibility(VISIBLE);
                break;
        }
    }
}
