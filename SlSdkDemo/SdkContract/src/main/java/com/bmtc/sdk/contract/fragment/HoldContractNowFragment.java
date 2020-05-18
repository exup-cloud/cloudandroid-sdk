package com.bmtc.sdk.contract.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.adapter.HoldContractAdapter;
import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractPosition;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicContractTicker;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.uilogic.LogicLoadAnimation;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.NoDoubleClickUtils;
import com.bmtc.sdk.library.utils.UtilSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/1.
 */

@SuppressLint("ValidFragment")
public class HoldContractNowFragment extends BaseFragment implements
     //   LogicUserState.IUserStateListener,
        LogicWebSocketContract.IWebSocketListener,
        LogicContractTicker.IContractTickerListener{

    private View m_RootView;
    private ImageView mNoresultIv;
    private TextView mNoresultTv;

    private List<ContractPosition> mPositionList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private HoldContractAdapter mHoldContractAdapter;
    private int mLastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private LogicLoadAnimation mLoadingPage = new LogicLoadAnimation();
    private SlLoadingDialog mLoadingDialog;
    private boolean mLoading = false;

    private int mType = 0;  //0normal; 1tradefragment
    private int mContractId = 1;

    public void setType(int type) {
        mType = type;
    }

    public void setContractId(int contractId) {
        mContractId = contractId;
    }

    public void setContractId(int contractId, boolean loading) {
        if (m_RootView == null) {
            return;
        }

        mContractId = contractId;

        if (loading) {
            mLoadingDialog.show();
        }

        updateData(true);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && !NoDoubleClickUtils.isDoubleClick()) {
            updateData(true);
        }
    }

    @Override
    public void onTimer(int times) {
        if (mType == 1 && isForeground()) {
            if (!LogicWebSocketContract.getInstance().isConnected()) {
                updateData(true);
            }
        }
    }

    private boolean isForeground() {
        return  getActivity() != null && isAdded() && !getHidden() && UtilSystem.isActivityForeground(LogicGlobal.sContext, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mType == 0) {
            m_RootView = inflater.inflate(R.layout.sl_fragment_open_order, null);
        } else {
            m_RootView = inflater.inflate(R.layout.sl_fragment_recycle_trade, null);
        }

        //LogicUserState.getInstance().registListener(this);
        LogicWebSocketContract.getInstance().registListener(this);
        LogicContractTicker.getInstance().registListener(this);

        mLoadingDialog = new SlLoadingDialog(getActivity());

        mNoresultIv = m_RootView.findViewById(R.id.iv_noresult);
        mNoresultTv = m_RootView.findViewById(R.id.tv_noresult);
        if (mType == 1) {
            mNoresultTv.setText(R.string.sl_str_none);
        }

        mRecyclerView = m_RootView.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(LogicGlobal.sContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mHoldContractAdapter.getItemCount()) {

                    if (mLoadingPage.IsLoadingShow()) {
                        return;
                    }

                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateData(true);
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

        if (mHoldContractAdapter == null) {
            mHoldContractAdapter = new HoldContractAdapter(LogicGlobal.sContext);
            mHoldContractAdapter.setData(mPositionList);
            mRecyclerView.setAdapter(mHoldContractAdapter);
        } else {
            mRecyclerView.setAdapter(mHoldContractAdapter);
        }

        mLoadingPage.ShowLoadAnimation(getActivity(), (ViewGroup) mRecyclerView.getParent());
        updateData(true);

        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // LogicUserState.getInstance().unregistListener(this);
        LogicWebSocketContract.getInstance().unregistListener(this);
        LogicContractTicker.getInstance().unregistListener(this);
    }

    private void updateData(boolean update) {
        if (mLoading) {
            return;
        }

        if (SLSDKAgent.slUser == null) {
            mLoadingDialog.dismiss();

            if (mLoadingPage.IsLoadingShow()) {
                mLoadingPage.ExitLoadAnimation();
            }
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        List<ContractPosition> positions = BTContract.getInstance().getCoinPositions(contract.getMargin_coin());
        if (positions != null) {
            List<ContractPosition> newPositions = new ArrayList<>();
            for (int i=0; i<positions.size(); i++) {
                ContractPosition position = positions.get(i);
                if (position == null) {
                    continue;
                }
                if (position.getInstrument_id() == mContractId) {
                    newPositions.add(position);
                }
            }

            if (newPositions.size() > 0) {
                mNoresultIv.setVisibility(View.GONE);
                mNoresultTv.setVisibility(View.GONE);

                if (mPositionList == null) {
                    mPositionList = new ArrayList<>();
                }

                mPositionList.clear();
                mPositionList.addAll(newPositions);

                if (mHoldContractAdapter == null) {
                    mHoldContractAdapter = new HoldContractAdapter(LogicGlobal.sContext);
                }

                mHoldContractAdapter.setData(mPositionList);
                mHoldContractAdapter.notifyDataSetChanged();

            } else {
                mPositionList.clear();
                mHoldContractAdapter.setData(mPositionList);
                mHoldContractAdapter.notifyDataSetChanged();

                mNoresultIv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
                mNoresultTv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
            }

            if (!update) {
                return;
            }
        } else {
            if (!update) {
                return;
            }
        }

        mLoading = true;
        BTContract.getInstance().userPositions(contract.getMargin_coin(), 1, 0, 0, new IResponse<List<ContractPosition>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractPosition> data) {
                mLoading = false;
                mLoadingDialog.dismiss();

                if (mLoadingPage.IsLoadingShow()) {
                    mLoadingPage.ExitLoadAnimation();
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    //if (TextUtils.equals(errno, BTConstants.ERRNO_NONETWORK)) {
                        //ToastUtil.shortToast(LogicGlobal.sContext, message);
                    //}

                    //mNoresultIv.setVisibility(View.VISIBLE);
                    //mNoresultTv.setVisibility(View.VISIBLE);
                    //clearData();
                    return;
                }

                List<ContractPosition> newPositions = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (int i=0; i<data.size(); i++) {
                        ContractPosition position = data.get(i);
                        if (position == null) {
                            continue;
                        }
                        if (position.getInstrument_id() == mContractId) {
                            newPositions.add(position);
                        }
                    }
                }

                if (newPositions.size() > 0) {
                    mNoresultIv.setVisibility(View.GONE);
                    mNoresultTv.setVisibility(View.GONE);

                    if (mPositionList == null) {
                        mPositionList = new ArrayList<>();
                    }
                    
                    mPositionList.clear();
                    mPositionList.addAll(newPositions);
                    
                    if (mHoldContractAdapter == null) {
                        mHoldContractAdapter = new HoldContractAdapter(LogicGlobal.sContext);
                    }

                    mHoldContractAdapter.setData(mPositionList);
                    mHoldContractAdapter.notifyDataSetChanged();

                } else {
                    mPositionList.clear();
                    mHoldContractAdapter.setData(mPositionList);
                    mHoldContractAdapter.notifyDataSetChanged();

                    mNoresultIv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
                    mNoresultTv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
    }

    private void clearData() {
        if (mPositionList == null) {
            mPositionList = new ArrayList<>();
        }

        mPositionList.clear();

        if (mHoldContractAdapter == null) {
            mHoldContractAdapter = new HoldContractAdapter(LogicGlobal.sContext);
        }

        mHoldContractAdapter.setData(mPositionList);
        mHoldContractAdapter.notifyDataSetChanged();
    }

    private void updateSinglePosition(ContractPosition position) {
        if (mPositionList == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        if (position.getInstrument_id() != mContractId) {
            return;
        }

        boolean exist = false;
        for (int i=0; i<mPositionList.size(); i++) {
            ContractPosition item = mPositionList.get(i);
            if (item == null) {
                continue;
            }

            if (item.getPid() == position.getPid()) {
                if (position.getStatus() == 1) {
                    mPositionList.set(i, position);
                } else {
                    mPositionList.remove(i);
                }
                exist = true;
                break;
            }
        }

        if (!exist) {
            mPositionList.add(position);
        }

        exist = false;
        List<ContractPosition> positions = BTContract.getInstance().getCoinPositions(contract.getMargin_coin());
        if (positions != null) {
            for (int i = 0; i < positions.size(); i++) {
                ContractPosition item = positions.get(i);
                if (position == null) {
                    continue;
                }

                if (item.getPid() == position.getPid()) {
                    if (position.getStatus() == 1) {
                        positions.set(i, position);
                    } else {
                        positions.remove(i);
                    }
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                positions.add(position);
            }

        }

        if (mHoldContractAdapter == null) {
            mHoldContractAdapter = new HoldContractAdapter(getActivity());
            mRecyclerView.setAdapter(mHoldContractAdapter);
            mHoldContractAdapter.setData(mPositionList);
        }

        mHoldContractAdapter.setData(mPositionList);
        mHoldContractAdapter.notifyDataSetChanged();

        mNoresultIv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
        mNoresultTv.setVisibility(mPositionList.size() > 0 ? View.GONE : View.VISIBLE);
    }

//    @Override
//    public void onLogin(Account account) {
//        updateData(true);
//    }
//
//    @Override
//    public void onUserMe(Account account) {
//
//    }

//    @Override
//    public void onContractAccount(ContractAccount account) {
//
//    }

//    @Override
//    public void onLogout(boolean forbidden) {
//        clearData();
//    }

    @Override
    public void onContractTickerChanged(ContractTicker ticker) {
        if (ticker == null) {
            return;
        }

        if (mPositionList == null || mPositionList.size() <= 0) {
            return;
        }

        if (mContractId == ticker.getInstrument_id()) {
            updateData(false);
        }
    }

    @Override
    public void onContractMessage(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            String action = jsonObject.optString("action");
            String group = jsonObject.optString("group");
            if (TextUtils.isEmpty(group)) {
                return;
            }

            String[] argGroup = group.split(":");
            if (argGroup.length == 1) {
                if (TextUtils.equals(argGroup[0], LogicWebSocketContract.WEBSOCKET_CUD)) {

                    JSONArray dataArray = jsonObject.optJSONArray("data");
                    if (dataArray == null) {
                        return;
                    }

                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject obj = dataArray.optJSONObject(i);
                        if (obj == null) {
                            continue;
                        }

                        JSONObject positionObj = obj.optJSONObject("position");
                        if (positionObj == null) {
                            continue;
                        }

                        ContractPosition position = new ContractPosition();
                        position.fromJson(positionObj);

                        updateSinglePosition(position);
                    }

                }

            } else {
                return;
            }

        } catch (JSONException ignored) {
        }
    }

    @Override
    public void connectFail(String url, int reCount) {

    }

    @Override
    public void reConnectSuccess(String url, int reCount) {

    }

}
