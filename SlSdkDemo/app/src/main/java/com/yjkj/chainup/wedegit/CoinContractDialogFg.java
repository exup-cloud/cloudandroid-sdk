package com.yjkj.chainup.wedegit;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yjkj.chainup.R;
import com.yjkj.chainup.base.NBaseDialogFragment;
import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.extra_service.eventbus.MessageEvent;
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil;
import com.yjkj.chainup.manager.Contract2PublicInfoManager;
import com.yjkj.chainup.net.api.ApiConstants;
import com.yjkj.chainup.net_new.websocket.MsgWSSClient;
import com.yjkj.chainup.new_version.adapter.SelectContractAdapter;
import com.yjkj.chainup.new_version.contract.ContractFragment;
import com.yjkj.chainup.new_version.view.EmptyForAdapterView;
import com.yjkj.chainup.treaty.bean.ContractBean;
import com.yjkj.chainup.util.WsLinkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-11-01 14:52
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-11-01 14:52
 * @UpdateRemark: 更新说明
 */
public class CoinContractDialogFg extends NBaseDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen dialog fragment
        setStyle(DialogFragment.STYLE_NORMAL, R.style.leftin_rightout_DialogFg_style);
    }


    @Override
    protected int setContentView() {
        return R.layout.dialogfg_left_contract;
    }

    @Override
    protected void initView() {
        initV();
        observeData();
    }

    private ArrayList<ContractBean> contractList;
    @Override
    protected void loadData() {
        contractList = Contract2PublicInfoManager.getAllContracts();
    }

    private SelectContractAdapter adapter;
    private void initV() {

        if(null==contractList || contractList.size()<=0){
            return;
        }
        adapter = new SelectContractAdapter(contractList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ContractBean contractBean = (ContractBean)(adapter.getData().get(position));

                ContractFragment.liveData4Contract.postValue(contractBean);
                /*MessageEvent msgEvent = new MessageEvent(MessageEvent.left_coin_contract_type);
                msgEvent.setMsg_content(contractBean);
                NLiveDataUtil.postValue(msgEvent);*/
                Contract2PublicInfoManager.currentContractId(contractBean.getId(),  true);

                dismissDialog();
            }
        });


        RecyclerView rv_contract = findViewById(R.id.rv_contract);

        rv_contract.setAdapter(adapter);

        rv_contract.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.bindToRecyclerView(rv_contract);
        rv_contract.setHasFixedSize(true);
        adapter.setEmptyView(new EmptyForAdapterView(getContext()));
        adapter.notifyDataSetChanged();

        findViewById(R.id.alpha_ll).setOnClickListener(this);
    }

    private MsgWSSClient mMsgWSSClient;
    private boolean contractOpen;
    public void initSocket() {
        contractOpen = PublicInfoDataService.getInstance().contractOpen(null);
        if(!contractOpen)
            return;
        if(null==contractList || contractList.size()<=0){
            return;
        }
        if (null == mMsgWSSClient || mMsgWSSClient.isConnected()) {
            mMsgWSSClient = new MsgWSSClient(ApiConstants.SOCKET_CONTRACT_ADDRESS);
            mMsgWSSClient.setSocketListener(new MsgWSSClient.SocketResultListener(){

                @Override
                public void onSuccess(JSONObject jsonObject) {
                    handleData(jsonObject);
                }

                @Override
                public void onFailure(String message) {

                }
            });

            ArrayList<String> paramList = new ArrayList<String>();
            for(ContractBean cb : contractList){
                String symbol = cb.getSymbol();
                if (null != symbol) {
                    paramList.add(WsLinkUtils.tickerFor24HLink(symbol.toLowerCase(),true,false));
                }
            }
            mMsgWSSClient.setSendMsg(paramList);
            mMsgWSSClient.connectWS();
        }
    }

    private void handleData(JSONObject json) {
        if (null == json)
            return;

        JSONObject tick = json.optJSONObject("tick");

        if (null == tick || tick.length() <= 0)
            return;

        String channel = json.optString("channel");
        if (null == channel || !channel.contains("_"))
            return;

        String symbol = channel.split("_")[1];

        if(null!=contractList && contractList.size()>0){
            for(ContractBean cb : contractList){
                String cbsymbol = cb.getSymbol();
                if(symbol.equalsIgnoreCase(cbsymbol)){
                    String close = tick.optString("close");
                    String rose = tick.optString("rose");
                    if (null!=cb && cb.getClosePrice() != close) {
                        cb.setClosePrice(close);
                        cb.setRose(rose);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void observeData() {
        NLiveDataUtil.observeData(this, new Observer<MessageEvent>() {
            @Override
            public void onChanged(@Nullable MessageEvent messageEvent) {
                if(MessageEvent.closeLeftCoinSearchType == messageEvent.getMsg_type()){
                    dismissDialog();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        dismissDialog();
    }

    @Override
    protected void dismissDialog(){
        super.dismissDialog();
        if(null!=mMsgWSSClient){
            mMsgWSSClient.close();
        }
    }

    /*
     * 展示dialog
     */
    public void showDialog(FragmentManager manager, String tag){
        initSocket();
        show(manager,tag);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
