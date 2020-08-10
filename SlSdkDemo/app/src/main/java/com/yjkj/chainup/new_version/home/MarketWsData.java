package com.yjkj.chainup.new_version.home;

import android.support.annotation.NonNull;
import com.yjkj.chainup.manager.SymbolWsData;
import com.yjkj.chainup.net.api.ApiConstants;
import com.yjkj.chainup.net_new.websocket.MsgWSSClient;
import com.yjkj.chainup.util.LogUtil;
import com.yjkj.chainup.util.WsLinkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Description:  24小时行情WS逻辑处理
 * @Author: wanghao
 * @CreateDate: 2019-11-11 16:36
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-11-11 16:36
 * @UpdateRemark: 更新说明
 */
public class MarketWsData {

    private static final String TAG = "MarketWsData";

    private RefreshWSListener mRefreshListener;
    public interface RefreshWSListener {
        void onRefreshWS(int pos);
    }

    private MsgWSSClient mMsgWSSClient;
    private ArrayList<JSONObject> dataList;
    public void initSocket(ArrayList<JSONObject> list, RefreshWSListener l){
        if(null==list || list.size()<=0){
            return;
        }
        this.dataList = list;
        this.mRefreshListener = l;
        if(null==mMsgWSSClient || !mMsgWSSClient.isConnected()){
            mMsgWSSClient = new MsgWSSClient(ApiConstants.SOCKET_ADDRESS);
            mMsgWSSClient.setSocketListener(new MsgWSSClient.SocketResultListener(){

                @Override
                public void onSuccess(JSONObject jsonObject) {
                    LogUtil.d(TAG,"initSocket==onSuccess==jsonObject is "+jsonObject);
                    showWsData(jsonObject);
                }

                @Override
                public void onFailure(String message) {
                    LogUtil.d(TAG,"initSocket==message is "+message);
                }
            });

            ArrayList<String> paramList = new ArrayList<String>();
            for(int i=0;i<list.size();i++){
                String symbol = list.get(i).optString("symbol");
                paramList.add(WsLinkUtils.tickerFor24HLink(symbol,true,false));
            }
            mMsgWSSClient.setSendMsg(paramList);
            mMsgWSSClient.connectWS();
        }

    }

    private void showWsData(JSONObject jsonObject) {
        if(null==mRefreshListener || null==dataList)
            return;
        LogUtil.d(TAG,"showWsData==jsonObject is "+jsonObject);
        JSONObject obj = new SymbolWsData().getNewSymbolObj(dataList,jsonObject);
        LogUtil.d(TAG,"showWsData==obj is "+obj);
        if(null!=obj && obj.length()>0){
            int pos = dataList.indexOf(obj);
            if(pos>=0){
                mRefreshListener.onRefreshWS(pos);
            }
        }
    }

    public void closeWS(){
        if(null!=mMsgWSSClient){
            mMsgWSSClient.close();
        }
    }

}
