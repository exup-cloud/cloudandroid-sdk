package com.bmtc.sdk.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bmtc.sdk.contract.adapter.FundsFlowAdapter;
import com.bmtc.sdk.library.base.BaseActivity;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractCashBook;
import com.bmtc.sdk.library.uilogic.LogicLoadAnimation;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class FundsFlowActivity extends BaseActivity {

    private ImageView mBackIv;
    private TextView mTitleTv;
    private ImageView mNoresultIv;

    private TextView mContractType;
    private ImageView mSelContractType;
    private TextView mActionType;
    private ImageView mSelActionType;

    private String mCoincode;

    private RecyclerView mRecyclerView;
    private FundsFlowAdapter mFundsFlowAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private List<ContractCashBook> mRecordList = new ArrayList<>();

    private int mAction = 0;
    private int mLimit = 10;
    private int mOffset = 0;
    private boolean mNomore = false;
    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();

    private boolean mLoading = false;

    private ListView mContractListView;
    private PopupWindow mContractWindow;
    private DropContractAdapter mContractAdapter;
    private View mContractPopupView;

    private ListView mTypeListView;
    private PopupWindow mTypeWindow;
    private DropTypeAdapter mTypeAdapter;
    private View mTypePopupView;

    private Animation mRotate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_funds_flow);

        try {
            mCoincode = getIntent().getStringExtra("coin_code");
            if (TextUtils.isEmpty(mCoincode)) {
                mCoincode = "USDT";
            }
        } catch (Exception ignored) {}

        //MobclickAgent.onEvent(LogicGlobal.sContext, "ss_as");

        setView();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        try {
            mCoincode = getIntent().getStringExtra("coin_code");
            if (TextUtils.isEmpty(mCoincode)) {
                mCoincode = "USDT";
            }
        } catch (Exception ignored) {}
    }

     public void setView() {
        mRotate = AnimationUtils.loadAnimation(this, R.anim.array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());

        mBackIv = findViewById(R.id.iv_back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mNoresultIv = findViewById(R.id.iv_noresult);

        mContractType = findViewById(R.id.tv_contract_type);
        mContractType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mSelContractType = findViewById(R.id.iv_sel_contract_type);
        mSelContractType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpotWindow();
            }
        });
        mActionType = findViewById(R.id.tv_type);
        mActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeWindow();
            }
        });

        mSelActionType = findViewById(R.id.iv_sel_type);
        mSelActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeWindow();
            }
        });

        mRecyclerView = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mFundsFlowAdapter.getItemCount()) {

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    if (!mNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(FundsFlowActivity.this, (ViewGroup) mRecyclerView.getParent());
                    }

                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                        }
                    }, 100);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        if (mFundsFlowAdapter == null) {
            mFundsFlowAdapter = new FundsFlowAdapter(FundsFlowActivity.this);
            mFundsFlowAdapter.setData(mRecordList);
            mRecyclerView.setAdapter(mFundsFlowAdapter);
        } else {
            mRecyclerView.setAdapter(mFundsFlowAdapter);
        }

        updateData();
    }

    private void updateData() {
        if (mLoading) {
            return;
        }

        final int offset = mOffset;

        mContractType.setText(mCoincode);
        int[] action = null;
        switch (mAction) {
            case 0:
                mActionType.setText(R.string.sl_str_all);
                break;
            case 1:
                action = new int[]{1};
                mActionType.setText(R.string.sl_str_buy_open);
                break;
            case 2:
                action = new int[]{2};
                mActionType.setText(R.string.sl_str_buy_close);
                break;
            case 3:
                action = new int[]{3};
                mActionType.setText(R.string.sl_str_sell_close);
                break;
            case 4:
                action = new int[]{4};
                mActionType.setText(R.string.sl_str_sell_open);
                break;
            case 5:
            case 7:
                action = new int[]{5, 7};
                mActionType.setText(R.string.sl_str_transfer_bb2contract);
                break;
            case 6:
            case 8:
                action = new int[]{6, 8};
                mActionType.setText(R.string.sl_str_transfer_contract2bb);
                break;
            case 9:
                action = new int[]{9};
                mActionType.setText(R.string.sl_str_transferim_position2contract);
                break;
            case 10:
                action = new int[]{10};
                mActionType.setText(R.string.sl_str_transferim_contract2position);
                break;
            case 11:
                action = new int[]{11};
                mActionType.setText(R.string.sl_str_position_fee);
                break;
            default:
                mActionType.setText("--");
                break;
        }

        mLoading = true;
        BTContract.getInstance().cashBooks(0, action, mCoincode, mLimit, mOffset, new IResponse<List<ContractCashBook>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractCashBook> data) {
                mLoading = false;

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                mOffset = offset;

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(FundsFlowActivity.this, message);
                    if (mOffset == 0) {
                        mNoresultIv.setVisibility(View.VISIBLE);
                        clearData();
                    }
                    return;
                }

                if (data != null && data.size() > 0) {

                    if (mRecordList == null) {
                        mRecordList = new ArrayList<>();
                    }

                    if (mOffset == 0) {
                        mRecordList.clear();
                        mRecordList.addAll(data);
                    } else {
                        mRecordList.addAll(data);
                    }

                    if (mFundsFlowAdapter == null) {
                        mFundsFlowAdapter = new FundsFlowAdapter(FundsFlowActivity.this);
                        mFundsFlowAdapter.setData(mRecordList);
                    }

                    mFundsFlowAdapter.setData(mRecordList);
                    mFundsFlowAdapter.notifyDataSetChanged();
                    mOffset += data.size();

                } else {
                    if (mOffset == 0) {
                        mRecordList.clear();
                    }

                    mNoresultIv.setVisibility(mRecordList.size() > 0 ? View.GONE : View.VISIBLE);

                    if (!mNomore) {
                        mNomore = true;
                        ToastUtil.shortToast(FundsFlowActivity.this, getString(R.string.sl_str_no_more_data));
                    }
                }

                if (mRecordList != null && mRecordList.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                } else {
                    mNoresultIv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void clearData() {
        mRecordList.clear();
        if (mFundsFlowAdapter == null) {
            mFundsFlowAdapter = new FundsFlowAdapter(this);
        }

        mFundsFlowAdapter.setData(mRecordList);
        mFundsFlowAdapter.notifyDataSetChanged();
    }

    private void showSpotWindow() {
        final List<ContractAccount> contractAccounts = BTContract.getInstance().getContractAccountList();
        if (contractAccounts == null || contractAccounts.size() <= 0) {
            return;
        }

        if (mContractWindow != null && mContractWindow.isShowing()) {
            mContractWindow.dismiss();
        }

        int itemAccountId = R.layout.sl_item_drop_text;
        mContractAdapter = new DropContractAdapter(this, itemAccountId, contractAccounts);

        mContractPopupView = LayoutInflater.from(this).inflate(R.layout.sl_view_dropdown, null);
        mContractListView = mContractPopupView.findViewById(R.id.lv_list);
        mContractListView.setAdapter(mContractAdapter);
        mContractListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mContractWindow != null) {
                    mCoincode = contractAccounts.get(i).getCoin_code();
                    mOffset = 0;

                    if (!mNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(FundsFlowActivity.this, (ViewGroup) mRecyclerView.getParent());
                    }
                    updateData();
                    mContractWindow.dismiss();
                }
            }
        });

        int listPadding = UtilSystem.dip2px(this, 5);
        int itemHeight = UtilSystem.dip2px(this, 40);
        int windowHeight = itemHeight * contractAccounts.size() + listPadding * 2;
        int min = itemHeight + listPadding * 2;
        int max = itemHeight * 10;
        if (windowHeight > max) {
            windowHeight = max;
        } else if (windowHeight < min) {
            windowHeight = min;
        }

        mContractWindow = new PopupWindow(mContractPopupView, UtilSystem.dip2px(this, 100), windowHeight);
        mContractWindow.setOutsideTouchable(true);
        mContractWindow.setBackgroundDrawable(new BitmapDrawable());
        mContractWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mRotate.setFillAfter(false);
                mSelContractType.startAnimation(mRotate);
            }
        });

        mRotate.setFillAfter(true);
        mSelContractType.startAnimation(mRotate);

        mContractWindow.setFocusable(true);
        mContractWindow.showAsDropDown(mContractType, 0, 2);
    }

    class DropContractAdapter extends ArrayAdapter<ContractAccount> {

        private Context mContext;
        private int mResId;
        private List<ContractAccount> mItems;

        public DropContractAdapter(Context context, int textViewResourceId, List<ContractAccount> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropContractAdapter.DropSpotViewHolder holder;
            if (convertView == null) {
                holder = new DropContractAdapter.DropSpotViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvStockCode = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropContractAdapter.DropSpotViewHolder)convertView.getTag();
            holder.tvStockCode.setText(getItem(position).getCoin_code());
            return convertView;
        }

        class DropSpotViewHolder {
            TextView tvStockCode;
        }
    }

    private void showTypeWindow() {
        final List<Pair<String, Integer>> actions = new ArrayList<>();
        actions.add(new Pair<>(getString(R.string.sl_str_all), 0));
        actions.add(new Pair<>(getString(R.string.sl_str_buy_open), 1));
        actions.add(new Pair<>(getString(R.string.sl_str_buy_close), 2));
        actions.add(new Pair<>(getString(R.string.sl_str_sell_close), 3));
        actions.add(new Pair<>(getString(R.string.sl_str_sell_open), 4));
        actions.add(new Pair<>(getString(R.string.sl_str_transfer_bb2contract), 5));
        actions.add(new Pair<>(getString(R.string.sl_str_transfer_contract2bb), 6));
        actions.add(new Pair<>(getString(R.string.sl_str_transferim_position2contract), 9));
        actions.add(new Pair<>(getString(R.string.sl_str_transferim_contract2position), 10));
        //actions.add(new Pair<>(getString(R.string.str_position_fee), 11));

        if (mTypeWindow != null && mTypeWindow.isShowing()) {
            mTypeWindow.dismiss();
        }

        int itemAccountId = R.layout.sl_item_drop_text;
        mTypeAdapter = new DropTypeAdapter(this, itemAccountId, actions);

        mTypePopupView = LayoutInflater.from(this).inflate(R.layout.sl_view_dropdown, null);
        mTypeListView = mTypePopupView.findViewById(R.id.lv_list);
        mTypeListView.setAdapter(mTypeAdapter);
        mTypeListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mTypeWindow != null) {
                    mAction = actions.get(i).second;
                    mOffset = 0;

                    if (!mNomore && !mLoadingPage.IsLoadingShow()) {
                        mLoadingPage.ShowLoadAnimation(FundsFlowActivity.this, (ViewGroup) mRecyclerView.getParent());
                    }

                    updateData();
                    mTypeWindow.dismiss();
                }
            }
        });

        int listPadding = UtilSystem.dip2px(this, 5);
        int itemHeight = UtilSystem.dip2px(this, 40);
        int windowHeight = itemHeight * actions.size() + listPadding * 2;
        int min = itemHeight + listPadding * 2;
        int max = itemHeight * 10;
        if (windowHeight > max) {
            windowHeight = max;
        } else if (windowHeight < min) {
            windowHeight = min;
        }

        mTypeWindow = new PopupWindow(mTypePopupView, UtilSystem.dip2px(this, 120), windowHeight);
        mTypeWindow.setOutsideTouchable(true);
        mTypeWindow.setBackgroundDrawable(new BitmapDrawable());
        mTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mRotate.setFillAfter(false);
                mSelActionType.startAnimation(mRotate);
            }
        });

        mRotate.setFillAfter(true);
        mSelActionType.startAnimation(mRotate);

        mTypeWindow.setFocusable(true);
        mTypeWindow.showAsDropDown(mActionType, -(UtilSystem.dip2px(this, 100) - mActionType.getWidth()), 2);
    }

    class DropTypeAdapter extends ArrayAdapter<Pair<String, Integer>> {

        private Context mContext;
        private int mResId;
        private List<Pair<String, Integer>> mItems;

        public DropTypeAdapter(Context context, int textViewResourceId, List<Pair<String, Integer>> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropTypeAdapter.DropTypeViewHolder holder;
            if (convertView == null) {
                holder = new DropTypeAdapter.DropTypeViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvStockCode = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropTypeAdapter.DropTypeViewHolder)convertView.getTag();
            holder.tvStockCode.setText(getItem(position).first);
            return convertView;
        }

        class DropTypeViewHolder {
            TextView tvStockCode;
        }
    }
}
