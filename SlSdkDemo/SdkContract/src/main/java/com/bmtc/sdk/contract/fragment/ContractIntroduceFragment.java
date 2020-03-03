package com.bmtc.sdk.contract.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractIndex;
import com.bmtc.sdk.library.uilogic.LogicGlobal;

import java.util.List;


/**
 * Created by zj on 2018/3/1.
 */

public class ContractIntroduceFragment extends BaseFragment {

    private View m_RootView;

    private TextView mContractUnderlyingTv;
    private TextView mMarginCoinTv;
    private TextView mContractPropertyTv;
    private TextView mContractSizeTv;
    private TextView mMaxLeverage;
    private TextView mIndexSourceTv;

    private int mContractId;
    public void setContractId(int contractId) {
        mContractId = contractId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_cotract_introduce, null);

        mContractUnderlyingTv = m_RootView.findViewById(R.id.tv_contract_underlying);
        mMarginCoinTv = m_RootView.findViewById(R.id.tv_margin_coin);
        mContractPropertyTv = m_RootView.findViewById(R.id.tv_contract_property);
        mContractSizeTv = m_RootView.findViewById(R.id.tv_contract_size);
        mMaxLeverage = m_RootView.findViewById(R.id.tv_max_leverage);
        mIndexSourceTv = m_RootView.findViewById(R.id.tv_index_source);

        setView();
        return m_RootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setView() {
        if (m_RootView == null) {
            return;
        }

        final Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        mContractUnderlyingTv.setText(contract.getBase_coin());
        mMarginCoinTv.setText(contract.getMargin_coin());
        mContractPropertyTv.setText(contract.isReserve() ? R.string.sl_str_reserve_contract : R.string.sl_str_positive_contract);
        mContractSizeTv.setText("1" + getString(R.string.sl_str_contracts_unit) + "=" + contract.getFace_value() + contract.getPrice_coin());
        mMaxLeverage.setText(contract.getMax_leverage() + getString(R.string.sl_str_bei));

        BTContract.getInstance().indexes(new IResponse<List<ContractIndex>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractIndex> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    return;
                }

                if (data != null) {
                    String index_source = "";
                    for (int i=0; i<data.size(); i++) {
                        ContractIndex index = data.get(i);
                        if (index == null) {
                            continue;
                        }

                        if (index.getIndex_id() == contract.getIndex_id()) {
                            for (int j=0; j<index.getMarket().size(); j++) {
                                index_source += index.getMarket().get(j);
                                if (j < index.getMarket().size()-1) {
                                    index_source += " , ";
                                }
                            }
                        }
                    }
                    mIndexSourceTv.setText(index_source);
                }
            }
        });
    }
}
