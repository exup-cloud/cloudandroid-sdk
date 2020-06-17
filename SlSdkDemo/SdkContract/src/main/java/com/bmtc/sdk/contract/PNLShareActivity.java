package com.bmtc.sdk.contract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.base.BaseActivity;
import com.bmtc.sdk.contract.common.SlLoadingDialog;
import com.bmtc.sdk.contract.uiLogic.LogicContractSetting;
import com.bmtc.sdk.contract.utils.SaveImageTask;
import com.bmtc.sdk.contract.utils.ShareToolUtil;
import com.contract.sdk.ContractPublicDataAgent;
import com.contract.sdk.ContractUserDataAgent;
import com.contract.sdk.data.Contract;
import com.contract.sdk.data.ContractPosition;
import com.contract.sdk.data.ContractTicker;
import com.contract.sdk.extra.Contract.ContractCalculate;
import com.contract.sdk.utils.MathHelper;
import com.contract.sdk.utils.NumberUtil;
import com.contract.sdk.utils.SDKLogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zj on 2018/3/8.
 */

public class PNLShareActivity extends BaseActivity {

    private static final int REQUEST_CODE = 100; // 请求码

    private String mCoincode;
    private long mPositionId;
    private ContractPosition mContractPosition;

    private double profitRate = 0.0; //未实现盈亏
    private double profitAmount = 0.0; //未实现盈亏额

    private ViewPager vp_layout;
    private LayoutInflater inflater;
    private ItemAdapter itemAdapter;
    private List<View> viewList = new ArrayList<>();
    private int index = 0;
    private LinearLayout ll_share_container_layout;
    private SlLoadingDialog loadingDialog;

    public static void show(Activity activity,String coin_code,int position_id){
        Intent intent = new Intent(activity, PNLShareActivity.class);
        intent.putExtra("coin_code", coin_code);
        intent.putExtra("position_id",position_id);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl_activity_pnl_share);
        inflater = LayoutInflater.from(this);
        try {
            mCoincode = getIntent().getStringExtra("coin_code");
            mPositionId = getIntent().getLongExtra("position_id", 0);
        } catch (Exception ignored) {
        }


        setView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            mPositionId = getIntent().getLongExtra("position_id", 0);
        } catch (Exception ignored) {
        }

    }


    @Override
    public void setView() {
        super.setView();
        loadingDialog = new SlLoadingDialog(PNLShareActivity.this);
        for (int i = 0 ; i < 2 ; i ++){
            viewList.add(createItemView(i));
        }
        vp_layout = findViewById(R.id.vp_layout);
        vp_layout.setPageMargin(50);
        itemAdapter = new ItemAdapter(viewList);
        vp_layout.setAdapter(itemAdapter);
        vp_layout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ll_share_container_layout = findViewById(R.id.ll_share_container_layout);



        findViewById(R.id.common_cancel_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PNLShareActivity.this.finish();
            }
        });


        findViewById(R.id.share_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View shotView = updateShareContainerLayout();
                loadingDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        View dView = shotView;
                        dView.setDrawingCacheEnabled(true);
                        dView.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
                        ShareToolUtil.sendLocalShare(PNLShareActivity.this,ShareToolUtil.WX_SEND_PEOPLE,bitmap);
                    }
                },2000);
            }
        });
        findViewById(R.id.share_weixin_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View shotView = updateShareContainerLayout();
                loadingDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        View dView = shotView;
                        dView.setDrawingCacheEnabled(true);
                        dView.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
                        ShareToolUtil.sendLocalShare(PNLShareActivity.this,ShareToolUtil.WX_SEND_FIRENDS,bitmap);
                    }
                },2000);

            }
        });
//
//
        findViewById(R.id.share_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View shotView = updateShareContainerLayout();
                loadingDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        View dView = shotView;
                        dView.setDrawingCacheEnabled(true);
                        dView.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());

                        new SaveImageTask(PNLShareActivity.this.getApplication()).execute(bitmap);
                    }
                },2000);
            }
        });
