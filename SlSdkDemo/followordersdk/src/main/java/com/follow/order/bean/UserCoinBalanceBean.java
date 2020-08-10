package com.follow.order.bean;

import java.util.HashMap;

/**
 * @time: 2020/3/30
 * @author: guodong
 */
public class UserCoinBalanceBean {

    /**
     * allCoinMap : {"BTC":{"normal_balance":"8.89800000","lock_position_v2_amount":"0.0000000","depositOpen":1,"overcharge_balance":1}}
     * totalBalance : 8.9
     * totalBalanceSymbol : BTC
     */

    private HashMap<String, InfoBean> allCoinMap;
    private String totalBalance;
    private String totalBalanceSymbol;

    public HashMap<String, InfoBean> getAllCoinMap() {
        return allCoinMap;
    }

    public void setAllCoinMap(HashMap<String, InfoBean> allCoinMap) {
        this.allCoinMap = allCoinMap;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getTotalBalanceSymbol() {
        return totalBalanceSymbol;
    }

    public void setTotalBalanceSymbol(String totalBalanceSymbol) {
        this.totalBalanceSymbol = totalBalanceSymbol;
    }

    public static class InfoBean {
        /**
         * normal_balance : 8.89800000
         * lock_position_v2_amount : 0.0000000
         * depositOpen : 1
         * overcharge_balance : 1
         */

        private String normal_balance;
        private String lock_position_v2_amount;
        private String depositOpen;
        private String overcharge_balance;

        public String getNormal_balance() {
            return normal_balance;
        }

        public void setNormal_balance(String normal_balance) {
            this.normal_balance = normal_balance;
        }

        public String getLock_position_v2_amount() {
            return lock_position_v2_amount;
        }

        public void setLock_position_v2_amount(String lock_position_v2_amount) {
            this.lock_position_v2_amount = lock_position_v2_amount;
        }

        public String getDepositOpen() {
            return depositOpen;
        }

        public void setDepositOpen(String depositOpen) {
            this.depositOpen = depositOpen;
        }

        public String getOvercharge_balance() {
            return overcharge_balance;
        }

        public void setOvercharge_balance(String overcharge_balance) {
            this.overcharge_balance = overcharge_balance;
        }
    }
}
