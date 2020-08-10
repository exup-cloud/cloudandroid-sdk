package com.yjkj.chainup.util;


import android.text.TextUtils;
import com.yjkj.chainup.bean.coin.CoinMapBean;
import com.yjkj.chainup.manager.DataManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SymbolInterceptUtils {

    /**
     * @param num    数据
     * @param symbol 货币对
     * @param type   "price"-价格；"volume"-数量
     * @return
     */
    public static String interceptData(String num, String symbol, String type) {
        CoinMapBean bean = DataManager.Companion.getCoinMapBySymbol(symbol);
        return interceptData(num, bean, type);
    }


    public static String interceptData(String num, CoinMapBean coinMapBean, String type) {
        if (TextUtils.isEmpty(num)) {
            return "--";
        }
        int position = 0;
        if (coinMapBean == null) {
            if (type.equals("price")) {
                position = 8;
            } else {
                position = 4;
            }
        } else {
            // 数据小数点处理
            if (type.equals("price")) {
                position = coinMapBean.getPrice();
            } else {
                position = coinMapBean.getVolume();
            }
        }

        return BigDecimalUtils.divForDown(num, position).toPlainString();
    }



    public static String interceptDataForDown(String num, CoinMapBean coinMapBean, String type) {
        if (TextUtils.isEmpty(num)) {
            return "--";
        }
        int position = 0;
        if (coinMapBean == null) {
            if (type.equals("price")) {
                position = 8;
            } else {
                position = 4;
            }
        } else {
            // 数据小数点处理
            if (type.equals("price")) {
                position = coinMapBean.getPrice();
            } else {
                position = coinMapBean.getVolume();
            }
        }
        return BigDecimalUtils.divForDown(num, position).toPlainString();
    }



    public static String interceptKlineData(String num, int position) {
        return BigDecimalUtils.intercept(num, position).toPlainString();
    }


    /**
     * 深度图按照指定位数，截取数字
     *
     * @param num
     * @param depth
     * @param type  "price" 返回指定位数
     *              "volume" 去掉多余的"0"
     * @return
     */
    public static String interceptData(String num, int depth, String type) {
        if (!StringUtil.isNumeric(num)) {
            return "--";
        }

        /**
         * type
         */
        if (type.equals("price")) {
            BigDecimal bigDecimal = new BigDecimal(num);
            return bigDecimal.setScale(depth, RoundingMode.FLOOR).toPlainString();
        } else {
            BigDecimal bigDecimal = new BigDecimal(num);
            return bigDecimal.stripTrailingZeros().toPlainString();
        }


    }


    public static class Rule {

        private String symbol;
        private int priceLength;
        private int volumeLength;

        public Rule(String symbol, int priceLength, int volumeLength) {
            this.symbol = symbol;
            this.priceLength = priceLength;
            this.volumeLength = volumeLength;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getPriceLength() {
            return priceLength;
        }

        public int getVolumeLength() {
            return volumeLength;
        }
    }
}
