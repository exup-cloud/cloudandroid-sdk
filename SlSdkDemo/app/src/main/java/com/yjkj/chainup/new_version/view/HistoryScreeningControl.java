package com.yjkj.chainup.new_version.view;

import android.util.Log;

import com.yjkj.chainup.new_version.activity.HistoryScreeningListener;

import java.util.ArrayList;
import java.util.Observer;

/**
 * @Author lianshangljl
 * @Date 2020-02-18-10:54
 * @Email buptjinlong@163.com
 * @description
 */
public class HistoryScreeningControl {

    private ArrayList<HistoryScreeningListener> listeners;

    public HistoryScreeningControl() {
        listeners = new ArrayList<>();
    }

    private static HistoryScreeningControl screeningControl;

    public static HistoryScreeningControl getInstance() {
        if (null == screeningControl) {
            screeningControl = new HistoryScreeningControl();
        }
        return screeningControl;
    }


    public synchronized void addListener(HistoryScreeningListener listener) {
        if (null == listener || listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public void removeListener(HistoryScreeningListener listener) {
        if (null == listener || !listeners.contains(listener)) {
            return;
        }
        listeners.remove(listener);
    }

    public void updateListener(Boolean status, String symbolCoin, String symbolAndUnit, int tradingType, int priceType, String begin, String end) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).ConfirmationScreen(status, symbolCoin, symbolAndUnit, tradingType, priceType, begin, end);
        }
    }


}
