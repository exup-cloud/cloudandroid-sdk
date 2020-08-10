package com.follow.order.net;


import com.follow.order.bean.BaseBean;
import com.follow.order.bean.ExchangeBean;
import com.follow.order.bean.MenuBean;
import com.follow.order.bean.PersonalInfoBean;
import com.follow.order.bean.TipBean;
import com.follow.order.bean.UserFinanceProfileBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by wanghui on 17/3/3.
 * Api接口
 */

public interface ApiInterface {

    @GET
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, String> map);

    /**
     * 获取公钥
     *
     * @param url
     * @return
     */
    @GET()
    Observable<BaseBean<HashMap<String, String>>> getPublicKey(@Url String url);

    /**
     * 获取kol列表筛选菜单
     *
     * @param url
     * @return
     */
    @GET()
    Observable<BaseBean<List<MenuBean>>> getKolStyle(@Url String url);

    /**
     * 获取币种列表
     *
     * @param url
     * @return
     */
    @GET()
    Observable<BaseBean<List<MenuBean>>> getKolCoinList(@Url String url);

    /**
     * 获取个人主页用户信息
     *
     * @param url
     * @param params
     * @return
     */
    @GET()
    Observable<BaseBean<PersonalInfoBean>> getPersonalUserInfo(@Url String url, @QueryMap HashMap<String, String> params);

    /**
     * 获取用户接入的交易所列表
     *
     * @param url
     * @param params
     * @return
     */
    @GET()
    Observable<BaseBean<List<ExchangeBean>>> getExchangeApiList(@Url String url, @QueryMap HashMap<String, String> params);

    /**
     * 获取用户对应交易所资产信息
     *
     * @param url
     * @param params
     * @return
     */
    @GET()
    Observable<BaseBean<UserFinanceProfileBean>> getLiveFinanceProfile(@Url String url, @QueryMap HashMap<String, String> params);

    /**
     * 转换USDT
     *
     * @param url
     * @param params
     * @return
     */
    @GET()
    Observable<BaseBean<HashMap<String, String>>> convertUsdt(@Url String url, @QueryMap HashMap<String, String> params);

    /**
     * 获取提示弹窗数据
     *
     * @param url
     * @return
     */
    @GET
    Observable<BaseBean<TipBean>> getCommonDialog(@Url String url);

}
