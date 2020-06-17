package com.bmtc.sdk.contract.common;


import com.bmtc.sdk.contract.common.chart.KLineEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数据辅助类 计算macd rsi等
 * Created by tifezh on 2016/11/26.
 */

public class DataHelper {

    /**
     * 计算RSI
     *
     * @param datas
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateRSI(List<KLineEntity> datas) {
        float rsi1 = 0;
        float rsi2 = 0;
        float rsi3 = 0;
        float rsi1ABSEma = 0;
        float rsi2ABSEma = 0;
        float rsi3ABSEma = 0;
        float rsi1MaxEma = 0;
        float rsi2MaxEma = 0;
        float rsi3MaxEma = 0;
        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                rsi1 = 0;
                rsi2 = 0;
                rsi3 = 0;
                rsi1ABSEma = 0;
                rsi2ABSEma = 0;
                rsi3ABSEma = 0;
                rsi1MaxEma = 0;
                rsi2MaxEma = 0;
                rsi3MaxEma = 0;
            } else {
                float Rmax = Math.max(0, closePrice - datas.get(i - 1).getClosePrice());
                float RAbs = Math.abs(closePrice - datas.get(i - 1).getClosePrice());
                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

                rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
            }
            point.rsi1 = rsi1;
            point.rsi2 = rsi2;
            point.rsi3 = rsi3;
        }
    }

    /**
     * 计算kdj
     *
     * @param dataList
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateKDJ(List<KLineEntity> dataList) {
        float k = 0;
        float d = 0;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();
            int startIndex = i - 8;
            if (startIndex < 0) {
                startIndex = 0;
            }
            float max14 = Float.MIN_VALUE;
            float min14 = Float.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max14 = Math.max(max14, dataList.get(index).getHighPrice());
                min14 = Math.min(min14, dataList.get(index).getLowPrice());
            }
            Float rsv = 100f * (closePrice - min14) / (max14 - min14);
            if (rsv.isNaN()) {
                rsv = 0f;
            }
            if (i == 0) {
                k = 50;
                d = 50;
            } else {
                k = (rsv + 2f * k) / 3f;
                d = (k + 2f * d) / 3f;
            }
            if (i < 8) {
                point.k = 0;
                point.d = 0;
                point.j = 0;
            } else if (i == 8 || i == 9) {
                point.k = k;
                point.d = 0;
                point.j = 0;
            } else {
                point.k = k;
                point.d = d;
                point.j = 3f * k - 2 * d;
            }
        }

    }

    /**
     * 计算wr
     *
     * @param dataList
     */
    static void calculateWR(List<KLineEntity> dataList) {
        Float r;
        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            int startIndex = i - 14;
            if (startIndex < 0) {
                startIndex = 0;
            }
            float max14 = Float.MIN_VALUE;
            float min14 = Float.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max14 = Math.max(max14, dataList.get(index).getHighPrice());
                min14 = Math.min(min14, dataList.get(index).getLowPrice());
            }
            if (i < 13) {
                point.r = -10;
            } else {
                r = -100 * (max14 - dataList.get(i).getClosePrice()) / (max14 - min14);
                if (r.isNaN()) {
                    point.r = 0;
                } else {
                    point.r = r;
                }
            }
        }

    }

    /**
     * 计算macd
     *
     * @param datas
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateMACD(List<KLineEntity> datas) {
        float ema12 = 0;
        float ema26 = 0;
        float dif = 0;
        float dea = 0;
        float macd = 0;

        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                ema12 = closePrice;
                ema26 = closePrice;
            } else {
//                EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
//                EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema12 = ema12 * 11f / 13f + closePrice * 2f / 13f;
                ema26 = ema26 * 25f / 27f + closePrice * 2f / 27f;
            }
//            DIF = EMA（12） - EMA（26） 。
//            今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
//            用（DIF-DEA）*2即为MACD柱状图。
            dif = ema12 - ema26;
            dea = dea * 8f / 10f + dif * 2f / 10f;
            macd = (dif - dea) * 2f;
            point.dif = dif;
            point.dea = dea;
            point.macd = macd;
        }

    }

    /**
     * 计算 BOLL 需要在计算ma之后进行
     *
     * @param datas
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateBOLL(List<KLineEntity> datas) {
        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                point.mb = closePrice;
                point.up = 0;
                point.dn = 0;
            } else {
                int n = 20;
                if (i < 20) {
                    n = i + 1;
                }
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = datas.get(j).getClosePrice();
                    float m = point.getMA20Price();
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                point.mb = point.getMA20Price();
                if (point.mb == 0) {
                    point.up = 0;
                    point.dn = 0;
                } else {
                    point.up = point.mb + 2f * md;
                    point.dn = point.mb - 2f * md;
                }
            }
        }

    }

    /**
     * 计算ma
     *
     * @param dataList
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateMA(List<KLineEntity> dataList) {
        float ma5 = 0;
        float ma10 = 0;
        float ma20 = 0;
        float ma30 = 0;
        float ma60 = 0;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            ma5 += closePrice;
            ma10 += closePrice;
            ma20 += closePrice;
            ma30 += closePrice;
            ma60 += closePrice;
            if (i == 4) {
                point.MA5Price = ma5 / 5f;
            } else if (i >= 5) {
                ma5 -= dataList.get(i - 5).getClosePrice();
                point.MA5Price = ma5 / 5f;
            } else {
                point.MA5Price = 0f;
            }
            if (i == 9) {
                point.MA10Price = ma10 / 10f;
            } else if (i >= 10) {
                ma10 -= dataList.get(i - 10).getClosePrice();
                point.MA10Price = ma10 / 10f;
            } else {
                point.MA10Price = 0f;
            }
            if (i == 19) {
                point.MA20Price = ma20 / 20f;
            } else if (i >= 20) {
                ma20 -= dataList.get(i - 20).getClosePrice();
                point.MA20Price = ma20 / 20f;
            } else {
                point.MA20Price = 0f;
            }
            if (i == 29) {
                point.MA30Price = ma30 / 30f;
            } else if (i >= 30) {
                ma30 -= dataList.get(i - 30).getClosePrice();
                point.MA30Price = ma30 / 30f;
            } else {
                point.MA30Price = 0f;
            }
            if (i == 59) {
                point.MA60Price = ma60 / 60f;
            } else if (i >= 60) {
                ma60 -= dataList.get(i - 60).getClosePrice();
                point.MA60Price = ma60 / 60f;
            } else {
                point.MA60Price = 0f;
            }
        }

    }
    /**
     * 计算ema
     *
     * @param dataList
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateEMA(List<KLineEntity> dataList) {
        float k5 = (float) (2.0 / (5 + 1.0));
        float k10 = (float) (2.0 / (10 + 1.0));
        float k20 = (float) (2.0 / (20 + 1.0));

        float ema5 = 0;
        float ema10 = 0;
        float ema20 = 0;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            if (i == 0) {
                ema5 = closePrice;
                ema10 = closePrice;
                ema20 = closePrice;

                point.EMA5Price = closePrice;
                point.EMA10Price = closePrice;
                point.EMA20Price = closePrice;
            } else {

                ema5 = closePrice * k5 + ema5 * (1 - k5);
                ema10 = closePrice * k10 + ema10 * (1 - k10);
                ema20 = closePrice * k20 + ema20 * (1 - k20);

                point.EMA5Price = ema5;
                point.EMA10Price = ema10;
                point.EMA20Price = ema20;
            }
        }
    }

    /**
     * 获取sar指标参数值
     *
     * @param step    参数step 0.02
     * @param maxStep 参数max 0.2
     * @return
     */
    private static void calculateSAR(List<KLineEntity> dataList, float step, float maxStep) {

        //记录是否初始化过
        float INIT_VALUE = -100;
        //加速因子
        float af = 0;
        //极值
        float ep = INIT_VALUE;
        //判断是上涨还是下跌  false：下跌
        boolean lasttrend = false;
        float SAR = 0;

        for (int i = 0; i < dataList.size() - 1; i++) {
            KLineEntity point = dataList.get(i);

            //上一个周期的sar
            float priorSAR = SAR;
            if (lasttrend) {
                //上涨
                if (ep == INIT_VALUE || ep < point.getHighPrice()) {
                    //重新初始化值
                    ep = point.getHighPrice();
                    af = Math.min(af + step, maxStep);
                }
                SAR = priorSAR + af * (ep - priorSAR);
                float lowestPrior2Lows = Math.min(dataList.get(Math.max(1, i) - 1).getLowPrice(), dataList.get(i).getLowPrice());
                if (SAR > dataList.get(i + 1).getLowPrice()) {
                    SAR = ep;
                    //重新初始化值
                    af = 0;
                    ep = INIT_VALUE;
                    lasttrend = !lasttrend;

                } else if (SAR > lowestPrior2Lows) {
                    SAR = lowestPrior2Lows;
                }
            } else {
                if (ep == INIT_VALUE || ep > dataList.get(i).getLowPrice()) {
                    //重新初始化值
                    ep = dataList.get(i).getLowPrice();
                    af = Math.min(af + step, maxStep);
                }
                SAR = priorSAR + af * (ep - priorSAR);
                float highestPrior2Highs = Math.max(dataList.get(Math.max(1, i) - 1).getHighPrice(), dataList.get(i).getHighPrice());
                if (SAR < dataList.get(i + 1).getHighPrice()) {
                    SAR = ep;
                    //重新初始化值
                    af = 0;
                    ep = INIT_VALUE;
                    lasttrend = !lasttrend;

                } else if (SAR < highestPrior2Highs) {
                    SAR = highestPrior2Highs;
                }
            }

            point.sar = SAR;
            point.lasttrend = lasttrend;
        }


        KLineEntity point = dataList.get(dataList.size() - 1);
        point.sar = SAR;
        point.lasttrend = lasttrend;
    }

    /**
     * cci
     *
     * @param dataList
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateCCI(List<KLineEntity> dataList, int n) {
        float typSum = 0;
        float cciValue = 0;
        float typMa;
        float typDiff;
        float typDiffSum;
        float typDEV;

        float []typ = new float[dataList.size()];

        int period;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            typ[i] = (point.getClosePrice() + point.getHighPrice() + point.getLowPrice()) / 3;
            typSum += typ[i];
            if (i >= (n - 1)) {
                period = i - n;
                if (period >= 0) {
                    typSum -= typ[period];
                }
                typMa = typSum/n;
                typDiffSum = 0;
                for (int j = period+1;j<=i;j++){
                    typDiff = Math.abs(typ[j]-typMa);
                    typDiffSum+=typDiff;
                }
                typDEV = typDiffSum/n;
                cciValue = (float) ((typ[i] - typMa)/(typDEV * 0.015));
            }

            point.cci = cciValue;
        }
    }


    /**
     * 计算ema
     *
     * @param dataList
     */
    @SuppressWarnings("JavaDoc")
    private static void calculateMTM(List<KLineEntity> dataList) {

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);

            if (i < 6) {
                point.mtm6 = 0;
            } else {
                KLineEntity point6 = dataList.get(i - 6);
                point.mtm6 = (float) (point.getClosePrice() - point6.getClosePrice()) ;
            }

            if (i < 12) {
                point.mtm12 = 0;
            } else {
                KLineEntity point12 = dataList.get(i - 12);
                point.mtm12 = (float) (point.getClosePrice() - point12.getClosePrice()) ;
            }
        }
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param datas
     */
    @SuppressWarnings("JavaDoc")
    public static void calculate(List<KLineEntity> datas) {
        if (datas == null) {
            return;
        }

        calculateMA(datas);
        calculateEMA(datas);
        calculateSAR(datas, (float) 0.02, (float) 0.2);
        calculateMACD(datas);
        calculateBOLL(datas);
        calculateRSI(datas);
        calculateMTM(datas);
        calculateKDJ(datas);
        calculateWR(datas);
        calculateCCI(datas, 14);
        calculateVolumeMA(datas);
    }

    private static void calculateVolumeMA(List<KLineEntity> entries) {
        float volumeMa5 = 0;
        float volumeMa10 = 0;

        for (int i = 0; i < entries.size(); i++) {
            KLineEntity entry = entries.get(i);

            volumeMa5 += entry.getVolume();
            volumeMa10 += entry.getVolume();

            if (i == 4) {
                entry.MA5Volume = (volumeMa5 / 5f);
            } else if (i > 4) {
                volumeMa5 -= entries.get(i - 5).getVolume();
                entry.MA5Volume = volumeMa5 / 5f;
            } else {
                entry.MA5Volume = 0f;
            }

            if (i == 9) {
                entry.MA10Volume = volumeMa10 / 10f;
            } else if (i > 9) {
                volumeMa10 -= entries.get(i - 10).getVolume();
                entry.MA10Volume = volumeMa10 / 10f;
            } else {
                entry.MA10Volume = 0f;
            }
        }

    }

    public static long calcStartTime() {
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.HOUR_OF_DAY, -10);

        Date dt1 = calendar.getTime();
        return dt1.getTime();
    }

    public static long calcEndTime() {
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);

        Date dt1 = calendar.getTime();
        return dt1.getTime();
    }
}
