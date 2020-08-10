package com.yjkj.chainup.extra_service.eventbus;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-10-14 18:31
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-10-14 18:31
 * @UpdateRemark: 更新说明
 */
public class NLiveDataUtil {

    private static MutableLiveData<MessageEvent> liveData = null;

    private static MutableLiveData<MessageEvent> getLiveData() {
        if (null == liveData) {
            liveData = new MutableLiveData<MessageEvent>();
        }
        return liveData;
    }

    public static void postValue(MessageEvent value) {
        getLiveData().postValue(value);
    }

    public static void setValue(MessageEvent value) {
        getLiveData().setValue(value);
    }

    public static void observeData(@NonNull LifecycleOwner owner, @NonNull Observer<MessageEvent> observer) {  //
        getLiveData().observe(owner, observer);
    }

    public static void observeForeverData(@NonNull Observer<MessageEvent> observer) {  //
        getLiveData().observeForever(observer);
    }

    /*
     * 类型事件处理完后需要调用此方法，防止事件再次触发
     */
    public static void removeObservers() {
        liveData = null;
    }

    public static void removeEvent(int eventType) {

    }

}
