package com.bmtc.sdk.contract.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.view.pickwindow.PickPopupWindow;
import com.bmtc.sdk.library.base.BaseFragment;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zj on 2018/3/1.
 */

public class ContractCalculateFragment extends BaseFragment {

    private View m_RootView;

    private RelativeLayout mDirectionRl;
    private TextView mDirectionTv;

    private RelativeLayout mTypeRl;
    private TextView mTypeTv;

    private RelativeLayout mLeverageRl;
    private TextView mLeverageTv;

    private LinearLayout mPositionLl;
    private EditText mPositionEt;
    private TextView mPositionUnitTv;

    private LinearLayout mOpenPriceLl;
    private EditText mOpenPriceEt;
    private TextView mOpenPriceUnitTv;

    private LinearLayout mClosePriceLl;
    private EditText mClosePriceEt;
    private TextView mClosePriceUnitTv;

    private LinearLayout mProfitValueLl;
    private EditText mProfitValueEt;
    private TextView mProfitValueUnitTv;

    private LinearLayout mProfitRateLl;
    private EditText mProfitRateEt;
    private TextView mProfitRateUnitTv;

    private Button mCalculateBtn;

    private RelativeLayout mMarginRl;
    private TextView mMarginTv;

    private RelativeLayout mLiquidationPriceRl;
    private TextView mLiquidationPriceTv;

    private RelativeLayout mPositionValueRl;
    private TextView mPositionValueTv;

    private RelativeLayout mPLRl;
    private TextView mPLTv;

    private RelativeLayout mProfitRateRl;
    private TextView mProfitRateTv;

    private RelativeLayout mInitialMarginRateRl;
    private TextView mInitialMarginRateTv;

    private RelativeLayout mMaintenanceMarginRateRl;
    private TextView mMaintenanceMarginRateTv;

    private RelativeLayout mTargetClosePriceRl;
    private TextView mTargetClosePriceTv;

    private int mDirection = 0; //0多 1空
    private int mLeverage = 100;
    private int mProfitType = 0; //0value 1rate

    private int mType = 0; //0盈亏 1强平 2目标价
    private int mContractId = 0;

    public void setType(int type) {
        mType = type;
    }

