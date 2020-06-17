package com.bmtc.sdk.contract.common.chart;

import com.github.tifezh.kchartlib.chart.EntityImpl.KLineImpl;

import java.util.Date;


/**
 * K线实体
 * Created by tifezh on 2016/5/16.
 */

public class KLineEntity implements KLineImpl {

    public Date getDatetime() {
        return date;
    }

    @Override
    public float getOpenPrice() {
        return Open;
    }

    @Override
    public float getHighPrice() {
        return High;
    }

    @Override
    public float getLowPrice() {
        return Low;
    }

    @Override
    public float getClosePrice() {
        return Close;
    }

    @Override
    public float getMA5Price() {
        return MA5Price;
    }

    @Override
    public float getMA10Price() {
        return MA10Price;
    }

    @Override
    public float getMA20Price() {
        return MA20Price;
    }

    @Override
    public float getMA30Price() {
        return MA30Price;
    }

    @Override
    public float getMA60Price() {
        return MA60Price;
    }

    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getDif() {
        return dif;
    }

    @Override
    public float getMacd() {
        return macd;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public float getRsi1() {
        return rsi1;
    }

    @Override
    public float getRsi2() {
        return rsi2;
    }

    @Override
    public float getRsi3() {
        return rsi3;
    }

    @Override
    public float getUp() {
        return up;
    }

    @Override
    public float getMb() {
        return mb;
    }

    @Override
    public float getDn() {
        return dn;
    }

    @Override
    public float getVolume() {
        return Volume;
    }

    @Override
    public float getChg() {
        return Chg;
    }

    @Override
    public float getMA5Volume() {
        return MA5Volume;
    }

    @Override
    public float getMA10Volume() {
        return MA10Volume;
    }

    @Override
    public float getR() {
        return r;
    }

    @Override
    public float getEMA5Price() {
        return EMA5Price;
    }

    @Override
    public float getEMA10Price() {
        return EMA10Price;
    }

    @Override
    public float getEMA20Price() {
        return EMA20Price;
    }

    @Override
    public float getSar() {
        return sar;
    }

    @Override
    public boolean getLasttrend() {
        return lasttrend;
    }

    @Override
    public float getCCI() {
        return cci;
    }

    @Override
    public float getMTM12() {
        return mtm6;
    }

    @Override
    public float getMTM6() {
        return mtm12;
    }

    public Date date;
    public float Open;
    public float High;
    public float Low;
    public float Close;
    public float Volume;
    public float Chg;

    public float MA5Price;

    public float MA10Price;

    public float MA20Price;

    public float MA30Price;

    public float MA60Price;

    public float dea;

    public float dif;

    public float macd;

    public float k;

    public float d;

    public float j;

    public float r;

    public float rsi1;

    public float rsi2;

    public float rsi3;

    public float up;

    public float mb;

    public float dn;

    public float MA5Volume;

    public float MA10Volume;

    public float EMA5Price;

    public float EMA10Price;

    public float EMA20Price;

    public float sar;

    public boolean lasttrend;

    public float cci;

    public float mtm6;

    public float mtm12;

}
