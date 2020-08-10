package com.yjkj.chainup.extra_service.push;

import android.content.Context;
/*import android.util.Log;

import com.AppConfig;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;*/

public class UmengPushUtil {

    private static final String TAG = "UmengPushUtil";
    private static final String aliastype_User = "1";
    private static final String appkey = "5b7f9c0df29d983bc90000cd";
    private static final String umengMessageSecret = "9ec2bb4eb488dae647a52014539abb1c";
    private static final String appMasterSecret = "3hedxqej6drw3yxnzzc7vdrvmdtkpiuk";

    public static void init(Context context){
       // UMConfigure.init(context,appkey,AppConfig.down_cl,UMConfigure.DEVICE_TYPE_PHONE, umengMessageSecret);
    }

    /*public static void register(Context context, UmengNotificationClickHandler clickHandler){
        init(context);
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //声音
        mPushAgent.setDisplayNotificationNumber(6);//通知栏按数量显示
        if(null!=clickHandler){
            mPushAgent.setNotificationClickHandler(clickHandler);
        }
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.d(TAG,"onSuccess==deviceToken is "+deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.d(TAG,"onFailure==s is "+s+", s1 is "+s1);
            }
        });
    }*/

    public static void onAppStart(Context context){
       // PushAgent.getInstance(context).onAppStart();
    }

    public static void addAlias(Context context,String userId){
        /*PushAgent.getInstance(context).addAlias(userId, aliastype_User, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
            }
        });*/
    }

    public static void deleteAlias(Context context,String userId){
        /*PushAgent.getInstance(context).deleteAlias(userId, aliastype_User, new UTrack.ICallBack(){
            @Override
            public void onMessage(boolean isSuccess, String message) {
            }
        });*/
    }
}