    public void setContractId(int contractId) {
        mContractId = contractId;
        updateData();
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable == mPositionEt.getEditableText()) {
                updateVol(mPositionEt);
            }
            if (editable == mOpenPriceEt.getEditableText()) {
                updatePrice(mOpenPriceEt);
            }
            if (editable == mClosePriceEt.getEditableText()) {
                updatePrice(mClosePriceEt);
            }
            if (editable == mProfitValueEt.getEditableText()) {
                updateValue(mProfitValueEt);
            }
            if (editable == mProfitRateEt.getEditableText()) {
                updateRate(mProfitRateEt);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_RootView = inflater.inflate(R.layout.sl_fragment_contract_calculate, null);

        mDirectionRl = m_RootView.findViewById(R.id.rl_direction);
        mDirectionRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDirection();
            }
        });
        mDirectionTv = m_RootView.findViewById(R.id.tv_direction_value);

        mTypeRl = m_RootView.findViewById(R.id.rl_type);
        mTypeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doType();
            }
        });
        mTypeTv = m_RootView.findViewById(R.id.tv_type_value);

        mLeverageRl = m_RootView.findViewById(R.id.rl_leverage);
        mLeverageRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLeverage();
            }
        });
        mLeverageTv = m_RootView.findViewById(R.id.tv_leverage_value);

        mPositionLl = m_RootView.findViewById(R.id.ll_position);
        mPositionEt = m_RootView.findViewById(R.id.et_position);
        mPositionEt.addTextChangedListener(mTextWatcher);
        mPositionUnitTv = m_RootView.findViewById(R.id.tv_position_unit);

        mOpenPriceLl = m_RootView.findViewById(R.id.ll_open_price);
        mOpenPriceEt = m_RootView.findViewById(R.id.et_open_price);
        mOpenPriceEt.addTextChangedListener(mTextWatcher);
        mOpenPriceUnitTv = m_RootView.findViewById(R.id.tv_open_price_unit);

        mClosePriceLl = m_RootView.findViewById(R.id.ll_close_price);
        mClosePriceEt = m_RootView.findViewById(R.id.et_close_price);
        mClosePriceEt.addTextChangedListener(mTextWatcher);
        mClosePriceUnitTv = m_RootView.findViewById(R.id.tv_close_price_unit);

        mProfitValueLl = m_RootView.findViewById(R.id.ll_profit_value);
        mProfitValueEt = m_RootView.findViewById(R.id.et_profit_value);
        mProfitValueEt.addTextChangedListener(mTextWatcher);
        mProfitValueUnitTv = m_RootView.findViewById(R.id.tv_profit_value_unit);

        mProfitRateLl = m_RootView.findViewById(R.id.ll_profit_rate);
        mProfitRateEt = m_RootView.findViewById(R.id.et_profit_rate);
        mProfitRateEt.addTextChangedListener(mTextWatcher);
        mProfitRateUnitTv = m_RootView.findViewById(R.id.tv_profit_rate_unit);

        mMarginRl = m_RootView.findViewById(R.id.rl_take_up_margin);
        mMarginTv = m_RootView.findViewById(R.id.tv_take_up_margin_value);

        mLiquidationPriceRl = m_RootView.findViewById(R.id.rl_liquidation_price);
        mLiquidationPriceTv = m_RootView.findViewById(R.id.tv_liquidation_price_value);

        mPositionValueRl = m_RootView.findViewById(R.id.rl_position_value);
        mPositionValueTv = m_RootView.findViewById(R.id.tv_position_value_value);

        mPLRl = m_RootView.findViewById(R.id.rl_pl);
        mPLTv = m_RootView.findViewById(R.id.tv_pl_value);

        mProfitRateRl = m_RootView.findViewById(R.id.rl_profit_rate);
        mProfitRateTv = m_RootView.findViewById(R.id.tv_profit_rate_value);

        mInitialMarginRateRl = m_RootView.findViewById(R.id.rl_initial_margin_rate);
        mInitialMarginRateTv = m_RootView.findViewById(R.id.tv_initial_margin_rate_value);

        mMaintenanceMarginRateRl = m_RootView.findViewById(R.id.rl_maintenance_margin_rate);
        mMaintenanceMarginRateTv = m_RootView.findViewById(R.id.tv_maintenance_margin_rate_value);

        mTargetClosePriceRl = m_RootView.findViewById(R.id.rl_target_close_price);
        mTargetClosePriceTv = m_RootView.findViewById(R.id.tv_target_close_price_value);

        mCalculateBtn = m_RootView.findViewById(R.id.btn_calculate);
        mCalculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCalculate();
            }
        });

        updateType();
        updateData();

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

    private void updateData() {
        if (m_RootView == null) {
            return;
        }
        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        if (mDirection == 0) {
            mDirectionTv.setText(R.string.sl_str_long);
        } else if (mDirection == 1) {
            mDirectionTv.setText(R.string.sl_str_short);
        }

        if (mProfitType == 0) {
            mTypeTv.setText(R.string.sl_str_profit_value);
        } else if (mDirection == 1) {
            mTypeTv.setText(R.string.sl_str_profit_rate);
        }

        int minLeverage = Integer.parseInt(contract.getMin_leverage());
        int maxLeverage = Integer.parseInt(contract.getMax_leverage());
        int leverage = (mLeverage == 0) ? 100 : mLeverage;
        if (leverage > maxLeverage || leverage < minLeverage) {
            leverage = 100;
            if (leverage <= maxLeverage && leverage >= minLeverage) {
                mLeverage = 0;
            } else {
                leverage = 50;
                if (leverage <= maxLeverage && leverage >= minLeverage) {
                    mLeverage = leverage;
                } else {
                    leverage = 20;
                    if (leverage <= maxLeverage && leverage >= minLeverage) {
                        mLeverage = leverage;
                    } else {
                        leverage = 10;
                        if (leverage <= maxLeverage && leverage >= minLeverage) {
                            mLeverage = leverage;
                        }
                    }
                }
            }
        }

        mLeverageTv.setText(mLeverage + "X");

        mOpenPriceUnitTv.setText(contract.getQuote_coin());
        mClosePriceUnitTv.setText(contract.getQuote_coin());
        mProfitValueUnitTv.setText(contract.getMargin_coin());
    }

    private void updateType() {
        if (mType == 0) {
            mDirectionRl.setVisibility(View.VISIBLE);
            mTypeRl.setVisibility(View.GONE);
            mLeverageRl.setVisibility(View.VISIBLE);
            mPositionLl.setVisibility(View.VISIBLE);
            mOpenPriceLl.setVisibility(View.VISIBLE);
            mClosePriceLl.setVisibility(View.VISIBLE);
            mProfitValueLl.setVisibility(View.GONE);
            mProfitRateLl.setVisibility(View.GONE);

            mMarginRl.setVisibility(View.VISIBLE);
            mLiquidationPriceRl.setVisibility(View.GONE);
            mPositionValueRl.setVisibility(View.VISIBLE);
            mPLRl.setVisibility(View.VISIBLE);
            mProfitRateRl.setVisibility(View.VISIBLE);
            mInitialMarginRateRl.setVisibility(View.GONE);
            mMaintenanceMarginRateRl.setVisibility(View.GONE);
            mTargetClosePriceRl.setVisibility(View.GONE);

        } else if (mType == 1) {
            mDirectionRl.setVisibility(View.VISIBLE);
            mTypeRl.setVisibility(View.GONE);
            mLeverageRl.setVisibility(View.VISIBLE);
            mPositionLl.setVisibility(View.VISIBLE);
            mOpenPriceLl.setVisibility(View.VISIBLE);
            mClosePriceLl.setVisibility(View.GONE);
            mProfitValueLl.setVisibility(View.GONE);
            mProfitRateLl.setVisibility(View.GONE);

            mMarginRl.setVisibility(View.GONE);
            mLiquidationPriceRl.setVisibility(View.VISIBLE);
            mPositionValueRl.setVisibility(View.VISIBLE);
            mPLRl.setVisibility(View.GONE);
            mProfitRateRl.setVisibility(View.GONE);
            mInitialMarginRateRl.setVisibility(View.VISIBLE);
            mMaintenanceMarginRateRl.setVisibility(View.VISIBLE);
            mTargetClosePriceRl.setVisibility(View.GONE);

        } else if (mType == 2) {
            mDirectionRl.setVisibility(View.VISIBLE);
            mTypeRl.setVisibility(View.VISIBLE);
            mLeverageRl.setVisibility(View.VISIBLE);
            mPositionLl.setVisibility(View.VISIBLE);
            mOpenPriceLl.setVisibility(View.VISIBLE);
            mClosePriceLl.setVisibility(View.GONE);
            if (mProfitType == 0) {
                mProfitValueLl.setVisibility(View.VISIBLE);
                mProfitRateLl.setVisibility(View.GONE);
            } else if (mProfitType == 1) {
                mProfitValueLl.setVisibility(View.GONE);
                mProfitRateLl.setVisibility(View.VISIBLE);
            }

            mMarginRl.setVisibility(View.VISIBLE);
            mLiquidationPriceRl.setVisibility(View.GONE);
            mPositionValueRl.setVisibility(View.GONE);
            mPLRl.setVisibility(View.GONE);
            mProfitRateRl.setVisibility(View.GONE);
            mInitialMarginRateRl.setVisibility(View.GONE);
            mMaintenanceMarginRateRl.setVisibility(View.GONE);
            mTargetClosePriceRl.setVisibility(View.VISIBLE);

        }
    }

    private void updatePrice(EditText etPrice) {
        String price = etPrice.getText().toString();
        price = price.replace(",", ".");

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        String price_unit = contract.getPx_unit();
        if (price_unit.contains(".")) {
            int price_index = price_unit.length() - price_unit.indexOf(".") - 1;
            if (price_index == 1) {
                price_index = 0;
            }

            if (price.contains(".")) {
                int index = price.indexOf(".");
                if (index + price_index < price.length()) {
                    price = price.substring(0, index + price_index);
                    etPrice.setText(price);
                }
            }

        } else {
            if (price.contains(".")) {
                price = price.replace(".", "");
                etPrice.setText(price);
            }
        }

        etPrice.setSelection(price.length());
    }

    private void updateVol(EditText etVolum) {
        String vol = etVolum.getText().toString();
        vol = vol.replace(",", ".");

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        String vol_unit = contract.getQty_unit();
        if (vol_unit.contains(".")) {
            int vol_index = vol_unit.length() - vol_unit.indexOf(".");
            if (vol.contains(".")) {
                int index = vol.indexOf(".");
                if (index + vol_index < vol.length()) {
                    vol = vol.substring(0, index + vol_index);
                    etVolum.setText(vol);
                }
            }
        } else {
            if (vol.contains(".")) {
                vol = vol.replace(".", "");
                etVolum.setText(vol);
            }
        }

        etVolum.setSelection(vol.length());
    }


    private void updateValue(EditText etValue) {
        String vol = etValue.getText().toString();
        vol = vol.replace(",", ".");

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }


        String value_unit = contract.getValue_unit();
        if (value_unit.contains(".")) {
            int vol_index = value_unit.length() - value_unit.indexOf(".");
            if (vol.contains(".")) {
                int index = vol.indexOf(".");
                if (index + vol_index < vol.length()) {
                    vol = vol.substring(0, index + vol_index);
                    etValue.setText(vol);
                }
            }
        } else {
            if (vol.contains(".")) {
                vol = vol.replace(".", "");
                etValue.setText(vol);
            }
        }

        etValue.setSelection(vol.length());
    }


    private void updateRate(EditText etValue) {
        String vol = etValue.getText().toString();
        vol = vol.replace(",", ".");

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }


        String value_unit = "0.01";
        if (value_unit.contains(".")) {
            int vol_index = value_unit.length() - value_unit.indexOf(".");
            if (vol.contains(".")) {
                int index = vol.indexOf(".");
                if (index + vol_index < vol.length()) {
                    vol = vol.substring(0, index + vol_index);
                    etValue.setText(vol);
                }
            }
        } else {
            if (vol.contains(".")) {
                vol = vol.replace(".", "");
                etValue.setText(vol);
            }
        }

        etValue.setSelection(vol.length());
    }

    private void doDirection() {
        Integer defaultIndex = mDirection;

        List<Pair<String, Object>> directionMap = new ArrayList<>();
        directionMap.add(new Pair<String, Object>(getString(R.string.sl_str_long), 0));
        directionMap.add(new Pair<String, Object>(getString(R.string.sl_str_short), 1));

        PickPopupWindow popWin = new PickPopupWindow(getActivity(), directionMap, defaultIndex, new PickPopupWindow.PickListener() {
            @Override
            public void onPickData(String text, Object data) {
                mDirection = (Integer) data;
                mDirectionTv.setText(text);
            }
        });

        popWin.setOutsideTouchable(true);
        popWin.setBackgroundDrawable(new BitmapDrawable());
        popWin.showAtLocation(mDirectionRl, Gravity.BOTTOM, 0, 0);
    }

    private void doType() {
        Integer defaultIndex = mProfitType;

        List<Pair<String, Object>> typeMap = new ArrayList<>();
        typeMap.add(new Pair<String, Object>(getString(R.string.sl_str_profit_value), 0));
        typeMap.add(new Pair<String, Object>(getString(R.string.sl_str_profit_rate), 1));

        PickPopupWindow popWin = new PickPopupWindow(getActivity(), typeMap, defaultIndex, new PickPopupWindow.PickListener() {
            @Override
            public void onPickData(String text, Object data) {
                mProfitType = (Integer) data;
                mTypeTv.setText(text);
                if (mProfitType == 0) {
                    mProfitValueLl.setVisibility(View.VISIBLE);
                    mProfitRateLl.setVisibility(View.GONE);
                } else if (mProfitType == 1) {
                    mProfitValueLl.setVisibility(View.GONE);
                    mProfitRateLl.setVisibility(View.VISIBLE);
                }
            }
        });

        popWin.setOutsideTouchable(true);
        popWin.setBackgroundDrawable(new BitmapDrawable());
        popWin.showAtLocation(mTypeRl, Gravity.BOTTOM, 0, 0);
    }

    private void doLeverage() {

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        int minLeverage = Integer.parseInt(contract.getMin_leverage());
        int maxLeverage = Integer.parseInt(contract.getMax_leverage());

        Map<Integer, Integer> leverageMap = new HashMap<>();
        leverageMap.put(100, 0);
        leverageMap.put(50, 1);
        leverageMap.put(20, 2);
        leverageMap.put(10, 3);

        Integer defaultIndex = leverageMap.get(mLeverage);

        final List<Pair<String, Object>> leverages = new ArrayList<>();
        if (100 <= maxLeverage && 100 >= minLeverage) leverages.add(new Pair<String, Object>("100X", 100));
        if (50 <= maxLeverage && 50 >= minLeverage) leverages.add(new Pair<String, Object>("50X", 50));
        if (20 <= maxLeverage && 20 >= minLeverage) leverages.add(new Pair<String, Object>("20X", 20));
        if (10 <= maxLeverage && 10 >= minLeverage) leverages.add(new Pair<String, Object>("10X", 10));

        PickPopupWindow popWin = new PickPopupWindow(getActivity(), leverages, defaultIndex, new PickPopupWindow.PickListener() {
            @Override
            public void onPickData(String text, Object data) {
                mLeverage = (Integer) data;
                mLeverageTv.setText(text);
            }
        });

        popWin.setOutsideTouchable(true);
        popWin.setBackgroundDrawable(new BitmapDrawable());
        popWin.showAtLocation(mLeverageRl, Gravity.BOTTOM, 0, 0);
    }

    private void doCalculate() {
        Contract contractBasic = LogicGlobal.getContractBasic(mContractId);
        if (contractBasic == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(mContractId);
        if (contract == null) {
            return;
        }

        DecimalFormat dfNormal = NumberUtil.getDecimal(-1);

        if (mType == 0) {
            String vol = mPositionEt.getText().toString();
            String openPrice = mOpenPriceEt.getText().toString();
            String closePrice = mClosePriceEt.getText().toString();
            if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice) || TextUtils.isEmpty(closePrice)) {
                ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_miss_param));
                return;
            }

            double value = ContractCalculate.CalculateContractValue(
                    vol,
                    openPrice,
                    contract);

            double margin = ContractCalculate.CalculateIM(dfNormal.format(value), mLeverage, contractBasic);

            ContractOrder contractOrder = new ContractOrder();
            contractOrder.setLeverage(mLeverage);
            contractOrder.setQty(vol);
            contractOrder.setPosition_type(1);
            contractOrder.setPx(openPrice);
            contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);

            double profitRate = 0.0; //未实现盈亏
            double profitAmount = 0.0; //未实现盈亏额
            if (mDirection == 0) {
                contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);

                profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                        vol,
                        openPrice,
                        closePrice,
                        contract.getFace_value(),
                        contract.isReserve());

                profitRate = MathHelper.div(profitAmount, margin) * 100;
            } else if (mDirection == 1) {
                contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);

                profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                        vol,
                        openPrice,
                        closePrice,
                        contract.getFace_value(),
                        contract.isReserve());

                profitRate = MathHelper.div(profitAmount, margin) * 100;
            }

            mMarginTv.setText(dfNormal.format(margin) + contract.getMargin_coin());
            mPositionValueTv.setText(dfNormal.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());
            mPLTv.setText(dfNormal.format(MathHelper.round(profitAmount, contract.getValue_index())) + contract.getMargin_coin());
            mProfitRateTv.setText(dfNormal.format(MathHelper.round(profitRate, 2)) + "%");

        } else if (mType == 1) {
            String vol = mPositionEt.getText().toString();
            String openPrice = mOpenPriceEt.getText().toString();
            if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice)) {
                ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_miss_param));
                return;
            }

            double value = ContractCalculate.CalculateContractValue(
                    vol,
                    openPrice,
                    contract);

            ContractOrder contractOrder = new ContractOrder();
            contractOrder.setInstrument_id(mContractId);
            contractOrder.setLeverage(mLeverage);
            contractOrder.setQty(vol);
            contractOrder.setPosition_type(1);
            contractOrder.setPx(openPrice);
            contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
            if (mDirection == 0) {
                contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);
            } else if (mDirection == 1) {
                contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);
            }

            double liquidationPrice = ContractCalculate.CalculateOrderLiquidatePrice(contractOrder, null, contractBasic);
            double IMR = ContractCalculate.CalculateIMR(dfNormal.format(value), contractBasic);
            double MMR = ContractCalculate.CalculateMMR(dfNormal.format(value), contractBasic);

            mLiquidationPriceTv.setText(dfNormal.format(MathHelper.round(liquidationPrice, contract.getPrice_index())) + contract.getQuote_coin());
            mPositionValueTv.setText(dfNormal.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());
            mInitialMarginRateTv.setText(IMR + "");
            mMaintenanceMarginRateTv.setText(MMR + "");
        } else if (mType == 2) {
            if (mProfitType == 0) {
                String vol = mPositionEt.getText().toString();
                String openPrice = mOpenPriceEt.getText().toString();
                String profitValue = mProfitValueEt.getText().toString();
                if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice) || TextUtils.isEmpty(profitValue)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_miss_param));
                    return;
                }

                double value = ContractCalculate.CalculateContractValue(
                        vol,
                        openPrice,
                        contract);

                double margin = ContractCalculate.CalculateIM(dfNormal.format(value), mLeverage, contractBasic);

                ContractOrder contractOrder = new ContractOrder();
                contractOrder.setInstrument_id(mContractId);
                contractOrder.setLeverage(mLeverage);
                contractOrder.setQty(vol);
                contractOrder.setPosition_type(1);
                contractOrder.setPx(openPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);
                if (mDirection == 0) {
                    contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);
                } else if (mDirection == 1) {
                    contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);
                }

                double targetPrice = ContractCalculate.CalculateOrderTargetPriceValue(contractOrder, profitValue, mProfitType, contractBasic);

                mMarginTv.setText(dfNormal.format(margin) + contract.getMargin_coin());
                mTargetClosePriceTv.setText(dfNormal.format(MathHelper.round(targetPrice, contract.getPrice_index())) + contract.getQuote_coin());

            } else if (mProfitType == 1) {
                String vol = mPositionEt.getText().toString();
                String openPrice = mOpenPriceEt.getText().toString();
                String profitRate = mProfitRateEt.getText().toString();
                if (TextUtils.isEmpty(vol) || TextUtils.isEmpty(openPrice) || TextUtils.isEmpty(profitRate)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, getString(R.string.sl_str_miss_param));
                    return;
                }

                double value = ContractCalculate.CalculateContractValue(
                        vol,
                        openPrice,
                        contract);

                double margin = ContractCalculate.CalculateIM(dfNormal.format(value), mLeverage, contractBasic);

                ContractOrder contractOrder = new ContractOrder();
                contractOrder.setInstrument_id(mContractId);
                contractOrder.setLeverage(mLeverage);
                contractOrder.setQty(vol);
                contractOrder.setPosition_type(1);
                contractOrder.setPx(openPrice);
                contractOrder.setCategory(ContractOrder.ORDER_CATEGORY_NORMAL);

                if (mDirection == 0) {
                    contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG);
                } else if (mDirection == 1) {
                    contractOrder.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT);
                }

                double targetPrice = ContractCalculate.CalculateOrderTargetPriceValue(contractOrder, profitRate, mProfitType, contractBasic);

                mMarginTv.setText(dfNormal.format(margin) + contract.getMargin_coin());
                mTargetClosePriceTv.setText(dfNormal.format(MathHelper.round(targetPrice, contract.getPrice_index())) + contract.getQuote_coin());

            }
        }
    }
}
