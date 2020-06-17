package com.bmtc.sdk.contract.uiLogic;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bmtc.sdk.contract.R;
import com.bmtc.sdk.contract.utils.UtilSystem;
import com.contract.sdk.ContractSDKAgent;
import com.contract.sdk.data.DepthData;
import com.contract.sdk.utils.MathHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;


/**
 * Created by zhoujing on 2017/10/19.
 */

public class LogicBuySell {

    private static int STATE_ORDER_TYPE = 20001;
    private static int STATE_DECIMAL = 20002;
    private static int STATE_ASK_BID = 20003;
    public static int STATE_BUY_SELL = 20004;

    public static int ASK_BID_DEFAULT = 0;
    public static int ASK_BID_ASK = 1;
    public static int ASK_BID_BID = 2;

    public static int ORDER_TYPE_LIMIT = 0;
    public static int ORDER_TYPE_MARKET = 1;
    public static int ORDER_TYPE_PLAN = 2;
    public static int ORDER_BID_PRICE = 3;
    public static int ORDER_ASK_PRICE = 4;

    public interface IBuySellListener {
        void onSwitchTab(int param, String stock_code);
        void onDecimalSelected(int decimals);
        void onOrderTypeSelected(int type);
        void onAskBidSelected(int type);
    }

    private static LogicBuySell instance = null;

    public static LogicBuySell getInstance(){
        if (null == instance)
            instance = new LogicBuySell();
        return instance;
    }

    private List<IBuySellListener> mListeners = new ArrayList<>();

    private Animation mRotate;

    private LogicBuySell(){
        mRotate = AnimationUtils.loadAnimation(ContractSDKAgent.INSTANCE.getContext(), R.anim.array_rotate);
        mRotate.setInterpolator(new LinearInterpolator());
    }

    public void registListener(IBuySellListener listener){

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount<mListeners.size(); iCount++){
            if(listener.equals(mListeners.get(iCount)))
                break;
        }

