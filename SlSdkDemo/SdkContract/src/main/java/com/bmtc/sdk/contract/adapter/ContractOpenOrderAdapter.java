package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.dialog.PromptWindow;
import com.bmtc.sdk.contract.utils.ToastUtil;
import com.bmtc.sdk.contract.utils.UtilSystem;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractOrder;
import com.contract.sdk.data.ContractOrders;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.impl.IResponse;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zj on 2018/3/8.
 */

public class ContractOpenOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractOrder> mNews = new ArrayList<>();

    public static class ContractOpenOrderHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvCancel;
        TextView tvCategory;
        TextView tvTime;
        TextView tvEntrustVolume;
        TextView tvVolume;
        TextView tvEntrustPrice;
        TextView tvEntrustValue;
        TextView tvDealPrice;

        public ContractOpenOrderHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvCancel = itemView.findViewById(R.id.tv_cancel);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvEntrustVolume = itemView.findViewById(R.id.tv_entrust_volume_value);
            tvVolume = itemView.findViewById(R.id.tv_volume_value);
            tvEntrustPrice = itemView.findViewById(R.id.tv_entrust_price_value);
            tvEntrustValue = itemView.findViewById(R.id.tv_entrust_value_value);
            tvDealPrice = itemView.findViewById(R.id.tv_deal_price_value);
        }
    }

    public ContractOpenOrderAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ContractOrder> news) {
        if (news == null) {
            mNews.clear();
            return;
        }
        mNews = news;
    }

    public ContractOrder getItem(int position) {
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
        final ContractOpenOrderHolder itemViewHolder = (ContractOpenOrderHolder) holder;

        Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mNews.get(position).getInstrument_id());
        if (contract == null) {
            return;
        }
        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());
        DecimalFormat dfValue = NumberUtil.getDecimal(contract.getValue_index());
        DecimalFormat dfVol = NumberUtil.getDecimal(contract.getVol_index());

        int way = mNews.get(position).getSide();
        if (way == ContractOrder.CONTRACT_ORDER_WAY_BUY_OPEN_LONG) {
            itemViewHolder.tvType.setText(R.string.sl_str_buy_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);
        } else if (way == ContractOrder.CONTRACT_ORDER_WAY_SELL_OPEN_SHORT) {
            itemViewHolder.tvType.setText(R.string.sl_str_sell_open);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);
        } else if (way == ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
            itemViewHolder.tvType.setText(R.string.sl_str_buy_close);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorGreen));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_green);
        } else if (way == ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
            itemViewHolder.tvType.setText(R.string.sl_str_sell_close);
            itemViewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            itemViewHolder.tvType.setBackgroundResource(R.drawable.sl_border_red);
        }

        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvEntrustVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getQty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getCum_qty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvEntrustPrice.setText(mNews.get(position).getPx() + contract.getQuote_coin());
        itemViewHolder.tvDealPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getAvg_px(), contract.getPrice_index())) + contract.getQuote_coin());

        double value = ContractCalculate.INSTANCE.CalculateContractValue(
                mNews.get(position).getQty(),
                mNews.get(position).getPx(),
                contract);

        itemViewHolder.tvEntrustValue.setText(dfDefault.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());

        final int category = mNews.get(position).getCategory();
        if ((category & 127) == 1) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_limit_entrust);
        } else if ((category & 127) == 2) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_market_entrust);
        } else if ((category & 127) == 3) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_stop_loss_entrust);
        } else if ((category & 127) == 4) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_stop_surplus_entrust);
        } else if ((category & 127) == 5) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_hide_entrust);
        } else if ((category & 127) == 6) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_iceberg_entrust);
        } else if ((category & 127) == 7) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_passive_entrust);
        } else if ((category & 127) == 8) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_trigger_entrust);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            String create_at = mNews.get(position).getCreated_at();
            create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
            Date date = sdf.parse(create_at);

            DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            itemViewHolder.tvTime.setText(gmtFormat.format(date));
        } catch (ParseException ignored) {
        }

        itemViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PromptWindow window = new PromptWindow(mContext);
                window.showTitle(mContext.getString(R.string.sl_str_tips));
                window.showTvContent(mContext.getString(R.string.sl_str_cancel_order_tips));
                window.showBtnOk(mContext.getString(R.string.sl_str_confirm));
                window.showBtnCancel(mContext.getString(R.string.sl_str_cancel));
                window.showAtLocation(view, Gravity.CENTER, 0, 0);
                window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();

                        if (mNews != null && mNews.size() > position) {
                            doCancel(view, mNews.get(position), "");
                        }
                    }
                });
                window.getBtnCancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });
            }
        });
    }

    private void doCancel(final View view, final ContractOrder order, String pwd) {
        if (order == null) {
            return ;
        }

        ContractOrders orders = new ContractOrders();
        orders.setContract_id(order.getInstrument_id());
        orders.getOrders().add(order);


        ContractUserDataAgent.INSTANCE.doCancelOrders(orders, new IResponse<List<Long>>() {
            @Override
            public void onSuccess(@NotNull List<Long> data) {
                if (data != null && data.size() > 0) {
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.sl_str_some_orders_cancel_failed));
                }
            }

            @Override
            public void onFail(@NotNull String code, @NotNull String msg) {
                ToastUtil.shortToast(mContext, msg);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_open_order, parent, false);
        return new ContractOpenOrderHolder(v, viewType);
    }
}
