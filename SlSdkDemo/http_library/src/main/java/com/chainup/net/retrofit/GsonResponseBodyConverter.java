package com.chainup.net.retrofit;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final Type type;


    public GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        //先将返回的json数据解析到Response中，如果code==200，则解析到我们的实体基类中，否则抛异常
        ResultResponse httpResult = gson.fromJson(response, ResultResponse.class);

        Log.d("==AA==","response:"+httpResult.toString()+"|||"+response);

        if (httpResult.getCode() == 0) {
            //200的时候就直接解析，不可能出现解析异常。因为我们实体基类中传入的泛型，就是数据成功时候的格式
            return gson.fromJson(response, type);
        } else {
//            ErrorResponse errorResponse = gson.fromJson(response,ErrorResponse.class);
//            //抛一个自定义ResultException 传入失败时候的状态码，和信息
//            throw new ResultException(errorResponse.getCode(),errorResponse.getMsg());
            //如果服务器返回错误code,就返回清除data之后的字符串，防止json解析错误，确保msg能够传达
            if (TextUtils.isEmpty(httpResult.getMsg())) {
                httpResult.setMsg("");
            }
            return gson.fromJson(gson.toJson(httpResult), type);
        }
    }

}
