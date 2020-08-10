package com.follow.order.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.follow.order.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class NetworkUtil {

    private static Boolean isNetWorkAvailable = null;



    public static boolean isNetworkAvailable(){
//        if (isNetWorkAvailable==null){
//            isNetWorkAvailable = checkNetworkAvailable(ApplicationUtils.getApplicationContext());
//        }
//        return isNetWorkAvailable;
        return true;
    }

    public static void changeNetWorkStatus(boolean netWorkAvailable,boolean wifi){
        isNetWorkAvailable = netWorkAvailable;
    }
    /**
     * check NetworkAvailable
     * @param context
     * @return
     */
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (null == manager)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info || !info.isAvailable())
            return false;
        return true;
    }

    /**
     * 获取当前的网络状态 ：netType: 没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @param context
     * @return
     */
    public static String getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return context.getResources().getString(R.string.fo_error_net);
        }//否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }

        String netWorkName = "";
        switch (netType) {
            case 0:
                netWorkName = context.getString(R.string.fo_error_net);
                break;
            case 1:
                netWorkName = "WIFI";
                break;
            case 2:
                netWorkName = "2G";
                break;

            case 3:
                netWorkName = "3G";
                break;
            case 4:
                netWorkName = "4G";
                break;
        }
        return netWorkName;
    }

    /**
     * 获取本机ip地址
     * @param context
     * @return
     */
    public static String getIP(Context context){

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机运营商
     * @return
     */
    public static String getOperatorName(Context context){
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String operator = telManager.getSimOperator();
        String operatorName = "";
        if(operator!=null){

            if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
                operatorName = "中国移动";
                //中国移动

            }else if(operator.equals("46001")){
                operatorName = "中国联通";
                //中国联通

            }else if(operator.equals("46003")){
                operatorName = "中国电信";
                //中国电信
            }else{
                operatorName = operator;
            }

        }
        return operatorName;
    }
}
