package com.follow.order.net;


import com.follow.order.bean.BaseBean;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wanghui on 17/3/3.
 */

public class RxUtil {
    static long time;

    public static long getTime() {
        return time;
    }

    public static void setTime(long time) {
        RxUtil.time = time;
    }

    /**
     * 对RESTful返回结果做预处理，对逻辑错误抛出异常
     *
     * @param <T>
     * @return
     */
    public static <T> Function<BaseBean<T>, T> handleRESTFulResult() {
        return new Function<BaseBean<T>, T>() {
            /**
             * Apply some calculation to the input value and return some other value.
             *
             * @param restResult the input value
             * @return the output value
             * @throws Exception on error
             */
            @Override
            public T apply(BaseBean<T> restResult) throws Exception {
                if (restResult.getCode() != BaseBean.SUCCESS) {
                    throw new ApiException(restResult.getCode(), restResult.getMsg());
                }
                return restResult.getData();
            }
        };
    }
    public static <T> Function<T, T> handleRESTFulResultWithoutStatus() {
        return new Function<T, T>() {
            /**
             * Apply some calculation to the input value and return some other value.
             *
             * @param t the input value
             * @return the output value
             * @throws Exception on error
             */
            @Override
            public T apply(T t) throws Exception {
                return t;
            }
        };
    }

    /**
     * 普通线程切换: IO -> Main
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> normalSchedulers() {
        return new ObservableTransformer<T, T>() {
            /**
             * Applies a function to the upstream Observable and returns an ObservableSource with
             * optionally different element type.
             *
             * @param upstream the upstream Observable instance
             * @return the transformed ObservableSource instance
             */
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