//        updateData();
//    }


    }

    private View updateShareContainerLayout() {
        ll_share_container_layout.removeAllViews();
        View view  = createItemView(index);
        final View shotView = inflater.inflate(R.layout.sl_item_share_shot_layout,null);
        LinearLayout linearLayout = shotView.findViewById(R.id.ll_item_layout);
        linearLayout.addView(view);
        ll_share_container_layout.addView(shotView);
        return shotView;
    }

    private View createItemView(int index) {
        View itemView  =  inflater.inflate(R.layout.sl_view_share_item_layout, null,true);
//        if (TextUtils.isEmpty(mCoincode)) {
//            return itemView;
//        }
        ImageView iv_logo_icon = itemView.findViewById(R.id.iv_logo_icon);
        TextView tv_intro = itemView.findViewById(R.id.tv_intro);
        TextView tv_name = itemView.findViewById(R.id.tv_name);
        TextView tv_type = itemView.findViewById(R.id.tv_type);
        TextView tv_contract_value = itemView.findViewById(R.id.tv_contract_value);
        TextView tv_latest_price = itemView.findViewById(R.id.tv_latest_price);
        TextView tv_latest_price_value = itemView.findViewById(R.id.tv_latest_price_value);
        TextView tv_open_price_value = itemView.findViewById(R.id.tv_open_price_value);
        TextView tv_earned = itemView.findViewById(R.id.tv_earned);
        ImageView iv_qrcode = itemView.findViewById(R.id.iv_qrcode);
        TextView tv_where = itemView.findViewById(R.id.tv_where);
        TextView tv_how = itemView.findViewById(R.id.tv_how);
        TextView tv_share_type = itemView.findViewById(R.id.tv_share_type);
        TextView tv_share_time = itemView.findViewById(R.id.tv_share_time);

        SDKLogUtil.INSTANCE.d("lb",index+"---");
        if(index == 0){
            tv_share_type.setText(getResources().getString(R.string.sl_str_earned_rate));
        }else {
            tv_share_type.setText(getResources().getString(R.string.sl_str_earned_amount));
        }

        tv_share_time.setText("2019-09-03 24:00:00");

        List<ContractPosition> contractPositions = ContractUserDataAgent.INSTANCE.getCoinPositions(mCoincode,false);
        if (contractPositions != null) {
            for (int i = 0; i < contractPositions.size(); i++) {
                ContractPosition position = contractPositions.get(i);
                if (position == null) {
                    continue;
                }
                if (position.getPid() == mPositionId) {
                    mContractPosition = position;
                    break;
                }
            }
        }

        if (mContractPosition != null) {
            Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mContractPosition.getInstrument_id());
            if (contract == null) {
                return itemView;
            }
            ContractTicker contractTicker =  ContractPublicDataAgent.INSTANCE.getContractTicker(mContractPosition.getInstrument_id());
            if (contractTicker == null) {
                return itemView;
            }
            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);
            int pnl_calculate = LogicContractSetting.getPnlCalculate(this);
            int position_type = mContractPosition.getSide();
            if (position_type == 1) { //多仓
                tv_type.setText(R.string.sl_str_open_long);
               // tv_type.setTextColor(getResources().getColor(R.color.colorGreen));

                profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                        mContractPosition.getCur_qty(),
                        mContractPosition.getAvg_cost_px(),
                        (pnl_calculate == 0) ? contractTicker.getFair_px() : contractTicker.getLast_px(),
                        contract.getFace_value(),
                        contract.isReserve());

                double p = MathHelper.add(mContractPosition.getCur_qty(), mContractPosition.getClose_qty());
                double plus = MathHelper.mul(
                        MathHelper.round(mContractPosition.getTax()),
                        MathHelper.div(MathHelper.round(mContractPosition.getCur_qty()), p));
                //profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mContractPosition.getOim()), plus)) * 100;

                profitRate =ContractCalculate.INSTANCE.calculateProfitByAmount(String.valueOf(profitAmount),mContractPosition.getOim()) * 100;

            } else if (position_type == 2) { //空仓
                tv_type.setText(R.string.sl_str_open_short);
               // tv_type.setTextColor(getResources().getColor(R.color.colorRed2));

                profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                        mContractPosition.getCur_qty(),
                        mContractPosition.getAvg_cost_px(),
                        (pnl_calculate == 0) ? contractTicker.getFair_px() : contractTicker.getLast_px(),
                        contract.getFace_value(),
                        contract.isReserve());

                double p = MathHelper.add(mContractPosition.getCur_qty(), mContractPosition.getClose_qty());
                double plus = MathHelper.mul(
                        MathHelper.round(mContractPosition.getTax()),
                        MathHelper.div(MathHelper.round(mContractPosition.getCur_qty()), p));
                //profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mContractPosition.getOim()), plus)) * 100;

                profitRate =ContractCalculate.INSTANCE.calculateProfitByAmount(String.valueOf(profitAmount),mContractPosition.getOim()) * 100;
            }

            tv_contract_value.setText(contract.getSymbol());


            if (LogicContractSetting.getPnlCalculate(this) == 0) {
                tv_latest_price.setText(R.string.sl_str_fair_price);
                tv_latest_price_value.setText(dfDefault.format(MathHelper.round(contractTicker.getFair_px(), contract.getPrice_index())));
            } else {
                tv_latest_price.setText(R.string.sl_str_latest_price);
                tv_latest_price_value.setText(dfDefault.format(MathHelper.round(contractTicker.getLast_px(), contract.getPrice_index())));
            }

            tv_open_price_value.setText(dfDefault.format(MathHelper.round(mContractPosition.getAvg_open_px(), contract.getPrice_index())));
            if(index == 0){
                tv_earned.setText(NumberUtil.getDecimal(2).format(MathHelper.round(profitRate, contract.getValue_index())) + "%");
            }else {
                tv_earned.setText(NumberUtil.getDecimal(-1).format(MathHelper.round(profitAmount, contract.getValue_index())) + contract.getMargin_coin());
            }

            tv_earned.setTextColor(profitRate >= 0.0 ? getResources().getColor(R.color.sl_colorGreen2) : getResources().getColor(R.color.sl_colorRed2));

