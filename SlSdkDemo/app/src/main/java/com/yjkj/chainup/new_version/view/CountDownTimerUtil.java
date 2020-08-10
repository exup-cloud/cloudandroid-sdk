package com.yjkj.chainup.new_version.view;


import android.app.Activity;
import android.os.CountDownTimer;

import com.yjkj.chainup.util.Utils;

/**
 * @Author lianshangljl
 * @Date 2020-02-18-12:51
 * @Email buptjinlong@163.com
 * @description
 */

public class CountDownTimerUtil {
    private static CountDownTimerUtil countDownTimerUtil;
    private static final long DefauteMillisInFuture = 0;
    private static final long DefauteCountDownInterval = 1;
    private MyCountDownTimer countDownTimer;

    private CountDownTimerUtil(long millisInFuture, long countDownInterval) {
        countDownTimer = new MyCountDownTimer(millisInFuture, countDownInterval);
    }

    public static CountDownTimerUtil newInstance(long millisInFuture, long countDownInterval) {
        if (countDownTimerUtil == null) {
            countDownTimerUtil = new CountDownTimerUtil(millisInFuture, countDownInterval);
        }
        return countDownTimerUtil;
    }

    public MyCountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(MyCountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public static CountDownTimerUtil getDefault() {
        return newInstance(DefauteMillisInFuture, DefauteCountDownInterval);
    }

    public MyCountDownTimer setContinueCountDownTimer(long currentTime) {
        setCountDownTimer(new MyCountDownTimer(currentTime, DefauteCountDownInterval));
        return getCountDownTimer();
    }

    public MyCountDownTimer setContinueCountDownTimer() {
        setCountDownTimer(new MyCountDownTimer(DefauteMillisInFuture, DefauteCountDownInterval));
        return getCountDownTimer();
    }

    public void setTimerListener(OnCountDownTimerListener activity) {
        setOnCountDownTimerListener(activity);
    }

    private OnCountDownTimerListener onCountDownTimerListener;

    public void setOnCountDownTimerListener(OnCountDownTimerListener onCountDownTimerListener) {
        this.onCountDownTimerListener = onCountDownTimerListener;
    }


    /**
     * 开始倒计时
     */
    public static void StartToCountDown(long currentTime, OnCountDownTimerListener activity) {
        CountDownTimerUtil aDefault = CountDownTimerUtil.getDefault();
        if (aDefault.getCountDownTimer().ismCancelled()) {
            if (currentTime > 0.005f) {
                //此时已经初始化过了
                aDefault.setContinueCountDownTimer(currentTime).toStart();
            } else if (currentTime == 0) {
                aDefault.setContinueCountDownTimer().toStart();
            } else {
                aDefault.getCountDownTimer().toStart();
            }
            aDefault.setTimerListener(activity);
        }

    }

    /**
     * 暂停倒计时
     */
    public static long StopCountDown() {
        CountDownTimerUtil aDefault = CountDownTimerUtil.getDefault();
        aDefault.setTimerListener(null);
        return aDefault.getCountDownTimer().toStop();
    }

    public class MyCountDownTimer extends CountDownTimer {

        private long currrntTime;
        private boolean mCancelled;

        public boolean ismCancelled() {
            return mCancelled;
        }

        public long getCurrrntTime() {
            return currrntTime;
        }


        public void setmCancelled(boolean cancelled) {
            if (mCancelled != cancelled)
                mCancelled = cancelled;
        }

        public void setCurrrntTime(long currrntTime) {
            this.currrntTime = currrntTime;
        }

        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mCancelled = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setCurrrntTime(millisUntilFinished);
            setmCancelled(false);
            if (onCountDownTimerListener != null) {
                onCountDownTimerListener.onNext(millisUntilFinished);
            }

        }

        /**
         * 比预期的要晚上一秒钟···
         */
        @Override
        public void onFinish() {
            setmCancelled(true);
            if (onCountDownTimerListener != null) {
                onCountDownTimerListener.onFinish();
            }

        }

        public void toStart() {
            if (ismCancelled()) start();
        }

        public long toStop() {
            if (!ismCancelled()) {
                setmCancelled(true);
                cancel();
            }
            return getCurrrntTime();
        }
    }

    public interface OnCountDownTimerListener {
        void onFinish();

        void onNext(long leftTimeFormatedStrings);
    }
}


