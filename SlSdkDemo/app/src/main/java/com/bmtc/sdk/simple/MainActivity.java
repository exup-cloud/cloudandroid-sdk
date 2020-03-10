package com.bmtc.sdk.simple;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bmtc.sdk.contract.PNLShareActivity;
import com.bmtc.sdk.library.SLSDKAgent;
import com.bmtc.sdk.library.base.HttpRequestConfigs;
import com.bmtc.sdk.library.trans.BTContract;
import com.bmtc.sdk.library.trans.IResponse;
import com.bmtc.sdk.library.trans.data.Contract;
import com.bmtc.sdk.library.trans.data.SLUser;
import com.bmtc.sdk.library.uilogic.LogicSDKState;
import com.bmtc.sdk.library.uilogic.LogicWebSocketContract;
import com.bmtc.sdk.library.utils.PreferenceManager;
import com.bmtc.sdk.library.utils.ShareToolUtil;
import com.bmtc.sdk.simple.contract.UsdtActivity;

import java.util.ArrayList;
import java.util.List;

import static com.bmtc.sdk.library.uilogic.LogicSDKState.STATE_LOGIN;


public class MainActivity extends AppCompatActivity implements LogicWebSocketContract.IWebSocketListener, LogicSDKState.ISDKStateListener,View.OnClickListener {
    private TextView tv_usdt_contract,tv_coin_contract,tv_trade_contract;
    private TextView tv_login;
    private TextView tv_share,tv_share2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initSLSDKAgent();
    }

    private void initViews() {
        tv_usdt_contract = findViewById(R.id.tv_usdt_contract);
        tv_coin_contract = findViewById(R.id.tv_coin_contract);
        tv_trade_contract = findViewById(R.id.tv_trade_contract);
        tv_login = findViewById(R.id.tv_login);
        tv_share = findViewById(R.id.tv_share);
        tv_share2 = findViewById(R.id.tv_share2);

        tv_usdt_contract.setOnClickListener(this);
        tv_coin_contract.setOnClickListener(this);
        tv_trade_contract.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_share2.setOnClickListener(this);
    }

    private void initSLSDKAgent() {
        //设置http接口相关参数
        HttpRequestConfigs httpRequestConfigs = new HttpRequestConfigs()
                .setPrefixHeader("ex")//设置header前缀
                .setHttpReleaseHost("http://co.mybts.info")//设置HTTP接口请求域名
                .setHttpWebSocketHost("ws://ws3.mybts.info/wsswap/realTime")//websocket
                .setExpiredTs("1583901738000000")//过期时间
                .setAccesskey("3e0b5935-6e67-4b55-b345-6f0ed43fafa8");
        httpRequestConfigs.bulid();
        SLSDKAgent.setHttpRequestConfigs(httpRequestConfigs);
        //初始化
        SLSDKAgent.init(this);
        //控制日志输出
        SLSDKAgent.setLogEnabled(true);

        SLSDKAgent.setHttpIsDev(true);
        //构造用户user相关信息
        SLUser user = new SLUser();
     //   user.setToken("7af5683c07c58db9c110149dee090df2");
        String token = "7af5683c07c58db9c110149dee090df2";//PreferenceManager.getString(MainActivity.this,LoginActivity.sTokenKey,"");
        if(!TextUtils.isEmpty(token)){
            user.setToken(token);
            //设置全局user对象
            SLSDKAgent.bindSLUser(user);
        }

        //注册SDK请求客服端事件监听
        SLSDKAgent.registerSLSDKRequestListener(this);
        //注册合约Ticker监听
        SLSDKAgent.registerContractTickerListener(this);
        //获取合约列表
        BTContract.getInstance().contracts(0, new IResponse<List<Contract>>() {
            @Override
            public void onResponse(String errno, String message, List<Contract> data) {
                if (data!=null){
                    List<String> contracts = new ArrayList<>();
                    for (int i = 0 ; i < data.size();i++){
                        contracts.add(data.get(i).getInstrument_id()+"");
                    }
                    if(contracts.size() > 0){
                        //订阅合约ticker
                        SLSDKAgent.sendContractSubscribe(LogicWebSocketContract.WEBSOCKET_TICKER,contracts);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消合约监听
        SLSDKAgent.unRegisterContractTickerListener(this);
        //断开WebSocket链接
        SLSDKAgent.disconnectLogicWebSocket();
        SLSDKAgent.unRegisterContractTickerListener(this);
    }

    /**
     * 合约定义信息回调
     * @param data
     */
    @Override
    public void onContractMessage(String data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_usdt_contract:
                UsdtActivity.show(MainActivity.this,0);
                break;
            case R.id.tv_coin_contract:
                UsdtActivity.show(MainActivity.this,1);
                break;
            case R.id.tv_trade_contract:
                UsdtActivity.show(MainActivity.this,2);
                break;
            case R.id.tv_login:
                LoginActivity.show(MainActivity.this);
                break;
            case R.id.tv_share:
                PNLShareActivity.show(MainActivity.this,"",0);
                break;
            case R.id.tv_share2:
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View shotView = inflater.inflate(com.bmtc.sdk.contract.R.layout.sl_pnl_share,null);
                final LinearLayout warpLayout = findViewById(R.id.ll_share_placeholder_layout);
                warpLayout.addView(shotView);
                //获取数据 渲染UI 可参考PNLShareActivity类的createItemView方法
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View dView = warpLayout;
                        dView.setDrawingCacheEnabled(true);
                        dView.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
                        ShareToolUtil.sendLocalShare(MainActivity.this,bitmap);
                    }
                },2000);

                break;
        }
    }


    /**
     * @param actionType LogicSDKState.STATE_LOGIN  登录
     *                   LogicSDKState.STATE_LOGOUT  退出登录
     *                   LogicSDKState.STATE_BIND   绑卡
     */
    @Override
    public void onEvent(int actionType) {
        switch (actionType){
            case STATE_LOGIN:
               LoginActivity.show(MainActivity.this);
                break;

        }
    }
}
