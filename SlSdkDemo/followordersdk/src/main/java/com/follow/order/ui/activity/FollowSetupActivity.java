package com.follow.order.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.follow.order.FollowOrderSDK;
import com.follow.order.R;
import com.follow.order.base.MVPBaseActivity;
import com.follow.order.bean.FollowOptionBean;
import com.follow.order.event.FollowRefreshEvent;
import com.follow.order.net.RxRetrofitClient;
import com.follow.order.presenter.FollowSetupPresenter;
import com.follow.order.presenter.contract.FollowSetupContract;
import com.follow.order.utils.StringUtil;
import com.follow.order.widget.shape.RoundLinearLayout;
import com.follow.order.widget.shape.RoundTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

public class FollowSetupActivity extends MVPBaseActivity<FollowSetupContract.View, FollowSetupPresenter> implements FollowSetupContract.View {
    private ImageButton ibBack;
    private TextView tvUserDivide;
    private TextView tvKolDivide;
    private RoundTextView tvCoin;
    private RoundTextView tvExchange;
    private RoundTextView tvWeight;
    private RoundTextView tvModel;
    private TextView tv_equal;
    private RoundLinearLayout ll_usdt;
    private TextView tvWeightDesc;
    private EditText etAmount;
    private TextView tvFollowUnit;
    private TextView tvUsdt;
    private TextView tvBalance;
    private CheckBox cbLoss;
    private TextView tvLossRemove;
    private EditText etLossPrecent;
    private TextView tvLossAdd;
    private CheckBox cbProfit;
    private TextView tvProfitRemove;
    private EditText etProfitPrecent;
    private TextView tvProfitAdd;
    private TextView tvTips;
    private RoundTextView btnFollowOrder;
    private RadioGroup rg_follow_config;
    private String uid, master_currency_id;
    private int lossInit = 50;
    private int lossMin = 20;
    private int lossMax = 80;
    private int lossOffset = 10;
    private int profitInit = 100;
    private int profitMin = 50;
    private int profitMax = 500;
    private int profitOffset = 20;
    private int follow_immediately = 1;
    private FollowOptionBean optionBean;
    private PublishSubject<String> mPublishSubject;

    public static void start(Context context, String uid, String master_currency_id) {
        Intent intent = new Intent(context, FollowSetupActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("master_currency_id", master_currency_id);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_follow_setup);
        ibBack = findViewById(R.id.ib_back);
        tvUserDivide = findViewById(R.id.tv_user_divide);
        tvKolDivide = findViewById(R.id.tv_kol_divide);
        tvCoin = findViewById(R.id.tv_coin);
        tvExchange = findViewById(R.id.tv_exchange);
        tvWeight = findViewById(R.id.tv_weight);
        tvModel = findViewById(R.id.tv_model);
        tv_equal = findViewById(R.id.tv_equal);
        ll_usdt = findViewById(R.id.ll_usdt);
        tvWeightDesc = findViewById(R.id.tv_weight_desc);
        etAmount = findViewById(R.id.et_amount);
        tvFollowUnit = findViewById(R.id.tv_follow_unit);
        tvUsdt = findViewById(R.id.tv_usdt);
        tvBalance = findViewById(R.id.tv_balance);
        cbLoss = findViewById(R.id.cb_loss);
        tvLossRemove = findViewById(R.id.tv_loss_remove);
        etLossPrecent = findViewById(R.id.et_loss_precent);
        tvLossAdd = findViewById(R.id.tv_loss_add);
        cbProfit = findViewById(R.id.cb_profit);
        tvProfitRemove = findViewById(R.id.tv_profit_remove);
        etProfitPrecent = findViewById(R.id.et_profit_precent);
        tvProfitAdd = findViewById(R.id.tv_profit_add);
        tvTips = findViewById(R.id.tv_tips);
        btnFollowOrder = findViewById(R.id.btn_follow_order);
        rg_follow_config = findViewById(R.id.rg_follow_config);
    }

