package com.yjkj.chainup.db.constant;

/**
 * @Description:   交易类型枚举类
 * @Author: wanghao
 * @CreateDate: 2019-11-14 13:24
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-11-14 13:24
 * @UpdateRemark: 更新说明
 */
public enum TradeTypeEnum {


    COIN_TRADE("币币交易",0), // 默认
    CONTRACT_TRADE("合约交易",1),
    LEVER_TRADE("杠杆交易",2);

    private String name;
    private int value;

    TradeTypeEnum(String name,int value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
