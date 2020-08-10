package com.yjkj.chainup.net_new.websocket;

import com.yjkj.chainup.util.GZIPUtils;
import com.yjkj.chainup.util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-08-30 15:42
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-08-30 15:42
 * @UpdateRemark: 更新说明
 */
public class MsgWSSClient {

    private static final String TAG = "MsgWSSClient";

    private WebSocketClient mWebSocketClient;
    private boolean hasClosed = false;
    public MsgWSSClient(String url){
        try {
            mWebSocketClient = new MyWebSocketClient(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public boolean connectWS(){
        if(mWebSocketClient.isClosed() || !mWebSocketClient.isOpen()){
            mWebSocketClient.connect();
            hasClosed = false;
        }
        return mWebSocketClient.isOpen();
    }

    public boolean isConnected(){
        return null!=mWebSocketClient && mWebSocketClient.isOpen();
    }

    private ArrayList<String> msgText;
    public void setSendMsg(ArrayList<String> msgText){
        this.msgText = msgText;
    }

    private SocketResultListener mSocketResultListener;
    public void setSocketListener(SocketResultListener l){
        mSocketResultListener = l;
    }

    public interface SocketResultListener {
        public void onSuccess(JSONObject jsonObject);
        public void onFailure(String message);
    }


    class MyWebSocketClient extends WebSocketClient{

        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            if(null!=msgText && !hasClosed){
                for(int i=0;i<msgText.size();i++){
                    if(!hasClosed){
                        mWebSocketClient.send(msgText.get(i));
                    }
                }
            }
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            super.onMessage(bytes);
            if(hasClosed)
                return;
            if(null==bytes)
                return;
            String data = GZIPUtils.uncompressToString(bytes.array());
            if(null!=data && data.contains("ping")){

                String replace = data.replace("ping","pong");
                mWebSocketClient.send(replace);
            }else{
                try{
                    JSONObject jsonObject = new JSONObject(data);
                    mSocketResultListener.onSuccess(jsonObject);
                }catch (JSONException e){
                }
            }
        }

        @Override
        public void onMessage(String message) {
            mSocketResultListener.onFailure(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            mSocketResultListener.onFailure(reason);
        }

        @Override
        public void onError(Exception ex) {
            mSocketResultListener.onFailure(ex.getMessage());
        }
    }

    public void close(){
        if(null!=mWebSocketClient){
            mWebSocketClient.close();
            hasClosed = true;
        }
    }
}
