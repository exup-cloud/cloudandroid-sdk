package com.bmtc.sdk.contract.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bmtc.sdk.contract.ContractDetailActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.FundsRateDlgAdapter;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.data.ContractFundingRate;
import com.contract.sdk.impl.IResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FundsRateWindow extends PopupWindow {

    private List<ContractFundingRate> mFundsRateList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private FundsRateDlgAdapter mFundsRateAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private Context context;
    private int mContractId;

    public FundsRateWindow(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View rootView = inflater.inflate(R.layout.sl_view_funds_rate, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //this.setAnimationStyle(R.style.popwin_anim_style);

        //this.setAnimationStyle(R.style.PopWinAnim);
        this.update();
        this.setBackgroundDrawable(new BitmapDrawable());

        //backgroundAlpha(1f);

        //添加pop窗口关闭事件
        //this.setOnDismissListener(new poponDismissListener());
        initView(rootView);
    }


    private void initView(View view) {

        view.findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itnt = new Intent(context, ContractDetailActivity.class);
                itnt.putExtra("contract_id", mContractId);
                itnt.putExtra("page_index", 2);
                context.startActivity(itnt);
                dismiss();
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mRecyclerView = view.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (mFundsRateAdapter == null) {
            mFundsRateAdapter = new FundsRateDlgAdapter(context);
            mFundsRateAdapter.setData(mFundsRateList);
            mRecyclerView.setAdapter(mFundsRateAdapter);
        } else {
            mRecyclerView.setAdapter(mFundsRateAdapter);
        }

    }

    public void setContractId(int contractId) {
        mContractId = contractId;
    }

    private void setData(List<ContractFundingRate> list) {
        if (list == null) {
            return;
        }

        mFundsRateList.clear();
        mFundsRateList.addAll(list);

        if (mFundsRateAdapter == null) {
            mFundsRateAdapter = new FundsRateDlgAdapter(context);
        }

        mFundsRateAdapter.setData(mFundsRateList);
        mFundsRateAdapter.notifyDataSetChanged();
    }

    public void updateData() {

        ContractPublicDataAgent.INSTANCE.loadFundingRate(mContractId, new IResponse<List<ContractFundingRate>>() {
            @Override
            public void onSuccess(List<ContractFundingRate> data) {
                if (data != null && data.size() > 0) {
                    List<ContractFundingRate> newData = new ArrayList<>();
                    for (int i=0; i<Math.min(data.size(), 4); i++) {
                        newData.add(data.get(i));
                    }

                    setData(newData);
                }
            }

            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                ToastUtil.shortToast(context, msg);
            }
        });
    }

}