        if(iCount >= mListeners.size())
            mListeners.add(listener);
    }


    public void unregistListener(IBuySellListener listener){

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount<mListeners.size(); iCount++){
            if(listener.equals(mListeners.get(iCount))){
                mListeners.remove(mListeners.get(iCount));
                return;
            }
        }
    }

    private void refresh(int type, int param){
        for (int i = 0; i<mListeners.size(); i++){
            if(mListeners.get(i) != null){
                if (type == STATE_ORDER_TYPE) {
                    mListeners.get(i).onOrderTypeSelected(param);
                } else if (type == STATE_DECIMAL) {
                    mListeners.get(i).onDecimalSelected(param);
                } else if (type == STATE_ASK_BID) {
                    mListeners.get(i).onAskBidSelected(param);
                }
            }
        }
    }

    public void switchTab(int param, String stock_code){
        for (int i = 0; i<mListeners.size(); i++){
            if(mListeners.get(i) != null){
                mListeners.get(i).onSwitchTab(param, stock_code);
            }
        }
    }


    private ListView mMalsListView;
    private PopupWindow mMalsWindow;
    private DropMalsAdapter mMalsAdapter;
    private View mMalsPopupView;
    private List<String> mMals = new ArrayList<>();


    class DropMalsAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private int mResId;
        private List<String> mItems;

        public DropMalsAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropMalsViewHolder holder;
            if (convertView == null) {
                holder = new DropMalsViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvText = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropMalsViewHolder)convertView.getTag();
            holder.tvText.setText(getItem(position) + mContext.getString(R.string.sl_str_mals));
            return convertView;
        }

        class DropMalsViewHolder {
            TextView tvText;
        }
    }

    private ListView mOrderListView;
    private PopupWindow mOrderWindow;
    private DropOrderAdapter mOrderAdapter;
    private View mOrderPopupView;
    private List<String> mOrderTypes = new ArrayList<>();

    public void showOrderTypeWindow(final Context context, View parent, boolean supportMarket, boolean supportPlan) {
        mOrderTypes.clear();
        mOrderTypes.add(context.getString(R.string.sl_str_limit_entrust));
        if (supportMarket) {
            mOrderTypes.add(context.getString(R.string.sl_str_market_entrust));
        }
        if (supportPlan) {
            mOrderTypes.add(context.getString(R.string.sl_str_plan_entrust));
        }

        if (mOrderWindow != null && mOrderWindow.isShowing()) {
            mOrderWindow.dismiss();
        }

        if (mOrderAdapter == null) {
            int itemAccountId = R.layout.sl_item_drop_text;
            mOrderAdapter = new DropOrderAdapter(context, itemAccountId, mOrderTypes);
        }

        mOrderPopupView = LayoutInflater.from(context).inflate(R.layout.sl_view_dropdown, null);
        mOrderListView = mOrderPopupView.findViewById(R.id.lv_list);
        mOrderListView.setAdapter(mOrderAdapter);
        mOrderListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mOrderTypes.get(i).equals(context.getString(R.string.sl_str_limit_entrust))) {
                    LogicBuySell.getInstance().refresh(STATE_ORDER_TYPE, ORDER_TYPE_LIMIT);
                } else if (mOrderTypes.get(i).equals(context.getString(R.string.sl_str_market_entrust))) {
                    LogicBuySell.getInstance().refresh(STATE_ORDER_TYPE, ORDER_TYPE_MARKET);
                } else if (mOrderTypes.get(i).equals(context.getString(R.string.sl_str_plan_entrust))) {
                    LogicBuySell.getInstance().refresh(STATE_ORDER_TYPE, ORDER_TYPE_PLAN);
                }
                mOrderWindow.dismiss();
            }
        });

        int listPadding = UtilSystem.dip2px(context, 5);
        int itemHeight = UtilSystem.dip2px(context, 40);
        int windowHeight = itemHeight * mOrderTypes.size() + listPadding * 2;
        int min = itemHeight + listPadding * 2;
        int max = itemHeight * 10;
        if (windowHeight > max) {
            windowHeight = max;
        } else if (windowHeight < min) {
            windowHeight = min;
        }
        mOrderWindow = new PopupWindow(mOrderPopupView, parent.getWidth()*2, windowHeight);
        mOrderWindow.setOutsideTouchable(true);
        mOrderWindow.setBackgroundDrawable(new BitmapDrawable());

        mOrderWindow.setFocusable(true);

        final int anchorLoc[] = new int[2];
        parent.getLocationOnScreen(anchorLoc);
        mOrderWindow.showAsDropDown(parent, 0,5);
    }

    class DropOrderAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private int mResId;
        private List<String> mItems;

        public DropOrderAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropOrderViewHolder holder;
            if (convertView == null) {
                holder = new DropOrderViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvText = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropOrderViewHolder)convertView.getTag();
            holder.tvText.setText(getItem(position));
            return convertView;
        }

        class DropOrderViewHolder {
            TextView tvText;
        }
    }


    private ListView mABListView;
    private PopupWindow mABWindow;
    private DropABAdapter mABAdapter;
    private View mABPopupView;
    private List<String> mAskBids = new ArrayList<>();

    public void showABWindow(Context context, View parent, final View animateView) {
        if (mAskBids.size() == 0) {
            mAskBids.add(context.getString(R.string.sl_str_default));
            mAskBids.add(context.getString(R.string.sl_str_ask));
            mAskBids.add(context.getString(R.string.sl_str_bid));
        }

        if (mABWindow != null && mABWindow.isShowing()) {
            mABWindow.dismiss();
        }

        if (mABAdapter == null) {
            int itemAccountId = R.layout.sl_item_drop_text_small;
            mABAdapter = new DropABAdapter(context, itemAccountId, mAskBids);
        }

        mABPopupView = LayoutInflater.from(context).inflate(R.layout.sl_view_dropdown, null);
        mABListView = mABPopupView.findViewById(R.id.lv_list);
        mABListView.setAdapter(mABAdapter);
        mABListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogicBuySell.getInstance().refresh(STATE_ASK_BID, i);
                mABWindow.dismiss();
            }
        });

        int listPadding = UtilSystem.dip2px(context, 5);
        int itemHeight = UtilSystem.dip2px(context, 30);
        int windowHeight = itemHeight * mAskBids.size() + listPadding * 2;
        int min = itemHeight + listPadding * 2;
        int max = itemHeight * 10;
        if (windowHeight > max) {
            windowHeight = max;
        } else if (windowHeight < min) {
            windowHeight = min;
        }
        mABWindow = new PopupWindow(mABPopupView, parent.getWidth(), windowHeight);
        mABWindow.setOutsideTouchable(true);
        mABWindow.setBackgroundDrawable(new BitmapDrawable());
        mABWindow.setFocusable(true);
        mABWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mRotate.setFillAfter(false);
                animateView.startAnimation(mRotate);
            }
        });

        mRotate.setFillAfter(true);
        animateView.startAnimation(mRotate);

        final int anchorLoc[] = new int[2];
        parent.getLocationOnScreen(anchorLoc);
        mABWindow.showAtLocation(parent, Gravity.TOP | Gravity.START, anchorLoc[0],anchorLoc[1]-windowHeight);
    }

    class DropABAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private int mResId;
        private List<String> mItems;

        public DropABAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            mResId = textViewResourceId;
            mItems = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            DropABViewHolder holder;
            if (convertView == null) {
                holder = new DropABViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(mResId, parent, false);
                holder.tvText = convertView.findViewById(R.id.tv_text);
                convertView.setTag(holder);
            }
            holder = (DropABViewHolder)convertView.getTag();
            holder.tvText.setText(getItem(position));
            return convertView;
        }

        class DropABViewHolder {
            TextView tvText;
        }
    }
}
