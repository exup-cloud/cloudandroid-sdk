package com.follow.order.net;

import com.follow.order.FollowOrderSDK;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.bean.MenuBean;
import com.follow.order.bean.PersonalInfoBean;
import com.follow.order.bean.TipBean;
import com.follow.order.bean.UserFinanceProfileBean;
import com.follow.order.constant.Api;
import com.follow.order.net.conver.CustomGsonConverterFactory;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RxRetrofitClient {
    private static final String BASE_URL = Api.HOSTNAME;
    private static RxRetrofitClient mRetrofitClient;
    private static ApiInterface apiInterface;

    private RxRetrofitClient() {
        initClient();
    }


    public static RxRetrofitClient getInstance() {
        if (mRetrofitClient == null) {
            mRetrofitClient = new RxRetrofitClient();
        }
        return mRetrofitClient;
    }

    private void initClient() {

        // 创建Retrofit实例
        Retrofit tradeRetrofit = new Retrofit.Builder()
                .client(OkHttp3Client.getInstance())
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        // 创建API接口类
        apiInterface = tradeRetrofit.create(ApiInterface.class);
    }

    /**
     * 获取首页推荐数据
     *
     * @param observer
     */
    public void getPublicKey(Observer<HashMap<String, String>> observer) {
        String url = Api.getUrlPublicKey();
        apiInterface.getPublicKey(url)
                .map(RxUtil.<HashMap<String, String>>handleRESTFulResult())
                .compose(RxUtil.<HashMap<String, String>>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取KOL列表数据
     *
     * @param observer
     */
    public void getKolStyle(Observer<List<MenuBean>> observer) {
        String url = Api.getUrlKolStyle();
        apiInterface.getKolStyle(url)
                .map(RxUtil.<List<MenuBean>>handleRESTFulResult())
                .compose(RxUtil.<List<MenuBean>>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取币种列表
     *
     * @param observer
     */
    public void getKolCoinList(Observer<List<MenuBean>> observer) {
        String url = Api.getUrlKolCoinList();
        apiInterface.getKolCoinList(url)
                .map(RxUtil.<List<MenuBean>>handleRESTFulResult())
                .compose(RxUtil.<List<MenuBean>>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取个人主页用户信息
     *
     * @param kol_id
     * @param observer
     * @return
     */
    public void getPersonalUserInfo(String kol_id, Observer<PersonalInfoBean> observer) {
        String url = Api.getUrlPersonalInfo();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (FollowOrderSDK.IS_ENCRY) {
            String params = "kol_id=" + kol_id;
            paramsMap.put("crypt", FollowOrderSDK.ins().encryptParams(params));
        } else {
            paramsMap.put("kol_id", kol_id);
        }
        apiInterface.getPersonalUserInfo(url, paramsMap)
                .map(RxUtil.<PersonalInfoBean>handleRESTFulResult())
                .compose(RxUtil.<PersonalInfoBean>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取用户接入的交易所列表
     *
     * @param uid
     * @param observer
     */
    public void getExchangeApiList(String uid, Observer<List<ExchangeBean>> observer) {
        String url = Api.getUrlApilist();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (FollowOrderSDK.IS_ENCRY) {
            String params = "uid=" + uid;
            paramsMap.put("crypt", FollowOrderSDK.ins().encryptParams(params));
        } else {
            paramsMap.put("uid", uid);
        }
        apiInterface.getExchangeApiList(url, paramsMap)
                .map(RxUtil.<List<ExchangeBean>>handleRESTFulResult())
                .compose(RxUtil.<List<ExchangeBean>>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取实盘资产信息
     *
     * @param uid
     * @param api_id
     * @param observer
     */
    public void getLiveFinanceProfile(String uid, String api_id, Observer<UserFinanceProfileBean> observer) {
        String url = Api.getUrlLiveInfo();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (FollowOrderSDK.IS_ENCRY) {
            String params = "uid=" + uid + "&api_id=" + api_id;
            paramsMap.put("crypt", FollowOrderSDK.ins().encryptParams(params));
        } else {
            paramsMap.put("uid", uid);
            paramsMap.put("api_id", api_id);
        }
        apiInterface.getLiveFinanceProfile(url, paramsMap)
                .map(RxUtil.<UserFinanceProfileBean>handleRESTFulResult())
                .compose(RxUtil.<UserFinanceProfileBean>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 转换usdt
     *
     * @param symbol
     * @param amount
     * @param observer
     */
    public void convertUsdt(String symbol, String amount, Observer<HashMap<String, String>> observer) {
        String url = Api.getUrlFollowUsdt();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (FollowOrderSDK.IS_ENCRY) {
            String params = "symbol=" + symbol + "&amount=" + amount;
            paramsMap.put("crypt", FollowOrderSDK.ins().encryptParams(params));
        } else {
            paramsMap.put("symbol", symbol);
            paramsMap.put("amount", amount);
        }
        apiInterface.convertUsdt(url, paramsMap)
                .map(RxUtil.<HashMap<String, String>>handleRESTFulResult())
                .compose(RxUtil.<HashMap<String, String>>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 获取跟单收益
     *
     * @param observer
     */
    public void getCommonDialog(Observer<TipBean> observer) {
        String url = Api.getUrlCommonDialog();
        apiInterface.getCommonDialog(url)
                .map(RxUtil.<TipBean>handleRESTFulResult())
                .compose(RxUtil.<TipBean>normalSchedulers())
                .subscribe(observer);
    }

    /**
     * 转换usdt
     *
     * @param symbol
     * @param amount
     */
    public Observable<HashMap<String, String>> convertUsdt(String symbol, String amount) {
        String url = Api.getUrlFollowUsdt();
        HashMap<String, String> paramsMap = new HashMap<>();
        if (FollowOrderSDK.IS_ENCRY) {
            String params = "symbol=" + symbol + "&amount=" + amount;
            paramsMap.put("crypt", FollowOrderSDK.ins().encryptParams(params));
        } else {
            paramsMap.put("symbol", symbol);
            paramsMap.put("amount", amount);
        }
        return apiInterface.convertUsdt(url, paramsMap)
                .map(RxUtil.<HashMap<String, String>>handleRESTFulResult())
                .compose(RxUtil.<HashMap<String, String>>normalSchedulers());
    }

}