    @Override
    protected void initListener() {
        ibBack.setOnClickListener(this);
        tvLossRemove.setOnClickListener(this);
        tvLossAdd.setOnClickListener(this);
        tvProfitRemove.setOnClickListener(this);
        tvProfitAdd.setOnClickListener(this);
        btnFollowOrder.setOnClickListener(this);
        rg_follow_config.setOnClickListener(this);
        etLossPrecent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || TextUtils.isEmpty(s.toString())) {
                    return;
                }
                String input = s.toString();
                if (!s.toString().endsWith("%")) {
                    etLossPrecent.setText(input + "%");
                    etLossPrecent.setSelection(etLossPrecent.getText().toString().length() - 1);
                }
            }
        });
        etProfitPrecent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || TextUtils.isEmpty(s.toString())) {
                    return;
                }
                String input = s.toString();
                if (!s.toString().endsWith("%")) {
                    etProfitPrecent.setText(input + "%");
                    etProfitPrecent.setSelection(etProfitPrecent.getText().toString().length() - 1);
                }
            }
        });
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    tvUsdt.setText("");
                }
                if (mPublishSubject != null && !TextUtils.isEmpty(s.toString()) && optionBean != null && optionBean.getTrade_type() != 2) {
                    mPublishSubject.onNext(s.toString());
                }
            }
        });
        rg_follow_config.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_follow_yes) {
                    follow_immediately = 1;
                } else if (checkedId == R.id.rb_follow_no) {
                    follow_immediately = 0;
                }
            }
        });
    }

    @Override
    protected void initData() {
        uid = getIntent().getStringExtra("uid");
        master_currency_id = getIntent().getStringExtra("master_currency_id");
        mPresenter.getFollowOption(master_currency_id);
        initConvertUsdt();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ib_back) {
            back();
        } else if (v.getId() == R.id.tv_loss_add) {
            addLossPercent();
        } else if (v.getId() == R.id.tv_loss_remove) {
            removeLossPercent();
        } else if (v.getId() == R.id.tv_profit_add) {
            addProfitPercent();
        } else if (v.getId() == R.id.tv_profit_remove) {
            removeProfitPercent();
        } else if (v.getId() == R.id.btn_follow_order) {
            followOrderClick();
        }
    }

    @Override
    public void showFollowOption(FollowOptionBean optionBean) {
        if (optionBean == null) {
            return;
        }
        this.optionBean = optionBean;
        if (optionBean.getTarget_profit() != null) {
            lossInit = optionBean.getTarget_profit().getStop_deficit();
            lossMin = optionBean.getTarget_profit().getStop_deficit_min();
            lossMax = optionBean.getTarget_profit().getStop_deficit_max();
            lossOffset = optionBean.getTarget_profit().getStop_deficit_offset();
            profitInit = optionBean.getTarget_profit().getStop_profit();
            profitMin = optionBean.getTarget_profit().getStop_profit_min();
            profitMax = optionBean.getTarget_profit().getStop_profit_max();
            profitOffset = optionBean.getTarget_profit().getStop_profit_offset();

            if (optionBean.getTarget_profit().getStop_deficit_force() == 1) {
                cbLoss.setEnabled(false);
                cbLoss.setChecked(true);
            } else {
                cbLoss.setEnabled(true);
                cbLoss.setChecked(true);
            }
            if (optionBean.getTarget_profit().getStop_profit_force() == 1) {
                cbProfit.setText(getString(R.string.fo_follow_setup_text_28));
                cbProfit.setEnabled(false);
                cbProfit.setChecked(true);
            } else {
                cbProfit.setText(getString(R.string.fo_follow_setup_text_8));
                cbProfit.setEnabled(true);
                cbProfit.setChecked(false);
            }
        }
        if (optionBean.getTrade_type() == 2) {//金本位
            tv_equal.setVisibility(View.GONE);
            ll_usdt.setVisibility(View.GONE);
        } else {
            tv_equal.setVisibility(View.VISIBLE);
            ll_usdt.setVisibility(View.VISIBLE);
        }
        tvUserDivide.setText(optionBean.getUser_commission());
        tvKolDivide.setText(optionBean.getKol_commission());
        tvCoin.setText(optionBean.getCurrency());
        tvExchange.setText(FollowOrderSDK.ins().getFollowOrderProxy().getAppName());
        tvWeight.setText(optionBean.getFollow_ratio());
        tvModel.setText(optionBean.getTrade_mode());
        etAmount.setHint(getString(R.string.fo_follow_setup_text_24, String.valueOf(optionBean.getMin_limit()), String.valueOf(optionBean.getMax_limit())));
        tvFollowUnit.setText(optionBean.getTrade_currency());

        etLossPrecent.setText(lossInit + "%");
        etLossPrecent.setSelection(etLossPrecent.getText().toString().length() - 1);
        etProfitPrecent.setText(profitInit + "%");
        etProfitPrecent.setSelection(etProfitPrecent.getText().toString().length() - 1);
        tvTips.setText(optionBean.getTips());
        mPresenter.getAccountBalance(optionBean.getTrade_currency());
    }

    @Override
    public void showAccountBalance(String balance) {
        if (!TextUtils.isEmpty(balance) && optionBean != null) {
            tvBalance.setText(StringUtil.formatBalance(balance) + " " + optionBean.getTrade_currency());
        }
    }

    @Override
    public void showUsdt(String usdt) {
        tvUsdt.setText(usdt);
    }

    @Override
    public void startFollowSuccess() {
        EventBus.getDefault().post(new FollowRefreshEvent());
        back();
    }

    private void addLossPercent() {
        double current = getCurrentPercent(etLossPrecent);
        double newPercent = current + lossOffset;
        if (newPercent > lossMax) {
            newPercent = lossMax;
            toast(getString(R.string.fo_follow_setup_text_20, lossMax + "") + "%");
        }
        etLossPrecent.setText(StringUtil.formatPercent(newPercent) + "%");
        etLossPrecent.setSelection(etLossPrecent.getText().toString().length() - 1);
    }

    private void removeLossPercent() {
        double current = getCurrentPercent(etLossPrecent);
        double newPercent = current - lossOffset;
        if (newPercent < lossMin) {
            newPercent = lossMin;
            toast(getString(R.string.fo_follow_setup_text_21, lossMin + "") + "%");
        }
        etLossPrecent.setText(StringUtil.formatPercent(newPercent) + "%");
        etLossPrecent.setSelection(etLossPrecent.getText().toString().length() - 1);
    }

    private void addProfitPercent() {
        double current = getCurrentPercent(etProfitPrecent);
        double newPercent = current + profitOffset;
        if (newPercent > profitMax) {
            newPercent = profitMax;
            toast(getString(R.string.fo_follow_setup_text_22, profitMax + "") + "%");
        }
        etProfitPrecent.setText(StringUtil.formatPercent(newPercent) + "%");
        etProfitPrecent.setSelection(etProfitPrecent.getText().toString().length() - 1);
    }

    private void removeProfitPercent() {
        double current = getCurrentPercent(etProfitPrecent);
        double newPercent = current - profitOffset;
        if (newPercent < profitMin) {
            newPercent = profitMin;
            toast(getString(R.string.fo_follow_setup_text_23, profitMin + "") + "%");
        }
        etProfitPrecent.setText(StringUtil.formatPercent(newPercent) + "%");
        etProfitPrecent.setSelection(etProfitPrecent.getText().toString().length() - 1);
    }

    private double getCurrentPercent(EditText editText) {
        double percent = 0;
        String input = editText.getText().toString();
        if (TextUtils.isEmpty(input) || TextUtils.equals(input, "%")) {
            return percent;
        }
        if (input.endsWith("%")) {
            input = input.substring(0, input.length() - 1);
        }
        percent = Double.parseDouble(input);
        return percent;

    }

    private void followOrderClick() {
        if (optionBean == null) {
            return;
        }
        String amount = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            toast(getString(R.string.fo_follow_setup_text_29));
            return;
        }
