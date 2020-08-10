package com.yjkj.chainup.new_version.view;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @Author lianshangljl
 * @Date 2019/3/21-4:48 PM
 * @Email buptjinlong@163.com
 * @description
 */
public class Gt3GeetestObserver {
    private Vector<Gt3GeeListener> listeners;

    public Gt3GeetestObserver() {
        listeners = new Vector<>();
    }

    public void addActivityResultObserver(Gt3GeeListener listener) {
        listeners.add(listener);
    }


    private static Gt3GeetestObserver quotesDetailObserver;

    public static Gt3GeetestObserver getQuotesDetailObserver() {
        if (quotesDetailObserver == null) {
            quotesDetailObserver = new Gt3GeetestObserver();
        }
        return quotesDetailObserver;
    }

    public void setActivityResultListeners(ArrayList<String> result) {
        if (listeners.size() > 0) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onSuccess(result);
            }
        }
    }
}
