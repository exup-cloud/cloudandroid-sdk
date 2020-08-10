package com.yjkj.chainup.new_version.view;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.fengniao.news.util.JsonUtils;
import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.db.service.UserDataService;

import org.json.JSONException;
import org.json.JSONObject;

import wendu.dsbridge.CompletionHandler;

/**
 * @Author lianshangljl
 * @Date 2019-12-03-11:08
 * @Email buptjinlong@163.com
 * @description
 */
public class JsEchoApi {


    @JavascriptInterface
    public Object syn(Object args) throws JSONException {
        return args;
    }

    @JavascriptInterface
    public void exchangeInfo(Object args, CompletionHandler handler) {
        if (!TextUtils.isEmpty(UserDataService.getInstance().getToken())) {
            try {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("exchange_token", UserDataService.getInstance().getToken());
                jsonObject.put("exchange_lan", JsonUtils.INSTANCE.getLanguage());
                jsonObject.put("exchange_skin", PublicInfoDataService.getInstance().getThemeModeNew());
                handler.complete(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