//        try {
//            double num = Double.parseDouble(amount);
//            if (num < Double.parseDouble(optionBean.getMin_limit()) || num > Double.parseDouble(optionBean.getMax_limit())) {
//                toast(getString(R.string.fo_follow_setup_text_24, String.valueOf(optionBean.getMin_limit()), String.valueOf(optionBean.getMax_limit())));
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        double currentLoss = getCurrentPercent(etLossPrecent);
//        if (cbLoss.isChecked()) {
//            if (currentLoss > lossMax) {
//                toast(getString(R.string.fo_follow_setup_text_20, lossMax + "") + "%");
//                return;
//            }
//            if (currentLoss < lossMin) {
//                toast(getString(R.string.fo_follow_setup_text_21, lossMin + "") + "%");
//                return;
//            }
//        }
//
        double currentProfit = getCurrentPercent(etProfitPrecent);
//        if (cbProfit.isChecked()) {
//            if (currentProfit > profitMax) {
//                toast(getString(R.string.fo_follow_setup_text_22, profitMax + "") + "%");
//                return;
//            }
//            if (currentProfit < profitMin) {
//                toast(getString(R.string.fo_follow_setup_text_23, profitMin + "") + "%");
//                return;
//            }
//        }

        mPresenter.startFollow(master_currency_id, uid, optionBean.getExchange(), amount, cbLoss.isChecked() ? 1 : 0, StringUtil.formatPercent(currentLoss), cbProfit.isChecked() ? 1 : 0, StringUtil.formatPercent(currentProfit), follow_immediately, optionBean.getSymbol(), optionBean.getCurrency(), optionBean.getTrade_currency());
    }


    private void initConvertUsdt() {
        mPublishSubject = PublishSubject.create();
        mPublishSubject.debounce(1000, TimeUnit.MILLISECONDS)
                .switchMap(new Function<String, ObservableSource<HashMap<String, String>>>() {
                    @Override
                    public ObservableSource<HashMap<String, String>> apply(String s) throws Exception {
                        Observable<HashMap<String, String>> observable = null;
                        if (optionBean != null) {
                            observable = RxRetrofitClient.getInstance().convertUsdt(optionBean.getSymbol(), s);
                        }
                        return observable;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HashMap<String, String>>() {
                    @Override
                    public void accept(HashMap<String, String> data) throws Exception {
                        if (data != null) {
                            String price = data.get("price");
                            tvUsdt.setText(price);
                        }

                    }
                });


    }

}