//            Account account = BTAccount.getInstance().getActiveAccount();
//            if (account != null) {
//                if (!TextUtils.isEmpty(account.getAccount_name())) {
//                    tv_name.setText(account.getAccount_name());
//                }
//                if (LogicLanguage.isZhEnv(this)) {
//                    Bitmap bmp = QRCodeUtil.createQRCodeBitmap(BTConstants.BTURL_REBATE_REGISTER_CN + account.getUid(), 480, 480);
//                    iv_qrcode.setImageBitmap(bmp);
//
//                    tv_where.setText(R.string.str_on_web);
//                    tv_how.setText(profitRate >= 0.0 ? R.string.str_earned : R.string.str_losed);
//                } else {
//                    Bitmap bmp = QRCodeUtil.createQRCodeBitmap(BTConstants.BTURL_REBATE_REGISTER_EN + account.getUid(), 480, 480);
//                    iv_qrcode.setImageBitmap(bmp);
//
//                    tv_where.setText(profitRate >= 0.0 ? R.string.str_earned : R.string.str_losed);
//                    tv_how.setText(R.string.str_on_web);
//                }
//            }

            if (profitRate > 0) {
                List<String> stringList = new ArrayList<>();
                stringList.add(getString(R.string.sl_str_win_intro1));
                stringList.add(getString(R.string.sl_str_win_intro2));
                stringList.add(getString(R.string.sl_str_win_intro3));
                stringList.add(getString(R.string.sl_str_win_intro4));
                stringList.add(getString(R.string.sl_str_win_intro5));

                Random random = new Random();
                int num = random.nextInt(stringList.size());
                tv_intro.setText(stringList.get(num));

                //iv_logo_icon.setImageResource(R.drawable.sl_icon_share_heads);
                tv_type.setBackgroundResource(R.drawable.sl_border_green_fill);
            } else if (profitRate < 0) {
                List<String> stringList = new ArrayList<>();
                stringList.add(getString(R.string.sl_str_lose_intro1));
                stringList.add(getString(R.string.sl_str_lose_intro2));
                stringList.add(getString(R.string.sl_str_lose_intro3));
                stringList.add(getString(R.string.sl_str_lose_intro4));
                stringList.add(getString(R.string.sl_str_lose_intro5));

                Random random = new Random();
                int num = random.nextInt(stringList.size());
                tv_intro.setText(stringList.get(num));

               // iv_logo_icon.setImageResource(R.drawable.icon_share_heads);
                tv_type.setBackgroundResource(R.drawable.sl_border_red_fill);
            } else {
                List<String> stringList = new ArrayList<>();
                stringList.add(getString(R.string.sl_str_tied_intro1));
                stringList.add(getString(R.string.sl_str_tied_intro2));
                stringList.add(getString(R.string.sl_str_tied_intro3));

                Random random = new Random();
                int num = random.nextInt(stringList.size());
                tv_intro.setText(stringList.get(num));

               // iv_logo_icon.setImageResource(R.drawable.icon_share_heads);
                tv_type.setBackgroundResource(R.drawable.sl_border_green_fill);
            }

        }else {
            Contract contract = ContractPublicDataAgent.INSTANCE.getContract(mCoincode);
            if (contract == null) {
                return itemView;
            }

            ContractTicker contractTicker = ContractPublicDataAgent.INSTANCE.getContractTicker(contract.getInstrument_id());
            if (contractTicker == null) {
                return itemView;
            }

            DecimalFormat dfDefault = NumberUtil.getDecimal(-1);

            tv_type.setText(R.string.sl_str_open_long);
            //tv_type.setTextColor(getResources().getColor(R.color.colorGreen3));
            tv_type.setBackgroundResource(R.drawable.sl_border_green_fill);

            tv_contract_value.setText(contract.getSymbol());
            if (LogicContractSetting.getPnlCalculate(this) == 0) {
                tv_latest_price.setText(R.string.sl_str_fair_price);
                tv_latest_price_value.setText(dfDefault.format(MathHelper.round(contractTicker.getFair_px(), contract.getPrice_index())));
            } else {
                tv_latest_price.setText(R.string.sl_str_latest_price);
                tv_latest_price_value.setText(dfDefault.format(MathHelper.round(contractTicker.getLast_px(), contract.getPrice_index())));
            }
            tv_open_price_value.setText(dfDefault.format(MathHelper.round(contractTicker.getLast_px(), contract.getPrice_index())));
            if(index == 0){
                tv_earned.setText("0.00%");
                tv_earned.setTextColor(getResources().getColor(R.color.sl_colorGreen2));
            }else {
                tv_earned.setText("0.00");
                tv_earned.setTextColor(getResources().getColor(R.color.sl_colorGreen2));
            }


          //  Account account = BTAccount.getInstance().getActiveAccount();
//            if (account != null) {
//                if (!TextUtils.isEmpty(account.getAccount_name())) {
//                    tv_name.setText(account.getAccount_name());
//                }
//
//                if (LogicLanguage.isZhEnv(this)) {
//                    Bitmap bmp = QRCodeUtil.createQRCodeBitmap(BTConstants.BTURL_REBATE_REGISTER_CN + account.getUid(), 480, 480);
//                    iv_qrcode.setImageBitmap(bmp);
//
//                    tv_where.setText(R.string.str_on_web);
//                    tv_how.setText(R.string.str_earned);
//                } else {
//                    Bitmap bmp = QRCodeUtil.createQRCodeBitmap(BTConstants.BTURL_REBATE_REGISTER_EN + account.getUid(), 480, 480);
//                    iv_qrcode.setImageBitmap(bmp);
//
//                    tv_where.setText(R.string.str_earned);
//                    tv_how.setText(R.string.str_on_web);
//                }
//            }
             List<String> stringList = new ArrayList<>();
            stringList.add(getString(R.string.sl_str_tied_intro1));
            stringList.add(getString(R.string.sl_str_tied_intro2));
            stringList.add(getString(R.string.sl_str_tied_intro3));

            Random random = new Random();
            int num = random.nextInt(stringList.size());
            tv_intro.setText(stringList.get(num));

            //iv_logo_icon.setImageResource(R.drawable.icon_share_heads);
        }
        return itemView;
    }




    public class ItemAdapter extends PagerAdapter {
        private List<View> myViewList;

        public ItemAdapter(List<View> myViewList) {
            this.myViewList = myViewList;
        }

        @Override
        public int getCount() {
            return myViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(myViewList.get(position));
            return myViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(myViewList.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return 0.9f;
        }
    }
}
