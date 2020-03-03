package com.bmtc.sdk.contract.adapter;

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

import com.bmtc.sdk.contract.HtmlActivity;
import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.dialog.WipedOutIntroduceWindow;
import com.bmtc.sdk.library.common.dialog.PromptWindow;
import com.bmtc.sdk.library.constants.BTConstants;
import com.bmtc.sdk.library.contract.ContractCalculate;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.ContractLiqRecord;
import com.bmtc.sdk.library.trans.data.ContractOrder;
import com.bmtc.sdk.library.uilogic.LogicGlobal;
import com.bmtc.sdk.library.utils.MathHelper;
import com.bmtc.sdk.library.utils.NumberUtil;
import com.bmtc.sdk.library.utils.ToastUtil;

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

public class ContractEntrustHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ContractOrder> mNews = new ArrayList<>();

    public static class ContractEntrustHistoryHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvContractName;
        TextView tvStatus;
        TextView tvDetails;
        TextView tvCategory;
        TextView tvTime;
        TextView tvEntrustVolume;
        TextView tvVolume;
        TextView tvEntrustPrice;
        TextView tvDealPrice;
        TextView tvEntrustValue;
        ImageView ivDetails;
        ImageView ivDetails2;

        public ContractEntrustHistoryHolder(View itemView, int type) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tv_type);
            tvContractName = itemView.findViewById(R.id.tv_contract_name);
            tvStatus = itemView.findViewById(R.id.tv_open_type);
            ivDetails = itemView.findViewById(R.id.iv_detail);
            ivDetails2 = itemView.findViewById(R.id.iv_detail2);
            tvDetails = itemView.findViewById(R.id.tv_detail);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvEntrustVolume = itemView.findViewById(R.id.tv_entrust_volume_value);
            tvVolume = itemView.findViewById(R.id.tv_volume_value);
            tvEntrustPrice = itemView.findViewById(R.id.tv_entrust_price_value);
            tvDealPrice = itemView.findViewById(R.id.tv_deal_price_value);
            tvEntrustValue = itemView.findViewById(R.id.tv_entrust_value_value);
        }
    }

    public ContractEntrustHistoryAdapter(Context context) {
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

        itemViewHolder.tvContractName.setText(contract.getSymbol());
        itemViewHolder.tvEntrustVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getQty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvVolume.setText(dfVol.format(MathHelper.round(mNews.get(position).getCum_qty())) + mContext.getString(R.string.sl_str_contracts_unit));
        itemViewHolder.tvEntrustPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getPx(), contract.getPrice_index())) + contract.getQuote_coin());
        itemViewHolder.tvDealPrice.setText(dfDefault.format(MathHelper.round(mNews.get(position).getAvg_px(), contract.getPrice_index())) + contract.getQuote_coin());

        double value = ContractCalculate.CalculateContractValue(
                mNews.get(position).getQty(),
                mNews.get(position).getPx(),
                contract);

        itemViewHolder.tvEntrustValue.setText(dfDefault.format(MathHelper.round(value, contract.getValue_index())) + contract.getMargin_coin());

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

        double done_vol = MathHelper.round(mNews.get(position).getCum_qty(), 8);

        int errno = mNews.get(position).getErrno();
        if (errno == ContractOrder.ORDER_ERRNO_NOERR) {
            itemViewHolder.tvStatus.setText(R.string.sl_str_order_complete);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorTextSelector));
        } else if (errno == ContractOrder.ORDER_ERRNO_CANCEL) {
            if (done_vol > 0) {
                itemViewHolder.tvStatus.setText(R.string.sl_str_order_part_filled);
                itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorYellowNormal));
            } else {
                itemViewHolder.tvStatus.setText(R.string.sl_str_user_canceled);
                itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
            }
        } else {
            itemViewHolder.tvStatus.setText(R.string.sl_str_system_canceled);
            itemViewHolder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.sl_colorRed));
        }

        if (errno >= ContractOrder.ORDER_ERRNO_TIMEOUT) {
            itemViewHolder.ivDetails.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.ivDetails.setVisibility(View.GONE);
        }

        final int category = mNews.get(position).getCategory();
        if ((category & 127) == 1) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_limit_entrust);
        } else if ((category & 127) == 2) {
            itemViewHolder.tvCategory.setText(R.string.sl_str_market_entrust);
            itemViewHolder.tvEntrustPrice.setText(R.string.sl_str_market_price_simple);
            itemViewHolder.tvEntrustValue.setText(R.string.sl_str_market_price_simple);
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


        if ((category & 128) > 0) { //第7位为1表示:强平委托单
            itemViewHolder.tvDetails.setText(R.string.sl_str_force_close_details);
            itemViewHolder.tvDetails.setVisibility(View.VISIBLE);
            itemViewHolder.ivDetails2.setVisibility(View.VISIBLE);
            itemViewHolder.tvEntrustPrice.setText("--");
            itemViewHolder.tvDealPrice.setText("--");

        } else if ((category & 256) > 0) { //第8位为1表示:爆仓委托单
            itemViewHolder.tvDetails.setText(R.string.sl_str_bankruptcy_details);
            itemViewHolder.tvDetails.setVisibility(View.VISIBLE);
            itemViewHolder.ivDetails2.setVisibility(View.VISIBLE);
            itemViewHolder.tvEntrustPrice.setText("--");
            itemViewHolder.tvDealPrice.setText("--");

        } else if ((category & 512) > 0){ //第9位为1表示:自动减仓委托单
            if (MathHelper.round(mNews.get(position).getTake_fee()) > 0) {
                itemViewHolder.tvDetails.setText(R.string.sl_str_force_close_details);
                itemViewHolder.tvDetails.setVisibility(View.VISIBLE);
                itemViewHolder.ivDetails2.setVisibility(View.VISIBLE);
                itemViewHolder.tvEntrustPrice.setText("--");
                itemViewHolder.tvDealPrice.setText("--");

            } else {
                itemViewHolder.tvDetails.setText(R.string.sl_str_reduce_position_details);
                itemViewHolder.tvDetails.setVisibility(View.VISIBLE);
                itemViewHolder.ivDetails2.setVisibility(View.VISIBLE);
            }
        } else {
            itemViewHolder.tvDetails.setVisibility(View.GONE);
            itemViewHolder.ivDetails2.setVisibility(View.GONE);
        }

        itemViewHolder.tvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryDetail(view, mNews.get(position));
            }
        });

        itemViewHolder.ivDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryDetail(view, mNews.get(position));
            }
        });

        itemViewHolder.ivDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryDetailCancel(view, mNews.get(position));
            }
        });
    }

    private void queryDetailCancel(final View view, final ContractOrder order) {

        String content = "";
        int errno = order.getErrno();

        switch (errno)  {
            case 2: content = mContext.getString(R.string.sl_str_system_cancel_reason2); break;
            case 3: content = mContext.getString(R.string.sl_str_system_cancel_reason3); break;
            case 4: content = mContext.getString(R.string.sl_str_system_cancel_reason4); break;
            case 5: content = mContext.getString(R.string.sl_str_system_cancel_reason5); break;
            case 6: content = mContext.getString(R.string.sl_str_system_cancel_reason6); break;
            case 7: content = mContext.getString(R.string.sl_str_system_cancel_reason7); break;
            case 8: content = mContext.getString(R.string.sl_str_system_cancel_reason8); break;
            case 9: content = mContext.getString(R.string.sl_str_system_cancel_reason9); break;
            case 10: content = mContext.getString(R.string.sl_str_trigger_failed_reason10); break;
            case 11: content = mContext.getString(R.string.sl_str_trigger_failed_reason11); break;
            default: content = mContext.getString(R.string.sl_str_system_canceled); break;
        }

        final PromptWindow window = new PromptWindow(mContext);
        window.showTitle(mContext.getString(R.string.sl_str_system_canceled));
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

    private void queryDetail(final View view, final ContractOrder order) {
        BTContract.getInstance().liqRecord(order.getOid(), new IResponse<List<ContractLiqRecord>>() {
            @Override
            public void onResponse(String errno, String message, List<ContractLiqRecord> data) {
                if (!TextUtils.equals(errno, BTConstants.ERRNO_OK) || !TextUtils.equals(message, BTConstants.ERRNO_SUCCESS)) {
                    ToastUtil.shortToast(LogicGlobal.sContext, message);
                    return;
                }

                if (data != null && data.size() > 0) {
                    ContractLiqRecord liqRecord = data.get(0);
                    for (ContractLiqRecord info : data){
                        if(info.getOid() == order.getOid()){
                            liqRecord = info;
                            break;
                        }
                    }

                    String created_at = "";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    try {
                        String create_at = liqRecord.getCreated_at();
                        create_at = create_at.substring(0, create_at.lastIndexOf(".")) + "Z";
                        Date date = sdf.parse(create_at);

                        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        created_at = gmtFormat.format(date);
                    } catch (ParseException ignored) {
                    }

                    Contract contract = LogicGlobal.getContract(liqRecord.getInstrument_id());
                    if (contract == null) {
                        return;
                    }

                    String contractName = contract.getSymbol();
                    DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
                    DecimalFormat dfPrice = NumberUtil.getDecimal(contract.getPrice_index());

                    String positionName = "";
                    String priceChange = "";
                    if (order.getSide() == ContractOrder.CONTRACT_ORDER_WAY_BUY_CLOSE_SHORT) {
                        priceChange = mContext.getString(R.string.sl_str_rose);
                        positionName = contractName + "-" + mContext.getString(R.string.sl_str_buy_close);
                    } else if (order.getSide() == ContractOrder.CONTRACT_ORDER_WAY_SELL_CLOSE_LONG) {
                        priceChange = mContext.getString(R.string.sl_str_fall);
                        positionName = contractName + "-" + mContext.getString(R.string.sl_str_sell_close);
                    }

                    int type = liqRecord.getType();
                    if (type == 1 ) {    //部分强平
                        String intro1 = String.format(mContext.getString(R.string.sl_str_wiped_out_tips0),
                                created_at,
                                contractName,
                                priceChange + dfDefault.format(MathHelper.round(liqRecord.getTrigger_px(), contract.getPrice_index())) + contract.getQuote_coin(),
                                positionName,
                                MathHelper.round(MathHelper.mul(liqRecord.getMmr(), "100"), 2) + "%");
                        String intro2 = String.format(mContext.getString(R.string.sl_str_wiped_out_tips2),
                                liqRecord.getOrder_px() + contract.getQuote_coin());
                        final WipedOutIntroduceWindow window = new WipedOutIntroduceWindow(mContext);
                        window.showTitle(mContext.getString(R.string.sl_str_force_close_details));
                        window.setIntro(intro1, intro2);
                        window.showAtLocation(view, Gravity.CENTER, 0, 0);
                        window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                window.dismiss();
                                Intent intent = new Intent(mContext, HtmlActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("url", BTConstants.BTURL_CONTRACT_FORCE_CLOSE);
                                intent.putExtra("title", mContext.getString(R.string.sl_str_force_close_mechanism));
                                mContext.startActivity(intent);
                            }
                        });
                    } else if (type == 2 || type == 3) { //爆仓
                        String intro1 = String.format(mContext.getString(R.string.sl_str_wiped_out_tips1),
                                created_at,
                                contractName,
                                priceChange + dfDefault.format(MathHelper.round(liqRecord.getTrigger_px(), contract.getPrice_index())) + contract.getQuote_coin(),
                                positionName,
                                MathHelper.round(MathHelper.mul(liqRecord.getMmr(), "100"), 2) + "%");
                        String intro2 = String.format(mContext.getString(R.string.sl_str_wiped_out_tips2),
                                liqRecord.getOrder_px() + contract.getQuote_coin());
                        final WipedOutIntroduceWindow window = new WipedOutIntroduceWindow(mContext);
                        window.showTitle(mContext.getString(R.string.sl_str_bankruptcy_details));
                        window.setIntro(intro1, intro2);
                        window.showAtLocation(view, Gravity.CENTER, 0, 0);
                        window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                window.dismiss();
                                Intent intent = new Intent(mContext, HtmlActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("url", BTConstants.BTURL_CONTRACT_FORCE_CLOSE);
                                intent.putExtra("title", mContext.getString(R.string.sl_str_force_close_mechanism));
                                mContext.startActivity(intent);
                            }
                        });
                    } else if (type == 4) {
                        String intro1 = String.format(mContext.getString(R.string.sl_str_reduce_position_tips),
                                created_at,
                                dfDefault.format(MathHelper.round(liqRecord.getTrigger_px(), contract.getPrice_index())) + contract.getQuote_coin(),
                                dfDefault.format(MathHelper.round(liqRecord.getOrder_px(), contract.getPrice_index())) + contract.getQuote_coin());
                        final PromptWindow window = new PromptWindow(mContext);
                        window.showTitle(mContext.getString(R.string.sl_str_reduce_position_details));
                        window.showTvContent(intro1);
                        window.showBtnOk(mContext.getString(R.string.sl_str_automatically_reduce));
                        window.showBtnClose("");
                        window.showAtLocation(view, Gravity.CENTER, 0, 0);
                        window.getBtnOk().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                window.dismiss();
                                Intent intent = new Intent(mContext, HtmlActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("url", BTConstants.BTURL_CONTRACT_AUTO_REDUCE);
                                intent.putExtra("title", mContext.getString(R.string.sl_str_automatically_reduce));
                                mContext.startActivity(intent);
                            }
                        });
                        window.getBtnClose().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                window.dismiss();
                            }
                        });
                    }

                }
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sl_item_contract_entrust_history, parent, false);
        return new ContractEntrustHistoryHolder(v, viewType);
    }
}
