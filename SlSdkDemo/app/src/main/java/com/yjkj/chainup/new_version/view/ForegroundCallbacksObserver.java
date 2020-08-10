package com.yjkj.chainup.new_version.view;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lianshangljl
 * @Date 2020-02-24-11:45
 * @Email buptjinlong@163.com
 * @description
 */
public class ForegroundCallbacksObserver {
    private List<ForegroundCallbacksListener> listeners;

    public ForegroundCallbacksObserver() {
        listeners = new ArrayList<>();
    }

    private static ForegroundCallbacksObserver foregroundCallbacksObserver;

    public static ForegroundCallbacksObserver getInstance() {
        if (null == foregroundCallbacksObserver) {
            foregroundCallbacksObserver = new ForegroundCallbacksObserver();
        }
        return foregroundCallbacksObserver;
    }


    public synchronized void addListener(ForegroundCallbacksListener listener) {
        if (null == listener || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void ForegroundListener() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).ForegroundListener();
        }
    }

    public void CallBacksListener() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).BackgroundListener();
        }
    }


    public void removeListener(ForegroundCallbacksListener listener) {
        if (null == listener || !listeners.contains(listener)) {
            return;
        }
        listeners.remove(listener);
    }
}
