package com.follow.order.utils;

import android.text.TextUtils;

import com.follow.order.constant.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @time: 2020/3/24
 * @author: guodong
 */
public class SignUtils {

    public static String getSign(Request request, long timestamp) {
        String sign = "";
        if (request == null) {
            return sign;
        }
        List<String> keyList = new ArrayList<>();
        keyList.add("timestamp=" + timestamp);
        if (TextUtils.equals(request.method(), "POST")) {
            RequestBody body = request.body();
            if (body != null && body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                for (int i = 0; i < formBody.size(); i++) {
                    String key = formBody.name(i);
                    String value = formBody.value(i);
                    if (!TextUtils.isEmpty(value) || !TextUtils.equals(value, "0")) {
                        keyList.add(key + "=" + value);
                    }
                }
            }
        } else if (TextUtils.equals(request.method(), "GET")) {
            HttpUrl httpUrl = request.url();
            if (httpUrl != null) {
                String query = httpUrl.query();
                if (!TextUtils.isEmpty(query)) {
                    String[] tempParams = query.split("&");
                    for (int i = 0; i < tempParams.length; i++) {
                        String value = tempParams[i];
                        if (!value.endsWith("=")) {
                            keyList.add(value);
                        }
                    }
                }
            }
        }
        String[] arrayToSort = keyList.toArray(new String[keyList.size()]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            sb.append(arrayToSort[i]);
            if (i != keyList.size() - 1) {
                sb.append("&");
            }
        }
        String signString = Config.APP_KEY + Config.APP_SECRET + sb.toString();
        LogUtil.d("signString = " + signString);
//        signString = CryptoHelper.sha256Encode(signString);
        signString = MD5Utils.digest(signString);
        LogUtil.d("sign = " + signString);
        sign = signString;
//        try {
//            sign = Base64.encodeToString(signString.getBytes("utf-8"), Base64.DEFAULT);
//            sign = sign.replace("==", "");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        LogUtil.d("signature = " + sign);
        return sign;
    }
}
