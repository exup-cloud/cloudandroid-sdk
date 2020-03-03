package com.bmtc.sdk.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.library.common.dialog.PromptWindow;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by zj on 2018/3/8.
 */

public class ContractPlanHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractOrder> mNews = new ArrayList<>();

    public static class ContractEntrustHistoryHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvStatus;
        ImageView ivDetails;
        TextView tvCategory;
        TextView tvTime;
        TextView tvVolume;
        TextView tvEntrustPrice;
        TextView tvTriggerPrice;
        TextView tvDeadline;
        TextView tvFinishTime;
        RelativeLayout rlFinishTime;

        public ContractEntrustHistoryHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvStatus = itemView.findViewById(R.id.tv_open_type);
            ivDetails = itemView.findViewById(R.id.iv_detail);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvVolume = itemView.findViewById(R.id.tv_volume_value);
            tvEntrustPrice = itemView.findViewById(R.id.tv_entrust_price_value);
            tvTriggerPrice = itemView.findViewById(R.id.tv_trigger_price_value);
            tvDeadline = itemView.findViewById(R.id.tv_finish_time_value);
            tvFinishTime = itemView.findViewById(R.id.tv_trigger_time_value);
            rlFinishTime = itemView.findViewById(R.id.rl_trigger_time);
        }
    }

    public ContractPlanHistoryAdapter(Context context) {
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
        final ContractEntrustHistoryHolder itemViewHolder = (ContractEntrustHistoryHolder) holder;

        Contract contract = LogicGlobal.getContract(mNews.get(position).getInstrument_id());
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

        String price_type = "";
        if (mNews.get(position).getTrigger_type() == 1) {
          price_type = mContext.getString(R.string.sl_str_latest_price_simple);
        } else if (mNews.get(position).getTrigger_type() == 2) {
            price_type = mContext.getString(R.string.sl_str_fair_price_simple);
        } else if (mNews.get(position).getTrigger_type() == 4) {
            price_type = mContext.getString(R.string.sl_str_index_price_simple);
        }
        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getQty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvEntrustPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getExec_px(), contract.getPrice_index())) + contract.getQuote_coin());
        itemViewHolder.tvTriggerPrice.setText(price_type + " " + dfDefault.format(MathHelper.round(mNews.get(position).getPx(), contract.getPrice_index())) + contract.getQuote_coin());

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

        try {
           String create_at = mNews.get(position).getFinished_at();
            if (create_at != null && mNews.get(position).getErrno() == ContractOrder.ORDER_ERRNO_NOERR) {
                itemViewHolder.rlFinishTime.setVisibility(View.VISIBLE);
                create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
                Date date = sdf.parse(create_at);

                DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                itemViewHolder.tvFinishTime.setText(gmtFormat.format(date));
            } else {
                itemViewHolder.rlFinishTime.setVisibility(View.GONE);
            }
        } catch (ParseException ignored) {
        }

        try {
            String create_at = mNews.get(position).getCreated_at();
            create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
            Date date = sdf.parse(create_at);

            DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            itemViewHolder.tvTime.setText(gmtFormat.format(date));


            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (mNews.get(position).getLife_cycle() == 24) {
                cal.add(Calendar.DATE, 1);
            } else {
                cal.add(Calendar.DATE, 7);
            }
            itemViewHolder.tvDeadline.setText(gmtFormat.format(cal.getTime()));

        } catch (ParseException ignored) {
        }



        int errno = mNews.get(position).getErrno();
        if (errno == ContractOrder.ORDER_ERRNO_NOERR) {
            itemViewHolder.tvStatus.setText(R.string.sl_str_order_complete);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorTextSelector));
        } else if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
            itemViewHolder.tvStatus.setText(R.string.sl_str_user_canceled);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
        } else if (errno == ContractOrder.ORDER_ERRNO_TIMEOUT) {
            itemViewHolder.tvStatus.setText(R.string.sl_str_order_timeout);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
        } else {
            itemViewHolder.tvStatus.setText(R.string.sl_str_trigger_failed);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
        }

        final int category = mNews.get(position).getCategory();
        if ((category & 127) == 1) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_limit_entrust);
        } else if ((category & 127) == 2) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_market_entrust);
            itemViewHolder.tvEntrustPrice.setText(R.string.sl_str_market_price_simple);
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

        if (errno >= ContractOrder.ORDER_ERRNO_ASSETS) {
            itemViewHolder.ivDetails.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.ivDetails.setVisibility(View.GONE);
        }

        itemViewHolder.ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryDetail(view, mNews.get(position));
            }
        });
    }

    private void queryDetail(final View view, final ContractOrder order) {

        Contract contract = LogicGlobal.getContract(order.getInstrument_id());
        if (contract == null) {
            return;
        }

        DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
        String time = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            String create_at = order.getFinished_at() == null ? order.getCreated_at() : order.getFinished_at();
            create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
            Date date = sdf.parse(create_at);

            DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = gmtFormat.format(date);
        } catch (ParseException ignored) {
        }

        String price_type = "";
        if (order.getTrigger_type() == 1) {
            price_type = mContext.getString(R.string.sl_str_latest_price_simple);
        } else if (order.getTrigger_type() == 2) {
            price_type = mContext.getString(R.string.sl_str_fair_price_simple);
        } else if (order.getTrigger_type() == 4) {
            price_type = mContext.getString(R.string.sl_str_index_price_simple);
        }

        String reason;
        String content = "";
        int errno = order.getErrno();
        switch (errno)  {
            case 3: reason = mContext.getString(R.string.sl_str_insufficient);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;
            case 4: reason = mContext.getString(R.string.sl_str_trigger_failed_reason4);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;
            case 6: reason = mContext.getString(R.string.sl_str_trigger_failed_reason6);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;
            case 7: reason = mContext.getString(R.string.sl_str_trigger_failed_reason7);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;
            case 8: reason = mContext.getString(R.string.sl_str_trigger_failed_reason8);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;
            case 9: reason = mContext.getString(R.string.sl_str_trigger_failed_reason9);
                content = String.format(mContext.getString(R.string.sl_str_trigger_failed_info),
                        time,
                        contract.getDisplayName(mContext), price_type,
                        dfDefault.format(MathHelper.round(order.getPx(), contract.getPrice_index())) + contract.getQuote_coin(),
                        reason);
                break;

            case 5: content = mContext.getString(R.string.sl_str_trigger_failed_reason5); break;
            case 10: content = mContext.getString(R.string.sl_str_trigger_failed_reason10); break;
            case 11: content = mContext.getString(R.string.sl_str_trigger_failed_reason11); break;
            case 12: content = mContext.getString(R.string.sl_str_trigger_failed_reason12); break;
            case 13: content = mContext.getString(R.string.sl_str_trigger_failed_reason13); break;
            default: content = mContext.getString(R.string.sl_str_trigger_failed_reason13); break;
        }

        final PromptWindow window = new PromptWindow(mContext);
        window.showTitle(mContext.getString(R.string.sl_str_trigger_failed));
        window.showTvContent(content);
        window.showBtnOk(mContext.getString(R.string.sl_str_confirm));
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
        window.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_plan_history, parent, false);
        return new ContractEntrustHistoryHolder(v, viewType);
    }
}
