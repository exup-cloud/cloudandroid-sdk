package com.yjkj.chainup.new_version.view;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observer;

/**
 * @Author lianshangljl
 * @Date 2020-02-18-10:54
 * @Email buptjinlong@163.com
 * @description
 */
public class CloseWsControl {

    private ArrayList<CloseWsListener> listeners;

    public CloseWsControl() {
        listeners = new ArrayList<>();
    }

    private static CloseWsControl closeWsControl;

    public static CloseWsControl getInstance() {
        if (null == closeWsControl) {
            closeWsControl = new CloseWsControl();
        }
        return closeWsControl;
    }


    public synchronized void addListener(CloseWsListener listener) {
        if (null == listener || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void removeListener(CloseWsListener listener) {
        if (null == listener || !listeners.contains(listener)) {
            return;
        }
        listeners.remove(listener);
    }

    public void updateListener() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).upListener();
        }
    }

    public void downListener() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).downListener();
        }
    }


}
