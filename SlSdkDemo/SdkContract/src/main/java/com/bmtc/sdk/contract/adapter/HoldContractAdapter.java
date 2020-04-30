package com.bmtc.sdk.contract.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmtc.sdk.contract.PNLShareActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.dialog.AddMarginWindow;
import com.bmtc.sdk.contract.dialog.CloseAllbyMarketPriceWindow;
import com.bmtc.sdk.contract.dialog.ClosePositionWindow;
import com.bmtc.sdk.library.common.dialog.PromptWindow;
import com.bmtc.sdk.library.common.pswkeyboard.OnPasswordInputFinish;
import com.bmtc.sdk.library.common.pswkeyboard.widget.PopEnterPassword;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractAccount;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.trans.data.ContractPosition;
import com.bmtc.sdk.library.trans.data.ContractTicker;
import com.bmtc.sdk.library.uilogic.LogicContractSetting;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.LogUtil;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.ToastUtil;
import com.bmtc.sdk.library.utils.UtilSystem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2018/3/8.
 */

public class HoldContractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractPosition> mNews = new ArrayList<>();

    public static class HoldContractHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvOpenType;
        TextView tvOpenPrice;
        TextView tvFloatingGains;
        TextView tvFloatingGainsBalance;
        TextView tvHoldings;
        TextView tvAmountCanbeLiquidated;
        TextView tvTagPrice;
        TextView tvForceClosePrice;
        TextView tvMargins;
        TextView tvLeverage;
        TextView tvGainsBalance;
        TextView tvAdjustMargins;
        TextView tvClosePositions;
        ImageView tvShare;

        public HoldContractHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvOpenType = itemView.findViewById(R.id.tv_open_type);
            tvOpenPrice = itemView.findViewById(R.id.tv_open_price_value);
            tvFloatingGains = itemView.findViewById(R.id.tv_floating_gains_value);
            tvFloatingGainsBalance = itemView.findViewById(R.id.tv_floating_gains_balance_value);
            tvHoldings = itemView.findViewById(R.id.tv_holdings_value);
            tvAmountCanbeLiquidated = itemView.findViewById(R.id.tv_amount_can_be_liquidated_value);
            tvTagPrice = itemView.findViewById(R.id.tv_tag_price_value);
            tvForceClosePrice = itemView.findViewById(R.id.tv_forced_close_price_value);
            tvMargins = itemView.findViewById(R.id.tv_margins_value);
            tvLeverage = itemView.findViewById(R.id.tv_leverage_value);
            tvGainsBalance = itemView.findViewById(R.id.tv_gains_balance_value);
            tvAdjustMargins = itemView.findViewById(R.id.tv_adjust_margins);
            tvClosePositions = itemView.findViewById(R.id.tv_close_position);
            tvShare = itemView.findViewById(R.id.iv_share);
        }
    }

    public HoldContractAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractPosition> news) {
        if (news == null) {
            return;
        }
        mNews = news;
    }

    public ContractPosition getItem(int position) {
        return mNews.get(position);
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HoldContractHolder itemViewHolder = (HoldContractHolder) holder;

        final Contract contractBasic = LogicGlobal.getContractBasic(mNews.get(position).getInstrument_id());
        final Contract contract = LogicGlobal.getContract(mNews.get(position).getInstrument_id());
        if (contract == null || contractBasic == null) {
            return;
        }

        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());

        double profitRate = 0.0; //未实现盈亏
        double profitAmount = 0.0; //未实现盈亏额
        ContractTicker contractTicker = LogicGlobal.getContractTicker(mNews.get(position).getInstrument_id());
        if (contractTicker == null) {
            return;
        }

        int pnl_calculate = LogicContractSetting.getPnlCalculate(mContext);
        int side = mNews.get(position).getSide();
        if (side == 1) { //多仓
            itemViewHolder.tvType.setText(R.string.sl_str_buy_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);

            profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                    mNews.get(position).getCur_qty(),
                    mNews.get(position).getAvg_cost_px(),
                    (pnl_calculate == 0) ? contractTicker.getFair_px() : contractTicker.getLast_px(),
                    contract.getFace_value(),
                    contract.isReserve());

            double p = MathHelper.add(mNews.get(position).getCur_qty(), mNews.get(position).getClose_qty());
            double plus = MathHelper.mul(
                    MathHelper.round(mNews.get(position).getTax()),
                    MathHelper.div(MathHelper.round(mNews.get(position).getCur_qty()), p));
            profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mNews.get(position).getIm()), plus)) * 100;

        } else if (side == 2) { //空仓
            itemViewHolder.tvType.setText(R.string.sl_str_sell_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);

            profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                    mNews.get(position).getCur_qty(),
                    mNews.get(position).getAvg_cost_px(),
                    (pnl_calculate == 0) ? contractTicker.getFair_px() : contractTicker.getLast_px(),
                    contract.getFace_value(),
                    contract.isReserve());

            double p = MathHelper.add(mNews.get(position).getCur_qty(), mNews.get(position).getClose_qty());
            double plus = MathHelper.mul(
                    MathHelper.round(mNews.get(position).getTax()),
                    MathHelper.div(MathHelper.round(mNews.get(position).getCur_qty()), p));
            profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mNews.get(position).getIm()), plus)) * 100;
        }

        double liqPrice = 0.0;  //强平价
        final int open_type = mNews.get(position).getPosition_type();
        if (open_type == 1) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_gradually_position);
            liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                    mNews.get(position), null, contractBasic);
            //计算杠杆
            itemViewHolder.tvLeverage.setText(ContractCalculate.CalculatePositionLeverage(mNews.get(position), contractBasic) + mContext.getString(R.string.sl_str_bei));
        } else if (open_type == 2) {
            itemViewHolder.tvOpenType.setText(R.string.sl_str_full_position);

            ContractAccount contractAccount = BTContract.getInstance().getContractAccount(contract.getMargin_coin());
            if (contractAccount != null) {
                liqPrice = ContractCalculate.CalculatePositionLiquidatePrice(
                        mNews.get(position), contractAccount, contractBasic);
            }
            //计算杠杆
            itemViewHolder.tvLeverage.setText(ContractCalculate.calculateRealPositionLeverage(mNews.get(position),contractAccount, contractBasic) + mContext.getString(R.string.sl_str_bei));
        }

        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvOpenPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getAvg_open_px(), contract.getPrice_index())) + contract.getQuote_coin());
        itemViewHolder.tvFloatingGains.setText(dfDefault.format(MathHelper.round(profitRate, contract.getValue_index())) + "%");
        itemViewHolder.tvFloatingGains.setTextColor(profitRate >= 0.0 ? mContext.getResources().getColor(R.color.sl_colorGreen) : mContext.getResources().getColor(R.color.sl_colorRed));
        itemViewHolder.tvFloatingGainsBalance.setText(dfDefault.format(MathHelper.round(profitAmount, contract.getValue_index())) + contract.getMargin_coin());
        itemViewHolder.tvHoldings.setText(dfVol.format(MathHelper.round(mNews.get(position).getCur_qty())) + mContext.getString(R.string.sl_str_contracts_unit));

        double amountCanbeLiquidated = MathHelper.sub(mNews.get(position).getCur_qty(), mNews.get(position).getFreeze_qty());
        itemViewHolder.tvAmountCanbeLiquidated.setText(dfVol.format(amountCanbeLiquidated) + mContext.getString(R.string.sl_str_contracts_unit));

        double value = ContractCalculate.CalculateContractValue(
                mNews.get(position).getCur_qty(),
                mNews.get(position).getAvg_open_px(),
                contract);

        itemViewHolder.tvTagPrice.setText(dfDefault.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());
        itemViewHolder.tvMargins.setText(dfDefault.format(MathHelper.round(mNews.get(position).getIm(), contract.getValue_index())) + contract.getMargin_coin());



        double profit = MathHelper.round(mNews.get(position).getEarnings());
        itemViewHolder.tvGainsBalance.setText(dfDefault.format(MathHelper.round(profit, contract.getValue_index())) + contract.getMargin_coin());
        itemViewHolder.tvForceClosePrice.setText(dfDefault.format(MathHelper.round(liqPrice, contract.getPrice_index()))+ contract.getQuote_coin());


        itemViewHolder.tvAdjustMargins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (open_type == 1) {
                if (mNews != null && mNews.size() > position) {
                    final AddMarginWindow window = new AddMarginWindow(mContext);
                    window.setContractPosition(mNews.get(position));
                    window.showAtLocation(view, Gravity.CENTER, 0, 0);
                }

//                } else if (open_type == 2) {
//                    ToastUtil.shortToast(LogicGlobal.sContext, mContext.getString(R.string.sl_str_full_position_no_adjust_margin));
//                }
            }
        });

        itemViewHolder.tvClosePositions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ClosePositionWindow window = new ClosePositionWindow(mContext);
                window.setContractPosition(mNews.get(position));
                window.showAtLocation(view, Gravity.CENTER, 0, 0);
                window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();

                        if (mNews != null && mNews.size() > position) {
                            String type = (mNews.get(position).getSide() == 1) ? mContext.getString(R.string.sl_str_buy_open) : mContext.getString(R.string.sl_str_sell_open);
                            String content = String.format(mContext.getString(R.string.sl_str_close_all_by_market_price_tips), contract.getSymbol() + type);
                            final PromptWindow promptWindow = new PromptWindow(mContext);
                            promptWindow.showTitle(mContext.getString(R.string.sl_str_close_all_by_market_price));
                            promptWindow.showTvContent(content);
                            promptWindow.showBtnOk(mContext.getString(R.string.sl_str_confirm));
                            promptWindow.showBtnCancel(mContext.getString(R.string.sl_str_cancel));
                            promptWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                            promptWindow.getBtnOk().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<ContractOrder> orderList = getEntrustOrders(mNews.get(position));
                                    if (orderList != null && orderList.size() > 0) {
                                        cancelOpenOrders(view, mNews.get(position), orderList);
                                    } else {
                                        closePositionByMarketPrice(view, mNews.get(position), "");
                                    }
                                    promptWindow.dismiss();
                                }
                            });
                            promptWindow.getBtnCancel().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    promptWindow.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });

        itemViewHolder.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PNLShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("coin_code", contract.getMargin_coin());
                intent.putExtra("position_id", mNews.get(position).getPid());
                mContext.startActivity(intent);
            }
        });
    }

    private List<ContractOrder> getEntrustOrders(final ContractPosition position) {
        if (position == null) {
            return null;
        }

        List<ContractOrder> orderList = BTContract.getInstance().getContractOrder(position.getInstrument_id());
        if (orderList == null || orderList.size() <= 0) {
            return null;
        }

        int position_type = position.getSide();
        List<ContractOrder> entrustList = new ArrayList<>();
        for (int i=0; i<orderList.size(); i++) {
            ContractOrder order = orderList.get(i);
            if (order == null) {
                continue;
            }

            if (position_type == ContractPosition.POSITION_TYPE_LONG) {
                if (order.getSide() == ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
                    entrustList.add(order);
                }
            } else if (position_type == ContractPosition.POSITION_TYPE_SHORT) {
                if (order.getSide() == ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
                    entrustList.add(order);
                }
            }
        }
        return entrustList;
    }

    private void closePositionByMarketPrice(final View view, final ContractPosition position, String pwd) {
        if (position == null || view == null) {
            return;
        }

        Contract contract = LogicGlobal.getContract(position.getInstrument_id());
        if (contract == null) {
            return;
        }

        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());
        double vol = MathHelper.sub(position.getCur_qty(), position.getFreeze_qty());


        ContractOrder order = new ContractOrder();
        order.setInstrument_id(position.getInstrument_id());
        order.setNonce(System.currentTimeMillis());
        order.setQty(dfVol.format(vol));
        LogUtil.d("lb","11position.getSide():"+position.getSide());
        if (position.getSide() == 1) {
            order.setPid(position.getPid());
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG);
        } else {
            order.setPid(position.getPid());
            order.setSide(ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT);
        }

        order.setCategory(ContractOrder.ORDER_CATEGORY_MARKET);

        view.setEnabled(false);
        IResponse<String> response = new IResponse<String>() {
            @Override
            public void onResponse(String errno, String message, String data) {
                view.setEnabled(true);

                if (TextUtils.equals(errno, BTConstants.ERRNO_PERMISSION_DENIED)) {
                    final PopEnterPassword popEnterPassword = new PopEnterPassword(mContext);
                    popEnterPassword.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    popEnterPassword.setOnFinishInput(new OnPasswordInputFinish() {
                        @Override
                        public void inputFinish(String password) {
                            closePositionByMarketPrice(view, position, UtilSystem.toMD5(password));
                            popEnterPassword.dismiss();
                        }
                    });
                    return;
                }

                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                    return;
                }

                ToastUtil.shortToast(LogicGlobal.sContext, LogicGlobal.sContext.getString(R.string.sl_str_order_submit_success));
                BTContract.getInstance().accounts(0, new IResponse<List<ContractAccount>>() {
                    @Override
                    public void onResponse(String errno, String message, List<ContractAccount> data) {

                    }
                });
            }
        };

        if (TextUtils.isEmpty(pwd)) {
            BTContract.getInstance().submitOrder(order, response);
        } else {
            BTContract.getInstance().submitOrder(order, response);
        }
    }

    private void cancelOpenOrders(final View view, final ContractPosition position, List<ContractOrder> orderList) {
        final CloseAllbyMarketPriceWindow window = new CloseAllbyMarketPriceWindow(mContext);
        window.setOrderList(position, orderList);
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
        window.getBtnCloseAll().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContractPosition newPosition = BTContract.getInstance().getContractPosition(position.getInstrument_id(), position.getSide());
                if (newPosition != null) {
                    closePositionByMarketPrice(view, newPosition, "");
                }
                window.dismiss();
            }
        });
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_hold_contract, parent, false);
        return new HoldContractHolder(v, viewType);
    }
}
