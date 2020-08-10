package com.follow.order.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.follow.order.widget.chart.charts.Chart;
import com.follow.order.widget.chart.highlight.Highlight;
import com.follow.order.widget.chart.listener.ChartTouchListener;
import com.follow.order.widget.chart.listener.OnChartGestureListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CommonUtils {

    public static boolean copyValue(Context context, String copyValue) {
        if (!TextUtils.isEmpty(copyValue)) {
            // 获取系统剪贴板
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
            ClipData clipData = ClipData.newPlainText(null, copyValue);
            // 把数据集设置（复制）到剪贴板
            clipboard.setPrimaryClip(clipData);
            return true;
        }
        return false;
    }

    /**
     * 取消RxBus订阅
     *
     * @param disposable 要取消的订阅标记
     */
    public static void unRegisterDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    //    首字母大写
    public static String captureNameUpperCase(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);

    }

    public static void openUrlWoithDefaultBrower(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    private static HashMap<Object, Disposable> mChartDisposable = new HashMap<>();

    public static void unBindChart(final Chart chart) {
        if (mChartDisposable != null && chart != null) {
            try {
                mChartDisposable.remove(chart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void setMarkerAutoDismiss(final Chart chart) {
        chart.setOnChartGestureListener(new OnChartGestureListener() {

            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                CommonUtils.unRegisterDisposable(mChartDisposable.get(chart));
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                Highlight[] highlighted = chart.getHighlighted();
                if (highlighted != null) {
                    Disposable mAutoDissmissDisposable = Observable.timer(3000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    if (chart != null) {
                                        chart.highlightValues(null);
                                    }
                                }
                            });

                    mChartDisposable.put(chart, mAutoDissmissDisposable);
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

}
